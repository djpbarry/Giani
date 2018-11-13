package ui;

import Extrema.MultiThreadedMaximaFinder;
import IO.BioFormats.BioFormatsImg;
import IO.PropertyWriter;
import Process.Filtering.MultiThreadedGaussianFilter;
import Process.MultiThreadedProcess;
import Process.ProcessPipeline;
import Process.Segmentation.MultiThreadedWatershed;
import UIClasses.GUIMethods;
import UIClasses.UIMethods;
import java.awt.Container;
import java.util.LinkedList;
import java.util.Properties;
import UIClasses.LayerPanel;
import UIClasses.PropertyExtractor;
import UtilClasses.GenUtils;
import core.LocalMapperExecutor;
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

        jScrollPane1 = new javax.swing.JScrollPane();
        statusTextArea = new javax.swing.JTextArea();
        buttonPanel = new javax.swing.JPanel();
        previousButton = new javax.swing.JButton();
        nextButton = new javax.swing.JButton();
        saveParamsButton = new javax.swing.JButton();
        loadParametersButton = new javax.swing.JButton();
        runButton = new javax.swing.JButton();
        selectInputPanel = new ui.SelectInputPanel(statusTextArea,props,img,null);
        filteringPanel = new ui.FilteringPanel(props,img);
        maximaFinderPanel = new ui.MaximaFinderPanel(props,img);
        segmentationPanel = new ui.SegmentationPanel(props,img);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new java.awt.GridBagLayout());

        statusTextArea.setColumns(20);
        statusTextArea.setRows(5);
        jScrollPane1.setViewportView(statusTextArea);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 0.8;
        getContentPane().add(jScrollPane1, gridBagConstraints);

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
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
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
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        buttonPanel.add(nextButton, gridBagConstraints);

        saveParamsButton.setText("Save Parameters");
        saveParamsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveParamsButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        buttonPanel.add(saveParamsButton, gridBagConstraints);

        loadParametersButton.setText("Load Parameters");
        loadParametersButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadParametersButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        buttonPanel.add(loadParametersButton, gridBagConstraints);

        runButton.setText("Run");
        runButton.setEnabled(false);
        runButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                runButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
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

        filteringPanel.setVisible(false);
        componentList.add(filteringPanel);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 0.8;
        getContentPane().add(filteringPanel, gridBagConstraints);

        maximaFinderPanel.setVisible(false);
        componentList.add(maximaFinderPanel);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 0.8;
        getContentPane().add(maximaFinderPanel, gridBagConstraints);

        segmentationPanel.setVisible(false);
        componentList.add(segmentationPanel);
        segmentationPanel.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                segmentationPanelComponentShown(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 0.8;
        getContentPane().add(segmentationPanel, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void nextButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextButtonActionPerformed
        componentList.get(layerIndex).setVariables();
        addProcess();
        layerIndex++;
        updateLayer();
    }//GEN-LAST:event_nextButtonActionPerformed

    private void previousButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_previousButtonActionPerformed
        componentList.get(layerIndex).setVariables();
        layerIndex--;
        removeProcess();
        updateLayer();
    }//GEN-LAST:event_previousButtonActionPerformed

    private void saveParamsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveParamsButtonActionPerformed
        setVariables();
        try {
            PropertyWriter.printProperties(props, props.getProperty(DefaultParams.INPUT_DIR_LABEL), title, true);
        } catch (Exception e) {
            GenUtils.logError(e, "Failed to save property file.");
        }
        cleanUp();
    }//GEN-LAST:event_saveParamsButtonActionPerformed

    private void loadParametersButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadParametersButtonActionPerformed
        try {
            PropertyWriter.loadProperties(props, title);
        } catch (Exception e) {
            GenUtils.logError(e, "Failed to load property file.");
        }
    }//GEN-LAST:event_loadParametersButtonActionPerformed

    private void segmentationPanelComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_segmentationPanelComponentShown
        segmentationPanel.updateChannels();
    }//GEN-LAST:event_segmentationPanelComponentShown

    private void runButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_runButtonActionPerformed
        addProcess();
        (new LocalMapperExecutor(pipeline, props)).executePipeline();
    }//GEN-LAST:event_runButtonActionPerformed

    void updateLayer() {
        for (int i = 0; i < componentList.size(); i++) {
            if (i == layerIndex) {
                componentList.get(i).setVisible(true);
            } else {
                componentList.get(i).setVisible(false);
            }
        }
        if (img != null) {
            statusTextArea.append(img.getInfo(Integer.parseInt(props.getProperty(DefaultParams.SERIES_SELECT_LABEL))));
            System.out.print(img.getInfo(Integer.parseInt(props.getProperty(DefaultParams.SERIES_SELECT_LABEL))));
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
        PropertyExtractor.setProperties(p, c);
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
        MultiThreadedProcess process = componentList.get(layerIndex).getProcess();
        if (process == null) {
            return;
        }
        MultiThreadedProcess newProcess = null;
        if (process instanceof MultiThreadedGaussianFilter) {
            newProcess = new MultiThreadedGaussianFilter();
        } else if (process instanceof MultiThreadedMaximaFinder) {
            newProcess = new MultiThreadedMaximaFinder();
        } else if (process instanceof MultiThreadedWatershed) {
            newProcess = new MultiThreadedWatershed();
        }
        newProcess.setup(img, props, process.getPropLabels());
        pipeline.addProcess(newProcess, layerIndex - 1);
    }

    void removeProcess() {
        pipeline.removeProcesses(layerIndex);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel buttonPanel;
    private ui.FilteringPanel filteringPanel;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton loadParametersButton;
    private ui.MaximaFinderPanel maximaFinderPanel;
    private javax.swing.JButton nextButton;
    private javax.swing.JButton previousButton;
    private javax.swing.JButton runButton;
    private javax.swing.JButton saveParamsButton;
    private ui.SegmentationPanel segmentationPanel;
    private ui.SelectInputPanel selectInputPanel;
    private javax.swing.JTextArea statusTextArea;
    // End of variables declaration//GEN-END:variables
}
