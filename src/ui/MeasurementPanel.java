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

import IO.BioFormats.BioFormatsImg;
import Process.ROI.MultiThreadedROIConstructor;
import UIClasses.LayerPanel;
import UIClasses.Updateable;
import ij.plugin.filter.Analyzer;
import java.util.Properties;
import javax.swing.DefaultListModel;
import javax.swing.ListSelectionModel;
import gianiparams.GianiDefaultParams;
import java.net.URI;

/**
 *
 * @author David Barry <david.barry at crick dot ac dot uk>
 */
public class MeasurementPanel extends LayerPanel implements Updateable {

    /**
     * Creates new form MeasurementPanel
     */
    public MeasurementPanel() {
        this(null, null, null, null, null);
    }

    public MeasurementPanel(Properties props, BioFormatsImg img, MultiThreadedROIConstructor process, String[] propLabels, URI helpURI) {
        super(props, img, process, propLabels, helpURI);
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

        measurePreviewButton = new javax.swing.JButton();
        channelScrollPane = new javax.swing.JScrollPane();
        channelList = new javax.swing.JList<>();
        channelSelectLabel = new javax.swing.JLabel();
        localiseSpotsToggleButton = new javax.swing.JToggleButton();
        helpButton = new javax.swing.JButton();

        setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        setLayout(new java.awt.GridBagLayout());

        measurePreviewButton.setText("Measure Preview");
        measurePreviewButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                measurePreviewButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        add(measurePreviewButton, gridBagConstraints);

        channelList.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        channelList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        channelList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                channelListMouseClicked(evt);
            }
        });
        channelScrollPane.setViewportView(channelList);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        add(channelScrollPane, gridBagConstraints);

        channelSelectLabel.setText(propLabels[0]);
        channelSelectLabel.setLabelFor(channelScrollPane);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(channelSelectLabel, gridBagConstraints);

        localiseSpotsToggleButton.setText(propLabels[1]);
        localiseSpotsToggleButton.setSelected(Boolean.parseBoolean(props.getProperty(propLabels[1])));
        localiseSpotsToggleButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                localiseSpotsToggleButtonMouseClicked(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        add(localiseSpotsToggleButton, gridBagConstraints);

        helpButton.setText("Help");
        helpButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                helpButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        add(helpButton, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void measurePreviewButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_measurePreviewButtonActionPerformed
        restartProcess();
        setVariables();
        process.start();
        try {
            process.join();
        } catch (InterruptedException e) {
        }
        Analyzer.getResultsTable().show("Measurements");
    }//GEN-LAST:event_measurePreviewButtonActionPerformed

    private void channelListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_channelListMouseClicked
        this.dispatchEvent(evt);
    }//GEN-LAST:event_channelListMouseClicked

    private void localiseSpotsToggleButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_localiseSpotsToggleButtonMouseClicked
        channelListMouseClicked(evt);
    }//GEN-LAST:event_localiseSpotsToggleButtonMouseClicked

    private void helpButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_helpButtonActionPerformed
        openHelpPage(GianiDefaultParams.HELP_ERROR_MESSAGE);
    }//GEN-LAST:event_helpButtonActionPerformed

    public void setupProcess() {
        process.setup(img, props, new String[]{GianiDefaultParams.SERIES_SELECT_LABEL, propLabels[0], GianiDefaultParams.OUTPUT_DIR_LABEL});
    }

    public void update() {
        if (img.getId() == null) {
            return;
        }
        int channels = img.getSizeC();
        DefaultListModel model = new DefaultListModel();
        for (int c = 0; c < channels; c++) {
            model.addElement(String.format("Channel %d", c));
        }
        channelList.setModel(model);
        String selectedChannels = props.getProperty(propLabels[0]);
        if (selectedChannels == null) {
            return;
        }
        int selection = Integer.parseInt(selectedChannels);
        int n = model.getSize();
        for (int i = 0; i < n; i++) {
            if ((selection & (int) Math.pow(2.0, i)) != 0) {
                channelList.addSelectionInterval(i, i);
            }
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JList<String> channelList;
    private javax.swing.JScrollPane channelScrollPane;
    private javax.swing.JLabel channelSelectLabel;
    private javax.swing.JButton helpButton;
    private javax.swing.JToggleButton localiseSpotsToggleButton;
    private javax.swing.JButton measurePreviewButton;
    // End of variables declaration//GEN-END:variables
}
