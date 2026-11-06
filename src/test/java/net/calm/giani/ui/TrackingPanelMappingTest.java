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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The tracking panel shows friendly controls but stores the original sentinel
 * values, so old parameter files, the headless CLI and the macro API keep
 * working. These pin that mapping in both directions.
 */
class TrackingPanelMappingTest {

    // --- stored values -> displayed mode -------------------------------------

    @Test
    void defaultsToFirstTimepointOnly() {
        assertEquals(TrackingPanel.MODE_FIRST, TrackingPanel.modeFor("false", "-1"));
    }

    @Test
    void processAllTimepointsShowsAll() {
        assertEquals(TrackingPanel.MODE_ALL, TrackingPanel.modeFor("true", "-1"));
    }

    @Test
    void validSpecificTimepointWinsOverProcessAll() {
        // Matches PipelineExecutor.timeRange, where a valid TIME_SELECT wins.
        assertEquals(TrackingPanel.MODE_SINGLE, TrackingPanel.modeFor("true", "3"));
        assertEquals(TrackingPanel.MODE_SINGLE, TrackingPanel.modeFor("false", "0"));
    }

    @Test
    void unparseableValuesFallBackToFirstOnly() {
        assertEquals(TrackingPanel.MODE_FIRST, TrackingPanel.modeFor(null, null));
        assertEquals(TrackingPanel.MODE_FIRST, TrackingPanel.modeFor("", "rubbish"));
    }

    // --- timepoint index is 0-based stored, 1-based displayed ----------------

    @Test
    void storedIndexIsShownOneBased() {
        assertEquals("1", TrackingPanel.timepointTextFor("0"));
        assertEquals("10", TrackingPanel.timepointTextFor("9"));
    }

    @Test
    void unsetTimepointShowsBlankRatherThanMinusOne() {
        assertEquals("", TrackingPanel.timepointTextFor("-1"));
        assertEquals("", TrackingPanel.timepointTextFor(null));
    }

    @Test
    void displayedTimepointIsStoredZeroBased() {
        assertEquals("0", TrackingPanel.timeSelectFor(TrackingPanel.MODE_SINGLE, "1"));
        assertEquals("9", TrackingPanel.timeSelectFor(TrackingPanel.MODE_SINGLE, "10"));
    }

    @Test
    void timepointIsIgnoredUnlessSingleModeSelected() {
        assertEquals("-1", TrackingPanel.timeSelectFor(TrackingPanel.MODE_ALL, "5"));
        assertEquals("-1", TrackingPanel.timeSelectFor(TrackingPanel.MODE_FIRST, "5"));
    }

    @Test
    void invalidOrOutOfRangeTimepointStoresUnset() {
        assertEquals("-1", TrackingPanel.timeSelectFor(TrackingPanel.MODE_SINGLE, ""));
        assertEquals("-1", TrackingPanel.timeSelectFor(TrackingPanel.MODE_SINGLE, "0"));
        assertEquals("-1", TrackingPanel.timeSelectFor(TrackingPanel.MODE_SINGLE, "-4"));
        assertEquals("-1", TrackingPanel.timeSelectFor(TrackingPanel.MODE_SINGLE, "two"));
    }

    @Test
    void timepointRoundTrips() {
        for (String stored : new String[]{"0", "4", "99"}) {
            String shown = TrackingPanel.timepointTextFor(stored);
            assertEquals(stored, TrackingPanel.timeSelectFor(TrackingPanel.MODE_SINGLE, shown));
        }
    }

    // --- process-all flag ----------------------------------------------------

    @Test
    void onlyAllModeSetsProcessAllTimepoints() {
        assertEquals("true", TrackingPanel.processAllFor(TrackingPanel.MODE_ALL));
        assertEquals("false", TrackingPanel.processAllFor(TrackingPanel.MODE_FIRST));
        assertEquals("false", TrackingPanel.processAllFor(TrackingPanel.MODE_SINGLE));
    }

    // --- linking distance: -1 is the "automatic" sentinel --------------------

    @Test
    void negativeLinkingDistanceMeansAutomatic() {
        assertEquals(TrackingPanel.LINK_AUTO, TrackingPanel.linkingModeFor("-1"));
        assertEquals(TrackingPanel.LINK_AUTO, TrackingPanel.linkingModeFor(null));
        assertEquals(TrackingPanel.LINK_AUTO, TrackingPanel.linkingModeFor("0"));
        assertEquals("", TrackingPanel.linkingDistanceTextFor("-1"));
    }

    @Test
    void positiveLinkingDistanceIsShownVerbatim() {
        assertEquals(TrackingPanel.LINK_MANUAL, TrackingPanel.linkingModeFor("12.5"));
        assertEquals("12.5", TrackingPanel.linkingDistanceTextFor("12.5"));
    }

    @Test
    void automaticModeStoresTheSentinel() {
        assertEquals("-1", TrackingPanel.linkingDistanceFor(TrackingPanel.LINK_AUTO, "12.5"));
    }

    @Test
    void manualModeStoresTheTypedDistance() {
        assertEquals("12.5", TrackingPanel.linkingDistanceFor(TrackingPanel.LINK_MANUAL, " 12.5 "));
    }

    @Test
    void manualModeWithNoUsableValueFallsBackToAutomatic() {
        assertEquals("-1", TrackingPanel.linkingDistanceFor(TrackingPanel.LINK_MANUAL, ""));
        assertEquals("-1", TrackingPanel.linkingDistanceFor(TrackingPanel.LINK_MANUAL, "wide"));
        assertEquals("-1", TrackingPanel.linkingDistanceFor(TrackingPanel.LINK_MANUAL, "-3"));
    }

    @Test
    void linkingDistanceRoundTrips() {
        String shown = TrackingPanel.linkingDistanceTextFor("7.25");
        assertEquals("7.25", TrackingPanel.linkingDistanceFor(TrackingPanel.LINK_MANUAL, shown));
    }

    // --- the trap this redesign removes --------------------------------------

    @Test
    void lineageTrackingOnlyAllowedWhenAnalysingAllTimepoints() {
        assertTrue(TrackingPanel.lineageAllowed(TrackingPanel.MODE_ALL));
        assertFalse(TrackingPanel.lineageAllowed(TrackingPanel.MODE_FIRST));
        assertFalse(TrackingPanel.lineageAllowed(TrackingPanel.MODE_SINGLE));
    }

    @Test
    void legacyDefaultCombinationIsNoLongerReachable() {
        // Shipped defaults were TRACK_LINEAGE=true with PROCESS_ALL_TIMEPOINTS=false,
        // which analysed t=0 only and silently produced no tracking output.
        assertFalse(TrackingPanel.lineageAllowed(TrackingPanel.modeFor("false", "-1")));
    }
}
