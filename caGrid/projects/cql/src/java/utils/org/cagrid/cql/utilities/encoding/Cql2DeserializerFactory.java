package org.cagrid.cql.utilities.encoding;

import javax.xml.namespace.QName;

import org.apache.axis.encoding.ser.BaseDeserializerFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class Cql2DeserializerFactory extends BaseDeserializerFactory {

	protected static Log LOG = LogFactory.getLog(Cql2DeserializerFactory.class.getName());


	public Cql2DeserializerFactory(Class<?> javaType, QName xmlType) {
		super(Cql2Deserializer.class, xmlType, javaType);
        if (LOG.isTraceEnabled()) {
            LOG.trace("Initializing " + Cql2Deserializer.class.getSimpleName() + 
                " for class:" + javaType + " and QName:" + xmlType);
        }
	}
}
