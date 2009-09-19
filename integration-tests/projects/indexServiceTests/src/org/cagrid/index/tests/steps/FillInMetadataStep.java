package org.cagrid.index.tests.steps;

import gov.nih.nci.cagrid.metadata.MetadataUtils;
import gov.nih.nci.cagrid.metadata.ServiceMetadata;
import gov.nih.nci.cagrid.metadata.service.Service;
import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class FillInMetadataStep extends Step {
    File serviceDir = null;

    public FillInMetadataStep(File serviceDir) {
        this.serviceDir = serviceDir;
    }


    public void runStep() throws Throwable {
        File metadataFile = new File(serviceDir, "etc" + File.separator + "serviceMetadata.xml");
        FileReader metadataReader = new FileReader(metadataFile);
        ServiceMetadata metadata = MetadataUtils.deserializeServiceMetadata(metadataReader);
        Service service = metadata.getServiceDescription().getService();
        if (service.getDescription() == null || service.getDescription().length() == 0) {
            service.setDescription(service.getName() + " Description");
        }
        metadataReader.close();
        FileWriter writer = new FileWriter(metadataFile);
        MetadataUtils.serializeServiceMetadata(metadata, writer);
        writer.flush();
        writer.close();
        
        /*
        // get service descriptor
        ServiceInformation serviceInfo = null;
        try {
            serviceInfo = new ServiceInformation(serviceDir);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error loading service descriptor document: " + ex.getMessage());
        }
        
        // ensure every service has a description
        ServicesType services = serviceInfo.getServiceDescriptor().getServices();
        if (services != null && services.getService() != null) {
            for (ServiceType service : services.getService()) {
                if (service.getDescription() == null || service.getDescription().length() == 0) {
                    service.setDescription(service.getName() + " Description");
                }
                // method descriptions too
                if (service.getMethods().getMethod() != null) {
                    for (MethodType method : service.getMethods().getMethod()) {
                        if (method.getDescription() == null || method.getDescription().length() == 0) {
                            method.setDescription(service.getName() + "." + method.getName() + " Description");
                        }
                        // inputs
                        if (method.getInputs().getInput() != null) {
                            for (MethodTypeInputsInput input : method.getInputs().getInput()) {
                                if (input.getDescription() == null || input.getDescription().length() == 0) {
                                    input.setDescription(service.getName() + "." 
                                        + method.getName() + "(" + input.getName() + ") Description");
                                }
                            }
                        }
                    }
                }
            }
        } else {
            fail("No services found in service descriptor");
        }
        
        // write the document back out
        try {
            FileWriter writer = new FileWriter(new File(serviceDir, IntroduceConstants.INTRODUCE_XML_FILE));
            Utils.serializeObject(serviceInfo.getServiceDescriptor(), IntroduceConstants.INTRODUCE_SKELETON_QNAME, writer);
            writer.flush();
            writer.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error writing service descriptor document: " + ex.getMessage());
        }
        */
    }
}
