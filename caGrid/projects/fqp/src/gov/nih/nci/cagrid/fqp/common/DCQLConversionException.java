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
package gov.nih.nci.cagrid.fqp.common;

public class DCQLConversionException extends Exception {

    public DCQLConversionException(String message) {
        super(message);
    }
    
    
    public DCQLConversionException(Throwable cause) {
        super(cause);
    }
    
    
    public DCQLConversionException(String message, Throwable cause) {
        super(message, cause);
    }
}
