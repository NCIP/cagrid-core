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
package org.cagrid.gaards.ui.dorian;

import gov.nih.nci.cagrid.common.Runner;
import gov.nih.nci.cagrid.metadata.exceptions.RemoteResourcePropertyRetrievalException;

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

        } catch (RemoteResourcePropertyRetrievalException e) {
        	String errMsg = e.getMessage();
        	log.warn(errMsg);        		
            log.debug(errMsg, e);
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
        }
    }
}
