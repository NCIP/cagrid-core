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
import gov.nih.nci.cagrid.gridgrouper.bean.GroupPrivilegeType;
import gov.nih.nci.cagrid.gridgrouper.bean.StemIdentifier;
import gov.nih.nci.cagrid.gridgrouper.bean.StemPrivilegeType;
import gov.nih.nci.cagrid.gridgrouper.client.GridGrouperClient;
import gov.nih.nci.cagrid.testing.system.haste.Step;

public class GrouperRevokePrivilegeStep extends Step {
	private String endpoint;
	private String path;
	private String subject;
	private String priv;

	public GrouperRevokePrivilegeStep(String path, String subject, String priv, String endpoint) {
		super();

		this.endpoint = endpoint;
		this.path = path;
		this.subject = subject;
		this.priv = priv;
	}

	@Override
	public void runStep() throws Exception {
		GridGrouperClient grouper = new GridGrouperClient(this.endpoint);
		grouper.setAnonymousPrefered(false);
		// group or stem?
		boolean isGroup = false;
		try {
			grouper.getGroup(new GroupIdentifier(null, this.path));
			isGroup = true;
		} catch (Exception e) {
			isGroup = false;
		}

		// grant
		if (isGroup) {
			grouper.revokeGroupPrivilege(new GroupIdentifier(null, this.path), this.subject, GroupPrivilegeType
					.fromString(this.priv));
		} else {
			grouper.revokeStemPrivilege(new StemIdentifier(null, this.path), this.subject, StemPrivilegeType.fromString(this.priv));
		}
	}

}
