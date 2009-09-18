package gov.nih.nci.cagrid.introduce.extension.utils;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.common.XMLUtilities;
import gov.nih.nci.cagrid.introduce.common.FileFilters;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/** 
 *  ExtensionUtilities
 *  Some generic utilities to make extensions easier
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * 
 * @created Jun 7, 2006 
 * @version $Id: ExtensionUtilities.java,v 1.15 2007-11-06 15:53:43 hastings Exp $ 
 */
public class ExtensionUtilities {
	public static final String CLASSPATHENTRY_ELEMENT = "classpathentry";


	/**
	 * Adds libraries to an eclipse .classpath file
	 * 
	 * @param classpathFile
	 * 		The .classpath file
	 * @param additionalLibs
	 * 		The libraries (jars) to add to the .classpath.  The jars will be added
	 * 		with paths <i>relative</i> to the .classpath file's location
	 * @throws Exception
	 */
	public static void syncEclipseClasspath(File classpathFile, File[] additionalLibs) throws Exception {
		Element classpathElement = XMLUtilities.fileNameToDocument(classpathFile.getAbsolutePath()).getRootElement();
		
		// get the list of additional libraries to add to the classpath
		Set libNames = new HashSet();
		for (int i = 0; i < additionalLibs.length; i++) {
			String relativeLibName = Utils.getRelativePath(classpathFile, additionalLibs[i]);
			relativeLibName = convertToUnixStylePath(relativeLibName);
			libNames.add(relativeLibName);
		}
		
		// find out which libs are NOT yet in the classpath
		Iterator classpathEntryIter = classpathElement.getChildren(
			CLASSPATHENTRY_ELEMENT, classpathElement.getNamespace()).iterator();
		while (classpathEntryIter.hasNext()) {
			Element entry = (Element) classpathEntryIter.next();
			if (entry.getAttributeValue("kind").equals("lib")) {
				libNames.remove(entry.getAttributeValue("path"));
			}
		}
		
		// anything left over now has to be added to the classpath
		Iterator additionalLibIter = libNames.iterator();
		while (additionalLibIter.hasNext()) {
			String libName = (String) additionalLibIter.next();
			Element entryElement = new Element(CLASSPATHENTRY_ELEMENT);
			entryElement.setAttribute("kind", "lib");
			entryElement.setAttribute("path", libName);
			classpathElement.addContent(entryElement);
		}
		
		// write the .classpath file back out to disk
		XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
		FileWriter writer = new FileWriter(classpathFile);
		outputter.output(classpathElement, writer);
		writer.flush();
		writer.close();
	}
	
    
    /**
     * Gets the libraries from an eclipse .classpath file
     * 
     * @param classpathFile
     * @return
     *      An array of Files for each library in the classpath
     * @throws Exception
     */
    public static File[] getLibrariesFromEclipseClasspath(File classpathFile) throws Exception {
        Element classpathElement = XMLUtilities.fileNameToDocument(classpathFile.getAbsolutePath()).getRootElement();
        
        List<File> libs = new ArrayList();
        // find out which libs are in the classpath
        Iterator classpathEntryIter = classpathElement.getChildren(
            CLASSPATHENTRY_ELEMENT, classpathElement.getNamespace()).iterator();
        while (classpathEntryIter.hasNext()) {
            Element entry = (Element) classpathEntryIter.next();
            if (entry.getAttributeValue("kind").equals("lib")) {
                String relPath = entry.getAttributeValue("path");
                File fullPath = new File(classpathFile.getParentFile().getAbsolutePath() 
                    + File.separator + relPath);
                libs.add(fullPath.getCanonicalFile());
            }
        }
        File[] libArray = new File[libs.size()];
        libs.toArray(libArray);
        return libArray;
    }
    
    
    /**
     * Removes library entries from an Eclipse .classpath file
     * Libraries to be removed may be fully qualified paths, and will first be
     * made relative to the .classpath file before removal is attempted.
     * 
     * @param classpathFile
     * @param removeLibs
     * @throws Exception
     */
    public static void removeLibrariesFromClasspath(File classpathFile, File[] removeLibs) throws Exception {
        Element classpathElement = XMLUtilities.fileNameToDocument(classpathFile.getAbsolutePath()).getRootElement();
        
        // get the list of libraries to be removed
        Set<String> libNames = new HashSet();
        for (File remove : removeLibs) {
            String relativeLibName = Utils.getRelativePath(classpathFile, remove);
            relativeLibName = convertToUnixStylePath(relativeLibName);
            libNames.add(relativeLibName);
        }
        
        // start removing entries
        List<Element> keptEntries = new LinkedList();
        Iterator classpathEntryIter = classpathElement.getChildren(
            CLASSPATHENTRY_ELEMENT, classpathElement.getNamespace()).iterator();
        while (classpathEntryIter.hasNext()) {
            Element entry = (Element) classpathEntryIter.next();
            boolean keep = true;
            if (entry.getAttributeValue("kind").equals("lib")) {
                String libPath = entry.getAttributeValue("path");
                if (libNames.contains(libPath)) {
                    keep = false;
                }
            }
            if (keep) {
                keptEntries.add(entry);
            }
        }
        // remove ALL classpath entries
        classpathElement.removeChildren(CLASSPATHENTRY_ELEMENT, classpathElement.getNamespace());
        // restore the ones we're not removing
        for (Element e : keptEntries) {
            classpathElement.addContent(e);
        }
        
        // write the .classpath file back out to disk
        XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
        FileWriter writer = new FileWriter(classpathFile);
        outputter.output(classpathElement, writer);
        writer.flush();
        writer.close();
    }
    
    
    /**
     * Resynchronizes a classpath file's entries with the contents
     * of the lib dir.  This method removes all entries in the classpath which
     * begin with 'lib/' and adds all jar files found recursively in 
     * the lib directory which is a sibling to the .classpath file.
     * 
     * @param classpathFile
     * @throws Exception
     */
    public static void resyncWithLibDir(File classpathFile) throws Exception {
        Element classpathElement = XMLUtilities.fileNameToDocument(classpathFile.getAbsolutePath()).getRootElement();
        
        // start removing entries
        List<Element> keptEntries = new LinkedList();
        Iterator classpathEntryIter = classpathElement.getChildren(
            CLASSPATHENTRY_ELEMENT, classpathElement.getNamespace()).iterator();
        while (classpathEntryIter.hasNext()) {
            Element entry = (Element) classpathEntryIter.next();
            boolean keep = true;
            if (entry.getAttributeValue("kind").equals("lib")) {
                String libPath = entry.getAttributeValue("path");
                // strip out anything from the lib directory
                keep = !libPath.startsWith("lib/");
            }
            if (keep) {
                keptEntries.add(entry);
            }
        }
        // remove ALL classpath entries from the root element
        classpathElement.removeChildren(CLASSPATHENTRY_ELEMENT, classpathElement.getNamespace());
        
        // list all libs in the lib dir
        File libDir = new File(classpathFile.getParentFile().getAbsolutePath() 
            + File.separator + "lib");
        List<File> libs = Utils.recursiveListFiles(libDir, new FileFilters.JarFileFilter());
        
        // create new entry elements for the libs
        for (File lib : libs) {
            Element entry = new Element(CLASSPATHENTRY_ELEMENT, classpathElement.getNamespace());
            entry.setAttribute("kind", "lib");
            String relPath = Utils.getRelativePath(libDir.getParentFile(), lib);
            relPath = convertToUnixStylePath(relPath);
            entry.setAttribute("path", relPath);
            // add the entry element
            keptEntries.add(entry);
        }
        
        // put all classpath entries in the classpath element
        for (Element e : keptEntries) {
            classpathElement.addContent(e);
        }
        
        
        // write the .classpath file back out to disk
        XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
        FileWriter writer = new FileWriter(classpathFile);
        outputter.output(classpathElement, writer);
        writer.flush();
        writer.close();
    }
    
	
	private static String convertToUnixStylePath(String pathname) {
		return pathname.replace('\\', '/');
	}
}
