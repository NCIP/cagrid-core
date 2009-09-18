/**
 * Event.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2RC2 Apr 28, 2006 (12:42:00 EDT) WSDL2Java emitter.
 */

package org.cagrid.tools.events;

public class Event implements java.io.Serializable {
    private long eventId;
    private java.lang.String targetId;
    private java.lang.String reportingPartyId;
    private java.lang.String eventType;
    private long occurredAt;
    private java.lang.String message;

    public Event() {
    }

    public Event(
           long eventId,
           java.lang.String eventType,
           java.lang.String message,
           long occurredAt,
           java.lang.String reportingPartyId,
           java.lang.String targetId) {
           this.eventId = eventId;
           this.targetId = targetId;
           this.reportingPartyId = reportingPartyId;
           this.eventType = eventType;
           this.occurredAt = occurredAt;
           this.message = message;
    }


    /**
     * Gets the eventId value for this Event.
     * 
     * @return eventId
     */
    public long getEventId() {
        return eventId;
    }


    /**
     * Sets the eventId value for this Event.
     * 
     * @param eventId
     */
    public void setEventId(long eventId) {
        this.eventId = eventId;
    }


    /**
     * Gets the targetId value for this Event.
     * 
     * @return targetId
     */
    public java.lang.String getTargetId() {
        return targetId;
    }


    /**
     * Sets the targetId value for this Event.
     * 
     * @param targetId
     */
    public void setTargetId(java.lang.String targetId) {
        this.targetId = targetId;
    }


    /**
     * Gets the reportingPartyId value for this Event.
     * 
     * @return reportingPartyId
     */
    public java.lang.String getReportingPartyId() {
        return reportingPartyId;
    }


    /**
     * Sets the reportingPartyId value for this Event.
     * 
     * @param reportingPartyId
     */
    public void setReportingPartyId(java.lang.String reportingPartyId) {
        this.reportingPartyId = reportingPartyId;
    }


    /**
     * Gets the eventType value for this Event.
     * 
     * @return eventType
     */
    public java.lang.String getEventType() {
        return eventType;
    }


    /**
     * Sets the eventType value for this Event.
     * 
     * @param eventType
     */
    public void setEventType(java.lang.String eventType) {
        this.eventType = eventType;
    }


    /**
     * Gets the occurredAt value for this Event.
     * 
     * @return occurredAt
     */
    public long getOccurredAt() {
        return occurredAt;
    }


    /**
     * Sets the occurredAt value for this Event.
     * 
     * @param occurredAt
     */
    public void setOccurredAt(long occurredAt) {
        this.occurredAt = occurredAt;
    }


    /**
     * Gets the message value for this Event.
     * 
     * @return message
     */
    public java.lang.String getMessage() {
        return message;
    }


    /**
     * Sets the message value for this Event.
     * 
     * @param message
     */
    public void setMessage(java.lang.String message) {
        this.message = message;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Event)) return false;
        Event other = (Event) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.eventId == other.getEventId() &&
            ((this.targetId==null && other.getTargetId()==null) || 
             (this.targetId!=null &&
              this.targetId.equals(other.getTargetId()))) &&
            ((this.reportingPartyId==null && other.getReportingPartyId()==null) || 
             (this.reportingPartyId!=null &&
              this.reportingPartyId.equals(other.getReportingPartyId()))) &&
            ((this.eventType==null && other.getEventType()==null) || 
             (this.eventType!=null &&
              this.eventType.equals(other.getEventType()))) &&
            this.occurredAt == other.getOccurredAt() &&
            ((this.message==null && other.getMessage()==null) || 
             (this.message!=null &&
              this.message.equals(other.getMessage())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        _hashCode += new Long(getEventId()).hashCode();
        if (getTargetId() != null) {
            _hashCode += getTargetId().hashCode();
        }
        if (getReportingPartyId() != null) {
            _hashCode += getReportingPartyId().hashCode();
        }
        if (getEventType() != null) {
            _hashCode += getEventType().hashCode();
        }
        _hashCode += new Long(getOccurredAt()).hashCode();
        if (getMessage() != null) {
            _hashCode += getMessage().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Event.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://cagrid.org/tools/events", "Event"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("eventId");
        elemField.setXmlName(new javax.xml.namespace.QName("http://cagrid.org/tools/events", "eventId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("targetId");
        elemField.setXmlName(new javax.xml.namespace.QName("http://cagrid.org/tools/events", "targetId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("reportingPartyId");
        elemField.setXmlName(new javax.xml.namespace.QName("http://cagrid.org/tools/events", "reportingPartyId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("eventType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://cagrid.org/tools/events", "eventType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("occurredAt");
        elemField.setXmlName(new javax.xml.namespace.QName("http://cagrid.org/tools/events", "occurredAt"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("message");
        elemField.setXmlName(new javax.xml.namespace.QName("http://cagrid.org/tools/events", "message"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
