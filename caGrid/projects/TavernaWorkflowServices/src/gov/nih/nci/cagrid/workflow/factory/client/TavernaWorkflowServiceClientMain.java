package gov.nih.nci.cagrid.workflow.factory.client;

import gov.nih.nci.cagrid.common.Utils;

import java.io.File;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.apache.axis.message.addressing.EndpointReferenceType;
import org.cagrid.gaards.cds.delegated.stubs.types.DelegatedCredentialReference;
import org.cagrid.transfer.context.stubs.types.TransferServiceContextReference;

import workflowmanagementfactoryservice.WorkflowOutputType;
import workflowmanagementfactoryservice.WorkflowPortType;
import workflowmanagementfactoryservice.WorkflowStatusType;


public class TavernaWorkflowServiceClientMain {

	public static void main(String [] args){

		System.out.println("Running the Grid Service Client");		
		System.out.println(System.getProperty("user.dir"));


		Map map = new HashMap();
		for (int i = 0; i< args.length; i++)
		{
			if(args[i].startsWith("-"))
			{
				map.put(args[i], args[++i]);						
			} else {
				TavernaWorkflowServiceClient.usage();
				System.exit(1);
			}
		}

		try{
			//			if(!(args.length < 4)){
			//				if(args[0].equals("-url")){
			//
			////					EndpointReferenceType eprt = TavernaWorkflowServiceClient.readEprFromFile("Test.epr");
			////					System.out.println(TavernaWorkflowServiceClient.getStatus(eprt));
			////					File outFile2 = TavernaWorkflowServiceClient.getOutputDataHelper(eprt);
			////					
			////					System.out.println(outFile2.getAbsolutePath());
			////					System.exit(0);
			//					// 1. Create Workflow Operations invoked.
			//
			//					if(!map.containsKey("-scuflDoc"))
			//					{
			//						TavernaWorkflowServiceClient.usage();
			//					}
			//					String url = (String) map.get("-url");
			//					String scuflDoc = (String) map.get("-scuflDoc");
			//
			String workflowName = "Test";
			String url = "https://localhost:8443/wsrf/services/cagrid/TavernaWorkflowService";					
			String scuflDoc = System.getProperty("user.dir") + "/secure-hellodina.t2flow";


			System.out.println("\n1. Running createWorkflow ..");

			//Setup a termination time of 1hour (60 Mins) from current time for this resource.
			// If not setup, the default value of 180Mins from current time will be used.
			Calendar terminationTime = Calendar.getInstance();
			terminationTime.add(Calendar.MINUTE, 60);
			EndpointReferenceType resourceEPR = 
				TavernaWorkflowServiceClient.setupWorkflow(url, scuflDoc, workflowName, terminationTime);
			System.out.println("Status after setup: " + TavernaWorkflowServiceClient.getStatus(resourceEPR));
			System.out.println("Created a resource with EPR ..");
			System.out.println("Writing EPR to file ..");
			TavernaWorkflowServiceClient.writeEprToFile(resourceEPR, workflowName);

			//************************************************************************// 
			// 2. Testing caTransfer.
			//					String location = "/Users/sulakhe/Desktop/caintgrator2-folder/cms_test.cls";
			//					TransferServiceContextReference ref1 = TavernaWorkflowServiceClient.putInputDataHelper(resourceEPR, location);
			//					Thread.sleep(5000);
			//					location = "/Users/sulakhe/Desktop/caintgrator2-folder/cms_test.gct";
			//					TransferServiceContextReference ref2 = TavernaWorkflowServiceClient.putInputDataHelper(resourceEPR, location);
			//					Thread.sleep(10000);

			//File outFile = TavernaWorkflowServiceClient.getOutputDataHelper(resourceEPR);
			//System.out.println("caTransfer Output: " + outFile.getAbsolutePath());
			//System.exit(0);

			//************************************************************************//					
			// 2. Delegate credential.
			DelegatedCredentialReference cdsRef = TavernaWorkflowServiceClient.delegateCredential(
					"https://cagrid-cds.nci.nih.gov:8443/wsrf/services/cagrid/CredentialDelegationService", 
					"/O=caBIG/OU=caGrid/OU=LOA1/OU=Services/CN=Sulakhe-2.local", null);
			//"/O=caBIG/OU=caGrid/OU=LOA1/OU=Services/CN=communicado.ci.uchicago.edu/CN=140142983", null);
			System.out.println("Delegated credential.....");
			TavernaWorkflowServiceClient.setDelegatedCredential(resourceEPR, cdsRef);


			// 3. Start Workflow Operations Invoked.
			WorkflowPortType [] inputArgs = null;
			//					WorkflowPortType [] inputArgs = {
			//							//new WorkflowPortType("fish", "Hello"),
			//							//new WorkflowPortType("soup", "Saina")
			//							new WorkflowPortType("clsFileName", "cms_test.cls"),
			//							new WorkflowPortType("cmsResultFileName", "CMSResult.zip"),
			//							new WorkflowPortType("gctFileName", "cms_test.gct"),
			//							new WorkflowPortType("workingDir", "some-working-dir")
			//							//new WorkflowPortType("fileName", "myOwnOutput.txt"),
			//					};
			//					
			String[] inputArgs1 = {"Sulakhe", " Dina"}; 

			System.out.println("\n3. Now starting the workflow ..");
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
			WorkflowStatusType workflowStatusElement =  TavernaWorkflowServiceClient.startWorkflow(inputArgs, readEPR);
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
			//TavernaWorkflowServiceClient.subscribeRP(readEPR, 3600);
			//workflowStatus = TavernaWorkflowServiceClient.getStatus(readEPR);
			while(!TavernaWorkflowServiceClient.getStatus(readEPR).equals(WorkflowStatusType.Done) 
					&& !TavernaWorkflowServiceClient.getStatus(readEPR).equals(WorkflowStatusType.Failed))
			{
				Thread.sleep(5000);
				System.out.println("Waiting for workflow to complete..");
			}

			//4. Get output of workflow.

			if(TavernaWorkflowServiceClient.getStatus(readEPR).equals(WorkflowStatusType.Done))
			{
				System.out.println("Workflow Executions is Completed.");
				//System.exit(0);
				System.out.println("\n4. Getting back the output file..");
				WorkflowOutputType workflowOutput = TavernaWorkflowServiceClient.getOutput(readEPR);

				WorkflowPortType[] outputs = workflowOutput.getOutput();
				for (int i=0; i < outputs.length; i++)
				{
					String outputFile = System.getProperty("user.dir") + "/" + workflowName +"-output-" + outputs[i].getPort() + "-" + i + ".xml";
					Utils.stringBufferToFile(new StringBuffer(outputs[i].getValue()), outputFile);
					System.out.println("Output file " + i + " : " + outputFile);
				}
			}
			else
			{
				System.out.println("Workflow Executiong either failed or incomplete..");
				System.out.println("Current status is: " + TavernaWorkflowServiceClient.getStatus(readEPR));
			}


			//EndpointReferenceType eprt = TavernaWorkflowServiceClient.readEprFromFile("Test.epr");
			System.out.println(TavernaWorkflowServiceClient.getStatus(resourceEPR));
			File outFile = TavernaWorkflowServiceClient.getOutputDataHelper(resourceEPR);

			System.out.println(outFile.getAbsolutePath());


		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
}
