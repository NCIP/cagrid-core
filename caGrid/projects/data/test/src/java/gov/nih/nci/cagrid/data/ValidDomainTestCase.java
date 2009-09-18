package gov.nih.nci.cagrid.data;

import gov.nih.nci.cagrid.common.SchemaValidator;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.data.cql.validation.CqlDomainValidator;
import gov.nih.nci.cagrid.data.cql.validation.DomainModelValidator;
import gov.nih.nci.cagrid.metadata.MetadataUtils;
import gov.nih.nci.cagrid.metadata.dataservice.DomainModel;

import java.io.File;
import java.io.FileReader;

import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.globus.wsrf.encoding.ObjectDeserializer;
import org.xml.sax.InputSource;

/** 
 *  ValidDomainTestCase
 *  Test case to verify the validity of CQL queries against the domain model
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * 
 * @created May 26, 2006 
 * @version $Id$ 
 */
public class ValidDomainTestCase extends TestCase {
	public static final String DOMAIN_MODEL_FILE = "test/resources/domainModel.xml";
	public static final String DOMAIN_MODEL_XSD = "ext/dependencies/xsd/cagrid/types/data/data.xsd";
	
	private CqlDomainValidator validator = null;
	private DomainModel domainModel = null;
	private String cqlDocsDir = null;
	
	public ValidDomainTestCase(String name) {
		super(name);
		validator = new DomainModelValidator();
		cqlDocsDir = System.getProperty("cql.docs.dir");
	}
	
	
	private CQLQuery getQuery(String filename) {
		try {
			System.out.println("Validating CQL " + filename + " against domain model");
			InputSource queryInput = new InputSource(new FileReader(filename));
			CQLQuery query = (CQLQuery) ObjectDeserializer.deserialize(queryInput, CQLQuery.class);
			return query;
		} catch (Exception ex) {
			ex.printStackTrace();
			fail(ex.getMessage());
		}
		return null;
	}
	
	
	private DomainModel getDomainModel() {
		if (domainModel == null) {
			try {
				FileReader domainModelReader = new FileReader(DOMAIN_MODEL_FILE);
				domainModel = MetadataUtils.deserializeDomainModel(domainModelReader);
				domainModelReader.close();
			} catch (Exception ex) {
				ex.printStackTrace();
				fail("Could not load domain model");
			}
		}
		return domainModel;
	}
	
	
	private void checkQuery(String filename) {
		CQLQuery query = getQuery(cqlDocsDir + File.separator + "domain" + File.separator + filename);
		try {
			validator.validateDomainModel(query, getDomainModel());
			System.out.println("The query appears valid");
			assertTrue("The query appears valid", true);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("Query is invalid: " + ex.getMessage());
			fail("Query found to be invalid: " + ex.getMessage());
		}
	}
	
	
	public void testValidObjectWithAttribute() {
		checkQuery("objectWithAttribute.xml");
	}
	
	
	public void testValidGroupWithAttributes() {
		checkQuery("groupWithValidAttributes.xml");
	}
	
	
	public void testValidObjectWithAssociation() {
		checkQuery("objectWithAssociation.xml");
	}
	
	
	/*
	 * This test has been disabled because caDSR returns multiple associations with the
	 * SAME source, target, role names, etc, but different ID values (a bug in caCORE, probably),
	 * which causes them to appear as different associations in the domain model.  This makes
	 * validation with no role name impossible because there will ALWAYS be more than one association
	 * found, which is ambiguous.  
	 
	public void testValidObjectWithAssociationNoRoleName() {
		checkQuery("objectWithAssociationNoRoleName.xml");
	}
	*/
	
	
	public void testDomainModelConformsToSchema() {
		try {
			SchemaValidator.validate(DOMAIN_MODEL_XSD, new File(DOMAIN_MODEL_FILE));
			assertTrue(DOMAIN_MODEL_FILE + " appears valid against schema " + DOMAIN_MODEL_XSD, true);
		} catch (Exception ex) {
			ex.printStackTrace();
			fail(DOMAIN_MODEL_FILE + " not valid against schema " + DOMAIN_MODEL_XSD + "\n\t" + ex.getMessage());
		}
	}
	
	
	public static void main(String[] args) {
		TestRunner runner = new TestRunner();
		TestResult result = runner.doRun(new TestSuite(ValidDomainTestCase.class));
		System.exit(result.errorCount() + result.failureCount());
	}
}
