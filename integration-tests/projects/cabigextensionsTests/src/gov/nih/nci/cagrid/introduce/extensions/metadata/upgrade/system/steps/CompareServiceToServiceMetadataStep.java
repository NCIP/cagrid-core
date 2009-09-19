package gov.nih.nci.cagrid.introduce.extensions.metadata.upgrade.system.steps;

import gov.nih.nci.cagrid.introduce.beans.service.ServicesType;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;
import gov.nih.nci.cagrid.metadata.MetadataUtils;
import gov.nih.nci.cagrid.metadata.ServiceMetadata;
import gov.nih.nci.cagrid.metadata.service.Service;

import java.io.File;
import java.io.FileReader;


/**
 * CompareServiceMetadataStep Compares every description tag is the same between
 * two metadata documents (this implicitly also verifies things such as the
 * number of services, operations, inputs, outputs, and faults are the same)
 * 
 * @author oster
 * @created Apr 11, 2007 3:38:47 PM
 * @version $Id: multiscaleEclipseCodeTemplates.xml,v 1.1 2007/03/02 14:35:01
 *          dervin Exp $
 */
public class CompareServiceToServiceMetadataStep extends XPathValidatingStep {
    protected File serviceDir;
    protected File serviceMetadata;


    public CompareServiceToServiceMetadataStep(File introduceServiceDir, File serviceMetadata) throws Exception {
        this.serviceDir = introduceServiceDir;
        this.serviceMetadata = serviceMetadata;
    }


    @Override
    public void runStep() throws Throwable {
        assertTrue("Introduce service directory (" + this.serviceDir + ") does not exist", 
            this.serviceDir.exists());
        assertTrue("Service Metadata file (" + this.serviceMetadata + ") does not exist", this.serviceMetadata.exists());

        ServiceMetadata metadata = MetadataUtils.deserializeServiceMetadata(new FileReader(this.serviceMetadata));
        ServiceInformation introduceService = new ServiceInformation(this.serviceDir);

        ServicesType duceServices = introduceService.getServiceDescriptor().getServices();
        Service metadataService = metadata.getServiceDescription().getService();

        // make sure all the descriptions are the same
        assertServicesEqual(duceServices, metadataService);
    }


    private void assertServicesEqual(ServicesType duceServices, Service metadataService) {
        // check that the services names are the same
        assertEquals(duceServices.getService()[0].getName(), metadataService.getName());

        System.out.println("Comparing method names:");
        assertStringIteratorsEqual(createIterator(duceServices, "/service/methods/method/@name"), createIterator(
            metadataService, "/serviceContextCollection/serviceContext/operationCollection/operation/@name"));

        System.out.println("Comparing method descriptions:");
        assertStringIteratorsEqual(createIterator(duceServices, "/service/methods/method/@description"),
            createIterator(metadataService,
                "/serviceContextCollection/serviceContext/operationCollection/operation/@description"));

        System.out.println("Comparing method descriptions:");
        assertStringIteratorsEqual(createIterator(duceServices, "/service/methods/method/@description"),
            createIterator(metadataService,
                "/serviceContextCollection/serviceContext/operationCollection/operation/@description"));

        // TODO: doesn't work because EVS model name change and anotation fails
        // System.out.println("Comparing method input descriptions:");
        // // check the method descriptions
        // assertStringIteratorsEqual(
        // createIterator(duceServices,
        // "/service/methods/method/inputs/input/@description"),
        // createIterator(
        // metadataService,
        // "/serviceContextCollection/serviceContext/operationCollection/operation/inputParameterCollection/inputParameter/uMLClass/@description"));

        // System.out.println("Comparing method output descriptions:");
        // // check the method descriptions
        // assertStringIteratorsEqual(
        // createIterator(duceServices,
        // "/service/methods/method/output/@description"),
        // createIterator(
        // metadataService,
        // "/serviceContextCollection/serviceContext/operationCollection/operation/output/uMLClass/@description"));

        System.out.println("Comparing method fault descriptions:");
        assertStringIteratorsEqual(
            createIterator(duceServices, "/service/methods/method/exceptions/exception/@description"),
            createIterator(metadataService,
                "/serviceContextCollection/serviceContext/operationCollection/operation/faultCollection/fault/@description"));

        System.out.println("Comparing metadata  descriptions:");
        assertStringIteratorsEqual(createIterator(duceServices,
            "/service/resourcePropertiesList/resourceProperty/@description"), createIterator(metadataService,
            "/serviceContextCollection/serviceContext/contextPropertyCollection/contextProperty/@description"));
    }



}