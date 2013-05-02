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
package org.cagrid.iso21090.sdkquery.translator;

/**
 * QueryTranslationException
 * Thrown when an error occurs during query translation
 * 
 * @author David
 */
public class QueryTranslationException extends Exception {

    public QueryTranslationException(String message) {
        super(message);
    }
    
    
    public QueryTranslationException(Exception cause) {
        super(cause);
    }
    
    
    public QueryTranslationException(String message, Exception cause) {
        super(message, cause);
    }
}
