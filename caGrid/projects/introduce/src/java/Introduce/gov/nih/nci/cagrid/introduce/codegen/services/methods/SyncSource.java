package gov.nih.nci.cagrid.introduce.codegen.services.methods;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.introduce.IntroduceConstants;
import gov.nih.nci.cagrid.introduce.beans.method.MethodType;
import gov.nih.nci.cagrid.introduce.beans.method.MethodTypeOutput;
import gov.nih.nci.cagrid.introduce.beans.service.ServiceType;
import gov.nih.nci.cagrid.introduce.codegen.common.SynchronizationException;
import gov.nih.nci.cagrid.introduce.common.CommonTools;
import gov.nih.nci.cagrid.introduce.common.SchemaInformation;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;
import gov.nih.nci.cagrid.introduce.common.SpecificMethodInformation;
import gov.nih.nci.cagrid.introduce.templates.service.globus.resource.ResourceCreatorTemplate;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.axis.utils.JavaUtils;
import org.apache.log4j.Logger;
import org.apache.ws.jaxme.js.JavaMethod;


/**
 * SyncSource
 * 
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella </A>
 * @created Jun 8, 2005
 * @version $Id: mobiusEclipseCodeTemplates.xml,v 1.2 2005/04/19 14:58:02 oster
 *          Exp $
 */
public class SyncSource {
    private static final Logger logger = Logger.getLogger(SyncSource.class);

    public static final String TAB = "  ";
    public static final String DOUBLE_TAB = TAB + TAB;
    public static final String TRIPPLE_TAB = DOUBLE_TAB + TAB;

    private String serviceClient;
    private String serviceInterface;
    private String serviceImpl;
    private String serviceProviderImpl;
    private ServiceInformation serviceInfo;
    private ServiceType service;


    public SyncSource(File baseDir, ServiceInformation info, ServiceType service) {
        this.service = service;
        serviceInfo = info;
        serviceClient = baseDir.getAbsolutePath() + File.separator + "src" + File.separator
            + CommonTools.getPackageDir(service) + File.separator + "client" + File.separator + service.getName()
            + "Client.java";
        serviceInterface = baseDir.getAbsolutePath() + File.separator + "src" + File.separator
            + CommonTools.getPackageDir(service) + File.separator + "common" + File.separator + service.getName()
            + "I.java";
        serviceImpl = baseDir.getAbsolutePath() + File.separator + "src" + File.separator
            + CommonTools.getPackageDir(service) + File.separator + "service" + File.separator + service.getName()
            + "Impl.java";
        serviceProviderImpl = baseDir.getAbsolutePath() + File.separator + "src" + File.separator
            + CommonTools.getPackageDir(service) + File.separator + "service" + File.separator + "globus"
            + File.separator + service.getName() + "ProviderImpl.java";
    }


    public void addMethods(List additions) throws SynchronizationException {
        for (int i = 0; i < additions.size(); i++) {
            // add it to the interface
            MethodType method = (MethodType) additions.get(i);
            if (!method.getName().equals(IntroduceConstants.SERVICE_SECURITY_METADATA_METHOD)) {
                StringBuffer fileContent = null;
                try {
                    fileContent = Utils.fileToStringBuffer(new File(serviceInterface));
                } catch (IOException e) {
                    throw new SynchronizationException("Error loading service interface file: " + e.getMessage(), e);
                }

                // insert the new client method
                int endOfClass = fileContent.lastIndexOf("}");
                StringBuffer clientMethod = new StringBuffer();
                if (method.isIsImported() && (method.getImportInformation().getFromIntroduce() != null)
                    && !method.getImportInformation().getFromIntroduce().booleanValue()) {
                    clientMethod.append(SyncHelper.createJavaDoc(method)).append("\n").append(TAB).append(
                        SyncHelper.createBoxedSignatureStringFromMethod(method)).append(" ").append(
                        SyncHelper.createClientExceptions(method, serviceInfo));
                } else {
                    clientMethod.append(SyncHelper.createJavaDoc(method)).append("\n").append(TAB).append(
                        SyncHelper.createClientUnBoxedSignatureStringFromMethod(method, serviceInfo)).append(" ")
                        .append(SyncHelper.createClientExceptions(method, serviceInfo));
                }
                clientMethod.append(";\n\n");

                fileContent.insert(endOfClass, clientMethod);
                try {
                    FileWriter fw = new FileWriter(new File(serviceInterface));
                    fw.write(SyncHelper.removeMultiNewLines(fileContent.toString()));
                    fw.close();
                } catch (IOException e1) {
                    throw new SynchronizationException("Error writing service interface: " + e1.getMessage(), e1);
                }

                if (!method.isIsProvided()) {
                    // populate the impl method
                    addImpl(method);
                    // populate the provider impl method
                    addProviderImpl(method);
                    // populate the client method
                }

                addClientImpl(method);
            }
        }
    }


