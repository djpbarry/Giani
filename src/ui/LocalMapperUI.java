package ui;

import Extrema.MultiThreadedMaximaFinder;
import IO.BioFormats.BioFormatsImg;
import IO.PropertyWriter;
import Process.Calculate.MultiThreadedImageCalculator;
import Process.Filtering.MultiThreadedGaussianFilter;
import Process.MultiThreadedProcess;
import Process.ProcessPipeline;
import Process.ROI.MultiThreadedROIConstructor;
import Process.Segmentation.MultiThreadedWatershed;
import UIClasses.GUIMethods;
import UIClasses.UIMethods;
import java.awt.Container;
import java.util.LinkedList;
import java.util.Properties;
import UIClasses.LayerPanel;
import UIClasses.PropertyExtractor;
import UIClasses.Updateable;
import UtilClasses.GenUtils;
import GIANI.LocalMapperExecutor;
import params.DefaultParams;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author David Barry <david.barry at crick dot ac dot uk>
 */
public class LocalMapperUI extends javax.swing.JFrame implements GUIMethods {

    private final BioFormatsImg img;
    private final Properties props = new Properties();
    private final LinkedList<LayerPanel> componentList = new LinkedList();
    private int layerIndex = 0;
    private final String title = "Local Mapper";
    private final ProcessPipeline pipeline;

//    private final ExecutorService exec = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    /**
     * Creates new form LocalMapperUI
     */
    public LocalMapperUI() {
        img = new BioFormatsImg();
        pipeline = new ProcessPipeline();
        initComponents();
        UIMethods.centreContainer(this);
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

        buttonPanel = new javax.swing.JPanel();
        previousButton = new javax.swing.JButton();
        nextButton = new javax.swing.JButton();
        loadParametersButton = new javax.swing.JButton();
        runButton = new javax.swing.JButton();
        selectInputPanel = new ui.SelectInputPanel(props,img,
            new String[]{
                DefaultParams.INPUT_DIR_LABEL,
                DefaultParams.INPUT_FILE_LABEL,
                DefaultParams.SERIES_SELECT_LABEL,
                DefaultParams.PREVIEW_CHAN_SELECT_LABEL});
        nuclearCentreFinderPanel = new ui.MaximaFinderPanel(props,img,new MultiThreadedMaximaFinder(null),
            new String[]{
                DefaultParams.BLOB_FIND_CHAN_SELECT_LABEL,
                DefaultParams.BLOB_RAD_LABEL,
                DefaultParams.BLOB_NOISE_TOL_LABEL});
        nuclearFilteringPanel = new ui.FilteringPanel(props,img, new MultiThreadedGaussianFilter(null),
            new String[]{
                DefaultParams.NUC_SEG_CHAN_SELECT_LABEL,
                DefaultParams.NUC_FILT_RAD_XY_LABEL,
                DefaultParams.NUC_FILT_RAD_Z_LABEL});
        nuclearSegmentationPanel = new ui.SegmentationPanel(
            props,
            img,
            new MultiThreadedWatershed(
                new MultiThreadedProcess[]{
                    nuclearCentreFinderPanel.getProcess(),
                    nuclearFilteringPanel.getProcess()
                },
                "Nuclei",
                false,
                false,
                false
            ),
            new String[]{
                DefaultParams.NUC_SEG_THRESH_LABEL,
                DefaultParams.NUC_VOL_MARKER,
                DefaultParams.NUC_MEM_MARKER
            }
        );
        cellFilteringPanel = new ui.FilteringPanel(props,img, new MultiThreadedGaussianFilter(null),
            new String[]{
                DefaultParams.CELL_SEG_CHAN_SELECT_LABEL,
                DefaultParams.CELL_FILT_RAD_XY_LABEL,
                DefaultParams.CELL_FILT_RAD_Z_LABEL});
        cellSegmentationPanel = new ui.SegmentationPanel(
            props,
            img,
            new MultiThreadedWatershed(
                new MultiThreadedProcess[]{
                    nuclearSegmentationPanel.getProcess(),
                    cellFilteringPanel.getProcess()
                },
                "Cells",
                true,
                true,
                true
            ),
            new String[]{
                DefaultParams.CELL_SEG_THRESH_LABEL,
                DefaultParams.CELL_VOL_MARKER,
                DefaultParams.CELL_MEM_MARKER
            }
        );
        measurementPanel = new ui.MeasurementPanel(
            props,
            img,
            new MultiThreadedROIConstructor(
                new MultiThreadedProcess[]{
                    nuclearSegmentationPanel.getProcess(),
                    cellSegmentationPanel.getProcess(),
                    new MultiThreadedImageCalculator(
                        new MultiThreadedProcess[]{
                            nuclearSegmentationPanel.getProcess(),
                            cellSegmentationPanel.getProcess()
                        },
                        "Cytoplasm",
                        "Difference"
                    )
                }
            ),
            new String[]{
                DefaultParams.CHAN_FOR_MEASURE
            }
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new java.awt.GridBagLayout());

        buttonPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        buttonPanel.setLayout(new java.awt.GridBagLayout());

        previousButton.setText("Previous");
        previousButton.setEnabled(false);
        previousButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                previousButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 20, 10, 20);
        buttonPanel.add(previousButton, gridBagConstraints);

