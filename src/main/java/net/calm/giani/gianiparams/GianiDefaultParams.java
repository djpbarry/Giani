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
package net.calm.giani.gianiparams;

import ij.IJ;
import net.calm.giani.ui.GIANIUI;
import net.calm.iaclasslibrary.UtilClasses.GenUtils;
import org.apache.commons.io.FilenameUtils;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Properties;
import java.util.Set;

/**
 * Stores all parameter names and values needed to run an instance of GIANI
 *
 * @author Dave Barry
 * @since 1.0.0
 */
public class GianiDefaultParams extends Properties {

    public static final String PREVIEW = "Preview";
    public static final String PREVIEW_MEASURE = "Preview Measures";
    public static final String HELP = "Help";
    public static final String INPUT_DIR_LABEL = "Input Directory";
    public static final String OUTPUT_DIR_LABEL = "Output Directory";
    public static final String SERIES_SELECT_LABEL = "Preview Series";
    public static final String INPUT_FILE_LABEL = "Preview File";
    public static final String PREVIEW_CHAN_SELECT_LABEL = "Preview Channel";
    public static final String BLOB_NUC_CHAN_SELECT_LABEL = "Nuclear Channel";
    public static final String BLOB_CHAN_SELECT_LABEL = "Detect Spots in Channel ";
    public static final String NUC_FILT_RAD_LABEL = "Filter Radius for Nuclear Channel";
    public static final String NUC_TOP_HAT_FILT_RAD_LABEL = "Top Hat Filter Radius for Nuclear Channel";
    public static final String NUC_TOP_HAT_DOWNSIZE_FACTOR_LABEL = "Downsizing Factor for Top Hat Filter for Nuclear Channel";
    public static final String BLOB_NUC_NOISE_TOL_LABEL = "Quality of Simple Nuclear Centroid Detections";
    public static final String BLOB_NUC_RAD_LABEL = "Nuclear Radius for Simple Centroid Detection";
    public static final String BLOB_CHAN_NOISE_TOL_LABEL = "Quality of Simple Spot Detections in Channel ";
    public static final String BLOB_CHAN_RAD_LABEL = "Radius for Simple Spot Detection in Channel ";
    public static final String NUC_SEG_THRESH_LABEL = "Threshold Method for Nuclear Segmentation";
    public static final String NUC_SEG_CHAN_SELECT_LABEL = "Channel for Nuclear Segmentation";
    public static final String CELL_FILT_RAD_LABEL = "Filter Radius for Cell Channel";
    public static final String CELL_SEG_THRESH_LABEL = "Threshold Method for Cell Segmentation";
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
    public static final String NUC_MAXIMA_DETECT_BLOBS = "Simple Nuclear Centroid Detector";
    public static final String NUC_MAXIMA_DETECT_HESSIAN_THRESH = "Quality of Advanced Nuclear Centroid Detections";
    public static final String NUC_MAXIMA_DETECT_HESSIAN = "Advanced Nuclear Centroid Detector";
    public static final String NUC_MAXIMA_DETECT_HESSIAN_START_SCALE = "Minimum Nuclear Radius for Advanced Centroid Detection";
    //public static final String NUC_MAXIMA_DETECT_HESSIAN_STOP_SCALE = "Maximum Nuclear Radius for Advanced Centroid Detection";
    //public static final String NUC_MAXIMA_DETECT_HESSIAN_SCALE_STEP = "Radius Step Size for Advanced Nuclear Centroid Detection";
    public static final String NUC_MAXIMA_DETECT_HESSIAN_ABS = "Absolute Nuclear Hessian Detection";
    public static final String FOCI_MAXIMA_DETECT_BLOBS = "Simple Spot Detector for Channel ";
    //public static final String FOCI_MAXIMA_DETECT_FILTER_RAD = String.format("Filter Radius (%cm) for EDM Detector for Channel ", IJ.micronSymbol);
    public static final String FOCI_MAXIMA_DETECT_HESSIAN_MAXIMA = "Advanced Spot Detector for Channel ";
    public static final String FOCI_MAXIMA_DETECT_HESSIAN_THRESH = "Quality of Advanced Spot Detections in Channel ";
    public static final String FOCI_MAXIMA_DETECT_HESSIAN_MIN_SIZE = "Minimum Spot Radius for Advanced Detection in Channel ";
    //public static final String FOCI_MAXIMA_DETECT_HESSIAN_MAX_SIZE = "Maximum Spot Radius for Advanced Detection in Channel ";
    //public static final String FOCI_MAXIMA_DETECT_HESSIAN_SCALE_STEP = "Radius Step Size for Advanced Spot Detection in Channel ";
    public static final String FOCI_MAXIMA_DETECT_HESSIAN_ABS = "Absolute Hessian Detection for Channel ";
    public static final String HELP_ERROR_MESSAGE = "Error: Can't open online help docs.";
    public static final String SPECIFIC_SERIES = "Specific Series";
    public static final String APP_TITLE = "GIANI";
    public static final String TITLE = getTitleWithVersion();
    public static final String NUC_GAUSS_FILTER_TITLE = "Gaussian Filtering to Suppress Noise in Nuclear Channel Prior to Segmentation";
    public static final String NUC_CENTROID_LOCALISATION_TITLE = "Blob Detection to Approximate Locations of Nuclear Centroids";
    public static final String NUC_TOP_HAT_TITLE = "Top Hat Filtering to Homogenise Background in Nuclear Channel Prior to Segmentation";
    public static final String NUC_SEG_TITLE = "Segmentation of Nuclei Using Centroids as Seeds";
    public static final String CELL_GAUSS_FILTER_TITLE = "Gaussian Filtering to Suppress Noise in Cell Channel Prior to Segmentation";
    public static final String CELL_SEG_TITLE = "Segmentation of Cells Using Nuclei as Seeds";
    public static final String FOCI_CENTROID_LOCALISATION_TITLE = "Blob Detection to Approximate Locations of Spots in Channel ";
    public static final String LOAD_PARAMETERS = "Load Parameters";
    public static final Font TITLE_FONT = new java.awt.Font("Segoe UI Semibold", 1, 14);
    public static final String UNITS = "Units";
    public static final String ENABLE_TOP_HAT_FILTER = "Enable Top Hat Filtering";
    public static final String NUC_CENTROID_LOCALISATION_METHOD = "Choose a Method to Detect Nuclei";
    public static final String NUC_MAXIMA_DETECT_STARDIST = "StarDist (beta)";
    public static final String STARDIST_PROB_THRESH = "StarDist Probability Threshold";
    public static final String STARDIST_OVERLAP_THRESH = "StarDist Overlap Threshold";
    public static final String STARDIST_ENV_DIRECTORY = "Location of StarDist Virtual Environment";
    public static final String STARDIST_MODEL_DIRECTORY = "Location of StarDist Model";
    public static final String STARDIST_TILE_XY = "Number of Tile Divisions in X and Y";
    public static final String STARDIST_TILE_Z = "Number of Tile Divisions in Z";
    public static final String NUC_DETECT_MODE = "Detection Method";
    public static final String NUC_MAXIMA_DETECT_ILASTIK = "ilastik (beta)";
    public static final String ILASTIK_PROJECT_FILE = "Location of ilastik Project File";
    public static final String ILASTIK_INPUT_CHANNEL = "Select channel to use in ilastik output";
    public static final String ILASTIK_DIRECTORY = "Location of ilastik installation";
    public static final String ILASTIK_THRESHOLD = "Probability threshold for ilastik output";
    public static final String ILASTIK_SMOOTHING = "Filter radius to smooth ilastik output";
    public static final String BLOB_CHAN_MAXIMA_DETECT_STARDIST = "StarDist (beta) for Channel ";
    public static final String BLOB_CHAN_STARDIST_OVERLAP_THRESH = "StarDist Overlap Threshold for Channel ";
    public static final String BLOB_CHAN_STARDIST_PROB_THRESH = "StarDist Probability Threshold for Channel ";
    public static final String BLOB_CHAN_STARDIST_ENV_DIRECTORY = "Location of StarDist Virtual Environment for Channel ";
    public static final String BLOB_CHAN_STARDIST_MODEL_DIRECTORY = "Location of StarDist Model for Channel ";
    public static final String BLOB_CHAN_STARDIST_TILE_XY = GianiDefaultParams.STARDIST_TILE_XY + " for Channel ";
    public static final String BLOB_CHAN_STARDIST_TILE_Z = GianiDefaultParams.STARDIST_TILE_Z + " for Channel ";
    public static final String BLOB_CHAN_ILASTIK_PROJECT_FILE = GianiDefaultParams.ILASTIK_PROJECT_FILE + " for Channel ";
    public static final String BLOB_CHAN_ILASTIK_DIRECTORY = GianiDefaultParams.ILASTIK_DIRECTORY + " for Channel ";
    public static final String BLOB_CHAN_ILASTIK_INPUT_CHANNEL = GianiDefaultParams.ILASTIK_INPUT_CHANNEL + " for Channel ";
    public static final String BLOB_CHAN_NUC_MAXIMA_DETECT_ILASTIK = GianiDefaultParams.NUC_MAXIMA_DETECT_ILASTIK + " for Channel ";
    public static final String BLOB_CHAN_ILASTIK_THRESHOLD = GianiDefaultParams.ILASTIK_THRESHOLD + " for Channel ";
    public static final String BLOB_CHAN_ILASTIK_SMOOTHING = GianiDefaultParams.ILASTIK_SMOOTHING + " for Channel ";
    public static final String BLOB_CHAN_CENTROID_LOCALISATION_METHOD = "Choose a Method to Detect Blobs in Channel ";
    public static final String NUC_MAXIMA_DETECT_THRESHOLD = "Grey Level Thresholding";
    public static final String NUC_MAXIMA_SEG_THRESH_LABEL = "Threshold Method for Nuclear Centroid Detection";
    public static final String BLOB_CHAN_DETECT_THRESHOLD = "Grey Level Thresholding for Blobs in Channel ";
    public static final String BLOB_CHAN_SEG_THRESH_LABEL = "Threshold Method for Blob Detection for Channel ";

