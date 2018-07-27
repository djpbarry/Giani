/*
 * Copyright (C) 2018 David Barry <david.barry at crick dot ac dot uk>
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
package ui;

import IO.BioFormats.BioFormatsFileLister;
import IO.BioFormats.BioFormatsFileReader;
import IO.BioFormats.BioFormatsImg;
import UtilClasses.GenUtils;
import UtilClasses.Utilities;
import java.io.File;
import java.util.ArrayList;
import java.util.Properties;
import javax.swing.DefaultComboBoxModel;
import UIClasses.LayerPanel;
import javax.swing.JTextArea;

/**
 *
 * @author David Barry <david.barry at crick dot ac dot uk>
 */
public class SelectInputPanel extends LayerPanel {

    private static File inputDirectory;
    private Properties props;
    public static final String INPUT_DIR_LABEL = "Input Directory:";
    public static final String INPUT_FILE_LABEL = "File for Preview";
    public static final String SERIES_SELECT_LABEL = "Series for Preview";
    public static final String CHANNEL_SELECT_LABEL = "Channel for Preview";
    private final JTextArea textArea;
//    private BioFormatsImg img;

    /**
     * Creates new form SelectInputPanel
     */
    public SelectInputPanel() {
        this(null);
    }

    public SelectInputPanel(JTextArea textArea) {
        super();
        this.textArea = textArea;
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

        inputDirTextField = new javax.swing.JTextField();
        chooseInputDirButton = new javax.swing.JButton();
        inputDirLabel = new javax.swing.JLabel();
        fileNameComboLabel = new javax.swing.JLabel();
        fileNameComboBox = new javax.swing.JComboBox<>();
        seriesSelectLabel = new javax.swing.JLabel();
        channelSelectLabel = new javax.swing.JLabel();
        seriesComboBox = new javax.swing.JComboBox<>();
        channelComboBox = new javax.swing.JComboBox<>();
        previewButton = new javax.swing.JButton();

        setBorder(javax.swing.BorderFactory.createEtchedBorder());
        setLayout(new java.awt.GridBagLayout());

        inputDirTextField.setText("Select input file...");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        add(inputDirTextField, gridBagConstraints);

        chooseInputDirButton.setText("Select Input");
        chooseInputDirButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chooseInputDirButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        add(chooseInputDirButton, gridBagConstraints);

        inputDirLabel.setText(INPUT_DIR_LABEL);
        inputDirLabel.setLabelFor(inputDirTextField);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        add(inputDirLabel, gridBagConstraints);

        fileNameComboLabel.setText(INPUT_FILE_LABEL);
        fileNameComboLabel.setEnabled(false);
        fileNameComboLabel.setLabelFor(fileNameComboBox);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        add(fileNameComboLabel, gridBagConstraints);

        fileNameComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        fileNameComboBox.setEnabled(false);
        fileNameComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fileNameComboBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        add(fileNameComboBox, gridBagConstraints);

        seriesSelectLabel.setText(SERIES_SELECT_LABEL);
        seriesSelectLabel.setEnabled(false);
        seriesSelectLabel.setLabelFor(seriesComboBox);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        add(seriesSelectLabel, gridBagConstraints);

        channelSelectLabel.setText(CHANNEL_SELECT_LABEL);
        channelSelectLabel.setEnabled(false);
        channelSelectLabel.setLabelFor(channelComboBox);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        add(channelSelectLabel, gridBagConstraints);

        seriesComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        seriesComboBox.setEnabled(false);
        seriesComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                seriesComboBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        add(seriesComboBox, gridBagConstraints);

        channelComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        channelComboBox.setEnabled(false);
        channelComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                channelComboBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        add(channelComboBox, gridBagConstraints);

        previewButton.setText("Preview");
        previewButton.setEnabled(false);
        previewButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                previewButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        add(previewButton, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void chooseInputDirButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chooseInputDirButtonActionPerformed
        boolean valid = false;
        try {
            while (!valid) {
                inputDirectory = Utilities.getFolder(inputDirectory, "Select input directory...", true);
                if (!inputDirectory.exists()) {
                    GenUtils.error(String.format("%s is not a valid input directory", inputDirectory.getPath()));
                    valid = false;
                } else {
                    valid = true;
                    inputDirTextField.setText(inputDirectory.getAbsolutePath());
                }
            }
        } catch (Exception e) {
            GenUtils.error("There was a problem with directory selection.");
        }
        enableFileDropDown(listFiles(textArea));
    }//GEN-LAST:event_chooseInputDirButtonActionPerformed

    private void previewButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_previewButtonActionPerformed
        setVariables();
        int series = seriesComboBox.getSelectedIndex();
        int channel = channelComboBox.getSelectedIndex();
        try {
            img.setImg(series, channel);
            img.getImg().show();
        } catch (Exception e) {
            GenUtils.error("An error occured while trying to display the image.");
            GenUtils.logError(e);
        }
    }//GEN-LAST:event_previewButtonActionPerformed

