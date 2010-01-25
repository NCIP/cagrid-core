package org.cagrid.data.test.upgrades.from1pt3.bdt;

import gov.nih.nci.cagrid.data.BdtMethodConstants;
import gov.nih.nci.cagrid.data.DataServiceConstants;
import gov.nih.nci.cagrid.introduce.beans.method.MethodType;
import gov.nih.nci.cagrid.introduce.beans.service.ServiceType;
import gov.nih.nci.cagrid.introduce.common.CommonTools;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;
import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.io.File;

import org.cagrid.data.test.creation.DataTestCaseInfo;

public class VerifyBdtRemovedStep extends Step {
    
    private DataTestCaseInfo testInfo = null;
    
    public VerifyBdtRemovedStep(DataTestCaseInfo testInfo) {
        super();
        this.testInfo = testInfo;
    }
    

    public void runStep() throws Throwable {
        ServiceInformation info = new ServiceInformation(new File(testInfo.getDir()));
        ServiceType baseService = info.getServices().getService(0);
        MethodType bdtQueryMethod = CommonTools.getMethod(baseService.getMethods(), 
            BdtMethodConstants.BDT_QUERY_METHOD_NAME);
        assertNull(BdtMethodConstants.BDT_QUERY_METHOD_NAME + " was not removed from the service", bdtQueryMethod);
    }
}
