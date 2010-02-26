package org.cagrid.cql.utilities;

import gov.nih.nci.cagrid.common.Utils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.text.DateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.xml.namespace.QName;

import org.cagrid.cql2.Aggregation;
import org.cagrid.cql2.results.CQLAggregateResult;
import org.cagrid.cql2.results.CQLAttributeResult;
import org.cagrid.cql2.results.CQLObjectResult;
import org.cagrid.cql2.results.CQLQueryResults;
import org.cagrid.cql2.results.TargetAttribute;
import org.exolab.castor.types.AnyNode;

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
    public static CQLQueryResults createObjectResults(Collection<?> data, String targetClassname, QName targetQName, 
        InputStream wsddStream) throws Exception {
        ByteArrayInputStream reusableWsdd = null;
        if (wsddStream != null) {
            reusableWsdd = new ByteArrayInputStream(Utils.inputStreamToStringBuffer(
                wsddStream).toString().getBytes());
        }
        CQLQueryResults results = new CQLQueryResults();
        results.setTargetClassname(targetClassname);
        // pushing everything into a list instead of an array to avoid calling .size() on
        // the data list.  Many implementations (i.e. caCORE SDK) provide a list impl
        // backed by a database and paged into memory as required.  Calling size()
        // on some of these implementations causes everything to be loaded at once.
        List<CQLObjectResult> objectResults = new LinkedList<CQLObjectResult>();
        for (Object o : data) {
            reusableWsdd.reset();
            StringWriter writer = new StringWriter();
            if (reusableWsdd != null) {
                Utils.serializeObject(o, targetQName, writer, reusableWsdd);
            } else {
                Utils.serializeObject(o, targetQName, writer);   
            }
            AnyNode node = AnyNodeHelper.convertStringToAnyNode(writer.getBuffer().toString());
            CQLObjectResult object = new CQLObjectResult(node);
            objectResults.add(object);
        }
        // back to an array
        CQLObjectResult[] resultArray = new CQLObjectResult[objectResults.size()];
        objectResults.toArray(resultArray);
        results.setObjectResult(resultArray);
        return results;
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
