package org.cagrid.gaards.dorian.service;

import org.cagrid.gaards.dorian.ca.CertificateAuthority;
import org.cagrid.gaards.dorian.ca.CertificateAuthorityProperties;
import org.cagrid.gaards.dorian.federation.IdentityFederationProperties;
import org.cagrid.gaards.dorian.federation.TrustedIdPManager;
import org.cagrid.gaards.dorian.idp.AssertionCredentialsManager;
import org.cagrid.gaards.dorian.idp.IdentityProvider;
import org.cagrid.gaards.dorian.idp.IdentityProviderProperties;
import org.cagrid.gaards.dorian.stubs.types.DorianInternalFault;
import org.cagrid.tools.database.Database;
import org.cagrid.tools.events.EventManager;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.AbstractResource;


public class BeanUtils {

    private XmlBeanFactory factory;


    public BeanUtils(AbstractResource dorianConf, AbstractResource dorianProperties) throws DorianInternalFault {
        this.factory = new XmlBeanFactory(dorianConf);
        PropertyPlaceholderConfigurer cfg = new PropertyPlaceholderConfigurer();
        cfg.setLocation(dorianProperties);
        cfg.postProcessBeanFactory(factory);
    }


    public DorianProperties getDorianProperties() throws Exception {
        DorianProperties props = (DorianProperties) factory.getBean(ConfigurationConstants.DORIAN_PROPERTIES_BEAN);
        return props;
    }


    public IdentityFederationProperties getIdentityFederationProperties() throws Exception {
        IdentityFederationProperties props = (IdentityFederationProperties) factory
            .getBean(ConfigurationConstants.IDENTITY_FEDERATION_PROPERTIES_BEAN);
        return props;
    }


    public IdentityProvider getIdentityProvider() throws Exception {
        return (IdentityProvider) factory.getBean(ConfigurationConstants.IDP_BEAN);
    }


    public EventManager getEventManager() throws Exception {
        return (EventManager) factory.getBean(ConfigurationConstants.EVENT_MANAGER_BEAN);
    }


    public TrustedIdPManager getTrustedIdPManager() throws Exception {
        return (TrustedIdPManager) factory.getBean(ConfigurationConstants.TRUSTED_IDP_MANAGER_BEAN);
    }


    public IdentityProviderProperties getIdentityProviderProperties() throws Exception {
        return (IdentityProviderProperties) factory.getBean(ConfigurationConstants.IDP_PROPERTIES_BEAN);
    }


    public AssertionCredentialsManager getAssertionCredentialsManager() throws Exception {
        return (AssertionCredentialsManager) factory.getBean(ConfigurationConstants.IDP_ASSERTION_MANAGER_BEAN);
    }


    public org.cagrid.gaards.dorian.idp.UserManager getIdPUserManager() throws Exception {
        return (org.cagrid.gaards.dorian.idp.UserManager) factory.getBean(ConfigurationConstants.IDP_USER_MANAGER_BEAN);
    }


    public Database getDatabase() throws Exception {
        return (Database) factory.getBean(ConfigurationConstants.DATABASE_BEAN);
    }


    public CertificateAuthorityProperties getCertificateAuthorityProperties() throws Exception {
        return (CertificateAuthorityProperties) factory.getBean(ConfigurationConstants.CA_PROPERTIES_BEAN);

    }


    public CertificateAuthority getCertificateAuthority() throws Exception {
        return (CertificateAuthority) factory.getBean(ConfigurationConstants.CA_BEAN);
    }

}
