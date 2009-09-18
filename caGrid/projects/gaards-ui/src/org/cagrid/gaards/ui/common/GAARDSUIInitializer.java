package org.cagrid.gaards.ui.common;

import org.cagrid.gaards.ui.dorian.ServicesManager;
import org.cagrid.grape.ApplicationInitializer;
import org.cagrid.grape.model.Application;


public class GAARDSUIInitializer implements ApplicationInitializer {

    public void intialize(Application app) throws Exception {
        ServicesManager.getInstance();   
    }

}
