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
    
    // CQL 2 constants
    public static final String CQL2_SCHEMA_FILENAME = "CQLQueryComponents.xsd";
    public static final String CQL2_RESULTS_SCHEMA_FILENAME = "CQLQueryResults.xsd";
    public static final String CQL2_QUERY_NAMESPACE = "http://CQL.caBIG/2/org.cagrid.cql2";
    public static final String CQL2_RESULTS_NAMESPACE = "http://CQL.caBIG/2/org.cagrid.cql2.results";
    public static final QName CQL2_QUERY_QNAME = new QName(CQL2_QUERY_NAMESPACE, "CQLQuery");
    public static final QName CQL2_RESULTS_QNAME = new QName(CQL2_RESULTS_NAMESPACE, "CQLQueryResults");
    public static final QName CQL2_RESULT_QNAME = new QName(CQL2_RESULTS_NAMESPACE, "CQLResult");
}
