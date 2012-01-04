import gov.nih.nci.cagrid.common.Utils;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.util.List;


public class ClasspathFixer {

    /**
     * @param args
     */
    public static void main(String[] args) {
        File base = new File("/Users/ervin/Projects/caGrid/caGrid-1_5_release/Software/core");
        try {
            List<File> classpaths = Utils.recursiveListFiles(base, new FileFilter() {
                
                @Override
                public boolean accept(File pathname) {
                    return pathname.getName().equals(".classpath");
                }
            });
            for (File f : classpaths) {
                if (f.isFile()) {
                    System.out.println("Editing " + f.getAbsolutePath());
                    FileInputStream fis = new FileInputStream(f);
                    StringBuffer buf = Utils.inputStreamToStringBuffer(fis);
                    fis.close();
                    String edited = buf.toString().replace("GT4_LOCATION", "GT4_SHA2_LOCATION");
                    Utils.stringBufferToFile(new StringBuffer(edited), f);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
