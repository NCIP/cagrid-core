package org.cagrid.data.test.system.enumeration;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.cqlquery.Attribute;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.cqlquery.Group;
import gov.nih.nci.cagrid.cqlquery.LogicalOperator;
import gov.nih.nci.cagrid.cqlquery.Predicate;
import gov.nih.nci.cagrid.data.enumeration.client.EnumerationDataServiceClient;
import gov.nih.nci.cagrid.data.faults.MalformedQueryExceptionType;
import gov.nih.nci.cagrid.data.faults.QueryProcessingExceptionType;
import gov.nih.nci.cagrid.enumeration.stubs.response.EnumerationResponseContainer;
import gov.nih.nci.cagrid.testing.system.deployment.ServiceContainer;
import gov.nih.nci.cagrid.testing.system.haste.Step;
import gov.nih.nci.cagrid.wsenum.utils.EnumerationResponseHelper;

import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import javax.xml.soap.SOAPElement;

import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.axis.types.URI;
import org.apache.axis.utils.ClassUtils;
import org.cagrid.data.test.system.TestQueryResultsGenerator;
import org.globus.ws.enumeration.ClientEnumIterator;
import org.projectmobius.bookstore.Book;
import org.xmlsoap.schemas.ws._2004._09.enumeration.DataSource;
import org.xmlsoap.schemas.ws._2004._09.enumeration.Release;

/** 
 *  InvokeEnumerationDataServiceStep
 *  Testing step to invoke an enumeration data service
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>  * 
 * @created Nov 23, 2006 
 * @version $Id: InvokeEnumerationDataServiceStep.java,v 1.3 2009-04-17 19:41:52 dervin Exp $ 
 */
public class InvokeEnumerationDataServiceStep extends Step {
	
    private ServiceContainer container;
	private String serviceName;
	
	public InvokeEnumerationDataServiceStep(ServiceContainer container, String serviceName) {
        this.container = container;
		this.serviceName = serviceName;
	}
	
    
    private static DataSource createDataSource(EndpointReferenceType epr) throws RemoteException {
        InputStream resourceAsStream = ClassUtils.getResourceAsStream(
            InvokeEnumerationDataServiceStep.class, "client-config.wsdd");
        DataSource port = EnumerationResponseHelper.createDataSource(epr, resourceAsStream);
        return port;
    }
    

