package org.cagrid.gaards.dorian.test.system.steps;

import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.util.List;

import org.cagrid.gaards.dorian.client.LocalAdministrationClient;
import org.cagrid.gaards.dorian.idp.LocalUser;
import org.cagrid.gaards.dorian.idp.LocalUserFilter;
import org.cagrid.gaards.dorian.idp.LocalUserStatus;

public class UpdateLocalUserStatusStep extends Step {

	private String serviceURL;
	private GridCredentialRequestStep admin;
	private String localUser;
	private LocalUserStatus status;

	public UpdateLocalUserStatusStep(String serviceURL,
			GridCredentialRequestStep admin, String uid, LocalUserStatus status) {
		this.serviceURL = serviceURL;
		this.admin = admin;
		this.localUser = uid;
		this.status = status;
	}

	public void runStep() throws Throwable {
		LocalAdministrationClient client = new LocalAdministrationClient(
				serviceURL, this.admin.getGridCredential());
		LocalUserFilter f = new LocalUserFilter();
		f.setUserId(this.localUser);
		List<LocalUser> users = client.findUsers(f);
		assertNotNull(users);
		assertEquals(1, users.size());
		LocalUser usr = users.get(0);
		usr.setStatus(this.status);
		client.updateUser(usr);
		List<LocalUser> users2 = client.findUsers(f);
		assertNotNull(users2);
		assertEquals(1, users2.size());
		assertEquals(usr, users2.get(0));

	}

}
