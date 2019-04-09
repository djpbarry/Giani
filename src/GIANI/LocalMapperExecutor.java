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
package GIANI;

import IO.BioFormats.BioFormatsFileLister;
import IO.BioFormats.BioFormatsImg;
import IO.DataWriter;
import Process.MultiThreadedProcess;
import Process.ProcessPipeline;
import UtilClasses.GenUtils;
import ij.IJ;
import ij.measure.ResultsTable;
import ij.plugin.filter.Analyzer;
import ij.plugin.frame.RoiManager;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.apache.commons.io.FilenameUtils;
import params.DefaultParams;
import ui.GIANIUI;

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
        RoiManager rm = RoiManager.getInstance();
        if (rm != null) {
            rm.reset();
        }
        ResultsTable rt = ResultsTable.getResultsTable();
        if (rt != null) {
            rt.reset();
        }
        File inputDir = new File(props.getProperty(DefaultParams.INPUT_DIR_LABEL));
        IJ.log(String.format("Input: %s", inputDir.getAbsolutePath()));
        File outputDir = new File(props.getProperty(DefaultParams.OUTPUT_DIR_LABEL));
        IJ.log(String.format("Output: %s", outputDir.getAbsolutePath()));
        ArrayList<String> files = BioFormatsFileLister.obtainValidFileList(inputDir);
        ExecutorService exec = Executors.newSingleThreadExecutor();
        for (String file : files) {
            BioFormatsImg img = new BioFormatsImg();
            img.setId(file);
            IJ.log(String.format("Analysing file %s", FilenameUtils.getName(file)));
            int nSeries = img.getSeriesCount();
            for (int s = 0; s < nSeries; s++) {
                IJ.log(String.format("Analysing series %d", s));
                if (img.getZSpatialRes(s) == null) {
                    IJ.log("Only 2 dimensions - skipping.");
                    continue;
                }
                props.setProperty(DefaultParams.SERIES_SELECT_LABEL, String.valueOf(s));
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
                    }
                }
            }
        }
        try {
            DataWriter.saveResultsTable(Analyzer.getResultsTable(), new File(String.format("%s%s%s_Output.csv", outputDir, File.separator, GIANIUI.TITLE)));
        } catch (IOException e) {
            GenUtils.logError(e, "Failed to save results file.");
        }
        IJ.log("Done");
    }

    boolean checkSeriesChannels(BioFormatsImg img) {
        return true;
    }

}
