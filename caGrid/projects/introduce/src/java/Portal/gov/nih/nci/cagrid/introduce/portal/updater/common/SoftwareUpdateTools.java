package gov.nih.nci.cagrid.introduce.portal.updater.common;

import gov.nih.nci.cagrid.introduce.beans.software.ExtensionType;
import gov.nih.nci.cagrid.introduce.beans.software.IntroduceType;
import gov.nih.nci.cagrid.introduce.extension.ExtensionsLoader;

import java.util.StringTokenizer;

public class SoftwareUpdateTools {

    public static boolean isOlderVersion(String currentVersion, String proposedVersion) {
    	StringTokenizer currentTokes = new StringTokenizer(currentVersion, ".",
    			false);
    	StringTokenizer proposedTokes = new StringTokenizer(proposedVersion,
    			".", false);
    	while (proposedTokes.hasMoreElements()) {
    		if (!currentTokes.hasMoreElements()) {
    			return false;
    		}
    		int proposedPartVersion = Integer
    				.valueOf(proposedTokes.nextToken()).intValue();
    		int currentPartVersion = Integer.valueOf(currentTokes.nextToken())
    				.intValue();
    		if (proposedPartVersion > currentPartVersion) {
    			return false;
    		}
    		if (proposedPartVersion < currentPartVersion) {
    			return true;
    		}
    	}
    	return false;
    }

    public static boolean isCompatibleExtension(IntroduceType introduce, String extensionIntroduceVersions) {
        StringTokenizer strtok = new StringTokenizer(extensionIntroduceVersions, ",", false);
        while (strtok.hasMoreElements()) {
            String extensionIntroduceVersion = strtok.nextToken();
            if (extensionIntroduceVersion.equals(introduce.getVersion())) {
                return true;
            }
        }
    
        return false;
    }

    public static boolean isInstalledOrExtensionNewer(ExtensionType extension) {
        boolean newer = false;
        if (ExtensionsLoader.getInstance().getExtension(extension.getName()) != null) {
            String installedVersion = ExtensionsLoader.getInstance().getExtension(extension.getName()).getVersion();
            if (installedVersion == null && extension.getVersion() != null) {
                newer = true;
            } else if (installedVersion != null && extension.getVersion() != null) {
                if (!SoftwareUpdateTools.isOlderVersion(installedVersion, extension.getVersion()) || SoftwareUpdateTools.isExtensionInstalled(extension)) {
                    newer = true;
                }
            } else if ((installedVersion == null && extension.getVersion() == null)
                || (installedVersion != null && extension.getVersion() != null && installedVersion.equals(extension
                    .getVersion()))) {
                newer = true;
            } else {
                newer = false;
            }
        } else {
            newer = true;
        }
        return newer;
    }

    public static boolean isExtensionInstalled(ExtensionType extension) {
        boolean extensionInstalled = false;
    
        if (ExtensionsLoader.getInstance().getExtension(extension.getName()) != null) {
            if (extension.getVersion() == null) {
                if (ExtensionsLoader.getInstance().getExtension(extension.getName()).getVersion() == null) {
                    extensionInstalled = true;
                }
            } else {
                if (ExtensionsLoader.getInstance().getExtension(extension.getName()).getVersion() != null
                    && ExtensionsLoader.getInstance().getExtension(extension.getName()).getVersion().equals(
                        extension.getVersion())) {
                    extensionInstalled = true;
                }
            }
        }
        return extensionInstalled;
    }

}
