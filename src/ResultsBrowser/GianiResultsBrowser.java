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
package ResultsBrowser;

import IO.BioFormats.BioFormatsFileLister;
import IO.BioFormats.BioFormatsImg;
import Revision.Revision;
import UtilClasses.GenUtils;
import UtilClasses.Utilities;
import ij.IJ;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import loci.formats.FormatException;
import mcib3d.geom.Objects3DPopulation;
import mcib_plugins.tools.RoiManager3D_2;
import org.apache.commons.io.filefilter.SuffixFileFilter;

/**
 *
 * @author David Barry <david.barry at crick dot ac dot uk>
 */
public class GianiResultsBrowser extends javax.swing.JFrame implements MouseListener {

    private RoiManager3D_2 roiManager;
    private JList roiManagerObjectList;
    private GianiResultsTable tableWrapper;
    private JTable resultsTable;
    private final Objects3DPopulation popImp;
    private File inputDirectory;
    public static final String TITLE = String.format("GIANI Results Browser v%d.%s", Revision.VERSION, new DecimalFormat("000").format(Revision.revisionNumber));

    /**
     * Creates new form GianiResultsBrowser
     */
    public GianiResultsBrowser() {
        popImp = new Objects3DPopulation();
        initComponents();
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
        if (e.getSource() instanceof JList) {
            String label = (String) roiManagerObjectList.getSelectedValue();
            for (int r = 0; r < resultsTable.getRowCount(); r++) {
                if (label.contentEquals((String) resultsTable.getValueAt(r, 0))) {
                    resultsTable.setRowSelectionInterval(r, r);
                }
            }
        } else if (e.getSource() instanceof JTable) {
            int row = resultsTable.getSelectedRow();
            String label = (String) resultsTable.getValueAt(row, 0);
            roiManagerObjectList.setSelectedValue(label, true);
        }
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
        loadObjectsButton.setEnabled(true);
        objectList.setEnabled(true);
        updateObjectList();
        openResultsTable();
    }//GEN-LAST:event_setDirectoryButtonActionPerformed

    private void loadObjectsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadObjectsButtonActionPerformed
        String selectedObjects = objectList.getSelectedValue();
        try {
            openImage(inputDirectory.getParentFile(), getOriginalFileName(selectedObjects), getSeries(selectedObjects));
        } catch (Exception e) {
            GenUtils.logError(e, String.format("Failed to open original image file for %s", selectedObjects));
        }
        popImp.loadObjects(String.format("%s%s%s", inputDirectory, File.separator, selectedObjects));
        if (roiManager == null) {
            roiManager = new RoiManager3D_2();
        }
        roiManager.addObjects3DPopulation(popImp);
        addListenerToRoiManager3DObjectList(roiManager.getComponents());
    }//GEN-LAST:event_loadObjectsButtonActionPerformed

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

    void updateObjectList() {
        DefaultListModel model = new DefaultListModel();
        String[] files = inputDirectory.list(new SuffixFileFilter(".zip"));
        for (int f = 0; f < files.length; f++) {
            model.addElement(files[f]);
        }
        objectList.setModel(model);
    }

    void openResultsTable() {
        String[] files = inputDirectory.list(new SuffixFileFilter(".csv"));
        File inputFile = new File(String.format("%s%s%s", inputDirectory.getAbsolutePath(), File.separator, files[0]));
        tableWrapper = new GianiResultsTable(inputFile);
        resultsTable = tableWrapper.getResultsTable();
        resultsTable.addMouseListener(this);
        tableWrapper.setVisible(true);
    }

    void openImage(File directory, String name, int series) throws IOException, FormatException {
        ArrayList<String> files = BioFormatsFileLister.obtainValidFileList(directory);
        BioFormatsImg img = new BioFormatsImg();
        for (String f : files) {
            if (f.contains(name)) {
                img.setId(String.format("%s%s%s", directory, File.separator, f));
                img.loadPixelData(series);
                img.getLoadedImage().show();
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
