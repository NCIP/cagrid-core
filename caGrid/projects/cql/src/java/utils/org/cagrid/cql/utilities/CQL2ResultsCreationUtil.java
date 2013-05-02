/**
*============================================================================
*  Copyright The Ohio State University Research Foundation, The University of Chicago - 
*	Argonne National Laboratory, Emory University, SemanticBits LLC, and 
*	Ekagra Software Technologies Ltd.
*
*  Distributed under the OSI-approved BSD 3-Clause License.
*  See http://ncip.github.com/cagrid-core/LICENSE.txt for details.
*============================================================================
**/
package org.cagrid.cql.utilities;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.text.DateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.axis.AxisEngine;
import org.apache.axis.EngineConfiguration;
import org.apache.axis.MessageContext;
import org.apache.axis.configuration.FileProvider;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.message.MessageElement;
import org.apache.axis.server.AxisServer;
import org.cagrid.cql2.Aggregation;
import org.cagrid.cql2.results.CQLAggregateResult;
import org.cagrid.cql2.results.CQLAttributeResult;
import org.cagrid.cql2.results.CQLObjectResult;
import org.cagrid.cql2.results.CQLQueryResults;
import org.cagrid.cql2.results.TargetAttribute;
import org.exolab.castor.types.AnyNode;
import org.globus.wsrf.encoding.ObjectSerializer;
import org.xml.sax.Attributes;

/**
 * CQL2ResultsCreationUtil
 * Utility for creating CQL 2 query results
 * 
 * @author David
 */
public class CQL2ResultsCreationUtil {

