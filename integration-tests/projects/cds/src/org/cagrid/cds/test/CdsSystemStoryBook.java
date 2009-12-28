package org.cagrid.cds.test;

import gov.nih.nci.cagrid.testing.system.deployment.ServiceContainerFactory;
import gov.nih.nci.cagrid.testing.system.deployment.ServiceContainerType;
import gov.nih.nci.cagrid.testing.system.haste.Story;

import org.junit.Test;

public class CdsSystemStoryBook {
	    
	@Test
    public void cdsTest() throws Throwable {
    	Story test = new DelegateCredentialTest(ServiceContainerFactory
                .createContainer(ServiceContainerType.SECURE_TOMCAT_CONTAINER));
    	test.runBare();

    }
}
