package org.cagrid.tests.data.styles.cacore42.steps;

import gov.nih.nci.cagrid.common.StreamGobbler;
import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.tests.data.styles.cacore42.AntCommandUtil;
import org.cagrid.tests.data.styles.cacore42.ExampleProjectInfo;
import org.cagrid.tests.data.styles.cacore42.ExecutableCommand;


public class BuildExampleProjectStep extends Step {
    
    // builds the system with NO CONTAINERS DOWNLOADED!
    public static final String BUILD_ANT_TARGET = "build-system";
    
    private static Log LOG = LogFactory.getLog(BuildExampleProjectStep.class);
    
    public BuildExampleProjectStep() {
        super();
    }


    public void runStep() throws Throwable {
        File buildDir = new File(ExampleProjectInfo.getExampleProjectDir(), "build");
        AntCommandUtil antUtil = new AntCommandUtil(buildDir, false);
        ExecutableCommand command = null;
        try {
            command = antUtil.getAntCommand(BUILD_ANT_TARGET);
        } catch (Exception ex) {
            String message = "Error generating ant build command: " + ex.getMessage();
            LOG.error(message, ex);
            fail(message);
        }
        Process proc = null;
        try {
            System.out.println(command);
            proc = Runtime.getRuntime().exec(command.getCommandArray(), command.getEnvironmentArray());
            StreamGobbler errGobbler = new StreamGobbler(
                proc.getErrorStream(), "ERR", System.err);
            StreamGobbler outGobbler = new StreamGobbler(
                proc.getInputStream(), "OUT", System.out);
            errGobbler.start();
            outGobbler.start();
        } catch (Exception ex) {
            String message = "Error executing ant build command: " + ex.getMessage();
            LOG.error(message, ex);
            fail(message);
        }
        int status = proc.waitFor();
        assertEquals("Build process exited abnormaly", 0, status);
    }
}
