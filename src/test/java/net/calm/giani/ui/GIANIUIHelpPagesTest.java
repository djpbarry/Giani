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
package net.calm.giani.ui;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The wizard's help buttons used to open pages on the retired GitHub wiki. They
 * now open the ReadTheDocs build of {@code docs/}, so each {@code HELP_*} page
 * must exist there, and no link back to the wiki may creep in.
 */
class GIANIUIHelpPagesTest {

    private static List<String> helpPages() throws IllegalAccessException {
        List<String> pages = new ArrayList<>();
        for (Field f : GIANIUI.class.getDeclaredFields()) {
            if (f.getName().startsWith("HELP_") && Modifier.isStatic(f.getModifiers())
                    && f.getType() == String.class) {
                pages.add((String) f.get(null));
            }
        }
        return pages;
    }

    @Test
    void everyHelpPageExistsInTheDocsSource() throws IllegalAccessException {
        List<String> pages = helpPages();
        assertEquals(9, pages.size(), "one help page per wizard panel type with a help button");
        for (String page : pages) {
            Path rst = Path.of("docs", page + ".rst");
            assertTrue(Files.isRegularFile(rst), "help page has no docs source: " + rst);
        }
    }

    @Test
    void helpUrisPointAtTheDocsBuild() throws IllegalAccessException {
        for (String page : helpPages()) {
            URI uri = GIANIUI.getHelpURI(page);
            assertNotNull(uri, page);
            assertEquals(GIANIUI.DOCS_BASE_URL + page + ".html", uri.toString());
        }
    }

    @Test
    void noHelpLinkPointsAtTheRetiredWiki() throws IOException {
        String source = Files.readString(Path.of("src/main/java/net/calm/giani/ui/GIANIUI.java"));
        assertFalse(source.contains("github.com/djpbarry/Giani/wiki"),
                "GIANIUI still links to the retired GitHub wiki");
    }
}
