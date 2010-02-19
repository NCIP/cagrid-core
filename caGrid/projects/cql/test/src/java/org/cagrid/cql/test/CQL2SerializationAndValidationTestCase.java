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

import org.cagrid.cql.utilities.CQLConstants;
import org.cagrid.cql2.AttributeValue;
import org.cagrid.cql2.BinaryPredicate;
import org.cagrid.cql2.CQLAssociatedObject;
import org.cagrid.cql2.CQLAttribute;
import org.cagrid.cql2.CQLGroup;
import org.cagrid.cql2.CQLQuery;
import org.cagrid.cql2.CQLTargetObject;
import org.cagrid.cql2.GroupLogicalOperator;
import org.cagrid.cql2.UnaryPredicate;

public class CQL2SerializationAndValidationTestCase extends TestCase {
    
    private SchemaValidator validator = null;
    private InputStream wsddStream = null;
    
    public CQL2SerializationAndValidationTestCase(String name) {
        super(name);
    }
    
    
    public void setUp() {
        File cql2Xsd = new File("schema/cql2.0/CQLQueryComponents.xsd");
        assertTrue(cql2Xsd.exists());
        try {
            String path = cql2Xsd.getCanonicalPath();
            validator = new SchemaValidator(path);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error setting up schema validator: " + ex.getMessage());
        }
        wsddStream = getClass().getResourceAsStream("/org/cagrid/cql2/mapping/client-config.wsdd");
        assertNotNull("Could not load CQL 2 client config", wsddStream);
    }
    
    
    public void tearDown() {
        try {
            wsddStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error closing client config input stream");
        }
    }
    
    
    private void validate(CQLQuery query) {
        // serialize
        StringWriter writer = new StringWriter();
        try {
            Utils.serializeObject(query, CQLConstants.CQL2_QUERY_QNAME, writer, wsddStream);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error serializing CQL 2 query: " + ex.getMessage());
        }
        String text = writer.getBuffer().toString();
        // validate
        try {
            validator.validate(text);
        } catch (SchemaValidationException ex) {
            ex.printStackTrace();
            System.err.println(text);
            fail("Error validating serialized CQL 2 query: " + ex.getMessage());
        }
    }
    
    
    public void testTargetOnly() {
        CQLQuery query = new CQLQuery();
        CQLTargetObject target = new CQLTargetObject();
        target.setClassName("foo.bar");
        target.set_instanceof("zor");
        query.setCQLTargetObject(target);
        
        validate(query);
    }
    
    
    public void testTargetWithBinaryPredicateAttribute() {
        CQLQuery query = new CQLQuery();
        CQLTargetObject target = new CQLTargetObject();
        target.setClassName("foo.bar");
        target.set_instanceof("zor");
        CQLAttribute attribute = new CQLAttribute();
        attribute.setName("word");
        attribute.setBinaryPredicate(BinaryPredicate.EQUAL_TO);
        AttributeValue value = new AttributeValue();
        value.setStringValue("hello");
        attribute.setAttributeValue(value);
        target.setCQLAttribute(attribute);
        query.setCQLTargetObject(target);
        
        validate(query);
    }
    
    
    public void testGroupOfAttributes() {
        CQLQuery query = new CQLQuery();
        CQLTargetObject target = new CQLTargetObject();
        target.setClassName("foo.bar");
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
        
        CQLGroup group = new CQLGroup();
        group.setLogicalOperation(GroupLogicalOperator.AND);
        group.setCQLAttribute(new CQLAttribute[] {a1, a2});
        
        target.setCQLGroup(group);
        query.setCQLTargetObject(target);
        
        validate(query);
    }
    
    
    public void testAssociation() {
        CQLQuery query = new CQLQuery();
        CQLTargetObject target = new CQLTargetObject();
        target.setClassName("foo.bar");
        target.set_instanceof("zor");
        CQLAssociatedObject assoc = new CQLAssociatedObject();
        assoc.setClassName("abc.def");
        CQLAttribute haveYouHeard = new CQLAttribute();
        haveYouHeard.setName("bird");
        haveYouHeard.setBinaryPredicate(BinaryPredicate.EQUAL_TO);
        AttributeValue value2 = new AttributeValue();
        value2.setStringValue("the word");
        haveYouHeard.setAttributeValue(value2);
        assoc.setCQLAttribute(haveYouHeard);
        target.setCQLAssociatedObject(assoc);
        query.setCQLTargetObject(target);
        
        validate(query);
    }
    
    
    public void testNestedAssociations() {
        CQLQuery query = new CQLQuery();
        CQLTargetObject target = new CQLTargetObject();
        target.setClassName("foo.bar");
        target.set_instanceof("zor");
        
        CQLAssociatedObject assoc = new CQLAssociatedObject();
        assoc.setClassName("abc.def");
        
        CQLAssociatedObject nested = new CQLAssociatedObject();
        nested.setClassName("lol.idk");
        
        assoc.setCQLAssociatedObject(nested);
        target.setCQLAssociatedObject(assoc);
        query.setCQLTargetObject(target);
        
        validate(query);
    }
    
    
    public void testGroupedAssociations() {
        CQLQuery query = new CQLQuery();
        CQLTargetObject target = new CQLTargetObject();
        target.setClassName("foo.bar");
        target.set_instanceof("zor");
        
        CQLAssociatedObject assoc1 = new CQLAssociatedObject();
        assoc1.setClassName("abc.def");
        CQLAttribute haveYouHeard = new CQLAttribute();
        haveYouHeard.setName("bird");
        haveYouHeard.setBinaryPredicate(BinaryPredicate.EQUAL_TO);
        AttributeValue value2 = new AttributeValue();
        value2.setStringValue("the word");
        haveYouHeard.setAttributeValue(value2);
        assoc1.setCQLAttribute(haveYouHeard);
        
        CQLAssociatedObject assoc2 = new CQLAssociatedObject();
        assoc2.setClassName("xyz.abc");
        
        CQLGroup group = new CQLGroup();
        group.setLogicalOperation(GroupLogicalOperator.AND);
        group.setCQLAssociatedObject(new CQLAssociatedObject[] {assoc1, assoc2});
        
        target.setCQLGroup(group);
        query.setCQLTargetObject(target);
        
        validate(query);
    }
    
    
    public void testNestedGroups() {
        CQLQuery query = new CQLQuery();
        CQLTargetObject target = new CQLTargetObject();
        target.setClassName("foo.bar");
        target.set_instanceof("zor");
        
        CQLAssociatedObject assoc1 = new CQLAssociatedObject();
        assoc1.setClassName("abc.def");
        CQLAttribute haveYouHeard = new CQLAttribute();
        haveYouHeard.setName("bird");
        haveYouHeard.setBinaryPredicate(BinaryPredicate.EQUAL_TO);
        AttributeValue value2 = new AttributeValue();
        value2.setStringValue("the word");
        haveYouHeard.setAttributeValue(value2);
        assoc1.setCQLAttribute(haveYouHeard);
        
        CQLAssociatedObject assoc2 = new CQLAssociatedObject();
        assoc2.setClassName("xyz.abc");
        
        CQLGroup nestedGroup = new CQLGroup();
        nestedGroup.setLogicalOperation(GroupLogicalOperator.OR);
        CQLAttribute a1 = new CQLAttribute();
        a1.setName("nested1");
        a1.setUnaryPredicate(UnaryPredicate.IS_NOT_NULL);
        CQLAttribute a2 = new CQLAttribute();
        a2.setName("nested2");
        a2.setUnaryPredicate(UnaryPredicate.IS_NULL);
        nestedGroup.setCQLAttribute(new CQLAttribute[] {a1, a2});
        
        CQLGroup group = new CQLGroup();
        group.setLogicalOperation(GroupLogicalOperator.AND);
        group.setCQLAssociatedObject(new CQLAssociatedObject[] {assoc1, assoc2});
        group.setCQLGroup(new CQLGroup[] {nestedGroup});
        
        target.setCQLGroup(group);
        query.setCQLTargetObject(target);
        
        validate(query);
    }
    

    public static void main(String args[]) {
        TestRunner runner = new TestRunner();
        TestResult result = runner.doRun(
            new TestSuite(CQL2SerializationAndValidationTestCase.class));
        System.exit(result.errorCount() + result.failureCount());
    }

}
