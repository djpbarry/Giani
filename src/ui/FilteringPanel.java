/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ui;

import IO.BioFormats.BioFormatsImg;
import Process.Filtering.MultiThreadedGaussianFilter;
import Process.MultiThreadedProcess;
import UIClasses.LayerPanel;
import ij.ImagePlus;
import ij.process.StackConverter;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import params.DefaultParams;
import static params.DefaultParams.FILT_RAD_XY_LABEL;
import static params.DefaultParams.FILT_RAD_Z_LABEL;

/**
 *
 * @author David Barry <david.barry at crick dot ac dot uk>
 */
public class FilteringPanel extends LayerPanel {

    /**
     * Creates new form FilteringPanel
     */
    public FilteringPanel() {
        this(null, null, null);
    }

    public FilteringPanel(Properties props, BioFormatsImg img, ExecutorService exec) {
        super(props, img, exec);
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
        filterRadiusXYLabel = new javax.swing.JLabel();
        filterRadiusXYTextField = new javax.swing.JTextField();
        filterRadiusZLabel = new javax.swing.JLabel();
        filterRadiusZTextField = new javax.swing.JTextField();

        setBorder(javax.swing.BorderFactory.createEtchedBorder());
        addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                formFocusGained(evt);
            }
        });
        setLayout(new java.awt.GridBagLayout());

        previewButton.setText("Preview");
        previewButton.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                previewButtonFocusLost(evt);
            }
        });
        previewButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                previewButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        add(previewButton, gridBagConstraints);

        filterRadiusXYLabel.setText(FILT_RAD_XY_LABEL);
        filterRadiusXYLabel.setLabelFor(filterRadiusXYTextField);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(filterRadiusXYLabel, gridBagConstraints);

        filterRadiusXYTextField.setText("0.0");
        filterRadiusXYTextField.setText("0.0");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(filterRadiusXYTextField, gridBagConstraints);

        filterRadiusZLabel.setText(FILT_RAD_Z_LABEL);
        filterRadiusZLabel.setLabelFor(filterRadiusZTextField);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(filterRadiusZLabel, gridBagConstraints);

        filterRadiusZTextField.setText("0.0");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(filterRadiusZTextField, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void previewButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_previewButtonActionPerformed
        setVariables();
        int series = Integer.parseInt(props.getProperty(DefaultParams.SERIES_SELECT_LABEL));
        int channel = Integer.parseInt(props.getProperty(DefaultParams.CHANNEL_SELECT_LABEL));
        double xySpatialRes = img.getXYSpatialRes(series).value().doubleValue();
        double zSpatialRes = img.getZSpatialRes(series).value().doubleValue();
        img.setImg(series, channel);
        ImagePlus image = img.getImg();
        (new StackConverter(image)).convertToGray32();
        double[] sigma = new double[]{Double.parseDouble(props.getProperty(DefaultParams.FILT_RAD_XY_LABEL)) / xySpatialRes,
            Double.parseDouble(props.getProperty(DefaultParams.FILT_RAD_XY_LABEL)) / xySpatialRes,
            Double.parseDouble(props.getProperty(DefaultParams.FILT_RAD_Z_LABEL)) / zSpatialRes};
        process = new MultiThreadedGaussianFilter(img, exec, sigma, series, channel);
        process.start();
    }//GEN-LAST:event_previewButtonActionPerformed

    private void previewButtonFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_previewButtonFocusLost
        if (process != null) {
            process.interrupt();
        }
    }//GEN-LAST:event_previewButtonFocusLost

    private void formFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_formFocusGained

    }//GEN-LAST:event_formFocusGained

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel filterRadiusXYLabel;
    private javax.swing.JTextField filterRadiusXYTextField;
    private javax.swing.JLabel filterRadiusZLabel;
    private javax.swing.JTextField filterRadiusZTextField;
    private javax.swing.JButton previewButton;
    // End of variables declaration//GEN-END:variables
}
