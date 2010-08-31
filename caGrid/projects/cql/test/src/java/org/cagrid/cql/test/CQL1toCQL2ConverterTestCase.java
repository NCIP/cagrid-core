package org.cagrid.cql.test;

import gov.nih.nci.cagrid.cqlquery.Association;
import gov.nih.nci.cagrid.cqlquery.Attribute;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.cqlquery.Object;
import gov.nih.nci.cagrid.cqlquery.Predicate;
import gov.nih.nci.cagrid.metadata.MetadataUtils;
import gov.nih.nci.cagrid.metadata.dataservice.DomainModel;

import java.io.FileReader;

import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.cagrid.cql.utilities.CQL1toCQL2Converter;
import org.cagrid.cql.utilities.QueryConversionException;
import org.cagrid.cql2.BinaryPredicate;

public class CQL1toCQL2ConverterTestCase extends TestCase {
    
    public static final String EXAMPLE_DOMAIN_MODEL = "test/resources/isoExample_domainModel.xml";
    
    private CQL1toCQL2Converter converter = null;
    
    public CQL1toCQL2ConverterTestCase(String name) {
        super(name);
    }
    
    
    public void setUp() {
        try {
            FileReader reader = new FileReader(EXAMPLE_DOMAIN_MODEL);
            DomainModel model = MetadataUtils.deserializeDomainModel(reader);
            reader.close();
            converter = new CQL1toCQL2Converter(model);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error setting up query converter: " + ex.getMessage());
        }
    }
    
    
    public void testBnNonNullQueryConversion() {
        CQLQuery query = new CQLQuery();
        gov.nih.nci.cagrid.cqlquery.Object target = new gov.nih.nci.cagrid.cqlquery.Object();
        target.setName("gov.nih.nci.cacoresdk.domain.other.datatype.BlNonNullDataType");
        Association association1 = new Association();
        association1.setName("gov.nih.nci.iso21090.BlNonNull");
        association1.setRoleName("value1");
        target.setAssociation(association1);
        Attribute attribute2 = new Attribute();
        attribute2.setName("value");
        attribute2.setPredicate(Predicate.EQUAL_TO);
        attribute2.setValue("false");
        association1.setAttribute(attribute2);
        query.setTarget(target);
        
        translateQuery(query);
    }
    
    
    public void testCdNullFlavorNiConversion() {
        CQLQuery query = new CQLQuery();
        Object target = new Object();
        target.setName("gov.nih.nci.cacoresdk.domain.other.datatype.CdDataType");
        Association assoc = new Association();
        assoc.setName("gov.nih.nci.iso21090.Cd");
        assoc.setRoleName("value3");
        Attribute attrib = new Attribute("nullFlavor", Predicate.EQUAL_TO, "NA");
        assoc.setAttribute(attrib);
        target.setAssociation(assoc);
        query.setTarget(target);
        
        translateQuery(query);
    }
    
    
    public void testConvertAttributeWithNullPredicate() {
        CQLQuery query = new CQLQuery();
        Object target = new Object();
        target.setName("gov.nih.nci.cacoresdk.domain.other.primarykey.FloatPrimitiveKey");
        Attribute attr = new Attribute("name", null, "123");
        target.setAttribute(attr);
        query.setTarget(target);
        
        org.cagrid.cql2.CQLQuery cql2 = translateQuery(query);
        assertEquals("Predicate did not get defaulted to EQUAL_TO!", 
            cql2.getCQLTargetObject().getCQLAttribute().getBinaryPredicate(), 
            BinaryPredicate.EQUAL_TO);
    }
    
    
    private org.cagrid.cql2.CQLQuery translateQuery(CQLQuery query) {
        org.cagrid.cql2.CQLQuery cql2 = null;
        try {
            cql2 = converter.convertToCql2Query(query);
        } catch (QueryConversionException ex) {
            ex.printStackTrace();
            fail("Error converting query: " + ex.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Unknown error converting query: " + ex.getMessage());
        }
        return cql2;
    }
    

    public static void main(String[] args) {
        TestRunner runner = new TestRunner();
        TestResult result = runner.doRun(new TestSuite(CQL1toCQL2ConverterTestCase.class));
        System.exit(result.errorCount() + result.failureCount());
    }
}
