/**
 * JoinCondition.java
 * 
 * This file was auto-generated from WSDL by the Apache Axis 1.2RC2 Apr 28, 2006
 * (12:42:00 EDT) WSDL2Java emitter.
 */

package org.cagrid.data.dcql;

/**
 * Specifies a relationship, defined by the predicate, between a local attribute
 * and a remote attribute.
 */
public class JoinCondition implements java.io.Serializable {
    private java.lang.String localAttributeName; // attribute
    private org.cagrid.cql2.BinaryPredicate predicate; // attribute
    private java.lang.String foreignAttributeName; // attribute


    public JoinCondition() {
    }


    public JoinCondition(java.lang.String foreignAttributeName, java.lang.String localAttributeName,
        org.cagrid.cql2.BinaryPredicate predicate) {
        this.localAttributeName = localAttributeName;
        this.predicate = predicate;
        this.foreignAttributeName = foreignAttributeName;
    }


    /**
     * Gets the localAttributeName value for this JoinCondition.
     * 
     * @return localAttributeName
     */
    public java.lang.String getLocalAttributeName() {
        return localAttributeName;
    }


    /**
     * Sets the localAttributeName value for this JoinCondition.
     * 
     * @param localAttributeName
     */
    public void setLocalAttributeName(java.lang.String localAttributeName) {
        this.localAttributeName = localAttributeName;
    }


    /**
     * Gets the predicate value for this JoinCondition.
     * 
     * @return predicate
     */
    public org.cagrid.cql2.BinaryPredicate getPredicate() {
        return predicate;
    }


    /**
     * Sets the predicate value for this JoinCondition.
     * 
     * @param predicate
     */
    public void setPredicate(org.cagrid.cql2.BinaryPredicate predicate) {
        this.predicate = predicate;
    }


    /**
     * Gets the foreignAttributeName value for this JoinCondition.
     * 
     * @return foreignAttributeName
     */
    public java.lang.String getForeignAttributeName() {
        return foreignAttributeName;
    }


    /**
     * Sets the foreignAttributeName value for this JoinCondition.
     * 
     * @param foreignAttributeName
     */
    public void setForeignAttributeName(java.lang.String foreignAttributeName) {
        this.foreignAttributeName = foreignAttributeName;
    }


    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof JoinCondition))
            return false;
        JoinCondition other = (JoinCondition) obj;
        if (obj == null)
            return false;
        if (this == obj)
            return true;
        boolean _equals;
        _equals = true
            && ((this.localAttributeName == null && other.getLocalAttributeName() == null) || (this.localAttributeName != null && this.localAttributeName
                .equals(other.getLocalAttributeName())))
            && ((this.predicate == null && other.getPredicate() == null) || (this.predicate != null && this.predicate
                .equals(other.getPredicate())))
            && ((this.foreignAttributeName == null && other.getForeignAttributeName() == null) || (this.foreignAttributeName != null && this.foreignAttributeName
                .equals(other.getForeignAttributeName())));
        return _equals;
    }


    public synchronized int hashCode() {
        int _hashCode = 1;
        if (getLocalAttributeName() != null) {
            _hashCode += getLocalAttributeName().hashCode();
        }
        if (getPredicate() != null) {
            _hashCode += getPredicate().hashCode();
        }
        if (getForeignAttributeName() != null) {
            _hashCode += getForeignAttributeName().hashCode();
        }
        return _hashCode;
    }
}
