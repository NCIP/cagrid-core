package org.cagrid.identifiers.namingauthority;

import java.net.URI;
import org.cagrid.identifiers.namingauthority.domain.IdentifierData;
import org.cagrid.identifiers.namingauthority.domain.KeyData;
import org.cagrid.identifiers.namingauthority.domain.NamingAuthorityConfig;

public interface NamingAuthority {
	
	//
	// Makes naming authority configuration available
	//
    public NamingAuthorityConfig getConfiguration();
    
    //
    // Runs any needed initialization code in the naming authority
    //
    public void initialize() throws NamingAuthorityConfigurationException;
    
    //
    // Returns only keys associated with a given identifier
    //
	public String[] getKeyNames(SecurityInfo secInfo, URI identifier) 
		throws 
			NamingAuthorityConfigurationException, 
			InvalidIdentifierException, 
			NamingAuthoritySecurityException;
	
	//
	// Returns values associated with a given key
	//
	public KeyData getKeyData(SecurityInfo secInfo, URI identifier, String key) 
		throws 
			InvalidIdentifierException, 
			NamingAuthoritySecurityException, 
			NamingAuthorityConfigurationException, 
			InvalidIdentifierValuesException;
	
	//
	// Returns all metadata (keys/values) associated with a given identifier
	//
    public IdentifierData resolveIdentifier(SecurityInfo secInfo, URI identifier) 
    	throws 
        	InvalidIdentifierException, 
        	NamingAuthorityConfigurationException, 
        	NamingAuthoritySecurityException;
}

