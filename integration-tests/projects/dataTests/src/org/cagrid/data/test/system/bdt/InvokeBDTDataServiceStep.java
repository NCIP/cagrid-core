package org.cagrid.data.test.system.bdt;

import gov.nih.nci.cagrid.bdt.client.BulkDataHandlerClient;
import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.common.XMLUtilities;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.data.bdt.client.BDTDataServiceClient;
import gov.nih.nci.cagrid.enumeration.stubs.response.EnumerationResponseContainer;
import gov.nih.nci.cagrid.introduce.extension.utils.AxisJdomUtils;
import gov.nih.nci.cagrid.testing.system.deployment.ServiceContainer;
import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.io.InputStream;
import java.io.StringReader;
import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.NoSuchElementException;

import javax.xml.soap.SOAPElement;

import org.apache.axis.EngineConfiguration;
import org.apache.axis.client.AxisClient;
import org.apache.axis.configuration.FileProvider;
import org.apache.axis.message.MessageElement;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.axis.types.URI;
import org.apache.axis.utils.ClassUtils;
import org.cagrid.data.test.system.enumeration.InvokeEnumerationDataServiceStep;
import org.globus.transfer.AnyXmlType;
import org.globus.transfer.EmptyType;
import org.globus.ws.enumeration.ClientEnumIterator;
import org.jdom.Element;
import org.projectmobius.bookstore.Book;
import org.xmlsoap.schemas.ws._2004._09.enumeration.DataSource;
import org.xmlsoap.schemas.ws._2004._09.enumeration.service.EnumerationServiceAddressingLocator;

/** 
 *  InvokeBDTDataServiceStep
 *  Step to invoke a BDT data service and exercise its methods
 * 
 * @author David Ervin
 * 
 * @created Mar 14, 2007 2:37:02 PM
 * @version $Id: InvokeBDTDataServiceStep.java,v 1.2 2008-08-21 15:07:25 dervin Exp $ 
 */
public class InvokeBDTDataServiceStep extends Step {
	
    private ServiceContainer container;
	private String serviceName;


	public InvokeBDTDataServiceStep(ServiceContainer container, String serviceName) {
        this.container = container;
		this.serviceName = serviceName;
	}
	

	public void runStep() throws Throwable {
		System.out.println("Running step " + getClass().getName());
		String serviceUrl = getServiceUrl();
		System.out.println("Invoking service at URL " + serviceUrl);
		
        // create the BDT service client handle
        BDTDataServiceClient bdtClient = new BDTDataServiceClient(serviceUrl);
        
        BulkDataHandlerClient bdtHandlerClient = startBdt(bdtClient);
        
        iterateEnumeration(bdtHandlerClient);
        
        invokeTransfer(bdtHandlerClient);
        
        // TODO: invoke getGridFTPURls()
	}
    
    
    private BulkDataHandlerClient startBdt(BDTDataServiceClient client) throws Exception {
        CQLQuery bookQuery = new CQLQuery();
        gov.nih.nci.cagrid.cqlquery.Object target = new gov.nih.nci.cagrid.cqlquery.Object();
        target.setName(Book.class.getName());
        bookQuery.setTarget(target);
        BulkDataHandlerClient bdtHandlerClient = client.bdtQuery(bookQuery);
        return bdtHandlerClient;
    }
    
    
    private EnumerationResponseContainer beginEnumeration(BulkDataHandlerClient client) throws Exception {
        EnumerationResponseContainer response = client.createEnumeration();
        assertNotNull("Enumeration response does not contain an enumeration context", response.getContext());
        return response;
    }
    
    
    private static DataSource createDataSource(EndpointReferenceType epr) throws RemoteException {

        EnumerationServiceAddressingLocator locator = new EnumerationServiceAddressingLocator();
        
        // attempt to load our context sensitive wsdd file
        InputStream resourceAsStream = ClassUtils.getResourceAsStream(
            InvokeEnumerationDataServiceStep.class, "client-config.wsdd");
        if (resourceAsStream != null) {
            // we found it, so tell axis to configure an engine to use it
            EngineConfiguration engineConfig = new FileProvider(resourceAsStream);
            // set the engine of the locator
            locator.setEngine(new AxisClient(engineConfig));
        }
        DataSource port = null;
        try {
            port = locator.getDataSourcePort(epr);
        } catch (Exception e) {
            throw new RemoteException("Unable to locate portType:" + e.getMessage(), e);
        }

        return port;
    }

    
    
    private void iterateEnumeration(BulkDataHandlerClient client) throws Exception {
        EnumerationResponseContainer response = beginEnumeration(client);
        
        /*
         * This is the preferred way to access an enumeration, but the client enum iterator hides
         * remote exceptions from the user and throws an empty NoSuchElement exception.
         */
        
        DataSource dataSource = createDataSource(response.getEPR());
        
        ClientEnumIterator iter = new ClientEnumIterator(dataSource, response.getContext());
        int resultCount = 0;
        try {
            while (iter.hasNext()) {
                SOAPElement elem = (SOAPElement) iter.next();
                String elemText = elem.toString();
                // ensure 'Book' at least appears in the text
                int bookIndex = elemText.indexOf("Book");
                if (bookIndex == -1) {
                    throw new NoSuchElementException("Element returned was not of the type Book!");
                }
                Object instance = null;
                try {
                    instance = Utils.deserializeObject(new StringReader(elemText), Book.class);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    fail("Error deserializing result from enumeration: " + ex.getMessage());
                }
                assertTrue("Deserialized object was not an instance of " 
                    + Book.class.getName(), instance instanceof Book);
                resultCount++;
            }
        } catch (NoSuchElementException ex) {
            if (resultCount == 0) {
                throw ex;
            }
        } finally {
            iter.release();
            try {
                iter.next();
                fail("Call to next() after release should have failed!");
            } catch (NoSuchElementException ex) {
                // expected
            } catch (Exception ex) {
                ex.printStackTrace();
                fail("Exception other than NoSuchElementException thrown: " + ex.getClass().getName());
            }
        }
        assertTrue("No results were returned from the enumeration", resultCount != 0);
    }
    
    
    private void invokeTransfer(BulkDataHandlerClient client) throws Exception {
        AnyXmlType any = client.get(new EmptyType());
        MessageElement[] anyElements = any.get_any();
        assertNotNull("Content from transfer was null", anyElements);
        assertTrue("Content from transfer was empty", anyElements.length != 0);
        
        Element resultsElement = AxisJdomUtils.fromMessageElement(anyElements[0]);
        // get object results out of it
        int resultCount = 0;
        Iterator objectElementIter = resultsElement.getChildren(
            "ObjectResult", resultsElement.getNamespace()).iterator();
        while (objectElementIter.hasNext()) {
            resultCount++;
            Element objectResultElement = (Element) objectElementIter.next();
            Element objectElement = (Element) objectResultElement.getChildren().get(0);
            // convert the object element back to a string
            String elementString = XMLUtilities.elementToString(objectElement);
            assertTrue("Element did not contain a Book object", 
                elementString.indexOf("Book") != -1);
        }
        assertTrue("Object results were not returned", resultCount != 0);
    }
	
	
	private String getServiceUrl() throws Exception {
        URI baseUri = container.getContainerBaseURI();
        return baseUri.toString() + "cagrid/" + serviceName;
	}
}
