package gov.nih.nci.cagrid.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.StringReader;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/** 
 *  SchemaValidator
 *  Validates XML documents against a schema.  For validating multiple documents against the
 *  same schema, create an instance of this class with the schema filename and pass the
 *  documents to the validator in succession to save on the overhead of creating new
 *  parser factories.
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * 
 * @created May 25, 2006 
 * @version $Id$ 
 */
public class SchemaValidator {
	public static final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
	public static final String JAXP_SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource";
	public static final String W3C_NAMESPACE = "http://www.w3.org/2001/XMLSchema";
	
	private SAXParserFactory factory;
	private SAXParser parser;
	
	/**
	 * Initializes the schema validator to perform validation against an XML Schema
	 * @param schemaFilename
	 * 		The filename of the schema to use for validation
	 * @throws SchemaValidationException
	 */
	public SchemaValidator(String schemaFilename) throws SchemaValidationException {
		try {
			// initialize the sax parser factory
			factory = SAXParserFactory.newInstance();
			factory.setValidating(true);
			factory.setNamespaceAware(true);
			if (!factory.isValidating()) {
				throw new SchemaValidationException("Unable to set validation on sax parser factory: " + factory.getClass().getName());
			}
			
			// create a parser
			parser = factory.newSAXParser();
			
			// enable scheams for the parser
			parser.setProperty(JAXP_SCHEMA_LANGUAGE, W3C_NAMESPACE);
			
			// configure the schema
			parser.setProperty(JAXP_SCHEMA_SOURCE, schemaFilename);
		}  catch (ParserConfigurationException ex) {
			throw new SchemaValidationException("Error configuring SAX parser: " + ex.getMessage(), ex);
		} catch (SAXException ex) {
			throw new SchemaValidationException("Error in SAX: " + ex.getMessage(), ex);
		}
	}
	
	
	/**
	 * Validates XML against the schema
	 * @param xml
	 * 		The XML text to validate
	 * @throws SchemaValidationException
	 */
	public void validate(String xml) throws SchemaValidationException {
		InputSource xmlInput = new InputSource(new BufferedReader(new StringReader(xml)));
		// only one document can be handled by the xml parser at once
		synchronized (parser) {
			try {
				// create an XML reader from the parser
				XMLReader xmlReader = parser.getXMLReader();
				
				// set content and error handlers on the reader
				xmlReader.setContentHandler(new SimpleErrorHandler());
				xmlReader.setErrorHandler(new SimpleErrorHandler());
				
				// parse the xml
				xmlReader.parse(xmlInput);
			} catch (Exception ex) {
				throw new SchemaValidationException("Invalid Document: " + ex.getMessage(), ex);
			}
		}
	}
	
	
	/**
	 * Validates the contents of an XML file against the schema
	 * @param xmlFile
	 * 		The file to load XML from for validation
	 * @throws SchemaValidationException
	 */
	public void validate(File xmlFile) throws SchemaValidationException {
		String xmlText = null;
		try {
			xmlText = Utils.fileToStringBuffer(xmlFile).toString();
		} catch (Exception ex) {
			throw new SchemaValidationException("Error reading file: " + ex.getMessage(), ex);
		}		
		validate(xmlText);
	}
	
	
	/**
	 * Validates xml text against an xml schema 
	 * @param xsdFilename
	 * 		The filename of the xml schema to validate against
	 * @param xmlText
	 * 		The text of an xml document to be validated
	 * @throws SchemaValidationException
	 */
	public static void validate(String xsdFilename, String xmlText) throws SchemaValidationException {
		SchemaValidator validator = new SchemaValidator(xsdFilename);
		validator.validate(xmlText);
	}
	
	
	/**
	 * Verify that a schema is well formed
	 * 
	 * @param xsdFilename
	 * @throws SchemaValidationException 
	 */
	public static void verify(String xsdFilename) throws SchemaValidationException{
	    final String sl = XMLConstants.W3C_XML_SCHEMA_NS_URI;
	    SchemaFactory factory = SchemaFactory.newInstance(sl);
        StreamSource ss = new StreamSource(new File(xsdFilename));
	    try {
            factory.newSchema(ss);
        } catch (SAXException e) {
            throw new SchemaValidationException(e);
        }
	}
	
	
	/**
	 * Validates the contents of an XML file against an xml schema
	 * @param xsdFilename
	 * 		The filename of the xml schema to validate against
	 * @param xmlFile
	 * 		The file to load XML from for validation
	 * @throws SchemaValidationException
	 */
	public static void validate(String xsdFilename, File xmlFile) throws SchemaValidationException {
		String xmlText = null;
		try {
			xmlText = Utils.fileToStringBuffer(xmlFile).toString();
		} catch (Exception ex) {
			throw new SchemaValidationException("Error reading file: " + ex.getMessage(), ex);
		}		
		validate(xsdFilename, xmlText);
	}
	
	
	private class SimpleErrorHandler extends DefaultHandler {
		public void warning(SAXParseException e) {
			System.out.println("Warning Line " + e.getLineNumber() + ": " + e.getMessage() + "\n");
		}
		
		
		public void error(SAXParseException e) throws SAXException {
			throw new SAXException("Error Line " + e.getLineNumber() + ": " + e.getMessage(), e);
		}
		
		
		public void fatalError(SAXParseException e) throws SAXException {
			throw new SAXException("Fatal Error Line " + e.getLineNumber() + ": " + e.getMessage(), e);
		}
	}
	
	
	private static void usage() {
		System.err.println("Usage: " + SchemaValidator.class.getName() + " <schemaFile> <xmlFile1> <xmlFile2> .. <xmlFileN>");
		System.err.println("\t<schemaFile> -- The filename of the XSD schema to validate documents against");
		System.err.println("\t<xmlFile1> .. <xmlFileN> -- A list of xml files to validate against the schema");
	}


	/**
	 * This utility can be run from the command line as well
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length < 2) {
			usage();
			System.exit(1);
		}
		int errorCount = 0;
		SchemaValidator validator = null;
		try {
			validator = new SchemaValidator(args[0]);
		} catch (SchemaValidationException ex) {
			System.err.println("Error initializing the schema validator, exiting...");
			ex.printStackTrace();
			System.exit(1);
		}
		
		for (int i = 1; i < args.length; i++) {
			try {
				validator.validate(new File(args[i]));
				System.out.println("Valid: " + args[i]);
			} catch (Exception ex) {
				System.err.println(ex.getMessage());
				errorCount++;
			}
		}
		System.exit(errorCount);
	}
}
