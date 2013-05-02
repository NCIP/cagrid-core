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
package org.cagrid.identifiers.namingauthority.impl;

import java.net.URI;

import org.cagrid.identifiers.namingauthority.IdentifierGenerator;
import org.cagrid.identifiers.namingauthority.domain.NamingAuthorityConfig;

public class IdentifierGeneratorImpl implements IdentifierGenerator {

	public URI generate(NamingAuthorityConfig config) {
		return URI.create(java.util.UUID.randomUUID().toString());
	}
}
