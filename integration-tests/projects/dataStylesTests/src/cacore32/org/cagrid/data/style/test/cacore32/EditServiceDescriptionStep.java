package org.cagrid.data.style.test.cacore32;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.data.DataServiceConstants;
import gov.nih.nci.cagrid.data.ExtensionDataUtils;
import gov.nih.nci.cagrid.data.extension.Data;
import gov.nih.nci.cagrid.introduce.IntroduceConstants;
import gov.nih.nci.cagrid.introduce.beans.ServiceDescription;
import gov.nih.nci.cagrid.introduce.beans.extension.ExtensionType;
import gov.nih.nci.cagrid.introduce.beans.resource.ResourcePropertyType;
import gov.nih.nci.cagrid.introduce.beans.service.ServiceType;
import gov.nih.nci.cagrid.introduce.common.CommonTools;
import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import org.cagrid.data.test.creation.DataTestCaseInfo;

public class EditServiceDescriptionStep extends Step {
    
    public static final String CLASS_MAPPINGS_PROPERTY = "dataService_classMappingsFilename";
    public static final String APPSERVICE_CONFIG_PROPERTY = "cqlQueryProcessorConfig_appserviceUrl";
    public static final String SDK_32_URL_SYSTEM_PROPETY = "sdk.32.application.url";
    
    // the query processor class name
    public static final String SDK_32_QUERY_PROCESSOR = "gov.nih.nci.cagrid.data.sdk32query.HQLCoreQueryProcessor";
    
    private DataTestCaseInfo testInfo = null;
    
    public EditServiceDescriptionStep(DataTestCaseInfo testInfo) {
        this.testInfo = testInfo;
    }
    

    public void runStep() throws Throwable {
        // load the Introduce service model
        File introduceXmlFile = new File(testInfo.getDir(), IntroduceConstants.INTRODUCE_XML_FILE);
        assertTrue("Service Model document " + IntroduceConstants.INTRODUCE_XML_FILE + " not found", introduceXmlFile.exists());
        assertTrue("Service Model document " + IntroduceConstants.INTRODUCE_XML_FILE + " could not be read", introduceXmlFile.canRead());
        FileReader introduceXmlReader = new FileReader(introduceXmlFile);
        ServiceDescription serviceDescription = (ServiceDescription) Utils.deserializeObject(introduceXmlReader, ServiceDescription.class);
        introduceXmlReader.close();
        
        // load the data services extension data
        File extensionDataFile = new File(Sdk32TestConstants.EXTENSION_DATA_DOCUMENT);
        FileReader extensionDataReader = new FileReader(extensionDataFile);
        Data extensionData = (Data) Utils.deserializeObject(extensionDataReader, Data.class);
        extensionDataReader.close();
        
        // insert the extension data
        boolean foundDataExtension = false;
        for (ExtensionType extension : serviceDescription.getExtensions().getExtension()) {
            if ("data".equals(extension.getName())) {
                ExtensionDataUtils.storeExtensionData(extension.getExtensionData(), extensionData);
                foundDataExtension = true;
                break;
            }
        }
        assertTrue("Data extension not found in service model", foundDataExtension);
        
        // set service properties
        CommonTools.setServiceProperty(serviceDescription, APPSERVICE_CONFIG_PROPERTY, getSdkUrl(), false);
        CommonTools.setServiceProperty(serviceDescription, CLASS_MAPPINGS_PROPERTY, Sdk32TestConstants.CLASS_TO_QNAME_FILENAME, false);
        CommonTools.setServiceProperty(serviceDescription, DataServiceConstants.QUERY_PROCESSOR_CLASS_PROPERTY, SDK_32_QUERY_PROCESSOR, false);
        
        // set up the domain model resource property
        ServiceType service = CommonTools.getService(serviceDescription.getServices(), testInfo.getName());
        for (ResourcePropertyType resourceProperty : service.getResourcePropertiesList().getResourceProperty()) {
            if (DataServiceConstants.DOMAIN_MODEL_QNAME.equals(resourceProperty.getQName())) {
                resourceProperty.setFileLocation(new File(Sdk32TestConstants.DOMAIN_MODEL_FILENAME).getAbsoluteFile().getName());
            }
        }
        
        // write the service model back out
        FileWriter introduceXmlWriter = new FileWriter(introduceXmlFile);
        Utils.serializeObject(serviceDescription, IntroduceConstants.INTRODUCE_SKELETON_QNAME, introduceXmlWriter);
        introduceXmlWriter.close();
    }
    
    
    private String getSdkUrl() {
        String url = System.getProperty(SDK_32_URL_SYSTEM_PROPETY);
        assertNotNull("System property " + SDK_32_URL_SYSTEM_PROPETY + " must be set to the SDK application service URL", url);
        return url;
    }
}
