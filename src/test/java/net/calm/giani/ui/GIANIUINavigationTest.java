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

import net.calm.giani.TestFixtures;
import net.calm.giani.exec.PipelineBuilder;
import net.calm.giani.gianiparams.GianiDefaultParams;
import net.calm.iaclasslibrary.IO.BioFormats.BioFormatsImg;
import net.calm.iaclasslibrary.UIClasses.LayerPanel;
import net.calm.iaclasslibrary.UIClasses.PropertyExtractor;
import net.calm.iaclasslibrary.UIClasses.Updateable;
import org.junit.jupiter.api.Test;

import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JPanel;
import java.awt.Container;
import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Wizard navigation hides every panel and then calls {@code update()} on the one
 * it shows. Several panels' {@code update()} rebuilds a combo or list from
 * {@code props} and re-selects from it, and nothing collected the panel being
 * left, so Next then Previous silently discarded an uncollected edit. An audit
 * (2026-11-27) found it on the channel combos of the Filtering and nuclear Maxima
 * Finder panels. {@code GIANIUI.navigate} now collects every visible
 * {@code Updateable} panel's bound controls before anything is hidden.
 *
 * <p>The structural tests use stub panels and pin the <em>order</em> of events,
 * which is the invariant that matters: collect, then hide, then update the next
 * panel. The end-to-end tests use the real panels on the 3-channel fixture.
 */
class GIANIUINavigationTest {

    private static final String FIXTURE = "test_data/single_timepoint/18.06.10_Experiment.lif_7.ome.tiff";

    // --- stub panels that record what navigation does to them ----------------

    private static class Stub extends LayerPanel {
        final String name;
        final List<String> log;

        Stub(String name, List<String> log) {
            this.name = name;
            this.log = log;
        }

        @Override
        public void setupProcess() {
        }

        @Override
        protected void setToolTips() {
        }

        @Override
        public void setProperties(Properties p, Container c) {
            log.add("collect " + name);
        }

        @Override
        public void setVisible(boolean visible) {
            if (isVisible() != visible) {
                log.add((visible ? "show " : "hide ") + name);
            }
            super.setVisible(visible);
        }
    }

    private static class UpdateableStub extends Stub implements Updateable {
        UpdateableStub(String name, List<String> log) {
            super(name, log);
        }

        @Override
        public void update() {
            log.add("update " + name);
        }
    }

    @Test
    void aVisibleUpdateablePanelIsCollectedBeforeItIsHiddenAndBeforeTheNextIsUpdated() {
        List<String> log = new ArrayList<>();
        Stub leaving = new UpdateableStub("A", log);
        Stub arriving = new UpdateableStub("B", log);
        arriving.setVisible(false);
        log.clear();

        GIANIUI.navigate(List.of(leaving, arriving), arriving, new Properties());

        assertEquals(List.of("collect A", "hide A", "show B", "update B"), log);
    }

    @Test
    void aPanelThatIsNotVisibleIsNotCollected() {
        // A hidden panel's controls are stale if props changed meanwhile; writing
        // them back would undo that change.
        List<String> log = new ArrayList<>();
        Stub hidden = new UpdateableStub("A", log);
        Stub arriving = new UpdateableStub("B", log);
        hidden.setVisible(false);
        arriving.setVisible(false);
        log.clear();

        GIANIUI.navigate(List.of(hidden, arriving), arriving, new Properties());

        assertEquals(List.of("show B", "update B"), log);
    }

    @Test
    void aVisiblePanelThatIsNotUpdateableIsNotCollected() {
        // Only panels whose update() re-reads props need collecting before they are
        // left; the rest are collected at Run, as before.
        List<String> log = new ArrayList<>();
        Stub plain = new Stub("P", log);
        Stub arriving = new UpdateableStub("U", log);
        arriving.setVisible(false);
        log.clear();

        GIANIUI.navigate(List.of(plain, arriving), arriving, new Properties());

        assertEquals(List.of("hide P", "show U", "update U"), log);
    }

    @Test
    void showingNothingStillCollectsAndHides() {
        List<String> log = new ArrayList<>();
        Stub leaving = new UpdateableStub("A", log);

        GIANIUI.navigate(List.of(leaving), null, new Properties());

        assertEquals(List.of("collect A", "hide A"), log);
    }

    // --- the real panels, on the 3-channel fixture ----------------------------

    @SuppressWarnings("unchecked")
    private static JComboBox<String> channelCombo(LayerPanel panel) throws Exception {
        Field field = panel.getClass().getDeclaredField("channelSelectComboBox");
        field.setAccessible(true);
        return (JComboBox<String>) field.get(panel);
    }

    private static BioFormatsImg fixtureImage() throws Exception {
        File fixture = TestFixtures.require(FIXTURE);
        BioFormatsImg img = new BioFormatsImg();
        img.setId(fixture.getAbsolutePath());
        return img;
    }

