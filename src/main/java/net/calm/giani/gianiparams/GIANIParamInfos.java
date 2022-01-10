package net.calm.giani.gianiparams;

import java.util.Properties;

public class GIANIParamInfos extends Properties {

    public GIANIParamInfos() {
        super();
        initialise();
    }

    private void initialise() {
        this.setProperty(GianiDefaultParams.LOAD_PARAMETERS, "Load analysis parameters from an existing properties.xml file.");
        this.setProperty(GianiDefaultParams.INPUT_DIR_LABEL, "Select the folder where your images are stored.");
        this.setProperty(GianiDefaultParams.INPUT_FILE_LABEL, "Select a file to use for testing/debugging different parameters.");
        this.setProperty(GianiDefaultParams.SERIES_SELECT_LABEL, "If the above file contains multiple datasets, select the one " +
                "you want to use.");
        this.setProperty(GianiDefaultParams.PREVIEW_CHAN_SELECT_LABEL, "Select a particular channel from the above dataset to preview.");
        this.setProperty(GianiDefaultParams.BLOB_NUC_CHAN_SELECT_LABEL, "Select the channel containing a nuclear volume " +
                "marker (e.g. DAPI).");
        this.setProperty(GianiDefaultParams.NUC_MAXIMA_DETECT_BLOBS, "Simple detection works well for regularly-shaped nuclei.");
        this.setProperty(GianiDefaultParams.NUC_MAXIMA_DETECT_HESSIAN, "Advanced detection is better for more irregularly-" +
                "shaped nuclei, but may take longer.");
        this.setProperty(GianiDefaultParams.BLOB_NUC_RAD_LABEL, "Approximate radius of nuclei in microns.");
        this.setProperty(GianiDefaultParams.BLOB_NUC_NOISE_TOL_LABEL, "Sensitivity of blob detector - increase this value if " +
                "non-nuclear artefacts are detected.");
        this.setProperty(GianiDefaultParams.NUC_MAXIMA_DETECT_HESSIAN_START_SCALE, "Approximate minimum radius of nuclei in microns.");
        this.setProperty(GianiDefaultParams.NUC_MAXIMA_DETECT_HESSIAN_STOP_SCALE, "Approximate maximum radius of nuclei in microns. " +
                "Set equal to the minimum radius to search for only one nuclear radius.");
        this.setProperty(GianiDefaultParams.NUC_MAXIMA_DETECT_HESSIAN_SCALE_STEP, "When attempting detection of nuclei between " +
                "the minimum and maximum radius, specify how much the detection radius should increase by at each stage.");
        this.setProperty(GianiDefaultParams.NUC_MAXIMA_DETECT_HESSIAN_THRESH, "Sensitivity of blob detector - increase this value if " +
                "non-nuclear artefacts are detected.");
        this.setProperty(GianiDefaultParams.NUC_SEG_CHAN_SELECT_LABEL, "Select the channel that will be used to segment " +
                "the nuclei.");
        this.setProperty(GianiDefaultParams.NUC_FILT_RAD_LABEL, "Specify the radius (in microns) of the Gaussian filter " +
                "that will be used to smooth the nuclear image prior to segmentation.");
        this.setProperty(GianiDefaultParams.NUC_TOP_HAT_FILT_RAD_LABEL, "Specify the radius (in microns) of the filter " +
                "that will be used for background subtraction in the nuclear channel.");
        this.setProperty(GianiDefaultParams.NUC_TOP_HAT_DOWNSIZE_FACTOR_LABEL, "Background subtraction can take a long time " +
                "for large images - increase the downsizing factor to speed up the process.");
        this.setProperty(GianiDefaultParams.NUC_SEG_THRESH_LABEL, "Select the threshold method to be used for segmenting nuclei.");
        this.setProperty(GianiDefaultParams.NUC_VOL_MARKER, "Select this if using a volume marker for nuclear segmentation.");
        this.setProperty(GianiDefaultParams.NUC_MEM_MARKER, "Select this if using a membrane marker for nuclear segmentation.");
        this.setProperty(GianiDefaultParams.NUC_DIST_WEIGHTING, "Increase this value to make the segmentation less sensitive " +
                "to local changes in fluorescence intensity.");
        this.setProperty(GianiDefaultParams.CELL_SEG_CHAN_SELECT_LABEL, "Select the channel that will be used to segment " +
                "cells.");
        this.setProperty(GianiDefaultParams.CELL_FILT_RAD_LABEL, "Specify the radius (in microns) of the Gaussian filter " +
                "that will be used to smooth the cell image prior to segmentation.");
        this.setProperty(GianiDefaultParams.CELL_SEG_THRESH_LABEL, "Select the threshold method to be used for segmenting cells.");
        this.setProperty(GianiDefaultParams.CELL_VOL_MARKER, "Select this if using a volume marker for cell segmentation.");
        this.setProperty(GianiDefaultParams.CELL_MEM_MARKER, "Select this if using a membrane marker for cell segmentation.");
        this.setProperty(GianiDefaultParams.CELL_DIST_WEIGHTING, "Increase this value to make the segmentation less sensitive " +
                "to local changes in fluorescence intensity.");
        this.setProperty(GianiDefaultParams.PREVIEW, "Click to preview the effect of the above parameters.");
        this.setProperty(GianiDefaultParams.PREVIEW_MEASURE, "Click to view measurements in the above channels for the preview dataset.");
        this.setProperty(GianiDefaultParams.CHAN_FOR_MEASURE, "Select which channels you wish to extract fluorescence intensity " +
                "measures from.");
        this.setProperty(GianiDefaultParams.LOCALISE_SPOTS, "Select to detect spots in the above-selected channels - click \"Next\" below " +
                "to set parameters for each channel.");
        this.setProperty(GianiDefaultParams.HELP, "Click to read the online documentation.");
    }
}
