package gov.nih.nci.cagrid.workflow.tests;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Properties;

import javax.xml.namespace.QName;
import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.workflow.client.WorkflowFactoryServiceClient;
import gov.nih.nci.cagrid.workflow.context.client.WorkflowServiceImplClient;
import gov.nih.nci.cagrid.workflow.stubs.types.StartInputType;
import gov.nih.nci.cagrid.workflow.stubs.types.WMSInputType;
import gov.nih.nci.cagrid.workflow.stubs.types.WMSOutputType;
import gov.nih.nci.cagrid.workflow.stubs.types.WSDLReferences;
import gov.nih.nci.cagrid.workflow.stubs.types.WorkflowInputType;
import gov.nih.nci.cagrid.workflow.stubs.types.WorkflowOutputType;
import gov.nih.nci.cagrid.workflow.stubs.types.WorkflowStatusType;

import org.apache.axis.message.MessageElement;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.axis.types.URI;
import org.globus.wsrf.encoding.ObjectSerializer;
import org.globus.wsrf.test.GridTestCase;
import org.globus.wsrf.utils.AnyHelper;
import org.globus.wsrf.utils.XmlUtils;
import org.oasis.wsrf.properties.GetResourcePropertyResponse;
import org.w3c.dom.Element;

public class BasicTests extends GridTestCase {

	public String url = "http://localhost:8080/wsrf/services/cagrid/WorkflowFactoryService";
	private static WorkflowFactoryServiceClient factoryClient = null;
	private static WorkflowServiceImplClient serviceClient = null;
	private static EndpointReferenceType epr = null;
	
	private  Properties props = new Properties();
	
	
	public BasicTests(String name) {
		super(name);
		setup();
	}
	
	private void setup() {
		try {
	        this.props.load(new FileInputStream("test.properties"));
	        this.url= this.props.getProperty("WORKFLOW_ENDPOINT");
	    } catch (IOException e) {
	    	System.err.println("Cannot read test props");
	    }
	// setup stuff if tests get executed in parallel	
	}
	
	public void testBasic() throws Exception {
		assertTrue(TEST_CONTAINER != null);
		FileWriter writer = null;
		this.factoryClient = new WorkflowFactoryServiceClient(url);
		WMSInputType input = createInput(null, "Test1.bpel");
		WMSOutputType output = this.factoryClient.createWorkflow(input);
		this.epr = output.getWorkflowEPR();
		assertTrue(epr != null);
		writer = new FileWriter("workflow_" + input.getWorkflowName() + "_epr");
		writer.write( 
				ObjectSerializer.toString(epr, new QName("", "WMS_EPR")));
		this.serviceClient = new WorkflowServiceImplClient(this.epr);
		StartInputType startInput = new StartInputType();
		WorkflowInputType inputArgs = new WorkflowInputType();
		FileInputStream in = new FileInputStream("input.xml");
		Element e2 = XmlUtils.newDocument(in).getDocumentElement();
		System.out.println(XmlUtils.toString(e2));
		MessageElement anyContent = AnyHelper.toAny(new MessageElement(e2));
		inputArgs.set_any(new MessageElement[] {anyContent});
		startInput.setInputArgs(inputArgs);
		WorkflowStatusType status = this.serviceClient.start(startInput);
		status = this.serviceClient.getStatus();
		System.out.println(status.getValue());
		assertTrue(status !=null);
		//assertTrue(status.getValue().equals("Active"));
		
	}

	public void testStatus() throws Exception {
		if (this.epr != null) {
			this.serviceClient = new WorkflowServiceImplClient(this.epr);
		} else {
			System.out.println("epr is null");
		}
		WorkflowStatusType status = this.serviceClient.getStatus();
		System.out.println(status.getValue());
		assertTrue(status != null);
	}
	
	public void testGetOutput() throws Exception {
		WorkflowOutputType output = this.serviceClient.getWorkflowOutput();
		System.out.println("result: " + AnyHelper.toSingleString(output.get_any()));
		assertTrue(output != null);
	}
	
	public void testStatusRP() throws Exception {
		if (this.serviceClient != null) {
			GetResourcePropertyResponse res = this.serviceClient.getPort().getResourceProperty(
					new QName("http://workflow.cagrid.nci.nih.gov/WorkflowServiceImpl", "Status"));
			MessageElement[] any = res.get_any();
	        assertTrue(any != null);
	        assertTrue(any.length > 0);
	        System.out.println(" Status RP : " + AnyHelper.toSingleString(any));
		}
	}
	public void testPause() throws Exception {
		WorkflowStatusType status = this.serviceClient.pause();
		assertTrue(status != null);
		System.out.println(status.getValue());
		
	}
	
	public void testResume() throws Exception {
		WorkflowStatusType status = this.serviceClient.resume();
		assertTrue(status != null);
		System.out.println(status.getValue());
	}
	public void testCancel() throws Exception {
		this.serviceClient.cancel();
	}
	
	public void testTerminationTime() throws Exception {
		FileWriter writer = null;
		this.factoryClient = new WorkflowFactoryServiceClient(url);
		Calendar termTime = Calendar.getInstance();
		termTime.add(Calendar.SECOND, 30);
		WMSInputType input = createInput(termTime, "Test1.bpel");
		WMSOutputType output = this.factoryClient.createWorkflow(input);
		this.epr = output.getWorkflowEPR();
		assertTrue(epr != null);
		writer = new FileWriter("workflow_" + input.getWorkflowName() + "_epr");
		writer.write( 
				ObjectSerializer.toString(epr, new QName("", "WMS_EPR")));
		this.serviceClient = new WorkflowServiceImplClient(this.epr);
		StartInputType startInput = new StartInputType();
		WorkflowInputType inputArgs = new WorkflowInputType();
		FileInputStream in = new FileInputStream("input.xml");
		Element e2 = XmlUtils.newDocument(in).getDocumentElement();
		System.out.println(XmlUtils.toString(e2));
		MessageElement anyContent = AnyHelper.toAny(new MessageElement(e2));
		inputArgs.set_any(new MessageElement[] {anyContent});
		startInput.setInputArgs(inputArgs);
		WorkflowStatusType status = this.serviceClient.start(startInput);
		status = this.serviceClient.getStatus();
		System.out.println(status.getValue());
		assertTrue(status !=null);
	}
	public  WMSInputType createInput(Calendar terminationTime, 
			String bpelFile) throws Exception {
		WMSInputType input = new WMSInputType();
		input.setTerminationTime(terminationTime);
		String bpelProcess = Utils.fileToStringBuffer(new File(bpelFile)).toString();
		input.setBpelDoc(bpelProcess);
		input.setWorkflowName("Test1");
		WSDLReferences[] wsdlRefArray = new WSDLReferences[1];
		wsdlRefArray[0] = new WSDLReferences();
		wsdlRefArray[0].setServiceUrl(new URI(this.props.getProperty("SERVICE1_ENDPOINT")));
		wsdlRefArray[0].setWsdlLocation(this.props.getProperty("SERVICE1_WSDL"));
		wsdlRefArray[0].setWsdlNamespace(new URI(this.props.getProperty("SERVICE1_NAMESPACE")));

		input.setWsdlReferences(wsdlRefArray);
		return input;
	}
}
