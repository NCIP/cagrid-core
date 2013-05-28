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
package org.cagrid.gridgrouper.test.system;

import gov.nih.nci.cagrid.testing.system.deployment.ServiceContainerFactory;
import gov.nih.nci.cagrid.testing.system.deployment.ServiceContainerType;
import gov.nih.nci.cagrid.testing.system.haste.Story;

import java.io.File;

import org.junit.Test;

public class GridGrouperSystemTestStoryBook {
	
	public static File DORIAN_PROPERTIES_FILE = new File("resources/dorian.properties");
	
    @Test
    public void gridgrouperTest() throws Throwable {
        System.setProperty("javax.xml.parsers.DocumentBuilderFactory", "com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl");
    	Story test = new GridGrouperTest(ServiceContainerFactory
                .createContainer(ServiceContainerType.SECURE_TOMCAT_6_CONTAINER), DORIAN_PROPERTIES_FILE);
    	test.runBare();
    }
}
