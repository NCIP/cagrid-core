package gov.nih.nci.cagrid.introduce.codegen.provider.providers;

import gov.nih.nci.cagrid.introduce.beans.service.ServiceType;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;


public interface Provider {

    public void addResourceProvider(ServiceType service, ServiceInformation info) throws ProviderException;

    public void removeResourceProvider(ServiceType service, ServiceInformation info) throws ProviderException;

}
