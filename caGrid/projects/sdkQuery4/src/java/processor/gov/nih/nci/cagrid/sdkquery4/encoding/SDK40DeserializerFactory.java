package gov.nih.nci.cagrid.sdkquery4.encoding;

import javax.xml.namespace.QName;

import org.apache.axis.encoding.ser.BaseDeserializerFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class SDK40DeserializerFactory extends BaseDeserializerFactory {

	protected static Log LOG = LogFactory.getLog(SDK40DeserializerFactory.class.getName());


	public SDK40DeserializerFactory(Class javaType, QName xmlType) {
		super(SDK40Deserializer.class, xmlType, javaType);
		LOG.debug("Initializing " + SDK40Deserializer.class.getSimpleName() + " for class:" + javaType + " and QName:" + xmlType);
	}
}
