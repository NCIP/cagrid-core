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

import org.cagrid.gaards.dorian.client.GridAdministrationClient;
import org.cagrid.gaards.dorian.federation.TrustedIdP;


public class AddTrustedIdPStep extends Step {

    private String serviceURL;

    private GridCredentialRequestStep admin;
    private TrustedIdP idp;


    public AddTrustedIdPStep(String serviceURL, GridCredentialRequestStep admin, TrustedIdP idp) {
        this.serviceURL = serviceURL;
        this.admin = admin;
        this.idp = idp;
    }


    public void runStep() throws Throwable {
        GridAdministrationClient client = new GridAdministrationClient(serviceURL, this.admin.getGridCredential());
        TrustedIdP temp=client.addTrustedIdP(idp);
        idp.setId(temp.getId());
    }


    public TrustedIdP getIdP() {
        return idp;
    }

}
