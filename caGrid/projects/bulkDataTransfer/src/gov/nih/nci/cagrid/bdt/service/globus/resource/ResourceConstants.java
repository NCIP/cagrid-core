package gov.nih.nci.cagrid.bdt.service.globus.resource;

import javax.xml.namespace.QName;


public interface ResourceConstants {
	public static final String SERVICE_NS = "http://cagrid.nci.nih.gov/BulkDataHandler";
	public static final QName RESOURCE_KEY = new QName(SERVICE_NS, "BulkDataHandlerKey");
	public static final QName RESOURCE_PROPERY_SET = new QName(SERVICE_NS, "BulkDataHandlerResourceProperties");

	//Service level metadata (exposed as resouce properties)
	
}
