package org.cagrid.gridgrouper.test.system;

import gov.nih.nci.cagrid.testing.system.deployment.ServiceContainerFactory;
import gov.nih.nci.cagrid.testing.system.deployment.ServiceContainerType;
import gov.nih.nci.cagrid.testing.system.haste.Story;

import java.io.File;

import org.junit.Test;

public class GridGrouperSystemTestStoryBook {
	
	public static File DORIAN_PROPERTIES_FILE = new File("../dorian/resources/dorian.properties");
	
    @Test
    public void gridgrouperTest() throws Throwable {
    	Story test = new GridGrouperTest(ServiceContainerFactory
                .createContainer(ServiceContainerType.SECURE_TOMCAT_CONTAINER), null, DORIAN_PROPERTIES_FILE);
    	test.runBare();

    }
}
