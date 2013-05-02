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
package gov.nih.nci.cagrid.data.cql.validation;

import gov.nih.nci.cagrid.data.MalformedQueryException;

public class MalformedStructureException extends MalformedQueryException implements StructureValidationError {

    public MalformedStructureException(String message) {
        super(message);
    }
    
    
    public MalformedStructureException(Exception ex) {
        super(ex);
    }
    
    
    public MalformedStructureException(String message, Exception ex) {
        super(message, ex);
    }
}
