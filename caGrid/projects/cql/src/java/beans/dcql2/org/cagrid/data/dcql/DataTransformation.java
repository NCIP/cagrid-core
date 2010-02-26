/**
 * DataTransformation.java
 * 
 * This file was auto-generated from WSDL by the Apache Axis 1.2RC2 Apr 28, 2006
 * (12:42:00 EDT) WSDL2Java emitter.
 */

package org.cagrid.data.dcql;

/**
 * Specifies a transformation to apply to attribute data values
 */
public class DataTransformation implements java.io.Serializable {
    private org.cagrid.data.dcql.DataTransformationOperation operation;
    private org.cagrid.cql2.CQLExtension transformationExtension;


    public DataTransformation() {
    }


    public DataTransformation(org.cagrid.data.dcql.DataTransformationOperation operation,
        org.cagrid.cql2.CQLExtension transformationExtension) {
        this.operation = operation;
        this.transformationExtension = transformationExtension;
    }


    /**
     * Gets the operation value for this DataTransformation.
     * 
     * @return operation
     */
    public org.cagrid.data.dcql.DataTransformationOperation getOperation() {
        return operation;
    }


    /**
     * Sets the operation value for this DataTransformation.
     * 
     * @param operation
     */
    public void setOperation(org.cagrid.data.dcql.DataTransformationOperation operation) {
        this.operation = operation;
    }


    /**
     * Gets the transformationExtension value for this DataTransformation.
     * 
     * @return transformationExtension
     */
    public org.cagrid.cql2.CQLExtension getTransformationExtension() {
        return transformationExtension;
    }


    /**
     * Sets the transformationExtension value for this DataTransformation.
     * 
     * @param transformationExtension
     */
    public void setTransformationExtension(org.cagrid.cql2.CQLExtension transformationExtension) {
        this.transformationExtension = transformationExtension;
    }


    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof DataTransformation))
            return false;
        DataTransformation other = (DataTransformation) obj;
        if (obj == null)
            return false;
        if (this == obj)
            return true;
        boolean _equals;
        _equals = true
            && ((this.operation == null && other.getOperation() == null) || (this.operation != null && this.operation
                .equals(other.getOperation())))
            && ((this.transformationExtension == null && other.getTransformationExtension() == null) || (this.transformationExtension != null && this.transformationExtension
                .equals(other.getTransformationExtension())));
        return _equals;
    }


    public synchronized int hashCode() {
        int _hashCode = 1;
        if (getOperation() != null) {
            _hashCode += getOperation().hashCode();
        }
        if (getTransformationExtension() != null) {
            _hashCode += getTransformationExtension().hashCode();
        }
        return _hashCode;
    }
}
