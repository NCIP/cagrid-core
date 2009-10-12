package gov.nih.nci.cagrid.data.transfer.service;

import gov.nih.nci.cagrid.common.ByteQueue;
import gov.nih.nci.cagrid.common.DiskByteBuffer;
import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.cqlresultset.CQLQueryResults;
import gov.nih.nci.cagrid.data.DataServiceConstants;
import gov.nih.nci.cagrid.data.MalformedQueryException;
import gov.nih.nci.cagrid.data.QueryProcessingException;
import gov.nih.nci.cagrid.data.cql.CQLQueryProcessor;
import gov.nih.nci.cagrid.data.faults.MalformedQueryExceptionType;
import gov.nih.nci.cagrid.data.faults.QueryProcessingExceptionType;
import gov.nih.nci.cagrid.data.service.BaseCQL1DataServiceImpl;
import gov.nih.nci.cagrid.data.service.DataServiceInitializationException;
import gov.nih.nci.cagrid.data.service.ServiceConfigUtil;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.rmi.RemoteException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

import org.apache.axis.MessageContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.transfer.context.service.helper.TransferServiceHelper;
import org.cagrid.transfer.context.stubs.types.TransferServiceContextReference;
import org.cagrid.transfer.descriptor.DataDescriptor;
import org.globus.wsrf.container.ServiceManager.HelperAxisEngine;

/** 
 * This is the server side implementation of the Data Service with Transfer query method
 * 
 * @created by Introduce Toolkit version 1.0
 * 
 */
public class TransferDataServiceImpl extends BaseCQL1DataServiceImpl {

    private static Log LOG = LogFactory.getLog(TransferDataServiceImpl.class);
    
    private byte[] serverConfigWsddBytes = null;
    
	
	public TransferDataServiceImpl() throws DataServiceInitializationException {
		super();
	}
    
	
	public TransferServiceContextReference transferQuery(gov.nih.nci.cagrid.cqlquery.CQLQuery cqlQuery) throws RemoteException, 
		gov.nih.nci.cagrid.data.faults.MalformedQueryExceptionType, 
		gov.nih.nci.cagrid.data.faults.QueryProcessingExceptionType {
        fireAuditQueryBegins(cqlQuery);
        
        try {
            preProcess(cqlQuery);
        } catch (MalformedQueryException ex) {
            throw (MalformedQueryExceptionType) getTypedException(ex, new MalformedQueryExceptionType());
        } catch (QueryProcessingException ex) {
            throw (QueryProcessingExceptionType) getTypedException(ex, new QueryProcessingExceptionType());
        }
		
		CQLQueryProcessor processor = null;
        try {
            processor = getCqlQueryProcessorInstance();
        } catch (QueryProcessingException ex) {
            throw (QueryProcessingExceptionType) getTypedException(ex, new QueryProcessingExceptionType());
        }
        
        TransferServiceContextReference transferReference = null;
        try {
            transferReference = performQuery(processor, cqlQuery);
        } catch (MalformedQueryException ex) {
            throw (MalformedQueryExceptionType) getTypedException(ex, new MalformedQueryExceptionType());
        } catch (QueryProcessingException ex) {
            throw (QueryProcessingExceptionType) getTypedException(ex, new QueryProcessingExceptionType());
        }
        return transferReference;
    }
    
    
    private TransferServiceContextReference performQuery(final CQLQueryProcessor processor, final CQLQuery query)
        throws QueryProcessingException, MalformedQueryException {
        // start up a ByteQueue which will be used to push data in and out of
        // TODO: configurable disk byte buffer dir via system property
        final ByteQueue byteQueue = new ByteQueue(new DiskByteBuffer());
        
        final MessageContext threadMessageContext = MessageContext.getCurrentContext();
        // perform the query and serialize results in a thread so we can return quickly
        Callable<Object> queryTask = new Callable<Object>() {
            public Object call() throws QueryProcessingException, MalformedQueryException {
                HelperAxisEngine.setCurrentMessageContext(threadMessageContext);
                LOG.debug("CQL query processing started");
                CQLQueryResults results = processor.processQuery(query);
                fireAuditQueryResults(query, results);
                LOG.debug("CQL query processing complete.");
                OutputStream byteOutput = byteQueue.getByteOutputStream();
                OutputStreamWriter writer = new OutputStreamWriter(byteOutput);
                try {
                    LOG.debug("Serializing CQL results to byte queue for transfer");
                    InputStream serverConfigWsdd = getServerConfigWsdd();
                    Utils.serializeObject(results, 
                        DataServiceConstants.CQL_RESULT_SET_QNAME, 
                        writer, serverConfigWsdd);
                    serverConfigWsdd.close();
                } catch (Exception ex) {
                    String error = "Error serializing CQL query results to byte queue: " 
                        + ex.getMessage();
                    LOG.error(error, ex);
                    throw new QueryProcessingException(error, ex);
                } finally {
                    try {
                        writer.flush();
                        writer.close();
                        byteOutput.flush();
                        byteOutput.close();
                        LOG.debug("CQL results serialized into byte queue");
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
            Executors.newSingleThreadExecutor().submit(queryTask).get();
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
        descriptor.setName(DataServiceConstants.CQL_RESULT_SET_QNAME.toString());

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
    
    
    private InputStream getServerConfigWsdd() throws IOException {
        if (serverConfigWsddBytes == null) {
            LOG.debug("Reading and caching server config wsdd file");
            String serverConfigLocation = null;
            try {
                serverConfigLocation = ServiceConfigUtil.getConfigProperty(
                    DataServiceConstants.SERVER_CONFIG_LOCATION);
            } catch (Exception ex) {
                String err = "Error obtaining server config location: " + ex.getMessage();
                LOG.error(err, ex);
                throw new IOException(err);
            }
            StringBuffer wsdd = Utils.fileToStringBuffer(new File(serverConfigLocation));
            serverConfigWsddBytes = wsdd.toString().getBytes();
        }
        return new ByteArrayInputStream(serverConfigWsddBytes);
    }
}