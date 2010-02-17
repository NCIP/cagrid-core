/**
 * Aggregation.java
 * 
 * This file was auto-generated from WSDL by the Apache Axis 1.2RC2 Apr 28, 2006
 * (12:42:00 EDT) WSDL2Java emitter.
 */

package org.cagrid.cql2;

public class Aggregation implements java.io.Serializable {
    private java.lang.String _value_;
    private static java.util.HashMap<String, Aggregation> _table_ = new java.util.HashMap<String, Aggregation>();

    public static final java.lang.String _COUNT = "COUNT";
    public static final java.lang.String _MIN = "MIN";
    public static final java.lang.String _MAX = "MAX";
    public static final Aggregation COUNT = new Aggregation(_COUNT);
    public static final Aggregation MIN = new Aggregation(_MIN);
    public static final Aggregation MAX = new Aggregation(_MAX);


    private Aggregation(java.lang.String value) {
        _value_ = value;
        _table_.put(_value_, this);
    }


    public java.lang.String getValue() {
        return _value_;
    }
    
    
    // for castor per http://www.castor.org/how-to-map-enums.html
    public static Aggregation valueOf(String value) throws IllegalArgumentException {
        return fromValue(value);
    }


    public static Aggregation fromValue(java.lang.String value) throws java.lang.IllegalArgumentException {
        Aggregation enumeration = _table_.get(value);
        if (enumeration == null)
            throw new java.lang.IllegalArgumentException();
        return enumeration;
    }


    public static Aggregation fromString(java.lang.String value) throws java.lang.IllegalArgumentException {
        return fromValue(value);
    }


    public boolean equals(java.lang.Object obj) {
        return (obj == this);
    }


    public int hashCode() {
        return toString().hashCode();
    }


    public java.lang.String toString() {
        return _value_;
    }
}
