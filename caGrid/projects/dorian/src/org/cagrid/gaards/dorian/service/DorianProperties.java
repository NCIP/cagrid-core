package org.cagrid.gaards.dorian.service;

import org.cagrid.gaards.dorian.ca.CertificateAuthority;
import org.cagrid.gaards.dorian.federation.IdentityFederationProperties;
import org.cagrid.gaards.dorian.idp.IdentityProviderProperties;
import org.cagrid.tools.database.Database;
import org.cagrid.tools.events.EventManager;


public class DorianProperties {
    private IdentityProviderProperties identityProviderProperties;
    private IdentityFederationProperties identityFederationProperties;
    private CertificateAuthority certificateAuthority;
    private Database database;
    private EventManager eventManager;


    public DorianProperties(Database db, IdentityProviderProperties identityProviderProperties,
        IdentityFederationProperties identityFederationProperties, CertificateAuthority certificateAuthority,
        EventManager eventManager) {
        this.database = db;
        this.identityFederationProperties = identityFederationProperties;
        this.identityProviderProperties = identityProviderProperties;
        this.certificateAuthority = certificateAuthority;
        this.eventManager = eventManager;
    }


    public EventManager getEventManager() {
        return eventManager;
    }


    public IdentityProviderProperties getIdentityProviderProperties() {
        return identityProviderProperties;
    }


    public IdentityFederationProperties getIdentityFederationProperties() {
        return identityFederationProperties;
    }


    public CertificateAuthority getCertificateAuthority() {
        return certificateAuthority;
    }


    public Database getDatabase() {
        return database;
    }

}
