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
import java.util.Properties;

/**
 *
 * @author David Barry <david.barry at crick dot ac dot uk>
 */
public class MeasurementPanel extends LayerPanel {

    /**
     * Creates new form MeasurementPanel
     */
    public MeasurementPanel() {
        this(null, null);
    }

    public MeasurementPanel(Properties props, BioFormatsImg img) {
        super(props, img);
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

        setLayout(new java.awt.GridBagLayout());

        measurePreviewButton.setText("Measure Preview");
        measurePreviewButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                measurePreviewButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        add(measurePreviewButton, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void measurePreviewButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_measurePreviewButtonActionPerformed
        process = new MultiThreadedROIConstructor();
        process.setup(img, props, null);
        process.start();
        try {
            process.join();
        } catch (InterruptedException e) {
        }
    }//GEN-LAST:event_measurePreviewButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton measurePreviewButton;
    // End of variables declaration//GEN-END:variables
}
