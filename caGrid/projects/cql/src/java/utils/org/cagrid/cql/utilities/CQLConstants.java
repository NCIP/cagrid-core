package org.cagrid.cql.utilities;

import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.cagrid.cql2.results.CQLAggregateResult;
import org.cagrid.cql2.results.CQLAttributeResult;
import org.cagrid.cql2.results.CQLObjectResult;
import org.cagrid.cql2.results.CQLResult;
import org.cagrid.cql2.results.ExtendedCQLResult;

public class CQLConstants {

    public static final String CQL_NAMESPACE_URI = "http://CQL.caBIG/1/gov.nih.nci.cagrid.CQLQuery";
    public static final QName CQL_QUERY_QNAME = new QName(CQL_NAMESPACE_URI, "CQLQuery");
    
    public static final String CQL2_NAMESPACE_URI = "http://CQL.caBIG/2/org.cagrid.cql2";
    public static final QName CQL2_QUERY_QNAME = new QName(CQL2_NAMESPACE_URI, "CQLQuery");
    
    public static final String CQL2_RESULTS_NAMESPACE_URI = "http://CQL.caBIG/2/org.cagrid.cql2.results";
    public static final QName CQL2_RESULTS_QNAME = new QName(CQL2_RESULTS_NAMESPACE_URI, "CQLQueryResults");
    
    public static final Map<Class<? extends CQLResult>, QName> CQL2_RESULTS_QNAMES = 
        new HashMap<Class<? extends CQLResult>, QName>();
    
    static {
        CQL2_RESULTS_QNAMES.put(CQLObjectResult.class, new QName(CQL2_RESULTS_NAMESPACE_URI, "CQLObjectResult"));
        CQL2_RESULTS_QNAMES.put(CQLAttributeResult.class, new QName(CQL2_RESULTS_NAMESPACE_URI, "CQLAttributeResult"));
        CQL2_RESULTS_QNAMES.put(CQLAggregateResult.class, new QName(CQL2_RESULTS_NAMESPACE_URI, "CQLAggregateResult"));
        CQL2_RESULTS_QNAMES.put(ExtendedCQLResult.class, new QName(CQL2_RESULTS_NAMESPACE_URI, "ExtendedCQLResult"));
    }
    
    
    public static final Map<Class<? extends CQLResult>, QName> CQL_RESULT_ELEMENT_QNAMES = 
        new HashMap<Class<? extends CQLResult>, QName>();
    
    static {
        CQL_RESULT_ELEMENT_QNAMES.put(CQLObjectResult.class, new QName(CQL2_RESULTS_NAMESPACE_URI, "ObjectResult"));
        CQL_RESULT_ELEMENT_QNAMES.put(CQLAttributeResult.class, new QName(CQL2_RESULTS_NAMESPACE_URI, "AttributeResult"));
        CQL_RESULT_ELEMENT_QNAMES.put(CQLAggregateResult.class, new QName(CQL2_RESULTS_NAMESPACE_URI, "AggregateResult"));
        CQL_RESULT_ELEMENT_QNAMES.put(ExtendedCQLResult.class, new QName(CQL2_RESULTS_NAMESPACE_URI, "ExtendedResult"));
    }
    
    
    private CQLConstants() {
        // no instantiation, just constants
    }
}
