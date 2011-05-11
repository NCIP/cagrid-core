package org.cagrid.transfer.test.system.steps;

import java.io.File;
import java.util.List;

import org.apache.axis.message.addressing.EndpointReferenceType;

import gov.nih.nci.cagrid.introduce.common.AntTools;
import gov.nih.nci.cagrid.introduce.common.CommonTools;
import gov.nih.nci.cagrid.introduce.test.TestCaseInfo;
import gov.nih.nci.cagrid.introduce.test.steps.BaseStep;
import gov.nih.nci.cagrid.testing.system.deployment.ServiceContainer;


public class InvokeClientStep extends BaseStep {

    private TestCaseInfo tci;
    private ServiceContainer container;


    public InvokeClientStep(ServiceContainer container, TestCaseInfo tci) throws Exception {
        super(tci.getDir(), false);
        this.tci = tci;
        this.container = container;
    }


    public void runStep() throws Throwable {
        System.out.println("Invoking a simple methods implementation.");

        List<String> cmd = AntTools.getAntCommand("runClient", tci.getDir());
        EndpointReferenceType serviceEPR = container.getServiceEPR("cagrid/" + tci.getName());
        String urlArg = "-Dservice.url=" + serviceEPR.getAddress().toString();
        cmd.add(urlArg);
        if (container.getProperties().isSecure()) {
            String certdir = "-DX509_CERT_DIR=" + container.getProperties().getContainerDirectory().getAbsolutePath() + File.separator + "certificates" + File.separator + "ca";
            cmd.add(certdir);
        }
        
        Process p = CommonTools.createAndOutputProcess(cmd, System.out, System.err);
        p.waitFor();

        assertTrue("ant runClient did not successfully complete!", p.exitValue() == 0);

        buildStep();
    }
}
