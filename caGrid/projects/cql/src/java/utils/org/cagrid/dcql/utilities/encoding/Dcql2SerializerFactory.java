package org.cagrid.dcql.utilities.encoding;

import javax.xml.namespace.QName;

import org.apache.axis.encoding.ser.BaseSerializerFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class Dcql2SerializerFactory extends BaseSerializerFactory {

	protected static Log LOG = LogFactory.getLog(Dcql2SerializerFactory.class.getName());


	public Dcql2SerializerFactory(Class<?> javaType, QName xmlType) {
		super(Dcql2Serializer.class, xmlType, javaType);
        if (LOG.isTraceEnabled()) {
            LOG.trace("Initializing " + Dcql2Serializer.class.getSimpleName() + 
                " for class:" + javaType + " and QName:" + xmlType);
        }
	}
}
