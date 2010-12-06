/**
 * 
 */
package gov.nih.nci.cagrid.introduce.upgrade.introduce;

import gov.nih.nci.cagrid.introduce.common.ServiceInformation;
import gov.nih.nci.cagrid.introduce.upgrade.common.IntroduceUpgradeStatus;
import gov.nih.nci.cagrid.introduce.upgrade.one.x.IntroduceUpgraderBase;

/**
 * Upgrade a service created by introduce 1.2 to introduce 1.4.1 standards.
 * 
 * @author Mark Grand
 */
public class Introduce_1_2__1_4_1_Upgrader extends IntroduceUpgraderBase {
    private Introduce_1_2__1_4_Upgrader upgrader_1_4;
    private Introduce_1_4__1_4_1_Upgrader upgrader_1_4_1;

    /**
     * Constructor
     * 
     * @param status
     * @param serviceInformation
     * @param servicePath
     * @throws Exception
     */
    public Introduce_1_2__1_4_1_Upgrader(IntroduceUpgradeStatus status, ServiceInformation serviceInformation, String servicePath) throws Exception {
        super(status, serviceInformation, servicePath, "1.2", "1.4.1");
        upgrader_1_4 = new Introduce_1_2__1_4_Upgrader(status, serviceInformation, servicePath);
        upgrader_1_4_1 = new Introduce_1_4__1_4_1_Upgrader(status, serviceInformation, servicePath);
    }

    /* (non-Javadoc)
     * @see gov.nih.nci.cagrid.introduce.upgrade.one.x.IntroduceUpgraderBase#upgrade()
     */
    @Override
    protected void upgrade() throws Exception {
        upgrader_1_4.upgrade();
        upgrader_1_4_1.upgrade();
    }

}
