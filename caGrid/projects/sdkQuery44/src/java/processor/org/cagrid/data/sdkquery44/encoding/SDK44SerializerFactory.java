package org.cagrid.data.sdkquery44.encoding;

import javax.xml.namespace.QName;

import org.apache.axis.encoding.ser.BaseSerializerFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class SDK44SerializerFactory extends BaseSerializerFactory {

	protected static Log LOG = LogFactory.getLog(SDK44SerializerFactory.class.getName());


	public SDK44SerializerFactory(Class<?> javaType, QName xmlType) {
		super(SDK44Serializer.class, xmlType, javaType);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Initializing " + SDK44Serializer.class.getSimpleName() + 
                " for class:" + javaType + " and QName:" + xmlType);
        }
	}
}
