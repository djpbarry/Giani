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
import ij.plugin.PlugIn;
import loci.common.DebugTools;
import mcib3d.geom.Objects3DPopulation;
import net.calm.giani.gianiparams.GianiDefaultParams;
import net.calm.giani.ui.GIANIUI;
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
public class GIANI implements PlugIn {

    /** Process exit code for a run that completed. */
    static final int EXIT_SUCCESS = 0;

    /**
     * Process exit code for any failure — bad arguments, an unreadable
     * properties or job-list file, or a pipeline that did not complete.
     *
     * <p>Before 3.13.14 every headless path exited {@code 0}, so HPC schedulers
     * reported failed jobs as successful. Per-file errors inside a directory
     * batch are still logged-and-skipped (deliberate, so one bad image does not
     * abort the batch) and do not on their own make the run fail.
     */
    static final int EXIT_FAILURE = 1;

    /**
     * Launches the interactive wizard. Invoked from the Fiji Plugins menu via
     * {@code plugins.config}.
     *
     * @param arg unused; GIANI always opens the GUI when launched interactively
     */
    @Override
    public void run(String arg) {
        GIANIUI ui = new GIANIUI();
        ui.setVisible(true);
    }

    /**
     * Run GIANI using the specified list of arguments
     *
     * <p>Exits {@link #EXIT_SUCCESS} if the run completed and
     * {@link #EXIT_FAILURE} otherwise, so batch schedulers can detect a failed
     * job.
     *
     * @param args see https://giani-docs.readthedocs.io/ (cluster section)
     */
    public static void main(String[] args) {
        if (args == null || args.length < 1) {
            GIANIUI ui = new GIANIUI();
            ui.setVisible(true);
        } else {
            System.exit(runHeadless(args));
        }
    }

