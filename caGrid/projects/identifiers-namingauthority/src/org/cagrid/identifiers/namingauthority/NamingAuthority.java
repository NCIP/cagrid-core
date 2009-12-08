package org.cagrid.identifiers.namingauthority;

import java.net.URI;
import java.net.URISyntaxException;

import org.cagrid.identifiers.namingauthority.domain.IdentifierValues;


public abstract class NamingAuthority {

    private NamingAuthorityConfig configuration;
   
    public NamingAuthorityConfig getConfiguration() {
        return this.configuration;
    }

    public void setConfiguration(NamingAuthorityConfig config) {
        this.configuration = config;
    }

    public void initialize() {
    }

    public abstract IdentifierValues resolveIdentifier(URI identifier) throws 
        InvalidIdentifierException, NamingAuthorityConfigurationException;
}
