package org.cagrid.gaards.saml.encoding;

import javax.xml.namespace.QName;

import org.apache.axis.encoding.ser.BaseSerializerFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class SAMLSerializerFactory extends BaseSerializerFactory {

	protected static Log LOG = LogFactory.getLog(SAMLSerializerFactory.class.getName());


	public SAMLSerializerFactory(Class javaType, QName xmlType) {
		super(SAMLSerializer.class, xmlType, javaType);
		LOG.debug("Initializing SDKSerializerFactory for class:" + javaType + " and QName:" + xmlType);
	}
}
