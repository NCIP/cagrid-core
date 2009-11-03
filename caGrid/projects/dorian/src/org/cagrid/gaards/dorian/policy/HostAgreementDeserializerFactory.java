package org.cagrid.gaards.dorian.policy;

import javax.xml.namespace.QName;

import org.apache.axis.encoding.ser.BaseDeserializerFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class HostAgreementDeserializerFactory extends BaseDeserializerFactory {

    protected static Log LOG = LogFactory.getLog(HostAgreementDeserializerFactory.class.getName());


    public HostAgreementDeserializerFactory(Class javaType, QName xmlType) {
        super(HostAgreementDeserializer.class, xmlType, javaType);
        LOG.debug("Initializing SDKDeserializerFactory for class:" + javaType + " and QName:" + xmlType);
    }
}
