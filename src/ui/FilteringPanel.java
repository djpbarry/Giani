/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ui;

import IO.BioFormats.BioFormatsImg;
import Process.Filtering.MultiThreadedGaussianFilter;
import UIClasses.LayerPanel;
import java.util.ArrayList;
import java.util.Properties;
import javax.swing.DefaultComboBoxModel;
import params.DefaultParams;
import UIClasses.Updateable;

/**
 *
 * @author David Barry <david.barry at crick dot ac dot uk>
 */
public class FilteringPanel extends LayerPanel implements Updateable {

    private ArrayList<String> channelLabels;

    /**
     * Creates new form FilteringPanel
     */
    public FilteringPanel() {
        this(null, null, null, null);
    }

    public FilteringPanel(Properties props, BioFormatsImg img, MultiThreadedGaussianFilter process) {
        this(props, img, process, null);
    }

    public FilteringPanel(Properties props, BioFormatsImg img, MultiThreadedGaussianFilter process, String[] propLabels) {
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

        previewButton = new javax.swing.JButton();
        filterRadiusXYLabel = new javax.swing.JLabel();
        filterRadiusXYTextField = new javax.swing.JTextField();
        filterRadiusZLabel = new javax.swing.JLabel();
        filterRadiusZTextField = new javax.swing.JTextField();
        channelSelectLabel = new javax.swing.JLabel();
        channelSelectComboBox = new javax.swing.JComboBox<>();

        setBorder(javax.swing.BorderFactory.createEtchedBorder());
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
        gridBagConstraints.gridwidth = 2;
        add(previewButton, gridBagConstraints);

        filterRadiusXYLabel.setText(propLabels[1]);
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

        filterRadiusZLabel.setText(propLabels[2]);
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

        channelSelectLabel.setText(propLabels[0]);
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

    protected void setupProcess() {
        process.setup(img, props, new String[]{
            DefaultParams.SERIES_SELECT_LABEL, propLabels[0], propLabels[1], propLabels[2]
        });
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
        channelSelectComboBox.setSelectedItem(props.get(propLabels[0]));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> channelSelectComboBox;
    private javax.swing.JLabel channelSelectLabel;
    private javax.swing.JLabel filterRadiusXYLabel;
    private javax.swing.JTextField filterRadiusXYTextField;
    private javax.swing.JLabel filterRadiusZLabel;
    private javax.swing.JTextField filterRadiusZTextField;
    private javax.swing.JButton previewButton;
    // End of variables declaration//GEN-END:variables
}
