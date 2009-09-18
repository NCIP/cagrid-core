package gov.nih.nci.cagrid.sdkquery4.encoding;

import javax.xml.namespace.QName;

import org.apache.axis.encoding.ser.BaseSerializerFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class SDK40SerializerFactory extends BaseSerializerFactory {

	protected static Log LOG = LogFactory.getLog(SDK40SerializerFactory.class.getName());


	public SDK40SerializerFactory(Class javaType, QName xmlType) {
		super(SDK40Serializer.class, xmlType, javaType);
		LOG.debug("Initializing " + SDK40Serializer.class.getSimpleName() + " for class:" + javaType + " and QName:" + xmlType);
	}
}
