package org.cagrid.cql.utilities;

import java.util.Calendar;
import java.util.Date;

import org.cagrid.cql2.AttributeValue;
import org.cagrid.cql2.BinaryPredicate;
import org.cagrid.cql2.CQLAttribute;

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
        // date or time?
        Calendar cal = Calendar.getInstance();
        cal.setTime(value);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        if (year == 0 || month == 0 || day == 0) {
            // time, since there's no date associated with it
            val.setTimeValue(value);
        } else {
            // must be date
            val.setDateValue(value);
        }
        attrib.setAttributeValue(val);
        return attrib;
    }
}
