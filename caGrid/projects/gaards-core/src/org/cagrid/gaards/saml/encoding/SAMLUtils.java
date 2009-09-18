package org.cagrid.gaards.saml.encoding;

import gov.nih.nci.cagrid.opensaml.SAMLAssertion;
import gov.nih.nci.cagrid.opensaml.SAMLAttribute;
import gov.nih.nci.cagrid.opensaml.SAMLAttributeStatement;

import java.io.ByteArrayInputStream;
import java.util.Iterator;

import org.w3c.dom.Element;

/**
 * @author <A href="mailto:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A href="mailto:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A href="mailto:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @version $Id: ArgumentManagerTable.java,v 1.2 2004/10/15 16:35:16 langella
 *          Exp $
 */
public class SAMLUtils {

	public static SAMLAssertion domToSAMLAssertion(Element dom)
			throws Exception {
		SAMLAssertion saml = new SAMLAssertion(dom);
		return saml;
	}

	public static SAMLAssertion stringToSAMLAssertion(String str)
			throws Exception {
		SAMLAssertion saml = new SAMLAssertion(new ByteArrayInputStream(str
				.getBytes()));
		return saml;
	}

	public static String samlAssertionToString(SAMLAssertion saml)
			throws Exception {
		String xml = saml.toString();
		return xml;
	}

	public static Element samlAssertionToDOM(SAMLAssertion saml)
			throws Exception {
		return (Element) saml.toDOM();
	}

	public static String getAttributeValue(SAMLAssertion saml,
			String namespace, String name) {
		Iterator itr = saml.getStatements();
		while (itr.hasNext()) {
			Object o = itr.next();
			if (o instanceof SAMLAttributeStatement) {
				SAMLAttributeStatement att = (SAMLAttributeStatement) o;
				Iterator attItr = att.getAttributes();
				while (attItr.hasNext()) {
					SAMLAttribute a = (SAMLAttribute) attItr.next();
					if ((a.getNamespace().equals(namespace))
							&& (a.getName().equals(name))) {
						Iterator vals = a.getValues();
						while (vals.hasNext()) {
							String val = gov.nih.nci.cagrid.common.Utils
									.clean((String) vals.next());
							if (val != null) {
								return val;
							}
						}
					}
				}
			}
		}
		return null;
	}
}
