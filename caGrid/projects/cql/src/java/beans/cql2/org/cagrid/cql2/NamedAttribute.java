/**
*============================================================================
*  Copyright The Ohio State University Research Foundation, The University of Chicago - 
*	Argonne National Laboratory, Emory University, SemanticBits LLC, and 
*	Ekagra Software Technologies Ltd.
*
*  Distributed under the OSI-approved BSD 3-Clause License.
*  See http://ncip.github.com/cagrid-core/LICENSE.txt for details.
*============================================================================
**/
/**
 * NamedAttribute.java
 * 
 * This file was auto-generated from WSDL by the Apache Axis 1.2RC2 Apr 28, 2006
 * (12:42:00 EDT) WSDL2Java emitter.
 */

package org.cagrid.cql2;

public class NamedAttribute implements java.io.Serializable {
    private java.lang.String attributeName; // attribute


    public NamedAttribute() {
    }


    public NamedAttribute(java.lang.String attributeName) {
        this.attributeName = attributeName;
    }


    /**
     * Gets the attributeName value for this NamedAttribute.
     * 
     * @return attributeName
     */
    public java.lang.String getAttributeName() {
        return attributeName;
    }


    /**
     * Sets the attributeName value for this NamedAttribute.
     * 
     * @param attributeName
     */
    public void setAttributeName(java.lang.String attributeName) {
        this.attributeName = attributeName;
    }


    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof NamedAttribute))
            return false;
        NamedAttribute other = (NamedAttribute) obj;
        if (obj == null)
            return false;
        if (this == obj)
            return true;
        boolean _equals;
        _equals = true && ((this.attributeName == null && other.getAttributeName() == null) || (this.attributeName != null && this.attributeName
            .equals(other.getAttributeName())));
        return _equals;
    }


    public synchronized int hashCode() {
        int _hashCode = 1;
        if (getAttributeName() != null) {
            _hashCode += getAttributeName().hashCode();
        }
        return _hashCode;
    }
}
