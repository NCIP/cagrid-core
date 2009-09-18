package gov.nih.nci.cagrid.workflow.tests;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.workflow.client.WorkflowFactoryServiceClient;
import gov.nih.nci.cagrid.workflow.context.client.WorkflowServiceImplClient;
import gov.nih.nci.cagrid.workflow.stubs.types.StartInputType;
import gov.nih.nci.cagrid.workflow.stubs.types.WMSInputType;
import gov.nih.nci.cagrid.workflow.stubs.types.WMSOutputType;
import gov.nih.nci.cagrid.workflow.stubs.types.WSDLReferences;
import gov.nih.nci.cagrid.workflow.stubs.types.WorkflowInputType;
import gov.nih.nci.cagrid.workflow.stubs.types.WorkflowStatusType;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;

import javax.xml.namespace.QName;

import org.apache.axis.message.MessageElement;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.axis.types.URI;
import org.globus.wsrf.encoding.ObjectSerializer;
import org.globus.wsrf.test.GridTestCase;
import org.globus.wsrf.utils.AnyHelper;
import org.globus.wsrf.utils.XmlUtils;
import org.w3c.dom.Element;

public class BasicSecurityTests extends GridTestCase {
	
	public String url = "http://localhost:8080/wsrf/services/cagrid/WorkflowFactoryService";
	private static WorkflowFactoryServiceClient factoryClient = null;
	private static WorkflowServiceImplClient serviceClient = null;
	private static EndpointReferenceType epr = null;
	
	
	public BasicSecurityTests(String name) {
		super(name);
	}
	
/*	public void testBasicSecurity() throws Exception {
		String inputFile = "inputTest1.xml";
		assertTrue(TEST_CONTAINER != null);
		FileWriter writer = null;
		this.factoryClient = new WorkflowFactoryServiceClient(url);
		WSDLReferences[] wsdlRefArray = new WSDLReferences[2];
		wsdlRefArray[0] = new WSDLReferences();
		wsdlRefArray[1] = new WSDLReferences();

        wsdlRefArray[0].setServiceUrl(new URI("http://localhost:8080/wsrf/services/cagrid/WorkflowTestService1"));
		wsdlRefArray[0].setWsdlLocation("http://localhost:8080/wsrf/share/schema/WorkflowTestService1/WorkflowTestService1.wsdl");
		wsdlRefArray[0].setWsdlNamespace(new URI("http://sample1.tests.workflow.cagrid.nci.nih.gov/WorkflowTestService1"));
		
		wsdlRefArray[1].setServiceUrl(new URI("https://localhost:8443/wsrf/services/cagrid/SecureSample"));
		wsdlRefArray[1].setWsdlLocation("http://localhost:8080/wsrf/share/schema/_cagrid_SecureSample/SecureSample_flattened.wsdl");
		wsdlRefArray[1].setWsdlNamespace(new URI("http://cagrid.nci.nih.gov/SecureSample"));
	
		WMSInputType input = createInput("SimpleSecure.bpel");
		input.setWsdlReferences(wsdlRefArray);
		input.setWorkflowName("SimpleSecure");
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
		
	}*/
	
	public void testSecureConvMessage() throws Exception {
        String inputFile = "inputTest1.xml";
        assertTrue(TEST_CONTAINER != null);
        FileWriter writer = null;
        this.factoryClient = new WorkflowFactoryServiceClient(url);
        WSDLReferences[] wsdlRefArray = new WSDLReferences[2];
        wsdlRefArray[0] = new WSDLReferences();
        wsdlRefArray[1] = new WSDLReferences();

        wsdlRefArray[0].setServiceUrl(new URI("http://localhost:8080/wsrf/services/cagrid/WorkflowTestService1"));
		wsdlRefArray[0].setWsdlLocation("http://localhost:8080/wsrf/share/schema/WorkflowTestService1/WorkflowTestService1.wsdl");
		wsdlRefArray[0].setWsdlNamespace(new URI("http://sample1.tests.workflow.cagrid.nci.nih.gov/WorkflowTestService1"));

        wsdlRefArray[1].setServiceUrl(new URI("http://localhost:8080/wsrf/services/cagrid/WorkflowTestService2"));
        wsdlRefArray[1].setWsdlLocation("http://localhost:8080/wsrf/share/schema/WorkflowTestService2/WorkflowTestService2.wsdl");
        wsdlRefArray[1].setWsdlNamespace(new URI("http://sample2.tests.workflow.cagrid.nci.nih.gov/WorkflowTestService2"));

        WMSInputType input = createInput("SecureConv.bpel");
        input.setWsdlReferences(wsdlRefArray);
        input.setWorkflowName("SecureConv");
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

	
	public   WMSInputType createInput(String bpelFile) throws Exception {
		WMSInputType input = new WMSInputType();
		String bpelProcess = Utils.fileToStringBuffer(new File(bpelFile)).toString();
		input.setBpelDoc(bpelProcess);
		
		
		return input;
	}
}
