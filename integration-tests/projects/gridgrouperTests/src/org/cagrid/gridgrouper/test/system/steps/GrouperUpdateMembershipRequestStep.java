package org.cagrid.gridgrouper.test.system.steps;

import gov.nih.nci.cagrid.gridgrouper.bean.GroupIdentifier;
import gov.nih.nci.cagrid.gridgrouper.bean.MembershipRequestStatus;
import gov.nih.nci.cagrid.gridgrouper.bean.MembershipRequestUpdate;
import gov.nih.nci.cagrid.gridgrouper.client.GridGrouperClient;
import gov.nih.nci.cagrid.testing.system.haste.Step;

public class GrouperUpdateMembershipRequestStep extends Step {
	private String endpoint;
	private String group;
	private String subject;
	private boolean shouldFail = false;

	public GrouperUpdateMembershipRequestStep(String group, String subject, String endpoint) {
		this(group, subject, false, endpoint);
	}

	public GrouperUpdateMembershipRequestStep(String group, String subject, boolean shouldFail, String endpoint) {
		super();

		this.endpoint = endpoint;
		this.group = group;
		this.subject = subject;
		this.shouldFail = shouldFail;
	}

	@Override
	public void runStep() throws Exception {
		GridGrouperClient grouper = new GridGrouperClient(this.endpoint);
		grouper.setAnonymousPrefered(false);

		try {
			MembershipRequestUpdate update = new MembershipRequestUpdate("", MembershipRequestStatus.Approved);
			grouper.updateMembershipRequest(new GroupIdentifier(null, this.group), this.subject, update);
			if (this.shouldFail) {
				fail("updateMembershipRequest should fail");
			}
		} catch (Exception e) {
			if (!this.shouldFail) {
				throw e;
			}
		}
	}

}
