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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

/**
 * Unit tests for the timelapse timepoint-range logic (Phase H1).
 */
class PipelineExecutorTest {

    @Test
    void defaultsToSingleFirstFrame() {
        assertArrayEquals(new int[]{0, 1}, PipelineExecutor.timeRange(5, -1, false));
    }

    @Test
    void timelapseSpansAllFrames() {
        assertArrayEquals(new int[]{0, 5}, PipelineExecutor.timeRange(5, -1, true));
    }

    @Test
    void specificTimepointWinsOverNoTimelapse() {
        assertArrayEquals(new int[]{3, 4}, PipelineExecutor.timeRange(5, 3, false));
    }

    @Test
    void specificTimepointWinsOverTimelapse() {
        assertArrayEquals(new int[]{3, 4}, PipelineExecutor.timeRange(5, 3, true));
    }

    @Test
    void invalidSpecificTimepointFallsThroughToTimelapse() {
        assertArrayEquals(new int[]{0, 5}, PipelineExecutor.timeRange(5, 9, true));
    }

    @Test
    void invalidSpecificTimepointFallsThroughToDefault() {
        assertArrayEquals(new int[]{0, 1}, PipelineExecutor.timeRange(5, 9, false));
    }

    @Test
    void zeroTimepointsClampsToSingleFrame() {
        assertArrayEquals(new int[]{0, 1}, PipelineExecutor.timeRange(0, -1, true));
    }
}
