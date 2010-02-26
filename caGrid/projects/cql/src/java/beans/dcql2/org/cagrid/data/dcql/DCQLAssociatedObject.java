/**
 * DCQLAssociatedObject.java
 * 
 * This file was auto-generated from WSDL by the Apache Axis 1.2RC2 Apr 28, 2006
 * (12:42:00 EDT) WSDL2Java emitter.
 */

package org.cagrid.data.dcql;

/**
 * Describes named relationship from the parent object to another
 */
public class DCQLAssociatedObject extends org.cagrid.data.dcql.DCQLObject implements java.io.Serializable {
    private java.lang.String endName; // attribute


    public DCQLAssociatedObject() {
    }


    public DCQLAssociatedObject(java.lang.String endName) {
        this.endName = endName;
    }


    /**
     * Gets the endName value for this DCQLAssociatedObject.
     * 
     * @return endName
     */
    public java.lang.String getEndName() {
        return endName;
    }


    /**
     * Sets the endName value for this DCQLAssociatedObject.
     * 
     * @param endName
     */
    public void setEndName(java.lang.String endName) {
        this.endName = endName;
    }


    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof DCQLAssociatedObject))
            return false;
        DCQLAssociatedObject other = (DCQLAssociatedObject) obj;
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
