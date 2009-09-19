/*
 * This class is no longer used by the Data Service,
 * and should be removed if the implementation it
 * contains is no longer needed.
 */

package gov.nih.nci.cagrid.tests.stubs.cql;

import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.cqlresultset.CQLQueryResults;
import gov.nih.nci.cagrid.data.MalformedQueryException;
import gov.nih.nci.cagrid.data.QueryProcessingException;
import gov.nih.nci.cagrid.data.cql.CQLQueryProcessor;

import java.util.Properties;

/** 
 *  StubCQLQueryProcessor
 *  This CQL Query Processor is provided as a stub to begin implementing CQL against your
 *  backend data source.  If another CQL query processor implementation is used, 
 *  this file may be safely deleted
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * 
 * @created Aug 24, 2006 
 * @version $Id: StubCQLQueryProcessor.java,v 1.1 2006-10-09 18:50:01 mccon012 Exp $ 
 * @deprecated The stub CQL query processor is a placeholder to provide a starting point for
 * implementation of CQL against a backend data source.
 */
public class StubCQLQueryProcessor extends CQLQueryProcessor {

	public CQLQueryResults processQuery(CQLQuery cqlQuery) throws MalformedQueryException, QueryProcessingException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("processQuery() is not yet implemented");
	}


	public Properties getRequiredParameters() {
		// TODO Auto-generated method stub
		return new Properties();
	}
}
