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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the Ultrack CSV writer (Phase H4).
 */
class UltrackCsvWriterTest {

    @Test
    void writesSortedUltrackCsv(@TempDir Path tmp) throws Exception {
        List<CellLineageTracker.CellTrack> tracks = List.of(
                new CellLineageTracker.CellTrack(2, 0, 3, 30.0, 31.0, 32.0, 5.0, 1),
                new CellLineageTracker.CellTrack(0, 0, 1, 10.0, 11.0, 12.0, 2.0, 2),
                new CellLineageTracker.CellTrack(1, 1, 1, 20.0, 21.0, 22.0, 3.0, 3),
                new CellLineageTracker.CellTrack(0, 0, 2, 40.0, 41.0, 42.0, 4.0, 4)
        );

        UltrackCsvWriter.write(tmp.toFile(), tracks);

        Path csv = tmp.resolve("GIANI_tracks.csv");
        assertTrue(Files.exists(csv));
        List<String> lines = Files.readAllLines(csv, StandardCharsets.UTF_8);
        assertEquals(5, lines.size());
        assertEquals("track_id,t,z,y,x,parent_track_id,radius", lines.get(0));
        // rows sorted by trackId, then time; track_id is offset by TRACK_LABEL_OFFSET (2)
        assertEquals("3,0,12.000000,11.000000,10.000000,-1,2.000000", lines.get(1));
        assertEquals("3,1,22.000000,21.000000,20.000000,-1,3.000000", lines.get(2));
        assertEquals("4,0,42.000000,41.000000,40.000000,-1,4.000000", lines.get(3));
        assertEquals("5,2,32.000000,31.000000,30.000000,-1,5.000000", lines.get(4));
    }

    @Test
    void skipsEmptyList(@TempDir Path tmp) throws Exception {
        UltrackCsvWriter.write(tmp.toFile(), List.of());
        assertTrue(Files.notExists(tmp.resolve("GIANI_tracks.csv")));
    }
}
