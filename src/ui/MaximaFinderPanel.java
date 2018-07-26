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

import UIClasses.LayerPanel;
import ij.IJ;

/**
 *
 * @author David Barry <david.barry at crick dot ac dot uk>
 */
public class MaximaFinderPanel extends LayerPanel {

    private Thread previewThread;
    public static final String FILT_RAD_XY_LABEL = String.format("XY Filter Radius (%cm):", IJ.micronSymbol);
    public static final String FILT_RAD_Z_LABEL = String.format("Z Filter Radius (%cm):", IJ.micronSymbol);
    public static final String NOISE_TOL_LABEL = "Noise Tolerance:";

    /**
     * Creates new form MaximaFinderPanel
     */
    public MaximaFinderPanel() {
        super();
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

        xyFiltRadLabel = new javax.swing.JLabel();
        xyFiltRadTextField = new javax.swing.JTextField();
        zFiltRadLabel = new javax.swing.JLabel();
        zFiltRadTextField = new javax.swing.JTextField();
        previewButton = new javax.swing.JButton();
        noiseTolLabel = new javax.swing.JLabel();
        noiseTolTextField = new javax.swing.JTextField();

        setBorder(javax.swing.BorderFactory.createEtchedBorder());
        setLayout(new java.awt.GridBagLayout());

        xyFiltRadLabel.setText(FILT_RAD_XY_LABEL);
        xyFiltRadLabel.setLabelFor(xyFiltRadTextField);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(xyFiltRadLabel, gridBagConstraints);

        xyFiltRadTextField.setText("0.0");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(xyFiltRadTextField, gridBagConstraints);

        zFiltRadLabel.setText(FILT_RAD_Z_LABEL);
        zFiltRadLabel.setLabelFor(zFiltRadTextField);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(zFiltRadLabel, gridBagConstraints);

        zFiltRadTextField.setText("0.0");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(zFiltRadTextField, gridBagConstraints);

        previewButton.setText("Preview");
        previewButton.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                previewButtonFocusLost(evt);
            }
        });
        previewButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                previewButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        add(previewButton, gridBagConstraints);

        noiseTolLabel.setText(NOISE_TOL_LABEL);
        noiseTolLabel.setLabelFor(noiseTolTextField);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(noiseTolLabel, gridBagConstraints);

        noiseTolTextField.setText("0.0");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(noiseTolTextField, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void previewButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_previewButtonActionPerformed
        setVariables();
        previewThread = new Thread() {
//            private final BioFormatsImg bioImage = img.copy();

            public void run() {
//                long[] dims = new long[bioImage.getInterval().numDimensions()];
//                bioImage.getInterval().dimensions(dims);
//                Img< BitType> maxima = MaximumFinder.findAndDisplayLocalMaxima(bioImage.getInterval(), dims,
//                        new FloatType(Float.parseFloat(props.getProperty(NOISE_TOL_LABEL))),
//                        new int[]{(int) Math.round(Double.parseDouble(props.getProperty(FILT_RAD_XY_LABEL)) / bioImage.getXySpatRes()),
//                            (int) Math.round(Double.parseDouble(props.getProperty(FILT_RAD_XY_LABEL)) / bioImage.getXySpatRes()),
//                            (int) Math.round(Double.parseDouble(props.getProperty(FILT_RAD_Z_LABEL)) / bioImage.getXySpatRes())}, true);
//                
//               Img<FloatType> distanceMap = bioImage.getImg().factory().create(bioImage.getInterval());
//                DistanceTransform.transform(maxima, distanceMap, DistanceTransform.DISTANCE_TYPE.EUCLIDIAN);
//
//                IJ.saveAs(ImageJFunctions.show(distanceMap, "Distance Transform"), "TIF", "C:/Users/barryd/Desktop/test.tif");
//                ImageJFunctions.show(maxima, "Detected Maxima");

//                Img<BitType> invertedBinaryImage = ImageInverter.invertBinaryImage(maxima);
//                DistanceTransform.transform(invertedBinaryImage, DistanceTransform.DISTANCE_TYPE.EUCLIDIAN);
//                ImageJFunctions.show(invertedBinaryImage, "Inverted Image");
//                IJ.saveAs(ImageJFunctions.show(invertedBinaryImage, "Distance Transform"), "TIF", "C:/Users/barryd/Desktop/test.tif");
//                Img< BitType> distanceMaxima = MaximumFinder.findAndDisplayLocalMaxima(maxima, dims,
//                        new FloatType(0.0f), new int[]{1, 1, 1}, false);
//                ImageJFunctions.show(distanceMaxima, "Distance Maxima");
            }
        };
        previewThread.start();
    }//GEN-LAST:event_previewButtonActionPerformed

    private void previewButtonFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_previewButtonFocusLost
        if (previewThread != null) {
            previewThread.interrupt();
        }
    }//GEN-LAST:event_previewButtonFocusLost


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel noiseTolLabel;
    private javax.swing.JTextField noiseTolTextField;
    private javax.swing.JButton previewButton;
    private javax.swing.JLabel xyFiltRadLabel;
    private javax.swing.JTextField xyFiltRadTextField;
    private javax.swing.JLabel zFiltRadLabel;
    private javax.swing.JTextField zFiltRadTextField;
    // End of variables declaration//GEN-END:variables
}