    /**
     * Initialises an instance of GianiDefaultParams with most parameters set to
     * zero
     */
    public GianiDefaultParams() {
        initialise();
    }

    private void initialise() {
        //this.setProperty(INPUT_DIR_LABEL, System.getProperty("user.home"));
        this.setProperty(INPUT_DIR_LABEL, "E:\\Dropbox (The Francis Crick)\\Debugging\\Giani\\images\\subset");
        this.setProperty(OUTPUT_DIR_LABEL, "");
        this.setProperty(SERIES_SELECT_LABEL, "0");
        this.setProperty(INPUT_FILE_LABEL, "0");
        this.setProperty(PREVIEW_CHAN_SELECT_LABEL, "0");
        this.setProperty(BLOB_NUC_CHAN_SELECT_LABEL, "0");
        this.setProperty(BLOB_CHAN_SELECT_LABEL, "0");
        this.setProperty(NUC_FILT_RAD_LABEL, "0.0");
        this.setProperty(NUC_TOP_HAT_FILT_RAD_LABEL, "0.0");
        this.setProperty(NUC_TOP_HAT_DOWNSIZE_FACTOR_LABEL, "1.0");
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
        //this.setProperty(NUC_MAXIMA_DETECT_HESSIAN_STOP_SCALE, "0.0");
        //this.setProperty(NUC_MAXIMA_DETECT_HESSIAN_SCALE_STEP, "0.0");
        this.setProperty(NUC_MAXIMA_DETECT_HESSIAN_ABS, "false");
        this.setProperty(SPECIFIC_SERIES, "-1");
        this.setProperty(UNITS, String.format("%cm", IJ.micronSymbol));
        this.setProperty(ENABLE_TOP_HAT_FILTER, "true");
        this.setProperty(NUC_MAXIMA_DETECT_STARDIST, "false");
        this.setProperty(STARDIST_PROB_THRESH, "0.75");
        this.setProperty(STARDIST_OVERLAP_THRESH, "0.6");
        this.setProperty(STARDIST_ENV_DIRECTORY, System.getProperty("user.home"));
        this.setProperty(STARDIST_MODEL_DIRECTORY, System.getProperty("user.home"));
        this.setProperty(STARDIST_TILE_XY, "8");
        this.setProperty(STARDIST_TILE_Z, "2");
        this.setProperty(NUC_MAXIMA_DETECT_ILASTIK, "false");
        //this.setProperty(ILASTIK_PROJECT_FILE, System.getProperty("user.home"));
        this.setProperty(ILASTIK_PROJECT_FILE, "E:\\Dropbox (The Francis Crick)\\Debugging\\Giani\\ilastik_integration\\MyProject.ilp");
        //this.setProperty(ILASTIK_DIRECTORY, System.getProperty("user.home"));
        this.setProperty(ILASTIK_DIRECTORY, "C:\\Program Files\\ilastik-1.3.3post3");
        this.setProperty(ILASTIK_INPUT_CHANNEL, "0");
        this.setProperty(ILASTIK_THRESHOLD, "0.75");
        this.setProperty(ILASTIK_SMOOTHING, "1.0");
    }

