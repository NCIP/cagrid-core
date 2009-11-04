package org.cagrid.gaards.dorian.policy;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;
import java.util.Map.Entry;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;


public class ParserPool implements ErrorHandler, EntityResolver {

    // Stacks of DocumentBuilder parsers keyed by the Schema they support
    private Map /* <Schema,Stack> */pools = new HashMap();

    // The stack of non-schema-validating parsers
    private Stack unparsedpool = new Stack();

    // Resolution of extension schemas keyed by XML namespace
    private Map /* <String,EntityResolver> */extensions = new HashMap();

    private Schema defaultSchema = null;

    private Log log;


    public synchronized void setDefaultSchema(Schema schema) {
        this.defaultSchema = schema;
    }


    public synchronized Schema getDefaultSchema() {
        return defaultSchema;
    }

    /*
     * The JAXP factory is set up once and is then used to create parsers in the
     * parser pool. Access to this field must be synchronized, and is in
     * ParserPool.get()
     */
    private DocumentBuilderFactory dbf = null;


    /**
     * Constructor for the ParserPool object
     * <p>
     * To demonstrate the technology, the current version of this code creates
     * both 1.0 and 1.1 Schema objects. However, it then selects only one of the
     * two to use. Future code could refine this and maintain two pools of
     * parsers.
     */
    public ParserPool() {
        log = LogFactory.getLog(this.getClass().getName());
        // Build a parser factory and the default schema set.
        dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        try {
            dbf.setFeature("http://apache.org/xml/features/validation/schema/normalized-value", false);
        } catch (ParserConfigurationException e) {
            log.warn("Unable to turn off data normalization in parser, supersignatures may fail with Xerces-J: " + e);
        }
        registerSchemas(null);

    }


    public synchronized void registerSchemas(Map /* <String,EntityResolver> */exts) {
        // First merge the new set into the maintained set.
        if (exts != null)
            extensions.putAll(exts);

        /*
         * Create a JAXP 1.3 Schema object from an array of open files. There is
         * no EntityResolver or ResourceResolver, so the list must be complete
         * (no dependencies on XSD files not in the list. Also, to compile
         * correctly, an XSD file must appear in the list before another XSD
         * that depends on (imports) it.
         */
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        ArrayList sources = new ArrayList();
        sources.add(new StreamSource(PolicyConstants.class.getResourceAsStream("/ws/" + PolicyConstants.XML_SCHEMA_ID),
            PolicyConstants.XML_SCHEMA_ID));
        sources.add(new StreamSource(PolicyConstants.class.getResourceAsStream("/Dorian/" + PolicyConstants.XMLSIG_SCHEMA_ID),
            PolicyConstants.XMLSIG_SCHEMA_ID)); 
        sources.add(new StreamSource(PolicyConstants.class.getResourceAsStream("/Dorian/" + PolicyConstants.HOST_AGREEMENT_ID),
            PolicyConstants.HOST_AGREEMENT_ID));
            
        for (Iterator i = extensions.entrySet().iterator(); i.hasNext();) {
            Entry entry = (Entry) i.next();
            try {
                sources.add(new SAXSource(((EntityResolver) entry.getValue()).resolveEntity(null, (String) entry
                    .getKey())));
            } catch (SAXException e) {
                log.error("Unable to obtain extension schema (" + entry.getKey() + "): " + e);
            } catch (IOException e) {
                log.error("Unable to obtain extension schema (" + entry.getKey() + "): " + e);
            }
        }
        try {
            defaultSchema = factory.newSchema((Source[]) sources.toArray(new Source[0]));
        } catch (SAXException e) {
            log.error("Unable to parse dorian policy schemas: " + e);
        }
    }


    /**
     * Get a DOM parser suitable for our task
     * 
     * @param schema
     *            JAXP 1.3 Schema object (or null for no XSD)
     * @return A DOM parser ready to use
     * @exception SAMLException
     *                Raised if a system error prevents a parser from being
     *                created
     */
    public synchronized DocumentBuilder get(Schema schema) throws Exception {
        DocumentBuilder p = null;

        Stack pool;
        if (schema != null) {
            pool = (Stack) pools.get(schema);
            if (pool == null) {
                pool = new Stack();
                pools.put(schema, pool);
            }
        } else {
            pool = unparsedpool; // Parser with no xsd validation
        }

        if (pool.empty()) {
            // Build a parser to order.

            dbf.setSchema(schema); // null for no validation, or a Schema
            // object
            p = dbf.newDocumentBuilder();
            p.setErrorHandler(this);
            p.setEntityResolver(this); // short-circuits URI resolution

        } else
            p = (DocumentBuilder) pool.pop();

        return p;
    }


    /**
     * Get a DocumentBuilder for the default Schema
     * <p>
     * Note: This uses the default (probably SAML 1.1) Schema. To get an
     * non-schema-validating parser, call "get(null)".
     * </p>
     * 
     * @return Document Builder
     * @throws SAMLException
     *             can't create a DocumentBuilder
     */
    public DocumentBuilder get() throws Exception {
        return get(getDefaultSchema());
    }