    @Test
    void aChannelChosenOnTheFilteringPanelSurvivesNextThenPrevious() throws Exception {
        try (BioFormatsImg img = fixtureImage()) {
            Properties props = new GianiDefaultParams();
            String key = GianiDefaultParams.NUC_SEG_CHAN_SELECT_LABEL;
            LayerPanel filtering = new FilteringPanel(props, img, null, PipelineBuilder.getNucFilteringPropLabels(),
                    null, GianiDefaultParams.NUC_GAUSS_FILTER_TITLE);
            LayerPanel next = new UpdateableStub("next", new ArrayList<>());
            List<LayerPanel> all = List.of(filtering, next);

            GIANIUI.navigate(all, filtering, props);        // first entry populates the combo
            channelCombo(filtering).setSelectedItem("2");   // the user picks a channel
            GIANIUI.navigate(all, next, props);             // Next
            GIANIUI.navigate(all, filtering, props);        // Previous

            assertEquals("2", channelCombo(filtering).getSelectedItem(), "the edit must survive re-entry");
            assertEquals("2", props.getProperty(key));
        }
    }

    @Test
    void aChannelChosenOnTheNuclearMaximaFinderSurvivesNextThenPrevious() throws Exception {
        try (BioFormatsImg img = fixtureImage()) {
            Properties props = new GianiDefaultParams();
            String key = GianiDefaultParams.BLOB_NUC_CHAN_SELECT_LABEL;
            LayerPanel maxima = new MaximaFinderPanel(props, img, null, PipelineBuilder.getMaximaFinderPropLabels(),
                    true, -1, null, "test");
            LayerPanel next = new UpdateableStub("next", new ArrayList<>());
            List<LayerPanel> all = List.of(maxima, next);

            GIANIUI.navigate(all, maxima, props);
            channelCombo(maxima).setSelectedItem("2");
            GIANIUI.navigate(all, next, props);
            GIANIUI.navigate(all, maxima, props);

            assertEquals("2", channelCombo(maxima).getSelectedItem());
            assertEquals("2", props.getProperty(key));
        }
    }

    @Test
    void aParametersLoadWhileOnAPanelIsNotUndoneByLeavingIt() throws Exception {
        try (BioFormatsImg img = fixtureImage()) {
            Properties props = new GianiDefaultParams();
            String key = GianiDefaultParams.NUC_SEG_CHAN_SELECT_LABEL;
            LayerPanel filtering = new FilteringPanel(props, img, null, PipelineBuilder.getNucFilteringPropLabels(),
                    null, GianiDefaultParams.NUC_GAUSS_FILTER_TITLE);
            JPanel parent = new JPanel();
            parent.add(filtering);
            LayerPanel next = new UpdateableStub("next", new ArrayList<>());
            List<LayerPanel> all = List.of(filtering, next);

            GIANIUI.navigate(all, filtering, props);
            channelCombo(filtering).setSelectedItem("2");        // an earlier, uncollected edit

            // Load Parameters replaces the stored value and refreshes every panel.
            props.setProperty(key, "1");
            PropertyExtractor.setProperties(props, parent, PropertyExtractor.READ);
            GIANIUI.navigate(all, next, props);                  // leave

            assertEquals("1", props.getProperty(key), "the loaded value must win over the earlier edit");
        }
    }

    @Test
    void aChannelSelectionMadeOnTheMeasurementPanelSurvivesNextThenPrevious() throws Exception {
        // In the wizard, mouse clicks on this list are forwarded to GIANIUI, which
        // collects the whole frame, so it is probably protected there; a
        // keyboard-only change is not. Either way the list must survive navigation.
        try (BioFormatsImg img = fixtureImage()) {
            Properties props = new GianiDefaultParams();
            MeasurementPanel measurement = new MeasurementPanel(props, img, null, PipelineBuilder.getMeasurePropLabels(), null);
            LayerPanel next = new UpdateableStub("next", new ArrayList<>());
            List<LayerPanel> all = List.of(measurement, next);
            Field listField = MeasurementPanel.class.getDeclaredField("channelList");
            listField.setAccessible(true);
            @SuppressWarnings("unchecked") JList<String> list = (JList<String>) listField.get(measurement);

            GIANIUI.navigate(all, measurement, props);
            list.setSelectedIndices(new int[]{1, 2});          // channels 1 and 2
            GIANIUI.navigate(all, next, props);
            GIANIUI.navigate(all, measurement, props);

            assertEquals("[1, 2]", java.util.Arrays.toString(list.getSelectedIndices()));
            assertEquals("6", props.getProperty(GianiDefaultParams.CHAN_FOR_MEASURE), "channels 1 and 2 as a bitmask");
        }
    }
}
