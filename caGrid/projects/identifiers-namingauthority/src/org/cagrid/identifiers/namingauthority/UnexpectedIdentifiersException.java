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
package org.cagrid.identifiers.namingauthority;

public class UnexpectedIdentifiersException extends Exception {

    private static final long serialVersionUID = 1L;


    public UnexpectedIdentifiersException() {
    }


    public UnexpectedIdentifiersException(String message) {
        super(message);
    }


    public UnexpectedIdentifiersException(Throwable cause) {
        super(cause);
    }


    public UnexpectedIdentifiersException(String message, Throwable cause) {
        super(message, cause);
    }

}
