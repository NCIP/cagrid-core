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
package org.cagrid.identifiers.retriever;

import org.cagrid.identifiers.namingauthority.domain.IdentifierData;

public interface Retriever {
	public String[] getRequiredKeys();
	public Object retrieve(IdentifierData ivs) throws Exception;
}
