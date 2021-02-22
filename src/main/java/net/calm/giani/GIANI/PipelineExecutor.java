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
package net.calm.giani.GIANI;

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

import loci.formats.FormatException;
import net.calm.giani.gianiparams.GianiDefaultParams;
import net.calm.iaclasslibrary.IO.BioFormats.BioFormatsFileLister;
import net.calm.iaclasslibrary.IO.BioFormats.BioFormatsImg;
import net.calm.iaclasslibrary.IO.DataWriter;
import net.calm.iaclasslibrary.IO.PropertyWriter;
import net.calm.iaclasslibrary.Process.MultiThreadedProcess;
import net.calm.iaclasslibrary.Process.ProcessPipeline;
import net.calm.iaclasslibrary.TimeAndDate.TimeAndDate;
import net.calm.iaclasslibrary.UtilClasses.GenUtils;
import org.apache.commons.io.FilenameUtils;

/**
 * @author David Barry <david.barry at crick dot ac dot uk>
 */
public class PipelineExecutor extends Thread {

    private final ProcessPipeline pipeline;
    private final Properties props;

    public PipelineExecutor(ProcessPipeline pipeline, Properties props) {
        this.pipeline = pipeline;
        this.props = props;
    }

    public void run() {
        IJ.log(String.format("%s %s", GianiDefaultParams.TITLE, TimeAndDate.getCurrentTimeAndDate()));
        RoiManager rm = RoiManager.getInstance();
        if (rm != null) {
            rm.reset();
        }
        ResultsTable rt = ResultsTable.getResultsTable();
        if (rt != null) {
            rt.reset();
        }
        File input = new File(props.getProperty(GianiDefaultParams.INPUT_DIR_LABEL));
        IJ.log(String.format("Input: %s", input.getAbsolutePath()));
        File outputDir = new File(props.getProperty(GianiDefaultParams.OUTPUT_DIR_LABEL));
        IJ.log(String.format("Output: %s", outputDir.getAbsolutePath()));
        ArrayList<String> files;
        if (input.isDirectory()) {
            files = BioFormatsFileLister.obtainValidFileList(input);
        } else {
            files = new ArrayList();
            files.add(input.getName());
            input = new File(input.getParent());
        }
        ExecutorService exec = Executors.newSingleThreadExecutor();
        for (String file : files) {
            BioFormatsImg img = new BioFormatsImg();
            try {
                img.setId(String.format("%s%s%s", input, File.separator, file));
            } catch (IOException | FormatException e) {
                GenUtils.logError(e, String.format("Failed to initialise %s", file));
            }
            props.put(img.reformatFileName(), file);
            IJ.log(String.format("Analysing file %s", FilenameUtils.getName(file)));
            int nSeries = img.getSeriesCount();
            int s0 = 0;
            try {
                int specificSeries = Integer.parseInt(props.getProperty(GianiDefaultParams.SPECIFIC_SERIES));
                if (specificSeries > -1 && specificSeries < nSeries) {
                    s0 = specificSeries;
                    nSeries = specificSeries + 1;
                } else {
//                    IJ.log(String.format("Specific series is outside range 0 - %d.", nSeries - 1));
                }
            } catch (NumberFormatException e) {
                IJ.log("Specific series not a parsable integer - ignoring.");
            }
            for (int s = s0; s < nSeries; s++) {
                IJ.log(String.format("Analysing series %d", s));
                if (img.getSizeZ(s) < 2) {
                    IJ.log("Only 2 dimensions - skipping.");
                    continue;
                }
                props.setProperty(GianiDefaultParams.SERIES_SELECT_LABEL, String.valueOf(s));
                for (MultiThreadedProcess process : pipeline) {
                    if (process != null) {
                        IJ.log(String.format("%s Process %s", TimeAndDate.getCurrentTimeAndDate(), process.getClass().toString()));
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
            DataWriter.saveResultsTable(Analyzer.getResultsTable(), new File(String.format("%s%s%s_Output.csv", outputDir, File.separator, GianiDefaultParams.TITLE)));
        } catch (IOException e) {
            GenUtils.logError(e, "Failed to save results file.");
        }
        try {
            PropertyWriter.saveProperties(props, outputDir.getAbsolutePath(), GianiDefaultParams.TITLE, true);
        } catch (Exception e) {
            GenUtils.logError(e, "Failed to save property file.");
        }
        IJ.log(String.format("Done %s", TimeAndDate.getCurrentTimeAndDate()));
    }

    boolean checkSeriesChannels(BioFormatsImg img) {
        return true;
    }

}
