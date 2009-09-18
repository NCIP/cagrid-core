package gov.nih.nci.cagrid.metadata;

import java.util.Iterator;
import java.util.Map;


public class XPathUtils {

	private static final String LOCAL_REPLACEMENT = "' and local-name()='$1'";
	private static final String URI_REPLACEMENT = "*[namespace-uri()='";
	private static final String NS_START_REGEX = ":([a-zA-Z]+)";


	/**
	 * This utilty takes an xpath that uses namespace prefixes (such as
	 * /a:B/a:C) and converts it to one without prefixes, by using the
	 * appropriate operators instead (such as
	 * /*[namespace-uri()='http://DOMAIN.COM/SCHEMA' and
	 * local-name()='B']/*[namespace-uri()='http://DOMAIN.COM/SCHEMA' and
	 * local-name()='C']). THe only conceivable use for this funciton is to
	 * write sane xpath and convert it to the insane xpath globus index service
	 * supports.
	 * 
	 * NOTE: This isn't perfect. The known limitations are: 1) its
	 * overly agressive, and will replace QName-looking string literals, 2) it
	 * won't work if you have namespaces attributes 3) it will silently not
	 * replace any QNames that you haven't supplied a prefix mapping for
	 * 
	 * @param prefixedXpath
	 *            An xpath [optionally] using namespace prefixes in nodetests
	 * @param namespaces
	 *            A Map<String,String> keyed on namespace prefixes to resolve
	 *            in the xpath, where the value is the actual namespace that
	 *            should be used.
	 * @return a converted, conformant, xpath
	 */
	public static String translateXPath(String prefixedXpath, Map namespaces) {
		// don't process an empty Xpath, or one with not ns prefixes
		if (prefixedXpath == null || prefixedXpath.trim().length() == 0 || prefixedXpath.indexOf(":") < 0) {
			return prefixedXpath;
		} else if (namespaces == null || namespaces.keySet().size() == 0) {
			throw new IllegalArgumentException(
				"You specified an XPath with prefixes, yet didn't define any prefix mappings.");
		}

		// process all the replacements based on prefixes
		Iterator iterator = namespaces.keySet().iterator();
		while (iterator.hasNext()) {
			String prefix = (String) iterator.next();
			String ns = (String) namespaces.get(prefix);

			// replace the last thing in the xpath being a qname
			prefixedXpath = prefixedXpath.replaceAll(prefix + NS_START_REGEX + "[\\s]*$", URI_REPLACEMENT + ns
				+ LOCAL_REPLACEMENT + "]");

			// replace any qname that is starting a predicate
			prefixedXpath = prefixedXpath.replaceAll(prefix + NS_START_REGEX + "\\[", URI_REPLACEMENT + ns
				+ LOCAL_REPLACEMENT + " and ");

			// replace any other qname (has some character after the qname that
			// isn't the start of a predicate)
			prefixedXpath = prefixedXpath.replaceAll(prefix + NS_START_REGEX + "([^\\[]){1,1}", URI_REPLACEMENT + ns
				+ LOCAL_REPLACEMENT + "]$2");

		}

		return prefixedXpath;
	}
}
