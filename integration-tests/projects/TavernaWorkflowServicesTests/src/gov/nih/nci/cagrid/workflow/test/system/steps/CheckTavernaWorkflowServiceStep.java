package gov.nih.nci.cagrid.workflow.test.system.steps;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.rmi.RemoteException;

import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.axis.types.URI.MalformedURIException;

import workflowmanagementfactoryservice.WorkflowOutputType;
import workflowmanagementfactoryservice.WorkflowStatusType;

import gov.nih.nci.cagrid.testing.system.haste.Step;
import gov.nih.nci.cagrid.workflow.factory.client.TavernaWorkflowServiceClient;

public class CheckTavernaWorkflowServiceStep extends Step {

	private final EndpointReferenceType twsEPR;
	private static final String SCUFL_FILE = "resources/input/input.t2flow";
	//private static final String OUTPUT_FILE = "resources/output/output.xml";
	private static final String OUTPUT_FILE = System.getProperty("java.io.tmpdir") 
		+ System.getProperty("file.separator") + "output.xml";
	private static final String[] INPUTS = {"TavernaWorkflowService", " Test Successful"};
	
	
	public CheckTavernaWorkflowServiceStep(EndpointReferenceType twsEPR) {
		this.twsEPR = twsEPR;
	}

	@Override
	public void runStep() throws RemoteException {
		assertNotNull("A non-null EPR must be passed in.", this.twsEPR);


		// Create a resource with the givne Input Scufl file.
		System.out.println("1. Creating A Resource with the given Scufl file.\n");
		File scuflDoc = new File(SCUFL_FILE);
		System.out.println(" >> SCUFL_FILE path = " + scuflDoc.getAbsolutePath());
		System.out.println(" >> ADDRESS = " + twsEPR.getAddress().toString());
		
		EndpointReferenceType serviceEPR;
		try {
			serviceEPR = TavernaWorkflowServiceClient.setupWorkflow(
					twsEPR.getAddress().toString(), scuflDoc.getAbsolutePath(), "TEST");
			assertNotNull("ERROR : The resource EPR is null.", serviceEPR);

			// Start the workflow with the input parameters.
			System.out.println("\n2. Starting the workflow.\n");
			WorkflowStatusType status = TavernaWorkflowServiceClient.startWorkflow (INPUTS, serviceEPR);
			
			assertTrue("Submitted workflow was not started.",status.equals(WorkflowStatusType.Active) 
					|| status.equals(WorkflowStatusType.Pending)
					|| status.equals(WorkflowStatusType.Done));
			System.out.println(" >> Workflow status is :" + status.getValue() + "\n");

			// Check the status of the Workflow..
			System.out.println("3. Checking the status of the workflow.\n");
			status = TavernaWorkflowServiceClient.getStatus(serviceEPR);
			System.out.println(" >> Workflow status is :" + status.getValue() + "\n");
			
			// Subscribing to the resource property..
			System.out.println("4. Waiting 30 mins for the workflow to complete.\n");
			//TavernaWorkflowServiceClient.subscribeRP(serviceEPR, 60);
			for(int i=0; (i<180 && !status.equals(WorkflowStatusType.Done)); i++)
			{
				Thread.sleep(10000);
				status = TavernaWorkflowServiceClient.getStatus(serviceEPR);
			}

			assertTrue("Submitted workflow was not Completed.", status.equals(WorkflowStatusType.Done));
			System.out.println(" >> Workflow status is :" + status.getValue() + "\n");
			
			//Get the output of the Workflow..
			System.out.println("5. Getting the output of the Workflow.\n");
			WorkflowOutputType output = TavernaWorkflowServiceClient.getOutput(serviceEPR);

			FileOutputStream out = new FileOutputStream(new File(OUTPUT_FILE));
			out.write((output.getOutputFile(0)).getBytes());
			out.flush();
			out.close();
			System.out.println(" >> Output File located at:" + (new File(OUTPUT_FILE)).getAbsolutePath() + "\n");
			System.out.println("\n\n >> Output : " + output.getOutputFile(0) + "\n");
			
			//System.out.println("result: " + output.getOutputFile()[0]);
			assertNotNull("The output of the workflow is null.", output);
			assertNotNull("The output of the workflow is null.",output.getOutputFile()[0]);

		} catch (MalformedURIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
