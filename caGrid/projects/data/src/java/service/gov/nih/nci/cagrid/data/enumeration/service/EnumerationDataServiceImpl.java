package gov.nih.nci.cagrid.data.enumeration.service;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.cqlresultset.CQLAttributeResult;
import gov.nih.nci.cagrid.cqlresultset.CQLCountResult;
import gov.nih.nci.cagrid.cqlresultset.CQLObjectResult;
import gov.nih.nci.cagrid.cqlresultset.CQLQueryResults;
import gov.nih.nci.cagrid.data.DataServiceConstants;
import gov.nih.nci.cagrid.data.MalformedQueryException;
import gov.nih.nci.cagrid.data.QueryProcessingException;
import gov.nih.nci.cagrid.data.cql.CQLQueryProcessor;
import gov.nih.nci.cagrid.data.cql.LazyCQLQueryProcessor;
import gov.nih.nci.cagrid.data.faults.MalformedQueryExceptionType;
import gov.nih.nci.cagrid.data.faults.QueryProcessingExceptionType;
import gov.nih.nci.cagrid.data.service.BaseServiceImpl;
import gov.nih.nci.cagrid.data.service.DataServiceInitializationException;
import gov.nih.nci.cagrid.data.service.ServiceConfigUtil;
import gov.nih.nci.cagrid.data.utilities.CQLQueryResultsIterator;
import gov.nih.nci.cagrid.enumeration.stubs.response.EnumerationResponseContainer;
import gov.nih.nci.cagrid.wsenum.common.WsEnumConstants;
import gov.nih.nci.cagrid.wsenum.utils.DummyEnumIterator;
import gov.nih.nci.cagrid.wsenum.utils.EnumConfigDiscoveryUtil;
import gov.nih.nci.cagrid.wsenum.utils.EnumIteratorFactory;
import gov.nih.nci.cagrid.wsenum.utils.IterImplType;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
 * TODO:I am the service side implementation class.  IMPLEMENT AND DOCUMENT ME
 * 
 * @created by Introduce Toolkit version 1.0
 * 
 */
public class EnumerationDataServiceImpl extends BaseServiceImpl {

    private static Log LOG = LogFactory.getLog(EnumerationDataServiceImpl.class);
	
