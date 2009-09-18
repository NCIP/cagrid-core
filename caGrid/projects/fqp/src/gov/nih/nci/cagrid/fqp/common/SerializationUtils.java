package gov.nih.nci.cagrid.fqp.common;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.cqlresultset.CQLQueryResults;
import gov.nih.nci.cagrid.data.DataServiceConstants;
import gov.nih.nci.cagrid.dcql.DCQLQuery;
import gov.nih.nci.cagrid.dcqlresult.DCQLQueryResultsCollection;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;

import org.apache.axis.utils.XMLUtils;
import org.globus.wsrf.encoding.ObjectDeserializer;


/**
 * SerializationUtils
 * Utilities for serializing and deserializing CQL and DCQL queries and results
 * 
 * @author oster
 * @author dervin
 */
public class SerializationUtils {

	/**
	 * Write the XML representation of the specified query to the specified
	 * writer. If either are null, an IllegalArgumentException will be thown.
	 * 
	 * @param cqlQuery
	 * @param writer
	 * @throws Exception
	 */
	public static void serializeCQLQuery(CQLQuery cqlQuery, Writer writer) throws Exception {
		if (cqlQuery == null || writer == null) {
			throw new IllegalArgumentException("Null is not a valid argument");
		}
		Utils.serializeObject(cqlQuery, DCQLConstants.CQL_QUERY_QNAME, writer);
	}


	/**
	 * Create an instance of CQLQuery from the specified input stream. The stream
	 * must contain an XML representation of the CQLQuery. If the reader is
	 * null, an IllegalArgumentException will be thown.
	 * 
	 * @param xmlStream
	 * @return an instance of CQLQuery from the specified inputstream.
	 * @throws Exception
	 *             on null argument or deserialization failure
	 */
	public static CQLQuery deserializeCQLQuery(InputStream xmlStream) throws Exception {
		if (xmlStream == null) {
			throw new IllegalArgumentException("Null is not a valid argument");
		}

		org.w3c.dom.Document doc = XMLUtils.newDocument(xmlStream);
		return (CQLQuery) ObjectDeserializer.toObject(doc.getDocumentElement(), CQLQuery.class);
	}


	/**
	 * Write the XML representation of the specified query to the specified
	 * writer. If either are null, an IllegalArgumentException will be thown.
	 * 
	 * @param dcqlQuery
	 * @param writer
	 * @throws Exception
	 */
	public static void serializeDCQLQuery(DCQLQuery dcqlQuery, Writer writer) throws Exception {
		if (dcqlQuery == null || writer == null) {
			throw new IllegalArgumentException("Null is not a valid argument");
		}
		Utils.serializeObject(dcqlQuery, DCQLConstants.DCQL_QUERY_QNAME, writer);
	}


