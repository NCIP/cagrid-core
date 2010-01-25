package gov.nih.nci.cagrid.data.utilities;

import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.data.faults.MalformedQueryExceptionType;
import gov.nih.nci.cagrid.data.faults.QueryProcessingExceptionType;

import java.rmi.RemoteException;
import java.util.Iterator;

/** 
 *  DataServiceIterator
 *  Interface for data service handles
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>  * 
 * @deprecated As of caGrid 1.4, CQL 2 is the preferred query language.  http://cagrid.org/display/dataservices/CQL+2
 * 
 * @created Nov 8, 2006 
 * @version $Id: DataServiceIterator.java,v 1.1 2006-11-08 19:08:14 dervin Exp $ 
 */
public interface DataServiceIterator {

	public Iterator query(CQLQuery query) 
		throws MalformedQueryExceptionType, QueryProcessingExceptionType, RemoteException;
}
