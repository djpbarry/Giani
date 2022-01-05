package net.calm.giani.macro;

import ij.IJ;
import ij.macro.Functions;
import ij.plugin.PlugIn;

public class GIANI_Macro_Extensions implements PlugIn {
    public void run(String args) {
        if (!IJ.macroRunning()) {
            IJ.error("Macro extensions are designed to be run from within a macro."
                    + "\n Instructions on how to do so will follow.");
            IJ.log(toString());
            return;
        } else {
            Functions.registerExtensions(new GIANIMacroExecutor());
        }
    }

    public String toString() {
        return "To gain access to GIANI Macro extensions from within a macro, put\n"
                + " the following line at the beginning of your macro:\n"
                + "\n"
                + "run(\"GIANI Macro Extensions\");\n"
                + "\n"
                + "This will enable the following macro functions:\n"
                + "\n";
    }
}
