/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ui;

import IO.BioFormats.BioFormatsImg;
import Process.ROI.MultiThreadedROIConstructor;
import Process.Segmentation.MultiThreadedWatershed;
import UIClasses.LayerPanel;
import UtilClasses.GenUtils;
import ij.gui.Roi;
import ij.measure.ResultsTable;
import ij.plugin.filter.Analyzer;
import java.util.ArrayList;
import java.util.Properties;
import javax.swing.DefaultComboBoxModel;
import mcib3d.geom.Objects3DPopulation;
import params.DefaultParams;
import static params.DefaultParams.SEG_CHAN_SELECT_LABEL;
import static params.DefaultParams.SEG_THRESH_LABEL;

/**
 *
 * @author David Barry <david.barry at crick dot ac dot uk>
 */
public class SegmentationPanel extends LayerPanel {

    private ArrayList<String> channelLabels;
    Objects3DPopulation objectPop;
    ArrayList<ArrayList<Roi>> allRois;

    /**
     * Creates new form SegmentationPanel
     */
    public SegmentationPanel() {
        this(null, null);
    }

    public SegmentationPanel(Properties props, BioFormatsImg img) {
        super(props, img);
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

        channelSelectLabel = new javax.swing.JLabel();
        channelSelectComboBox = new javax.swing.JComboBox<>();
        previewButton = new javax.swing.JButton();
        thresholdLabel = new javax.swing.JLabel();
        thresholdTextField = new javax.swing.JTextField();
        measurePreviewButton = new javax.swing.JButton();

        setLayout(new java.awt.GridBagLayout());

        channelSelectLabel.setText(SEG_CHAN_SELECT_LABEL);
        channelSelectLabel.setLabelFor(channelSelectComboBox);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(channelSelectLabel, gridBagConstraints);

        channelSelectComboBox.setModel(new DefaultComboBoxModel(new String[]{}));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(channelSelectComboBox, gridBagConstraints);

        previewButton.setText("Preview");
        previewButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                previewButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(previewButton, gridBagConstraints);

        thresholdLabel.setText(SEG_THRESH_LABEL);
        thresholdLabel.setLabelFor(thresholdTextField);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(thresholdLabel, gridBagConstraints);

        thresholdTextField.setText("0.0");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(thresholdTextField, gridBagConstraints);

        measurePreviewButton.setText("Measure Preview");
        measurePreviewButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                measurePreviewButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(measurePreviewButton, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void previewButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_previewButtonActionPerformed
        setVariables();
        int series = Integer.parseInt(props.getProperty(DefaultParams.SERIES_SELECT_LABEL));
        int channel = Integer.parseInt(props.getProperty(DefaultParams.SEG_CHAN_SELECT_LABEL));
        double thresh = Double.parseDouble(props.getProperty(DefaultParams.SEG_THRESH_LABEL));
        double[] sigma = getDoubleSigma(DefaultParams.SERIES_SELECT_LABEL, DefaultParams.FILT_RAD_XY_LABEL,
                DefaultParams.FILT_RAD_XY_LABEL, DefaultParams.FILT_RAD_Z_LABEL);
        process = new MultiThreadedWatershed(img, exec, sigma, series, channel, thresh, props);
        process.start();
        try {
            process.join();
        } catch (InterruptedException e) {
        }

    }//GEN-LAST:event_previewButtonActionPerformed

    private void measurePreviewButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_measurePreviewButtonActionPerformed
        if (process.isAlive()) {
            process.interrupt();
            GenUtils.error("Segmentation incomplete.");
            return;
        }
        process = new MultiThreadedROIConstructor(img, props);
        process.start();
        try {
            process.join();
        } catch (InterruptedException e) {
        }
        objectPop = ((MultiThreadedROIConstructor) process).getObjectPop();
        ArrayList<double[]> measures = objectPop.getMeasuresStats(img.getImg().getImageStack());
        ResultsTable rt = Analyzer.getResultsTable();
        String[] headings = {"Index", "Mean Pixel Value", "Pixel Standard Deviation", "Min Pixel Value", "Max Pixel Value", "Integrated Density"};
        for (double[] m : measures) {
            rt.incrementCounter();
            for (int i = 0; i < m.length; i++) {
                rt.addValue(headings[i], m[i]);
            }
        }
        rt.updateResults();
        rt.show("Measures");
    }//GEN-LAST:event_measurePreviewButtonActionPerformed

    public void updateChannels() {
        int channels = img.getChannelCount();
        channelLabels = new ArrayList();
        for (int c = 0; c < channels; c++) {
            channelLabels.add(String.valueOf(c));
        }
        channelSelectComboBox.setModel(new DefaultComboBoxModel(channelLabels.toArray()));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> channelSelectComboBox;
    private javax.swing.JLabel channelSelectLabel;
    private javax.swing.JButton measurePreviewButton;
    private javax.swing.JButton previewButton;
    private javax.swing.JLabel thresholdLabel;
    private javax.swing.JTextField thresholdTextField;
    // End of variables declaration//GEN-END:variables
}
