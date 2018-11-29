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
package core;

import IO.BioFormats.BioFormatsImg;
import IO.DataWriter;
import Process.MultiThreadedProcess;
import Process.ProcessPipeline;
import UtilClasses.GenUtils;
import ij.IJ;
import ij.ImagePlus;
import ij.plugin.filter.Analyzer;
import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import params.DefaultParams;

/**
 *
 * @author David Barry <david.barry at crick dot ac dot uk>
 */
public class LocalMapperExecutor extends Thread {

    private final ProcessPipeline pipeline;
    private final Properties props;

    public LocalMapperExecutor(ProcessPipeline pipeline, Properties props) {
        this.pipeline = pipeline;
        this.props = props;
    }

    public void run() {
        File inputDir = new File(props.getProperty(DefaultParams.INPUT_DIR_LABEL));
        IJ.log(String.format("Input: %s", inputDir.getAbsolutePath()));
        File outputDir = new File(GenUtils.openResultsDirectory(String.format("%s%s%s", inputDir.getAbsolutePath(), File.separator, "Local_Mapper")));
        IJ.log(String.format("Output: %s", outputDir.getAbsolutePath()));
        File[] files = inputDir.listFiles();
        ExecutorService exec = Executors.newSingleThreadExecutor();
        for (File file : files) {
            BioFormatsImg img = new BioFormatsImg(file.getAbsolutePath());
            if (!img.isValidID()) {
                IJ.log(String.format("Skipping %s", file.getName()));
                continue;
            }
            IJ.log(String.format("Analysing file %s", file.getName()));
            int nSeries = img.getSeriesCount();
            ImagePlus output = null;
            for (int s = 0; s < nSeries; s++) {
                IJ.log(String.format("Analysing series %d", s));
                if (img.getZSpatialRes(s) == null) {
                    IJ.log("Only 2 dimensions - skipping.");
                    continue;
                }
                props.setProperty(DefaultParams.SERIES_SELECT_LABEL, String.valueOf(s));
                int p=0;
                for (MultiThreadedProcess process : pipeline) {
                    if (process != null) {
                        IJ.log(String.format("Process %s", process.getClass().toString()));
                        try {
                            process.setup(img, props, process.getPropLabels());
                            Future f = exec.submit(process, img);
                            f.get();
                        } catch (InterruptedException | ExecutionException e) {
                            GenUtils.logError(e, String.format("Failed to execute process %s", process.getClass().toString()));
                        }
                        output = process.getOutput();
                        IJ.saveAs(output, "TIF", String.format("%s%s%s_Process_%d_Output", outputDir, File.separator, file.getName(), p++));
                    }
                }
                IJ.log("Processing complete.");
                if (output != null) {
                    IJ.saveAs(output, "TIF", String.format("%s%s%s_Series_%d_Output", outputDir, File.separator, file.getName(), s));
                }
            }
        }
        try {
            DataWriter.saveResultsTable(Analyzer.getResultsTable(), new File(String.format("%s%sLocalMapper_Output", outputDir, File.separator)));
        } catch (IOException e) {
            GenUtils.logError(e, "Failed to save results file.");
        }
    }

}
