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
package gianiparams;

import Revision.Revision;
import UtilClasses.GenUtils;
import ij.IJ;
import java.io.File;
import java.text.DecimalFormat;
import java.util.Properties;

/**
 *
 * @author David Barry <david.barry at crick dot ac dot uk>
 */
public class GianiDefaultParams extends Properties {

    public static final String INPUT_DIR_LABEL = "Input Directory";
    public static final String OUTPUT_DIR_LABEL = "Output Directory";
    public static final String SERIES_SELECT_LABEL = "Preview Series";
    public static final String INPUT_FILE_LABEL = "Preview File";
    public static final String PREVIEW_CHAN_SELECT_LABEL = "Preview Channel";
    public static final String BLOB_NUC_CHAN_SELECT_LABEL = "Nuclear Channel";
    public static final String BLOB_CHAN_SELECT_LABEL = "Detect Spots in Channel ";
    public static final String NUC_FILT_RAD_LABEL = String.format("Filter Radius for Nuclear Channel (%cm)", IJ.micronSymbol);
    public static final String BLOB_NUC_NOISE_TOL_LABEL = " Threshold for Simple Nuclear Detection";
    public static final String BLOB_NUC_RAD_LABEL = String.format("Nuclear Radius for Simple Detection (%cm)", IJ.micronSymbol);
    public static final String BLOB_CHAN_NOISE_TOL_LABEL = "Simple Blob Detection Threshold for Channel ";
    public static final String BLOB_CHAN_RAD_LABEL = String.format("Simple Detection Blob Radius (%cm) for Channel ", IJ.micronSymbol);
    public static final String NUC_SEG_THRESH_LABEL = "Threshold for Nuclear Segmentation";
    public static final String NUC_SEG_CHAN_SELECT_LABEL = "Channel for Nuclear Segmentation";
    public static final String CELL_FILT_RAD_LABEL = String.format("Filter Radius for Cell Channel (%cm)", IJ.micronSymbol);
    public static final String CELL_SEG_THRESH_LABEL = "Threshold for Cell Segmentation";
    public static final String CELL_SEG_CHAN_SELECT_LABEL = "Channel for Cell Segmentation";
    public static final String CHAN_FOR_MEASURE = "Select Channels to Measure";
    public static final String NUC_VOL_MARKER = "Nuclear Volume Marker";
    public static final String NUC_MEM_MARKER = "Nuclear Membrane Marker";
    public static final String CELL_VOL_MARKER = "Cell Volume Marker";
    public static final String CELL_MEM_MARKER = "Cell Membrane Marker";
    public static final String NUC_DIST_WEIGHTING = "Nuclear Distance Weighting";
    public static final String CELL_DIST_WEIGHTING = "Cell Distance Weighting";
    public static final String FOCI_DIST_WEIGHTING = "Distance Weighting for Channel ";
    public static final String LOCALISE_SPOTS = "Localise Spots";
    public static final String NUC_MAXIMA_DETECT_BLOBS = "Simple Nuclear Blob Detector";
    public static final String NUC_MAXIMA_DETECT_HESSIAN_THRESH = "Threshold for Nuclear Advanced Detection";
    public static final String NUC_MAXIMA_DETECT_HESSIAN = "Advanced Nuclear Blob Detector";
    public static final String NUC_MAXIMA_DETECT_HESSIAN_START_SCALE = String.format("Min Radius for Advanced Nuclear Detection (%cm)", IJ.micronSymbol);
    public static final String NUC_MAXIMA_DETECT_HESSIAN_STOP_SCALE = String.format("Max Radius for Advanced Nuclear Detection (%cm)", IJ.micronSymbol);
    public static final String NUC_MAXIMA_DETECT_HESSIAN_SCALE_STEP = String.format("Scale Step for Advanced Nuclear Detection (%cm)", IJ.micronSymbol);
    public static final String NUC_MAXIMA_DETECT_HESSIAN_ABS = "Absolute Nuclear Hessian Detection";
    public static final String FOCI_MAXIMA_DETECT_BLOBS = "Simple Blob Detector for Channel ";
    public static final String FOCI_MAXIMA_DETECT_FILTER_RAD = String.format("Filter Radius (%cm) for EDM Detector for Channel ", IJ.micronSymbol);
    public static final String FOCI_MAXIMA_DETECT_HESSIAN_MAXIMA = "Advanced Blob Detector for Channel ";
    public static final String FOCI_MAXIMA_DETECT_HESSIAN_THRESH = "Threshold for Advanced Detection for Channel ";
    public static final String FOCI_MAXIMA_DETECT_HESSIAN_MIN_SIZE = "Min Size for Advanced Detection for Channel ";
    public static final String FOCI_MAXIMA_DETECT_HESSIAN_MAX_SIZE = "Max Size for Advanced Detection for Channel ";
    public static final String FOCI_MAXIMA_DETECT_HESSIAN_SCALE_STEP = "Scale Step for Advanced Detection for Channel ";
    public static final String FOCI_MAXIMA_DETECT_HESSIAN_ABS = "Absolute Hessian Detection for Channel ";
    public static final String HELP_ERROR_MESSAGE = "Error: Can't open online help docs.";
    public static final String SPECIFIC_SERIES = "Specific Series";
    public static final String TITLE = String.format("GIANI v%d.%s", Revision.VERSION, new DecimalFormat("000").format(Revision.revisionNumber));

