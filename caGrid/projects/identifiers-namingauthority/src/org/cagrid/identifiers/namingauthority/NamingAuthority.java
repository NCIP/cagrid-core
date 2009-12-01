package org.cagrid.identifiers.namingauthority;

import java.net.URI;
import java.net.URISyntaxException;

import org.cagrid.identifiers.namingauthority.domain.IdentifierValues;


public abstract class NamingAuthority {

    private NamingAuthorityConfig configuration;
    private IdentifierGenerator identifierGenerator;


    public NamingAuthorityConfig getConfiguration() {
        return this.configuration;
    }


    public void setConfiguration(NamingAuthorityConfig config) {
        this.configuration = config;
    }


    public URI generateIdentifier() {
        return identifierGenerator.generate(configuration);
    }


    public IdentifierGenerator getIdentifierGenerator() {
        return identifierGenerator;
    }


    public void setIdentifierGenerator(IdentifierGenerator generator) {
        this.identifierGenerator = generator;
    }


    public void initialize() {
    }


    public abstract IdentifierValues resolveIdentifier(URI identifier) throws URISyntaxException,
        InvalidIdentifierException, NamingAuthorityConfigurationException;


    public abstract URI createIdentifier(IdentifierValues values) throws Exception,
        NamingAuthorityConfigurationException, InvalidIdentifierValuesException;
}
