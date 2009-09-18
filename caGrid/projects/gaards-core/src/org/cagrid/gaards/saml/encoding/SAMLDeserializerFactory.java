package org.cagrid.gaards.saml.encoding;

import javax.xml.namespace.QName;

import org.apache.axis.encoding.ser.BaseDeserializerFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class SAMLDeserializerFactory extends BaseDeserializerFactory {

	protected static Log LOG = LogFactory.getLog(SAMLDeserializerFactory.class.getName());


	public SAMLDeserializerFactory(Class javaType, QName xmlType) {
		super(SAMLDeserializer.class, xmlType, javaType);
		LOG.debug("Initializing SDKDeserializerFactory for class:" + javaType + " and QName:" + xmlType);
	}
}
