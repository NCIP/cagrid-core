package org.cagrid.introduce.test.system.steps;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.introduce.IntroduceConstants;
import gov.nih.nci.cagrid.introduce.beans.ServiceDescription;
import gov.nih.nci.cagrid.introduce.beans.method.MethodType;
import gov.nih.nci.cagrid.introduce.beans.method.MethodTypeOutput;
import gov.nih.nci.cagrid.introduce.beans.method.MethodsType;
import gov.nih.nci.cagrid.introduce.beans.security.AnonymousCommunication;
import gov.nih.nci.cagrid.introduce.beans.security.CommunicationMethod;
import gov.nih.nci.cagrid.introduce.beans.security.MethodSecurity;
import gov.nih.nci.cagrid.introduce.beans.security.SecuritySetting;
import gov.nih.nci.cagrid.introduce.beans.security.TransportLevelSecurity;
import gov.nih.nci.cagrid.introduce.codegen.SyncTools;
import gov.nih.nci.cagrid.introduce.common.CommonTools;
import gov.nih.nci.cagrid.introduce.test.TestCaseInfo;
import gov.nih.nci.cagrid.introduce.test.steps.BaseStep;
import gov.nih.nci.cagrid.introduce.test.steps.StepTools;

import java.io.File;

import javax.xml.namespace.QName;


public class AddSecurityMethodsStep extends BaseStep {
    private TestCaseInfo tci;


    public AddSecurityMethodsStep(TestCaseInfo tci, boolean build) throws Exception {
        super(tci.getDir(), build);
        this.tci = tci;
    }


    public void runStep() throws Throwable {
        System.out.println("Adding security test methods");

        ServiceDescription introService = (ServiceDescription) Utils.deserializeDocument(getBaseDir() + File.separator
            + tci.getDir() + File.separator + "introduce.xml", ServiceDescription.class);
        MethodsType methodsType = CommonTools.getService(introService.getServices(), tci.getName()).getMethods();

        MethodType method = new MethodType();
        method.setName("anonPrefered");
        MethodTypeOutput output = new MethodTypeOutput();
        output.setQName(new QName(IntroduceConstants.W3CNAMESPACE, "string"));
        method.setOutput(output);
        MethodSecurity security = new MethodSecurity();
        security.setSecuritySetting(SecuritySetting.Custom);
        TransportLevelSecurity sec = new TransportLevelSecurity(CommunicationMethod.Privacy);
        security.setTransportLevelSecurity(sec);
        security.setAnonymousClients(AnonymousCommunication.Yes);
        method.setMethodSecurity(security);

        CommonTools.addMethod(CommonTools.getService(introService.getServices(), tci.getName()), method);

        MethodType method2 = new MethodType();
        method2.setName("anonNotPrefered");
        MethodTypeOutput output2 = new MethodTypeOutput();
        output2.setQName(new QName(IntroduceConstants.W3CNAMESPACE, "string"));
        method2.setOutput(output2);
        MethodSecurity security2 = new MethodSecurity();
        security2.setSecuritySetting(SecuritySetting.Custom);
        TransportLevelSecurity sec2 = new TransportLevelSecurity(CommunicationMethod.Privacy);
        security2.setTransportLevelSecurity(sec);
        security2.setAnonymousClients(AnonymousCommunication.No);
        method2.setMethodSecurity(security2);

        CommonTools.addMethod(CommonTools.getService(introService.getServices(), tci.getName()), method2);

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
        assertTrue(StepTools.methodExists(serviceInterface, "anonPrefered"));
        assertTrue(StepTools.methodExists(serviceInterface, "anonNotPrefered"));

        buildStep();
    }

}
