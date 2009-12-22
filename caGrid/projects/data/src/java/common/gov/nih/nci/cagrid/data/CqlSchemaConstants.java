package gov.nih.nci.cagrid.data;

import javax.xml.namespace.QName;

public interface CqlSchemaConstants {

    // query schema constants
    public static final String CQL_QUERY_SCHEMA = "1_gov.nih.nci.cagrid.CQLQuery.xsd";
    public static final String CQL_RESULT_SET_SCHEMA = "1_gov.nih.nci.cagrid.CQLResultSet.xsd";
    public static final String CQL_QUERY_URI = "http://CQL.caBIG/1/gov.nih.nci.cagrid.CQLQuery";
    public static final String CQL_RESULT_SET_URI = "http://CQL.caBIG/1/gov.nih.nci.cagrid.CQLResultSet";
    public static final String CQL_QUERY_TYPE = gov.nih.nci.cagrid.cqlquery.CQLQuery.class.getName();
    public static final String CQL_RESULT_SET_TYPE = gov.nih.nci.cagrid.cqlresultset.CQLQueryResults.class.getName();
    public static final QName CQL_QUERY_QNAME = new QName(CQL_QUERY_URI, "CQLQuery");
    public static final QName CQL_RESULT_SET_QNAME = new QName(CQL_RESULT_SET_URI, "CQLQueryResults");
    public static final QName CQL_RESULT_COLLECTION_QNAME = new QName(CQL_RESULT_SET_URI, "CQLQueryResultCollection");
}
