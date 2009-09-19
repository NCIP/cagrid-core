package gov.nih.nci.cagrid.workflow.service.globus.resource;

import javax.xml.namespace.QName;


public interface ResourceConstants {
	public static final String SERVICE_NS = "http://workflow.cagrid.nci.nih.gov/SampleService1";
	public static final QName RESOURCE_KEY = new QName(SERVICE_NS, "SampleService1Key");
	public static final QName RESOURCE_PROPERY_SET = new QName(SERVICE_NS, "SampleService1ResourceProperties");

	//Service level metadata (exposed as resouce properties)
	
}
