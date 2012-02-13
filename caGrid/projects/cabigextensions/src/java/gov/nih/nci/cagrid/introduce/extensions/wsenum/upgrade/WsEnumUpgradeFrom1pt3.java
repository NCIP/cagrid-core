package gov.nih.nci.cagrid.introduce.extensions.wsenum.upgrade;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.introduce.beans.extension.ExtensionType;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;
import gov.nih.nci.cagrid.introduce.extension.ExtensionsLoader;
import gov.nih.nci.cagrid.introduce.extension.utils.ExtensionUtilities;
import gov.nih.nci.cagrid.introduce.upgrade.one.x.ExtensionUpgraderBase;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * WsEnumUpgradeFrom1pt3
 * Upgrades caGrid ws-enumeration support from 1.3 to current
 * 
 * @author ervin
 * @created Apr 9, 2007 11:21:24 AM
 * @version $Id: multiscaleEclipseCodeTemplates.xml,v 1.1 2007/03/02 14:35:01
 *          dervin Exp $
 */
public class WsEnumUpgradeFrom1pt3 extends ExtensionUpgraderBase {
    private static final String CAGRID_WS_ENUM_JAR_PREFIX = "caGrid-wsEnum-";
    private static final String CAGRID_WS_ENUM_JAR_SUFFIX = "-1.3.jar";
    
    private static Log LOG = LogFactory.getLog(WsEnumUpgradeFrom1pt3.class);

    public WsEnumUpgradeFrom1pt3(ExtensionType extensionType, ServiceInformation serviceInfo, String servicePath,
        String fromVersion, String toVersion) {
        super(WsEnumUpgradeFrom1pt3.class.getSimpleName(),
            extensionType, serviceInfo, servicePath, fromVersion, toVersion);
    }

    
    protected void upgrade() throws Exception {
        upgradeJars();
    }
    
    
    /**
     * Upgrade the jars which are required for metadata
     */
    private void upgradeJars() throws Exception {
        FileFilter enumLibFilter = new FileFilter() {
            public boolean accept(File pathname) {
                String name = pathname.getName();
                return name.endsWith(CAGRID_WS_ENUM_JAR_SUFFIX) && name.startsWith(CAGRID_WS_ENUM_JAR_PREFIX);
            }
        };
        FileFilter newEnumLibFilter = new FileFilter() {
            public boolean accept(File pathname) {
                String name = pathname.getName();
                return name.endsWith(".jar") 
                && name.startsWith("caGrid-wsEnum-");
            }
        };

        // locate the old data service libs in the service
        LOG.debug("Finding old libraries in service lib dir");
        File serviceLibDir = new File(getServicePath() + File.separator + "lib");
        File[] serviceEnumLibs = serviceLibDir.listFiles(enumLibFilter);
        // delete the old libraries
        for (File oldLib : serviceEnumLibs) {
            oldLib.delete();
            String description = "caGrid 1.3 library " + oldLib.getName() + " removed";
            getStatus().addDescriptionLine(description);
            LOG.debug(description);
        }
        
        // copy new libraries in
        LOG.debug("Copying in new libraries");
        File extLibDir = new File(ExtensionsLoader.EXTENSIONS_DIRECTORY + File.separator + "lib");
        File[] enumLibs = extLibDir.listFiles(newEnumLibFilter);
        List<File> outLibs = new ArrayList<File>(enumLibs.length);
        for (File newLib : enumLibs) {
            File out = new File(serviceLibDir.getAbsolutePath() + File.separator + newLib.getName());
            Utils.copyFile(newLib, out);
            String description = "caGrid Enumeration " + UpgraderConstants.ENUMERATION_CURRENT_VERSION 
                + " library " + newLib.getName() + " added";
            getStatus().addDescriptionLine(description);
            LOG.debug(description);
            outLibs.add(out);
        }
        
        // update the Eclipse .classpath file
        LOG.debug("Updating eclipse classpath");
        File classpathFile = new File(getServicePath() + File.separator + ".classpath");
        File[] outLibArray = new File[enumLibs.length];
        outLibs.toArray(outLibArray);
        ExtensionUtilities.syncEclipseClasspath(classpathFile, outLibArray);
        String description = "Eclipse .classpath file updated";
        getStatus().addDescriptionLine(description);
        LOG.debug(description);
    }
}
