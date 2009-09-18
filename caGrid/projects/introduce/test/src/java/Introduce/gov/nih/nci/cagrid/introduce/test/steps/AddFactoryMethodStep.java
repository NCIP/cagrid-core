package gov.nih.nci.cagrid.introduce.test.steps;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.introduce.IntroduceConstants;
import gov.nih.nci.cagrid.introduce.beans.ServiceDescription;
import gov.nih.nci.cagrid.introduce.beans.method.MethodType;
import gov.nih.nci.cagrid.introduce.beans.method.MethodTypeInputs;
import gov.nih.nci.cagrid.introduce.beans.method.MethodTypeOutput;
import gov.nih.nci.cagrid.introduce.codegen.SyncTools;
import gov.nih.nci.cagrid.introduce.common.CommonTools;
import gov.nih.nci.cagrid.introduce.test.TestCaseInfo;

import java.io.File;

import javax.xml.namespace.QName;


public class AddFactoryMethodStep extends BaseStep {
    private TestCaseInfo factorytci;
    private TestCaseInfo ctci;
    private String factoryMethodName;
    private boolean copyFiles;


    public AddFactoryMethodStep(TestCaseInfo factorytci, TestCaseInfo ctci, boolean build) throws Exception {
        super(factorytci.getDir(), build);
        this.factorytci = factorytci;
        this.ctci = ctci;
        this.factoryMethodName = "create" + ctci.getName();
        this.copyFiles = copyFiles;
    }


    public void runStep() throws Throwable {
        System.out.println("Adding an factory method for " + factorytci.getName() + " generating " + ctci.getName());

        ServiceDescription introService = (ServiceDescription) Utils.deserializeDocument(getBaseDir() + File.separator
            + factorytci.getDir() + File.separator + "introduce.xml", ServiceDescription.class);

        MethodType method = new MethodType();
        method.setName(this.factoryMethodName);
        MethodTypeOutput output = new MethodTypeOutput();
        output.setIsClientHandle(true);
        output.setIsCreatingResourceForClientHandle(true);
        output.setClientHandleClass(ctci.getPackageName()+".client." + ctci.getName() + "Client");
        output.setResourceClientIntroduceServiceName(ctci.getName());
        output.setQName(new QName(ctci.getNamespace()+ "/types",ctci.getName() + "Reference"));
        method.setOutput(output);

        MethodTypeInputs inputs = new MethodTypeInputs();
        method.setInputs(inputs);

        CommonTools.addMethod(CommonTools.getService(introService.getServices(), factorytci.getName()), method);
        
        Utils.serializeDocument(getBaseDir() + File.separator + factorytci.getDir() + File.separator + "introduce.xml",
            introService, IntroduceConstants.INTRODUCE_SKELETON_QNAME);
        
        try {
            SyncTools sync = new SyncTools(new File(getBaseDir() + File.separator + factorytci.getDir()));
            sync.sync();
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

        buildStep();
    }

}
