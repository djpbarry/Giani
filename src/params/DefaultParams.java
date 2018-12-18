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

    public static final String INPUT_DIR_LABEL = "Input Directory";
    public static final String SERIES_SELECT_LABEL = "Preview Series";
    public static final String INPUT_FILE_LABEL = "Preview File";
    public static final String PREVIEW_CHAN_SELECT_LABEL = "Preview Channel";
    public static final String BLOB_FIND_CHAN_SELECT_LABEL = "Nuclear Channel";
    public static final String NUC_FILT_RAD_XY_LABEL = String.format("XY Filter Radius for Nuclear Channel (%cm)", IJ.micronSymbol);
    public static final String NUC_FILT_RAD_Z_LABEL = String.format("Z Filter Radius for Nuclear Channel (%cm)", IJ.micronSymbol);
    public static final String BLOB_NOISE_TOL_LABEL = "Nuclear Blob Detection Threshold";
    public static final String BLOB_RAD_LABEL = String.format("Nuclear 'Blob' Radius (%cm)", IJ.micronSymbol);
    public static final String NUC_SEG_THRESH_LABEL = "Threshold for Nuclear Segmentation";
    public static final String NUC_SEG_CHAN_SELECT_LABEL = "Channel for Nuclear Segmentation";
    public static final String CELL_FILT_RAD_XY_LABEL = String.format("XY Filter Radius for Cell Channel (%cm)", IJ.micronSymbol);
    public static final String CELL_FILT_RAD_Z_LABEL = String.format("Z Filter Radius for Cell Channel (%cm)", IJ.micronSymbol);
    public static final String CELL_SEG_THRESH_LABEL = "Threshold for Cell Segmentation";
    public static final String CELL_SEG_CHAN_SELECT_LABEL = "Channel for Cell Segmentation";
    public static final String CHAN_FOR_MEASURE = "Select Channels to Measure";
    public static final String VOL_MARKER = "Volume Marker";
    public static final String MEM_MARKER = "Membrane Marker";

    public DefaultParams() {
        this.setProperty(INPUT_DIR_LABEL, System.getProperty("user.home"));
        this.setProperty(SERIES_SELECT_LABEL, "0");
        this.setProperty(INPUT_FILE_LABEL, "0");
        this.setProperty(PREVIEW_CHAN_SELECT_LABEL, "0");
        this.setProperty(BLOB_FIND_CHAN_SELECT_LABEL, "0");
        this.setProperty(NUC_FILT_RAD_XY_LABEL, "0.0");
        this.setProperty(NUC_FILT_RAD_Z_LABEL, "0.0");
        this.setProperty(BLOB_NOISE_TOL_LABEL, "0.0");
        this.setProperty(BLOB_RAD_LABEL, "0.0");
        this.setProperty(NUC_SEG_THRESH_LABEL, "0");
        this.setProperty(NUC_SEG_CHAN_SELECT_LABEL, "0");
        this.setProperty(CELL_FILT_RAD_XY_LABEL, "0.0");
        this.setProperty(CELL_FILT_RAD_Z_LABEL, "0.0");
        this.setProperty(CELL_SEG_THRESH_LABEL, "0");
        this.setProperty(CELL_SEG_CHAN_SELECT_LABEL, "0");
        this.setProperty(CHAN_FOR_MEASURE, "0");
        this.setProperty(VOL_MARKER, "true");
        this.setProperty(MEM_MARKER, "false");
    }
    
}
