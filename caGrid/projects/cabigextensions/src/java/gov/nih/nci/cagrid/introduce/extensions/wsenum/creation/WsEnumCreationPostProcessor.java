package gov.nih.nci.cagrid.introduce.extensions.wsenum.creation;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.introduce.beans.ServiceDescription;
import gov.nih.nci.cagrid.introduce.beans.extension.ServiceExtensionDescriptionType;
import gov.nih.nci.cagrid.introduce.beans.method.MethodType;
import gov.nih.nci.cagrid.introduce.beans.method.MethodTypeImportInformation;
import gov.nih.nci.cagrid.introduce.beans.method.MethodTypeInputs;
import gov.nih.nci.cagrid.introduce.beans.method.MethodTypeInputsInput;
import gov.nih.nci.cagrid.introduce.beans.method.MethodTypeOutput;
import gov.nih.nci.cagrid.introduce.beans.method.MethodTypeProviderInformation;
import gov.nih.nci.cagrid.introduce.beans.namespace.NamespaceType;
import gov.nih.nci.cagrid.introduce.beans.service.Custom;
import gov.nih.nci.cagrid.introduce.beans.service.ResourceFrameworkOptions;
import gov.nih.nci.cagrid.introduce.beans.service.ServiceType;
import gov.nih.nci.cagrid.introduce.common.CommonTools;
import gov.nih.nci.cagrid.introduce.common.FileFilters;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;
import gov.nih.nci.cagrid.introduce.extension.CreationExtensionException;
import gov.nih.nci.cagrid.introduce.extension.CreationExtensionPostProcessor;
import gov.nih.nci.cagrid.introduce.extension.ExtensionsLoader;
import gov.nih.nci.cagrid.introduce.extension.utils.ExtensionUtilities;
import gov.nih.nci.cagrid.wsenum.common.WsEnumConstants;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

import javax.xml.namespace.QName;

import org.globus.ws.enumeration.EnumProvider;


/**
 * WsEnumCreationPostProcessor Post-creation extension to Introduce to add
 * WS-Enumeration support to a Grid Service
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A> *
 * @created Nov 16, 2006
 * @version $Id$
 */
public class WsEnumCreationPostProcessor implements CreationExtensionPostProcessor {

    public WsEnumCreationPostProcessor() {

    }


    public void postCreate(ServiceExtensionDescriptionType desc, ServiceInformation info)
        throws CreationExtensionException {
        
        checkServiceNaming(info);
        
        setIterImplTypeServiceProperty(info);
        
        try {
            if (!enumerationServiceContextExists(info)) {
                // execute steps to add ws-enumeration to the grid service
                createServiceContext(info);
                copySchemasToService(desc, info);
                copyWsdlToService(desc, info);
                copyLibrariesToService(info);
                addEnumerationNamespaces(info);
                addEnumerationMethods(info);
            }
        } catch (CreationExtensionException ex) {
            ex.printStackTrace(System.out);
            System.out.flush();
            throw ex;
        } catch (Exception ex) {
            ex.printStackTrace(System.out);
            System.out.flush();
            throw new CreationExtensionException(ex);
        }
    }
    
    
    private void checkServiceNaming(ServiceInformation serviceInfo) throws CreationExtensionException {
        ServiceType mainService = serviceInfo.getServices().getService(0);
        if (WsEnumConstants.CAGRID_ENUMERATION_SERVICE_NAME.equals(mainService.getName())) {
            throw new CreationExtensionException(
                "The caGrid WS-Enumeration infrastructure already makes use of the Service Name " + WsEnumConstants.CAGRID_ENUMERATION_SERVICE_NAME);
        }
        if (WsEnumConstants.CAGRID_ENUMERATION_SERVICE_PACKAGE.equals(mainService.getPackageName())) {
            throw new CreationExtensionException(
                "The caGrid WS-Enumeration infrastructure already makes use of the package name " + WsEnumConstants.CAGRID_ENUMERATION_SERVICE_PACKAGE);
        }
        if (WsEnumConstants.CAGRID_ENUMERATION_SERVICE_NAMESPACE.equals(mainService.getNamespace())) {
            throw new CreationExtensionException(
                "The caGrid WS-Enumeration infrastructure already makes use of the namespace " + WsEnumConstants.CAGRID_ENUMERATION_SERVICE_NAMESPACE);
        }
    }
    
    
    private boolean enumerationServiceContextExists(ServiceInformation info) {
        if (info.getServices() != null && info.getServices().getService() != null) {
            for (ServiceType service : info.getServices().getService()) {
                if (WsEnumConstants.CAGRID_ENUMERATION_SERVICE_NAME.equals(service.getName())
                    && WsEnumConstants.CAGRID_ENUMERATION_SERVICE_PACKAGE.equals(service.getPackageName())
                    && WsEnumConstants.CAGRID_ENUMERATION_SERVICE_NAMESPACE.equals(service.getNamespace())) {
                    return true;
                }
            }
        }
        return false;
    }


