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

import net.calm.giani.gianiparams.GIANIParamInfos;
import net.calm.giani.gianiparams.GianiDefaultParams;
import net.calm.iaclasslibrary.IO.BioFormats.BioFormatsImg;
import net.calm.iaclasslibrary.UIClasses.LayerPanel;
import net.calm.iaclasslibrary.UIClasses.Updateable;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.border.BevelBorder;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Properties;

/**
 * Tracking parameters panel in the {@link GIANIUI}.
 *
 * <p><b>Why the controls are not bound directly to parameter keys.</b>
 * {@code PropertyExtractor} derives a parameter key from a control's visible
 * text — a {@link JToggleButton}'s label, or the {@link JLabel} that
 * {@code setLabelFor}s a field or combo — and that key/value pair is what lands
 * in {@code properties.xml}. The stored tracking parameters are not in a shape
 * worth showing to a user, though: {@code Specific Timepoint = -1} and
 * {@code Linking Distance = -1} are sentinels meaning "unset", and the timepoint
 * index is 0-based while Fiji counts frames from 1.
 *
 * <p>So the mode combos and their fields are deliberately left *unbound*: their
 * labels do not call {@code setLabelFor}, which makes them invisible to
 * {@code PropertyExtractor}. This panel translates them to and from the existing
 * keys itself, in {@link #setVariables()} and {@link #update()}. The storage
 * format is therefore unchanged, so old parameter files, the headless CLI and
 * {@code Ext.GIANI.setProperty} all keep working untouched.
 *
 * <p>Binding them would also not work: {@code PropertyExtractor} recurses into a
 * panel <em>after</em> calling its {@code update()}, so a raw read of a bound
 * control always overwrites whatever {@code update()} had just derived.
 */
public class TrackingPanel extends LayerPanel implements Updateable {

    /** Analyse only the first timepoint — the legacy default. */
    static final String MODE_FIRST = "First timepoint only";
    /** Analyse every timepoint; the only mode in which lineage tracking works. */
    static final String MODE_ALL = "All timepoints";
    /** Analyse one chosen timepoint. */
    static final String MODE_SINGLE = "A single timepoint";

    /** Derive the linking distance from the median nucleus size. */
    static final String LINK_AUTO = "Automatic";
    /** Use the distance typed into the field. */
    static final String LINK_MANUAL = "Specify below";

    private final JLabel timepointsModeLabel;
    private final JComboBox<String> timepointsModeCombo;
    private final JLabel timepointLabel;
    private final JTextField timepointField;
    private final JToggleButton trackLineageToggleButton;
    private final JLabel linkingModeLabel;
    private final JComboBox<String> linkingModeCombo;
    private final JLabel linkingDistanceLabel;
    private final JTextField linkingDistanceField;
    private final JLabel linkingUnitsLabel;
    private final JLabel maxMissingLabel;
    private final JTextField maxMissingField;
    private final GIANIParamInfos info;

    /**
     * Constructs a TrackingPanel bound to the supplied properties.
     *
     * @param props the parameters governing how the pipeline will run
     * @param img   the image that the pipeline will run on
     */
    public TrackingPanel(Properties props, BioFormatsImg img) {
        super(props, img, null, null, null);
        timepointsModeLabel = new JLabel(GianiDefaultParams.TIMEPOINTS_MODE_LABEL);
        timepointsModeCombo = new JComboBox<>(new String[]{MODE_FIRST, MODE_ALL, MODE_SINGLE});
        timepointLabel = new JLabel(GianiDefaultParams.TIMEPOINT_LABEL);
        timepointField = new JTextField();
        trackLineageToggleButton = new JToggleButton(GianiDefaultParams.TRACK_LINEAGE);
        linkingModeLabel = new JLabel(GianiDefaultParams.LINKING_MODE_LABEL);
        linkingModeCombo = new JComboBox<>(new String[]{LINK_AUTO, LINK_MANUAL});
        linkingDistanceLabel = new JLabel(GianiDefaultParams.LINKING_VALUE_LABEL);
        linkingDistanceField = new JTextField();
        linkingUnitsLabel = new JLabel(unitsText(props));
        maxMissingLabel = new JLabel(GianiDefaultParams.TRACK_MAX_FRAME_GAP);
        maxMissingField = new JTextField();
        this.info = new GIANIParamInfos();
        initComponents();
        setToolTips();
    }

