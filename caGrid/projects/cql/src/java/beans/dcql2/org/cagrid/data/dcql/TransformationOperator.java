/**
 * TransformationOperator.java
 * 
 * This file was auto-generated from WSDL by the Apache Axis 1.2RC2 Apr 28, 2006
 * (12:42:00 EDT) WSDL2Java emitter.
 */

package org.cagrid.data.dcql;

public class TransformationOperator implements java.io.Serializable {
    private java.lang.String _value_;
    private static java.util.HashMap _table_ = new java.util.HashMap();


    // Constructor
    protected TransformationOperator(java.lang.String value) {
        _value_ = value;
        _table_.put(_value_, this);
    }

    public static final java.lang.String _ADD = "ADD";
    public static final java.lang.String _SUBTRACT = "SUBTRACT";
    public static final java.lang.String _MULTIPLY = "MULTIPLY";
    public static final java.lang.String _DIVIDE = "DIVIDE";
    public static final java.lang.String _ABS = "ABS";
    public static final java.lang.String _APPEND = "APPEND";
    public static final java.lang.String _PREPEND = "PREPEND";
    public static final java.lang.String _STRLENGTH = "STRLENGTH";
    public static final java.lang.String _UPPER = "UPPER";
    public static final java.lang.String _LOWER = "LOWER";
    public static final TransformationOperator ADD = new TransformationOperator(_ADD);
    public static final TransformationOperator SUBTRACT = new TransformationOperator(_SUBTRACT);
    public static final TransformationOperator MULTIPLY = new TransformationOperator(_MULTIPLY);
    public static final TransformationOperator DIVIDE = new TransformationOperator(_DIVIDE);
    public static final TransformationOperator ABS = new TransformationOperator(_ABS);
    public static final TransformationOperator APPEND = new TransformationOperator(_APPEND);
    public static final TransformationOperator PREPEND = new TransformationOperator(_PREPEND);
    public static final TransformationOperator STRLENGTH = new TransformationOperator(_STRLENGTH);
    public static final TransformationOperator UPPER = new TransformationOperator(_UPPER);
    public static final TransformationOperator LOWER = new TransformationOperator(_LOWER);


    public java.lang.String getValue() {
        return _value_;
    }
    
    
    public static TransformationOperator valueOf(String value) {
        return fromString(value);
    }


    public static TransformationOperator fromValue(java.lang.String value) throws java.lang.IllegalArgumentException {
        TransformationOperator enumeration = (TransformationOperator) _table_.get(value);
        if (enumeration == null)
            throw new java.lang.IllegalArgumentException();
        return enumeration;
    }


    public static TransformationOperator fromString(java.lang.String value) throws java.lang.IllegalArgumentException {
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
