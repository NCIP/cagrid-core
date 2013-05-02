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

import org.cagrid.gaards.dorian.client.GridAdministrationClient;
import org.cagrid.gaards.dorian.client.GridUserClient;
import org.cagrid.gaards.dorian.client.LocalAdministrationClient;
import org.cagrid.gaards.dorian.client.LocalUserClient;
import org.globus.gsi.GlobusCredential;


public class DorianSession {
    private DorianHandle handle;

    private GlobusCredential credential;


    public DorianSession(DorianHandle handle) {
        this(handle, null);
    }


    public DorianSession(DorianHandle handle, GlobusCredential credential) {
        this.handle = handle;
        this.credential = credential;
    }


    public GridAdministrationClient getAdminClient() throws Exception {
        return handle.getAdminClient(credential);
    }


    public GridUserClient getUserClient() throws Exception {
        return handle.getUserClient(this.credential);
    }


    public LocalAdministrationClient getLocalAdminClient() throws Exception {
        return handle.getLocalAdminClient(credential);
    }


    public LocalUserClient getLocalUserClient() throws Exception {
        return handle.getLocalUserClient();
    }


    public GlobusCredential getCredential() {
        return credential;
    }


    public DorianHandle getHandle() {
        return handle;
    }

}
