/*
 * Created on Jun 13, 2006
 */
package gov.nci.nih.cagrid.tests.core.steps;

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

import org.apache.axis.message.addressing.EndpointReferenceType;


/**
 * This step attempts to discover a service registered in an index service based
 * upon a locally cached metadata XML file.
 * 
 * @author Patrick McConnell
 */
public class ServiceDiscoveryStep extends Step {
    private EndpointReferenceType indexServiceEndpoint;
    private EndpointReferenceType domainServiceEndpoint;
    private String domainServicePath;
    private ServiceMetadata metadata;
    private boolean shouldBeFound = true;


    public ServiceDiscoveryStep(EndpointReferenceType indexServiceEndpoint,
        EndpointReferenceType domainServiceEndpoint, File metadataFile, boolean shouldBeFound) throws Exception {
        super();

        this.shouldBeFound = shouldBeFound;
        this.indexServiceEndpoint = indexServiceEndpoint;
        this.metadata = (ServiceMetadata) Utils.deserializeDocument(metadataFile.toString(), ServiceMetadata.class);

        this.domainServiceEndpoint = domainServiceEndpoint;
        this.domainServicePath = domainServiceEndpoint.getAddress().toString();
        String search = ":" + domainServiceEndpoint.getAddress().getPort() + "/";
        int index = this.domainServicePath.indexOf(search);
        this.domainServicePath = this.domainServicePath.substring(index + search.length());
    }


    @Override
    public void runStep() throws Throwable {
        DiscoveryClient client = new DiscoveryClient(this.indexServiceEndpoint);
        assertEquals(this.shouldBeFound, foundService(client.getAllServices(false)));
        assertEquals(this.shouldBeFound, foundService(client.getAllServices(true)));

        // service
        assertEquals(this.shouldBeFound, foundService(client.discoverServicesByName(this.metadata
            .getServiceDescription().getService().getName())));
        for (ServiceContext context : this.metadata.getServiceDescription().getService().getServiceContextCollection()
            .getServiceContext()) {
            for (Operation operation : context.getOperationCollection().getOperation()) {
                assertEquals(this.shouldBeFound, foundService(client.discoverServicesByOperationName(operation
                    .getName())));
            }
        }

        // center
        assertEquals(this.shouldBeFound, foundService(client.discoverServicesByResearchCenter(this.metadata
            .getHostingResearchCenter().getResearchCenter().getShortName())));
        assertEquals(this.shouldBeFound, foundService(client.discoverServicesByResearchCenter(this.metadata
            .getHostingResearchCenter().getResearchCenter().getDisplayName())));
        for (PointOfContact poc : this.metadata.getHostingResearchCenter().getResearchCenter()
            .getPointOfContactCollection().getPointOfContact()) {
            assertEquals(this.shouldBeFound, foundService(client.discoverServicesByPointOfContact(poc)));
        }

        // model
        if (shouldBeFound) {
            DomainModel model = MetadataUtils.getDomainModel(this.domainServiceEndpoint);
            assertEquals(this.shouldBeFound, foundService(client.discoverDataServicesByModelConceptCode(model
                .getExposedUMLClassCollection().getUMLClass()[0].getUmlAttributeCollection().getUMLAttribute(0)
                .getSemanticMetadata(0).getConceptCode())));
        }
    }


    private boolean foundService(EndpointReferenceType[] endpoints) {
        if (endpoints == null) {
            return false;
        }

        for (EndpointReferenceType endpoint : endpoints) {
            if (endpoint.getAddress().toString().endsWith(this.domainServicePath)) {
                return true;
            }
        }
        return false;
    }

}
