package gov.nih.nci.cagrid.introduce.upgrade;

import gov.nih.nci.cagrid.introduce.beans.extension.ExtensionDescription;
import gov.nih.nci.cagrid.introduce.beans.extension.ExtensionType;
import gov.nih.nci.cagrid.introduce.beans.extension.UpgradeDescriptionType;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;
import gov.nih.nci.cagrid.introduce.extension.ExtensionsLoader;
import gov.nih.nci.cagrid.introduce.upgrade.common.ExtensionUpgraderI;
import gov.nih.nci.cagrid.introduce.upgrade.common.IntroduceUpgradeStatus;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;


public class ExtensionsUpgradeManager {
    private ServiceInformation serviceInformation;
    private String pathToService;


    public ExtensionsUpgradeManager(ServiceInformation serviceInformation, String pathToService) {
        this.serviceInformation = serviceInformation;
        this.pathToService = pathToService;
    }


    public boolean needsUpgrading() {
        ExtensionType[] extensions = serviceInformation.getServiceDescriptor().getExtensions().getExtension();
        if (extensions != null) {
            for (int extensionI = 0; extensionI < extensions.length; extensionI++) {
                ExtensionType extension = extensions[extensionI];
                String serviceExtensionVersion = extension.getVersion();
                ExtensionDescription extDescription = ExtensionsLoader.getInstance().getExtension(extension.getName());
                if ((extDescription != null) && (extDescription.getVersion() != null)) {
                    if ((serviceExtensionVersion == null)
                        || !extDescription.getVersion().equals(serviceExtensionVersion)) {
                        return true;
                    }

                }
            }
            return false;
        }
        return false;
    }


    public void upgrade(IntroduceUpgradeStatus status) throws Exception {
        List error = new ArrayList();

        ExtensionType[] extensions = serviceInformation.getServiceDescriptor().getExtensions().getExtension();
        for (int extensionI = 0; extensionI < extensions.length; extensionI++) {
            ExtensionType extension = extensions[extensionI];
            String serviceExtensionVersion = extension.getVersion();
            ExtensionDescription extDescription = ExtensionsLoader.getInstance().getExtension(extension.getName());
            if ((extDescription != null) && (extDescription.getVersion() != null)) {
                List upgrades = new ArrayList();
                if (((serviceExtensionVersion == null) && (extDescription.getVersion() != null))
                    || !extDescription.getVersion().equals(serviceExtensionVersion)) {
                    // service needs to be upgraded
                    // put together a list of upgrades to run
                    UpgradeDescriptionType[] extensionUpgrades = null;
                    if ((extDescription.getUpgradesDescription() != null)
                        && (extDescription.getUpgradesDescription().getUpgradeDescription() != null)) {
                        extensionUpgrades = extDescription.getUpgradesDescription().getUpgradeDescription();

                        String currentVersion = serviceExtensionVersion;
                        while (((currentVersion == null) || !currentVersion.equals(extDescription.getVersion()))) {
                            boolean found = false;
                            int i = 0;
                            for (i = 0; i < extensionUpgrades.length; i++) {
                                if ((extensionUpgrades[i].getFromVersion() == null) && (currentVersion == null)) {
                                    found = true;
                                    break;
                                } else if (extensionUpgrades[i].getFromVersion() != null
                                    && extensionUpgrades[i].getFromVersion().equals(currentVersion)) {
                                    found = true;
                                    break;
                                }

                            }
                            if (found) {
                                upgrades.add(extensionUpgrades[i]);
                                currentVersion = extensionUpgrades[i].getToVersion();
                            } else {
                                error
                                    .add(extension.getName()
                                        + " extension used on service is older than currently installed and does not appear to have correct upgrade.");
                                break;
                            }

                        }

                    } else {
                        error
                            .add(extension.getName()
                                + " extension used on service is older than currently installed and does not appear to have any upgrades.");
                    }

                }

                // run the upgraders that we put together in order
                for (int i = 0; i < upgrades.size(); i++) {
                    UpgradeDescriptionType upgrade = (UpgradeDescriptionType) upgrades.get(i);
                    Class clazz = Class.forName(upgrade.getUpgradeClass());
                    Constructor con = clazz.getConstructor(new Class[]{ExtensionType.class, ServiceInformation.class,
                            String.class, String.class, String.class});
                    ExtensionUpgraderI upgrader = (ExtensionUpgraderI) con.newInstance(new Object[]{extension,
                            serviceInformation, pathToService, upgrade.getFromVersion(), upgrade.getToVersion()});
                    upgrader.execute();
                    status.addExtensionUpgradeStatus(upgrader.getStatus());
                }

            }
        }

        if (error.size() > 0) {
            String errorString = "";
            for (int errorI = 0; errorI < error.size(); errorI++) {
                errorString += (String) error.get(errorI) + "\n";
            }
            throw new Exception(errorString);
        }

    }
}
