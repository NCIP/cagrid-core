/**
 * CQLQueryModifier.java
 * 
 * This file was auto-generated from WSDL by the Apache Axis 1.2RC2 Apr 28, 2006
 * (12:42:00 EDT) WSDL2Java emitter.
 */

package org.cagrid.cql2;

public class CQLQueryModifier implements java.io.Serializable {
    private org.cagrid.cql2.NamedAttribute[] namedAttribute;
    private org.cagrid.cql2.DistinctAttribute distinctAttribute;
    private java.lang.Boolean countOnly;
    private org.cagrid.cql2.CQLExtension modifierExtension;


    public CQLQueryModifier() {
    }


    public CQLQueryModifier(java.lang.Boolean countOnly, org.cagrid.cql2.DistinctAttribute distinctAttribute,
        org.cagrid.cql2.CQLExtension modifierExtension, org.cagrid.cql2.NamedAttribute[] namedAttribute) {
        this.namedAttribute = namedAttribute;
        this.distinctAttribute = distinctAttribute;
        this.countOnly = countOnly;
        this.modifierExtension = modifierExtension;
    }


    /**
     * Gets the namedAttribute value for this CQLQueryModifier.
     * 
     * @return namedAttribute
     */
    public org.cagrid.cql2.NamedAttribute[] getNamedAttribute() {
        return namedAttribute;
    }


    /**
     * Sets the namedAttribute value for this CQLQueryModifier.
     * 
     * @param namedAttribute
     */
    public void setNamedAttribute(org.cagrid.cql2.NamedAttribute[] namedAttribute) {
        this.namedAttribute = namedAttribute;
    }


    public org.cagrid.cql2.NamedAttribute getNamedAttribute(int i) {
        return this.namedAttribute[i];
    }


    public void setNamedAttribute(int i, org.cagrid.cql2.NamedAttribute _value) {
        this.namedAttribute[i] = _value;
    }


    /**
     * Gets the distinctAttribute value for this CQLQueryModifier.
     * 
     * @return distinctAttribute
     */
    public org.cagrid.cql2.DistinctAttribute getDistinctAttribute() {
        return distinctAttribute;
    }


    /**
     * Sets the distinctAttribute value for this CQLQueryModifier.
     * 
     * @param distinctAttribute
     */
    public void setDistinctAttribute(org.cagrid.cql2.DistinctAttribute distinctAttribute) {
        this.distinctAttribute = distinctAttribute;
    }


    /**
     * Gets the countOnly value for this CQLQueryModifier.
     * 
     * @return countOnly
     */
    public java.lang.Boolean getCountOnly() {
        return countOnly;
    }


    /**
     * Sets the countOnly value for this CQLQueryModifier.
     * 
     * @param countOnly
     */
    public void setCountOnly(java.lang.Boolean countOnly) {
        this.countOnly = countOnly;
    }


    /**
     * Gets the modifierExtension value for this CQLQueryModifier.
     * 
     * @return modifierExtension
     */
    public org.cagrid.cql2.CQLExtension getModifierExtension() {
        return modifierExtension;
    }


    /**
     * Sets the modifierExtension value for this CQLQueryModifier.
     * 
     * @param modifierExtension
     */
    public void setModifierExtension(org.cagrid.cql2.CQLExtension modifierExtension) {
        this.modifierExtension = modifierExtension;
    }


    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof CQLQueryModifier))
            return false;
        CQLQueryModifier other = (CQLQueryModifier) obj;
        if (obj == null)
            return false;
        if (this == obj)
            return true;
        boolean _equals;
        _equals = true
            && ((this.namedAttribute == null && other.getNamedAttribute() == null) || (this.namedAttribute != null && java.util.Arrays
                .equals(this.namedAttribute, other.getNamedAttribute())))
            && ((this.distinctAttribute == null && other.getDistinctAttribute() == null) || (this.distinctAttribute != null && this.distinctAttribute
                .equals(other.getDistinctAttribute())))
            && ((this.countOnly == null && other.getCountOnly() == null) || (this.countOnly != null && this.countOnly
                .equals(other.getCountOnly())))
            && ((this.modifierExtension == null && other.getModifierExtension() == null) || (this.modifierExtension != null && this.modifierExtension
                .equals(other.getModifierExtension())));
        return _equals;
    }


    public synchronized int hashCode() {
        int _hashCode = 1;
        if (getNamedAttribute() != null) {
            for (int i = 0; i < java.lang.reflect.Array.getLength(getNamedAttribute()); i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getNamedAttribute(), i);
                if (obj != null && !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getDistinctAttribute() != null) {
            _hashCode += getDistinctAttribute().hashCode();
        }
        if (getCountOnly() != null) {
            _hashCode += getCountOnly().hashCode();
        }
        if (getModifierExtension() != null) {
            _hashCode += getModifierExtension().hashCode();
        }
        return _hashCode;
    }
}
