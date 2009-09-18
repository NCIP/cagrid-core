package gov.nih.nci.cagrid.workflow.tests.sample2.service.globus.resource;

import javax.xml.namespace.QName;


public interface ResourceConstants {
	public static final String SERVICE_NS = "http://sample2.tests.workflow.cagrid.nci.nih.gov/WorkflowTestService2";
	public static final QName RESOURCE_KEY = new QName(SERVICE_NS, "WorkflowTestService2Key");
	public static final QName RESOURCE_PROPERTY_SET = new QName(SERVICE_NS, "WorkflowTestService2ResourceProperties");

	//Service level metadata (exposed as resouce properties)
	
}
