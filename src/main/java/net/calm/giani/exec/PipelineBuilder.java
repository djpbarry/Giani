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
package net.calm.giani.exec;

import mcib3d.geom.Objects3DPopulation;
import net.calm.giani.gianiparams.GianiDefaultParams;
import net.calm.iaclasslibrary.Extrema.MultiThreadedMaximaFinder;
import net.calm.iaclasslibrary.IO.BioFormats.BioFormatsImg;
import net.calm.iaclasslibrary.Process.Colocalise.MultiThreadedColocalise;
import net.calm.iaclasslibrary.Process.Filtering.MultiThreadedGaussianFilter;
import net.calm.iaclasslibrary.Process.Filtering.MultiThreadedTopHatFilter;
import net.calm.iaclasslibrary.Process.MultiThreadedProcess;
import net.calm.iaclasslibrary.Process.ProcessPipeline;
import net.calm.iaclasslibrary.Process.ROI.MultiThreadedROIConstructor;
import net.calm.iaclasslibrary.Process.Segmentation.MultiThreadedWatershed;

import java.util.ArrayList;
import java.util.Properties;

/**
 * Main entry point for building a GIANI pipeline. To build your own custom pipeline, extend this class and override
 * the buildFullPipeline method
 *
 * @author Dave Barry
 * @since 1.0.0
 */
public class PipelineBuilder {

    /**
     * Returns the default blob detector process used by GIANI
     *
     * @param props the properties containing key-value parameter-setting pairs to be associated with the process
     * @return a new {@link MultiThreadedMaximaFinder}
     */
    public static MultiThreadedMaximaFinder getDefaultMaximaFinder(Properties props) {
        String[] propLabels = new String[MultiThreadedMaximaFinder.N_PROP_LABELS];
        propLabels[MultiThreadedMaximaFinder.CHANNEL_SELECT] = GianiDefaultParams.BLOB_NUC_CHAN_SELECT_LABEL;
        propLabels[MultiThreadedMaximaFinder.BLOB_DETECT] = GianiDefaultParams.NUC_MAXIMA_DETECT_BLOBS;
        propLabels[MultiThreadedMaximaFinder.BLOB_SIZE] = GianiDefaultParams.BLOB_NUC_RAD_LABEL;
        propLabels[MultiThreadedMaximaFinder.BLOB_THRESH] = GianiDefaultParams.BLOB_NUC_NOISE_TOL_LABEL;
        propLabels[MultiThreadedMaximaFinder.HESSIAN_DETECT] = GianiDefaultParams.NUC_MAXIMA_DETECT_HESSIAN;
        propLabels[MultiThreadedMaximaFinder.HESSIAN_START_SCALE] = GianiDefaultParams.NUC_MAXIMA_DETECT_HESSIAN_START_SCALE;
        propLabels[MultiThreadedMaximaFinder.HESSIAN_THRESH] = GianiDefaultParams.NUC_MAXIMA_DETECT_HESSIAN_THRESH;
        propLabels[MultiThreadedMaximaFinder.SERIES_SELECT] = GianiDefaultParams.SERIES_SELECT_LABEL;
        propLabels[MultiThreadedMaximaFinder.HESSIAN_ABS] = GianiDefaultParams.NUC_MAXIMA_DETECT_HESSIAN_ABS;
        propLabels[MultiThreadedMaximaFinder.METHOD] = GianiDefaultParams.NUC_DETECT_MODE;
        propLabels[MultiThreadedMaximaFinder.STARDIST_DETECT] = GianiDefaultParams.NUC_MAXIMA_DETECT_STARDIST;
        propLabels[MultiThreadedMaximaFinder.STARDIST_OVERLAP] = GianiDefaultParams.STARDIST_OVERLAP_THRESH;
        propLabels[MultiThreadedMaximaFinder.STARDIST_PROB] = GianiDefaultParams.STARDIST_PROB_THRESH;
        propLabels[MultiThreadedMaximaFinder.STARDIST_DIR] = GianiDefaultParams.STARDIST_ENV_DIRECTORY;
        propLabels[MultiThreadedMaximaFinder.STARDIST_MODEL] = GianiDefaultParams.STARDIST_MODEL_DIRECTORY;
        propLabels[MultiThreadedMaximaFinder.STARDIST_TILE_XY] = GianiDefaultParams.STARDIST_TILE_XY;
        propLabels[MultiThreadedMaximaFinder.STARDIST_TILE_Z] = GianiDefaultParams.STARDIST_TILE_Z;
        propLabels[MultiThreadedMaximaFinder.ILASTIK_FILE] = GianiDefaultParams.ILASTIK_PROJECT_FILE;
        propLabels[MultiThreadedMaximaFinder.ILASTIK_DIR] = GianiDefaultParams.ILASTIK_DIRECTORY;
        propLabels[MultiThreadedMaximaFinder.ILASTIK_CHANNEL] = GianiDefaultParams.ILASTIK_INPUT_CHANNEL;
        propLabels[MultiThreadedMaximaFinder.ILASTIK_DETECT] = GianiDefaultParams.NUC_MAXIMA_DETECT_ILASTIK;
        propLabels[MultiThreadedMaximaFinder.ILASTIK_THRESH] = GianiDefaultParams.ILASTIK_THRESHOLD;
        propLabels[MultiThreadedMaximaFinder.ILASTIK_SMOOTHING] = GianiDefaultParams.ILASTIK_SMOOTHING;
        MultiThreadedMaximaFinder process = new MultiThreadedMaximaFinder(null);
        process.setup(new BioFormatsImg(), props, propLabels);
        return process;
    }

