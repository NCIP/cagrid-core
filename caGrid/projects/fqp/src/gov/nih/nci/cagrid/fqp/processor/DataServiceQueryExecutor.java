package gov.nih.nci.cagrid.fqp.processor;

import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.cqlresultset.CQLQueryResults;
import gov.nih.nci.cagrid.data.client.DataServiceClient;
import gov.nih.nci.cagrid.fqp.common.SerializationUtils;
import gov.nih.nci.cagrid.fqp.processor.exceptions.RemoteDataServiceException;

import java.io.StringWriter;
import java.rmi.RemoteException;

import org.apache.axis.types.URI.MalformedURIException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.globus.gsi.GlobusCredential;


/**
 * Used to consistently contact remote data services.
 * 
 * @author oster
 */
public class DataServiceQueryExecutor {
    protected static Log LOG = LogFactory.getLog(DataServiceQueryExecutor.class.getName());


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
                SerializationUtils.serializeCQLQuery(cqlQuery, s);
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
            cqlResults = client.query(cqlQuery);
        } catch (MalformedURIException e) {
            throw new RemoteDataServiceException("Invalid target service URL:" + targetServiceURL, e);
        } catch (RemoteException e) {
            LOG.error("Problem querying remote service:" + targetServiceURL, e);
            throw new RemoteDataServiceException("Problem querying data service at URL:" + targetServiceURL, e);
        }
        return cqlResults;
    }
}
