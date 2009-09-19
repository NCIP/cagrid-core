package org.cagrid.gaards.dorian.test.system.steps;

import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.util.List;

import org.cagrid.gaards.dorian.client.GridAdministrationClient;
import org.cagrid.gaards.dorian.federation.TrustedIdP;
import org.cagrid.gaards.dorian.federation.TrustedIdPStatus;


public class UpdateTrustedIdPStatusStep extends Step {

    private String serviceURL;
    private GridCredentialRequestStep admin;
    private String name;
    private TrustedIdPStatus status;


    public UpdateTrustedIdPStatusStep(String serviceURL, GridCredentialRequestStep admin, String name,
        TrustedIdPStatus status) {
        this.serviceURL = serviceURL;
        this.admin = admin;
        this.name = name;
        this.status = status;
    }


    public void runStep() throws Throwable {
        GridAdministrationClient client = new GridAdministrationClient(serviceURL, this.admin.getGridCredential());
        List<TrustedIdP> idps = client.getTrustedIdPs();
        boolean found = false;
        for (int i = 0; i < idps.size(); i++) {
            TrustedIdP idp = idps.get(i);
            if (idp.getName().endsWith(this.name)) {
                found = true;
               idp.setStatus(status);
               client.updateTrustedIdP(idp);
            }
        }
        if (!found) {
            fail("Could not update the identity provider " + name
                + ", it was not found.");
        }
    }
}
