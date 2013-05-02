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
package org.cagrid.cql.utilities;

public class ResultsConversionException extends Exception {

    public ResultsConversionException(String message) {
        super(message);
    }
    
    
    public ResultsConversionException(Exception cause) {
        super(cause);
    }
    
    
    public ResultsConversionException(String message, Exception cause) {
        super(message, cause);
    }
}
