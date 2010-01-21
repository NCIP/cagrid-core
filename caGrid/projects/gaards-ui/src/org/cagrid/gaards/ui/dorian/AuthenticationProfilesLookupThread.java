package org.cagrid.gaards.ui.dorian;

import gov.nih.nci.cagrid.common.Runner;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.gaards.authentication.client.AuthenticationClient;

public class AuthenticationProfilesLookupThread extends Runner {

	private static Log log = LogFactory.getLog(AuthenticationProfilesLookupThread.class);
    private AuthenticationServiceHandle handle;

    public AuthenticationProfilesLookupThread(AuthenticationServiceHandle handle) {
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
