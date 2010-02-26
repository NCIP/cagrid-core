/**
 * CQLExtension.java
 * 
 * This file was auto-generated from WSDL by the Apache Axis 1.2RC2 Apr 28, 2006
 * (12:42:00 EDT) WSDL2Java emitter.
 */

package org.cagrid.cql2;

import org.exolab.castor.types.AnyNode;

/**
 * Wraps a custom extension to the CQL 2 query language
 */
public class CQLExtension implements java.io.Serializable {
    private AnyNode _any;
    private java.lang.Boolean mustUnderstand; // attribute


    public CQLExtension() {
    }


    public CQLExtension(AnyNode _any, java.lang.Boolean mustUnderstand) {
        this._any = _any;
        this.mustUnderstand = mustUnderstand;
    }


    /**
     * Gets the _any value for this CQLExtension.
     * 
     * @return _any
     */
    public Object get_any() {
        return _any;
    }


    /**
     * Sets the _any value for this CQLExtension.
     * 
     * @param _any
     */
    public void set_any(AnyNode _any) {
        this._any = _any;
    }


    /**
     * Gets the mustUnderstand value for this CQLExtension.
     * 
     * @return mustUnderstand
     */
    public java.lang.Boolean getMustUnderstand() {
        return mustUnderstand;
    }


    /**
     * Sets the mustUnderstand value for this CQLExtension.
     * 
     * @param mustUnderstand
     */
    public void setMustUnderstand(java.lang.Boolean mustUnderstand) {
        this.mustUnderstand = mustUnderstand;
    }


    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof CQLExtension))
            return false;
        CQLExtension other = (CQLExtension) obj;
        if (obj == null)
            return false;
        if (this == obj)
            return true;
        boolean _equals;
        _equals = true
            && ((this._any == null && other.get_any() == null) || (this._any != null && this._any.equals(other.get_any())))
            && ((this.mustUnderstand == null && other.getMustUnderstand() == null) || (this.mustUnderstand != null && this.mustUnderstand
                .equals(other.getMustUnderstand())));
        return _equals;
    }


    public synchronized int hashCode() {
        int _hashCode = 1;
        if (get_any() != null) {
            _hashCode += get_any().hashCode();
        }
        if (getMustUnderstand() != null) {
            _hashCode += getMustUnderstand().hashCode();
        }
        return _hashCode;
    }
}
