package gov.nih.nci.cagrid.data.utilities.validation;

import gov.nih.nci.cagrid.common.SchemaValidationException;
import gov.nih.nci.cagrid.common.SchemaValidator;
import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.common.XMLUtilities;
import gov.nih.nci.cagrid.cqlresultset.CQLQueryResults;
import gov.nih.nci.cagrid.data.CqlSchemaConstants;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import javax.wsdl.Definition;

import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.axis.utils.ClassUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.globus.wsrf.utils.AddressingUtils;
import org.jdom.Element;


/**
 * Validates CQL Results for a particular service. This contacts the service to
 * parse its WSDL, extracting the CQL Results schema, and rewriting it using the
 * schema information the service publishes about the allowable types the
 * service is exposing. Not only the structure of the result set is validated,
 * but the actual object instances are too.
 * 
 * @author oster
 */
public class CQLQueryResultsValidator {

	private static final String CQLRESULT_TYPES = "CQLResultTypes";
	private static final String VALIDATION_XSD_TEMPLATE = "RestrictedCQLResultSet.xsd.template";
	private static final String TOKEN_CQL_RESULT_XSD_LOCATION = "@CQL_RESULT_XSD_LOCATION@";
	private static final String TOKEN_SERVICE_RESTRICTIONS_XSD_NAMESPACE = "@SERVICE_RESTRICTIONS_XSD_NAMESPACE@";
	private static final String TOKEN_SERVICE_RESTRICTIONS_XSD_LOCATION = "@SERVICE_RESTRICTIONS_XSD_LOCATION@";

	protected static Log LOG = LogFactory.getLog(CQLQueryResultsValidator.class.getName());

	// wsdl sources
	private EndpointReferenceType epr = null;
	private String wsdlFile = null;

	private boolean initialized = false;
	private SchemaValidator validator = null;

	private String cqlResultXSDLocation = null;
	private String serviceResultTypesXSDLocation;
	private String serviceResultTypesNamespace;
	private String xsdText = null;


	public CQLQueryResultsValidator(EndpointReferenceType serviceEPR) {
		this.epr = serviceEPR;
	}
	
	
	public CQLQueryResultsValidator(String wsdlFileName) {
		this.wsdlFile = wsdlFileName;
	}


	protected void initializeRestrictedXSD() throws SchemaValidationException {
		if (!initialized) {
			LOG.debug("Initializing...");
			// parse the service's WSDL, setting the proper XSD locations
			parseWSDL();

			// load RestrictedCQLResultSet.xsd template
			InputStream templateResourceAsStream = ClassUtils.getResourceAsStream(
			    this.getClass(), VALIDATION_XSD_TEMPLATE);
			if (templateResourceAsStream == null) {
				throw new SchemaValidationException("Problem loading service specific XSD template.");
			}

			// populate the resultset template
			try {
				xsdText = XMLUtilities.streamToString(templateResourceAsStream);
				LOG.debug("Initial XSD:\n" + xsdText);
				xsdText = xsdText.replaceAll(TOKEN_CQL_RESULT_XSD_LOCATION, this.cqlResultXSDLocation);
				xsdText = xsdText.replaceAll(TOKEN_SERVICE_RESTRICTIONS_XSD_LOCATION,
					this.serviceResultTypesXSDLocation);
				xsdText = xsdText
					.replaceAll(TOKEN_SERVICE_RESTRICTIONS_XSD_NAMESPACE, this.serviceResultTypesNamespace);
				LOG.debug("Created XSD:\n" + xsdText);
			} catch (Exception e) {
				LOG.error(e);
				throw new SchemaValidationException("Problem configuring service specific XSD.");
			}

			// write the populated result to temp file
			String xsdFileLocation = null;
			try {
				File xsdFile = File.createTempFile("RestrictedCQLResultSet", ".xsd");
				xsdFile.deleteOnExit();
				xsdFileLocation = xsdFile.getAbsolutePath();
				LOG.debug("Writing XSD to file:" + xsdFileLocation);
				FileWriter fw = new FileWriter(xsdFile);
				fw.write(xsdText);
				fw.close();
			} catch (IOException e) {
				LOG.error(e);
				throw new SchemaValidationException("Unable to create temporary.");
			}

			// configure the validator to point to the XSD
			validator = new SchemaValidator(xsdFileLocation);
			initialized = true;
		}
	}


	/**
	 * Saves the XSD defining the restricted result set to a file
	 * 
	 * @param fileLocation
	 * 		The file in which to store the XSD
	 * @throws SchemaValidationException
	 */
	public void saveRestrictedCQLResultSetXSD(File fileLocation) throws SchemaValidationException {
		initializeRestrictedXSD();
		try {
			FileWriter fw = new FileWriter(fileLocation);
			fw.write(xsdText);
			fw.close();
		} catch (IOException e) {
			LOG.error(e);
			throw new SchemaValidationException("Unable to save XSD to file.");
		}
	}


