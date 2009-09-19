package gov.nih.nci.cagrid.workflow.samples.secure.service.globus.resource;

import javax.xml.namespace.QName;


public interface ResourceConstants {
	public static final String SERVICE_NS = "http://cagrid.nci.nih.gov/SecureSample/SecureSample";
	public static final QName RESOURCE_KEY = new QName(SERVICE_NS, "SecureSampleKey");
	public static final QName RESOURCE_PROPERY_SET = new QName(SERVICE_NS, "SecureSampleResourceProperties");

	//Service level metadata (exposed as resouce properties)
	
}
