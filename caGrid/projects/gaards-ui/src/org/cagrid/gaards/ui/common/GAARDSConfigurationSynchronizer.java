package org.cagrid.gaards.ui.common;

import org.cagrid.gaards.ui.dorian.ServicesManager;
import org.cagrid.grape.ConfigurationSynchronizer;

public class GAARDSConfigurationSynchronizer implements ConfigurationSynchronizer {

    public void syncronize() throws Exception {
        ServicesManager.getInstance().syncWithUpdatedConfiguration();
    }

}
