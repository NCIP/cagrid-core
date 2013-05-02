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
package gov.nih.nci.cagrid.advertisement.exceptions;

public class InvalidConfigurationException extends Exception {

    public InvalidConfigurationException() {
        super();
    }


    public InvalidConfigurationException(String message) {
        super(message);
    }


    public InvalidConfigurationException(Throwable cause) {
        super(cause);
    }


    public InvalidConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }

}
