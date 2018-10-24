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
package params;

import ij.IJ;
import java.util.Properties;

/**
 *
 * @author David Barry <david.barry at crick dot ac dot uk>
 */
public class DefaultParams extends Properties {

    public static final String INPUT_DIR_LABEL = "Input Directory:";
    public static final String SERIES_SELECT_LABEL = "Series for Preview";
    public static final String INPUT_FILE_LABEL = "File for Preview";
    public static final String CHANNEL_SELECT_LABEL = "Channel for Preview";
    public static final String FILT_RAD_XY_LABEL = String.format("XY Gaussian Radius (%cm):", IJ.micronSymbol);
    public static final String FILT_RAD_Z_LABEL = String.format("Z Gaussian Radius (%cm):", IJ.micronSymbol);
    public static final String MAX_RAD_Z_LABEL = String.format("Z Local Max Radius (%cm):", IJ.micronSymbol);
    public static final String MAX_NOISE_TOL_LABEL = "Noise Tolerance:";
    public static final String MAX_RAD_XY_LABEL = String.format("XY Local Max Radius (%cm):", IJ.micronSymbol);
    public static final String SEG_THRESH_LABEL = "Threshold";
    public static final String SEG_CHAN_SELECT_LABEL = "Channel for Segmentation";

    public DefaultParams() {
        this.setProperty(INPUT_DIR_LABEL, System.getProperty("user.home"));
        this.setProperty(SERIES_SELECT_LABEL, "0");
        this.setProperty(INPUT_FILE_LABEL, "0");
        this.setProperty(CHANNEL_SELECT_LABEL, "0");
        this.setProperty(FILT_RAD_XY_LABEL, "0.0");
        this.setProperty(FILT_RAD_Z_LABEL, "0.0");
        this.setProperty(MAX_RAD_Z_LABEL, "0.0");
        this.setProperty(MAX_NOISE_TOL_LABEL, "0.0");
        this.setProperty(MAX_RAD_XY_LABEL, "0.0");
        this.setProperty(SEG_THRESH_LABEL, "0");
        this.setProperty(SEG_CHAN_SELECT_LABEL, "0");
    }

}
