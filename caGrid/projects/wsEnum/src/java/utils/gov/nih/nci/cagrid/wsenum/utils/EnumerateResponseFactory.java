package gov.nih.nci.cagrid.wsenum.utils;

import gov.nih.nci.cagrid.enumeration.stubs.response.EnumerationResponseContainer;
import gov.nih.nci.cagrid.wsenum.common.WsEnumConstants;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.axis.message.MessageElement;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.globus.ws.enumeration.EnumIterator;
import org.globus.ws.enumeration.EnumProvider;
import org.globus.ws.enumeration.EnumResource;
import org.globus.ws.enumeration.EnumResourceHome;
import org.globus.ws.enumeration.IndexedObjectFileEnumIterator;
import org.globus.ws.enumeration.SimpleEnumIterator;
import org.globus.ws.enumeration.VisibilityProperties;
import org.globus.wsrf.ResourceKey;
import org.globus.wsrf.container.ServiceHost;
import org.globus.wsrf.utils.AddressingUtils;
import org.globus.wsrf.utils.io.IndexedObjectFileUtils;
import org.xmlsoap.schemas.ws._2004._09.enumeration.EnumerateResponse;
import org.xmlsoap.schemas.ws._2004._09.enumeration.EnumerationContextType;
import org.xmlsoap.schemas.ws._2004._09.enumeration.ExpirationType;

/** 
 *  EnumerateResponseFactory
 *  Utility to create Enumeration resources, set up Enum Iterators,
 *  and finally create EnumerateResponses  
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * 
 * @created Aug 17, 2006 
 * @version $Id: EnumerateResponseFactory.java,v 1.1 2007-05-16 15:00:57 dervin Exp $ 
 */
public class EnumerateResponseFactory {
    
    public static EnumerationResponseContainer createEnumerationResponse(EnumIterator enumIter)
        throws EnumerationCreationException {
        try {
            EnumResourceHome resourceHome = EnumResourceHome.getEnumResourceHome();
            VisibilityProperties visibility = new VisibilityProperties(
                "cagrid/" + WsEnumConstants.CAGRID_ENUMERATION_SERVICE_NAME, null);

            EnumResource resource = resourceHome.createEnumeration(
                enumIter, visibility, false);
            ResourceKey key = resourceHome.getKey(resource);

            EnumerationContextType enumContext = 
                EnumProvider.createEnumerationContextType(key);

            URL baseURL = ServiceHost.getBaseURL();
            String serviceURI = baseURL.toString() 
                + "cagrid/" + WsEnumConstants.CAGRID_ENUMERATION_SERVICE_NAME;

            EndpointReferenceType epr = 
                AddressingUtils.createEndpointReference(serviceURI, key);

            EnumerationResponseContainer container = new EnumerationResponseContainer();
            container.setContext(enumContext);
            container.setEPR(epr);
            return container;
        } catch (Exception ex) {
            throw new EnumerationCreationException(ex.getMessage(), ex);
        }
    }
    

