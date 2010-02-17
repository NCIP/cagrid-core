import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.common.XMLUtilities;

import java.io.File;
import java.io.FileFilter;
import java.io.StringWriter;
import java.util.List;

import org.exolab.castor.tools.MappingTool;


public class MappingDriver {

    /**
     * @param args
     */
    public static void main(String[] args) {
        try {
            MappingTool tool = new MappingTool();
            File srcDir = new File("src/java/beans");
            List<File> files = Utils.recursiveListFiles(srcDir, new FileFilter() {
                public boolean accept(File pathname) {
                    return pathname.getName().endsWith(".java");
                }
            });
            for (File f : files) {
                if (f.isFile() && !f.getName().startsWith("MappingDriver")) {
                    String path = Utils.getRelativePath(srcDir, f.getCanonicalFile());
                    path = path.substring(0, path.length() - 5);
                    String className = path.replace(File.separatorChar, '.');
                    System.out.println("Found " + className);
                    System.out.flush();
                    tool.addClass(className);
                }
            }
            StringWriter writer = new StringWriter();
            tool.write(writer);
            String pretty = XMLUtilities.formatXML(writer.getBuffer().toString());
            System.out.println(pretty);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

}
