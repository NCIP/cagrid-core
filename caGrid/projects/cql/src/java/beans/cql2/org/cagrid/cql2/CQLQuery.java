/**
 * CQLQuery.java
 * 
 * This file was auto-generated from WSDL by the Apache Axis 1.2RC2 Apr 28, 2006
 * (12:42:00 EDT) WSDL2Java emitter.
 */

package org.cagrid.cql2;

public class CQLQuery implements java.io.Serializable {
    private org.cagrid.cql2.CQLTargetObject CQLTargetObject;
    private org.cagrid.cql2.CQLQueryModifier CQLQueryModifier;
    private org.cagrid.cql2.AssociationPopulationSpecification associationPopulationSpecification;


    public CQLQuery() {
    }


    public CQLQuery(org.cagrid.cql2.CQLQueryModifier CQLQueryModifier, org.cagrid.cql2.CQLTargetObject CQLTargetObject,
        org.cagrid.cql2.AssociationPopulationSpecification associationPopulationSpecification) {
        this.CQLTargetObject = CQLTargetObject;
        this.CQLQueryModifier = CQLQueryModifier;
        this.associationPopulationSpecification = associationPopulationSpecification;
    }


    /**
     * Gets the CQLTargetObject value for this CQLQuery.
     * 
     * @return CQLTargetObject
     */
    public org.cagrid.cql2.CQLTargetObject getCQLTargetObject() {
        return CQLTargetObject;
    }


    /**
     * Sets the CQLTargetObject value for this CQLQuery.
     * 
     * @param CQLTargetObject
     */
    public void setCQLTargetObject(org.cagrid.cql2.CQLTargetObject CQLTargetObject) {
        this.CQLTargetObject = CQLTargetObject;
    }


    /**
     * Gets the CQLQueryModifier value for this CQLQuery.
     * 
     * @return CQLQueryModifier
     */
    public org.cagrid.cql2.CQLQueryModifier getCQLQueryModifier() {
        return CQLQueryModifier;
    }


    /**
     * Sets the CQLQueryModifier value for this CQLQuery.
     * 
     * @param CQLQueryModifier
     */
    public void setCQLQueryModifier(org.cagrid.cql2.CQLQueryModifier CQLQueryModifier) {
        this.CQLQueryModifier = CQLQueryModifier;
    }


    /**
     * Gets the associationPopulationSpecification value for this CQLQuery.
     * 
     * @return associationPopulationSpecification
     */
    public org.cagrid.cql2.AssociationPopulationSpecification getAssociationPopulationSpecification() {
        return associationPopulationSpecification;
    }


    /**
     * Sets the associationPopulationSpecification value for this CQLQuery.
     * 
     * @param associationPopulationSpecification
     */
    public void setAssociationPopulationSpecification(
        org.cagrid.cql2.AssociationPopulationSpecification associationPopulationSpecification) {
        this.associationPopulationSpecification = associationPopulationSpecification;
    }


    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof CQLQuery))
            return false;
        CQLQuery other = (CQLQuery) obj;
        if (obj == null)
            return false;
        if (this == obj)
            return true;
        boolean _equals;
        _equals = true
            && ((this.CQLTargetObject == null && other.getCQLTargetObject() == null) || (this.CQLTargetObject != null && this.CQLTargetObject
                .equals(other.getCQLTargetObject())))
            && ((this.CQLQueryModifier == null && other.getCQLQueryModifier() == null) || (this.CQLQueryModifier != null && this.CQLQueryModifier
                .equals(other.getCQLQueryModifier())))
            && ((this.associationPopulationSpecification == null && other.getAssociationPopulationSpecification() == null) || (this.associationPopulationSpecification != null && this.associationPopulationSpecification
                .equals(other.getAssociationPopulationSpecification())));
        return _equals;
    }
    

    public synchronized int hashCode() {
        int _hashCode = 1;
        if (getCQLTargetObject() != null) {
            _hashCode += getCQLTargetObject().hashCode();
        }
        if (getCQLQueryModifier() != null) {
            _hashCode += getCQLQueryModifier().hashCode();
        }
        if (getAssociationPopulationSpecification() != null) {
            _hashCode += getAssociationPopulationSpecification().hashCode();
        }
        return _hashCode;
    }
}
