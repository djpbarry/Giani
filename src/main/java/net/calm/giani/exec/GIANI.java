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
            System.setProperty("java.awt.headless", "true");
            File file = null;
            File list = null;
            File props = null;
            File output = null;
            int jobNumber = -1;
            int series = 0;
            try {
                for (int i = 0; i < args.length - 1; i++) {
                    switch (args[i]) {
                        case "-f":
                            file = new File(args[i + 1]);
                            break;
                        case "-o":
                            output = new File(args[i + 1]);
                            break;
                        case "-l":
                            list = new File(args[i + 1]);
                            break;
                        case "-p":
                            props = new File(args[i + 1]);
                            break;
                        case "-n":
                            jobNumber = Integer.parseInt(args[i + 1]);
                        case "-s":
                            series = Integer.parseInt(args[i + 1]);
                            break;
                        case "-h":
                            System.out.println("For instructions on how to run GIANI from a command line or shell, " +
                                    "see here: https://github.com/djpbarry/Giani/wiki/Running-GIANI-on-a-HPC-Cluster");
                        default:

                    }
                }
            } catch (NumberFormatException e) {
                GenUtils.logError(e, "Invalid numeric argument - using default value.");
            } catch (NullPointerException e) {
                GenUtils.logError(e, "Invalid filename - aborting.");
            }
            if (props == null) {
                System.out.println("No valid properties file specified - exiting.");
                System.exit(0);
            } else if (list == null && file == null) {
                System.out.println("No valid input file or file list specified - exiting.");
                System.exit(0);
            }
            if (list != null && jobNumber < 0) {
                System.out.println("Invalid job number specified - exiting.");
                System.exit(0);
            }
            GIANI.init(props, list, file, jobNumber, series, output);
            System.exit(0);
        }
    }

    private static void init(File inputProps, File jobListFile, File file, int jobNumber, int series, File outputDir) {
        IJ.log(String.format("Properties File: %s", inputProps.getAbsolutePath()));
        if (jobListFile != null) {
            IJ.log(String.format("Job List: %s", jobListFile.getAbsolutePath()));
            IJ.log(String.format("Job Number: %d", jobNumber));
        } else IJ.log(String.format("Input File: %s", file));
        System.setProperty("java.awt.headless", "true");
        Properties props = new GianiDefaultParams();
        try {
            PropertyWriter.loadProperties(props, null, inputProps);
        } catch (Exception e) {
            GenUtils.logError(e, "Failed to load properties file.");
        }
        String[] jobDetails;
        if (jobListFile != null) {
            if (!jobListFile.exists()) {
                IJ.log(String.format("%s is not a valid job list file - exiting.", jobListFile.getAbsolutePath()));
                return;
            }
            try {
                jobDetails = getJobDetails(jobListFile, jobNumber);
                file = new File(jobDetails[0]);
                series = Integer.parseInt(jobDetails[1]);
            } catch (FileNotFoundException e) {
                GenUtils.logError(e, "Job list file not found - aborting.");
                return;
            } catch (IOException | NullPointerException e) {
                GenUtils.logError(e, "Could not read job list file - aborting.");
                return;
            }
        }
        props.setProperty(GianiDefaultParams.INPUT_DIR_LABEL, file.getAbsolutePath());
        props.setProperty(GianiDefaultParams.SPECIFIC_SERIES, String.valueOf(series));
        String label = String.format("%s_S%s", FilenameUtils.getName(file.getAbsolutePath()), series);
        if (outputDir == null) {
            outputDir = new File(props.getProperty(GianiDefaultParams.INPUT_DIR_LABEL));
        }
        if (!GianiDefaultParams.setOutputDirectory(props, label, outputDir.getAbsolutePath())) {
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
