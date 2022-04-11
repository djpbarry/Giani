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

import java.util.HashMap;

/**
 * Utility class containing old names for parameters
 *
 * @author Dave Barry
 * @since 3.2.1
 */
public class DeprecatedProps {

    public static final String BLOB_NUC_NOISE_TOL_LABEL = "Threshold for Simple Nuclear Centroid Detection";
    public static final String BLOB_CHAN_NOISE_TOL_LABEL = "Threshold for Simple Foci Detection in Channel ";
    public static final String NUC_SEG_THRESH_LABEL = "Threshold for Nuclear Segmentation";
    public static final String CELL_SEG_THRESH_LABEL = "Threshold for Cell Segmentation";
    public static final String NUC_MAXIMA_DETECT_HESSIAN_THRESH = "Threshold for Advanced Nuclear Centroid Detection";
    public static final String FOCI_MAXIMA_DETECT_HESSIAN_THRESH = "Threshold for Advanced Foci Detection in Channel ";
    public static final String BLOB_CHAN_NOISE_TOL_LABEL_2 = "Quality of Simple Foci Detections in Channel ";
    public static final String BLOB_CHAN_RAD_LABEL = String.format("Radius (%cm) for Simple Foci Detection in Channel ", IJ.micronSymbol);
    public static final String FOCI_MAXIMA_DETECT_BLOBS = "Simple Foci Detector for Channel ";
    public static final String FOCI_MAXIMA_DETECT_HESSIAN_MAXIMA = "Advanced Foci Detector for Channel ";
    public static final String FOCI_MAXIMA_DETECT_HESSIAN_THRESH_2 = "Quality of Advanced Foci Detections in Channel ";
    public static final String FOCI_MAXIMA_DETECT_HESSIAN_MIN_SIZE = "Minimum Foci Radius for Advanced Detection in Channel ";
    public static final String FOCI_MAXIMA_DETECT_HESSIAN_MAX_SIZE = "Maximum Foci Radius for Advanced Detection in Channel ";
    public static final String FOCI_MAXIMA_DETECT_HESSIAN_SCALE_STEP = "Radius Step Size for Advanced Foci Detection in Channel ";
    public static final String FOCI_CENTROID_LOCALISATION_TITLE = "Blob Detection to Approximate Locations of Foci in Channel ";
    public static final String BLOB_NUC_RAD_LABEL = String.format("Nuclear Radius for Simple Centroid Detection (%cm)", IJ.micronSymbol);
    public static final String NUC_FILT_RAD_LABEL = String.format("Filter Radius for Nuclear Channel (%cm)", IJ.micronSymbol);
    public static final String NUC_TOP_HAT_FILT_RAD_LABEL = String.format("Top Hat Filter Radius for Nuclear Channel (%cm)", IJ.micronSymbol);
    public static final String BLOB_CHAN_RAD_LABEL_2 = String.format("Radius (%cm) for Simple Spot Detection in Channel ", IJ.micronSymbol);
    public static final String CELL_FILT_RAD_LABEL = String.format("Filter Radius for Cell Channel (%cm)", IJ.micronSymbol);
    public static final String NUC_MAXIMA_DETECT_HESSIAN_START_SCALE = String.format("Minimum Nuclear Radius for Advanced Centroid Detection (%cm)", IJ.micronSymbol);
    public static final String NUC_MAXIMA_DETECT_HESSIAN_STOP_SCALE = String.format("Maximum Nuclear Radius for Advanced Centroid Detection (%cm)", IJ.micronSymbol);
    public static final String NUC_MAXIMA_DETECT_HESSIAN_SCALE_STEP = String.format("Radius Step Size for Advanced Nuclear Centroid Detection (%cm)", IJ.micronSymbol);
    public static final String FOCI_MAXIMA_DETECT_FILTER_RAD = String.format("Filter Radius (%cm) for EDM Detector for Channel ", IJ.micronSymbol);

