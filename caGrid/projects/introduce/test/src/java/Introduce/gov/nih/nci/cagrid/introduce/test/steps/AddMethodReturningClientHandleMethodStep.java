package gov.nih.nci.cagrid.introduce.test.steps;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.introduce.IntroduceConstants;
import gov.nih.nci.cagrid.introduce.beans.ServiceDescription;
import gov.nih.nci.cagrid.introduce.beans.method.MethodType;
import gov.nih.nci.cagrid.introduce.beans.method.MethodTypeOutput;
import gov.nih.nci.cagrid.introduce.codegen.SyncTools;
import gov.nih.nci.cagrid.introduce.common.CommonTools;
import gov.nih.nci.cagrid.introduce.test.TestCaseInfo;

import java.io.File;

import javax.xml.namespace.QName;


public class AddMethodReturningClientHandleMethodStep extends BaseStep {
    private TestCaseInfo tci;
    private TestCaseInfo contextTCI;
    private String methodName;
    private boolean isArray;


    public AddMethodReturningClientHandleMethodStep(TestCaseInfo tci, TestCaseInfo contextTCI, String methodName,
        boolean isArray, boolean build) throws Exception {
        super(tci.getDir(), build);
        this.tci = tci;
        this.contextTCI = contextTCI;
        this.methodName = methodName;
        this.isArray = isArray;
    }


    public void runStep() throws Throwable {
        System.out.println("Adding method wich returns client handle with array");

        ServiceDescription introService = (ServiceDescription) Utils.deserializeDocument(getBaseDir() + File.separator
            + tci.getDir() + File.separator + "introduce.xml", ServiceDescription.class);

        MethodType method = new MethodType();
        method.setName(methodName);
        MethodTypeOutput output = new MethodTypeOutput();
        output.setIsArray(this.isArray);
        output.setClientHandleClass(contextTCI.getPackageName() + ".client." + contextTCI.getName() + "Client");
        output.setQName(new QName(contextTCI.getNamespace() + "/types", contextTCI.getName() + "Reference"));
        output.setIsClientHandle(new Boolean(true));
        method.setOutput(output);

        CommonTools.addMethod(introService.getServices().getService(0), method);

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
        String serviceInterface = getBaseDir() + File.separator + tci.getDir() + File.separator + "src"
            + File.separator + tci.getPackageDir() + File.separator + "common" + File.separator + tci.getName()
            + "I.java";
        assertTrue(StepTools.methodExists(serviceInterface, methodName));

        buildStep();
    }

}
