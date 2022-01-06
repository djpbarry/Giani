package net.calm.giani.macro;

import ij.IJ;
import ij.macro.ExtensionDescriptor;
import ij.macro.MacroExtension;
import net.calm.giani.exec.GIANI;
import net.calm.giani.gianiparams.GianiDefaultParams;
import net.calm.iaclasslibrary.IO.PropertyWriter;
import net.calm.iaclasslibrary.UtilClasses.GenUtils;

import java.io.File;
import java.util.Properties;

public class GIANIMacroExecutor implements MacroExtension {
    private final String LOAD_PROPS = "loadPropertiesFile";
    private final String RUN = "run";
    private final String SET_PROP = "setProperty";
    private final String GET_PROP_LIST = "getPropertyList";
    private final Properties props;

    public GIANIMacroExecutor() {
        this.props = new GianiDefaultParams();
    }

    public ExtensionDescriptor[] getExtensionFunctions() {
        return new ExtensionDescriptor[]{
                new ExtensionDescriptor(LOAD_PROPS, new int[]{
                        MacroExtension.ARG_STRING}, this),
                new ExtensionDescriptor(SET_PROP, new int[]{
                        MacroExtension.ARG_STRING, MacroExtension.ARG_STRING
                }, this),
                new ExtensionDescriptor(GET_PROP_LIST, new int[0], this),
                new ExtensionDescriptor(RUN, new int[0], this),};
    }

    public String handleExtension(String name, Object[] args) {
        switch (name) {
            case LOAD_PROPS:
                loadPropertiesFile(args);
                break;
            case SET_PROP:
                setProperty(args);
                break;
            case GET_PROP_LIST:
                String output = "";
                for (String p : props.stringPropertyNames()) {
                    output = output + p + ",";
                }
                return output;
            case RUN:
                run();
                break;
            default:
                IJ.log(String.format("Error: %s is not a valid command.", name));
        }
        return null;
    }

    void loadPropertiesFile(Object[] args) {
        if (!(args[0] instanceof String)) {
            invalidArguments();
            return;
        }
        try {
            PropertyWriter.loadProperties(props, null, new File((String) args[0]));
        } catch (Exception e) {
            GenUtils.logError(e, "Failed to load properties file.");
        }
    }

    void run() {
        (new GIANI()).run(props);
    }

    void setProperty(Object[] args) {
        if (!(args[0] instanceof String && args[1] instanceof String)) {
            invalidArguments();
            return;
        }
        props.setProperty((String) args[0], (String) args[1]);
    }

    void invalidArguments() {
        IJ.log("Arguments are invalid");
    }
}
