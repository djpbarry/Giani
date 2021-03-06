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
package net.calm.giani.GIANI;

import mcib3d.geom.Objects3DPopulation;
import net.calm.giani.gianiparams.GianiDefaultParams;
import net.calm.iaclasslibrary.Extrema.MultiThreadedMaximaFinder;
import net.calm.iaclasslibrary.IO.BioFormats.BioFormatsImg;
import net.calm.iaclasslibrary.Process.Filtering.MultiThreadedGaussianFilter;
import net.calm.iaclasslibrary.Process.Filtering.MultiThreadedTopHatFilter;
import net.calm.iaclasslibrary.Process.MultiThreadedProcess;
import net.calm.iaclasslibrary.Process.ProcessPipeline;
import net.calm.iaclasslibrary.Process.ROI.MultiThreadedROIConstructor;
import net.calm.iaclasslibrary.Process.Segmentation.MultiThreadedWatershed;

import java.util.Properties;

/**
 * @author David Barry <david.barry at crick dot ac dot uk>
 */
public class PipelineBuilder {

    public static MultiThreadedMaximaFinder getDefaultMaximaFinder(Properties props) {
        String[] propLabels = new String[MultiThreadedMaximaFinder.N_PROP_LABELS];
        propLabels[MultiThreadedMaximaFinder.CHANNEL_SELECT] = GianiDefaultParams.BLOB_NUC_CHAN_SELECT_LABEL;
        propLabels[MultiThreadedMaximaFinder.BLOB_DETECT] = GianiDefaultParams.NUC_MAXIMA_DETECT_BLOBS;
        propLabels[MultiThreadedMaximaFinder.BLOB_SIZE] = GianiDefaultParams.BLOB_NUC_RAD_LABEL;
        propLabels[MultiThreadedMaximaFinder.BLOB_THRESH] = GianiDefaultParams.BLOB_NUC_NOISE_TOL_LABEL;
        propLabels[MultiThreadedMaximaFinder.HESSIAN_DETECT] = GianiDefaultParams.NUC_MAXIMA_DETECT_HESSIAN;
        propLabels[MultiThreadedMaximaFinder.HESSIAN_STOP_SCALE] = GianiDefaultParams.NUC_MAXIMA_DETECT_HESSIAN_STOP_SCALE;
        propLabels[MultiThreadedMaximaFinder.HESSIAN_START_SCALE] = GianiDefaultParams.NUC_MAXIMA_DETECT_HESSIAN_START_SCALE;
        propLabels[MultiThreadedMaximaFinder.HESSIAN_SCALE_STEP] = GianiDefaultParams.NUC_MAXIMA_DETECT_HESSIAN_SCALE_STEP;
        propLabels[MultiThreadedMaximaFinder.HESSIAN_THRESH] = GianiDefaultParams.NUC_MAXIMA_DETECT_HESSIAN_THRESH;
        propLabels[MultiThreadedMaximaFinder.SERIES_SELECT] = GianiDefaultParams.SERIES_SELECT_LABEL;
        propLabels[MultiThreadedMaximaFinder.EDM_FILTER] = GianiDefaultParams.NUC_MAXIMA_DETECT_HESSIAN_THRESH;
        propLabels[MultiThreadedMaximaFinder.HESSIAN_ABS] = GianiDefaultParams.NUC_MAXIMA_DETECT_HESSIAN_ABS;
        MultiThreadedMaximaFinder process = new MultiThreadedMaximaFinder(null);
        process.setup(new BioFormatsImg(), props, propLabels);
        return process;
    }

    public static MultiThreadedTopHatFilter getDefaultNucTopHatFilteringProcess(Properties props, MultiThreadedProcess[] inputs) {
        String[] propLabels = new String[MultiThreadedTopHatFilter.N_PROP_LABELS];
        propLabels[MultiThreadedTopHatFilter.CHANNEL_LABEL] = GianiDefaultParams.NUC_SEG_CHAN_SELECT_LABEL;
        propLabels[MultiThreadedTopHatFilter.SERIES_LABEL] = GianiDefaultParams.SERIES_SELECT_LABEL;
        propLabels[MultiThreadedTopHatFilter.FILT_RAD_LABEL] = GianiDefaultParams.NUC_TOP_HAT_FILT_RAD_LABEL;
        propLabels[MultiThreadedTopHatFilter.RESIZE_FACTOR_LABEL] = GianiDefaultParams.NUC_TOP_HAT_DOWNSIZE_FACTOR_LABEL;
        MultiThreadedTopHatFilter process = new MultiThreadedTopHatFilter(inputs);
        process.setup(new BioFormatsImg(), props, propLabels);
        return process;
    }

    public static MultiThreadedGaussianFilter getDefaultNucFilteringProcess(Properties props) {
        String[] propLabels = new String[MultiThreadedGaussianFilter.N_PROP_LABELS];
        propLabels[MultiThreadedGaussianFilter.CHANNEL_LABEL] = GianiDefaultParams.NUC_SEG_CHAN_SELECT_LABEL;
        propLabels[MultiThreadedGaussianFilter.SERIES_LABEL] = GianiDefaultParams.SERIES_SELECT_LABEL;
        propLabels[MultiThreadedGaussianFilter.FILT_RAD_LABEL] = GianiDefaultParams.NUC_FILT_RAD_LABEL;
        MultiThreadedGaussianFilter process = new MultiThreadedGaussianFilter(null);
        process.setup(new BioFormatsImg(), props, propLabels);
        return process;
    }

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

    public static MultiThreadedGaussianFilter getDefaultCellFilteringProcess(Properties props) {
        MultiThreadedGaussianFilter process = new MultiThreadedGaussianFilter(null);
        String[] propLabels = new String[MultiThreadedGaussianFilter.N_PROP_LABELS];
        propLabels[MultiThreadedGaussianFilter.CHANNEL_LABEL] = GianiDefaultParams.CELL_SEG_CHAN_SELECT_LABEL;
        propLabels[MultiThreadedGaussianFilter.SERIES_LABEL] = GianiDefaultParams.SERIES_SELECT_LABEL;
        propLabels[MultiThreadedGaussianFilter.FILT_RAD_LABEL] = GianiDefaultParams.CELL_FILT_RAD_LABEL;
        process.setup(new BioFormatsImg(), props, propLabels);
        return process;
    }

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

    public static ProcessPipeline buildFullPipeline(Properties props, Objects3DPopulation cells) {
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
        return pipeline;
    }
}
