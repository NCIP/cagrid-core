/**
*============================================================================
*  Copyright The Ohio State University Research Foundation, The University of Chicago - 
*	Argonne National Laboratory, Emory University, SemanticBits LLC, and 
*	Ekagra Software Technologies Ltd.
*
*  Distributed under the OSI-approved BSD 3-Clause License.
*  See http://ncip.github.com/cagrid-core/LICENSE.txt for details.
*============================================================================
**/
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

