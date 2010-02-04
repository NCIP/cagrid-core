package org.cagrid.identifiers.namingauthority;

import java.net.URI;
import org.cagrid.identifiers.namingauthority.domain.IdentifierValues;

public interface NamingAuthority {
    public NamingAuthorityConfig getConfiguration();
    public IdentifierValues resolveIdentifier(SecurityInfo secInfo, URI identifier) throws 
        InvalidIdentifierException, NamingAuthorityConfigurationException, NamingAuthoritySecurityException;
}

