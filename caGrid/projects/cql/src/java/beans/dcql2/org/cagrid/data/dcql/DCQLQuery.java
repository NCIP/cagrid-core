/**
 * DCQLQuery.java
 * 
 * This file was auto-generated from WSDL by the Apache Axis 1.2RC2 Apr 28, 2006
 * (12:42:00 EDT) WSDL2Java emitter.
 */

package org.cagrid.data.dcql;

public class DCQLQuery implements java.io.Serializable {
    private org.cagrid.data.dcql.DCQLObject targetObject;
    /**
     * The URL of a data service which should be sent the resulting CQL query
     */
    private java.lang.String[] targetServiceURL;
    private org.cagrid.cql2.CQLQueryModifier queryModifier;


    public DCQLQuery() {
    }


    public DCQLQuery(org.cagrid.cql2.CQLQueryModifier queryModifier, org.cagrid.data.dcql.DCQLObject targetObject,
        java.lang.String[] targetServiceURL) {
        this.targetObject = targetObject;
        this.targetServiceURL = targetServiceURL;
        this.queryModifier = queryModifier;
    }


    /**
     * Gets the targetObject value for this DCQLQuery.
     * 
     * @return targetObject
     */
    public org.cagrid.data.dcql.DCQLObject getTargetObject() {
        return targetObject;
    }


    /**
     * Sets the targetObject value for this DCQLQuery.
     * 
     * @param targetObject
     */
    public void setTargetObject(org.cagrid.data.dcql.DCQLObject targetObject) {
        this.targetObject = targetObject;
    }


    /**
     * Gets the targetServiceURL value for this DCQLQuery.
     * 
     * @return targetServiceURL The URL of a data service which should be sent
     *         the resulting CQL query
     */
    public java.lang.String[] getTargetServiceURL() {
        return targetServiceURL;
    }


    /**
     * Sets the targetServiceURL value for this DCQLQuery.
     * 
     * @param targetServiceURL
     *            The URL of a data service which should be sent the resulting
     *            CQL query
     */
    public void setTargetServiceURL(java.lang.String[] targetServiceURL) {
        this.targetServiceURL = targetServiceURL;
    }


    public java.lang.String getTargetServiceURL(int i) {
        return this.targetServiceURL[i];
    }


    public void setTargetServiceURL(int i, java.lang.String _value) {
        this.targetServiceURL[i] = _value;
    }


    /**
     * Gets the queryModifier value for this DCQLQuery.
     * 
     * @return queryModifier
     */
    public org.cagrid.cql2.CQLQueryModifier getQueryModifier() {
        return queryModifier;
    }


    /**
     * Sets the queryModifier value for this DCQLQuery.
     * 
     * @param queryModifier
     */
    public void setQueryModifier(org.cagrid.cql2.CQLQueryModifier queryModifier) {
        this.queryModifier = queryModifier;
    }


    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof DCQLQuery))
            return false;
        DCQLQuery other = (DCQLQuery) obj;
        if (obj == null)
            return false;
        if (this == obj)
            return true;
        boolean _equals;
        _equals = true
            && ((this.targetObject == null && other.getTargetObject() == null) || (this.targetObject != null && this.targetObject
                .equals(other.getTargetObject())))
            && ((this.targetServiceURL == null && other.getTargetServiceURL() == null) || (this.targetServiceURL != null && java.util.Arrays
                .equals(this.targetServiceURL, other.getTargetServiceURL())))
            && ((this.queryModifier == null && other.getQueryModifier() == null) || (this.queryModifier != null && this.queryModifier
                .equals(other.getQueryModifier())));
        return _equals;
    }


    public synchronized int hashCode() {
        int _hashCode = 1;
        if (getTargetObject() != null) {
            _hashCode += getTargetObject().hashCode();
        }
        if (getTargetServiceURL() != null) {
            for (int i = 0; i < java.lang.reflect.Array.getLength(getTargetServiceURL()); i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getTargetServiceURL(), i);
                if (obj != null && !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getQueryModifier() != null) {
            _hashCode += getQueryModifier().hashCode();
        }
        return _hashCode;
    }
}
