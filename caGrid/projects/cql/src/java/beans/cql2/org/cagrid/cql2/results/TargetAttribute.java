/**
 * TargetAttribute.java
 * 
 * This file was auto-generated from WSDL by the Apache Axis 1.2RC2 Apr 28, 2006
 * (12:42:00 EDT) WSDL2Java emitter.
 */

package org.cagrid.cql2.results;

/**
 * An attribute (name and value pair) of a target data object instance
 */
public class TargetAttribute implements java.io.Serializable {
    private java.lang.String name; // attribute
    private java.lang.String value; // attribute


    public TargetAttribute() {
    }


    public TargetAttribute(java.lang.String name, java.lang.String value) {
        this.name = name;
        this.value = value;
    }


    /**
     * Gets the name value for this TargetAttribute.
     * 
     * @return name
     */
    public java.lang.String getName() {
        return name;
    }


    /**
     * Sets the name value for this TargetAttribute.
     * 
     * @param name
     */
    public void setName(java.lang.String name) {
        this.name = name;
    }


    /**
     * Gets the value value for this TargetAttribute.
     * 
     * @return value
     */
    public java.lang.String getValue() {
        return value;
    }


    /**
     * Sets the value value for this TargetAttribute.
     * 
     * @param value
     */
    public void setValue(java.lang.String value) {
        this.value = value;
    }


    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof TargetAttribute))
            return false;
        TargetAttribute other = (TargetAttribute) obj;
        if (obj == null)
            return false;
        if (this == obj)
            return true;
        boolean _equals;
        _equals = true
            && ((this.name == null && other.getName() == null) || (this.name != null && this.name.equals(other
                .getName())))
            && ((this.value == null && other.getValue() == null) || (this.value != null && this.value.equals(other
                .getValue())));
        return _equals;
    }


    public synchronized int hashCode() {
        int _hashCode = 1;
        if (getName() != null) {
            _hashCode += getName().hashCode();
        }
        if (getValue() != null) {
            _hashCode += getValue().hashCode();
        }
        return _hashCode;
    }
}
