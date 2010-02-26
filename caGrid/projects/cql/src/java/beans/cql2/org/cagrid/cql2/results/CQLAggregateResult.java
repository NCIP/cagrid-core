/**
 * CQLAggregateResult.java
 * 
 * This file was auto-generated from WSDL by the Apache Axis 1.2RC2 Apr 28, 2006
 * (12:42:00 EDT) WSDL2Java emitter.
 */

package org.cagrid.cql2.results;

/**
 * An aggregation result (count, min, max, etc)
 */
public class CQLAggregateResult extends org.cagrid.cql2.results.CQLResult implements java.io.Serializable {
    private java.lang.String attributeName; // attribute
    private java.lang.String value; // attribute
    private org.cagrid.cql2.Aggregation aggregation; // attribute


    public CQLAggregateResult() {
    }


    public CQLAggregateResult(org.cagrid.cql2.Aggregation aggregation, java.lang.String attributeName,
        java.lang.String value) {
        this.attributeName = attributeName;
        this.value = value;
        this.aggregation = aggregation;
    }


    /**
     * Gets the attributeName value for this CQLAggregateResult.
     * 
     * @return attributeName
     */
    public java.lang.String getAttributeName() {
        return attributeName;
    }


    /**
     * Sets the attributeName value for this CQLAggregateResult.
     * 
     * @param attributeName
     */
    public void setAttributeName(java.lang.String attributeName) {
        this.attributeName = attributeName;
    }


    /**
     * Gets the value value for this CQLAggregateResult.
     * 
     * @return value
     */
    public java.lang.String getValue() {
        return value;
    }


    /**
     * Sets the value value for this CQLAggregateResult.
     * 
     * @param value
     */
    public void setValue(java.lang.String value) {
        this.value = value;
    }


    /**
     * Gets the aggregation value for this CQLAggregateResult.
     * 
     * @return aggregation
     */
    public org.cagrid.cql2.Aggregation getAggregation() {
        return aggregation;
    }


    /**
     * Sets the aggregation value for this CQLAggregateResult.
     * 
     * @param aggregation
     */
    public void setAggregation(org.cagrid.cql2.Aggregation aggregation) {
        this.aggregation = aggregation;
    }


    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof CQLAggregateResult))
            return false;
        CQLAggregateResult other = (CQLAggregateResult) obj;
        if (obj == null)
            return false;
        if (this == obj)
            return true;
        boolean _equals;
        _equals = super.equals(obj)
            && ((this.attributeName == null && other.getAttributeName() == null) || (this.attributeName != null && this.attributeName
                .equals(other.getAttributeName())))
            && ((this.value == null && other.getValue() == null) || (this.value != null && this.value.equals(other
                .getValue())))
            && ((this.aggregation == null && other.getAggregation() == null) || (this.aggregation != null && this.aggregation
                .equals(other.getAggregation())));
        return _equals;
    }


    public synchronized int hashCode() {
        int _hashCode = super.hashCode();
        if (getAttributeName() != null) {
            _hashCode += getAttributeName().hashCode();
        }
        if (getValue() != null) {
            _hashCode += getValue().hashCode();
        }
        if (getAggregation() != null) {
            _hashCode += getAggregation().hashCode();
        }
        return _hashCode;
    }
}
