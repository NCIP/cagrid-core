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
    
    public String getServiceURL(){
        return getServiceDescriptor().getServiceURL();
    }
    
    public String getDisplayName(){
        return des.getDisplayName();
    }

}
