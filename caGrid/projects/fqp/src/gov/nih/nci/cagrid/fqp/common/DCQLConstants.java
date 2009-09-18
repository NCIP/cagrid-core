package gov.nih.nci.cagrid.fqp.common;

import gov.nih.nci.cagrid.data.DataServiceConstants;

import javax.xml.namespace.QName;


/**
 * @author oster
 * 
 */
public class DCQLConstants {

	// CQL Defined
	public static final String CQL_RESULT_SET_URI = DataServiceConstants.CQL_RESULT_SET_URI;
	public static final String CQL_QUERY_URI = DataServiceConstants.CQL_QUERY_URI;
	public static final String CQL_QUERY_NAME = DataServiceConstants.CQL_QUERY_TYPE;
	public static final QName CQL_QUERY_QNAME = new QName(CQL_QUERY_URI, CQL_QUERY_NAME);

	// DCQL
	public static final String DCQL_QUERY_URI = "http://caGrid.caBIG/1.0/gov.nih.nci.cagrid.dcql";
	public static final String DCQL_QUERY_NAME = "DCQLQuery";
	public static final QName DCQL_QUERY_QNAME = new QName(DCQL_QUERY_URI, DCQL_QUERY_NAME);
    
    public static final String DCQL_RESULTS_URI = "http://caGrid.caBIG/1.0/gov.nih.nci.cagrid.dcqlresult";
    public static final String DCQL_RESULTS_NAME = "DCQLQueryResultsCollection";
    public static final QName DCQL_RESULTS_QNAME = new QName(DCQL_RESULTS_URI, DCQL_RESULTS_NAME);

}
