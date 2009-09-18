package org.cagrid.introduce.test.system.steps;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.introduce.test.TestCaseInfo;
import gov.nih.nci.cagrid.testing.system.deployment.SecureContainer;
import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.io.File;


public class CopyProxyStep extends Step {
    private TestCaseInfo tci;
    private SecureContainer container;


    public CopyProxyStep(SecureContainer container, TestCaseInfo tci) throws Exception {
        this.tci = tci;
        this.container = container;

    }


    public void runStep() throws Throwable {
        System.out.println("Copying user proxys to services dir");

        File inFileClient = new File(container.getCertificatesDirectory().getAbsolutePath() + File.separator + "user.proxy");
        File outFileClient = new File(tci.getDir() + File.separator + "user.proxy");
        Utils.copyFile(inFileClient, outFileClient);
        
        inFileClient = new File(container.getCertificatesDirectory().getAbsolutePath() + File.separator + "user2.proxy");
        outFileClient = new File(tci.getDir() + File.separator + "user2.proxy");
        Utils.copyFile(inFileClient, outFileClient);
        
        inFileClient = new File(container.getCertificatesDirectory().getAbsolutePath() + File.separator + "user3.proxy");
        outFileClient = new File(tci.getDir() + File.separator + "user3.proxy");
        Utils.copyFile(inFileClient, outFileClient);
    }

}
