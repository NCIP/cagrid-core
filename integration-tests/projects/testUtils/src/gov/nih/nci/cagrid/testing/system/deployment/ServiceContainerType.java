package gov.nih.nci.cagrid.testing.system.deployment;

import java.util.Calendar;
import java.util.GregorianCalendar;

/** 
 *  ServiceContainerType
 *  Identifies the type of service container
 * 
 * @author David Ervin
 * 
 * @created Oct 16, 2007 12:01:50 PM
 * @version $Id: ServiceContainerType.java,v 1.2 2008-10-13 20:42:35 oster Exp $ 
 */
public enum ServiceContainerType {
    GLOBUS_CONTAINER, TOMCAT_CONTAINER, JBOSS_CONTAINER, SECURE_TOMCAT_CONTAINER;
    
    public static final String CONTAINER_DIR_PROPERTY = "testing.containers.dir";
    public static final String DEFAULT_CONTAINER_DIR = "../testUtils/containers";
    
    public String getZip() {
        String base = getContainerBaseDir();
        switch (this) {
            case GLOBUS_CONTAINER:
                throw new AssertionError("Container type " + this + " is no longer supported");
            case TOMCAT_CONTAINER:
                return base + "/minimal-tomcat-5.5.27-with-globus-4.0.3.zip";
            case JBOSS_CONTAINER:
                throw new AssertionError("Container type " + this + " is not yet supported");
            case SECURE_TOMCAT_CONTAINER:
                return base + "/minimal-secure-tomcat-5.5.27-with-globus-4.0.3.zip";
        }
        throw new AssertionError("Unknown service container type: " + this);
    }
    
    
    public String toString() {
        switch (this) {
            case GLOBUS_CONTAINER:
                return "Globus";
            case TOMCAT_CONTAINER:
                return "Tomcat";
            case JBOSS_CONTAINER:
                return "JBoss";
            case SECURE_TOMCAT_CONTAINER:
                return "SecureTomcat";
        }
        throw new AssertionError("Unknown service container type: " + this);
    }
    
    
    private String getContainerBaseDir() {
        String baseDir = System.getProperty(CONTAINER_DIR_PROPERTY);
        if (baseDir == null) {
            baseDir = DEFAULT_CONTAINER_DIR;
        }
        return baseDir;
    }
    
}
