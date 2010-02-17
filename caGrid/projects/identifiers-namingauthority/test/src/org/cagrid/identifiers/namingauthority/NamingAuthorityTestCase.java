package org.cagrid.identifiers.namingauthority;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.cagrid.identifiers.namingauthority.domain.IdentifierValues;
import org.cagrid.identifiers.namingauthority.domain.KeyData;
import org.cagrid.identifiers.namingauthority.test.NamingAuthorityTestCaseBase;


public class NamingAuthorityTestCase extends NamingAuthorityTestCaseBase {

	
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
        prefix = URI.create(this.NamingAuthority.getConfiguration().getPrefix() 
        		+ "BADIDENTIFIER");

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
	
	//
	// Can create and resolve identifier that has no IdentifierValues
	//
	public void testNullIdentifierValues() {
        assertResolvedValues(null);
    }
	
	// Create identifier with multiple values per key
	public void testMultipleIdentifierValues() {
		IdentifierValues values = new IdentifierValues();
		
		values.put("URL", new KeyData( null, 
				new String[]{"http://na.cagrid.org/foo", "http://na.cagrid.org/bar"} ));
		
		values.put("CODE", new KeyData( null,
				new String[]{ "007" }));
			
		assertResolvedValues(values);
	}
	
	public void testGetKeys() {
		IdentifierValues values = new IdentifierValues();
		
		values.put("URL", new KeyData( null, 
				new String[]{"http://na.cagrid.org/foo", "http://na.cagrid.org/bar"} ));
		
		values.put("CODE", new KeyData( null,
				new String[]{ "007" }));
		
		assertKeys(values);
	}
	
	public void testGetKeyValues() {
		IdentifierValues values = new IdentifierValues();
		
		values.put("URL", new KeyData( null, 
				new String[]{"http://na.cagrid.org/foo", "http://na.cagrid.org/bar"} ));
		
		values.put("CODE", new KeyData( null,
				new String[]{ "007" }));
		
		assertKeyValues(values, new String[]{ "URL", "CODE" });
	}
	
	private void assertKeyValues(IdentifierValues values, String[] keyNames) {
        URI id = null;
        try {
            id = this.NamingAuthority.createIdentifier(null, values);
        } catch (NamingAuthorityConfigurationException e) {
            e.printStackTrace();
            fail("test configuration error");

        } catch (InvalidIdentifierValuesException e) {
            fail("Unexpected failure on creation:" + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            fail("test configuration error");
        }

        String[] resolvedValues = null;

        for(String key : keyNames) {
        	try {
        		resolvedValues = this.NamingAuthority.getKeyValues(null, id, key);
        	} catch (NamingAuthorityConfigurationException e) {
        		e.printStackTrace();
        		fail("test configuration error");
        	} catch (InvalidIdentifierException e) {
        		fail("Unexpected failure on resolution:" + e.getMessage());
        	} catch (Exception e) {
        		e.printStackTrace();
        		fail("test configuration error");
        	}
        	List<String> inValues = values.getValues(key).getValues();
        	assertEquals(inValues.toArray(new String[inValues.size()]), resolvedValues);
        }
    }
	
    private void assertKeys(IdentifierValues values) {
        URI id = null;
        try {
            id = this.NamingAuthority.createIdentifier(null, values);
        } catch (NamingAuthorityConfigurationException e) {
            e.printStackTrace();
            fail("test configuration error");

        } catch (InvalidIdentifierValuesException e) {
            fail("Unexpected failure on creation:" + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            fail("test configuration error");
        }

        String[] resolvedKeys = null;

        try {
            resolvedKeys = this.NamingAuthority.getKeys(null, id);
        } catch (NamingAuthorityConfigurationException e) {
            e.printStackTrace();
            fail("test configuration error");
        } catch (InvalidIdentifierException e) {
            fail("Unexpected failure on resolution:" + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            fail("test configuration error");
        }
        assertEquals(values.getKeys(), resolvedKeys);
    }

    private void assertResolvedValues(IdentifierValues values) {
        URI id = null;
        try {
            id = this.NamingAuthority.createIdentifier(null, values);
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
            resolvedValues = this.NamingAuthority.resolveIdentifier(null, id);
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
    
    private void assertEquals(String[] arr1, String[] arr2) {

        Arrays.sort(arr1);
        Arrays.sort(arr2);

        assertEquals(Arrays.asList(arr1), Arrays.asList(arr2));
    }
}
