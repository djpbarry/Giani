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
package ui;

import Extrema.MultiThreadedMaximaFinder;
import IO.BioFormats.BioFormatsImg;
import UIClasses.LayerPanel;
import UIClasses.Updateable;
import ij.ImagePlus;
import ij.gui.OvalRoi;
import ij.gui.Overlay;
import java.util.ArrayList;
import java.util.Properties;
import javax.swing.DefaultComboBoxModel;
import gianiparams.GianiDefaultParams;
import ij.gui.Roi;
import ij.process.AutoThresholder;
import java.awt.Color;
import java.net.URI;
import ome.units.quantity.Length;

/**
 *
 * @author David Barry <david.barry at crick dot ac dot uk>
 */
public class MaximaFinderPanel extends LayerPanel implements Updateable {

    private ArrayList<String> channelLabels;
    boolean allowChannelSelect;
    int defaultChannel;

    /**
     * Creates new form MaximaFinderPanel
     */
    public MaximaFinderPanel() {
        this(null, null, null, null, true, -1, null);
    }

    public MaximaFinderPanel(Properties props, BioFormatsImg img, MultiThreadedMaximaFinder process, String[] propLabels, boolean allowChannelSelect, int defaultChannel, URI helpURI) {
        super(props, img, process, propLabels, helpURI);
        this.allowChannelSelect = allowChannelSelect;
        this.defaultChannel = defaultChannel;
        initComponents();
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

        blobRadLabel = new javax.swing.JLabel();
        blobRadTextField = new javax.swing.JTextField();
        previewButton = new javax.swing.JButton();
        noiseTolLabel = new javax.swing.JLabel();
        noiseTolTextField = new javax.swing.JTextField();
        channelSelectLabel = new javax.swing.JLabel();
        channelSelectComboBox = new javax.swing.JComboBox<>();
        blobDetectToggleButton = new javax.swing.JToggleButton();
        edmDetectToggleButton = new javax.swing.JToggleButton();
        edmThreshLabel = new javax.swing.JLabel();
        edmMinSizeLabel = new javax.swing.JLabel();
        edmMaxSizeLabel = new javax.swing.JLabel();
        edmThreshComboBox = new javax.swing.JComboBox<>();
        edmMinSizeTextField = new javax.swing.JTextField();
        edmMaxSizeTextField = new javax.swing.JTextField();
        helpButton = new javax.swing.JButton();

        setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        setLayout(new java.awt.GridBagLayout());

        blobRadLabel.setText(propLabels[MultiThreadedMaximaFinder.BLOB_SIZE]);
        blobRadLabel.setLabelFor(blobRadTextField);
        blobRadLabel.setEnabled(Boolean.parseBoolean(props.getProperty(propLabels[MultiThreadedMaximaFinder.BLOB_DETECT])));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        add(blobRadLabel, gridBagConstraints);

        blobRadTextField.setText(props.getProperty(propLabels[MultiThreadedMaximaFinder.BLOB_SIZE]));
        blobRadTextField.setEnabled(Boolean.parseBoolean(props.getProperty(propLabels[MultiThreadedMaximaFinder.BLOB_DETECT])));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        add(blobRadTextField, gridBagConstraints);

        previewButton.setText("Preview");
        previewButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                previewButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        add(previewButton, gridBagConstraints);

        noiseTolLabel.setText(propLabels[MultiThreadedMaximaFinder.BLOB_THRESH]);
        noiseTolLabel.setLabelFor(noiseTolTextField);
        noiseTolLabel.setEnabled(Boolean.parseBoolean(props.getProperty(propLabels[MultiThreadedMaximaFinder.BLOB_DETECT])));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        add(noiseTolLabel, gridBagConstraints);

        noiseTolTextField.setText(props.getProperty(propLabels[MultiThreadedMaximaFinder.BLOB_THRESH]));
        noiseTolTextField.setEnabled(Boolean.parseBoolean(props.getProperty(propLabels[MultiThreadedMaximaFinder.BLOB_DETECT])));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        add(noiseTolTextField, gridBagConstraints);

        channelSelectLabel.setText(propLabels[MultiThreadedMaximaFinder.CHANNEL_SELECT]);
        channelSelectLabel.setLabelFor(channelSelectComboBox);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        add(channelSelectLabel, gridBagConstraints);

        channelSelectComboBox.setModel(new DefaultComboBoxModel(new String[]{}));
        channelSelectComboBox.setVisible(allowChannelSelect);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        add(channelSelectComboBox, gridBagConstraints);

        blobDetectToggleButton.setText(propLabels[MultiThreadedMaximaFinder.BLOB_DETECT]);
        blobDetectToggleButton.setSelected(Boolean.parseBoolean(props.getProperty(propLabels[MultiThreadedMaximaFinder.BLOB_DETECT])));
        blobDetectToggleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                blobDetectToggleButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        add(blobDetectToggleButton, gridBagConstraints);

        edmDetectToggleButton.setText(propLabels[MultiThreadedMaximaFinder.EDM_DETECT]);
        edmDetectToggleButton.setSelected(!Boolean.parseBoolean(props.getProperty(propLabels[MultiThreadedMaximaFinder.BLOB_DETECT])));
        edmDetectToggleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                edmDetectToggleButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        add(edmDetectToggleButton, gridBagConstraints);

        edmThreshLabel.setText(propLabels[MultiThreadedMaximaFinder.EDM_THRESH]);
        edmThreshLabel.setLabelFor(edmThreshComboBox);
        edmThreshLabel.setEnabled(!Boolean.parseBoolean(props.getProperty(propLabels[MultiThreadedMaximaFinder.BLOB_DETECT])));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        add(edmThreshLabel, gridBagConstraints);

        edmMinSizeLabel.setText(propLabels[MultiThreadedMaximaFinder.EDM_MIN_SIZE]);
        edmMinSizeLabel.setLabelFor(edmMinSizeTextField);
        edmMinSizeLabel.setEnabled(!Boolean.parseBoolean(props.getProperty(propLabels[MultiThreadedMaximaFinder.BLOB_DETECT])));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        add(edmMinSizeLabel, gridBagConstraints);

        edmMaxSizeLabel.setText(propLabels[MultiThreadedMaximaFinder.EDM_MAX_SIZE]);
        edmMaxSizeLabel.setLabelFor(edmMaxSizeTextField);
        edmMaxSizeLabel.setEnabled(!Boolean.parseBoolean(props.getProperty(propLabels[MultiThreadedMaximaFinder.BLOB_DETECT])));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        add(edmMaxSizeLabel, gridBagConstraints);

        edmThreshComboBox.setModel(new DefaultComboBoxModel(AutoThresholder.getMethods()));
        edmThreshComboBox.setEnabled(!Boolean.parseBoolean(props.getProperty(propLabels[MultiThreadedMaximaFinder.BLOB_DETECT])));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        add(edmThreshComboBox, gridBagConstraints);

        edmMinSizeTextField.setText(props.getProperty(propLabels[MultiThreadedMaximaFinder.EDM_MIN_SIZE]));
        edmMinSizeTextField.setEnabled(!Boolean.parseBoolean(props.getProperty(propLabels[MultiThreadedMaximaFinder.BLOB_DETECT])));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        add(edmMinSizeTextField, gridBagConstraints);

        edmMaxSizeTextField.setText(props.getProperty(propLabels[MultiThreadedMaximaFinder.EDM_MAX_SIZE]));
        edmMaxSizeTextField.setEnabled(!Boolean.parseBoolean(props.getProperty(propLabels[MultiThreadedMaximaFinder.BLOB_DETECT])));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        add(edmMaxSizeTextField, gridBagConstraints);

        helpButton.setText("Help");
        helpButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                helpButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        add(helpButton, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void previewButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_previewButtonActionPerformed
        restartProcess();
        setVariables();
        process.start();
        try {
            process.join();
        } catch (InterruptedException e) {
            return;
        }
        showOutput(((MultiThreadedMaximaFinder) process).getMaxima(), process.getOutput().getTitle(), ((MultiThreadedMaximaFinder) process).getEdmThresholdOutline());
    }//GEN-LAST:event_previewButtonActionPerformed

    private void blobDetectToggleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_blobDetectToggleButtonActionPerformed
        edmDetectToggleButton.setSelected(!blobDetectToggleButton.isSelected());
        edmDetectToggleButtonActionPerformed(evt);
    }//GEN-LAST:event_blobDetectToggleButtonActionPerformed

