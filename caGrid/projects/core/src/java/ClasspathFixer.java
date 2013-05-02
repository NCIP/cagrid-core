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
