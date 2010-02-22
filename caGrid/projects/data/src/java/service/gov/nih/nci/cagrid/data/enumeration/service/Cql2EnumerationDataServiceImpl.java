package gov.nih.nci.cagrid.data.enumeration.service;

import gov.nih.nci.cagrid.data.MalformedQueryException;
import gov.nih.nci.cagrid.data.QueryProcessingException;
import gov.nih.nci.cagrid.data.faults.MalformedQueryExceptionType;
import gov.nih.nci.cagrid.data.faults.QueryProcessingExceptionType;
import gov.nih.nci.cagrid.data.service.BaseDataServiceImpl;
import gov.nih.nci.cagrid.data.service.DataServiceInitializationException;
import gov.nih.nci.cagrid.enumeration.stubs.response.EnumerationResponseContainer;
import gov.nih.nci.cagrid.wsenum.common.WsEnumConstants;
import gov.nih.nci.cagrid.wsenum.utils.EnumConfigDiscoveryUtil;
import gov.nih.nci.cagrid.wsenum.utils.EnumIteratorFactory;
import gov.nih.nci.cagrid.wsenum.utils.IterImplType;

import java.net.URL;
import java.util.Iterator;

import javax.xml.namespace.QName;

import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.cql.utilities.CQLConstants;
import org.cagrid.cql2.CQLQuery;
import org.cagrid.cql2.results.CQLObjectResult;
import org.cagrid.cql2.results.CQLResult;
import org.globus.ws.enumeration.EnumIterator;
import org.globus.ws.enumeration.EnumProvider;
import org.globus.ws.enumeration.EnumResource;
import org.globus.ws.enumeration.EnumResourceHome;
import org.globus.ws.enumeration.VisibilityProperties;
import org.globus.wsrf.ResourceKey;
import org.globus.wsrf.container.ServiceHost;
import org.globus.wsrf.utils.AddressingUtils;
import org.xmlsoap.schemas.ws._2004._09.enumeration.EnumerationContextType;

/**
 * Cql2EnumerationDataServiceImpl
 * Enumeration query method implementation for CQL 2 data services
 * 
 * @author David
 */
public class Cql2EnumerationDataServiceImpl extends BaseDataServiceImpl {

    private static Log LOG = LogFactory.getLog(Cql2EnumerationDataServiceImpl.class);


    public Cql2EnumerationDataServiceImpl() throws DataServiceInitializationException {
        super();
    }


    public EnumerationResponseContainer executeEnumerationQuery(CQLQuery query) 
        throws QueryProcessingExceptionType, MalformedQueryExceptionType {
        Iterator<CQLResult> resultsIterator;
        try {
            resultsIterator = processCql2QueryAndIterate(query);
        } catch (QueryProcessingException ex) {
            throw getTypedException(ex, new QueryProcessingExceptionType());
        } catch (MalformedQueryException ex) {
            throw getTypedException(ex, new MalformedQueryExceptionType());
        }

        // get the service property for the enum iterator type
        IterImplType implType = EnumConfigDiscoveryUtil.getConfiguredIterImplType();

        // wrap up the results iterator in an EnumIterator implementation
        LOG.debug("Creating EnumIterator for results");
        EnumIterator enumIter = null;
        try {
            // need to pass the QName for whatever the result type is (Object, attribute, etc),
            // so the iterator is wrapped by a supporting tool that can determine that
            CQL2ResultsTypeDeterminingIterator wrapIter = 
                new CQL2ResultsTypeDeterminingIterator(resultsIterator);
            enumIter = EnumIteratorFactory.createIterator(implType, wrapIter, 
                wrapIter.getResultQName(), getServerConfigWsddStream());
        } catch (Exception ex) {
            throw getTypedException(
                new QueryProcessingException("Error creating EnumIterator implementation: " + ex.getMessage(), ex),
                new QueryProcessingExceptionType());
        }

        LOG.debug("Creating enumeration resource");
        EnumerationResponseContainer container = null;
        try {
            // set up the enumeration resource
            EnumResourceHome resourceHome = EnumResourceHome.getEnumResourceHome();
            VisibilityProperties visibility = new VisibilityProperties(
                "cagrid/" + WsEnumConstants.CAGRID_ENUMERATION_SERVICE_NAME, null);

            EnumResource resource = resourceHome.createEnumeration(enumIter, visibility, false);
            ResourceKey key = resourceHome.getKey(resource);

            // create the enumeration context
            EnumerationContextType enumContext = EnumProvider.createEnumerationContextType(key);

            // create the context's EPR
            URL baseURL = ServiceHost.getBaseURL();
            // TODO: the "cagrid" part is configurable, so we need to read this from somewhere
            String serviceURI = baseURL.toString() + "cagrid/" + WsEnumConstants.CAGRID_ENUMERATION_SERVICE_NAME;

            EndpointReferenceType epr = AddressingUtils.createEndpointReference(serviceURI, key);

            // Create the response container and return it
            container = new EnumerationResponseContainer();
            container.setContext(enumContext);
            container.setEPR(epr);
        } catch (Exception ex) {
            throw getTypedException(
                new QueryProcessingException("Error creating enum resource: " + ex.getMessage(), ex),
                new QueryProcessingExceptionType());
        }
        return container;
    }
    
    
    private class CQL2ResultsTypeDeterminingIterator implements Iterator<CQLResult> {
        
        private Iterator<CQLResult> realIterator = null;
        private CQLResult firstResult = null;
        boolean triedFirstResult = false;
        boolean returnedFirstResult = false;
        
        public CQL2ResultsTypeDeterminingIterator(Iterator<CQLResult> realIterator) {
            this.realIterator = realIterator;
        }
        

        public boolean hasNext() {
            return realIterator.hasNext();
        }

        
        public CQLResult next() {
            CQLResult item = null;
            if (returnedFirstResult) {
                item = realIterator.next();
            } else {
                item = getFirstResult();
                returnedFirstResult = true;
            }
            return item;
        }

        
        public void remove() {
            throw new UnsupportedOperationException("remove() is not supported");
        }
        
        
        public QName getResultQName() {
            QName name = null;
            CQLResult first = getFirstResult();
            if (first == null) {
                // default to object results
                name = CQLConstants.CQL_RESULT_ELEMENT_QNAMES.get(CQLObjectResult.class);
            } else {
                name = CQLConstants.CQL_RESULT_ELEMENT_QNAMES.get(first.getClass());
            }
            return name;
        }
        
        
        private CQLResult getFirstResult() {
            if (!triedFirstResult) {
                if (realIterator.hasNext()) {
                    firstResult = realIterator.next();
                }
                triedFirstResult = true;
            }
            return firstResult;
        }
    }
}
