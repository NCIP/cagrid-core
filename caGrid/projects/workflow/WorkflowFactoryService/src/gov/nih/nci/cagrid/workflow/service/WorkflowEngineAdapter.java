package gov.nih.nci.cagrid.workflow.service;

import gov.nih.nci.cagrid.workflow.stubs.types.StartInputType;
import gov.nih.nci.cagrid.workflow.stubs.types.WSDLReferences;
import gov.nih.nci.cagrid.workflow.stubs.types.WorkflowException;
import gov.nih.nci.cagrid.workflow.stubs.types.WorkflowStatusType;

public interface WorkflowEngineAdapter {
	
	public String deployWorkflow(String bpelFileName, 
			String workflowName, 
			WSDLReferences[] wsdlRefArray) throws WorkflowException;
	
	public WorkflowStatusType startWorkflow(StartInputType startInput) throws WorkflowException;
	
	public WorkflowStatusType getWorkflowStatus() throws WorkflowException;
	
	public void suspend() throws WorkflowException;
	
	public void resume() throws WorkflowException;
	
	public void cancel() throws WorkflowException;
	
}