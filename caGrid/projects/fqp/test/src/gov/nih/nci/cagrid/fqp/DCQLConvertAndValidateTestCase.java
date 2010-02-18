package gov.nih.nci.cagrid.fqp;

import gov.nih.nci.cagrid.common.SchemaValidationException;
import gov.nih.nci.cagrid.common.SchemaValidator;
import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.dcql.DCQLQuery;
import gov.nih.nci.cagrid.fqp.common.DCQLConstants;
import gov.nih.nci.cagrid.fqp.common.DCQLConversionException;
import gov.nih.nci.cagrid.fqp.common.DCQLConverter;

import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.StringWriter;

import junit.framework.TestCase;

public class DCQLConvertAndValidateTestCase extends TestCase {
    
    public DCQLConvertAndValidateTestCase(String name) {
        super(name);
    }
    
    
    public InputStream getClientConfig() {
        InputStream clientConfig = getClass().getResourceAsStream("/gov/nih/nci/cagrid/fqp/client/client-config.wsdd");
        assertNotNull("Client config wsdd stream was null", clientConfig);
        return clientConfig;
    }
    
    
    public void testCaBioExample() {
        convertAndValidate("caBIO_Example.xml");
    }
    
    
    private void convertAndValidate(String dcqlQueryFilename) {
        DCQLQuery dcql1 = loadDcql1Query(dcqlQueryFilename);
        org.cagrid.data.dcql.DCQLQuery converted = convertToDcql2(dcql1);
        validateDcql2AgainstSchema(converted);
    }
    
    
    private DCQLQuery loadDcql1Query(String filename) {
        File queryFile = new File("test/resources/" + filename);
        DCQLQuery query = null;
        try {
            FileReader reader = new FileReader(queryFile);
            InputStream clientConfig = getClientConfig();
            query = Utils.deserializeObject(reader, DCQLQuery.class, clientConfig);
            clientConfig.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error deserializing DCQL 1 query: " + ex.getMessage());
        }
        return query;
    }
    
    
    private org.cagrid.data.dcql.DCQLQuery convertToDcql2(DCQLQuery query) {
        DCQLConverter converter = new DCQLConverter();
        org.cagrid.data.dcql.DCQLQuery converted = null;
        try {
            converted = converter.convertToDcql2(query);
        } catch (DCQLConversionException ex) {
            ex.printStackTrace();
            fail("Error converting DCQL 1 to DCQL 2: " + ex.getMessage());
        }
        return converted;
    }
    
    
    private void validateDcql2AgainstSchema(org.cagrid.data.dcql.DCQLQuery query) {
        File dcql2Schema = new File("schema/cql2.0/DCQL_2.0.xsd");
        SchemaValidator validator = null;
        try {
            validator = new SchemaValidator(dcql2Schema.getAbsolutePath());
        } catch (SchemaValidationException e) {
            e.printStackTrace();
            fail("Error setting up DCQL 2 schema validator: " + e.getMessage());
        }
        String xml = null;
        try {
            StringWriter writer = new StringWriter();
            InputStream clientConfig = getClientConfig();
            Utils.serializeObject(query, DCQLConstants.DCQL2_QUERY_QNAME, writer, clientConfig);
            clientConfig.close();
            xml = writer.getBuffer().toString();
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error serializing DCQL 2 query for schema validation: " + ex.getMessage());
        }
        try {
            validator.validate(xml);
        } catch (SchemaValidationException ex) {
            System.err.println("Error validating DCQL 2:");
            System.err.println(xml);
            ex.printStackTrace();
            fail("Error validating DCQL 2: " + ex.getMessage());
        }
    }
    

    public static void main(String[] args) {
        // TODO Auto-generated method stub

    }
}
