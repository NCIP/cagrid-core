package org.cagrid.cacore.sdk4x.cql2.processor;

import gov.nih.nci.cagrid.cql2.aggregations.Aggregation;
import gov.nih.nci.cagrid.cql2.modifiers.CQLQueryModifier;
import gov.nih.nci.cagrid.cql2.modifiers.DistinctAttribute;
import gov.nih.nci.cagrid.cql2.modifiers.NamedAttribute;
import gov.nih.nci.cagrid.cql2.results.CQLAggregateResult;
import gov.nih.nci.cagrid.cql2.results.CQLAttributeResult;
import gov.nih.nci.cagrid.cql2.results.TargetAttribute;
import gov.nih.nci.cagrid.data.QueryProcessingException;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


/** 
 *  QueryModifierProcessor
 *  Applies query modifiers to a query results list
 *  
 *  TODO: The things this class does should be pushed into the HQL query
 * 
 * @author David Ervin
 * 
 * @created Apr 2, 2008 11:42:10 AM
 * @version $Id: QueryModifierProcessor.java,v 1.3 2008/04/04 14:52:06 dervin Exp $ 
 */
public class QueryModifierProcessor {

    /**
     * Applies query modifiers to a list of domain objects.
     * 
     * @param targetObjectName
     *      The class name of the target domain object
     * @param baseObjects
     *      The domain objects
     * @param modifiers
     *      The query modifiers
     * @return
     *      Either CQLAttributeResult[] or CQLAggregateResult
     * @throws QueryProcessingException
     */
    public static Object applyQueryModifiers(String targetObjectName, List<Object> baseObjects,
        CQLQueryModifier modifiers) throws QueryProcessingException {
        // all modifiers start with attributes
        boolean distinct = false;
        NamedAttribute[] attributes = null;
        if (modifiers.getNamedAttribute() != null) {
            attributes = modifiers.getNamedAttribute();
        } else if (modifiers.getDistinctAttribute() != null) {
            distinct = true;
            attributes = new NamedAttribute[] {modifiers.getDistinctAttribute()};
        } else {
            throw new QueryProcessingException("Invalid query modifier; No attributes found");
        }
        
        List<TargetAttribute[]> attributeResults = handleAttributeList(targetObjectName, baseObjects, attributes);
        
        Object moddedResult = null;
        
        if (distinct) {
            // filter for distinct values
            List<TargetAttribute[]> minimizedResults = new ArrayList<TargetAttribute[]>();
            Set<String> distinctValues = new HashSet<String>();
            for (TargetAttribute[] instanceAttributes : attributeResults) {
                if (instanceAttributes.length != 1) {
                    throw new QueryProcessingException(
                        "INVALID DISTINCT ATTRIBUTE.  Expected 1 attribute, found " + instanceAttributes.length);
                }
                if (distinctValues.add(instanceAttributes[0].getValue())) {
                    minimizedResults.add(instanceAttributes);
                }
            }
            attributeResults = minimizedResults;
        }
        
        if (distinct && modifiers.getDistinctAttribute().getAggregation() != null) {
            moddedResult = createAggregateResult(
                modifiers.getDistinctAttribute(), attributeResults);
        } else {
            CQLAttributeResult[] cqlAttributeResults = new CQLAttributeResult[attributeResults.size()];
            for (int i = 0; i < attributeResults.size(); i++) {
                cqlAttributeResults[i] = new CQLAttributeResult(attributeResults.get(i));
            }
            moddedResult = cqlAttributeResults;
        }
        
        return moddedResult;
    }
    
    
    private static CQLAggregateResult createAggregateResult(
        DistinctAttribute distinctAttribute, 
        List<TargetAttribute[]> distinctAttributes) throws QueryProcessingException {
        CQLAggregateResult aggResult = new CQLAggregateResult();
        Aggregation aggregation = distinctAttribute.getAggregation();
        aggResult.setAggregation(aggregation);
        aggResult.setAttributeName(distinctAttribute.getAttributeName());
        if (aggregation.equals(Aggregation.COUNT)) {
            aggResult.setValue(String.valueOf(distinctAttributes.size()));
        } else if (aggregation.equals(Aggregation.MAX)) {
            if (distinctAttributes.size() != 0) {
                sortDistinctAttributeValues(distinctAttributes);
                // get last result
                TargetAttribute[] last = distinctAttributes.get(distinctAttributes.size() - 1);
                aggResult.setValue(last[0].getValue());
            } else {
                aggResult.setValue(null);
            }
        } else if (aggregation.equals(Aggregation.MIN)) {
            if (distinctAttributes.size() != 0) {
                sortDistinctAttributeValues(distinctAttributes);
                TargetAttribute[] first = distinctAttributes.get(0);
                aggResult.setValue(first[0].getValue());
            } else {
                aggResult.setValue(null);
            }
        } else {
            throw new QueryProcessingException("Unknown aggregation: " + aggregation.getValue());
        }

        return aggResult;
    }
    
    
    private static void sortDistinctAttributeValues(List<TargetAttribute[]> distinctAttributes) {
        Comparator<TargetAttribute[]> comparator = new Comparator<TargetAttribute[]>() {
            public int compare(TargetAttribute[] first, TargetAttribute[] compare) {
                String value1 = first[0].getValue();
                String value2 = compare[0].getValue();
                if (value1 != null) {
                    return value1.compareTo(value2);
                } else if (value1 == null && value2 == null){
                    return 0;
                } else {
                    return -1;
                }
            }
        };
        Collections.sort(distinctAttributes, comparator);
    }


