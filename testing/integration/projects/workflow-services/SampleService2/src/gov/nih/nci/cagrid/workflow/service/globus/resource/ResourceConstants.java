package gov.nih.nci.cagrid.workflow.service.globus.resource;

import javax.xml.namespace.QName;


public interface ResourceConstants {
	public static final String SERVICE_NS = "http://workflow.cagrid.nci.nih.gov/SampleService2";
	public static final QName RESOURCE_KEY = new QName(SERVICE_NS, "SampleService2Key");
	public static final QName RESOURCE_PROPERTY_SET = new QName(SERVICE_NS, "SampleService2ResourceProperties");

	//Service level metadata (exposed as resouce properties)
	
}
