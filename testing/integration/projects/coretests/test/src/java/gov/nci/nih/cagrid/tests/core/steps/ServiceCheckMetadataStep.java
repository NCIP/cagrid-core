/*
 * Created on Jun 5, 2006
 */
package gov.nci.nih.cagrid.tests.core.steps;

import gov.nci.nih.cagrid.tests.core.compare.BeanComparator;
import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.metadata.MetadataUtils;
import gov.nih.nci.cagrid.metadata.ServiceMetadata;
import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.axis.message.addressing.Address;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.axis.types.URI.MalformedURIException;


/**
 * This step retrieves the service-level metadata from a running service and
 * compares it to a locally cached service metadata XML file using a
 * BeanComparator.
 * 
 * @author Patrick McConnell
 */
public class ServiceCheckMetadataStep extends Step {
    private EndpointReferenceType endpoint;
    private File localMetadata;


    public ServiceCheckMetadataStep(EndpointReferenceType endpoint, File localMetadata) {
        super();

        this.endpoint = endpoint;
        this.localMetadata = localMetadata;
    }


    public void runStep() throws Throwable {
        ServiceMetadata serviceMetadata = MetadataUtils.getServiceMetadata(endpoint);
        ServiceMetadata localMetadata = (ServiceMetadata) Utils.deserializeDocument(this.localMetadata.toString(),
            ServiceMetadata.class);

        List<String> ignoreMethods= new ArrayList<String>();
        //ignore the caDSR materialized view ids, which change
        ignoreMethods.add("getId");
        new BeanComparator(this,ignoreMethods).assertEquals(localMetadata, serviceMetadata);
    }


    public static void main(String[] args) throws Throwable {
        int port = Integer.parseInt(System.getProperty("test.globus.port", "8080"));

        EndpointReferenceType endpoint;
        try {
            endpoint = new EndpointReferenceType(new Address("http://localhost:" + port
                + "/wsrf/services/cagrid/BasicAnalyticalService"));
        } catch (MalformedURIException e) {
            throw new IllegalArgumentException("endpoint badly formed");
        }
        File metadataFile = new File(System.getProperty("GlobusHelperTest.file", "test" + File.separator + "data"
            + File.separator + "serviceMetadata.xml"));
        ServiceCheckMetadataStep step = new ServiceCheckMetadataStep(endpoint, metadataFile);
        step.runStep();
    }

}
