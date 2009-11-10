package org.cagrid.tests.data.styles.cacore42;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class AntCommandUtil {
    
    public static final String PATH_ENV = "PATH";
    public static final String ANT_HOME_ENV = "ANT_HOME";
    
    public static final String DEBUG_ANT_CALL_JAVA_OPTS[] = {"-Xdebug", "-Xnoagent", "-Djava.compiler=NONE",
        "-Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=8000"};
    
    private static Log LOG = LogFactory.getLog(AntCommandUtil.class);
    
    private File buildDir = null;
    private boolean debug = false;
    private Boolean isWindows = null;

    public AntCommandUtil(File buildDir, boolean debug) {
        this.buildDir = buildDir;
        this.debug = debug;
    }
    
    
    public ExecutableCommand getAntCommand(String command) throws Exception {
        List<String> cmd = getAntCommandCall();

        StringTokenizer stok = new StringTokenizer(command, " ");
        while (stok.hasMoreTokens()) {
            cmd.add(stok.nextToken());
        }
        
        if (LOG.isDebugEnabled()) {
            cmd.add("-verbose");
        }
        
        Map<String, String> environment = getEnvironment();
        return new ExecutableCommand(cmd, environment);
    }
    
    
    private List<String> getAntCommandCall() throws Exception {
        List<String> command = new ArrayList<String>();
        if (isWindowsOS()) {
            command.add("java.exe");
        } else {
            command.add("java");
        }

        if (debug) {
            command.addAll(Arrays.asList(DEBUG_ANT_CALL_JAVA_OPTS));
        }
        
        File buildFile = new File(buildDir, "build.xml");

        command.add("-classpath");
        command.add(escapeIfNecessary(getAntLauncherJarLocation()));
        command.add("org.apache.tools.ant.launch.Launcher");
        command.add("-buildfile");
        command.add(escapeIfNecessary(buildFile.getAbsolutePath()));

        return command;
    }


    private String getAntLauncherJarLocation() throws Exception {
        String classpath = System.getProperty("java.class.path");
        StringTokenizer pathTokenizer = new StringTokenizer(classpath, File.pathSeparator);
        while (pathTokenizer.hasMoreTokens()) {
            String pathElement = pathTokenizer.nextToken();
            if ((pathElement.indexOf("ant-launcher") != -1) && pathElement.endsWith(".jar")) {
                return pathElement;
            }
        }
        throw new Exception("Unable to locate ant-launcher in classpath");
    }
    
    
    private String escapeIfNecessary(String s) {
        if (isWindowsOS() && needsQuoting(s)) {
            return winQuote(s);
        } else {
            return s;
        }
    }
    
    
    private boolean isWindowsOS() {
        if (isWindows == null) {
            String os = System.getProperty("os.name").toLowerCase();
            isWindows = Boolean.valueOf(os.contains("windows"));
        }
        return isWindows.booleanValue();
    }
    
    
    private boolean needsQuoting(String s) {
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


    private String winQuote(String s) {
        s = s.replaceAll("([\\\\]*)\"", "$1$1\\\\\"");
        s = s.replaceAll("([\\\\]*)\\z", "$1$1");
        return "\"" + s + "\"";
    }
    
    
    private Map<String, String> getEnvironment() throws Exception {
        Map<String, String> mutableEnv = new HashMap<String, String>(System.getenv());
        if (!isAntOnPath()) {
            // see if we have ANT_HOME defined
            String antHome = mutableEnv.get(ANT_HOME_ENV);
            if (antHome != null) {
                String path = antHome + File.separator + "bin" 
                    + File.pathSeparator + mutableEnv.get(PATH_ENV);
                mutableEnv.put(PATH_ENV, path);
                LOG.debug("Adding " + ANT_HOME_ENV + File.separator + "bin to path");
            } else {
                // no ant on path, no ANT_HOME, no-can-run
                throw new Exception("Ant not found on " + PATH_ENV 
                    + " and " + ANT_HOME_ENV + " was not defined!");
            }
        }
        return mutableEnv;
    }
    
    
    private boolean isAntOnPath() {
        String antExecutable = isWindowsOS() ? "ant.bat" : "ant";
        StringTokenizer pathTokenizer = new StringTokenizer(System.getenv(PATH_ENV), File.pathSeparator);
        while (pathTokenizer.hasMoreTokens()) {
            String path = pathTokenizer.nextToken();
            File maybeAnt = new File(path, antExecutable);
            if (maybeAnt.exists() && maybeAnt.isFile()) {
                LOG.debug("Probably found ant on the path: " + maybeAnt.getAbsolutePath());
                return true;
            }
        }
        LOG.debug("Ant not found on the path");
        return false;
    }
}
