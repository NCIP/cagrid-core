/**
 * CQLQueryResults.java
 * 
 * This file was auto-generated from WSDL by the Apache Axis 1.2RC2 Apr 28, 2006
 * (12:42:00 EDT) WSDL2Java emitter.
 */

package org.cagrid.cql2.results;

/**
 * Results from a CQL query executed against a caGrid data service
 */
public class CQLQueryResults implements java.io.Serializable {
    private org.cagrid.cql2.results.CQLObjectResult[] objectResult;
    private org.cagrid.cql2.results.CQLAttributeResult[] attributeResult;
    private org.cagrid.cql2.results.CQLAggregateResult aggregationResult;
    private org.cagrid.cql2.results.ExtendedCQLResult extendedResult;
    private java.lang.String targetClassname; // attribute


    public CQLQueryResults() {
    }


    public CQLQueryResults(org.cagrid.cql2.results.CQLAggregateResult aggregationResult,
        org.cagrid.cql2.results.CQLAttributeResult[] attributeResult,
        org.cagrid.cql2.results.ExtendedCQLResult extendedResult,
        org.cagrid.cql2.results.CQLObjectResult[] objectResult, java.lang.String targetClassname) {
        this.objectResult = objectResult;
        this.attributeResult = attributeResult;
        this.aggregationResult = aggregationResult;
        this.extendedResult = extendedResult;
        this.targetClassname = targetClassname;
    }


    /**
     * Gets the objectResult value for this CQLQueryResults.
     * 
     * @return objectResult
     */
    public org.cagrid.cql2.results.CQLObjectResult[] getObjectResult() {
        return objectResult;
    }


    /**
     * Sets the objectResult value for this CQLQueryResults.
     * 
     * @param objectResult
     */
    public void setObjectResult(org.cagrid.cql2.results.CQLObjectResult[] objectResult) {
        this.objectResult = objectResult;
    }


    public org.cagrid.cql2.results.CQLObjectResult getObjectResult(int i) {
        return this.objectResult[i];
    }


    public void setObjectResult(int i, org.cagrid.cql2.results.CQLObjectResult _value) {
        this.objectResult[i] = _value;
    }


    /**
     * Gets the attributeResult value for this CQLQueryResults.
     * 
     * @return attributeResult
     */
    public org.cagrid.cql2.results.CQLAttributeResult[] getAttributeResult() {
        return attributeResult;
    }


    /**
     * Sets the attributeResult value for this CQLQueryResults.
     * 
     * @param attributeResult
     */
    public void setAttributeResult(org.cagrid.cql2.results.CQLAttributeResult[] attributeResult) {
        this.attributeResult = attributeResult;
    }


    public org.cagrid.cql2.results.CQLAttributeResult getAttributeResult(int i) {
        return this.attributeResult[i];
    }


    public void setAttributeResult(int i, org.cagrid.cql2.results.CQLAttributeResult _value) {
        this.attributeResult[i] = _value;
    }


    /**
     * Gets the aggregationResult value for this CQLQueryResults.
     * 
     * @return aggregationResult
     */
    public org.cagrid.cql2.results.CQLAggregateResult getAggregationResult() {
        return aggregationResult;
    }


    /**
     * Sets the aggregationResult value for this CQLQueryResults.
     * 
     * @param aggregationResult
     */
    public void setAggregationResult(org.cagrid.cql2.results.CQLAggregateResult aggregationResult) {
        this.aggregationResult = aggregationResult;
    }


    /**
     * Gets the extendedResult value for this CQLQueryResults.
     * 
     * @return extendedResult
     */
    public org.cagrid.cql2.results.ExtendedCQLResult getExtendedResult() {
        return extendedResult;
    }


    /**
     * Sets the extendedResult value for this CQLQueryResults.
     * 
     * @param extendedResult
     */
    public void setExtendedResult(org.cagrid.cql2.results.ExtendedCQLResult extendedResult) {
        this.extendedResult = extendedResult;
    }


    /**
     * Gets the targetClassname value for this CQLQueryResults.
     * 
     * @return targetClassname
     */
    public java.lang.String getTargetClassname() {
        return targetClassname;
    }


    /**
     * Sets the targetClassname value for this CQLQueryResults.
     * 
     * @param targetClassname
     */
    public void setTargetClassname(java.lang.String targetClassname) {
        this.targetClassname = targetClassname;
    }


    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof CQLQueryResults))
            return false;
        CQLQueryResults other = (CQLQueryResults) obj;
        if (obj == null)
            return false;
        if (this == obj)
            return true;
        boolean _equals;
        _equals = true
            && ((this.objectResult == null && other.getObjectResult() == null) || (this.objectResult != null && java.util.Arrays
                .equals(this.objectResult, other.getObjectResult())))
            && ((this.attributeResult == null && other.getAttributeResult() == null) || (this.attributeResult != null && java.util.Arrays
                .equals(this.attributeResult, other.getAttributeResult())))
            && ((this.aggregationResult == null && other.getAggregationResult() == null) || (this.aggregationResult != null && this.aggregationResult
                .equals(other.getAggregationResult())))
            && ((this.extendedResult == null && other.getExtendedResult() == null) || (this.extendedResult != null && this.extendedResult
                .equals(other.getExtendedResult())))
            && ((this.targetClassname == null && other.getTargetClassname() == null) || (this.targetClassname != null && this.targetClassname
                .equals(other.getTargetClassname())));
        return _equals;
    }


    public synchronized int hashCode() {
        int _hashCode = 1;
        if (getObjectResult() != null) {
            for (int i = 0; i < java.lang.reflect.Array.getLength(getObjectResult()); i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getObjectResult(), i);
                if (obj != null && !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getAttributeResult() != null) {
            for (int i = 0; i < java.lang.reflect.Array.getLength(getAttributeResult()); i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getAttributeResult(), i);
                if (obj != null && !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getAggregationResult() != null) {
            _hashCode += getAggregationResult().hashCode();
        }
        if (getExtendedResult() != null) {
            _hashCode += getExtendedResult().hashCode();
        }
        if (getTargetClassname() != null) {
            _hashCode += getTargetClassname().hashCode();
        }
        return _hashCode;
    }
}
