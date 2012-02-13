package gov.nih.nci.cagrid.common;

import java.io.File;
import java.io.FileFilter;
import java.util.Iterator;
import java.util.List;

import org.jdom.Element;

public class ClasspathCaGridVersionSetter {
    
    public static final String CLASSPATH_FILE = ".classpath";

    public static void main(String[] args) {
        File basedir = new File(args[0]);
        String oldVersion = args[1] + ".jar";
        String newVersion = args[2] + ".jar";
        
        List<File> classpathFiles = Utils.recursiveListFiles(basedir, new FileFilter() {
            
            public boolean accept(File pathname) {
                return pathname.getName().equals(CLASSPATH_FILE);
            }
        });
        
        for (File f : classpathFiles) {
            System.out.println("Editing " + f.getAbsolutePath());
            try {
                Element elem = XMLUtilities.fileNameToDocument(f.getAbsolutePath()).getRootElement();
                List cpEntries = elem.getChildren("classpathentry", elem.getNamespace());
                Iterator entryIter = cpEntries.iterator();
                while (entryIter.hasNext()) {
                    Element entry = (Element) entryIter.next();
                    if (entry.getAttributeValue("kind").equals("lib")) {
                        String path = entry.getAttributeValue("path");
                        File libPath = new File(path);
                        String libName = libPath.getName();
                        if (libName.startsWith("caGrid") && libName.endsWith(oldVersion)) {
                            libName = libName.substring(0, libName.length() - oldVersion.length());
                            libName += newVersion;
                            File updated = new File(libPath.getParent(), libName);
                            entry.setAttribute("path", updated.getPath());
                        }
                    }
                }
                String xml = XMLUtilities.formatXML(XMLUtilities.elementToString(elem));
                System.out.println(xml);
                Utils.stringBufferToFile(new StringBuffer(xml), f);
            } catch (Exception ex) {
                ex.printStackTrace();
                System.exit(1);
            }
        }
    }
}