    /**
     * Set the output directory based on the input directory and store it in the
     * Properties
     *
     * @param props contains the input directory and will contain the location
     *              of the output directory on exiting
     * @param label if not null, the output directory will contain this label
     * @return true if output directory has been successfully set, false
     * otherwise
     */
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

    protected static String getTitleWithVersion() {
        try {
            File gianiJar = new File(GIANIUI.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
            String filename = FilenameUtils.getBaseName(gianiJar.getAbsolutePath());
            if (!filename.toUpperCase().startsWith(APP_TITLE.toUpperCase())) {
                throw new IOException();
            }
            return filename.toUpperCase();
        } catch (IOException | URISyntaxException e) {
            return APP_TITLE;
        }
    }

    /**
     * Search for any deprecated parameter names in the specifed properties and
     * update them
     *
     * @param props the parameters to update
     */
    public static void updateDeprecatedProps(Properties props) {
        HashMap<String, String> map = DeprecatedProps.getDepMap();
        Set<String> propNames = props.stringPropertyNames();
        Set<String> deprecNames = map.keySet();
        for (String propName : propNames) {
            for (String deprecatedName : deprecNames) {
                if (propName.startsWith(deprecatedName)) {
                    String val = props.getProperty(propName);
                    String updatedPropName = propName.replace(deprecatedName, map.get(deprecatedName));
                    props.setProperty(updatedPropName, val);
                }
            }
        }
        DeprecatedProps.mapControlChanges(props);
    }
}
