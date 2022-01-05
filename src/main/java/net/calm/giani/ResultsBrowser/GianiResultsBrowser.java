/*
 * Copyright (C) 2019 David Barry <david.barry at crick dot ac dot uk>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.calm.giani.ResultsBrowser;

import fiji.plugin.trackmate.Spot;
import ij.CompositeImage;
import ij.IJ;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Properties;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import loci.formats.FormatException;
import mcib3d.geom.Object3D;
import mcib3d.geom.Objects3DPopulation;
import mcib_plugins.tools.RoiManager3D_2;
import net.calm.giani.gianiparams.GianiDefaultParams;
import net.calm.iaclasslibrary.IO.BioFormats.BioFormatsFileLister;
import net.calm.iaclasslibrary.IO.BioFormats.BioFormatsImg;
import net.calm.iaclasslibrary.IO.DataReader;
import net.calm.iaclasslibrary.IO.PropertyWriter;
import net.calm.iaclasslibrary.UtilClasses.GenUtils;
import net.calm.iaclasslibrary.UtilClasses.Utilities;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.SuffixFileFilter;

public class GianiResultsBrowser extends javax.swing.JFrame implements MouseListener {

    private RoiManager3D_2 roiManager;
    private JList roiManagerObjectList;
    private GianiResultsTable tableWrapper;
    private JTable resultsTable;
    private Objects3DPopulation popImp;
    private static File inputDirectory;
    private CompositeImage imp;
    private BioFormatsImg img;
    private Properties props;
//    private boolean spotData;
    private ArrayList<ArrayList<Spot>> spots;
    private LinkedHashMap<Integer, Integer> cellIndexSpotMap;
    private int currentSeries;
    public static final String TITLE = "GIANI Results Browser";

    /**
     * Creates new form GianiResultsBrowser
     */
    public GianiResultsBrowser() {
        initComponents();
        setIcon();
    }

    private void setIcon() {
        URL iconURL = getClass().getResource("/icon/icon.png");
        ImageIcon icon = new ImageIcon(iconURL);
        this.setIconImage(icon.getImage());
    }

    public void mouseExited(MouseEvent e) {

    }

    public void mouseEntered(MouseEvent e) {

    }

    public void mousePressed(MouseEvent e) {

    }

    public void mouseReleased(MouseEvent e) {

    }

    public void mouseClicked(MouseEvent e) {
        Object o = e.getSource();
        if (!(o instanceof JList || o instanceof JTable)) {
            return;
        }
        if (o instanceof JList) {
            String label = (String) roiManagerObjectList.getSelectedValue();
            for (int r = 0; r < resultsTable.getRowCount(); r++) {
                if (label.contentEquals((String) resultsTable.getValueAt(r, 0))) {
                    resultsTable.setRowSelectionInterval(r, r);
                    break;
                }
            }
        } else if (o instanceof JTable) {
            int row = resultsTable.getSelectedRow();
            String label = (String) resultsTable.getValueAt(row, 0);
            roiManagerObjectList.setSelectedValue(label, true);
        }
        setStackPosition();
//        if (this.spotData) {
//            int row = resultsTable.getSelectedRow();
//            String[] colHeadings = tableWrapper.getTableHeadings();
//            int col = 0;
//            while (!colHeadings[col].equalsIgnoreCase("Index")) {
//                col++;
//            }
//            int id = (int) Math.round((double) resultsTable.getValueAt(row, col));
//            ArrayList<Spot> spotSelection = spots.get(cellIndexSpotMap.get(id));
//            if (spotSelection.size() < 1) {
//                return;
//            }
//            ArrayList<int[]> maxima = OverlayDrawer.convertSpotsToMaximas(spotSelection, img.getCalibration(currentSeries));
//            double maxRadius = spotSelection.get(0).getFeature("RADIUS");
//        }
    }

    void setStackPosition() {
        int index = roiManagerObjectList.getSelectedIndex();
        Object3D object = popImp.getObject(index);
        int z = (int) Math.round(object.getCenterZ());
        imp.setZ(z);
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
        objectList = new javax.swing.JList<>();
        loadObjectsButton = new javax.swing.JButton();
        setDirectoryButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(TITLE);
        getContentPane().setLayout(new java.awt.GridBagLayout());

        objectList.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = {"Select input directory..."};
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        objectList.setEnabled(false);
        jScrollPane1.setViewportView(objectList);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        getContentPane().add(jScrollPane1, gridBagConstraints);

        loadObjectsButton.setText("Load Dataset");
        loadObjectsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadObjectsButtonActionPerformed(evt);
            }
        });
        loadObjectsButton.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        getContentPane().add(loadObjectsButton, gridBagConstraints);

        setDirectoryButton.setText("Select Results Directory");
        setDirectoryButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setDirectoryButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        getContentPane().add(setDirectoryButton, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void setDirectoryButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_setDirectoryButtonActionPerformed
        try {
            inputDirectory = Utilities.getFolder(inputDirectory, "Select results directory", false);
        } catch (InterruptedException | InvocationTargetException e) {
            GenUtils.logError(e, "Failed to open results directory.");
            loadObjectsButton.setEnabled(false);
            objectList.setEnabled(false);
        }
        if (!inputDirectory.canWrite()) {
            GenUtils.logError(null, String.format("Cannot write to %s - need write access to unpack zip files.", inputDirectory.getAbsolutePath()));
        }
        if (!updateObjectList()) {
            GenUtils.error("GIANI output data not found");
            return;
        }
        loadObjectsButton.setEnabled(true);
        objectList.setEnabled(true);
    }//GEN-LAST:event_setDirectoryButtonActionPerformed

    private void loadObjectsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadObjectsButtonActionPerformed
        String selectedObjects = objectList.getSelectedValue();
        props = new GianiDefaultParams();
        try {
            PropertyWriter.loadProperties(props, TITLE, new File(String.format("%s%s%s.xml", inputDirectory.getAbsolutePath(), File.separator, PropertyWriter.FILENAME)));
        } catch (Exception e) {
            GenUtils.logError(e, "Failed to load property file.");
        }
        this.currentSeries = getSeries(selectedObjects);
        try {
            openImage(inputDirectory.getParentFile(), props.getProperty(getOriginalFileName(selectedObjects)), currentSeries);
        } catch (Exception e) {
            GenUtils.logError(e, String.format("Failed to open original image file for %s", selectedObjects));
        }
        popImp = new Objects3DPopulation();
        popImp.loadObjects(String.format("%s%s%s", inputDirectory, File.separator, selectedObjects));
        if (roiManager == null) {
            roiManager = new RoiManager3D_2();
            roiManager.run(null);
        }
        roiManager.addObjects3DPopulation(popImp);
        addListenerToRoiManager3DObjectList(roiManager.getComponents());
        enableRoiManagerLiveMode(roiManager.getComponents());
        openResultsTable(FilenameUtils.getBaseName(selectedObjects));
