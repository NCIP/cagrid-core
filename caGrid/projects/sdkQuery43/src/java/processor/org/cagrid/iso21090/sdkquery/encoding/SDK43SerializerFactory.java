package org.cagrid.iso21090.sdkquery.encoding;

import javax.xml.namespace.QName;

import org.apache.axis.encoding.ser.BaseSerializerFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class SDK43SerializerFactory extends BaseSerializerFactory {

	protected static Log LOG = LogFactory.getLog(SDK43SerializerFactory.class.getName());


	public SDK43SerializerFactory(Class<?> javaType, QName xmlType) {
		super(SDK43Serializer.class, xmlType, javaType);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Initializing " + SDK43Serializer.class.getSimpleName() + 
                " for class:" + javaType + " and QName:" + xmlType);
        }
	}
}
