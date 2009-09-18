package gov.nih.nci.cagrid.data.utilities;

import gov.nih.nci.cagrid.common.FaultHelper;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.data.enumeration.common.EnumerationDataServiceI;
import gov.nih.nci.cagrid.data.faults.MalformedQueryExceptionType;
import gov.nih.nci.cagrid.data.faults.QueryProcessingExceptionType;
import gov.nih.nci.cagrid.enumeration.stubs.response.EnumerationResponseContainer;
import gov.nih.nci.cagrid.wsenum.utils.EnumerationResponseHelper;

import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.Iterator;

import org.apache.axis.utils.ClassUtils;
import org.globus.ws.enumeration.ClientEnumIterator;
import org.globus.ws.enumeration.IterationConstraints;

/** 
 *  EnumDataServiceHandle
 *  Data Service with Enumeration 'Handle' class to wrap complexity
 *  of WS-Enumeration interface with a reasonable API
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>  * 
 * @created Nov 8, 2006 
 * @version $Id: EnumDataServiceHandle.java,v 1.8 2007-05-16 18:47:12 dervin Exp $ 
 */
public class EnumDataServiceHandle implements DataServiceIterator {

	private EnumerationDataServiceI queryService;
	private IterationConstraints constraints;
	
    /**
     * Creates a simplified enumeration data service handle using
     * the default iteration constraints for WS-Enumeration
     * 
     * @param enumQueryService
     */
	public EnumDataServiceHandle(EnumerationDataServiceI enumQueryService) {
		this(enumQueryService, new IterationConstraints());
	}
	
	
    /**
     * Creates a simplified enumeration data service handle
     * using the supplied iteration constraints for
     * WS-Enumeration
     * 
     * @param enumQueryService
     * @param iterationConstraints
     */
	public EnumDataServiceHandle(EnumerationDataServiceI enumQueryService, IterationConstraints iterationConstraints) {
		this.queryService = enumQueryService;
		this.constraints = iterationConstraints;
	}
	
	
    /**
     * Performs a CQL query against the data source and returns an Iterator
     * implementation, which hides the complexity of initializing a
     * WS-Enumeration client session
     */
	public Iterator query(CQLQuery query) 
		throws MalformedQueryExceptionType, QueryProcessingExceptionType, RemoteException {
		Class targetClass = null;
		try {
			targetClass = Class.forName(query.getTarget().getName());
		} catch (ClassNotFoundException ex) {
			FaultHelper helper = new FaultHelper(new QueryProcessingExceptionType());
			helper.addFaultCause(ex);
			throw (QueryProcessingExceptionType) helper.getFault();
		}
		EnumerationResponseContainer responseContainer = queryService.enumerationQuery(query);
        
        ClientEnumIterator iterator = null;
        // see if there's a wsdd-config to be had
        InputStream wsddStream = 
            ClassUtils.getResourceAsStream(getClass(), "client-config.wsdd");
        if (wsddStream != null) {
            iterator = EnumerationResponseHelper.createClientIterator(responseContainer, wsddStream);
        } else {
            iterator = EnumerationResponseHelper.createClientIterator(responseContainer);
        }
        
		iterator.setIterationConstraints(constraints);
        iterator.setItemType(targetClass);
		return iterator;
	}
}
