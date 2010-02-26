/**
 * ExtendedCQLResult.java
 * 
 * This file was auto-generated from WSDL by the Apache Axis 1.2RC2 Apr 28, 2006
 * (12:42:00 EDT) WSDL2Java emitter.
 */

package org.cagrid.cql2.results;

import org.exolab.castor.types.AnyNode;


/**
 * Result type for extensibility
 */
public class ExtendedCQLResult extends org.cagrid.cql2.results.CQLResult implements java.io.Serializable {
    private Object _any;


    public ExtendedCQLResult() {
    }


    public ExtendedCQLResult(AnyNode _any) {
        this._any = _any;
    }


    /**
     * Gets the _any value for this ExtendedCQLResult.
     * 
     * @return _any
     */
    public Object get_any() {
        return _any;
    }


    /**
     * Sets the _any value for this ExtendedCQLResult.
     * 
     * @param _any
     */
    public void set_any(AnyNode _any) {
        this._any = _any;
    }


    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ExtendedCQLResult))
            return false;
        ExtendedCQLResult other = (ExtendedCQLResult) obj;
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
