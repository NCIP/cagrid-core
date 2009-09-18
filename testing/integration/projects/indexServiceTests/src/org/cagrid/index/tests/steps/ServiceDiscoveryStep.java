package org.cagrid.index.tests.steps;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.discovery.client.DiscoveryClient;
import gov.nih.nci.cagrid.metadata.MetadataUtils;
import gov.nih.nci.cagrid.metadata.ServiceMetadata;
import gov.nih.nci.cagrid.metadata.common.PointOfContact;
import gov.nih.nci.cagrid.metadata.dataservice.DomainModel;
import gov.nih.nci.cagrid.metadata.service.Operation;
import gov.nih.nci.cagrid.metadata.service.ServiceContext;
import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.io.File;
import java.io.FileReader;

import org.apache.axis.message.addressing.EndpointReferenceType;


/**
 * This step attempts to discover a service registered in an index service based
 * upon a locally cached metadata XML file.
 * 
 * @author Patrick McConnell
 * @author David Ervin
 */
public class ServiceDiscoveryStep extends Step {
    
    public static final String INTRODUCE_SERVICEMETADATA_FILENAME = "serviceMetadata.xml";
    
    private EndpointReferenceType indexServiceEndpoint = null;
    private EndpointReferenceType domainServiceEndpoint = null;
    private File targetServiceDir = null;
    private String domainServicePath = null;
    private boolean shouldBeFound = true;

    public ServiceDiscoveryStep(EndpointReferenceType indexServiceEndpoint,
        EndpointReferenceType domainServiceEndpoint, File targetServiceDir, boolean shouldBeFound) {
        super();
        this.shouldBeFound = shouldBeFound;
        this.indexServiceEndpoint = indexServiceEndpoint;
        this.domainServiceEndpoint = domainServiceEndpoint;
        this.targetServiceDir = targetServiceDir;
        this.domainServicePath = domainServiceEndpoint.getAddress().toString();
        
        String search = ":" + domainServiceEndpoint.getAddress().getPort() + "/";
        int index = this.domainServicePath.indexOf(search);
        this.domainServicePath = this.domainServicePath.substring(index + search.length());
    }


    public void runStep() throws Throwable {
        ServiceMetadata expectedMetadata = getExpectedServiceMetadata();
        
        DiscoveryClient client = new DiscoveryClient(this.indexServiceEndpoint);
        assertEquals(this.shouldBeFound, foundService(client.getAllServices(false)));
        assertEquals(this.shouldBeFound, foundService(client.getAllServices(true)));

        // service
        assertEquals(this.shouldBeFound, foundService(client.discoverServicesByName(
            expectedMetadata.getServiceDescription().getService().getName())));
        for (ServiceContext context : expectedMetadata.getServiceDescription().getService()
            .getServiceContextCollection().getServiceContext()) {
            for (Operation operation : context.getOperationCollection().getOperation()) {
                assertEquals(this.shouldBeFound, 
                    foundService(client.discoverServicesByOperationName(operation.getName())));
            }
        }

        // center
        assertEquals(this.shouldBeFound, foundService(client.discoverServicesByResearchCenter(
            expectedMetadata.getHostingResearchCenter().getResearchCenter().getShortName())));
        assertEquals(this.shouldBeFound, foundService(client.discoverServicesByResearchCenter(
            expectedMetadata.getHostingResearchCenter().getResearchCenter().getDisplayName())));
        for (PointOfContact poc : expectedMetadata.getHostingResearchCenter().getResearchCenter()
            .getPointOfContactCollection().getPointOfContact()) {
            assertEquals(this.shouldBeFound, foundService(client.discoverServicesByPointOfContact(poc)));
        }

        // domain model
        if (shouldBeFound && MetadataUtils.isDataService(domainServiceEndpoint)) {
            DomainModel model = MetadataUtils.getDomainModel(this.domainServiceEndpoint);
            assertEquals(this.shouldBeFound, foundService(client.discoverDataServicesByModelConceptCode(
                model.getExposedUMLClassCollection().getUMLClass()[0].getUmlAttributeCollection().
                getUMLAttribute(0).getSemanticMetadata(0).getConceptCode())));
        }
    }
    
    
    private ServiceMetadata getExpectedServiceMetadata() {
        File metadataDocument = new File(targetServiceDir, "etc" + File.separator + INTRODUCE_SERVICEMETADATA_FILENAME);
        assertTrue("Service metadata document (" + metadataDocument.getAbsolutePath() 
            + ") could not be found", metadataDocument.exists());
        ServiceMetadata metadata = null;
        try {
            FileReader reader = new FileReader(metadataDocument);
            metadata = (ServiceMetadata) Utils.deserializeObject(reader, ServiceMetadata.class);
            reader.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error deserializing metadata document: " + ex.getMessage());
        }
        return metadata;
    }


    private boolean foundService(EndpointReferenceType[] endpoints) {
        System.out.println("Looking for addresses ending with " + this.domainServicePath);
        boolean found = false;
        if (endpoints != null) {
            for (EndpointReferenceType endpoint : endpoints) {
                String address = endpoint.getAddress().toString();
                System.out.println("Checking address: " + address);
                if (address.endsWith(this.domainServicePath)) {
                    found = true;
                }
            }
        }
        return found;
    }
}
