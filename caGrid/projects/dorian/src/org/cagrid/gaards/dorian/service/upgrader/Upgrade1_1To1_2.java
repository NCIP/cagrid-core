package org.cagrid.gaards.dorian.service.upgrader;

import org.cagrid.gaards.dorian.service.PropertyManager;
import org.cagrid.tools.database.Database;


public class Upgrade1_1To1_2 extends Upgrade {

    public String getStartingVersion() {
        return PropertyManager.DORIAN_VERSION_1_1;
    }


    public String getUpgradedVersion() {
        return PropertyManager.DORIAN_VERSION_1_2;
    }


    public void upgrade(boolean trialRun) throws Exception {
        Database db = getBeanUtils().getDatabase();
        db.createDatabaseIfNeeded();
        PropertyManager pm = new PropertyManager(db);
        if (pm.getVersion().equals(PropertyManager.DORIAN_VERSION_1_1)) {
            if (!trialRun) {
                pm.setVersion(PropertyManager.DORIAN_VERSION_1_2);
            }
        } else {
            if (!trialRun) {
                throw new Exception("Failed to run upgrader " + getClass().getName()
                    + " the version of Dorian you are running is not " + PropertyManager.DORIAN_VERSION_1_1 + ".");
            }
        }
    }
}
