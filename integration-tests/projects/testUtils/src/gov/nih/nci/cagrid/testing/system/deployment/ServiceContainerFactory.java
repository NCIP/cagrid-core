package gov.nih.nci.cagrid.testing.system.deployment;

import gov.nih.nci.cagrid.testing.core.TestingConstants;

import java.io.File;
import java.io.IOException;

/** 
 *  ServiceContainerFactory
 *  Creates service container instances
 * 
 * @author David Ervin
 * 
 * @created Oct 16, 2007 12:09:02 PM
 * @version $Id: ServiceContainerFactory.java,v 1.4 2008-11-07 19:13:02 dervin Exp $ 
 */
public class ServiceContainerFactory {
    
    private static Integer maxContainerHeapSize = null;
    
    private ServiceContainerFactory() {
        // prevent instantiation
    }
    
    
    /**
     * Sets the max heap size for the created container 
     * processes. This is equivalent to starting the 
     * process with -Xmx#m set to some value
     * 
     * The provided value may be <code>null</code> to allow 
     * the system default to take precedence.
     * 
     * @param heapMB
     */
    public static void setMaxContainerHeapSizeMB(Integer heapMB) {
        maxContainerHeapSize = heapMB;
    }
    
    
    /**
     * Gets the max heap size for the created container
     * processes.  This may be <code>null</code> if
     * no value has been set, which indicates the system
     * default will take precedence.
     * 
     * @return
     */
    public static Integer getMaxContainerHeapSizeMB() {
        return maxContainerHeapSize;
    }

    /**
     * Creates a new service container of the specified type.
     * No security will be enabled, and the service will start on a 
     * port determined automatically by the PortFactory.
     *  
     * @param type
     *      The container type
     * @return
     *      The service container instance
     * @throws IOException
     */
    public static ServiceContainer createContainer(ServiceContainerType type) throws IOException {
        ContainerPorts ports = PortFactory.getContainerPorts();
        return createContainer(type , ports);
    }
    

    /**
     * Creates a new service container, with all options configured by the caller.
     * <note>For now, the TOMCAT_CONTAINER returns the same as TOMCAT_6_CONTAINER</note>
     * 
     * @param type
     *      The type of container
     * @param ports
     *      The port preference to use when starting the container
     * @return
     *      The service container instance
     * @throws IOException
     */
    public static ServiceContainer createContainer(
        ServiceContainerType type, ContainerPorts ports) throws IOException {
        File containerTempDir = getTempDirectory(type);
        String zipLocation = type.getZip();
        File containerZip = new File(zipLocation);
        ContainerProperties props = new ContainerProperties(containerTempDir,
        	containerZip, ports, false,
        	null, null, getMaxContainerHeapSizeMB());
        
        ServiceContainer container = null;
        switch (type) {
            case TOMCAT_5_CONTAINER:
                container = new TomcatServiceContainer(props);
                break;
            case TOMCAT_CONTAINER:
            case TOMCAT_6_CONTAINER:
                container = new Tomcat6ServiceContainer(props);
                break;
            case SECURE_TOMCAT_5_CONTAINER:
                props.setSecure(true);
                container = new TomcatSecureServiceContainer(props);
                break;
            case SECURE_TOMCAT_CONTAINER:
            case SECURE_TOMCAT_6_CONTAINER:
                props.setSecure(true);
                container = new Tomcat6SecureServiceContainer(props);
                break;
            case JBOSS_CONTAINER:
                throw new UnsupportedOperationException(ServiceContainerType.JBOSS_CONTAINER + " is not yet supported");
            default:
                throw new AssertionError("Service container type: " + type + " is not valid");
        }
        return container;
    }
    
    
    private static File getTempDirectory(ServiceContainerType type) throws IOException {
        File tempDir = new File(TestingConstants.TEST_TEMP_DIR);
        if (!tempDir.exists()) {
            tempDir.mkdirs();
        }
        File tempContainerDir = File.createTempFile(type.toString(), "tmp", tempDir);
        // create a directory, not a file
        tempContainerDir.delete();
        tempContainerDir.mkdirs();
        return tempContainerDir;
    }
    
    
    public static void main(String[] args) {
        ServiceContainerType type = ServiceContainerType.valueOf(args[0]);
        File containerOutDir = new File(args[1]);
        try {
            ContainerPorts ports = PortFactory.getContainerPorts();
            ServiceContainer container = createContainer(type, ports);
            container.getProperties().setContainerDirectory(containerOutDir);
            container.unpackContainer();
            System.out.println("Container listens on port " + ports.getPort() + "\t\tShutdown on port " + ports.getShutdownPort());
        } catch (Exception ex) {
            System.err.println("Error setting up container: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
