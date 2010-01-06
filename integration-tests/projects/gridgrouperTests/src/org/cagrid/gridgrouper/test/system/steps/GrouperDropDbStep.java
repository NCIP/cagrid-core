package org.cagrid.gridgrouper.test.system.steps;

import gov.nih.nci.cagrid.introduce.common.AntTools;
import gov.nih.nci.cagrid.introduce.common.CommonTools;
import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.io.File;
import java.util.List;

public class GrouperDropDbStep extends Step {

	private File grouperDir;

	public GrouperDropDbStep(String gridGrouperTestDir) {
		grouperDir = new File(gridGrouperTestDir);
	}

	@Override
	public void runStep() throws Exception {
        List<String> cmd = AntTools.getAntCommand("dropTestDatabase", grouperDir.getAbsolutePath());

        Process p = CommonTools.createAndOutputProcess(cmd);
        p.waitFor();
        assertEquals("Build process exited abnormally", 0, p.exitValue());
        p.destroy();
	}
        
}
