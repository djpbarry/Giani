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

import ij.process.ShortProcessor;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for the track-labelled mask writer (Phase H5).
 */
class TrackMaskWriterTest {

    @Test
    void buildRemapMapsLabelsPerTimepoint() {
        List<CellLineageTracker.CellTrack> tracks = List.of(
                new CellLineageTracker.CellTrack(0, 0, 1, 0.0, 0.0, 0.0, 0.0, 10),
                new CellLineageTracker.CellTrack(0, 1, 2, 0.0, 0.0, 0.0, 0.0, 20),
                new CellLineageTracker.CellTrack(1, 0, 1, 0.0, 0.0, 0.0, 0.0, 30)
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
}
