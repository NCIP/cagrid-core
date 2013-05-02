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
package org.cagrid.cadsr.portal;

import gov.nih.nci.cadsr.umlproject.domain.UMLClassMetadata;


public interface ClassSelectedListener {
	public void handleClassSelection(UMLClassMetadata clazz);
}
