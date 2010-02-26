package org.cagrid.cql.test;

import gov.nih.nci.cagrid.common.SchemaValidationException;
import gov.nih.nci.cagrid.common.SchemaValidator;
import gov.nih.nci.cagrid.common.Utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.cagrid.cql.utilities.DCQL2Constants;
import org.cagrid.cql.utilities.DCQL2SerializationUtil;
import org.cagrid.cql2.AttributeValue;
import org.cagrid.cql2.BinaryPredicate;
import org.cagrid.cql2.CQLAttribute;
import org.cagrid.cql2.GroupLogicalOperator;
import org.cagrid.cql2.UnaryPredicate;
import org.cagrid.data.dcql.DCQLAssociatedObject;
import org.cagrid.data.dcql.DCQLGroup;
import org.cagrid.data.dcql.DCQLObject;
import org.cagrid.data.dcql.DCQLQuery;
import org.cagrid.data.dcql.results.DCQLQueryResultsCollection;

public class DCQL2SerializationAndValidationTestCase extends TestCase {
    
    private SchemaValidator queryValidator = null;
    private SchemaValidator resultsValidator = null;
    private InputStream wsddStream = null;
    
    public DCQL2SerializationAndValidationTestCase(String name) {
        super(name);
    }
    
    
    public void setUp() {
        File dcql2Xsd = new File("schema/dcql2.0/DCQL_2.0.xsd");
        File resultsXsd = new File("schema/dcql2.0/DCQLResults_2.0.xsd");
        assertTrue(dcql2Xsd.exists());
        assertTrue(resultsXsd.exists());
        try {
            String queryPath = dcql2Xsd.getCanonicalPath();
            queryValidator = new SchemaValidator(queryPath);
            String resultsPath = resultsXsd.getAbsolutePath();
            resultsValidator = new SchemaValidator(resultsPath);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error setting up schema validator: " + ex.getMessage());
        }
        wsddStream = getClass().getResourceAsStream(DCQL2SerializationUtil.CLIENT_CONFIG_LOCATION);
        assertNotNull("Could not load DCQL 2 client config", wsddStream);
    }
    
    
    public void tearDown() {
        try {
            wsddStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error closing client config input stream");
        }
    }
    
    
    private void validate(DCQLQuery query) {
        // serialize
        StringWriter writer = new StringWriter();
        try {
            Utils.serializeObject(query, DCQL2Constants.DCQL2_QUERY_QNAME, writer, wsddStream);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error serializing DCQL 2 query: " + ex.getMessage());
        }
        String text = writer.getBuffer().toString();
        // validate
        try {
            queryValidator.validate(text);
        } catch (SchemaValidationException ex) {
            ex.printStackTrace();
            System.err.println(text);
            fail("Error validating serialized DCQL 2 query: " + ex.getMessage());
        }
    }
    
    
    private void validate(DCQLQueryResultsCollection results) {
        // serialize
        StringWriter writer = new StringWriter();
        try {
            Utils.serializeObject(results, DCQL2Constants.DCQL2_RESULTS_QNAME, writer, wsddStream);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error serializing DCQL 2 results: " + ex.getMessage());
        }
        String text = writer.getBuffer().toString();
        // validate
        try {
            resultsValidator.validate(text);
        } catch (SchemaValidationException ex) {
            ex.printStackTrace();
            System.err.println(text);
            fail("Error validating serialized DCQL 2 results: " + ex.getMessage());
        }
    }
    
    
    public void testTargetOnly() {
        DCQLQuery query = new DCQLQuery();
        DCQLObject target = new DCQLObject();
        target.setName("foo.bar");
        query.setTargetObject(target);
        query.setTargetServiceURL(new String[] {"http://fake.com"});
        
        validate(query);
    }
    
    
    public void testTargetWithBinaryPredicateAttribute() {
        DCQLQuery query = new DCQLQuery();
        DCQLObject target = new DCQLObject();
        target.setName("foo.bar");
        target.set_instanceof("zor");
        query.setTargetObject(target);
        
        CQLAttribute attribute = new CQLAttribute();
        attribute.setName("word");
        attribute.setBinaryPredicate(BinaryPredicate.EQUAL_TO);
        AttributeValue value = new AttributeValue();
        value.setStringValue("hello");
        attribute.setAttributeValue(value);
        target.setAttribute(attribute);
        
        query.setTargetObject(target);
        
        query.setTargetServiceURL(new String[] {"http://fake.com"});
        
        validate(query);
    }
    
    
    public void testGroupOfAttributes() {
        DCQLQuery query = new DCQLQuery();
        DCQLObject target = new DCQLObject();
        target.setName("foo.bar");
        target.set_instanceof("zor");
        
        CQLAttribute a1 = new CQLAttribute();
        a1.setName("alpha");
        a1.setBinaryPredicate(BinaryPredicate.LESS_THAN_EQUAL_TO);
        AttributeValue value1 = new AttributeValue();
        value1.setStringValue("beta");
        a1.setAttributeValue(value1);
        
        CQLAttribute a2 = new CQLAttribute();
        a2.setName("bird");
        a2.setBinaryPredicate(BinaryPredicate.EQUAL_TO);
        AttributeValue value2 = new AttributeValue();
        value2.setStringValue("the word");
        a2.setAttributeValue(value2);
        
        DCQLGroup group = new DCQLGroup();
        group.setLogicalOperation(GroupLogicalOperator.AND);
        group.setAttribute(new CQLAttribute[] {a1, a2});
        target.setGroup(group);
        
        query.setTargetObject(target);
        query.setTargetServiceURL(new String[] {"http://fake.com"});
        
        validate(query);
    }
    
    
    public void testAssociation() {
        DCQLQuery query = new DCQLQuery();
        DCQLObject target = new DCQLObject();
        target.setName("foo.bar");
        target.set_instanceof("zor");
        
        DCQLAssociatedObject assoc = new DCQLAssociatedObject();
        assoc.setName("abc.def");
        CQLAttribute haveYouHeard = new CQLAttribute();
        haveYouHeard.setName("bird");
        haveYouHeard.setBinaryPredicate(BinaryPredicate.EQUAL_TO);
        AttributeValue value2 = new AttributeValue();
        value2.setStringValue("the word");
        haveYouHeard.setAttributeValue(value2);
        assoc.setAttribute(haveYouHeard);
        
        target.setAssociatedObject(assoc);
        
        query.setTargetObject(target);
        query.setTargetServiceURL(new String[] {"http://fake.com"});
        
        validate(query);
    }
    
    
    public void testNestedAssociations() {
        DCQLQuery query = new DCQLQuery();
        DCQLObject target = new DCQLObject();
        target.setName("foo.bar");
        target.set_instanceof("zor");
        
        DCQLAssociatedObject assoc = new DCQLAssociatedObject();
        assoc.setName("abc.def");
        
        DCQLAssociatedObject nested = new DCQLAssociatedObject();
        nested.setName("lol.idk");
        
        assoc.setAssociatedObject(nested);
        target.setAssociatedObject(assoc);
        query.setTargetObject(target);
        
        query.setTargetServiceURL(new String[] {"http://fake.com"});
        
        validate(query);
    }
    
    
    public void testGroupedAssociations() {
        DCQLQuery query = new DCQLQuery();
        DCQLObject target = new DCQLObject();
        target.setName("foo.bar");
        target.set_instanceof("zor");
        
        DCQLAssociatedObject assoc1 = new DCQLAssociatedObject();
        assoc1.setName("abc.def");
        CQLAttribute haveYouHeard = new CQLAttribute();
        haveYouHeard.setName("bird");
        haveYouHeard.setBinaryPredicate(BinaryPredicate.EQUAL_TO);
        AttributeValue value2 = new AttributeValue();
        value2.setStringValue("the word");
        haveYouHeard.setAttributeValue(value2);
        assoc1.setAttribute(haveYouHeard);
        
        DCQLAssociatedObject assoc2 = new DCQLAssociatedObject();
        assoc2.setName("xyz.abc");
        
        DCQLGroup group = new DCQLGroup();
        group.setLogicalOperation(GroupLogicalOperator.AND);
        group.setAssociatedObject(new DCQLAssociatedObject[] {assoc1, assoc2});
        
        target.setGroup(group);
        query.setTargetObject(target);
        
        query.setTargetServiceURL(new String[] {"http://fake.com"});
        
        validate(query);
    }
    
    
    public void testNestedGroups() {
        DCQLQuery query = new DCQLQuery();
        DCQLObject target = new DCQLObject();
        target.setName("foo.bar");
        target.set_instanceof("zor");
        
        DCQLAssociatedObject assoc1 = new DCQLAssociatedObject();
        assoc1.setName("abc.def");
        CQLAttribute haveYouHeard = new CQLAttribute();
        haveYouHeard.setName("bird");
        haveYouHeard.setBinaryPredicate(BinaryPredicate.EQUAL_TO);
        AttributeValue value2 = new AttributeValue();
        value2.setStringValue("the word");
        haveYouHeard.setAttributeValue(value2);
        assoc1.setAttribute(haveYouHeard);
        
        DCQLAssociatedObject assoc2 = new DCQLAssociatedObject();
        assoc2.setName("xyz.abc");
        
        DCQLGroup nestedGroup = new DCQLGroup();
        nestedGroup.setLogicalOperation(GroupLogicalOperator.OR);
        CQLAttribute a1 = new CQLAttribute();
        a1.setName("nested1");
        a1.setUnaryPredicate(UnaryPredicate.IS_NOT_NULL);
        CQLAttribute a2 = new CQLAttribute();
        a2.setName("nested2");
        a2.setUnaryPredicate(UnaryPredicate.IS_NULL);
        nestedGroup.setAttribute(new CQLAttribute[] {a1, a2});
        
        DCQLGroup group = new DCQLGroup();
        group.setLogicalOperation(GroupLogicalOperator.AND);
        group.setAssociatedObject(new DCQLAssociatedObject[] {assoc1, assoc2});
        group.setGroup(new DCQLGroup[] {nestedGroup});
        
        target.setGroup(group);
        query.setTargetObject(target);
        query.setTargetServiceURL(new String[] {"http://fake.com"});
        
        validate(query);
    }
    
    
    /*
    public void testAggregationResult() {
        CQLQueryResults results = new CQLQueryResults();
        results.setTargetClassname("foo.bar");
        CQLAggregateResult agg = new CQLAggregateResult();
        agg.setAggregation(Aggregation.COUNT);
        agg.setAttributeName("id");
        agg.setValue("5");
        results.setAggregationResult(agg);
        
        validate(results);
    }
    
    
    public void testObjectResult() {
        CQLQueryResults results = new CQLQueryResults();
        results.setTargetClassname("foo.bar");
        CQLObjectResult obj = new CQLObjectResult();
        AnyNode node = null;
        try {
            node = AnyNodeHelper.convertStringToAnyNode("<foo/>");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Error creating node: " + e.getMessage());
        }
        obj.set_any(node);
        results.setObjectResult(new CQLObjectResult[] {obj});
        
        validate(results);
    }
    
    
    public void testAttributeResults() {
        CQLQueryResults results = new CQLQueryResults();
        results.setTargetClassname("foo.bar");
        CQLAttributeResult[] attribResults = new CQLAttributeResult[5];
        for (int i = 0; i < attribResults.length; i++) {
            CQLAttributeResult attributeResult = new CQLAttributeResult();
            TargetAttribute[] tas = new TargetAttribute[4];
            for (int j = 0; j < tas.length; j++) {
                tas[j] = new TargetAttribute("Name " + i + ", " + j, "Name " + i + ", " + j);
            }
            attributeResult.setAttribute(tas);
            attribResults[i] = attributeResult;
        }
        results.setAttributeResult(attribResults);
        
        validate(results);
    }
    */
    

    public static void main(String args[]) {
        TestRunner runner = new TestRunner();
        TestResult result = runner.doRun(
            new TestSuite(DCQL2SerializationAndValidationTestCase.class));
        System.exit(result.errorCount() + result.failureCount());
    }

}
