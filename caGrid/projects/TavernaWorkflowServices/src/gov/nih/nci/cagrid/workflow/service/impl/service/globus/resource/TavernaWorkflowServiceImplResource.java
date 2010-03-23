package gov.nih.nci.cagrid.workflow.service.impl.service.globus.resource;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.common.security.ProxyUtil;
import gov.nih.nci.cagrid.workflow.factory.service.TavernaWorkflowServiceConfiguration;

import org.cagrid.gaards.cds.client.DelegatedCredentialUserClient;
import org.cagrid.gaards.cds.delegated.stubs.types.DelegatedCredentialReference;
import org.cagrid.transfer.context.service.globus.resource.TransferServiceContextResource;
import org.cagrid.transfer.context.service.helper.DataStagedCallback;
import org.cagrid.transfer.context.service.helper.TransferServiceHelper;
import org.cagrid.transfer.context.stubs.types.TransferServiceContextReference;
import org.cagrid.transfer.descriptor.DataDescriptor;
import org.globus.gsi.GlobusCredential;
import org.globus.wsrf.ResourceException;

import workflowmanagementfactoryservice.StartInputType;
import workflowmanagementfactoryservice.WMSInputType;
import workflowmanagementfactoryservice.WorkflowOutputType;
import workflowmanagementfactoryservice.WorkflowPortType;
import workflowmanagementfactoryservice.WorkflowStatusType;


/** 
 * The implementation of this TavernaWorkflowServiceImplResource type.
 * 
 * @created by Introduce Toolkit version 1.2
 * 
 */


public class TavernaWorkflowServiceImplResource extends TavernaWorkflowServiceImplResourceBase {

	private String scuflDoc = null;
	private WorkflowPortType[] outputDoc = null;
	private WorkflowPortType[] inputDoc = null;

	/// These two are to support Backward compatibility ..
	//  should be removed in future release.
	private String[] inputDocOld = null;
	private String[] outputDocOld = null;
	
	public String[] getOutputDocOld() {
		return outputDocOld;
	}

	public void setOutputDocOld(String[] outputDocOld) {
		this.outputDocOld = outputDocOld;
	}

	public String[] getInputDocOld() {
		return inputDocOld;
	}

	public void setInputDocOld(String[] inputDocOld) {
		this.inputDocOld = inputDocOld;
	}

	private String baseDir = null;

	private String tempDir = null;
	private String workflowName = null;
	
	private String TWS_USER_PROXY = null;
	
	private String caTransferCwd = null;

	private WorkflowStatusType workflowStatus = WorkflowStatusType.Pending;
	
	private static TavernaWorkflowServiceConfiguration config = null;

	private TransferServiceContextReference transferRefForOutput = null;
	
	public TransferServiceContextReference getTransferRefForOutput() {
		return transferRefForOutput;
	}

	public void setTransferRefForOutput(TransferServiceContextReference transferRefForOutput) {
		this.transferRefForOutput = transferRefForOutput;
	}

	public WorkflowStatusType getWorkflowStatus() {
		return this.workflowStatus;//super.getWorkflowStatusElement();
	}

	public void setWorkflowStatus(WorkflowStatusType workflowStatus) throws ResourceException {
		this.workflowStatus = workflowStatus;
		super.setWorkflowStatusElement(this.getWorkflowStatus());
	}


	public String getWorkflowName() {
		return workflowName;
	}

	public void setWorkflowName(String workflowName) {
		this.workflowName = workflowName;
	}

	public String getTempDir() {
		return tempDir;
	}

	public void setTempDir(String tempDir) {
		this.tempDir = tempDir;
	}

	public String getBaseDir() {
		return baseDir;
	}

	public void setBaseDir(String baseDir) {
		this.baseDir = baseDir;
	}

	public WorkflowPortType[] getInputDoc() {
		return inputDoc;
	}

	public void setInputDoc(WorkflowPortType[] inputDoc) {
		this.inputDoc = inputDoc;
	}

	public WorkflowPortType[] getOutputDoc() {
		return outputDoc;
	}

	public void setOutputDoc(WorkflowPortType[] workflowOuput) {
		outputDoc = workflowOuput;
	}

	public String getScuflDoc() {
		return scuflDoc;
	}

	public void setScuflDoc(String scuflDoc) {
		this.scuflDoc = scuflDoc;
	}

	public String getTWS_USER_PROXY() {
		return TWS_USER_PROXY;
	}

