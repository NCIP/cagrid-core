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
package gov.nih.nci.cagrid.introduce.upgrade.introduce;

import gov.nih.nci.cagrid.introduce.common.ServiceInformation;
import gov.nih.nci.cagrid.introduce.upgrade.common.IntroduceUpgradeStatus;
import gov.nih.nci.cagrid.introduce.upgrade.one.x.IntroduceUpgraderBase;


public class Introduce_1_4__1_6_Upgrader extends IntroduceUpgraderBase {

    public Introduce_1_4__1_6_Upgrader(IntroduceUpgradeStatus status, ServiceInformation serviceInformation,
        String servicePath) throws Exception {
        super(status, serviceInformation, servicePath, "1.4", "1.6");
    }


    protected void upgrade() throws Exception {

    }
    
    protected void fixDevBuildDeploy() throws Exception{

    }


    protected void fixSecurityOnMetadataAccessProviders() {

    }


    protected void fixSource() throws Exception {

    }


    protected void fixWSDD() throws Exception {

    }

}
