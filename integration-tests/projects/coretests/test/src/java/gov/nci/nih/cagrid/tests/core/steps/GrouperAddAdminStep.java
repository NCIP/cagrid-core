/*
 * Created on Sep 8, 2006
 */
package gov.nci.nih.cagrid.tests.core.steps;

import gov.nci.nih.cagrid.tests.core.util.AntUtils;
import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.io.File;
import java.util.Properties;


public class GrouperAddAdminStep extends Step {
    private File grouperDir;
    private String userName;


    public GrouperAddAdminStep(File grouperDir, String userName) {
        super();

        this.grouperDir = grouperDir;
        this.userName = userName;
    }


    @Override
    public void runStep() throws Throwable {
        Properties sysProps = new Properties();
        sysProps.setProperty("gridId.input", this.userName);

        AntUtils.runAnt(this.grouperDir, null, "addAdmin", sysProps, null);
    }
}