	public void runStep() throws Throwable {
		System.out.println("Running step " + getClass().getName());
		String serviceUrl = getServiceUrl();
		System.out.println("Invoking service at URL " + serviceUrl);
		// create the generic enumeration client
		EnumerationDataServiceClient client = new EnumerationDataServiceClient(serviceUrl);
		
		// iterate over an enumeration response
		iterateEnumeration(client);
		
        // make sure invalid classes still throw exceptions
		queryForInvalidClass(client);
		
        // make sure malformed queries behave as expected
		submitMalformedQuery(client);
    }
	
	
	private void queryForInvalidClass(EnumerationDataServiceClient client) throws Exception {
		CQLQuery query = new CQLQuery();
		gov.nih.nci.cagrid.cqlquery.Object target = new gov.nih.nci.cagrid.cqlquery.Object();
		target.setName("non.existant.class");
		query.setTarget(target);
		EnumerationResponseContainer enumContainer = null;
		try {
			enumContainer = client.enumerationQuery(query);
		} catch (QueryProcessingExceptionType ex) {
			assertTrue("Query Processing Exception Type thrown", true);
		} finally {
			if (enumContainer != null && enumContainer.getContext() != null) {
				Release release = new Release();
				release.setEnumerationContext(enumContainer.getContext());
                createDataSource(enumContainer.getEPR()).releaseOp(release);
			}
		}
	}
	
	
	private void submitMalformedQuery(EnumerationDataServiceClient client) throws Exception {
		CQLQuery query = new CQLQuery();
		gov.nih.nci.cagrid.cqlquery.Object target = new gov.nih.nci.cagrid.cqlquery.Object();
		target.setName(Book.class.getName());
		Attribute attrib1 = new Attribute("name", Predicate.LIKE, "E%");
		target.setAttribute(attrib1);
		Group group = new Group();
		group.setLogicRelation(LogicalOperator.AND);
		group.setAttribute(new Attribute[] {
			new Attribute("author", Predicate.IS_NOT_NULL, ""),
			new Attribute("ISBN", Predicate.IS_NULL, "")
		});
		target.setGroup(group);
		query.setTarget(target);
		EnumerationResponseContainer enumContainer = null;
		try {
			enumContainer = client.enumerationQuery(query);
		} catch (MalformedQueryExceptionType ex) {
			assertTrue("Malformed Query Exception Type thrown", true);
		} finally {
			if (enumContainer != null && enumContainer.getContext() != null) {
				Release release = new Release();
				release.setEnumerationContext(enumContainer.getContext());
				createDataSource(enumContainer.getEPR()).releaseOp(release);
			}
		}		
	}
	
	
	private EnumerationResponseContainer queryForBooks(EnumerationDataServiceClient client) throws Exception {
		CQLQuery query = new CQLQuery();
		gov.nih.nci.cagrid.cqlquery.Object target = new gov.nih.nci.cagrid.cqlquery.Object();
		target.setName(Book.class.getName());
		query.setTarget(target);
		EnumerationResponseContainer enumContainer = null;
		try {
			enumContainer = client.enumerationQuery(query);
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
		return enumContainer;
	}
	
	
	private void iterateEnumeration(EnumerationDataServiceClient client) throws Exception {
		EnumerationResponseContainer enumContainer = queryForBooks(client);
        
        DataSource dataSource = createDataSource(enumContainer.getEPR());
        
		/*
		 * This is the preferred way to access an enumeration, but the client enum iterator hides
		 * remote exceptions from the user and throws an empty NoSuchElement exception.
		 */
		ClientEnumIterator iter = new ClientEnumIterator(dataSource, enumContainer.getContext());
        List<Object> returnedObjects = new LinkedList<Object>();
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
                returnedObjects.add(instance);
            }
        } catch (NoSuchElementException ex) {
            if (returnedObjects.size() == 0) {
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
        List goldObjects = TestQueryResultsGenerator.getResultBooks();
        // same number of results?
        assertEquals("Unexpected number of results returned from the enumeration", goldObjects.size(), returnedObjects.size());
        // verify each returned object matches one of the expected objects
        Iterator returnedObjectIter = returnedObjects.iterator();
        while (returnedObjectIter.hasNext()) {
            Book returnedBook = (Book) returnedObjectIter.next();
            boolean bookFound = goldObjects.contains(returnedBook);
            if (!bookFound) {
                // serialize it so we can see what happened
                StringWriter writer = new StringWriter();
                Utils.serializeObject(returnedBook, Book.getTypeDesc().getXmlType(), writer);
                System.err.println("Unexpected object found in results:");
                System.err.println(writer.getBuffer().toString());
                fail("Unexpected object found in results");
            }
        }
        // verify every gold object exists in the results
        Iterator goldObjectIter = goldObjects.iterator();
        while (goldObjectIter.hasNext()) {
            Book goldBook = (Book) goldObjectIter.next();
            boolean bookFound = returnedObjects.contains(goldBook);
            if (!bookFound) {
                // serialize it so we can see what happened
                StringWriter writer = new StringWriter();
                Utils.serializeObject(goldBook, Book.getTypeDesc().getXmlType(), writer);
                System.err.println("Expected object NOT found in results:");
                System.err.println(writer.getBuffer().toString());
                fail("Expected object NOT found in results");
            }
        }
	}
	
	
	private String getServiceUrl() throws Exception {
        URI baseUri = container.getContainerBaseURI();
        return baseUri.toString() + "cagrid/" + serviceName;
	}
}
