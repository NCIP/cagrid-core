package org.cagrid.index.tests.steps;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.metadata.MetadataConstants;
import gov.nih.nci.cagrid.metadata.ServiceMetadata;
import gov.nih.nci.cagrid.metadata.ServiceMetadataHostingResearchCenter;
import gov.nih.nci.cagrid.metadata.service.ServicePointOfContactCollection;
import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.StringReader;

/**
 * SetMetadataHostingResearchCenterStep
 * Sets the service metadata document of a service
 * 
 * @author David
 */
public class SetMetadataHostingResearchCenterStep extends Step {
    public static final String INTRODUCE_SERVICEMETADATA_FILENAME = "serviceMetadata.xml";
    
    private File serviceDir = null;
    private StringBuffer researchCenterDocument = null;
    
    public SetMetadataHostingResearchCenterStep(File serviceDir, StringBuffer researchCenterDocument) {
        this.serviceDir = serviceDir;
        this.researchCenterDocument = researchCenterDocument;
    }
    

    public void runStep() throws Throwable {
        File serviceMetadataFile = new File(serviceDir, "etc" + File.separator + INTRODUCE_SERVICEMETADATA_FILENAME);
        assertTrue("Service metadata document not found", serviceMetadataFile.exists());
        
        ServiceMetadata serviceMetadata = null;
        try {
            FileReader metadataReader = new FileReader(serviceMetadataFile);
            serviceMetadata = (ServiceMetadata) Utils.deserializeObject(metadataReader, ServiceMetadata.class);
            metadataReader.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error deserializing service metadata: " + ex.getMessage());
        }
        
        ServiceMetadataHostingResearchCenter researchCenter = null;
        try {
            researchCenter = (ServiceMetadataHostingResearchCenter) Utils.deserializeObject(
                new StringReader(researchCenterDocument.toString()), ServiceMetadataHostingResearchCenter.class);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error deserializing testing research center metadata: " + ex.getMessage());
        }
        
        // overall service metadata
        serviceMetadata.setHostingResearchCenter(researchCenter);
        
        // specific service metadata
        ServicePointOfContactCollection servicePOC = new ServicePointOfContactCollection();
        servicePOC.setPointOfContact(researchCenter.getResearchCenter().getPointOfContactCollection().getPointOfContact());
        serviceMetadata.getServiceDescription().getService().setPointOfContactCollection(servicePOC);
        
        try {
            FileWriter metadataWriter = new FileWriter(serviceMetadataFile);
            Utils.serializeObject(serviceMetadata, MetadataConstants.CAGRID_MD_QNAME, metadataWriter);
            metadataWriter.flush();
            metadataWriter.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error writing edited service metadata: " + ex.getMessage());
        }
    }
}
