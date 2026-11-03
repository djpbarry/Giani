/*
 * Copyright (C) 2019 David Barry <david.barry at crick dot ac dot uk>
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
package net.calm.giani.gianiparams;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the default parameter values and constants.
 */
class GianiDefaultParamsTest {

    @Test
    void hasExpectedCoreDefaults() {
        GianiDefaultParams p = new GianiDefaultParams();
        assertEquals("0", p.getProperty(GianiDefaultParams.SERIES_SELECT_LABEL));
        assertEquals("0", p.getProperty(GianiDefaultParams.BLOB_NUC_CHAN_SELECT_LABEL));
        assertEquals("true", p.getProperty(GianiDefaultParams.NUC_VOL_MARKER));
        assertEquals("false", p.getProperty(GianiDefaultParams.NUC_MEM_MARKER));
        assertEquals("true", p.getProperty(GianiDefaultParams.CELL_VOL_MARKER));
        assertEquals("false", p.getProperty(GianiDefaultParams.CELL_MEM_MARKER));
        assertEquals("false", p.getProperty(GianiDefaultParams.LOCALISE_SPOTS));
        assertEquals("0", p.getProperty(GianiDefaultParams.CHAN_FOR_MEASURE));
        assertEquals("-1", p.getProperty(GianiDefaultParams.SPECIFIC_SERIES));
        assertEquals("false", p.getProperty(GianiDefaultParams.PROCESS_ALL_TIMEPOINTS));
        assertEquals("-1", p.getProperty(GianiDefaultParams.TIME_SELECT));
        assertEquals("true", p.getProperty(GianiDefaultParams.TRACK_LINEAGE));
        assertEquals("-1", p.getProperty(GianiDefaultParams.TRACK_LINKING_DISTANCE));
        assertEquals("2", p.getProperty(GianiDefaultParams.TRACK_MAX_FRAME_GAP));
        assertEquals("true", p.getProperty(GianiDefaultParams.ENABLE_TOP_HAT_FILTER));
        assertEquals("1.0", p.getProperty(GianiDefaultParams.NUC_FILT_RAD_LABEL));
        assertEquals("10.0", p.getProperty(GianiDefaultParams.NUC_TOP_HAT_FILT_RAD_LABEL));
        assertEquals("5.0", p.getProperty(GianiDefaultParams.BLOB_NUC_RAD_LABEL));
        assertEquals("1.0", p.getProperty(GianiDefaultParams.CELL_FILT_RAD_LABEL));
        assertEquals("5.0", p.getProperty(GianiDefaultParams.NUC_MAXIMA_DETECT_HESSIAN_START_SCALE));
        assertEquals("0.5", p.getProperty(GianiDefaultParams.NUC_DIST_WEIGHTING));
    }

    @Test
    void maxChannelsIsTen() {
        assertEquals(10, GianiDefaultParams.MAX_CHANNELS);
    }

    @Test
    void titleFallsBackToGianiWhenNotRunFromGianiJar() {
        assertEquals("GIANI", GianiDefaultParams.TITLE);
    }

    @Test
    void titleAppendsManifestVersion() {
        assertEquals("GIANI-3.13.2", GianiDefaultParams.titleFor("3.13.2"));
        assertEquals("GIANI", GianiDefaultParams.titleFor(null));
        assertEquals("GIANI", GianiDefaultParams.titleFor("  "));
    }

    /**
     * Headless and macro runs load parameters through the same helper as the
     * GUI, so a file saved before a parameter was renamed must still apply the
     * old value under the new name (previously only the GUI migrated names).
     */
    @Test
    void loadPropertiesMigratesDeprecatedNames(@TempDir Path tmp) throws Exception {
        Properties saved = new Properties();
        saved.setProperty(DeprecatedProps.TRACK_TIMELAPSE, "true");
        File file = tmp.resolve("properties.xml").toFile();
        try (OutputStream out = new FileOutputStream(file)) {
            saved.storeToXML(out, "GIANI-3.8.0");
        }

        GianiDefaultParams props = new GianiDefaultParams();
        assertEquals("false", props.getProperty(GianiDefaultParams.PROCESS_ALL_TIMEPOINTS));

        GianiDefaultParams.loadProperties(props, null, file);

        assertEquals("true", props.getProperty(GianiDefaultParams.PROCESS_ALL_TIMEPOINTS));
    }

    @Test
    void perChannelLabelsEndWithChannelAndSuffixWithNumber() {
        assertTrue(GianiDefaultParams.BLOB_CHAN_SELECT_LABEL.endsWith("Channel "));
        assertEquals("Detect Spots in Channel 3",
                String.format("%s%d", GianiDefaultParams.BLOB_CHAN_SELECT_LABEL, 3));
        assertEquals("Radius for Simple Spot Detection in Channel 7",
                String.format("%s%d", GianiDefaultParams.BLOB_CHAN_RAD_LABEL, 7));
    }
}