    protected static HashMap<String, String> getDepMap() {
        HashMap<String, String> map = new HashMap<>();
        map.put(DeprecatedProps.BLOB_NUC_NOISE_TOL_LABEL, GianiDefaultParams.BLOB_NUC_NOISE_TOL_LABEL);
        map.put(DeprecatedProps.BLOB_CHAN_NOISE_TOL_LABEL, GianiDefaultParams.BLOB_CHAN_NOISE_TOL_LABEL);
        map.put(DeprecatedProps.NUC_SEG_THRESH_LABEL, GianiDefaultParams.NUC_SEG_THRESH_LABEL);
        map.put(DeprecatedProps.CELL_SEG_THRESH_LABEL, GianiDefaultParams.CELL_SEG_THRESH_LABEL);
        map.put(DeprecatedProps.NUC_MAXIMA_DETECT_HESSIAN_THRESH, GianiDefaultParams.NUC_MAXIMA_DETECT_HESSIAN_THRESH);
        map.put(DeprecatedProps.FOCI_MAXIMA_DETECT_HESSIAN_THRESH, GianiDefaultParams.FOCI_MAXIMA_DETECT_HESSIAN_THRESH);
        map.put(DeprecatedProps.BLOB_CHAN_NOISE_TOL_LABEL_2, GianiDefaultParams.BLOB_CHAN_NOISE_TOL_LABEL);
        map.put(DeprecatedProps.BLOB_CHAN_RAD_LABEL, GianiDefaultParams.BLOB_CHAN_RAD_LABEL);
        map.put(DeprecatedProps.FOCI_MAXIMA_DETECT_BLOBS, GianiDefaultParams.FOCI_MAXIMA_DETECT_BLOBS);
        map.put(DeprecatedProps.FOCI_MAXIMA_DETECT_HESSIAN_MAXIMA, GianiDefaultParams.FOCI_MAXIMA_DETECT_HESSIAN_MAXIMA);
        map.put(DeprecatedProps.FOCI_MAXIMA_DETECT_HESSIAN_THRESH_2, GianiDefaultParams.FOCI_MAXIMA_DETECT_HESSIAN_THRESH);
        map.put(DeprecatedProps.FOCI_MAXIMA_DETECT_HESSIAN_MIN_SIZE, GianiDefaultParams.FOCI_MAXIMA_DETECT_HESSIAN_MIN_SIZE);
        map.put(DeprecatedProps.FOCI_MAXIMA_DETECT_HESSIAN_MAX_SIZE, GianiDefaultParams.FOCI_MAXIMA_DETECT_HESSIAN_MAX_SIZE);
        map.put(DeprecatedProps.FOCI_MAXIMA_DETECT_HESSIAN_SCALE_STEP, GianiDefaultParams.FOCI_MAXIMA_DETECT_HESSIAN_SCALE_STEP);
        map.put(DeprecatedProps.FOCI_CENTROID_LOCALISATION_TITLE, GianiDefaultParams.FOCI_CENTROID_LOCALISATION_TITLE);
        map.put(DeprecatedProps.BLOB_NUC_RAD_LABEL, GianiDefaultParams.BLOB_NUC_RAD_LABEL);
        map.put(DeprecatedProps.NUC_FILT_RAD_LABEL, GianiDefaultParams.NUC_FILT_RAD_LABEL);
        map.put(DeprecatedProps.NUC_TOP_HAT_FILT_RAD_LABEL, GianiDefaultParams.NUC_TOP_HAT_FILT_RAD_LABEL);
        map.put(DeprecatedProps.BLOB_CHAN_RAD_LABEL_2, GianiDefaultParams.BLOB_CHAN_RAD_LABEL);
        map.put(DeprecatedProps.CELL_FILT_RAD_LABEL, GianiDefaultParams.CELL_FILT_RAD_LABEL);
        map.put(DeprecatedProps.NUC_MAXIMA_DETECT_HESSIAN_START_SCALE, GianiDefaultParams.NUC_MAXIMA_DETECT_HESSIAN_START_SCALE);
        map.put(DeprecatedProps.NUC_MAXIMA_DETECT_HESSIAN_STOP_SCALE, GianiDefaultParams.NUC_MAXIMA_DETECT_HESSIAN_STOP_SCALE);
        map.put(DeprecatedProps.NUC_MAXIMA_DETECT_HESSIAN_SCALE_STEP, GianiDefaultParams.NUC_MAXIMA_DETECT_HESSIAN_SCALE_STEP);
        //map.put(DeprecatedProps.FOCI_MAXIMA_DETECT_FILTER_RAD, GianiDefaultParams.FOCI_MAXIMA_DETECT_FILTER_RAD);
        return map;
    }
}
