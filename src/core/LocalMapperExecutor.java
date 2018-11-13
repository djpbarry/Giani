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
import Process.MultiThreadedProcess;
import Process.ProcessPipeline;
import UtilClasses.GenUtils;
import ij.IJ;
import ij.ImagePlus;
import java.io.File;
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
public class LocalMapperExecutor {

    private final ProcessPipeline pipeline;
    private final Properties props;

    public LocalMapperExecutor(ProcessPipeline pipeline, Properties props) {
        this.pipeline = pipeline;
        this.props = props;
    }

    public void executePipeline() {
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
            for (int s = 0; s < nSeries; s++) {
                IJ.log(String.format("Analysing series %d", s));
                img.loadPixelData(s);
                for (MultiThreadedProcess process : pipeline) {
                    if (process != null) {
                        IJ.log(String.format("Process %s", process.getClass().toString()));
                        try {
                            Future f = exec.submit(process, img);
                            f.get();
                        } catch (InterruptedException | ExecutionException e) {
                            GenUtils.logError(e, String.format("Failed to execute process %s", process.getClass().toString()));
                        }
                    }
                }
                IJ.log("Processing complete.");
                ImagePlus imp = img.getProcessedImage();
                IJ.saveAs(imp, "TIF", String.format("%s%s%s_Series_%d_Output", outputDir, File.separator, file.getName(), s));
            }
        }
    }

}
