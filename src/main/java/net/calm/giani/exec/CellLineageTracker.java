/*
 * Copyright (C) 2018 David Barry <david.barry at crick dot ac dot uk>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.calm.giani.exec;

import fiji.plugin.trackmate.Model;
import fiji.plugin.trackmate.Spot;
import fiji.plugin.trackmate.SpotCollection;
import fiji.plugin.trackmate.TrackModel;
import fiji.plugin.trackmate.graph.TimeDirectedNeighborIndex;
import fiji.plugin.trackmate.tracking.SpotTracker;
import fiji.plugin.trackmate.tracking.TrackerKeys;
import fiji.plugin.trackmate.tracking.jaqaman.SparseLAPTrackerFactory;
import mcib3d.geom.Object3D;
import mcib3d.geom.Objects3DPopulation;
import mcib3d.geom.Vector3D;
import net.calm.iaclasslibrary.Cell3D.Cell3D;
import net.calm.iaclasslibrary.Cell3D.Nucleus3D;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Builds a TrackMate lineage over the per-timepoint {@code cells} population.
 * Each cell is represented by one {@link Spot} per frame (nucleus centroid +
 * equivalent radius), linked by TrackMate's sparse LAP tracker.
 */
public class CellLineageTracker {

    /**
     * Public track label offset. In the exported CSVs and mask images, {@code 0}
     * is background and {@code 1} is reserved for untracked cells, so a tracked
     * cell is labelled {@code TRACK_LABEL_OFFSET + trackId}. Both the CSV and
     * mask writers use this so they expose the same value.
     */
    public static final int TRACK_LABEL_OFFSET = 2;

    /**
     * One cell's lineage assignment: which cell (by timepoint index) landed in
     * which TrackMate track, plus its calibrated centroid and equivalent radius
     * for export.
     */
    public record CellTrack(int time, int cellIndex, int trackId, double x, double y, double z, double radius, int label, int parentTrackId) {
    }

    private record CellObservation(int time, int cellIndex, double x, double y, double z, double radius, int label) {
    }

    private final SpotCollection spots = new SpotCollection();
    private final Map<Spot, CellObservation> spotCell = new HashMap<>();
    private final double linkingDistance;
    private final int maxFrameGap;

    /**
     * @param linkingDistance the max linking / gap-closing / splitting distance
     *                        in physical units, or {@code <= 0} to derive it
     *                        automatically from the observed nucleus radii
     * @param maxFrameGap     the max number of frames a track may be missing
     *                        before it is considered interrupted
     */
    public CellLineageTracker(double linkingDistance, int maxFrameGap) {
        this.linkingDistance = linkingDistance;
        this.maxFrameGap = maxFrameGap;
    }

    public void addTimepoint(int time, Objects3DPopulation cells) {
        for (int i = 0; i < cells.getNbObjects(); i++) {
            Object3D object = cells.getObject(i);
            if (!(object instanceof Cell3D cell)) {
                continue;
            }
            Nucleus3D nucleus = cell.getNucleus();
            if (nucleus == null) {
                continue;
            }
            Vector3D center = nucleus.getCenterAsVectorUnit();
            double radius = Math.cbrt(3.0 * nucleus.getVolumeUnit() / (4.0 * Math.PI));
            Spot spot = new Spot(center.x, center.y, center.z, radius, 1.0);
            spots.add(spot, time);
            spotCell.put(spot, new CellObservation(time, i, center.x, center.y, center.z, radius, nucleus.getValue()));
        }
    }

