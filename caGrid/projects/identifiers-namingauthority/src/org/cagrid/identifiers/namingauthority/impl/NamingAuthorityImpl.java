package org.cagrid.identifiers.namingauthority.impl;

import java.net.URI;

import org.cagrid.identifiers.namingauthority.IdentifierGenerator;
import org.cagrid.identifiers.namingauthority.InvalidIdentifierException;
import org.cagrid.identifiers.namingauthority.InvalidIdentifierValuesException;
import org.cagrid.identifiers.namingauthority.MaintainerNamingAuthority;
import org.cagrid.identifiers.namingauthority.NamingAuthorityConfig;
import org.cagrid.identifiers.namingauthority.NamingAuthorityConfigurationException;
import org.cagrid.identifiers.namingauthority.dao.IdentifierMetadataDao;
import org.cagrid.identifiers.namingauthority.domain.IdentifierValues;
import org.cagrid.identifiers.namingauthority.util.IdentifierUtil;


public class NamingAuthorityImpl implements MaintainerNamingAuthority {

    private IdentifierMetadataDao identifierDao = null;
	private IdentifierGenerator identifierGenerator = null;   
	private NamingAuthorityConfig configuration = null;
	
	//
	// Getters/Setters
	//

    public void setIdentifierDao(IdentifierMetadataDao identifierDao) {
        this.identifierDao = identifierDao;
    }

    public IdentifierMetadataDao getIdentifierDao() {
        return identifierDao;
    }

    public void setConfiguration(NamingAuthorityConfig config) {
    	this.configuration = config;
    }
    
	public NamingAuthorityConfig getConfiguration() {
		return this.configuration;
	}

	public void setIdentifierGenerator(IdentifierGenerator generator) {
		this.identifierGenerator = generator;
	}
	
	public IdentifierGenerator getIdentifierGenerator() {
		return identifierGenerator;
	}

	//
	// Interfaces
	//

    public void initialize() { // nothing to initialize 
    }

    public URI createIdentifier(IdentifierValues ivalues) throws NamingAuthorityConfigurationException {

        URI identifier = generateIdentifier();
        
        this.identifierDao.saveIdentifierValues( identifier, ivalues );

        return IdentifierUtil.build(getConfiguration().getPrefix(), identifier);
    }

    public IdentifierValues resolveIdentifier(URI identifier) throws InvalidIdentifierException, NamingAuthorityConfigurationException {
  
        URI localIdentifier = IdentifierUtil.getLocalName(getConfiguration().getPrefix(), identifier);

        IdentifierValues result = null;
        try {
        	result = this.identifierDao.getIdentifierValues( localIdentifier );
        } catch(InvalidIdentifierException e) {
        	throw new InvalidIdentifierException("The specified identifier (" + identifier + ") was not found.");
        }

        return result;
    }

    //
    // Private
    //
	private URI generateIdentifier() {
		return identifierGenerator.generate(getConfiguration());
	}
}
