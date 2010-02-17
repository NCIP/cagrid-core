/**
 * CQLAttributeResult.java
 * 
 * This file was auto-generated from WSDL by the Apache Axis 1.2RC2 Apr 28, 2006
 * (12:42:00 EDT) WSDL2Java emitter.
 */

package org.cagrid.cql2.results;

/**
 * Result attribute
 */
public class CQLAttributeResult extends org.cagrid.cql2.results.CQLResult implements java.io.Serializable {
    private org.cagrid.cql2.results.TargetAttribute[] attribute;


    public CQLAttributeResult() {
    }


    public CQLAttributeResult(org.cagrid.cql2.results.TargetAttribute[] attribute) {
        this.attribute = attribute;
    }


    /**
     * Gets the attribute value for this CQLAttributeResult.
     * 
     * @return attribute
     */
    public org.cagrid.cql2.results.TargetAttribute[] getAttribute() {
        return attribute;
    }


    /**
     * Sets the attribute value for this CQLAttributeResult.
     * 
     * @param attribute
     */
    public void setAttribute(org.cagrid.cql2.results.TargetAttribute[] attribute) {
        this.attribute = attribute;
    }


    public org.cagrid.cql2.results.TargetAttribute getAttribute(int i) {
        return this.attribute[i];
    }


    public void setAttribute(int i, org.cagrid.cql2.results.TargetAttribute _value) {
        this.attribute[i] = _value;
    }


    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof CQLAttributeResult))
            return false;
        CQLAttributeResult other = (CQLAttributeResult) obj;
        if (obj == null)
            return false;
        if (this == obj)
            return true;
        boolean _equals;
        _equals = super.equals(obj)
            && ((this.attribute == null && other.getAttribute() == null) || (this.attribute != null && java.util.Arrays
                .equals(this.attribute, other.getAttribute())));
        return _equals;
    }


    public synchronized int hashCode() {
        int _hashCode = super.hashCode();
        if (getAttribute() != null) {
            for (int i = 0; i < java.lang.reflect.Array.getLength(getAttribute()); i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getAttribute(), i);
                if (obj != null && !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        return _hashCode;
    }
}
