package gov.nih.nci.cagrid.bdt.test.steps;

import gov.nih.nci.cagrid.bdt.test.unit.CreationTest;
import gov.nih.nci.cagrid.common.StreamGobbler;
import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.common.StreamGobbler.LogPriority;
import gov.nih.nci.cagrid.introduce.IntroduceConstants;
import gov.nih.nci.cagrid.introduce.beans.ServiceDescription;
import gov.nih.nci.cagrid.introduce.beans.extension.ExtensionType;
import gov.nih.nci.cagrid.introduce.beans.method.MethodType;
import gov.nih.nci.cagrid.introduce.beans.method.MethodTypeOutput;
import gov.nih.nci.cagrid.introduce.beans.service.ServiceType;
import gov.nih.nci.cagrid.introduce.common.AntTools;
import gov.nih.nci.cagrid.introduce.common.CommonTools;
import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.io.File;

import javax.xml.namespace.QName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * CreationStep 
 * Step to create a BDT service using the Introduce engine
 * 
 * @created Aug 22, 2006
 * @version $Id: CreationStep.java,v 1.15 2008-04-17 15:13:13 dervin Exp $
 */
public class CreationStep extends Step {
    
    private static final Log logger = LogFactory.getLog(CreationStep.class);
    
    private static final String BDT_START_RETURNS_CLIENT = "bdtStartReturnsClient";
    public static final String BDT_START_RETURNS_REFERENCE = "bdtStartReturnsReference";
   
    private String introduceDir;


    public CreationStep(String introduceDir) {
        super();
        this.introduceDir = introduceDir;
    }


