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
import params.DefaultParams;

/**
 *
 * @author David Barry <david.barry at crick dot ac dot uk>
 */
public class MaximaFinderPanel extends LayerPanel implements Updateable {

    private ArrayList<String> channelLabels;

    /**
     * Creates new form MaximaFinderPanel
     */
    public MaximaFinderPanel() {
        this(null, null, null, null);
    }

    public MaximaFinderPanel(Properties props, BioFormatsImg img, MultiThreadedMaximaFinder process, String[] propLabels) {
        super(props, img, process, propLabels);
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

        xyFiltRadLabel = new javax.swing.JLabel();
        xyFiltRadTextField = new javax.swing.JTextField();
        previewButton = new javax.swing.JButton();
        noiseTolLabel = new javax.swing.JLabel();
        noiseTolTextField = new javax.swing.JTextField();
        channelSelectLabel = new javax.swing.JLabel();
        channelSelectComboBox = new javax.swing.JComboBox<>();

        setBorder(javax.swing.BorderFactory.createEtchedBorder());
        setLayout(new java.awt.GridBagLayout());

        xyFiltRadLabel.setText(propLabels[1]);
        xyFiltRadLabel.setLabelFor(xyFiltRadTextField);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(xyFiltRadLabel, gridBagConstraints);

        xyFiltRadTextField.setText("0.0");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(xyFiltRadTextField, gridBagConstraints);

        previewButton.setText("Preview");
        previewButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                previewButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        add(previewButton, gridBagConstraints);

        noiseTolLabel.setText(propLabels[2]);
        noiseTolLabel.setLabelFor(noiseTolTextField);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(noiseTolLabel, gridBagConstraints);

        noiseTolTextField.setText("0.0");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(noiseTolTextField, gridBagConstraints);

        channelSelectLabel.setText(DefaultParams.BLOB_FIND_CHAN_SELECT_LABEL);
        channelSelectLabel.setLabelFor(channelSelectComboBox);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(channelSelectLabel, gridBagConstraints);

        channelSelectComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(channelSelectComboBox, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void previewButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_previewButtonActionPerformed
        setVariables();
        restartProcess();
        process.setup(img, props, new String[]{
            DefaultParams.SERIES_SELECT_LABEL,
            propLabels[0], propLabels[1], propLabels[2]});
        process.start();
        try {
            process.join();
        } catch (InterruptedException e) {
            return;
        }
        showOutput(((MultiThreadedMaximaFinder) process).getMaxima(), process.getOutput().getTitle());
    }//GEN-LAST:event_previewButtonActionPerformed

    private void showOutput(ArrayList<int[]> maxima, String title) {
        ImagePlus imp = img.getLoadedImage();
        Overlay o = new Overlay();
        double zSpatRes = img.getZSpatialRes(Integer.parseInt(props.getProperty(DefaultParams.SERIES_SELECT_LABEL))).value().doubleValue();
        double xySpatRes = img.getXYSpatialRes(Integer.parseInt(props.getProperty(DefaultParams.SERIES_SELECT_LABEL))).value().doubleValue();
        double maxXYRadiusMic = Double.parseDouble(props.getProperty(propLabels[1]));
        double maxZRadiusMic = Double.parseDouble(props.getProperty(propLabels[1]));
        double maxZRadiusMic2 = Math.pow(maxZRadiusMic, 2.0);
        double maxXYRadiusMic2 = Math.pow(maxXYRadiusMic, 2.0);
        int zRadiusPix = (int) Math.ceil(maxZRadiusMic / zSpatRes);
        for (int[] pix : maxima) {
            int z0 = pix[2] + 1;
            for (int z = z0 - zRadiusPix; z <= z0 + zRadiusPix; z++) {
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
        imp.setTitle(title);
        imp.show();
        imp.setOverlay(o);
    }

    public void update() {
        if (img.getId() == null) {
            return;
        }
        int channels = img.getChannelCount();
        channelLabels = new ArrayList();
        for (int c = 0; c < channels; c++) {
            channelLabels.add(String.valueOf(c));
        }
        channelSelectComboBox.setModel(new DefaultComboBoxModel(channelLabels.toArray()));
        channelSelectComboBox.setSelectedItem(props.get(DefaultParams.BLOB_FIND_CHAN_SELECT_LABEL));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> channelSelectComboBox;
    private javax.swing.JLabel channelSelectLabel;
    private javax.swing.JLabel noiseTolLabel;
    private javax.swing.JTextField noiseTolTextField;
    protected javax.swing.JButton previewButton;
    private javax.swing.JLabel xyFiltRadLabel;
    private javax.swing.JTextField xyFiltRadTextField;
    // End of variables declaration//GEN-END:variables
}
