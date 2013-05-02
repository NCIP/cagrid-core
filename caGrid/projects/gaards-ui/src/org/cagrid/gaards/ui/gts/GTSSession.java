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