    /**
     * Headless entry point, split out of {@link #main} so the exit status can be
     * asserted in tests without killing the JVM.
     *
     * @param args the command-line arguments
     * @return {@link #EXIT_SUCCESS} or {@link #EXIT_FAILURE}
     */
    static int runHeadless(String[] args) {
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
                    case "-f" -> file = new File(args[i + 1]);
                    case "-o" -> output = new File(args[i + 1]);
                    case "-l" -> list = new File(args[i + 1]);
                    case "-p" -> props = new File(args[i + 1]);
                    case "-n" -> jobNumber = Integer.parseInt(args[i + 1]);
                    case "-s" -> series = Integer.parseInt(args[i + 1]);
                    case "-h" -> IJ.log("For instructions on how to run GIANI from a command line or shell, " +
                            "see here: https://giani-docs.readthedocs.io/");
                    default -> {
                    }
                }
            }
        } catch (NumberFormatException e) {
            GenUtils.logError(e, "Invalid numeric argument - using default value.");
        } catch (NullPointerException e) {
            GenUtils.logError(e, "Invalid filename - aborting.");
            return EXIT_FAILURE;
        }
        if (props == null) {
            IJ.log("No valid properties file specified - exiting.");
            return EXIT_FAILURE;
        } else if (list == null && file == null) {
            IJ.log("No valid input file or file list specified - exiting.");
            return EXIT_FAILURE;
        }
        if (list != null && jobNumber < 0) {
            IJ.log("Invalid job number specified - exiting.");
            return EXIT_FAILURE;
        }
        return GIANI.init(props, list, file, jobNumber, series, output)
                ? EXIT_SUCCESS : EXIT_FAILURE;
    }

    /**
     * @return {@code true} if the run completed, {@code false} on any failure
     */
    private static boolean init(File inputProps, File jobListFile, File file, int jobNumber, int series, File outputDir) {
        IJ.log(String.format("Properties File: %s", inputProps.getAbsolutePath()));
        if (jobListFile != null) {
            IJ.log(String.format("Job List: %s", jobListFile.getAbsolutePath()));
            IJ.log(String.format("Job Number: %d", jobNumber));
        } else IJ.log(String.format("Input File: %s", file));
        System.setProperty("java.awt.headless", "true");
        Properties props = new GianiDefaultParams();
        try {
            GianiDefaultParams.loadProperties(props, null, inputProps);
        } catch (Exception e) {
            // Continuing here would silently run with default parameters rather
            // than the user's, producing plausible-looking but wrong output.
            GenUtils.logError(e, "Failed to load properties file - aborting.");
            return false;
        }
        String[] jobDetails;
        if (jobListFile != null) {
            if (!jobListFile.exists()) {
                IJ.log(String.format("%s is not a valid job list file - exiting.", jobListFile.getAbsolutePath()));
                return false;
            }
            try {
                jobDetails = getJobDetails(jobListFile, jobNumber);
                file = new File(jobDetails[0]);
                series = Integer.parseInt(jobDetails[1]);
            } catch (FileNotFoundException e) {
                GenUtils.logError(e, "Job list file not found - aborting.");
                return false;
            } catch (IOException | NullPointerException e) {
                // NullPointerException covers getJobDetails returning null, i.e.
                // the job number is absent from the list.
                GenUtils.logError(e, "Could not read job list file - aborting.");
                return false;
            }
        }
        props.setProperty(GianiDefaultParams.INPUT_DIR_LABEL, file.getAbsolutePath());
        props.setProperty(GianiDefaultParams.SPECIFIC_SERIES, String.valueOf(series));
        String label = String.format("%s_S%s", FilenameUtils.getName(file.getAbsolutePath()), series);
        if (outputDir == null) {
            outputDir = new File(props.getProperty(GianiDefaultParams.INPUT_DIR_LABEL));
        }
        if (!GianiDefaultParams.setOutputDirectory(props, label, outputDir.getAbsolutePath())) {
            IJ.log("Could not create the output directory - aborting.");
            return false;
        }
        IJ.log(String.format("Output Directory: %s", props.getProperty(GianiDefaultParams.OUTPUT_DIR_LABEL)));
        return (new GIANI()).run(props);
    }

    /**
     * Run GIANI using the specified properties.
     *
     * <p>{@link PipelineExecutor} is a {@link Thread}, so an exception escaping
     * its {@code run()} only reaches the default handler — {@link Thread#join()}
     * then returns normally and the caller cannot tell the pipeline died. An
     * uncaught-exception handler is installed to capture that.
     *
     * @param props contains parameter name-value pairs for all parameters necessary to run GIANI
     * @return {@code true} if the pipeline completed without an uncaught error
     */
    public boolean run(Properties props) {
        DebugTools.setRootLevel("WARN");
        Objects3DPopulation cells = new Objects3DPopulation();
        ProcessPipeline pipeline = (new PipelineBuilder()).buildFullPipeline(props, cells);
        PipelineExecutor exec = new PipelineExecutor(pipeline, props, cells);
        Throwable[] uncaught = new Throwable[1];
        exec.setUncaughtExceptionHandler((thread, throwable) -> uncaught[0] = throwable);
        exec.start();
        try {
            exec.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            GenUtils.logError(e, "Pipeline execution interrupted - aborting.");
            return false;
        }
        if (uncaught[0] != null) {
            // GenUtils.logError takes an Exception, but what escapes may be an
            // Error — OutOfMemoryError is realistic here, given the 3D
            // segmentation working set — so wrap rather than narrow.
            Throwable t = uncaught[0];
            GenUtils.logError(t instanceof Exception e ? e : new RuntimeException(t),
                    "Pipeline execution failed.");
            return false;
        }
        return true;
    }

    private static String[] getJobDetails(File jobList, int jobNumber) throws FileNotFoundException, IOException {
        try (BufferedReader fileListReader = new BufferedReader(new FileReader(jobList))) {
            String line;
            while ((line = fileListReader.readLine()) != null) {
                Scanner scanner = new Scanner(line);
                scanner.useDelimiter(",");
                if (jobNumber == Integer.parseInt(scanner.next())) {
                    return new String[]{scanner.next().trim(), scanner.next().trim()};
                }
            }
        }
        return null;
    }
}
