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
import org.cagrid.identifiers.namingauthority.domain.IdentifierValues;

public interface MaintainerNamingAuthority extends NamingAuthority {

	public URI createIdentifier(SecurityInfo secInfo, IdentifierData values) 
		throws NamingAuthorityConfigurationException, InvalidIdentifierValuesException, 
			InvalidIdentifierException, NamingAuthoritySecurityException;
	
	public void deleteKeys(SecurityInfo secInfo, URI identifier, String[] keyList)
		throws NamingAuthorityConfigurationException, InvalidIdentifierValuesException, 
			InvalidIdentifierException, NamingAuthoritySecurityException;
  
	public void createKeys(SecurityInfo secInfo, URI identifier, IdentifierData values)
		throws NamingAuthorityConfigurationException, InvalidIdentifierValuesException, 
			InvalidIdentifierException, NamingAuthoritySecurityException;;

	public void replaceKeyValues(SecurityInfo secInfo, URI identifier, IdentifierValues values)
		throws NamingAuthorityConfigurationException, InvalidIdentifierValuesException, 
			InvalidIdentifierException, NamingAuthoritySecurityException;
}
