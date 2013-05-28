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

import gov.nih.nci.cagrid.gridgrouper.bean.StemIdentifier;
import gov.nih.nci.cagrid.gridgrouper.client.GridGrouperClient;
import gov.nih.nci.cagrid.testing.system.haste.Step;

public class GrouperRemoveStemStep extends Step {
	private String endpoint;
	private String stem;
	private boolean shouldFail;

	public GrouperRemoveStemStep(String stem, String endpoint) {
		this(stem, false, endpoint);
	}

	public GrouperRemoveStemStep(String stem, boolean shouldFail, String endpoint) {
		super();

		this.endpoint = endpoint;
		this.stem = stem;
		this.shouldFail = shouldFail;
	}

	@Override
	public void runStep() throws Exception {
		GridGrouperClient grouper = new GridGrouperClient(this.endpoint);
		grouper.setAnonymousPrefered(false);
		// remove stem
		try {
			grouper.deleteStem(new StemIdentifier(null, this.stem));
			if (this.shouldFail) {
				fail("deleteMember should fail");
			}
		} catch (Exception e) {
			if (!this.shouldFail) {
				throw e;
			}
		}
	}

}