    public void modifyMethods(List modifiedMethods) throws SynchronizationException {
        for (int i = 0; i < modifiedMethods.size(); i++) {
            // add it to the interface
            Modification mod = (Modification) modifiedMethods.get(i);
  
            MethodType method = mod.getMethodType();
            if (!method.getName().equals(IntroduceConstants.SERVICE_SECURITY_METADATA_METHOD)) {
                StringBuffer fileContent = null;
                try {
                    fileContent = Utils.fileToStringBuffer(new File(serviceInterface));
                } catch (IOException e) {
                    throw new SynchronizationException("Error loading service interface file: " + e.getMessage(), e);
                }

                // remove the old interface method
                String clientMethod = null;

                if (method.isIsImported() && (method.getImportInformation().getFromIntroduce() != null)
                    && !method.getImportInformation().getFromIntroduce().booleanValue()) {
                    clientMethod = SyncHelper.createBoxedSignatureStringFromMethod(mod.getIMethod());
                } else {
                    clientMethod = SyncHelper.createClientUnBoxedSignatureStringFromMethod(mod.getIMethod());
                }
                int startOfMethod = SyncHelper.startOfSignature(fileContent, clientMethod);
                String restOfFile = fileContent.substring(startOfMethod);
                int endOfMethod = startOfMethod + restOfFile.indexOf(";") + 1;
                startOfMethod = SyncHelper.startOfJavaDoc(fileContent, startOfMethod);

                if ((startOfMethod == -1) || (endOfMethod == -1)) {
                    System.err.println("WARNING: Unable to locate method in I : "
                        + CommonTools.lowerCaseFirstCharacter(method.getName()));
                    return;
                }

                fileContent.delete(startOfMethod, endOfMethod);

                // insert the new interface method
                int endOfClass = fileContent.lastIndexOf("}");

                if (method.isIsImported() && (method.getImportInformation().getFromIntroduce() != null)
                    && !method.getImportInformation().getFromIntroduce().booleanValue()) {
                    clientMethod = SyncHelper.createJavaDoc(method) + "\n" + TAB
                        + SyncHelper.createBoxedSignatureStringFromMethod(method) + " "
                        + SyncHelper.createClientExceptions(method, serviceInfo);
                } else {
                    clientMethod = SyncHelper.createJavaDoc(method) + "\n" + TAB
                        + SyncHelper.createClientUnBoxedSignatureStringFromMethod(method, serviceInfo) + " "
                        + SyncHelper.createClientExceptions(method, serviceInfo);
                }
                clientMethod += ";\n\n";

                fileContent.insert(endOfClass, clientMethod);
                try {
                    FileWriter fw = new FileWriter(new File(serviceInterface));
                    fw.write(SyncHelper.removeMultiNewLines(fileContent.toString()));
                    fw.close();
                } catch (IOException e1) {
                    throw new SynchronizationException("Error saving service interface: " + e1.getMessage(), e1);
                }
            }

            // if the method was not provided
            if (!method.isIsProvided()) {
                // just clean up the modified impl
                modifyImpl(mod);
                // redo the provider impl method
                removeProviderImpl(mod.getIMethod());
                addProviderImpl(method);
            }
            // redo the client method
            removeClientImpl(mod.getIMethod());
            addClientImpl(method);
        }
    }


    public void addClientImpl(MethodType method) throws SynchronizationException {
        StringBuffer fileContent = null;
        try {
            fileContent = Utils.fileToStringBuffer(new File(serviceClient));
        } catch (IOException e) {
            throw new SynchronizationException("Error loading service client: " + e.getMessage(), e);
        }

        // insert the new client method
        int endOfClass = fileContent.lastIndexOf("}");

        addClientImpl(method, endOfClass);
    }


