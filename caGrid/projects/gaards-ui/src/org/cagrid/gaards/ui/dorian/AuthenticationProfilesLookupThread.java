package org.cagrid.gaards.ui.dorian;

import gov.nih.nci.cagrid.common.Runner;

import org.apache.log4j.Logger;
import org.cagrid.gaards.authentication.client.AuthenticationClient;


public class AuthenticationProfilesLookupThread extends Runner {

    private Logger log;
    private AuthenticationServiceHandle handle;


    public AuthenticationProfilesLookupThread(AuthenticationServiceHandle handle) {
        this.log = Logger.getLogger(getClass());
        this.handle = handle;
    }


    public void execute() {
        try {
            AuthenticationClient client = this.handle.getAuthenticationClient();
            handle.setAuthenticationProfiles(client.getSupportedAuthenticationProfiles());

        } catch (Throwable e) {
            log.error(e.getMessage(), e);
        }
    }
}
