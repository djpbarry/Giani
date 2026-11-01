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
import net.calm.iaclasslibrary.UIClasses.LayerPanel;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.border.BevelBorder;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Properties;

/**
 * Tracking parameters panel in the {@link GIANIUI}: surfaces the timepoint,
 * lineage-tracking, and tracking-tuning settings as wizard controls.
 */
public class TrackingPanel extends LayerPanel {

    private final JToggleButton processAllTimepointsToggleButton;
    private final JToggleButton trackLineageToggleButton;
    private final JLabel specificTimepointLabel;
    private final JTextField specificTimepointField;
    private final JLabel linkingDistanceLabel;
    private final JTextField linkingDistanceField;
    private final JLabel maxFrameGapLabel;
    private final JTextField maxFrameGapField;

    /**
     * Constructs a TrackingPanel bound to the supplied properties.
     *
     * @param props the parameters governing how the pipeline will run
     * @param img   the image that the pipeline will run on
     */
    public TrackingPanel(Properties props, BioFormatsImg img) {
        super(props, img, null, null, null);
        processAllTimepointsToggleButton = new JToggleButton(GianiDefaultParams.PROCESS_ALL_TIMEPOINTS);
        trackLineageToggleButton = new JToggleButton(GianiDefaultParams.TRACK_LINEAGE);
        specificTimepointLabel = new JLabel(GianiDefaultParams.TIME_SELECT);
        specificTimepointField = new JTextField();
        linkingDistanceLabel = new JLabel(GianiDefaultParams.TRACK_LINKING_DISTANCE);
        linkingDistanceField = new JTextField();
        maxFrameGapLabel = new JLabel(GianiDefaultParams.TRACK_MAX_FRAME_GAP);
        maxFrameGapField = new JTextField();
        initComponents();
        setToolTips();
    }

    private void initComponents() {
        setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        setLayout(new GridBagLayout());

        processAllTimepointsToggleButton.setSelected(Boolean.parseBoolean(props.getProperty(GianiDefaultParams.PROCESS_ALL_TIMEPOINTS)));
        add(processAllTimepointsToggleButton, toggleConstraints(0));

        addLabelField(1, specificTimepointLabel, specificTimepointField, props.getProperty(GianiDefaultParams.TIME_SELECT));

        trackLineageToggleButton.setSelected(Boolean.parseBoolean(props.getProperty(GianiDefaultParams.TRACK_LINEAGE)));
        add(trackLineageToggleButton, toggleConstraints(2));

        addLabelField(3, linkingDistanceLabel, linkingDistanceField, props.getProperty(GianiDefaultParams.TRACK_LINKING_DISTANCE));
        addLabelField(4, maxFrameGapLabel, maxFrameGapField, props.getProperty(GianiDefaultParams.TRACK_MAX_FRAME_GAP));
    }

    private void addLabelField(int gridy, JLabel label, JTextField field, String value) {
        label.setLabelFor(field);
        GridBagConstraints labelConstraints = new GridBagConstraints();
        labelConstraints.gridx = 0;
        labelConstraints.gridy = gridy;
        labelConstraints.fill = GridBagConstraints.HORIZONTAL;
        labelConstraints.weightx = 1.0;
        labelConstraints.weighty = 1.0;
        labelConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        add(label, labelConstraints);

        field.setText(value);
        GridBagConstraints fieldConstraints = new GridBagConstraints();
        fieldConstraints.gridx = 1;
        fieldConstraints.gridy = gridy;
        fieldConstraints.fill = GridBagConstraints.HORIZONTAL;
        fieldConstraints.weightx = 1.0;
        fieldConstraints.weighty = 1.0;
        fieldConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        add(field, fieldConstraints);
    }

    private GridBagConstraints toggleConstraints(int gridy) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = gridy;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.insets = new java.awt.Insets(10, 10, 10, 10);
        return gbc;
    }

    @Override
    public void setupProcess() {
        // no pipeline process to configure
    }

    @Override
    protected void setToolTips() {
        processAllTimepointsToggleButton.setToolTipText("Analyse every timepoint in the selected file, not just the first.");
        trackLineageToggleButton.setToolTipText("Track cell lineages across timepoints using TrackMate. "
                + "Requires more than one timepoint to be analysed.");
        specificTimepointField.setToolTipText("Leave at -1 to follow the Process All Timepoints setting; "
                + "otherwise the 0-based timepoint index to analyse.");
        linkingDistanceField.setToolTipText("Maximum allowed cell movement between consecutive frames, in physical units. "
                + "Leave at -1 to derive it automatically from the median nucleus size.");
        maxFrameGapField.setToolTipText("Maximum number of frames a track may be missing before it is considered interrupted.");
    }
}
