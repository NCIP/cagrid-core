package gov.nih.nci.cagrid.encoding;

import javax.xml.namespace.QName;

import org.apache.axis.encoding.ser.BaseDeserializerFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class SDKDeserializerFactory extends BaseDeserializerFactory {

	protected static Log LOG = LogFactory.getLog(SDKDeserializerFactory.class.getName());


	public SDKDeserializerFactory(Class javaType, QName xmlType) {
		super(SDKDeserializer.class, xmlType, javaType);
		LOG.debug("Initializing SDKDeserializerFactory for class:" + javaType + " and QName:" + xmlType);
	}
}
