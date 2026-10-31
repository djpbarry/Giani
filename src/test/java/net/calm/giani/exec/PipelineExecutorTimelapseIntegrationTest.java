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

import net.calm.giani.gianiparams.GianiDefaultParams;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Phase H1 end-to-end smoke test: runs the full pipeline over a T=2 fixture with
 * {@code TRACK_TIMELAPSE=true}, proving the executor time loop processes every
 * timepoint without error.
 */
@Tag("integration")
class PipelineExecutorTimelapseIntegrationTest {

    private static final String FIXTURE = "test_data/multiple_timepoints/Fluo-N3DH-CHO.ome.tiff";

    @Test
    @Timeout(value = 25, unit = TimeUnit.MINUTES)
    void timelapseRunWritesOutput(@TempDir Path outputDir) throws IOException {
        File fixture = new File(FIXTURE);
        Assumptions.assumeTrue(fixture.isFile(), "test fixture not present: " + FIXTURE);

        System.setProperty("java.awt.headless", "true");

        GianiDefaultParams props = new GianiDefaultParams();
        props.setProperty(GianiDefaultParams.INPUT_DIR_LABEL, fixture.getAbsolutePath());
        props.setProperty(GianiDefaultParams.OUTPUT_DIR_LABEL, outputDir.toString());
        props.setProperty(GianiDefaultParams.SPECIFIC_SERIES, "0");
        props.setProperty(GianiDefaultParams.LOCALISE_SPOTS, "false");
        props.setProperty(GianiDefaultParams.TRACK_TIMELAPSE, "true");

        props.setProperty(GianiDefaultParams.NUC_MAXIMA_DETECT_BLOBS, "false");
        props.setProperty(GianiDefaultParams.NUC_MAXIMA_DETECT_HESSIAN, "true");
        props.setProperty(GianiDefaultParams.NUC_MAXIMA_DETECT_HESSIAN_START_SCALE, "5");
        props.setProperty(GianiDefaultParams.NUC_MAXIMA_DETECT_HESSIAN_THRESH, "0.011");
        props.setProperty(GianiDefaultParams.NUC_DETECT_MODE, GianiDefaultParams.NUC_MAXIMA_DETECT_HESSIAN);
        props.setProperty(GianiDefaultParams.NUC_FILT_RAD_LABEL, "0.5");
        props.setProperty(GianiDefaultParams.CELL_FILT_RAD_LABEL, "1");
        props.setProperty(GianiDefaultParams.CELL_SEG_CHAN_SELECT_LABEL, "0");
        props.setProperty(GianiDefaultParams.ENABLE_TOP_HAT_FILTER, "false");
        props.setProperty(GianiDefaultParams.NUC_SEG_THRESH_LABEL, "Li");
        props.setProperty(GianiDefaultParams.CELL_SEG_THRESH_LABEL, "Huang");
        props.setProperty(GianiDefaultParams.CHAN_FOR_MEASURE, "1");

        new GIANI().run(props);

        File csv = outputDir.resolve(GianiDefaultParams.TITLE + "_Output.csv").toFile();
        File properties = outputDir.resolve("properties.xml").toFile();

        assertTrue(csv.isFile(), "expected output CSV: " + csv);
        assertTrue(csv.length() > 0, "output CSV is empty: " + csv);
        assertTrue(properties.isFile(), "expected saved properties file: " + properties);

        List<String> csvLines = Files.readAllLines(csv.toPath(), StandardCharsets.ISO_8859_1);
        assertTrue(csvLines.size() >= 2, "expected a header row plus at least one measurement row");

        String[] header = csvLines.get(0).split(",");
        int timeIndex = -1;
        int seriesIndex = -1;
        for (int i = 0; i < header.length; i++) {
            if ("Time".equals(header[i].trim())) {
                timeIndex = i;
            } else if ("Series".equals(header[i].trim())) {
                seriesIndex = i;
            }
        }
        assertTrue(timeIndex >= 0, "expected a Time column in header: " + csvLines.get(0));
        assertTrue(seriesIndex >= 0, "expected a Series column in header: " + csvLines.get(0));

        Set<String> times = new HashSet<>();
        for (int i = 1; i < csvLines.size(); i++) {
            String[] row = csvLines.get(i).split(",");
            if (row.length > timeIndex) {
                times.add(row[timeIndex].trim());
            }
        }
        assertTrue(times.size() >= 2, "expected at least 2 distinct timepoints, got: " + times);

        int trackIdIndex = -1;
        for (int i = 0; i < header.length; i++) {
            if ("Track ID".equals(header[i].trim())) {
                trackIdIndex = i;
                break;
            }
        }
        assertTrue(trackIdIndex >= 0, "expected a Track ID column in header: " + csvLines.get(0));

        Set<Double> outputTrackIds = new HashSet<>();
        for (int i = 1; i < csvLines.size(); i++) {
            String[] row = csvLines.get(i).split(",");
            if (row.length > trackIdIndex) {
                double trackId = Double.parseDouble(row[trackIdIndex].trim());
                if (trackId != 0.0) {
                    outputTrackIds.add(trackId);
                }
            }
        }
        assertFalse(outputTrackIds.isEmpty(), "expected at least one tracked cell");

        File tracksCsv = outputDir.resolve("GIANI_tracks.csv").toFile();
        assertTrue(tracksCsv.isFile(), "expected tracks CSV: " + tracksCsv);
        List<String> trackLines = Files.readAllLines(tracksCsv.toPath(), StandardCharsets.UTF_8);
        assertTrue(trackLines.size() >= 2, "expected a tracks header plus at least one row");
        Set<Double> trackIds = new HashSet<>();
        for (int i = 1; i < trackLines.size(); i++) {
            trackIds.add(Double.parseDouble(trackLines.get(i).split(",")[0].trim()));
        }
        assertEquals(outputTrackIds, trackIds);
    }
}
