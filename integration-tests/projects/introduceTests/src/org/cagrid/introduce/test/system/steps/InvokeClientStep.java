package org.cagrid.introduce.test.system.steps;

import gov.nih.nci.cagrid.introduce.common.AntTools;
import gov.nih.nci.cagrid.introduce.common.CommonTools;
import gov.nih.nci.cagrid.introduce.test.TestCaseInfo;
import gov.nih.nci.cagrid.introduce.test.steps.BaseStep;
import gov.nih.nci.cagrid.testing.system.deployment.ServiceContainer;


public class InvokeClientStep extends BaseStep { 
    public static final String TEST_URL_SUFFIX = "/wsrf/services/cagrid/";
    
	private TestCaseInfo tci;
	private String methodName;
    private ServiceContainer container;


	public InvokeClientStep(ServiceContainer container, TestCaseInfo tci) throws Exception {
		super(tci.getDir(),false);
		this.tci = tci;
        this.container = container;
	}


	public void runStep() throws Throwable {
		System.out.println("Invoking a simple methods implementation.");

		String cmd = AntTools.getAntCommand("runClient", tci.getDir());
		cmd += " -Dservice.url=";
		if(container.getProperties().isSecure()){
		    cmd +=  "https://";
		} else{
		    cmd +=  "http://";
		}
		cmd +=  "localhost:" 
            + container.getProperties().getPortPreference().getPort() + TEST_URL_SUFFIX + tci.getName();
		Process p = CommonTools.createAndOutputProcess(cmd);
		p.waitFor();
		
		assertTrue(p.exitValue()==0);
	}

}
