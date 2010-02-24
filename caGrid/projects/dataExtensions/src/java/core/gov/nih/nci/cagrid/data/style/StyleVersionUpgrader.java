package gov.nih.nci.cagrid.data.style;

import gov.nih.nci.cagrid.introduce.beans.extension.ExtensionTypeExtensionData;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;
import gov.nih.nci.cagrid.introduce.upgrade.common.ExtensionUpgradeStatus;

public interface StyleVersionUpgrader {

    public void upgradeStyle(ServiceInformation serviceInformation, ExtensionTypeExtensionData extensionData,
            ExtensionUpgradeStatus status, String serviceFromVersion, String serviceToVersion) throws Exception;
}
