package org.cagrid.identifiers.namingauthority.impl;

import org.cagrid.identifiers.namingauthority.IdentifierGenerator;
import org.cagrid.identifiers.namingauthority.NamingAuthorityConfig;

public class IdentifierGeneratorImpl implements IdentifierGenerator {

	public String generate(NamingAuthorityConfig config) {
		return java.util.UUID.randomUUID().toString();
	}
}
