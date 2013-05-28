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
package org.cagrid.gaards.authentication.test.system.steps;

import gov.nih.nci.cagrid.opensaml.SAMLAssertion;
import gov.nih.nci.cagrid.testing.system.haste.Step;


public abstract class BaseAuthenticationStep extends Step {
    private String serviceURL;
    private AuthenticationOutcome outcome;
    private SAMLAssertion saml;


    public BaseAuthenticationStep(String serviceURL, AuthenticationOutcome outcome) {
        this.serviceURL = serviceURL;
        this.outcome = outcome;
    }


    public abstract SAMLAssertion authenticate() throws Exception;


    public final void runStep() throws Throwable {
        Exception error = null;
        try {
            this.saml = null;
            this.saml = authenticate();
        } catch (Exception e) {
            error = e;
            error.printStackTrace();
        }
        outcome.check(saml, error);
    }


    public String getServiceURL() {
        return serviceURL;
    }


    public SAMLAssertion getSAML() {
        return saml;
    }
}
