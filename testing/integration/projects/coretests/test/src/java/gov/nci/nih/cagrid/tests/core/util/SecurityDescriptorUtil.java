package gov.nci.nih.cagrid.tests.core.util;

import java.io.File;

/** 
 *  SecurityDescriptorUtil
 *  TODO:DOCUMENT ME
 * 
 * @author David Ervin
 * 
 * @created Nov 15, 2007 11:46:03 AM
 * @version $Id: SecurityDescriptorUtil.java,v 1.1 2007-11-15 19:47:52 dervin Exp $ 
 */
public class SecurityDescriptorUtil {

    public static final String SECURITY_DESCRIPTOR_FILENAME = "security-descriptor.xml";
    public static final String GLOBUS_SECURITY_DESCRIPTOR_PROPERTY = "globus.securitydescriptor";

    public static File getDefaultDescriptor() {
        File securityDescriptor = new File(
            System.getProperty(GLOBUS_SECURITY_DESCRIPTOR_PROPERTY, 
                SECURITY_DESCRIPTOR_FILENAME)
        );
        return securityDescriptor;
    }
}
