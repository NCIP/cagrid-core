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
package gov.nih.nci.cagrid.fqp.processor;

public class TransformationHandlingException extends Exception {

    public TransformationHandlingException(String message) {
        super(message);
    }
    
    
    public TransformationHandlingException(Throwable cause) {
        super(cause);
    }
    
    
    public TransformationHandlingException(String message, Throwable cause) {
        super(message, cause);
    }
}
