package gov.nih.nci.cagrid.syncgts.service;

import gov.nih.nci.cagrid.syncgts.bean.SyncDescription;
import gov.nih.nci.cagrid.syncgts.core.SyncGTS;
import gov.nih.nci.cagrid.syncgts.core.SyncGTSDefault;

import java.rmi.RemoteException;


/**
 * gov.nih.nci.cagrid.syncgtsI TODO:DOCUMENT ME
 * 
 * @created by Introduce Toolkit version 1.0
 */
public class SyncGTSImpl {
    private SyncGTSConfiguration configuration;

    public SyncGTSImpl() throws RemoteException {
        try {
            this.configuration = SyncGTSConfiguration.getConfiguration();
            SyncGTSDefault.setServiceSyncDescriptionLocation(SyncGTSConfiguration.getConfiguration().getSyncDescription());
            SyncDescription description = SyncGTSDefault.getSyncDescription();
            try {
                SyncGTS sync = SyncGTS.getInstance();
                if ((this.configuration.getPerformFirstSync() != null)
                    && (this.configuration.getPerformFirstSync().equalsIgnoreCase("true"))) {
                    sync.syncOnce(description);
                }
                sync.syncAndResyncInBackground(description, true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RemoteException("Error Starting SyncGTS Service: " + ex.getMessage());
        }

    }

}