    /**
     * Returns the default top hat filter process used by GIANI
     *
     * @param props  the properties containing key-value parameter-setting pairs to be associated with the process
     * @param inputs the processes whose outputs will be used as inputs for this process
     * @return a new {@link MultiThreadedTopHatFilter}
     */
    public static MultiThreadedTopHatFilter getDefaultNucTopHatFilteringProcess(Properties props, MultiThreadedProcess[] inputs) {
        String[] propLabels = new String[MultiThreadedTopHatFilter.N_PROP_LABELS];
        propLabels[MultiThreadedTopHatFilter.CHANNEL_LABEL] = GianiDefaultParams.NUC_SEG_CHAN_SELECT_LABEL;
        propLabels[MultiThreadedTopHatFilter.SERIES_LABEL] = GianiDefaultParams.SERIES_SELECT_LABEL;
        propLabels[MultiThreadedTopHatFilter.FILT_RAD_LABEL] = GianiDefaultParams.NUC_TOP_HAT_FILT_RAD_LABEL;
        propLabels[MultiThreadedTopHatFilter.RESIZE_FACTOR_LABEL] = GianiDefaultParams.NUC_TOP_HAT_DOWNSIZE_FACTOR_LABEL;
        propLabels[MultiThreadedTopHatFilter.ENABLE_FILTER_LABEL] = GianiDefaultParams.ENABLE_TOP_HAT_FILTER;
        MultiThreadedTopHatFilter process = new MultiThreadedTopHatFilter(inputs);
        process.setup(new BioFormatsImg(), props, propLabels);
        return process;
    }

    /**
     * Returns the default nuclear Gaussian filter process used by GIANI
     *
     * @param props the properties containing key-value parameter-setting pairs to be associated with the process
     * @return a new {@link MultiThreadedGaussianFilter}
     */
    public static MultiThreadedGaussianFilter getDefaultNucFilteringProcess(Properties props) {
        String[] propLabels = new String[MultiThreadedGaussianFilter.N_PROP_LABELS];
        propLabels[MultiThreadedGaussianFilter.CHANNEL_LABEL] = GianiDefaultParams.NUC_SEG_CHAN_SELECT_LABEL;
        propLabels[MultiThreadedGaussianFilter.SERIES_LABEL] = GianiDefaultParams.SERIES_SELECT_LABEL;
        propLabels[MultiThreadedGaussianFilter.FILT_RAD_LABEL] = GianiDefaultParams.NUC_FILT_RAD_LABEL;
        MultiThreadedGaussianFilter process = new MultiThreadedGaussianFilter(null);
        process.setup(new BioFormatsImg(), props, propLabels);
        return process;
    }

    /**
     * Returns the default nuclear segmentation process used by GIANI
     *
     * @param props  the properties containing key-value parameter-setting pairs to be associated with the process
     * @param inputs the processes whose outputs will be used as inputs for this process
     * @param cells  instance of an {@link Objects3DPopulation} that will store the resulting segmentation
     * @return a new {@link MultiThreadedWatershed}
     */
    public static MultiThreadedWatershed getDefaultNucSegmenter(Properties props, MultiThreadedProcess[] inputs, Objects3DPopulation cells) {
        String[] propLabels = new String[MultiThreadedWatershed.N_PROP_LABELS];
        propLabels[MultiThreadedWatershed.SERIES_LABEL] = GianiDefaultParams.SERIES_SELECT_LABEL;
        propLabels[MultiThreadedWatershed.THRESHOLD_LABEL] = GianiDefaultParams.NUC_SEG_THRESH_LABEL;
        propLabels[MultiThreadedWatershed.VOL_MARKER_LABEL] = GianiDefaultParams.NUC_VOL_MARKER;
        propLabels[MultiThreadedWatershed.MEMB_MARKER_LABEL] = GianiDefaultParams.NUC_MEM_MARKER;
        propLabels[MultiThreadedWatershed.LAMBDA_LABEL] = GianiDefaultParams.NUC_DIST_WEIGHTING;
        MultiThreadedWatershed process = new MultiThreadedWatershed(inputs,
                "Nuclei",
                false,
                false,
                false,
                cells,
                MultiThreadedWatershed.NUCLEI);
        process.setup(new BioFormatsImg(), props, propLabels);
        return process;
    }

