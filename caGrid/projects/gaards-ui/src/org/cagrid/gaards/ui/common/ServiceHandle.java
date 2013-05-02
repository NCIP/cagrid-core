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
package org.cagrid.gaards.ui.common;

import org.cagrid.grape.configuration.ServiceDescriptor;


public class ServiceHandle {

    private ServiceDescriptor des;


    public ServiceHandle(ServiceDescriptor des) {
        this.des = des;
    }


    public String toString() {
        return des.getDisplayName();
    }


    public ServiceDescriptor getServiceDescriptor() {
        return des;
    }


    public String getServiceURL() {
        return getServiceDescriptor().getServiceURL();
    }


    public String getDisplayName() {
        return des.getDisplayName();
    }
}
