/**
*============================================================================
*  Copyright The Ohio State University Research Foundation, The University of Chicago - 
*	Argonne National Laboratory, Emory University, SemanticBits LLC, and 
*	Ekagra Software Technologies Ltd.
*
*  Distributed under the OSI-approved BSD 3-Clause License.
*  See http://ncip.github.com/cagrid-core/LICENSE.txt for details.
*============================================================================
**/
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
    	    ServiceContainerFactory.createContainer(ServiceContainerType.SECURE_TOMCAT_6_CONTAINER),
			new File("resources/authentication-config.xml"));

    	s.runBare();
	}
}
