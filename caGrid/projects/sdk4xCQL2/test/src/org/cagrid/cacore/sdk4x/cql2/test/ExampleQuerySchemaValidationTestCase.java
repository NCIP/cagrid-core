package org.cagrid.cacore.sdk4x.cql2.test;

import gov.nih.nci.cagrid.common.SchemaValidationException;
import gov.nih.nci.cagrid.common.SchemaValidator;

import java.io.File;
import java.io.FileFilter;

import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

public class ExampleQuerySchemaValidationTestCase extends TestCase {
    
    public static final String EXAMPLE_DOCS_LOCATION = "test/docs/cql2Examples";
    public static final String INVALID_DOCS_LOCATION = EXAMPLE_DOCS_LOCATION + "/invalid";
    public static final String CQL2_SCHEMA_LOCATION = "ext/dependencies/test/xsds/CQLQueryComponents.xsd";
    
    private SchemaValidator schemaValidator = null;
    
    public ExampleQuerySchemaValidationTestCase(String name) {
        super(name);
    }
    
    
    public void setUp() {
        try {
            schemaValidator = new SchemaValidator(CQL2_SCHEMA_LOCATION);
        } catch (SchemaValidationException e) {
            e.printStackTrace();
            fail("Error setting up schema validator: " + e.getMessage());
        }
    }
    
    
    public void testValidQueries() {
        File queriesDir = new File(EXAMPLE_DOCS_LOCATION);
        File[] queries = queriesDir.listFiles(new FileFilter() {
            public boolean accept(File pathname) {
                return pathname.getName().endsWith(".xml");
            }
        });
        assertNotNull("There should have been some valid queries to test", queries);
        assertTrue("There should have been some valid queries to test", queries.length != 0);
        for (File doc : queries) {
            System.out.println("Validating " + doc);
            try {
                schemaValidator.validate(doc);
            } catch (SchemaValidationException ex) {
                ex.printStackTrace();
                fail(doc.getName() + " should have been valid: " + ex.getMessage());
            }
        }
    }
    
    
    public void testInvalidQueries() {
        File queriesDir = new File(INVALID_DOCS_LOCATION);
        File[] queries = queriesDir.listFiles(new FileFilter() {
            public boolean accept(File pathname) {
                return pathname.getName().endsWith(".xml");
            }
        });
        assertNotNull("There should have been some invalid queries to test", queries);
        assertTrue("There should have been some invalid queries to test", queries.length != 0);
        for (File doc : queries) {
            System.out.println("Validating " + doc);
            try {
                schemaValidator.validate(doc);
                fail(doc.getName() + " should have been invalid, but passed");
            } catch (SchemaValidationException ex) {
                // expected
                System.out.println("As expected, " + doc.getName() + " was invalid: " + ex.getMessage());
            }
        }
    }
    

    public static void main(String[] args) {
        TestRunner runner = new TestRunner();
        TestResult result = runner.doRun(new TestSuite(ExampleQuerySchemaValidationTestCase.class));
        System.exit(result.errorCount() + result.failureCount());
    }
}
