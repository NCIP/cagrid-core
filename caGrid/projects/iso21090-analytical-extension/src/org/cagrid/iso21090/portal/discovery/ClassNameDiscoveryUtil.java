/**
*============================================================================
*  Copyright The Ohio State University Research Foundation, The University of Chicago - 
*	Argonne National Laboratory, Emory University, SemanticBits LLC, and 
*	Ekagra Software Technologies Ltd.
*
*  Distributed under the OSI-approved BSD 3-Clause License.
*  See http://ncip.github.com/cagrid-core/LICENSE.txt for details.
*============================================================================
**/
package org.cagrid.iso21090.portal.discovery;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.xml.bind.annotation.XmlRootElement;

public class ClassNameDiscoveryUtil {

    private List<File> libFiles = null;

    public ClassNameDiscoveryUtil(List<File> libFiles) {
        this.libFiles = libFiles;
    }
    
    
    public String getJavaClassName(String packageName, String xmlElementName) throws IOException, ClassNotFoundException {
        Set<Class<?>> classesInPackage = getClassesInPackage(packageName);
        for (Class<?> clazz : classesInPackage) {
            XmlRootElement rootElement = clazz.getAnnotation(XmlRootElement.class);
            if (rootElement != null) {
                String boundElemName = rootElement.name();
                if (xmlElementName.equals(boundElemName)) {
                    // found the class we want
                    return clazz.getSimpleName();
                }
            }
        }
        return null;
    }
    
    
    private Set<Class<?>> getClassesInPackage(String packageName) throws IOException, ClassNotFoundException {
        String slashifiedPackage = packageName.replace('.', '/');
        ClassLoader libClassLoader = getLibClassLoader();
        Set<Class<?>> classes = new HashSet<Class<?>>();
        for (File f : libFiles) {
            JarFile jar = new JarFile(f);
            Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                if (!entry.isDirectory() && entry.getName().endsWith(".class") 
                    && entry.getName().startsWith(slashifiedPackage)) {
                    String dotifiedClassname = entry.getName().replace('/', '.');
                    String cleanClassname = dotifiedClassname.substring(0, dotifiedClassname.length() - 6);
                    Class<?> clazz = libClassLoader.loadClass(cleanClassname);
                    classes.add(clazz);
                }
            }
            jar.close();
        }
        return classes;
    }
    
    
    private ClassLoader getLibClassLoader() throws MalformedURLException {
        URL[] urls = new URL[libFiles.size()];
        for (int i = 0; i < libFiles.size(); i++) {
            urls[i] = libFiles.get(i).toURI().toURL();
        }
        ClassLoader loader = new URLClassLoader(urls, getClass().getClassLoader());
        return loader;
    }
    
    
    public static void main(String[] args) {
        List<File> libs = Arrays.asList(new File("lib").listFiles(new FileFilter() {
            
            public boolean accept(File pathname) {
                return pathname.isFile() && pathname.getName().endsWith(".jar");
            }
        }));
        ClassNameDiscoveryUtil util = new ClassNameDiscoveryUtil(libs);
        try {
            String name = util.getJavaClassName("org.iso._21090", "tr");
            System.out.println("Class name found: " + name); // should be Tr
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
