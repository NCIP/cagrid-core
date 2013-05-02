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
package gov.nih.nci.cagrid.introduce.test.steps;

import gov.nih.nci.cagrid.introduce.test.TestCaseInfo;
import gov.nih.nci.cagrid.introduce.upgrade.UpgradeManager;
import gov.nih.nci.cagrid.introduce.upgrade.common.UpgradeStatus;


public class UpgradesStep extends BaseStep {
    private TestCaseInfo tci;


    public UpgradesStep(TestCaseInfo tci, boolean build) throws Exception {
        super(tci.getDir(), build);
        this.tci = tci;
    }


    public void runStep() throws Throwable {
        System.out.println("Upgrading Service");


        UpgradeManager upgrader = new UpgradeManager(this.tci.getDir());

        try {
            UpgradeStatus status = upgrader.upgrade();
            System.out.println(status);
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
     
      

        buildStep();
    }
}
