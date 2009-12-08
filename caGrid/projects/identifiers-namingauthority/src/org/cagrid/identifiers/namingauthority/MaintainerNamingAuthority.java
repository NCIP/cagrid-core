/**
 * 
 */
package org.cagrid.identifiers.namingauthority;

import java.net.URI;

import org.cagrid.identifiers.namingauthority.domain.IdentifierValues;

public abstract class MaintainerNamingAuthority extends NamingAuthority {
	private IdentifierGenerator identifierGenerator;   

	public abstract URI createIdentifier(IdentifierValues values) 
		throws NamingAuthorityConfigurationException, InvalidIdentifierValuesException;

	public URI generateIdentifier() {
		return identifierGenerator.generate(getConfiguration());
	}

	public IdentifierGenerator getIdentifierGenerator() {
		return identifierGenerator;
	}

	public void setIdentifierGenerator(IdentifierGenerator generator) {
		this.identifierGenerator = generator;
	}

}
