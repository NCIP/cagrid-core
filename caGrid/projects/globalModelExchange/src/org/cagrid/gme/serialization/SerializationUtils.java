package org.cagrid.gme.serialization;

import gov.nih.nci.cagrid.common.Utils;

import java.io.Reader;
import java.io.Writer;

import org.cagrid.gme.client.GlobalModelExchangeClient;
import org.cagrid.gme.domain.XMLSchema;
import org.cagrid.gme.domain.XMLSchemaBundle;
import org.cagrid.gme.domain.XMLSchemaImportInformation;


public class SerializationUtils {

    /**
     * Write the XML representation of the specified XMLSchema to the specified
     * writer. If either are null, an IllegalArgumentException will be thrown.
     * 
     * @param metadata
     * @param writer
     * @throws Exception
     */
    public static void serializeXMLSchema(XMLSchema schema, Writer writer) throws Exception {
        if (schema == null || writer == null) {
            throw new IllegalArgumentException("Null is not a valid argument");
        }
        Utils.serializeObject(schema, Constants.XML_SCHEMA_QNAME, writer, GlobalModelExchangeClient.class
            .getResourceAsStream("client-config.wsdd"));
    }


    /**
     * Create an instance of the XMLSchema from the specified reader. The reader
     * must point to a stream that contains an XML representation of the
     * XMLSchema. If the reader is null, an IllegalArgumentException will be
     * thrown.
     * 
     * @param xmlReader
     * @return The deserialized XMLSchema
     * @throws Exception
     */
    public static XMLSchema deserializeXMLSchema(Reader xmlReader) throws Exception {
        if (xmlReader == null) {
            throw new IllegalArgumentException("Null is not a valid argument");
        }
        return (XMLSchema) Utils.deserializeObject(xmlReader, XMLSchema.class, GlobalModelExchangeClient.class
            .getResourceAsStream("client-config.wsdd"));
    }


    /**
     * Write the XML representation of the specified XMLSchemaImportInformation
     * to the specified writer. If either are null, an IllegalArgumentException
     * will be thrown.
     * 
     * @param importInfo
     * @param writer
     * @throws Exception
     */
    public static void serializeXMLSchemaImportInformation(XMLSchemaImportInformation importInfo, Writer writer)
        throws Exception {
        if (importInfo == null || writer == null) {
            throw new IllegalArgumentException("Null is not a valid argument");
        }
        Utils.serializeObject(importInfo, Constants.XML_SCHEMA_IMPORT_INFO_QNAME, writer,
            GlobalModelExchangeClient.class.getResourceAsStream("client-config.wsdd"));
    }


    /**
     * Create an instance of the XMLSchemaImportInformation from the specified
     * reader. The reader must point to a stream that contains an XML
     * representation of the XMLSchemaImportInformation. If the reader is null,
     * an IllegalArgumentException will be thrown.
     * 
     * @param xmlReader
     * @return The deserialized XMLSchemaImportInformation
     * @throws Exception
     */
    public static XMLSchemaImportInformation deserializeXMLSchemaImportInformation(Reader xmlReader) throws Exception {
        if (xmlReader == null) {
            throw new IllegalArgumentException("Null is not a valid argument");
        }
        return (XMLSchemaImportInformation) Utils.deserializeObject(xmlReader, XMLSchemaImportInformation.class,
            GlobalModelExchangeClient.class.getResourceAsStream("client-config.wsdd"));
    }


    /**
     * Write the XML representation of the specified XMLSchemaBundle to the
     * specified writer. If either are null, an IllegalArgumentException will be
     * thrown.
     * 
     * @param bundle
     * @param writer
     * @throws Exception
     */
    public static void serializeXMLSchemaBundle(XMLSchemaBundle bundle, Writer writer) throws Exception {
        if (bundle == null || writer == null) {
            throw new IllegalArgumentException("Null is not a valid argument");
        }
        Utils.serializeObject(bundle, Constants.XML_SCHEMA_BUNDLE_QNAME, writer, GlobalModelExchangeClient.class
            .getResourceAsStream("client-config.wsdd"));
    }


    /**
     * Create an instance of the XMLSchemaBundle from the specified reader. The
     * reader must point to a stream that contains an XML representation of the
     * XMLSchemaBundle. If the reader is null, an IllegalArgumentException will
     * be thrown.
     * 
     * @param xmlReader
     * @return The deserialized XMLSchemaBundle
     * @throws Exception
     */
    public static XMLSchemaBundle deserializeXMLSchemaBundle(Reader xmlReader) throws Exception {
        if (xmlReader == null) {
            throw new IllegalArgumentException("Null is not a valid argument");
        }
        return (XMLSchemaBundle) Utils.deserializeObject(xmlReader, XMLSchemaBundle.class,
            GlobalModelExchangeClient.class.getResourceAsStream("client-config.wsdd"));
    }

}
