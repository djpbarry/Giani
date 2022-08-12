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

import ij.ImagePlus;
import ij.gui.OvalRoi;
import ij.gui.Overlay;
import java.util.ArrayList;
import java.util.Properties;
import javax.swing.DefaultComboBoxModel;
import ij.gui.Roi;
import java.awt.Color;
import java.net.URI;

import net.calm.giani.gianiparams.GIANIParamInfos;
import net.calm.giani.gianiparams.GianiDefaultParams;
import net.calm.iaclasslibrary.Extrema.MultiThreadedMaximaFinder;
import net.calm.iaclasslibrary.IO.BioFormats.BioFormatsImg;
import net.calm.iaclasslibrary.Process.Segmentation.MultiThreadedStarDist;
import net.calm.iaclasslibrary.UIClasses.LayerPanel;
import net.calm.iaclasslibrary.UIClasses.Updateable;
import ome.units.quantity.Length;

/**
 * Blob detection (centroid estimation) panel in the {@link GIANIUI}
 *
 * @author Dave Barry
 * @since 1.0.0
 */
public class MaximaFinderPanel1 extends LayerPanel implements Updateable {

    private ArrayList<String> channelLabels;
    boolean allowChannelSelect;
    int defaultChannel;
    private final String title;
    private final GIANIParamInfos info;

    /**
     * Default constructor
     */
    public MaximaFinderPanel1() {
        this(null, null, null, null, true, -1, null, null);
    }

