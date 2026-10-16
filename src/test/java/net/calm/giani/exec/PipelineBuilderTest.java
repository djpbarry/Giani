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
package net.calm.giani.exec;

import mcib3d.geom.Objects3DPopulation;
import net.calm.giani.gianiparams.GianiDefaultParams;
import net.calm.iaclasslibrary.Extrema.MultiThreadedMaximaFinder;
import net.calm.iaclasslibrary.Process.Filtering.MultiThreadedGaussianFilter;
import net.calm.iaclasslibrary.Process.Filtering.MultiThreadedTopHatFilter;
import net.calm.iaclasslibrary.Process.MultiThreadedProcess;
import net.calm.iaclasslibrary.Process.ProcessPipeline;
import net.calm.iaclasslibrary.Process.ROI.MultiThreadedROIConstructor;
import net.calm.iaclasslibrary.Process.Segmentation.MultiThreadedWatershed;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pins the fixed, index-coupled pipeline stage ordering.
 */
class PipelineBuilderTest {

    @Test
    void buildsFixedSevenStagePipelineWhenSpotsDisabled() {
        GianiDefaultParams props = new GianiDefaultParams();
        ProcessPipeline pipeline = new PipelineBuilder().buildFullPipeline(props, new Objects3DPopulation());

        List<MultiThreadedProcess> stages = toList(pipeline);
        assertEquals(7, stages.size());
        assertInstanceOf(MultiThreadedMaximaFinder.class, stages.get(0));
        assertInstanceOf(MultiThreadedGaussianFilter.class, stages.get(1));
        assertInstanceOf(MultiThreadedTopHatFilter.class, stages.get(2));
        assertInstanceOf(MultiThreadedWatershed.class, stages.get(3));
        assertInstanceOf(MultiThreadedGaussianFilter.class, stages.get(4));
        assertInstanceOf(MultiThreadedWatershed.class, stages.get(5));
        assertInstanceOf(MultiThreadedROIConstructor.class, stages.get(6));
    }

    @Test
    void everyStageHasPropLabels() {
        GianiDefaultParams props = new GianiDefaultParams();
        ProcessPipeline pipeline = new PipelineBuilder().buildFullPipeline(props, new Objects3DPopulation());

        for (MultiThreadedProcess process : pipeline) {
            assertNotNull(process.getPropLabels());
        }
    }

    @Test
    void maximaFinderPropLabelsReferenceKnownConstants() {
        GianiDefaultParams props = new GianiDefaultParams();
        ProcessPipeline pipeline = new PipelineBuilder().buildFullPipeline(props, new Objects3DPopulation());

        MultiThreadedMaximaFinder maximaFinder =
                (MultiThreadedMaximaFinder) toList(pipeline).get(0);
        assertEquals(GianiDefaultParams.BLOB_NUC_CHAN_SELECT_LABEL,
                maximaFinder.getPropLabels()[MultiThreadedMaximaFinder.CHANNEL_SELECT]);
    }

    private static List<MultiThreadedProcess> toList(ProcessPipeline pipeline) {
        List<MultiThreadedProcess> list = new ArrayList<>();
        for (MultiThreadedProcess process : pipeline) {
            list.add(process);
        }
        return list;
    }
}
