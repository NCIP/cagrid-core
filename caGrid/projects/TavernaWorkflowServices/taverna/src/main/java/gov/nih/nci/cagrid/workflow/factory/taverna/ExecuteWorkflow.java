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
package gov.nih.nci.cagrid.workflow.factory.taverna;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;

import net.sf.taverna.platform.spring.RavenAwareClassPathXmlApplicationContext;
import net.sf.taverna.t2.facade.ResultListener;
import net.sf.taverna.t2.facade.WorkflowInstanceFacade;
import net.sf.taverna.t2.facade.impl.WorkflowInstanceFacadeImpl;
import net.sf.taverna.t2.invocation.InvocationContext;
import net.sf.taverna.t2.invocation.WorkflowDataToken;
//import net.sf.taverna.t2.provenance.connector.ProvenanceConnector;
import net.sf.taverna.t2.provenance.reporter.ProvenanceReporter;
import net.sf.taverna.t2.reference.ReferenceService;
import net.sf.taverna.t2.reference.T2Reference;
import net.sf.taverna.t2.workflowmodel.Dataflow;
import net.sf.taverna.t2.workflowmodel.DataflowInputPort;
import net.sf.taverna.t2.workflowmodel.EditException;
import net.sf.taverna.t2.workflowmodel.serialization.DeserializationException;
import net.sf.taverna.t2.workflowmodel.serialization.xml.XMLDeserializer;
import net.sf.taverna.t2.workflowmodel.serialization.xml.XMLDeserializerRegistry;
//added new one in 2.1
import net.sf.taverna.t2.invocation.impl.InvocationContextImpl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.springframework.context.ApplicationContext;


