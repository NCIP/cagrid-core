package org.cagrid.data.sdkquery42.encoding;

import javax.xml.namespace.QName;

import org.apache.axis.encoding.ser.BaseSerializerFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class SDK42SerializerFactory extends BaseSerializerFactory {

	protected static Log LOG = LogFactory.getLog(SDK42SerializerFactory.class.getName());


	public SDK42SerializerFactory(Class<?> javaType, QName xmlType) {
		super(SDK42Serializer.class, xmlType, javaType);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Initializing " + SDK42Serializer.class.getSimpleName() + 
                " for class:" + javaType + " and QName:" + xmlType);
        }
	}
}
