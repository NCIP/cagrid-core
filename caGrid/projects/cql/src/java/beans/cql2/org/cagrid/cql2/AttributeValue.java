/**
 * AttributeValue.java
 * 
 * This file was auto-generated from WSDL by the Apache Axis 1.2RC2 Apr 28, 2006
 * (12:42:00 EDT) WSDL2Java emitter.
 */

package org.cagrid.cql2;

public class AttributeValue implements java.io.Serializable {
    private java.lang.String stringValue;
    private java.util.Date dateValue;
    private org.apache.axis.types.Time timeValue;
    private java.lang.Long longValue;
    private java.lang.Integer integerValue;
    private java.lang.Boolean booleanValue;
    private java.lang.Double doubleValue;


    public AttributeValue() {
    }


    public AttributeValue(java.lang.Boolean booleanValue, java.util.Date dateValue, java.lang.Double doubleValue,
        java.lang.Integer integerValue, java.lang.Long longValue, java.lang.String stringValue,
        org.apache.axis.types.Time timeValue) {
        this.stringValue = stringValue;
        this.dateValue = dateValue;
        this.timeValue = timeValue;
        this.longValue = longValue;
        this.integerValue = integerValue;
        this.booleanValue = booleanValue;
        this.doubleValue = doubleValue;
    }


    /**
     * Gets the stringValue value for this AttributeValue.
     * 
     * @return stringValue
     */
    public java.lang.String getStringValue() {
        return stringValue;
    }


    /**
     * Sets the stringValue value for this AttributeValue.
     * 
     * @param stringValue
     */
    public void setStringValue(java.lang.String stringValue) {
        this.stringValue = stringValue;
    }


    /**
     * Gets the dateValue value for this AttributeValue.
     * 
     * @return dateValue
     */
    public java.util.Date getDateValue() {
        return dateValue;
    }


    /**
     * Sets the dateValue value for this AttributeValue.
     * 
     * @param dateValue
     */
    public void setDateValue(java.util.Date dateValue) {
        this.dateValue = dateValue;
    }


    /**
     * Gets the timeValue value for this AttributeValue.
     * 
     * @return timeValue
     */
    public org.apache.axis.types.Time getTimeValue() {
        return timeValue;
    }


    /**
     * Sets the timeValue value for this AttributeValue.
     * 
     * @param timeValue
     */
    public void setTimeValue(org.apache.axis.types.Time timeValue) {
        this.timeValue = timeValue;
    }


    /**
     * Gets the longValue value for this AttributeValue.
     * 
     * @return longValue
     */
    public java.lang.Long getLongValue() {
        return longValue;
    }


    /**
     * Sets the longValue value for this AttributeValue.
     * 
     * @param longValue
     */
    public void setLongValue(java.lang.Long longValue) {
        this.longValue = longValue;
    }


    /**
     * Gets the integerValue value for this AttributeValue.
     * 
     * @return integerValue
     */
    public java.lang.Integer getIntegerValue() {
        return integerValue;
    }


    /**
     * Sets the integerValue value for this AttributeValue.
     * 
     * @param integerValue
     */
    public void setIntegerValue(java.lang.Integer integerValue) {
        this.integerValue = integerValue;
    }


    /**
     * Gets the booleanValue value for this AttributeValue.
     * 
     * @return booleanValue
     */
    public java.lang.Boolean getBooleanValue() {
        return booleanValue;
    }


    /**
     * Sets the booleanValue value for this AttributeValue.
     * 
     * @param booleanValue
     */
    public void setBooleanValue(java.lang.Boolean booleanValue) {
        this.booleanValue = booleanValue;
    }


    /**
     * Gets the doubleValue value for this AttributeValue.
     * 
     * @return doubleValue
     */
    public java.lang.Double getDoubleValue() {
        return doubleValue;
    }


    /**
     * Sets the doubleValue value for this AttributeValue.
     * 
     * @param doubleValue
     */
    public void setDoubleValue(java.lang.Double doubleValue) {
        this.doubleValue = doubleValue;
    }


    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof AttributeValue))
            return false;
        AttributeValue other = (AttributeValue) obj;
        if (obj == null)
            return false;
        if (this == obj)
            return true;
        boolean _equals;
        _equals = true
            && ((this.stringValue == null && other.getStringValue() == null) || (this.stringValue != null && this.stringValue
                .equals(other.getStringValue())))
            && ((this.dateValue == null && other.getDateValue() == null) || (this.dateValue != null && this.dateValue
                .equals(other.getDateValue())))
            && ((this.timeValue == null && other.getTimeValue() == null) || (this.timeValue != null && this.timeValue
                .equals(other.getTimeValue())))
            && ((this.longValue == null && other.getLongValue() == null) || (this.longValue != null && this.longValue
                .equals(other.getLongValue())))
            && ((this.integerValue == null && other.getIntegerValue() == null) || (this.integerValue != null && this.integerValue
                .equals(other.getIntegerValue())))
            && ((this.booleanValue == null && other.getBooleanValue() == null) || (this.booleanValue != null && this.booleanValue
                .equals(other.getBooleanValue())))
            && ((this.doubleValue == null && other.getDoubleValue() == null) || (this.doubleValue != null && this.doubleValue
                .equals(other.getDoubleValue())));
        return _equals;
    }


    public synchronized int hashCode() {
        int _hashCode = 1;
        if (getStringValue() != null) {
            _hashCode += getStringValue().hashCode();
        }
        if (getDateValue() != null) {
            _hashCode += getDateValue().hashCode();
        }
        if (getTimeValue() != null) {
            _hashCode += getTimeValue().hashCode();
        }
        if (getLongValue() != null) {
            _hashCode += getLongValue().hashCode();
        }
        if (getIntegerValue() != null) {
            _hashCode += getIntegerValue().hashCode();
        }
        if (getBooleanValue() != null) {
            _hashCode += getBooleanValue().hashCode();
        }
        if (getDoubleValue() != null) {
            _hashCode += getDoubleValue().hashCode();
        }
        return _hashCode;
    }
}