    /**
     * Constructs a MaximaFinderPanel and associates the specified Properties,
     * BioFormatsImg and process with it.
     *
     * @param props contains the parameters governing how the process associated
     * with this panel will run
     * @param img the image that the process associated with this panel will run
     * on
     * @param process the process that this panel is seeking user-specified
     * parameters for
     * @param propLabels the labels associated with the parameters that this
     * panel will display
     * @param allowChannelSelect set to true to include a dropdown menu allowing
     * channel selection
     * @param defaultChannel if allowChannelSelect is false, specify the
     * specific channel the process associated with this panel will run on
     * @param helpURI link to an online help page describing how to use this
     * panel
     * @param title description of what this panel does
     */
    public MaximaFinderPanel1(Properties props, BioFormatsImg img, MultiThreadedStarDist process, String[] propLabels, boolean allowChannelSelect, int defaultChannel, URI helpURI, String title) {
        super(props, img, process, propLabels, helpURI);
        this.allowChannelSelect = allowChannelSelect;
        this.defaultChannel = defaultChannel;
        this.title = title;
        this.info = new GIANIParamInfos();
        initComponents();
        setToolTips();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        previewButton = new javax.swing.JButton();
        channelSelectLabel = new javax.swing.JLabel();
        channelSelectComboBox = new javax.swing.JComboBox<>();
        helpButton = new javax.swing.JButton();
        titleLabel = new javax.swing.JLabel();
        methodComboBox = new javax.swing.JComboBox<>();
        methodLabel = new javax.swing.JLabel();
        simpleDetectionPanel = new javax.swing.JPanel();
        blobRadLabel = new javax.swing.JLabel();
        blobRadTextField = new javax.swing.JTextField();
        unitsLabel1 = new javax.swing.JLabel();
        noiseTolTextField = new javax.swing.JTextField();
        noiseTolLabel = new javax.swing.JLabel();
        advancedDetectionPanel = new javax.swing.JPanel();
        hessianMinSizeLabel = new javax.swing.JLabel();
        hessianMaxSizeLabel = new javax.swing.JLabel();
        hessianStepSizeLabel = new javax.swing.JLabel();
        hessianThreshLabel = new javax.swing.JLabel();
        hessianThreshTextField = new javax.swing.JTextField();
        hessianStepSizeTextField = new javax.swing.JTextField();
        hessianMaxSizeTextField = new javax.swing.JTextField();
        hessianMinSizeTextField = new javax.swing.JTextField();
        unitsLabel2 = new javax.swing.JLabel();
        unitsLabel3 = new javax.swing.JLabel();
        unitsLabel4 = new javax.swing.JLabel();
        stardistPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        starDistProbTextField = new javax.swing.JTextField();
        starDistOverlapTextField = new javax.swing.JTextField();

        setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        setLayout(new java.awt.GridBagLayout());

        previewButton.setText("Preview");
        previewButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                previewButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 14;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        add(previewButton, gridBagConstraints);

        channelSelectLabel.setText(propLabels[MultiThreadedMaximaFinder.CHANNEL_SELECT]);
        channelSelectLabel.setLabelFor(channelSelectComboBox);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        add(channelSelectLabel, gridBagConstraints);

        channelSelectComboBox.setModel(new DefaultComboBoxModel(new String[]{}));
        channelSelectComboBox.setVisible(allowChannelSelect);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        add(channelSelectComboBox, gridBagConstraints);

        helpButton.setText("Help");
        helpButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                helpButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 14;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        add(helpButton, gridBagConstraints);

        titleLabel.setFont(GianiDefaultParams.TITLE_FONT);
        titleLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        titleLabel.setText(title);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        add(titleLabel, gridBagConstraints);

        methodComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] {GianiDefaultParams.NUC_MAXIMA_DETECT_BLOBS, GianiDefaultParams.NUC_MAXIMA_DETECT_HESSIAN, GianiDefaultParams.NUC_MAXIMA_DETECT_STARDIST}));
        methodComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                methodComboBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        add(methodComboBox, gridBagConstraints);

        methodLabel.setText(propLabels[MultiThreadedMaximaFinder.METHOD]);
        methodLabel.setLabelFor(methodComboBox);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        add(methodLabel, gridBagConstraints);

        simpleDetectionPanel.setMinimumSize(new java.awt.Dimension(191, 180));
        simpleDetectionPanel.setLayout(new java.awt.GridBagLayout());

        blobRadLabel.setText(propLabels[MultiThreadedMaximaFinder.BLOB_SIZE]);
        blobRadLabel.setLabelFor(blobRadTextField);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        simpleDetectionPanel.add(blobRadLabel, gridBagConstraints);

        blobRadTextField.setText(props.getProperty(propLabels[MultiThreadedMaximaFinder.BLOB_SIZE]));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        simpleDetectionPanel.add(blobRadTextField, gridBagConstraints);

        unitsLabel1.setText(unitText);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        simpleDetectionPanel.add(unitsLabel1, gridBagConstraints);

        noiseTolTextField.setText(props.getProperty(propLabels[MultiThreadedMaximaFinder.BLOB_THRESH]));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        simpleDetectionPanel.add(noiseTolTextField, gridBagConstraints);

        noiseTolLabel.setText(propLabels[MultiThreadedMaximaFinder.BLOB_THRESH]);
        noiseTolLabel.setLabelFor(noiseTolTextField);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        simpleDetectionPanel.add(noiseTolLabel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(simpleDetectionPanel, gridBagConstraints);

        advancedDetectionPanel.setMinimumSize(new java.awt.Dimension(191, 180));
        advancedDetectionPanel.setLayout(new java.awt.GridBagLayout());

        hessianMinSizeLabel.setText(propLabels[MultiThreadedMaximaFinder.HESSIAN_START_SCALE]);
        hessianMinSizeLabel.setLabelFor(hessianMinSizeTextField);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        advancedDetectionPanel.add(hessianMinSizeLabel, gridBagConstraints);

        hessianMaxSizeLabel.setText(propLabels[MultiThreadedMaximaFinder.HESSIAN_STOP_SCALE]);
        hessianMaxSizeLabel.setLabelFor(hessianMaxSizeTextField);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        advancedDetectionPanel.add(hessianMaxSizeLabel, gridBagConstraints);

        hessianStepSizeLabel.setText(propLabels[MultiThreadedMaximaFinder.HESSIAN_SCALE_STEP]);
        hessianStepSizeLabel.setLabelFor(hessianStepSizeTextField);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        advancedDetectionPanel.add(hessianStepSizeLabel, gridBagConstraints);

        hessianThreshLabel.setText(propLabels[MultiThreadedMaximaFinder.HESSIAN_THRESH]);
        hessianThreshLabel.setLabelFor(hessianThreshTextField);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        advancedDetectionPanel.add(hessianThreshLabel, gridBagConstraints);

        hessianThreshTextField.setText(props.getProperty(propLabels[MultiThreadedMaximaFinder.HESSIAN_THRESH]));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        advancedDetectionPanel.add(hessianThreshTextField, gridBagConstraints);

        hessianStepSizeTextField.setText(props.getProperty(propLabels[MultiThreadedMaximaFinder.HESSIAN_SCALE_STEP]));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        advancedDetectionPanel.add(hessianStepSizeTextField, gridBagConstraints);

        hessianMaxSizeTextField.setText(props.getProperty(propLabels[MultiThreadedMaximaFinder.HESSIAN_STOP_SCALE]));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        advancedDetectionPanel.add(hessianMaxSizeTextField, gridBagConstraints);

        hessianMinSizeTextField.setText(props.getProperty(propLabels[MultiThreadedMaximaFinder.HESSIAN_START_SCALE]));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        advancedDetectionPanel.add(hessianMinSizeTextField, gridBagConstraints);

        unitsLabel2.setText(unitText);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        advancedDetectionPanel.add(unitsLabel2, gridBagConstraints);

        unitsLabel3.setText(unitText);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        advancedDetectionPanel.add(unitsLabel3, gridBagConstraints);

        unitsLabel4.setText(unitText);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        advancedDetectionPanel.add(unitsLabel4, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(advancedDetectionPanel, gridBagConstraints);

        stardistPanel.setMinimumSize(new java.awt.Dimension(136, 180));
        stardistPanel.setLayout(new java.awt.GridBagLayout());

        jLabel1.setText(propLabels[MultiThreadedMaximaFinder.STARDIST_OVERLAP]);
        jLabel1.setLabelFor(starDistProbTextField);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        stardistPanel.add(jLabel1, gridBagConstraints);

        jLabel2.setText(propLabels[MultiThreadedMaximaFinder.STARDIST_PROB]);
        jLabel2.setLabelFor(starDistOverlapTextField);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        stardistPanel.add(jLabel2, gridBagConstraints);

        starDistProbTextField.setText(props.getProperty(propLabels[MultiThreadedMaximaFinder.STARDIST_PROB]));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        stardistPanel.add(starDistProbTextField, gridBagConstraints);

        starDistOverlapTextField.setText(props.getProperty(propLabels[MultiThreadedMaximaFinder.STARDIST_OVERLAP]));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        stardistPanel.add(starDistOverlapTextField, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(stardistPanel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    protected void setToolTips() {
        channelSelectComboBox.setToolTipText(info.getProperty(propLabels[MultiThreadedMaximaFinder.CHANNEL_SELECT]));
//        simpleDetectToggleButton.setToolTipText(info.getProperty(propLabels[MultiThreadedMaximaFinder.BLOB_DETECT]));
//        advancedDetectToggleButton.setToolTipText(info.getProperty(propLabels[MultiThreadedMaximaFinder.HESSIAN_DETECT]));
        methodComboBox.setToolTipText(info.getProperty(propLabels[MultiThreadedMaximaFinder.METHOD]));
        blobRadTextField.setToolTipText(info.getProperty(propLabels[MultiThreadedMaximaFinder.BLOB_SIZE]));
        noiseTolTextField.setToolTipText(info.getProperty(propLabels[MultiThreadedMaximaFinder.BLOB_THRESH]));
        hessianMinSizeTextField.setToolTipText(info.getProperty(propLabels[MultiThreadedMaximaFinder.HESSIAN_START_SCALE]));
        hessianMaxSizeTextField.setToolTipText(info.getProperty(propLabels[MultiThreadedMaximaFinder.HESSIAN_STOP_SCALE]));
        hessianStepSizeTextField.setToolTipText(info.getProperty(propLabels[MultiThreadedMaximaFinder.HESSIAN_SCALE_STEP]));
        hessianThreshTextField.setToolTipText(info.getProperty(propLabels[MultiThreadedMaximaFinder.HESSIAN_THRESH]));
        previewButton.setToolTipText(info.getProperty(GianiDefaultParams.PREVIEW));
        helpButton.setToolTipText(info.getProperty(GianiDefaultParams.HELP));
    }

    public boolean setVariables() {
        setProperties(props, this);
        String method = (String) methodComboBox.getSelectedItem();
        props.setProperty(GianiDefaultParams.NUC_MAXIMA_DETECT_BLOBS, "false");
        props.setProperty(GianiDefaultParams.NUC_MAXIMA_DETECT_HESSIAN, "false");
        props.setProperty(GianiDefaultParams.NUC_MAXIMA_DETECT_STARDIST, "false");
        switch (method) {
            case GianiDefaultParams.NUC_MAXIMA_DETECT_BLOBS:
                props.setProperty(GianiDefaultParams.NUC_MAXIMA_DETECT_BLOBS, "true");
                break;
            case GianiDefaultParams.NUC_MAXIMA_DETECT_HESSIAN:
                props.setProperty(GianiDefaultParams.NUC_MAXIMA_DETECT_HESSIAN, "true");
                break;
            default:
                props.setProperty(GianiDefaultParams.NUC_MAXIMA_DETECT_STARDIST, "true");
        }
        setupProcess();
        return true;
    }

    private void previewButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_previewButtonActionPerformed
        restartProcess();
        setVariables();
        process.start();
        try {
            process.join();
        } catch (InterruptedException e) {
            return;
        }
        showOutput(((MultiThreadedStarDist) process).getOutput(), process.getOutput().getTitle());
    }//GEN-LAST:event_previewButtonActionPerformed

    private void helpButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_helpButtonActionPerformed
        openHelpPage(GianiDefaultParams.HELP_ERROR_MESSAGE);
    }//GEN-LAST:event_helpButtonActionPerformed

    private void methodComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_methodComboBoxActionPerformed
        String method = (String) methodComboBox.getSelectedItem();
        simpleDetectionPanel.setVisible(false);
        advancedDetectionPanel.setVisible(false);
        stardistPanel.setVisible(false);
        switch (method) {
            case GianiDefaultParams.NUC_MAXIMA_DETECT_BLOBS:
                simpleDetectionPanel.setVisible(true);
                break;
            case GianiDefaultParams.NUC_MAXIMA_DETECT_HESSIAN:
                advancedDetectionPanel.setVisible(true);
                break;
            default:
                stardistPanel.setVisible(true);
        }
    }//GEN-LAST:event_methodComboBoxActionPerformed

    public void setupProcess() {

        process.setup(img, props, propLabels);
    }

    private void showOutput(ImagePlus imp, String title) {
        imp.setTitle(title);
        imp.show();
    }

    private void showOutput(ArrayList<int[]> maxima, String title, Roi[] binaryOutline) {
        ImagePlus imp = img.getLoadedImage();
        Overlay o = new Overlay();
        Length zLength = img.getZSpatialRes(Integer.parseInt(props.getProperty(GianiDefaultParams.SERIES_SELECT_LABEL)));
        double zSpatRes = 1.0;
        if (zLength != null) {
            zSpatRes = zLength.value().doubleValue();
        }
        Length xyLength = img.getXYSpatialRes(Integer.parseInt(props.getProperty(GianiDefaultParams.SERIES_SELECT_LABEL)));
        double xySpatRes = 1.0;
        if (xyLength != null) {
            xySpatRes = xyLength.value().doubleValue();
        }
        double maxXYRadiusMic = Double.parseDouble(props.getProperty(propLabels[MultiThreadedMaximaFinder.BLOB_SIZE]));
        double maxZRadiusMic = Double.parseDouble(props.getProperty(propLabels[MultiThreadedMaximaFinder.BLOB_SIZE]));
        if (!Boolean.parseBoolean(props.getProperty(propLabels[MultiThreadedMaximaFinder.BLOB_DETECT]))) {
            maxXYRadiusMic = Double.parseDouble(props.getProperty(propLabels[MultiThreadedMaximaFinder.HESSIAN_START_SCALE]));
            maxZRadiusMic = Double.parseDouble(props.getProperty(propLabels[MultiThreadedMaximaFinder.HESSIAN_START_SCALE]));
        }
        double maxZRadiusMic2 = Math.pow(maxZRadiusMic, 2.0);
        double maxXYRadiusMic2 = Math.pow(maxXYRadiusMic, 2.0);
        int zRadiusPix = (int) Math.ceil(maxZRadiusMic / zSpatRes);
        for (int[] pix : maxima) {
            int z0 = pix[2] + 1;
            for (int z = z0 - zRadiusPix < 1 ? 1 : z0 - zRadiusPix; z <= z0 + zRadiusPix && z <= imp.getNSlices(); z++) {
                double z2 = Math.pow((z - z0) * zSpatRes, 2.0);
                double cr = Math.sqrt((1.0 - z2 / maxZRadiusMic2) * maxXYRadiusMic2);
                int currentRadius = (int) Math.round(cr / xySpatRes);
                if (currentRadius < 1) {
                    currentRadius = 1;
                }
                OvalRoi roi = new OvalRoi(pix[0] - currentRadius, pix[1] - currentRadius, 2 * currentRadius + 1, 2 * currentRadius + 1);
                roi.setPosition(z);
                o.add(roi);
            }
        }
        if (props.getProperty(GianiDefaultParams.NUC_CENTROID_LOCALISATION_METHOD).matches(GianiDefaultParams.NUC_MAXIMA_DETECT_HESSIAN)) {
            for (int i = 0; i < imp.getNSlices(); i++) {
                if (binaryOutline[i] == null) {
                    continue;
                }
                binaryOutline[i].setStrokeColor(Color.red);
                binaryOutline[i].setPosition(i + 1);
                o.add(binaryOutline[i]);
            }
        }
        imp.setTitle(title);
        imp.show();
        imp.setOverlay(o);
    }

    public void update() {
        if (img.getId() == null) {
            return;
        }
        int channels = img.getSizeC(img.getCurrentSeries());
        channelLabels = new ArrayList();
        for (int c = 0; c < channels; c++) {
            channelLabels.add(String.valueOf(c));
        }
        channelSelectComboBox.setModel(new DefaultComboBoxModel(channelLabels.toArray()));
        channelSelectComboBox.setSelectedItem(props.get(propLabels[MultiThreadedMaximaFinder.CHANNEL_SELECT]));
        channelSelectComboBox.setVisible(allowChannelSelect);
        if (defaultChannel > -1) {
            channelSelectComboBox.setSelectedIndex(defaultChannel);
        }
        //simpleDetectToggleButtonActionPerformed(null);
        methodComboBoxActionPerformed(null);
        unitsLabel1.setText(props.getProperty(GianiDefaultParams.UNITS));
        unitsLabel2.setText(props.getProperty(GianiDefaultParams.UNITS));
        unitsLabel3.setText(props.getProperty(GianiDefaultParams.UNITS));
        unitsLabel4.setText(props.getProperty(GianiDefaultParams.UNITS));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel advancedDetectionPanel;
    private javax.swing.JLabel blobRadLabel;
    private javax.swing.JTextField blobRadTextField;
    private javax.swing.JComboBox<String> channelSelectComboBox;
    private javax.swing.JLabel channelSelectLabel;
    private javax.swing.JButton helpButton;
    private javax.swing.JLabel hessianMaxSizeLabel;
    private javax.swing.JTextField hessianMaxSizeTextField;
    private javax.swing.JLabel hessianMinSizeLabel;
    private javax.swing.JTextField hessianMinSizeTextField;
    private javax.swing.JLabel hessianStepSizeLabel;
    private javax.swing.JTextField hessianStepSizeTextField;
    private javax.swing.JLabel hessianThreshLabel;
    private javax.swing.JTextField hessianThreshTextField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JComboBox<String> methodComboBox;
    private javax.swing.JLabel methodLabel;
    private javax.swing.JLabel noiseTolLabel;
    private javax.swing.JTextField noiseTolTextField;
    protected javax.swing.JButton previewButton;
    private javax.swing.JPanel simpleDetectionPanel;
    private javax.swing.JTextField starDistOverlapTextField;
    private javax.swing.JTextField starDistProbTextField;
    private javax.swing.JPanel stardistPanel;
    private javax.swing.JLabel titleLabel;
    private javax.swing.JLabel unitsLabel1;
    private javax.swing.JLabel unitsLabel2;
    private javax.swing.JLabel unitsLabel3;
    private javax.swing.JLabel unitsLabel4;
    // End of variables declaration//GEN-END:variables
}
