package org.cagrid.data.test.upgrades;

import gov.nih.nci.cagrid.introduce.codegen.SyncTools;
import gov.nih.nci.cagrid.introduce.upgrade.UpgradeManager;
import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.io.File;


/**
 * UpgradeIntroduceServiceStep 
 * Upgrades introduce managed parts of the service
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A> *
 * @created Feb 21, 2007
 * @version $Id: UpgradeIntroduceServiceStep.java,v 1.7 2007/04/16 13:36:12
 *          dervin Exp $
 */
public class UpgradeIntroduceServiceStep extends Step {
    private String serviceDir;


    public UpgradeIntroduceServiceStep(String serviceDir) {
        this.serviceDir = serviceDir;
    }


    public void runStep() throws Throwable {
        // create the introduce upgrade manager
        UpgradeManager upgrader = new UpgradeManager(serviceDir);
        assertTrue("Introduce service should have required upgrade to current version", upgrader.canIntroduceBeUpgraded());
        upgrader.upgrade();

        try {
            SyncTools sync = new SyncTools(new File(serviceDir));
            sync.sync();
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
}
