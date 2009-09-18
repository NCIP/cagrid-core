package gov.nih.nci.cagrid.testing.system.deployment;

import java.io.IOException;


/**
 * NoAvailablePortException
 * 
 * @author oster
 * @created Mar 29, 2007 11:16:15 AM
 * @version $Id: multiscaleEclipseCodeTemplates.xml,v 1.1 2007/03/02 14:35:01
 *          dervin Exp $
 */
public class NoAvailablePortException extends IOException {

    public NoAvailablePortException() {
        super();
    }


    /**
     * @param message
     */
    public NoAvailablePortException(String message) {
        super(message);

    }

}