    private static String unitsText(Properties props) {
        String units = props == null ? null : props.getProperty(GianiDefaultParams.UNITS);
        return units == null || units.isBlank() ? "" : units;
    }

    private void initComponents() {
        setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        setLayout(new GridBagLayout());

        addRow(0, timepointsModeLabel, timepointsModeCombo, null);
        addRow(1, timepointLabel, timepointField, null);

        trackLineageToggleButton.setSelected(Boolean.parseBoolean(props.getProperty(GianiDefaultParams.TRACK_LINEAGE)));
        add(trackLineageToggleButton, toggleConstraints(2));

        addRow(3, linkingModeLabel, linkingModeCombo, null);
        addRow(4, linkingDistanceLabel, linkingDistanceField, linkingUnitsLabel);

        // The only control bound to its key in the usual way: a plain integer
        // with no sentinel, so the stored value is also the sensible display.
        maxMissingLabel.setLabelFor(maxMissingField);
        addRow(5, maxMissingLabel, maxMissingField, null);

        timepointsModeCombo.addActionListener(e -> syncEnabledState());
        linkingModeCombo.addActionListener(e -> syncEnabledState());

        readFromProps();
    }

    private void addRow(int gridy, JLabel label, Component field, Component suffix) {
        GridBagConstraints labelConstraints = cell(0, gridy);
        add(label, labelConstraints);
        add(field, cell(1, gridy));
        if (suffix != null) {
            add(suffix, cell(2, gridy));
        }
    }

