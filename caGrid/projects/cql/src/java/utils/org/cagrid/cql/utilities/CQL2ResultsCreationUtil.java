package org.cagrid.cql.utilities;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.axis.message.MessageElement;
import org.cagrid.cql2.Aggregation;
import org.cagrid.cql2.results.CQLAggregateResult;
import org.cagrid.cql2.results.CQLAttributeResult;
import org.cagrid.cql2.results.CQLObjectResult;
import org.cagrid.cql2.results.CQLQueryResults;
import org.cagrid.cql2.results.TargetAttribute;

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
     * @return
     */
    public static CQLQueryResults createObjectResults(Collection<?> data, String targetClassname, QName targetQName) {
        CQLQueryResults results = new CQLQueryResults();
        results.setTargetClassname(targetClassname);
        // pushing everything into a list instead of an array to avoid calling .size() on
        // the data list.  Many implementations (i.e. caCORE SDK) provide a list impl
        // backed by a database and paged into memory as required.  Calling size()
        // on some of these implementations causes everything to be loaded at once.
        List<CQLObjectResult> objectResults = new LinkedList<CQLObjectResult>();
        for (Object o : data) {
            MessageElement[] objectElement = new MessageElement[] {
                new MessageElement(targetQName, o)
            };
            CQLObjectResult object = new CQLObjectResult(objectElement);
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
    public static CQLQueryResults createAttributeResults(Collection<?> data, String targetClassname, String[] attributeNames) {
        CQLQueryResults results = new CQLQueryResults();
        results.setTargetClassname(targetClassname);
        List<CQLAttributeResult> attribResults = new LinkedList<CQLAttributeResult>();
        for (Object attributeArray : data) {
            TargetAttribute[] attribs = new TargetAttribute[attributeNames.length];
            String[] attribValues = new String[attributeNames.length];
            if (attributeArray == null) {
                Arrays.fill(attribValues, null);
            } else if (attributeArray.getClass().isArray()) {
                for (int j = 0; j < Array.getLength(attributeArray); j++) {
                    Object singleValue = Array.get(attributeArray, j); 
                    attribValues[j] = singleValue == null ? null : singleValue.toString();
                }
            } else {
                // not an array, but a single value
                attribValues = new String[] {attributeArray.toString()};
            }
            
            for (int j = 0; j < attributeNames.length; j++) {
                attribs[j] = new TargetAttribute(attributeNames[j], attribValues[j]);
            }
            attribResults.add(new CQLAttributeResult(attribs));
        }
        CQLAttributeResult[] attributeResultArray = new CQLAttributeResult[attribResults.size()];
        attribResults.toArray(attributeResultArray);
        results.setAttributeResult(attributeResultArray);
        return results;
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
    public static CQLQueryResults createAggregateResults(String data, String targetClassname, String attributeName, Aggregation aggregation) {
        CQLQueryResults results = new CQLQueryResults();
        results.setTargetClassname(targetClassname);
        CQLAggregateResult aggregateResult = new CQLAggregateResult(aggregation, attributeName, data);
        results.setAggregationResult(aggregateResult);
        return results;
    }
}
