package org.cagrid.data.test.system.transfer;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.data.faults.MalformedQueryExceptionType;
import gov.nih.nci.cagrid.data.faults.QueryProcessingExceptionType;
import gov.nih.nci.cagrid.data.transfer.client.TransferDataServiceClient;
import gov.nih.nci.cagrid.testing.system.deployment.ServiceContainer;
import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.axis.types.URI;
import org.cagrid.cql.utilities.iterator.CQL2QueryResultsIterator;
import org.cagrid.cql2.AttributeValue;
import org.cagrid.cql2.BinaryPredicate;
import org.cagrid.cql2.CQLAttribute;
import org.cagrid.cql2.CQLGroup;
import org.cagrid.cql2.CQLQuery;
import org.cagrid.cql2.CQLTargetObject;
import org.cagrid.cql2.GroupLogicalOperator;
import org.cagrid.cql2.UnaryPredicate;
import org.cagrid.cql2.results.CQLQueryResults;
import org.cagrid.data.test.system.TestQueryResultsGenerator;
import org.cagrid.transfer.context.client.TransferServiceContextClient;
import org.cagrid.transfer.context.client.helper.TransferClientHelper;
import org.cagrid.transfer.context.stubs.types.TransferServiceContextReference;
import org.cagrid.transfer.descriptor.DataTransferDescriptor;
import org.projectmobius.bookstore.Book;

/** 
 *  InvokeTransferDataServiceStep
 *  Testing step to invoke a caGrid transfer data service
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>  * 
 * @created Nov 23, 2006 
 * @version $Id: InvokeTransferDataServiceStep.java,v 1.2 2009-04-17 19:21:12 dervin Exp $ 
 */
public class InvokeCql2TransferDataServiceStep extends Step {
	
    private ServiceContainer container;
	private String serviceName;
	
	public InvokeCql2TransferDataServiceStep(ServiceContainer container, String serviceName) {
        this.container = container;
		this.serviceName = serviceName;
	}
    

