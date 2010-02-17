/**
 * NamedAssociation.java
 * 
 * This file was auto-generated from WSDL by the Apache Axis 1.2RC2 Apr 28, 2006
 * (12:42:00 EDT) WSDL2Java emitter.
 */

package org.cagrid.cql2;

public class NamedAssociation implements java.io.Serializable {
    private org.cagrid.cql2.NamedAssociationList namedAssociationList;
    private org.cagrid.cql2.PopulationDepth populationDepth;
    private java.lang.String endName; // attribute
    private java.lang.String _instanceof; // attribute


    public NamedAssociation() {
    }


    public NamedAssociation(java.lang.String _instanceof, java.lang.String endName,
        org.cagrid.cql2.NamedAssociationList namedAssociationList, org.cagrid.cql2.PopulationDepth populationDepth) {
        this.namedAssociationList = namedAssociationList;
        this.populationDepth = populationDepth;
        this.endName = endName;
        this._instanceof = _instanceof;
    }


    /**
     * Gets the namedAssociationList value for this NamedAssociation.
     * 
     * @return namedAssociationList
     */
    public org.cagrid.cql2.NamedAssociationList getNamedAssociationList() {
        return namedAssociationList;
    }


    /**
     * Sets the namedAssociationList value for this NamedAssociation.
     * 
     * @param namedAssociationList
     */
    public void setNamedAssociationList(org.cagrid.cql2.NamedAssociationList namedAssociationList) {
        this.namedAssociationList = namedAssociationList;
    }


    /**
     * Gets the populationDepth value for this NamedAssociation.
     * 
     * @return populationDepth
     */
    public org.cagrid.cql2.PopulationDepth getPopulationDepth() {
        return populationDepth;
    }


    /**
     * Sets the populationDepth value for this NamedAssociation.
     * 
     * @param populationDepth
     */
    public void setPopulationDepth(org.cagrid.cql2.PopulationDepth populationDepth) {
        this.populationDepth = populationDepth;
    }


    /**
     * Gets the endName value for this NamedAssociation.
     * 
     * @return endName
     */
    public java.lang.String getEndName() {
        return endName;
    }


    /**
     * Sets the endName value for this NamedAssociation.
     * 
     * @param endName
     */
    public void setEndName(java.lang.String endName) {
        this.endName = endName;
    }


    /**
     * Gets the _instanceof value for this NamedAssociation.
     * 
     * @return _instanceof
     */
    public java.lang.String get_instanceof() {
        return _instanceof;
    }


    /**
     * Sets the _instanceof value for this NamedAssociation.
     * 
     * @param _instanceof
     */
    public void set_instanceof(java.lang.String _instanceof) {
        this._instanceof = _instanceof;
    }


    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof NamedAssociation))
            return false;
        NamedAssociation other = (NamedAssociation) obj;
        if (obj == null)
            return false;
        if (this == obj)
            return true;
        boolean _equals;
        _equals = true
            && ((this.namedAssociationList == null && other.getNamedAssociationList() == null) || (this.namedAssociationList != null && this.namedAssociationList
                .equals(other.getNamedAssociationList())))
            && ((this.populationDepth == null && other.getPopulationDepth() == null) || (this.populationDepth != null && this.populationDepth
                .equals(other.getPopulationDepth())))
            && ((this.endName == null && other.getEndName() == null) || (this.endName != null && this.endName
                .equals(other.getEndName())))
            && ((this._instanceof == null && other.get_instanceof() == null) || (this._instanceof != null && this._instanceof
                .equals(other.get_instanceof())));
        return _equals;
    }


    public synchronized int hashCode() {
        int _hashCode = 1;
        if (getNamedAssociationList() != null) {
            _hashCode += getNamedAssociationList().hashCode();
        }
        if (getPopulationDepth() != null) {
            _hashCode += getPopulationDepth().hashCode();
        }
        if (getEndName() != null) {
            _hashCode += getEndName().hashCode();
        }
        if (get_instanceof() != null) {
            _hashCode += get_instanceof().hashCode();
        }
        return _hashCode;
    }
}
