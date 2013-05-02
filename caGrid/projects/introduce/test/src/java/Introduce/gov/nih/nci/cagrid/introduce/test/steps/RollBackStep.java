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

import gov.nih.nci.cagrid.introduce.common.ResourceManager;
import gov.nih.nci.cagrid.introduce.test.TestCaseInfo;


public class RollBackStep extends BaseStep {
	private TestCaseInfo tci;


	public RollBackStep(TestCaseInfo tci) throws Exception {
		super(tci.getDir(),false);
		this.tci = tci;
	}


	public void runStep() throws Throwable {
		System.out.println("Rolling back to previous version.");

		ResourceManager.restoreLatest(String.valueOf(System.currentTimeMillis()), tci.getName(), tci.getDir());

		// check to see that this is same as before....
	}
}