    private CQL2ResultsCreationUtil() {
        // no...
    }
    
    
    /**
     * Creates CQL 2 query results which contain CQLObjectResults
     * 
     * @param data
     *      A collection of the data to wrap as CQL 2 results
     * @param targetClassname
     *      The classname of the target data type which generated these results
     * @param targetQName
     *      The QName of the target data type which generated these results
     * @param wsddStream
     *      <b>Optional</b> stream to the client or server config.wsdd for custom serialization
     * @return
     */
    public static CQLQueryResults createObjectResults(Collection<?> data, String targetClassname, 
        QName targetQName, InputStream wsddStream) throws Exception {
        // consolidate the axis config and message context
        AxisEngine axisEngine = null;
        if (wsddStream != null) {
            // configure the axis engine to use the supplied wsdd file
            EngineConfiguration engineConfig = new FileProvider(wsddStream);
            axisEngine = new AxisServer(engineConfig);
        } else {
            // no wsdd, do the default
            axisEngine = new AxisServer();
        }
        MessageContext xmlMessageContext = new MessageContext(axisEngine);
        xmlMessageContext.setEncodingStyle("");
        xmlMessageContext.setProperty(AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        // the following two properties prevent xsd types from appearing in
        // every single element in the serialized XML
        xmlMessageContext.setProperty(AxisEngine.PROP_EMIT_ALL_TYPES, Boolean.FALSE);
        xmlMessageContext.setProperty(AxisEngine.PROP_SEND_XSI, Boolean.FALSE);
        
        // create the query results instance
        CQLQueryResults results = new CQLQueryResults();
        results.setTargetClassname(targetClassname);
        // pushing everything into a list instead of an array to avoid calling .size() on
        // the data list.  Many implementations (i.e. caCORE SDK) provide a list impl
        // backed by a database and paged into memory as required.  Calling size()
        // on some of these implementations causes everything to be loaded at once.
        List<CQLObjectResult> objectResults = new LinkedList<CQLObjectResult>();
        for (Object o : data) {
            String xml = serializeObject(o, xmlMessageContext, targetQName);
            AnyNode node = AnyNodeHelper.convertStringToAnyNode(xml);
            CQLObjectResult object = new CQLObjectResult(node);
            objectResults.add(object);
        }
        // back to an array
        CQLObjectResult[] resultArray = new CQLObjectResult[objectResults.size()];
        objectResults.toArray(resultArray);
        results.setObjectResult(resultArray);
        return results;
    }
    
    
    private static String serializeObject(Object obj, MessageContext context, QName targetQName) throws Exception {
        StringWriter writer = new StringWriter();

        // derive a message element for the object
        MessageElement element = (MessageElement) ObjectSerializer.toSOAPElement(obj, targetQName);

        // create a serialization context to use the new message context
        SerializationContext serializationContext = new SerializationContext(writer, context) {
            public void serialize(QName elemQName, Attributes attributes, Object value)
            throws IOException {
                serialize(elemQName, attributes, value, null, Boolean.FALSE, null);
            }
        };
        serializationContext.setPretty(true);

        // output the message element through the serialization context
        element.output(serializationContext);
        writer.write("\n");
        writer.flush();
        return writer.getBuffer().toString();
    }
    
    
    /**
     * Creates CQL 2 query results which contain CQLAttributeResults
     * 
     * @param data
     *      A collection of arrays of attribute values
     * @param targetClassname
     *      The classname of the target data type which generated these results
     * @param attributeNames
     *      The attribute names in the order in which the values appear in the data arrays
     * @return
     */
    public static CQLQueryResults createAttributeResults(Collection<Object[]> data, String targetClassname, String[] attributeNames) {
        CQLQueryResults results = new CQLQueryResults();
        results.setTargetClassname(targetClassname);
        List<CQLAttributeResult> attributeResults = new LinkedList<CQLAttributeResult>();
        for (Object[] array : data) {
            if (array.length != attributeNames.length) {
                throw new IllegalArgumentException(
                    "Number of attributes (" + array.length + ") and number of attribute names (" 
                    + attributeNames.length + ") must match!");
            }
            CQLAttributeResult singleResult = new CQLAttributeResult();
            TargetAttribute[] attributes = new TargetAttribute[array.length];
            for (int i = 0; i < array.length; i++) {
                String stringValue = null;
                if (array[i] != null) {
                    if (array[i] instanceof Date) {
                        stringValue = DateFormat.getDateInstance().format((Date) array[i]);
                    } else {
                        stringValue = String.valueOf(array[i]);
                    }
                }
                attributes[i] = new TargetAttribute(attributeNames[i], stringValue);
            }
            singleResult.setAttribute(attributes);
            attributeResults.add(singleResult);
        }
        CQLAttributeResult[] resultArray = new CQLAttributeResult[attributeResults.size()];
        attributeResults.toArray(resultArray);
        results.setAttributeResult(resultArray);
        return results;
    }
    

    public static CQLQueryResults createDistinctAttributeResults(
        List<Object> attributeValues, String targetClassname, String attributeName) {
        List<Object[]> attributeArrays = new LinkedList<Object[]>();
        for (Object value : attributeValues) {
            attributeValues.add(new Object[] {value});
        }
        return createAttributeResults(attributeArrays, targetClassname, new String[] {attributeName});
    }
    
    
    /**
     * Creates CQL 2 query results which contains a single CQLAggregateResult
     * 
     * @param data
     *      The aggregate data value
     * @param targetClassname
     *      The classname of the target data type which generated these results
     * @param attributeName
     *      The attribute name of the target which was aggregated
     * @param aggregation
     *      The aggregation operation which was performed
     * @return
     */
    public static CQLQueryResults createAggregateResults(String data, String targetClassname, 
        String attributeName, Aggregation aggregation) {
        CQLQueryResults results = new CQLQueryResults();
        results.setTargetClassname(targetClassname);
        CQLAggregateResult aggregateResult = new CQLAggregateResult(aggregation, attributeName, data);
        results.setAggregationResult(aggregateResult);
        return results;
    }
    
    
    public static CQLQueryResults createCountResults(long count, String targetClassname) {
        return createAggregateResults(String.valueOf(count), targetClassname, "id", Aggregation.COUNT);
    }
}