    private void createServiceContext(ServiceInformation info) {
        ServiceType enumServiceContext = new ServiceType();
        enumServiceContext.setName(WsEnumConstants.CAGRID_ENUMERATION_SERVICE_NAME);
        enumServiceContext.setPackageName(WsEnumConstants.CAGRID_ENUMERATION_SERVICE_PACKAGE);
        enumServiceContext.setNamespace(WsEnumConstants.CAGRID_ENUMERATION_SERVICE_NAMESPACE);
        enumServiceContext.setResourceFrameworkOptions(new ResourceFrameworkOptions());
        enumServiceContext.getResourceFrameworkOptions().setCustom(new Custom());
        CommonTools.addService(info.getServices(), enumServiceContext);
    }


    private void copySchemasToService(ServiceExtensionDescriptionType desc, ServiceInformation info)
        throws CreationExtensionException {
        File extensionSchemaDir = getExtensionSchemaDir(desc);
        File serviceSchemasDir = new File(info.getBaseDirectory().getAbsolutePath() + File.separator + "schema"
            + File.separator + info.getServices().getService(0).getName());
        File[] sourceSchemas = extensionSchemaDir.listFiles(new FileFilters.XSDFileFilter());
        for (File source : sourceSchemas) {
            if (source.isFile()) {
                File outFile = new File(serviceSchemasDir.getAbsolutePath() 
                    + File.separator + source.getName());
                try {
                    Utils.copyFile(source, outFile);
                } catch (IOException ex) {
                    throw new CreationExtensionException("Error copying schema file " 
                        + source.getAbsolutePath(), ex);
                }
            }
        }
    }


    private void copyWsdlToService(ServiceExtensionDescriptionType desc, ServiceInformation info)
        throws CreationExtensionException {
        File extensionSchemaDir = getExtensionSchemaDir(desc);
        File sourceWsdl = new File(extensionSchemaDir.getAbsolutePath() + File.separator + "enumeration.wsdl");
        File serviceSchemasDir = new File(info.getBaseDirectory().getAbsolutePath() + File.separator + "schema"
            + File.separator + info.getServices().getService()[0].getName());
        File outWsdl = new File(serviceSchemasDir.getAbsolutePath() + File.separator + sourceWsdl.getName());
        try {
            Utils.copyFile(sourceWsdl, outWsdl);
        } catch (IOException ex) {
            throw new CreationExtensionException("Error copying enumeration wsdl file", ex);
        }
    }


    private void copyLibrariesToService(ServiceInformation info) throws CreationExtensionException {
        File libDir = getExtensionLibDir();
        File[] sourceLibs = libDir.listFiles(new FileFilter() {
            public boolean accept(File pathname) {
                String name = pathname.getName();
                if (name.endsWith(".jar")) {
                    return name.endsWith("_enum.jar") || name.indexOf("wsEnum") != -1
                        || name.indexOf("cabigextensions-stubs") != -1;
                }
                return false;
            }
        });
        File serviceLibDir = new File(info.getBaseDirectory().getAbsolutePath() + File.separator + "lib");
        File[] outputLibs = new File[sourceLibs.length];
        for (int i = 0; i < sourceLibs.length; i++) {
            File outFile = new File(serviceLibDir.getAbsolutePath() + File.separator + sourceLibs[i].getName());
            outputLibs[i] = outFile;
            try {
                Utils.copyFile(sourceLibs[i], outFile);
            } catch (IOException ex) {
                throw new CreationExtensionException("Error copying library " + sourceLibs[i].getAbsolutePath(), ex);
            }
        }
        File classpathFile = new File(info.getBaseDirectory().getAbsolutePath() + File.separator + ".classpath");
        try {
            ExtensionUtilities.syncEclipseClasspath(classpathFile, outputLibs);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new CreationExtensionException("Error synchronizing Eclipse .classpath file", ex);
        }
    }


