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
package net.calm.giani;

import org.junit.jupiter.api.Assumptions;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Resolves a checked-in image fixture under {@code test_data/}.
 *
 * <p>Locally a missing fixture <em>skips</em> the test, so a developer without
 * the (33 MB) images can still run the suite. In CI that is the wrong default:
 * the fixtures are tracked in git and must be present, and a silent skip would
 * let a green build hide the fact that nothing ran. This actually happened —
 * the fixtures were moved into {@code test_data/single_timepoint/} and
 * {@code test_data/multiple_timepoints/} while the tests still referenced the
 * old paths, and both integration tests skipped unnoticed for several days.
 *
 * <p>Setting {@code -Dgiani.requireFixtures=true} (which the integration CI job
 * does) therefore turns a missing fixture into a failure.
 */
public final class TestFixtures {

    /** System property that makes a missing fixture fail rather than skip. */
    public static final String REQUIRE_PROPERTY = "giani.requireFixtures";

    private TestFixtures() {
    }

    /**
     * @param path repo-relative path to the fixture
     * @return the fixture file; the calling test is skipped (or failed, under
     * {@code -Dgiani.requireFixtures=true}) if it does not exist
     */
    public static File require(String path) {
        File fixture = new File(path);
        String message = "test fixture not present: " + path;
        if (Boolean.getBoolean(REQUIRE_PROPERTY)) {
            assertTrue(fixture.isFile(),
                    message + " (-D" + REQUIRE_PROPERTY + "=true, so this fails rather than skips)");
        } else {
            Assumptions.assumeTrue(fixture.isFile(), message);
        }
        return fixture;
    }
}
