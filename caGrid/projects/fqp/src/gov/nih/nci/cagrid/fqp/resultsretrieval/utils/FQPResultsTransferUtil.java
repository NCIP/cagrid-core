package gov.nih.nci.cagrid.fqp.resultsretrieval.utils;

import gov.nih.nci.cagrid.common.ByteQueue;
import gov.nih.nci.cagrid.common.DiskByteBuffer;
import gov.nih.nci.cagrid.common.FaultHelper;
import gov.nih.nci.cagrid.fqp.processor.exceptions.FederatedQueryProcessingException;
import gov.nih.nci.cagrid.fqp.results.stubs.types.InternalErrorFault;

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
import org.cagrid.cql.utilities.DCQL2Constants;
import org.cagrid.cql.utilities.DCQL2SerializationUtil;
import org.cagrid.data.dcql.results.DCQLQueryResultsCollection;
import org.cagrid.transfer.context.service.helper.TransferServiceHelper;
import org.cagrid.transfer.context.stubs.types.TransferServiceContextReference;
import org.cagrid.transfer.descriptor.DataDescriptor;
import org.globus.wsrf.container.ServiceManager.HelperAxisEngine;

/**
 * FQPResultsEnumerationUtil
 * Sets up a WS-Enumeration retrieval of CQL 2 query results.
 * 
 * @author ervin
 */
public class FQPResultsTransferUtil {
    
    private static Log LOG = LogFactory.getLog(FQPResultsTransferUtil.class);

    private FQPResultsTransferUtil() {
        
    }
    
    
    public static TransferServiceContextReference setUpTransfer(final DCQLQueryResultsCollection results) 
        throws FederatedQueryProcessingException, InternalErrorFault {
        
        // set up a byte queue to receive data from the serialization of CQL query results
        ByteQueue byteQueue = new ByteQueue(new DiskByteBuffer());
        
        // grab the reader / writers from the byte queue
        final OutputStream byteOutput = byteQueue.getByteOutputStream();
        final InputStream byteInput = byteQueue.getByteInputStream();
        final OutputStreamWriter byteWriter = new OutputStreamWriter(byteOutput);

        final MessageContext threadMessageContext = MessageContext.getCurrentContext();
        // perform the query and serialize results in a thread so we can return quickly
        // and let results serialize into the writer as they are processed by
        // the DCQL 2 serializer util
        Callable<Object> queryTask = new Callable<Object>() {
            public Object call() throws FederatedQueryProcessingException {
                HelperAxisEngine.setCurrentMessageContext(threadMessageContext);
                try {
                    DCQL2SerializationUtil.serializeDcql2QueryResults(results, byteWriter);
                } catch (Exception ex) {
                    String error = "Error serializing DCQL 2 results to byte queue: " 
                        + ex.getMessage();
                    LOG.error(error, ex);
                    throw new FederatedQueryProcessingException(error, ex);
                } finally {
                    try {
                        byteWriter.flush();
                        byteWriter.close();
                        byteOutput.flush();
                        byteOutput.close();
                        LOG.debug("DCQL 2 results serialized into byte queue");
                    } catch (IOException ex) {
                        String error = "Unable to flush and close serialization output stream: " 
                            + ex.getMessage();
                        LOG.error(error, ex);
                        throw new FederatedQueryProcessingException(error, ex);
                    }
                }
                return null;
            }
        };
        
        // start the serialization task
        try {
            LOG.debug("Starting results serialization task");
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
                if (cause instanceof FederatedQueryProcessingException) {
                    throw (FederatedQueryProcessingException) cause;
                } else {
                    throw new FederatedQueryProcessingException(ex.getMessage(), ex);
                }
            }
            throw new FederatedQueryProcessingException("Error serializing query results: " + ex.getMessage(), ex);
        } catch (InterruptedException ex) {
            throw new FederatedQueryProcessingException("Error serializing query results: " + ex.getMessage(), ex);
        }

        // create a data descriptor for the results
        DataDescriptor descriptor = new DataDescriptor();
        descriptor.setName(DCQL2Constants.DCQL2_RESULTS_QNAME.toString());

        TransferServiceContextReference transferReference = null;
        try {
            transferReference = TransferServiceHelper.createTransferContext(byteInput, descriptor);
        } catch (RemoteException ex) {
            FaultHelper helper = new FaultHelper(new InternalErrorFault());
            helper.addDescription("Unable to create transfer contex");
            helper.addDescription(ex.getMessage());
            helper.addFaultCause(ex);
            throw (InternalErrorFault) helper.getFault();
        }

        return transferReference;
    }
}
