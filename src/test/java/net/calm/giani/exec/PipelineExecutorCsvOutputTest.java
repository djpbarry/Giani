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

import ij.measure.ResultsTable;
import net.calm.giani.gianiparams.GianiDefaultParams;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A run whose results CSV, or tracks CSV when tracking produced tracks, could
 * not be saved has produced nothing usable, so the headless CLI must exit
 * non-zero (3.14.11). These tests drive {@link PipelineExecutor#saveCsvOutputs}
 * against the real writers.
 *
 * <p>A CSV is made unwritable by putting a <em>directory</em> at its path, which
 * makes the real writer throw the real {@code IOException} on any platform
 * without needing file permissions.
 */
class PipelineExecutorCsvOutputTest {

    private static final String TRACKS_CSV = "GIANI_tracks.csv";

    private static String resultsCsvName() {
        return GianiDefaultParams.TITLE + "_Output.csv";
    }

    private static ResultsTable oneRowTable() {
        ResultsTable rt = new ResultsTable();
        rt.incrementCounter();
        rt.setValue("Series", 0, 0.0);
        rt.setValue("Time", 0, 0.0);
        return rt;
    }

    private static List<CellLineageTracker.CellTrack> oneTrack() {
        return List.of(new CellLineageTracker.CellTrack(0, 0, 1, 10.0, 11.0, 12.0, 2.0, 1, -1));
    }

    private static void blockWithDirectory(Path dir, String name) throws Exception {
        Files.createDirectory(dir.resolve(name));
    }

    @Test
    void reportsSuccessAndWritesBothCsvsWhenBothCanBeSaved(@TempDir Path out) {
        boolean saved = PipelineExecutor.saveCsvOutputs(out.toFile(), oneRowTable(), oneTrack());

        assertTrue(saved);
        assertTrue(out.resolve(resultsCsvName()).toFile().length() > 0, "results CSV should be written");
        assertTrue(out.resolve(TRACKS_CSV).toFile().length() > 0, "tracks CSV should be written");
    }

    @Test
    void failsWhenTheResultsCsvCannotBeSavedButStillWritesTheTracksCsv(@TempDir Path out) throws Exception {
        blockWithDirectory(out, resultsCsvName());

        boolean saved = PipelineExecutor.saveCsvOutputs(out.toFile(), oneRowTable(), oneTrack());

        assertFalse(saved, "an unsaved results CSV must fail the run");
        // Whatever can be saved is still saved, for diagnosis.
        assertTrue(out.resolve(TRACKS_CSV).toFile().length() > 0,
                "the tracks CSV should still be attempted after the results CSV fails");
    }

    @Test
    void failsWhenTheTracksCsvCannotBeSavedButStillWritesTheResultsCsv(@TempDir Path out) throws Exception {
        blockWithDirectory(out, TRACKS_CSV);

        boolean saved = PipelineExecutor.saveCsvOutputs(out.toFile(), oneRowTable(), oneTrack());

        assertFalse(saved, "an unsaved tracks CSV must fail the run when there were tracks");
        assertTrue(out.resolve(resultsCsvName()).toFile().length() > 0,
                "the results CSV should still be written when only the tracks CSV fails");
    }

    @Test
    void doesNotRequireATracksCsvWhenTrackingProducedNoTracks(@TempDir Path out) throws Exception {
        // Tracking is on by default, but a single-timepoint run yields no tracks;
        // that is normal, not a failure, and writes no tracks CSV.
        boolean saved = PipelineExecutor.saveCsvOutputs(out.toFile(), oneRowTable(), List.of());

        assertTrue(saved);
        assertTrue(Files.notExists(out.resolve(TRACKS_CSV)), "no tracks CSV when there are no tracks");
        assertTrue(out.resolve(resultsCsvName()).toFile().length() > 0);
    }

    /**
     * Pins today's behaviour rather than endorsing it: IAClassLibrary's
     * {@code DataWriter.saveResultsTable} returns without writing when the table
     * has no rows, so a run that measured nothing writes no results CSV and is
     * still reported as saved. If the library changes, or the plan's open
     * question on this is settled the other way, this test should change with it.
     */
    @Test
    void runThatMeasuredNothingWritesNoCsvAndStillSucceeds(@TempDir Path out) {
        boolean saved = PipelineExecutor.saveCsvOutputs(out.toFile(), new ResultsTable(), List.of());

        assertTrue(saved);
        File resultsCsv = out.resolve(resultsCsvName()).toFile();
        assertFalse(resultsCsv.exists(), "no rows, so the library writes no file");
        assertEquals(0, out.toFile().list().length, "nothing at all should be written");
    }
}
