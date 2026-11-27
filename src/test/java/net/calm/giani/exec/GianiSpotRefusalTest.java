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

import net.calm.giani.gianiparams.GianiDefaultParams;
import net.calm.iaclasslibrary.Extrema.MultiThreadedMaximaFinder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.util.concurrent.Callable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A headless run whose spot settings cannot give meaningful data (a simple-
 * detector radius of 0 makes every voxel a "spot") is refused up front, with a
 * logged error and a non-zero exit, before any pipeline is built or run.
 */
class GianiSpotRefusalTest {

    private static final String RADIUS_KEY =
            PipelineBuilder.getFociPropLabels(0)[MultiThreadedMaximaFinder.BLOB_SIZE];

    /** Spot localisation on for channel 0, with the given blob radius. */
    private static Properties spotRunWithRadius(String radius) {
        Properties props = new GianiDefaultParams();
        props.setProperty(GianiDefaultParams.LOCALISE_SPOTS, "true");
        props.setProperty(GianiDefaultParams.CHAN_FOR_MEASURE, "1");
        PipelineBuilder.setDefaultFociParams(props, 0);
        props.setProperty(RADIUS_KEY, radius);
        return props;
    }

    /** Runs {@code action} and returns what it wrote to standard output (where IJ.log goes headless). */
    private static <T> Captured<T> capturingStdout(Callable<T> action) throws Exception {
        PrintStream original = System.out;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        System.setOut(new PrintStream(buffer, true, StandardCharsets.UTF_8));
        try {
            T result = action.call();
            return new Captured<>(result, buffer.toString(StandardCharsets.UTF_8));
        } finally {
            System.setOut(original);
        }
    }

    private record Captured<T>(T result, String stdout) {
    }

    @Test
    void runRefusesAZeroSpotRadiusAndSaysWhy(@TempDir Path dir) throws Exception {
        Properties props = spotRunWithRadius("0.0");
        // Safety net: the default input directory is the user's home folder. If the
        // refusal ever regressed, run() would carry on and scan it, so point both
        // directories at empty temp folders and let that failure be harmless.
        props.setProperty(GianiDefaultParams.INPUT_DIR_LABEL, Files.createDirectory(dir.resolve("in")).toString());
        props.setProperty(GianiDefaultParams.OUTPUT_DIR_LABEL, Files.createDirectory(dir.resolve("out")).toString());

        Captured<Boolean> run = capturingStdout(() -> new GIANI().run(props));

        assertFalse(run.result(), "a run that cannot give meaningful spot data must report failure");
        assertTrue(run.stdout().contains("ERROR: Spot detection in channel 0"), run.stdout());
        assertTrue(run.stdout().contains(RADIUS_KEY), run.stdout());
    }

    @Test
    void theCommandLineExitsNonZeroForAZeroSpotRadiusBeforeAnyAnalysis(@TempDir Path dir) throws Exception {
        File props = dir.resolve("properties.xml").toFile();
        try (var out = Files.newOutputStream(props.toPath())) {
            spotRunWithRadius("0.0").storeToXML(out, null);
        }
        // The input never has to be readable: the refusal comes before any image is opened.
        File image = dir.resolve("image.tiff").toFile();
        Files.writeString(image.toPath(), "not an image", StandardCharsets.UTF_8);
        File outputDir = Files.createDirectory(dir.resolve("out")).toFile();

        Captured<Integer> run = capturingStdout(() -> GIANI.runHeadless(new String[]{
                "-p", props.getAbsolutePath(), "-f", image.getAbsolutePath(), "-o", outputDir.getAbsolutePath()}));

        assertEquals(GIANI.EXIT_FAILURE, run.result());
        assertTrue(run.stdout().contains("ERROR: Spot detection in channel 0"),
                "the exit status should come from the spot refusal, not some other failure: " + run.stdout());
    }
}
