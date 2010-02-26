/**
 * DCQLResult.java
 * 
 * This file was auto-generated from WSDL by the Apache Axis 1.2RC2 Apr 28, 2006
 * (12:42:00 EDT) WSDL2Java emitter.
 */

package org.cagrid.data.dcql.results;

public class DCQLResult implements java.io.Serializable {
    private org.cagrid.cql2.results.CQLQueryResults CQLQueryResults;
    private java.lang.String targetServiceURL; // attribute


    public DCQLResult() {
    }


    public DCQLResult(org.cagrid.cql2.results.CQLQueryResults CQLQueryResults, java.lang.String targetServiceURL) {
        this.CQLQueryResults = CQLQueryResults;
        this.targetServiceURL = targetServiceURL;
    }


    /**
     * Gets the CQLQueryResults value for this DCQLResult.
     * 
     * @return CQLQueryResults
     */
    public org.cagrid.cql2.results.CQLQueryResults getCQLQueryResults() {
        return CQLQueryResults;
    }


    /**
     * Sets the CQLQueryResults value for this DCQLResult.
     * 
     * @param CQLQueryResults
     */
    public void setCQLQueryResults(org.cagrid.cql2.results.CQLQueryResults CQLQueryResults) {
        this.CQLQueryResults = CQLQueryResults;
    }


    /**
     * Gets the targetServiceURL value for this DCQLResult.
     * 
     * @return targetServiceURL
     */
    public java.lang.String getTargetServiceURL() {
        return targetServiceURL;
    }


    /**
     * Sets the targetServiceURL value for this DCQLResult.
     * 
     * @param targetServiceURL
     */
    public void setTargetServiceURL(java.lang.String targetServiceURL) {
        this.targetServiceURL = targetServiceURL;
    }


    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof DCQLResult))
            return false;
        DCQLResult other = (DCQLResult) obj;
        if (obj == null)
            return false;
        if (this == obj)
            return true;
        boolean _equals;
        _equals = true
            && ((this.CQLQueryResults == null && other.getCQLQueryResults() == null) || (this.CQLQueryResults != null && this.CQLQueryResults
                .equals(other.getCQLQueryResults())))
            && ((this.targetServiceURL == null && other.getTargetServiceURL() == null) || (this.targetServiceURL != null && this.targetServiceURL
                .equals(other.getTargetServiceURL())));
        return _equals;
    }


    public synchronized int hashCode() {
        int _hashCode = 1;
        if (getCQLQueryResults() != null) {
            _hashCode += getCQLQueryResults().hashCode();
        }
        if (getTargetServiceURL() != null) {
            _hashCode += getTargetServiceURL().hashCode();
        }
        return _hashCode;
    }
}
