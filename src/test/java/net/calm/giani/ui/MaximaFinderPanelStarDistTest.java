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

import net.calm.giani.exec.PipelineBuilder;
import net.calm.giani.gianiparams.GianiDefaultParams;
import net.calm.iaclasslibrary.Extrema.MultiThreadedMaximaFinder;
import net.calm.iaclasslibrary.IO.BioFormats.BioFormatsImg;
import net.calm.iaclasslibrary.UIClasses.PropertyExtractor;
import org.junit.jupiter.api.Test;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * {@code PropertyExtractor} writes each text field's value under the key named by
 * the {@code JLabel} that {@code setLabelFor}s it, and reads it back the same
 * way. So a panel is only correct if every label carries the key of the
 * parameter its field is <em>initialised from</em>. The StarDist probability and
 * overlap labels used to carry each other's keys, so the first collect of panel
 * state silently swapped the two thresholds.
 *
 * <p>These tests build the panel and round-trip its state through
 * {@code PropertyExtractor}: whatever went in must come back under the same key.
 */
class MaximaFinderPanelStarDistTest {

    @Test
    void nuclearStarDistThresholdsSurviveACollectOfPanelState() {
        String[] propLabels = PipelineBuilder.getMaximaFinderPropLabels();
        Properties props = new GianiDefaultParams();
        props.setProperty(GianiDefaultParams.STARDIST_PROB_THRESH, "0.81");
        props.setProperty(GianiDefaultParams.STARDIST_OVERLAP_THRESH, "0.33");

        MaximaFinderPanel panel = new MaximaFinderPanel(props, new BioFormatsImg(), null,
                propLabels, true, -1, null, "test");
        PropertyExtractor.setProperties(props, panel, PropertyExtractor.WRITE);

        assertEquals("0.81", props.getProperty(GianiDefaultParams.STARDIST_PROB_THRESH),
                "probability threshold was written under the wrong key");
        assertEquals("0.33", props.getProperty(GianiDefaultParams.STARDIST_OVERLAP_THRESH),
                "overlap threshold was written under the wrong key");
    }

    @Test
    void defaultStarDistThresholdsAreNotSwappedByTheFirstCollect() {
        // The shipped defaults are probability 0.75 and overlap 0.6; untouched,
        // they must still be those after the panel has been collected once.
        String[] propLabels = PipelineBuilder.getMaximaFinderPropLabels();
        Properties props = new GianiDefaultParams();

        MaximaFinderPanel panel = new MaximaFinderPanel(props, new BioFormatsImg(), null,
                propLabels, true, -1, null, "test");
        PropertyExtractor.setProperties(props, panel, PropertyExtractor.WRITE);

        assertEquals("0.75", props.getProperty(GianiDefaultParams.STARDIST_PROB_THRESH));
        assertEquals("0.6", props.getProperty(GianiDefaultParams.STARDIST_OVERLAP_THRESH));
    }

    /**
     * Every parameter shown in a text field, nuclear and per-channel alike.
     * Giving each a distinct value makes any crossed label/key pair show up as a
     * value turning up under the wrong key, which a single pair of thresholds
     * (as above) cannot reveal for the other fields.
     */
    private static final int[] TEXT_FIELD_PARAMS = {
            MultiThreadedMaximaFinder.BLOB_SIZE,
            MultiThreadedMaximaFinder.BLOB_THRESH,
            MultiThreadedMaximaFinder.HESSIAN_START_SCALE,
            MultiThreadedMaximaFinder.HESSIAN_THRESH,
            MultiThreadedMaximaFinder.STARDIST_PROB,
            MultiThreadedMaximaFinder.STARDIST_OVERLAP,
            MultiThreadedMaximaFinder.STARDIST_DIR,
            MultiThreadedMaximaFinder.STARDIST_MODEL,
            MultiThreadedMaximaFinder.STARDIST_TILE_XY,
            MultiThreadedMaximaFinder.STARDIST_TILE_Z,
            MultiThreadedMaximaFinder.ILASTIK_FILE,
            MultiThreadedMaximaFinder.ILASTIK_CHANNEL,
            MultiThreadedMaximaFinder.ILASTIK_DIR,
            MultiThreadedMaximaFinder.ILASTIK_THRESH,
            MultiThreadedMaximaFinder.ILASTIK_SMOOTHING
    };

    @Test
    void everyNuclearTextFieldParameterSurvivesACollectOfPanelState() {
        assertEveryTextFieldParameterSurvives(PipelineBuilder.getMaximaFinderPropLabels(), true, -1);
    }

    @Test
    void everyPerChannelTextFieldParameterSurvivesACollectOfPanelState() {
        assertEveryTextFieldParameterSurvives(PipelineBuilder.getFociPropLabels(3), false, 3);
    }

    private static void assertEveryTextFieldParameterSurvives(String[] propLabels, boolean allowChannelSelect, int channel) {
        Properties props = new GianiDefaultParams();
        if (channel >= 0) {
            PipelineBuilder.setDefaultFociParams(props, channel);
        }
        for (int param : TEXT_FIELD_PARAMS) {
            props.setProperty(propLabels[param], "value-" + param);
        }

        MaximaFinderPanel panel = new MaximaFinderPanel(props, new BioFormatsImg(), null,
                propLabels, allowChannelSelect, channel, null, "test");
        PropertyExtractor.setProperties(props, panel, PropertyExtractor.WRITE);

        for (int param : TEXT_FIELD_PARAMS) {
            assertEquals("value-" + param, props.getProperty(propLabels[param]),
                    "'" + propLabels[param] + "' did not survive a collect; its label and field are crossed");
        }
    }

    @Test
    void perChannelStarDistThresholdsSurviveACollectOfPanelState() {
        // The spot panels are built after parameters may already have been loaded,
        // so this is the path where a user's saved values were being swapped.
        int channel = 2;
        String[] propLabels = PipelineBuilder.getFociPropLabels(channel);
        Properties props = new GianiDefaultParams();
        PipelineBuilder.setDefaultFociParams(props, channel);
        props.setProperty(propLabels[MultiThreadedMaximaFinder.STARDIST_PROB], "0.88");
        props.setProperty(propLabels[MultiThreadedMaximaFinder.STARDIST_OVERLAP], "0.22");

        MaximaFinderPanel panel = new MaximaFinderPanel(props, new BioFormatsImg(), null,
                propLabels, false, channel, null, "test");
        PropertyExtractor.setProperties(props, panel, PropertyExtractor.WRITE);

        assertEquals("0.88", props.getProperty(propLabels[MultiThreadedMaximaFinder.STARDIST_PROB]));
        assertEquals("0.22", props.getProperty(propLabels[MultiThreadedMaximaFinder.STARDIST_OVERLAP]));
    }
}
