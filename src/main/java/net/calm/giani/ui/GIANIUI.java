package net.calm.giani.ui;

import java.awt.Container;
import java.util.LinkedList;
import java.util.Properties;
import java.awt.GridBagConstraints;
import java.util.ArrayList;
import mcib3d.geom.Objects3DPopulation;
import net.calm.giani.GIANI.PipelineBuilder;
import net.calm.giani.GIANI.PipelineExecutor;
import net.calm.giani.gianiparams.GianiDefaultParams;
import net.calm.iaclasslibrary.Extrema.MultiThreadedMaximaFinder;
import net.calm.iaclasslibrary.IO.BioFormats.BioFormatsImg;
import net.calm.iaclasslibrary.IO.PropertyWriter;
import net.calm.iaclasslibrary.Process.Colocalise.MultiThreadedColocalise;
import net.calm.iaclasslibrary.Process.MultiThreadedProcess;
import net.calm.iaclasslibrary.Process.ProcessPipeline;
import net.calm.iaclasslibrary.Process.ROI.MultiThreadedROIConstructor;
import net.calm.iaclasslibrary.Process.Segmentation.MultiThreadedWatershed;
import net.calm.iaclasslibrary.UIClasses.*;
import net.calm.iaclasslibrary.UtilClasses.GenUtils;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import net.calm.iaclasslibrary.Process.Filtering.MultiThreadedGaussianFilter;
import net.calm.iaclasslibrary.Process.Filtering.MultiThreadedTopHatFilter;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
public class GIANIUI extends javax.swing.JFrame implements GUIMethods {

