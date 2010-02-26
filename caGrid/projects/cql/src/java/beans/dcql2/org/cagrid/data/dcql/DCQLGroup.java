/**
 * DCQLGroup.java
 * 
 * This file was auto-generated from WSDL by the Apache Axis 1.2RC2 Apr 28, 2006
 * (12:42:00 EDT) WSDL2Java emitter.
 */

package org.cagrid.data.dcql;

/**
 * A collection of two or more sub-constraints, grouped together by a logical
 * operation
 */
public class DCQLGroup implements java.io.Serializable {
    private org.cagrid.data.dcql.DCQLAssociatedObject[] associatedObject;
    private org.cagrid.cql2.CQLAttribute[] attribute;
    private org.cagrid.data.dcql.ForeignAssociatedObject[] foreignAssociatedObject;
    private org.cagrid.data.dcql.DCQLGroup[] group;
    private org.cagrid.cql2.CQLExtension[] objectExtension;
    private org.cagrid.cql2.GroupLogicalOperator logicalOperation; // attribute


    public DCQLGroup() {
    }


    public DCQLGroup(org.cagrid.data.dcql.DCQLAssociatedObject[] associatedObject,
        org.cagrid.cql2.CQLAttribute[] attribute,
        org.cagrid.data.dcql.ForeignAssociatedObject[] foreignAssociatedObject, org.cagrid.data.dcql.DCQLGroup[] group,
        org.cagrid.cql2.GroupLogicalOperator logicalOperation, org.cagrid.cql2.CQLExtension[] objectExtension) {
        this.associatedObject = associatedObject;
        this.attribute = attribute;
        this.foreignAssociatedObject = foreignAssociatedObject;
        this.group = group;
        this.objectExtension = objectExtension;
        this.logicalOperation = logicalOperation;
    }


    /**
     * Gets the associatedObject value for this DCQLGroup.
     * 
     * @return associatedObject
     */
    public org.cagrid.data.dcql.DCQLAssociatedObject[] getAssociatedObject() {
        return associatedObject;
    }


    /**
     * Sets the associatedObject value for this DCQLGroup.
     * 
     * @param associatedObject
     */
    public void setAssociatedObject(org.cagrid.data.dcql.DCQLAssociatedObject[] associatedObject) {
        this.associatedObject = associatedObject;
    }


    public org.cagrid.data.dcql.DCQLAssociatedObject getAssociatedObject(int i) {
        return this.associatedObject[i];
    }


    public void setAssociatedObject(int i, org.cagrid.data.dcql.DCQLAssociatedObject _value) {
        this.associatedObject[i] = _value;
    }


    /**
     * Gets the attribute value for this DCQLGroup.
     * 
     * @return attribute
     */
    public org.cagrid.cql2.CQLAttribute[] getAttribute() {
        return attribute;
    }


    /**
     * Sets the attribute value for this DCQLGroup.
     * 
     * @param attribute
     */
    public void setAttribute(org.cagrid.cql2.CQLAttribute[] attribute) {
        this.attribute = attribute;
    }


    public org.cagrid.cql2.CQLAttribute getAttribute(int i) {
        return this.attribute[i];
    }


    public void setAttribute(int i, org.cagrid.cql2.CQLAttribute _value) {
        this.attribute[i] = _value;
    }


    /**
     * Gets the foreignAssociatedObject value for this DCQLGroup.
     * 
     * @return foreignAssociatedObject
     */
    public org.cagrid.data.dcql.ForeignAssociatedObject[] getForeignAssociatedObject() {
        return foreignAssociatedObject;
    }


    /**
     * Sets the foreignAssociatedObject value for this DCQLGroup.
     * 
     * @param foreignAssociatedObject
     */
    public void setForeignAssociatedObject(org.cagrid.data.dcql.ForeignAssociatedObject[] foreignAssociatedObject) {
        this.foreignAssociatedObject = foreignAssociatedObject;
    }


    public org.cagrid.data.dcql.ForeignAssociatedObject getForeignAssociatedObject(int i) {
        return this.foreignAssociatedObject[i];
    }


    public void setForeignAssociatedObject(int i, org.cagrid.data.dcql.ForeignAssociatedObject _value) {
        this.foreignAssociatedObject[i] = _value;
    }


