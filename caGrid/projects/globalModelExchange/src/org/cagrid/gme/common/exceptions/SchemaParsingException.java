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
package org.cagrid.gme.common.exceptions;

import java.io.IOException;


@SuppressWarnings("serial")
public class SchemaParsingException extends IOException {

    public SchemaParsingException() {
        super();
    }


    public SchemaParsingException(String s) {
        super(s);
    }

}