	/**
	 * Create an instance of DCQLQuery from the specified input stream. The
	 * stream must contain an XML representation of the DCQLQuery. If the
	 * reader is null, an IllegalArgumentException will be thown.
	 * 
	 * @param xmlStream
	 * @return an instance of DCQLQuery from the specified inputstream.
	 * @throws Exception
	 *             on null argument or deserialization failure
	 */
	public static DCQLQuery deserializeDCQLQuery(InputStream xmlStream) throws Exception {
		if (xmlStream == null) {
			throw new IllegalArgumentException("Null is not a valid argument");
		}

		org.w3c.dom.Document doc = XMLUtils.newDocument(xmlStream);
		return (DCQLQuery) ObjectDeserializer.toObject(doc.getDocumentElement(), DCQLQuery.class);
	}
    
    
    /**
     * Write the XML representation of the specified query results to the specified
     * writer. If either are null, an IllegalArgumentException will be thown.
     * 
     * @param results
     * @param writer
     * @throws Exception
     */
    public static void serializeCQLQueryResults(CQLQueryResults results, Writer writer) throws Exception {
        serializeCQLQueryResults(results, writer, null);
    }
    
    
    /**
     * Write the XML representation of the specified query results to the specified
     * writer, using the configuration specified by the WSDD configuration.
     * If either the results or writer are null, an IllegalArgumentException will be thown.
     * 
     * @param results
     * @param writer
     * @param wsddStream
     * @throws Exception
     */
    public static void serializeCQLQueryResults(CQLQueryResults results, Writer writer, InputStream wsddStream) throws Exception {
        if (results == null || writer == null) {
            throw new IllegalArgumentException("Null is not a valid argument");
        }
        if (wsddStream == null) {
            Utils.serializeObject(results, DataServiceConstants.CQL_RESULT_SET_QNAME, writer);
        } else {
            Utils.serializeObject(results, DataServiceConstants.CQL_RESULT_SET_QNAME, writer, wsddStream);
        }
    }
    
    
    /**
     * Create an instance of CQLQuery from the specified input stream. The stream
     * must contain an XML representation of the CQLQuery. If the reader is
     * null, an IllegalArgumentException will be thown.
     * 
     * @param xmlStream
     * @return an instance of CQLQueryResults from the specified input stream.
     * @throws Exception
     *             on null argument or deserialization failure
     */
    public static CQLQueryResults deserializeCQLQueryResults(InputStream xmlStream) throws Exception {
        return deserializeCQLQueryResults(xmlStream, null);
    }
    
    
    /**
     * Create an instance of CQLQueryResults from the specified input stream, 
     * using the configuration supplied by the WSDD stream. The xml stream
     * must contain an XML representation of the CQLQueryResults. If the reader is
     * null, an IllegalArgumentException will be thown.
     * 
     * @param xmlStream
     * @param wsddStream
     * @return an instance of CQLQueryResuls from the specified input stream.
     * @throws Exception
     *             on null argument or deserialization failure
     */
    public static CQLQueryResults deserializeCQLQueryResults(InputStream xmlStream, InputStream wsddStream) throws Exception {
        if (xmlStream == null) {
            throw new IllegalArgumentException("Null is not a valid argument");
        }
        
        InputStreamReader reader = new InputStreamReader(xmlStream);
        CQLQueryResults results = null;
        if (wsddStream == null) {
            results = (CQLQueryResults) Utils.deserializeObject(reader, CQLQueryResults.class);
        } else {
            results = (CQLQueryResults) Utils.deserializeObject(reader, CQLQueryResults.class, wsddStream);
        }
        return results;
    }
    
    
    /**
     * Write the XML representation of the specified query results to the specified
     * writer. If either are null, an IllegalArgumentException will be thown.
     * 
     * @param results
     * @param writer
     * @throws Exception
     */
    public static void serializeDCQLQueryResults(DCQLQueryResultsCollection results, Writer writer) throws Exception {
        serializeDCQLQueryResults(results, writer, null);
    }
    
    
    /**
     * Write the XML representation of the specified query results to the specified
     * writer, using the configuration specified by the WSDD configuration.
     * If either the results or writer are null, an IllegalArgumentException will be thown.
     * 
     * @param results
     * @param writer
     * @param wsddStream
     * @throws Exception
     */
    public static void serializeDCQLQueryResults(DCQLQueryResultsCollection results, Writer writer, InputStream wsddStream) throws Exception {
        if (results == null || writer == null) {
            throw new IllegalArgumentException("Null is not a valid argument");
        }
        if (wsddStream == null) {
            Utils.serializeObject(results, DCQLConstants.DCQL_RESULTS_QNAME, writer);
        } else {
            Utils.serializeObject(results, DCQLConstants.DCQL_RESULTS_QNAME, writer, wsddStream);
        }
    }
    
    
    /**
     * Create an instance of DCQLQueryResultsCollection from the specified input stream. 
     * The stream must contain an XML representation of the DCQLQueryResultsCollection.
     * If the reader is null, an IllegalArgumentException will be thown.
     * 
     * @param xmlStream
     * @return an instance of DCQLQueryResultsCollection from the specified input stream.
     * @throws Exception
     *             on null argument or deserialization failure
     */
    public static DCQLQueryResultsCollection deserializeDCQLQueryResults(InputStream xmlStream) throws Exception {
        return deserializeDCQLQueryResults(xmlStream, null);
    }
    
    
    /**
     * Create an instance of DCQLQueryResultsCollection from the specified input stream,
     * using the configuration supplied by the WSDD stream.  The xml stream
     * must contain an XML representation of the DCQLQueryResultsCollection.
     * If the reader is null, an IllegalArgumentException will be thown.
     * 
     * @param xmlStream
     * @param wsddStream
     * @return an instance of DCQLQueryResultsCollection from the specified input stream.
     * @throws Exception
     *             on null argument or deserialization failure
     */
    public static DCQLQueryResultsCollection deserializeDCQLQueryResults(InputStream xmlStream, InputStream wsddStream) throws Exception {
        if (xmlStream == null) {
            throw new IllegalArgumentException("Null is not a valid argument");
        }
        
        InputStreamReader reader = new InputStreamReader(xmlStream);
        DCQLQueryResultsCollection results = null;
        if (wsddStream == null) {
            results = (DCQLQueryResultsCollection) Utils.deserializeObject(
                reader, DCQLQueryResultsCollection.class);
        } else {
            results = (DCQLQueryResultsCollection) Utils.deserializeObject(
                reader, DCQLQueryResultsCollection.class, wsddStream);
        }
        return results;
    }
}
