/*
 * Copyright (C) 2026 David Barry <david.barry at crick dot ac dot uk>
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

import mcib3d.geom.Objects3DPopulation;
import net.calm.giani.gianiparams.GianiDefaultParams;
import net.calm.iaclasslibrary.Process.ProcessPipeline;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * An input file that Bio-Formats cannot open must be skipped, not abort the
 * run: previously {@code updateFileList} threw an {@code IllegalStateException}
 * on the uninitialised reader, escaping {@code run()} before any output
 * (results CSV, {@code properties.xml}) was written.
 */
class PipelineExecutorUnreadableInputTest {

    @Test
    @Timeout(value = 2, unit = TimeUnit.MINUTES)
    void unreadableSingleFileIsSkippedAndRunCompletes(@TempDir Path tmp) throws IOException {
        System.setProperty("java.awt.headless", "true");
        File corrupt = tmp.resolve("corrupt.tif").toFile();
        Files.writeString(corrupt.toPath(), "not an image");
        Path outputDir = Files.createDirectory(tmp.resolve("out"));

        GianiDefaultParams props = new GianiDefaultParams();
        props.setProperty(GianiDefaultParams.INPUT_DIR_LABEL, corrupt.getAbsolutePath());
        props.setProperty(GianiDefaultParams.OUTPUT_DIR_LABEL, outputDir.toString());

        PipelineExecutor executor = new PipelineExecutor(new ProcessPipeline(), props, new Objects3DPopulation());
        assertDoesNotThrow(executor::run);

        File properties = outputDir.resolve("properties.xml").toFile();
        assertTrue(properties.isFile(), "run should complete and save properties: " + properties);
    }
}
