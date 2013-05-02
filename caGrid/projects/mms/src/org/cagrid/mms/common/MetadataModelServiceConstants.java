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
package org.cagrid.mms.common;

import javax.xml.namespace.QName;


/**
 * Constants class that extends the introduce managed constants. Developers can
 * add constants to this file.
 * 
 * @created by Introduce Toolkit version 1.3
 */
public interface MetadataModelServiceConstants extends MetadataModelServiceConstantsBase {
    // This is used to store UMLProjectIdentifier's in extension data
    public static final String UML_PROJECT_IDENTIFIER_EXTENSION_NAME = "UmlProjectIdentifier";
    // This version should be changed if the UMLProjectIdentifier model changes
    public static final String UML_PROJECT_IDENTIFIER_EXTENSION_VERSION = "1.0";

}
