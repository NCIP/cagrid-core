package org.cagrid.dcql.utilities.encoding;

import javax.xml.namespace.QName;

import org.apache.axis.encoding.ser.BaseDeserializerFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class Dcql2DeserializerFactory extends BaseDeserializerFactory {

	protected static Log LOG = LogFactory.getLog(Dcql2DeserializerFactory.class.getName());


	public Dcql2DeserializerFactory(Class<?> javaType, QName xmlType) {
		super(Dcql2Deserializer.class, xmlType, javaType);
        if (LOG.isTraceEnabled()) {
            LOG.trace("Initializing " + Dcql2Deserializer.class.getSimpleName() + 
                " for class:" + javaType + " and QName:" + xmlType);
        }
	}
}