	public void setTWS_USER_PROXY(String tws_user_proxy) {
		TWS_USER_PROXY = tws_user_proxy;
	}

	public String getCaTransferCwd() {
		return caTransferCwd;
	}

	public void setCaTransferCwd(String caTransferCwd) {
		this.caTransferCwd = caTransferCwd;
	}

	
	private class WorkflowExecutionThread extends Thread {
		
		private ArrayList<String> myArgs = null;

	    public WorkflowExecutionThread (ArrayList<String> args )
		{
	    	this.myArgs = args;
		}
		public void run()
		{	
			
			String tavernaDir = config.getTavernaDir();
			String repository = config.getBaseRepositoryDir();

			// Add the Workflow scufl file to the input arguments list.
			if(getScuflDoc() != null){
				
				myArgs.add("-scuflFile");
				myArgs.add(getScuflDoc());
			}
			
			//If the inputs are sent as key-value pairs, then add them into the ArrayList. 
			//ArrayList is a FIFO, so I should be able to match them when I pull out.
			if(getInputDoc() != null)
			{
				for(WorkflowPortType input : getInputDoc())
				{
					/*
					 * If the workflow submitted uses caTransfer service, then a input port with portname
					 * "workingDir" should be present in the Workflow. The value of this inputport is not added, 
					 * because it is replaced with the value of the workingDir that is set on the service side.
					 */
					// If the client uploaded some file, then caTransferCwd shoudn't be null.
					// if the client didn't upload any file using caTransfer, but still uses a port called "workingDir"
					// then create an empty caTransferCwd using the helper method createTransferDir().
					
					if(input.getPort().equals("workingDir")){
						this.myArgs.add("-input:" + input.getPort());
						String workingDir = (getCaTransferCwd() == null) ? createTransferDir() : getCaTransferCwd();
						this.myArgs.add(workingDir);						
						continue;
					}
					
					this.myArgs.add("-input:"+input.getPort());
					this.myArgs.add(input.getValue());
				}
			}
			/*
			 * This else-if needs to be removed after depracating the String[] inputs.
			 */
			else if(getInputDocOld() != null){
				for(String input : getInputDocOld()){
					this.myArgs.add(input);
				}
			}
			
			//Create a processbilder with the prepared argument list.
			ProcessBuilder builder = new ProcessBuilder(this.myArgs);
			builder.redirectErrorStream(true);
			
			
			builder.directory(new File(tavernaDir + File.separator + "target" + File.separator + "classes"));
			
			
			String completeOutStream = "";
			try {
				
				Map<String, String> environment = builder.environment();
				String classpath = tavernaDir + File.separator + "target" + File.separator + "classes";

				// lisfOfJars is a method that returns all the jars from Taverna repository in CLASSPATH format (: seperated). 
				classpath = classpath + listOfJars(repository);
				environment.put("CLASSPATH", classpath);
				if(getTWS_USER_PROXY() != null)
				{	
					System.out.println("Setting TWS_USER_PROXY");
					environment.put("TWS_USER_PROXY", getTWS_USER_PROXY());
				}

				Process process;
				process = builder.start();

				InputStream is = process.getInputStream();
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr);
				String line;
				System.out.printf("Output of running %s is: ", 
						Arrays.deepToString(myArgs.toArray()));
				
				
				boolean finished = false;
				boolean firstLine = true;
				
				/*
				 * Starting caGrid release 1.4, the TWS supports multiple outputs. It captures all the
				 * ouputs with port-value pairs in a list of WorkflowPortType. 
				 */
				WorkflowPortType[] outputs = null;
				int portsCounter = -1;

				while ((line = br.readLine()) != null) {
					//System.out.println(line);
					completeOutStream += line + "\n";
					if(finished == true)
					{
						if((firstLine == true) && (line.startsWith("TotalOutputPorts")))
						{
							String[] portsLine = line.split(":::");
							int ports = Integer.parseInt(portsLine[1]);
							outputs = new WorkflowPortType[ports];
							firstLine = false;
							
						}else{						
							if(line.indexOf(":::TWS:::") > 0){ //if String contains :::TWS::: anywhere
								String[] temp = line.split(":::TWS:::");
								portsCounter++;
								outputs[portsCounter] = new WorkflowPortType();
								outputs[portsCounter].setPort(temp[0]);
								outputs[portsCounter].setValue(temp[1]);
							} else {
								outputs[portsCounter].setValue(outputs[portsCounter].getValue() + "\n" + line);
							}
						}
					}
					if(line.equals("Finished!")){		
						finished = true;
					}
				}
				int status = process.waitFor();
				if (status != 0)
					throw new Exception();
				

				System.out.println("\nOUTPUTs:\n");
				//for(String output : outputs)
				for(int i =0; i < outputs.length; i++)
                {
                    System.out.format("Output-%d:%n", i+1);
                    outputs[i].setValue(outputs[i].getValue().replaceAll("^\\[+\\n|\\]+$", ""));
                    outputs[i].setValue(outputs[i].getValue().replaceAll("^\\[+|\\]+$", ""));
                    System.out.println(outputs[i].getPort() +" : " + outputs[i].getValue());
                }
				setOutputDoc(outputs);
				//workflowStatus = WorkflowStatusType.Done;
				setWorkflowStatus(WorkflowStatusType.Done);
				//this.statusRP.set(0, workflowStatus);

				System.out.println("Final Status: " + getWorkflowStatus().getValue());
				
			} catch (IOException e) {
				System.err.println("IOException : Error in running the Workflow");
				try {
					setWorkflowStatus(WorkflowStatusType.Failed);
				} catch (ResourceException e1) {
					System.err.println("Unable to set workflowstatus!");
					e1.printStackTrace();
				}
				e.printStackTrace();
			} catch (InterruptedException e) {
				System.err.println("InterruptedException: Process Interrupted");
				try {
					setWorkflowStatus(WorkflowStatusType.Failed);
				} catch (ResourceException e1) {
					System.err.println("Unable to set workflowstatus!");
					e1.printStackTrace();
				}				
				e.printStackTrace();
			} catch (Exception e) {
				System.err.println("Exception : Error in running the Workflow");
				System.err.println(completeOutStream);
				try {
					setWorkflowStatus(WorkflowStatusType.Failed);
				} catch (ResourceException e1) {
					System.err.println("Unable to set workflowstatus!");
					e1.printStackTrace();
				}
				
				e.printStackTrace();
			} 


		}
	}

	public TavernaWorkflowServiceImplResource() throws RemoteException {
		
		try {
			config = TavernaWorkflowServiceConfiguration.getConfiguration();
			this.setBaseDir(config.getBaseRepositoryDir());

			if((config.getTavernaDir().equals(null)) || config.getBaseRepositoryDir().equals(null))
			{
				throw new RemoteException("tavernaDir is not set in the services.properties file"); 
			}
			if(!new File(config.getBaseRepositoryDir()).exists()){
				throw new RemoteException("tavernaDir or baseRepositoryDir doesn't exist as per the services.properties file"); 
			}

			System.out.println("\nTaverna Repository:" + this.getBaseDir());						
			System.out.println("\nTaverna Basedir: " + config.getTavernaDir());
			System.out.println("NOTE: Please set the Taverna base directly correctly. " +
					"This can be set in the service.properties file of service code.\n\n");			

		} catch (Exception e) {			
			e.printStackTrace();
			throw new RemoteException("Error: Unable to get Taverna configuration on the server.");
		}

	}


	public void createWorkflow(WMSInputType wMSInputElement) throws RemoteException
	{
		
		try {

			String [] keys = this.getResourceKey().toString().trim().split("TavernaWorkflowServiceImplResultsKey=");
			System.out.println("\nWorkflow NAME :" + wMSInputElement.getWorkflowName());
			
			//Set the workflow name.
			this.setWorkflowName(wMSInputElement.getWorkflowName());
			
			//Create a temporary directory that stores the required files for this execution.
			this.setTempDir(System.getProperty("java.io.tmpdir") + File.separator + keys[1]);
			new File(this.getTempDir()).mkdir();

			String scuflDocTemp = this.getTempDir() + File.separator + this.getWorkflowName() + "--workflow.t2flow";
			Utils.stringBufferToFile(new StringBuffer(wMSInputElement.getScuflDoc()), scuflDocTemp);
			this.setScuflDoc(scuflDocTemp);
			this.setWorkflowStatus(WorkflowStatusType.Pending);

		} catch (Exception e){
			setWorkflowStatus(WorkflowStatusType.Failed);
			e.printStackTrace();
			throw new RemoteException("Error: Unable to setup the workflow on the server.");
		}

	}

	public WorkflowStatusType start (StartInputType startInput) throws RemoteException {

		try {
			
			if(startInput != null)
			{

				if(startInput.getInputParams() != null){
					System.out.println("Inputs provided for the workflow.");
					WorkflowPortType[] inputs = startInput.getInputParams();
					for (int i=0; i < inputs.length; i++)
					{
						String inputFile = this.getTempDir() + File.separator + "input-" + i + ".xml";					
						Utils.stringBufferToFile(
								new StringBuffer(inputs[i].getPort() +" : "+inputs[i].getValue()), inputFile);
						System.out.println("Input file " + i + " : " + inputFile);
					}
					this.setInputDoc(inputs);					
				}
				
				/************* To be Removed in Next release **********************
				 * Else-if to support backward compatibility and support input strings.
				 */
				else if(startInput.getInputArgs() != null){
					System.out.println("Inputs Strings provided for the workflow.");
					this.setInputDocOld(startInput.getInputArgs());					
				}
				/// ************** To Be Removed in next release ***************** ///
			}
			
			//Create a ArrayList that holds all the args sent to the ProcessBuilder(in a new thread).
			ArrayList<String> myArgs = new ArrayList<String>();
			myArgs.addAll(Arrays.asList("java", 
							"-Xms256m", 
							"-Xmx1g",
							"net.sf.taverna.raven.prelauncher.PreLauncher"));
							
			this.setWorkflowStatus(WorkflowStatusType.Active);
			WorkflowExecutionThread executor = new WorkflowExecutionThread(myArgs);
			executor.start();			

		} catch (Exception e) {
			setWorkflowStatus(WorkflowStatusType.Failed);
			e.printStackTrace();
			throw new RemoteException("Error: Unable to start the workflow.");
		}
		return this.getWorkflowStatus();
	}

	
	
	public WorkflowOutputType getWorkflowOutput()
	{
		WorkflowOutputType workflowOuputElement = new WorkflowOutputType();
		try {
			if( super.getWorkflowStatusElement().equals(WorkflowStatusType.Done) )
			{
				workflowOuputElement.setOutput(this.getOutputDoc());

				/// Following two lines are to support backward compatibility. In future release (after 1.4) 
				/// these should be removed.
				this.convertMappedOutputToStringArrayOutput();
				workflowOuputElement.setOutputFile(this.getOutputDocOld());
			}
			else
			{
				throw new RemoteException("Either the Workflow execution is not Completed or Failed.");
			}
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return workflowOuputElement;
	}


	public WorkflowStatusType getStatus()
	{
		return super.getWorkflowStatusElement();
	}
	


	
	public void setDelegatedCredential(DelegatedCredentialReference delegatedCredentialReference) throws RemoteException, gov.nih.nci.cagrid.workflow.service.impl.stubs.types.CannotSetCredential {
		// The default credential of the user on the service side that is currently logged in.
		//GlobusCredential credential = ProxyUtil.getDefaultProxy();
		
		try {
			GlobusCredential credential = new GlobusCredential("/Users/sulakhe/.cagrid/certificates/Sulakhe-2.local-cert.pem", "/Users/sulakhe/.cagrid/certificates/Sulakhe-2.local-key.pem");		

			//DelegatedCredentialUserClient client;
			//client = new DelegatedCredentialUserClient(delegatedCredentialReference, credential);

			DelegatedCredentialUserClient client;
				client = new DelegatedCredentialUserClient(delegatedCredentialReference);


			// The get credential method obtains a signed delegated credential from the CDS.
			GlobusCredential delegatedCredential;

			delegatedCredential = client.getDelegatedCredential();
			//Save the delegated credential in a custom location.
			String proxyPath = this.getTempDir() + File.separator + "delegatedProxy";
			System.out.println("ProxyPath : " + proxyPath);
			ProxyUtil.saveProxy(delegatedCredential, proxyPath);
			
			//Sets the TWS_USER_PROXY variable that indicates a user has delegated his proxy.
			this.setTWS_USER_PROXY(proxyPath);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RemoteException("Error: Failed to setup the Delegated Credential on the Server.");
		}
		
	}

	/*
	 * This operation is called by the client to upload large files using catransfer.
	 */
	
	//This holds the list of files uploaded by the client as input to the workflow (using caTransfer).
	private Collection<? extends File> listOfFilesUploadedByClient = null;
	public TransferServiceContextReference putInputData(String filename) throws RemoteException {
		
		final String tmpDir = this.getTempDir();
		
        // create a data descriptor for the upload for thed ata to be uploaded
        DataDescriptor dd = new DataDescriptor(null, filename);
        
        // create a callback that will handle the data once it is uploaded
        DataStagedCallback callback = new DataStagedCallback() {
            public void dataStaged(TransferServiceContextResource resource) {
            	
            	//Create a TransferDir.
            	
            	String workDir = createTransferDir();
                File dataFileUserSentMe = new File(resource.getDataStorageDescriptor().getLocation());
                File fileInTmpDir = new File(workDir+ File.separator + 
                		resource.getDataStorageDescriptor().getDataDescriptor().getName());
                
                try {
					copy(dataFileUserSentMe, fileInTmpDir);
					unzipFile(fileInTmpDir);
    			} catch (IOException e) {
					e.printStackTrace();
				}
				
    			//Get the list of files that are uploaded by the client.
    			listOfFilesUploadedByClient = listFiles(new File(getCaTransferCwd()), null, true);
				
                System.out.println("Location of the file: " + dataFileUserSentMe.getAbsolutePath());
                System.out.println("Temp Dir: " + tmpDir);
                System.out.println("New File Location: " + fileInTmpDir.getAbsolutePath());
                System.out.println("DataDescriptor: " + resource.getDataStorageDescriptor().getDataDescriptor().getName());
                
            }

        };

        // create the transfer resource that will handle receiving the data and
        // return the reference to the user
        return TransferServiceHelper.createTransferContext(dd, callback);
	}
	
	private String createTransferDir(){
    	//Create a Working directory insde the Temp directory.
    	String transferWorkDir = this.getTempDir() + File.separator + "transferWorkDir";
    	new File(transferWorkDir).mkdir();
    	
    	//Set the caTransferCwd if its not already set.
    	if (getCaTransferCwd() == null){
    		this.setCaTransferCwd(transferWorkDir); 
    	}    	
    	return transferWorkDir;
	}

	private Collection<? extends File> listOfFilesAfterComplete = null;
	public TransferServiceContextReference getOutputData() throws RemoteException {
		//remove this line after testing
		//this.workflowStatus = WorkflowStatusType.Done;

		if(super.getWorkflowStatusElement().equals(WorkflowStatusType.Done))
		{
			if(this.getCaTransferCwd() == null){
				System.out.println("No output files to transfer.");
				throw new RemoteException("No output files to transfer (No Transfer dir was created).");
			}
			listOfFilesAfterComplete = listFiles(new File(getCaTransferCwd()), null, true);

			//If no files were uploaded, then don't get a diff.
			if(this.listOfFilesUploadedByClient != null){
				listOfFilesAfterComplete.removeAll(this.listOfFilesUploadedByClient);
			}
			if(!listOfFilesAfterComplete.isEmpty())
			{
				File outputFile = this.createZipFile(listOfFilesAfterComplete);
			    // create a descriptor for that data with filename as the Name of the Descriptor.
			    DataDescriptor dd = new DataDescriptor(null, outputFile.getName());
			    // create the transfer resource that will handle delivering the data and
			    // return the reference to the user
			    return TransferServiceHelper.createTransferContext(outputFile, dd, true);	
			}
			else{
				System.out.println("No output files to transfer.");
				throw new RemoteException("No output files to transfer.");
			}
		}
		else if(super.getWorkflowStatusElement().equals(WorkflowStatusType.Failed)){
			throw new RemoteException("FAILED: Workflow execution failed. Please look at the error log.");
		}
		else
			throw new RemoteException("ACTIVE: Workflow execution is not complete.");
	}

	//****** Following two methods are to get a list of all the jars from the Taverna repository******
	//******  These jars are used to add to the classpath in process builder.				******
	//****** Methods: listFilesAsArray() and listFiles
	
	public String listOfJars(String directory)
	{
		File dir = new File(directory);
	    Collection<File> children = listFiles(dir, null, true);		    
	    File[] fileArray = new File[children.size()];
		children.toArray(fileArray);
	    
		String classpath = "";
	    if (children == null) {
	    	System.out.println("Error : Taverna repository is Empty.");
	    } else {
	        for (int i=0; i<fileArray.length; i++) {
	            // Get filename of file or directory
	            String filename = fileArray[i].toString();
	            if(filename.endsWith(".jar"))
	            {
	            	//System.out.println(i+1 + " : " + filename);
	            	classpath = classpath + ":" + filename;
	            }
	        }
	    }
    	//System.out.println(classpath);
	    return classpath;
	}

	public static Collection<File> listFiles(
	// Java4: public static Collection listFiles(
			File directory,
			FilenameFilter filter,
			boolean recurse)
	{
		// List of files / directories
		Vector<File> files = new Vector<File>();
	// Java4: Vector files = new Vector();
		
		// Get files / directories in the directory
		File[] entries = directory.listFiles();
		
		// Go over entries
		for (File entry : entries)
		{
	// Java4: for (int f = 0; f < files.length; f++) {
	// Java4: 	File entry = (File) files[f];

			// If there is no filter or the filter accepts the 
			// file / directory, add it to the list
			if (filter == null || filter.accept(directory, entry.getName()))
			{
				files.add(entry);
			}
			
			// If the file is a directory and the recurse flag
			// is set, recurse into the directory
			if (recurse && entry.isDirectory())
			{
				files.addAll(listFiles(entry, filter, recurse));
			}
		}
		return files;		
		// Return collection of files
	}

	void copy(File src, File dst) throws IOException 
	{ 
		InputStream in = new FileInputStream(src); 
		OutputStream out = new FileOutputStream(dst); 
		// Transfer bytes from in to out 
		byte[] buf = new byte[1024]; 
		int len; 
		while ((len = in.read(buf)) > 0) 
		{ 
			out.write(buf, 0, len); 
		} 
		in.close(); 
		out.close(); 
		 
	}
	
	private File createZipFile(Collection<? extends File> files)
	{
		
		String outFilename = this.getTempDir() + File.separator + "outfile.zip"; 
		byte[] buf = new byte[1024];
		try { // Create the ZIP file 
			ZipOutputStream out = new ZipOutputStream(new FileOutputStream(outFilename));
			for (File file : files) {
				if(!file.isDirectory())
				{
					
					FileInputStream in = new FileInputStream(file);
					out.putNextEntry(new ZipEntry(file.getAbsolutePath().replaceFirst(getCaTransferCwd() + File.separator, "")));
					int len;
					while ((len = in.read(buf)) > 0) 
					{ 
						out.write(buf, 0, len); 
					}
					out.closeEntry(); 
					in.close();
				}
			}
			out.close(); 
			} 
		catch (IOException e) {
			e.getStackTrace();
		}
		return new File(outFilename);
	}
	
	private void unzipFile(File fileInTmpDir) throws FileNotFoundException, IOException
	{
		if(fileInTmpDir.getName().endsWith(".zip"))
		{
			String destinationname = this.getCaTransferCwd() + File.separator;
		    FileInputStream fis = new FileInputStream(fileInTmpDir.getAbsolutePath());
            byte[] buf = new byte[1024];
            ZipInputStream zipinputstream = new ZipInputStream(new BufferedInputStream(fis));
            ZipEntry zipentry;
            
            while ((zipentry = zipinputstream.getNextEntry()) != null) 
            { 
                //for each entry to be extracted
                String entryName = zipentry.getName();
                System.out.println("Extracting: "+ entryName);

                File newFile = new File(entryName);
                String directory = newFile.getParent();
                
//                if(directory == null)
//                {
//                    if(newFile.isDirectory())
//                        break;
//                }
                directory = (directory == null) ? "" : directory;
                new File(destinationname + directory).mkdirs();

                if(entryName.endsWith("/")){
                	new File(destinationname + entryName).mkdir();
                	continue;
                }
                FileOutputStream fileoutputstream = new FileOutputStream(
                        destinationname+entryName);
                BufferedOutputStream dest = new BufferedOutputStream(fileoutputstream, 1024);

                int n;
                while ((n = zipinputstream.read(buf, 0, 1024)) > -1)
                    dest.write(buf, 0, n);

                dest.flush();
                dest.close(); 
                zipinputstream.closeEntry();
                
            }//while

            zipinputstream.close();
          }
		
	}
	
	// This method is added to support backward compbatibility in 1.4. Will be removed in the next versions.
	private void convertMappedOutputToStringArrayOutput(){
		String[] outputStrings = new String[this.getOutputDoc().length];
		int count = 0;
		for(WorkflowPortType out : this.getOutputDoc()){
			outputStrings[count] = out.getValue();
		}
		this.setOutputDocOld(outputStrings);
	}


}
