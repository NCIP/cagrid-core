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
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.common.security.ProxyUtil;
import gov.nih.nci.cagrid.workflow.factory.common.TavernaWorkflowServiceConstants;
import gov.nih.nci.cagrid.workflow.factory.service.TavernaWorkflowServiceConfiguration;
import gov.nih.nci.cagrid.workflow.service.impl.common.TavernaWorkflowServiceImplConstantsBase;
import gov.nih.nci.cagrid.workflow.service.impl.stubs.types.CannotSetCredential;

import org.apache.axis.message.addressing.EndpointReferenceType;
import org.cagrid.gaards.cds.client.DelegatedCredentialUserClient;
import org.cagrid.gaards.cds.delegated.stubs.types.DelegatedCredentialReference;
import org.cagrid.gaards.cds.stubs.types.CDSInternalFault;
import org.cagrid.gaards.cds.stubs.types.DelegationFault;
import org.cagrid.gaards.cds.stubs.types.PermissionDeniedFault;
import org.cagrid.transfer.context.service.globus.resource.TransferServiceContextResource;
import org.cagrid.transfer.context.service.helper.DataStagedCallback;
import org.cagrid.transfer.context.service.helper.TransferServiceHelper;
import org.cagrid.transfer.context.stubs.types.TransferServiceContextReference;
import org.cagrid.transfer.descriptor.DataDescriptor;
import org.globus.gsi.GlobusCredential;
import org.globus.gsi.GlobusCredentialException;
import org.globus.wsrf.InvalidResourceKeyException;
import org.globus.wsrf.NoSuchResourceException;
import org.globus.wsrf.ResourceException;
import org.globus.wsrf.ResourceKey;
import org.globus.wsrf.ResourceProperty;
import org.globus.wsrf.ResourcePropertySet;
import org.globus.wsrf.impl.SimpleResourcePropertySet;

import workflowmanagementfactoryservice.StartInputType;
import workflowmanagementfactoryservice.WMSInputType;
import workflowmanagementfactoryservice.WMSOutputType;
import workflowmanagementfactoryservice.WorkflowOutputType;
import workflowmanagementfactoryservice.WorkflowStatusType;


/** 
 * The implementation of this TavernaWorkflowServiceImplResource type.
 * 
 * @created by Introduce Toolkit version 1.2
 * 
 */


public class TavernaWorkflowServiceImplResource extends TavernaWorkflowServiceImplResourceBase {

	private String scuflDoc = null;
	private String[] outputDoc = null;
	private String[] inputDoc = null;
	private String baseDir = null;

	private String tempDir = null;
	private String workflowName = null;
	
	private String TWS_USER_PROXY = null;
	
	private String caTransferCwd = null;

	private WorkflowStatusType workflowStatus = WorkflowStatusType.Pending;
	private static TavernaWorkflowServiceConfiguration config = null;

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

	public String[] getInputDoc() {
		return inputDoc;
	}

	public void setInputDoc(String[] inputDoc) {
		this.inputDoc = inputDoc;
	}

	public String[] getOutputDoc() {
		return outputDoc;
	}

