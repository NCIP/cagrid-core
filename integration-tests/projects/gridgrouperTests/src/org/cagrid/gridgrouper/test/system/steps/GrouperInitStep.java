/**
*============================================================================
*  Copyright The Ohio State University Research Foundation, The University of Chicago - 
*	Argonne National Laboratory, Emory University, SemanticBits LLC, and 
*	Ekagra Software Technologies Ltd.
*
*  Distributed under the OSI-approved BSD 3-Clause License.
*  See http://ncip.github.com/cagrid-core/LICENSE.txt for details.
*============================================================================
**/
package org.cagrid.gridgrouper.test.system.steps;

import gov.nih.nci.cagrid.introduce.common.AntTools;
import gov.nih.nci.cagrid.introduce.common.CommonTools;
import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.io.File;
import java.util.List;

public class GrouperInitStep extends Step {
	private File grouperDir;

	public GrouperInitStep(File grouperDir) {
		super();

		this.grouperDir = grouperDir;
	}

	public void runStep() throws Throwable {

        List<String> cmd = AntTools.getAntCommand("grouperInit", grouperDir.getAbsolutePath());

        Process p = CommonTools.createAndOutputProcess(cmd);
        p.waitFor();
        assertEquals("Build process exited abnormally", 0, p.exitValue());
        p.destroy();
	}
}
