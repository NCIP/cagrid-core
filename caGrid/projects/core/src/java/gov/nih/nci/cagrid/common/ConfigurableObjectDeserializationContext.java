package gov.nih.nci.cagrid.common;

import org.apache.axis.MessageContext;
import org.apache.axis.encoding.DeserializationContext;
import org.apache.axis.encoding.Deserializer;
import org.apache.axis.message.EnvelopeHandler;
import org.apache.axis.message.SOAPHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/** 
 *  ConfigurableObjectDeserializationContext
 *  Object Deserialization Context that allows configuration by WSDD
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * 
 * @created Jun 23, 2006 
 * @version $Id$ 
 */
public class ConfigurableObjectDeserializationContext extends DeserializationContext {
	
	private Class objectClass = null; 
	private Object value = null;

	public ConfigurableObjectDeserializationContext(
		MessageContext context, InputSource source, Class clazz) {
		super(context, new SOAPHandler());
		this.inputSource = source;
		this.objectClass = clazz;
	}
	
	
	public Object getValue() throws SAXException {
		if (value == null) {
			popElementHandler();
			Deserializer deserializer = getDeserializerForClass(objectClass);
			if (deserializer == null) {
				throw new NullPointerException("No deserializer found for class " + objectClass.getName());
			}
			pushElementHandler(new EnvelopeHandler((SOAPHandler) deserializer));
			parse();
			value = deserializer.getValue();
		}
		return value;
	}
}
