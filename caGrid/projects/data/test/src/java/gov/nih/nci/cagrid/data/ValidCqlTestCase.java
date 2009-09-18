package gov.nih.nci.cagrid.data;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.data.cql.validation.CqlStructureValidator;
import gov.nih.nci.cagrid.data.cql.validation.ObjectWalkingCQLValidator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

/** 
 *  ValidCqlTestCase
 *  Tests CQL documents for validity with the Object Walking CQL Validator 
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * 
 * @created May 23, 2006 
 * @version $Id$ 
 */
public class ValidCqlTestCase extends TestCase {
	private CqlStructureValidator validator;
	private String cqlDocsDir;
	
	public ValidCqlTestCase(String name) {
		super(name);
		validator = new ObjectWalkingCQLValidator();
		cqlDocsDir = System.getProperty("cql.docs.dir");
	}
	
	
	private CQLQuery getQuery(String filename) {
		try {
			System.out.println("Validating structure of CQL: " + filename);
            FileReader reader = new FileReader(filename);
			CQLQuery query = (CQLQuery) Utils.deserializeObject(reader, CQLQuery.class);
            reader.close();
			return query;
		} catch (FileNotFoundException ex) {
			System.out.println("File not found: " + filename);
			fail(ex.getMessage());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	
	private void checkQuery(String filename) {
		CQLQuery query = getQuery(cqlDocsDir + File.separator + filename);
		try {
			validator.validateCqlStructure(query);
			assertTrue("Query is valid CQL", true);
		} catch (Exception ex) {
			System.out.println("Query is invalid: " + ex.getMessage());
			fail("Query found to be invalid: " + ex.getMessage());
		}
	}
	
	
	public void testReturnAllCql() {
		checkQuery("returnAllOfType.xml");
	}
	
	
	public void testObjectWithAttribute() {
		checkQuery("objectWithAttribute.xml");
	}
	
	
	public void testObjectWithAssociation() {
		checkQuery("objectWithAssociation.xml");
	}
	
	
	public void testAttributePredicates() {
		checkQuery("attributePredicates.xml");
	}
	
	
	public void testObjectWithAssociationNoRoleName() {
		checkQuery("objectWithAssociationNoRoleName.xml");
	}
	
	
	public void testObjectWithGroup() {
		checkQuery("objectWithGroup.xml");
	}
	
	
	public void testObjectWithNestedGroup() {
		checkQuery("objectWithNestedGroup.xml");
	}
	
	
	public void testCountAllOfType() {
		checkQuery("countAllOfType.xml");
	}
	
	
	public void testCountDistinctAttributes() {
		checkQuery("countDistinctAttributes.xml");
	}
	
	
	public void testReturnNamedAttributes() {
		checkQuery("returnNamedAttributes.xml");
	}

	
	public static void main(String[] args) {
		TestRunner runner = new TestRunner();
		TestResult result = runner.doRun(new TestSuite(ValidCqlTestCase.class));
		System.exit(result.errorCount() + result.failureCount());
	}
}
