package gov.nci.nih.cagrid.tests.core.steps;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.metadata.ServiceMetadata;
import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.io.File;


public class AddBasicServiceMetadata extends Step {

    File generatedServiceDir = null;
    File goldMetadataFile = null;


    public AddBasicServiceMetadata(File generatedServiceDir, File goldMetadataFile) {
        this.generatedServiceDir = generatedServiceDir;
        this.goldMetadataFile = goldMetadataFile;
    }


    @Override
    public void runStep() throws Throwable {
        ServiceMetadata goldMetadata = (ServiceMetadata) Utils.deserializeDocument(goldMetadataFile.getAbsolutePath(),
            ServiceMetadata.class);
        ServiceMetadata serviceMetadata = (ServiceMetadata) Utils.deserializeDocument(generatedServiceDir
            .getAbsolutePath()
            + File.separator + "etc" + File.separator + "serviceMetadata.xml", ServiceMetadata.class);

        serviceMetadata.setHostingResearchCenter(goldMetadata.getHostingResearchCenter());
        serviceMetadata.getServiceDescription().getService().setPointOfContactCollection(
            goldMetadata.getServiceDescription().getService().getPointOfContactCollection());
        
        Utils.serializeDocument(generatedServiceDir
            .getAbsolutePath()
            + File.separator + "etc" + File.separator + "serviceMetadata.xml", serviceMetadata, serviceMetadata.getTypeDesc().getXmlType());

    }

}
