package org.cagrid.data.test.system;

import gov.nih.nci.cagrid.data.client.DataServiceClient;
import gov.nih.nci.cagrid.data.faults.MalformedQueryExceptionType;
import gov.nih.nci.cagrid.data.faults.QueryProcessingExceptionType;
import gov.nih.nci.cagrid.testing.system.deployment.ServiceContainer;
import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.util.Iterator;

import org.apache.axis.message.MessageElement;
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
import org.cagrid.cql2.results.CQLObjectResult;
import org.cagrid.cql2.results.CQLQueryResults;
import org.projectmobius.bookstore.Book;

/** 
 *  InvokeCql2DataServiceStep
 * 	Step to invoke the deployed CQL 2 supporting data service
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>  * 
 * @created Nov 8, 2006 
 * @version $Id: InvokeDataServiceStep.java,v 1.1 2008-05-16 19:25:25 dervin Exp $ 
 */
public class InvokeCql2DataServiceStep extends Step {
	
    private ServiceContainer container;
    private String serviceName;
    
	public InvokeCql2DataServiceStep(ServiceContainer container, String serviceName) {
		this.container = container;
        this.serviceName = serviceName;
	}
	

	public void runStep() throws Throwable {
		// create the data service client
		DataServiceClient client = new DataServiceClient(getServiceUrl());
		// run a query
		CQLQueryResults results = queryForBooks(client);
		// verify the query has object results
		checkForObjectResults(results);
		// iterate the results
		iterateBookResults(results);
		// run a query for an invalid class
		queryForInvalidClass(client);
		// run an invalid syntax query
		submitMalformedQuery(client);
	}
	
	
	private CQLQueryResults queryForBooks(DataServiceClient client) throws Exception {
		CQLQuery query = new CQLQuery();
		CQLTargetObject target = new CQLTargetObject();
		target.setClassName(Book.class.getName());
		query.setCQLTargetObject(target);
		CQLQueryResults results = null;
		try {
			results = client.executeQuery(query);
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
		return results;
	}
	
	
	private void checkForObjectResults(CQLQueryResults results) throws Exception {
		CQLObjectResult[] objResults = results.getObjectResult();
		if (objResults == null || objResults.length == 0) {
			fail("No object results returned");
		}
		for (int i = 0; i < objResults.length; i++) {
			MessageElement[] elements = objResults[i].get_any();
			if (elements == null) {
				fail("Object result returned with null object contents");
			}
			if (elements.length != 1) {
				fail("Object result returned with number of object contents != 1 (" 
					+ elements.length + " found!)");
			}
		}
	}
	
	
	private void iterateBookResults(CQLQueryResults results) throws Exception {
		Iterator<?> iter = new CQL2QueryResultsIterator(results);
		while (iter.hasNext()) {
			java.lang.Object obj = iter.next();
			if (!(obj instanceof Book)) {
				fail("Iterator returned other than book (" + obj.getClass().getName() + ")");
			}
		}
	}
	
	
	private void queryForInvalidClass(DataServiceClient client) throws Exception {
		CQLQuery query = new CQLQuery();
		CQLTargetObject target = new CQLTargetObject();
		target.setClassName("non.existant.class");
		query.setCQLTargetObject(target);
		
		try {
			client.executeQuery(query);
			fail("Exception QueryProcessingExceptionType should have been thrown");
		} catch (QueryProcessingExceptionType ex) {
			// expected
		}
	}
	
	
	private void submitMalformedQuery(DataServiceClient client) throws Exception {
		CQLQuery query = new CQLQuery();
		CQLTargetObject target = new CQLTargetObject();
		target.setClassName(Book.class.getName());
		CQLAttribute attrib1 = new CQLAttribute();
		attrib1.setName("name");
		attrib1.setBinaryPredicate(BinaryPredicate.LIKE);
		AttributeValue nameValue = new AttributeValue();
		nameValue.setStringValue("E%");
		target.setCQLAttribute(attrib1);
		CQLGroup group = new CQLGroup();
		group.setLogicalOperation(GroupLogicalOperator.AND);
		CQLAttribute groupAttr1 = new CQLAttribute();
		groupAttr1.setName("author");
		groupAttr1.setUnaryPredicate(UnaryPredicate.IS_NOT_NULL);
		CQLAttribute groupAttr2 = new CQLAttribute();
		groupAttr2.setName("ISBN");
		groupAttr2.setUnaryPredicate(UnaryPredicate.IS_NULL);
		group.setCQLAttribute(new CQLAttribute[] {
		    groupAttr1, groupAttr2});
		target.setCQLGroup(group);
		query.setCQLTargetObject(target);
		try {
			client.executeQuery(query);
			fail("Exception MalformedQueryExceptionType should have been thrown");
		} catch (MalformedQueryExceptionType ex) {
			// expected
		}
	}
	
	
	private String getServiceUrl() throws Exception {
		URI baseUri = container.getContainerBaseURI();
        String url = baseUri.toString() + "cagrid/" + serviceName;
        return url;
	}
}