        nextButton.setText("Next");
        nextButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nextButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 20, 10, 20);
        buttonPanel.add(nextButton, gridBagConstraints);

        loadParametersButton.setText("Load Parameters");
        loadParametersButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadParametersButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 20, 10, 20);
        buttonPanel.add(loadParametersButton, gridBagConstraints);

        runButton.setText("Run");
        runButton.setEnabled(false);
        runButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                runButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 20, 10, 20);
        buttonPanel.add(runButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.2;
        getContentPane().add(buttonPanel, gridBagConstraints);

        componentList.add(selectInputPanel);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 0.8;
        getContentPane().add(selectInputPanel, gridBagConstraints);

        nuclearCentreFinderPanel.setVisible(false);
        componentList.add(nuclearCentreFinderPanel);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 0.8;
        getContentPane().add(nuclearCentreFinderPanel, gridBagConstraints);

        nuclearFilteringPanel.setVisible(false);
        componentList.add(nuclearFilteringPanel);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 0.8;
        getContentPane().add(nuclearFilteringPanel, gridBagConstraints);

        nuclearSegmentationPanel.setVisible(false);
        componentList.add(nuclearSegmentationPanel);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 0.8;
        getContentPane().add(nuclearSegmentationPanel, gridBagConstraints);

        cellFilteringPanel.setVisible(false);
        componentList.add(cellFilteringPanel);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 0.8;
        getContentPane().add(cellFilteringPanel, gridBagConstraints);

        cellSegmentationPanel.setVisible(false);
        componentList.add(cellSegmentationPanel);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 0.8;
        getContentPane().add(cellSegmentationPanel, gridBagConstraints);

        measurementPanel.setVisible(false);
        componentList.add(measurementPanel);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        getContentPane().add(measurementPanel, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void nextButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextButtonActionPerformed
        addProcess();
        layerIndex++;
        updateLayer();
    }//GEN-LAST:event_nextButtonActionPerformed

    private void previousButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_previousButtonActionPerformed
        layerIndex--;
        removeProcess();
        updateLayer();
    }//GEN-LAST:event_previousButtonActionPerformed

    private void loadParametersButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadParametersButtonActionPerformed
        try {
            PropertyWriter.loadProperties(props, title);
            updateProperties(props, this);
        } catch (Exception e) {
            GenUtils.logError(e, "Failed to load property file.");
        }
    }//GEN-LAST:event_loadParametersButtonActionPerformed

    private void runButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_runButtonActionPerformed
        setVariables();
        String outputDirectoryName = GenUtils.openResultsDirectory(props.getProperty(DefaultParams.OUTPUT_DIR_LABEL));
        props.setProperty(DefaultParams.OUTPUT_DIR_LABEL, outputDirectoryName);
        try {
            PropertyWriter.printProperties(props, outputDirectoryName, title, true);
        } catch (Exception e) {
            GenUtils.logError(e, "Failed to save property file.");
        }
        addProcess();
        (new LocalMapperExecutor(pipeline, props)).start();
        runButton.setEnabled(false);
    }//GEN-LAST:event_runButtonActionPerformed

    void updateLayer() {
        for (int i = 0; i < componentList.size(); i++) {
            if (i == layerIndex) {
                componentList.get(i).setVisible(true);
                if (componentList.get(i) instanceof Updateable) {
                    ((Updateable) componentList.get(i)).update();
                }
            } else {
                componentList.get(i).setVisible(false);
            }
        }
        checkLayerIndex();
    }

    void checkLayerIndex() {
        if (layerIndex == 0) {
            previousButton.setEnabled(false);
        } else {
            previousButton.setEnabled(true);
        }
        if (layerIndex == componentList.size() - 1) {
            nextButton.setEnabled(false);
            runButton.setEnabled(true);
        } else {
            nextButton.setEnabled(true);
            runButton.setEnabled(false);
        }
    }

    public boolean setVariables() {
        setProperties(props, this);
        return false;
    }

    public void setProperties(Properties p, Container c) {
        PropertyExtractor.setProperties(p, c, PropertyExtractor.WRITE);
    }

    private void updateProperties(Properties p, Container c) {
        PropertyExtractor.setProperties(p, c, PropertyExtractor.READ);
    }

    /**
     * @param args the command line arguments //
     */
