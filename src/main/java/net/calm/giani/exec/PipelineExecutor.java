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
package net.calm.giani.exec;

import ij.IJ;
import ij.measure.ResultsTable;
import ij.plugin.filter.Analyzer;
import ij.plugin.frame.RoiManager;
import loci.common.DebugTools;
import loci.formats.FormatException;
import mcib3d.geom.Objects3DPopulation;
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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Runs a GIANI {@link ProcessPipeline}
 *
 * @author Dave Barry
 * @since 1.0.0
 */
public class PipelineExecutor extends Thread {

    private final ProcessPipeline pipeline;
    private final Properties props;
    private final Objects3DPopulation cells;
    private final List<CellLineageTracker.CellTrack> lineageTracks = new ArrayList<>();
    private final Map<String, Integer> trackIdByCell = new HashMap<>();

    /**
     * Construct an executor with the specified pipeline, properties, and the
     * shared cell population that the segmentation stage fills.
     * @param pipeline the {@link ProcessPipeline} to be executed
     * @param props the parameter-value pairs for every process in the pipeline
     * @param cells the {@link Objects3DPopulation} that stores the segmentations
     */
    public PipelineExecutor(ProcessPipeline pipeline, Properties props, Objects3DPopulation cells) {
        this.pipeline = pipeline;
        this.props = props;
        this.cells = cells;
    }

    /**
     * Execute the pipeline
     */
    public void run() {
        DebugTools.setRootLevel("WARN");
        IJ.log(String.format("Start %s", TimeAndDate.getCurrentTimeAndDate()));
        IJ.log(String.format("Number of Processors: %s", Runtime.getRuntime().availableProcessors()));
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
            files = new ArrayList<>();
            files.add(input.getName());
            input = new File(input.getParent());
        }
        ExecutorService exec = Executors.newSingleThreadExecutor();
        boolean trackTimelapse = Boolean.parseBoolean(props.getProperty(GianiDefaultParams.TRACK_TIMELAPSE));
        boolean trackLineage = Boolean.parseBoolean(props.getProperty(GianiDefaultParams.TRACK_LINEAGE));
        int timeSelect = -1;
        try {
            timeSelect = Integer.parseInt(props.getProperty(GianiDefaultParams.TIME_SELECT));
        } catch (NumberFormatException e) {
            IJ.log("Specific timepoint not a parsable integer - ignoring.");
        }
        while (files.size() > 0) {
            String file = files.get(0);
            try (BioFormatsImg img = new BioFormatsImg()) {
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
                    props.setProperty(GianiDefaultParams.UNITS, img.getUnits(s));
                    CellLineageTracker lineageTracker = trackLineage ? new CellLineageTracker() : null;
                    int[] tRange = timeRange(img.getSizeT(s), timeSelect, trackTimelapse);
                    for (int t = tRange[0]; t < tRange[1]; t++) {
                        IJ.log(String.format("Analysing timepoint %d", t));
                        for (MultiThreadedProcess process : pipeline) {
                            if (process != null) {
                                IJ.log(String.format("%s Process %s", TimeAndDate.getCurrentTimeAndDate(), process.getClass().toString()));
                                try {
                                    process.setTime(t);
                                    process.setup(img, props, process.getPropLabels());
                                    Future f = exec.submit(process, img);
                                    f.get();
                                } catch (InterruptedException | ExecutionException e) {
                                    GenUtils.logError(e, String.format("Failed to execute process %s", process.getClass().toString()));
                                }
                            }
                        }
                        if (lineageTracker != null) {
                            lineageTracker.addTimepoint(t, cells);
                        }
                    }
                    if (lineageTracker != null) {
                        List<CellLineageTracker.CellTrack> tracks = lineageTracker.track();
                        lineageTracks.addAll(tracks);
                        for (CellLineageTracker.CellTrack track : tracks) {
                            trackIdByCell.put(trackKey(s, track.time(), track.label()), track.trackId() + CellLineageTracker.TRACK_LABEL_OFFSET);
                        }
                        IJ.log(String.format("Tracked %d cell observations across %d lineages", tracks.size(),
                                tracks.stream().map(CellLineageTracker.CellTrack::trackId).distinct().count()));
                    }
                }
                updateFileList(img, files);
            } catch (IOException e) {
                GenUtils.logError(e, String.format("Failed to close image reader for %s", file));
            }
        }
        exec.shutdown();
        if (!trackIdByCell.isEmpty()) {
            annotateTrackIds(Analyzer.getResultsTable(), trackIdByCell);
        }
        try {
            DataWriter.saveResultsTable(Analyzer.getResultsTable(), new File(String.format("%s%s%s_Output.csv", outputDir, File.separator, GianiDefaultParams.TITLE)), false, true);
        } catch (IOException e) {
            GenUtils.logError(e, "Failed to save results file.");
        }
        try {
            UltrackCsvWriter.write(outputDir, lineageTracks);
        } catch (IOException e) {
            GenUtils.logError(e, "Failed to save tracking CSV.");
        }
        try {
            TrackMaskWriter.write(outputDir, lineageTracks);
        } catch (IOException e) {
            GenUtils.logError(e, "Failed to write track-labelled masks.");
        }
        try {
            PropertyWriter.saveProperties(props, outputDir.getAbsolutePath(), GianiDefaultParams.TITLE, true);
        } catch (Exception e) {
            GenUtils.logError(e, "Failed to save property file.");
        }
        IJ.log(String.format("Done %s", TimeAndDate.getCurrentTimeAndDate()));
    }

    private void updateFileList(BioFormatsImg img, ArrayList<String> files) {
        String[] relatedFiles = img.getFileList();
        for (String rf : relatedFiles) {
            for (String f : files) {
                if (f.equalsIgnoreCase(FilenameUtils.getName(rf))) {
                    files.remove(f);
                    break;
                }
            }
        }
    }

    /**
     * Resolves the inclusive-start / exclusive-end timepoint range for a series.
     * A valid specific timepoint wins; otherwise timelapse mode spans all frames;
     * otherwise a single frame ({@code t = 0}) is processed.
     */
    static int[] timeRange(int nTime, int timeSelect, boolean trackTimelapse) {
        if (timeSelect >= 0 && timeSelect < nTime) {
            return new int[]{timeSelect, timeSelect + 1};
        }
        if (trackTimelapse && nTime > 0) {
            return new int[]{0, nTime};
        }
        return new int[]{0, 1};
    }

    static String trackKey(int series, int time, int cellIndex) {
        return series + ":" + time + ":" + cellIndex;
    }

    static void annotateTrackIds(ResultsTable rt, Map<String, Integer> trackIdByCell) {
        for (int row = 0; row < rt.getCounter(); row++) {
            int series = (int) rt.getValue("Series", row);
            int time = (int) rt.getValue("Time", row);
            int cellIndex = (int) rt.getValue("Cell_Index", row);
            rt.setValue("Track ID", row, trackIdByCell.getOrDefault(trackKey(series, time, cellIndex), 0));
        }
    }

}
