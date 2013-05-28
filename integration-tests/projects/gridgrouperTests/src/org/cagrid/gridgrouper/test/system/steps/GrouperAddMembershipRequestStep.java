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

import gov.nih.nci.cagrid.gridgrouper.bean.GroupIdentifier;
import gov.nih.nci.cagrid.gridgrouper.client.GridGrouperClient;
import gov.nih.nci.cagrid.testing.system.haste.Step;

public class GrouperAddMembershipRequestStep extends Step {
	private String endpoint;
	private String group;
	private boolean shouldFail = false;

	public GrouperAddMembershipRequestStep(String group, String endpoint) {
		this(group, false, endpoint);
	}

	public GrouperAddMembershipRequestStep(String group, boolean shouldFail, String endpoint) {
		super();

		this.endpoint = endpoint;
		this.group = group;
		this.shouldFail = shouldFail;
	}

	@Override
	public void runStep() throws Exception {
		GridGrouperClient grouper = new GridGrouperClient(this.endpoint);
		grouper.setAnonymousPrefered(false);
		try {
			grouper.addMembershipRequest(new GroupIdentifier(null, this.group));
			if (this.shouldFail) {
				fail("addMember should fail");
			}
		} catch (Exception e) {
			if (!this.shouldFail) {
				throw e;
			}
		}
	}

}
