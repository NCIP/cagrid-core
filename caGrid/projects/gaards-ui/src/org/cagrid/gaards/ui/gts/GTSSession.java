package org.cagrid.gaards.ui.gts;

import gov.nih.nci.cagrid.gts.client.GTSAdminClient;
import gov.nih.nci.cagrid.gts.client.GTSPublicClient;

import org.globus.gsi.GlobusCredential;


public class GTSSession {

    private GTSHandle gts;
    private GlobusCredential credential;


    public GTSSession(GTSHandle gts, GlobusCredential credential) {
        this.gts = gts;
        this.credential = credential;
    }


    public GTSAdminClient getAdminClient() throws Exception {
        return this.gts.getAdminClient(credential);
    }


    public GTSPublicClient getUserClient() throws Exception {
        return this.gts.getUserClient();
    }
    
    public GTSHandle getHandle(){
        return this.gts;
    }

}