    public List<CellTrack> track() {
        SparseLAPTrackerFactory factory = new SparseLAPTrackerFactory();
        Map<String, Object> settings = factory.getDefaultSettings();
        settings.put(TrackerKeys.KEY_ALLOW_TRACK_SPLITTING, true);
        settings.put(TrackerKeys.KEY_GAP_CLOSING_MAX_FRAME_GAP, maxFrameGap);
        double distance = linkingDistance > 0 ? linkingDistance : autoLinkingDistance();
        settings.put(TrackerKeys.KEY_LINKING_MAX_DISTANCE, distance);
        settings.put(TrackerKeys.KEY_GAP_CLOSING_MAX_DISTANCE, distance);
        settings.put(TrackerKeys.KEY_SPLITTING_MAX_DISTANCE, distance);
        SpotTracker tracker = factory.create(spots, settings);
        tracker.setNumThreads();
        if (!tracker.checkInput() || !tracker.process()) {
            return List.of();
        }
        Model model = new Model();
        model.setSpots(spots, false);
        model.setTracks(tracker.getResult(), true);
        TrackModel trackModel = model.getTrackModel();

        // Re-label each TrackMate track (a connected component) into true lineage
        // branches: a division (a spot with more than one successor) starts a
        // fresh lineage for every daughter, recording the parent lineage so that
        // parent_track_id is meaningful rather than always -1.
        TimeDirectedNeighborIndex neighborIndex = trackModel.getDirectedNeighborIndex();
        Map<Spot, Integer> spotLineage = new HashMap<>();
        Map<Integer, Integer> lineageParent = new HashMap<>();
        int[] nextLineage = {0};
        List<Integer> trackIds = new ArrayList<>(trackModel.trackIDs(true));
        Collections.sort(trackIds);
        for (Integer trackId : trackIds) {
            Set<Spot> trackSpots = trackModel.trackSpots(trackId);
            List<Spot> roots = new ArrayList<>();
            for (Spot spot : trackSpots) {
                if (neighborIndex.predecessorsOf(spot).isEmpty()) {
                    roots.add(spot);
                }
            }
            if (roots.isEmpty()) {
                trackSpots.stream().min(Spot.frameComparator).ifPresent(roots::add);
            }
            roots.sort(Spot.frameComparator);
            for (Spot root : roots) {
                int rootLineage = nextLineage[0]++;
                lineageParent.put(rootLineage, -1);
                assignLineage(root, rootLineage, neighborIndex, spotLineage, lineageParent, nextLineage);
            }
        }

        List<CellTrack> result = new ArrayList<>();
        for (Integer trackId : trackIds) {
            for (Spot spot : trackModel.trackSpots(trackId)) {
                CellObservation obs = spotCell.get(spot);
                if (obs != null) {
                    int lineage = spotLineage.getOrDefault(spot, 0);
                    int parent = lineageParent.getOrDefault(lineage, -1);
                    result.add(new CellTrack(obs.time, obs.cellIndex, lineage, obs.x, obs.y, obs.z, obs.radius, obs.label, parent));
                }
            }
        }
        return result;
    }

    /**
     * Derives a defensible per-frame linking distance from the observed nucleus
     * radii. The spot coordinates are already in physical units, so the linking
     * distance should scale with the objects being tracked rather than using
     * TrackMate's hard-coded {@link TrackerKeys#DEFAULT_LINKING_MAX_DISTANCE}.
     */
    private double autoLinkingDistance() {
        List<Double> radii = spotCell.values().stream()
                .map(obs -> obs.radius)
                .filter(r -> r > 0)
                .sorted()
                .toList();
        if (radii.isEmpty()) {
            return TrackerKeys.DEFAULT_LINKING_MAX_DISTANCE;
        }
        return 3.0 * radii.get(radii.size() / 2);
    }

    /**
     * Assigns a lineage id to a spot and recurses through its descendants. A
     * spot with a single successor (or none) continues its lineage; a spot with
     * multiple successors is a division, and each daughter starts a new lineage
     * whose parent is the current one.
     */
    private void assignLineage(Spot spot, int lineage, TimeDirectedNeighborIndex neighborIndex,
                               Map<Spot, Integer> spotLineage, Map<Integer, Integer> lineageParent, int[] nextLineage) {
        if (spotLineage.containsKey(spot)) {
            return;
        }
        spotLineage.put(spot, lineage);
        Set<Spot> successors = neighborIndex.successorsOf(spot);
        if (successors.size() <= 1) {
            for (Spot child : successors) {
                assignLineage(child, lineage, neighborIndex, spotLineage, lineageParent, nextLineage);
            }
        } else {
            for (Spot child : successors) {
                int childLineage = nextLineage[0]++;
                lineageParent.put(childLineage, lineage);
                assignLineage(child, childLineage, neighborIndex, spotLineage, lineageParent, nextLineage);
            }
        }
    }
}
