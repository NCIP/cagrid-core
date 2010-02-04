package org.cagrid.identifiers.namingauthority;

import java.net.URI;

import org.cagrid.identifiers.namingauthority.domain.IdentifierValues;

public interface MaintainerNamingAuthority extends NamingAuthority {
	public URI createIdentifier(IdentifierValues values) 
		throws NamingAuthorityConfigurationException, InvalidIdentifierValuesException;
}