    private static GridBagConstraints cell(int gridx, int gridy) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = gridx;
        gbc.gridy = gridy;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.insets = new java.awt.Insets(10, 10, 10, 10);
        return gbc;
    }

    private static GridBagConstraints toggleConstraints(int gridy) {
        GridBagConstraints gbc = cell(0, gridy);
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.LINE_START;
        return gbc;
    }

    /**
     * Enables only the fields that the current modes actually use, and forces
     * lineage tracking off when the chosen mode cannot produce it.
     *
     * <p>Disabling the toggle is not enough on its own: {@code PropertyExtractor}
     * harvests disabled controls too (its {@code isEnabled} check is commented
     * out), so a disabled-but-selected toggle would still write
     * {@code Track Lineage=true} and reproduce exactly the trap this redesign
     * removes — tracking switched on, no tracking output.
     */
    private void syncEnabledState() {
        String mode = selected(timepointsModeCombo, MODE_FIRST);
        timepointLabel.setEnabled(MODE_SINGLE.equals(mode));
        timepointField.setEnabled(MODE_SINGLE.equals(mode));

        boolean lineagePossible = lineageAllowed(mode);
        if (!lineagePossible) {
            trackLineageToggleButton.setSelected(false);
        }
        trackLineageToggleButton.setEnabled(lineagePossible);

        boolean manual = LINK_MANUAL.equals(selected(linkingModeCombo, LINK_AUTO));
        boolean linkingRelevant = lineagePossible && trackLineageToggleButton.isSelected();
        linkingModeLabel.setEnabled(linkingRelevant);
        linkingModeCombo.setEnabled(linkingRelevant);
        linkingDistanceLabel.setEnabled(linkingRelevant && manual);
        linkingDistanceField.setEnabled(linkingRelevant && manual);
        linkingUnitsLabel.setEnabled(linkingRelevant && manual);
        maxMissingLabel.setEnabled(linkingRelevant);
        maxMissingField.setEnabled(linkingRelevant);
    }

    private static String selected(JComboBox<String> combo, String fallback) {
        Object value = combo.getSelectedItem();
        return value == null ? fallback : value.toString();
    }

    /** Sets the unbound controls from the stored parameter values. */
    private void readFromProps() {
        timepointsModeCombo.setSelectedItem(modeFor(
                props.getProperty(GianiDefaultParams.PROCESS_ALL_TIMEPOINTS),
                props.getProperty(GianiDefaultParams.TIME_SELECT)));
        timepointField.setText(timepointTextFor(props.getProperty(GianiDefaultParams.TIME_SELECT)));
        String linkingDistance = props.getProperty(GianiDefaultParams.TRACK_LINKING_DISTANCE);
        linkingModeCombo.setSelectedItem(linkingModeFor(linkingDistance));
        linkingDistanceField.setText(linkingDistanceTextFor(linkingDistance));
        linkingUnitsLabel.setText(unitsText(props));
        syncEnabledState();
    }

    /** Writes the unbound controls back to the stored parameter values. */
    private void writeToProps() {
        String mode = selected(timepointsModeCombo, MODE_FIRST);
        props.setProperty(GianiDefaultParams.PROCESS_ALL_TIMEPOINTS, processAllFor(mode));
        props.setProperty(GianiDefaultParams.TIME_SELECT, timeSelectFor(mode, timepointField.getText()));
        props.setProperty(GianiDefaultParams.TRACK_LINKING_DISTANCE,
                linkingDistanceFor(selected(linkingModeCombo, LINK_AUTO), linkingDistanceField.getText()));
    }

    @Override
    public boolean setVariables() {
        // Force lineage off first, so super.setVariables() harvests the corrected
        // toggle rather than a stale selection.
        syncEnabledState();
        boolean result = super.setVariables();
        writeToProps();
        return result;
    }

    @Override
    public void update() {
        readFromProps();
    }

    @Override
    public void setupProcess() {
        // no pipeline process to configure
    }

    @Override
    protected void setToolTips() {
        timepointsModeCombo.setToolTipText(info.getProperty(GianiDefaultParams.TIMEPOINTS_MODE_LABEL));
        timepointField.setToolTipText(info.getProperty(GianiDefaultParams.TIMEPOINT_LABEL));
        trackLineageToggleButton.setToolTipText(info.getProperty(GianiDefaultParams.TRACK_LINEAGE));
        linkingModeCombo.setToolTipText(info.getProperty(GianiDefaultParams.LINKING_MODE_LABEL));
        linkingDistanceField.setToolTipText(info.getProperty(GianiDefaultParams.LINKING_VALUE_LABEL));
        maxMissingField.setToolTipText(info.getProperty(GianiDefaultParams.TRACK_MAX_FRAME_GAP));
    }

    // ---------------------------------------------------------------------
    // Pure value mapping between the stored keys and the displayed controls.
    // Kept static and free of Swing so it can be unit tested directly.
    // ---------------------------------------------------------------------

    /**
     * Which timepoint mode the stored values represent. A valid
     * {@code TIME_SELECT} wins over {@code PROCESS_ALL_TIMEPOINTS}, matching
     * {@code PipelineExecutor.timeRange}.
     */
    static String modeFor(String processAllTimepoints, String timeSelect) {
        if (parseInt(timeSelect, -1) >= 0) {
            return MODE_SINGLE;
        }
        return Boolean.parseBoolean(processAllTimepoints) ? MODE_ALL : MODE_FIRST;
    }

    /** 0-based stored index to the 1-based number shown to the user. */
    static String timepointTextFor(String timeSelect) {
        int t = parseInt(timeSelect, -1);
        return t >= 0 ? String.valueOf(t + 1) : "";
    }

    static String processAllFor(String mode) {
        return String.valueOf(MODE_ALL.equals(mode));
    }

    /** 1-based display back to the 0-based stored index; {@code -1} when unset. */
    static String timeSelectFor(String mode, String oneBasedText) {
        if (!MODE_SINGLE.equals(mode)) {
            return "-1";
        }
        int t = parseInt(oneBasedText, 0);
        return t >= 1 ? String.valueOf(t - 1) : "-1";
    }

    static String linkingModeFor(String linkingDistance) {
        return parseDouble(linkingDistance) > 0 ? LINK_MANUAL : LINK_AUTO;
    }

    /** Echoes the stored text verbatim when it is a real distance, so it round-trips. */
    static String linkingDistanceTextFor(String linkingDistance) {
        return parseDouble(linkingDistance) > 0 ? linkingDistance.trim() : "";
    }

    static String linkingDistanceFor(String linkingMode, String text) {
        if (!LINK_MANUAL.equals(linkingMode)) {
            return "-1";
        }
        return parseDouble(text) > 0 ? text.trim() : "-1";
    }

    /** Lineage tracking needs every timepoint; any other mode yields no output. */
    static boolean lineageAllowed(String mode) {
        return MODE_ALL.equals(mode);
    }

    private static int parseInt(String value, int fallback) {
        try {
            return Integer.parseInt(value == null ? "" : value.trim());
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    private static double parseDouble(String value) {
        try {
            return Double.parseDouble(value == null ? "" : value.trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
