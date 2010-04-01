package org.cagrid.identifiers.namingauthority.impl;

import java.net.URI;
import java.util.List;

import org.cagrid.identifiers.namingauthority.IdentifierGenerator;
import org.cagrid.identifiers.namingauthority.InvalidIdentifierException;
import org.cagrid.identifiers.namingauthority.InvalidIdentifierValuesException;
import org.cagrid.identifiers.namingauthority.MaintainerNamingAuthority;
import org.cagrid.identifiers.namingauthority.domain.NamingAuthorityConfig;
import org.cagrid.identifiers.namingauthority.NamingAuthorityConfigurationException;
import org.cagrid.identifiers.namingauthority.NamingAuthoritySecurityException;
import org.cagrid.identifiers.namingauthority.SecurityInfo;
import org.cagrid.identifiers.namingauthority.dao.IdentifierMetadataDao;
import org.cagrid.identifiers.namingauthority.domain.IdentifierData;
import org.cagrid.identifiers.namingauthority.domain.IdentifierValues;
import org.cagrid.identifiers.namingauthority.domain.KeyData;
import org.cagrid.identifiers.namingauthority.domain.KeyValues;
import org.cagrid.identifiers.namingauthority.util.IdentifierUtil;
import org.cagrid.identifiers.namingauthority.util.Keys;
import org.cagrid.identifiers.namingauthority.util.SecurityUtil;


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
		this.identifierDao.initialize(configuration.getNaPrefixURI());
	}
	
	public URI createIdentifier(SecurityInfo secInfo, IdentifierData ivalues) 
		throws 
			NamingAuthorityConfigurationException, 
			InvalidIdentifierException, 
			NamingAuthoritySecurityException, 
			InvalidIdentifierValuesException {
 
		validateIdentifierValues(ivalues);
		
		URI identifier = generateIdentifier();
        this.identifierDao.createIdentifier( secInfo, identifier, ivalues );
        return IdentifierUtil.build(configuration.getNaPrefixURI(), identifier);
    }

    public IdentifierData resolveIdentifier(SecurityInfo secInfo, URI identifier) 
    	throws 
    		InvalidIdentifierException, 
    		NamingAuthorityConfigurationException, 
    		NamingAuthoritySecurityException {
  
        return this.identifierDao.resolveIdentifier( secInfo, identifier );
    }

	public void createKeys(SecurityInfo secInfo, URI identifier, IdentifierData newKeys)
		throws 
			NamingAuthorityConfigurationException,
			InvalidIdentifierValuesException, 
			InvalidIdentifierException,
			NamingAuthoritySecurityException {
		
		validateIdentifierValues(newKeys);
    	this.identifierDao.createKeys(secInfo, identifier, newKeys);
	}

	public String[] getKeyNames(SecurityInfo secInfo, URI identifier)
		throws 
			NamingAuthorityConfigurationException,
			InvalidIdentifierException,
			NamingAuthoritySecurityException {
		
    	return this.identifierDao.getKeyNames(secInfo, identifier);
	}
	
	public KeyData getKeyData(SecurityInfo secInfo, URI identifier, String key) 
		throws 
			InvalidIdentifierException, 
			NamingAuthoritySecurityException, 
			NamingAuthorityConfigurationException, 
			InvalidIdentifierValuesException {
		
		return this.identifierDao.getKeyData(secInfo, identifier, key);
	}

	public void deleteKeys(SecurityInfo secInfo, URI identifier, String[] keyList)
		throws 
			NamingAuthorityConfigurationException,
			InvalidIdentifierValuesException, InvalidIdentifierException,
			NamingAuthoritySecurityException {
		
    	this.identifierDao.deleteKeys(secInfo, identifier, keyList);	
	}

	public void replaceKeyValues(SecurityInfo secInfo, URI identifier,
			IdentifierValues values)
		throws 
			NamingAuthorityConfigurationException,
			InvalidIdentifierValuesException, 
			InvalidIdentifierException,
			NamingAuthoritySecurityException {

		validateKeyValues(values);
    	this.identifierDao.replaceKeyValues(secInfo, identifier, values);
	}
	
	//
	// Other public
	//
	
	public URI getSystemIdentifier() {
		return URI.create(configuration.getNaPrefixURI().normalize().toString() +
				SecurityUtil.LOCAL_SYSTEM_IDENTIFIER.normalize().toString());
	}


    //
    // Private
    //
	
	private URI generateIdentifier() {
		return identifierGenerator.generate(getConfiguration());
	}
	
	private void validateKeyValues(IdentifierValues keyValues) 
		throws 
			NamingAuthorityConfigurationException, 
			InvalidIdentifierValuesException {
		
		if (keyValues == null || keyValues.getValues() == null) {
			return;
		}
		
		for(String key : keyValues.getKeys()) {
			validateKeyValues(key, keyValues.getValues(key));
		}
	}
	
	private void validateKeyValues(String key, KeyValues keyValues)
		throws 
			NamingAuthorityConfigurationException, 
			InvalidIdentifierValuesException {

		if (keyValues == null) {
			return;
		}

		List<String> values = keyValues.getValues();

		if (key == null || key.equals("")) {
			throw new InvalidIdentifierValuesException("Key names are required");
		}

		if (values == null) {
			return;
		}

		if (key.equals(Keys.ADMIN_IDENTIFIERS) ||
				key.equals(Keys.READWRITE_IDENTIFIERS)) {

			for(String identifier : values) {
				// make sure it's local to prefix
				try {
					IdentifierUtil.getLocalName(configuration.getNaPrefixURI(), 
							URI.create(identifier));
				} catch (InvalidIdentifierException e) {
					throw new InvalidIdentifierValuesException(e.getMessage());
				}
			}
		}
	}
	
	private void validateIdentifierValues(IdentifierData values) 
		throws 
			NamingAuthorityConfigurationException, 
			InvalidIdentifierValuesException {
		
		if (values == null || values.getKeys() == null) {
			return;
		}
		
		for(String key : values.getKeys()) {
			
			if (key == null || key.equals("")) {
				throw new InvalidIdentifierValuesException("Key names are required");
			}
			
			KeyData kd = values.getValues(key);
			if (kd == null)
				continue;
			
			if (kd.getPolicyIdentifier() != null) {
				// make sure it's local to prefix
				try {
					IdentifierUtil.getLocalName(configuration.getNaPrefixURI(), 
							kd.getPolicyIdentifier());
				} catch (InvalidIdentifierException e) {
					throw new InvalidIdentifierValuesException(e.getMessage());
				}
			}
			
			validateKeyValues(key, kd);
		}
	}
 }

