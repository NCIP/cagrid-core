package gov.nih.nci.cagrid.testing.system.deployment;

import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


/**
 * PortFactory Utility to hand out PortPreference instances in a static way.
 * This should help in the static initialization which Haste uses when setting
 * up story steps.
 * 
 * @author David Ervin
 * @created Nov 5, 2007 10:13:07 AM
 * @version $Id: PortFactory.java,v 1.5 2009-01-08 19:16:46 dervin Exp $
 */
public class PortFactory {
    private static List<Integer> assignedPortNumbers = null;


    public static synchronized ContainerPorts getContainerPorts() throws NoAvailablePortException {
        if (assignedPortNumbers == null) {
            assignedPortNumbers = new LinkedList<Integer>();
        }
        Integer[] usedPorts = new Integer[assignedPortNumbers.size()];
        assignedPortNumbers.toArray(usedPorts);
        ContainerPorts port = null;

        Integer testPort = getPort();
        assignedPortNumbers.add(testPort);
        Integer shutdownPort = getPort();
        assignedPortNumbers.add(shutdownPort);
        port = new ContainerPorts(testPort, shutdownPort);
        
        return port;
    }


    public static List<Integer> getAssignedPortNumbers() {
        // clone it so modifications to the returned list won't
        // affect the inner workings of this utility
        List<Integer> clone = new ArrayList<Integer>(assignedPortNumbers.size());
        clone.addAll(assignedPortNumbers);
        return clone;
    }


    /**
     * Iterates through all assigned ports and checks if they are currently
     * bound. All unbound ports will be removed from the assignment list, and
     * made available for future assignment.
     */
    public static synchronized void reclaimPorts() {
        Iterator<Integer> portIter = assignedPortNumbers.iterator();
        while (portIter.hasNext()) {
            Integer port = portIter.next();
            if (isPortAvailable(port.intValue())) {
                portIter.remove();
            }
        }
    }


    private static boolean isPortAvailable(int port) {
        boolean available = false;
        ServerSocket sock = null;
        try {
            sock = new ServerSocket(port);
            available = true;
        } catch (Throwable e) {
        } finally {
            if (sock != null) {
                try {
                    sock.close();
                } catch (Throwable t) {
                    available = false;
                }
            }
        }
        return available;
    }


    private static Integer getPort() throws NoAvailablePortException {
        int range = TestingConstants.TEST_PORT_UPPER_BOUND.intValue()
            - TestingConstants.TEST_PORT_LOWER_BOUND.intValue();
        boolean used = true;
        Integer testPort = null;
        int offset = (int) (System.currentTimeMillis() % range);
        for (int count = 0; count < range; count++) {
            offset = (offset + count) % range;
            testPort = Integer.valueOf(TestingConstants.TEST_PORT_LOWER_BOUND.intValue() + offset);
            used = assignedPortNumbers.contains(testPort) || !isPortAvailable(testPort.intValue());
            if (!used) {
                // found an unused port
                break;
            }
        }
        if (used) {
            throw new NoAvailablePortException("Could not obtain a network port in the range [" 
                + TestingConstants.TEST_PORT_LOWER_BOUND + ", " + TestingConstants.TEST_PORT_UPPER_BOUND + ")");
        }
        return testPort;
    }
}
