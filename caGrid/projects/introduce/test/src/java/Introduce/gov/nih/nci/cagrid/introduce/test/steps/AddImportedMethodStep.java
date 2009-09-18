package gov.nih.nci.cagrid.introduce.test.steps;

import gov.nih.nci.cagrid.introduce.beans.method.MethodTypeImportInformation;
import gov.nih.nci.cagrid.introduce.codegen.SyncTools;
import gov.nih.nci.cagrid.introduce.common.CommonTools;
import gov.nih.nci.cagrid.introduce.test.TestCaseInfo;

import java.io.File;

import javax.xml.namespace.QName;


public class AddImportedMethodStep extends BaseStep {
	private TestCaseInfo tci;
	private TestCaseInfo importedTCI;
	private String methodName;
	private boolean copyFiles;


	public AddImportedMethodStep(TestCaseInfo tci, TestCaseInfo importedTCI, String methodName, boolean build,
		boolean copyFiles) throws Exception {
		super(tci.getDir(), build);
		this.tci = tci;
		this.importedTCI = importedTCI;
		this.methodName = methodName;
		this.copyFiles = copyFiles;
	}


	public void runStep() throws Throwable {
		System.out.println("Adding an imported simple method.");

		MethodTypeImportInformation ii = new MethodTypeImportInformation();
		ii.setNamespace(importedTCI.getNamespace());
		ii.setPortTypeName(importedTCI.getName() + "PortType");
		ii.setPackageName(importedTCI.getPackageName() + ".stubs");
		ii.setWsdlFile(importedTCI.getName() + ".wsdl");
		ii.setInputMessage(new QName(importedTCI.getNamespace(), CommonTools.upperCaseFirstCharacter(methodName)
			+ "Request"));
		ii.setOutputMessage(new QName(importedTCI.getNamespace(), CommonTools.upperCaseFirstCharacter(methodName)
			+ "Response"));

		CommonTools.importMethod(ii, new File(getBaseDir() + File.separator + importedTCI.getDir()), new File(
			getBaseDir() + File.separator + tci.getDir()), importedTCI.getName(), tci.getName(), methodName, copyFiles);

		try {
			SyncTools sync = new SyncTools(new File(getBaseDir() + File.separator + tci.getDir()));
			sync.sync();
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

		// look at the interface to make sure method exists.......
		String serviceInterface = getBaseDir() + File.separator + tci.getDir() + File.separator + "src"
			+ File.separator + tci.getPackageDir() + File.separator + "common" + File.separator + tci.getName()
			+ "I.java";
		assertTrue(StepTools.methodExists(serviceInterface, methodName));

		buildStep();
	}

}
