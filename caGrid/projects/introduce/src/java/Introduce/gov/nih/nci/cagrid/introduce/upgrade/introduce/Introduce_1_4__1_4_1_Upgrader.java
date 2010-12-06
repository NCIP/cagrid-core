/**
 * 
 */
package gov.nih.nci.cagrid.introduce.upgrade.introduce;

import gov.nih.nci.cagrid.introduce.common.ServiceInformation;
import gov.nih.nci.cagrid.introduce.upgrade.common.IntroduceUpgradeStatus;
import gov.nih.nci.cagrid.introduce.upgrade.one.x.IntroduceUpgraderBase;

/**
 * Upgrade a service created by introduce 1.4 to introduce 1.4.1 standards.
 * 
 * @author Mark Grand
 */
public class Introduce_1_4__1_4_1_Upgrader extends IntroduceUpgraderBase {

    /**
     * Constructor
     * 
     * @param status
     * @param serviceInformation
     * @param servicePath
     * @throws Exception
     */
    public Introduce_1_4__1_4_1_Upgrader(IntroduceUpgradeStatus status, ServiceInformation serviceInformation, String servicePath) throws Exception {
        super(status, serviceInformation, servicePath, "1.4", "1.4.1");
    }

    /* (non-Javadoc)
     * @see gov.nih.nci.cagrid.introduce.upgrade.one.x.IntroduceUpgraderBase#upgrade()
     */
    @Override
    protected void upgrade() throws Exception {
        // TODO Auto-generated method stub

    }

}
