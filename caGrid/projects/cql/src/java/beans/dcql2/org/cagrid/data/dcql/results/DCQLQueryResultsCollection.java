/**
 * DCQLQueryResultsCollection.java
 * 
 * This file was auto-generated from WSDL by the Apache Axis 1.2RC2 Apr 28, 2006
 * (12:42:00 EDT) WSDL2Java emitter.
 */

package org.cagrid.data.dcql.results;

/**
 * Represents a collection of DCQL2 Results
 */
public class DCQLQueryResultsCollection implements java.io.Serializable {
    private org.cagrid.data.dcql.results.DCQLResult[] DCQLResult;


    public DCQLQueryResultsCollection() {
    }


    public DCQLQueryResultsCollection(org.cagrid.data.dcql.results.DCQLResult[] DCQLResult) {
        this.DCQLResult = DCQLResult;
    }


    /**
     * Gets the DCQLResult value for this DCQLQueryResultsCollection.
     * 
     * @return DCQLResult
     */
    public org.cagrid.data.dcql.results.DCQLResult[] getDCQLResult() {
        return DCQLResult;
    }


    /**
     * Sets the DCQLResult value for this DCQLQueryResultsCollection.
     * 
     * @param DCQLResult
     */
    public void setDCQLResult(org.cagrid.data.dcql.results.DCQLResult[] DCQLResult) {
        this.DCQLResult = DCQLResult;
    }


    public org.cagrid.data.dcql.results.DCQLResult getDCQLResult(int i) {
        return this.DCQLResult[i];
    }


    public void setDCQLResult(int i, org.cagrid.data.dcql.results.DCQLResult _value) {
        this.DCQLResult[i] = _value;
    }


    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof DCQLQueryResultsCollection))
            return false;
        DCQLQueryResultsCollection other = (DCQLQueryResultsCollection) obj;
        if (obj == null)
            return false;
        if (this == obj)
            return true;
        boolean _equals;
        _equals = true && ((this.DCQLResult == null && other.getDCQLResult() == null) || (this.DCQLResult != null && java.util.Arrays
            .equals(this.DCQLResult, other.getDCQLResult())));
        return _equals;
    }


    public synchronized int hashCode() {
        int _hashCode = 1;
        if (getDCQLResult() != null) {
            for (int i = 0; i < java.lang.reflect.Array.getLength(getDCQLResult()); i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getDCQLResult(), i);
                if (obj != null && !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        return _hashCode;
    }
}
