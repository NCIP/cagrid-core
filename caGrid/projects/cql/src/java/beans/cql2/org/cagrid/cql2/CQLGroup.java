/**
 * CQLGroup.java
 * 
 * This file was auto-generated from WSDL by the Apache Axis 1.2RC2 Apr 28, 2006
 * (12:42:00 EDT) WSDL2Java emitter.
 */

package org.cagrid.cql2;

public class CQLGroup implements java.io.Serializable {
    private org.cagrid.cql2.CQLAssociatedObject[] CQLAssociatedObject;
    private org.cagrid.cql2.CQLGroup[] CQLGroup;
    private org.cagrid.cql2.CQLAttribute[] CQLAttribute;
    private org.cagrid.cql2.CQLExtension[] CQLExtension;
    private org.cagrid.cql2.GroupLogicalOperator logicalOperation; // attribute


    public CQLGroup() {
    }


    public CQLGroup(org.cagrid.cql2.CQLAssociatedObject[] CQLAssociatedObject,
        org.cagrid.cql2.CQLAttribute[] CQLAttribute, org.cagrid.cql2.CQLExtension[] CQLExtension,
        org.cagrid.cql2.CQLGroup[] CQLGroup, org.cagrid.cql2.GroupLogicalOperator logicalOperation) {
        this.CQLAssociatedObject = CQLAssociatedObject;
        this.CQLGroup = CQLGroup;
        this.CQLAttribute = CQLAttribute;
        this.CQLExtension = CQLExtension;
        this.logicalOperation = logicalOperation;
    }


    /**
     * Gets the CQLAssociatedObject value for this CQLGroup.
     * 
     * @return CQLAssociatedObject
     */
    public org.cagrid.cql2.CQLAssociatedObject[] getCQLAssociatedObject() {
        return CQLAssociatedObject;
    }


    /**
     * Sets the CQLAssociatedObject value for this CQLGroup.
     * 
     * @param CQLAssociatedObject
     */
    public void setCQLAssociatedObject(org.cagrid.cql2.CQLAssociatedObject[] CQLAssociatedObject) {
        this.CQLAssociatedObject = CQLAssociatedObject;
    }


    public org.cagrid.cql2.CQLAssociatedObject getCQLAssociatedObject(int i) {
        return this.CQLAssociatedObject[i];
    }


    public void setCQLAssociatedObject(int i, org.cagrid.cql2.CQLAssociatedObject _value) {
        this.CQLAssociatedObject[i] = _value;
    }


    /**
     * Gets the CQLGroup value for this CQLGroup.
     * 
     * @return CQLGroup
     */
    public org.cagrid.cql2.CQLGroup[] getCQLGroup() {
        return CQLGroup;
    }


    /**
     * Sets the CQLGroup value for this CQLGroup.
     * 
     * @param CQLGroup
     */
    public void setCQLGroup(org.cagrid.cql2.CQLGroup[] CQLGroup) {
        this.CQLGroup = CQLGroup;
    }


    public org.cagrid.cql2.CQLGroup getCQLGroup(int i) {
        return this.CQLGroup[i];
    }


    public void setCQLGroup(int i, org.cagrid.cql2.CQLGroup _value) {
        this.CQLGroup[i] = _value;
    }


    /**
     * Gets the CQLAttribute value for this CQLGroup.
     * 
     * @return CQLAttribute
     */
    public org.cagrid.cql2.CQLAttribute[] getCQLAttribute() {
        return CQLAttribute;
    }


    /**
     * Sets the CQLAttribute value for this CQLGroup.
     * 
     * @param CQLAttribute
     */
    public void setCQLAttribute(org.cagrid.cql2.CQLAttribute[] CQLAttribute) {
        this.CQLAttribute = CQLAttribute;
    }


    public org.cagrid.cql2.CQLAttribute getCQLAttribute(int i) {
        return this.CQLAttribute[i];
    }


    public void setCQLAttribute(int i, org.cagrid.cql2.CQLAttribute _value) {
        this.CQLAttribute[i] = _value;
    }


    /**
     * Gets the CQLExtension value for this CQLGroup.
     * 
     * @return CQLExtension
     */
    public org.cagrid.cql2.CQLExtension[] getCQLExtension() {
        return CQLExtension;
    }


    /**
     * Sets the CQLExtension value for this CQLGroup.
     * 
     * @param CQLExtension
     */
    public void setCQLExtension(org.cagrid.cql2.CQLExtension[] CQLExtension) {
        this.CQLExtension = CQLExtension;
    }


    public org.cagrid.cql2.CQLExtension getCQLExtension(int i) {
        return this.CQLExtension[i];
    }


    public void setCQLExtension(int i, org.cagrid.cql2.CQLExtension _value) {
        this.CQLExtension[i] = _value;
    }


    /**
     * Gets the logicalOperation value for this CQLGroup.
     * 
     * @return logicalOperation
     */
    public org.cagrid.cql2.GroupLogicalOperator getLogicalOperation() {
        return logicalOperation;
    }


    /**
     * Sets the logicalOperation value for this CQLGroup.
     * 
     * @param logicalOperation
     */
    public void setLogicalOperation(org.cagrid.cql2.GroupLogicalOperator logicalOperation) {
        this.logicalOperation = logicalOperation;
    }


    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof CQLGroup))
            return false;
        CQLGroup other = (CQLGroup) obj;
        if (obj == null)
            return false;
        if (this == obj)
            return true;
        boolean _equals;
        _equals = true
            && ((this.CQLAssociatedObject == null && other.getCQLAssociatedObject() == null) || (this.CQLAssociatedObject != null && java.util.Arrays
                .equals(this.CQLAssociatedObject, other.getCQLAssociatedObject())))
            && ((this.CQLGroup == null && other.getCQLGroup() == null) || (this.CQLGroup != null && java.util.Arrays
                .equals(this.CQLGroup, other.getCQLGroup())))
            && ((this.CQLAttribute == null && other.getCQLAttribute() == null) || (this.CQLAttribute != null && java.util.Arrays
                .equals(this.CQLAttribute, other.getCQLAttribute())))
            && ((this.CQLExtension == null && other.getCQLExtension() == null) || (this.CQLExtension != null && java.util.Arrays
                .equals(this.CQLExtension, other.getCQLExtension())))
            && ((this.logicalOperation == null && other.getLogicalOperation() == null) || (this.logicalOperation != null && this.logicalOperation
                .equals(other.getLogicalOperation())));
        return _equals;
    }


    public synchronized int hashCode() {
        int _hashCode = 1;
        if (getCQLAssociatedObject() != null) {
            for (int i = 0; i < java.lang.reflect.Array.getLength(getCQLAssociatedObject()); i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getCQLAssociatedObject(), i);
                if (obj != null && !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getCQLGroup() != null) {
            for (int i = 0; i < java.lang.reflect.Array.getLength(getCQLGroup()); i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getCQLGroup(), i);
                if (obj != null && !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getCQLAttribute() != null) {
            for (int i = 0; i < java.lang.reflect.Array.getLength(getCQLAttribute()); i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getCQLAttribute(), i);
                if (obj != null && !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getCQLExtension() != null) {
            for (int i = 0; i < java.lang.reflect.Array.getLength(getCQLExtension()); i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getCQLExtension(), i);
                if (obj != null && !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getLogicalOperation() != null) {
            _hashCode += getLogicalOperation().hashCode();
        }
        return _hashCode;
    }
}
