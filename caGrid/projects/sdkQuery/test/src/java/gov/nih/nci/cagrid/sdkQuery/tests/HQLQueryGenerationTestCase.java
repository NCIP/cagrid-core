package gov.nih.nci.cagrid.sdkQuery.tests;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.data.cql.cacore.CQL2HQL;
import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

/** 
 *  HQLQueryGenerationTestCase
 *  Test case for generating HQL queries from CQL documents
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * 
 * @created Jul 31, 2006 
 * @version $Id$ 
 */
public class HQLQueryGenerationTestCase extends TestCase {
    
    public static final String TEST_QUERIES_DIR="ext/dependencies/test/xml/";
	
	public HQLQueryGenerationTestCase(String name) {
		super(name);
	}
	
	
	private CQLQuery deserializeQuery(String filename) {
		CQLQuery query = null;
		try {
			query = (CQLQuery) Utils.deserializeDocument(filename, CQLQuery.class);
		} catch (Exception ex) {
			ex.printStackTrace();
			fail("Error deserializing CQL query: " + ex.getMessage());
		}
		return query;
	}
	
	
	private void translateQuery(String filename) {
		CQLQuery query = deserializeQuery(filename);
		String criteria = null;
		try {
			criteria = CQL2HQL.translate(query, false, false);
			System.out.println(criteria);
		} catch (Exception ex) {
			ex.printStackTrace();
			fail("Error translating query: " + ex.getMessage());
		}
	}
	
	
	public void testReturnAllQuery() {
		translateQuery(TEST_QUERIES_DIR + "returnAllOfType.xml");
	}
	
	
	public void testAttributePredicates() {
		translateQuery(TEST_QUERIES_DIR + "attributePredicates.xml");
	}
	
	
	public void testObjectWithAssociation() {
		translateQuery(TEST_QUERIES_DIR + "objectWithAssociation.xml");
	}
	
	
	public void testObjectWithAssociationNoRoleName() {
		translateQuery(TEST_QUERIES_DIR + "objectWithAssociationNoRoleName.xml");
	}
	
	
	public void testObjectWithAttribute() {
		translateQuery(TEST_QUERIES_DIR + "objectWithAttribute.xml");
	}
	
	
	public void testObjectWithGroup() {
		translateQuery(TEST_QUERIES_DIR + "objectWithGroup.xml");
	}
	
	
	public void testObjectWithNestedGroup() {
		translateQuery(TEST_QUERIES_DIR + "objectWithNestedGroup.xml");
	}
	

	public static void main(String[] args) {
		TestRunner runner = new TestRunner();
		TestResult result = runner.doRun(
			new TestSuite(HQLQueryGenerationTestCase.class));
		System.exit(result.errorCount() + result.failureCount());
	}
}
