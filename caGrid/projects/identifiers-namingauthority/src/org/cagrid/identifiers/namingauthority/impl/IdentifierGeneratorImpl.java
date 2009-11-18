package org.cagrid.identifiers.namingauthority.impl;

import java.net.URI;

import org.cagrid.identifiers.namingauthority.IdentifierGenerator;
import org.cagrid.identifiers.namingauthority.NamingAuthorityConfig;

public class IdentifierGeneratorImpl implements IdentifierGenerator {

	public URI generate(NamingAuthorityConfig config) {
		return URI.create(java.util.UUID.randomUUID().toString());
	}
}
