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

import net.calm.iaclasslibrary.IO.BioFormats.BioFormatsImg;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Guard for the timelapse fixture used by the Phase H1 end-to-end test.
 */
class TimelapseFixtureTest {

    static final String FIXTURE = "test_data/18.06.10_Experiment.lif_7_time.ome.tiff";

    @Test
    void fixtureHasMultipleTimepointsAndZSlices() throws Exception {
        File file = new File(FIXTURE);
        Assumptions.assumeTrue(file.isFile(), "test fixture not present: " + FIXTURE);
        try (BioFormatsImg img = new BioFormatsImg()) {
            img.setId(file.getAbsolutePath());
            System.out.println("Timelapse fixture series=" + img.getSeriesCount()
                    + " C=" + img.getSizeC(0) + " Z=" + img.getSizeZ(0) + " T=" + img.getSizeT(0));
            assertTrue(img.getSizeZ(0) >= 2, "fixture needs Z>=2 so the pipeline does not skip it");
            assertTrue(img.getSizeT(0) >= 2, "fixture needs T>=2 for a timelapse run");
        }
    }
}
