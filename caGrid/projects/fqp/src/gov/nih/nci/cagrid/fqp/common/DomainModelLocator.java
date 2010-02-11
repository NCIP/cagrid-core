package gov.nih.nci.cagrid.fqp.common;

import gov.nih.nci.cagrid.metadata.dataservice.DomainModel;

public interface DomainModelLocator {

    public DomainModel getDomainModel(String targetServiceUrl) throws Exception;
}
