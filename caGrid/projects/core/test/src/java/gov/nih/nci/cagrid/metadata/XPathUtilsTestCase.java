package gov.nih.nci.cagrid.metadata;

import gov.nih.nci.cagrid.metadata.XPathUtils;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class XPathUtilsTestCase extends TestCase {

	private Map prefixMap;
	protected static Log LOG = LogFactory.getLog(XPathUtilsTestCase.class.getName());


	public static void main(String[] args) {
		junit.textui.TestRunner.run(XPathUtilsTestCase.class);
	}


	protected void setUp() throws Exception {
		super.setUp();

		prefixMap = new HashMap();
		prefixMap.put("a", "http://DOMAIN.COM/SCHEMA");
	}


	public void testNoUse() {
		assertNoModification(null);
		assertNoModification("");
		assertNoModification("/");
		assertNoModification("/a");
		assertNoModification("/a/b/c");
		assertNoModification("/a[@b!='foo']");
		assertNoModification("/a[@b!='foo']/*[namespace-uri()='http://somedomain.com/200/foobar' and local-name()='SomeElementName']");
		assertNoModification("/child::node()/child::node()[@id='a']");
	}


	public void assertNoModification(String xpath) {
		try {
			String translated = XPathUtils.translateXPath(xpath, prefixMap);
			assertEquals(translated, xpath);
		} catch (IllegalArgumentException e) {
			fail(e.getMessage());
		}
	}


	public void assertReplacedAndRepeatable(String xpath) {
		try {
			String translated = XPathUtils.translateXPath(xpath, prefixMap);
			LOG.debug("ORIGINAL:" + xpath);
			LOG.debug("TRANSLATED:" + translated);

			assertTrue(translated.length() > xpath.length());
			assertTrue(translated.indexOf("][") < 0);
			assertTrue(translated.indexOf("namespace-uri") >= 0);
			// make sure if we run it again, it doesn't alter it
			assertEquals(translated, XPathUtils.translateXPath(translated, prefixMap));
		} catch (IllegalArgumentException e) {
			fail(e.getMessage());
		}
	}


	public void testValidPaths() {
		assertReplacedAndRepeatable("/a:B");
		assertReplacedAndRepeatable("/a:B/a:C");
		assertReplacedAndRepeatable("/a:B[@b!='foo']");
		assertReplacedAndRepeatable("/a:B[@b!='foo']/a:C");
		assertReplacedAndRepeatable("/a:B[@b!='foo' and a:C/text()='something']/a:C");
		assertReplacedAndRepeatable("/a:B[@b!='foo' and a:C or a:B/[@b='a']]/a:C");
		assertReplacedAndRepeatable("/a:B[@b!='foo' and a:C or a:B/[@b='a']]/*[namespace-uri()='http://DOMAIN.COM:80/SCHEMA' and local-name()='B']");
	}
}