    public void addClientImpl(MethodType method, int fileLocation) throws SynchronizationException {
        StringBuffer fileContent = null;
        String methodName = CommonTools.lowerCaseFirstCharacter(method.getName());
        try {
            fileContent = Utils.fileToStringBuffer(new File(serviceClient));
        } catch (Exception e) {
            throw new SynchronizationException("Error loading service client: " + e.getMessage(), e);
        }

        // insert the new client method
        String clientMethod = null;
        if (method.isIsImported() && (method.getImportInformation().getFromIntroduce() != null)
            && !method.getImportInformation().getFromIntroduce().booleanValue()) {
            clientMethod = TAB + SyncHelper.createBoxedSignatureStringFromMethod(method) + " "
                + SyncHelper.createClientExceptions(method, serviceInfo);
        } else {
            clientMethod = TAB + SyncHelper.createClientUnBoxedSignatureStringFromMethod(method, serviceInfo) + " "
                + SyncHelper.createClientExceptions(method, serviceInfo);
        }
        clientMethod += "{\n";
        clientMethod += DOUBLE_TAB + "synchronized(portTypeMutex){\n";
        clientMethod += TRIPPLE_TAB + "configureStubSecurity((Stub)portType,\""
            + CommonTools.lowerCaseFirstCharacter(method.getName()) + "\");\n";

        // put in the call to the client
        String var = "portType";

        String methodString = DOUBLE_TAB;
        MethodTypeOutput returnTypeEl = method.getOutput();

        if (!method.isIsImported()
            || ((method.getImportInformation().getFromIntroduce() == null) || (method.getImportInformation()
                .getFromIntroduce().booleanValue()))) {
            // always a boxed call now becuase using complex types in the wsdl
            // create handle for the boxed wrapper
            methodString += method.getInputMessageClass() + " params = new " + method.getInputMessageClass() + "();\n";
            // set the values fo the boxed wrapper
            if ((method.getInputs() != null) && (method.getInputs().getInput() != null)) {
                for (int j = 0; j < method.getInputs().getInput().length; j++) {
                    SchemaInformation inNamespace = CommonTools.getSchemaInformation(serviceInfo.getNamespaces(),
                        method.getInputs().getInput(j).getQName());
                    String paramName = method.getInputs().getInput(j).getName();
                    String containerClassName = method.getInputs().getInput(j).getContainerClass();
                    String containerMethodCall = CommonTools.upperCaseFirstCharacter(JavaUtils
                        .xmlNameToJava(inNamespace.getType().getType()));
                    methodString += DOUBLE_TAB;
                    if (inNamespace.getNamespace().getNamespace().equals(IntroduceConstants.W3CNAMESPACE)) {
                        methodString += "params.set" + CommonTools.upperCaseFirstCharacter(paramName) + "(" + paramName
                            + ");\n";
                    } else {
                        methodString += containerClassName + " " + paramName + "Container = new " + containerClassName
                            + "();\n";
                        methodString += DOUBLE_TAB;
                        methodString += paramName + "Container.set" + containerMethodCall + "(" + paramName + ");\n";
                        methodString += DOUBLE_TAB;
                        methodString += "params.set" + CommonTools.upperCaseFirstCharacter(paramName) + "(" + paramName
                            + "Container);\n";
                    }
                }
            }
            // make the call
            methodString += DOUBLE_TAB;

            // always boxed returns now because of complex types in wsdl
            methodString += method.getOutputMessageClass() + " boxedResult = " + var + "." + methodName + "(params);\n";

            if (!returnTypeEl.getQName().getNamespaceURI().equals("")
                && !returnTypeEl.getQName().getLocalPart().equals("void")) {
                methodString += DOUBLE_TAB;
                SchemaInformation info = CommonTools.getSchemaInformation(serviceInfo.getNamespaces(), returnTypeEl
                    .getQName());
                if (info.getNamespace().getNamespace().equals(IntroduceConstants.W3CNAMESPACE)) {
                    if (info.getType().getType().equals("boolean") && !returnTypeEl.isIsArray()) {
                        methodString += "return boxedResult.isResponse();\n";
                    } else {
                        methodString += "return boxedResult.getResponse();\n";
                    }
                } else {
                    if ((returnTypeEl.getIsClientHandle() != null) && returnTypeEl.getIsClientHandle().booleanValue()) {
                        // create the client handle and put the EPR in it
                        // then return the client handle...
                        if (returnTypeEl.isIsArray()) {
                            methodString += returnTypeEl.getClientHandleClass() + "[] clientArray = null;\n";
                            methodString += DOUBLE_TAB + "if(boxedResult.get" + CommonTools.fixPortTypeMethodName(info.getType().getType()) + "()!=null){\n";
                            methodString += DOUBLE_TAB + "  clientArray = new " + returnTypeEl.getClientHandleClass()
                                + "[boxedResult.get" + CommonTools.fixPortTypeMethodName(info.getType().getType()) + "().length];\n";
                            methodString += DOUBLE_TAB
                                + "  for(int i = 0; i < boxedResult.get" + CommonTools.fixPortTypeMethodName(info.getType().getType()) + "().length; i++){\n";
                            methodString += DOUBLE_TAB + "	   clientArray[i] = new "
                                + returnTypeEl.getClientHandleClass() + "(boxedResult.get" + CommonTools.fixPortTypeMethodName(info.getType().getType())
                                + "(i).getEndpointReference(),getProxy());\n";
                            methodString += DOUBLE_TAB + "  }\n";
                            methodString += DOUBLE_TAB + "}\n";
                            methodString += DOUBLE_TAB + "return clientArray;\n";
                        } else {
                            methodString += "EndpointReferenceType ref = boxedResult.get";
                            methodString += CommonTools.upperCaseFirstCharacter(info.getType().getType())
                                + "().getEndpointReference();\n";
                            methodString += DOUBLE_TAB + "return new " + returnTypeEl.getClientHandleClass()
                                + "(ref,getProxy());\n";
                        }
                    } else {
                        methodString += "return boxedResult.get"
                            + CommonTools.fixPortTypeMethodName(info.getType().getType()) + "();\n";
                    }
                }
            }
        } else {
            // if the method is unboxable then i need to just
            // call it straight up.

            if (method.getOutputMessageClass() != null) {
                methodString += "return ";
            }
            methodString += var + "." + CommonTools.lowerCaseFirstCharacter(method.getName());
            if (method.getInputMessageClass() != null) {
                methodString += "(params);\n";
            } else {
                methodString += "();\n";
            }
        }

        clientMethod += methodString;
        clientMethod += DOUBLE_TAB + "}\n";
        clientMethod += TAB + "}\n\n";

        fileContent.insert(fileLocation, clientMethod);
        try {
            FileWriter fw = new FileWriter(new File(SyncHelper.removeMultiNewLines(serviceClient)));
            fw.write(SyncHelper.removeMultiNewLines(fileContent.toString()));
            fw.close();
        } catch (IOException e1) {
            throw new SynchronizationException("Error saving service client: " + e1.getMessage(), e1);
        }
    }


