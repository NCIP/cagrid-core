/**
 * CQLObject.java
 * 
 * This file was auto-generated from WSDL by the Apache Axis 1.2RC2 Apr 28, 2006
 * (12:42:00 EDT) WSDL2Java emitter.
 */

package org.cagrid.cql2;

public abstract class CQLObject implements java.io.Serializable {
    private org.cagrid.cql2.CQLAssociatedObject CQLAssociatedObject;
    private org.cagrid.cql2.CQLGroup CQLGroup;
    private org.cagrid.cql2.CQLAttribute CQLAttribute;
    private org.cagrid.cql2.CQLExtension CQLExtension;
    private java.lang.String className; // attribute
    private java.lang.String _instanceof; // attribute


    public CQLObject() {
    }


    public CQLObject(org.cagrid.cql2.CQLAssociatedObject CQLAssociatedObject,
        org.cagrid.cql2.CQLAttribute CQLAttribute, org.cagrid.cql2.CQLExtension CQLExtension,
        org.cagrid.cql2.CQLGroup CQLGroup, java.lang.String _instanceof, java.lang.String className) {
        this.CQLAssociatedObject = CQLAssociatedObject;
        this.CQLGroup = CQLGroup;
        this.CQLAttribute = CQLAttribute;
        this.CQLExtension = CQLExtension;
        this.className = className;
        this._instanceof = _instanceof;
    }


    /**
     * Gets the CQLAssociatedObject value for this CQLObject.
     * 
     * @return CQLAssociatedObject
     */
    public org.cagrid.cql2.CQLAssociatedObject getCQLAssociatedObject() {
        return CQLAssociatedObject;
    }


    /**
     * Sets the CQLAssociatedObject value for this CQLObject.
     * 
     * @param CQLAssociatedObject
     */
    public void setCQLAssociatedObject(org.cagrid.cql2.CQLAssociatedObject CQLAssociatedObject) {
        this.CQLAssociatedObject = CQLAssociatedObject;
    }


    /**
     * Gets the CQLGroup value for this CQLObject.
     * 
     * @return CQLGroup
     */
    public org.cagrid.cql2.CQLGroup getCQLGroup() {
        return CQLGroup;
    }


    /**
     * Sets the CQLGroup value for this CQLObject.
     * 
     * @param CQLGroup
     */
    public void setCQLGroup(org.cagrid.cql2.CQLGroup CQLGroup) {
        this.CQLGroup = CQLGroup;
    }


    /**
     * Gets the CQLAttribute value for this CQLObject.
     * 
     * @return CQLAttribute
     */
    public org.cagrid.cql2.CQLAttribute getCQLAttribute() {
        return CQLAttribute;
    }


    /**
     * Sets the CQLAttribute value for this CQLObject.
     * 
     * @param CQLAttribute
     */
    public void setCQLAttribute(org.cagrid.cql2.CQLAttribute CQLAttribute) {
        this.CQLAttribute = CQLAttribute;
    }


    /**
     * Gets the CQLExtension value for this CQLObject.
     * 
     * @return CQLExtension
     */
    public org.cagrid.cql2.CQLExtension getCQLExtension() {
        return CQLExtension;
    }


    /**
     * Sets the CQLExtension value for this CQLObject.
     * 
     * @param CQLExtension
     */
    public void setCQLExtension(org.cagrid.cql2.CQLExtension CQLExtension) {
        this.CQLExtension = CQLExtension;
    }


    /**
     * Gets the className value for this CQLObject.
     * 
     * @return className
     */
    public java.lang.String getClassName() {
        return className;
    }


    /**
     * Sets the className value for this CQLObject.
     * 
     * @param className
     */
    public void setClassName(java.lang.String className) {
        this.className = className;
    }


    /**
     * Gets the _instanceof value for this CQLObject.
     * 
     * @return _instanceof
     */
    public java.lang.String get_instanceof() {
        return _instanceof;
    }


    /**
     * Sets the _instanceof value for this CQLObject.
     * 
     * @param _instanceof
     */
    public void set_instanceof(java.lang.String _instanceof) {
        this._instanceof = _instanceof;
    }


    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof CQLObject))
            return false;
        CQLObject other = (CQLObject) obj;
        if (obj == null)
            return false;
        if (this == obj)
            return true;
        boolean _equals;
        _equals = true
            && ((this.CQLAssociatedObject == null && other.getCQLAssociatedObject() == null) || (this.CQLAssociatedObject != null && this.CQLAssociatedObject
                .equals(other.getCQLAssociatedObject())))
            && ((this.CQLGroup == null && other.getCQLGroup() == null) || (this.CQLGroup != null && this.CQLGroup
                .equals(other.getCQLGroup())))
            && ((this.CQLAttribute == null && other.getCQLAttribute() == null) || (this.CQLAttribute != null && this.CQLAttribute
                .equals(other.getCQLAttribute())))
            && ((this.CQLExtension == null && other.getCQLExtension() == null) || (this.CQLExtension != null && this.CQLExtension
                .equals(other.getCQLExtension())))
            && ((this.className == null && other.getClassName() == null) || (this.className != null && this.className
                .equals(other.getClassName())))
            && ((this._instanceof == null && other.get_instanceof() == null) || (this._instanceof != null && this._instanceof
                .equals(other.get_instanceof())));
        return _equals;
    }


    public synchronized int hashCode() {
        int _hashCode = 1;
        if (getCQLAssociatedObject() != null) {
            _hashCode += getCQLAssociatedObject().hashCode();
        }
        if (getCQLGroup() != null) {
            _hashCode += getCQLGroup().hashCode();
        }
        if (getCQLAttribute() != null) {
            _hashCode += getCQLAttribute().hashCode();
        }
        if (getCQLExtension() != null) {
            _hashCode += getCQLExtension().hashCode();
        }
        if (getClassName() != null) {
            _hashCode += getClassName().hashCode();
        }
        if (get_instanceof() != null) {
            _hashCode += get_instanceof().hashCode();
        }
        return _hashCode;
    }
}
