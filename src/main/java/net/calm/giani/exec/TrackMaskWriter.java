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
import ij.ImagePlus;
import ij.plugin.PNG_Writer;
import ij.process.ImageProcessor;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Post-processes the per-timepoint Nucleus, Cell and Cytoplasm mask PNGs into
 * parallel {@code <type>_track_masks/} folders, relabelling each pixel from its
 * per-timepoint object label to {@code CellLineageTracker.TRACK_LABEL_OFFSET +
 * trackId} so a tracked cell keeps the same label across timepoints. In the
 * track-labelled masks {@code 0} is background and {@code 1} is reserved for
 * untracked cells, matching the labels in {@code GIANI_tracks.csv}.
 *
 * <p>Only those three object types share the cell label space (the cell
 * watershed is built with {@code remap=true}, and cytoplasm is derived from
 * cell and nucleus). Spot masks ({@code *_Channel_N_Spot_masks}) are labelled
 * by spot index, so applying the cell map to them produced meaningless output
 * before 3.14.10; they are now left alone.
 */
public final class TrackMaskWriter {

    private static final Pattern TIME_FILE_PATTERN = Pattern.compile("_T(\\d+)_Z\\d+\\.png$");

    /** Suffixes of the mask folders whose labels are cell labels. */
    static final List<String> TRACKABLE_MASK_SUFFIXES =
            List.of("_Nucleus_masks", "_Cell_masks", "_Cytoplasm_masks");

    private TrackMaskWriter() {
    }

    /**
     * Remaps every Nucleus, Cell and Cytoplasm mask PNG in {@code outputDir}
     * into a parallel {@code *_track_masks} folder.
     *
     * @param outputDir the output directory holding the {@code *_masks} folders
     * @param tracks    the tracked cell observations
     * @throws IOException if a track mask cannot be written
     */
    public static void write(File outputDir, List<CellLineageTracker.CellTrack> tracks) throws IOException {
        if (tracks == null || tracks.isEmpty() || outputDir == null || !outputDir.isDirectory()) {
            return;
        }
        File[] maskDirs = outputDir.listFiles(f -> f.isDirectory() && isTrackableMaskDir(f.getName()));
        if (maskDirs == null) {
            return;
        }
        for (File maskDir : maskDirs) {
            writeTrackMasks(maskDir, tracks);
        }
    }

    /**
     * @return {@code true} for a Nucleus, Cell or Cytoplasm mask folder; this
     * excludes spot masks and the {@code *_track_masks} folders themselves
     */
    static boolean isTrackableMaskDir(String dirName) {
        return TRACKABLE_MASK_SUFFIXES.stream().anyMatch(dirName::endsWith);
    }

    private static void writeTrackMasks(File maskDir, List<CellLineageTracker.CellTrack> tracks) throws IOException {
        String dirName = maskDir.getName();
        String baseName = dirName.substring(0, dirName.length() - "_masks".length());
        File trackDir = new File(maskDir.getParentFile(), baseName + "_track_masks");
        if (!trackDir.exists() && !trackDir.mkdirs()) {
            throw new IOException("Failed to create track mask directory " + trackDir.getAbsolutePath());
        }
        File[] pngs = maskDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".png"));
        if (pngs == null) {
            return;
        }
        PNG_Writer writer = new PNG_Writer();
        for (File png : pngs) {
            ImagePlus mask = IJ.openImage(png.getAbsolutePath());
            if (mask == null) {
                continue;
            }
            int time = timeFromFileName(png.getName());
            remapPixels(mask.getProcessor(), buildRemap(tracks, time));
            File out = new File(trackDir, png.getName());
            try {
                writer.writeImage(mask, out.getAbsolutePath(), 0);
            } catch (Exception e) {
                throw new IOException("Failed to write track mask " + out.getAbsolutePath(), e);
            }
        }
    }

    /**
     * Extracts the timepoint index from a mask filename ({@code T} is only
     * present when the run spanned multiple timepoints).
     */
    static int timeFromFileName(String fileName) {
        Matcher matcher = TIME_FILE_PATTERN.matcher(fileName);
        return matcher.find() ? Integer.parseInt(matcher.group(1)) : 0;
    }

    /**
     * Builds the per-timepoint label remap from the original mask label
     * ({@code CellTrack.label()}, the nucleus value) to the public track label.
     */
    static Map<Integer, Integer> buildRemap(List<CellLineageTracker.CellTrack> tracks, int time) {
        Map<Integer, Integer> remap = new HashMap<>();
        for (CellLineageTracker.CellTrack track : tracks) {
            if (track.time() == time) {
                remap.put(track.label(), track.trackId() + CellLineageTracker.TRACK_LABEL_OFFSET);
            }
        }
        return remap;
    }

    /**
     * Rewrites the processor's pixels in place: background ({@code 0}) stays
     * {@code 0}, tracked labels map to their public track label, and any other
     * label becomes {@code 1} (untracked).
     */
    static void remapPixels(ImageProcessor ip, Map<Integer, Integer> remap) {
        for (int y = 0; y < ip.getHeight(); y++) {
            for (int x = 0; x < ip.getWidth(); x++) {
                int value = ip.get(x, y);
                if (value == 0) {
                    continue;
                }
                ip.set(x, y, remap.getOrDefault(value, 1));
            }
        }
    }
}
