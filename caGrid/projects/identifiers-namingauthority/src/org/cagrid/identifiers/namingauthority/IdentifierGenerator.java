package org.cagrid.identifiers.namingauthority;

import java.net.URI;

import org.cagrid.identifiers.namingauthority.domain.NamingAuthorityConfig;

public interface IdentifierGenerator {
	URI generate( NamingAuthorityConfig config );
}
