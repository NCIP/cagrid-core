package gov.nih.nci.cagrid.workflow.factory.taverna;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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
	protected static final long TIMEOUT = 5;
	
	private static XMLDeserializer deserializer = XMLDeserializerRegistry
			.getInstance().getDeserializer();

	protected static void help() {
		System.err.println("Execute workflow");
		System.err.println("Usage:");
		System.err.println("executeworkflow <workflowFile> [portInput] ..");
	}

//	public static void main(String[] args) throws Exception {
//		String[] inputs;
//		if(args.length > 0)
//		{
//			inputs = args;
//		}
//		else{
//			
//			//	String workflow = System.getProperty("user.dir") + System.getProperty("file.separator") + "workflows/" + "caINT2_PCA_CMS_090826.t2flow";
//			//String workflow = System.getProperty("user.dir") + System.getProperty("file.separator") + "workflows/" + "PCA_transfer_plugin.t2flow";
//			//	String[] inputArgs = {"/Users/sulakhe/Desktop/dina/workingdir", "/Users/sulakhe/Desktop/dina/all_aml_train.gct"};
//
//			String input1 = System.getProperty("user.dir");
//			String input = System.getProperty("user.dir") + System.getProperty("file.separator") + "workflows/all_aml_train.gct";
//		
////			String workflow = System.getProperty("user.dir") + System.getProperty("file.separator") + "workflows/PCA_transfer_plugin.t2flow";
//			String workflow = System.getProperty("user.dir") + System.getProperty("file.separator") + "workflows/fishsoup.t2flow";
//			String[] temp = {workflow, input1, input};
//		
//			inputs = temp;
//		}
//		new ExecuteWorkflow().run(inputs);		
//	}

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

	protected void run(String[] args) throws Exception {

		
		logger.info("Entering the Run method .... #####");
		if (args.length == 0 || args[0].equalsIgnoreCase("-h")
				|| args[0].equalsIgnoreCase("--help")) {
			help();
			System.exit(Exit.HELP.ordinal());
		}

		File workflowFile = new File(args[0]);
		if (!workflowFile.isFile()) {
			System.err.println("Not a workflow file: " + workflowFile);
			System.exit(Exit.WORKFLOW_FILE.ordinal());
		}
		
		createContext();
		Dataflow dataflow = loadDataflow(workflowFile);
		List<? extends DataflowInputPort> ports = dataflow.getInputPorts();
		if (args.length != ports.size() + 1) {
			helpPorts(args[0], ports);
			System.exit(Exit.PORTS.ordinal());
		}
		// Skip the workflow name
		List<String> inputList = Arrays.asList(args).subList(1, args.length);
		Map<String, T2Reference> inputs = registerInputs(ports, inputList);

		String owningProcess = "executeWorkflow" + UUID.randomUUID();
		WorkflowInstanceFacade facade = new WorkflowInstanceFacadeImpl(
				dataflow, invocationContext, owningProcess);
		SimpleResultListener simpleListener = new SimpleResultListener();
		facade.addResultListener(simpleListener);

		//SimpleFailureListener simpleFailure = new SimpleFailureListener();
		//facade.addFailureListener(simpleFailure);

		
		long until = System.currentTimeMillis() + TIMEOUT * 1000;
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
			Iterable<String> inputValues) {
		

		
		HashMap<String, T2Reference> inputs = new HashMap<String, T2Reference>();
		Iterator<? extends DataflowInputPort> portIterator = ports.iterator();
		Iterator<String> inputsIterator = inputValues.iterator();
		while (portIterator.hasNext() && inputsIterator.hasNext()) {
			String string = inputsIterator.next();
			DataflowInputPort inputPort = portIterator.next();
			
			System.out.println("Input Port : " + inputPort.getName());
			
			T2Reference inputRef = referenceService.register(string, 0, true,
					invocationContext);
			inputs.put(inputPort.getName(), inputRef);
		}
		return inputs;
	}

	public int launch(String[] arg0) throws Exception {
		
		String[] inputs;
		if(arg0.length > 0)
		{
			inputs = arg0;
		}
		else{
			
			//	String workflow = System.getProperty("user.dir") + System.getProperty("file.separator") + "workflows/" + "caINT2_PCA_CMS_090826.t2flow";
			//String workflow = System.getProperty("user.dir") + System.getProperty("file.separator") + "workflows/" + "PCA_transfer_plugin.t2flow";
			//	String[] inputArgs = {"/Users/sulakhe/Desktop/dina/workingdir", "/Users/sulakhe/Desktop/dina/all_aml_train.gct"};

			//String input1 = System.getProperty("user.dir");
			//String input = System.getProperty("user.dir") + System.getProperty("file.separator") + "workflows/all_aml_train.gct";


			String input1 = "Hello ";
			String input = "World!";
		
//			String workflow = System.getProperty("user.dir") + System.getProperty("file.separator") + "workflows/PCA_transfer_plugin.t2flow";
			String workflow = System.getProperty("user.dir") + System.getProperty("file.separator") + "workflows/fishsoup.t2flow";
			System.out.println("Workflow Path: " + workflow);
			String[] temp = {workflow, input1, input};
		
			inputs = temp;
		}
		new ExecuteWorkflow().run(inputs);	
		return 0;
	}
}

