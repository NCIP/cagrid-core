package org.cagrid.identifiers.namingauthority.test;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import org.cagrid.identifiers.namingauthority.InvalidIdentifierException;
import org.cagrid.identifiers.namingauthority.InvalidIdentifierValuesException;
import org.cagrid.identifiers.namingauthority.MaintainerNamingAuthority;
import org.cagrid.identifiers.namingauthority.NamingAuthorityConfigurationException;
import org.cagrid.identifiers.namingauthority.domain.IdentifierValues;
import org.cagrid.identifiers.namingauthority.util.SecurityUtil;


public abstract class NamingAuthorityTestCaseBase extends NamingAuthorityIntegrationTestCaseBase {
    protected MaintainerNamingAuthority NamingAuthority;


    @Override
    protected void onSetUp() throws Exception {
        super.onSetUp();
        assertNotNull(this.NamingAuthority);
    }

	protected void checkKeysWithValues(IdentifierValues resolvedValues, String[] keyNames) {

        for(String key : keyNames) {
        	List<String> values = null;
        	try {
        		values = resolvedValues.getValues(key).getValues();
        	} catch (Exception e) {
        		e.printStackTrace();
        		fail(e.getMessage());
        	} 
        	
        	if (values.size() == 0) {
        		fail("Keys has no values");
        	}
        }
    }
	
	protected void assertKeyValues(IdentifierValues values, String[] keyNames) {
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
	
	protected void assertKeys(IdentifierValues values) {
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

	protected void assertResolvedValues(IdentifierValues values) {
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
    
	protected void assertEquals(String[] arr1, String[] arr2) {

        Arrays.sort(arr1);
        Arrays.sort(arr2);

        assertEquals(Arrays.asList(arr1), Arrays.asList(arr2));
    }
    
	protected URI getSystemIdentifier() {
    	return URI.create(this.NamingAuthority.getConfiguration().getPrefix().normalize().toString()
				+ SecurityUtil.LOCAL_SYSTEM_IDENTIFIER);
    }
}
