package org.cagrid.gaards.ui.dorian.federation;

import gov.nih.nci.cagrid.opensaml.SAMLAssertion;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.cagrid.gaards.credentials.DorianUserCredentialDescriptor;
import org.cagrid.gaards.credentials.EncodingUtil;
import org.cagrid.gaards.saml.encoding.SAMLUtils;
import org.globus.gsi.GlobusCredential;


public class CredentialUtils {
    private static List<QName> COMMON_FIRST_NAMES = null;
    private static List<QName> COMMON_LAST_NAMES = null;
    private static List<QName> COMMON_EMAIL = null;


    private static List<QName> getCommonFirstNames() {
        if (COMMON_FIRST_NAMES == null) {
            COMMON_FIRST_NAMES = new ArrayList<QName>();
            COMMON_FIRST_NAMES.add(new QName("urn:mace:shibboleth:1.0:attributeNamespace:uri",
                "urn:mace:dir:attribute-def:givenName"));
        }
        return COMMON_FIRST_NAMES;
    }


    private static List<QName> getCommonLastNames() {
        if (COMMON_LAST_NAMES == null) {
            COMMON_LAST_NAMES = new ArrayList<QName>();
            COMMON_LAST_NAMES.add(new QName("urn:mace:shibboleth:1.0:attributeNamespace:uri",
                "urn:mace:dir:attribute-def:sn"));
        }
        return COMMON_LAST_NAMES;
    }


    private static List<QName> getCommonEmail() {
        if (COMMON_EMAIL == null) {
            COMMON_EMAIL = new ArrayList<QName>();
            COMMON_EMAIL.add(new QName("urn:mace:shibboleth:1.0:attributeNamespace:uri",
                "urn:mace:dir:attribute-def:mail"));
        }
        return COMMON_EMAIL;
    }


    public static String determineFirstName(SAMLAssertion saml) {
        return determineCommonAttribute(saml, getCommonFirstNames());
    }


    public static String determineLastName(SAMLAssertion saml) {
        return determineCommonAttribute(saml, getCommonLastNames());
    }


    public static String determineEmail(SAMLAssertion saml) {
        return determineCommonAttribute(saml, getCommonEmail());
    }


    public static String determineCommonAttribute(SAMLAssertion saml, List<QName> atts) {
        for (int i = 0; i < atts.size(); i++) {
            QName ns = atts.get(i);
            String val = SAMLUtils.getAttributeValue(saml, ns.getNamespaceURI(), ns.getLocalPart());
            if (val != null) {
                return val;
            }
        }
        return null;
    }


    public static DorianUserCredentialDescriptor encode(String dorianURL, String authenticationServiceURL,
        String organization, SAMLAssertion saml, GlobusCredential cred) throws Exception {
        DorianUserCredentialDescriptor des = new DorianUserCredentialDescriptor();
        des = (DorianUserCredentialDescriptor) EncodingUtil.encode(cred, des);
        des.setAuthenticationServiceURL(authenticationServiceURL);
        des.setDorianURL(dorianURL);
        des.setOrganization(organization);
        des.setFirstName(determineFirstName(saml));
        des.setLastName(determineLastName(saml));
        des.setEmail(determineEmail(saml));
        return des;
    }

}
