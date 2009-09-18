package org.cagrid.data.style.test.cacore32;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.introduce.IntroduceConstants;
import gov.nih.nci.cagrid.introduce.beans.ServiceDescription;
import gov.nih.nci.cagrid.introduce.beans.namespace.NamespaceType;
import gov.nih.nci.cagrid.introduce.beans.namespace.SchemaElementType;
import gov.nih.nci.cagrid.introduce.common.CommonTools;
import gov.nih.nci.cagrid.introduce.common.FileFilters;
import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import org.cagrid.data.test.creation.DataTestCaseInfo;

public class AddCabioSchemaStep extends Step {
    
    private static final String CABIO_PACKAGE_NAME = "gov.nih.nci.cabio.domain";
    private static final String SDK_32_SERIALIZER = "gov.nih.nci.cagrid.data.style.cacore32.encoding.SDK32SerializerFactory";
    private static final String SDK_32_DESERIALIZER = "gov.nih.nci.cagrid.data.style.cacore32.encoding.SDK32DeserializerFactory";
    
    private DataTestCaseInfo testInfo = null;
    private File sdkPackageDir = null;
    
    public AddCabioSchemaStep(DataTestCaseInfo testInfo, File sdkPackageDir) {
        this.testInfo = testInfo;
        this.sdkPackageDir = sdkPackageDir;
    }
    

    public void runStep() throws Throwable {
        // locate the schema file and copy it in to the service
        File serviceSchemaDir = new File(testInfo.getDir(), "schema" + File.separator + testInfo.getName());
        File sdkSchemaDir = new File(sdkPackageDir, "sdk3.2.1" + File.separator + "client" + File.separator + "conf");
        File[] schemas = sdkSchemaDir.listFiles(new FileFilters.XSDFileFilter());
        assertEquals("Unexpected number of schemas found in sdk package", 1, schemas.length);
        File schemaOut = new File(serviceSchemaDir, schemas[0].getName());
        Utils.copyFile(schemas[0], schemaOut);
        
        // get the service model
        File introduceXmlFile = new File(testInfo.getDir(), IntroduceConstants.INTRODUCE_XML_FILE);
        assertTrue("Service Model document " + IntroduceConstants.INTRODUCE_XML_FILE + " not found", introduceXmlFile.exists());
        assertTrue("Service Model document " + IntroduceConstants.INTRODUCE_XML_FILE + " could not be read", introduceXmlFile.canRead());
        FileReader introduceXmlReader = new FileReader(introduceXmlFile);
        ServiceDescription serviceDescription = (ServiceDescription) Utils.deserializeObject(introduceXmlReader, ServiceDescription.class);
        introduceXmlReader.close();
        
        // create the namespace type
        NamespaceType cabioNsType = CommonTools.createNamespaceType(schemas[0].getAbsolutePath(), serviceSchemaDir);
        cabioNsType.setLocation("./" + schemas[0].getName());
        
        // set the serialization of the types in the new namespace
        for (SchemaElementType type : cabioNsType.getSchemaElement()) {
            type.setSerializer(SDK_32_SERIALIZER);
            type.setDeserializer(SDK_32_DESERIALIZER);
            type.setClassName(type.getType());
        }
        cabioNsType.setGenerateStubs(Boolean.FALSE);
        cabioNsType.setPackageName(CABIO_PACKAGE_NAME);
        
        // add the namespace type to the service model
        CommonTools.addNamespace(serviceDescription, cabioNsType);
        
        // write the service model back out
        FileWriter introduceXmlWriter = new FileWriter(introduceXmlFile);
        Utils.serializeObject(serviceDescription, IntroduceConstants.INTRODUCE_SKELETON_QNAME, introduceXmlWriter);
        introduceXmlWriter.close();
    }
}
