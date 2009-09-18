package gov.nih.nci.cagrid.data.ui.auditors;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.data.service.auditing.DataServiceAuditor;
import gov.nih.nci.cagrid.introduce.common.FileFilters;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/** 
 *  AuditorsLoader
 *  Utility to locate / load data service auditors
 * 
 * @author David Ervin
 * 
 * @created May 23, 2007 4:05:52 PM
 * @version $Id: AuditorsLoader.java,v 1.3 2007-12-18 19:11:40 dervin Exp $ 
 */
public class AuditorsLoader {

    public static List<Class> getAvailableAuditorClasses(File libDir) throws MalformedURLException, IOException {
        // list jars from the lib dir as URLs
        List jarFiles = Utils.recursiveListFiles(libDir, new FileFilters.JarFileFilter());
        List<URL> jarUrls = new LinkedList<URL>();
        for (int i = 0; i < jarFiles.size(); i++) {
            File jarFile = (File) jarFiles.get(i);
            if (jarFile.isFile()) {
                jarUrls.add(jarFile.toURL());
            }
        }
        URL[] urlArray = new URL[jarUrls.size()];
        jarUrls.toArray(urlArray);
        System.out.println("Looking for data service auditor subclasses");
        // load all subclasses of DataServiceAuditor
        List<Class> subclasses = new LinkedList<Class>();
        for (int i = 0; i < jarFiles.size(); i++) {
            // loader created each time because 
            // iterating over many classes clogs up the cache in the loader
            ClassLoader loader = new URLClassLoader(urlArray);
            JarFile jar = new JarFile((File) jarFiles.get(i));
            Enumeration jarEntries = jar.entries();
            while (jarEntries.hasMoreElements()) {
                JarEntry entry = (JarEntry) jarEntries.nextElement();
                String name = entry.getName();
                if (name.endsWith(".class")) {
                    name = name.replace('/', '.');
                    name = name.substring(0, name.length() - 6);
                    Class loadedClass = null;
                    try {
                        loadedClass = loader.loadClass(name);
                    } catch (Throwable e) {
                        // theres a lot of these...
                        // System.err.println("Error loading class (" + name
                        // + "):" + e.getMessage());
                    }
                    if (loadedClass != null && DataServiceAuditor.class.isAssignableFrom(loadedClass)
                        && !DataServiceAuditor.class.getName().equals(loadedClass.getName())) {
                        subclasses.add(loadedClass);
                    }
                }
            }
            loader = null;
            jar.close();
        }
        return subclasses;
    }
    
    
    public static DataServiceAuditor loadAuditor(File libDir, String auditorClassName) 
        throws MalformedURLException, IOException, IllegalAccessException, InstantiationException {
        List<Class> availableClasses = getAvailableAuditorClasses(libDir);
        for (Class c : availableClasses) {
            if (c.getName().equals(auditorClassName)) {
                DataServiceAuditor auditor = (DataServiceAuditor) c.newInstance();
                return auditor;
            }
        }
        return null;
    }
}
