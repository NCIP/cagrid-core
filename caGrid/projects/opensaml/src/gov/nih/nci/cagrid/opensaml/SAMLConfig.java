/*
 *  Copyright 2001-2005 Internet2
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package gov.nih.nci.cagrid.opensaml;

import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Properties;

import javax.xml.namespace.QName;
import javax.xml.parsers.FactoryConfigurationError;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;

/**
 *  OpenSAML configuration bundle.  Implemented as a singleton.  
 * 
 * @author     Walter Hoehn (wassa@columbia.edu)
 */
public class SAMLConfig {

	private static SAMLConfig instance;
    private Logger log = Logger.getLogger(SAMLConfig.class.getName());

    protected Properties properties;
    private SAMLIdentifier IDProvider = null;
    private Hashtable bindingMap = new Hashtable();

	protected SAMLConfig() {

		verifyUsableXmlParser();

		properties = new Properties();
		try {
			loadProperties(this.getClass().getResourceAsStream("/conf/cagrid.opensaml.properties"));
		} catch (IOException e) {
			log.warn("Unable to load default library properties.");
		}

		org.apache.xml.security.Init.init();

		SAMLCondition.conditionTypeMap.put(
			new QName(XML.SAML_NS, "AudienceRestrictionCondition"),
			"gov.nih.nci.cagrid.opensaml.SAMLAudienceRestrictionCondition");
        SAMLCondition.conditionTypeMap.put(
            new QName(XML.SAML_NS, "AudienceRestrictionConditionType"),
            "gov.nih.nci.cagrid.opensaml.SAMLAudienceRestrictionCondition");
        SAMLCondition.conditionTypeMap.put(
            new QName(XML.SAML_NS, "DoNotCacheCondition"),
            "gov.nih.nci.cagrid.opensaml.SAMLDoNotCacheCondition");
        SAMLCondition.conditionTypeMap.put(
            new QName(XML.SAML_NS, "DoNotCacheConditionType"),
            "gov.nih.nci.cagrid.opensaml.SAMLDoNotCacheCondition");

		SAMLQuery.queryTypeMap.put(
            new QName(XML.SAMLP_NS, "AttributeQuery"),
            "gov.nih.nci.cagrid.opensaml.SAMLAttributeQuery");
		SAMLQuery.queryTypeMap.put(
            new QName(XML.SAMLP_NS, "AttributeQueryType"),
            "gov.nih.nci.cagrid.opensaml.SAMLAttributeQuery");
		SAMLQuery.queryTypeMap.put(
			new QName(XML.SAMLP_NS, "AuthenticationQuery"),
			"gov.nih.nci.cagrid.opensaml.SAMLAuthenticationQuery");
		SAMLQuery.queryTypeMap.put(
			new QName(XML.SAMLP_NS, "AuthenticationQueryType"),
			"gov.nih.nci.cagrid.opensaml.SAMLAuthenticationQuery");
		SAMLQuery.queryTypeMap.put(
			new QName(XML.SAMLP_NS, "AuthorizationDecisionQuery"),
			"gov.nih.nci.cagrid.opensaml.SAMLAuthorizationDecisionQuery");
		SAMLQuery.queryTypeMap.put(
			new QName(XML.SAMLP_NS, "AuthorizationDecisionQueryType"),
			"gov.nih.nci.cagrid.opensaml.SAMLAuthorizationDecisionQuery");

		SAMLStatement.statementTypeMap.put(
			new QName(XML.SAML_NS, "AttributeStatement"),
			"gov.nih.nci.cagrid.opensaml.SAMLAttributeStatement");
		SAMLStatement.statementTypeMap.put(
			new QName(XML.SAML_NS, "AttributeStatementType"),
			"gov.nih.nci.cagrid.opensaml.SAMLAttributeStatement");
		SAMLStatement.statementTypeMap.put(
			new QName(XML.SAML_NS, "AuthenticationStatement"),
			"gov.nih.nci.cagrid.opensaml.SAMLAuthenticationStatement");
		SAMLStatement.statementTypeMap.put(
			new QName(XML.SAML_NS, "AuthenticationStatementType"),
			"gov.nih.nci.cagrid.opensaml.SAMLAuthenticationStatement");
		SAMLStatement.statementTypeMap.put(
			new QName(XML.SAML_NS, "AuthorizationDecisionStatement"),
			"gov.nih.nci.cagrid.opensaml.SAMLAuthorizationDecisionStatement");
		SAMLStatement.statementTypeMap.put(
			new QName(XML.SAML_NS, "AuthorizationDecisionStatementType"),
			"gov.nih.nci.cagrid.opensaml.SAMLAuthorizationDecisionStatement");

        // Register default binding implementations...
        setDefaultBindingProvider(SAMLBinding.SOAP, getProperty("gov.nih.nci.cagrid.opensaml.provider.soapbinding"));
    }

