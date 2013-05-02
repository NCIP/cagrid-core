/**
*============================================================================
*  Copyright The Ohio State University Research Foundation, The University of Chicago - 
*	Argonne National Laboratory, Emory University, SemanticBits LLC, and 
*	Ekagra Software Technologies Ltd.
*
*  Distributed under the OSI-approved BSD 3-Clause License.
*  See http://ncip.github.com/cagrid-core/LICENSE.txt for details.
*============================================================================
**/
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

import org.cagrid.cql.utilities.AnyNodeHelper;
import org.cagrid.cql.utilities.DCQL2Constants;
import org.cagrid.cql.utilities.DCQL2SerializationUtil;
import org.cagrid.cql2.Aggregation;
import org.cagrid.cql2.AttributeValue;
import org.cagrid.cql2.BinaryPredicate;
import org.cagrid.cql2.CQLAttribute;
import org.cagrid.cql2.CQLQueryModifier;
import org.cagrid.cql2.DistinctAttribute;
import org.cagrid.cql2.GroupLogicalOperator;
import org.cagrid.cql2.NamedAttribute;
import org.cagrid.cql2.UnaryPredicate;
import org.cagrid.cql2.results.CQLAttributeResult;
import org.cagrid.cql2.results.CQLObjectResult;
import org.cagrid.cql2.results.CQLQueryResults;
import org.cagrid.cql2.results.TargetAttribute;
import org.cagrid.data.dcql.DCQLAssociatedObject;
import org.cagrid.data.dcql.DCQLGroup;
import org.cagrid.data.dcql.DCQLObject;
import org.cagrid.data.dcql.DCQLQuery;
import org.cagrid.data.dcql.ForeignAssociatedObject;
import org.cagrid.data.dcql.JoinCondition;
import org.cagrid.data.dcql.results.DCQLQueryResultsCollection;
import org.cagrid.data.dcql.results.DCQLResult;
import org.exolab.castor.types.AnyNode;

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
        // deserialize
        DCQLQuery deserializedQuery = null;
        try {
            deserializedQuery = DCQL2SerializationUtil.deserializeDcql2Query(text);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error deserializing serialized XML! " + ex.getMessage());
        }
        assertEquals("Deserialized query didn't match the input", query, deserializedQuery);
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
    
    
    public void testForeignAssociation() {
        DCQLQuery query = new DCQLQuery();
        DCQLObject target = new DCQLObject();
        target.setName("foo.bar");
        
        ForeignAssociatedObject fa = new ForeignAssociatedObject();
        fa.setName("foo.bar.foreign");
        fa.setTargetServiceURL("http://also-fake.com");
        JoinCondition join = new JoinCondition();
        join.setForeignAttributeName("id");
        join.setLocalAttributeName("id");
        join.setPredicate(BinaryPredicate.EQUAL_TO);
        fa.setJoinCondition(join);
        
        DCQLAssociatedObject assoc1 = new DCQLAssociatedObject();
        assoc1.setName("abc.def");
        CQLAttribute haveYouHeard = new CQLAttribute();
        haveYouHeard.setName("bird");
        haveYouHeard.setBinaryPredicate(BinaryPredicate.EQUAL_TO);
        AttributeValue value2 = new AttributeValue();
        value2.setStringValue("the word");
        haveYouHeard.setAttributeValue(value2);
        assoc1.setAttribute(haveYouHeard);
        fa.setAssociatedObject(assoc1);
        
        target.setForeignAssociatedObject(fa);
        query.setTargetObject(target);
        
        query.setTargetServiceURL(new String[] {"http://fake.com"});
        
        validate(query);
    }
    
    
    public void testQueryModifierCountOnly() {
        DCQLQuery query = new DCQLQuery();
        DCQLObject target = new DCQLObject();
        target.setName("foo.bar");
        target.set_instanceof("zor");
        
        CQLQueryModifier mods = new CQLQueryModifier();
        mods.setCountOnly(Boolean.TRUE);
        
        query.setTargetObject(target);
        query.setQueryModifier(mods);
        query.setTargetServiceURL(new String[] {"http://fake.com"});
        
        validate(query);
    }
    
    
    public void testQueryModifierDistinctAttributeWithAggregation() {
        DCQLQuery query = new DCQLQuery();
        DCQLObject target = new DCQLObject();
        target.setName("foo.bar");
        target.set_instanceof("zor");
        
        CQLQueryModifier mods = new CQLQueryModifier();
        DistinctAttribute da = new DistinctAttribute();
        da.setAttributeName("id");
        da.setAggregation(Aggregation.MAX);
        mods.setDistinctAttribute(da);
        
        query.setTargetObject(target);
        query.setQueryModifier(mods);
        query.setTargetServiceURL(new String[] {"http://fake.com"});
        
        validate(query);
    }
    
    
    public void testQueryModifierDistinctAttribute() {
        DCQLQuery query = new DCQLQuery();
        DCQLObject target = new DCQLObject();
        target.setName("foo.bar");
        target.set_instanceof("zor");
        
        CQLQueryModifier mods = new CQLQueryModifier();
        DistinctAttribute da = new DistinctAttribute();
        da.setAttributeName("id");
        mods.setDistinctAttribute(da);
        
        query.setTargetObject(target);
        query.setQueryModifier(mods);
        query.setTargetServiceURL(new String[] {"http://fake.com"});
        
        validate(query);
    }
    
    
    public void testQueryModifierNamedAttribute() {
        DCQLQuery query = new DCQLQuery();
        DCQLObject target = new DCQLObject();
        target.setName("foo.bar");
        target.set_instanceof("zor");
        
        CQLQueryModifier mods = new CQLQueryModifier();
        NamedAttribute na = new NamedAttribute();
        na.setAttributeName("id");
        mods.setNamedAttribute(new NamedAttribute[] {na});
        
        query.setTargetObject(target);
        query.setQueryModifier(mods);
        query.setTargetServiceURL(new String[] {"http://fake.com"});
        
        validate(query);
    }
    
    
    public void testQueryModifierMultipleNamedAttribute() {
        DCQLQuery query = new DCQLQuery();
        DCQLObject target = new DCQLObject();
        target.setName("foo.bar");
        target.set_instanceof("zor");
        
        CQLQueryModifier mods = new CQLQueryModifier();
        NamedAttribute[] nas = new NamedAttribute[4];
        for (int i = 0; i < nas.length; i++) {
            nas[i] = new NamedAttribute();
            nas[i].setAttributeName("att" + i);
        }
        mods.setNamedAttribute(nas);
        
        query.setTargetObject(target);
        query.setQueryModifier(mods);
        query.setTargetServiceURL(new String[] {"http://fake.com"});
        
        validate(query);
    }
    
    
    public void testEverything() {
        DCQLQuery query = new DCQLQuery();
        DCQLObject target = new DCQLObject();
        target.setName("gov.nih.nci.cabio.domain.NucleicAcidSequence");
        
        DCQLAssociatedObject assoc1 = new DCQLAssociatedObject();
        assoc1.setName("gov.nih.nci.cabio.domain.Gene");
        assoc1.setEndName("geneCollection");
        
        ForeignAssociatedObject fa = new ForeignAssociatedObject();
        fa.setName("edu.georgetown.pir.domain.Protein");
        fa.setTargetServiceURL("http://141.161.25.20:8080/wsrf/services/cagrid/GridPIR");
        JoinCondition join = new JoinCondition();
        join.setForeignAttributeName("uniprotkbEntryName");
        join.setLocalAttributeName("uniProtCode");
        join.setPredicate(BinaryPredicate.EQUAL_TO);
        fa.setJoinCondition(join);
        
        DCQLGroup group = new DCQLGroup();
        group.setLogicalOperation(GroupLogicalOperator.AND);
        
        DCQLAssociatedObject assoc2 = new DCQLAssociatedObject();
        assoc2.setName("edu.georgetown.pir.domain.Protein");
        assoc2.setEndName("geneCollection");
        CQLAttribute attr1 = new CQLAttribute();
        attr1.setName("name");
        attr1.setBinaryPredicate(BinaryPredicate.EQUAL_TO);
        AttributeValue val1 = new AttributeValue();
        val1.setStringValue("brca1");
        attr1.setAttributeValue(val1);
        assoc2.setAttribute(attr1);
        
        DCQLAssociatedObject assoc3 = new DCQLAssociatedObject();
        assoc3.setName("edu.georgetown.pir.domain.Organism");
        assoc3.setEndName("organismCollection");
        CQLAttribute attr2 = new CQLAttribute();
        attr2.setName("scientificName");
        attr2.setBinaryPredicate(BinaryPredicate.EQUAL_TO);
        AttributeValue val2 = new AttributeValue();
        val2.setStringValue("homo sapiens");
        attr2.setAttributeValue(val2);
        assoc3.setAttribute(attr2);
        
        group.setAssociatedObject(new DCQLAssociatedObject[] {assoc2, assoc3});
        
        fa.setGroup(group);
        
        assoc1.setForeignAssociatedObject(fa);
        
        target.setAssociatedObject(assoc1);
        
        query.setTargetObject(target);
        
        query.setTargetServiceURL(new String[] {"http://cabiogrid32.nci.nih.gov:80/wsrf/services/cagrid/CaBIO32GridSvc"});
        
        CQLQueryModifier mods = new CQLQueryModifier();
        DistinctAttribute da = new DistinctAttribute();
        da.setAttributeName("id");
        da.setAggregation(Aggregation.MAX);
        mods.setDistinctAttribute(da);
        
        query.setQueryModifier(mods);
        
        validate(query);
    }
    
    
    public void testObjectResult() {
        CQLQueryResults cqlResults = new CQLQueryResults();
        cqlResults.setTargetClassname("foo.bar");
        CQLObjectResult obj = new CQLObjectResult();
        AnyNode node = null;
        try {
            node = AnyNodeHelper.convertStringToAnyNode("<foo>here's some text</foo>");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Error creating node: " + e.getMessage());
        }
        obj.set_any(node);
        cqlResults.setObjectResult(new CQLObjectResult[] {obj});
        
        DCQLResult dcqlResult = new DCQLResult(cqlResults, "http://fake.com");
        
        DCQLQueryResultsCollection collection = new DCQLQueryResultsCollection(new DCQLResult[] {dcqlResult});
        
        validate(collection);
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
        
        DCQLResult dcqlResult = new DCQLResult(results, "http://fake.com");
        
        DCQLQueryResultsCollection collection = new DCQLQueryResultsCollection(new DCQLResult[] {dcqlResult});
        
        validate(collection);
    }
    

    public static void main(String args[]) {
        TestRunner runner = new TestRunner();
        TestResult result = runner.doRun(
            new TestSuite(DCQL2SerializationAndValidationTestCase.class));
        System.exit(result.errorCount() + result.failureCount());
    }

}