    public void addImpl(MethodType method) throws SynchronizationException {
        StringBuffer fileContent = null;
        try {
            fileContent = Utils.fileToStringBuffer(new File(serviceImpl));
        } catch (IOException e) {
            throw new SynchronizationException("Error loading service impl: " + e.getMessage(), e);
        }

        String clientMethod = null;
        // insert the new client method
        int endOfClass = fileContent.lastIndexOf("}");
        if (method.isIsImported() && (method.getImportInformation().getFromIntroduce() != null)
            && !method.getImportInformation().getFromIntroduce().booleanValue()) {
            clientMethod = TAB + SyncHelper.createBoxedSignatureStringFromMethod(method) + " "
                + SyncHelper.createClientExceptions(method, serviceInfo);
        } else {
            clientMethod = TAB + SyncHelper.createUnBoxedSignatureStringFromMethod(method, serviceInfo) + " "
                + SyncHelper.createExceptions(method, serviceInfo);
        }

        clientMethod += "{\n";

        // if this method is returning a new client handle and is creating a
        // resource to do so
        if (method.getOutput() != null
            && method.getOutput().getIsClientHandle() != null
            && method.getOutput().getIsClientHandle().booleanValue()
            && method.getOutput().getIsCreatingResourceForClientHandle() != null
            && method.getOutput().getIsCreatingResourceForClientHandle().booleanValue()
            && !(method.getOutput().isIsArray())
            && !(CommonTools.getService(serviceInfo.getServices(),
                method.getOutput().getResourceClientIntroduceServiceName()).getResourceFrameworkOptions().getSingleton()!=null || CommonTools.getService(serviceInfo.getServices(),
                method.getOutput().getResourceClientIntroduceServiceName()).getResourceFrameworkOptions().getCustom()!=null)) {
            SpecificMethodInformation smi = new SpecificMethodInformation(serviceInfo, service, method);
            ResourceCreatorTemplate resourceCreatorTemplate = new ResourceCreatorTemplate();
            String createResourceCode = resourceCreatorTemplate.generate(smi);
            clientMethod += createResourceCode;
        } else {
            clientMethod += DOUBLE_TAB + "//TODO: Implement this autogenerated method\n";
            clientMethod += DOUBLE_TAB + "throw new RemoteException(\"Not yet implemented\");\n";
        }

        clientMethod += TAB + "}\n\n";

        fileContent.insert(endOfClass, clientMethod);
        try {
            String fileContentString = fileContent.toString();
            FileWriter fw = new FileWriter(new File(serviceImpl));
            fw.write(SyncHelper.removeMultiNewLines(fileContentString));
            fw.close();
        } catch (IOException e1) {
            throw new SynchronizationException("Error saving service impl: " + e1.getMessage(), e1);
        }
    }