    private void addEnumerationNamespaces(ServiceInformation info) throws CreationExtensionException {
        File serviceSchemasDir = new File(info.getBaseDirectory().getAbsolutePath() + File.separator + "schema"
            + File.separator + info.getServices().getService()[0].getName());
        try {
            // enumeration.xsd
            NamespaceType enumNsType = CommonTools.createNamespaceType(serviceSchemasDir.getAbsolutePath()
                + File.separator + WsEnumConstants.ENUMERATION_XSD_NAME, serviceSchemasDir);
            enumNsType.setGenerateStubs(Boolean.FALSE);
            enumNsType.setPackageName(WsEnumConstants.ENUMERATION_PACKAGE_NAME);
            CommonTools.addNamespace(info.getServiceDescriptor(), enumNsType);
            // addressing.xsd
            NamespaceType addyNsType = CommonTools.createNamespaceType(serviceSchemasDir.getAbsolutePath()
                + File.separator + WsEnumConstants.ADDRESSING_XSD_NAME, serviceSchemasDir);
            addyNsType.setGenerateStubs(Boolean.FALSE);
            addyNsType.setPackageName(WsEnumConstants.ADDRESSING_PACKAGE_NAME);
            CommonTools.addNamespace(info.getServiceDescriptor(), addyNsType);
            // enumeration response container
            NamespaceType ercNsType = CommonTools.createNamespaceType(serviceSchemasDir.getAbsolutePath()
                + File.separator + WsEnumConstants.ENUMERATION_RESPONSE_XSD, serviceSchemasDir);
            ercNsType.setGenerateStubs(Boolean.FALSE);
            ercNsType.setPackageName(WsEnumConstants.ENUMERATION_RESPONSE_PACKAGE);
            CommonTools.addNamespace(info.getServiceDescriptor(), ercNsType);
        } catch (Exception ex) {
            throw new CreationExtensionException("Error creating namespace types", ex);
        }
    }


    private void addEnumerationMethods(ServiceInformation info) {
        // get the main service
        ServiceType service = CommonTools.getService(
            info.getServices(), WsEnumConstants.CAGRID_ENUMERATION_SERVICE_NAME);

        // Pull method
        MethodType pullMethod = new MethodType();
        pullMethod.setName("PullOp");
        MethodTypeInputs pullInputs = new MethodTypeInputs();
        MethodTypeInputsInput pullParameter = new MethodTypeInputsInput();
        pullParameter.setIsArray(false);
        pullParameter.setName("pull");
        pullParameter.setQName(new QName(WsEnumConstants.WS_ENUMERATION_URI, "Pull"));
        pullInputs.setInput(new MethodTypeInputsInput[]{pullParameter});
        pullMethod.setInputs(pullInputs);
        MethodTypeOutput pullOutput = new MethodTypeOutput();
        pullOutput.setIsArray(false);
        pullOutput.setQName(new QName(WsEnumConstants.WS_ENUMERATION_URI, "PullResponse"));
        pullMethod.setOutput(pullOutput);
        setMethodImportInformation(pullMethod, "PullMessage", "PullResponseMessage");
        CommonTools.addMethod(service, pullMethod);

        // Renew method
        MethodType renewMethod = new MethodType();
        renewMethod.setName("RenewOp");
        MethodTypeInputs renewInputs = new MethodTypeInputs();
        MethodTypeInputsInput renewParameter = new MethodTypeInputsInput();
        renewParameter.setIsArray(false);
        renewParameter.setName("renew");
        renewParameter.setQName(new QName(WsEnumConstants.WS_ENUMERATION_URI, "Renew"));
        renewInputs.setInput(new MethodTypeInputsInput[]{renewParameter});
        renewMethod.setInputs(renewInputs);
        MethodTypeOutput renewOutput = new MethodTypeOutput();
        renewOutput.setIsArray(false);
        renewOutput.setQName(new QName(WsEnumConstants.WS_ENUMERATION_URI, "RenewResponse"));
        renewMethod.setOutput(renewOutput);
        setMethodImportInformation(renewMethod, "RenewMessage", "RenewResponseMessage");
        CommonTools.addMethod(service, renewMethod);

        // GetStatus method
        MethodType getStatusMethod = new MethodType();
        getStatusMethod.setName("GetStatusOp");
        MethodTypeInputs getStatusInputs = new MethodTypeInputs();
        MethodTypeInputsInput getStatusParam = new MethodTypeInputsInput();
        getStatusParam.setIsArray(false);
        getStatusParam.setName("status");
        getStatusParam.setQName(new QName(WsEnumConstants.WS_ENUMERATION_URI, "GetStatus"));
        getStatusInputs.setInput(new MethodTypeInputsInput[]{getStatusParam});
        getStatusMethod.setInputs(getStatusInputs);
        MethodTypeOutput getStatusOutput = new MethodTypeOutput();
        getStatusOutput.setIsArray(false);
        getStatusOutput.setQName(new QName(WsEnumConstants.WS_ENUMERATION_URI, "GetStatusResponse"));
        getStatusMethod.setOutput(getStatusOutput);
        setMethodImportInformation(getStatusMethod, "GetStatusMessage", "GetStatusResponseMessage");
        CommonTools.addMethod(service, getStatusMethod);

        // Release method
        MethodType releaseMethod = new MethodType();
        releaseMethod.setName("ReleaseOp");
        MethodTypeInputs releaseInputs = new MethodTypeInputs();
        MethodTypeInputsInput releaseParameter = new MethodTypeInputsInput();
        releaseParameter.setIsArray(false);
        releaseParameter.setName("release");
        releaseParameter.setQName(new QName(WsEnumConstants.WS_ENUMERATION_URI, "Release"));
        releaseInputs.setInput(new MethodTypeInputsInput[]{releaseParameter});
        releaseMethod.setInputs(releaseInputs);
        // even void return methods require a method output
        MethodTypeOutput releaseOutput = new MethodTypeOutput();
        releaseOutput.setQName(new QName("", "void"));
        releaseOutput.setIsArray(false);
        releaseMethod.setOutput(releaseOutput);
        setMethodImportInformation(releaseMethod, "ReleaseMessage", "ReleaseResponseMessage");
        CommonTools.addMethod(service, releaseMethod);
    }


