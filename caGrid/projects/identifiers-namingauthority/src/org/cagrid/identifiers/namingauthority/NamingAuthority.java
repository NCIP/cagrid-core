package org.cagrid.identifiers.namingauthority;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class NamingAuthority {
	
	private NamingAuthorityConfig configuration;
	private IdentifierGenerator identifierGenerator;
	
	public NamingAuthorityConfig getConfiguration() { 
		return this.configuration; 
	}
	
	public void setConfiguration( NamingAuthorityConfig config ) {
		this.configuration = config;
	}
	
	public String generateIdentifier() { 
		return identifierGenerator.generate(configuration);
	}
	
	public IdentifierGenerator getIdentifierGenerator() {
		return identifierGenerator;
	}
	
	public void setIdentifierGenerator( IdentifierGenerator generator ) {
		this.identifierGenerator = generator;
	}
	
	public abstract void initialize();
	
	public abstract void processHttpRequest(HttpServletRequest request, HttpServletResponse response) throws IOException;

	public abstract IdentifierValues resolveIdentifier(Object identifier);
	
	public abstract Object createIdentifier(IdentifierValues values) throws Exception;
}
