package org.cagrid.cql.utilities;

import javax.xml.namespace.QName;

public interface CQLConstants {

    public static final String CQL_NAMESPACE_URI = "http://CQL.caBIG/1/gov.nih.nci.cagrid.CQLQuery";
    public static final QName CQL_QUERY_QNAME = new QName(CQL_NAMESPACE_URI, "CQLQuery");
    
    public static final String CQL2_NAMESPACE_URI = "http://CQL.caBIG/2/org.cagrid.cql2";
    public static final QName CQL2_QUERY_QNAME = new QName(CQL2_NAMESPACE_URI, "CQLQuery");
}
