package gov.nci.nih.cagrid.tests.core.util;

import java.net.ServerSocket;
import java.util.Arrays;


/**
 * PortFinder
 * 
 * @author oster
 * @created Mar 29, 2007 10:58:18 AM
 * @version $Id: multiscaleEclipseCodeTemplates.xml,v 1.1 2007/03/02 14:35:01
 *          dervin Exp $
 */
public class PortPreference {
    private Integer portRangeMinimum;
    private Integer portRangeMaximum;
    private Integer specificPort;
    private Integer portExcludes[];


    /**
     * @return Returns the portRangeMinimum.
     */
    public Integer getPortRangeMinimum() {
        return this.portRangeMinimum;
    }


    /**
     * @return Returns the portRangeMaximum.
     */
    public Integer getPortRangeMaximum() {
        return this.portRangeMaximum;
    }


    /**
     * @return Returns the specificPort.
     */
    public Integer getSpecificPort() {
        return this.specificPort;
    }


    /**
     * @return Returns the portExcludes.
     */
    public Integer[] getPortExcludes() {
        return this.portExcludes;
    }


    /**
     * Specifies the identified port should be used.
     * 
     * @param specificPort
     * @throws IllegalArgumentException
     *             if port is null or less than 0
     */
    public PortPreference(Integer specificPort) throws IllegalArgumentException {
        if (specificPort == null || specificPort.intValue() < 0) {
            throw new IllegalArgumentException("Cannot specify null or negative port!");
        }
        this.specificPort = specificPort;

    }


    /**
     * Specifies to search the specified port range (inclusive) and find a port
     * that can be bound to, which is not listed in the portExcludes.
     * 
     * @param portRangeMinimum
     * @param portRangeMaximum
     * @param specificPort
     * @param portExcludes
     * @throws IllegalArgumentException
     *             if portRangeMinimum or portRangeMaximum are either null or
     *             less than 0
     */
    public PortPreference(Integer portRangeMinimum, Integer portRangeMaximum, Integer[] portExcludes)
        throws IllegalArgumentException {
        if (portRangeMinimum == null || portRangeMaximum == null || portRangeMinimum.intValue() < 0
            || portRangeMaximum.intValue() < 0) {
            throw new IllegalArgumentException("Cannot specify null or negative range constraints (min,max)=("
                + portRangeMinimum + "," + portRangeMaximum + ")!");
        }

        this.portRangeMinimum = portRangeMinimum;
        this.portRangeMaximum = portRangeMaximum;
        if (portExcludes != null) {
            Arrays.sort(portExcludes);
            this.portExcludes = portExcludes;
        }

    }


    /**
     * If specificPort is specified, that is returned. Otherwise, will search
     * the port range (inclusive) and find a port that can be bound to, which is
     * not listed in the portExcludes.
     * 
     * @return
     * @throws NoAvailablePortException
     */
    public Integer getPort() throws NoAvailablePortException {
        if (this.specificPort != null) {
            return this.specificPort;
        }

        for (int i = this.portRangeMinimum.intValue(); i <= this.portRangeMaximum.intValue(); i++) {
            if (this.portExcludes != null && Arrays.binarySearch(this.portExcludes, new Integer(i)) >= 0) {
                continue;
            }
            ServerSocket sock = null;
            try {
                sock = new ServerSocket(i);
                return new Integer(i);
            } catch (Throwable e) {
            } finally {
                if (sock != null) {
                    try {
                        sock.close();
                    } catch (Throwable t) {
                    }
                }
            }
        }

        throw new NoAvailablePortException("Could not find an available port.");
    }
}
