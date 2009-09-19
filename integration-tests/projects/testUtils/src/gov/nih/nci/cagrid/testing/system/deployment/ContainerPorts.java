package gov.nih.nci.cagrid.testing.system.deployment;

import java.net.ServerSocket;


/**
 * ContainerPorts
 * 
 * @author oster
 * @author ervin
 * @created Mar 29, 2007 10:58:18 AM
 * @version $Id: multiscaleEclipseCodeTemplates.xml,v 1.1 2007/03/02 14:35:01
 *          dervin Exp $
 */
public class ContainerPorts {

    private Integer port = null;
    private Integer shutdownPort = null;


    public ContainerPorts(Integer port, Integer shutdownPort) {
        this.port = port;
        this.shutdownPort = shutdownPort;
    }


    /**
     * Gets the port number the container listens for connections on
     * 
     * @return The port number
     */
    public Integer getPort() {
        return port;
    }


    /**
     * Gets the port number the container listens for shutdown requests on 
     * 
     * @return
     */
    public Integer getShutdownPort() {
        return shutdownPort;
    }


    /**
     * Determines if the port is available or not
     * @return
     */
    public boolean isPortAvailable() {
        boolean available = false;
        ServerSocket sock = null;
        try {
            sock = new ServerSocket(port.intValue());
            available = true;
        } catch (Throwable e) {
        } finally {
            if (sock != null) {
                try {
                    sock.close();
                } catch (Throwable t) {
                }
            }
        }
        return available;
    }
}
