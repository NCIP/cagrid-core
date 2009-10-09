package org.cagrid.identifiers.namingauthority;

public abstract class NamingAuthority {
	
	private NamingAuthorityConfig configuration;
	private IdentifierGenerator identifierGenerator;
	private HttpProcessor httpProcessor;
	
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
	
	public HttpProcessor getHttpProcessor() {
		return this.httpProcessor;
	}
	
	public void setHttpProcessor( HttpProcessor processor ) {
		this.httpProcessor = processor;
		this.httpProcessor.setNamingAuthority(this);
	}
	
	public void initialize(){};
	
	public abstract IdentifierValues resolveIdentifier(Object identifier);
	
	public abstract Object createIdentifier(IdentifierValues values) throws Exception;
}
