package gov.nih.nci.cagrid.data.style.cacore32.encoding;

import javax.xml.namespace.QName;

import org.apache.axis.encoding.ser.BaseDeserializerFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class SDK32DeserializerFactory extends BaseDeserializerFactory {

	protected static Log LOG = LogFactory.getLog(SDK32DeserializerFactory.class.getName());


	public SDK32DeserializerFactory(Class javaType, QName xmlType) {
		super(SDK32Deserializer.class, xmlType, javaType);
		LOG.debug("Initializing SDK32DeserializerFactory for class:" + javaType + " and QName:" + xmlType);
	}
}
