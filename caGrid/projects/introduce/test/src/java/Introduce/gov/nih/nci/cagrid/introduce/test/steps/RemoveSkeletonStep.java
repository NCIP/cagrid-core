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
package gov.nih.nci.cagrid.introduce.test.steps;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.introduce.test.TestCaseInfo;

import java.io.File;

public class RemoveSkeletonStep extends BaseStep {
	private TestCaseInfo tci;

	public RemoveSkeletonStep(TestCaseInfo tci) throws Exception {
		super(tci.getDir(), false);
		this.tci = tci;
	}
    

	public void runStep() throws Throwable {
		System.out.println("Removing the service skeleton");

		Utils.deleteDir(new File(getBaseDir()
				+ File.separator + tci.getDir()));
		//assertTrue(results);
	}
}
