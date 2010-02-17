/**
 * UnaryPredicate.java
 * 
 * This file was auto-generated from WSDL by the Apache Axis 1.2RC2 Apr 28, 2006
 * (12:42:00 EDT) WSDL2Java emitter.
 */

package org.cagrid.cql2;

public class UnaryPredicate implements java.io.Serializable {
    private java.lang.String _value_;
    private static java.util.HashMap<String, UnaryPredicate> _table_ = new java.util.HashMap<String, UnaryPredicate>();

    public static final java.lang.String _IS_NULL = "IS_NULL";
    public static final java.lang.String _IS_NOT_NULL = "IS_NOT_NULL";
    public static final UnaryPredicate IS_NULL = new UnaryPredicate(_IS_NULL);
    public static final UnaryPredicate IS_NOT_NULL = new UnaryPredicate(_IS_NOT_NULL);


    private UnaryPredicate(java.lang.String value) {
        _value_ = value;
        _table_.put(_value_, this);
    }


    public java.lang.String getValue() {
        return _value_;
    }
    
    
    // for castor per http://www.castor.org/how-to-map-enums.html
    public static UnaryPredicate valueOf(String value) throws IllegalArgumentException {
        return fromValue(value);
    }


    public static UnaryPredicate fromValue(java.lang.String value) throws java.lang.IllegalArgumentException {
        UnaryPredicate enumeration = _table_.get(value);
        if (enumeration == null)
            throw new java.lang.IllegalArgumentException();
        return enumeration;
    }


    public static UnaryPredicate fromString(java.lang.String value) throws java.lang.IllegalArgumentException {
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
