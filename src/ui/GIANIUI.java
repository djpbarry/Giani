package ui;

import Extrema.MultiThreadedMaximaFinder;
import IO.BioFormats.BioFormatsImg;
import IO.PropertyWriter;
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
import Process.Colocalise.MultiThreadedColocalise;
import Revision.Revision;
import java.awt.GridBagConstraints;
import java.text.DecimalFormat;
import java.util.ArrayList;
import mcib3d.geom.Objects3DPopulation;
import gianiparams.GianiDefaultParams;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author David Barry <david.barry at crick dot ac dot uk>
 */
public class GIANIUI extends javax.swing.JFrame implements GUIMethods {

    private final BioFormatsImg img;
    private static Properties props;
    private final LinkedList<LayerPanel> componentList = new LinkedList();
    private int layerIndex = 0;
    public static final String TITLE = String.format("GIANI v%d.%s", Revision.VERSION, new DecimalFormat("000").format(Revision.revisionNumber));
    private final ProcessPipeline pipeline;
    private ArrayList<MaximaFinderPanel> maximaFinderPanels;
    private LocalisationPanel localisationPanel;
    private final Objects3DPopulation cells;

    static {
        if (props == null) {
            props = new GianiDefaultParams();
        }
    }

//    private final ExecutorService exec = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    /**
     * Creates new form LocalMapperUI
     */
    public GIANIUI() {
        img = new BioFormatsImg();
        pipeline = new ProcessPipeline();
        cells = new Objects3DPopulation();
        initComponents();
        UIMethods.centreContainer(this);
        URL iconURL = getClass().getResource("/icon/icon.png");
// iconURL is null when not found
        ImageIcon icon = new ImageIcon(iconURL);
        this.setIconImage(icon.getImage());
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
                GianiDefaultParams.INPUT_DIR_LABEL,
                GianiDefaultParams.INPUT_FILE_LABEL,
                GianiDefaultParams.SERIES_SELECT_LABEL,
                GianiDefaultParams.PREVIEW_CHAN_SELECT_LABEL});
        String[] nuclearCentreFinderPanelPropLabels = new String[MultiThreadedMaximaFinder.N_PROP_LABELS];
        nuclearCentreFinderPanelPropLabels[MultiThreadedMaximaFinder.CHANNEL_SELECT] = GianiDefaultParams.BLOB_NUC_CHAN_SELECT_LABEL;
        nuclearCentreFinderPanelPropLabels[MultiThreadedMaximaFinder.BLOB_DETECT] = GianiDefaultParams.NUC_MAXIMA_DETECT_BLOBS;
        nuclearCentreFinderPanelPropLabels[MultiThreadedMaximaFinder.BLOB_SIZE] = GianiDefaultParams.BLOB_NUC_RAD_LABEL;
        nuclearCentreFinderPanelPropLabels[MultiThreadedMaximaFinder.BLOB_THRESH] = GianiDefaultParams.BLOB_NUC_NOISE_TOL_LABEL;
        nuclearCentreFinderPanelPropLabels[MultiThreadedMaximaFinder.EDM_DETECT] = GianiDefaultParams.NUC_MAXIMA_DETECT_EDM_MAXIMA;
        nuclearCentreFinderPanelPropLabels[MultiThreadedMaximaFinder.EDM_MAX_SIZE] = GianiDefaultParams.NUC_MAXIMA_DETECT_EDM_MAX_SIZE;
        nuclearCentreFinderPanelPropLabels[MultiThreadedMaximaFinder.EDM_MIN_SIZE] = GianiDefaultParams.NUC_MAXIMA_DETECT_EDM_MIN_SIZE;
        nuclearCentreFinderPanelPropLabels[MultiThreadedMaximaFinder.EDM_THRESH] = GianiDefaultParams.NUC_MAXIMA_DETECT_EDM_THRESH;
        nuclearCentreFinderPanelPropLabels[MultiThreadedMaximaFinder.SERIES_SELECT] = GianiDefaultParams.SERIES_SELECT_LABEL;
        nuclearCentreFinderPanel = new ui.MaximaFinderPanel(props,img,new MultiThreadedMaximaFinder(null),
            nuclearCentreFinderPanelPropLabels, true, -1);
        nuclearFilteringPanel = new ui.FilteringPanel(props,img, new MultiThreadedGaussianFilter(null),
            new String[]{
                GianiDefaultParams.NUC_SEG_CHAN_SELECT_LABEL,
                GianiDefaultParams.NUC_FILT_RAD_XY_LABEL,
                GianiDefaultParams.NUC_FILT_RAD_Z_LABEL});
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
                false,
                cells,
                MultiThreadedWatershed.NUCLEI
            ),
            new String[]{
                GianiDefaultParams.SERIES_SELECT_LABEL,
                GianiDefaultParams.NUC_SEG_THRESH_LABEL,
                GianiDefaultParams.NUC_VOL_MARKER,
                GianiDefaultParams.NUC_MEM_MARKER,
                GianiDefaultParams.DIST_WEIGHTING
            }
        );
        cellFilteringPanel = new ui.FilteringPanel(props,img, new MultiThreadedGaussianFilter(null),
            new String[]{
                GianiDefaultParams.CELL_SEG_CHAN_SELECT_LABEL,
                GianiDefaultParams.CELL_FILT_RAD_XY_LABEL,
                GianiDefaultParams.CELL_FILT_RAD_Z_LABEL});
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
                true,
                cells,
                MultiThreadedWatershed.CELLS
            ),
            new String[]{
                GianiDefaultParams.SERIES_SELECT_LABEL,
                GianiDefaultParams.CELL_SEG_THRESH_LABEL,
                GianiDefaultParams.CELL_VOL_MARKER,
                GianiDefaultParams.CELL_MEM_MARKER,
                GianiDefaultParams.DIST_WEIGHTING
            }
        );
        measurementPanel = new ui.MeasurementPanel(
            props,
            img,
            new MultiThreadedROIConstructor(
                new MultiThreadedProcess[]{
                    nuclearSegmentationPanel.getProcess(),
                    cellSegmentationPanel.getProcess()
                },
                cells
            ),
            new String[]{
                GianiDefaultParams.CHAN_FOR_MEASURE,
                GianiDefaultParams.LOCALISE_SPOTS
            }
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(TITLE);
        setIconImages(null);
        setMinimumSize(new java.awt.Dimension(800, 800));
        getContentPane().setLayout(new java.awt.GridBagLayout());

        buttonPanel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
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
        nextButton.setEnabled(false);
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
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.2;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        getContentPane().add(buttonPanel, gridBagConstraints);

        componentList.add(selectInputPanel);
        selectInputPanel.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentHidden(java.awt.event.ComponentEvent evt) {
                selectInputPanelComponentHidden(evt);
            }
            public void componentShown(java.awt.event.ComponentEvent evt) {
                selectInputPanelComponentShown(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.8;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        getContentPane().add(selectInputPanel, gridBagConstraints);

        nuclearCentreFinderPanel.setVisible(false);
        componentList.add(nuclearCentreFinderPanel);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.8;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        getContentPane().add(nuclearCentreFinderPanel, gridBagConstraints);

        nuclearFilteringPanel.setVisible(false);
        componentList.add(nuclearFilteringPanel);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.8;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        getContentPane().add(nuclearFilteringPanel, gridBagConstraints);

        nuclearSegmentationPanel.setVisible(false);
        componentList.add(nuclearSegmentationPanel);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.8;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        getContentPane().add(nuclearSegmentationPanel, gridBagConstraints);

        cellFilteringPanel.setVisible(false);
        componentList.add(cellFilteringPanel);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.8;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        getContentPane().add(cellFilteringPanel, gridBagConstraints);

        cellSegmentationPanel.setVisible(false);
        componentList.add(cellSegmentationPanel);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.8;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        getContentPane().add(cellSegmentationPanel, gridBagConstraints);

        measurementPanel.setVisible(false);
        componentList.add(measurementPanel);
        measurementPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                measurementPanelMouseClicked(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.8;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
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
            PropertyWriter.loadProperties(props, TITLE, null);
            updateProperties(props, this);
            measurementPanelMouseClicked(null);
            runButton.setEnabled(true);
        } catch (Exception e) {
            GenUtils.logError(e, "Failed to load property file.");
        }
    }//GEN-LAST:event_loadParametersButtonActionPerformed

    private void runButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_runButtonActionPerformed
        setVariables();
        String outputDirectoryName = GenUtils.openResultsDirectory(props.getProperty(GianiDefaultParams.OUTPUT_DIR_LABEL));
        props.setProperty(GianiDefaultParams.OUTPUT_DIR_LABEL, outputDirectoryName);
        addProcess();
        LocalMapperExecutor exec = new LocalMapperExecutor(pipeline, props);
        exec.start();
        runButton.setEnabled(false);
        try {
            exec.join();
        } catch (InterruptedException e) {
            GenUtils.logError(e, "Failed to execute pipeline.");
        }
        runButton.setEnabled(true);
    }//GEN-LAST:event_runButtonActionPerformed

    private void measurementPanelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_measurementPanelMouseClicked
        addAdditionalBlobDetectionPanels();
    }//GEN-LAST:event_measurementPanelMouseClicked

    private void selectInputPanelComponentHidden(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_selectInputPanelComponentHidden
        if (evt.getComponent() instanceof JComboBox)
            nextButton.setEnabled(false);
    }//GEN-LAST:event_selectInputPanelComponentHidden

    private void selectInputPanelComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_selectInputPanelComponentShown
        if (evt.getComponent() instanceof JComboBox)
            nextButton.setEnabled(true);
    }//GEN-LAST:event_selectInputPanelComponentShown

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
        pipeline.addProcess(process, layerIndex - 1);
    }

    void removeProcess() {
        pipeline.removeProcesses(layerIndex);
    }

    protected boolean addAdditionalBlobDetectionPanels() {
        setVariables();
        if (maximaFinderPanels != null) {
            for (MaximaFinderPanel mfp : maximaFinderPanels) {
                componentList.remove(mfp);
                getContentPane().remove(mfp);
            }
            if (localisationPanel != null) {
                componentList.remove(localisationPanel);
                getContentPane().remove(localisationPanel);
            }
        }
        maximaFinderPanels = new ArrayList();
        String selectedChannels = props.getProperty(GianiDefaultParams.CHAN_FOR_MEASURE);
        if (selectedChannels == null || !Boolean.parseBoolean(props.getProperty(GianiDefaultParams.LOCALISE_SPOTS))) {
            updateUI();
            return false;
        }
        int channels = Integer.parseInt(selectedChannels);
        int n = img.getSizeC();
        for (int i = 0; i < n; i++) {
            if ((channels & (int) Math.pow(2.0, i)) != 0) {
                String[] propLabels = new String[MultiThreadedMaximaFinder.N_PROP_LABELS];
                propLabels[MultiThreadedMaximaFinder.CHANNEL_SELECT] = String.format("%s%d", GianiDefaultParams.BLOB_CHAN_SELECT_LABEL, i);
                propLabels[MultiThreadedMaximaFinder.BLOB_DETECT] = String.format("%s%d", GianiDefaultParams.FOCI_MAXIMA_DETECT_BLOBS, i);
                props.setProperty(String.format("%s%d", GianiDefaultParams.FOCI_MAXIMA_DETECT_BLOBS, i), "true");
                propLabels[MultiThreadedMaximaFinder.BLOB_SIZE] = String.format("%s%d", GianiDefaultParams.BLOB_CHAN_RAD_LABEL, i);
                propLabels[MultiThreadedMaximaFinder.BLOB_THRESH] = String.format("%s%d", GianiDefaultParams.BLOB_CHAN_NOISE_TOL_LABEL, i);
                propLabels[MultiThreadedMaximaFinder.EDM_DETECT] = String.format("%s%d", GianiDefaultParams.FOCI_MAXIMA_DETECT_EDM_MAXIMA, i);
                propLabels[MultiThreadedMaximaFinder.EDM_MAX_SIZE] = String.format("%s%d", GianiDefaultParams.FOCI_MAXIMA_DETECT_EDM_MAX_SIZE, i);
                propLabels[MultiThreadedMaximaFinder.EDM_MIN_SIZE] = String.format("%s%d", GianiDefaultParams.FOCI_MAXIMA_DETECT_EDM_MIN_SIZE, i);
                propLabels[MultiThreadedMaximaFinder.EDM_THRESH] = String.format("%s%d", GianiDefaultParams.FOCI_MAXIMA_DETECT_EDM_THRESH, i);
                propLabels[MultiThreadedMaximaFinder.SERIES_SELECT] = GianiDefaultParams.SERIES_SELECT_LABEL;
                MaximaFinderPanel mFP = new ui.MaximaFinderPanel(props, img, new MultiThreadedMaximaFinder(null),
                        propLabels, false, i);
                maximaFinderPanels.add(mFP);
                addPanel(mFP);
            }
        }
        int nInputs = maximaFinderPanels.size() + 2;
        MultiThreadedProcess[] localisationInputs = new MultiThreadedProcess[nInputs];
        for (int j = 0; j < maximaFinderPanels.size(); j++) {
            localisationInputs[j] = maximaFinderPanels.get(j).getProcess();
        }
        localisationInputs[nInputs - 2] = cellSegmentationPanel.getProcess();
        localisationInputs[nInputs - 1] = nuclearSegmentationPanel.getProcess();
        localisationPanel = new ui.LocalisationPanel(props, img, new MultiThreadedColocalise(localisationInputs, cells), new String[]{GianiDefaultParams.SERIES_SELECT_LABEL, GianiDefaultParams.OUTPUT_DIR_LABEL});
        addPanel(localisationPanel);
        updateUI();
        return true;
    }

    void updateUI() {
        getContentPane().revalidate();
        getContentPane().repaint();
        checkLayerIndex();
        updateProperties(props, this);
    }

    void addPanel(LayerPanel panel) {
        panel.setVisible(false);
        componentList.add(panel);
        GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.8;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        getContentPane().add(panel, gridBagConstraints);
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
