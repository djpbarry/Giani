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
    public static final String BLOB_CHAN_NOISE_TOL_LABEL = "Threshold for Simple Foci Detection in Channel ";
    public static final String NUC_SEG_THRESH_LABEL = "Threshold for Nuclear Segmentation";
    public static final String CELL_SEG_THRESH_LABEL = "Threshold for Cell Segmentation";
    public static final String NUC_MAXIMA_DETECT_HESSIAN_THRESH = "Threshold for Advanced Nuclear Centroid Detection";
    public static final String FOCI_MAXIMA_DETECT_HESSIAN_THRESH = "Threshold for Advanced Foci Detection in Channel ";

    public static HashMap<String, String> getDepMap() {
        HashMap<String, String> map = new HashMap<>();
        map.put(DeprecatedProps.BLOB_NUC_NOISE_TOL_LABEL, GianiDefaultParams.BLOB_NUC_NOISE_TOL_LABEL);
        map.put(DeprecatedProps.BLOB_CHAN_NOISE_TOL_LABEL, GianiDefaultParams.BLOB_CHAN_NOISE_TOL_LABEL);
        map.put(DeprecatedProps.NUC_SEG_THRESH_LABEL, GianiDefaultParams.NUC_SEG_THRESH_LABEL);
        map.put(DeprecatedProps.CELL_SEG_THRESH_LABEL, GianiDefaultParams.CELL_SEG_THRESH_LABEL);
        map.put(DeprecatedProps.NUC_MAXIMA_DETECT_HESSIAN_THRESH, GianiDefaultParams.NUC_MAXIMA_DETECT_HESSIAN_THRESH);
        map.put(DeprecatedProps.FOCI_MAXIMA_DETECT_HESSIAN_THRESH, GianiDefaultParams.FOCI_MAXIMA_DETECT_HESSIAN_THRESH);
        return map;
    }
}
