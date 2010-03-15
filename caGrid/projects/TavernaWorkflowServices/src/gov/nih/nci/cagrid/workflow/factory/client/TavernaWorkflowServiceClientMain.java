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
		System.out.println("OS: " + System.getProperty("user.home"));
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
			if(!(args.length < 4)){
				if(args[0].equals("-url")){

					
					// 1. Create Workflow Operations invoked.

					if(!map.containsKey("-scuflDoc"))
					{
						TavernaWorkflowServiceClient.usage();
					}
					String url = (String) map.get("-url");
					String scuflDoc = (String) map.get("-scuflDoc");

					String workflowName = "Test";


					System.out.println("\n1. Running createWorkflow ..");

					//Setup a termination time of 1hour (60 Mins) from current time for this resource.
					// If not setup, the default value of 180Mins from current time will be used.
					Calendar terminationTime = Calendar.getInstance();
					terminationTime.add(Calendar.MINUTE, 60);
					EndpointReferenceType resourceEPR = 
						TavernaWorkflowServiceClient.setupWorkflow(url, scuflDoc, workflowName, terminationTime);

					System.out.println("Created a resource with EPR ..");
					System.out.println("Writing EPR to file ..");
					TavernaWorkflowServiceClient.writeEprToFile(resourceEPR, workflowName);

					//************************************************************************// 
					// 2. Testing caTransfer.
/*					String location = "/Users/sulakhe/Desktop/ttemp.zip";
					TransferServiceContextReference ref1 = TavernaWorkflowServiceClient.putInputDataHelper(resourceEPR, location);
					  
					File outFile = TavernaWorkflowServiceClient.getOutputDataHelper(resourceEPR);
					System.out.println("caTransfer Output: " + outFile.getAbsolutePath());
					System.exit(0);
*/
					//************************************************************************//					
					// 2. Delegate credential.
/*					DelegatedCredentialReference cdsRef = TavernaWorkflowServiceClient.delegateCredential(
							"https://cagrid-cds.nci.nih.gov:8443/wsrf/services/cagrid/CredentialDelegationService", 
							"/O=caBIG/OU=caGrid/OU=LOA1/OU=Services/CN=Sulakhe-2.local", null);
					
					TavernaWorkflowServiceClient.setDelegatedCredential(resourceEPR, cdsRef);

*/					
					// 3. Start Workflow Operations Invoked.
					WorkflowPortType [] inputArgs = { 
							new WorkflowPortType("fish", "Saina"),
							new WorkflowPortType("soup", "Sulakhe")
					};
					
					//String[] inputArgs = {"Dina", " Sulakhe"}; 
					//String[] inputArgs = {"<ns1:CQLQuery xmlns:ns1=\"http://CQL.caBIG/1/gov.nih.nci.cagrid.CQLQuery\">  <ns1:Target name=\"gov.nih.nci.caarray.domain.project.Experiment\">   <ns1:Group logicRelation=\"AND\">    <ns1:Attribute name=\"id\" predicate=\"EQUAL_TO\" value=\"95\"/>   </ns1:Group>  </ns1:Target> </ns1:CQLQuery>"};
					//String[] inputArgs = {"caCore"};
					
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
						System.out.println("Current status is: " + workflowStatus.getValue());
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
}
