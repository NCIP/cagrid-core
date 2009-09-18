package gov.nih.nci.cagrid.syncgts.service;

import gov.nih.nci.cagrid.syncgts.bean.SyncDescription;
import gov.nih.nci.cagrid.syncgts.core.SyncGTS;
import gov.nih.nci.cagrid.syncgts.core.SyncGTSDefault;

import java.io.File;
import java.rmi.RemoteException;

import org.apache.axis.MessageContext;
import org.globus.wsrf.config.ContainerConfig;


/**
 * gov.nih.nci.cagrid.syncgtsI TODO:DOCUMENT ME
 * 
 * @created by Introduce Toolkit version 1.0
 */
public class SyncGTSImpl {
    private static final String SYNC_DESCRIPTION = "syncDescription";
    private SyncGTSConfiguration configuration;


    public SyncGTSImpl() throws RemoteException {
        try {
            this.configuration = SyncGTSConfiguration.getConfiguration();
            String configFileEnd = (String) MessageContext.getCurrentContext().getProperty(SYNC_DESCRIPTION);
            String configFile = ContainerConfig.getBaseDirectory() + File.separator + configFileEnd;
            SyncGTSDefault.setServiceSyncDescriptionLocation(configFile);
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
