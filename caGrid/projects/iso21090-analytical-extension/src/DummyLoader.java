import gov.nih.nci.cagrid.common.XMLUtilities;
import gov.nih.nci.cagrid.introduce.IntroduceConstants;
import gov.nih.nci.cagrid.introduce.beans.ServiceDescription;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;

import java.io.File;

import org.jdom.Element;


public class DummyLoader {
    
    public static final String EXPECTED_INTRODUCE_VERSION = "1.3";

    /**
     * @param args
     */
    public static void main(String[] args) {
        // need the service basedir, which should be the current working dir.
        // if it's not, pass in a service property from ant, and set it to ${basedir}
        // get it with System.getProperty("my.1337.property");
        String basedir = new File(".").getAbsolutePath();
        File introduceXml = new File(basedir, IntroduceConstants.INTRODUCE_XML_FILE);
        try {
            Element root = XMLUtilities.fileNameToDocument(introduceXml.getAbsolutePath()).getRootElement();
            String introduceVersion = root.getAttributeValue("introduceVersion");
            if (!EXPECTED_INTRODUCE_VERSION.equals(introduceVersion)) {
                // bail out here, unexpected version
                System.err.println("Unexpected introduce version: " + introduceVersion);
            } else {
                // load up the beans
                ServiceInformation serviceInfo = new ServiceInformation(introduceXml.getParentFile());
                ServiceDescription descriptor = serviceInfo.getServiceDescriptor();
                // or
                serviceInfo.getServices();
                // or
                serviceInfo.getNamespaces();
                
                // parse service descriptor and edit code here
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
