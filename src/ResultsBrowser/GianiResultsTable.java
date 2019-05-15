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

import IO.DataReader;
import UtilClasses.GenUtils;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import org.apache.commons.csv.CSVFormat;

/**
 *
 * @author David Barry <david.barry at crick dot ac dot uk>
 */
public class GianiResultsTable extends javax.swing.JFrame {

    private final File inputFile;
    private TableModel resultsTableModel;

    /**
     * Creates new form TextResultsFrame
     */
    public GianiResultsTable(File inputFile) {
        this.inputFile = inputFile;
        try {
            loadResults();
        } catch (IOException e) {
            GenUtils.logError(e, "Failed to load results file.");
        }
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

        jScrollPane1 = new javax.swing.JScrollPane();
        resultsTable = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new java.awt.GridBagLayout());

        resultsTable.setModel(resultsTableModel);
        jScrollPane1.setViewportView(resultsTable);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        getContentPane().add(jScrollPane1, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    public void loadResults() throws IOException {
        ArrayList<String> fileHeadings = new ArrayList();
        ArrayList<String> labels = new ArrayList();
        double[][] data = DataReader.readCSVFile(inputFile, CSVFormat.EXCEL, fileHeadings, labels);
        String[] tableHeadings = new String[fileHeadings.size() + 1];
        tableHeadings[0] = "Image";
        for (int i = 0; i < fileHeadings.size(); i++) {
            tableHeadings[i + 1] = fileHeadings.get(i);
        }
        resultsTableModel = new DefaultTableModel(tableHeadings, labels.size());
        for (int j = 0; j < data.length; j++) {
            resultsTableModel.setValueAt(labels.get(j), j, 0);
            for (int i = 0; i < data[j].length; i++) {
                resultsTableModel.setValueAt(data[j][i], j, i + 1);
            }
        }
    }

    public JTable getResultsTable() {
        return resultsTable;
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable resultsTable;
    // End of variables declaration//GEN-END:variables
}
