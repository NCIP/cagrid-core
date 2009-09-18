package gov.nih.nci.cagrid.introduce.test.steps;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.introduce.IntroduceConstants;
import gov.nih.nci.cagrid.introduce.beans.ServiceDescription;
import gov.nih.nci.cagrid.introduce.beans.method.MethodType;
import gov.nih.nci.cagrid.introduce.beans.method.MethodTypeExceptions;
import gov.nih.nci.cagrid.introduce.beans.method.MethodTypeExceptionsException;
import gov.nih.nci.cagrid.introduce.beans.method.MethodTypeInputs;
import gov.nih.nci.cagrid.introduce.beans.method.MethodTypeInputsInput;
import gov.nih.nci.cagrid.introduce.beans.method.MethodTypeOutput;
import gov.nih.nci.cagrid.introduce.beans.method.MethodsType;
import gov.nih.nci.cagrid.introduce.beans.namespace.NamespaceType;
import gov.nih.nci.cagrid.introduce.beans.namespace.SchemaElementType;
import gov.nih.nci.cagrid.introduce.codegen.SyncTools;
import gov.nih.nci.cagrid.introduce.common.CommonTools;
import gov.nih.nci.cagrid.introduce.test.TestCaseInfo;

import java.io.File;

import javax.xml.namespace.QName;


public class AddComplexMethodWithFaultStep extends BaseStep {
	private TestCaseInfo tci;

	private String methodName;


	public AddComplexMethodWithFaultStep(TestCaseInfo tci, String methodName, boolean build) throws Exception {
		super(tci.getDir(), build);
		this.tci = tci;
		this.methodName = methodName;
	}


	public void runStep() throws Throwable {
		System.out.println("Adding a complex method with fault.");

		// copy over the bookstore schema to be used with the test
		File schemaFile = new File(Utils.decodeUrl(this.getClass().getResource("/schema/bookstore.xsd")));
		Utils.copyFile(schemaFile, new File(getBaseDir() + File.separator + tci.getDir() + File.separator + "schema"
			+ File.separator + tci.getName() + File.separator + "bookstore.xsd"));

		ServiceDescription introService = (ServiceDescription) Utils.deserializeDocument(getBaseDir() + File.separator
			+ tci.getDir() + File.separator + "introduce.xml", ServiceDescription.class);
		

		NamespaceType type = new NamespaceType();
		type.setLocation("." + File.separator + "bookstore.xsd");
		type.setNamespace("gme://projectmobius.org/1/BookStore");
		type.setPackageName("projectmobius.org");
		SchemaElementType etype = new SchemaElementType();
		etype.setType("Book");
		SchemaElementType[] etypeArr = new SchemaElementType[1];
		etypeArr[0] = etype;
		type.setSchemaElement(etypeArr);
		
		CommonTools.addNamespace(introService, type);
		
		MethodsType methodsType =  CommonTools.getService(introService.getServices(),tci.getName()).getMethods();
		if(methodsType==null){
			methodsType = new MethodsType();
			CommonTools.getService(introService.getServices(),tci.getName()).setMethods(methodsType);
		}
		
		MethodType method = new MethodType();
		method.setName(methodName);

		// set the output
		MethodTypeOutput output = new MethodTypeOutput();
		output.setQName(new QName("gme://projectmobius.org/1/BookStore","Book"));
		output.setIsArray(false);

		// set some parameters
		MethodTypeInputs inputs = new MethodTypeInputs();
		MethodTypeInputsInput[] inputsArray = new MethodTypeInputsInput[1];
		MethodTypeInputsInput input = new MethodTypeInputsInput();
		input.setName("inputOne");
		input.setQName(new QName("gme://projectmobius.org/1/BookStore","Book"));
		input.setIsArray(true);
		inputsArray[0] = input;
		inputs.setInput(inputsArray);
		method.setInputs(inputs);

		// set a fault
		MethodTypeExceptionsException[] exceptionsArray = new MethodTypeExceptionsException[1];
		MethodTypeExceptionsException exception = new MethodTypeExceptionsException();
		exception.setName("testFault");
		exceptionsArray[0] = exception;
		MethodTypeExceptions exceptions = new MethodTypeExceptions();
		exceptions.setException(exceptionsArray);
		method.setExceptions(exceptions);

		method.setOutput(output);

		// add new method to array in bean
		// this seems to be a wierd way be adding things....
		MethodType[] newMethods;
		int newLength = 0;
		if (methodsType.getMethod() != null) {
			newLength = methodsType.getMethod().length + 1;
			newMethods = new MethodType[newLength];
			System.arraycopy(methodsType.getMethod(), 0, newMethods, 0, methodsType.getMethod().length);
		} else {
			newLength = 1;
			newMethods = new MethodType[newLength];
		}
		newMethods[newLength - 1] = method;
		methodsType.setMethod(newMethods);

		Utils.serializeDocument(getBaseDir() + File.separator + tci.getDir() + File.separator + "introduce.xml",
			introService, IntroduceConstants.INTRODUCE_SKELETON_QNAME);
		
		Thread.sleep(10000);
		
		try {
			SyncTools sync = new SyncTools(new File(getBaseDir() + File.separator + tci.getDir()));
			sync.sync();
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

		// look at the interface to make sure method exists.......
		String serviceInterface = getBaseDir() + File.separator + tci.getDir() + File.separator + "src" + File.separator
			+ tci.getPackageDir() + File.separator + File.separator + "common" + File.separator + tci.getName() + "I.java";
		assertTrue(StepTools.methodExists(serviceInterface, methodName));
		
		buildStep();

	}
}
