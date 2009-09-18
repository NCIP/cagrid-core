package gov.nih.nci.cagrid.data.style.cacore32.encoding;

import gov.nih.nci.cagrid.encoding.AxisContentHandler;

import java.io.IOException;

import javax.xml.namespace.QName;

import org.apache.axis.Constants;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.encoding.Serializer;
import org.apache.axis.wsdl.fromJava.Types;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.exolab.castor.mapping.Mapping;
import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.ValidationException;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;


public class SDK32Serializer implements Serializer {

	protected static Log LOG = LogFactory.getLog(SDK32Serializer.class.getName());


	public void serialize(QName name, Attributes attributes, Object value, SerializationContext context)
		throws IOException {
		long startTime = System.currentTimeMillis();

		AxisContentHandler hand = new AxisContentHandler(context);
		Marshaller marshaller = new Marshaller(hand);

		try {
			Mapping mapping = SDK32EncodingUtils.getMarshallerMapping(context.getMessageContext());
			marshaller.setMapping(mapping);
			marshaller.setValidation(true);
		} catch (MappingException e) {
			LOG.error("Problem establishing castor mapping!  Using default mapping.", e);
		}
		try {
			marshaller.marshal(value);
		} catch (MarshalException e) {
			LOG.error("Problem using castor marshalling.", e);
			throw new IOException("Problem using castor marshalling." + e.getMessage());
		} catch (ValidationException e) {
			LOG.error("Problem validating castor marshalling; message doesn't comply with the associated XML schema.",
				e);
			throw new IOException(
				"Problem validating castor marshalling; message doesn't comply with the associated XML schema."
					+ e.getMessage());
		}
		long duration = System.currentTimeMillis() - startTime;
		LOG.debug("Total time to serialize(" + name.getLocalPart() + "):" + duration + " ms.");
	}


	public String getMechanismType() {
		return Constants.AXIS_SAX;
	}


	/**
	 * Return XML schema for the specified type, suitable for insertion into the
	 * &lt;types&gt; element of a WSDL document, or underneath an
	 * &lt;element&gt; or &lt;attribute&gt; declaration.
	 * 
	 * @param javaType
	 *            the Java Class we're writing out schema for
	 * @param types
	 *            the Java2WSDL Types object which holds the context for the
	 *            WSDL being generated.
	 * @return a type element containing a schema simpleType/complexType
	 * @see org.apache.axis.wsdl.fromJava.Types
	 */
	public Element writeSchema(Class javaType, Types types) throws Exception {
		return null;
	}
}
