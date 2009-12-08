package org.cagrid.identifiers.namingauthority.util;

import java.net.URI;
import java.net.URISyntaxException;

import junit.framework.TestCase;

import org.cagrid.identifiers.namingauthority.InvalidIdentifierException;
import org.cagrid.identifiers.namingauthority.NamingAuthorityConfigurationException;
import org.junit.Test;


public class IdentifierUtilTestCase extends TestCase {

    @Test
    public void testInvalidPrefixes() {
        assertInvalidPrefix("");
        assertInvalidPrefix("asdf");
        assertInvalidPrefix("http://foo.com/?foo");
        assertInvalidPrefix("http://foo.com/#foo");
        assertInvalidPrefix("http://foo.com/?foo#foo");
        assertInvalidPrefix("http://foo.com");
        assertInvalidPrefix("http://foo.com/a/b");
    }


    public void testInvalidIdentifiers() {
        assertInvalidIdentifier("http://foo.com/", "http://foo.com");
        assertInvalidIdentifier("http://foo.com/", "http://foo.com/");
        assertInvalidIdentifier("http://foo.com/", "");
        assertInvalidIdentifier("http://foo.com/", "http://foobar.com/asdf");
        assertInvalidIdentifier("http://foo.com/", "http://foobar.com/asdf/asdf");
        assertInvalidIdentifier("http://foo.com/asdf/", "http://foobar.com/../asdf");
        assertInvalidIdentifier("http://foo.com/asdf/", "http://foobar.com/../a");

    }


    private void assertInvalidIdentifier(String prefix, String identifier) {
        try {
            IdentifierUtil.verifyPrefix(new URI(prefix));
        } catch (URISyntaxException e) {
            e.printStackTrace();
            fail("Test configuration issue.");
        } catch (Exception e) {
            fail("Unexpected exception for prefix (" + prefix + ").");
        }

        try {
            IdentifierUtil.getLocalName(new URI(prefix), new URI(identifier));
            fail("Excepted exception was not thrown for prefix (" + prefix + ") and id (" + identifier + ")");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            fail("Test configuration issue.");
        } catch (InvalidIdentifierException e) {
            // expected
        } catch (Exception e) {
            fail("Unexpected exception for prefix (" + prefix + ") and id (" + identifier + ") :" + e.getMessage());
        }

    }


    private void assertInvalidPrefix(String prefix) {
        try {
            IdentifierUtil.verifyPrefix(new URI(prefix));
            fail("Expected exception for invalid prefix (" + prefix + "), but it was not thrown.");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            fail("Test configuration issue.");
        } catch (IllegalArgumentException e) {
            // expected
        } catch (Exception e) {
            fail("Expected exception for invalid prefix (" + prefix + "), but unexcepted Exception type thrown ("
                + e.getClass() + ").");
        }
    }


    @Test
    public void testBuild() throws URISyntaxException, NamingAuthorityConfigurationException {
        URI prefix = new URI("http://na.cagrid.org/foo/");
        URI shortID = new URI("http://na.cagrid.org/foo/1");

        // NOTE: our current algorithm treats this as the same, as the localName
        // isn't just a "string" its a relative path in a URI
        assertEquals(shortID, IdentifierUtil.build(prefix, URI.create("1")));
        assertEquals(shortID, IdentifierUtil.build(prefix, URI.create("/1")));
        assertEquals(shortID, IdentifierUtil.build(prefix, URI.create("./1")));
        assertEquals(shortID, IdentifierUtil.build(prefix, URI.create("../foo/1")));

        URI longPrefix = new URI("http://na.cagrid.org/foo/bar/baz/");
        URI longID = new URI("http://na.cagrid.org/foo/bar/baz/foo/bar/");

        assertEquals(longID, IdentifierUtil.build(longPrefix, URI.create("foo/bar/")));
        assertEquals(longID, IdentifierUtil.build(longPrefix, URI.create("/foo/bar/")));
        assertEquals(longID, IdentifierUtil.build(longPrefix, URI.create("./foo/bar/")));
        assertEquals(longID, IdentifierUtil.build(longPrefix, URI.create("../baz/foo/bar/")));
    }


    @Test
    public void testLocalName() throws URISyntaxException, InvalidIdentifierException, NamingAuthorityConfigurationException {
        URI prefix = new URI("http://na.cagrid.org/foo/");
        URI shortID = new URI("http://na.cagrid.org/foo/1");

        assertEquals(new URI("1"), IdentifierUtil.getLocalName(prefix, shortID));
        assertEquals(new URI("1"), IdentifierUtil.getLocalName(prefix, new URI("http://na.cagrid.org/foo/./1")));
        assertEquals(new URI("1"), IdentifierUtil.getLocalName(prefix, new URI("http://na.cagrid.org/foo/../foo/1")));


        URI longPrefix = new URI("http://na.cagrid.org/foo/bar/baz/");
        URI longID = new URI("http://na.cagrid.org/foo/bar/baz/foo/bar/");

        assertEquals(new URI("foo/bar/"), IdentifierUtil.getLocalName(longPrefix, longID));

    }

}
