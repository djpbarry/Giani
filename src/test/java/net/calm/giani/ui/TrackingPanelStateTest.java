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
package net.calm.giani.ui;

import net.calm.giani.gianiparams.GianiDefaultParams;
import net.calm.iaclasslibrary.IO.BioFormats.BioFormatsImg;
import net.calm.iaclasslibrary.UIClasses.PropertyExtractor;
import org.junit.jupiter.api.Test;

import javax.swing.JPanel;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * What the wizard stores after collecting the tracking panel's state.
 *
 * <p>{@code TrackingPanelMappingTest} covers the value conversions, but nothing
 * built the panel and checked what a collect writes. That let the "Max Missing
 * Timepoints" field ship (3.14.0) without ever being given its initial value, so
 * the first collect of a fresh session wrote {@code ""} over the default
 * {@code 2} and every saved {@code properties.xml} carried a blank. Loading a
 * parameters file first (a read before the write) masked it in the live test.
 *
 * <p>The panel is collected the way the wizard does it: placed in a parent
 * container and written by {@code PropertyExtractor}, which calls the panel's
 * {@code setVariables()} and then writes its label-bound fields.
 */
class TrackingPanelStateTest {

    private static final String LINEAGE = GianiDefaultParams.TRACK_LINEAGE;
    private static final String PROCESS_ALL = GianiDefaultParams.PROCESS_ALL_TIMEPOINTS;
    private static final String TIME_SELECT = GianiDefaultParams.TIME_SELECT;
    private static final String LINKING = GianiDefaultParams.TRACK_LINKING_DISTANCE;
    private static final String MAX_MISSING = GianiDefaultParams.TRACK_MAX_FRAME_GAP;

    /** Builds the panel from {@code props} and collects its state back into {@code props}. */
    private static void collect(Properties props) {
        TrackingPanel panel = new TrackingPanel(props, new BioFormatsImg());
        JPanel parent = new JPanel();
        parent.add(panel);
        PropertyExtractor.setProperties(props, parent, PropertyExtractor.WRITE);
    }

    @Test
    void aFreshSessionKeepsTheDefaultMaxMissingTimepoints() {
        Properties props = new GianiDefaultParams();
        assertEquals("2", props.getProperty(MAX_MISSING), "precondition: the shipped default");

        collect(props);

        assertEquals("2", props.getProperty(MAX_MISSING),
                "the first collect must not blank the default; the executor cannot parse '' and logs a fallback");
    }

    @Test
    void aSavedMaxMissingTimepointsSurvivesACollect() {
        Properties props = new GianiDefaultParams();
        props.setProperty(MAX_MISSING, "5");

        collect(props);

        assertEquals("5", props.getProperty(MAX_MISSING));
    }

    @Test
    void aFreshSessionLeavesEveryTrackingParameterAtItsIntendedValue() {
        Properties props = new GianiDefaultParams();

        collect(props);

        assertEquals("false", props.getProperty(PROCESS_ALL));
        assertEquals("-1", props.getProperty(TIME_SELECT));
        assertEquals("-1", props.getProperty(LINKING));
        assertEquals("2", props.getProperty(MAX_MISSING));
        // By design: the shipped default is lineage on, but "first timepoint only"
        // cannot produce tracking, so the panel deselects it rather than letting a
        // run silently produce no tracking output.
        assertEquals("false", props.getProperty(LINEAGE));
    }

    @Test
    void everyTrackingParameterSurvivesACollectWhenAnalysingAllTimepoints() {
        Properties props = new GianiDefaultParams();
        props.setProperty(PROCESS_ALL, "true");
        props.setProperty(TIME_SELECT, "-1");
        props.setProperty(LINEAGE, "true");
        props.setProperty(LINKING, "12.5");
        props.setProperty(MAX_MISSING, "3");

        collect(props);

        assertEquals("true", props.getProperty(PROCESS_ALL));
        assertEquals("-1", props.getProperty(TIME_SELECT));
        assertEquals("true", props.getProperty(LINEAGE));
        assertEquals("12.5", props.getProperty(LINKING));
        assertEquals("3", props.getProperty(MAX_MISSING));
    }

    @Test
    void aSingleTimepointKeepsItsZeroBasedIndexAndTheOtherSettings() {
        Properties props = new GianiDefaultParams();
        props.setProperty(PROCESS_ALL, "false");
        props.setProperty(TIME_SELECT, "4");
        props.setProperty(MAX_MISSING, "3");

        collect(props);

        assertEquals("4", props.getProperty(TIME_SELECT), "shown as 5, stored 0-based");
        assertEquals("false", props.getProperty(PROCESS_ALL));
        assertEquals("3", props.getProperty(MAX_MISSING));
    }
}