    /**
     * Returns the default cell Gaussian filter process used by GIANI
     *
     * @param props the properties containing key-value parameter-setting pairs to be associated with the process
     * @return a new {@link MultiThreadedGaussianFilter}
     */
    public static MultiThreadedGaussianFilter getDefaultCellFilteringProcess(Properties props) {
        MultiThreadedGaussianFilter process = new MultiThreadedGaussianFilter(null);
        String[] propLabels = new String[MultiThreadedGaussianFilter.N_PROP_LABELS];
        propLabels[MultiThreadedGaussianFilter.CHANNEL_LABEL] = GianiDefaultParams.CELL_SEG_CHAN_SELECT_LABEL;
        propLabels[MultiThreadedGaussianFilter.SERIES_LABEL] = GianiDefaultParams.SERIES_SELECT_LABEL;
        propLabels[MultiThreadedGaussianFilter.FILT_RAD_LABEL] = GianiDefaultParams.CELL_FILT_RAD_LABEL;
        process.setup(new BioFormatsImg(), props, propLabels);
        return process;
    }

    /**
     * Returns the default cell segmentation process used by GIANI
     *
     * @param props  the properties containing key-value parameter-setting pairs to be associated with the process
     * @param inputs the processes whose outputs will be used as inputs for this process
     * @param cells  instance of an {@link Objects3DPopulation} that will store the resulting segmentation
     * @return a new {@link MultiThreadedWatershed}
     */
    public static MultiThreadedWatershed getDefaultCellSegmenter(Properties props, MultiThreadedProcess[] inputs, Objects3DPopulation cells) {
        String[] propLabels = new String[MultiThreadedWatershed.N_PROP_LABELS];
        propLabels[MultiThreadedWatershed.SERIES_LABEL] = GianiDefaultParams.SERIES_SELECT_LABEL;
        propLabels[MultiThreadedWatershed.THRESHOLD_LABEL] = GianiDefaultParams.CELL_SEG_THRESH_LABEL;
        propLabels[MultiThreadedWatershed.VOL_MARKER_LABEL] = GianiDefaultParams.CELL_VOL_MARKER;
        propLabels[MultiThreadedWatershed.MEMB_MARKER_LABEL] = GianiDefaultParams.CELL_MEM_MARKER;
        propLabels[MultiThreadedWatershed.LAMBDA_LABEL] = GianiDefaultParams.CELL_DIST_WEIGHTING;
        MultiThreadedWatershed process = new MultiThreadedWatershed(
                inputs,
                "Cells",
                true,
                true,
                true,
                cells,
                MultiThreadedWatershed.CELLS
        );
        process.setup(new BioFormatsImg(), props, propLabels);
        return process;
    }

    /**
     * Returns the default object measurement process used by GIANI
     *
     * @param props  the properties containing key-value parameter-setting pairs to be associated with the process
     * @param inputs the processes whose outputs will be used as inputs for this process
     * @param cells  instance of an {@link Objects3DPopulation} that will store the resulting segmentation
     * @return a new {@link MultiThreadedWatershed}
     */
    public static MultiThreadedROIConstructor getDefaultMeasure(Properties props, MultiThreadedProcess[] inputs, Objects3DPopulation cells) {
        String[] propLabels = new String[MultiThreadedROIConstructor.N_PROP_LABELS];
        propLabels[MultiThreadedROIConstructor.SERIES_LABEL] = GianiDefaultParams.SERIES_SELECT_LABEL;
        propLabels[MultiThreadedROIConstructor.CHANNELS_LABEL] = GianiDefaultParams.CHAN_FOR_MEASURE;
        propLabels[MultiThreadedROIConstructor.LOCALISE_LABEL] = GianiDefaultParams.LOCALISE_SPOTS;
        propLabels[MultiThreadedROIConstructor.OUTPUT_LABEL] = GianiDefaultParams.OUTPUT_DIR_LABEL;
        MultiThreadedROIConstructor process = new MultiThreadedROIConstructor(
                inputs,
                cells
        );
        process.setup(new BioFormatsImg(), props, propLabels);
        return process;
    }

