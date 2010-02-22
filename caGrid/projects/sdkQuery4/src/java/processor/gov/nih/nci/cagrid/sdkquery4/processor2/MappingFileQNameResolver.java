package gov.nih.nci.cagrid.sdkquery4.processor2;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.data.mapping.ClassToQname;
import gov.nih.nci.cagrid.data.mapping.Mappings;

import java.io.File;
import java.io.FileReader;

import javax.xml.namespace.QName;

/** 
 *  MappingFileQNameResolver
 *  QName resolver which can make use of a ClassToQnames mapping file
 * 
 * @author David Ervin
 * 
 * @created Apr 3, 2008 2:50:44 PM
 * @version $Id: MappingFileQNameResolver.java,v 1.1 2008/04/03 19:39:12 dervin Exp $ 
 */
public class MappingFileQNameResolver implements QNameResolver {
    
    private Mappings mappings = null;
    
    public MappingFileQNameResolver(File classToQnameFile) throws Exception {
        this(deserializeMappings(classToQnameFile));
    }
    
    
    public MappingFileQNameResolver(Mappings mappings) {
        this.mappings = mappings;
    }
    

    public QName getQName(String javaClassName) {
        for (ClassToQname map : mappings.getMapping()) {
            if (javaClassName.equals(map.getClassName())) {
                return new QName(map.getQname());
            }
        }
        return null;
    }
    
    
    public static Mappings deserializeMappings(File mappingsFile) throws Exception {
        FileReader reader = new FileReader(mappingsFile);
        Mappings map = Utils.deserializeObject(reader, Mappings.class);
        return map;
    }
}
