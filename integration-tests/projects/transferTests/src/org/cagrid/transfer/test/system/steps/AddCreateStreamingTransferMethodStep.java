/**
*============================================================================
*  Copyright The Ohio State University Research Foundation, The University of Chicago - 
*	Argonne National Laboratory, Emory University, SemanticBits LLC, and 
*	Ekagra Software Technologies Ltd.
*
*  Distributed under the OSI-approved BSD 3-Clause License.
*  See http://ncip.github.com/cagrid-core/LICENSE.txt for details.
*============================================================================
**/
package org.cagrid.transfer.test.system.steps;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.introduce.IntroduceConstants;
import gov.nih.nci.cagrid.introduce.beans.ServiceDescription;
import gov.nih.nci.cagrid.introduce.beans.method.MethodType;
import gov.nih.nci.cagrid.introduce.beans.method.MethodTypeInputs;
import gov.nih.nci.cagrid.introduce.beans.method.MethodTypeOutput;
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
import gov.nih.nci.cagrid.testing.system.deployment.ServiceContainer;

import java.io.File;

import javax.xml.namespace.QName;

public class AddCreateStreamingTransferMethodStep extends BaseStep {
    private TestCaseInfo tci;
    private ServiceContainer container;


    public AddCreateStreamingTransferMethodStep(TestCaseInfo tci, ServiceContainer container, boolean build) throws Exception {
        super(tci.getDir(), build);
        this.tci = tci;
        this.container = container;
    }


    public void runStep() throws Throwable {
        System.out.println("Adding a createStreamingTransferMethodStep method.");

        ServiceDescription introService = Utils.deserializeDocument(getBaseDir() + File.separator
            + tci.getDir() + File.separator + "introduce.xml", ServiceDescription.class);
        
        MethodType method = new MethodType();
        method.setName("createStreamingTransferMethodStep");
        MethodTypeOutput output = new MethodTypeOutput();
        output.setQName(new QName("http://transfer.cagrid.org/TransferService/Context/types",
            "TransferServiceContextReference"));
        method.setOutput(output);
        MethodTypeInputs inputs = new MethodTypeInputs();
        method.setInputs(inputs);

        if (container.getProperties().isSecure()) {
            MethodSecurity msec = new MethodSecurity();
            msec.setSecuritySetting(SecuritySetting.Custom);
            TransportLevelSecurity security = new TransportLevelSecurity();
            security.setCommunicationMethod(CommunicationMethod.Privacy);
            msec.setAnonymousClients(AnonymousCommunication.No);
            msec.setTransportLevelSecurity(security);

            method.setMethodSecurity(msec);
        }

        CommonTools.addMethod(CommonTools.getService(introService.getServices(), tci.getName()), method);

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
        assertTrue(StepTools.methodExists(serviceInterface, "createStreamingTransferMethodStep"));

        buildStep();
    }
}
