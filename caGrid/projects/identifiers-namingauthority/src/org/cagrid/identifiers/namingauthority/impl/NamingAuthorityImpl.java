package org.cagrid.identifiers.namingauthority.impl;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.cagrid.identifiers.namingauthority.IdentifierGenerator;
import org.cagrid.identifiers.namingauthority.InvalidIdentifierException;
import org.cagrid.identifiers.namingauthority.InvalidIdentifierValuesException;
import org.cagrid.identifiers.namingauthority.MaintainerNamingAuthority;
import org.cagrid.identifiers.namingauthority.NamingAuthorityConfig;
import org.cagrid.identifiers.namingauthority.NamingAuthorityConfigurationException;
import org.cagrid.identifiers.namingauthority.NamingAuthoritySecurityException;
import org.cagrid.identifiers.namingauthority.SecurityInfo;
import org.cagrid.identifiers.namingauthority.dao.IdentifierMetadataDao;
import org.cagrid.identifiers.namingauthority.domain.IdentifierValues;
import org.cagrid.identifiers.namingauthority.domain.KeyData;
import org.cagrid.identifiers.namingauthority.util.IdentifierUtil;
import org.cagrid.identifiers.namingauthority.util.Keys;
import org.cagrid.identifiers.namingauthority.util.SecurityUtil;
import org.cagrid.identifiers.namingauthority.util.SecurityUtil.Access;


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

	public URI createIdentifier(SecurityInfo secInfo, IdentifierValues ivalues) 
		throws NamingAuthorityConfigurationException, InvalidIdentifierException, NamingAuthoritySecurityException {
 
		URI identifier = generateIdentifier();
        this.identifierDao.createIdentifier( secInfo, identifier, ivalues );
        return IdentifierUtil.build(getConfiguration().getPrefix(), identifier);
    }

    public IdentifierValues resolveIdentifier(SecurityInfo secInfo, URI identifier) 
    	throws InvalidIdentifierException, NamingAuthorityConfigurationException, NamingAuthoritySecurityException {
  
        URI localIdentifier = IdentifierUtil.getLocalName(getConfiguration().getPrefix(), identifier);
        return this.identifierDao.resolveIdentifier( secInfo, localIdentifier );
    }

	public void createKeys(SecurityInfo secInfo, URI identifier, IdentifierValues newKeys)
		throws NamingAuthorityConfigurationException,
			InvalidIdentifierValuesException, InvalidIdentifierException,
			NamingAuthoritySecurityException {
		
    	URI localIdentifier = IdentifierUtil.getLocalName(getConfiguration().getPrefix(), identifier);
    	this.identifierDao.createKeys(secInfo, localIdentifier, newKeys);
	}

	public void deleteAllKeys(SecurityInfo secInfo, URI identifier)
			throws NamingAuthorityConfigurationException,
			InvalidIdentifierValuesException, InvalidIdentifierException,
			NamingAuthoritySecurityException {
		
		URI localIdentifier = IdentifierUtil.getLocalName(getConfiguration().getPrefix(), identifier);
    	this.identifierDao.deleteAllKeys(secInfo, localIdentifier);
	}

	public void deleteKeys(SecurityInfo secInfo, URI identifier, String[] keyList)
			throws NamingAuthorityConfigurationException,
			InvalidIdentifierValuesException, InvalidIdentifierException,
			NamingAuthoritySecurityException {
		
		URI localIdentifier = IdentifierUtil.getLocalName(getConfiguration().getPrefix(), identifier);
    	this.identifierDao.deleteKeys(secInfo, localIdentifier, keyList);	
	}

	public void replaceKeys(SecurityInfo secInfo, URI identifier,
			IdentifierValues values)
			throws NamingAuthorityConfigurationException,
			InvalidIdentifierValuesException, InvalidIdentifierException,
			NamingAuthoritySecurityException {
		
		URI localIdentifier = IdentifierUtil.getLocalName(getConfiguration().getPrefix(), identifier);
    	this.identifierDao.replaceKeys(secInfo, localIdentifier, values);
	}
	

    //
    // Private
    //
	
	private URI generateIdentifier() {
		return identifierGenerator.generate(getConfiguration());
	}
	

 }

