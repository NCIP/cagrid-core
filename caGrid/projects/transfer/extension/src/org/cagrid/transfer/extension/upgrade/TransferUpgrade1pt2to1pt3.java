package org.cagrid.transfer.extension.upgrade;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.introduce.beans.extension.ExtensionType;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;
import gov.nih.nci.cagrid.introduce.extension.ExtensionsLoader;
import gov.nih.nci.cagrid.introduce.extension.utils.ExtensionUtilities;
import gov.nih.nci.cagrid.introduce.upgrade.common.StatusBase;
import gov.nih.nci.cagrid.introduce.upgrade.one.x.ExtensionUpgraderBase;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * MetadataUpgrade1pt0to1pt1 copies metadata descriptions
 * 
 * @author oster
 * @created Apr 9, 2007 11:21:24 AM
 * @version $Id: multiscaleEclipseCodeTemplates.xml,v 1.1 2007/03/02 14:35:01
 *          dervin Exp $
 */
public class TransferUpgrade1pt2to1pt3 extends ExtensionUpgraderBase {

    private static final String CAGRID_1_2_TRANSFER_JAR_PREFIX = "caGrid-Transfer";
    private static final String CAGRID_1_2_TRANSFER_JAR_SUFFIX = "-1.2.jar";

    private File extensionDir = new File(ExtensionsLoader.EXTENSIONS_DIRECTORY + File.separator + "caGrid_Transfer");
    private File extensionSchemaDir = new File(extensionDir.getAbsolutePath() + File.separator + "schema");
    private File extensionLibDir = new File(extensionDir.getAbsolutePath() + File.separator + "lib");


    public TransferUpgrade1pt2to1pt3(ExtensionType extensionType, ServiceInformation serviceInfo, String servicePath,
        String fromVersion, String toVersion) {
        super("TransferUpgrade1pt2to1pt3", extensionType, serviceInfo, servicePath, fromVersion, toVersion);
    }


    @Override
    protected void upgrade() throws Exception {
        upgradeJars();
        getStatus().setStatus(StatusBase.UPGRADE_OK);
    }


    /**
     * Upgrade the jars which are required for Transfer
     */
    private void upgradeJars() {
        FileFilter metadataLibFilter = new FileFilter() {
            public boolean accept(File pathname) {
                String name = pathname.getName();
                return name.endsWith(CAGRID_1_2_TRANSFER_JAR_SUFFIX) && name.startsWith(CAGRID_1_2_TRANSFER_JAR_PREFIX);
            }
        };

        // locate the old data service libs in the service
        File serviceLibDir = new File(getServicePath() + File.separator + "lib");
        File[] serviceMetadataLibs = serviceLibDir.listFiles(metadataLibFilter);
        // delete the old libraries
        for (File oldLib : serviceMetadataLibs) {
            oldLib.delete();
            getStatus().addDescriptionLine("caGrid 1.2 library " + oldLib.getName() + " removed");
        }
        // copy new libraries in
        File[] metadataLibs = extensionLibDir.listFiles();
        List<File> outLibs = new ArrayList<File>(metadataLibs.length);
        for (File newLib : metadataLibs) {
            File out = new File(serviceLibDir.getAbsolutePath() + File.separator + newLib.getName());
            try {
                Utils.copyFile(newLib, out);
                getStatus().addDescriptionLine("caGrid 1.3 library " + newLib.getName() + " added");
            } catch (IOException ex) {
                // TODO: change this to use a better exception
                throw new RuntimeException("Error copying new metadata library: " + ex.getMessage(), ex);
            }
            outLibs.add(out);
        }

        // update the Eclipse .classpath file
        File classpathFile = new File(getServicePath() + File.separator + ".classpath");
        File[] outLibArray = new File[metadataLibs.length];
        outLibs.toArray(outLibArray);
        try {
            ExtensionUtilities.syncEclipseClasspath(classpathFile, outLibArray);
            getStatus().addDescriptionLine("Eclipse .classpath file updated");
        } catch (Exception ex) {
            // TODO: change this to use a better exception
            throw new RuntimeException("Error updating Eclipse .classpath file: " + ex.getMessage(), ex);
        }

    }
}
