package gov.nih.nci.cagrid.introduce.upgrade.common;

import gov.nih.nci.cagrid.common.XMLUtilities;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.xpath.XPath;

/**
 * Utility class for getting information from a service's introduce XML file.
 */
public class UpgradeUtilities {

    /**
     * Read the XML document in the named introduce XML file and return the
     * value of the ServiceDescription element's introduceVersion attribute.
     * 
     * @param introduceFile
     *            The absolute file path of an introduce XML file.
     * @return the value of the introduceVersion attribute ("1.3", "1.4", ...)
     *         or null.
     * @throws Exception
     *             if there is a problem.
     */
    public static String getCurrentServiceVersion(String introduceFile) throws Exception {

        Document doc = XMLUtilities.fileNameToDocument(introduceFile);
        XPath xpath = XPath.newInstance("tns:ServiceDescription/@introduceVersion");
        xpath.addNamespace("tns", "gme://gov.nih.nci.cagrid/1/Introduce");
        Attribute result = (Attribute) xpath.selectSingleNode(doc);
        String serviceVersion = null;
        if (result != null) {
            serviceVersion = result.getValue();
        }

        return serviceVersion;

    }

    /**
     * Read the XML document in the named introduce XML file and return the
     * name of the first service described therein.
     * 
     * @param introduceFile
     *            The absolute file path of an introduce XML file.
     * @return the value of the introduceVersion attribute ("1.3", "1.4", ...)
     *         or null.
     * @throws Exception
     *             if there is a problem.
     */
    public static String getServiceName(String introduceFile) throws Exception {

        Document doc = XMLUtilities.fileNameToDocument(introduceFile);
        XPath xpath = XPath.newInstance("tns:ServiceDescription/servicens:Services/servicens:Service[1]/@name");
        xpath.addNamespace("tns", "gme://gov.nih.nci.cagrid/1/Introduce");
        xpath.addNamespace("servicens", "gme://gov.nih.nci.cagrid.introduce/1/Services");
        Attribute result = (Attribute) xpath.selectSingleNode(doc);
        String serviceName = null;
        if (result != null) {
            serviceName = result.getValue();
        }

        return serviceName;

    }
}
