package org.cagrid.cql.utilities.encoding;

import javax.xml.namespace.QName;

import org.apache.axis.encoding.ser.BaseSerializerFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class Cql2SerializerFactory extends BaseSerializerFactory {

	protected static Log LOG = LogFactory.getLog(Cql2SerializerFactory.class.getName());


	public Cql2SerializerFactory(Class<?> javaType, QName xmlType) {
		super(Cql2Serializer.class, xmlType, javaType);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Initializing " + Cql2Serializer.class.getSimpleName() + 
                " for class:" + javaType + " and QName:" + xmlType);
        }
	}
}
