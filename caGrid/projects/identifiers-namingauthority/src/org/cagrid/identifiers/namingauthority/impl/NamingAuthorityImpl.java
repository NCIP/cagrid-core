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
	private SecurityHelper securityHelper = null;
	
	public NamingAuthorityImpl() {
		this.securityHelper = new SecurityHelper(this);
	}
	
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
	
	public void setSecurityHelper(SecurityHelper helper) {
		this.securityHelper = helper;
	}
	


	//
	// Interfaces
	//

	public URI createIdentifier(SecurityInfo secInfo, IdentifierValues ivalues) throws NamingAuthorityConfigurationException, InvalidIdentifierException, NamingAuthoritySecurityException {
    	
		SecurityInfo securityInfo = checkSecurityInfo(secInfo);
		
    	securityHelper.checkCreateIdentifierSecurity(securityInfo);

        URI identifier = generateIdentifier();
        
        this.identifierDao.saveIdentifierValues( identifier, ivalues );

        return IdentifierUtil.build(getConfiguration().getPrefix(), identifier);
    }

    public IdentifierValues resolveIdentifier(SecurityInfo secInfo, URI identifier) 
    	throws InvalidIdentifierException, NamingAuthorityConfigurationException, NamingAuthoritySecurityException {
  
    	SecurityInfo securityInfo = checkSecurityInfo(secInfo);
   
    	IdentifierValues values = loadIdentifier(identifier);
       
        IdentifierValues result = null;
		try {
			result = securityHelper.checkSecurity(securityInfo, values);
		} catch (Exception e) {
			e.printStackTrace();
			throw new NamingAuthoritySecurityException(e.getMessage() 
					+ " " + IdentifierUtil.getStackTrace(e));
		} 

        return result;
    }

	public void createKeys(SecurityInfo secInfo, URI identifier, IdentifierValues values)
		throws NamingAuthorityConfigurationException,
			InvalidIdentifierValuesException, InvalidIdentifierException,
			NamingAuthoritySecurityException {
		
    	//SecurityInfo securityInfo = checkSecurityInfo(secInfo);
    	URI localIdentifier = IdentifierUtil.getLocalName(getConfiguration().getPrefix(), identifier);
    	this.identifierDao.createKeys(localIdentifier, values);
        //IdentifierValues resolvedValues = loadIdentifier(identifier);
	}

	public void deleteAllKeys(SecurityInfo secInfo, URI identifier)
			throws NamingAuthorityConfigurationException,
			InvalidIdentifierValuesException, InvalidIdentifierException,
			NamingAuthoritySecurityException {
		// TODO Auto-generated method stub
		
	}

	public void deleteKeys(SecurityInfo secInfo, URI identifier, String[] keyList)
			throws NamingAuthorityConfigurationException,
			InvalidIdentifierValuesException, InvalidIdentifierException,
			NamingAuthoritySecurityException {
		// TODO Auto-generated method stub
		
	}

	public void replaceKeys(SecurityInfo secInfo, URI identifier,
			IdentifierValues values)
			throws NamingAuthorityConfigurationException,
			InvalidIdentifierValuesException, InvalidIdentifierException,
			NamingAuthoritySecurityException {
		// TODO Auto-generated method stub
		
	}
	
	//
	// Other public methods
	//
	
	public IdentifierValues resolveLocalIdentifier(URI localIdentifier) throws InvalidIdentifierException {
		return this.identifierDao.getIdentifierValues( localIdentifier );
	}
	
    //
    // Private
    //
	
	private IdentifierValues loadIdentifier(URI identifier) throws InvalidIdentifierException, NamingAuthorityConfigurationException {
	
		URI localIdentifier = IdentifierUtil.getLocalName(getConfiguration().getPrefix(), identifier);
	
		try {
			return resolveLocalIdentifier( localIdentifier );
		} catch(InvalidIdentifierException e) {
			throw new InvalidIdentifierException("Identifier [" + identifier + "] does not exist");
		}
	}
	
	private URI generateIdentifier() {
		return identifierGenerator.generate(getConfiguration());
	}
	
	private SecurityInfo checkSecurityInfo( SecurityInfo secInfo ) {
		if (secInfo == null || secInfo.getUser() == null) {
			return new SecurityInfoImpl("");
		}
		
		return secInfo;
	}
 }