	/**
	 * Creates an enumerate response using an in-memory SimpleEnumIterator,
	 * using the default ExpirationType
	 * 
	 * @param enumObjects
	 * 		The list of objects to enumerate over
	 * @param objectQName
	 * 		The QName of the objects
	 * @return
	 * 		An enumerate response instance
	 * @throws EnumerationCreationException
	 */
	public static EnumerateResponse createInMemoryResponse(List enumObjects, QName objectQName) 
		throws EnumerationCreationException {
		return createInMemoryResponse(enumObjects, objectQName, new ExpirationType());
	}
	
	
	/**
	 * Creates an enumerate response using an in-memory SimpleEnumIterator
	 * using the provided expiration information
	 * 
	 * @param enumObjects
	 * 		The list of objects to enumerate over
	 * @param objectQName
	 * 		The QName of the objects
	 * @param expiration
	 * 		The expiration information for the newly created resource
	 * @return
	 * 		An enumerate response instance
	 * @throws EnumerationCreationException
	 */
	public static EnumerateResponse createInMemoryResponse(List enumObjects, QName objectQName, ExpirationType expiration) 
		throws EnumerationCreationException {
		SimpleEnumIterator simpleIter = new SimpleEnumIterator(enumObjects, objectQName);
		return getResponse(simpleIter, false, expiration);
	}
	
	
	/**
	 * Creates an enumerate response using an IndexedObjectFileEnumIterator.
	 * This requires that all the objects given to this method must implement
	 * the <code>java.io.Serializable</code> interface.  The default ExpirationType will
	 * be used.
	 * 
	 * @param enumObjects
	 * 		The list of objects to be enumerated
	 * @param objectQName
	 * 		The QName of the objects
	 * @return
	 * 		An enumerate response instance
	 * @throws EnumerationCreationException
	 */
	public static EnumerateResponse createPersistantResponse(List enumObjects, QName objectQName) 
		throws EnumerationCreationException {
		return createPersistantResponse(enumObjects, objectQName, new ExpirationType());
	}
	
	
	/**
	 * Creates an enumerate response using an IndexedObjectFileEnumIterator.
	 * This requires that all the objects given to this method must implement
	 * the java.io.Serializable interface.  The given ExpirationType will
	 * be used to specify lifetime of the enumeration.
	 * 
	 * @param enumObjects
	 * 		The list of objects to be enumerated
	 * @param objectQName
	 * 		The QName of the objects
	 * @param expiration
	 * 		The expiration information for the enumeration
	 * @return
	 * 		An enumerate response instance
	 * @throws EnumerationCreationException
	 */
	public static EnumerateResponse createPersistantResponse(List enumObjects, QName objectQName, ExpirationType expiration) 
		throws EnumerationCreationException {
		File indexedFile = null;
		try {
			indexedFile = IndexedObjectFileUtils.createIndexedObjectFile(enumObjects);
			IndexedObjectFileEnumIterator fileIter = new IndexedObjectFileEnumIterator(indexedFile, objectQName);
			return getResponse(fileIter, true, expiration);
		} catch (IOException ex) {
			throw new EnumerationCreationException(ex.getClass().getName() + " -- " + ex.getMessage(), ex);
		}
	}
	
	
	/**
	 * Creates an enumeration response using a custom EnumIterator
	 * and the default ExpirationType.
	 * 
	 * @param iter
	 * 		The custom EnumIter implementation to use
	 * @param persist
	 * 		If <code>true</code>, the iterator will have it's contents
	 * 		serialized to disk.  This requires the objects returned by
	 * 		the iterator implement <code>java.io.Serializable</code>
	 * @return
	 * 		An enumerate response instance
	 * @throws EnumerationCreationException
	 */
	public static EnumerateResponse createCustomResponse(EnumIterator iter, boolean persist)
		throws EnumerationCreationException {
		return createCustomResponse(iter, persist, new ExpirationType());
	}
	
	
	/**
	 * Creates an enumeration response using a custom EnumIterator
	 * and the supplied ExpirationType.
	 * 
	 * @param iter
	 * 		The custom EnumIter implementation to use
	 * @param persist
	 * 		If <code>true</code>, the iterator will have it's contents
	 * 		serialized to disk.  This requires the objects returned by
	 * 		the iterator implement <code>java.io.Serializable</code>
	 * @param expiration
	 * 		The expiration information to use for the enumeration's lifetime.
	 * @return
	 * 		An enumerate response instance
	 * @throws EnumerationCreationException
	 */
	public static EnumerateResponse createCustomResponse(EnumIterator iter, boolean persist, ExpirationType expiration)
		throws EnumerationCreationException {
		return getResponse(iter, persist, expiration);
	}
	
	
	/**
	 * Private method to create the enumeration resource in the enum resource home
	 * 
	 * @param iter
	 * 		The enum iter instance to use
	 * @param persist
	 * 		True to use the enumeration impl's persistance (mostly broken)
	 * @param expiration
	 * 		Resource expiration information
	 * @return
	 * 		The enumerate response
	 * @throws EnumerationCreationException
	 */
	private static EnumerateResponse getResponse(EnumIterator iter, boolean persist, ExpirationType expiration) 
		throws EnumerationCreationException {
		try {
			EnumResourceHome resourceHome = EnumResourceHome.getEnumResourceHome();
			EnumResource resource = resourceHome.createEnumeration(iter, persist);
			ResourceKey key = resourceHome.getKey(resource);
			EnumerationContextType enumContext = 
				EnumProvider.createEnumerationContextType(key);
			
			EnumerateResponse response = new EnumerateResponse(new MessageElement[] {}, enumContext, expiration);
			return response;
		} catch (Exception ex) {
			throw new EnumerationCreationException(ex.getClass().getName() + " -- " + ex.getMessage(), ex);
		}
	}
}
