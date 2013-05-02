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

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.gts.client.GTSAdminClient;
import gov.nih.nci.cagrid.gts.client.GTSPublicClient;

import org.cagrid.gaards.ui.common.ServiceHandle;
import org.cagrid.grape.configuration.ServiceDescriptor;
import org.globus.gsi.GlobusCredential;
import org.globus.wsrf.impl.security.authorization.IdentityAuthorization;


public class GTSHandle extends ServiceHandle {

    public GTSHandle(ServiceDescriptor des) {
        super(des);
    }


    public GTSAdminClient getAdminClient(GlobusCredential credential) throws Exception {
        GTSAdminClient client = new GTSAdminClient(getServiceDescriptor().getServiceURL(), credential);
        if (Utils.clean(getServiceDescriptor().getServiceIdentity()) != null) {
            IdentityAuthorization auth = new IdentityAuthorization(getServiceDescriptor().getServiceIdentity());
            client.setAuthorization(auth);
        }
        return client;
    }


    public GTSPublicClient getUserClient() throws Exception {
        GTSPublicClient client = new GTSPublicClient(getServiceDescriptor().getServiceURL());
        if (Utils.clean(getServiceDescriptor().getServiceIdentity()) != null) {
            IdentityAuthorization auth = new IdentityAuthorization(getServiceDescriptor().getServiceIdentity());
            client.setAuthorization(auth);
        }
        return client;
    }
}