    /**
     * Assemble a complete GIANI pipeline
     *
     * @param props the parameter-value pairs to be used for all processes in the pipeline
     * @param cells instance of an {@link Objects3DPopulation} that will store the resulting segmentations
     * @return a new {@link ProcessPipeline}
     */
    public ProcessPipeline buildFullPipeline(Properties props, Objects3DPopulation cells) {
        ProcessPipeline pipeline = new ProcessPipeline();

        pipeline.addProcess(PipelineBuilder.getDefaultMaximaFinder(props));
        pipeline.addProcess(PipelineBuilder.getDefaultNucFilteringProcess(props));
        pipeline.addProcess(PipelineBuilder.getDefaultNucTopHatFilteringProcess(props, new MultiThreadedProcess[]{
                pipeline.getProcess(1)}));
        pipeline.addProcess(PipelineBuilder.getDefaultNucSegmenter(props,
                new MultiThreadedProcess[]{
                        pipeline.getProcess(0), pipeline.getProcess(2)
                }, cells));
        pipeline.addProcess(PipelineBuilder.getDefaultCellFilteringProcess(props));
        pipeline.addProcess(PipelineBuilder.getDefaultCellSegmenter(props,
                new MultiThreadedProcess[]{
                        pipeline.getProcess(3), pipeline.getProcess(4)
                }, cells));
        pipeline.addProcess(PipelineBuilder.getDefaultMeasure(props,
                new MultiThreadedProcess[]{
                        pipeline.getProcess(3), pipeline.getProcess(5)
                }, cells));

        String selectedChannels = props.getProperty(GianiDefaultParams.CHAN_FOR_MEASURE);
        if (selectedChannels == null || !Boolean.parseBoolean(props.getProperty(GianiDefaultParams.LOCALISE_SPOTS))) {
            return pipeline;
        }
        int channels = Integer.parseInt(selectedChannels);
        ArrayList<MultiThreadedMaximaFinder> maxFinders = new ArrayList<>();
        for (int i = 0; i < GianiDefaultParams.MAX_CHANNELS; i++) {
            if ((channels & (int) Math.pow(2.0, i)) != 0) {
                maxFinders.add(PipelineBuilder.getDefaultFociMaximaFinder(props, i));
            }
        }
        int nInputs = maxFinders.size() + 2;
        MultiThreadedProcess[] localisationInputs = new MultiThreadedProcess[nInputs];
        for (int j = 0; j < maxFinders.size(); j++) {
            pipeline.addProcess(maxFinders.get(j));
            localisationInputs[j] = maxFinders.get(j);
        }
        localisationInputs[nInputs - 2] = pipeline.getProcess(5);
        localisationInputs[nInputs - 1] = pipeline.getProcess(3);
        pipeline.addProcess(getDefaultColocalisationProcess(props, localisationInputs, cells));

        return pipeline;
    }

