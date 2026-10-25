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
 * Tracking parameters panel in the {@link GIANIUI}: surfaces the timelapse,
 * lineage-tracking, and specific-timepoint settings as wizard controls.
 */
public class TrackingPanel extends LayerPanel {

    private final JToggleButton trackTimelapseToggleButton;
    private final JToggleButton trackLineageToggleButton;
    private final JLabel specificTimepointLabel;
    private final JTextField specificTimepointField;

    /**
     * Constructs a TrackingPanel bound to the supplied properties.
     *
     * @param props the parameters governing how the pipeline will run
     * @param img   the image that the pipeline will run on
     */
    public TrackingPanel(Properties props, BioFormatsImg img) {
        super(props, img, null, null, null);
        trackTimelapseToggleButton = new JToggleButton(GianiDefaultParams.TRACK_TIMELAPSE);
        trackLineageToggleButton = new JToggleButton(GianiDefaultParams.TRACK_LINEAGE);
        specificTimepointLabel = new JLabel(GianiDefaultParams.TIME_SELECT);
        specificTimepointField = new JTextField();
        initComponents();
        setToolTips();
    }

    private void initComponents() {
        setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        setLayout(new GridBagLayout());

        trackTimelapseToggleButton.setSelected(Boolean.parseBoolean(props.getProperty(GianiDefaultParams.TRACK_TIMELAPSE)));
        GridBagConstraints gbc = toggleConstraints(0);
        add(trackTimelapseToggleButton, gbc);

        trackLineageToggleButton.setSelected(Boolean.parseBoolean(props.getProperty(GianiDefaultParams.TRACK_LINEAGE)));
        add(trackLineageToggleButton, toggleConstraints(1));

        specificTimepointLabel.setLabelFor(specificTimepointField);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.insets = new java.awt.Insets(10, 10, 10, 10);
        add(specificTimepointLabel, gbc);

        specificTimepointField.setText(props.getProperty(GianiDefaultParams.TIME_SELECT));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.insets = new java.awt.Insets(10, 10, 10, 10);
        add(specificTimepointField, gbc);
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
        trackTimelapseToggleButton.setToolTipText("Process every timepoint in the selected file.");
        trackLineageToggleButton.setToolTipText("Track cell lineages across timepoints using TrackMate.");
        specificTimepointField.setToolTipText("Leave at -1 to process all timepoints (with Track Timelapse on) "
                + "or the first frame (off); otherwise the 0-based timepoint index to process.");
    }
}
