/**
 * SupportedExtensions.java
 * 
 * This file was auto-generated from WSDL by the Apache Axis 1.2RC2 Apr 28, 2006
 * (12:42:00 EDT) WSDL2Java emitter.
 */

package org.cagrid.cql2.extensionsupport;

public class SupportedExtensions implements java.io.Serializable {
    private javax.xml.namespace.QName[] modifierExtension;
    private javax.xml.namespace.QName[] attributeExtension;
    private javax.xml.namespace.QName[] objectExtension;
    private javax.xml.namespace.QName[] resultExtension;


    public SupportedExtensions() {
    }


    public SupportedExtensions(javax.xml.namespace.QName[] attributeExtension,
        javax.xml.namespace.QName[] modifierExtension, javax.xml.namespace.QName[] objectExtension,
        javax.xml.namespace.QName[] resultExtension) {
        this.modifierExtension = modifierExtension;
        this.attributeExtension = attributeExtension;
        this.objectExtension = objectExtension;
        this.resultExtension = resultExtension;
    }


    /**
     * Gets the modifierExtension value for this SupportedExtensions.
     * 
     * @return modifierExtension
     */
    public javax.xml.namespace.QName[] getModifierExtension() {
        return modifierExtension;
    }


    /**
     * Sets the modifierExtension value for this SupportedExtensions.
     * 
     * @param modifierExtension
     */
    public void setModifierExtension(javax.xml.namespace.QName[] modifierExtension) {
        this.modifierExtension = modifierExtension;
    }


    public javax.xml.namespace.QName getModifierExtension(int i) {
        return this.modifierExtension[i];
    }


    public void setModifierExtension(int i, javax.xml.namespace.QName _value) {
        this.modifierExtension[i] = _value;
    }


    /**
     * Gets the attributeExtension value for this SupportedExtensions.
     * 
     * @return attributeExtension
     */
    public javax.xml.namespace.QName[] getAttributeExtension() {
        return attributeExtension;
    }


    /**
     * Sets the attributeExtension value for this SupportedExtensions.
     * 
     * @param attributeExtension
     */
    public void setAttributeExtension(javax.xml.namespace.QName[] attributeExtension) {
        this.attributeExtension = attributeExtension;
    }


    public javax.xml.namespace.QName getAttributeExtension(int i) {
        return this.attributeExtension[i];
    }


    public void setAttributeExtension(int i, javax.xml.namespace.QName _value) {
        this.attributeExtension[i] = _value;
    }


    /**
     * Gets the objectExtension value for this SupportedExtensions.
     * 
     * @return objectExtension
     */
    public javax.xml.namespace.QName[] getObjectExtension() {
        return objectExtension;
    }


    /**
     * Sets the objectExtension value for this SupportedExtensions.
     * 
     * @param objectExtension
     */
    public void setObjectExtension(javax.xml.namespace.QName[] objectExtension) {
        this.objectExtension = objectExtension;
    }


    public javax.xml.namespace.QName getObjectExtension(int i) {
        return this.objectExtension[i];
    }


    public void setObjectExtension(int i, javax.xml.namespace.QName _value) {
        this.objectExtension[i] = _value;
    }


    /**
     * Gets the resultExtension value for this SupportedExtensions.
     * 
     * @return resultExtension
     */
    public javax.xml.namespace.QName[] getResultExtension() {
        return resultExtension;
    }


    /**
     * Sets the resultExtension value for this SupportedExtensions.
     * 
     * @param resultExtension
     */
    public void setResultExtension(javax.xml.namespace.QName[] resultExtension) {
        this.resultExtension = resultExtension;
    }


    public javax.xml.namespace.QName getResultExtension(int i) {
        return this.resultExtension[i];
    }


    public void setResultExtension(int i, javax.xml.namespace.QName _value) {
        this.resultExtension[i] = _value;
    }


    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof SupportedExtensions))
            return false;
        SupportedExtensions other = (SupportedExtensions) obj;
        if (obj == null)
            return false;
        if (this == obj)
            return true;
        boolean _equals;
        _equals = true
            && ((this.modifierExtension == null && other.getModifierExtension() == null) || (this.modifierExtension != null && java.util.Arrays
                .equals(this.modifierExtension, other.getModifierExtension())))
            && ((this.attributeExtension == null && other.getAttributeExtension() == null) || (this.attributeExtension != null && java.util.Arrays
                .equals(this.attributeExtension, other.getAttributeExtension())))
            && ((this.objectExtension == null && other.getObjectExtension() == null) || (this.objectExtension != null && java.util.Arrays
                .equals(this.objectExtension, other.getObjectExtension())))
            && ((this.resultExtension == null && other.getResultExtension() == null) || (this.resultExtension != null && java.util.Arrays
                .equals(this.resultExtension, other.getResultExtension())));
        return _equals;
    }


    public synchronized int hashCode() {
        int _hashCode = 1;
        if (getModifierExtension() != null) {
            for (int i = 0; i < java.lang.reflect.Array.getLength(getModifierExtension()); i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getModifierExtension(), i);
                if (obj != null && !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getAttributeExtension() != null) {
            for (int i = 0; i < java.lang.reflect.Array.getLength(getAttributeExtension()); i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getAttributeExtension(), i);
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
        if (getResultExtension() != null) {
            for (int i = 0; i < java.lang.reflect.Array.getLength(getResultExtension()); i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getResultExtension(), i);
                if (obj != null && !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        return _hashCode;
    }
}
