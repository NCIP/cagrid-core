package gov.nih.nci.cagrid.fqp.processor2;

import gov.nih.nci.cagrid.data.client.DataServiceClient;
import gov.nih.nci.cagrid.data.utilities.DataServiceFeatureDiscoveryUtil;
import gov.nih.nci.cagrid.fqp.common.SerializationUtils;
import gov.nih.nci.cagrid.fqp.processor.exceptions.RemoteDataServiceException;

import java.io.StringWriter;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

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
    
    protected static Log LOG = LogFactory.getLog(Cql2QueryExecutor.class.getName());
    protected static Map<String, CachedCql2Support> cql2Support = new HashMap<String, CachedCql2Support>();
    protected static long CQL2_SUPPORT_CACHE_TIME = 20 * 60 * 1000; // 20 minutes

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
    
    
    private static boolean serviceSupportsCql2(String targetServiceUrl) throws Exception {
        LOG.debug("Determining CQL 2 support for " + targetServiceUrl);
        CachedCql2Support cached = cql2Support.get(targetServiceUrl);
        boolean supportsCql2 = false;
        if (cached != null) {
            LOG.debug("Support flag was in the cache");
            long age = System.currentTimeMillis() - cached.cacheTime;
            if (age > CQL2_SUPPORT_CACHE_TIME) {
                LOG.debug("Cached support flag is too old");
                cql2Support.remove(targetServiceUrl);
                supportsCql2 = getAndCacheCql2Support(targetServiceUrl);
            } else {
                LOG.debug("Support flag age does not excede " + CQL2_SUPPORT_CACHE_TIME + " ms");
                supportsCql2 = cached.supportsCql2;
            }
        } else {
            LOG.debug("Support flag was not in the cache");
            supportsCql2 = getAndCacheCql2Support(targetServiceUrl);
        }
        LOG.debug("Service " + targetServiceUrl + 
            (supportsCql2 ? " supports" : " does not support") + " CQL 2");
        return supportsCql2;
    }
    
    
    private static boolean getAndCacheCql2Support(String targetServiceUrl) throws Exception {
        boolean flag = DataServiceFeatureDiscoveryUtil.serviceSupportsCql2(
            new EndpointReferenceType(new Address(targetServiceUrl)));
        CachedCql2Support support = new CachedCql2Support();
        support.supportsCql2 = flag;
        support.cacheTime = System.currentTimeMillis();
        cql2Support.put(targetServiceUrl, support);
        return flag;
    }
    
    
    private static class CachedCql2Support {
        public boolean supportsCql2;
        public long cacheTime;
    }
}