	public void runStep() throws Throwable {
		String serviceUrl = getServiceUrl();
		// create the generic transfer data service client
		TransferDataServiceClient client = new TransferDataServiceClient(serviceUrl);
		
		// run a query and transfer the results
		queryAndTransfer(client);
		
        // make sure invalid classes still throw exceptions
		queryForInvalidClass(client);
		
        // make sure malformed queries behave as expected
		submitMalformedQuery(client);
    }
	
	
	private void queryForInvalidClass(TransferDataServiceClient client) throws Throwable {
	    CQLQuery query = new CQLQuery();
        CQLTargetObject target = new CQLTargetObject();
        target.setClassName("non.existant.class");
        query.setCQLTargetObject(target);
		try {
            client.executeTransferQuery(query);
            fail("Query for non-existant class did not throw an exception!");
		} catch (QueryProcessingExceptionType ex) {
			// expected
		} catch (Exception ex) {
            ex.printStackTrace();
		    fail("Unexpected exception thrown: " + ex.getClass().getName());
        }
	}
	
	
	private void submitMalformedQuery(TransferDataServiceClient client) throws Throwable {
	    CQLQuery query = new CQLQuery();
        CQLTargetObject target = new CQLTargetObject();
        target.setClassName(Book.class.getName());
        CQLAttribute attrib = new CQLAttribute();
        attrib.setName("name");
        attrib.setBinaryPredicate(BinaryPredicate.LIKE);
        AttributeValue val1 = new AttributeValue();
        val1.setStringValue("E%");
        attrib.setAttributeValue(val1);
        target.setCQLAttribute(attrib);
        CQLGroup group = new CQLGroup();
        group.setLogicalOperation(GroupLogicalOperator.AND);
        CQLAttribute a1 = new CQLAttribute();
        a1.setName("author");
        a1.setUnaryPredicate(UnaryPredicate.IS_NOT_NULL);
        CQLAttribute a2 = new CQLAttribute();
        a2.setName("ISBN");
        a2.setUnaryPredicate(UnaryPredicate.IS_NULL);
        group.setCQLAttribute(new CQLAttribute[] {a1, a2});
        target.setCQLGroup(group);
        query.setCQLTargetObject(target);
        
		try {
            client.executeTransferQuery(query);
            fail("Malformed query did not throw an exception!");
		} catch (MalformedQueryExceptionType ex) {
			// expected
		} catch (Exception ex) {
            ex.printStackTrace();
		    fail("Unexpected exception thrown: " + ex.getClass().getName());
        }
	}
	
	
	private TransferServiceContextReference queryForBooks(TransferDataServiceClient client) throws Throwable {
	    CQLQuery query = new CQLQuery();
        CQLTargetObject target = new CQLTargetObject();
        target.setClassName(Book.class.getName());
        query.setCQLTargetObject(target);
        
        TransferServiceContextReference transferContext = null;
		try {
			transferContext = client.executeTransferQuery(query);
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
		return transferContext;
	}
	
	
	private void queryAndTransfer(TransferDataServiceClient client) throws Throwable {
		TransferServiceContextReference transferContext = queryForBooks(client);
        
		TransferServiceContextClient transferClient = null;
        try {
            transferClient = new TransferServiceContextClient(transferContext.getEndpointReference());
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error creating transfer client: " + ex.getMessage());
        }
        
        /*
         * FIXME: This throws a no deserializer found if the metadata is populated
         */
        DataTransferDescriptor transferDescriptor = null;
        try {
            transferDescriptor = transferClient.getDataTransferDescriptor();
        } catch (RemoteException ex) {
            ex.printStackTrace();
            fail("Error getting data transfer descriptor: " + ex.getMessage());
        }
        
        InputStream dataStream = null;
        try {
            dataStream = TransferClientHelper.getData(transferDescriptor);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error getting data input stream from transfer context: " + ex.getMessage());
        }
        
        ByteArrayOutputStream dataBuffer = new ByteArrayOutputStream();
        byte[] temp = new byte[1024];
        int bytesRead = -1;
        try {
            while ((bytesRead = dataStream.read(temp)) != -1) {
                dataBuffer.write(temp, 0, bytesRead);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            fail("Error reading data from transfer service: " + ex.getMessage());
        }
        
        try {
            dataStream.close();
        } catch (IOException ex) {
            System.err.println("Error closing data streams: " + ex.getMessage());
        }
        
        String xml = dataBuffer.toString();
        System.out.println("Got these results:");
        System.out.println(xml);
        StringReader xmlReader = new StringReader(xml);
        CQLQueryResults results = null;
        try {
            results = Utils.deserializeObject(xmlReader, CQLQueryResults.class);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error deserializing CQL 2 query results: " + ex.getMessage());
        }
        
        // release the transfer resource
        try {
            transferClient.destroy();
        } catch (RemoteException ex) {
            ex.printStackTrace();
            System.err.println("Error destroying transfer context: " + ex.getMessage());
        }
        
        // validate results from transfer
        List<Book> returnedObjects = new LinkedList<Book>();
        CQL2QueryResultsIterator iter = new CQL2QueryResultsIterator(results);
        while (iter.hasNext()) {
            Object instance = iter.next();
            assertTrue("Returned data was not of the type " + Book.class.getName(),
                instance instanceof Book);
            returnedObjects.add((Book) instance);
        }
        List<Book> goldObjects = TestQueryResultsGenerator.getResultBooks();
        // same number of results?
        assertEquals("Unexpected number of results returned from the transfer", goldObjects.size(), returnedObjects.size());
        // verify each returned object matches one of the expected objects
        Iterator<Book> returnedObjectIter = returnedObjects.iterator();
        while (returnedObjectIter.hasNext()) {
            Book returnedBook = returnedObjectIter.next();
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
        Iterator<Book> goldObjectIter = goldObjects.iterator();
        while (goldObjectIter.hasNext()) {
            Book goldBook = goldObjectIter.next();
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