    /**
     * Parses a document using a pooled parser with the proper settings
     * 
     * @param in
     *            A stream containing the content to be parsed
     * @param schema
     *            Schema object or null
     * @return The DOM document resulting from the parse
     * @exception SAMLException
     *                Raised if a parser is unavailable
     * @exception SAXException
     *                Raised if a parsing error occurs
     * @exception java.io.IOException
     *                Raised if an I/O error occurs
     */
    public Document parse(InputSource in, Schema schema) throws Exception, SAXException, java.io.IOException {
        DocumentBuilder p = get(schema);
        try {
            Document doc = p.parse(in);
            return doc;
        } finally {
            put(p);
        }
    }


    /**
     * Short form of parse to support legacy callers
     * <p>
     * This version is not preferred. If the caller converts the InputStream to
     * an InputSource, then it can append a file name as the systemId. Here we
     * only get the InputStream and create an InputSource with no identifier to
     * be used in logging or generating error messages.
     * 
     * @param in
     *            InputStream of XML to be parsed
     * @return DOM Document
     * @exception SAMLException
     *                Raised if a parser is unavailable
     * @exception SAXException
     *                Raised if a parsing error occurs
     * @exception java.io.IOException
     *                Raised if an I/O error occurs
     */
    public Document parse(InputStream in) throws Exception, SAXException, java.io.IOException {
        return parse(new InputSource(in), getDefaultSchema());
    }


    /**
     * Parses a document using a pooled parser with the proper settings
     * 
     * @param systemId
     *            The URI to parse
     * @return The DOM document resulting from the parse
     * @exception SAMLException
     *                Raised if a parser is unavailable
     * @exception SAXException
     *                Raised if a parsing error occurs
     * @exception java.io.IOException
     *                Raised if an I/O error occurs
     */
    public Document parse(String systemId, Schema schema) throws Exception, SAXException, java.io.IOException {
        DocumentBuilder p = get(schema);
        try {
            Document doc = p.parse(new InputSource(systemId));
            return doc;
        } finally {
            put(p);
        }
    }


    /**
     * Legacy version of parse where the default Schema is implied
     * 
     * @param systemId
     *            URI to be parsed, becomes systemId of InputSource
     * @return DOM Document
     * @exception SAMLException
     *                Raised if a parser is unavailable
     * @exception SAXException
     *                Raised if a parsing error occurs
     * @exception java.io.IOException
     *                Raised if an I/O error occurs
     */
    public Document parse(String systemId) throws Exception, SAXException, java.io.IOException {
        return parse(systemId, getDefaultSchema());
    }


    /**
     * Builds a new DOM document
     * <p>
     * In JAXP, you get a new empty DOM document from a DocumentBuilder. There
     * is no evidence that the Schema is attached to the DOM, so it doesn't
     * matter what pool to use.
     * 
     * @return An empty DOM document
     */
    public Document newDocument() {
        DocumentBuilder p = null;
        try {
            p = get();
        } catch (Exception e) {
            // Configuration error, no XML support. Return null??
            // Throw RuntimeException??
            return null;
        }
        Document doc = p.newDocument();
        put(p);
        return doc;
    }


    /**
     * Return a parser to the pool
     * 
     * @param p
     *            Description of Parameter
     */
    public synchronized void put(DocumentBuilder p) {
        Schema schema = p.getSchema();
        if (schema == null) {
            unparsedpool.push(p);
        } else {
            Stack pool = (Stack) pools.get(schema);
            pool.push(p);
        }
    }


    /**
     * Called by parser if a fatal error is detected, does nothing
     * 
     * @param exception
     *            Describes the error
     * @exception SAXException
     *                Can be raised to indicate an explicit error
     */
    public void fatalError(SAXParseException e) throws SAXException {
        throw e;
    }


    /**
     * Called by parser if an error is detected, currently just throws e
     * 
     * @param e
     *            Description of Parameter
     * @exception SAXParseException
     *                Can be raised to indicate an explicit error
     */
    public void error(SAXParseException e) throws SAXParseException {
        throw e;
    }


    /**
     * Called by parser if a warning is issued, currently logs the condition
     * 
     * @param e
     *            Describes the warning
     * @exception SAXParseException
     *                Can be raised to indicate an explicit error
     */
    public void warning(SAXParseException e) throws SAXParseException {
        log.warn("Parser warning: line = " + e.getLineNumber() + " : uri = " + e.getSystemId());
        log.warn("Parser warning (root cause): " + e.getMessage());
    }


    public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
        /*
         * During parsing, this should not be called with a systemId
         * corresponding to any known externally resolvable entity. It prevents
         * "accidental" resolution of external entities via URI resolution.
         * Network based retrieval of resources is NOT allowable and should
         * really be something the parser can block globally. We also can't
         * return null, because that signals URI resolution. So what we return
         * is a dummy source to shortcut and fail any such attempts.
         */
        return new InputSource(); // Hopefully this will fail the parser and not
        // be treated as null.
    }
}
