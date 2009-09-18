package gov.nih.nci.cagrid.data;

import gov.nih.nci.cagrid.common.SchemaValidationException;
import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.cqlresultset.CQLQueryResults;
import gov.nih.nci.cagrid.data.utilities.validation.CQLQueryResultsValidator;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;

import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

/** 
 *  ResultsValidationTestCase
 *  Testing for CQL Query Results validation
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * 
 * @created Oct 20, 2006 
 * @version $Id$ 
 */
public class ResultsValidationTestCase extends TestCase {
	
	public ResultsValidationTestCase(String name) {
		super(name);
	}
	
	
	private CQLQueryResults getResults(String filename) {
		try {
			InputStream clientConfigStream = new FileInputStream("test/resources/cabio-client-config.wsdd");
			FileReader reader = new FileReader(filename);
			return (CQLQueryResults) Utils.deserializeObject(reader, CQLQueryResults.class, clientConfigStream);
		} catch (Exception ex) {
			ex.printStackTrace();
			fail("Error deserializing CQL Results document " + filename + ": " + ex.getMessage());
		}
		// unreachable, but the compiler doesn't know that
		return null;
	}
	
	
	private void validateResults(CQLQueryResults results) throws SchemaValidationException {
		// EndpointReferenceType epr = AddressingUtils.createEndpointReference("http://localhost:8080/wsrf/services/cagrid/CabioDataService", null);
		// CQLQueryResultsValidator validator = new CQLQueryResultsValidator(epr);
		CQLQueryResultsValidator validator = new CQLQueryResultsValidator("test/resources/CabioDataService.wsdl");
		
		validator.validateCQLResultSet(results);
	}
	
	
	public void testValidGeneQueryResults() {
		CQLQueryResults results = getResults("test/resources/geneQueryResults.xml");
		try {
			validateResults(results);
			assertTrue("Results were valid", true);
		} catch (SchemaValidationException ex) {
			fail("Results were not valid: " + ex.getMessage());
		} catch (Exception ex) {
			ex.printStackTrace();
			fail("Other exception occured: " + ex.getMessage());
		}
	}
	
	
	public void testInvalidChromosomeResults() {
		CQLQueryResults results = getResults("test/resources/chromosomeQueryResults.xml");
		try {
			validateResults(results);
			fail("Results should have been invalid, but passed validation");
		} catch (SchemaValidationException ex) {
			assertTrue("Results were not valid: " + ex.getMessage(), true);
		} catch (Exception ex) {
			ex.printStackTrace();
			fail("Other exception occured: " + ex.getMessage());
		}
	}
	

	public static void main(String[] args) {
		TestRunner runner = new TestRunner();
		TestResult result = runner.doRun(new TestSuite(ResultsValidationTestCase.class));
		System.exit(result.errorCount() + result.failureCount());
	}
}