    private void edmDetectToggleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_edmDetectToggleButtonActionPerformed
        blobDetectToggleButton.setSelected(!edmDetectToggleButton.isSelected());
        blobRadLabel.setEnabled(blobDetectToggleButton.isSelected());
        blobRadTextField.setEnabled(blobDetectToggleButton.isSelected());
        noiseTolLabel.setEnabled(blobDetectToggleButton.isSelected());
        noiseTolTextField.setEnabled(blobDetectToggleButton.isSelected());
        edmThreshLabel.setEnabled(edmDetectToggleButton.isSelected());
        edmThreshComboBox.setEnabled(edmDetectToggleButton.isSelected());
        edmMinSizeLabel.setEnabled(edmDetectToggleButton.isSelected());
        edmMinSizeTextField.setEnabled(edmDetectToggleButton.isSelected());
        edmMaxSizeLabel.setEnabled(edmDetectToggleButton.isSelected());
        edmMaxSizeTextField.setEnabled(edmDetectToggleButton.isSelected());
    }//GEN-LAST:event_edmDetectToggleButtonActionPerformed

    private void helpButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_helpButtonActionPerformed
        openHelpPage(GianiDefaultParams.HELP_ERROR_MESSAGE);
    }//GEN-LAST:event_helpButtonActionPerformed

    public void setupProcess() {
        process.setup(img, props, propLabels);
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
            maxXYRadiusMic = Double.parseDouble(props.getProperty(propLabels[MultiThreadedMaximaFinder.EDM_MIN_SIZE]));
            maxZRadiusMic = Double.parseDouble(props.getProperty(propLabels[MultiThreadedMaximaFinder.EDM_MIN_SIZE]));
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
        if (edmDetectToggleButton.isSelected()) {
            for (int i = 0; i < imp.getNSlices(); i++) {
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
        int channels = img.getSizeC();
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
        blobDetectToggleButtonActionPerformed(null);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToggleButton blobDetectToggleButton;
    private javax.swing.JLabel blobRadLabel;
    private javax.swing.JTextField blobRadTextField;
    private javax.swing.JComboBox<String> channelSelectComboBox;
    private javax.swing.JLabel channelSelectLabel;
    private javax.swing.JToggleButton edmDetectToggleButton;
    private javax.swing.JLabel edmMaxSizeLabel;
    private javax.swing.JTextField edmMaxSizeTextField;
    private javax.swing.JLabel edmMinSizeLabel;
    private javax.swing.JTextField edmMinSizeTextField;
    private javax.swing.JComboBox<String> edmThreshComboBox;
    private javax.swing.JLabel edmThreshLabel;
    private javax.swing.JButton helpButton;
    private javax.swing.JLabel noiseTolLabel;
    private javax.swing.JTextField noiseTolTextField;
    protected javax.swing.JButton previewButton;
    // End of variables declaration//GEN-END:variables
}
