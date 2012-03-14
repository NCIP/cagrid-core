package org.cagrid.gaards.dorian.service.upgrader;

import org.cagrid.gaards.dorian.service.PropertyManager;
import org.cagrid.tools.database.Database;


public class Upgrade1_4To1_5 extends Upgrade {

    public String getStartingVersion() {
        return PropertyManager.DORIAN_VERSION_1_4;
    }


    public String getUpgradedVersion() {
        return PropertyManager.DORIAN_VERSION_1_5;
    }


    public void upgrade(boolean trialRun) throws Exception {
        Database db = getBeanUtils().getDatabase();
        db.createDatabaseIfNeeded();
        PropertyManager pm = new PropertyManager(db);
        if (pm.getVersion().equals(PropertyManager.DORIAN_VERSION_1_4)) {
            if (!trialRun) {
                System.out.print("Upgrading version number from " + pm.getVersion() + " to "
                    + PropertyManager.DORIAN_VERSION_1_5 + "....");
                pm.setVersion(PropertyManager.DORIAN_VERSION_1_5);
                System.out.println(" COMPLETED.");
            } else {
                System.out.println("The version needs to be upgraded from " + pm.getVersion() + " to "
                    + PropertyManager.DORIAN_VERSION_1_5 + ".");
            }
        } else {
            if (!trialRun) {
                throw new Exception("Failed to run upgrader " + getClass().getName()
                    + " the version of Dorian you are running is not " + PropertyManager.DORIAN_VERSION_1_4 + ".");
            }
        }
    }
}
