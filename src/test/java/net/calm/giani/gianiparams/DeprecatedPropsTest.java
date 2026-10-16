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
package net.calm.giani.gianiparams;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the deprecated parameter-name migration.
 */
class DeprecatedPropsTest {

    @Test
    void depMapContainsKnownMigrations() {
        HashMap<String, String> map = DeprecatedProps.getDepMap();
        assertFalse(map.isEmpty());
        assertEquals(GianiDefaultParams.BLOB_NUC_NOISE_TOL_LABEL,
                map.get(DeprecatedProps.BLOB_NUC_NOISE_TOL_LABEL));
        assertEquals(GianiDefaultParams.BLOB_CHAN_NOISE_TOL_LABEL,
                map.get(DeprecatedProps.BLOB_CHAN_NOISE_TOL_LABEL));
        assertEquals(GianiDefaultParams.NUC_SEG_THRESH_LABEL,
                map.get(DeprecatedProps.NUC_SEG_THRESH_LABEL));
        assertEquals(GianiDefaultParams.CELL_SEG_THRESH_LABEL,
                map.get(DeprecatedProps.CELL_SEG_THRESH_LABEL));
    }

    @Test
    void mapControlChangesSetsMethodFromBlobFlag() {
        Properties p = new Properties();
        p.setProperty(GianiDefaultParams.NUC_MAXIMA_DETECT_BLOBS, "true");
        p.setProperty(GianiDefaultParams.NUC_MAXIMA_DETECT_HESSIAN, "false");
        DeprecatedProps.mapControlChanges(p);
        assertEquals(GianiDefaultParams.NUC_MAXIMA_DETECT_BLOBS,
                p.getProperty(GianiDefaultParams.NUC_CENTROID_LOCALISATION_METHOD));
    }

    @Test
    void mapControlChangesSetsMethodFromHessianFlagWhenBlobFalse() {
        Properties p = new Properties();
        p.setProperty(GianiDefaultParams.NUC_MAXIMA_DETECT_BLOBS, "false");
        p.setProperty(GianiDefaultParams.NUC_MAXIMA_DETECT_HESSIAN, "true");
        DeprecatedProps.mapControlChanges(p);
        assertEquals(GianiDefaultParams.NUC_MAXIMA_DETECT_HESSIAN,
                p.getProperty(GianiDefaultParams.NUC_CENTROID_LOCALISATION_METHOD));
    }
}
