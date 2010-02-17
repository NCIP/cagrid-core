/**
 * BinaryPredicate.java
 * 
 * This file was auto-generated from WSDL by the Apache Axis 1.2RC2 Apr 28, 2006
 * (12:42:00 EDT) WSDL2Java emitter.
 */

package org.cagrid.cql2;

public class BinaryPredicate implements java.io.Serializable {
    private java.lang.String _value_;
    private static java.util.HashMap<String, BinaryPredicate> _table_ = new java.util.HashMap<String, BinaryPredicate>();


    // Constructor
    protected BinaryPredicate(java.lang.String value) {
        _value_ = value;
        _table_.put(_value_, this);
    }

    public static final java.lang.String _EQUAL_TO = "EQUAL_TO";
    public static final java.lang.String _NOT_EQUAL_TO = "NOT_EQUAL_TO";
    public static final java.lang.String _LIKE = "LIKE";
    public static final java.lang.String _LESS_THAN = "LESS_THAN";
    public static final java.lang.String _LESS_THAN_EQUAL_TO = "LESS_THAN_EQUAL_TO";
    public static final java.lang.String _GREATER_THAN = "GREATER_THAN";
    public static final java.lang.String _GREATER_THAN_EQUAL_TO = "GREATER_THAN_EQUAL_TO";
    public static final BinaryPredicate EQUAL_TO = new BinaryPredicate(_EQUAL_TO);
    public static final BinaryPredicate NOT_EQUAL_TO = new BinaryPredicate(_NOT_EQUAL_TO);
    public static final BinaryPredicate LIKE = new BinaryPredicate(_LIKE);
    public static final BinaryPredicate LESS_THAN = new BinaryPredicate(_LESS_THAN);
    public static final BinaryPredicate LESS_THAN_EQUAL_TO = new BinaryPredicate(_LESS_THAN_EQUAL_TO);
    public static final BinaryPredicate GREATER_THAN = new BinaryPredicate(_GREATER_THAN);
    public static final BinaryPredicate GREATER_THAN_EQUAL_TO = new BinaryPredicate(_GREATER_THAN_EQUAL_TO);


    public java.lang.String getValue() {
        return _value_;
    }


    public static BinaryPredicate fromValue(java.lang.String value) throws java.lang.IllegalArgumentException {
        BinaryPredicate enumeration = _table_.get(value);
        if (enumeration == null)
            throw new java.lang.IllegalArgumentException();
        return enumeration;
    }


    public static BinaryPredicate fromString(java.lang.String value) throws java.lang.IllegalArgumentException {
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
