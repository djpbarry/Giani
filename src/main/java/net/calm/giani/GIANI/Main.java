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

import ij.IJ;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.Scanner;
import mcib3d.geom.Objects3DPopulation;
import net.calm.giani.gianiparams.GianiDefaultParams;
import net.calm.iaclasslibrary.IO.PropertyWriter;
import net.calm.iaclasslibrary.Process.ProcessPipeline;
import net.calm.iaclasslibrary.UtilClasses.GenUtils;
import org.apache.commons.io.FilenameUtils;

/**
 *
 * @author David Barry <david.barry at crick dot ac dot uk>
 */
public class Main {

    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("Insufficient arguments specified.");
            System.exit(0);
        }
        IJ.log(String.format("Job List: %s", args[0]));
        IJ.log(String.format("Properties File: %s", args[1]));
        IJ.log(String.format("Job Number: %s", args[2]));
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
        if (!GianiDefaultParams.setOutputDirectory(props, label)) {
            System.exit(0);
        }
        ProcessPipeline pipeline = PipelineBuilder.buildFullPipeline(props, new Objects3DPopulation());
        PipelineExecutor exec = new PipelineExecutor(pipeline, props);
        exec.run();
        System.exit(0);
    }

    public static String[] getJobDetails(File jobList, int jobNumber) throws FileNotFoundException, IOException {
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
