package org.cagrid.identifiers.namingauthority;

import java.net.URI;
import java.util.ArrayList;

import org.cagrid.identifiers.namingauthority.domain.IdentifierData;
import org.cagrid.identifiers.namingauthority.domain.IdentifierValues;
import org.cagrid.identifiers.namingauthority.domain.KeyData;
import org.cagrid.identifiers.namingauthority.domain.KeyValues;
import org.cagrid.identifiers.namingauthority.test.NamingAuthorityTestCaseBase;
import org.cagrid.identifiers.namingauthority.util.Keys;


public class NamingAuthorityTestCase extends NamingAuthorityTestCaseBase {

    private static IdentifierData globalValues = null;

    static {
        globalValues = new IdentifierData();

        globalValues
            .put("URL", new KeyData(null, new String[]{"http://na.cagrid.org/foo", "http://na.cagrid.org/bar"}));

        globalValues.put("CODE", new KeyData(null, new String[]{"007"}));
    }


    public void testInvalidIdentifier() {

        //
        // Identifier is not local to prefix hosted by naming authority
        //
        URI prefix = URI.create("http://na.cagrid.org/foo/");

        try {
            this.NamingAuthority.resolveIdentifier(null, prefix);
        } catch (InvalidIdentifierException e) {
            // expected
        } catch (NamingAuthorityConfigurationException e) {
            e.printStackTrace();
            fail("test configuration error");
        } catch (NamingAuthoritySecurityException e) {
            e.printStackTrace();
            fail("test configuration exception error");
        }

        //
        // Identifier does not exist
        //
        prefix = URI.create(this.NamingAuthority.getConfiguration().getNaPrefixURI() + "BADIDENTIFIER");

        try {
            this.NamingAuthority.resolveIdentifier(null, prefix);
        } catch (InvalidIdentifierException e) {
            // expected
        } catch (NamingAuthorityConfigurationException e) {
            e.printStackTrace();
            fail("test configuration error");
        } catch (NamingAuthoritySecurityException e) {
            e.printStackTrace();
            fail("test configuration security exception");
        }
    }


    // ////////////////////////////////////////////////////////////////////
    // Test missing key name
    // ////////////////////////////////////////////////////////////////////
    public void testMissingKeyName() {
        IdentifierData values = new IdentifierData();
        values.put("", null);
        try {
            URI id = this.NamingAuthority.createIdentifier(null, values);
            System.out.println("testMissingKeyName: " + id.normalize().toString());
            fail("Expected InvalidIdentifierValuesException was not raised");
        } catch (InvalidIdentifierValuesException e) {
            // expected
        } catch (Exception e) {
            e.printStackTrace();
            fail("Unexpected exception: " + e.getMessage());
        }
    }


