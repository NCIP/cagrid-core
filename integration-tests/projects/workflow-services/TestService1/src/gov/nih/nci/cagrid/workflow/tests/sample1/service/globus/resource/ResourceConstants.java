package gov.nih.nci.cagrid.workflow.tests.sample1.service.globus.resource;

import javax.xml.namespace.QName;


public interface ResourceConstants {
	public static final String SERVICE_NS = "http://sample1.tests.workflow.cagrid.nci.nih.gov/WorkflowTestService1";
	public static final QName RESOURCE_KEY = new QName(SERVICE_NS, "WorkflowTestService1Key");
	public static final QName RESOURCE_PROPERTY_SET = new QName(SERVICE_NS, "WorkflowTestService1ResourceProperties");

	//Service level metadata (exposed as resouce properties)
	
}
