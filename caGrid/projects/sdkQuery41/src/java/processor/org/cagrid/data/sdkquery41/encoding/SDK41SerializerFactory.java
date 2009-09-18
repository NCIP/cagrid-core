package org.cagrid.data.sdkquery41.encoding;

import javax.xml.namespace.QName;

import org.apache.axis.encoding.ser.BaseSerializerFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class SDK41SerializerFactory extends BaseSerializerFactory {

	protected static Log LOG = LogFactory.getLog(SDK41SerializerFactory.class.getName());


	public SDK41SerializerFactory(Class javaType, QName xmlType) {
		super(SDK41Serializer.class, xmlType, javaType);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Initializing " + SDK41Serializer.class.getSimpleName() + 
                " for class:" + javaType + " and QName:" + xmlType);
        }
	}
}
