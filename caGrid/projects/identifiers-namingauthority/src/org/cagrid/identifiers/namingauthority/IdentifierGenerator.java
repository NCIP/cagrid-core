package org.cagrid.identifiers.namingauthority;

import java.net.URI;

public interface IdentifierGenerator {
	URI generate( NamingAuthorityConfig config );
}
