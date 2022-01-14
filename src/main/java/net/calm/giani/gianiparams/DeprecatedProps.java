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
package net.calm.giani.gianiparams;

import java.util.HashMap;

public class DeprecatedProps {

    public static final String BLOB_NUC_NOISE_TOL_LABEL = "Threshold for Simple Nuclear Centroid Detection";

    public static HashMap<String, String> getDepMap() {
        HashMap<String, String> map = new HashMap<>();
        map.put(DeprecatedProps.BLOB_NUC_NOISE_TOL_LABEL, GianiDefaultParams.BLOB_NUC_NOISE_TOL_LABEL);
        return map;
    }
}