	private void parseWSDL() throws SchemaValidationException {
		// parse the WSDL
		Definition wsdlDefinition;
		try {
			String location = null;
			if (this.epr != null) {
				location = WSDLUtils.getWSDLLocation(this.epr);
			} else {
				location = this.wsdlFile;
			}
			LOG.debug("Looking for WSDL at:" + location);
			wsdlDefinition = WSDLUtils.parseServiceWSDL(location);
		} catch (Exception e) {
			LOG.error(e);
			throw new SchemaValidationException("Unable to process service's WSDL!", e);
		}

		// extract the schema types
		Map<String, Element> schemas = new HashMap<String, Element>();
		WSDLUtils.walkWSDLFindingSchema(wsdlDefinition, schemas);

		// determine SERVICE_URL_TO_CQL_RESULT_XSD
		String resultURI = CqlSchemaConstants.CQL_RESULT_COLLECTION_QNAME.getNamespaceURI();
		URI cqlResultXSDLocationURI = WSDLUtils.determineSchemaLocation(schemas, resultURI);
		if (cqlResultXSDLocationURI == null) {
		    throw new SchemaValidationException("Unable to determine remote location of :" + resultURI);
		}
		this.cqlResultXSDLocation = cqlResultXSDLocationURI.toString();

		// determine SERVICE_TYPES_NAMESPACE
		String serviceNamespaceURI = wsdlDefinition.getQName().getNamespaceURI();
		URI serNSURI = URI.create(serviceNamespaceURI);
		URI cqlResultTypesURI = serNSURI.resolve(CQLRESULT_TYPES);
		this.serviceResultTypesNamespace = cqlResultTypesURI.toString();

		// determine SERVICE_URL_TO_TYPES_XSD
		URI cqlResultTypesXSDLocationURI = WSDLUtils.determineSchemaLocation(schemas,
		    this.serviceResultTypesNamespace);
		if (cqlResultTypesXSDLocationURI == null) {
		    throw new SchemaValidationException("Unable to determine remote location of schema "
		        + this.serviceResultTypesNamespace);
		}
		this.serviceResultTypesXSDLocation = cqlResultTypesXSDLocationURI.toString();
	}


	/**
	 * Validates a CQL result set's contents against the restricted result set schema
	 * 
	 * @param resultSet
	 * @throws SchemaValidationException
	 */
	public void validateCQLResultSet(CQLQueryResults resultSet) throws SchemaValidationException {
		// make sure we are ready to go
		initializeRestrictedXSD();

		if (resultSet == null) {
			LOG.debug("Null results passed, ignoring.");
			return;
		}

		StringWriter writer = new StringWriter();
		try {
			Utils.serializeObject(resultSet, CqlSchemaConstants.CQL_RESULT_COLLECTION_QNAME, writer);
		} catch (Exception e) {
			LOG.error(e);
			throw new SchemaValidationException("Problem serializing result set", e);
		}

		String xmlContents = writer.getBuffer().toString();
		if (LOG.isDebugEnabled()) {
			LOG.debug("RESULTS:\n" + xmlContents);
		}
		this.validator.validate(xmlContents);
	}
	
	
	public void validateCQL2ResultSet(org.cagrid.cql2.results.CQLQueryResults results) throws SchemaValidationException {
	    // make sure we are ready to go
        initializeRestrictedXSD();

        if (results == null) {
            LOG.debug("Null results passed, ignoring.");
            return;
        }

        StringWriter writer = new StringWriter();
        try {
            Utils.serializeObject(results, CqlSchemaConstants.CQL2_RESULTS_QNAME, writer);
        } catch (Exception e) {
            LOG.error(e);
            throw new SchemaValidationException("Problem serializing result set", e);
        }

        String xmlContents = writer.getBuffer().toString();
        if (LOG.isDebugEnabled()) {
            LOG.debug("RESULTS:\n" + xmlContents);
        }
        this.validator.validate(xmlContents);
	}


	public static void main(String[] args) {
		try {
			EndpointReferenceType epr = AddressingUtils.createEndpointReference(args[0], null);
			CQLQueryResultsValidator validator = new CQLQueryResultsValidator(epr);
			CQLQueryResults result = Utils.deserializeDocument(args[1], CQLQueryResults.class);
			validator.validateCQLResultSet(result);
			System.out.println("Results were valid.");
		} catch (Exception e) {
			System.out.println("Results were invalid: " + e.getMessage());
			e.printStackTrace();
		}
	}
}