	public EnumerationDataServiceImpl() throws DataServiceInitializationException {
		super();
	}
    
	
	public EnumerationResponseContainer enumerationQuery(gov.nih.nci.cagrid.cqlquery.CQLQuery cqlQuery) throws RemoteException, 
		gov.nih.nci.cagrid.data.faults.MalformedQueryExceptionType, 
		gov.nih.nci.cagrid.data.faults.QueryProcessingExceptionType {
        fireAuditQueryBegins(cqlQuery);
        
        try {
            preProcess(cqlQuery);
        } catch (MalformedQueryException ex) {
            throw (MalformedQueryExceptionType) getTypedException(ex, new MalformedQueryExceptionType());
        } catch (QueryProcessingException ex) {
            throw (QueryProcessingExceptionType) getTypedException(ex, new QueryProcessingExceptionType());
        }
		
		CQLQueryProcessor processor = null;
        try {
            processor = getCqlQueryProcessorInstance();
        } catch (QueryProcessingException ex) {
            throw (QueryProcessingExceptionType) getTypedException(ex, new QueryProcessingExceptionType());
        }
		EnumIterator enumIter = null;
		try {
			if (processor instanceof LazyCQLQueryProcessor) {
				enumIter = processLazyQuery((LazyCQLQueryProcessor) processor, cqlQuery);
			} else {
				enumIter = processQuery(processor, cqlQuery);
			}
		} catch (gov.nih.nci.cagrid.data.QueryProcessingException ex) {
            fireAuditQueryProcessingFailure(cqlQuery, ex);
			throw (QueryProcessingExceptionType) getTypedException(ex, new QueryProcessingExceptionType());
		} catch (gov.nih.nci.cagrid.data.MalformedQueryException ex) {
			throw (MalformedQueryExceptionType) getTypedException(ex, new MalformedQueryExceptionType());
		} catch (FileNotFoundException ex) {
			throw (QueryProcessingExceptionType) getTypedException(ex, new QueryProcessingExceptionType());
		} catch (IOException ex) {
			throw (QueryProcessingExceptionType) getTypedException(ex, new QueryProcessingExceptionType());
		}
		
		try {
		    EnumResourceHome resourceHome = EnumResourceHome.getEnumResourceHome();
            VisibilityProperties visibility = new VisibilityProperties(
                "cagrid/" + WsEnumConstants.CAGRID_ENUMERATION_SERVICE_NAME, null);
            
            EnumResource resource = resourceHome.createEnumeration(enumIter, visibility, false);
            ResourceKey key = resourceHome.getKey(resource);
            
            EnumerationContextType enumContext = 
                EnumProvider.createEnumerationContextType(key);
            
            URL baseURL = ServiceHost.getBaseURL();
            String serviceURI = baseURL.toString() + "cagrid/" + WsEnumConstants.CAGRID_ENUMERATION_SERVICE_NAME;

            EndpointReferenceType epr = AddressingUtils.createEndpointReference(serviceURI, key);
            
            EnumerationResponseContainer container = new EnumerationResponseContainer();
            container.setContext(enumContext);
            container.setEPR(epr);
            return container;
		} catch (Exception ex) {
			throw (QueryProcessingExceptionType) getTypedException(ex, new QueryProcessingExceptionType());
		}
	}	
	
	
	private EnumIterator processQuery(CQLQueryProcessor processor, CQLQuery query)
		throws gov.nih.nci.cagrid.data.QueryProcessingException, 
		gov.nih.nci.cagrid.data.MalformedQueryException,
		FileNotFoundException, IOException {
        // a placeholder for the enum iterator
        EnumIterator enumIter = null;
		// perform the query
		CQLQueryResults results = processor.processQuery(query);
        // fire off the results auditing
        fireAuditQueryResults(query, results);
		// pump the results into a list
		List<Object> resultList = new LinkedList<Object>();
		
		try {
			String serverConfigLocation = ServiceConfigUtil.getConfigProperty(
				DataServiceConstants.SERVER_CONFIG_LOCATION);
			InputStream configStream = new FileInputStream(serverConfigLocation);
			Iterator resIter = new CQLQueryResultsIterator(results, configStream);
			while (resIter.hasNext()) {
				resultList.add(resIter.next());
			}
			if (resultList.size() != 0) {
			    // create the EnumIterator from the objects
			    configStream = new FileInputStream(serverConfigLocation);
			    Class resultClass = resultList.get(0).getClass();
			    QName name = Utils.getRegisteredQName(resultClass);

			    // get the service property for the enum iterator type
			    IterImplType implType = EnumConfigDiscoveryUtil.getConfiguredIterImplType();
			    LOG.debug("Using enum iterator of type " + implType);

			    enumIter = EnumIteratorFactory.createIterator(implType, resultList, name, configStream);
            } else {
                LOG.debug("No results to enumerate, creating dummy EnumIterator instance");
                enumIter = new DummyEnumIterator();
            }
		} catch (Exception ex) {
			throw new QueryProcessingException(ex);
		}
        return enumIter;
	}
	
	
	private EnumIterator processLazyQuery(LazyCQLQueryProcessor processor, CQLQuery query)
		throws gov.nih.nci.cagrid.data.QueryProcessingException, 
		gov.nih.nci.cagrid.data.MalformedQueryException {
		// perform the query
		Iterator results = processor.processQueryLazy(query);
        // TODO: fire results auditing, but HOW?
        
        try {
            // figure out the result type
            QName name = null;
            if (query.getQueryModifier() == null) {
                name = CQLObjectResult.getTypeDesc().getXmlType();
            } else if (query.getQueryModifier().isCountOnly()) {
                name = CQLCountResult.getTypeDesc().getXmlType();
            } else {
                name = CQLAttributeResult.getTypeDesc().getXmlType();
            }
            
            // get the service property for the enum iterator type
            IterImplType implType = EnumConfigDiscoveryUtil.getConfiguredIterImplType();
            String serverConfigLocation = ServiceConfigUtil.getConfigProperty(
                DataServiceConstants.SERVER_CONFIG_LOCATION);
            InputStream wsddStream = new FileInputStream(serverConfigLocation);

            // create an iterator
            EnumIterator iterator = EnumIteratorFactory.createIterator(implType, results, name, wsddStream);
            return iterator;
        } catch (Exception ex) {
            throw new QueryProcessingException(ex);
        }
	}
}

