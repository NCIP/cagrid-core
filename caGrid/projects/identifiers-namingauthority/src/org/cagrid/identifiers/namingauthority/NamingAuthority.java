package org.cagrid.identifiers.namingauthority;

import java.net.URI;
import org.cagrid.identifiers.namingauthority.domain.IdentifierValues;

public interface NamingAuthority {
    public NamingAuthorityConfig getConfiguration();
    public void initialize();
    public abstract IdentifierValues resolveIdentifier(URI identifier) throws 
        InvalidIdentifierException, NamingAuthorityConfigurationException;
}
