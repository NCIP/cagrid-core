package gov.nih.nci.cagrid.data;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.data.cql.validation.CqlStructureValidator;
import gov.nih.nci.cagrid.data.cql.validation.JaxPCqlValidator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;

import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

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
public class JaxPInvalidCqlTestCase extends TestCase {
	private CqlStructureValidator validator;
	private String cqlDocsDir;
	
	public JaxPInvalidCqlTestCase(String name) {
		super(name);
		try {
			validator = new JaxPCqlValidator("ext/dependencies/xsd/1_gov.nih.nci.cagrid.CQLQuery.xsd");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		cqlDocsDir = System.getProperty("cql.docs.dir") + File.separator + "invalid";
	}	
	
	
	private void checkQuery(String filename) {
		try {
			File queryFile = new File(cqlDocsDir + File.separator + filename);
			System.out.println("Validating structure of CQL: " + queryFile.getCanonicalPath());
			FileInputStream configStream = new FileInputStream(new File("client-config.wsdd"));
			CQLQuery query = (CQLQuery) Utils.deserializeObject(
				new FileReader(queryFile), CQLQuery.class, configStream);
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
		TestResult result = runner.doRun(new TestSuite(JaxPInvalidCqlTestCase.class));
		System.exit(result.errorCount() + result.failureCount());
	}
}
