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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Every headless failure path used to exit {@code 0}, so HPC schedulers
 * reported failed jobs as successful. These pin the non-zero status.
 */
class GianiExitCodeTest {

    @Test
    void missingPropertiesFileFails() {
        assertEquals(GIANI.EXIT_FAILURE,
                GIANI.runHeadless(new String[]{"-f", "some-image.tiff", "-x"}));
    }

    @Test
    void missingInputFails(@TempDir Path dir) throws IOException {
        File props = writeProperties(dir);
        assertEquals(GIANI.EXIT_FAILURE,
                GIANI.runHeadless(new String[]{"-p", props.getAbsolutePath(), "-x"}));
    }

    @Test
    void jobListWithoutJobNumberFails(@TempDir Path dir) throws IOException {
        File props = writeProperties(dir);
        File list = dir.resolve("jobs.csv").toFile();
        Files.writeString(list.toPath(), "0,image.tiff,0\n", StandardCharsets.UTF_8);
        assertEquals(GIANI.EXIT_FAILURE, GIANI.runHeadless(
                new String[]{"-p", props.getAbsolutePath(), "-l", list.getAbsolutePath(), "-x"}));
    }

    @Test
    void absentJobListFails(@TempDir Path dir) throws IOException {
        File props = writeProperties(dir);
        File list = dir.resolve("no-such-list.csv").toFile();
        assertEquals(GIANI.EXIT_FAILURE, GIANI.runHeadless(new String[]{
                "-p", props.getAbsolutePath(), "-l", list.getAbsolutePath(), "-n", "0", "-x"}));
    }

    @Test
    void jobNumberAbsentFromListFails(@TempDir Path dir) throws IOException {
        File props = writeProperties(dir);
        File list = dir.resolve("jobs.csv").toFile();
        Files.writeString(list.toPath(), "0,image.tiff,0\n", StandardCharsets.UTF_8);
        // Job 7 is not in the list: getJobDetails returns null and the NPE path
        // must abort rather than report success.
        assertEquals(GIANI.EXIT_FAILURE, GIANI.runHeadless(new String[]{
                "-p", props.getAbsolutePath(), "-l", list.getAbsolutePath(), "-n", "7", "-x"}));
    }

    @Test
    void unreadablePropertiesFileFails(@TempDir Path dir) throws IOException {
        // Not valid properties XML: loading must abort rather than silently
        // falling back to defaults and producing wrong-but-plausible output.
        File props = dir.resolve("broken.xml").toFile();
        Files.writeString(props.toPath(), "this is not properties xml", StandardCharsets.UTF_8);
        File image = dir.resolve("image.tiff").toFile();
        Files.writeString(image.toPath(), "not an image", StandardCharsets.UTF_8);
        assertEquals(GIANI.EXIT_FAILURE, GIANI.runHeadless(new String[]{
                "-p", props.getAbsolutePath(), "-f", image.getAbsolutePath(), "-x"}));
    }

    private static File writeProperties(Path dir) throws IOException {
        File props = dir.resolve("properties.xml").toFile();
        java.util.Properties p = new java.util.Properties();
        p.setProperty("Input Directory", dir.toString());
        try (var out = Files.newOutputStream(props.toPath())) {
            p.storeToXML(out, null);
        }
        return props;
    }
}
