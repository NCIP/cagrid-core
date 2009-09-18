/*
 * Created on Jul 14, 2006
 */
package gov.nih.nci.cagrid.validator.steps.base;

import gov.nih.nci.cagrid.metadata.MetadataUtils;
import gov.nih.nci.cagrid.metadata.ServiceMetadata;
import gov.nih.nci.cagrid.testing.system.haste.Step;

import org.apache.axis.message.addressing.Address;
import org.apache.axis.message.addressing.EndpointReferenceType;


/**
 * This step connects to a caGrid service and pulls its service metadata
 * 
 * @author Rakesh Dhaval
 */
public class TestServiceMetaData extends Step {

    private String serviceURL;


    public TestServiceMetaData(String serviceURL) {
        this.serviceURL = serviceURL;
    }


    @Override
    public void runStep() throws Throwable {
        System.out.println("Checking Service @ " + serviceURL);
        // connect to the url and validate a return
        
        try {
            // TODO: maybe we can have checks in here for the various parts of the metadata?
            Address address = new Address(serviceURL);
            EndpointReferenceType serviceMetaDataEPR = new EndpointReferenceType(address);

            ServiceMetadata serviceMetaData = MetadataUtils.getServiceMetadata(serviceMetaDataEPR);
            System.out.println("   Service Name: "
                + serviceMetaData.getServiceDescription().getService().getName().toString());
            System.out.println("   POC: "
                + serviceMetaData.getServiceDescription().getService()
                    .getPointOfContactCollection().getPointOfContact(0).getFirstName()
                + " "
                + serviceMetaData.getServiceDescription().getService()
                    .getPointOfContactCollection().getPointOfContact(0).getLastName());
            System.out.println("   Hosting Research Center: "
                + serviceMetaData.getHostingResearchCenter().getResearchCenter().getDisplayName());
        } catch (Exception e) {
            e.printStackTrace();
            fail("Error getting resource property from " + serviceURL + " : " + e.getMessage());
        }
    }
}