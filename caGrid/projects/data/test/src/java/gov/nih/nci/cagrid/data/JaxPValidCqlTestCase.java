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
 *  ValidCqlTestCase
 *  Tests CQL documents for validity with the Object Walking CQL Validator 
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * 
 * @created May 23, 2006 
 * @version $Id$ 
 */
public class JaxPValidCqlTestCase extends TestCase {
	private CqlStructureValidator validator;
	private String cqlDocsDir;
	
	public JaxPValidCqlTestCase(String name) {
		super(name);
		try {
			validator = new JaxPCqlValidator("ext/dependencies/xsd/1_gov.nih.nci.cagrid.CQLQuery.xsd");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		cqlDocsDir = System.getProperty("cql.docs.dir");	
	}	
	
	
	private void checkQuery(String filename) {
		try {
			File queryFile = new File(cqlDocsDir + File.separator + filename);
			System.out.println("Validating structure of CQL: " + queryFile.getCanonicalPath());
			FileInputStream configStream = new FileInputStream(new File("client-config.wsdd"));
			CQLQuery query = (CQLQuery) Utils.deserializeObject(
				new FileReader(queryFile), CQLQuery.class, configStream);
			validator.validateCqlStructure(query);
			assertTrue("Query is valid CQL", true);
		} catch (Exception ex) {
			ex.printStackTrace();
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
		TestResult result = runner.doRun(new TestSuite(JaxPValidCqlTestCase.class));
		System.exit(result.errorCount() + result.failureCount());
	}
}
