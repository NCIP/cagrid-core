/**
*============================================================================
*  Copyright The Ohio State University Research Foundation, The University of Chicago - 
*	Argonne National Laboratory, Emory University, SemanticBits LLC, and 
*	Ekagra Software Technologies Ltd.
*
*  Distributed under the OSI-approved BSD 3-Clause License.
*  See http://ncip.github.com/cagrid-core/LICENSE.txt for details.
*============================================================================
**/
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
