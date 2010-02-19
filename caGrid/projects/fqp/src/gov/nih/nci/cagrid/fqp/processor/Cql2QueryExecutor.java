package gov.nih.nci.cagrid.fqp.processor;

import gov.nih.nci.cagrid.data.client.DataServiceClient;
import gov.nih.nci.cagrid.data.utilities.DataServiceFeatureDiscoveryUtil;
import gov.nih.nci.cagrid.fqp.common.SerializationUtils;
import gov.nih.nci.cagrid.fqp.common.TimeLimitedCache;
import gov.nih.nci.cagrid.fqp.processor.exceptions.RemoteDataServiceException;

import java.io.StringWriter;
import java.rmi.RemoteException;

import org.apache.axis.message.addressing.Address;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.axis.types.URI.MalformedURIException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.cql.utilities.CQL1ResultsToCQL2ResultsConverter;
import org.cagrid.cql.utilities.CQL2toCQL1Converter;
import org.cagrid.cql.utilities.QueryConversionException;
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
    public static final long CQL2_SUPPORT_CACHE_TIME = 20 * 60 * 1000; // 20 minutes
    public static final int MAX_CACHED_CQL2_SUPPORT = 50; // max number of services to cache CQL2 support info for
    
    protected static Log LOG = LogFactory.getLog(Cql2QueryExecutor.class.getName());
    protected static TimeLimitedCache<String, Boolean> cql2Support = 
        new TimeLimitedCache<String, Boolean>(CQL2_SUPPORT_CACHE_TIME, MAX_CACHED_CQL2_SUPPORT);


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
                LOG.debug("Sending query to service (" + targetServiceURL + "):\n" + s.toString());
                s.close();
            } catch (Exception e) {
                LOG.error("Problem in debug printout of CQL 2 query: " + e.getMessage(), e);
            }
        }
        
        CQLQueryResults cqlResults = null;
        boolean supportsCql2 = true;
        try {
            supportsCql2 = serviceSupportsCql2(targetServiceURL);
        } catch (Exception ex) {
            throw new RemoteDataServiceException("Error determining support for CQL 2: " + ex.getMessage(), ex);
        }
        
        DataServiceClient client = null;
        try {
            client = new DataServiceClient(targetServiceURL, cred);
        } catch (MalformedURIException ex) {
            throw new RemoteDataServiceException("Invalid target service URL:" + targetServiceURL, ex);
        } catch (RemoteException ex) {
            String message = "Problem creating client for " + targetServiceURL + ": " + ex.getMessage();
            LOG.error(message, ex);
            throw new RemoteDataServiceException(message, ex);
        }
        // if we have been supplied a credential, make sure we always use it
        if (cred != null) {
            client.setAnonymousPrefered(false);
        }
        
        if (supportsCql2) {
            LOG.debug("Service " + targetServiceURL + " natively supports CQL 2");
            try {
                cqlResults = client.executeQuery(cqlQuery);
            } catch (RemoteException e) {
                LOG.error("Problem querying remote service:" + targetServiceURL, e);
                throw new RemoteDataServiceException("Problem querying data service at URL:" + targetServiceURL, e);
            }
        } else {
            LOG.debug("Converting CQL 2 to CQL 1 for " + targetServiceURL);
            gov.nih.nci.cagrid.cqlquery.CQLQuery cql1Query = null;
            try {
                cql1Query = CQL2toCQL1Converter.convertToCql1Query(cqlQuery);
            } catch (QueryConversionException ex) {
                throw new RemoteDataServiceException("Erroe converting query to CQL 1 for " +
                    targetServiceURL + ": " + ex.getMessage(), ex);
            }
            try {
                if (LOG.isDebugEnabled()) {
                    try {
                        StringWriter s = new StringWriter();
                        SerializationUtils.serializeCQLQuery(cql1Query, s);
                        LOG.debug("Sending converted CQL 1 query to service (" + targetServiceURL + "):\n" + s.toString());
                        s.close();
                    } catch (Exception e) {
                        LOG.error("Problem in debug printout of CQL query: " + e.getMessage(), e);
                    }
                }
                gov.nih.nci.cagrid.cqlresultset.CQLQueryResults cql1Results = client.query(cql1Query);
                cqlResults = CQL1ResultsToCQL2ResultsConverter.convertResults(cql1Results);
            } catch (RemoteException e) {
                LOG.error("Problem querying remote service:" + targetServiceURL, e);
                throw new RemoteDataServiceException("Problem querying data service at URL:" + targetServiceURL, e);
            }
        }
        return cqlResults;
    }
    
    
    private static synchronized boolean serviceSupportsCql2(String targetServiceUrl) throws Exception {
        LOG.debug("Determining CQL 2 support for " + targetServiceUrl);
        Boolean supportsCql2 = cql2Support.getItem(targetServiceUrl);
        if (supportsCql2 != null) {
            LOG.debug("Support flag was in the cache");
        } else {
            LOG.debug("Support flag was not in the cache");
            supportsCql2 = getAndCacheCql2Support(targetServiceUrl);
        }
        LOG.debug("Service " + targetServiceUrl + 
            (supportsCql2.booleanValue() ? " supports" : " does not support") + " CQL 2");
        return supportsCql2.booleanValue();
    }
    
    
    private static Boolean getAndCacheCql2Support(String targetServiceUrl) throws Exception {
        Boolean flag = Boolean.valueOf(
            DataServiceFeatureDiscoveryUtil.serviceSupportsCql2(
                new EndpointReferenceType(new Address(targetServiceUrl))));
        cql2Support.cacheItem(targetServiceUrl, flag);
        return flag;
    }
}
