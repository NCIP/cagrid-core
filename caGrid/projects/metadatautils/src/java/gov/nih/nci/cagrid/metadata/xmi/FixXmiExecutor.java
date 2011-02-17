package gov.nih.nci.cagrid.metadata.xmi;

import gov.nih.nci.cagrid.common.StreamGobbler;
import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.common.StreamGobbler.LogPriority;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * FixXmiExecutor Executes SDK 3.1 / 3.2 / 3.2.1's fix-xmi ant target
 * 
 * @author David Ervin
 * @created Oct 29, 2007 2:11:31 PM
 * @version $Id: FixXmiExecutor.java,v 1.9 2008-04-28 19:31:07 dervin Exp $
 */
public class FixXmiExecutor {
    public static final Log LOG = LogFactory.getLog(FixXmiExecutor.class);

    // ant tasks
    public static final String FIX_XMI_TASK = "fix-xmi";
    public static final String COMPILE_GENERATOR_TASK = "compile-generator";

    // properties for the ant tasks
    public static final String MODEL_DIR_PROPERTY = "dir.model";
    public static final String MODEL_FILENAME_PROPERTY = "model_filename";
    public static final String FIXED_MODEL_FILENAME_PROPERTY = "fixed_filename";
    public static final String PREPROCESSOR_PROPERTY = "xmi_preprocessor";

    // the EA xmi preprocessor class
    public static final String EA_XMI_PREPROCESSOR = "gov.nih.nci.codegen.core.util.EAXMIPreprocessor";

    private static Boolean isWindows = null;


    private FixXmiExecutor() {
        // prevent instantiation
    }


    /**
     * Runs the SDK's fix-xmi target against the specified model
     * 
     * @param originalModel
     *            The file containing the original XMI model from EA
     * @param sdkDir
     *            The caCORE SDK base directory
     * @return The file containing the 'fixed' model
     */
    public static File fixEaXmiModel(File originalModel, File sdkDir) throws IOException, InterruptedException {
        File cleanModelFile = cleanXmi(originalModel);
        List<String> command = new ArrayList<String>();
        // get the base ant command
        command.addAll(getAntCall(sdkDir.getAbsolutePath()));
        // add properties and their values
        String modelDirProp = "-D" + MODEL_DIR_PROPERTY + "=";
        String modelFileDir = cleanModelFile.getAbsoluteFile().getParent();

        modelDirProp += modelFileDir;

        command.add(modelDirProp);
        command.add("-D" + MODEL_FILENAME_PROPERTY + "=" + cleanModelFile.getName());
        command.add("-D" + FIXED_MODEL_FILENAME_PROPERTY + "=fixed_" + originalModel.getName());
        command.add("-D" + PREPROCESSOR_PROPERTY + "=" + EA_XMI_PREPROCESSOR);
        // windows command line issues:
        // http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6468220
        String[] cmdArray = command.toArray(new String[0]);
        if (osIsWindows()) {
            for (int i = 0; i < cmdArray.length; i++) {
                if (needsQuoting(cmdArray[i])) {
                    cmdArray[i] = winQuote(cmdArray[i]);
                }
            }
        }
        // System.out.println("Executing command...");
        if (LOG.isDebugEnabled()) {
            LOG.debug("Executing command...");
            for (String c : cmdArray) {
                LOG.debug("\t" + c);
                // System.out.println("\t" + c);
            }
        }
        // execute the command
        Process proc = Runtime.getRuntime().exec(cmdArray);
        // streams to LOG
        new StreamGobbler(proc.getInputStream(), StreamGobbler.TYPE_OUT, LOG, LogPriority.DEBUG).start();
        new StreamGobbler(proc.getErrorStream(), StreamGobbler.TYPE_ERR, LOG, LogPriority.DEBUG).start();
        LOG.debug("Waiting");
        proc.waitFor();
        if (proc.exitValue() == 0) {
            return new File(originalModel.getParent() + File.separator + "fixed_" + originalModel.getName());
        } else {
            throw new RuntimeException("Error executing fix-xmi command:\n" + command.toString());
        }
    }


    private static List<String> getAntCall(String buildFileDir) {
        List<String> command = new ArrayList<String>();
        if (osIsWindows()) {
            command.add("java.exe");
        } else {
            command.add("java");
        }
        
        command.add("-classpath");
        command.add(getAntLauncherJarLocation(System.getProperty("java.class.path")));
        command.add("org.apache.tools.ant.launch.Launcher");
        command.add("-buildfile");
        command.add(buildFileDir + File.separator + "build.xml");
        
        // add targets
        command.add(FIX_XMI_TASK);
        return command;
    }


    private static String getAntLauncherJarLocation(String path) {
        StringTokenizer pathTokenizer = new StringTokenizer(path, File.pathSeparator);
        while (pathTokenizer.hasMoreTokens()) {
            String pathElement = pathTokenizer.nextToken();
            if ((pathElement.indexOf("ant-launcher") != -1) && pathElement.endsWith(".jar")) {
                return pathElement;
            }
        }
        return null;
    }


    private static File cleanXmi(File originalXmi) throws IOException {
        LOG.debug("Clean XMI");
        File cleanedFile = new File(originalXmi.getParentFile(), "cleaned_" + originalXmi.getName());
        StringBuffer xmiContents = Utils.fileToStringBuffer(originalXmi);
        XmiCleaner.cleanXmi(xmiContents);
        Utils.stringBufferToFile(xmiContents, cleanedFile.getAbsolutePath());
        return cleanedFile;
    }


    private static boolean osIsWindows() {
        if (isWindows == null) {
            String os = System.getProperty("os.name").toLowerCase();
            isWindows = Boolean.valueOf(os.contains("windows"));
        }
        return isWindows.booleanValue();
    }


    static boolean needsQuoting(String s) {
        int len = s.length();
        if (len == 0) // empty string have to be quoted
            return true;
        for (int i = 0; i < len; i++) {
            switch (s.charAt(i)) {
                case ' ' :
                case '\t' :
                case '\\' :
                case '"' :
                    return true;
            }
        }
        return false;
    }


    static String winQuote(String s) {
        if (!needsQuoting(s))
            return s;
        s = s.replaceAll("([\\\\]*)\"", "$1$1\\\\\"");
        s = s.replaceAll("([\\\\]*)\\z", "$1$1");
        return "\"" + s + "\"";
    }
}
