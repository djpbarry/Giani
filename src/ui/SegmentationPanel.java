/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ui;

import IO.BioFormats.BioFormatsImg;
import Process.Segmentation.MultiThreadedWatershed;
import UIClasses.LayerPanel;
import UIClasses.Updateable;
import gianiparams.GianiDefaultParams;
import ij.IJ;
import ij.ImagePlus;
import ij.plugin.LutLoader;
import ij.process.AutoThresholder;
import java.io.File;
import java.net.URI;
import java.util.Properties;
import javax.swing.DefaultComboBoxModel;

/**
 *
 * @author David Barry <david.barry at crick dot ac dot uk>
 */
public class SegmentationPanel extends LayerPanel implements Updateable {

    /**
     * Creates new form SegmentationPanel
     */
    public SegmentationPanel() {
        this(null, null, null, null, null);
    }

    public SegmentationPanel(Properties props, BioFormatsImg img, MultiThreadedWatershed process, String[] propLabels, URI helpURI) {
        super(props, img, process, propLabels, helpURI);
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

        previewButton = new javax.swing.JButton();
        thresholdLabel = new javax.swing.JLabel();
        thresholdComboBox = new javax.swing.JComboBox<>();
        volumeToggleButton = new javax.swing.JToggleButton();
        membraneToggleButton = new javax.swing.JToggleButton();
        distWeightingLabel = new javax.swing.JLabel();
        distWeightingTextField = new javax.swing.JTextField();
        helpButton = new javax.swing.JButton();

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
        gridBagConstraints.gridy = 3;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        add(previewButton, gridBagConstraints);

        thresholdLabel.setText(propLabels[MultiThreadedWatershed.THRESHOLD_LABEL]);
        thresholdLabel.setLabelFor(thresholdComboBox);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        add(thresholdLabel, gridBagConstraints);

        thresholdComboBox.setModel(new DefaultComboBoxModel(AutoThresholder.getMethods()));
        thresholdComboBox.setSelectedItem("Default");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        add(thresholdComboBox, gridBagConstraints);

        volumeToggleButton.setText(propLabels[MultiThreadedWatershed.VOL_MARKER_LABEL]);
        volumeToggleButton.setSelected(Boolean.parseBoolean(props.getProperty(propLabels[2])));
        volumeToggleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                volumeToggleButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        add(volumeToggleButton, gridBagConstraints);

        membraneToggleButton.setText(propLabels[MultiThreadedWatershed.MEMB_MARKER_LABEL]);
        membraneToggleButton.setSelected(!Boolean.parseBoolean(props.getProperty(propLabels[2])));
        membraneToggleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                membraneToggleButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        add(membraneToggleButton, gridBagConstraints);

        distWeightingLabel.setText(propLabels[MultiThreadedWatershed.LAMBDA_LABEL]);
        distWeightingLabel.setLabelFor(distWeightingTextField);
        distWeightingLabel.setEnabled(!Boolean.parseBoolean(props.getProperty(propLabels[2])));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        add(distWeightingLabel, gridBagConstraints);

        distWeightingTextField.setText(props.getProperty(propLabels[4]));
        distWeightingTextField.setEnabled(!Boolean.parseBoolean(props.getProperty(propLabels[2])));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        add(distWeightingTextField, gridBagConstraints);

        helpButton.setText("Help");
        helpButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                helpButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
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
        ImagePlus imp = process.getOutput();
        String lutPath = String.format("%s%s%s", IJ.getDirectory("luts"), File.separator, "glasbey_on_dark.lut");
        if (IJ.getInstance() == null) {
            lutPath = "C:\\Users\\barryd\\FIJI\\fiji-nojre\\Fiji.app\\luts\\glasbey_on_dark.lut";
        }
        imp.setLut(LutLoader.openLut(lutPath));
        imp.resetDisplayRange();
        imp.show();
    }//GEN-LAST:event_previewButtonActionPerformed

    public void setupProcess() {
        process.setup(img, props, propLabels);
    }

    private void volumeToggleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_volumeToggleButtonActionPerformed
        membraneToggleButton.setSelected(!volumeToggleButton.isSelected());
        membraneToggleButtonActionPerformed(evt);
    }//GEN-LAST:event_volumeToggleButtonActionPerformed

    private void membraneToggleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_membraneToggleButtonActionPerformed
        volumeToggleButton.setSelected(!membraneToggleButton.isSelected());
        updateDistanceWeighting();
    }//GEN-LAST:event_membraneToggleButtonActionPerformed

    private void helpButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_helpButtonActionPerformed
        openHelpPage(GianiDefaultParams.HELP_ERROR_MESSAGE);
    }//GEN-LAST:event_helpButtonActionPerformed

    void updateDistanceWeighting() {
        distWeightingLabel.setEnabled(membraneToggleButton.isSelected());
        distWeightingTextField.setEnabled(membraneToggleButton.isSelected());
    }

    public void update() {
        volumeToggleButtonActionPerformed(null);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel distWeightingLabel;
    private javax.swing.JTextField distWeightingTextField;
    private javax.swing.JButton helpButton;
    private javax.swing.JToggleButton membraneToggleButton;
    private javax.swing.JButton previewButton;
    private javax.swing.JComboBox<String> thresholdComboBox;
    private javax.swing.JLabel thresholdLabel;
    private javax.swing.JToggleButton volumeToggleButton;
    // End of variables declaration//GEN-END:variables
}
