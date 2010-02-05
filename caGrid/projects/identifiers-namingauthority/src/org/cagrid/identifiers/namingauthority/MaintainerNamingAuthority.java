package org.cagrid.identifiers.namingauthority;

import java.net.URI;
import org.cagrid.identifiers.namingauthority.domain.IdentifierValues;

public interface MaintainerNamingAuthority extends NamingAuthority {

	public URI createIdentifier(SecurityInfo secInfo, IdentifierValues values) 
		throws NamingAuthorityConfigurationException, InvalidIdentifierValuesException, 
			InvalidIdentifierException, NamingAuthoritySecurityException;
	
	public void deleteKeys(SecurityInfo secInfo, URI identifier, String[] keyList)
		throws NamingAuthorityConfigurationException, InvalidIdentifierValuesException, 
			InvalidIdentifierException, NamingAuthoritySecurityException;
  
	public void createKeys(SecurityInfo secInfo, URI identifier, IdentifierValues values)
		throws NamingAuthorityConfigurationException, InvalidIdentifierValuesException, 
			InvalidIdentifierException, NamingAuthoritySecurityException;;

	public void replaceKeys(SecurityInfo secInfo, URI identifier, IdentifierValues values)
		throws NamingAuthorityConfigurationException, InvalidIdentifierValuesException, 
			InvalidIdentifierException, NamingAuthoritySecurityException;;

	public void deleteAllKeys(SecurityInfo secInfo, URI identifier)
		throws NamingAuthorityConfigurationException, InvalidIdentifierValuesException, 
			InvalidIdentifierException, NamingAuthoritySecurityException;
}
