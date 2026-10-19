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
import net.calm.iaclasslibrary.Process.MultiThreadedProcess;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Capability guard for Phase H1: asserts the IAClassLibrary API surface that the
 * timelapse time loop relies on is present at the pinned version.
 */
class TimelapseApiTest {

    @Test
    void processExposesSetTime() throws NoSuchMethodException {
        Method setTime = MultiThreadedProcess.class.getMethod("setTime", int.class);
        assertEquals(void.class, setTime.getReturnType());
        assertTrue(Modifier.isPublic(setTime.getModifiers()));
    }

    @Test
    void bioFormatsImgExposesGetSizeT() throws NoSuchMethodException {
        Method getSizeT = BioFormatsImg.class.getMethod("getSizeT", int.class);
        assertEquals(int.class, getSizeT.getReturnType());
        assertTrue(Modifier.isPublic(getSizeT.getModifiers()));
    }
}