    public GianiDefaultParams() {
        initialise();
    }

    private void initialise() {
        this.setProperty(INPUT_DIR_LABEL, System.getProperty("user.home"));
        this.setProperty(OUTPUT_DIR_LABEL, "");
        this.setProperty(SERIES_SELECT_LABEL, "0");
        this.setProperty(INPUT_FILE_LABEL, "0");
        this.setProperty(PREVIEW_CHAN_SELECT_LABEL, "0");
        this.setProperty(BLOB_NUC_CHAN_SELECT_LABEL, "0");
        this.setProperty(BLOB_CHAN_SELECT_LABEL, "0");
        this.setProperty(NUC_FILT_RAD_LABEL, "0.0");
        this.setProperty(BLOB_NUC_NOISE_TOL_LABEL, "0.0");
        this.setProperty(BLOB_NUC_RAD_LABEL, "0.0");
        this.setProperty(BLOB_CHAN_NOISE_TOL_LABEL, "0.0");
        this.setProperty(BLOB_CHAN_RAD_LABEL, "0.0");
        this.setProperty(NUC_SEG_THRESH_LABEL, "Default");
        this.setProperty(NUC_SEG_CHAN_SELECT_LABEL, "0");
        this.setProperty(CELL_FILT_RAD_LABEL, "0.0");
        this.setProperty(CELL_SEG_THRESH_LABEL, "Default");
        this.setProperty(CELL_SEG_CHAN_SELECT_LABEL, "0");
        this.setProperty(CHAN_FOR_MEASURE, "0");
        this.setProperty(NUC_VOL_MARKER, "true");
        this.setProperty(NUC_MEM_MARKER, "false");
        this.setProperty(CELL_VOL_MARKER, "true");
        this.setProperty(CELL_MEM_MARKER, "false");
        this.setProperty(NUC_DIST_WEIGHTING, "0.5");
        this.setProperty(CELL_DIST_WEIGHTING, "0.5");
        this.setProperty(LOCALISE_SPOTS, "false");
        this.setProperty(NUC_MAXIMA_DETECT_HESSIAN_THRESH, "0.0");
        this.setProperty(NUC_MAXIMA_DETECT_BLOBS, "true");
        this.setProperty(NUC_MAXIMA_DETECT_HESSIAN, "false");
        this.setProperty(NUC_MAXIMA_DETECT_HESSIAN_START_SCALE, "0.0");
        this.setProperty(NUC_MAXIMA_DETECT_HESSIAN_STOP_SCALE, "0.0");
        this.setProperty(NUC_MAXIMA_DETECT_HESSIAN_SCALE_STEP, "0.0");
        this.setProperty(NUC_MAXIMA_DETECT_HESSIAN_ABS, "false");
        this.setProperty(SPECIFIC_SERIES, "-1");
    }

    public static boolean setOutputDirectory(Properties props, String label) {
        File input = new File(props.getProperty(GianiDefaultParams.INPUT_DIR_LABEL));
        String parent;
        if (input.isDirectory()) {
            parent = input.getAbsolutePath();
        } else {
            parent = input.getParent();
        }
        if (label != null) {
            props.setProperty(GianiDefaultParams.OUTPUT_DIR_LABEL, String.format("%s%s%s_%s", parent, File.separator, GianiDefaultParams.TITLE, label));
        } else {
            props.setProperty(GianiDefaultParams.OUTPUT_DIR_LABEL, String.format("%s%s%s", parent, File.separator, GianiDefaultParams.TITLE));
        }
        String outputDirectoryName = GenUtils.openResultsDirectory(props.getProperty(GianiDefaultParams.OUTPUT_DIR_LABEL));
        if (outputDirectoryName == null) {
            return false;
        }
        props.setProperty(GianiDefaultParams.OUTPUT_DIR_LABEL, outputDirectoryName);
        return true;
    }
}