    public void addProviderImpl(MethodType method) throws SynchronizationException {
        StringBuffer fileContent = null;
        try {
            fileContent = Utils.fileToStringBuffer(new File(serviceProviderImpl));
        } catch (Exception e) {
            throw new SynchronizationException("Error loading service provider impl: " + e.getMessage(), e);
        }

        // insert the new client method
        int endOfClass = fileContent.lastIndexOf("}");

        String var = "impl";
        String methodName = CommonTools.lowerCaseFirstCharacter(method.getName());

        String clientMethod = "";
        String methodString = "";

        // can i create the unboxed call to the implementation
        if (!method.isIsImported()
            || ((method.getImportInformation().getFromIntroduce() == null) || method.getImportInformation()
                .getFromIntroduce().booleanValue())) {
            // slh -- in migration to globus 4 we need to check here for
            // autoboxing
            // and get appropriate
            clientMethod = DOUBLE_TAB + SyncHelper.createBoxedSignatureStringFromMethod(method) + " "
                + SyncHelper.createExceptions(method, serviceInfo);

            // clientMethod += " throws RemoteException";
            clientMethod += "{\n";

            methodString = "";
            MethodTypeOutput returnTypeEl = method.getOutput();

            // unbox the params
            String params = "";

            if ((method.getInputs() != null) && (method.getInputs().getInput() != null)) {
                // always unbox now
                if (method.getInputs().getInput().length >= 1) {
                    // inputs were boxed and need to be unboxed
                    for (int j = 0; j < method.getInputs().getInput().length; j++) {
                        SchemaInformation inNamespace = CommonTools.getSchemaInformation(serviceInfo.getNamespaces(),
                            method.getInputs().getInput(j).getQName());
                        String paramName = method.getInputs().getInput(j).getName();
                        if (inNamespace.getNamespace().getNamespace().equals(IntroduceConstants.W3CNAMESPACE)) {
                            if (inNamespace.getType().getType().equals("boolean")
                                && !method.getInputs().getInput(j).isIsArray()) {
                                params += "params.is" + CommonTools.upperCaseFirstCharacter(paramName) + "()";
                            } else {
                                params += "params.get" + CommonTools.upperCaseFirstCharacter(paramName) + "()";
                            }
                        } else {
                            params += "params.get"
                                + CommonTools.upperCaseFirstCharacter(paramName)
                                + "().get"
                                + CommonTools.upperCaseFirstCharacter(JavaUtils.xmlNameToJava(inNamespace.getType()
                                    .getType())) + "()";
                        }
                        if (j < method.getInputs().getInput().length - 1) {
                            params += ",";
                        }
                    }
                } else {
                    // inputs are not boxed and can just be passed through
                    for (int j = 0; j < method.getInputs().getInput().length; j++) {
                        String paramName = method.getInputs().getInput(j).getName();
                        params += paramName;
                        if (j < method.getInputs().getInput().length - 1) {
                            params += ",";
                        }
                    }
                }
            }

            // need to unbox on the way out
            methodString += DOUBLE_TAB;
            methodString += method.getOutputMessageClass() + " boxedResult = new " + method.getOutputMessageClass()
                + "();\n";
            methodString += DOUBLE_TAB;
            if (returnTypeEl.getQName().getNamespaceURI().equals("")
                && returnTypeEl.getQName().getLocalPart().equals("void")) {
                // just call but dont set anything
                methodString += var + "." + methodName + "(" + params + ");\n";
            } else {
                SchemaInformation outputNamespace = CommonTools.getSchemaInformation(serviceInfo.getNamespaces(),
                    returnTypeEl.getQName());
                if (outputNamespace.getNamespace().getNamespace().equals(IntroduceConstants.W3CNAMESPACE)) {
                    methodString += "boxedResult.setResponse(" + var + "." + methodName + "(" + params + "));\n";
                } else {
                    methodString += "boxedResult.set"
                        + CommonTools.fixPortTypeMethodName(outputNamespace.getType().getType()) + "(" + var + "."
                        + methodName + "(" + params + "));\n";
                }
            }
            methodString += DOUBLE_TAB;
            methodString += "return boxedResult;\n";
            clientMethod += methodString;
            clientMethod += TAB + "}\n\n";
        } else {
            // create a boxed call
            clientMethod = TAB + SyncHelper.createBoxedSignatureStringFromMethod(method) + " "
                + SyncHelper.createExceptions(method, serviceInfo);
            clientMethod += "{\n" + DOUBLE_TAB;
            if (method.getOutputMessageClass() != null) {
                clientMethod += "return ";
            }
            clientMethod += var + "." + methodName;
            if (method.getInputMessageClass() != null) {
                clientMethod += "(params);\n";
            } else {
                clientMethod += "();\n";
            }

            clientMethod += methodString;
            clientMethod += TAB + "}\n\n";
        }

        fileContent.insert(endOfClass, clientMethod);

        try {
            FileWriter fw = new FileWriter(new File(serviceProviderImpl));
            fw.write(SyncHelper.removeMultiNewLines(fileContent.toString()));
            fw.close();
        } catch (IOException e1) {
            throw new SynchronizationException("Error saving service provider impl: " + e1.getMessage(), e1);
        }
    }


