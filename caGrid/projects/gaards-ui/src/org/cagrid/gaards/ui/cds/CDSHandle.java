package org.cagrid.gaards.ui.cds;

import gov.nih.nci.cagrid.common.Utils;

import org.cagrid.gaards.cds.client.DelegationAdminClient;
import org.cagrid.gaards.cds.client.DelegationUserClient;
import org.cagrid.gaards.ui.common.ServiceHandle;
import org.cagrid.grape.configuration.ServiceDescriptor;
import org.globus.gsi.GlobusCredential;
import org.globus.wsrf.impl.security.authorization.IdentityAuthorization;


public class CDSHandle extends ServiceHandle {

    public CDSHandle(ServiceDescriptor des) {
        super(des);
    }


    public DelegationAdminClient getAdminClient(GlobusCredential credential) throws Exception {
        DelegationAdminClient client = new DelegationAdminClient(getServiceDescriptor().getServiceURL(), credential);
        if (Utils.clean(getServiceDescriptor().getServiceIdentity()) != null) {
            IdentityAuthorization auth = new IdentityAuthorization(getServiceDescriptor().getServiceIdentity());
            client.setAuthorization(auth);
        }
        return client;
    }


    public DelegationUserClient getUserClient(GlobusCredential credential) throws Exception {
        DelegationUserClient client = new DelegationUserClient(getServiceDescriptor().getServiceURL(), credential);
        if (Utils.clean(getServiceDescriptor().getServiceIdentity()) != null) {
            IdentityAuthorization auth = new IdentityAuthorization(getServiceDescriptor().getServiceIdentity());
            client.setAuthorization(auth);
        }
        return client;
    }
}
