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
    private AnyNode _any;


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
    public AnyNode get_any() {
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
            && ((this._any == null && other.get_any() == null) || (this._any != null && this._any.equals(other.get_any())));
        return _equals;
    }


    public synchronized int hashCode() {
        int _hashCode = super.hashCode();
        if (get_any() != null) {
            for (int i = 0; i < java.lang.reflect.Array.getLength(get_any()); i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(get_any(), i);
                if (obj != null && !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        return _hashCode;
    }
}
