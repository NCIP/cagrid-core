/*
 * Created on Apr 12, 2006
 */
package gov.nci.nih.cagrid.tests.core.steps;

import gov.nci.nih.cagrid.tests.core.util.FileUtils;
import gov.nci.nih.cagrid.tests.core.util.GlobusHelper;
import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.io.File;

import org.globus.common.CoGProperties;


/**
 * This step copies the testing credentials and security descriptor to globus
 * locataion created by GlobusHelper (create Globus should be called first!),
 * and configures the security descriptor on GlobusHelper to use it.
 * 
 * @author Scott Oster
 */
public class GlobusInstallSecurityDescriptorStep extends Step {
    private GlobusHelper globus;


    public GlobusInstallSecurityDescriptorStep(GlobusHelper globus) {
        super();
        this.globus = globus;
    }


    @Override
    public void runStep() throws Throwable {
        if (this.globus.getTempGlobusLocation() == null) {
            fail("GlobusHelper has not created a temporary Globus yet!  Call createTempGlobus() first!");
        }

        // copy the certs and security descriptor to the temp globus
        File certsDir = new File("test" + File.separator + "resources" + File.separator + "certificates");
        FileUtils.copyRecursive(certsDir, this.globus.getTempGlobusLocation(), null);

        // TOOD: how can we use a different location?
        // copy the cert's CA cert to the trusted location
        String caDir = CoGProperties.getDefault().getCaCertLocations();
        FileUtils.copy(new File(certsDir, "cagrid-training-CA.0"), new File(caDir));

        // tell globus to use this descriptor
        this.globus.setSecurityDescriptor(new File(this.globus.getTempGlobusLocation(), "security-descriptor.xml"));

    }
}