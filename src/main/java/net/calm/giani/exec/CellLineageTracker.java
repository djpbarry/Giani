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
import fiji.plugin.trackmate.tracking.SpotTracker;
import fiji.plugin.trackmate.tracking.TrackerKeys;
import fiji.plugin.trackmate.tracking.jaqaman.SparseLAPTrackerFactory;
import mcib3d.geom.Object3D;
import mcib3d.geom.Objects3DPopulation;
import mcib3d.geom.Vector3D;
import net.calm.iaclasslibrary.Cell3D.Cell3D;
import net.calm.iaclasslibrary.Cell3D.Nucleus3D;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public record CellTrack(int time, int cellIndex, int trackId, double x, double y, double z, double radius) {
    }

    private record CellObservation(int time, int cellIndex, double x, double y, double z, double radius) {
    }

    private final SpotCollection spots = new SpotCollection();
    private final Map<Spot, CellObservation> spotCell = new HashMap<>();

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
            double[] centroid = nucleus.getCentroid();
            double radius = Math.cbrt(3.0 * nucleus.getVolumeUnit() / (4.0 * Math.PI));
            Spot spot = new Spot(centroid[0], centroid[1], centroid[2], radius, 1.0);
            spots.add(spot, time);
            Vector3D center = nucleus.getCenterAsVectorUnit();
            spotCell.put(spot, new CellObservation(time, i, center.x, center.y, center.z, radius));
        }
    }

    public List<CellTrack> track() {
        SparseLAPTrackerFactory factory = new SparseLAPTrackerFactory();
        Map<String, Object> settings = factory.getDefaultSettings();
        settings.put(TrackerKeys.KEY_ALLOW_TRACK_SPLITTING, true);
        SpotTracker tracker = factory.create(spots, settings);
        tracker.setNumThreads();
        if (!tracker.checkInput() || !tracker.process()) {
            return List.of();
        }
        Model model = new Model();
        model.setSpots(spots, false);
        model.setTracks(tracker.getResult(), true);
        TrackModel trackModel = model.getTrackModel();
        List<CellTrack> result = new ArrayList<>();
        for (Integer trackId : trackModel.trackIDs(true)) {
            for (Spot spot : trackModel.trackSpots(trackId)) {
                CellObservation obs = spotCell.get(spot);
                if (obs != null) {
                    result.add(new CellTrack(obs.time, obs.cellIndex, trackId, obs.x, obs.y, obs.z, obs.radius));
                }
            }
        }
        return result;
    }
}
