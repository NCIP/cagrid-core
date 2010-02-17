/**
 * AssociationPopulationSpecification.java
 * 
 * This file was auto-generated from WSDL by the Apache Axis 1.2RC2 Apr 28, 2006
 * (12:42:00 EDT) WSDL2Java emitter.
 */

package org.cagrid.cql2;

public class AssociationPopulationSpecification implements java.io.Serializable {
    private org.cagrid.cql2.PopulationDepth populationDepth;
    private org.cagrid.cql2.NamedAssociationList namedAssociationList;


    public AssociationPopulationSpecification() {
    }


    public AssociationPopulationSpecification(org.cagrid.cql2.NamedAssociationList namedAssociationList,
        org.cagrid.cql2.PopulationDepth populationDepth) {
        this.populationDepth = populationDepth;
        this.namedAssociationList = namedAssociationList;
    }


    /**
     * Gets the populationDepth value for this
     * AssociationPopulationSpecification.
     * 
     * @return populationDepth
     */
    public org.cagrid.cql2.PopulationDepth getPopulationDepth() {
        return populationDepth;
    }


    /**
     * Sets the populationDepth value for this
     * AssociationPopulationSpecification.
     * 
     * @param populationDepth
     */
    public void setPopulationDepth(org.cagrid.cql2.PopulationDepth populationDepth) {
        this.populationDepth = populationDepth;
    }


    /**
     * Gets the namedAssociationList value for this
     * AssociationPopulationSpecification.
     * 
     * @return namedAssociationList
     */
    public org.cagrid.cql2.NamedAssociationList getNamedAssociationList() {
        return namedAssociationList;
    }


    /**
     * Sets the namedAssociationList value for this
     * AssociationPopulationSpecification.
     * 
     * @param namedAssociationList
     */
    public void setNamedAssociationList(org.cagrid.cql2.NamedAssociationList namedAssociationList) {
        this.namedAssociationList = namedAssociationList;
    }


    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof AssociationPopulationSpecification))
            return false;
        AssociationPopulationSpecification other = (AssociationPopulationSpecification) obj;
        if (obj == null)
            return false;
        if (this == obj)
            return true;
        boolean _equals;
        _equals = true
            && ((this.populationDepth == null && other.getPopulationDepth() == null) || (this.populationDepth != null && this.populationDepth
                .equals(other.getPopulationDepth())))
            && ((this.namedAssociationList == null && other.getNamedAssociationList() == null) || (this.namedAssociationList != null && this.namedAssociationList
                .equals(other.getNamedAssociationList())));
        return _equals;
    }


    public synchronized int hashCode() {
        int _hashCode = 1;
        if (getPopulationDepth() != null) {
            _hashCode += getPopulationDepth().hashCode();
        }
        if (getNamedAssociationList() != null) {
            _hashCode += getNamedAssociationList().hashCode();
        }
        return _hashCode;
    }
}
