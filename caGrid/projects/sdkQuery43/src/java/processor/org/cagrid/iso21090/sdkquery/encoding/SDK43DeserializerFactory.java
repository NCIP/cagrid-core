package org.cagrid.iso21090.sdkquery.encoding;

import javax.xml.namespace.QName;

import org.apache.axis.encoding.ser.BaseDeserializerFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class SDK43DeserializerFactory extends BaseDeserializerFactory {

	protected static Log LOG = LogFactory.getLog(SDK43DeserializerFactory.class.getName());


	public SDK43DeserializerFactory(Class<?> javaType, QName xmlType) {
		super(SDK43Deserializer.class, xmlType, javaType);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Initializing " + SDK43Deserializer.class.getSimpleName() + 
                " for class:" + javaType + " and QName:" + xmlType);
        }
	}
}