    // ////////////////////////////////////////////////////////////////////
    // Can create and resolve identifier that has no IdentifierValues
    // ////////////////////////////////////////////////////////////////////
    public void testNullIdentifierValues() {
        // //////////////////////////////////////////////////////////////////////
        // Null IdentifierValues
        assertResolvedValues(null);

        // //////////////////////////////////////////////////////////////////////
        // Empty IdentifierValues
        IdentifierData values = null;
        try {
            URI id = this.NamingAuthority.createIdentifier(null, new IdentifierData());
            values = this.NamingAuthority.resolveIdentifier(null, id);
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

        if (values != null) {
            fail("Values is expected to be null");
        }
    }


    // //////////////////////////////////////////////////////////////////////////
    // Create Identifier with keys that have no data
    // //////////////////////////////////////////////////////////////////////////
    public void testCreateIdentifierKeysNoData() {
        IdentifierData values = new IdentifierData();
        values.put("KEY1", null);
        values.put("KEY2", new KeyData());
        values.put("KEY3", new KeyData(null, (ArrayList<String>) null));
        values.put("KEY4", new KeyData(null, new ArrayList<String>()));
        values.put("KEY5", new KeyData(null, (String[]) null));
        values.put("KEY6", new KeyData(null, new String[]{}));
        try {
            URI id = this.NamingAuthority.createIdentifier(null, values);
            System.out.println("testCreateIdentifierKeysNoData: " + id.normalize().toString());
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }


    // //////////////////////////////////////////////////////////////////////////
    // Create identifier with multiple values per key
    // //////////////////////////////////////////////////////////////////////////
    public void testMultipleIdentifierValues() {
        assertResolvedValues(globalValues);
    }


    // //////////////////////////////////////////////////////////////////////////
    // Test getKeys interface
    // //////////////////////////////////////////////////////////////////////////
    public void testGetKeys() {
        assertKeys(globalValues);
    }


    // //////////////////////////////////////////////////////////////////////////
    // Test getKeyValues interface
    // //////////////////////////////////////////////////////////////////////////
    public void testGetKeyValues() {
        assertKeyValues(globalValues, new String[]{"URL", "CODE"});
    }


    // //////////////////////////////////////////////////////////////////////////
    // Test createKeys interface
    // //////////////////////////////////////////////////////////////////////////
    public void testCreateKeys() {
        URI id = null;
        IdentifierData resolvedValues = null;
        
        try {
            id = this.NamingAuthority.createIdentifier(null, globalValues);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Failed to create identifier");
        }

        // //////////////////////////////////////////////////////////////////////////
        // InvalidIdentifierValues (null)
        //
        try {
            this.NamingAuthority.createKeys(null, id, null);
            fail("Expected InvalidIdentifierValuesException was not raised");
        } catch (InvalidIdentifierValuesException e) {
            // expected
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

        // //////////////////////////////////////////////////////////////////////////
        // InvalidIdentifierValues (bad key's rwidentifier)
        //
        IdentifierData newKeys = new IdentifierData();
        newKeys.put("BAD RWINDENTIFIER", new KeyData(URI.create("http://badurl"), new ArrayList<String>()));
        try {
            this.NamingAuthority.createKeys(null, id, newKeys);
            fail("Expected InvalidIdentifierValuesException was not raised");
        } catch (InvalidIdentifierValuesException e) {
            // expected
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

        // //////////////////////////////////////////////////////////////////////////
        // InvalidIdentifierValues (bad ADMIN_IDENTIFIER)
        //
        newKeys = new IdentifierData();
        newKeys.put(Keys.ADMIN_IDENTIFIERS, new KeyData(null, new String[]{"http://bad"}));
        try {
            this.NamingAuthority.createKeys(null, id, newKeys);
            fail("Expected InvalidIdentifierValuesException was not raised");
        } catch (InvalidIdentifierValuesException e) {
            // expected
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // //////////////////////////////////////////////////////////////////////////
        // InvalidIdentifierValues (bad READWRITE_IDENTIFIERS)
        //
        newKeys = new IdentifierData();
        newKeys.put(Keys.READWRITE_IDENTIFIERS, new KeyData(null, new String[]{"http://bad"}));
        try {
            this.NamingAuthority.createKeys(null, id, newKeys);
            fail("Expected InvalidIdentifierValuesException was not raised");
        } catch (InvalidIdentifierValuesException e) {
            // expected
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // //////////////////////////////////////////////////////////////////////////
        // This should be successful
        //
        newKeys = new IdentifierData();
        newKeys.put("ADD KEY1", new KeyData(null, new String[]{"key1 value1", "key1 value2"}));
        newKeys.put("ADD KEY2", new KeyData(null, new String[]{"key2 value"}));
        try {
            this.NamingAuthority.createKeys(null, id, newKeys);
            resolvedValues = this.NamingAuthority.resolveIdentifier(null, id);
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
        checkKeysWithValues(resolvedValues, new String[]{"CODE", "URL", "ADD KEY1", "ADD KEY2"});

        // //////////////////////////////////////////////////////////////////////////
        // Key already exists
        //
        newKeys = new IdentifierData();
        newKeys.put("CODE", new KeyData(null, new String[]{"code value"}));
        try {
            this.NamingAuthority.createKeys(null, id, newKeys);
            resolvedValues = this.NamingAuthority.resolveIdentifier(null, id);
            fail("Expected InvalidIdentifierValuesException was not raised");
        } catch (InvalidIdentifierValuesException e) {
            // expected
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }


    // //////////////////////////////////////////////////////////////////////////
    // Test deleteKeys interface
    // //////////////////////////////////////////////////////////////////////////
    public void testDeleteKeys() {
        URI id = null;
        IdentifierData resolvedValues = null;

        try {
            id = this.NamingAuthority.createIdentifier(null, globalValues);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Failed to create identifier");
        }

        // //////////////////////////////////////////////////////////////////////////
        // InvalidIdentifierValues (null)
        //
        try {
            this.NamingAuthority.deleteKeys(null, id, null);
            fail("Expected InvalidIdentifierValuesException was not raised");
        } catch (InvalidIdentifierValuesException e) {
            // expected
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

        // //////////////////////////////////////////////////////////////////////////
        // InvalidIdentifierValues (key doesn't exist)
        //
        String[] keyList = new String[]{"wrongKeyName"};
        try {
            this.NamingAuthority.deleteKeys(null, id, keyList);
            fail("Expected InvalidIdentifierValuesException was not raised");
        } catch (InvalidIdentifierValuesException e) {
            // expected
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

        // //////////////////////////////////////////////////////////////////////////
        // This should be successful
        //
        keyList = new String[]{"CODE"};
        try {
            this.NamingAuthority.deleteKeys(null, id, keyList);
            resolvedValues = this.NamingAuthority.resolveIdentifier(null, id);
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

        if (resolvedValues.getValues("CODE") != null) {
            fail("CODE still exists");
        }

        if (resolvedValues.getValues("URL") == null) {
            fail("URL is no longer present");
        }
    }


    // ////////////////////////////////////////////////////////////////////////////
    // Test replaceKeys interface
    // ////////////////////////////////////////////////////////////////////////////
    public void testReplaceKeys() {
        URI id = null;
        IdentifierData resolvedValues = null;
        
        try {
            id = this.NamingAuthority.createIdentifier(null, globalValues);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Failed to create identifier");
        }

        // //////////////////////////////////////////////////////////////////////////
        // InvalidIdentifierValues (null)
        //
        try {
            this.NamingAuthority.replaceKeyValues(null, id, null);
            fail("Expected InvalidIdentifierValuesException was not raised");
        } catch (InvalidIdentifierValuesException e) {
            // expected
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

        // //////////////////////////////////////////////////////////////////////////
        // InvalidIdentifierValues (key doesn't exist)
        //
        IdentifierValues values = new IdentifierValues();
        values.put("wrongKeyName", null);
        try {
            this.NamingAuthority.replaceKeyValues(null, id, values);
            fail("Expected InvalidIdentifierValuesException was not raised");
        } catch (InvalidIdentifierValuesException e) {
            // expected
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

        // //////////////////////////////////////////////////////////////////////////
        // This should be successful
        //
        String newCode = "008";
        values = new IdentifierValues();
        values.put("CODE", new KeyValues(new String[]{newCode}));
        try {
            this.NamingAuthority.replaceKeyValues(null, id, values);
            resolvedValues = this.NamingAuthority.resolveIdentifier(null, id);
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

        if (!resolvedValues.getValues("CODE").getValues().get(0).equals(newCode)) {
            fail("Unexpected CODE");
        }
    }
}
