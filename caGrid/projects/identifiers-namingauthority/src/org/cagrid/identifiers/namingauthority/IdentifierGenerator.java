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
package org.cagrid.identifiers.namingauthority;

import java.net.URI;

import org.cagrid.identifiers.namingauthority.domain.NamingAuthorityConfig;

public interface IdentifierGenerator {
	URI generate( NamingAuthorityConfig config );
}
