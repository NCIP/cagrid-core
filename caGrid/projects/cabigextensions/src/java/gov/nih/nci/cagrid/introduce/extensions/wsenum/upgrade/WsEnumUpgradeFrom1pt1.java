package gov.nih.nci.cagrid.introduce.extensions.wsenum.upgrade;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.introduce.beans.ServiceDescription;
import gov.nih.nci.cagrid.introduce.beans.extension.ExtensionType;
import gov.nih.nci.cagrid.introduce.common.CommonTools;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;
import gov.nih.nci.cagrid.introduce.extension.ExtensionsLoader;
import gov.nih.nci.cagrid.introduce.extension.utils.ExtensionUtilities;
import gov.nih.nci.cagrid.introduce.upgrade.one.x.ExtensionUpgraderBase;
import gov.nih.nci.cagrid.wsenum.common.WsEnumConstants;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * WsEnumUpgradeFrom1pt1
 * Upgrades caGrid ws-enumeration support from 1.1 to current
 * 
 * @author ervin
 * @created Apr 9, 2007 11:21:24 AM
 * @version $Id: multiscaleEclipseCodeTemplates.xml,v 1.1 2007/03/02 14:35:01
 *          dervin Exp $
 */
public class WsEnumUpgradeFrom1pt1 extends ExtensionUpgraderBase {
    private static final String CAGRID_1_1_WS_ENUM_JAR_PREFIX = "caGrid-1.1-wsEnum";
    
    private static final String DATA_SERVICE_ENUM_PROPERTY = "dataService_enumIteratorType";
    
    private static Log LOG = LogFactory.getLog(WsEnumUpgradeFrom1pt1.class);

    public WsEnumUpgradeFrom1pt1(ExtensionType extensionType, ServiceInformation serviceInfo, String servicePath,
        String fromVersion, String toVersion) {
        super(WsEnumUpgradeFrom1pt1.class.getSimpleName(),
            extensionType, serviceInfo, servicePath, fromVersion, toVersion);
    }

    
    protected void upgrade() throws Exception {
        upgradeJars();        
        addIterImplTypeServiceProperty();
    }
    
    
    /**
     * Upgrade the jars which are required for metadata
     */
    private void upgradeJars() {
        FileFilter enumLibFilter = new FileFilter() {
            public boolean accept(File pathname) {
                String name = pathname.getName();
                return name.endsWith(".jar") 
                && (name.startsWith(CAGRID_1_1_WS_ENUM_JAR_PREFIX));
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
            String description = "caGrid 1.1 library " + oldLib.getName() + " removed";
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
            try {
                Utils.copyFile(newLib, out);
                String description = "caGrid Enumeration " + UpgraderConstants.ENUMERATION_CURRENT_VERSION 
                    + " library " + newLib.getName() + " added";
                getStatus().addDescriptionLine(description);
                System.out.println(description);
            } catch (IOException ex) {
                // TODO: change this to use a better exception
                throw new RuntimeException("Error copying new metadata library: " + ex.getMessage(), ex);
            }
            outLibs.add(out);
        }
        
        // remove unused wsrf_core_[stubs_]enum.jar files
        LOG.debug("Removing unused wsrf_core_[stubs_]enum.jar files");
        File deleteJar1 = new File(serviceLibDir, "wsrf_core_enum.jar");
        File deleteJar2 = new File(serviceLibDir, "wsrf_core_stubs_enum.jar");
        if (deleteJar1.exists()) {
            deleteJar1.delete();
            String description = "caGrid 1.1 library " + deleteJar1.getName() + " removed";
            getStatus().addDescriptionLine(description);
            LOG.debug(description);
        }
        if (deleteJar2.exists()) {
            deleteJar2.delete();
            String description = "caGrid 1.1 library " + deleteJar2.getName() + " removed";
            getStatus().addDescriptionLine(description);
            System.out.println(description);
        }
        
        // update the Eclipse .classpath file
        LOG.debug("Updating eclipse classpath");
        File classpathFile = new File(getServicePath() + File.separator + ".classpath");
        File[] outLibArray = new File[enumLibs.length];
        outLibs.toArray(outLibArray);
        try {
            ExtensionUtilities.syncEclipseClasspath(classpathFile, outLibArray);
            ExtensionUtilities.removeLibrariesFromClasspath(classpathFile, 
                    new File[] {deleteJar1, deleteJar2});
            String description = "Eclipse .classpath file updated";
            getStatus().addDescriptionLine(description);
            LOG.debug(description);
        } catch (Exception ex) {
            // TODO: change this to use a better exception
            throw new RuntimeException("Error updating Eclipse .classpath file: " + ex.getMessage(), ex);
        }
    }
    
    
    private void addIterImplTypeServiceProperty() throws Exception {
        String line = "Adding service property " + WsEnumConstants.ITER_IMPL_TYPE_PROPERTY;
        getStatus().addDescriptionLine(line);
        LOG.debug(line);
        // only procede if the base upgrade worked
        String iterTypeName = WsEnumConstants.DEFAULT_ITER_IMPL_TYPE;
        // if the data service extension has set the enum iterator type, use it
        ServiceDescription desc = getServiceInformation().getServiceDescriptor();
        if (CommonTools.servicePropertyExists(desc, DATA_SERVICE_ENUM_PROPERTY)) {
            iterTypeName = CommonTools.getServicePropertyValue(desc, DATA_SERVICE_ENUM_PROPERTY);
            line = "Using data service ws-enum iter implementation property value of " + iterTypeName;
            getStatus().addDescriptionLine(line);
            LOG.debug(line);
        }
        setIterImplTypeServiceProperty(iterTypeName);
        System.out.println("ADDING ISSUE:");
        String issue = "New WS-Enumeration IterImplType service property added";
        String resolution = "If it exists, the data service property for controling the WS-Enumeration IterImplType (" 
            + DATA_SERVICE_ENUM_PROPERTY + ") is deprecated.  Use " + WsEnumConstants.ITER_IMPL_TYPE_PROPERTY; 
        getStatus().addIssue(issue, resolution);
        LOG.debug("Issue: " + issue);
        LOG.debug("Resolution: " + resolution);
    }
    
    
    private void setIterImplTypeServiceProperty(String iterTypeName) {
        ServiceDescription desc = getServiceInformation().getServiceDescriptor();
        if (!CommonTools.servicePropertyExists(desc, WsEnumConstants.ITER_IMPL_TYPE_PROPERTY)) {
            LOG.debug("Setting service property " + WsEnumConstants.ITER_IMPL_TYPE_PROPERTY);
            CommonTools.setServiceProperty(desc, WsEnumConstants.ITER_IMPL_TYPE_PROPERTY, 
                iterTypeName, false);
        }
    }
}
