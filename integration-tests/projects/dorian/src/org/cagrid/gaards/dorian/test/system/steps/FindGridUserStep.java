package org.cagrid.gaards.dorian.test.system.steps;

import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.util.List;

import org.cagrid.gaards.dorian.client.GridAdministrationClient;
import org.cagrid.gaards.dorian.federation.GridUser;
import org.cagrid.gaards.dorian.federation.GridUserFilter;
import org.cagrid.gaards.dorian.federation.GridUserStatus;

public class FindGridUserStep extends Step {

	private String serviceURL;
	private GridUserStatus status;
	private GridCredentialRequestStep admin;
	private GridUser gridUser;
	private String localUserId;
	private String firstName;
	private String lastName;
	private String email;
	private GridCredentialRequestStep gridCredential;

	public FindGridUserStep(String serviceURL, GridCredentialRequestStep admin,
			GridCredentialRequestStep user) {
		this.serviceURL = serviceURL;
		this.admin = admin;
		this.gridCredential = user;
	}

	public GridUser getGridUser() {
		return gridUser;
	}

	public void setExpectedStatus(GridUserStatus status) {
		this.status = status;
	}

	public void setExpectedLocalUserId(String localUserId) {
		this.localUserId = localUserId;
	}

	public void setExpectedFirstName(String firstName) {
		this.firstName = firstName;
	}

	public void setExpectedLastName(String lastName) {
		this.lastName = lastName;
	}

	public void setExpectedEmail(String email) {
		this.email = email;
	}

	public void runStep() throws Throwable {
		GridAdministrationClient client = new GridAdministrationClient(
				serviceURL, this.admin.getGridCredential());
		assertNotNull(gridCredential.getGridCredential());
		GridUserFilter filter = new GridUserFilter();
		filter.setGridId(this.gridCredential.getGridCredential().getIdentity());
		List<GridUser> users = client.findUsers(filter);
		assertNotNull(users);
		assertEquals(1, users.size());
		GridUser u = users.get(0);

		assertEquals(gridCredential.getGridCredential().getIdentity(), u
				.getGridId());

		if (localUserId != null) {
			assertEquals(localUserId, u.getUID());
		}
		if (firstName != null) {
			assertEquals(firstName, u.getFirstName());
		}
		if (lastName != null) {
			assertEquals(lastName, u.getLastName());
		}
		if (email != null) {
			assertEquals(email, u.getEmail());
		}
		if (status != null) {
			assertEquals(status, u.getUserStatus());
		}
		this.gridUser = u;
	}

}
