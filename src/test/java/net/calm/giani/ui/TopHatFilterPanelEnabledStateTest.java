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

import net.calm.giani.exec.PipelineBuilder;
import net.calm.giani.gianiparams.GianiDefaultParams;
import net.calm.iaclasslibrary.IO.BioFormats.BioFormatsImg;
import org.junit.jupiter.api.Test;

import javax.swing.JComponent;
import javax.swing.JToggleButton;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * The top-hat toggle disables the radius and downsizing controls when it is off.
 * {@code update()} runs on every wizard re-entry and calls
 * {@code setComponentsEnabled()}, which enables every child, so those controls
 * came back enabled even with the filter off (found 2026-11-27). The value was
 * never affected and the filter still did not run, so only the display misled.
 */
class TopHatFilterPanelEnabledStateTest {

    /** The five controls the toggle governs. */
    private static final List<String> TOGGLE_GOVERNED = List.of(
            "filterRadiusXYLabel", "filterRadiusXYTextField", "downSizeFactorTextField",
            "downsizeFactorLabel", "unitsLabel1");

    private static <T> T field(TopHatFilterPanel panel, String name) throws Exception {
        Field field = TopHatFilterPanel.class.getDeclaredField(name);
        field.setAccessible(true);
        @SuppressWarnings("unchecked") T value = (T) field.get(panel);
        return value;
    }

    private static TopHatFilterPanel panel(Properties props) {
        return new TopHatFilterPanel(props, new BioFormatsImg(), null, PipelineBuilder.getNucTopHatPropLabels(),
                null, GianiDefaultParams.NUC_TOP_HAT_TITLE);
    }

    private static void assertAllEnabled(TopHatFilterPanel panel, boolean expected, String message) throws Exception {
        for (String name : TOGGLE_GOVERNED) {
            JComponent component = field(panel, name);
            assertEquals(expected, component.isEnabled(), message + " (" + name + ")");
        }
    }

    @Test
    void theControlsStayDisabledOnReEntryWhenTopHatIsSwitchedOff() throws Exception {
        TopHatFilterPanel panel = panel(new GianiDefaultParams());
        panel.update();
        JToggleButton toggle = field(panel, "topHatToggleButton");

        toggle.doClick();                       // the user switches top-hat off
        assertAllEnabled(panel, false, "right after switching off");

        panel.update();                         // wizard re-entry
        assertAllEnabled(panel, false, "after re-entry");
    }

    @Test
    void theControlsAreEnabledWhenTopHatIsOn() throws Exception {
        TopHatFilterPanel panel = panel(new GianiDefaultParams());

        panel.update();

        assertAllEnabled(panel, true, "top-hat on by default");
    }

    @Test
    void switchingTopHatBackOnEnablesTheControlsAgain() throws Exception {
        TopHatFilterPanel panel = panel(new GianiDefaultParams());
        panel.update();
        JToggleButton toggle = field(panel, "topHatToggleButton");
        toggle.doClick();
        panel.update();

        toggle.doClick();

        assertAllEnabled(panel, true, "after switching back on");
        panel.update();
        assertAllEnabled(panel, true, "and after re-entry");
    }

    @Test
    void aPanelDisabledByStarDistStaysDisabledWhateverTheToggleSays() throws Exception {
        // With StarDist selected the nuclear top-hat stage is not used at all, so
        // the whole panel is disabled; the toggle must not re-enable its children.
        Properties props = new GianiDefaultParams();
        props.setProperty(GianiDefaultParams.NUC_MAXIMA_DETECT_STARDIST, "true");
        TopHatFilterPanel panel = panel(props);

        panel.update();

        JToggleButton toggle = field(panel, "topHatToggleButton");
        assertEquals(true, toggle.isSelected(), "precondition: the toggle itself is on");
        assertAllEnabled(panel, false, "StarDist disables the panel");
    }
}
