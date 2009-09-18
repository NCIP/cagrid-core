package gov.nih.nci.cagrid.encoding;

import javax.xml.namespace.QName;

import org.apache.axis.encoding.ser.BaseSerializerFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class SDKSerializerFactory extends BaseSerializerFactory {

	protected static Log LOG = LogFactory.getLog(SDKSerializerFactory.class.getName());


	public SDKSerializerFactory(Class javaType, QName xmlType) {
		super(SDKSerializer.class, xmlType, javaType);
		LOG.debug("Initializing SDKSerializerFactory for class:" + javaType + " and QName:" + xmlType);
	}
}
