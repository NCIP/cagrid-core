package org.cagrid.transfer.context.service.helper;

import org.cagrid.transfer.context.stubs.types.TransferServiceContextReference;

public interface PersistentTransferCallback {
	
	public DataStagedCallback getCallback(TransferServiceContextReference ref) throws Exception;

}
