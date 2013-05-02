/**
*============================================================================
*  Copyright The Ohio State University Research Foundation, The University of Chicago - 
*	Argonne National Laboratory, Emory University, SemanticBits LLC, and 
*	Ekagra Software Technologies Ltd.
*
*  Distributed under the OSI-approved BSD 3-Clause License.
*  See http://ncip.github.com/cagrid-core/LICENSE.txt for details.
*============================================================================
**/
package gov.nih.nci.cagrid.workflow.factory.client;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.workflow.service.impl.client.TavernaWorkflowServiceImplClient;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.apache.axis.message.addressing.EndpointReferenceType;
import org.cagrid.gaards.cds.delegated.stubs.types.DelegatedCredentialReference;
import org.cagrid.transfer.context.stubs.types.TransferServiceContextReference;
import org.globus.gsi.GlobusCredential;

import workflowmanagementfactoryservice.WorkflowOutputType;
import workflowmanagementfactoryservice.WorkflowPortType;
import workflowmanagementfactoryservice.WorkflowStatusType;


public class TavernaWorkflowServiceClientMain {

	public static void main(String [] args){

		System.out.println("Running the Grid Service Client");		
		System.out.println("OS: " + System.getProperty("os.name"));
		System.out.println("CWD: " + System.getProperty("user.dir"));
		
		try{
			if(!(args.length < 4)){
				if(args[0].equals("-url")){
					
					// 1. Create Workflow Operations invoked.
					String url = null;
					String scuflDoc = null;
					ArrayList<WorkflowPortType> inputArgs = new ArrayList<WorkflowPortType>();
					
					for (int i = 0; i<args.length; i++) {
						if(args[i].equals("-url")){
							url = args[i+1];
						}
						if(args[i].startsWith("-scuflDoc")){
							scuflDoc = args[i+1];
						}
						if(args[i].startsWith("-input"))
						{
							String[] key = args[i].split(":");
							inputArgs.add(new WorkflowPortType(key[1],args[i+1]));
						}
					}
					
					if(url==null || scuflDoc==null){
						TavernaWorkflowServiceClient.usage();
					}
					
					String workflowName = "Test";
					//String[] inputArgs = {"caCore", " and caBig4"}; 
					//String[] inputArgs = {"Hello", "World"};
									
					WorkflowPortType [] inputArgs1 = {
										new WorkflowPortType("fish", "Hello"),
										new WorkflowPortType("soup", "World")
										};

					WorkflowPortType [] inputArgs2 = { new WorkflowPortType("EXPID", "95")};
					
					WorkflowPortType [] inputArgs0;
					
					if(inputArgs.isEmpty()){
						inputArgs0 = inputArgs1;
					}
					else{
						inputArgs0 = new WorkflowPortType[inputArgs.size()];
						inputArgs.toArray(inputArgs0);
					}

					System.out.println("\n1. Running createWorkflow ..");

					//WMSOutputType wMSOutputElement =  client.createWorkflow(input);
					EndpointReferenceType resourceEPR = TavernaWorkflowServiceClient.setupWorkflow(url, scuflDoc, workflowName, null);

					System.out.println("Created a resource with EPR ..");
					System.out.println("Writing EPR to file ..");
					TavernaWorkflowServiceClient.writeEprToFile(resourceEPR, workflowName);

					//************************************************************************// 

					// 2. Start Workflow Operations Invoked.

					System.out.println("\n2. Now starting the workflow ..");
					System.out.println("Reading EPR from file ..");
					EndpointReferenceType readEPR = new EndpointReferenceType();
					try {
						System.out.println(System.getProperty("user.dir") + "/" + workflowName + ".epr");
						readEPR = TavernaWorkflowServiceClient.readEprFromFile(System.getProperty("user.dir") + "/"+ workflowName + ".epr");
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

					//This method runs the workflow with the resource represented by the EPR.
					// If there is no inputFile for the workflow, give "null"
					WorkflowStatusType workflowStatusElement =  TavernaWorkflowServiceClient.startWorkflow(inputArgs0, readEPR);
					
					if (workflowStatusElement.equals(WorkflowStatusType.Done))
					{
						System.out.println("Workflow successfully executed..");
					}
					else if(workflowStatusElement.equals(WorkflowStatusType.Active))
					{
						System.out.println("Workflow Active, still running.");
					}
					else if(workflowStatusElement.equals(WorkflowStatusType.Failed))
					{
						throw new Exception("Failed to execute the workflow! Please try again.");
					}
					
					

					// 3. Get Status operation invoked.
					System.out.println("\n3. Checking the status of the workflow..");
					WorkflowStatusType workflowStatus = TavernaWorkflowServiceClient.getStatus(readEPR);
					if(workflowStatus.equals(WorkflowStatusType.Done))
					{
						System.out.println("Workflow Executions is Completed.");
					}
					else if (workflowStatus.equals(WorkflowStatusType.Failed))
					{
						System.out.println("Workflow failed to execute.");
					}
					else
					{
						System.out.println("Workflow execution is either pending or active.");
					}
					
					//Subscribe to the Resource property:
					//TavernaWorkflowServiceClient.subscribeRP(readEPR, 900);
					int count=0;
					while(TavernaWorkflowServiceClient.getStatus(readEPR).equals(WorkflowStatusType.Active)){
						Thread.sleep(10000);
						count++;
						if(count % 2 == 0){
							System.out.println("If you think the workflow is taking too long, cancel (Ctrl-C) the test and check the status on the server..");
						}
						if(count == 1){
							System.out.println("" +
									"\n== NOTE: If this is the first workflow being submitted to the Workflow Service,\n" +
									"==       it will take longer for your workflow to complete, as it dowonloads\n" +
									"==       (one time) all the required artifacts (approximately: 10-15mins depending\n" +
									"==       on the network speed). Subsequent workflow submissions will take less time.\n"									
									);
						}
					}
					
					workflowStatus = TavernaWorkflowServiceClient.getStatus(readEPR);

					//4. Get output of workflow.
					
					
					System.out.println("\n4. Getting back the output file..");
					WorkflowOutputType workflowOutput = TavernaWorkflowServiceClient.getOutput(readEPR);
					
					String[] outputs = workflowOutput.getOutputFile();
					for (int i=0; i < outputs.length; i++)
					{
						String outputFile = System.getProperty("user.dir") + "/" + workflowName +"-output-" + i + ".xml";
						Utils.stringBufferToFile(new StringBuffer(outputs[i]), outputFile);
						System.out.println("Output file " + i + " : " + outputFile);
					}

				} else {
					TavernaWorkflowServiceClient.usage();
					System.exit(1);
				}
			} else {
				TavernaWorkflowServiceClient.usage();
				System.exit(1);
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	private static EndpointReferenceType readEPR(String location){
		EndpointReferenceType readEPR = new EndpointReferenceType();
		try {
			//System.out.println(System.getProperty("user.dir") + "/" + workflowName + ".epr");
			readEPR = TavernaWorkflowServiceClient.readEprFromFile(location);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return readEPR;

	}
}
