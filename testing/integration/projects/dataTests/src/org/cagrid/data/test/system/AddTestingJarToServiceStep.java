package org.cagrid.data.test.system;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.data.test.creation.DataTestCaseInfo;

/** 
 *  AddTestingJarToServiceStep
 *  Adds the testing jar to a grid service's libraries
 * 
 * @author David Ervin
 * 
 * @created May 21, 2008 4:07:21 PM
 * @version $Id: AddTestingJarToServiceStep.java,v 1.3 2008-08-21 14:36:25 dervin Exp $ 
 */
public class AddTestingJarToServiceStep extends Step {
        
    public static final String DATA_TESTS_JAR_PREFIX = "caGrid-dataTests";
    
    private static Log LOGGER = LogFactory.getLog(AddTestingJarToServiceStep.class);

    private DataTestCaseInfo info;
    
    public AddTestingJarToServiceStep(DataTestCaseInfo info) {
        this.info = info;
    }
    
    
    public void runStep() throws Throwable {
        File libDir = new File(info.getDir(), "lib");
        // find the testing jar
        boolean jarsCopied = false;
        LOGGER.debug("Installing data tests jar to class loader");
        assertTrue("Class loader was not a URLClassLoader", getClass().getClassLoader() instanceof URLClassLoader);
        URLClassLoader currentClassLoader = (URLClassLoader) getClass().getClassLoader();
        URL[] urls = currentClassLoader.getURLs();
        for (URL u : urls) {
            URI uri = u.toURI();
            LOGGER.debug("CLASSPATH URI: " + uri.toString());
            if (uri.getScheme().equalsIgnoreCase("file")) {
                // is a file
                File classpathFile = new File(uri);
                String name = classpathFile.getName();
                if (name.endsWith(".jar") && name.startsWith(DATA_TESTS_JAR_PREFIX)) {
                    // found the data tests jar file
                    // copy the jar to the service's lib dir
                    File libOut = new File(libDir, classpathFile.getName());
                    Utils.copyFile(classpathFile, libOut);
                    jarsCopied = true;
                }
            }
        }
        assertTrue("Could not locate caGrid-dataTests jar on classpath", jarsCopied);
    }
}