public class ExecuteWorkflow extends java.lang.Object
								implements net.sf.taverna.raven.launcher.Launchable {
	
//	public class Handler implements Thread.UncaughtExceptionHandler{
//
//		public void uncaughtException(Thread t, Throwable e) {
//			
//			System.out.println("\n\nSome error\n\n");
//			e.printStackTrace();
//		}
//	}
	
	protected final class SimpleResultListener implements ResultListener {
		public Map<String, Object> results = new HashMap<String, Object>();

		public void resultTokenProduced(WorkflowDataToken token,
				String portName) {
			
			Object rendered = referenceService.renderIdentifier(token.getData(),  String.class, invocationContext);
		

			if (! token.isFinal()) {
				System.out.print("Intermediate result on " + portName);
				System.out.print(Arrays.asList(token.getIndex()) + ": ");
				System.out.println(rendered);
			} else {
				synchronized(this) {
					results.put(portName, rendered);
					System.out.println("Final result on " + portName + ": "
							+ rendered);
					notifyAll();
				}
			}
		}
	}

	protected final class ExampleInvocationContext implements InvocationContext {

//Comented the following getEntities method while updating to T2.1
		
//		public <T> List<? extends T> getEntities(Class<T> arg0) {
//			return null;
//		}

/*		//This method was part of InvocationContext class in Taverna 2.0 based code. In 2.1Beta2, this doesn't exist.

  		public ProvenanceConnector getProvenanceConnector() {
			return null;
		}
*/
		public ReferenceService getReferenceService() {
			return referenceService;
		}

		// This method has been added in the 2.1Beta2 code.
		public ProvenanceReporter getProvenanceReporter() {
			// TODO Auto-generated method stub
			return null;
		}

		//The following two methods were added after updating to T2.1
		public void addEntity(Object arg0) {
			// TODO Auto-generated method stub
			
		}

		public <T> List<T> getEntities(Class<T> arg0) {
			// TODO Auto-generated method stub
			return null;
		}
	}

	protected enum Exit {
		HELP, NORMAL, PORTS, WORKFLOW_FILE,
	}

	// Maximum number of seconds to wait
	protected static final long TIMEOUT = 3600;//1 hour
	
	private static XMLDeserializer deserializer = XMLDeserializerRegistry
			.getInstance().getDeserializer();

	protected static void help() {
		System.err.println("Execute workflow");
		System.err.println("Usage:");
		System.err.println("executeworkflow -scuflFile <workflowFile> -input:<portname> <inputValue> -input:<portname> <invputvalue>.....");
	}

	protected ApplicationContext appContext;

	protected InvocationContext invocationContext;

	protected ReferenceService referenceService;
	protected final Log logger = LogFactory.getLog(getClass());


	protected void createContext() {
		appContext = new RavenAwareClassPathXmlApplicationContext(
				"inMemoryReferenceServiceContext.xml");
		referenceService = (ReferenceService) appContext
				.getBean("t2reference.service.referenceService");
		//invocationContext = new ExampleInvocationContext();
		 invocationContext  = new InvocationContextImpl(referenceService, null);

	}

	protected void helpPorts(String workflowFile,
			List<? extends DataflowInputPort> ports) {
		System.err
				.println("Invalid number of port inputs provided for workflow");

		System.err.println("Usage:");
		System.err.print("executeworkflow ");
		System.err.print(workflowFile + " ");

		for (DataflowInputPort inputPort : ports) {
			System.err.print("'inputValueFor" + inputPort.getName() + "' ");
		}
		System.err.println();

	}

	protected Dataflow loadDataflow(File workflowFile) throws IOException,
			JDOMException, DeserializationException, EditException {
		InputStream inStream = new BufferedInputStream(new FileInputStream(
				workflowFile));
		if (inStream == null) {
			throw new IOException("Unable to find resource for t2 dataflow: "
					+ workflowFile);
		}
		SAXBuilder builder = new SAXBuilder();
		Element element = builder.build(inStream).detachRootElement();
		Dataflow dataflow = deserializer.deserializeDataflow(element);
		return dataflow;
	}
//	protected void run(String scuflFile, Map<String, String> inputArgs) throws Exception {
	protected void run(String scuflFile, Map<String, String> inputArgs, ArrayList<String> inputStringArgs) throws Exception {
			
		if(scuflFile == null){
			help();
		}

		File workflowFile = new File(scuflFile);
		if (!workflowFile.isFile()) {
			throw new Exception("Error: Not a workflow file: " + workflowFile);
		}
		
		
		createContext();
		Dataflow dataflow = loadDataflow(workflowFile);
		List<? extends DataflowInputPort> ports = dataflow.getInputPorts();
		
		//System.out.println("Ports:" + ports.size() + "::" + "InputArgs:" + inputArgs.size());
		if (inputArgs != null && inputArgs.size() != ports.size()) {
			throw new Exception("Error: Invalid number of input ports: " + workflowFile);
		}
		if (inputStringArgs != null && inputStringArgs.size() != ports.size()) {
			throw new Exception("Error: Invalid number of input ports: " + workflowFile);
		}

		//Regsiter the inputs with Taverna Reference service.
		//Map<String, T2Reference> inputs = registerInputs(ports, inputArgs);
		
		Map<String, T2Reference> inputs;
		if(inputStringArgs != null){
			inputs = registerInputs(ports, inputStringArgs);
		}
		else{ 
			inputs = registerInputs(ports, inputArgs);
		}
		

		String owningProcess = "executeWorkflow" + UUID.randomUUID();
		WorkflowInstanceFacade facade = new WorkflowInstanceFacadeImpl(
				dataflow, invocationContext, owningProcess);
		SimpleResultListener simpleListener = new SimpleResultListener();
		facade.addResultListener(simpleListener);

		//SimpleFailureListener simpleFailure = new SimpleFailureListener();
		//facade.addFailureListener(simpleFailure);

		
		long until = System.currentTimeMillis() + 3600 * 1000;
		System.out.println("Executing workflow " + workflowFile);
		
		facade.fire();
		for (Entry<String, T2Reference> entry : inputs.entrySet()) {
			String portName = entry.getKey();
			T2Reference dataRef = entry.getValue();
			WorkflowDataToken inputToken = new WorkflowDataToken(owningProcess ,new int[]{}, dataRef, invocationContext);
			facade.pushData(inputToken, portName);
		}
		
		int expectedOutputs = dataflow.getOutputPorts().size();
		synchronized(simpleListener) {
			while (simpleListener.results.size() < expectedOutputs) {
				long sleep = until - System.currentTimeMillis();
				if (sleep <= 0) {
					// timed out
					System.out.println("ERROR : Workflow Execution Timed Out.");
					break;
				}
				simpleListener.wait(sleep);
			}
		}

		if(simpleListener.results.entrySet().size() != expectedOutputs)
		{
			System.out.println("\n\nERROR : Failed to execute the workflow.");
			throw new Exception();
		}
		System.out.println("Finished!");
		System.out.println("TotalOutputPorts:::" + expectedOutputs);
		for (Entry<String, Object> entry : simpleListener.results.entrySet()) {
			System.out.println(entry.getKey() + ":::TWS:::" + entry.getValue());
		}
		
	}

	protected Map<String, T2Reference> registerInputs(
			Iterable<? extends DataflowInputPort> ports,
			Map<String, String> inputMap) {
				
		HashMap<String, T2Reference> inputs = new HashMap<String, T2Reference>();
		Iterator<? extends DataflowInputPort> portIterator = ports.iterator();
		while (portIterator.hasNext()) {
			DataflowInputPort inputPort = portIterator.next();
			System.out.println("Input Port : " + inputPort.getName());
			
			T2Reference inputRef = referenceService.register(inputMap.get(inputPort.getName()), 0, true,
					invocationContext);
			inputs.put(inputPort.getName(), inputRef);
		}
		return inputs;
	}
	
	/*
	 * This registerInputs method registers inputs given as a list of Strings [in a sequence] this is to support
	 * backward compabilit of cagrid 1.4 release. An input with Map <port, value> will be used in future.
	 */
	protected Map<String, T2Reference> registerInputs(
			Iterable<? extends DataflowInputPort> ports,
			ArrayList<String> inputMap) {
				
		HashMap<String, T2Reference> inputs = new HashMap<String, T2Reference>();
		Iterator<? extends DataflowInputPort> portIterator = ports.iterator();
		int count = 0;
		while (portIterator.hasNext()) {
			DataflowInputPort inputPort = portIterator.next();
			System.out.println("Input Port : " + inputPort.getName());
			
			T2Reference inputRef = referenceService.register(inputMap.get(count++), 0, true,
					invocationContext);
			inputs.put(inputPort.getName(), inputRef);
		}
		return inputs;
	}

	public int launch(String[] args) throws Exception {
		
		String workflow = null;
		
		Map<String, String> inputArgs = null;
		ArrayList<String> inputStrings = null;

		/// FOR BACKWARD COMPABILITY
		/// This If needs to removed after String[] as inputs is deprecated in future.
		if(args.length > 2 && !args[2].startsWith("-input"))
		{	
			System.err.println("Workflow is using old interface (With String[] inputs)");
			inputStrings = new ArrayList<String>();
			workflow = args[1];
			for(int i =2; i<args.length; i++){
				inputStrings.add(args[i]);
			}
		}
		/// ******* To be removed in the next release after deprecating String[] inputs ******* ///
		else if(args.length > 0)
		//if(args.length > 0)
		{
			inputArgs = new HashMap<String, String>();
		
			for(int i=0; i<args.length; i++){
				if(args[i].startsWith("-input:"))
				{
					String[] temp = args[i].split(":");
					inputArgs.put(temp[1], args[i+1]);
				}
				if(args[i].startsWith("-scuflFile"))
				{
					workflow = args[i+1];
				}
				
			}
			if(workflow == null){
				help();
			}
		}
		else{
			help();
			throw new Exception("Error: The workflow needs atleast one input argument (scufl file).");
		}
		//new ExecuteWorkflow().run(workflow, inputArgs );	
		new ExecuteWorkflow().run(workflow, inputArgs, inputStrings );	
		return 0;
	}
}

