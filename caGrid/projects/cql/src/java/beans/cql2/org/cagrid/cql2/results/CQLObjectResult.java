/**
 * CQLObjectResult.java
 * 
 * This file was auto-generated from WSDL by the Apache Axis 1.2RC2 Apr 28, 2006
 * (12:42:00 EDT) WSDL2Java emitter.
 */

package org.cagrid.cql2.results;

import org.exolab.castor.types.AnyNode;


/**
 * Result object
 */
public class CQLObjectResult extends org.cagrid.cql2.results.CQLResult implements java.io.Serializable {
    private Object _any;


    public CQLObjectResult() {
    }


    public CQLObjectResult(AnyNode _any) {
        this._any = _any;
    }


    /**
     * Gets the _any value for this CQLObjectResult.
     * 
     * @return _any
     */
    public Object get_any() {
        return _any;
    }


    /**
     * Sets the _any value for this CQLObjectResult.
     * 
     * @param _any
     */
    public void set_any(AnyNode _any) {
        this._any = _any;
    }


    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof CQLObjectResult))
            return false;
        CQLObjectResult other = (CQLObjectResult) obj;
        if (obj == null)
            return false;
        if (this == obj)
            return true;
        boolean _equals;
        _equals = super.equals(obj)
            && ((this._any == null && other.get_any() == null) || (this._any != null && this._any.equals(other
                .get_any())));
        return _equals;
    }


    public synchronized int hashCode() {
        int _hashCode = super.hashCode();
        if (get_any() != null) {
            _hashCode += get_any().hashCode();
        }
        return _hashCode;
    }
}
