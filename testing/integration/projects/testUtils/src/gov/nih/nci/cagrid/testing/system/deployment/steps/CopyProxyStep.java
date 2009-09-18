package gov.nih.nci.cagrid.testing.system.deployment.steps;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.testing.system.deployment.SecureContainer;
import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.io.File;

/**
 * CopyProxyStep
 * Copies proxies to the service
 * 
 * @autho Hastings
 * @author David
 */
public class CopyProxyStep extends Step {
    private File serviceDir;
    private SecureContainer container;


    public CopyProxyStep(SecureContainer container, File serviceDir) {
        this.container = container;
        this.serviceDir = serviceDir;
    }


    public void runStep() throws Throwable {
        System.out.println("Copying user proxys to services dir");

        File inFileClient = new File(container.getCertificatesDirectory().getAbsolutePath() + File.separator + "user.proxy");
        File outFileClient = new File(serviceDir, "user.proxy");
        Utils.copyFile(inFileClient, outFileClient);
        
        inFileClient = new File(container.getCertificatesDirectory().getAbsolutePath() + File.separator + "user2.proxy");
        outFileClient = new File(serviceDir, "user2.proxy");
        Utils.copyFile(inFileClient, outFileClient);
        
        inFileClient = new File(container.getCertificatesDirectory().getAbsolutePath() + File.separator + "user3.proxy");
        outFileClient = new File(serviceDir, "user3.proxy");
        Utils.copyFile(inFileClient, outFileClient);
    }

}
