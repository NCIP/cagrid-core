package org.cagrid.transfer.context.service.globus.resource;

import org.cagrid.transfer.context.service.helper.DataStagedCallback;
import org.cagrid.transfer.context.stubs.types.TransferServiceContextReference;

/**
 * PersistentTransferCallback should be implemented by an Resource using the TransferServiceHelper
 * to create upload transfers with callbacks.  This interface will enable the TransferServiceContextResource 
 * to call back into your resource to get a handle to the DataStorageCallback if the TransferServiceContextResource
 * gets garbage collected or the container restarts.  Implemntation of this interface on the Resource of your service
 * which is using Transfers for upload is required to avoid bug CAGRID-100.
 */
public interface PersistentTransferCallback {
	
	public DataStagedCallback getCallback(String transferContextID) throws Exception;

}
