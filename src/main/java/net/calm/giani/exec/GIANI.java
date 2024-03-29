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

import ij.IJ;
import loci.common.DebugTools;
import mcib3d.geom.Objects3DPopulation;
import net.calm.giani.gianiparams.GianiDefaultParams;
import net.calm.giani.ui.GIANIUI;
import net.calm.iaclasslibrary.IO.PropertyWriter;
import net.calm.iaclasslibrary.Process.ProcessPipeline;
import net.calm.iaclasslibrary.UtilClasses.GenUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.*;
import java.util.Properties;
import java.util.Scanner;

/**
 * Main class for running GIANI from the command line
 *
 * @author Dave Barry
 * @since 1.0.0
 */
public class GIANI {

    /**
     * Run GIANI using the specified list of arguments
     *
     * @param args see https://github.com/djpbarry/Giani/wiki/Running-GIANI-on-a-HPC-Cluster
     */
    public static void main(String[] args) {
        if (args == null || args.length < 1) {
            GIANIUI ui = new GIANIUI();
            ui.setVisible(true);
        } else {
            GIANI.init(args);
            System.exit(0);
        }
    }
//    public static void main(String[] args) {
//        try {
//            ImporterOptions io = new ImporterOptions();
//            io.setCBegin(0, 0);
//            io.setCEnd(0, 0);
//            io.setCStep(0, 1);
//            io.setSpecifyRanges(true);
//            io.setId("C:/Users/davej/ilastikTemp/ilastik_temp.ilastik.tiff");
//            ImagePlus output = BF.openImagePlus(io)[0];
//            output.show();
//        } catch (IOException | FormatException e) {
//
//        }
//        //System.exit(0);
//    }

    private static void init(String[] args) {
        if (args.length < 3) {
            System.out.println("Insufficient arguments specified.");
            System.exit(0);
        }
        IJ.log(String.format("Job List: %s", args[0]));
        IJ.log(String.format("Properties File: %s", args[1]));
        IJ.log(String.format("Job Number: %s", args[2]));
//        if (args.length > 3) {
//            if (new File(args[3]).exists()) {
//                IJ.log(String.format("Output Directory: %s", args[3]));
//            } else {
//                IJ.log(String.format("%s is not a valid file path.", args[3]));
//            }
//        }
        System.setProperty("java.awt.headless", "true");
        Properties props = new GianiDefaultParams();
        try {
            PropertyWriter.loadProperties(props, null, new File(args[1]));
        } catch (Exception e) {
            GenUtils.logError(e, "Failed to load properties file.");
        }
        File jobListFile = new File(args[0]);
        if (!jobListFile.exists()) {
            IJ.log(String.format("%s is not a valid job list file - skipping.", args[0]));
        }
        String[] jobDetails;
        try {
            jobDetails = getJobDetails(jobListFile, Integer.parseInt(args[2]));
        } catch (FileNotFoundException e) {
            GenUtils.logError(e, "Job list file not found - aborting.");
            return;
        } catch (IOException e) {
            GenUtils.logError(e, "Could not read job list file - aborting.");
            return;
        }
        props.setProperty(GianiDefaultParams.INPUT_DIR_LABEL, jobDetails[0]);
        props.setProperty(GianiDefaultParams.SPECIFIC_SERIES, jobDetails[1]);
        String label = String.format("%s_S%s", FilenameUtils.getName(jobDetails[0]), jobDetails[1]);
        String outputDir;
        if (args.length > 3) {
            outputDir = args[3];
        } else {
            outputDir = props.getProperty(GianiDefaultParams.INPUT_DIR_LABEL);
        }
        if (!GianiDefaultParams.setOutputDirectory(props, label, outputDir)) {
            System.exit(0);
        }
        IJ.log(String.format("Output Directory: %s", props.getProperty(GianiDefaultParams.OUTPUT_DIR_LABEL)));
        (new GIANI()).run(props);
    }

    /**
     * Run GIANI using the specified properties
     *
     * @param props contains parameter name-value pairs for all parameters necessary to run GIANI
     */
    public void run(Properties props) {
        DebugTools.setRootLevel("WARN");
        ProcessPipeline pipeline = (new PipelineBuilder()).buildFullPipeline(props, new Objects3DPopulation());
        PipelineExecutor exec = new PipelineExecutor(pipeline, props);
        exec.start();
        try {
            exec.join();
        } catch (InterruptedException e) {
            GenUtils.logError(e, "Pipeline execution interrupted - aborting.");
        }
    }

    private static String[] getJobDetails(File jobList, int jobNumber) throws FileNotFoundException, IOException {
        BufferedReader fileListReader = new BufferedReader(new FileReader(jobList));
        while (fileListReader.ready()) {
            String line = fileListReader.readLine();
            Scanner scanner = new Scanner(line);
            scanner.useDelimiter(",");
            if (jobNumber == Integer.parseInt(scanner.next())) {
                return new String[]{scanner.next().trim(), scanner.next().trim()};
            }
        }
        return null;
    }
}
