package gov.nih.nci.cagrid.introduce.upgrade;

import gov.nih.nci.cagrid.introduce.IntroduceConstants;
import gov.nih.nci.cagrid.introduce.codegen.SyncTools;
import gov.nih.nci.cagrid.introduce.common.ResourceManager;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;
import gov.nih.nci.cagrid.introduce.upgrade.common.IntroduceUpgradeStatus;
import gov.nih.nci.cagrid.introduce.upgrade.common.UpgradeStatus;
import gov.nih.nci.cagrid.introduce.upgrade.common.UpgradeUtilities;

import java.io.File;

import org.apache.log4j.Logger;


public class UpgradeManager {

    private IntroduceUpgradeManager iUpgrader;

    private String pathToService;
    private String id;
    private static final Logger logger = Logger.getLogger(UpgradeManager.class);


    public UpgradeManager(String pathToService) {
        this.pathToService = pathToService;
        iUpgrader = new IntroduceUpgradeManager(pathToService);
    }


    public boolean canIntroduceBeUpgraded() {
        try {
            if (iUpgrader.needsUpgrading()
                && iUpgrader.canBeUpgraded(UpgradeUtilities.getCurrentServiceVersion(
                    pathToService + File.separator + IntroduceConstants.INTRODUCE_XML_FILE))) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            logger.error(e);
            return false;
        }

    }
    
    public boolean introduceNeedsUpgraded() {
            return iUpgrader.needsUpgrading();
    }


    public boolean extensionsNeedUpgraded() {
        if (!canIntroduceBeUpgraded()) {
            ServiceInformation info = null;
            try {
                info = new ServiceInformation(new File(pathToService));
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            ExtensionsUpgradeManager eUpgradeManager = 
                new ExtensionsUpgradeManager(info, pathToService);
            if (eUpgradeManager.needsUpgrading()) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }


    private void backup() throws Exception {
        logger.info("Creating backup of service prior to upgrading.");
        id = String.valueOf(System.currentTimeMillis());
        ResourceManager.createArchive(id, getUpgradeServiceName(), pathToService);
    }


    public void recover() throws Exception {
        logger.info("Recovering backup of service after failed upgrade.");
        ResourceManager.restoreSpecific(id, getUpgradeServiceName(), pathToService);
    }


    private String getUpgradeServiceName() throws Exception {
        String upgradeServiceName = UpgradeUtilities.getServiceName(
            pathToService + File.separator + IntroduceConstants.INTRODUCE_XML_FILE)
                + "UPGRADE";
        return upgradeServiceName;
    }


    public UpgradeStatus upgrade() throws Exception {
        UpgradeStatus status = new UpgradeStatus();
        backup();

        if (canIntroduceBeUpgraded()) {
            upgradeIntroduce(status);
            try {
                SyncTools sync = new SyncTools(new File(this.pathToService));
                sync.sync();
            } catch (Exception e) {
                status.addIssue(
                    "Re-Sync Failed",
                    e.getMessage() +  "\n" + "This could be due to modifications you may have made to Introduce\n"
                    + "managed files such as the build files, source files or wsdl files.\n" +
                    "Once the build is fixed then a sync must be done to " +
                    "complete the upgrade.  To complete the upgrade simply " +
                    "open introduce and open this service for modification " +
                    "and then click save.");
                e.printStackTrace();
            }
            return status;
        } else if (extensionsNeedUpgraded()) {
            status.addIntroduceUpgradeStatus(upgradeExtensionsOnly());
            try {
                SyncTools sync = new SyncTools(new File(this.pathToService));
                sync.sync();
            } catch (Exception e) {
                status.addIssue(
                    "Re-Sync Failed",
                    e.getMessage() +  "\n" + "This could be due to modifications you may have made to Introduce\n"
                    + "managed files such as the build files, source files or wsdl files.\n" +
                    "Once the build is fixed then a sync must be done to " +
                    "complete the upgrade. To complete the upgrade simply open introduce and open " +
                    "this service for modification and then click save.");
                e.printStackTrace();
            }
            return status;
        } else {
            return status;
        }
    }


    private void upgradeIntroduce(UpgradeStatus status) throws Exception {
        if (iUpgrader.needsUpgrading()) {
            try {
                iUpgrader.upgrade(status);
            } catch (Exception e) {
                e.printStackTrace();
                throw new Exception(
                    "Service upgrader failed: " + e.getMessage(), e);
            }
        }
    }


    private IntroduceUpgradeStatus upgradeExtensionsOnly() throws Exception {
        if (!iUpgrader.needsUpgrading()) {
            try {
                ServiceInformation info = null;
                try {
                    info = new ServiceInformation(new File(pathToService));
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                IntroduceUpgradeStatus status = new IntroduceUpgradeStatus();
                ExtensionsUpgradeManager eUpgradeManager = new ExtensionsUpgradeManager(info, pathToService);
                eUpgradeManager.upgrade(status);
                return status;
            } catch (Exception e) {
                e.printStackTrace();
                throw new Exception(
                    "Extensions upgrader failed: " + e.getMessage(), e);
            }
        }
        return null;
    }
}
