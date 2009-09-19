package org.cagrid.transfer.test.system.steps;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.introduce.test.TestCaseInfo;
import gov.nih.nci.cagrid.testing.system.deployment.SecureContainer;
import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.io.File;


public class CopyCAStep extends Step {
    private TestCaseInfo tci;
    private SecureContainer container;


    public CopyCAStep(SecureContainer container, TestCaseInfo tci) throws Exception {
        this.tci = tci;
        this.container = container;

    }


    public void runStep() throws Throwable {
        System.out.println("Copying user proxys to services dir");

        File inFileClient = new File(container.getCertificatesDirectory().getAbsolutePath() + File.separator + "ca" + File.separator + "testing_ca_cert.0");
        File outFileClient = new File(tci.getDir() + File.separator + "caCert.0");
        Utils.copyFile(inFileClient, outFileClient);
    }

}