    private static List<TargetAttribute[]> handleAttributeList(String targetObjectName, 
        List<Object> baseObjects, NamedAttribute[] attributes) throws QueryProcessingException {
        // get the java class of the target data type
        Class<?> targetClass = getClass(targetObjectName);
        
        // find the fields of the class referenced by the named attributes 
        Map<NamedAttribute, Method> attributeGetterMethods = new HashMap<NamedAttribute, Method>();
        for (NamedAttribute attribute : attributes) {
            Method attributeGetter = null;
            try {
                attributeGetter = ClassAccessUtilities.getNamedGetterMethod(
                    targetClass, attribute.getAttributeName());
            } catch (Exception ex) {
                throw new QueryProcessingException(
                    "Error finding getter for field of target object: " + ex.getMessage(), ex);
            }
            attributeGetterMethods.put(attribute, attributeGetter);
        }
        
        // walk the classes and extract attribute values
        List<TargetAttribute[]> results = new ArrayList<TargetAttribute[]>(baseObjects.size());
        for (Object instance : baseObjects) {
            TargetAttribute[] instanceAttributes = new TargetAttribute[attributes.length];
            int attribIndex = 0;
            for (NamedAttribute attribute : attributes) {
                Method getter = attributeGetterMethods.get(attribute);
                Object value = null;
                try {
                    value = getter.invoke(instance, new Object[0]);
                } catch (Exception ex) {
                    throw new QueryProcessingException(
                        "Error extracting value of attribute field: " + ex.getMessage(), ex);
                }
                instanceAttributes[attribIndex] = 
                    new TargetAttribute(attribute.getAttributeName(), valueToString(value));
                attribIndex++;
            }
            results.add(instanceAttributes);
        }
        return results;
    }
    
    
    private static String valueToString(Object rawValue) {
        if (rawValue == null) {
            return null;
        }
        return String.valueOf(rawValue);
    }


    private static Class<?> getClass(String className) throws QueryProcessingException {
        Class<?> clazz = null;
        try {
            clazz = QueryModifierProcessor.class.getClassLoader().loadClass(className);
        } catch (ClassNotFoundException ex) {
            throw new QueryProcessingException("Error loading target class: " + ex.getMessage(), ex);
        }
        return clazz;
    }
}
