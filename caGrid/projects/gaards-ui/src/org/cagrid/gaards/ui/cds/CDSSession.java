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
