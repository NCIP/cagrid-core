package gov.nih.nci.cagrid.introduce.upgrade.model;

import gov.nih.nci.cagrid.introduce.upgrade.common.IntroduceUpgradeStatus;
import gov.nih.nci.cagrid.introduce.upgrade.common.ModelUpgraderBase;


public class Model_1_3__1_5_Upgrader extends ModelUpgraderBase {
    private Model_1_3__1_4_Upgrader upgrader_1_4;
    private Model_1_4__1_5_Upgrader upgrader_1_5;

    public Model_1_3__1_5_Upgrader(IntroduceUpgradeStatus status, String servicePath) {
        super(status, servicePath, "1.3", "1.5");
        upgrader_1_4 = new Model_1_3__1_4_Upgrader(status, servicePath);
        upgrader_1_5 = new Model_1_4__1_5_Upgrader(status, servicePath);
    }


    protected void upgrade() throws Exception {
       upgrader_1_4.upgrade();
       upgrader_1_5.upgrade();
    }
}
