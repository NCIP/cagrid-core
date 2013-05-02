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
package gov.nih.nci.cagrid.introduce.upgrade.common;

import gov.nih.nci.cagrid.common.XMLUtilities;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.xpath.XPath;


public class UpgradeUtilities {

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
