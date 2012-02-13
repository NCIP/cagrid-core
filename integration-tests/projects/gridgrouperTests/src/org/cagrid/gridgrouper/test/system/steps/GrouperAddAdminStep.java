package org.cagrid.gridgrouper.test.system.steps;

import gov.nih.nci.cagrid.introduce.common.AntTools;
import gov.nih.nci.cagrid.introduce.common.CommonTools;
import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.io.File;
import java.util.List;

public class GrouperAddAdminStep extends Step {
	private File grouperDir;
	private String userName;

	public GrouperAddAdminStep(File grouperDir, String userName) {
		super();

		this.grouperDir = grouperDir;
		this.userName = userName;
	}

	public void runStep() throws Throwable {
		
        List<String> cmd = AntTools.getAntCommand("-DgridId.input=" + userName + " addAdmin", grouperDir.getAbsolutePath());

        Process p = CommonTools.createAndOutputProcess(cmd);
        p.waitFor();
        assertEquals("Build process exited abnormally", 0, p.exitValue());
        p.destroy();

	}

}
