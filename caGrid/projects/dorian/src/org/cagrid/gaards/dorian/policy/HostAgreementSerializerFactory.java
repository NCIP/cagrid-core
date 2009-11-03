package org.cagrid.gaards.dorian.policy;

import javax.xml.namespace.QName;

import org.apache.axis.encoding.ser.BaseSerializerFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class HostAgreementSerializerFactory extends BaseSerializerFactory {

    protected static Log LOG = LogFactory.getLog(HostAgreementSerializerFactory.class.getName());


    public HostAgreementSerializerFactory(Class javaType, QName xmlType) {
        super(HostAgreementSerializer.class, xmlType, javaType);
        LOG.debug("Initializing SDKSerializerFactory for class:" + javaType + " and QName:" + xmlType);
    }
}
