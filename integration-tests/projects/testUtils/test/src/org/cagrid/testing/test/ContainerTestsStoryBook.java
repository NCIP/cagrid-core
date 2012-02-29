package org.cagrid.testing.test;

import gov.nih.nci.cagrid.testing.system.deployment.ServiceContainerFactory;
import gov.nih.nci.cagrid.testing.system.deployment.ServiceContainerType;
import gov.nih.nci.cagrid.testing.system.haste.Story;

import java.io.IOException;


public class ContainerTestsStoryBook {

    @org.junit.Test
    public void testAllContainers() throws Throwable {
        try {
            ServiceContainerFactory.setMaxContainerHeapSizeMB(Integer.valueOf(256));
            
            Story s2 = new ContainerTest(ServiceContainerFactory
                .createContainer(ServiceContainerType.TOMCAT_CONTAINER));
        	
        	s2.runBare();
        	
        	Story s3 = new ContainerTest(ServiceContainerFactory
                .createContainer(ServiceContainerType.SECURE_TOMCAT_CONTAINER));
        	
        	s3.runBare();
        	
        	Story s4 = new ContainerTest(ServiceContainerFactory
        	    .createContainer(ServiceContainerType.TOMCAT_6_CONTAINER));
        	
        	s4.runBare();
        	
        	Story s5 = new ContainerTest(ServiceContainerFactory
        	    .createContainer(ServiceContainerType.SECURE_TOMCAT_6_CONTAINER));
        	
        	s5.runBare();
        	
        	Story s6 = new ContainerTest(ServiceContainerFactory
        	    .createContainer(ServiceContainerType.JBOSS_51_CONTAINER));
        	
        	s6.runBare();
        	
        	Story s7 = new ContainerTest(ServiceContainerFactory
                .createContainer(ServiceContainerType.SECURE_JBOSS_51_CONTAINER));
            
            s7.runBare();        	
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
