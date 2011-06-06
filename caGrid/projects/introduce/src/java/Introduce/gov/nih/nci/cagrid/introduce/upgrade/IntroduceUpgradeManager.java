package gov.nih.nci.cagrid.introduce.upgrade;

import gov.nih.nci.cagrid.introduce.IntroduceConstants;
import gov.nih.nci.cagrid.introduce.common.IntroducePropertiesManager;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;
import gov.nih.nci.cagrid.introduce.extension.utils.ExtensionUtilities;
import gov.nih.nci.cagrid.introduce.upgrade.common.IntroduceUpgradeStatus;
import gov.nih.nci.cagrid.introduce.upgrade.common.IntroduceUpgraderI;
import gov.nih.nci.cagrid.introduce.upgrade.common.ModelUpgraderI;
import gov.nih.nci.cagrid.introduce.upgrade.common.UpgradeStatus;
import gov.nih.nci.cagrid.introduce.upgrade.common.UpgradeUtilities;

import java.io.File;
import java.lang.reflect.Constructor;

import org.apache.log4j.Logger;

public class IntroduceUpgradeManager {

    private static final Logger logger = Logger.getLogger(IntroduceUpgradeManager.class);

    private ExtensionsUpgradeManager eUpgrader;
    private String pathToService;

    public IntroduceUpgradeManager(String pathToService) {
        this.pathToService = pathToService;
    }

    private static String getIntroduceUpgradeClass(String oldVersion) {
        if (oldVersion.equals("1.2")) {
            return "gov.nih.nci.cagrid.introduce.upgrade.introduce.Introduce_1_2__1_4_1_Upgrader";
        } else if (oldVersion.equals("1.3")) {
            return "gov.nih.nci.cagrid.introduce.upgrade.introduce.Introduce_1_3__1_4_1_Upgrader";
        } else if (oldVersion.equals("1.4")) {
            return "gov.nih.nci.cagrid.introduce.upgrade.introduce.Introduce_1_4__1_4_1_Upgrader";
        }
        return null;
    }

    private static String getModelUpgradeClass(String oldVersion) {
        if (oldVersion.equals("1.2")) {
            return "gov.nih.nci.cagrid.introduce.upgrade.model.Model_1_2__1_4_Upgrader";
        } else if (oldVersion.equals("1.3")) {
            return "gov.nih.nci.cagrid.introduce.upgrade.model.Model_1_3__1_4_Upgrader";
        }
        return null;
    }

    /**
     * Return true if the if the service was created by a different or unknown
     * version of introduce.
     */
    protected boolean needsUpgrading() {
        try {
            String serviceVersion = getServiceVersion();
            if ((serviceVersion == null) || !serviceVersion.equals(IntroducePropertiesManager.getIntroduceVersion())) {
                return true;
            }
            return false;
        } catch (Exception e) {
            logger.error(e);
            return false;
        }
    }

    /**
     * Return the version of introduce that created the service or null if
     * unknown.
     * 
     * @throws Exception
     *             If there is a problem.
     */
    protected String getServiceVersion() throws Exception {
        String serviceVersion = UpgradeUtilities.getCurrentServiceVersion(pathToService + File.separator
                + IntroduceConstants.INTRODUCE_XML_FILE);
        return serviceVersion;
    }

    /**
     * Return true if there is an upgrader class for the specified version of
     * Introduce.
     * 
     * @param version
     *            an Introduce version string.
     */
    protected boolean canBeUpgraded(String version) {
        if (getIntroduceUpgradeClass(version) != null) {
            return true;
        } else {
            return false;
        }
    }

    protected void upgrade(UpgradeStatus status) throws Exception {

        String serviceVersion = getServiceVersion();

        if (canBeUpgraded(serviceVersion)) {

            // upgrade the introduce service
            String version = IntroducePropertiesManager.getIntroduceVersion();
            if (version != null) {

                String vers = getServiceVersion();

                String className = getModelUpgradeClass(vers);
                if (className == null) {
                    logger.warn("The model" + " is upgradeable however no upgrade class from the version " + vers + " could be found.");
                    return;
                }

                IntroduceUpgradeStatus iStatus = new IntroduceUpgradeStatus();
                status.addIntroduceUpgradeStatus(iStatus);

                // upgrade the introduce service
                Class<?> clazz = Class.forName(className);
                Constructor<?> con = clazz.getConstructor(new Class[] { IntroduceUpgradeStatus.class, String.class });
                ModelUpgraderI modelupgrader = (ModelUpgraderI) con.newInstance(new Object[] { iStatus, pathToService });
                modelupgrader.execute();

                ServiceInformation serviceInfo = new ServiceInformation(new File(pathToService));

                className = getIntroduceUpgradeClass(vers);
                if (className == null) {
                    logger.warn("The service" + " is upgradeable however no upgrade class from the version " + vers + " could be found.");
                    return;
                }

                // upgrade the introduce service
                clazz = Class.forName(className);
                con = clazz.getConstructor(new Class[] { IntroduceUpgradeStatus.class, ServiceInformation.class, String.class });
                IntroduceUpgraderI upgrader = (IntroduceUpgraderI) con.newInstance(new Object[] { iStatus, serviceInfo, pathToService });
                upgrader.execute();

                eUpgrader = new ExtensionsUpgradeManager(serviceInfo, pathToService);

                if (eUpgrader.needsUpgrading()) {
                    try {
                        eUpgrader.upgrade(iStatus);
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new Exception("Extensions Upgrader Failed: ", e);
                    }
                }

                serviceInfo.persistInformation();

                // resync the eclipse classpath doc with what is in the lib
                // directory
                try {
                    ExtensionUtilities.resyncWithLibDir(new File(pathToService + File.separator + ".classpath"));
                } catch (Exception e) {
                    throw new Exception("Unable to resync the eclipse .classpath file:", e);
                }

            } else {
                throw new Exception("ERROR: The service" + " is not upgradable because it's version cannot be determined or is corrupt");
            }

        } else {
            throw new Exception("ERROR: The service" + " needs to be upgraded but no upgrader can be found");
        }
    }
}
