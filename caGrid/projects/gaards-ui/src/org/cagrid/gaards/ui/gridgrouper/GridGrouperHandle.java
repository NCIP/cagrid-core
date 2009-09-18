package org.cagrid.gaards.ui.gridgrouper;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.gridgrouper.client.GridGrouper;

import org.cagrid.gaards.ui.common.ServiceHandle;
import org.cagrid.grape.configuration.ServiceDescriptor;
import org.globus.gsi.GlobusCredential;
import org.globus.wsrf.impl.security.authorization.IdentityAuthorization;


public class GridGrouperHandle extends ServiceHandle {

    public GridGrouperHandle(ServiceDescriptor des) {
        super(des);
    }


    public GridGrouper getClient() {
        return getClient(null);
    }


    public GridGrouper getClient(GlobusCredential credential) {
        GridGrouper client = null;
        if (credential == null) {
            client = new GridGrouper(getServiceDescriptor().getServiceURL());
        } else {
            client = new GridGrouper(getServiceDescriptor().getServiceURL(), credential);
        }
        if (Utils.clean(getServiceDescriptor().getServiceIdentity()) != null) {
            IdentityAuthorization auth = new IdentityAuthorization(getServiceDescriptor().getServiceIdentity());
            client.setAuthorization(auth);
        }
        return client;
    }
}
