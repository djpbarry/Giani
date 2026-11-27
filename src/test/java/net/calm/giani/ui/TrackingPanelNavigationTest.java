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

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import java.lang.reflect.Field;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Wizard navigation hides every panel and then calls {@code update()} on the one
 * it shows ({@code GIANIUI.updateLayer}). {@code TrackingPanel.update()} rebuilds
 * its controls from {@code props}, and nothing collected the panel when it was
 * left, so Next then Previous silently discarded edits (found 2026-11-27: the
 * linking mode snapped back to Automatic and the distance was cleared). The panel
 * now collects its own state when it goes from visible to hidden.
 *
 * <p>{@code GIANIUI} is a {@code JFrame} and cannot be built headless, so this
 * drives the same sequence of {@code setVisible} and {@code update()} calls on
 * the panel directly.
 */
class TrackingPanelNavigationTest {

    private static final String PROCESS_ALL = GianiDefaultParams.PROCESS_ALL_TIMEPOINTS;
    private static final String TIME_SELECT = GianiDefaultParams.TIME_SELECT;
    private static final String LINEAGE = GianiDefaultParams.TRACK_LINEAGE;
    private static final String LINKING = GianiDefaultParams.TRACK_LINKING_DISTANCE;
    private static final String MAX_MISSING = GianiDefaultParams.TRACK_MAX_FRAME_GAP;

    // The controls are private; reach them reflectively rather than widen the
    // production API for a test.
    @SuppressWarnings("unchecked")
    private static <T> T control(TrackingPanel panel, String name) throws Exception {
        Field field = TrackingPanel.class.getDeclaredField(name);
        field.setAccessible(true);
        return (T) field.get(panel);
    }

    /** The edits a user makes on the panel: analyse everything, track, and set a manual distance. */
    private static void userEdits(TrackingPanel panel) throws Exception {
        JComboBox<String> mode = control(panel, "timepointsModeCombo");
        JToggleButton lineage = control(panel, "trackLineageToggleButton");
        JComboBox<String> linkingMode = control(panel, "linkingModeCombo");
        JTextField distance = control(panel, "linkingDistanceField");
        JTextField maxMissing = control(panel, "maxMissingField");

        mode.setSelectedItem(TrackingPanel.MODE_ALL);
        lineage.setSelected(true);
        linkingMode.setSelectedItem(TrackingPanel.LINK_MANUAL);
        distance.setText("7.5");
        maxMissing.setText("4");
    }

    private static JPanel parentOf(TrackingPanel panel) {
        JPanel parent = new JPanel();
        parent.add(panel);
        return parent;
    }

    @Test
    void editsSurviveLeavingAndReEnteringThePanel() throws Exception {
        Properties props = new GianiDefaultParams();
        TrackingPanel panel = new TrackingPanel(props, new BioFormatsImg());
        parentOf(panel);
        panel.setVisible(true);
        userEdits(panel);

        panel.setVisible(false);   // Next: updateLayer hides every panel...
        panel.setVisible(true);    // ...Previous: shows this one again...
        panel.update();            // ...and calls update() on it.

        JComboBox<String> mode = control(panel, "timepointsModeCombo");
        JComboBox<String> linkingMode = control(panel, "linkingModeCombo");
        JTextField distance = control(panel, "linkingDistanceField");
        JTextField maxMissing = control(panel, "maxMissingField");
        assertEquals(TrackingPanel.MODE_ALL, mode.getSelectedItem());
        assertEquals(TrackingPanel.LINK_MANUAL, linkingMode.getSelectedItem());
        assertEquals("7.5", distance.getText());
        assertEquals("4", maxMissing.getText());

        assertEquals("true", props.getProperty(PROCESS_ALL));
        assertEquals("-1", props.getProperty(TIME_SELECT));
        assertEquals("true", props.getProperty(LINEAGE));
        assertEquals("7.5", props.getProperty(LINKING));
        assertEquals("4", props.getProperty(MAX_MISSING));
    }

    @Test
    void aPanelThatIsAlreadyHiddenNeverWritesToProps() {
        // updateLayer hides every panel on every navigation, hidden ones included.
        // Only the panel actually being left may collect: a hidden panel's controls
        // are stale if props was changed meanwhile, and writing them back would
        // undo that change.
        Properties props = new GianiDefaultParams();
        TrackingPanel panel = new TrackingPanel(props, new BioFormatsImg());
        parentOf(panel);
        panel.setVisible(false);   // the one real leave

        props.setProperty(PROCESS_ALL, "true");
        props.setProperty(LINKING, "9");
        panel.setVisible(false);   // a later navigation hides it again

        assertEquals("true", props.getProperty(PROCESS_ALL));
        assertEquals("9", props.getProperty(LINKING));
    }

    @Test
    void leavingThePanelDoesNotUndoAParametersLoad() throws Exception {
        Properties props = new GianiDefaultParams();
        TrackingPanel panel = new TrackingPanel(props, new BioFormatsImg());
        JPanel parent = parentOf(panel);
        panel.setVisible(true);
        userEdits(panel);

        // Load Parameters replaces the stored values and refreshes every panel.
        props.setProperty(PROCESS_ALL, "false");
        props.setProperty(TIME_SELECT, "-1");
        props.setProperty(LINKING, "-1");
        props.setProperty(MAX_MISSING, "2");
        PropertyExtractor.setProperties(props, parent, PropertyExtractor.READ);

        panel.setVisible(false);

        assertEquals("false", props.getProperty(PROCESS_ALL), "the loaded value must win over the earlier edit");
        assertEquals("-1", props.getProperty(LINKING));
        assertEquals("2", props.getProperty(MAX_MISSING));
        JComboBox<String> mode = control(panel, "timepointsModeCombo");
        assertTrue(TrackingPanel.MODE_FIRST.equals(mode.getSelectedItem()));
    }
}
