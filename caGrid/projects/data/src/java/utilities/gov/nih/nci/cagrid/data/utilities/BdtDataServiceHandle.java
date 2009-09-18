package gov.nih.nci.cagrid.data.utilities;

import gov.nih.nci.cagrid.bdt.client.BulkDataHandlerClient;
import gov.nih.nci.cagrid.common.FaultHelper;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.data.bdt.common.BDTDataServiceI;
import gov.nih.nci.cagrid.data.faults.MalformedQueryExceptionType;
import gov.nih.nci.cagrid.data.faults.QueryProcessingExceptionType;
import gov.nih.nci.cagrid.enumeration.stubs.response.EnumerationResponseContainer;
import gov.nih.nci.cagrid.wsenum.utils.EnumerationResponseHelper;

import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.Iterator;

import org.apache.axis.types.URI.MalformedURIException;
import org.apache.axis.utils.ClassUtils;
import org.globus.ws.enumeration.ClientEnumIterator;
import org.globus.ws.enumeration.IterationConstraints;

/** 
 *  BdtDataServiceHandle
 *  Simplified 'Handle' class for a BDT Data Service
 * 
 * @author David Ervin
 * 
 * @created May 10, 2007 1:26:11 PM
 * @version $Id: BdtDataServiceHandle.java,v 1.1 2007-05-16 18:47:27 dervin Exp $ 
 */
public class BdtDataServiceHandle implements DataServiceIterator {
    
    private BDTDataServiceI bdtClient = null;
    private IterationConstraints iterConstraints = null;
    
    public BdtDataServiceHandle(BDTDataServiceI bdtClient) {
        this.bdtClient = bdtClient;
        iterConstraints = new IterationConstraints();
    }
    
    
    public BdtDataServiceHandle(BDTDataServiceI bdtClient, IterationConstraints iterConstraints) {
        this.bdtClient = bdtClient;
        this.iterConstraints = iterConstraints;
    }
    

    public Iterator query(CQLQuery query) throws MalformedQueryExceptionType, 
        QueryProcessingExceptionType, RemoteException {
        Class targetClass = null;
        try {
            targetClass = Class.forName(query.getTarget().getName());
        } catch (ClassNotFoundException ex) {
            FaultHelper helper = new FaultHelper(new QueryProcessingExceptionType());
            helper.addFaultCause(ex);
            throw (QueryProcessingExceptionType) helper.getFault();
        }
        
        BulkDataHandlerClient bdtHandler = null;
        try {
            bdtHandler = bdtClient.bdtQuery(query);
        } catch (MalformedURIException ex) {
            FaultHelper helper = new FaultHelper(new QueryProcessingExceptionType());
            helper.addDescription(ex.getMessage());
            helper.addFaultCause(ex);
            throw helper.getFault();
        }
        EnumerationResponseContainer responseContainer = bdtHandler.createEnumeration();
        
        ClientEnumIterator iterator = null;
        // see if there's a wsdd-config to be had
        InputStream wsddStream = 
            ClassUtils.getResourceAsStream(getClass(), "client-config.wsdd");
        if (wsddStream != null) {
            iterator = EnumerationResponseHelper.createClientIterator(responseContainer, wsddStream);
        } else {
            iterator = EnumerationResponseHelper.createClientIterator(responseContainer);
        }
        
        iterator.setIterationConstraints(iterConstraints);
        iterator.setItemType(targetClass);
        return iterator;
    }

}
