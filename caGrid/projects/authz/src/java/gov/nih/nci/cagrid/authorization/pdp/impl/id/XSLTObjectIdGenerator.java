package gov.nih.nci.cagrid.authorization.pdp.impl.id;

import gov.nih.nci.cagrid.authorization.pdp.ObjectIdGenerator;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

public class XSLTObjectIdGenerator implements ObjectIdGenerator {

	private static Log logger = LogFactory.getLog(XSLTObjectIdGenerator.class
			.getName());

	private String xsl;

	private Transformer xslt;

	public void init() {
		try {
			String s = getXsl();
			logger.debug("Parsing this xsl:\n" + s);
			Source source = new SAXSource(new InputSource(new StringReader(
					getXsl())));
			setXslt(TransformerFactory.newInstance().newTransformer(source));
		} catch (Exception ex) {
			throw new RuntimeException("Error initializing transformer: "
					+ ex.getMessage(), ex);
		}
	}

	public String generateId(Node node) {
		String xml = toString(node);
		logger.debug("Input node:\n" + xml);

		Transformer trans = getXslt();
		if (trans == null) {
			init();
			trans = getXslt();
		}
		StringWriter w = new StringWriter();
		try {
			trans.transform(new DOMSource(node), new StreamResult(w));
		} catch (Exception ex) {
			throw new RuntimeException(
					"Error transforming: " + ex.getMessage(), ex);
		}
		return w.getBuffer().toString().trim();
	}

	private String toString(Node node) {

		StringWriter w = new StringWriter();
		try {
			TransformerFactory transformerFactory = TransformerFactory
					.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty("indent", "yes");
			transformer.transform(new DOMSource(node), new StreamResult(w));
		} catch (Exception ex) {
			throw new RuntimeException("Error deserializing: "
					+ ex.getMessage(), ex);
		}
		return w.getBuffer().toString();
	}

	public String getXsl() {
		return xsl;
	}

	public void setXsl(String xsl) {
		this.xsl = xsl;
	}

	public Transformer getXslt() {
		return xslt;
	}

	public void setXslt(Transformer xslt) {
		this.xslt = xslt;
	}

}
