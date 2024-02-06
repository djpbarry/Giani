/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.calm.giani.ui;

import net.calm.giani.gianiparams.GIANIParamInfos;
import net.calm.giani.gianiparams.GianiDefaultParams;
import net.calm.iaclasslibrary.IO.BioFormats.LocationAgnosticBioFormatsImg;
import net.calm.iaclasslibrary.Process.Filtering.MultiThreadedTopHatFilter;
import net.calm.iaclasslibrary.UIClasses.LayerPanel;
import net.calm.iaclasslibrary.UIClasses.Updateable;

import java.net.URI;
import java.util.ArrayList;
import java.util.Properties;

/**
 * Top-Hat Filtering panel in the {@link GIANIUI}
 *
 * @author Dave Barry
 * @since 3.0.0
 */
public class TopHatFilterPanel extends LayerPanel implements Updateable {

    private ArrayList<String> channelLabels;
    private final String title;
    private final GIANIParamInfos info;

    /**
     * Default constructor
     */
    public TopHatFilterPanel() {
        this(null, null, null, null);
    }

    /**
     * Constructs a TopHatFilterPanel and associates the specified Properties,
     * BioFormatsImg and process with it.
     *
     * @param props      contains the parameters governing how the process associated
     *                   with this panel will run
     * @param img        the image that the process associated with this panel will run
     *                   on
     * @param process    the process that this panel is seeking user-specified
     *                   parameters for
     * @param propLabels the labels associated with the parameters that this
     *                   panel will display
     */
    public TopHatFilterPanel(Properties props, LocationAgnosticBioFormatsImg img, MultiThreadedTopHatFilter process, String[] propLabels) {
        this(props, img, process, propLabels, null, null);
    }

    /**
     * Constructs a TopHatFilterPanel and associates the specified Properties,
     * BioFormatsImg and process with it.
     *
     * @param props      contains the parameters governing how the process associated
     *                   with this panel will run
     * @param img        the image that the process associated with this panel will run
     *                   on
     * @param process    the process that this panel is seeking user-specified
     *                   parameters for
     * @param propLabels the labels associated with the parameters that this
     *                   panel will display
     * @param helpURI    link to an online help page describing how to use this
     *                   panel
     * @param title      description of what this panel does
     */
    public TopHatFilterPanel(Properties props, LocationAgnosticBioFormatsImg img, MultiThreadedTopHatFilter process, String[] propLabels, URI helpURI, String title) {
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
        helpButton = new javax.swing.JButton();
        downsizeFactorLabel = new javax.swing.JLabel();
        downSizeFactorTextField = new javax.swing.JTextField();
        titleLabel = new javax.swing.JLabel();
        unitsLabel1 = new javax.swing.JLabel();
        topHatToggleButton = new javax.swing.JToggleButton();

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
        gridBagConstraints.gridy = 4;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        add(previewButton, gridBagConstraints);

        filterRadiusXYLabel.setText(propLabels[MultiThreadedTopHatFilter.FILT_RAD_LABEL]);
        filterRadiusXYLabel.setLabelFor(filterRadiusXYTextField);
        filterRadiusXYLabel.setEnabled(Boolean.parseBoolean(props.getProperty(GianiDefaultParams.ENABLE_TOP_HAT_FILTER)));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        add(filterRadiusXYLabel, gridBagConstraints);

        filterRadiusXYTextField.setText(props.getProperty(propLabels[MultiThreadedTopHatFilter.FILT_RAD_LABEL]));
        filterRadiusXYTextField.setEnabled(Boolean.parseBoolean(props.getProperty(GianiDefaultParams.ENABLE_TOP_HAT_FILTER)));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
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
        gridBagConstraints.gridy = 4;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        add(helpButton, gridBagConstraints);

        downsizeFactorLabel.setText(propLabels[MultiThreadedTopHatFilter.RESIZE_FACTOR_LABEL]);
        downsizeFactorLabel.setLabelFor(downSizeFactorTextField);
        downsizeFactorLabel.setEnabled(Boolean.parseBoolean(props.getProperty(GianiDefaultParams.ENABLE_TOP_HAT_FILTER)));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        add(downsizeFactorLabel, gridBagConstraints);

        downSizeFactorTextField.setText(props.getProperty(propLabels[MultiThreadedTopHatFilter.RESIZE_FACTOR_LABEL]));
        downSizeFactorTextField.setEnabled(Boolean.parseBoolean(props.getProperty(GianiDefaultParams.ENABLE_TOP_HAT_FILTER)));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
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

        unitsLabel1.setText(unitText);
        unitsLabel1.setEnabled(Boolean.parseBoolean(props.getProperty(GianiDefaultParams.ENABLE_TOP_HAT_FILTER)));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        add(unitsLabel1, gridBagConstraints);

        topHatToggleButton.setText(propLabels[MultiThreadedTopHatFilter.ENABLE_FILTER_LABEL]);
        topHatToggleButton.setEnabled(Boolean.parseBoolean(props.getProperty(GianiDefaultParams.ENABLE_TOP_HAT_FILTER)));
        topHatToggleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                topHatToggleButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        add(topHatToggleButton, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    protected void setToolTips() {
        filterRadiusXYTextField.setToolTipText(info.getProperty(propLabels[MultiThreadedTopHatFilter.FILT_RAD_LABEL]));
        downSizeFactorTextField.setToolTipText(info.getProperty(propLabels[MultiThreadedTopHatFilter.RESIZE_FACTOR_LABEL]));
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

    private void topHatToggleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_topHatToggleButtonActionPerformed
        filterRadiusXYLabel.setEnabled(topHatToggleButton.isSelected());
        filterRadiusXYTextField.setEnabled(topHatToggleButton.isSelected());
        downSizeFactorTextField.setEnabled(topHatToggleButton.isSelected());
        downsizeFactorLabel.setEnabled(topHatToggleButton.isSelected());
        unitsLabel1.setEnabled(topHatToggleButton.isSelected());
    }//GEN-LAST:event_topHatToggleButtonActionPerformed

    public void setupProcess() {
        process.setup(img, props, propLabels);
    }

    public void update() {
        unitsLabel1.setText(props.getProperty(GianiDefaultParams.UNITS));
        if (Boolean.parseBoolean(props.getProperty(GianiDefaultParams.NUC_MAXIMA_DETECT_STARDIST))
                && this.title.matches(GianiDefaultParams.NUC_TOP_HAT_TITLE)) {
            this.setEnabled(false);
        } else this.setEnabled(true);
        setComponentsEnabled();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField downSizeFactorTextField;
    private javax.swing.JLabel downsizeFactorLabel;
    private javax.swing.JLabel filterRadiusXYLabel;
    private javax.swing.JTextField filterRadiusXYTextField;
    private javax.swing.JButton helpButton;
    private javax.swing.JButton previewButton;
    private javax.swing.JLabel titleLabel;
    private javax.swing.JToggleButton topHatToggleButton;
    private javax.swing.JLabel unitsLabel1;
    // End of variables declaration//GEN-END:variables
}