	public void setOutputDoc(String[] workflowOuput) {
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
		
		private String[] args = null;
		private ArrayList<String> myArgs = null;
		private ResourcePropertySet propSet;
		private ResourceProperty statusRP;
	   // private File tmpFile;
	    //private FileOutputStream outtemp;
	    public WorkflowExecutionThread (ArrayList<String> args, ResourcePropertySet propSet )
		{
	    	this.myArgs = args;
			this.propSet  = propSet;
			statusRP = this.propSet.get(TavernaWorkflowServiceImplConstantsBase.WORKFLOWSTATUSELEMENT);
			//statusRP.set(0, workflowStatus);
		}
		public void run()
		{	
			
			String tavernaDir = config.getTavernaDir();
			String repository = config.getBaseRepositoryDir();

			ProcessBuilder builder = new ProcessBuilder(this.myArgs);
			builder.redirectErrorStream(true);
			
			builder.directory(new File(tavernaDir + File.separator + "target" + File.separator + "classes"));
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
				
				//Currently multiple outputs are stored in array. Eventually the return type should be changed to a Map
				// with output port names as keys.
				
				String[] outputs = new String[1];
				int portsCounter = -1;

				while ((line = br.readLine()) != null) {
					System.out.println(line);
					if(finished == true)
					{
						if((firstLine == true) && (line.startsWith("TotalOutputPorts")))
						{
							String[] portsLine = line.split(":::");
							int ports = Integer.parseInt(portsLine[1]);
							outputs = new String[ports];
							firstLine = false;
							
						}else{						
							if(line.indexOf(":::TWS:::") > 0){ //if String contains :::TWS::: anywhere
								String[] temp = line.split(":::TWS:::");
								portsCounter++;
								outputs[portsCounter] = temp[1];
							} else {
								outputs[portsCounter] = outputs[portsCounter] + "\n" + line;
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
                    outputs[i] = outputs[i].replaceAll("^\\[+\\n|\\]+$", "");
                    outputs[i] = outputs[i].replaceAll("^\\[+|\\]+$", "");
                    System.out.println(outputs[i]);
                }
				setOutputDoc(outputs);
				workflowStatus = WorkflowStatusType.Done;
				this.statusRP.set(0, workflowStatus);

				System.out.println("Final Status: " + workflowStatus.getValue());
				
			} catch (IOException e) {
				System.err.println("IOException : Erorr in running the Workflow");
				workflowStatus = WorkflowStatusType.Failed;
				this.statusRP.set(0, workflowStatus);
				e.printStackTrace();
			} catch (InterruptedException e) {
				workflowStatus = WorkflowStatusType.Failed;
				this.statusRP.set(0, workflowStatus);
				System.err.println("InterruptedException: Process Interrupted");
				e.printStackTrace();
			} catch (Exception e) {
				workflowStatus = WorkflowStatusType.Failed;
				this.statusRP.set(0, workflowStatus);
				System.err.println("Exception : Erorr in running the Workflow");
				e.printStackTrace();
			} 


		}
	}

	public TavernaWorkflowServiceImplResource() {
		
		try {
			config = TavernaWorkflowServiceConfiguration.getConfiguration();
			this.setBaseDir(config.getBaseRepositoryDir());

			if((config.getTavernaDir().equals(null)) || config.getBaseRepositoryDir().equals(null))
			{
				throw new RemoteException("tavernaDir is not set the services.properties file"); 
			}

			System.out.println("\nTaverna Repository:" + this.getBaseDir());						
			System.out.println("\nTaverna Basedir: " + config.getTavernaDir());
			System.out.println("NOTE: Please set the Taverna base directly correctly. This can be set in the service.properties file of service code.\n\n");

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}


	public void createWorkflow(WMSInputType wMSInputElement)
	{
		
		try {

			String [] keys = this.getResourceKey().toString().trim().split("TavernaWorkflowServiceImplResultsKey=");
			System.out.println("\nWorkflow NAME :" + wMSInputElement.getWorkflowName());
			
			//Set the workflow name.
			this.setWorkflowName(wMSInputElement.getWorkflowName());
			
			//Create a temporary directory that stores the required files for this execution.
			this.setTempDir(System.getProperty("java.io.tmpdir") + File.separator + keys[1]);
			new File(this.getTempDir()).mkdir();

			String scuflDocTemp = this.getTempDir() + File.separator + this.getWorkflowName() + "--workflow.xml";
			Utils.stringBufferToFile(new StringBuffer(wMSInputElement.getScuflDoc()), scuflDocTemp);
			this.setScuflDoc(scuflDocTemp);

		} catch (Exception e){
			this.workflowStatus = WorkflowStatusType.Failed;
			e.printStackTrace();
			System.exit(1);
		}

	}

	public WorkflowStatusType start (StartInputType startInput) {

		try {
			
			int inputPorts = 0;
			
			if(startInput != null)
			{
				System.out.println("InputFile provided for the workflow.");
				String[] inputs = startInput.getInputArgs();
				for (int i=0; i < inputs.length; i++)
				{
					String inputFile = this.getTempDir() + File.separator + "input-" + i + ".xml";					
					Utils.stringBufferToFile(new StringBuffer(inputs[i]), inputFile);
					System.out.println("Input file " + i + " : " + inputFile);
				}
				this.setInputDoc(inputs);
				inputPorts = this.getInputDoc().length;
			}

			//Create a ArrayList that holds all the args sent to the ProcessBuilder(in a new thread).
			ArrayList<String> myArgs = new ArrayList<String>();
			myArgs.addAll(Arrays.asList("java", 
							"-Xms256m", 
							"-Xmx1g",
							"net.sf.taverna.raven.prelauncher.PreLauncher",
							this.getScuflDoc()));
			
			for(int i = 0; i < inputPorts; i++)
			{
				myArgs.add(this.getInputDoc()[i]);
			}			
			
			//if caTransfer is used, add the working diretory as the last argument of the ArrayList.
			if(this.getCaTransferCwd() != null){
				myArgs.add(this.getCaTransferCwd());
			}
				
			workflowStatus = WorkflowStatusType.Active;
			super.setWorkflowStatusElement(workflowStatus);
			
			WorkflowExecutionThread executor = new WorkflowExecutionThread(myArgs, this.getResourcePropertySet());
			executor.start();			

		} catch (Exception e) {
			this.workflowStatus = WorkflowStatusType.Failed;
			e.printStackTrace();
		}
		return this.workflowStatus;
	}

	public WorkflowOutputType getWorkflowOutput()
	{
		WorkflowOutputType workflowOuputElement = new WorkflowOutputType();
		try {
			//if( (new File(this.getOutputDoc()).exists()) && (this.workflowStatus.equals(WorkflowStatusType.Done)))
			if( this.workflowStatus.equals(WorkflowStatusType.Done) )
			{
				//workflowOuputElement.setOutputFile(Utils.fileToStringBuffer( new File(this.getOutputDoc())).toString());
				// Currently, the results are stored as array of string holding the outputs.
				// Ideally, the output files should be created for each output in the "start" operation,
				//	 and then access those files here, convert them into string buffers and then store them below.
				// Will add it once we have some examples of multiple outout workflows.
				//UPDATE: SEE COMMENT IN THE PROCEEBUILDER CODE(NEED MAP).

				workflowOuputElement.setOutputFile(this.getOutputDoc());
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
		return this.workflowStatus;
	}
	

	public void setDelegatedCredential(DelegatedCredentialReference delegatedCredentialReference){
		// The default credential of the user on the service side that is currently logged in.
		//GlobusCredential credential = ProxyUtil.getDefaultProxy();
		
		try {
			GlobusCredential credential;
			credential = new GlobusCredential("/Users/sulakhe/.cagrid/certificates/Sulakhe-2.local-cert.pem", "/Users/sulakhe/.cagrid/certificates/Sulakhe-2.local-key.pem");		

			DelegatedCredentialUserClient client;
			client = new DelegatedCredentialUserClient(delegatedCredentialReference, credential);

		//	DelegatedCredentialUserClient client = new DelegatedCredentialUserClient(delegatedCredentialReference);

		// The get credential method obtains a signed delegated credential from the CDS.
			GlobusCredential delegatedCredential;

			delegatedCredential = client.getDelegatedCredential();
			//Save the delegated credential in a custom location.
			String proxyPath = this.getTempDir() + File.separator + "delegatedProxy";
			System.out.println("ProxyPath : " + proxyPath);
			ProxyUtil.saveProxy(delegatedCredential, proxyPath);
			
			//Sets the TWS_USER_PROXY variable that indicates a user has delegated his proxy.
			this.setTWS_USER_PROXY(proxyPath);

			
		} catch (CDSInternalFault e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DelegationFault e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (PermissionDeniedFault e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
            	
            	//Create a Working directory insde the Temp directory.
            	String workDir = tmpDir + File.separator + "transferWorkDir";
            	new File(workDir).mkdir();
            	
            	//Set the caTransferCwd if its not already set.
            	if (getCaTransferCwd() == null){
            		setCaTransferCwd(workDir); 
            	}
            	
            	
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

	private Collection<? extends File> listOfFilesAfterComplete = null;
	public TransferServiceContextReference getOutputData() throws RemoteException {
		//remove this line after testing
		this.workflowStatus = WorkflowStatusType.Done;

		if(this.workflowStatus.equals(WorkflowStatusType.Done))
		{
			listOfFilesAfterComplete = listFiles(new File(getCaTransferCwd()), null, true);
			listOfFilesAfterComplete.removeAll(this.listOfFilesUploadedByClient);
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
		else
			throw new RemoteException("Workflow execution is not complete.");
	}

	//****** Following two methods is get a list of all the jars from the Taverna repository******
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


}
