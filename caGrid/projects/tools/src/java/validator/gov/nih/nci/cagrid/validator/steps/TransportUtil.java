package gov.nih.nci.cagrid.validator.steps;

import org.apache.axis.client.Call;

/** 
 *  TransportUtil
 *  The Axis org.apache.axis.client.Call class changes the packages which Java
 *  searches for transport handlers when making URL connections to give priority
 *  to Globus handlers.  Once that's been done, subsequent use of URLConnections
 *  will go through Globus-specific implementations, which breaks any special 
 *  connection type handling
 * 
 * @author David Ervin
 * 
 * @created Mar 31, 2008 2:50:44 PM
 * @version $Id: TransportUtil.java,v 1.1 2008-03-31 20:04:23 dervin Exp $ 
 */
public class TransportUtil {
    private static String DEFAULT_TRANSPORTS = null;
    
    static {
        DEFAULT_TRANSPORTS = System.getProperty(Call.TRANSPORT_PROPERTY, "");
    }

    public static void resetUrlTransports() {
        System.setProperty(Call.TRANSPORT_PROPERTY, DEFAULT_TRANSPORTS);
    }
}
