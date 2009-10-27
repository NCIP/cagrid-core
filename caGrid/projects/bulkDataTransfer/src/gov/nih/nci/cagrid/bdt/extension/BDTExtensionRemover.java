package gov.nih.nci.cagrid.bdt.extension;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.introduce.beans.extension.ServiceExtensionDescriptionType;
import gov.nih.nci.cagrid.introduce.beans.method.MethodType;
import gov.nih.nci.cagrid.introduce.beans.method.MethodTypeOutput;
import gov.nih.nci.cagrid.introduce.beans.service.ServiceType;
import gov.nih.nci.cagrid.introduce.codegen.services.methods.SyncHelper;
import gov.nih.nci.cagrid.introduce.common.CommonTools;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;
import gov.nih.nci.cagrid.introduce.extension.ExtensionRemovalException;
import gov.nih.nci.cagrid.introduce.extension.ServiceExtensionRemover;

import java.io.File;
import java.io.IOException;

import javax.xml.namespace.QName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Removes the BDT extension from a service
 * 
 * @author David
 */
public class BDTExtensionRemover implements ServiceExtensionRemover {
    
    public static final QName BDT_HANDLER_REFERENCE_QNAME = new QName(
        "http://cagrid.nci.nih.gov/BulkDataHandlerReference", "BulkDataHandlerReference");
    
    private static Log LOG = LogFactory.getLog(BDTExtensionRemover.class);

    public void remove(ServiceExtensionDescriptionType extDesc, ServiceInformation info) throws ExtensionRemovalException {
        // throw away BDTResource.java
        removeBdtResourceClass(info);
        // find any methods that return a BDT handle, comment out the impl (NOT blank it...) and just throw not implemented exception
        for (ServiceType service : info.getServices().getService()) {
            if (service.getMethods() != null && service.getMethods().getMethod() != null) {
                for (MethodType method : service.getMethods().getMethod()) {
                    if (methodReturnsBdtHandle(service, method)) {
                        LOG.debug("Method " + method.getName() + " of service " + service.getName() 
                            + " returns a BDT handle and will have it's implementation disabled");
                        unimplementBdtMethod(info, service, method);
                    }
                }
            }
        }
        // rm BulkDataHandler.wsdl, BulkDataHandlerReference.xsd, BulkDataTransferServiceMetadata.xsd -- also rm those from ServiceInformation
    }
    
    
    private void removeBdtResourceClass(ServiceInformation info) {
        File resourceClassFile = new File(info.getBaseDirectory(),
            "src" + File.separator + CommonTools.getPackageDir(info.getServices().getService(0))
            + File.separator + "service" + File.separator + "BDTResource.java");
        LOG.debug("Deleting BDT Resource class " + resourceClassFile.getAbsolutePath());
        resourceClassFile.delete();
    }
    
    
    private boolean methodReturnsBdtHandle(ServiceType service, MethodType method) {
        MethodTypeOutput methodOutput = method.getOutput();
        if (methodOutput != null) {
            if (methodOutput.getIsClientHandle() != null && methodOutput.getIsClientHandle().booleanValue()) {
                // method returns a client handle
                String specificServiceClientHandleClass = service.getPackageName() + ".bdt.client." + service.getName()
                    + "BulkDataHandlerClient";
                return methodOutput.getClientHandleClass().equals(specificServiceClientHandleClass);
            } else {
                // check the output QName
                QName outputName = methodOutput.getQName();
                return outputName.getLocalPart().equals(service.getName() + "BulkDataHandlerReference")
                    || (BDT_HANDLER_REFERENCE_QNAME.getLocalPart().equals(outputName.getLocalPart()) 
                        && BDT_HANDLER_REFERENCE_QNAME.getNamespaceURI().equals(outputName.getNamespaceURI()));
            }
        }
        return false;
    }
    
    
    private void unimplementBdtMethod(ServiceInformation info, ServiceType service, MethodType method) throws ExtensionRemovalException {
        File implSourceFile = new File(info.getBaseDirectory(), "src" + File.separator
            + CommonTools.getPackageDir(service) + File.separator 
            + "service" + File.separator + service.getName() + "Impl.java");
        LOG.debug("Editing source file: " + implSourceFile.getAbsolutePath());

        StringBuffer fileContent = null;
        try {
            fileContent = Utils.fileToStringBuffer(implSourceFile);
        } catch (IOException ex) {
            throw new ExtensionRemovalException(
                "Error loading service implementaton: " + ex.getMessage(), ex);
        }

        // comment out the implementation of that method
        String methodSignature = SyncHelper.createUnBoxedSignatureStringFromMethod(method, info);
        LOG.debug("Looking for method signature " + methodSignature);
        int startOfMethod = SyncHelper.startOfSignature(fileContent, methodSignature);
        int endOfMethod = SyncHelper.bracketMatch(fileContent, startOfMethod);
        // verify the method exists
        if (startOfMethod == -1 || endOfMethod == -1) {
            throw new ExtensionRemovalException("WARNING: Unable to locate method in Impl : " + method.getName());
        }
        // find the start of the method body
        startOfMethod = fileContent.indexOf("{", startOfMethod);
        // comment out whatever implementation is there
        fileContent.insert(startOfMethod, "/*");
        int insertIndex = endOfMethod + 2;
        insertIndex = insertString(fileContent, insertIndex, "*/");
        
        // insert a comment and throw a not yet implemented exception
        insertIndex = insertString(fileContent, insertIndex, "// BDT has been removed from caGrid 1.4\n");
        insertIndex = insertString(fileContent, insertIndex, "throw new java.rmi.RemoteException(\"BDT has been removed from caGrid 1.4\")\n");
        
        // write out the edited service implementation
        try {
            Utils.stringBufferToFile(fileContent, implSourceFile.getAbsolutePath());
        } catch (IOException ex) {
            throw new ExtensionRemovalException(
                "Error saving service implementation: " + ex.getMessage(), ex);
        }
    }
    
    
    private int insertString(StringBuffer buff, int index, String s) {
        buff.insert(index, s);
        return index + s.length();
    }
}
