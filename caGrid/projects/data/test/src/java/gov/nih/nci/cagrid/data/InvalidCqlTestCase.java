package gov.nih.nci.cagrid.data;

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

import org.globus.wsrf.encoding.ObjectDeserializer;
import org.xml.sax.InputSource;

/** 
 *  InvalidCqlTestCase
 *  Negative testing for CQL validation.  All documents loaded here
 *  should fail the validator.
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * 
 * @created May 23, 2006 
 * @version $Id$ 
 */
public class InvalidCqlTestCase extends TestCase {
	private CqlStructureValidator validator;
	private String cqlDocsDir;
	
	public InvalidCqlTestCase(String name) {
		super(name);
		validator = new ObjectWalkingCQLValidator();
		cqlDocsDir = System.getProperty("cql.docs.dir") + File.separator + "invalid";
	}
	
	
	private CQLQuery getQuery(String filename) {
		try {
			System.out.println("Validating structure of CQL: " + filename);
			InputSource queryInput = new InputSource(new FileReader(filename));
			CQLQuery query = (CQLQuery) ObjectDeserializer.deserialize(queryInput, CQLQuery.class);
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
			fail("Query should have been invalid, was not");
		} catch (MalformedQueryException ex) {
			System.out.println("Query verified invalid: " + ex.getMessage());
			assertTrue("Query verified invalid: " + ex.getMessage(), true);
		} catch (Exception ex) {
			ex.printStackTrace();
			fail("Caught some other exception: " + ex.getMessage());
		}
	}
	
	
	public void testTargetWithoutName() {
		checkQuery("targetWithoutName.xml");
	}
	
	
	public void testAttributeWithoutValue() {
		checkQuery("attributeWithoutValue.xml");
	}
	
	
	public void testAttributeWithoutName() {
		checkQuery("attributeWithoutName.xml");
	}
	
	
	public void testMultipleChildrenOnTarget() {
		checkQuery("tooManyTargetChildren.xml");
	}
	
	
	public void testOneChildOnGroup() {
		checkQuery("oneGroupChild.xml");
	}
	
	
	public void testNoChildrenOnGroup() {
		checkQuery("noGroupChildren.xml");
	}
	
	
	public void testGroupWithoutLogic() {
		checkQuery("groupWithoutLogic.xml");
	}
	
	
	public void testAssociationWithoutName() {
		checkQuery("associationWithoutName.xml");
	}
	
	
	public void testMultipleChildrenOnAssociation() {
		checkQuery("associationWithTooManyChildren.xml");
	}
	
	
	/*
	 * Can't run this since the QueryModifier object will always have a default
	 * value for the count flag
	public void testModifierMissingCount() {
		checkQuery("modifierMissingCount.xml");
	}
	*/
	
	
	public void testModifierWithBothAttributeTypes() {
		checkQuery("modifierWithBothAttributeTypes.xml");
	}
	
	
	public static void main(String[] args) {
		TestRunner runner = new TestRunner();
		TestResult result = runner.doRun(new TestSuite(InvalidCqlTestCase.class));
		System.exit(result.errorCount() + result.failureCount());
	}
}
