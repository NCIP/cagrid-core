package gov.nih.nci.cagrid.sdkinstall;

import gov.nih.nci.cagrid.common.ZipUtilities;
import gov.nih.nci.cagrid.sdkinstall.description.InstallationDescription;
import gov.nih.nci.cagrid.sdkinstall.description.JBossDescription;
import gov.nih.nci.cagrid.sdkinstall.description.JBossDescriptionNewInstallation;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;


/**
 * SDKInstaller Base "driver" class for installing the caCORE sdk
 * 
 * @author David Ervin
 * @created Jun 13, 2007 10:45:43 AM
 * @version $Id: SDKInstaller.java,v 1.9 2008-02-22 19:04:26 oster Exp $
 */
public class SDKInstaller {

    public static void installSdk(SdkVersion version, InstallationDescription description) throws Exception {
        installSdk(version, description, File.createTempFile("caCORE", "_" + System.currentTimeMillis()));
    }


    public static void installSdk(SdkVersion version, InstallationDescription description, File installDir)
        throws Exception {
        // start by unpacking the caCORE SDK
        unpackSdk(version, installDir);
        // deal with JBoss
        JBossDescription jbossDesc = description.getJBossDescription();
        File jbossDir = null;
        if (jbossDesc.getExistingInstallation() != null) {
            jbossDir = new File(jbossDesc.getExistingInstallation().getJbossLocation()).getAbsoluteFile();
        } else {
            JBossDescriptionNewInstallation newInstall = jbossDesc.getNewInstallation();
            File jbossZip = new File(newInstall.getJbossZipFile()).getAbsoluteFile();
            File unpackDir = new File(newInstall.getUnpackDirectory()).getAbsoluteFile();
            ZipUtilities.unzip(jbossZip, unpackDir);
            // jboss zips unpack a subdirectory... find it, call it jboss
            // location
            File[] dirs = unpackDir.listFiles(new FileFilter() {
                public boolean accept(File pathname) {
                    return pathname.isDirectory();
                }
            });
            if (dirs.length != 1) {
                throw new FileNotFoundException("No jboss subdirectory could be found in dir "
                    + unpackDir.getAbsolutePath());
            }
            jbossDir = dirs[0];
        }
        // locate the directory the SDK itself was installed into
        File sdkDir = new File(installDir.getAbsolutePath() + File.separator + "cacoresdk");

        // handle the specialized deployment configuration
        runDeployPropertiesManager(version, description, sdkDir);

        // invoke the build process
        invokeBuildProcess(version, description, sdkDir);

        // wait for JBoss to finish up
        JBossTwiddler twiddler = new JBossTwiddler(jbossDir);
        while (!twiddler.isJBossRunning()) {
            System.out.println("JBoss not ready yet...");
            try {
                Thread.sleep(3000);
            } catch (Exception ex) {
                // 
            }
        }

        // invoke the deploy process
        invokeDeployProcess(version, description, sdkDir);
    }


    private static void runDeployPropertiesManager(SdkVersion version, InstallationDescription description, File sdkDir)
        throws DeploymentConfigurationException {
        DeployPropertiesManager deployManager = null;
        switch (version) {
            case VERSION_3_2_1 :
                deployManager = new Version321DeployPropertiesManager(description, sdkDir);
        }
        deployManager.configureDeployment();
    }


    private static void invokeBuildProcess(SdkVersion version, InstallationDescription description, File sdkDir)
        throws BuildInvocationException {
        BuildInvoker builder = null;
        switch (version) {
            case VERSION_3_2_1 :
                builder = new Version321BuildInvoker(description, sdkDir);
        }
        builder.invokeBuildProcess();
    }


    private static void invokeDeployProcess(SdkVersion version, InstallationDescription description, File sdkDir)
        throws DeployInvocationException {
        DeployInvoker invoker = null;
        switch (version) {
            case VERSION_3_2_1 :
                invoker = new Version321DeployInvoker(description, sdkDir);
        }
        invoker.invokeDeployProcess();
    }


    private static void unpackSdk(SdkVersion version, File dir) throws IOException {
        String resourcesDir = "ext" + File.separator + "dependencies" + File.separator + "zips" + File.separator;
        File sdkZip = new File(resourcesDir + version.getZipFileName());
        ZipUtilities.unzip(sdkZip, dir);
    }
}
