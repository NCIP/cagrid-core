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
package org.cagrid.introduce.test.system.steps;

import java.util.List;

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

        List<String> command = AntTools.getAntCommand("runClient", tci.getDir());
        String url = container.getServiceEPR("cagrid/" + tci.getName()).getAddress().toString();
        command.add("-Dservice.url=" + url);

        Process p = CommonTools.createAndOutputProcess(command, System.out, System.err);
        p.waitFor();
        
        int exitCode = p.exitValue();
        if (exitCode != 0) {
            System.err.println("The error logs:\n");
            System.err.println(container.getErrorLogs().toString());
            System.err.println("The output logs:\n");
            System.err.println(container.getOutLogs().toString());
        }
        
        assertEquals("Run Client did not complete successfully ", 0, p.exitValue());
    }
}
