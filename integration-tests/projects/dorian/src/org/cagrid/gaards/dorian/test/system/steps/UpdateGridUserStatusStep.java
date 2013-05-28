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
package org.cagrid.gaards.dorian.test.system.steps;

import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.util.List;

import org.cagrid.gaards.dorian.client.GridAdministrationClient;
import org.cagrid.gaards.dorian.federation.GridUser;
import org.cagrid.gaards.dorian.federation.GridUserFilter;
import org.cagrid.gaards.dorian.federation.GridUserStatus;

public class UpdateGridUserStatusStep extends Step {

	private String serviceURL;
	private GridCredentialRequestStep admin;
	private GridCredentialRequestStep user;
	private GridUserStatus status;

	public UpdateGridUserStatusStep(String serviceURL,
			GridCredentialRequestStep admin, GridCredentialRequestStep user, GridUserStatus status) {
		this.serviceURL = serviceURL;
		this.admin = admin;
		this.user = user;
		this.status = status;
	}

	public void runStep() throws Throwable {
		GridAdministrationClient client = new GridAdministrationClient(
				serviceURL, this.admin.getGridCredential());
		GridUserFilter f = new GridUserFilter();
		f.setGridId(user.getGridCredential().getIdentity());
		List<GridUser> users = client.findUsers(f);
		assertNotNull(users);
		assertEquals(1, users.size());
		GridUser usr = users.get(0);
		usr.setUserStatus(this.status);
		client.updateUser(usr);
		List<GridUser> users2 = client.findUsers(f);
		assertNotNull(users2);
		assertEquals(1, users2.size());
		assertEquals(usr, users2.get(0));

	}

}
