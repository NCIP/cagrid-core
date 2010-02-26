/**
 * ForeignAssociatedObject.java
 * 
 * This file was auto-generated from WSDL by the Apache Axis 1.2RC2 Apr 28, 2006
 * (12:42:00 EDT) WSDL2Java emitter.
 */

package org.cagrid.data.dcql;

/**
 * An association or relationship defined between the parent object and another
 * object located on the data service located at the targetServiceURL. The
 * nature of the relationship is described by the join condition and optional
 * data transformation.
 */
public class ForeignAssociatedObject extends org.cagrid.data.dcql.DCQLAssociatedObject implements java.io.Serializable {
    private org.cagrid.data.dcql.JoinCondition joinCondition;
    private org.cagrid.data.dcql.DataTransformation dataTransformation;
    private java.lang.String targetServiceURL; // attribute


    public ForeignAssociatedObject() {
    }


    public ForeignAssociatedObject(org.cagrid.data.dcql.DataTransformation dataTransformation,
        org.cagrid.data.dcql.JoinCondition joinCondition, java.lang.String targetServiceURL) {
        this.joinCondition = joinCondition;
        this.dataTransformation = dataTransformation;
        this.targetServiceURL = targetServiceURL;
    }


    /**
     * Gets the joinCondition value for this ForeignAssociatedObject.
     * 
     * @return joinCondition
     */
    public org.cagrid.data.dcql.JoinCondition getJoinCondition() {
        return joinCondition;
    }


    /**
     * Sets the joinCondition value for this ForeignAssociatedObject.
     * 
     * @param joinCondition
     */
    public void setJoinCondition(org.cagrid.data.dcql.JoinCondition joinCondition) {
        this.joinCondition = joinCondition;
    }


    /**
     * Gets the dataTransformation value for this ForeignAssociatedObject.
     * 
     * @return dataTransformation
     */
    public org.cagrid.data.dcql.DataTransformation getDataTransformation() {
        return dataTransformation;
    }


    /**
     * Sets the dataTransformation value for this ForeignAssociatedObject.
     * 
     * @param dataTransformation
     */
    public void setDataTransformation(org.cagrid.data.dcql.DataTransformation dataTransformation) {
        this.dataTransformation = dataTransformation;
    }


    /**
     * Gets the targetServiceURL value for this ForeignAssociatedObject.
     * 
     * @return targetServiceURL
     */
    public java.lang.String getTargetServiceURL() {
        return targetServiceURL;
    }


    /**
     * Sets the targetServiceURL value for this ForeignAssociatedObject.
     * 
     * @param targetServiceURL
     */
    public void setTargetServiceURL(java.lang.String targetServiceURL) {
        this.targetServiceURL = targetServiceURL;
    }


    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ForeignAssociatedObject))
            return false;
        ForeignAssociatedObject other = (ForeignAssociatedObject) obj;
        if (obj == null)
            return false;
        if (this == obj)
            return true;
        boolean _equals;
        _equals = super.equals(obj)
            && ((this.joinCondition == null && other.getJoinCondition() == null) || (this.joinCondition != null && this.joinCondition
                .equals(other.getJoinCondition())))
            && ((this.dataTransformation == null && other.getDataTransformation() == null) || (this.dataTransformation != null && this.dataTransformation
                .equals(other.getDataTransformation())))
            && ((this.targetServiceURL == null && other.getTargetServiceURL() == null) || (this.targetServiceURL != null && this.targetServiceURL
                .equals(other.getTargetServiceURL())));
        return _equals;
    }


    public synchronized int hashCode() {
        int _hashCode = super.hashCode();
        if (getJoinCondition() != null) {
            _hashCode += getJoinCondition().hashCode();
        }
        if (getDataTransformation() != null) {
            _hashCode += getDataTransformation().hashCode();
        }
        if (getTargetServiceURL() != null) {
            _hashCode += getTargetServiceURL().hashCode();
        }
        return _hashCode;
    }
}
