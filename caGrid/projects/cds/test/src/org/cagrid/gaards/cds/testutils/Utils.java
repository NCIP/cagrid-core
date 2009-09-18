package org.cagrid.gaards.cds.testutils;

import org.cagrid.gaards.cds.common.DelegatedCredentialAuditFilter;
import org.cagrid.gaards.cds.common.DelegatedCredentialEvent;
import org.cagrid.gaards.cds.common.DelegationIdentifier;
import org.cagrid.gaards.cds.service.ConfigurationConstants;
import org.cagrid.gaards.cds.service.DelegatedCredentialManager;
import org.cagrid.gaards.cds.service.DelegationManager;
import org.cagrid.gaards.cds.service.KeyManager;
import org.cagrid.gaards.cds.service.PropertyManager;
import org.cagrid.gaards.cds.service.policy.IdentityPolicyHandler;
import org.cagrid.tools.database.Database;
import org.cagrid.tools.events.EventManager;
import org.cagrid.tools.groups.GroupManager;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;

public class Utils {

	public static XmlBeanFactory loadConfiguration() throws Exception {

		ClassPathResource cpr = new ClassPathResource(
				Constants.CDS_CONFIGURATION);
		XmlBeanFactory factory = new XmlBeanFactory(cpr);
		PropertyPlaceholderConfigurer cfg = new PropertyPlaceholderConfigurer();
		cfg.setLocation(new ClassPathResource(Constants.CDS_PROPERTIES));
		cfg.postProcessBeanFactory(factory);
		return factory;
	}

	public static DelegationManager getCDS() throws Exception {
		XmlBeanFactory factory = loadConfiguration();
		return (DelegationManager) factory
				.getBean(ConfigurationConstants.CDS_BEAN);
	}
	
	public static GroupManager getGroupManager() throws Exception {
		XmlBeanFactory factory = loadConfiguration();
		return (GroupManager) factory
				.getBean(ConfigurationConstants.GROUP_MANAGER_BEAN);
	}

	public static DelegatedCredentialManager getDelegatedCredentialManager()
			throws Exception {
		XmlBeanFactory factory = loadConfiguration();
		return (DelegatedCredentialManager) factory
				.getBean(ConfigurationConstants.DELEGATED_CREDENTIAL_MANAGER_CONFIGURATION_BEAN);
	}

	public static KeyManager getKeyManager() throws Exception {
		XmlBeanFactory factory = loadConfiguration();
		return (KeyManager) factory
				.getBean(ConfigurationConstants.KEY_MANAGER_CONFIGURATION_BEAN);
	}

	public static IdentityPolicyHandler getIdentityPolicyHandler()
			throws Exception {
		XmlBeanFactory factory = loadConfiguration();
		return (IdentityPolicyHandler) factory
				.getBean(ConfigurationConstants.IDENTITY_POLICY_HANDLER);
	}

	public static Database getDatabase() throws Exception {
		XmlBeanFactory factory = loadConfiguration();
		return (Database) factory
				.getBean(ConfigurationConstants.DATABASE_CONFIGURATION_BEAN);
	}

	public static PropertyManager getPropertyManager() throws Exception {
		XmlBeanFactory factory = loadConfiguration();
		return (PropertyManager) factory
				.getBean(ConfigurationConstants.PROPERTY_MANAGER_CONFIGURATION_BEAN);
	}

	public static EventManager getEventManager() throws Exception {
		XmlBeanFactory factory = loadConfiguration();
		return (EventManager) factory
				.getBean(ConfigurationConstants.EVENT_MANAGER);
	}

	public static DelegatedCredentialAuditFilter getInitiatedAuditFilter() {
		return getInitiatedAuditFilter(null);
	}

	public static DelegatedCredentialAuditFilter getInitiatedAuditFilter(
			DelegationIdentifier id) {
		DelegatedCredentialAuditFilter f = new DelegatedCredentialAuditFilter();
		f.setEvent(DelegatedCredentialEvent.DelegationInitiated);
		f.setDelegationIdentifier(id);
		return f;
	}
	
	public static DelegatedCredentialAuditFilter getApprovedAuditFilter() {
		return getApprovedAuditFilter(null);
	}

	public static DelegatedCredentialAuditFilter getApprovedAuditFilter(
			DelegationIdentifier id) {
		DelegatedCredentialAuditFilter f = new DelegatedCredentialAuditFilter();
		f.setEvent(DelegatedCredentialEvent.DelegationApproved);
		f.setDelegationIdentifier(id);
		return f;
	}
	
	public static DelegatedCredentialAuditFilter getIssuedAuditFilter() {
		return getIssuedAuditFilter(null);
	}

	public static DelegatedCredentialAuditFilter getIssuedAuditFilter(
			DelegationIdentifier id) {
		DelegatedCredentialAuditFilter f = new DelegatedCredentialAuditFilter();
		f.setEvent(DelegatedCredentialEvent.DelegatedCredentialIssued);
		f.setDelegationIdentifier(id);
		return f;
	}
	
	public static DelegatedCredentialAuditFilter getAccessDeniedAuditFilter() {
		return getAccessDeniedAuditFilter(null);
	}

	public static DelegatedCredentialAuditFilter getAccessDeniedAuditFilter(
			DelegationIdentifier id) {
		DelegatedCredentialAuditFilter f = new DelegatedCredentialAuditFilter();
		f.setEvent(DelegatedCredentialEvent.DelegatedCredentialAccessDenied);
		f.setDelegationIdentifier(id);
		return f;
	}
	
	public static DelegatedCredentialAuditFilter getUpdateStatusdAuditFilter() {
		return getUpdateStatusAuditFilter(null);
	}

	public static DelegatedCredentialAuditFilter getUpdateStatusAuditFilter(
			DelegationIdentifier id) {
		DelegatedCredentialAuditFilter f = new DelegatedCredentialAuditFilter();
		f.setEvent(DelegatedCredentialEvent.DelegationStatusUpdated);
		f.setDelegationIdentifier(id);
		return f;
	}
}
