package org.cagrid.gaards.ui.cds;

import org.cagrid.gaards.cds.client.DelegationAdminClient;
import org.cagrid.gaards.cds.client.DelegationUserClient;
import org.globus.gsi.GlobusCredential;


public class CDSSession {

    private CDSHandle cds;
    private GlobusCredential credential;


    public CDSSession(CDSHandle cds, GlobusCredential credential) {
        this.cds = cds;
        this.credential = credential;
    }


    public DelegationAdminClient getAdminClient() throws Exception {
        return this.cds.getAdminClient(credential);
    }


    public DelegationUserClient getUserClient() throws Exception {
        return this.cds.getUserClient(credential);
    }

}
