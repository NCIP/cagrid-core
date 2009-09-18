package gov.nih.nci.cagrid.introduce.test.steps;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.introduce.IntroduceConstants;
import gov.nih.nci.cagrid.introduce.beans.ServiceDescription;
import gov.nih.nci.cagrid.introduce.beans.method.MethodType;
import gov.nih.nci.cagrid.introduce.beans.method.MethodTypeInputs;
import gov.nih.nci.cagrid.introduce.beans.method.MethodTypeInputsInput;
import gov.nih.nci.cagrid.introduce.beans.method.MethodsType;
import gov.nih.nci.cagrid.introduce.codegen.SyncTools;
import gov.nih.nci.cagrid.introduce.common.CommonTools;
import gov.nih.nci.cagrid.introduce.test.TestCaseInfo;

import java.io.File;

import javax.xml.namespace.QName;


public class ModifySimpleMethodStep extends BaseStep {
	private TestCaseInfo tci;
	private String methodName;


	public ModifySimpleMethodStep(TestCaseInfo tci, String methodName, boolean build) throws Exception {
		super(tci.getDir(),build);
		this.tci = tci;
		this.methodName = methodName;
	}


	public void runStep() throws Throwable {
		System.out.println("modifying the simple method.");
		
		ServiceDescription introService = (ServiceDescription) Utils.deserializeDocument(getBaseDir() + File.separator
			+ tci.getDir() + File.separator + "introduce.xml", ServiceDescription.class);
		MethodsType methodsType = CommonTools.getService(introService.getServices(),tci.getName()).getMethods();
		MethodType method = null;
		for (int i = 0; i < methodsType.getMethod().length; i++) {
			MethodType methodt = methodsType.getMethod(i);
			if (methodt.getName().equals(methodName)) {
				method = methodt;
			}
		}

		// create a new input param
		MethodTypeInputsInput input = new MethodTypeInputsInput();
		input.setQName(new QName("http://www.w3.org/2001/XMLSchema","integer"));
		input.setName("bar");
		input.setIsArray(false);

		// add new input to array in bean
		// this seems to be a wierd way be adding things....
		MethodTypeInputsInput[] newInputs;
		int newLength = 0;
		if (method.getInputs() != null && method.getInputs().getInput() != null) {
			newLength = method.getInputs().getInput().length + 1;
			newInputs = new MethodTypeInputsInput[newLength];
			System.arraycopy(method.getInputs().getInput(), 0, newInputs, 0, method.getInputs().getInput().length);
		} else {
			newLength = 1;
			newInputs = new MethodTypeInputsInput[newLength];
		}
		newInputs[newLength - 1] = input;
		MethodTypeInputs inputs = new MethodTypeInputs();
		inputs.setInput(newInputs);
		method.setInputs(inputs);

		Utils.serializeDocument(getBaseDir() + File.separator + tci.getDir() + File.separator + "introduce.xml",
			introService, IntroduceConstants.INTRODUCE_SKELETON_QNAME);

		try {
			SyncTools sync = new SyncTools(new File(getBaseDir() + File.separator + tci.getDir()));
			sync.sync();
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

		// look at the interface to make sure method exists.......
		String serviceInterface = getBaseDir() + File.separator + tci.getDir() + File.separator + "src" + File.separator
			+ tci.getPackageDir() + File.separator + "common" + File.separator + tci.getName() + "I.java";
		assertTrue(StepTools.methodExists(serviceInterface, methodName));

		buildStep();
	}
}
