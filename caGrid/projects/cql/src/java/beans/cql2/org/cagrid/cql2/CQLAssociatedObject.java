/**
 * CQLAssociatedObject.java
 * 
 * This file was auto-generated from WSDL by the Apache Axis 1.2RC2 Apr 28, 2006
 * (12:42:00 EDT) WSDL2Java emitter.
 */

package org.cagrid.cql2;

public class CQLAssociatedObject extends org.cagrid.cql2.CQLObject implements java.io.Serializable {
    private java.lang.String endName; // attribute


    public CQLAssociatedObject() {
    }


    public CQLAssociatedObject(java.lang.String endName) {
        this.endName = endName;
    }


    /**
     * Gets the endName value for this CQLAssociatedObject.
     * 
     * @return endName
     */
    public java.lang.String getEndName() {
        return endName;
    }


    /**
     * Sets the endName value for this CQLAssociatedObject.
     * 
     * @param endName
     */
    public void setEndName(java.lang.String endName) {
        this.endName = endName;
    }


    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof CQLAssociatedObject))
            return false;
        CQLAssociatedObject other = (CQLAssociatedObject) obj;
        if (obj == null)
            return false;
        if (this == obj)
            return true;
        boolean _equals;
        _equals = super.equals(obj)
            && ((this.endName == null && other.getEndName() == null) || (this.endName != null && this.endName
                .equals(other.getEndName())));
        return _equals;
    }


    public synchronized int hashCode() {

        int _hashCode = super.hashCode();
        if (getEndName() != null) {
            _hashCode += getEndName().hashCode();
        }
        return _hashCode;
    }
}