	/**
	 * Returns the active OpenSAML configuration.
	 * @return SAMLConfig
	 */
	public synchronized static SAMLConfig instance() {

		if (instance == null) {
			instance = new SAMLConfig();
			return instance;
		}
		return instance;
	}

    /**
     * Returns the default provider of the SAMLIdentifier interface
     * @return  the default provider
     */
    public synchronized SAMLIdentifier getDefaultIDProvider() {
        if (IDProvider == null)
            IDProvider = SAMLIdentifierFactory.getInstance();
        return IDProvider;
    }
    
    public synchronized String getDefaultBindingProvider(String binding) {
        return (String)bindingMap.get(binding);
    }
    
    public synchronized void setDefaultBindingProvider(String binding, String provider) {
        bindingMap.put(binding,provider);
    }
    
	/**
	 * Enables a set of configuration properties.
	 * @param properties the configuration properties to be enabled
	 */
	public void setProperties(Properties properties) {
		this.properties.putAll(properties);
	}

	/**
	 * Enables a set of configuration properties.
	 * @param inStream an <code>InputStream</code> from which 
	 * a java properties file can be obtained.
	 */
	public void loadProperties(InputStream inStream) throws IOException {
		Properties newProperties = new Properties();
		newProperties.load(inStream);
		setProperties(newProperties);
	}

	/**
	 *  Sets a library configuration property<p>
	 * 
	 * @param  key      A property name
	 * @param  value    The value to set
	 */
	public void setProperty(String key, String value) {
		properties.setProperty(key, value);
	}

	/**
	 *  Gets a library configuration property
	 *
	 * @param  key      A property name
	 * @return          The property's value, or null if the property isn't set
	 */
	public String getProperty(String key) {
		return properties.getProperty(key);
	}

	/**
	 *  Gets a binary library configuration property in boolean form
	 *
	 * @param  key      A property name
	 * @return          The property's boolean value, or false if the property isn't set
	 */
	public boolean getBooleanProperty(String key) {
		return new Boolean(properties.getProperty(key)).booleanValue();
	}

    /**
     *  Sets a binary library configuration property in boolean form
     *
     * @param  key      A property name
     * @return          The property's boolean value, or false if the property isn't set
     */
    public void setBooleanProperty(String key, Boolean value) {
        setProperty(key, value.toString());
    }
    
    /**
     *  Gets a binary library configuration property in boolean form
     *
     * @param  key      A property name
     * @return          The property's boolean value, or false if the property isn't set
     */
    public int getIntProperty(String key) {
        return new Integer(properties.getProperty(key)).intValue();
    }

    /**
     *  Sets a binary library configuration property in boolean form
     *
     * @param  key      A property name
     * @return          The property's boolean value, or false if the property isn't set
     */
    public void setIntProperty(String key, int value) {
        setProperty(key, new Integer(value).toString());
    }
    
	private void verifyUsableXmlParser() {
		try {
			Class.forName("javax.xml.validation.SchemaFactory");
			Element.class.getDeclaredMethod("setIdAttributeNS", new Class[]{String.class, String.class,
					java.lang.Boolean.TYPE});
		} catch (NoSuchMethodException e) {
			throw new FactoryConfigurationError("OpenSAML requires an xml parser that supports DOM3 calls. "
					+ "Sun JAXP 1.3 has been included with this release and is strongly recommended. "
					+ "If you are using Java 1.4, make sure that you have enabled the Endorsed "
					+ "Standards Override Mechanism for this parser "
					+ "(see http://java.sun.com/j2se/1.4.2/docs/guide/standards/ for details).");
		} catch (ClassNotFoundException e) {
			throw new FactoryConfigurationError("OpenSAML requires an xml parser that supports JAXP 1.3. "
					+ "Sun JAXP 1.3 has been included with this release and is strongly recommended. "
					+ "If you are using Java 1.4, make sure that you have enabled the Endorsed "
					+ "Standards Override Mechanism for this parser "
					+ "(see http://java.sun.com/j2se/1.4.2/docs/guide/standards/ for details).");
		}
	}
}
