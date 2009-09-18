package gov.nih.nci.cagrid.fqp;

import gov.nih.nci.cagrid.common.SchemaValidationException;
import gov.nih.nci.cagrid.common.SchemaValidator;

import java.io.File;

import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.textui.TestRunner;


/**
 * 
 * @author oster
 * 
 */
public class SchemaValidationTestCase extends TestCase {
	public static final String QUERIES_DIR = "test" + File.separator + "resources" + File.separator;
	public static final String DCQL_XSD = "schema" + File.separator + "FederatedQueryProcessor" + File.separator
		+ "Distributed_CQL_schema_2.0.xsd";


	public SchemaValidationTestCase(String name) {
		super(name);
	}


	public void testQuery1() {
		assertFileValid(QUERIES_DIR + "exampleQuery1.xml");
	}


	public void testQuery2() {
		assertFileValid(QUERIES_DIR + "exampleQuery2.xml");
	}
	
	
	public void testQuery2NoPredicate() {
	    assertFileValid(QUERIES_DIR + "exampleQuery2_NoPredicate.xml");
	}


	public void assertFileValid(String file) {
		try {
			SchemaValidator.validate(DCQL_XSD, new File(file));
			assertTrue(file + " appears valid against schema " + DCQL_XSD, true);
		} catch (SchemaValidationException ex) {
			ex.printStackTrace();
			fail(file + " not valid against schema " + DCQL_XSD + "\n\t" + ex.getMessage());
		}
	}


	public static void main(String[] args) {
		TestRunner runner = new TestRunner();
		TestResult result = runner.doRun(new TestSuite(SchemaValidationTestCase.class));
		System.exit(result.errorCount() + result.failureCount());
	}
}
