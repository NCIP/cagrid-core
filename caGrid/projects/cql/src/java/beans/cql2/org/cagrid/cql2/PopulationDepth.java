/**
 * PopulationDepth.java
 * 
 * This file was auto-generated from WSDL by the Apache Axis 1.2RC2 Apr 28, 2006
 * (12:42:00 EDT) WSDL2Java emitter.
 */

package org.cagrid.cql2;

public class PopulationDepth implements java.io.Serializable {
    private int depth; // attribute


    public PopulationDepth() {
    }


    public PopulationDepth(int depth) {
        this.depth = depth;
    }


    /**
     * Gets the depth value for this PopulationDepth.
     * 
     * @return depth
     */
    public int getDepth() {
        return depth;
    }


    /**
     * Sets the depth value for this PopulationDepth.
     * 
     * @param depth
     */
    public void setDepth(int depth) {
        this.depth = depth;
    }


    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof PopulationDepth))
            return false;
        PopulationDepth other = (PopulationDepth) obj;
        if (obj == null)
            return false;
        if (this == obj)
            return true;
        boolean _equals;
        _equals = true && this.depth == other.getDepth();
        return _equals;
    }


    public synchronized int hashCode() {
        int _hashCode = 1;
        _hashCode += getDepth();
        return _hashCode;
    }
}
