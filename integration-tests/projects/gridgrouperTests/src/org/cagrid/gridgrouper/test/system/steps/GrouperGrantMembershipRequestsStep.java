package org.cagrid.gridgrouper.test.system.steps;

import gov.nih.nci.cagrid.gridgrouper.bean.GroupIdentifier;
import gov.nih.nci.cagrid.gridgrouper.client.GridGrouperClient;
import gov.nih.nci.cagrid.testing.system.haste.Step;

public class GrouperGrantMembershipRequestsStep extends Step {
	private String endpoint;
	private String group;

	public GrouperGrantMembershipRequestsStep(String group, String endpoint) {
		super();

		this.endpoint = endpoint;
		this.group = group;
	}

	@Override
	public void runStep() throws Exception {
		GridGrouperClient grouper = new GridGrouperClient(this.endpoint);
		grouper.setAnonymousPrefered(false);

		grouper.grantMembershipRequests(new GroupIdentifier(null, this.group));
	}
}
