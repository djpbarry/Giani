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
package graphics;

import Extrema.MultiThreadedMaximaFinder;
import IO.BioFormats.BioFormatsImg;
import fiji.plugin.trackmate.Spot;
import gianiparams.GianiDefaultParams;
import ij.CompositeImage;
import ij.IJ;
import ij.ImagePlus;
import ij.gui.OvalRoi;
import ij.gui.Overlay;
import ij.gui.Roi;
import java.awt.Color;
import java.util.List;
import java.util.ArrayList;
import java.util.Properties;
import ome.units.quantity.Length;

/**
 *
 * @author David Barry <david.barry at crick dot ac dot uk>
 */
public class OverlayDrawer {

    public static void showOutput(ArrayList<int[]> maxima, String title, Roi[] binaryOutline, BioFormatsImg img, Properties props, String[] propLabels, boolean edm, double maxXYRadiusMic, double maxZRadiusMic, ImagePlus imp) {
        Overlay o = new Overlay();
        Length zLength = img.getZSpatialRes(Integer.parseInt(props.getProperty(GianiDefaultParams.SERIES_SELECT_LABEL)));
        double zSpatRes = 1.0;
        if (zLength != null) {
            zSpatRes = zLength.value().doubleValue();
        }
        Length xyLength = img.getXYSpatialRes(Integer.parseInt(props.getProperty(GianiDefaultParams.SERIES_SELECT_LABEL)));
        double xySpatRes = 1.0;
        if (xyLength != null) {
            xySpatRes = xyLength.value().doubleValue();
        }
        if (edm) {
            maxXYRadiusMic = Double.parseDouble(props.getProperty(propLabels[MultiThreadedMaximaFinder.EDM_MIN_SIZE]));
            maxZRadiusMic = Double.parseDouble(props.getProperty(propLabels[MultiThreadedMaximaFinder.EDM_MIN_SIZE]));
        }
        double maxZRadiusMic2 = Math.pow(maxZRadiusMic, 2.0);
        double maxXYRadiusMic2 = Math.pow(maxXYRadiusMic, 2.0);
        int zRadiusPix = (int) Math.ceil(maxZRadiusMic / zSpatRes);
        for (int[] pix : maxima) {
            int z0 = pix[2] + 1;
            for (int z = z0 - zRadiusPix < 1 ? 1 : z0 - zRadiusPix; z <= z0 + zRadiusPix && z <= imp.getNSlices(); z++) {
                double z2 = Math.pow((z - z0) * zSpatRes, 2.0);
                double cr = Math.sqrt((1.0 - z2 / maxZRadiusMic2) * maxXYRadiusMic2);
                int currentRadius = (int) Math.round(cr / xySpatRes);
                if (currentRadius < 1) {
                    currentRadius = 1;
                }
                OvalRoi roi = new OvalRoi(pix[0] - currentRadius, pix[1] - currentRadius, 2 * currentRadius + 1, 2 * currentRadius + 1);
                roi.setPosition(z);
                
                o.add(roi);
            }
        }
        if (edm) {
            for (int i = 0; i < imp.getNSlices(); i++) {
                if (binaryOutline[i] == null) {
                    continue;
                }
                binaryOutline[i].setStrokeColor(Color.red);
                binaryOutline[i].setPosition(i + 1);
                o.add(binaryOutline[i]);
            }
        }
       
        imp.setTitle(title);
        imp.show();
        imp.setOverlay(o);
    }

    public static ArrayList<int[]> convertSpotsToMaximas(List<Spot> maximas, double[] calibration) {
        ArrayList<int[]> maxima = new ArrayList();
        for (Spot s : maximas) {
            int[] pos = new int[3];
            for (int d = 0; d < 3; d++) {
                pos[d] = (int) Math.round(s.getFloatPosition(d) / calibration[d]);
            }
            maxima.add(pos);
        }
        return maxima;
    }

}
