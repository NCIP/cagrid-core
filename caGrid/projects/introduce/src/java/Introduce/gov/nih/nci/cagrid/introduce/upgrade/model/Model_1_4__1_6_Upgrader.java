package gov.nih.nci.cagrid.introduce.upgrade.model;

import gov.nih.nci.cagrid.introduce.upgrade.common.IntroduceUpgradeStatus;
import gov.nih.nci.cagrid.introduce.upgrade.common.ModelUpgraderBase;;

public class Model_1_4__1_6_Upgrader extends ModelUpgraderBase {
    
    public Model_1_4__1_6_Upgrader(IntroduceUpgradeStatus status, String servicePath) {
        super(status, servicePath, "1.4", "1.6");
    }
    

    @Override
    protected void upgrade() throws Exception {
        getStatus().addDescriptionLine("Nothing to upgrade in the Introduce service model");
    }

}
