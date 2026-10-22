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

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Comparator;
import java.util.List;

/**
 * Writes the GIANI cell lineage as a CSV in
 * <a href="https://github.com/royerlab/inTRACKtive">inTRACKtive</a>'s Ultrack
 * format: {@code track_id, t, z, y, x, parent_track_id} (plus a {@code radius}
 * column). For now {@code track_id} is TrackMate's raw track ID and
 * {@code parent_track_id} is always {@code -1} (lineage/division re-labelling is
 * deferred — see Phase H4/H5 in {@code DEVELOPMENT_PLAN.md}).
 */
public final class UltrackCsvWriter {

    private UltrackCsvWriter() {
    }

    /**
     * Writes the given lineage tracks to {@code <outputDir>/GIANI_tracks.csv}.
     *
     * @param outputDir the output directory (must exist)
     * @param tracks    the tracked cell observations
     * @throws IOException if the file cannot be written
     */
    public static void write(File outputDir, List<CellLineageTracker.CellTrack> tracks) throws IOException {
        if (tracks == null || tracks.isEmpty()) {
            return;
        }
        List<CellLineageTracker.CellTrack> sorted = tracks.stream()
                .sorted(Comparator.comparingInt(CellLineageTracker.CellTrack::trackId)
                        .thenComparingInt(CellLineageTracker.CellTrack::time))
                .toList();
        File csv = new File(outputDir, "GIANI_tracks.csv");
        try (BufferedWriter writer = Files.newBufferedWriter(csv.toPath(), StandardCharsets.UTF_8)) {
            writer.write("track_id,t,z,y,x,parent_track_id,radius\n");
            for (CellLineageTracker.CellTrack track : sorted) {
                writer.write(String.format("%d,%d,%.6f,%.6f,%.6f,%d,%.6f%n",
                        track.trackId(), track.time(), track.z(), track.y(), track.x(), -1, track.radius()));
            }
        }
    }
}
