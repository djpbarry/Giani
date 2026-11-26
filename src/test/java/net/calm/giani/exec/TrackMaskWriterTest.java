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

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.PNG_Writer;
import ij.process.ShortProcessor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the track-labelled mask writer (Phase H5).
 */
class TrackMaskWriterTest {

    @Test
    void buildRemapMapsLabelsPerTimepoint() {
        List<CellLineageTracker.CellTrack> tracks = List.of(
                new CellLineageTracker.CellTrack(0, 0, 1, 0.0, 0.0, 0.0, 0.0, 10, -1),
                new CellLineageTracker.CellTrack(0, 1, 2, 0.0, 0.0, 0.0, 0.0, 20, -1),
                new CellLineageTracker.CellTrack(1, 0, 1, 0.0, 0.0, 0.0, 0.0, 30, -1)
        );

        assertEquals(Map.of(10, 3, 20, 4), TrackMaskWriter.buildRemap(tracks, 0));
        assertEquals(Map.of(30, 3), TrackMaskWriter.buildRemap(tracks, 1));
        assertEquals(Map.of(), TrackMaskWriter.buildRemap(tracks, 2));
    }

    @Test
    void remapPixelsRewritesInPlace() {
        ShortProcessor ip = new ShortProcessor(4, 1);
        ip.set(0, 0, 0);   // background stays 0
        ip.set(1, 0, 10);  // tracked -> 3
        ip.set(2, 0, 20);  // tracked -> 4
        ip.set(3, 0, 99);  // untracked -> 1

        TrackMaskWriter.remapPixels(ip, Map.of(10, 3, 20, 4));

        assertEquals(0, ip.get(0, 0));
        assertEquals(3, ip.get(1, 0));
        assertEquals(4, ip.get(2, 0));
        assertEquals(1, ip.get(3, 0));
    }

    @Test
    void onlyCellLabelledMaskFoldersAreTrackable() {
        assertTrue(TrackMaskWriter.isTrackableMaskDir("img.ome.tiff-S0_Nucleus_masks"));
        assertTrue(TrackMaskWriter.isTrackableMaskDir("img.ome.tiff-S0_Cell_masks"));
        assertTrue(TrackMaskWriter.isTrackableMaskDir("img.ome.tiff-S0_Cytoplasm_masks"));
        // Spot masks are labelled by spot index, not by cell.
        assertFalse(TrackMaskWriter.isTrackableMaskDir("img.ome.tiff-S0_Channel_0_Spot_masks"));
        // The writer's own output must never be re-processed.
        assertFalse(TrackMaskWriter.isTrackableMaskDir("img.ome.tiff-S0_Cell_track_masks"));
    }

    /**
     * Before 3.14.10 every {@code *_masks} folder was relabelled with the cell
     * map, producing meaningless {@code *_Spot_track_masks}.
     */
    @Test
    void writeRelabelsCellMasksAndLeavesSpotMasksAlone(@TempDir Path out) throws Exception {
        writeMask(out.resolve("img-S0_Cell_masks"), "img-S0_Cell_T0_Z0001.png", 10);
        writeMask(out.resolve("img-S0_Channel_0_Spot_masks"), "img-S0_Channel_0_Spot_T0_Z0001.png", 10);
        int trackId = 1;
        List<CellLineageTracker.CellTrack> tracks = List.of(
                new CellLineageTracker.CellTrack(0, 0, trackId, 0.0, 0.0, 0.0, 0.0, 10, -1));

        TrackMaskWriter.write(out.toFile(), tracks);

        File cellTrack = out.resolve("img-S0_Cell_track_masks").resolve("img-S0_Cell_T0_Z0001.png").toFile();
        assertTrue(cellTrack.isFile());
        assertEquals(CellLineageTracker.TRACK_LABEL_OFFSET + trackId,
                IJ.openImage(cellTrack.getAbsolutePath()).getProcessor().get(0, 0));
        assertFalse(Files.exists(out.resolve("img-S0_Channel_0_Spot_track_masks")),
                "spot masks must not be relabelled with the cell map");
    }

    private static void writeMask(Path dir, String name, int label) throws Exception {
        Files.createDirectories(dir);
        ShortProcessor ip = new ShortProcessor(2, 1);
        ip.set(0, 0, label);
        new PNG_Writer().writeImage(new ImagePlus(name, ip), dir.resolve(name).toString(), 0);
    }
}
