package org.cagrid.iso21090.tests.integration.steps;

import gov.nih.nci.cagrid.common.StreamGobbler;
import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.iso21090.tests.integration.AntCommandUtil;
import org.cagrid.iso21090.tests.integration.ExampleProjectInfo;
import org.cagrid.iso21090.tests.integration.ExecutableCommand;


public class BuildExampleProjectStep extends Step {
    
    // builds the system with NO CONTAINERS DOWNLOADED!
    /*
    public static final String[] BUILD_TARGETS = {
        "clean:SDK", "clean:all", "build:SDK"
    };
    */
    public static final String[] BUILD_TARGETS = {
        "build:SDK"
    };
    
    private static Log LOG = LogFactory.getLog(BuildExampleProjectStep.class);
    
    public BuildExampleProjectStep() {
        super();
    }


    public void runStep() throws Throwable {
        File buildDir = new File(ExampleProjectInfo.getExampleProjectDir(), "build");
        AntCommandUtil antUtil = new AntCommandUtil(buildDir, false);
        for (String target : BUILD_TARGETS) {
            LOG.info("Executing ant target " + target + " in dir " + buildDir.getAbsolutePath());
            ExecutableCommand command = null;
            try {
                command = antUtil.getAntCommand(target);
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
}
