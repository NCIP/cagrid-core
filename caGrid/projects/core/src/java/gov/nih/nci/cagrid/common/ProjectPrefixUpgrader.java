package gov.nih.nci.cagrid.common;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;

import javax.swing.JFileChooser;

import org.apache.axis.utils.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/** 
 *  ProjectPrefixUpgrader
 *  TODO:DOCUMENT ME
 * 
 * @author David Ervin
 * 
 * @created Jul 3, 2007 9:32:02 AM
 * @version $Id: ProjectPrefixUpgrader.java,v 1.1 2007-10-02 19:56:28 dervin Exp $ 
 */
public class ProjectPrefixUpgrader {

    /**
     * @param args
     */
    public static void main(String[] args) {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int choice = chooser.showOpenDialog(null);
        if (choice == JFileChooser.APPROVE_OPTION) {
            File baseDir = chooser.getSelectedFile();
            List<File> projectFiles = Utils.recursiveListFiles(baseDir, new FileFilter() {
                public boolean accept(File pathname) {
                    return pathname.getName().equals(".project");
                }
            });
            for (int i = 0; i < projectFiles.size(); i++) {
                File proj = projectFiles.get(i);
                System.out.println("Upgrading " + proj.getAbsolutePath());
                try {
                    FileInputStream in = new FileInputStream(proj);
                    Document doc = XMLUtils.newDocument(in);
                    NodeList nodes = doc.getDocumentElement().getChildNodes();
                    for (int j = 0; j < nodes.getLength(); j++) {
                        Node n = nodes.item(j);
                        if (n.getNodeType() == Node.ELEMENT_NODE) {
                            Element e = (Element) n;
                            if (e.getNodeName().equals("name")) {
                                String projName = e.getTextContent();                                
                                if (projName.startsWith("caGrid-1.0-")) {
                                    projName = "caGrid-" + projName.substring("caGrid-1.0-".length());
                                }
                                e.setTextContent(projName);
                                break;
                            }
                        }
                    }
                    in.close();
                    FileOutputStream out = new FileOutputStream(proj);
                    XMLUtils.DocumentToStream(doc, out);
                    out.flush();
                    out.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    System.exit(1);
                }
            }
        }
    }
}
