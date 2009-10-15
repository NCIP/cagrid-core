package org.cagrid.tests.data.styles.cacore42.steps;

import gov.nih.nci.cagrid.common.StreamGobbler;
import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.io.File;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.tests.data.styles.cacore42.AntCommandUtil;
import org.cagrid.tests.data.styles.cacore42.ExampleProjectInfo;


public class BuildExampleProjectStep extends Step {
    
    // per Satish, but doesn't generate the output dirs... 
    // public static final String BUILD_ANT_TARGET = "deploy:local:install:re-configure";
    
    // generates EVERYTHING and tries to install Tomcat, which I don't want, but does create the output dir
    public static final String BUILD_ANT_TARGET = "deploy:local:install";
    
    private static Log LOG = LogFactory.getLog(BuildExampleProjectStep.class);
    
    public BuildExampleProjectStep() {
        super();
    }


    public void runStep() throws Throwable {
        File buildDir = new File(ExampleProjectInfo.getExampleProjectDir(), "build");
        AntCommandUtil antUtil = new AntCommandUtil(buildDir, false);
        List<String> command = null;
        try {
            command = antUtil.getAntCommand(BUILD_ANT_TARGET);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error generating ant build command: " + ex.getMessage());
        }
        Process proc = null;
        try {
            proc = Runtime.getRuntime().exec(command.toArray(new String[command.size()]));
            StreamGobbler errGobbler = new StreamGobbler(
                proc.getErrorStream(), "ERR", System.err);
            StreamGobbler outGobbler = new StreamGobbler(
                proc.getInputStream(), "OUT", System.out);
            errGobbler.start();
            outGobbler.start();
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error executing ant build command: " + ex.getMessage());
        }
        int status = proc.waitFor();
        assertEquals("Build process exited abnormaly", 0, status);
    }
}
