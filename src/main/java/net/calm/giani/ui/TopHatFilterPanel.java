/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.calm.giani.ui;

import net.calm.giani.gianiparams.GianiDefaultParams;
import net.calm.iaclasslibrary.IO.BioFormats.BioFormatsImg;
import net.calm.iaclasslibrary.Process.Filtering.MultiThreadedTopHatFilter;
import net.calm.iaclasslibrary.UIClasses.LayerPanel;

import java.util.ArrayList;
import java.util.Properties;
import java.net.URI;

public class TopHatFilterPanel extends LayerPanel {

    private ArrayList<String> channelLabels;
    private final String title;

    /**
     * Creates new form FilteringPanel
     */
    public TopHatFilterPanel() {
        this(null, null, null, null);
    }

    public TopHatFilterPanel(Properties props, BioFormatsImg img, MultiThreadedTopHatFilter process, String[] propLabels) {
        this(props, img, process, propLabels, null, null);
    }

    public TopHatFilterPanel(Properties props, BioFormatsImg img, MultiThreadedTopHatFilter process, String[] propLabels, URI helpURI, String title) {
        super(props, img, process, propLabels, helpURI);
        this.title = title;
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
        helpButton = new javax.swing.JButton();
        downsizeFactorLabel = new javax.swing.JLabel();
        downSizeFactorTextField = new javax.swing.JTextField();
        titleLabel = new javax.swing.JLabel();

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

        filterRadiusXYLabel.setText(propLabels[MultiThreadedTopHatFilter.FILT_RAD_LABEL]);
        filterRadiusXYLabel.setLabelFor(filterRadiusXYTextField);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        add(filterRadiusXYLabel, gridBagConstraints);

        filterRadiusXYTextField.setText(props.getProperty(propLabels[MultiThreadedTopHatFilter.FILT_RAD_LABEL]));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        add(filterRadiusXYTextField, gridBagConstraints);

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

        downsizeFactorLabel.setText(propLabels[MultiThreadedTopHatFilter.RESIZE_FACTOR_LABEL]);
        downsizeFactorLabel.setLabelFor(downSizeFactorTextField);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        add(downsizeFactorLabel, gridBagConstraints);

        downSizeFactorTextField.setText(props.getProperty(propLabels[MultiThreadedTopHatFilter.RESIZE_FACTOR_LABEL]));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        add(downSizeFactorTextField, gridBagConstraints);

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
        process.getOutput().show();
    }//GEN-LAST:event_previewButtonActionPerformed

    private void helpButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_helpButtonActionPerformed
        openHelpPage(GianiDefaultParams.HELP_ERROR_MESSAGE);
    }//GEN-LAST:event_helpButtonActionPerformed

    public void setupProcess() {
        process.setup(img, props, propLabels);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField downSizeFactorTextField;
    private javax.swing.JLabel downsizeFactorLabel;
    private javax.swing.JLabel filterRadiusXYLabel;
    private javax.swing.JTextField filterRadiusXYTextField;
    private javax.swing.JButton helpButton;
    private javax.swing.JButton previewButton;
    private javax.swing.JLabel titleLabel;
    // End of variables declaration//GEN-END:variables
}
