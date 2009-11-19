package org.cagrid.identifiers.namingauthority;

import java.net.URI;
import java.net.URISyntaxException;

import org.cagrid.identifiers.namingauthority.test.NamingAuthorityTestCaseBase;


public class NamingAuthorityTestCase extends NamingAuthorityTestCaseBase {

    public void test() {
        URI prefix = URI.create("http://na.cagrid.org/foo/");

        IdentifierValues values = null;
        try {
            values = this.NamingAuthority.resolveIdentifier(prefix);
        } catch (URISyntaxException e) {
            fail("test configuration error");
        } catch (InvalidIdentifierException e) {
            // expected
        } catch (NamingAuthorityConfigurationException e) {
            fail("test configuration error");
        }

        assertResolvedValues(null);
        assertResolvedValues(values);
        
        values = new IdentifierValues();
        values.add("URL", "http://www.google.com");
        values.add("URL", "http://www.gmail.com");
        values.add("ERP", "End point reference 1");
        assertResolvedValues(values);

    }


    private void assertResolvedValues(IdentifierValues values) {
        URI id = null;
        try {
            id = this.NamingAuthority.createIdentifier(values);
 System.out.println("IDENTIFIER:" + id.toString());
        } catch (NamingAuthorityConfigurationException e) {
            e.printStackTrace();
            fail("test configuration error");

        } catch (InvalidIdentifierValuesException e) {
            fail("Unexpected failure on creation:" + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            fail("test configuration error");
        }

        IdentifierValues resolvedValues = null;

        try {
            resolvedValues = this.NamingAuthority.resolveIdentifier(id);
        } catch (NamingAuthorityConfigurationException e) {
            e.printStackTrace();
            fail("test configuration error");
        } catch (InvalidIdentifierException e) {
            fail("Unexpected failure on resolution:" + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            fail("test configuration error");
        }
        assertEquals(values, resolvedValues);
    }
}
