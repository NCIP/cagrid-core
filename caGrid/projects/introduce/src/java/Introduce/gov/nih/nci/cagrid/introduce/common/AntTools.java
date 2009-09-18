package gov.nih.nci.cagrid.introduce.common;

import gov.nih.nci.cagrid.introduce.IntroduceConstants;

import java.io.File;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;


/**
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella </A>
 */
public class AntTools {
    private static final Logger logger = Logger.getLogger(AntTools.class);

    public static final String DEBUG_ANT_CALL_JAVA_OPTS = "-Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=8000";


    public static String getAntCommand(String antCommand, String buildFileDir) throws Exception {
        String cmd = " " + antCommand;
        cmd = getAntCommandCall(buildFileDir) + cmd;
        return cmd;
    }


    public static String getAntAllCommand(String buildFileDir) throws Exception {
        return getAntCommand("clean all", buildFileDir);
    }


    public static String getAntMergeCommand(String buildFileDir) throws Exception {
        return getAntCommand("clean merge", buildFileDir);
    }


    public static String getAntDeployTomcatCommand(String buildFileDir) throws Exception {
        return createDeploymentCommand(buildFileDir, "deployTomcat");
    }


    public static String getAntDeployJBossCommand(String buildFileDir) throws Exception {
        return createDeploymentCommand(buildFileDir, "deployJBoss");
    }


    public static String getAntDeployGlobusCommand(String buildFileDir) throws Exception {
        return createDeploymentCommand(buildFileDir, "deployGlobus");
    }


    public static String getAntUndeployTomcatCommand(String buildFileDir) throws Exception {
        return createDeploymentCommand(buildFileDir, "undeployTomcat");
    }


    public static String getAntUndeployJBossCommand(String buildFileDir) throws Exception {
        return createDeploymentCommand(buildFileDir, "undeployJBoss");
    }


    public static String getAntUndeployGlobusCommand(String buildFileDir) throws Exception {
        return createDeploymentCommand(buildFileDir, "undeployGlobus");
    }


    private static String fixPathforOS(String path) {
        String os = System.getProperty("os.name");
        if ((os.indexOf("Windows") >= 0) || (os.indexOf("windows") >= 0)) {
            path = "\"" + path + "\"";
        } else {
            path = path.replaceAll(" ", "\\ ");
        }
        return path;
    }


    private static String createDeploymentCommand(String buildFileDir, String deployTarget) throws Exception {
        String dir = buildFileDir;
        File dirF = new File(dir);
        if (!dirF.isAbsolute()) {
            dir = buildFileDir + File.separator + dir;
        }
        dir = fixPathforOS(dir);
        String cmd = " -Dservice.properties.file=" + dir + File.separator
            + IntroduceConstants.INTRODUCE_SERVICE_PROPERTIES;

        cmd = getAntCommand(deployTarget, buildFileDir) + " " + cmd;
        return cmd;
    }


    public static String getAntSkeletonCreationCommand(String buildFileDir, String name, String dir,
        String packagename, String namespacedomain, String resourceOptions, String extensions) throws Exception {
        return getAntSkeletonCreationCommand(buildFileDir, name, dir, packagename, namespacedomain, resourceOptions,
            extensions, false);
    }


    public static String getAntSkeletonCreationCommand(String buildFileDir, String name, String dir,
        String packagename, String namespacedomain, String resourceOptions, String extensions, boolean debug)
        throws Exception {
        // fix dir path if it relative......
        logger.debug("CREATION: builddir: " + buildFileDir);
        logger.debug("CREATION: destdir: " + dir);
        File dirF = new File(dir);
        if (!dirF.isAbsolute()) {
            dir = buildFileDir + File.separator + dir;
        }
        dir = fixPathforOS(dir);
        String cmd = " -Dintroduce.skeleton.destination.dir=" + dir + " -Dintroduce.skeleton.service.name=" + name
            + " -Dintroduce.skeleton.package=" + packagename + " -Dintroduce.skeleton.package.dir="
            + packagename.replace('.', File.separatorChar) + " -Dintroduce.skeleton.namespace.domain="
            + namespacedomain + " -Dintroduce.skeleton.resource.options=" + resourceOptions
            + " -Dintroduce.skeleton.extensions=" + extensions + " createService";
        cmd = getAntCommandCall(buildFileDir, debug) + cmd;
        logger.debug("CREATION: cmd: " + cmd);
        return cmd;
    }


