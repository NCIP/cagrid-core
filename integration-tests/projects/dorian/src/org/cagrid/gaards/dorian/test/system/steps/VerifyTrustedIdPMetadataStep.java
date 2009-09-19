package org.cagrid.gaards.dorian.test.system.steps;

import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.util.List;

import org.cagrid.gaards.dorian.client.GridUserClient;
import org.cagrid.gaards.dorian.federation.TrustedIdentityProvider;


public class VerifyTrustedIdPMetadataStep extends Step {

    private String serviceURL;
    private String name;
    private String displayName;
    private String authenticationServiceURL;
    private String authenticationServiceIdentity;
    private boolean found;


    public VerifyTrustedIdPMetadataStep(String serviceURL, String name) {
       this(serviceURL,name,true);
    }
    
    public VerifyTrustedIdPMetadataStep(String serviceURL, String name, boolean found) {
        this.serviceURL = serviceURL;
        this.name = name;
        this.found = found;
    }



    public void runStep() throws Throwable {
        GridUserClient client = new GridUserClient(serviceURL);
        List<TrustedIdentityProvider> idps = client.getTrustedIdentityProviders();
        boolean isFound = false;
        for (int i = 0; i < idps.size(); i++) {
            TrustedIdentityProvider idp = idps.get(i);
            if (idp.getName().endsWith(this.name)) {
                isFound = true;
                if (getDisplayName() != null) {
                    assertEquals(getDisplayName(), idp.getDisplayName());
                }

                if (getAuthenticationServiceURL() != null) {
                    assertEquals(getAuthenticationServiceURL(), idp.getAuthenticationServiceURL());
                }

                if (getAuthenticationServiceIdentity() != null) {
                    assertEquals(getAuthenticationServiceIdentity(), idp.getAuthenticationServiceIdentity());
                }

            }
        }
        if ((found) && (!isFound)) {
            fail("The identity provider " + name
                + " was not found as a trusted identity provider when it was expected to be.");
        } else if ((!found) && (isFound)) {
            fail("The identity provider " + name
                + " was found and it should not have been.");
        }
    }


    public String getDisplayName() {
        return displayName;
    }


    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }


    public String getAuthenticationServiceURL() {
        return authenticationServiceURL;
    }


    public void setAuthenticationServiceURL(String authenticationServiceURL) {
        this.authenticationServiceURL = authenticationServiceURL;
    }


    public String getAuthenticationServiceIdentity() {
        return authenticationServiceIdentity;
    }


    public void setAuthenticationServiceIdentity(String authenticationServiceIdentity) {
        this.authenticationServiceIdentity = authenticationServiceIdentity;
    }
}