    private void setMethodImportInformation(MethodType method, String inputName, String outputName) {
        method.setIsImported(true);
        method.setIsProvided(true);
        MethodTypeImportInformation info = new MethodTypeImportInformation();
        info.setNamespace(WsEnumConstants.WS_ENUMERATION_URI);
        info.setWsdlFile(WsEnumConstants.ENUMERATION_WSDL_NAME);
        info.setPackageName("org.globus.ws.enumeration");
        info.setPortTypeName(WsEnumConstants.PORT_TYPE_NAME);
        info.setFromIntroduce(Boolean.FALSE);
        // input and output message types
        QName inputQname = new QName(WsEnumConstants.WS_ENUMERATION_URI, inputName);
        info.setInputMessage(inputQname);
        QName outputQname = new QName(WsEnumConstants.WS_ENUMERATION_URI, outputName);
        info.setOutputMessage(outputQname);
        MethodTypeProviderInformation provider = new MethodTypeProviderInformation();
        provider.setProviderClass(EnumProvider.class.getName());
        method.setImportInformation(info);
        method.setProviderInformation(provider);
    }
    
    
    private void setIterImplTypeServiceProperty(ServiceInformation info) throws CreationExtensionException {
        ServiceDescription desc = info.getServiceDescriptor();
        if (!CommonTools.servicePropertyExists(desc, WsEnumConstants.ITER_IMPL_TYPE_PROPERTY)) {
            CommonTools.setServiceProperty(desc, WsEnumConstants.ITER_IMPL_TYPE_PROPERTY, 
                WsEnumConstants.DEFAULT_ITER_IMPL_TYPE, false);
        }
    }


    private File getExtensionSchemaDir(ServiceExtensionDescriptionType desc) {
        // find the extension's schema directory
        File extensionsDir = ExtensionsLoader.getInstance().getExtensionsDir();
        String enumExtensionDir = extensionsDir + File.separator + desc.getName();
        String enumSchemasDir = enumExtensionDir + File.separator + "schema";
        return new File(enumSchemasDir);
    }


    private File getExtensionLibDir() {
        return new File(ExtensionsLoader.getInstance().getExtensionsDir().getAbsolutePath() + File.separator + "lib");
    }
}
