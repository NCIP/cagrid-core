package org.cagrid.gts.test.system.steps;

import gov.nih.nci.cagrid.metadata.MetadataUtils;
import gov.nih.nci.cagrid.metadata.ServiceMetadata;
import gov.nih.nci.cagrid.metadata.exceptions.InvalidResourcePropertyException;
import gov.nih.nci.cagrid.metadata.exceptions.ResourcePropertyRetrievalException;
import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.rmi.RemoteException;

import org.apache.axis.message.addressing.EndpointReferenceType;


public class CheckMetadataStep extends Step {

    private final EndpointReferenceType mmsEPR;


    public CheckMetadataStep(EndpointReferenceType mmsEPR) {
        this.mmsEPR = mmsEPR;
    }


    @Override
    public void runStep() throws RemoteException {
        assertNotNull("A non-null EPR must be passed in.", this.mmsEPR);

        // check standard metadata
        checkStandardMetadata("GTS");

    }


    private void checkStandardMetadata(String serviceName) {
        ServiceMetadata serviceMetadata = null;
        try {

            serviceMetadata = MetadataUtils.getServiceMetadata(mmsEPR);
        } catch (InvalidResourcePropertyException e) {
            fail("MMS didn't expose the standard ServiceMetadata:" + e.getMessage());
            e.printStackTrace();
        } catch (ResourcePropertyRetrievalException e) {
            fail("Problem accessing the standard ServiceMetadata:" + e.getMessage());
            e.printStackTrace();
        }

        assertNotNull(serviceMetadata);
        assertNotNull(serviceMetadata.getServiceDescription());
        assertNotNull(serviceMetadata.getServiceDescription().getService());
        assertEquals(serviceName, serviceMetadata.getServiceDescription().getService().getName());
    }
}
