package org.cagrid.transfer.context.service.helper;

import org.cagrid.transfer.context.service.globus.resource.TransferServiceContextResource;

/**
 * In the case where the transfer service is being used to receive data the service
 * might want to be called back when the data is available so that it can be
 * processed.  Implementing this callback and sending the instance of this class
 * into the createDataTransfer method of the TransferServiceHelper will
 * cause this callback to be invoked when the data staging is completed and the
 * data is available to be processed.
 * 
 * @author hastings
 *
 */
public interface DataStagedCallback {
    
    /**
     * Method will be called once the data is fully received from the client
     * and the set status operation is called and status is set to 
     * Staged.
     * 
     * @param resource
     */
    public void dataStaged(TransferServiceContextResource resource);

}
