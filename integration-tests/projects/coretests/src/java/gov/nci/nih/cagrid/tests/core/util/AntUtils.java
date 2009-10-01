package gov.nci.nih.cagrid.tests.core.util;

import gov.nih.nci.cagrid.common.StreamGobbler;
import gov.nih.nci.cagrid.introduce.common.AntTools;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;


/**
 * AntUtils Utilities to fire off ant commands
 * 
 * @author David Ervin
 * @created Nov 6, 2007 12:52:56 PM
 * @version $Id: AntUtils.java,v 1.14 2008-02-22 20:55:49 oster Exp $
 */
public class AntUtils {
    public static String getAntCommand() {
        // ant home
        String ant = System.getenv("ANT_HOME");
        if (ant == null) {
            throw new IllegalArgumentException("ANT_HOME not set");
        }

        // ant home/bin
        if (!ant.endsWith(File.separator)) {
            ant += File.separator;
        }
        ant += "bin" + File.separator;

        // ant home/bin/ant
        if (OSUtils.isWindows()) {
            ant += "ant.bat";
        } else {
            ant += "ant";
        }

        if (!new File(ant).exists()) {
            throw new IllegalArgumentException(ant + " does not exist");
        }
        return ant;
    }


    /**
     * @deprecated This code is a MESS! use getAntCommand();
     * @param dir
     * @param buildFile
     * @param target
     * @param sysProps
     * @param envp
     * @throws IOException
     * @throws InterruptedException
     */
    public static void runAnt(File dir, String buildFile, String target, Properties sysProps, String[] envp)
        throws IOException, InterruptedException {
        // build command
        ArrayList<String> cmd = new ArrayList<String>();
        cmd.add(getAntCommand());

        // add system properties
        if (sysProps != null) {
            Enumeration keys = sysProps.keys();
            while (keys.hasMoreElements()) {
                String name = (String) keys.nextElement();
                String value = sysProps.getProperty(name);
                if (!OSUtils.isWindows()) {
                    value = value.replaceAll(" ", "\\\\ ");
                }
                cmd.add("\"-D" + name + "=" + value + "\"");
            }
        }

        // add build file
        if (buildFile != null) {
            cmd.add("-f");
            cmd.add(buildFile);
        }

        // add target
        if (target != null) {
            cmd.add(target);
        }

        // run ant
        Process p = Runtime.getRuntime().exec(cmd.toArray(new String[0]), envp, dir);
        // track stdout and stderr
        new StreamGobbler(p.getInputStream(), StreamGobbler.TYPE_OUT, System.out).start();
        new StreamGobbler(p.getErrorStream(), StreamGobbler.TYPE_ERR, System.err).start();

        // wait and return
        if (p.waitFor() != 0) {
            throw new IOException("ant command '" + target + "' failed");
        }
    }


    public static List<String> getAntCommand(File baseDir, String target) throws Exception {
        return getAntCommand(baseDir, target, null);
    }


    public static List<String> getAntCommand(File baseDir, String target, Properties systemProps) throws Exception {
        List<String> command = AntTools.getAntCommand(target, baseDir.getAbsolutePath());
        // add system properties
        if (systemProps != null) {
            Enumeration keys = systemProps.keys();
            while (keys.hasMoreElements()) {
                String name = (String) keys.nextElement();
                String value = systemProps.getProperty(name);
                if (!OSUtils.isWindows()) {
                    value = value.replaceAll(" ", "\\\\ ");
                }
                String propPart = " \"-D" + name + "=" + value + "\"";
                command.add(propPart);
            }
        }

        return command;
    }
}
