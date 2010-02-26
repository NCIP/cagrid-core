/**
 * DataTransformationOperation.java
 * 
 * This file was auto-generated from WSDL by the Apache Axis 1.2RC2 Apr 28, 2006
 * (12:42:00 EDT) WSDL2Java emitter.
 */

package org.cagrid.data.dcql;

public class DataTransformationOperation implements java.io.Serializable {
    private org.cagrid.data.dcql.TransformationOperator operator; // attribute
    private java.lang.String value; // attribute


    public DataTransformationOperation() {
    }


    public DataTransformationOperation(org.cagrid.data.dcql.TransformationOperator operator, java.lang.String value) {
        this.operator = operator;
        this.value = value;
    }


    /**
     * Gets the operator value for this DataTransformationOperation.
     * 
     * @return operator
     */
    public org.cagrid.data.dcql.TransformationOperator getOperator() {
        return operator;
    }


    /**
     * Sets the operator value for this DataTransformationOperation.
     * 
     * @param operator
     */
    public void setOperator(org.cagrid.data.dcql.TransformationOperator operator) {
        this.operator = operator;
    }


    /**
     * Gets the value value for this DataTransformationOperation.
     * 
     * @return value
     */
    public java.lang.String getValue() {
        return value;
    }


    /**
     * Sets the value value for this DataTransformationOperation.
     * 
     * @param value
     */
    public void setValue(java.lang.String value) {
        this.value = value;
    }


    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof DataTransformationOperation))
            return false;
        DataTransformationOperation other = (DataTransformationOperation) obj;
        if (obj == null)
            return false;
        if (this == obj)
            return true;
        boolean _equals;
        _equals = true
            && ((this.operator == null && other.getOperator() == null) || (this.operator != null && this.operator
                .equals(other.getOperator())))
            && ((this.value == null && other.getValue() == null) || (this.value != null && this.value.equals(other
                .getValue())));
        return _equals;
    }


    public synchronized int hashCode() {
        int _hashCode = 1;
        if (getOperator() != null) {
            _hashCode += getOperator().hashCode();
        }
        if (getValue() != null) {
            _hashCode += getValue().hashCode();
        }
        return _hashCode;
    }
}
