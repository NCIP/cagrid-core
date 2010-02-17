/**
 * NamedAssociationList.java
 * 
 * This file was auto-generated from WSDL by the Apache Axis 1.2RC2 Apr 28, 2006
 * (12:42:00 EDT) WSDL2Java emitter.
 */

package org.cagrid.cql2;

public class NamedAssociationList implements java.io.Serializable {
    private org.cagrid.cql2.NamedAssociation[] namedAssociation;


    public NamedAssociationList() {
    }


    public NamedAssociationList(org.cagrid.cql2.NamedAssociation[] namedAssociation) {
        this.namedAssociation = namedAssociation;
    }


    /**
     * Gets the namedAssociation value for this NamedAssociationList.
     * 
     * @return namedAssociation
     */
    public org.cagrid.cql2.NamedAssociation[] getNamedAssociation() {
        return namedAssociation;
    }


    /**
     * Sets the namedAssociation value for this NamedAssociationList.
     * 
     * @param namedAssociation
     */
    public void setNamedAssociation(org.cagrid.cql2.NamedAssociation[] namedAssociation) {
        this.namedAssociation = namedAssociation;
    }


    public org.cagrid.cql2.NamedAssociation getNamedAssociation(int i) {
        return this.namedAssociation[i];
    }


    public void setNamedAssociation(int i, org.cagrid.cql2.NamedAssociation _value) {
        this.namedAssociation[i] = _value;
    }


    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof NamedAssociationList))
            return false;
        NamedAssociationList other = (NamedAssociationList) obj;
        if (obj == null)
            return false;
        if (this == obj)
            return true;
        boolean _equals;
        _equals = true && ((this.namedAssociation == null && other.getNamedAssociation() == null) || (this.namedAssociation != null && java.util.Arrays
            .equals(this.namedAssociation, other.getNamedAssociation())));
        return _equals;
    }


    public synchronized int hashCode() {
        int _hashCode = 1;
        if (getNamedAssociation() != null) {
            for (int i = 0; i < java.lang.reflect.Array.getLength(getNamedAssociation()); i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getNamedAssociation(), i);
                if (obj != null && !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        return _hashCode;
    }
}