    public static MultiThreadedMaximaFinder getDefaultFociMaximaFinder(Properties props, int i) {
        String[] propLabels = new String[MultiThreadedMaximaFinder.N_PROP_LABELS];
        propLabels[MultiThreadedMaximaFinder.CHANNEL_SELECT] = String.format("%s%d", GianiDefaultParams.BLOB_CHAN_SELECT_LABEL, i);
        propLabels[MultiThreadedMaximaFinder.BLOB_DETECT] = String.format("%s%d", GianiDefaultParams.FOCI_MAXIMA_DETECT_BLOBS, i);
        propLabels[MultiThreadedMaximaFinder.BLOB_SIZE] = String.format("%s%d", GianiDefaultParams.BLOB_CHAN_RAD_LABEL, i);
        propLabels[MultiThreadedMaximaFinder.BLOB_THRESH] = String.format("%s%d", GianiDefaultParams.BLOB_CHAN_NOISE_TOL_LABEL, i);
        propLabels[MultiThreadedMaximaFinder.HESSIAN_DETECT] = String.format("%s%d", GianiDefaultParams.FOCI_MAXIMA_DETECT_HESSIAN_MAXIMA, i);
        propLabels[MultiThreadedMaximaFinder.HESSIAN_START_SCALE] = String.format("%s%d", GianiDefaultParams.FOCI_MAXIMA_DETECT_HESSIAN_MIN_SIZE, i);
        propLabels[MultiThreadedMaximaFinder.HESSIAN_THRESH] = String.format("%s%d", GianiDefaultParams.FOCI_MAXIMA_DETECT_HESSIAN_THRESH, i);
        propLabels[MultiThreadedMaximaFinder.HESSIAN_ABS] = String.format("%s%d", GianiDefaultParams.FOCI_MAXIMA_DETECT_HESSIAN_ABS, i);
        propLabels[MultiThreadedMaximaFinder.SERIES_SELECT] = GianiDefaultParams.SERIES_SELECT_LABEL;
        propLabels[MultiThreadedMaximaFinder.STARDIST_DETECT] = String.format("%s%d", GianiDefaultParams.BLOB_CHAN_MAXIMA_DETECT_STARDIST, i);
        propLabels[MultiThreadedMaximaFinder.STARDIST_PROB] = String.format("%s%d", GianiDefaultParams.BLOB_CHAN_STARDIST_PROB_THRESH, i);
        propLabels[MultiThreadedMaximaFinder.STARDIST_OVERLAP] = String.format("%s%d", GianiDefaultParams.BLOB_CHAN_STARDIST_OVERLAP_THRESH, i);
        propLabels[MultiThreadedMaximaFinder.STARDIST_DIR] = String.format("%s%d", GianiDefaultParams.BLOB_CHAN_STARDIST_ENV_DIRECTORY, i);
        propLabels[MultiThreadedMaximaFinder.STARDIST_MODEL] = String.format("%s%d", GianiDefaultParams.BLOB_CHAN_STARDIST_MODEL_DIRECTORY, i);
        propLabels[MultiThreadedMaximaFinder.STARDIST_TILE_XY] = String.format("%s%d", GianiDefaultParams.BLOB_CHAN_STARDIST_TILE_XY, i);
        propLabels[MultiThreadedMaximaFinder.STARDIST_TILE_Z] = String.format("%s%d", GianiDefaultParams.BLOB_CHAN_STARDIST_TILE_Z, i);
        propLabels[MultiThreadedMaximaFinder.ILASTIK_DETECT] = String.format("%s%d", GianiDefaultParams.BLOB_CHAN_NUC_MAXIMA_DETECT_ILASTIK, i);
        propLabels[MultiThreadedMaximaFinder.ILASTIK_FILE] = String.format("%s%d", GianiDefaultParams.BLOB_CHAN_ILASTIK_PROJECT_FILE, i);
        propLabels[MultiThreadedMaximaFinder.ILASTIK_DIR] = String.format("%s%d", GianiDefaultParams.BLOB_CHAN_ILASTIK_DIRECTORY, i);
        propLabels[MultiThreadedMaximaFinder.ILASTIK_CHANNEL] = String.format("%s%d", GianiDefaultParams.BLOB_CHAN_ILASTIK_INPUT_CHANNEL, i);
        propLabels[MultiThreadedMaximaFinder.ILASTIK_THRESH] = String.format("%s%d", GianiDefaultParams.BLOB_CHAN_ILASTIK_THRESHOLD, i);
        propLabels[MultiThreadedMaximaFinder.ILASTIK_SMOOTHING] = String.format("%s%d", GianiDefaultParams.BLOB_CHAN_ILASTIK_SMOOTHING, i);
        propLabels[MultiThreadedMaximaFinder.METHOD] = String.format("%s%d", GianiDefaultParams.BLOB_CHAN_CENTROID_LOCALISATION_METHOD, i);
        MultiThreadedMaximaFinder process = new MultiThreadedMaximaFinder(null);
        process.setup(new BioFormatsImg(), props, propLabels);
        return process;
    }

    public static MultiThreadedColocalise getDefaultColocalisationProcess(Properties props, MultiThreadedProcess[] inputs, Objects3DPopulation cells) {
        String[] localisationPropLabels = new String[MultiThreadedColocalise.N_PROP_LABELS];
        localisationPropLabels[MultiThreadedColocalise.SERIES_LABEL] = GianiDefaultParams.SERIES_SELECT_LABEL;
        localisationPropLabels[MultiThreadedColocalise.CHANNELS_LABEL] = GianiDefaultParams.CHAN_FOR_MEASURE;
        localisationPropLabels[MultiThreadedColocalise.OUTPUT_LABEL] = GianiDefaultParams.OUTPUT_DIR_LABEL;
        MultiThreadedColocalise process = new MultiThreadedColocalise(inputs, cells);
        process.setup(new BioFormatsImg(), props, localisationPropLabels);
        return process;
    }
}