    public static String getAntSkeletonPostCreationCommand(String buildFileDir, String name, String dir,
        String packagename, String namespacedomain, String extensions) throws Exception {
        return getAntSkeletonPostCreationCommand(buildFileDir, name, dir, packagename, namespacedomain, extensions,
            false);
    }


    public static String getAntSkeletonPostCreationCommand(String buildFileDir, String name, String dir,
        String packagename, String namespacedomain, String extensions, boolean debug) throws Exception {
        // fix dir path if it relative......
        logger.debug("CREATION: builddir: " + buildFileDir);
        logger.debug("CREATION: destdir: " + dir);
        File dirF = new File(dir);
        if (!dirF.isAbsolute()) {
            dir = buildFileDir + File.separator + dir;
        }
        dir = fixPathforOS(dir);
        String cmd = " -Dintroduce.skeleton.destination.dir=" + dir + " -Dintroduce.skeleton.service.name=" + name
            + " -Dintroduce.skeleton.package=" + packagename + " -Dintroduce.skeleton.package.dir="
            + packagename.replace('.', File.separatorChar) + " -Dintroduce.skeleton.namespace.domain="
            + namespacedomain + " -Dintroduce.skeleton.extensions=" + extensions + " postCreateService";
        cmd = getAntCommandCall(buildFileDir, debug) + cmd;
        logger.debug("CREATION: cmd: " + cmd);
        return cmd;
    }


    static String getAntCommandCall(String buildFileDir) throws Exception {
        return getAntCommandCall(buildFileDir, false);
    }


    static String getAntCommandCall(String buildFileDir, boolean debug) throws Exception {
        String os = System.getProperty("os.name");
        String cmd = "";
        if ((os.indexOf("Windows") >= 0) || (os.indexOf("windows") >= 0)) {
            cmd = "-classpath \"" + AntTools.getAntLauncherJarLocation(System.getProperty("java.class.path"), true)
                + "\" org.apache.tools.ant.launch.Launcher -verbose -buildfile " + "\"" + buildFileDir + File.separator
                + "build.xml\"" + cmd;
            if (debug) {
                cmd = "java.exe " + DEBUG_ANT_CALL_JAVA_OPTS + " " + cmd;
            } else {
                cmd = "java.exe " + cmd;
            }
        } else {
            // escape out the spaces.....
            buildFileDir = buildFileDir.replaceAll("\\s", "\\ ");
            cmd = "-classpath " + AntTools.getAntLauncherJarLocation(System.getProperty("java.class.path"), false)
                + " org.apache.tools.ant.launch.Launcher -buildfile " + buildFileDir + File.separator + "build.xml"
                + cmd;
            if (debug) {
                cmd = "java " + DEBUG_ANT_CALL_JAVA_OPTS + " " + cmd;
            } else {
                cmd = "java " + cmd;
            }
        }
        return cmd;
    }


    static String getAntLauncherJarLocation(String path, boolean isWindows) {
        String separator = isWindows ? ";" : ":";
        StringTokenizer pathTokenizer = new StringTokenizer(path, separator);
        while (pathTokenizer.hasMoreTokens()) {
            String pathElement = pathTokenizer.nextToken();
            if ((pathElement.indexOf("ant-launcher") != -1) && pathElement.endsWith(".jar")) {
                return pathElement;
            }
        }
        return null;
    }

}
