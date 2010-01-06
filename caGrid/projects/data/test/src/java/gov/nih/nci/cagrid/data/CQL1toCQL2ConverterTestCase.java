package gov.nih.nci.cagrid.data;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.data.cql2.validation.Cql2DomainValidator;
import gov.nih.nci.cagrid.data.cql2.validation.Cql2StructureValidator;
import gov.nih.nci.cagrid.data.cql2.validation.DomainModelCql2DomainValidator;
import gov.nih.nci.cagrid.data.cql2.validation.DomainValidationException;
import gov.nih.nci.cagrid.data.cql2.validation.ObjectWalkingCql2StructureValidator;
import gov.nih.nci.cagrid.data.cql2.validation.StructureValidationException;
import gov.nih.nci.cagrid.metadata.MetadataUtils;
import gov.nih.nci.cagrid.metadata.dataservice.DomainModel;

import java.io.File;
import java.io.FileReader;

import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.cagrid.cql.utilities.CQL1toCQL2Converter;
import org.cagrid.cql.utilities.QueryConversionException;

public class CQL1toCQL2ConverterTestCase extends TestCase {
    
    public static final String DOMAIN_MODEL_FILE = "test/resources/domainModel.xml";
    
    private CQL1toCQL2Converter converter = null;
    private Cql2DomainValidator domainValidator = null;
    private Cql2StructureValidator structureValidator = null;
    private String cqlDocsDir = null;
    
    public CQL1toCQL2ConverterTestCase() {
        super("CQL1 to CQL2 Converter Test");
        this.cqlDocsDir = System.getProperty("cql.docs.dir");
    }
    
    
    public void setUp() {
        DomainModel model = null;
        try {
            FileReader reader = new FileReader(DOMAIN_MODEL_FILE);
            model = MetadataUtils.deserializeDomainModel(reader);
            reader.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error deserializing domain model: " + ex.getMessage());
        }
        converter = new CQL1toCQL2Converter(model);
        domainValidator = new DomainModelCql2DomainValidator(model);
        structureValidator = new ObjectWalkingCql2StructureValidator();
    }
    
    
    private CQLQuery loadCqlQuery(String filename) {
        CQLQuery query = null;
        try {
            System.out.println("Loading CQL 1 query from " + filename);
            FileReader reader = new FileReader(cqlDocsDir + File.separator + "domain" + File.separator + filename);
            query = Utils.deserializeObject(reader, CQLQuery.class);
            reader.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error loading CQL query: " + ex.getMessage());
        }
        return query;
    }
    
    
    private void convertAndValidate(String queryFilename) {
        CQLQuery query = loadCqlQuery(queryFilename);
        org.cagrid.cql2.CQLQuery cql2Query = null;
        try {
            cql2Query = converter.convertToCql2Query(query);
        } catch (QueryConversionException ex) {
            ex.printStackTrace();
            fail("Error converting CQL1 to CQL2 query: " + ex.getMessage());
        }
        try {
            domainValidator.validateAgainstDomainModel(cql2Query);
        } catch (DomainValidationException ex) {
            ex.printStackTrace();
            fail("Error validating CQL 2 query against domain model: " + ex.getMessage());
        }
        try {
            structureValidator.validateQuerySyntax(cql2Query);
        } catch (StructureValidationException ex) {
            ex.printStackTrace();
            fail("Error validating CQL 2 query syntax: " + ex.getMessage());
        }
    }
    
    
    public void testValidObjectWithAttribute() {
        convertAndValidate("objectWithAttribute.xml");
    }
    
    
    public void testValidGroupWithAttributes() {
        convertAndValidate("groupWithValidAttributes.xml");
    }
    
    
    public void testValidObjectWithAssociation() {
        convertAndValidate("objectWithAssociation.xml");
    }
    
    
    public void testValidObjectWithAssociationNoRoleName() {
        convertAndValidate("objectWithAssociationNoRoleName.xml");
    }
    

    public static void main(String[] args) {
        TestRunner runner = new TestRunner();
        TestResult result = runner.doRun(new TestSuite(InvalidCqlTestCase.class));
        System.exit(result.errorCount() + result.failureCount());
    }
}
