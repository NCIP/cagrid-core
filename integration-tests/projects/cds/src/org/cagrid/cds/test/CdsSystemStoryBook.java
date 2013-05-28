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
package org.cagrid.cds.test;

import gov.nih.nci.cagrid.testing.system.deployment.ServiceContainerFactory;
import gov.nih.nci.cagrid.testing.system.deployment.ServiceContainerType;
import gov.nih.nci.cagrid.testing.system.haste.Story;

import org.junit.Test;

public class CdsSystemStoryBook {
	    
	@Test
    public void cdsTest() throws Throwable {
    	Story test = new DelegateCredentialTest(ServiceContainerFactory
                .createContainer(ServiceContainerType.SECURE_TOMCAT_6_CONTAINER));
    	test.runBare();
    }
}