    public void removeMethods(List removals) throws SynchronizationException {
        for (int i = 0; i < removals.size(); i++) {
            JavaMethod method = (JavaMethod) removals.get(i);

            StringBuffer fileContent = null;
            try {
                fileContent = Utils.fileToStringBuffer(new File(serviceInterface));
            } catch (IOException e) {
                throw new SynchronizationException("Error loading service interface: " + e.getMessage(), e);
            }

            // remove the method
            String clientMethod = SyncHelper.createClientUnBoxedSignatureStringFromMethod(method);
            System.err.println("Looking to remove method: |" + clientMethod + "|");
            int startOfMethod = SyncHelper.startOfSignature(fileContent, clientMethod);
            String restOfFile = fileContent.substring(startOfMethod);
            int endOfMethod = startOfMethod + restOfFile.indexOf(";") + 1;
            startOfMethod = SyncHelper.startOfJavaDoc(fileContent, startOfMethod);

            if ((startOfMethod == -1) || (endOfMethod == -1)) {
                System.err.println("WARNING: Unable to locate method in I : " + method.getName());
                return;
            }

            fileContent.delete(startOfMethod, endOfMethod);

            try {
                FileWriter fw = new FileWriter(new File(serviceInterface));
                fw.write(SyncHelper.removeMultiNewLines(fileContent.toString()));
                fw.close();
            } catch (IOException e1) {
                throw new SynchronizationException("Error saving service interface: " + e1.getMessage(), e1);
            }

            // fail silent here in caase the method was not implemented
            try {
                // remove the impl method
                removeImpl(method);
                // remove the provider impl method
                removeProviderImpl(method);
            } catch (Exception e) {
                logger.warn("WARNING: " + e.getMessage()
                    + "\n might be due to method implementation provided by another service");
            }
            // remove the client method
            removeClientImpl(method);
        }
    }


    public void removeClientImpl(JavaMethod method) throws SynchronizationException {
        StringBuffer fileContent = null;
        try {
            fileContent = Utils.fileToStringBuffer(new File(serviceClient));
        } catch (IOException e) {
            throw new SynchronizationException("Error loading service client: " + e.getMessage(), e);
        }

        // remove the method
        String clientMethod = SyncHelper.createClientUnBoxedSignatureStringFromMethod(method);
        int startOfMethod = SyncHelper.startOfSignature(fileContent, clientMethod);
        int endOfMethod = SyncHelper.bracketMatch(fileContent, startOfMethod);

        if ((startOfMethod == -1) || (endOfMethod == -1)) {
            System.err.println("WARNING: Unable to locate method in clientImpl : " + method.getName());
            return;
        }

        fileContent.delete(startOfMethod, endOfMethod);

        try {
            FileWriter fw = new FileWriter(new File(serviceClient));
            fw.write(SyncHelper.removeMultiNewLines(fileContent.toString()));
            fw.close();
        } catch (IOException e1) {
            throw new SynchronizationException("Error writing service client: " + e1.getMessage(), e1);
        }
    }