    private final BioFormatsImg img;
    private static Properties props;
    private final LinkedList<LayerPanel> componentList = new LinkedList();
    private int layerIndex = 0;
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
        setIcon();
    }

    private void setIcon() {
        URL iconURL = getClass().getResource("/icon/icon.png");
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
        selectInputPanel = new net.calm.giani.ui.SelectInputPanel(props,img,
            new String[]{
                GianiDefaultParams.INPUT_DIR_LABEL,
                GianiDefaultParams.INPUT_FILE_LABEL,
                GianiDefaultParams.SERIES_SELECT_LABEL,
                GianiDefaultParams.PREVIEW_CHAN_SELECT_LABEL},
            getHelpURI("https://github.com/djpbarry/Giani/wiki/Selecting-the-Input-Files"));
        componentList.add(selectInputPanel);
        MultiThreadedMaximaFinder maximaFinder = PipelineBuilder.getDefaultMaximaFinder(props);
        nuclearCentreFinderPanel = new net.calm.giani.ui.MaximaFinderPanel(props,img,maximaFinder,
            maximaFinder.getPropLabels(), true, -1,
            getHelpURI("https://github.com/djpbarry/Giani/wiki/Estimating-the-centres-of-nuclei"),
            GianiDefaultParams.NUC_CENTROID_LOCALISATION_TITLE);
        nuclearCentreFinderPanel.setVisible(false);
        componentList.add(nuclearCentreFinderPanel);
        MultiThreadedGaussianFilter nucGaussFilter = PipelineBuilder.getDefaultNucFilteringProcess(props);
        nuclearFilteringPanel = new net.calm.giani.ui.FilteringPanel(props,img,
            nucGaussFilter,
            nucGaussFilter.getPropLabels(),
            getHelpURI("https://github.com/djpbarry/Giani/wiki/Filtering-Prior-to-Nuclear-Segmentation"),
            GianiDefaultParams.NUC_GAUSS_FILTER_TITLE);
        nuclearFilteringPanel.setVisible(false);
        componentList.add(nuclearFilteringPanel);
        MultiThreadedTopHatFilter nucTopHatFilter = PipelineBuilder.getDefaultNucTopHatFilteringProcess(
            props,
            new MultiThreadedProcess[]{
                nuclearFilteringPanel.getProcess()
            }
        );
        nuclearTopHatFilterPanel = new TopHatFilterPanel(props,img, nucTopHatFilter,
            nucTopHatFilter.getPropLabels(),
            getHelpURI("https://github.com/djpbarry/Giani/wiki/Background-Subrtraction-Prior-to-Nuclear-Segmentation"),
            GianiDefaultParams.NUC_TOP_HAT_TITLE);
        nuclearTopHatFilterPanel .setVisible(false);
        componentList.add(nuclearTopHatFilterPanel);
        MultiThreadedWatershed nucSeg = PipelineBuilder.getDefaultNucSegmenter(props, new MultiThreadedProcess[]{
            nuclearCentreFinderPanel.getProcess(),            nuclearTopHatFilterPanel.getProcess()        }, cells);
    nuclearSegmentationPanel = new net.calm.giani.ui.SegmentationPanel(
        props,
        img,
        nucSeg,
        nucSeg.getPropLabels(),
        getHelpURI("https://github.com/djpbarry/Giani/wiki/Segmenting-Nuclei"),
        GianiDefaultParams.NUC_SEG_TITLE
    );
    nuclearSegmentationPanel.setVisible(false);
    componentList.add(nuclearSegmentationPanel);
    net.calm.iaclasslibrary.Process.Filtering.MultiThreadedGaussianFilter cellGaussFilter = PipelineBuilder.getDefaultCellFilteringProcess(props);
    cellFilteringPanel = new net.calm.giani.ui.FilteringPanel(props,img, cellGaussFilter,
        cellGaussFilter.getPropLabels(),
        getHelpURI("https://github.com/djpbarry/Giani/wiki/Filtering-Prior-to-Cell-Segmentation"),
        GianiDefaultParams.CELL_GAUSS_FILTER_TITLE);
    cellFilteringPanel.setVisible(false);
    componentList.add(cellFilteringPanel);
    MultiThreadedWatershed cellSeg = PipelineBuilder.getDefaultCellSegmenter(
        props,
        new MultiThreadedProcess[]{
            nuclearSegmentationPanel.getProcess(),
            cellFilteringPanel.getProcess()
        },
        cells
    );
    cellSegmentationPanel = new net.calm.giani.ui.SegmentationPanel(
        props,
        img,
        cellSeg,
        cellSeg.getPropLabels(),
        getHelpURI("https://github.com/djpbarry/Giani/wiki/Complete-Segmentation-of-Cells"),
        GianiDefaultParams.CELL_SEG_TITLE
    );
    cellSegmentationPanel.setVisible(false);
    componentList.add(cellSegmentationPanel);
    MultiThreadedROIConstructor process = PipelineBuilder.getDefaultMeasure(props,
        new MultiThreadedProcess[]{
            nuclearSegmentationPanel.getProcess(),
            cellSegmentationPanel.getProcess()
        },
        cells);
    measurementPanel = new net.calm.giani.ui.MeasurementPanel(
        props,
        img,
        process,
        process.getPropLabels(),
        getHelpURI("https://github.com/djpbarry/Giani/wiki/Specifying-Channels-to-Measure")
    );
    measurementPanel.setVisible(false);
    componentList.add(measurementPanel);

    setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
    setTitle(GianiDefaultParams.TITLE);
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
    loadParametersButton.setEnabled(false);
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
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 0.8;
    gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
    getContentPane().add(nuclearCentreFinderPanel, gridBagConstraints);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 0.8;
    gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
    getContentPane().add(nuclearFilteringPanel, gridBagConstraints);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 0.8;
    gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
    getContentPane().add(nuclearTopHatFilterPanel, gridBagConstraints);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 0.8;
    gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
    getContentPane().add(nuclearSegmentationPanel, gridBagConstraints);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 0.8;
    gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
    getContentPane().add(cellFilteringPanel, gridBagConstraints);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 0.8;
    gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
    getContentPane().add(cellSegmentationPanel, gridBagConstraints);

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
        loadParameters(props.getProperty(GianiDefaultParams.INPUT_DIR_LABEL));
    }//GEN-LAST:event_loadParametersButtonActionPerformed

    public void loadParameters(String location) {
        String inputDirectory = props.getProperty(GianiDefaultParams.INPUT_DIR_LABEL);
        try {
            PropertyWriter.loadProperties(props, GianiDefaultParams.TITLE, new File(location));
            props.setProperty(GianiDefaultParams.INPUT_DIR_LABEL, inputDirectory);
            updateProperties(props, this);
            measurementPanelMouseClicked(null);
            runButton.setEnabled(true);
        } catch (Exception e) {
            GenUtils.logError(e, "Failed to load property file.");
        }
    }

    private void runButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_runButtonActionPerformed
        setVariables();
        if (!GianiDefaultParams.setOutputDirectory(props, null)) {
            return;
        }
        while (nextButton.isEnabled()) {
            nextButtonActionPerformed(null);
        }
        addProcess();
        PipelineExecutor exec = new PipelineExecutor(pipeline, props);
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
        if (evt.getComponent() instanceof JComboBox) {
            nextButton.setEnabled(false);
            loadParametersButton.setEnabled(false);
        }
    }//GEN-LAST:event_selectInputPanelComponentHidden

    private void selectInputPanelComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_selectInputPanelComponentShown
        if (evt.getComponent() instanceof JComboBox) {
            nextButton.setEnabled(true);
            loadParametersButton.setEnabled(true);
        }
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
        int n = img.getSizeC(Integer.parseInt(props.getProperty(GianiDefaultParams.SERIES_SELECT_LABEL)));
        for (int i = 0; i < n; i++) {
            if ((channels & (int) Math.pow(2.0, i)) != 0) {
                String[] propLabels = new String[MultiThreadedMaximaFinder.N_PROP_LABELS];
                propLabels[MultiThreadedMaximaFinder.CHANNEL_SELECT] = String.format("%s%d", GianiDefaultParams.BLOB_CHAN_SELECT_LABEL, i);
                props.setProperty(propLabels[MultiThreadedMaximaFinder.CHANNEL_SELECT], "0");
                propLabels[MultiThreadedMaximaFinder.BLOB_DETECT] = String.format("%s%d", GianiDefaultParams.FOCI_MAXIMA_DETECT_BLOBS, i);
                props.setProperty(propLabels[MultiThreadedMaximaFinder.BLOB_DETECT], "true");
                propLabels[MultiThreadedMaximaFinder.BLOB_SIZE] = String.format("%s%d", GianiDefaultParams.BLOB_CHAN_RAD_LABEL, i);
                props.setProperty(propLabels[MultiThreadedMaximaFinder.BLOB_SIZE], "0.0");
                propLabels[MultiThreadedMaximaFinder.BLOB_THRESH] = String.format("%s%d", GianiDefaultParams.BLOB_CHAN_NOISE_TOL_LABEL, i);
                props.setProperty(propLabels[MultiThreadedMaximaFinder.BLOB_THRESH], "0.0");
                propLabels[MultiThreadedMaximaFinder.HESSIAN_DETECT] = String.format("%s%d", GianiDefaultParams.FOCI_MAXIMA_DETECT_HESSIAN_MAXIMA, i);
                props.setProperty(propLabels[MultiThreadedMaximaFinder.HESSIAN_DETECT], "false");
                propLabels[MultiThreadedMaximaFinder.HESSIAN_STOP_SCALE] = String.format("%s%d", GianiDefaultParams.FOCI_MAXIMA_DETECT_HESSIAN_MAX_SIZE, i);
                props.setProperty(propLabels[MultiThreadedMaximaFinder.HESSIAN_STOP_SCALE], "0.0");
                propLabels[MultiThreadedMaximaFinder.HESSIAN_START_SCALE] = String.format("%s%d", GianiDefaultParams.FOCI_MAXIMA_DETECT_HESSIAN_MIN_SIZE, i);
                props.setProperty(propLabels[MultiThreadedMaximaFinder.HESSIAN_START_SCALE], "0.0");
                propLabels[MultiThreadedMaximaFinder.HESSIAN_THRESH] = String.format("%s%d", GianiDefaultParams.FOCI_MAXIMA_DETECT_HESSIAN_THRESH, i);
                props.setProperty(propLabels[MultiThreadedMaximaFinder.HESSIAN_THRESH], "0.0");
                propLabels[MultiThreadedMaximaFinder.HESSIAN_SCALE_STEP] = String.format("%s%d", GianiDefaultParams.FOCI_MAXIMA_DETECT_HESSIAN_SCALE_STEP, i);
                props.setProperty(propLabels[MultiThreadedMaximaFinder.HESSIAN_SCALE_STEP], "0.0");
                propLabels[MultiThreadedMaximaFinder.HESSIAN_ABS] = String.format("%s%d", GianiDefaultParams.FOCI_MAXIMA_DETECT_HESSIAN_ABS, i);
                props.setProperty(propLabels[MultiThreadedMaximaFinder.HESSIAN_ABS], "true");
                propLabels[MultiThreadedMaximaFinder.SERIES_SELECT] = GianiDefaultParams.SERIES_SELECT_LABEL;
                MaximaFinderPanel mFP = new MaximaFinderPanel(props, img, new MultiThreadedMaximaFinder(null),
                        propLabels, false, i, null, String.format("%s%d", GianiDefaultParams.FOCI_CENTROID_LOCALISATION_TITLE, i));
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
        String[] localisationPropLabels = new String[MultiThreadedColocalise.N_PROP_LABELS];
        localisationPropLabels[MultiThreadedColocalise.SERIES_LABEL] = GianiDefaultParams.SERIES_SELECT_LABEL;
        localisationPropLabels[MultiThreadedColocalise.CHANNELS_LABEL] = GianiDefaultParams.CHAN_FOR_MEASURE;
        localisationPropLabels[MultiThreadedColocalise.OUTPUT_LABEL] = GianiDefaultParams.OUTPUT_DIR_LABEL;

        localisationPanel = new LocalisationPanel(props, img, new MultiThreadedColocalise(localisationInputs, cells), localisationPropLabels);
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

    private URI getHelpURI(String url) {
        URI helpURI;
        try {
            helpURI = new URI(url);
        } catch (URISyntaxException e) {
            return null;
        }
        return helpURI;
    }

//    public ProcessPipeline buildPipeline() {
//        ProcessPipeline output = new ProcessPipeline();
//        for (LayerPanel c : componentList) {
//            c.setupProcess();
//            MultiThreadedProcess process = c.getProcess();
//            if (process != null) {
//                output.addProcess(process);
//            }
//        }
//        return output;
//    }
    public static Properties getProps() {
        return props;
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel buttonPanel;
    private net.calm.giani.ui.FilteringPanel cellFilteringPanel;
    private net.calm.giani.ui.SegmentationPanel cellSegmentationPanel;
    private javax.swing.JButton loadParametersButton;
    private net.calm.giani.ui.MeasurementPanel measurementPanel;
    private javax.swing.JButton nextButton;
    private net.calm.giani.ui.MaximaFinderPanel nuclearCentreFinderPanel;
    private net.calm.giani.ui.FilteringPanel nuclearFilteringPanel;
    private net.calm.giani.ui.SegmentationPanel nuclearSegmentationPanel;
    private net.calm.giani.ui.TopHatFilterPanel nuclearTopHatFilterPanel;
    private javax.swing.JButton previousButton;
    private javax.swing.JButton runButton;
    private net.calm.giani.ui.SelectInputPanel selectInputPanel;
    // End of variables declaration//GEN-END:variables
}
