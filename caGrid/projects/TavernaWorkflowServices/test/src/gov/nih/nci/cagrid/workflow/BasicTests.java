package gov.nih.nci.cagrid.workflow;

import gov.nih.nci.cagrid.workflow.factory.client.TavernaWorkflowServiceClient;
import gov.nih.nci.cagrid.workflow.service.impl.client.TavernaWorkflowServiceImplClient;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import junit.framework.TestCase;

import org.apache.axis.message.addressing.EndpointReferenceType;

import workflowmanagementfactoryservice.WorkflowOutputType;
import workflowmanagementfactoryservice.WorkflowStatusType;

public class BasicTests extends TestCase {

	public String url = "http://localhost:8080/wsrf/services/cagrid/WorkflowFactoryService";
	private String scuflDoc = "";
	private String outputFile = "";
	private static TavernaWorkflowServiceClient factoryClient = null;
	private static TavernaWorkflowServiceImplClient serviceClient = null;
	private static EndpointReferenceType epr = null;
	
	private  Properties props = new Properties();
	
	public BasicTests(String name) {
		super(name);
		setup();
		// TODO Auto-generated constructor stub
	}
	
	private void setup() {
		try {
	        this.props.load(new FileInputStream("test.properties"));
	        this.url= this.props.getProperty("WORKFLOW_ENDPOINT");
	        this.scuflDoc = this.props.getProperty("WORKFLOW_INPUT");
	        this.outputFile = this.props.getProperty("WORKFLOW_OUPUT");
	    } catch (IOException e) {
	    	System.err.println("Cannot read test props");
	    }
	}
	
	public void testBasic() throws Exception {
		//assertTrue(TEST_CONTAINER != null);
		System.out.println("TESTING");
		this.epr = TavernaWorkflowServiceClient.setupWorkflow(this.url, this.scuflDoc, "TEST1");		
		assertTrue(this.epr != null);
		
		WorkflowStatusType status = TavernaWorkflowServiceClient.startWorkflow(new String[]{"Dina", " Sulakhe"}, this.epr);
		assertTrue(status.equals(WorkflowStatusType.Active) 
				|| status.equals(WorkflowStatusType.Pending)
				|| status.equals(WorkflowStatusType.Done));
	}
	
	public void testStatus() throws Exception {
	
		WorkflowStatusType status = TavernaWorkflowServiceClient.getStatus(this.epr);
		for(int i=0; (i<5 && !status.equals(WorkflowStatusType.Done)); i++)
		{
			Thread.sleep(5000);
			status = TavernaWorkflowServiceClient.getStatus(epr);
		}
		assertTrue(status.equals(WorkflowStatusType.Done));
	
	}
	
	public void testGetOutput() throws Exception {
		WorkflowOutputType output = TavernaWorkflowServiceClient.getOutput(this.epr);
		System.out.println("result: " + output.getOutputFile()[0]);
		assertTrue(output != null);
		assertTrue(output.getOutputFile()[0] != null);
		//assertTrue(output.getOutputFile()[0].equals("Dina Sulakhe"));
	}

	
}
