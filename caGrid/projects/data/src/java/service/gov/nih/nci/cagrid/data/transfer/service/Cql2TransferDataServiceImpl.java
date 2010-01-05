package gov.nih.nci.cagrid.data.transfer.service;

import gov.nih.nci.cagrid.common.ByteQueue;
import gov.nih.nci.cagrid.common.DiskByteBuffer;
import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.data.CqlSchemaConstants;
import gov.nih.nci.cagrid.data.MalformedQueryException;
import gov.nih.nci.cagrid.data.QueryProcessingException;
import gov.nih.nci.cagrid.data.faults.MalformedQueryExceptionType;
import gov.nih.nci.cagrid.data.faults.QueryProcessingExceptionType;
import gov.nih.nci.cagrid.data.service.BaseDataServiceImpl;
import gov.nih.nci.cagrid.data.service.DataServiceInitializationException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.rmi.RemoteException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.apache.axis.MessageContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.cql2.CQLQuery;
import org.cagrid.cql2.results.CQLQueryResults;
import org.cagrid.transfer.context.service.helper.TransferServiceHelper;
import org.cagrid.transfer.context.stubs.types.TransferServiceContextReference;
import org.cagrid.transfer.descriptor.DataDescriptor;
import org.globus.wsrf.container.ServiceManager.HelperAxisEngine;

/** 
 * This is the server side implementation of the CQL 2 Data Service with Transfer query method
 * 
 * @created by Introduce Toolkit version 1.0
 * 
 */
public class Cql2TransferDataServiceImpl extends BaseDataServiceImpl {

    private static Log LOG = LogFactory.getLog(Cql2TransferDataServiceImpl.class);    
	
	public Cql2TransferDataServiceImpl() throws DataServiceInitializationException {
		super();
	}
    
	
	public TransferServiceContextReference executeTransferQuery(CQLQuery cqlQuery) throws RemoteException, 
		gov.nih.nci.cagrid.data.faults.MalformedQueryExceptionType, 
		gov.nih.nci.cagrid.data.faults.QueryProcessingExceptionType {
        
        TransferServiceContextReference transferReference = null;
        try {
            transferReference = performQuery(cqlQuery);
        } catch (MalformedQueryException ex) {
            throw getTypedException(ex, new MalformedQueryExceptionType());
        } catch (QueryProcessingException ex) {
            throw getTypedException(ex, new QueryProcessingExceptionType());
        }
        return transferReference;
    }
    
    
    private TransferServiceContextReference performQuery(final CQLQuery query)
        throws QueryProcessingException, MalformedQueryException {
        // start up a ByteQueue which will be used to push data in and out of
        // TODO: configurable disk byte buffer dir via system property
        final ByteQueue byteQueue = new ByteQueue(new DiskByteBuffer());
        
        final MessageContext threadMessageContext = MessageContext.getCurrentContext();
        // perform the query and serialize results in a thread so we can return quickly
        Callable<Object> queryTask = new Callable<Object>() {
            public Object call() throws QueryProcessingException, MalformedQueryException {
                HelperAxisEngine.setCurrentMessageContext(threadMessageContext);
                LOG.debug("CQL 2 query processing started");
                CQLQueryResults results = processCql2Query(query);
                LOG.debug("CQL 2 query processing complete.");
                OutputStream byteOutput = byteQueue.getByteOutputStream();
                OutputStreamWriter writer = new OutputStreamWriter(byteOutput);
                try {
                    LOG.debug("Serializing CQL 2 results to byte queue for transfer");
                    InputStream serverConfigWsdd = getServerConfigWsddStream();
                    Utils.serializeObject(results, 
                        CqlSchemaConstants.CQL2_RESULTS_QNAME, 
                        writer, serverConfigWsdd);
                    serverConfigWsdd.close();
                } catch (Exception ex) {
                    String error = "Error serializing CQL 2 query results to byte queue: " 
                        + ex.getMessage();
                    LOG.error(error, ex);
                    throw new QueryProcessingException(error, ex);
                } finally {
                    try {
                        writer.flush();
                        writer.close();
                        byteOutput.flush();
                        byteOutput.close();
                        LOG.debug("CQL 2 results serialized into byte queue");
                    } catch (IOException ex) {
                        String error = "Unable to flush and close serialization output stream: " 
                            + ex.getMessage();
                        LOG.error(error, ex);
                        throw new QueryProcessingException(error, ex);
                    }
                }
                return null;
            }
        };
        
        // actually execute the query and serialize task
        try {
            LOG.debug("Starting query execution task");
            ThreadFactory daemonThreadFactory = new ThreadFactory() {
                public Thread newThread(Runnable r) {
                    Thread th = Executors.defaultThreadFactory().newThread(r);
                    th.setDaemon(true);
                    return th;
                }
            };
            Executors.newSingleThreadExecutor(daemonThreadFactory).submit(queryTask).get();
        } catch (ExecutionException ex) {
            Throwable cause = ex.getCause();
            if (cause != null) {
                if (cause instanceof QueryProcessingException) {
                    throw (QueryProcessingException) cause;
                } else if (cause instanceof MalformedQueryException) {
                    throw (MalformedQueryException) cause;
                }
            }
            throw new QueryProcessingException("Error executing query: " + ex.getMessage(), ex);
        } catch (InterruptedException ex) {
            throw new QueryProcessingException("Error executing query: " + ex.getMessage(), ex);
        }
        
        // set up the transfer context
        // create a data descriptor for the results
        DataDescriptor descriptor = new DataDescriptor();
        descriptor.setName(CqlSchemaConstants.CQL2_RESULTS_QNAME.toString());

        // create the reference using the transfer service helper
        TransferServiceContextReference transferReference = null;
        try {
            LOG.debug("Creating transfer context");
            transferReference = TransferServiceHelper.createTransferContext(
                byteQueue.getByteInputStream(), descriptor);
        } catch (RemoteException ex) {
            String error = "Unable to create transfer contex: " + ex.getMessage();
            throw new QueryProcessingException(error, ex);
        }
        
        return transferReference;
    }
}