    private void fileNameComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fileNameComboBoxActionPerformed
        String fileName = (String) fileNameComboBox.getSelectedItem();
        try {
            String fullFileName = String.format("%s%s%s", inputDirectory, File.separator, fileName);
            img = new BioFormatsImg(fullFileName);
            int series = img.getSeriesCount();
            ArrayList<String> seriesLabels = new ArrayList();
            for (int s = 0; s < series; s++) {
                seriesLabels.add(String.valueOf(s));
            }
            seriesComboBox.setModel(new DefaultComboBoxModel(seriesLabels.toArray()));
            seriesComboBox.setEnabled(true);
            seriesSelectLabel.setEnabled(true);
        } catch (Exception e) {
            GenUtils.error(String.format("Problem reading %s", fileName));
        }
    }//GEN-LAST:event_fileNameComboBoxActionPerformed

    private void seriesComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_seriesComboBoxActionPerformed
        String fileName = (String) fileNameComboBox.getSelectedItem();
        try {
            int channels = img.getChannelCount();
            ArrayList<String> channelLabels = new ArrayList();
            for (int c = 0; c < channels; c++) {
                channelLabels.add(String.valueOf(c));
            }
            channelComboBox.setModel(new DefaultComboBoxModel(channelLabels.toArray()));
            channelComboBox.setEnabled(true);
            channelSelectLabel.setEnabled(true);
        } catch (Exception e) {
            GenUtils.error(String.format("Problem reading %s", fileName));
        }
    }//GEN-LAST:event_seriesComboBoxActionPerformed

    private void channelComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_channelComboBoxActionPerformed
        previewButton.setEnabled(true);
    }//GEN-LAST:event_channelComboBoxActionPerformed

    public boolean setVariables() {
        try {
            setProperties(props, this);
            String fileName = (String) fileNameComboBox.getSelectedItem();
            String fullFileName = String.format("%s%s%s", inputDirectory, File.separator, fileName);
            img = new BioFormatsImg(fullFileName);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public Properties getProps() {
        return props;
    }

    ArrayList<String> listFiles(JTextArea textArea) {
        ArrayList<String> fileNames = BioFormatsFileLister.obtainValidFileList(inputDirectory);
        if (textArea != null) {
            textArea.setText("");
            textArea.append(String.format("%s\n", inputDirectory.getAbsolutePath()));
            if (fileNames.size() > 0) {
                textArea.append(String.format("%d valid files found.\n", fileNames.size()));
                for (String file : fileNames) {
                    textArea.append(String.format("%s\n", file));
                }
            } else {
                textArea.append("No valid files found.\n");
            }
        }
        return fileNames;
    }

    void enableFileDropDown(ArrayList<String> fileNames) {
        if (!(fileNames.size() > 0)) {
            return;
        }
        fileNameComboBox.setModel(new DefaultComboBoxModel(fileNames.toArray()));
        fileNameComboBox.setEnabled(true);
        fileNameComboLabel.setEnabled(true);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> channelComboBox;
    private javax.swing.JLabel channelSelectLabel;
    private javax.swing.JButton chooseInputDirButton;
    private javax.swing.JComboBox<String> fileNameComboBox;
    private javax.swing.JLabel fileNameComboLabel;
    private javax.swing.JLabel inputDirLabel;
    private javax.swing.JTextField inputDirTextField;
    private javax.swing.JButton previewButton;
    private javax.swing.JComboBox<String> seriesComboBox;
    private javax.swing.JLabel seriesSelectLabel;
    // End of variables declaration//GEN-END:variables
}
