package org.cagrid.cds.test;

import gov.nih.nci.cagrid.testing.system.deployment.ServiceContainerFactory;
import gov.nih.nci.cagrid.testing.system.deployment.ServiceContainerType;
import gov.nih.nci.cagrid.testing.system.haste.Story;

import java.io.File;

import org.junit.Test;

public class CdsSystemStoryBook {
	
	public static File DORIAN_PROPERTIES_FILE = new File("resources/dorian.properties");
	
    @Test
    public void cdsTest() throws Throwable {
    	Story test = new DelegateCredentialTest(ServiceContainerFactory
                .createContainer(ServiceContainerType.SECURE_TOMCAT_CONTAINER));
    	test.runBare();

    }
}
