package gov.nih.nci.cagrid.introduce.test.steps;

import gov.nih.nci.cagrid.introduce.common.AntTools;
import gov.nih.nci.cagrid.introduce.common.CommonTools;
import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.io.File;
import java.util.List;


public abstract class BaseStep extends Step {

    private String baseDir;
    private String serviceDir;
    private boolean build;


    public BaseStep(String serviceDir, boolean build) throws Exception {
        baseDir = System.getProperty("basedir");
        if (baseDir == null) {
            System.err.println("basedir system property not set");
            throw new Exception("basedir system property not set");
        }
        this.serviceDir = serviceDir;
        this.build = build;
    }


    public String getBaseDir() {
        return baseDir;
    }


    public abstract void runStep() throws Throwable;


    public void buildStep() throws Throwable {
        if (build) {
            List<String> cmd = AntTools.getAntAllCommand(new File(baseDir + File.separator + serviceDir).getAbsolutePath());

            Process p = CommonTools.createAndOutputProcess(cmd);
            p.waitFor();
            assertEquals("Build process exited abnormally", 0, p.exitValue());
            p.destroy();
        }
    }
}
