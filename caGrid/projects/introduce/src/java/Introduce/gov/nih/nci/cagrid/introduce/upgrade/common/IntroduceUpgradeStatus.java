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
package gov.nih.nci.cagrid.introduce.upgrade.common;

import java.util.ArrayList;
import java.util.List;

public class IntroduceUpgradeStatus extends StatusBase {

    private List extensionUpgradesStatus = null;

    public IntroduceUpgradeStatus() {
        extensionUpgradesStatus = new ArrayList();
    }
    
    public void addExtensionUpgradeStatus(ExtensionUpgradeStatus extensionStatus){
        this.extensionUpgradesStatus.add(extensionStatus);
    }
    
    public List getExtensionUgradesStatus(){
        return this.extensionUpgradesStatus;
    }
}
