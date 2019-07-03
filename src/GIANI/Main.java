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
package GIANI;

import IO.PropertyWriter;
import Process.ProcessPipeline;
import UtilClasses.GenUtils;
import gianiparams.GianiDefaultParams;
import java.io.File;
import java.util.Properties;
import mcib3d.geom.Objects3DPopulation;

/**
 *
 * @author David Barry <david.barry at crick dot ac dot uk>
 */
public class Main {

    public static void main(String[] args) {
        System.setProperty("java.awt.headless", "true");
        Properties props = new GianiDefaultParams();
        try {
            PropertyWriter.loadProperties(props, null, new File(args[1]));
        } catch (Exception e) {
            GenUtils.logError(e, "Failed to load properties file.");
        }
        props.setProperty(GianiDefaultParams.INPUT_DIR_LABEL, args[0]);
        GianiDefaultParams.setOutputDirectory(props);
        ProcessPipeline pipeline = PipelineBuilder.buildFullPipeline(props, new Objects3DPopulation());
        PipelineExecutor exec = new PipelineExecutor(pipeline, props);
        exec.run();
        System.exit(0);
    }
}