    /**
     * Gets the group value for this DCQLGroup.
     * 
     * @return group
     */
    public org.cagrid.data.dcql.DCQLGroup[] getGroup() {
        return group;
    }


    /**
     * Sets the group value for this DCQLGroup.
     * 
     * @param group
     */
    public void setGroup(org.cagrid.data.dcql.DCQLGroup[] group) {
        this.group = group;
    }


    public org.cagrid.data.dcql.DCQLGroup getGroup(int i) {
        return this.group[i];
    }


    public void setGroup(int i, org.cagrid.data.dcql.DCQLGroup _value) {
        this.group[i] = _value;
    }


    /**
     * Gets the objectExtension value for this DCQLGroup.
     * 
     * @return objectExtension
     */
    public org.cagrid.cql2.CQLExtension[] getObjectExtension() {
        return objectExtension;
    }


    /**
     * Sets the objectExtension value for this DCQLGroup.
     * 
     * @param objectExtension
     */
    public void setObjectExtension(org.cagrid.cql2.CQLExtension[] objectExtension) {
        this.objectExtension = objectExtension;
    }


    public org.cagrid.cql2.CQLExtension getObjectExtension(int i) {
        return this.objectExtension[i];
    }


    public void setObjectExtension(int i, org.cagrid.cql2.CQLExtension _value) {
        this.objectExtension[i] = _value;
    }


    /**
     * Gets the logicalOperation value for this DCQLGroup.
     * 
     * @return logicalOperation
     */
    public org.cagrid.cql2.GroupLogicalOperator getLogicalOperation() {
        return logicalOperation;
    }


    /**
     * Sets the logicalOperation value for this DCQLGroup.
     * 
     * @param logicalOperation
     */
    public void setLogicalOperation(org.cagrid.cql2.GroupLogicalOperator logicalOperation) {
        this.logicalOperation = logicalOperation;
    }


    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof DCQLGroup))
            return false;
        DCQLGroup other = (DCQLGroup) obj;
        if (obj == null)
            return false;
        if (this == obj)
            return true;
        boolean _equals;
        _equals = true
            && ((this.associatedObject == null && other.getAssociatedObject() == null) || (this.associatedObject != null && java.util.Arrays
                .equals(this.associatedObject, other.getAssociatedObject())))
            && ((this.attribute == null && other.getAttribute() == null) || (this.attribute != null && java.util.Arrays
                .equals(this.attribute, other.getAttribute())))
            && ((this.foreignAssociatedObject == null && other.getForeignAssociatedObject() == null) || (this.foreignAssociatedObject != null && java.util.Arrays
                .equals(this.foreignAssociatedObject, other.getForeignAssociatedObject())))
            && ((this.group == null && other.getGroup() == null) || (this.group != null && java.util.Arrays.equals(
                this.group, other.getGroup())))
            && ((this.objectExtension == null && other.getObjectExtension() == null) || (this.objectExtension != null && java.util.Arrays
                .equals(this.objectExtension, other.getObjectExtension())))
            && ((this.logicalOperation == null && other.getLogicalOperation() == null) || (this.logicalOperation != null && this.logicalOperation
                .equals(other.getLogicalOperation())));
        return _equals;
    }


    public synchronized int hashCode() {
        int _hashCode = 1;
        if (getAssociatedObject() != null) {
            for (int i = 0; i < java.lang.reflect.Array.getLength(getAssociatedObject()); i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getAssociatedObject(), i);
                if (obj != null && !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getAttribute() != null) {
            for (int i = 0; i < java.lang.reflect.Array.getLength(getAttribute()); i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getAttribute(), i);
                if (obj != null && !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getForeignAssociatedObject() != null) {
            for (int i = 0; i < java.lang.reflect.Array.getLength(getForeignAssociatedObject()); i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getForeignAssociatedObject(), i);
                if (obj != null && !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getGroup() != null) {
            for (int i = 0; i < java.lang.reflect.Array.getLength(getGroup()); i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getGroup(), i);
                if (obj != null && !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getObjectExtension() != null) {
            for (int i = 0; i < java.lang.reflect.Array.getLength(getObjectExtension()); i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getObjectExtension(), i);
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
