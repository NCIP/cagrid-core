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
import org.mortbay.log.Log;


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

	public void initialize() throws NamingAuthorityConfigurationException {
		this.identifierDao.initialize(getConfiguration().getPrefix());
	}
	
	public URI createIdentifier(SecurityInfo secInfo, IdentifierValues ivalues) 
		throws 
			NamingAuthorityConfigurationException, 
			InvalidIdentifierException, 
			NamingAuthoritySecurityException {
 
		validateIdentifierValues(ivalues);
		
		URI identifier = generateIdentifier();
        this.identifierDao.createIdentifier( secInfo, identifier, ivalues );
        return IdentifierUtil.build(getConfiguration().getPrefix(), identifier);
    }

    public IdentifierValues resolveIdentifier(SecurityInfo secInfo, URI identifier) 
    	throws 
    		InvalidIdentifierException, 
    		NamingAuthorityConfigurationException, 
    		NamingAuthoritySecurityException {
  
        return this.identifierDao.resolveIdentifier( secInfo, identifier );
    }

	public void createKeys(SecurityInfo secInfo, URI identifier, IdentifierValues newKeys)
		throws 
			NamingAuthorityConfigurationException,
			InvalidIdentifierValuesException, 
			InvalidIdentifierException,
			NamingAuthoritySecurityException {
		
		validateIdentifierValues(newKeys);
    	this.identifierDao.createKeys(secInfo, identifier, newKeys);
	}

	public String[] getKeys(SecurityInfo secInfo, URI identifier)
		throws 
			NamingAuthorityConfigurationException,
			InvalidIdentifierException,
			NamingAuthoritySecurityException {
		
    	return this.identifierDao.getKeys(secInfo, identifier);
	}
	
	public String[] getKeyValues(SecurityInfo secInfo, URI identifier, String key) 
		throws 
			InvalidIdentifierException, 
			NamingAuthoritySecurityException, 
			NamingAuthorityConfigurationException {
		
		return this.identifierDao.getKeyValues(secInfo, identifier, key);
	}

	public void deleteKeys(SecurityInfo secInfo, URI identifier, String[] keyList)
		throws 
			NamingAuthorityConfigurationException,
			InvalidIdentifierValuesException, InvalidIdentifierException,
			NamingAuthoritySecurityException {
		
    	this.identifierDao.deleteKeys(secInfo, identifier, keyList);	
	}

	public void replaceKeys(SecurityInfo secInfo, URI identifier,
			IdentifierValues values)
		throws 
			NamingAuthorityConfigurationException,
			InvalidIdentifierValuesException, 
			InvalidIdentifierException,
			NamingAuthoritySecurityException {

		validateIdentifierValues(values);
    	this.identifierDao.replaceKeys(secInfo, identifier, values);
	}
	
	//
	// Other public
	//
	
	public URI getSystemIdentifier() {
		return URI.create(getConfiguration().getPrefix().normalize().toString() +
				SecurityUtil.LOCAL_SYSTEM_IDENTIFIER.normalize().toString());
	}


    //
    // Private
    //
	
	private URI generateIdentifier() {
		return identifierGenerator.generate(getConfiguration());
	}
	
	private void validateIdentifierValues(IdentifierValues values) 
		throws 
			InvalidIdentifierException, 
			NamingAuthorityConfigurationException {
		
		if (values == null || values.getKeys() == null) {
			return;
		}
		
		for(String key : values.getKeys()) {
			
			KeyData kd = values.getValues(key);
			
			if (kd.getReadWriteIdentifier() != null) {
				// make sure it's local to prefix
				IdentifierUtil.getLocalName(getConfiguration().getPrefix(), 
						kd.getReadWriteIdentifier());
			}
			
			if (key.equals(Keys.ADMIN_IDENTIFIERS) ||
					key.equals(Keys.READWRITE_IDENTIFIERS)) {
				
				List<String> identifiers = kd.getValues();
				if (identifiers != null) {
					for(String identifier : identifiers) {
						// make sure it's local to prefix
						IdentifierUtil.getLocalName(getConfiguration().getPrefix(), 
								URI.create(identifier));
					}
				}
			}
		}
	}
 }

