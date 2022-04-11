/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.calm.giani.ui;

import net.calm.giani.gianiparams.GIANIParamInfos;
import net.calm.giani.gianiparams.GianiDefaultParams;
import net.calm.iaclasslibrary.IO.BioFormats.BioFormatsImg;
import net.calm.iaclasslibrary.Process.Filtering.MultiThreadedGaussianFilter;
import net.calm.iaclasslibrary.UIClasses.LayerPanel;
import net.calm.iaclasslibrary.UIClasses.Updateable;

import javax.swing.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.Properties;

/**
 * Gaussian Filtering panel in the {@link GIANIUI}
 *
 * @author Dave Barry
 * @since 1.0.0
 */
public class FilteringPanel extends LayerPanel implements Updateable {

    private ArrayList<String> channelLabels;
    private final String title;
    private final GIANIParamInfos info;

    /**
     * Default constructor
     */
    public FilteringPanel() {
        this(null, null, null, null);
    }

    /**
     * Constructs a FilteringPanel and associates the specified Properties, BioFormatsImg and process with it.
     *
     * @param props contains the parameters governing how the process associated with this panel will run
     * @param img the image that the process associated with this panel will run on
     * @param process the process that this panel is seeking user-specified parameters for
     * @param propLabels the labels associated with the parameters that this panel will display
     */
    public FilteringPanel(Properties props, BioFormatsImg img, MultiThreadedGaussianFilter process, String[] propLabels) {
        this(props, img, process, propLabels, null, null);
    }

    /**
     * Constructs a FilteringPanel and associates the specified Properties, BioFormatsImg and process with it.
     *
     * @param props contains the parameters governing how the process associated with this panel will run
     * @param img the image that the process associated with this panel will run on
     * @param process the process that this panel is seeking user-specified parameters for
     * @param propLabels the labels associated with the parameters that this panel will display
     * @param helpURI link to an online help page describing how to use this panel
     * @param title description of what this panel does
     */
    public FilteringPanel(Properties props, BioFormatsImg img, MultiThreadedGaussianFilter process, String[] propLabels, URI helpURI, String title) {
        super(props, img, process, propLabels, helpURI);
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
        filterRadiusXYLabel = new javax.swing.JLabel();
        filterRadiusXYTextField = new javax.swing.JTextField();
        channelSelectLabel = new javax.swing.JLabel();
        channelSelectComboBox = new javax.swing.JComboBox<>();
        helpButton = new javax.swing.JButton();
        titleLabel = new javax.swing.JLabel();
        unitsLabel1 = new javax.swing.JLabel();

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
        gridBagConstraints.gridy = 5;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        add(previewButton, gridBagConstraints);

        filterRadiusXYLabel.setText(propLabels[MultiThreadedGaussianFilter.FILT_RAD_LABEL]);
        filterRadiusXYLabel.setLabelFor(filterRadiusXYTextField);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        add(filterRadiusXYLabel, gridBagConstraints);

        filterRadiusXYTextField.setText(props.getProperty(propLabels[MultiThreadedGaussianFilter.FILT_RAD_LABEL]));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        add(filterRadiusXYTextField, gridBagConstraints);

        channelSelectLabel.setText(propLabels[net.calm.iaclasslibrary.Process.Filtering.MultiThreadedGaussianFilter.CHANNEL_LABEL]);
        channelSelectLabel.setLabelFor(channelSelectComboBox);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        add(channelSelectLabel, gridBagConstraints);

        channelSelectComboBox.setModel(new DefaultComboBoxModel(new String[]{}));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
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
        gridBagConstraints.gridy = 5;
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

        unitsLabel1.setText(unitText);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        add(unitsLabel1, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    protected void setToolTips() {
        channelSelectComboBox.setToolTipText(info.getProperty(propLabels[MultiThreadedGaussianFilter.CHANNEL_LABEL]));
        filterRadiusXYTextField.setToolTipText(info.getProperty(propLabels[MultiThreadedGaussianFilter.FILT_RAD_LABEL]));
        previewButton.setToolTipText(info.getProperty(GianiDefaultParams.PREVIEW));
        helpButton.setToolTipText(info.getProperty(GianiDefaultParams.HELP));
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
        process.getOutput().show();
    }//GEN-LAST:event_previewButtonActionPerformed

    private void helpButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_helpButtonActionPerformed
        openHelpPage(GianiDefaultParams.HELP_ERROR_MESSAGE);
    }//GEN-LAST:event_helpButtonActionPerformed

    /**
     * Initialises the process associated with this panel
     */
    public void setupProcess() {
        process.setup(img, props, propLabels);
    }

    /**
     * Updates options on this panel that are influenced by image parameters (for example, the number of channels the
     * user can choose from).
     */
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
        channelSelectComboBox.setSelectedItem(props.get(propLabels[MultiThreadedGaussianFilter.CHANNEL_LABEL]));
        unitsLabel1.setText(props.getProperty(GianiDefaultParams.UNITS));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> channelSelectComboBox;
    private javax.swing.JLabel channelSelectLabel;
    private javax.swing.JLabel filterRadiusXYLabel;
    private javax.swing.JTextField filterRadiusXYTextField;
    private javax.swing.JButton helpButton;
    private javax.swing.JButton previewButton;
    private javax.swing.JLabel titleLabel;
    private javax.swing.JLabel unitsLabel1;
    // End of variables declaration//GEN-END:variables
}
