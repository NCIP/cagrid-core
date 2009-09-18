package gov.nih.nci.cagrid.core;

import org.xml.sax.SAXException;

import gov.nih.nci.cagrid.common.SchemaValidationException;
import gov.nih.nci.cagrid.common.SchemaValidator;
import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

/** 
 *  SchemaValidatorTestCase
 *  Runs some basic smoke tests of the schema validator utility
 * 
 * @author David Ervin
 * 
 * @created Mar 18, 2008 9:41:23 AM
 * @version $Id: SchemaValidatorTestCase.java,v 1.1 2008-03-18 14:23:50 dervin Exp $ 
 */
public class SchemaValidatorTestCase extends TestCase {
    
    
    public void testVerifyValidSchema() {
        try {
            SchemaValidator.verify("test/resources/schemas/validSchema.xsd");
        } catch (SchemaValidationException ex) {
            ex.printStackTrace();
            fail("Schema should have been valid: " + ex.getMessage());
        }
    }
    
    
    public void testVerifyValidSchemaWithImport() {
        try {
            SchemaValidator.verify("test/resources/schemas/validImportSchema1.xsd");
        } catch (SchemaValidationException ex) {
            ex.printStackTrace();
            fail("Schema should have been valid: " + ex.getMessage());
        }
    }
    
    
    public void testVerifyInvalidSchema() {
        try {
            SchemaValidator.verify("test/resources/schemas/invalidSchema.xsd");
        } catch (SchemaValidationException ex) {
            // expected
            assertTrue("Unexpected failure cause", ex.getCause() instanceof SAXException);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Unexpected exception thrown by validation: " + ex.getMessage());
        }
    }
    
    
    public void testVerifySchemaWithInvalidImport() {
        try {
            SchemaValidator.verify("test/resources/schemas/invalidImportSchema1.xsd");
            fail("Schema validation should have failed!");
        } catch (SchemaValidationException ex) {
            // expected
            assertTrue("Unexpected failure cause", ex.getCause() instanceof SAXException);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Unexpected exception thrown by validation: " + ex.getMessage());
        }
    }
    

    public static void main(String[] args) {
        TestRunner runner = new TestRunner();
        TestResult result = runner.doRun(new TestSuite(SchemaValidatorTestCase.class));
        System.exit(result.errorCount() + result.failureCount());
    }
}