    public void removeProviderImpl(JavaMethod method) throws SynchronizationException {
        StringBuffer fileContent = null;
        try {
            fileContent = Utils.fileToStringBuffer(new File(serviceProviderImpl));
        } catch (IOException e) {
            throw new SynchronizationException("Error loading service provider impl: " + e.getMessage(), e);
        }

        // find the method
        String searchString = "public " + method.getName();
        int startLocation = -1;
        BufferedReader br = new BufferedReader(new StringReader(fileContent.toString()));
        // tokenizer to compress all parts, then start matching the parts
        int charsRead = 0;
        try {
            String line1 = null;
            String line2 = null;
            String line3 = null;

            line1 = br.readLine();
            if (line1 != null) {
                line1 += "\n";
                line2 = br.readLine();
                if (line2 != null) {
                    line2 += "\n";
                    line3 = br.readLine();
                    if (line3 != null) {
                        line3 += "\n";
                    }
                }
            }

            String matchedLine = null;
            boolean found = false;

            while ((line1 != null) && !found) {
                matchedLine = line1;
                // if the line is empty just skip it...
                if (!line1.equals("\n")) {
                    if (line2 != null) {
                        matchedLine += line2;
                        if (line3 != null) {
                            matchedLine += line3;
                        }
                    }

                    StringTokenizer searchStringTokenizer = new StringTokenizer(searchString, " \t\n\r\f(),");
                    StringTokenizer lineTokenizer = new StringTokenizer(matchedLine, " \t\n\r\f(),");
                    int matchCount = 0;
                    // this could be advanced to support multiple lines......
                    while (searchStringTokenizer.hasMoreTokens() && lineTokenizer.hasMoreTokens()) {
                        String searchToken = searchStringTokenizer.nextToken();
                        String lineToken = lineTokenizer.nextToken();
                        if (searchToken.equals(lineToken)) {
                            matchCount++;
                            if (matchCount == 1) {
                                lineTokenizer.nextToken();
                                matchCount++;
                            }
                            if (matchCount == 3) {
                                found = true;
                                break;
                            }
                        } else {
                            break;
                        }
                    }
                }
                if (!found) {
                    charsRead += line1.length();
                    line1 = line2;
                    line2 = line3;
                    line3 = br.readLine();
                    if (line3 != null) {
                        line3 += "\n";
                    }
                }
            }
            if (!found) {
                startLocation = -1;
            }
            // if the last line i found the match then lets look for the start
            // of the method
            if (found) {
                StringTokenizer searchStringTokenizer = new StringTokenizer(searchString);
                String startToken = searchStringTokenizer.nextToken();
                int index = charsRead + matchedLine.indexOf(startToken);

                char prevChar = fileContent.toString().charAt(--index);
                while ((prevChar != '\n') && ((prevChar == ' ') || (prevChar == '\t'))) {
                    prevChar = fileContent.toString().charAt(--index);
                }
                index++;
                startLocation = index;
            }
        } catch (IOException e) {
            throw new SynchronizationException("Error reading impl: " + e.getMessage(), e);
        }

        // remove the method
        int startOfMethod = startLocation;
        int endOfMethod = SyncHelper.bracketMatch(fileContent, startOfMethod);

        if ((startOfMethod == -1) || (endOfMethod == -1)) {
            System.err.println("WARNING: Unable to locate method in providerImpl: " + method.getName());
            return;
        }

        fileContent.delete(startOfMethod, endOfMethod);

        try {
            FileWriter fw = new FileWriter(new File(serviceProviderImpl));
            fw.write(SyncHelper.removeMultiNewLines(fileContent.toString()));
            fw.close();
        } catch (IOException e1) {
            throw new SynchronizationException("Error saving service provider impl: " + e1.getMessage(), e1);
        }
    }


