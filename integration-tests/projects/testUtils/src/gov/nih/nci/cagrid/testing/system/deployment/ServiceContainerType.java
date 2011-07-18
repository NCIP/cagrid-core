package gov.nih.nci.cagrid.testing.system.deployment;


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
    TOMCAT_CONTAINER, TOMCAT_5_CONTAINER, TOMCAT_6_CONTAINER, 
    JBOSS_CONTAINER, SECURE_TOMCAT_CONTAINER, SECURE_TOMCAT_5_CONTAINER, SECURE_TOMCAT_6_CONTAINER;
    
    public static final String CONTAINER_DIR_PROPERTY = "testing.containers.dir";
    public static final String DEFAULT_CONTAINER_DIR = "../testUtils/containers";
    
    /**
     * Returns the Container zip file that goes with each container type.
     * For now, TOMCAT_CONTAINER will return the same as TOMCAT_5_CONTAINER,
     * but once stabilized will be moved to TOMCAT_6_CONTAINER
     * @return
     */
    public String getZip() {
        String base = getContainerBaseDir();
        switch (this) {
            case TOMCAT_CONTAINER:
            case TOMCAT_5_CONTAINER:
                return base + "/minimal-tomcat-5.0.28-with-globus-4.0.3.zip";
            case TOMCAT_6_CONTAINER:
                return base + "/apache-tomcat-6.0.32-testing.zip";
            case JBOSS_CONTAINER:
                throw new AssertionError("Container type " + this + " is not yet supported");
            case SECURE_TOMCAT_CONTAINER:
            case SECURE_TOMCAT_5_CONTAINER:
                return base + "/minimal-secure-tomcat-5.0.28-with-globus-4.0.3.zip";
            case SECURE_TOMCAT_6_CONTAINER:
                return base + "/apache-tomcat-6.0.32-secure-testing.zip";
        }
        throw new AssertionError("Unknown service container type: " + this);
    }
    
    
    public String toString() {
        switch (this) {
            case TOMCAT_CONTAINER:
            case TOMCAT_5_CONTAINER:
                return "Tomcat5";
            case TOMCAT_6_CONTAINER:
                return "Tomcat6";
            case JBOSS_CONTAINER:
                return "JBoss";
            case SECURE_TOMCAT_CONTAINER:
            case SECURE_TOMCAT_5_CONTAINER:
                return "SecureTomcat5";
            case SECURE_TOMCAT_6_CONTAINER:
                return "SecureTomcat6";
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
