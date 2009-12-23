package gov.nih.nci.cagrid.data.enumeration.service;

import java.net.URL;
import java.util.Iterator;

import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.cql2.CQLQuery;
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

import gov.nih.nci.cagrid.data.CqlSchemaConstants;
import gov.nih.nci.cagrid.data.MalformedQueryException;
import gov.nih.nci.cagrid.data.QueryProcessingException;
import gov.nih.nci.cagrid.data.service.BaseDataServiceImpl;
import gov.nih.nci.cagrid.data.service.DataServiceInitializationException;
import gov.nih.nci.cagrid.enumeration.stubs.response.EnumerationResponseContainer;
import gov.nih.nci.cagrid.wsenum.common.WsEnumConstants;
import gov.nih.nci.cagrid.wsenum.utils.EnumConfigDiscoveryUtil;
import gov.nih.nci.cagrid.wsenum.utils.EnumIteratorFactory;
import gov.nih.nci.cagrid.wsenum.utils.IterImplType;

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
        throws QueryProcessingException, MalformedQueryException {
        Iterator<CQLResult> resultsIterator = processCql2QueryAndIterate(query);

        // get the service property for the enum iterator type
        IterImplType implType = EnumConfigDiscoveryUtil.getConfiguredIterImplType();

        // wrap up the results iterator in an EnumIterator implementation
        LOG.debug("Creating EnumIterator for results");
        EnumIterator enumIter = null;
        try {
            enumIter = EnumIteratorFactory.createIterator(implType, resultsIterator, 
                CqlSchemaConstants.CQL2_RESULT_QNAME, getServerConfigWsddStream());
        } catch (Exception ex) {
            throw new QueryProcessingException("Error creating EnumIterator implementation: " 
                + ex.getMessage(), ex);
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
            // TODO: the "cagrid" part is configurable, so we need to read this// from somewhere
            String serviceURI = baseURL.toString() + "cagrid/" + WsEnumConstants.CAGRID_ENUMERATION_SERVICE_NAME;

            EndpointReferenceType epr = AddressingUtils.createEndpointReference(serviceURI, key);

            // Create the response container and return it
            container = new EnumerationResponseContainer();
            container.setContext(enumContext);
            container.setEPR(epr);
        } catch (Exception ex) {
            throw new QueryProcessingException("Error creating enum resource: " + ex.getMessage(), ex);
        }
        return container;
    }
}
