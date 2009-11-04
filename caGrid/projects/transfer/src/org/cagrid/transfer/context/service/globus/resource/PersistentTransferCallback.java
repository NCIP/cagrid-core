package org.cagrid.transfer.context.service.globus.resource;

import org.cagrid.transfer.context.service.helper.DataStagedCallback;
import org.cagrid.transfer.context.stubs.types.TransferServiceContextReference;

public interface PersistentTransferCallback {
	
	public DataStagedCallback getCallback(TransferServiceContextReference ref) throws Exception;

}