//        if (Boolean.parseBoolean(props.getProperty(GianiDefaultParams.LOCALISE_SPOTS))) {
//            try {
//                loadSpotData();
//                this.spotData = true;
//            } catch (IOException e) {
//                GenUtils.logError(e, "Failed to load spot data.");
//            }
//        }
    }//GEN-LAST:event_loadObjectsButtonActionPerformed

    void loadSpotData() throws IOException {
        ArrayList<String> fileHeadings = new ArrayList();
        spots = new ArrayList();
        cellIndexSpotMap = new LinkedHashMap();
        double[][] data = DataReader.readCSVFile(new File(String.format("%s%sSpot_Data.csv", inputDirectory.getAbsolutePath(), File.separator)), CSVFormat.EXCEL, fileHeadings, null);
        LinkedHashMap<String, Integer> colIndexMap = new LinkedHashMap();
        for (int h = 0; h < fileHeadings.size(); h++) {
            colIndexMap.put(fileHeadings.get(h), h);
        }
        for (double[] d : data) {
            int cellID = (int) Math.round(d[colIndexMap.get("Cell ID")]);
            Integer spotsIndex = cellIndexSpotMap.get(cellID);
            if (spotsIndex == null) {
                cellIndexSpotMap.put(cellID, spots.size());
                spots.add(new ArrayList());
                spotsIndex = cellIndexSpotMap.get(cellID);
            }
            double distToNuc = d[colIndexMap.get("DISTANCE_TO_NUCLEUS")];
            double radius = d[colIndexMap.get("RADIUS")];
            double quality = d[colIndexMap.get("QUALITY")];
            double nuclear = d[colIndexMap.get("NUCLEAR")];
            double channel = d[colIndexMap.get("CHANNEL")];
            double x = d[colIndexMap.get("POSITION_X")];
            double y = d[colIndexMap.get("POSITION_Y")];
            double z = d[colIndexMap.get("POSITION_Z")];
            Spot s = new Spot(x, y, z, radius, quality);
            s.putFeature("DISTANCE_TO_NUCLEUS", distToNuc);
            s.putFeature("NUCLEAR", nuclear);
            s.putFeature("CHANNEL", channel);
            spots.get(spotsIndex).add(s);
        }
    }

    void addListenerToRoiManager3DObjectList(Component[] components) {
        for (Component c : components) {
            if (c instanceof JScrollPane) {
                roiManagerObjectList = (JList) ((JScrollPane) c).getViewport().getView();
                roiManagerObjectList.addMouseListener(this);
            } else if (c instanceof Container) {
                addListenerToRoiManager3DObjectList(((Container) c).getComponents());
            }
        }
    }

    void enableRoiManagerLiveMode(Component[] components) {
        for (Component c : components) {
            if (c instanceof JButton) {
                String text = ((JButton) c).getText();
                if (text.contains("Live Roi")) {
                    ((JButton) c).doClick();
                }
            } else if (c instanceof Container) {
                enableRoiManagerLiveMode(((Container) c).getComponents());
            }
        }
    }

    boolean updateObjectList() {
        DefaultListModel model = new DefaultListModel();
        String[] files = inputDirectory.list(new SuffixFileFilter(".zip"));
        if (files.length < 1) {
            return false;
        }
        for (int f = 0; f < files.length; f++) {
            model.addElement(files[f]);
        }
        objectList.setModel(model);
        return true;
    }

    void openResultsTable(String objectLabel) {
        String[] files = inputDirectory.list(new SuffixFileFilter(".csv"));
        File inputFile = new File(String.format("%s%s%s", inputDirectory.getAbsolutePath(), File.separator, files[0]));
        tableWrapper = new GianiResultsTable(inputFile, objectLabel);
        resultsTable = tableWrapper.getResultsTable();
        resultsTable.addMouseListener(this);
        tableWrapper.setVisible(true);
    }

    void openImage(File directory, String name, int series) throws IOException, FormatException {
        ArrayList<String> files = BioFormatsFileLister.obtainValidFileList(directory);
        img = new BioFormatsImg();
        for (String f : files) {
            if (f.contains(name)) {
                img.setId(String.format("%s%s%s", directory, File.separator, f));
                img.loadPixelData(series);
                imp = new CompositeImage(img.getLoadedImage(), IJ.COMPOSITE);
                imp.show();
                return;
            }
        }
        IJ.log(String.format("%s not found in %s.", name, directory));
    }

    String getOriginalFileName(String name) {
        return name.substring(0, name.lastIndexOf("-S"));
    }

    int getSeries(String name) {
        return Integer.parseInt(name.substring(name.lastIndexOf("-S") + 2, name.lastIndexOf("_")));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton loadObjectsButton;
    private javax.swing.JList<String> objectList;
    private javax.swing.JButton setDirectoryButton;
    // End of variables declaration//GEN-END:variables
}
