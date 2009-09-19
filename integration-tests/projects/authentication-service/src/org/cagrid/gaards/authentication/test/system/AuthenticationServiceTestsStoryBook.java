package org.cagrid.gaards.authentication.test.system;

import gov.nih.nci.cagrid.testing.system.deployment.ServiceContainerFactory;
import gov.nih.nci.cagrid.testing.system.deployment.ServiceContainerType;
import gov.nih.nci.cagrid.testing.system.haste.Story;

import java.io.File;

import org.junit.Test;

public class AuthenticationServiceTestsStoryBook {
	
	

    @Test
    public void authenticationServiceTests() throws Throwable {
    	Story s = new AuthenticationServiceTest(
							ServiceContainerFactory
									.createContainer(ServiceContainerType.SECURE_TOMCAT_CONTAINER),
			new File("resources/authentication-config.xml"));

    	s.runBare();
	}

}
