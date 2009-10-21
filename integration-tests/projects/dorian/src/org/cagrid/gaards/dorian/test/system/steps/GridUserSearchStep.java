package org.cagrid.gaards.dorian.test.system.steps;

import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.util.List;

import org.cagrid.gaards.dorian.client.GridUserClient;
import org.cagrid.gaards.dorian.federation.GridUserRecord;
import org.cagrid.gaards.dorian.federation.GridUserSearchCriteria;


public class GridUserSearchStep extends Step {

    private String serviceURL;
    private GridUserRecord gridUser;
    private String firstName;
    private String lastName;
    private String email;
    private GridCredentialRequestStep gridCredential;


    public GridUserSearchStep(String serviceURL, GridCredentialRequestStep user) {
        this.serviceURL = serviceURL;
        this.gridCredential = user;
    }


    public GridUserRecord getGridUser() {
        return gridUser;
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
        GridUserClient client = new GridUserClient(serviceURL, null, true);
        assertNotNull(gridCredential.getGridCredential());
        GridUserSearchCriteria filter = new GridUserSearchCriteria();
        filter.setIdentity(this.gridCredential.getGridCredential().getIdentity());
        List<GridUserRecord> users = client.userSearch(filter);
        assertNotNull(users);
        assertEquals(1, users.size());
        GridUserRecord u = users.get(0);

        assertEquals(gridCredential.getGridCredential().getIdentity(), u.getIdentity());

        if (firstName != null) {
            assertEquals(firstName, u.getFirstName());
        }
        if (lastName != null) {
            assertEquals(lastName, u.getLastName());
        }
        if (email != null) {
            assertEquals(email, u.getEmail());
        }

        this.gridUser = u;
    }
}
