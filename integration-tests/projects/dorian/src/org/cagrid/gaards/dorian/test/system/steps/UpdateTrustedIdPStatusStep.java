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
import org.cagrid.gaards.dorian.federation.TrustedIdP;
import org.cagrid.gaards.dorian.federation.TrustedIdPStatus;


public class UpdateTrustedIdPStatusStep extends Step {

    private String serviceURL;
    private GridCredentialRequestStep admin;
    private String name;
    private TrustedIdPStatus status;
    private boolean publish;


    public UpdateTrustedIdPStatusStep(String serviceURL, GridCredentialRequestStep admin, String name,
        TrustedIdPStatus status, boolean publish) {
        this.serviceURL = serviceURL;
        this.admin = admin;
        this.name = name;
        this.status = status;
        this.publish = publish;
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
               client.setPublish(idp, publish);
            }
        }
        if (!found) {
            fail("Could not update the identity provider " + name
                + ", it was not found.");
        }
    }
}
