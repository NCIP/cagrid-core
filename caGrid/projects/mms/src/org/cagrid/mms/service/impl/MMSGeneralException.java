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
package org.cagrid.mms.service.impl;

@SuppressWarnings("serial")
public class MMSGeneralException extends Exception {

    public MMSGeneralException() {
    }


    public MMSGeneralException(String message) {
        super(message);
    }


    public MMSGeneralException(Throwable cause) {
        super(cause);
    }


    public MMSGeneralException(String message, Throwable cause) {
        super(message, cause);
    }

}
