package org.cagrid.identifiers.namingauthority;

import java.net.URI;
import org.cagrid.identifiers.namingauthority.domain.IdentifierValues;

public interface NamingAuthority {
    NamingAuthorityConfig getConfiguration();
    void initialize();
    IdentifierValues resolveIdentifier(URI identifier) throws 
        InvalidIdentifierException, NamingAuthorityConfigurationException;
}
