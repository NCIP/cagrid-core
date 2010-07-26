package org.cagrid.iso21090.tests.integration.steps;

import gov.nih.nci.cagrid.common.JarUtilities;
import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import org.cagrid.iso21090.tests.integration.ExampleProjectInfo;

/**
 * Add the commons-beanutils-1.6.jar to the SDK's webapp war file.
 * 
 * A temporary fix for GForge #29196:
 * https://gforge.nci.nih.gov/tracker/index.php?func=detail&aid=29196&group_id=148&atid=730
 * 
 * @author David W. Ervin
 */
public class FixSdkWarStep extends Step {
    public static final String OUTPUT_PACKAGE_PATH = "target" + File.separator + "dist" 
        + File.separator + "exploded" + File.separator + "output" 
        + File.separator + ExampleProjectInfo.EXAMPLE_PROJECT_NAME
        + File.separator + "package";
    public static final String LOCAL_CLIENT_PATH = OUTPUT_PACKAGE_PATH + File.separator + "local-client";
    public static final String SERVER_PATH = OUTPUT_PACKAGE_PATH + File.separator + "server" 
        + File.separator + "tomcat" + File.separator + "webapps";
    
    public static final String BEANUTILS_JAR_NAME = "commons-beanutils-1.6.jar";
    public static final String JAR_PATH = "WEB-INF/lib/" + BEANUTILS_JAR_NAME;
    
    public FixSdkWarStep() {
        super();
    }


    public void runStep() throws Throwable {
        File webappWarDir = new File(ExampleProjectInfo.getExampleProjectDir(), SERVER_PATH);
        // TODO: get the application name from properties
        File warFile = new File(webappWarDir, "isoExample.war");
        JarFile warAsJar = new JarFile(warFile);
        ZipEntry entry = warAsJar.getEntry(JAR_PATH);
        warAsJar.close();
        if (entry == null) {
            // sure enough, it's not there
            System.out.println("Beanutils entry not found in war... will add it");
            try {
                byte[] beanutilsBytes = getBeanutilsJarBytes();
                JarUtilities.insertEntry(warFile, JAR_PATH, beanutilsBytes);
            } catch (Exception ex) {
                ex.printStackTrace();
                fail("Error adding beanutils jar to the war: " + ex.getMessage());
            }
        }
    }
    
    
    private byte[] getBeanutilsJarBytes() throws IOException {
        File localClientLibDir = 
            new File(ExampleProjectInfo.getExampleProjectDir(), 
            LOCAL_CLIENT_PATH + File.separator + "lib");
        File jar = new File(localClientLibDir, BEANUTILS_JAR_NAME);
        assertTrue("Beanutils jar (" + jar.getAbsolutePath() + ") not found", jar.exists());
        ByteArrayOutputStream tempBytes = new ByteArrayOutputStream();
        byte[] readBytes = new byte[1024];
        int len = -1;
        BufferedInputStream buffStream = new BufferedInputStream(new FileInputStream(jar));
        while ((len = buffStream.read(readBytes)) != -1) {
            tempBytes.write(readBytes, 0, len);
        }
        buffStream.close();
        return tempBytes.toByteArray();
    }
}
