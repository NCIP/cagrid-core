package org.cagrid.mms.test.system.steps;

import gov.nih.nci.cagrid.metadata.MetadataUtils;
import gov.nih.nci.cagrid.metadata.ServiceMetadata;
import gov.nih.nci.cagrid.metadata.exceptions.InvalidResourcePropertyException;
import gov.nih.nci.cagrid.metadata.exceptions.ResourcePropertyRetrievalException;
import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.rmi.RemoteException;

import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.axis.types.URI.MalformedURIException;
import org.cagrid.mms.client.MetadataModelServiceClient;
import org.cagrid.mms.domain.ModelSourceMetadata;
import org.cagrid.mms.domain.SourceDescriptor;


public class CheckMetadataStep extends Step {

    private final EndpointReferenceType mmsEPR;


    public CheckMetadataStep(EndpointReferenceType mmsEPR) {
        this.mmsEPR = mmsEPR;
    }


    @Override
    public void runStep() throws RemoteException {
        assertNotNull("A non-null EPR must be passed in.", this.mmsEPR);

        // check standard metadata
        checkStandardMetadata("MetadataModelService");

        // check model source metadata
        MetadataModelServiceClient mms = null;
        try {

            mms = new MetadataModelServiceClient(this.mmsEPR);
        } catch (MalformedURIException e) {
            fail("Bad EPR (" + this.mmsEPR + "):" + e.getMessage());
        }

        ModelSourceMetadata sourceMD = mms.getModelSourceMetadata();

        assertNotNull(sourceMD);
        assertNotNull(sourceMD.getDefaultSourceIdentifier());
        assertNotNull(sourceMD.getSupportedModelSources());
        assertTrue("Supported sources are expected to be non-zero.",
            sourceMD.getSupportedModelSources().getSource().length > 0);

        boolean defaultFound = false;
        String sourceList = "";
        for (SourceDescriptor sd : sourceMD.getSupportedModelSources().getSource()) {
            sourceList += sd.getIdentifier() + " ";
            if (sd.getIdentifier().equals(sourceMD.getDefaultSourceIdentifier())) {
                defaultFound = true;
                break;
            }
        }
        if (!defaultFound) {
            fail("Default source identifier (" + sourceMD.getDefaultSourceIdentifier() + ") not found in source list ("
                + sourceList + ")");
        }

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
