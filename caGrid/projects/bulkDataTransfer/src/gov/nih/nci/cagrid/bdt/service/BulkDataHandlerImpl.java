package gov.nih.nci.cagrid.bdt.service;

import gov.nih.nci.cagrid.bdt.service.globus.resource.BDTException;
import gov.nih.nci.cagrid.bdt.service.globus.resource.BDTResourceI;
import gov.nih.nci.cagrid.enumeration.stubs.response.EnumerationResponseContainer;
import gov.nih.nci.cagrid.wsenum.common.WsEnumConstants;

import java.net.URL;
import java.rmi.RemoteException;

import javax.naming.NamingException;

import org.apache.axis.message.addressing.EndpointReferenceType;
import org.globus.ws.enumeration.EnumIterator;
import org.globus.ws.enumeration.EnumProvider;
import org.globus.ws.enumeration.EnumResource;
import org.globus.ws.enumeration.EnumResourceHome;
import org.globus.ws.enumeration.VisibilityProperties;
import org.globus.wsrf.ResourceContext;
import org.globus.wsrf.ResourceKey;
import org.globus.wsrf.container.ServiceHost;
import org.globus.wsrf.utils.AddressingUtils;
import org.xmlsoap.schemas.ws._2004._09.enumeration.EnumerationContextType;


/**
 * gov.nih.nci.cagrid.bdtI TODO:DOCUMENT ME
 * 
 * @created by Introduce Toolkit version 1.0
 */
public class BulkDataHandlerImpl extends BulkDataHandlerImplBase {

	public BulkDataHandlerImpl() throws RemoteException {
		super();
	}


	public EnumerationResponseContainer createEnumeration() throws RemoteException {
		try {
			BDTResourceI bdtResource = (BDTResourceI) ResourceContext.getResourceContext().getResource();
			EnumIterator iter = bdtResource.createEnumeration();
			EnumResourceHome resourceHome = null;
			try {
				resourceHome = EnumResourceHome.getEnumResourceHome();
			} catch (NamingException ex) {
			    throw new RemoteException(ex.getMessage(), ex);
			}
            
            VisibilityProperties visibility = new VisibilityProperties(
                "cagrid/" + WsEnumConstants.CAGRID_ENUMERATION_SERVICE_NAME, null);
            
            EnumResource resource = resourceHome.createEnumeration(iter, visibility, false);
            ResourceKey key = resourceHome.getKey(resource);
            
			try {
                EnumerationContextType enumContext = 
                    EnumProvider.createEnumerationContextType(key);
                
                URL baseURL = ServiceHost.getBaseURL();
                String serviceURI = baseURL.toString() + "cagrid/" + WsEnumConstants.CAGRID_ENUMERATION_SERVICE_NAME;

                
                EndpointReferenceType epr = AddressingUtils.createEndpointReference(serviceURI, key);
                
                EnumerationResponseContainer container = new EnumerationResponseContainer();
                container.setContext(enumContext);
                container.setEPR(epr);
                return container;
			} catch (Exception e) {
				e.printStackTrace();
				throw new RemoteException(e.getMessage(), e);
			}
		} catch (BDTException e) {
			e.printStackTrace();
			throw new RemoteException(e.getMessage(), e);
		}
	}


	public org.globus.transfer.AnyXmlType get(org.globus.transfer.EmptyType empty) throws RemoteException {
		try {
			BDTResourceI bdtResource = (BDTResourceI) ResourceContext.getResourceContext().getResource();
			return bdtResource.get();
		} catch (BDTException e) {
			e.printStackTrace();
			throw new RemoteException(e.getMessage(), e);
		}
	}
	
	public org.apache.axis.types.URI[] getGridFTPURLs() throws RemoteException {
		try {
			BDTResourceI bdtResource = (BDTResourceI) ResourceContext.getResourceContext().getResource();
			return bdtResource.getGridFTPURLs();
		} catch (BDTException e) {
			e.printStackTrace();
			throw new RemoteException(e.getMessage(), e);
		}
	}
	
}
