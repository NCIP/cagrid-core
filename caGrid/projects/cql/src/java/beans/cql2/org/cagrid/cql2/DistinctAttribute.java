/**
 * DistinctAttribute.java
 * 
 * This file was auto-generated from WSDL by the Apache Axis 1.2RC2 Apr 28, 2006
 * (12:42:00 EDT) WSDL2Java emitter.
 */

package org.cagrid.cql2;

public class DistinctAttribute extends org.cagrid.cql2.NamedAttribute implements java.io.Serializable {
    private org.cagrid.cql2.Aggregation aggregation; // attribute


    public DistinctAttribute() {
    }


    public DistinctAttribute(org.cagrid.cql2.Aggregation aggregation) {
        this.aggregation = aggregation;
    }


    /**
     * Gets the aggregation value for this DistinctAttribute.
     * 
     * @return aggregation
     */
    public org.cagrid.cql2.Aggregation getAggregation() {
        return aggregation;
    }


    /**
     * Sets the aggregation value for this DistinctAttribute.
     * 
     * @param aggregation
     */
    public void setAggregation(org.cagrid.cql2.Aggregation aggregation) {
        this.aggregation = aggregation;
    }


    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof DistinctAttribute))
            return false;
        DistinctAttribute other = (DistinctAttribute) obj;
        if (obj == null)
            return false;
        if (this == obj)
            return true;
        boolean _equals;
        _equals = super.equals(obj)
            && ((this.aggregation == null && other.getAggregation() == null) || (this.aggregation != null && this.aggregation
                .equals(other.getAggregation())));
        return _equals;
    }


    public synchronized int hashCode() {
        int _hashCode = super.hashCode();
        if (getAggregation() != null) {
            _hashCode += getAggregation().hashCode();
        }
        return _hashCode;
    }
}
