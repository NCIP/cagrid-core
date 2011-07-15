package gov.nih.nci.cagrid.common;

import java.io.File;
import java.io.FileFilter;
import java.util.Iterator;

import org.jdom.Element;

public class ClasspathJarVersionEditor {

    public static final String OLD_ENDING = "-1.4.jar";
    public static final String NEW_ENDING = "-1.5.jar";

    /**
     * @param args
     */
    public static void main(String[] args) {
        try {
            File baseDir = new File("c:/Users/David/workspace/cagrid_sha2/core/caGrid/projects");
            File[] projDirs = baseDir.listFiles(new FileFilter() {
                
                public boolean accept(File pathname) {
                    return pathname.isDirectory() && !pathname.getName().startsWith(".");
                }
            });
            for (File dir : projDirs) {
                File classpathFile = new File(dir, ".classpath");
                System.out.println("Editing " + classpathFile.getAbsolutePath());
                Element root = XMLUtilities.fileNameToDocument(classpathFile.getAbsolutePath()).getRootElement();
                Iterator<Element> entryIter = root.getChildren("classpathentry", root.getNamespace()).iterator();
                while (entryIter.hasNext()) {
                    Element entryElem = entryIter.next();
                    String kind = entryElem.getAttributeValue("kind");
                    if ("lib".equals(kind)) {
                        String fullName = entryElem.getAttributeValue("path");int cut = fullName.lastIndexOf('/') + 1;
                        String name = fullName.substring(cut);
                        if (name.startsWith("caGrid-") && name.endsWith(OLD_ENDING)) {
                            name = name.substring(0, name.lastIndexOf(OLD_ENDING));
                            name = fullName.substring(0, cut) + name + NEW_ENDING;
                            System.out.println("Turned " + fullName + " into " + name);
                            entryElem.setAttribute("path", name);
                        }
                    }
                }
                String doneXml = XMLUtilities.formatXML(XMLUtilities.elementToString(root));
                
                Utils.stringBufferToFile(new StringBuffer(doneXml), classpathFile);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
