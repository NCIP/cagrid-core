package gov.nih.nci.cagrid.data.utilities;

import gov.nih.nci.cagrid.data.mapping.ClassToQname;
import gov.nih.nci.cagrid.data.mapping.Mappings;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
 * Utility for creating CQL 2 Results
 * 
 * @author David
 */
public class CQL2ResultsCreationUtil {
        
    private Map<String, QName> classToQname = null;
    
    public CQL2ResultsCreationUtil(Mappings mappings) {
        classToQname = new HashMap<String, QName>();
        initializeMappings(mappings);
    }
    
    
    private void initializeMappings(Mappings mappings) {
        for (ClassToQname c2q : mappings.getMapping()) {
            classToQname.put(c2q.getClassName(), QName.valueOf(c2q.getQname()));
        }
    }
    

    public CQLQueryResults createObjectResults(List<Object> data, String targetClassname) throws ResultsCreationException {
        CQLQueryResults results = new CQLQueryResults();
        results.setTargetClassname(targetClassname);
        QName qname = classToQname.get(targetClassname);
        // pushing all object results into a list to avoid calling size() on the data list
        // this is done because the caCORE SDK returns results lists as a proxy to the actual
        // results, and size() requires materializing everything in the result set at once,
        // a potentially very expensive operation
        List<CQLObjectResult> objectResults = new LinkedList<CQLObjectResult>();
        for (Object o : data) {
            MessageElement elem = new MessageElement(qname, o);
            CQLObjectResult singleResult = new CQLObjectResult(new MessageElement[] {elem});
            objectResults.add(singleResult);
        }
        CQLObjectResult[] resultArray = new CQLObjectResult[objectResults.size()];
        objectResults.toArray(resultArray);
        results.setObjectResult(resultArray);
        return results;
    }
    
    
    public CQLQueryResults createAttributeResults(
        List<Object[]> attributeArrays, String[] attributeNames, String targetClassname)
        throws ResultsCreationException {
        CQLQueryResults results = new CQLQueryResults();
        results.setTargetClassname(targetClassname);
        List<CQLAttributeResult> attributeResults = new LinkedList<CQLAttributeResult>();
        for (Object[] array : attributeArrays) {
            if (array.length != attributeNames.length) {
                throw new ResultsCreationException(
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
    
    
    public CQLQueryResults createDistinctAttributeResults(
        List<Object> attributeValues, String attributeName, String targetClassname) 
        throws ResultsCreationException {
        List<Object[]> attributeArrays = new LinkedList<Object[]>();
        for (Object value : attributeValues) {
            attributeValues.add(new Object[] {value});
        }
        return createAttributeResults(attributeArrays, new String[] {attributeName}, targetClassname);
    }
    
    
    public CQLQueryResults createAggregationResult(
        String valueAsString, Aggregation aggregation, String targetClassname) {
        CQLQueryResults results = new CQLQueryResults();
        results.setTargetClassname(targetClassname);
        CQLAggregateResult countAggregate = new CQLAggregateResult();
        countAggregate.setAggregation(aggregation);
        countAggregate.setValue(valueAsString);
        results.setAggregationResult(countAggregate);
        return results;
    }
    
    
    public CQLQueryResults createCountResults(long count, String targetClassname) {
        return createAggregationResult(Long.valueOf(count).toString(), Aggregation.COUNT, targetClassname);
    }
}
