package gov.nih.nci.cagrid.data.service;

import gov.nih.nci.cagrid.cqlresultset.CQLQueryResults;
import gov.nih.nci.cagrid.data.MalformedQueryException;
import gov.nih.nci.cagrid.data.QueryProcessingException;
import gov.nih.nci.cagrid.data.faults.MalformedQueryExceptionType;
import gov.nih.nci.cagrid.data.faults.QueryProcessingExceptionType;

import java.rmi.RemoteException;

import javax.naming.InitialContext;

import org.apache.axis.MessageContext;
import org.globus.wsrf.Constants;
import org.globus.wsrf.ResourceHome;

/**
  *  DataServiceImpl
  *  Implementation of the caGrid data service
  * 
  * @author David Ervin
  * 
  * @created May 17, 2007 2:20:26 PM
  * @version $Id$
 */
public class DataServiceImpl extends BaseCQL1DataServiceImpl {
	
	public DataServiceImpl() throws DataServiceInitializationException {
		super();
	}
	
	
	public gov.nih.nci.cagrid.cqlresultset.CQLQueryResults query(gov.nih.nci.cagrid.cqlquery.CQLQuery cqlQuery) 
		throws RemoteException, gov.nih.nci.cagrid.data.faults.QueryProcessingExceptionType, gov.nih.nci.cagrid.data.faults.MalformedQueryExceptionType {
        fireAuditQueryBegins(cqlQuery);
        
        try {
            preProcess(cqlQuery);
        } catch (MalformedQueryException ex) {
            throw (MalformedQueryExceptionType) getTypedException(ex, new MalformedQueryExceptionType());
        } catch (QueryProcessingException ex) {
            throw (QueryProcessingExceptionType) getTypedException(ex, new QueryProcessingExceptionType());
        }
		
		// process the query
		gov.nih.nci.cagrid.data.cql.CQLQueryProcessor processor = null;
		try {
			processor = getCqlQueryProcessorInstance();
		} catch (Exception ex) {
			throw (QueryProcessingExceptionType) getTypedException(ex, new QueryProcessingExceptionType());
		}
		try {
            CQLQueryResults results = processor.processQuery(cqlQuery);
            fireAuditQueryResults(cqlQuery, results);
            return results;
		} catch (gov.nih.nci.cagrid.data.QueryProcessingException ex) {
            fireAuditQueryProcessingFailure(cqlQuery, ex);
			throw (QueryProcessingExceptionType) getTypedException(ex, new QueryProcessingExceptionType());
		} catch (gov.nih.nci.cagrid.data.MalformedQueryException ex) {
			throw (MalformedQueryExceptionType) getTypedException(ex, new MalformedQueryExceptionType());
		}
	}
	
	
	public ResourceHome getResourceHome(String resourceKey) throws Exception {
		MessageContext ctx = MessageContext.getCurrentContext();

		ResourceHome resourceHome = null;
		
		String servicePath = ctx.getTargetService();

		String jndiName = Constants.JNDI_SERVICES_BASE_NAME + servicePath + "/" + resourceKey;
		try {
			javax.naming.Context initialContext = new InitialContext();
			resourceHome = (ResourceHome) initialContext.lookup(jndiName);
		} catch (Exception e) {
			throw new Exception("Unable to instantiate resource home. : " + resourceKey, e);
		}

		return resourceHome;
	}
}

