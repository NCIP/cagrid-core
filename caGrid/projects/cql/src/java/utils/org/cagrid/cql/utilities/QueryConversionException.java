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


public class QueryConversionException extends Exception {

    public QueryConversionException(String message) {
        super(message);
    }


    public QueryConversionException(Exception ex) {
        super(ex);
    }


    public QueryConversionException(String message, Exception ex) {
        super(message, ex);
    }
}