    public void runStep() throws Throwable {
        System.out.println("Creating service...");
        String cmd = AntTools.getAntSkeletonCreationCommand(introduceDir, CreationTest.SERVICE_NAME,
            CreationTest.SERVICE_DIR, CreationTest.PACKAGE_NAME, CreationTest.SERVICE_NAMESPACE, IntroduceConstants.INTRODUCE_MAIN_RESOURCE + "," + IntroduceConstants.INTRODUCE_SINGLETON_RESOURCE, "bdt");
        Process p = CommonTools.createAndOutputProcess(cmd);
        new StreamGobbler(p.getInputStream(), StreamGobbler.TYPE_OUT, logger, LogPriority.DEBUG).start();
        new StreamGobbler(p.getErrorStream(), StreamGobbler.TYPE_ERR, logger, LogPriority.ERROR).start();
        p.waitFor();
        assertTrue("Creating new bdt service failed", p.exitValue() == 0);
        
        addBdtMethods();

        System.out.println("Invoking post creation processes...");
        cmd = AntTools.getAntSkeletonPostCreationCommand(introduceDir, CreationTest.SERVICE_NAME,
            CreationTest.SERVICE_DIR, CreationTest.PACKAGE_NAME, CreationTest.SERVICE_NAMESPACE, "bdt");
        p = CommonTools.createAndOutputProcess(cmd);
        new StreamGobbler(p.getInputStream(), StreamGobbler.TYPE_OUT, logger, LogPriority.DEBUG).start();
        new StreamGobbler(p.getErrorStream(), StreamGobbler.TYPE_ERR, logger, LogPriority.ERROR).start();
        p.waitFor();
        assertTrue("Service post creation process failed", p.exitValue() == 0);

        System.out.println("Building created service...");
        cmd = AntTools.getAntAllCommand(CreationTest.SERVICE_DIR);
        p = CommonTools.createAndOutputProcess(cmd);
        new StreamGobbler(p.getInputStream(), StreamGobbler.TYPE_OUT, logger, LogPriority.DEBUG).start();
        new StreamGobbler(p.getErrorStream(), StreamGobbler.TYPE_ERR, logger, LogPriority.ERROR).start();
        p.waitFor();
        assertTrue("Build process failed", p.exitValue() == 0);
    }
    
    
    private void addBdtMethods() throws Throwable {
        // verify the service model exists
        System.out.println("Verifying the service model file exists");
        File serviceModelFile = new File(CreationTest.SERVICE_DIR + File.separator
            + IntroduceConstants.INTRODUCE_XML_FILE);
        assertTrue("Service model file did not exist: " + serviceModelFile.getAbsolutePath(), 
            serviceModelFile.exists());
        assertTrue("Service model file cannot be read: " + serviceModelFile.getAbsolutePath(), 
            serviceModelFile.canRead());

        // deserialize the service model
        System.out.println("Deserializing service description from introduce.xml");
        ServiceDescription serviceDesc = (ServiceDescription) Utils.deserializeDocument(
            serviceModelFile.getAbsolutePath(), ServiceDescription.class);

        // get the extensions, verify BDT exists
        ExtensionType[] extensions = serviceDesc.getExtensions().getExtension();
        ExtensionType bdtExtension = null;
        for (int i = 0; i < extensions.length; i++) {
            if (extensions[i].getName().equals("bdt")) {
                bdtExtension = extensions[i];
                break;
            }
        }
        assertNotNull("BDT extension was not found in the service model", bdtExtension);
        
        addBdtMethodReturnsBdtReference(serviceDesc);
        addBdtMethodReturnsClientHandle(serviceDesc);
        
        // save the model back to disk for the post creation process
        Utils.serializeDocument(serviceModelFile.getAbsolutePath(), serviceDesc,
            IntroduceConstants.INTRODUCE_SKELETON_QNAME);
    }
    
    
    private void addBdtMethodReturnsBdtReference(ServiceDescription serviceDesc) throws Throwable {
        // create the BDT start method
        ServiceType mainService = serviceDesc.getServices().getService(0);
        MethodType bdtQueryMethod = new MethodType();
        bdtQueryMethod.setName(BDT_START_RETURNS_REFERENCE);
        bdtQueryMethod.setDescription("Starts a BDT operation and returns a BDT reference");
        // output of BDT client handle
        MethodTypeOutput bdtReferenceOutput = new MethodTypeOutput();
        QName referenceQname = new QName(
            "http://cagrid.nci.nih.gov/BulkDataHandlerReference",
            "BulkDataHandlerReference"
        );
        bdtReferenceOutput.setQName(referenceQname);
        bdtReferenceOutput.setIsArray(false);
        bdtQueryMethod.setOutput(bdtReferenceOutput);
        
        // add the method to the service
        CommonTools.addMethod(mainService, bdtQueryMethod);
    }
    
    
    private void addBdtMethodReturnsClientHandle(ServiceDescription serviceDesc) throws Throwable {
        // create the BDT start method
        ServiceType mainService = serviceDesc.getServices().getService(0);
        MethodType bdtQueryMethod = new MethodType();
        bdtQueryMethod.setName(BDT_START_RETURNS_CLIENT);
        bdtQueryMethod.setDescription("Starts a BDT operation and returns a client handle");
        // output of BDT client handle
        MethodTypeOutput bdtHandleOutput = new MethodTypeOutput();
        QName handleQname = new QName(
            mainService.getNamespace() + "BDT/types",
            mainService.getName() + "BulkDataHandlerReference"
        );
        bdtHandleOutput.setQName(handleQname);
        bdtHandleOutput.setIsArray(false);
        bdtHandleOutput.setIsClientHandle(Boolean.TRUE);
        bdtHandleOutput.setIsCreatingResourceForClientHandle(Boolean.TRUE);
        bdtHandleOutput.setResourceClientIntroduceServiceName(mainService.getName() + "BulkDataHandler");
        String clientHandleClass = mainService.getPackageName() 
            + ".bdt.client." + mainService.getName() + "BulkDataHandlerClient";
        bdtHandleOutput.setClientHandleClass(clientHandleClass);
        bdtQueryMethod.setOutput(bdtHandleOutput);
        
        // add the method to the service
        CommonTools.addMethod(mainService, bdtQueryMethod);
    }
}
