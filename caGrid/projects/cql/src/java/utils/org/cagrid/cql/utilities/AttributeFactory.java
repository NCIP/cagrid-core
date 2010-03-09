package org.cagrid.cql.utilities;

import java.util.Date;

import org.cagrid.cql2.AttributeValue;
import org.cagrid.cql2.BinaryPredicate;
import org.cagrid.cql2.CQLAttribute;
import org.exolab.castor.types.Time;

public class AttributeFactory {

    private AttributeFactory() {
        
    }
    
    
    public static CQLAttribute createAttribute(String name, BinaryPredicate predicate, String value) {
        CQLAttribute attrib = new CQLAttribute();
        attrib.setName(name);
        attrib.setBinaryPredicate(predicate);
        AttributeValue val = new AttributeValue();
        val.setStringValue(value);
        attrib.setAttributeValue(val);
        return attrib;
    }
    
    
    public static CQLAttribute createAttribute(String name, BinaryPredicate predicate, Integer value) {
        CQLAttribute attrib = new CQLAttribute();
        attrib.setName(name);
        attrib.setBinaryPredicate(predicate);
        AttributeValue val = new AttributeValue();
        val.setIntegerValue(value);
        attrib.setAttributeValue(val);
        return attrib;
    }
    
    
    public static CQLAttribute createAttribute(String name, BinaryPredicate predicate, Long value) {
        CQLAttribute attrib = new CQLAttribute();
        attrib.setName(name);
        attrib.setBinaryPredicate(predicate);
        AttributeValue val = new AttributeValue();
        val.setLongValue(value);
        attrib.setAttributeValue(val);
        return attrib;
    }
    
    
    public static CQLAttribute createAttribute(String name, BinaryPredicate predicate, Double value) {
        CQLAttribute attrib = new CQLAttribute();
        attrib.setName(name);
        attrib.setBinaryPredicate(predicate);
        AttributeValue val = new AttributeValue();
        val.setDoubleValue(value);
        attrib.setAttributeValue(val);
        return attrib;
    }
    
    
    public static CQLAttribute createAttribute(String name, BinaryPredicate predicate, Boolean value) {
        CQLAttribute attrib = new CQLAttribute();
        attrib.setName(name);
        attrib.setBinaryPredicate(predicate);
        AttributeValue val = new AttributeValue();
        val.setBooleanValue(value);
        attrib.setAttributeValue(val);
        return attrib;
    }
    
    
    public static CQLAttribute createAttribute(String name, BinaryPredicate predicate, Date value) {
        CQLAttribute attrib = new CQLAttribute();
        attrib.setName(name);
        attrib.setBinaryPredicate(predicate);
        AttributeValue val = new AttributeValue();
        val.setDateValue(value);
        attrib.setAttributeValue(val);
        return attrib;
    }
    
    
    public static CQLAttribute createAttribute(String name, BinaryPredicate predicate, Time value) {
        CQLAttribute attrib = new CQLAttribute();
        attrib.setName(name);
        attrib.setBinaryPredicate(predicate);
        AttributeValue val = new AttributeValue();
        val.setTimeValue(value);
        attrib.setAttributeValue(val);
        return attrib;
    }
}
