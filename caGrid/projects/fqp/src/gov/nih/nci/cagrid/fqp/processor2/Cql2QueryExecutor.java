package gov.nih.nci.cagrid.fqp.processor2;

import gov.nih.nci.cagrid.data.client.DataServiceClient;
import gov.nih.nci.cagrid.fqp.common.SerializationUtils;
import gov.nih.nci.cagrid.fqp.processor.exceptions.RemoteDataServiceException;

import java.io.StringWriter;
import java.rmi.RemoteException;

import org.apache.axis.types.URI.MalformedURIException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.cql2.CQLQuery;
import org.cagrid.cql2.results.CQLQueryResults;
import org.globus.gsi.GlobusCredential;


/**
 * Cql2QueryExecutor
 * Provides a consistent way to query data services.
 * If the data service does not support CQL 2, the attempted query will be 
 * converted to CQL 1.  If the query cannot be converted, an exception is thrown
 * 
 * @author David
 */
public class Cql2QueryExecutor {
    protected static Log LOG = LogFactory.getLog(Cql2QueryExecutor.class.getName());


    /**
     * Executes the specified query against the specified service, properly
     * handling remote exceptions.
     * 
     * @param cqlQuery
     * @param targetServiceURL
     * @return The results of querying a the data service
     * @throws RemoteDataServiceException
     */
    public static CQLQueryResults queryDataService(CQLQuery cqlQuery, String targetServiceURL)
        throws RemoteDataServiceException {
        return queryDataService(cqlQuery, targetServiceURL, null);
    }


    /**
     * Executes the specified query against the specified service, properly
     * handling remote exceptions.
     * 
     * @param cqlQuery
     * @param targetServiceURL
     * @param cred
     *            The credentials to use to invoke the data service
     * @return The results of querying a the data service
     * @throws RemoteDataServiceException
     */
    public static CQLQueryResults queryDataService(CQLQuery cqlQuery, String targetServiceURL, GlobusCredential cred)
        throws RemoteDataServiceException {

        if (LOG.isDebugEnabled()) {
            try {
                StringWriter s = new StringWriter();
                SerializationUtils.serializeCQL2Query(cqlQuery, s);
                LOG.debug("Sending, to service (" + targetServiceURL + "), Query:\n" + s.toString());
                s.close();
            } catch (Exception e) {
                LOG.error("Problem in debug printout of CQL query:" + e.getMessage(), e);
            }
        }

        CQLQueryResults cqlResults = null;
        try {
            DataServiceClient client = new DataServiceClient(targetServiceURL, cred);
            // if we have been supplied a credential, make sure we always use it
            if (cred != null) {
                client.setAnonymousPrefered(false);
            }
            cqlResults = client.executeQuery(cqlQuery);
        } catch (MalformedURIException e) {
            throw new RemoteDataServiceException("Invalid target service URL:" + targetServiceURL, e);
        } catch (RemoteException e) {
            LOG.error("Problem querying remote service:" + targetServiceURL, e);
            throw new RemoteDataServiceException("Problem querying data service at URL:" + targetServiceURL, e);
        }
        return cqlResults;
    }
}
