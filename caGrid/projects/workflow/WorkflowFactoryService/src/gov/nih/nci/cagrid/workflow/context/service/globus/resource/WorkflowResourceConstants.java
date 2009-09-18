package gov.nih.nci.cagrid.workflow.context.service.globus.resource;

import javax.xml.namespace.QName;


public interface WorkflowResourceConstants {
	public static final String SERVICE_NS = "http://workflow.cagrid.nci.nih.gov/WorkflowServiceImpl";
	public static final QName RESOURCE_KEY = new QName(SERVICE_NS, "WorkflowServiceImplKey");
	public static final QName RESOURCE_PROPERY_SET = new QName(SERVICE_NS, "WorkflowServiceImplResourceProperties");

	//Service level metadata (exposed as resouce properties)
	public static final QName WORKFLOWSTATUSELEMENT_MD_RP = new QName("http://types.workflow.cagrid.nci.nih.gov/WorkflowManagementFactoryService", "WorkflowStatusElement");
	
}
