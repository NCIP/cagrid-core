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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.springframework.context.ApplicationContext;

public class ExecuteWorkflow {

	protected final class SimpleResultListener implements ResultListener {
		public Map<String, Object> results = new HashMap<String, Object>();

		public void resultTokenProduced(WorkflowDataToken token,
				String portName) {
			Object rendered = referenceService.renderIdentifier(token.getData(), String.class, invocationContext);
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
		public <T> List<? extends T> getEntities(Class<T> arg0) {
			return null;
		}

	//	public ProvenanceConnector getProvenanceConnector() {
	//		return null;
	//	}

		public ReferenceService getReferenceService() {
			return referenceService;
		}

		public ProvenanceReporter getProvenanceReporter() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	protected enum Exit {
		HELP, NORMAL, PORTS, WORKFLOW_FILE,
	}

	// Maximum number of seconds to wait
	protected static final long TIMEOUT = 3600;
	
	private static XMLDeserializer deserializer = XMLDeserializerRegistry
			.getInstance().getDeserializer();

	protected static void help() {
		System.err.println("Execute workflow");
		System.err.println("Usage:");
		System.err.println("executeworkflow <workflowFile> [portInput] ..");
	}

	public static void main(String[] args) throws Exception {
		new ExecuteWorkflow().run(args);
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
		invocationContext = new ExampleInvocationContext();
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
			T2Reference inputRef = referenceService.register(string, 0, true,
					invocationContext);
			inputs.put(inputPort.getName(), inputRef);
		}
		return inputs;
	}
}
