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

import org.junit.jupiter.api.Test;
import org.opentest4j.TestAbortedException;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Guards the fixture guard: if {@code requireFixtures} ever stopped failing on
 * a missing fixture, CI would go back to reporting green for a run that tested
 * nothing — the exact failure this helper exists to prevent.
 */
class TestFixturesTest {

    private static final String MISSING = "test_data/no-such-fixture.ome.tiff";

    @Test
    void failsOnMissingFixtureWhenRequireIsSet() {
        System.setProperty(TestFixtures.REQUIRE_PROPERTY, "true");
        try {
            assertThrows(AssertionError.class, () -> TestFixtures.require(MISSING));
        } finally {
            System.clearProperty(TestFixtures.REQUIRE_PROPERTY);
        }
    }

    @Test
    void skipsOnMissingFixtureByDefault() {
        System.clearProperty(TestFixtures.REQUIRE_PROPERTY);
        // A skip is an aborted test, which is distinct from a failure; assert on
        // it here rather than letting it abort this test.
        assertThrows(TestAbortedException.class, () -> TestFixtures.require(MISSING));
    }

    @Test
    void returnsTheFileWhenPresent() {
        File self = TestFixtures.require("pom.xml");
        assertEquals("pom.xml", self.getName());
    }
}
