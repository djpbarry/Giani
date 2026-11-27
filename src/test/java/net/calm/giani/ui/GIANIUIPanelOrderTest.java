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

import net.calm.iaclasslibrary.UIClasses.LayerPanel;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * On the default path the wizard shows only the core panels, so the per-channel
 * spot panels have to be slotted into that list, in order, right after the
 * measurement panel. {@code GIANIUI} is a {@code JFrame} and cannot be built
 * headless, so the list handling is tested through its static helper; what the
 * user actually sees needs a live check in Fiji.
 */
class GIANIUIPanelOrderTest {

    private static LayerPanel panel(String name) {
        LayerPanel panel = new LayerPanel() {
            @Override
            public void setupProcess() {
            }

            @Override
            protected void setToolTips() {
            }
        };
        panel.setName(name);
        return panel;
    }

    private static List<String> names(List<LayerPanel> panels) {
        return panels.stream().map(LayerPanel::getName).toList();
    }

    @Test
    void spotPanelsGoInOrderImmediatelyAfterTheAnchor() {
        LayerPanel input = panel("input");
        LayerPanel measure = panel("measure");
        LayerPanel later = panel("later");
        List<LayerPanel> core = new ArrayList<>(List.of(input, measure, later));

        GIANIUI.insertAfter(core, measure, List.of(panel("spots0"), panel("spots1")));

        assertEquals(List.of("input", "measure", "spots0", "spots1", "later"), names(core));
    }

    @Test
    void anAnchorThatIsLastGetsThePanelsAppended() {
        LayerPanel input = panel("input");
        LayerPanel measure = panel("measure");
        List<LayerPanel> core = new ArrayList<>(List.of(input, measure));

        GIANIUI.insertAfter(core, measure, List.of(panel("spots0")));

        assertEquals(List.of("input", "measure", "spots0"), names(core));
    }

    @Test
    void aMissingAnchorAppendsRatherThanFailing() {
        List<LayerPanel> core = new ArrayList<>(List.of(panel("input")));

        GIANIUI.insertAfter(core, panel("not in the list"), List.of(panel("spots0")));

        assertEquals(List.of("input", "spots0"), names(core));
    }

    @Test
    void insertingNothingChangesNothing() {
        LayerPanel input = panel("input");
        LayerPanel measure = panel("measure");
        List<LayerPanel> core = new ArrayList<>(List.of(input, measure));

        GIANIUI.insertAfter(core, measure, List.of());

        assertEquals(List.of("input", "measure"), names(core));
    }

    @Test
    void rebuildingTheSpotPanelsReplacesTheOldOnesInPlace() {
        // Mirrors addAdditionalBlobDetectionPanels: drop the previous spot panels,
        // then slot the new ones in after the measurement panel. Clicking the
        // channel list repeats this, so it must not accumulate or reorder panels.
        LayerPanel input = panel("input");
        LayerPanel measure = panel("measure");
        List<LayerPanel> core = new ArrayList<>(List.of(input, measure));
        List<LayerPanel> previous = List.of(panel("old0"), panel("old1"));
        GIANIUI.insertAfter(core, measure, previous);

        core.removeAll(previous);
        GIANIUI.insertAfter(core, measure, List.of(panel("new0")));

        assertEquals(List.of("input", "measure", "new0"), names(core));
    }

    @Test
    void untickingLocaliseSpotsLeavesJustTheCorePanels() {
        LayerPanel input = panel("input");
        LayerPanel measure = panel("measure");
        List<LayerPanel> core = new ArrayList<>(List.of(input, measure));
        List<LayerPanel> spots = List.of(panel("spots0"));
        GIANIUI.insertAfter(core, measure, spots);

        core.removeAll(spots);
        GIANIUI.insertAfter(core, measure, List.of());

        assertEquals(List.of("input", "measure"), names(core));
    }
}