    public void modifyImpl(Modification mod) throws SynchronizationException {
        MethodType method = mod.getMethodType();
        JavaMethod oldMethod = mod.getImplMethod();

        StringBuffer fileContent = null;
        try {
            fileContent = Utils.fileToStringBuffer(new File(serviceImpl));
        } catch (IOException e) {
            throw new SynchronizationException("Error loading service impl: " + e.getMessage(), e);
        }

        // remove the old method signature
        String clientMethod = "";
        if (method.isIsImported() && (method.getImportInformation().getFromIntroduce() != null)
            && !method.getImportInformation().getFromIntroduce().booleanValue()) {
            clientMethod = SyncHelper.createBoxedSignatureStringFromMethod(oldMethod);
        } else {
            clientMethod = SyncHelper.createClientUnBoxedSignatureStringFromMethod(oldMethod);
        }
        int startOfMethod = SyncHelper.startOfSignature(fileContent, clientMethod);
        int endOfSignature = SyncHelper.endOfSignature(fileContent, startOfMethod);

        if ((startOfMethod == -1) || (endOfSignature == -1)) {
            System.err.println("WARNING: Unable to locate method in Impl : " + oldMethod.getName());
            return;
        }

        fileContent.delete(startOfMethod, endOfSignature);

        // add in the new modified signature
        if (method.isIsImported() && (method.getImportInformation().getFromIntroduce() != null)
            && !method.getImportInformation().getFromIntroduce().booleanValue()) {
            clientMethod = TAB + SyncHelper.createBoxedSignatureStringFromMethod(method) + " "
                + SyncHelper.createClientExceptions(method, serviceInfo);
        } else {
            clientMethod = TAB + SyncHelper.createUnBoxedSignatureStringFromMethod(method, serviceInfo) + " "
                + SyncHelper.createExceptions(method, serviceInfo);
        }
        clientMethod += "{";
        fileContent.insert(startOfMethod, clientMethod);

        try {
            FileWriter fw = new FileWriter(new File(serviceImpl));
            fw.write(SyncHelper.removeMultiNewLines(fileContent.toString()));
            fw.close();
        } catch (IOException e1) {
            throw new SynchronizationException("Error saving service impl: " + e1.getMessage(), e1);
        }
    }


    public void removeImpl(JavaMethod method) throws SynchronizationException {
        StringBuffer fileContent = null;
        try {
            fileContent = Utils.fileToStringBuffer(new File(serviceImpl));
        } catch (IOException e) {
            throw new SynchronizationException("Error loading service impl: " + e.getMessage(), e);
        }

        // remove the method
        String clientMethod = SyncHelper.createUnBoxedSignatureStringFromMethod(method, serviceInfo);
        int startOfMethod = SyncHelper.startOfSignature(fileContent, clientMethod);
        int endOfMethod = SyncHelper.bracketMatch(fileContent, startOfMethod);

        if ((startOfMethod == -1) || (endOfMethod == -1)) {
            System.err.println("WARNING: Unable to locate method in Impl : " + method.getName());
            return;
        }

        fileContent.delete(startOfMethod, endOfMethod);

        try {
            FileWriter fw = new FileWriter(new File(serviceImpl));
            fw.write(SyncHelper.removeMultiNewLines(fileContent.toString()));
            fw.close();
        } catch (IOException e1) {
            throw new SynchronizationException("Error saving service impl: " + e1.getMessage(), e1);
        }
    }


    public ServiceType getService() {
        return service;
    }


    public void setService(ServiceType service) {
        this.service = service;
    }


    public String getServiceClient() {
        return serviceClient;
    }


    public void setServiceClient(String serviceClient) {
        this.serviceClient = serviceClient;
    }


    public String getServiceImpl() {
        return serviceImpl;
    }


    public void setServiceImpl(String serviceImpl) {
        this.serviceImpl = serviceImpl;
    }


    public ServiceInformation getServiceInfo() {
        return serviceInfo;
    }


    public void setServiceInfo(ServiceInformation serviceInfo) {
        this.serviceInfo = serviceInfo;
    }


    public String getServiceInterface() {
        return serviceInterface;
    }


    public void setServiceInterface(String serviceInterface) {
        this.serviceInterface = serviceInterface;
    }


    public String getServiceProviderImpl() {
        return serviceProviderImpl;
    }


    public void setServiceProviderImpl(String serviceProviderImpl) {
        this.serviceProviderImpl = serviceProviderImpl;
    }
}
