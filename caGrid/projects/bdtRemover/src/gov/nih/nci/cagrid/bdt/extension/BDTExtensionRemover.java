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
import java.io.InputStream;
import java.io.StringReader;
import java.util.Iterator;

import javax.xml.namespace.QName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ws.jaxme.js.JavaMethod;
import org.apache.ws.jaxme.js.JavaSource;
import org.apache.ws.jaxme.js.JavaSourceFactory;
import org.apache.ws.jaxme.js.util.JavaParser;

/**
 * Removes the BDT extension from a service
 * 
 * @author David
 */
public class BDTExtensionRemover implements ServiceExtensionRemover {
    
    public static final QName BDT_HANDLER_REFERENCE_QNAME = new QName(
        "http://cagrid.nci.nih.gov/BulkDataHandlerReference", "BulkDataHandlerReference");
    public static final String BDT_RESOURCE_PACKAGE_PLACEHOLDER = "@resource.package@";
    
    private static Log LOG = LogFactory.getLog(BDTExtensionRemover.class);

    public void remove(ServiceExtensionDescriptionType extDesc, ServiceInformation info) throws ExtensionRemovalException {
        // find any methods that return a BDT handle, comment out the impl (NOT blank it...) and just throw not implemented exception
        for (ServiceType service : info.getServices().getService()) {
            if (service.getMethods() != null && service.getMethods().getMethod() != null) {
                for (MethodType method : service.getMethods().getMethod()) {
                    if (methodReturnsBdtHandle(service, method)) {
                        if (!method.isIsProvided()) {
                            LOG.debug("Method " + method.getName() + " of service " + service.getName() 
                                + " returns a BDT handle and will have it's implementation disabled");
                            unimplementBdtMethod(info, service, method);
                        } else {
                            LOG.debug("Method " + method.getName() + " of service " + service.getName()
                                + " returns a BDT handle, but is provided and so will not be edited");
                        }
                    }
                }
            }
        }
        // can't just throw away the BDT resource since we also can't remove the service context
        // also can't rm BulkDataHandler.wsdl, BulkDataHandlerReference.xsd, BulkDataTransferServiceMetadata.xsd since they
        // might be used throughout the service, and cleaning all that up is basically impossible
        unimplementBdtResourceClass(info);
    }
    
    
    private void unimplementBdtResourceClass(ServiceInformation info) throws ExtensionRemovalException {
        // move the old resource class to a .old copy
        File resourceClassFile = new File(info.getBaseDirectory(),
            "src" + File.separator + CommonTools.getPackageDir(info.getServices().getService(0))
            + File.separator + "service" + File.separator + "BDTResource.java");
        File movedResourceClassFile = new File(resourceClassFile.getParentFile(), "BDTResource.java.old");
        LOG.debug("Moving BDT Resource class " + resourceClassFile.getAbsolutePath() + " to " + movedResourceClassFile.getAbsolutePath());
        try {
            Utils.copyFile(resourceClassFile, movedResourceClassFile);
        } catch (IOException ex) {
            throw new ExtensionRemovalException("Error moving BDT Resource class: " + ex.getMessage());
        }
        // load up the replacement class
        InputStream replacementStream = getClass().getResourceAsStream("/BDTResourceReplacement.java.template");
        try {
            StringBuffer replacementText = Utils.inputStreamToStringBuffer(replacementStream);
            String resourcePackageName = info.getServices().getService(0).getPackageName() + ".service";
            LOG.debug("Setting BDT Resource package name to " + resourcePackageName);
            StringBuffer complete = new StringBuffer(replacementText.toString()
                .replace(BDT_RESOURCE_PACKAGE_PLACEHOLDER, resourcePackageName));
            Utils.stringBufferToFile(complete, resourceClassFile);
            LOG.debug("Replaced BDT Resource class");
        } catch (IOException ex) {
            throw new ExtensionRemovalException("Error creating replacement BDT Resource class: " + ex.getMessage());
        }
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
        String implClassName = service.getName() + "Impl";
        File implSourceFile = new File(info.getBaseDirectory(), "src" + File.separator
            + CommonTools.getPackageDir(service) + File.separator 
            + "service" + File.separator + implClassName + ".java");
        LOG.debug("Editing source file: " + implSourceFile.getAbsolutePath());

        StringBuffer fileContent = null;
        try {
            fileContent = Utils.fileToStringBuffer(implSourceFile);
        } catch (IOException ex) {
            throw new ExtensionRemovalException(
                "Error loading service implementaton: " + ex.getMessage(), ex);
        }

        // find the method in the source code using Jaxme
        JavaSource implSource = null;
        JavaSourceFactory sourceFactory = new JavaSourceFactory();
        JavaParser sourceParser = new JavaParser(sourceFactory);
        try {
            sourceParser.parse(new StringReader(fileContent.toString()));
        } catch (Exception ex) {
            throw new ExtensionRemovalException("Error parsing source file " 
                + implSourceFile.getAbsolutePath() + ": " + ex.getMessage(), ex);
        }
        Iterator<?> implIter = sourceFactory.getJavaSources();
        while (implIter.hasNext()) {
            JavaSource source = (JavaSource) implIter.next();
            if (source.getClassName().equals(implClassName)) {
                implSource = source;
                break;
            }
        }
        if (implSource == null) {
            throw new ExtensionRemovalException("Could not locate class " + implClassName + " in parsed source");
        }
        JavaMethod implMethod = null;
        for (JavaMethod m : implSource.getMethods()) {
            if (CommonTools.lowerCaseFirstCharacter(method.getName()).equals(m.getName())) {
                implMethod = m;
                break;
            }
        }
        if (implMethod == null) {
            throw new ExtensionRemovalException("Could not locate method " + method.getName() + " in parsed source");
        }
        // comment out the implementation of that method
        String methodSignature = SyncHelper.createUnBoxedSignatureStringFromMethod(implMethod, info);
        LOG.debug("Looking for method signature " + methodSignature);
        int startOfMethod = SyncHelper.startOfSignature(fileContent, methodSignature);
        int endOfMethod = SyncHelper.bracketMatch(fileContent, startOfMethod);
        // verify the method exists
        if (startOfMethod == -1 || endOfMethod == -1) {
            throw new ExtensionRemovalException("WARNING: Unable to locate method in Impl : " + method.getName());
        }
        // find the start of the method body
        int startOfBody = fileContent.indexOf("{", startOfMethod) + 1;
        // comment out whatever implementation is there
        fileContent.insert(startOfBody, "/*");
        int insertIndex = endOfMethod + 2; // offset from "/*"
        insertIndex = insertString(fileContent, insertIndex - 1, "*/\n"); // index  - 1 to be before the }
        
        // insert a comment and throw a not yet implemented exception
        insertIndex = insertString(fileContent, insertIndex, "// BDT has been removed from caGrid 1.4\n");
        insertIndex = insertString(fileContent, insertIndex, "throw new java.rmi.RemoteException(\"BDT has been removed from caGrid 1.4\");\n");
                
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
