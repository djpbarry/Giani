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
import net.calm.iaclasslibrary.Extrema.MultiThreadedMaximaFinder;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * TrackMate's LoG detector treats a radius of 0 as "every voxel is a spot", so
 * localising spots with the simple detector and no positive radius silently
 * produces meaningless data (probed 2026-11-26: 65,536 spots on a 64×64×16
 * stack). These pin the default radius and the run-time refusal, including that
 * no other detection method is ever blocked by its unused blob radius.
 */
class SpotRadiusValidationTest {

    /** Parameters for a run that localises spots in the channels set in {@code mask}. */
    private static Properties spotRun(int mask) {
        Properties props = new GianiDefaultParams();
        props.setProperty(GianiDefaultParams.LOCALISE_SPOTS, "true");
        props.setProperty(GianiDefaultParams.CHAN_FOR_MEASURE, String.valueOf(mask));
        for (int i = 0; i < GianiDefaultParams.MAX_CHANNELS; i++) {
            if ((mask & (1 << i)) != 0) {
                PipelineBuilder.setDefaultFociParams(props, i);
            }
        }
        return props;
    }

    private static String key(int channel, int param) {
        return PipelineBuilder.getFociPropLabels(channel)[param];
    }

    private static void setRadius(Properties props, int channel, String radius) {
        props.setProperty(key(channel, MultiThreadedMaximaFinder.BLOB_SIZE), radius);
    }

    // --- the default --------------------------------------------------------

    @Test
    void defaultSpotRadiusIsPositive() {
        Properties props = spotRun(1);
        assertEquals(PipelineBuilder.DEFAULT_SPOT_RADIUS, props.getProperty(key(0, MultiThreadedMaximaFinder.BLOB_SIZE)));
        assertEquals("0.5", PipelineBuilder.DEFAULT_SPOT_RADIUS);
    }

    @Test
    void defaultSettingsPassValidation() {
        assertTrue(PipelineBuilder.spotRadiusProblems(spotRun(0b101)).isEmpty());
    }

    @Test
    void aSavedRadiusIsKeptSoOldAnalysesStayReproducible() {
        Properties props = new GianiDefaultParams();
        setRadius(props, 0, "0.0");
        setRadius(props, 1, "3.2");

        PipelineBuilder.setDefaultFociParams(props, 0);
        PipelineBuilder.setDefaultFociParams(props, 1);

        assertEquals("0.0", props.getProperty(key(0, MultiThreadedMaximaFinder.BLOB_SIZE)));
        assertEquals("3.2", props.getProperty(key(1, MultiThreadedMaximaFinder.BLOB_SIZE)));
    }

    // --- refusing a radius the simple detector cannot use -------------------

    @Test
    void aZeroRadiusWithTheSimpleDetectorIsRefused() {
        Properties props = spotRun(1);
        setRadius(props, 0, "0.0");

        List<String> problems = PipelineBuilder.spotRadiusProblems(props);

        assertEquals(1, problems.size());
        assertTrue(problems.get(0).contains("channel 0"), problems.get(0));
        // Names the on-screen label, which is also the properties.xml key.
        assertTrue(problems.get(0).contains(key(0, MultiThreadedMaximaFinder.BLOB_SIZE)), problems.get(0));
        assertTrue(problems.get(0).contains("\"0.0\""), problems.get(0));
    }

    @Test
    void anyRadiusThatIsNotAPositiveNumberIsRefused() {
        for (String bad : new String[]{"-1", "-0.5", "0", "", "   ", "abc", "NaN", "Infinity"}) {
            Properties props = spotRun(1);
            setRadius(props, 0, bad);
            assertEquals(1, PipelineBuilder.spotRadiusProblems(props).size(), "radius '" + bad + "' should be refused");
        }
    }

    @Test
    void aMissingRadiusIsRefusedAndSaidToBeNotSet() {
        Properties props = spotRun(1);
        props.remove(key(0, MultiThreadedMaximaFinder.BLOB_SIZE));

        List<String> problems = PipelineBuilder.spotRadiusProblems(props);

        assertEquals(1, problems.size());
        assertTrue(problems.get(0).contains("not set"), problems.get(0));
    }

