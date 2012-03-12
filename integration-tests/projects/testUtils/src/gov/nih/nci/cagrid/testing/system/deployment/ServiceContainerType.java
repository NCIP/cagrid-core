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
    SECURE_TOMCAT_CONTAINER, SECURE_TOMCAT_5_CONTAINER, SECURE_TOMCAT_6_CONTAINER,
    JBOSS_51_CONTAINER, SECURE_JBOSS_51_CONTAINER;
    
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
            case TOMCAT_5_CONTAINER:
                return base + "/minimal-tomcat-5.0.28-with-globus-4.0.3.zip";
            case TOMCAT_CONTAINER:
            case TOMCAT_6_CONTAINER:
                return base + "/apache-tomcat-6.0.35-testing.zip";
            case SECURE_TOMCAT_5_CONTAINER:
                return base + "/minimal-secure-tomcat-5.0.28-with-globus-4.0.3.zip";
            case SECURE_TOMCAT_CONTAINER:
            case SECURE_TOMCAT_6_CONTAINER:
                return base + "/apache-tomcat-6.0.35-secure-testing.zip";
            case JBOSS_51_CONTAINER:
                return base + "/jboss-5.1.0.GA-testing.zip";
            case SECURE_JBOSS_51_CONTAINER:
                return base + "/jboss-5.1.0.GA-secure-testing.zip";
        }
        throw new AssertionError("Unknown service container type: " + this);
    }
    
    
    public String toString() {
        switch (this) {
            case TOMCAT_5_CONTAINER:
                return "Tomcat5";
            case TOMCAT_CONTAINER:
            case TOMCAT_6_CONTAINER:
                return "Tomcat6";
            case SECURE_TOMCAT_5_CONTAINER:
                return "SecureTomcat5";
            case SECURE_TOMCAT_CONTAINER:
            case SECURE_TOMCAT_6_CONTAINER:
                return "SecureTomcat6";
            case JBOSS_51_CONTAINER:
                return "JBoss51";
            case SECURE_JBOSS_51_CONTAINER:
                return "SecureJBoss51";
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
