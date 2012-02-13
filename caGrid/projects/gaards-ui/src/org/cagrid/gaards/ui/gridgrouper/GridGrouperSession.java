package org.cagrid.gaards.ui.gridgrouper;

import gov.nih.nci.cagrid.gridgrouper.client.GridGrouper;

import org.globus.gsi.GlobusCredential;


public class GridGrouperSession {

    private GridGrouperHandle handle;
    private GlobusCredential credential;


    public GridGrouperSession(GridGrouperHandle handle, GlobusCredential credential) {
        this.handle = handle;
        this.credential = credential;
    }


    public GridGrouper getClient() {
        return this.handle.getClient(this.credential);
    }


    public String getIdentity() {
        if (credential != null) {
            return credential.getIdentity();
        } else {
            return "Anonymous";
        }
    }
    
    public String getServiceURL(){
        return handle.getServiceURL();
    }

}
