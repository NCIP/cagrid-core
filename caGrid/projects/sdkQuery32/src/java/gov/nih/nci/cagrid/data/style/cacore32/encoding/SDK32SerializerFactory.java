package gov.nih.nci.cagrid.data.style.cacore32.encoding;

import javax.xml.namespace.QName;

import org.apache.axis.encoding.ser.BaseSerializerFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class SDK32SerializerFactory extends BaseSerializerFactory {

	protected static Log LOG = LogFactory.getLog(SDK32SerializerFactory.class.getName());


	public SDK32SerializerFactory(Class javaType, QName xmlType) {
		super(SDK32Serializer.class, xmlType, javaType);
		LOG.debug("Initializing SDK32SerializerFactory for class:" + javaType + " and QName:" + xmlType);
	}
}
