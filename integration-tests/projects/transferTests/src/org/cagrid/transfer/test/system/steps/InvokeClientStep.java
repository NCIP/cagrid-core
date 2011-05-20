package org.cagrid.transfer.test.system.steps;

import java.util.List;

import gov.nih.nci.cagrid.introduce.common.AntTools;
import gov.nih.nci.cagrid.introduce.common.CommonTools;
import gov.nih.nci.cagrid.introduce.test.TestCaseInfo;
import gov.nih.nci.cagrid.introduce.test.steps.BaseStep;
import gov.nih.nci.cagrid.testing.system.deployment.ServiceContainer;


public class InvokeClientStep extends BaseStep {
    public static final String TEST_URL_SUFFIX = "/wsrf/services/cagrid/";

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
        String urlArg = "-Dservice.url=" + 
            container.getServiceEPR("cagrid/" + tci.getName()).getAddress().toString();
        cmd.add(urlArg);        
        
        Process p = CommonTools.createAndOutputProcess(cmd, System.out, System.err);
        p.waitFor();

        assertEquals("Unexpected exit of the runClient process", 0, p.exitValue());

        buildStep();
    }
}