//    public static void main(String args[]) {
//        /* Set the Nimbus look and feel */
//        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
//        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
//         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
//         */
//        try {
//            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
//                if ("Nimbus".equals(info.getName())) {
//                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
//                    break;
//                }
//            }
//        } catch (ClassNotFoundException ex) {
//            java.util.logging.Logger.getLogger(LocalMapperUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (InstantiationException ex) {
//            java.util.logging.Logger.getLogger(LocalMapperUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (IllegalAccessException ex) {
//            java.util.logging.Logger.getLogger(LocalMapperUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
//            java.util.logging.Logger.getLogger(LocalMapperUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        }
//        //</editor-fold>
//
//        /* Create and display the form */
//        java.awt.EventQueue.invokeLater(new Runnable() {
//            public void run() {
//                new LocalMapperUI().setVisible(true);
//            }
//        });
//    }
    void cleanUp() {
        this.dispose();
    }

    void addProcess() {
        componentList.get(layerIndex).setVariables();
        MultiThreadedProcess process = componentList.get(layerIndex).getProcess();
        if (process == null) {
            return;
        }
//        MultiThreadedProcess newProcess = null;
//        if (process instanceof MultiThreadedGaussianFilter) {
//            newProcess = new MultiThreadedGaussianFilter();
//        } else if (process instanceof MultiThreadedMaximaFinder) {
//            newProcess = new MultiThreadedMaximaFinder();
//        } else if (process instanceof MultiThreadedWatershed) {
//            newProcess = new MultiThreadedWatershed();
//        } else if (process instanceof MultiThreadedROIConstructor) {
//            newProcess = new MultiThreadedROIConstructor();
//        }
//        newProcess.setup(img, props, process.getPropLabels(), null);
        pipeline.addProcess(process, layerIndex - 1);
    }

    void removeProcess() {
        pipeline.removeProcesses(layerIndex);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel buttonPanel;
    private ui.FilteringPanel cellFilteringPanel;
    private ui.SegmentationPanel cellSegmentationPanel;
    private javax.swing.JButton loadParametersButton;
    private ui.MeasurementPanel measurementPanel;
    private javax.swing.JButton nextButton;
    private ui.MaximaFinderPanel nuclearCentreFinderPanel;
    private ui.FilteringPanel nuclearFilteringPanel;
    private ui.SegmentationPanel nuclearSegmentationPanel;
    private javax.swing.JButton previousButton;
    private javax.swing.JButton runButton;
    private ui.SelectInputPanel selectInputPanel;
    // End of variables declaration//GEN-END:variables
}
