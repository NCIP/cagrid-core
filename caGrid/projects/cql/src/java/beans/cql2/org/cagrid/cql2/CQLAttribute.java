/**
 * CQLAttribute.java
 * 
 * This file was auto-generated from WSDL by the Apache Axis 1.2RC2 Apr 28, 2006
 * (12:42:00 EDT) WSDL2Java emitter.
 */

package org.cagrid.cql2;

public class CQLAttribute implements java.io.Serializable {
    private org.cagrid.cql2.BinaryPredicate binaryPredicate;
    private org.cagrid.cql2.AttributeValue attributeValue;
    private org.cagrid.cql2.UnaryPredicate unaryPredicate;
    private org.cagrid.cql2.CQLExtension attributeExtension;
    private java.lang.String name; // attribute


    public CQLAttribute() {
    }


    public CQLAttribute(org.cagrid.cql2.CQLExtension attributeExtension, org.cagrid.cql2.AttributeValue attributeValue,
        org.cagrid.cql2.BinaryPredicate binaryPredicate, java.lang.String name,
        org.cagrid.cql2.UnaryPredicate unaryPredicate) {
        this.binaryPredicate = binaryPredicate;
        this.attributeValue = attributeValue;
        this.unaryPredicate = unaryPredicate;
        this.attributeExtension = attributeExtension;
        this.name = name;
    }


    /**
     * Gets the binaryPredicate value for this CQLAttribute.
     * 
     * @return binaryPredicate
     */
    public org.cagrid.cql2.BinaryPredicate getBinaryPredicate() {
        return binaryPredicate;
    }


    /**
     * Sets the binaryPredicate value for this CQLAttribute.
     * 
     * @param binaryPredicate
     */
    public void setBinaryPredicate(org.cagrid.cql2.BinaryPredicate binaryPredicate) {
        this.binaryPredicate = binaryPredicate;
    }


    /**
     * Gets the attributeValue value for this CQLAttribute.
     * 
     * @return attributeValue
     */
    public org.cagrid.cql2.AttributeValue getAttributeValue() {
        return attributeValue;
    }


    /**
     * Sets the attributeValue value for this CQLAttribute.
     * 
     * @param attributeValue
     */
    public void setAttributeValue(org.cagrid.cql2.AttributeValue attributeValue) {
        this.attributeValue = attributeValue;
    }


    /**
     * Gets the unaryPredicate value for this CQLAttribute.
     * 
     * @return unaryPredicate
     */
    public org.cagrid.cql2.UnaryPredicate getUnaryPredicate() {
        return unaryPredicate;
    }


    /**
     * Sets the unaryPredicate value for this CQLAttribute.
     * 
     * @param unaryPredicate
     */
    public void setUnaryPredicate(org.cagrid.cql2.UnaryPredicate unaryPredicate) {
        this.unaryPredicate = unaryPredicate;
    }


    /**
     * Gets the attributeExtension value for this CQLAttribute.
     * 
     * @return attributeExtension
     */
    public org.cagrid.cql2.CQLExtension getAttributeExtension() {
        return attributeExtension;
    }


    /**
     * Sets the attributeExtension value for this CQLAttribute.
     * 
     * @param attributeExtension
     */
    public void setAttributeExtension(org.cagrid.cql2.CQLExtension attributeExtension) {
        this.attributeExtension = attributeExtension;
    }


    /**
     * Gets the name value for this CQLAttribute.
     * 
     * @return name
     */
    public java.lang.String getName() {
        return name;
    }


    /**
     * Sets the name value for this CQLAttribute.
     * 
     * @param name
     */
    public void setName(java.lang.String name) {
        this.name = name;
    }
    

    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof CQLAttribute))
            return false;
        CQLAttribute other = (CQLAttribute) obj;
        if (obj == null)
            return false;
        if (this == obj)
            return true;
        boolean _equals;
        _equals = true
            && ((this.binaryPredicate == null && other.getBinaryPredicate() == null) || (this.binaryPredicate != null && this.binaryPredicate
                .equals(other.getBinaryPredicate())))
            && ((this.attributeValue == null && other.getAttributeValue() == null) || (this.attributeValue != null && this.attributeValue
                .equals(other.getAttributeValue())))
            && ((this.unaryPredicate == null && other.getUnaryPredicate() == null) || (this.unaryPredicate != null && this.unaryPredicate
                .equals(other.getUnaryPredicate())))
            && ((this.attributeExtension == null && other.getAttributeExtension() == null) || (this.attributeExtension != null && this.attributeExtension
                .equals(other.getAttributeExtension())))
            && ((this.name == null && other.getName() == null) || (this.name != null && this.name.equals(other
                .getName())));
        return _equals;
    }


    public synchronized int hashCode() {
        int _hashCode = 1;
        if (getBinaryPredicate() != null) {
            _hashCode += getBinaryPredicate().hashCode();
        }
        if (getAttributeValue() != null) {
            _hashCode += getAttributeValue().hashCode();
        }
        if (getUnaryPredicate() != null) {
            _hashCode += getUnaryPredicate().hashCode();
        }
        if (getAttributeExtension() != null) {
            _hashCode += getAttributeExtension().hashCode();
        }
        if (getName() != null) {
            _hashCode += getName().hashCode();
        }
        return _hashCode;
    }
}