    @Test
    void anyPositiveRadiusIsAccepted() {
        for (String good : new String[]{"0.5", " 2 ", "1e-1", "12.75"}) {
            Properties props = spotRun(1);
            setRadius(props, 0, good);
            assertTrue(PipelineBuilder.spotRadiusProblems(props).isEmpty(), "radius '" + good + "' should be accepted");
        }
    }

    // --- other detection methods are never blocked --------------------------

    @Test
    void noOtherDetectionMethodIsBlockedByAZeroBlobRadius() {
        int[] otherDetectors = {
                MultiThreadedMaximaFinder.THRESH_DETECT,
                MultiThreadedMaximaFinder.HESSIAN_DETECT,
                MultiThreadedMaximaFinder.STARDIST_DETECT,
                MultiThreadedMaximaFinder.ILASTIK_DETECT
        };
        for (int detector : otherDetectors) {
            Properties props = spotRun(1);
            setRadius(props, 0, "0.0");
            props.setProperty(key(0, detector), "true");

            assertFalse(PipelineBuilder.usesSimpleSpotDetector(props, 0), "detector " + detector + " replaces the simple one");
            assertTrue(PipelineBuilder.spotRadiusProblems(props).isEmpty(),
                    "detector " + detector + " does not use the blob radius, so a zero radius must not block it");
        }
    }

    @Test
    void theSimpleDetectorIsUsedOnlyWhenNoOtherDetectorIsSwitchedOn() {
        Properties props = spotRun(1);
        assertTrue(PipelineBuilder.usesSimpleSpotDetector(props, 0));

        // Neither the METHOD string nor the BLOB_DETECT flag takes part in the
        // library's choice (MultiThreadedMaximaFinder.run), so changing them must
        // not change the answer.
        props.setProperty(key(0, MultiThreadedMaximaFinder.METHOD), "Advanced Nuclear Centroid Detector");
        props.setProperty(key(0, MultiThreadedMaximaFinder.BLOB_DETECT), "false");
        assertTrue(PipelineBuilder.usesSimpleSpotDetector(props, 0));
    }

    // --- which runs and channels are checked --------------------------------

    @Test
    void nothingIsCheckedWhenSpotsAreNotLocalised() {
        Properties props = spotRun(1);
        setRadius(props, 0, "0.0");
        props.setProperty(GianiDefaultParams.LOCALISE_SPOTS, "false");

        assertTrue(PipelineBuilder.spotRadiusProblems(props).isEmpty());
    }

    @Test
    void channelsThatAreNotSelectedAreNotChecked() {
        Properties props = spotRun(0b11);
        setRadius(props, 0, "0.0");
        setRadius(props, 1, "0.0");
        props.setProperty(GianiDefaultParams.CHAN_FOR_MEASURE, "2"); // channel 1 only

        List<String> problems = PipelineBuilder.spotRadiusProblems(props);

        assertEquals(1, problems.size());
        assertTrue(problems.get(0).contains("channel 1"), problems.get(0));
    }

    @Test
    void oneMessagePerOffendingChannelInChannelOrder() {
        Properties props = spotRun(0b111);
        setRadius(props, 0, "0.0");
        setRadius(props, 2, "-3");

        List<String> problems = PipelineBuilder.spotRadiusProblems(props);

        assertEquals(2, problems.size());
        assertTrue(problems.get(0).contains("channel 0"), problems.get(0));
        assertTrue(problems.get(1).contains("channel 2"), problems.get(1));
    }

    @Test
    void anUnparseableChannelSelectionIsLeftToTheExistingHandling() {
        Properties props = spotRun(1);
        props.setProperty(GianiDefaultParams.CHAN_FOR_MEASURE, "not a number");

        assertTrue(PipelineBuilder.spotRadiusProblems(props).isEmpty());
    }

    @Test
    void theMessageGivesTheUnitsWhenKnown() {
        Properties props = spotRun(1);
        setRadius(props, 0, "0");
        props.setProperty(GianiDefaultParams.UNITS, "nm");

        assertTrue(PipelineBuilder.spotRadiusProblems(props).get(0).contains("in nm"));
    }
}
