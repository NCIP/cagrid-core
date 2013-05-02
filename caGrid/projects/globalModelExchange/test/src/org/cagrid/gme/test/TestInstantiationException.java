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
package org.cagrid.gme.test;

public class TestInstantiationException extends Exception {

    public TestInstantiationException() {
    }


    public TestInstantiationException(String message) {
        super(message);
    }


    public TestInstantiationException(Throwable cause) {
        super(cause);
    }


    public TestInstantiationException(String message, Throwable cause) {
        super(message, cause);
    }

}
