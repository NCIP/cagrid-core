/**
 * DCQLObject.java
 * 
 * This file was auto-generated from WSDL by the Apache Axis 1.2RC2 Apr 28, 2006
 * (12:42:00 EDT) WSDL2Java emitter.
 */

package org.cagrid.data.dcql;

/**
 * Describes a query for a particular data type
 */
public class DCQLObject implements java.io.Serializable {
    private org.cagrid.cql2.CQLAttribute attribute;
    private org.cagrid.data.dcql.DCQLAssociatedObject associatedObject;
    private org.cagrid.data.dcql.ForeignAssociatedObject foreignAssociatedObject;
    private org.cagrid.data.dcql.DCQLGroup group;
    private org.cagrid.cql2.CQLExtension objectExtension;
    private java.lang.String name; // attribute
    private java.lang.String _instanceof; // attribute


    public DCQLObject() {
    }


    public DCQLObject(java.lang.String _instanceof, org.cagrid.data.dcql.DCQLAssociatedObject associatedObject,
        org.cagrid.cql2.CQLAttribute attribute, org.cagrid.data.dcql.ForeignAssociatedObject foreignAssociatedObject,
        org.cagrid.data.dcql.DCQLGroup group, java.lang.String name, org.cagrid.cql2.CQLExtension objectExtension) {
        this.attribute = attribute;
        this.associatedObject = associatedObject;
        this.foreignAssociatedObject = foreignAssociatedObject;
        this.group = group;
        this.objectExtension = objectExtension;
        this.name = name;
        this._instanceof = _instanceof;
    }


    /**
     * Gets the attribute value for this DCQLObject.
     * 
     * @return attribute
     */
    public org.cagrid.cql2.CQLAttribute getAttribute() {
        return attribute;
    }


    /**
     * Sets the attribute value for this DCQLObject.
     * 
     * @param attribute
     */
    public void setAttribute(org.cagrid.cql2.CQLAttribute attribute) {
        this.attribute = attribute;
    }


    /**
     * Gets the associatedObject value for this DCQLObject.
     * 
     * @return associatedObject
     */
    public org.cagrid.data.dcql.DCQLAssociatedObject getAssociatedObject() {
        return associatedObject;
    }


    /**
     * Sets the associatedObject value for this DCQLObject.
     * 
     * @param associatedObject
     */
    public void setAssociatedObject(org.cagrid.data.dcql.DCQLAssociatedObject associatedObject) {
        this.associatedObject = associatedObject;
    }


    /**
     * Gets the foreignAssociatedObject value for this DCQLObject.
     * 
     * @return foreignAssociatedObject
     */
    public org.cagrid.data.dcql.ForeignAssociatedObject getForeignAssociatedObject() {
        return foreignAssociatedObject;
    }


    /**
     * Sets the foreignAssociatedObject value for this DCQLObject.
     * 
     * @param foreignAssociatedObject
     */
    public void setForeignAssociatedObject(org.cagrid.data.dcql.ForeignAssociatedObject foreignAssociatedObject) {
        this.foreignAssociatedObject = foreignAssociatedObject;
    }


    /**
     * Gets the group value for this DCQLObject.
     * 
     * @return group
     */
    public org.cagrid.data.dcql.DCQLGroup getGroup() {
        return group;
    }


    /**
     * Sets the group value for this DCQLObject.
     * 
     * @param group
     */
    public void setGroup(org.cagrid.data.dcql.DCQLGroup group) {
        this.group = group;
    }


    /**
     * Gets the objectExtension value for this DCQLObject.
     * 
     * @return objectExtension
     */
    public org.cagrid.cql2.CQLExtension getObjectExtension() {
        return objectExtension;
    }


    /**
     * Sets the objectExtension value for this DCQLObject.
     * 
     * @param objectExtension
     */
    public void setObjectExtension(org.cagrid.cql2.CQLExtension objectExtension) {
        this.objectExtension = objectExtension;
    }


    /**
     * Gets the name value for this DCQLObject.
     * 
     * @return name
     */
    public java.lang.String getName() {
        return name;
    }


    /**
     * Sets the name value for this DCQLObject.
     * 
     * @param name
     */
    public void setName(java.lang.String name) {
        this.name = name;
    }


    /**
     * Gets the _instanceof value for this DCQLObject.
     * 
     * @return _instanceof
     */
    public java.lang.String get_instanceof() {
        return _instanceof;
    }


    /**
     * Sets the _instanceof value for this DCQLObject.
     * 
     * @param _instanceof
     */
    public void set_instanceof(java.lang.String _instanceof) {
        this._instanceof = _instanceof;
    }


    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof DCQLObject))
            return false;
        DCQLObject other = (DCQLObject) obj;
        if (obj == null)
            return false;
        if (this == obj)
            return true;
        boolean _equals;
        _equals = true
            && ((this.attribute == null && other.getAttribute() == null) || (this.attribute != null && this.attribute
                .equals(other.getAttribute())))
            && ((this.associatedObject == null && other.getAssociatedObject() == null) || (this.associatedObject != null && this.associatedObject
                .equals(other.getAssociatedObject())))
            && ((this.foreignAssociatedObject == null && other.getForeignAssociatedObject() == null) || (this.foreignAssociatedObject != null && this.foreignAssociatedObject
                .equals(other.getForeignAssociatedObject())))
            && ((this.group == null && other.getGroup() == null) || (this.group != null && this.group.equals(other
                .getGroup())))
            && ((this.objectExtension == null && other.getObjectExtension() == null) || (this.objectExtension != null && this.objectExtension
                .equals(other.getObjectExtension())))
            && ((this.name == null && other.getName() == null) || (this.name != null && this.name.equals(other
                .getName())))
            && ((this._instanceof == null && other.get_instanceof() == null) || (this._instanceof != null && this._instanceof
                .equals(other.get_instanceof())));
        return _equals;
    }


    public synchronized int hashCode() {
        int _hashCode = 1;
        if (getAttribute() != null) {
            _hashCode += getAttribute().hashCode();
        }
        if (getAssociatedObject() != null) {
            _hashCode += getAssociatedObject().hashCode();
        }
        if (getForeignAssociatedObject() != null) {
            _hashCode += getForeignAssociatedObject().hashCode();
        }
        if (getGroup() != null) {
            _hashCode += getGroup().hashCode();
        }
        if (getObjectExtension() != null) {
            _hashCode += getObjectExtension().hashCode();
        }
        if (getName() != null) {
            _hashCode += getName().hashCode();
        }
        if (get_instanceof() != null) {
            _hashCode += get_instanceof().hashCode();
        }
        return _hashCode;
    }
}
