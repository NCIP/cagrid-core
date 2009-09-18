package gov.nih.nci.cagrid.introduce.codegen;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.common.XMLUtilities;
import gov.nih.nci.cagrid.introduce.IntroduceConstants;
import gov.nih.nci.cagrid.introduce.beans.ServiceDescription;
import gov.nih.nci.cagrid.introduce.beans.extension.ExtensionType;
import gov.nih.nci.cagrid.introduce.beans.extension.ExtensionsType;
import gov.nih.nci.cagrid.introduce.beans.extension.ServiceExtensionDescriptionType;
import gov.nih.nci.cagrid.introduce.beans.method.MethodType;
import gov.nih.nci.cagrid.introduce.beans.method.MethodTypeExceptionsException;
import gov.nih.nci.cagrid.introduce.beans.method.MethodTypeInputsInput;
import gov.nih.nci.cagrid.introduce.beans.namespace.NamespaceType;
import gov.nih.nci.cagrid.introduce.beans.namespace.SchemaElementType;
import gov.nih.nci.cagrid.introduce.beans.service.ServiceType;
import gov.nih.nci.cagrid.introduce.codegen.base.SyncBase;
import gov.nih.nci.cagrid.introduce.codegen.common.SyncTool;
import gov.nih.nci.cagrid.introduce.codegen.common.SynchronizationException;
import gov.nih.nci.cagrid.introduce.codegen.properties.SyncProperties;
import gov.nih.nci.cagrid.introduce.codegen.provider.ProviderTools;
import gov.nih.nci.cagrid.introduce.codegen.serializers.SyncSerialization;
import gov.nih.nci.cagrid.introduce.codegen.services.SyncServices;
import gov.nih.nci.cagrid.introduce.codegen.utils.SyncUtils;
import gov.nih.nci.cagrid.introduce.common.AntTools;
import gov.nih.nci.cagrid.introduce.common.CommonTools;
import gov.nih.nci.cagrid.introduce.common.IntroducePropertiesManager;
import gov.nih.nci.cagrid.introduce.common.SchemaInformation;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;
import gov.nih.nci.cagrid.introduce.common.SpecificServiceInformation;
import gov.nih.nci.cagrid.introduce.creator.SkeletonEtcCreator;
import gov.nih.nci.cagrid.introduce.creator.SkeletonSchemaCreator;
import gov.nih.nci.cagrid.introduce.creator.SkeletonSecurityOperationProviderCreator;
import gov.nih.nci.cagrid.introduce.creator.SkeletonSourceCreator;
import gov.nih.nci.cagrid.introduce.extension.CodegenExtensionPostProcessor;
import gov.nih.nci.cagrid.introduce.extension.CodegenExtensionPreProcessor;
import gov.nih.nci.cagrid.introduce.extension.CreationExtensionPostProcessor;
import gov.nih.nci.cagrid.introduce.extension.ExtensionTools;
import gov.nih.nci.cagrid.introduce.extension.ExtensionsLoader;
import gov.nih.nci.cagrid.introduce.extension.ServiceExtensionRemover;
import gov.nih.nci.cagrid.introduce.servicetasks.beans.deployment.validator.DeploymentValidatorDescriptor;
import gov.nih.nci.cagrid.introduce.servicetasks.beans.deployment.validator.ValidatorDescriptor;
import gov.nih.nci.cagrid.introduce.templates.NamespaceMappingsTemplate;
import gov.nih.nci.cagrid.introduce.templates.NewServerConfigTemplate;
import gov.nih.nci.cagrid.introduce.templates.NewServiceJNDIConfigTemplate;
import gov.nih.nci.cagrid.introduce.templates.schema.service.ServiceWSDLTemplate;
import gov.nih.nci.cagrid.introduce.templates.schema.service.ServiceXSDTemplate;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

import javax.xml.namespace.QName;

import org.apache.axis.wsdl.symbolTable.Element;
import org.apache.axis.wsdl.symbolTable.MessageEntry;
import org.apache.axis.wsdl.symbolTable.SymbolTable;
import org.apache.axis.wsdl.symbolTable.Type;
import org.apache.axis.wsdl.toJava.Emitter;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.jdom.Document;
import org.jdom.Namespace;

import com.ibm.wsdl.PartImpl;


/**
 * Top level controller for re-syncing the service.
 * 
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella </A>
 * @created Jun 8, 2005
 * @version $Id: mobiusEclipseCodeTemplates.xml,v 1.2 2005/04/19 14:58:02 oster
 *          Exp $
 */
public class SyncTools {
    public static final String DEPLOYMENT_VALIDATOR_FILE = "deploymentValidator.xml";

    private static final Logger logger = Logger.getLogger(SyncTools.class);


    class MultiServiceSymbolTable {

        ServiceInformation info;

        Set excludedSet;

        List symbolTables;


        public MultiServiceSymbolTable(ServiceInformation info, Set excludedSet) throws Exception {
            this.info = info;
            this.excludedSet = excludedSet;
            this.symbolTables = new ArrayList();
        }


        public Element getElement(QName qname) {
            Element element = null;
            for (int i = 0; i < this.symbolTables.size(); i++) {

                element = ((SymbolTable) this.symbolTables.get(i)).getElement(qname);
                if (element != null) {
                    break;
                }
            }
            return element;
        }


        public Type getType(QName qname) {
            Type type = null;
            for (int i = 0; i < this.symbolTables.size(); i++) {

                type = ((SymbolTable) this.symbolTables.get(i)).getType(qname);
                if (type != null) {
                    break;
                }
            }
            return type;
        }


        public MessageEntry getMessageEntry(QName qname) {
            MessageEntry type = null;
            for (int i = 0; i < this.symbolTables.size(); i++) {

                type = ((SymbolTable) this.symbolTables.get(i)).getMessageEntry(qname);
                if (type != null) {
                    break;
                }
            }
            return type;
        }


        public void dump(PrintStream stream) {
            for (int i = 0; i < this.symbolTables.size(); i++) {
                ((SymbolTable) this.symbolTables.get(i)).dump(stream);
            }
        }


        public void generateSymbolTable() throws Exception {

            if ((this.info.getServices() != null) && (this.info.getServices().getService() != null)) {
                for (int serviceI = 0; serviceI < this.info.getServices().getService().length; serviceI++) {

                    ServiceType service = this.info.getServices().getService(serviceI);

                    Emitter parser = new Emitter();
                    SymbolTable table = null;

                    parser.setQuiet(true);
                    // parser.setAllWanted(true);
                    parser.setImports(true);

                    List excludeList = new ArrayList();
                    // one hammer(List), one solution
                    excludeList.addAll(this.excludedSet);
                    parser.setNamespaceExcludes(excludeList);

                    parser.setOutputDir(SyncTools.this.baseDirectory.getAbsolutePath() + File.separator + "tmp");
                    parser.setNStoPkg(SyncTools.this.baseDirectory.getAbsolutePath() + File.separator + "build"
                        + File.separator + IntroduceConstants.NAMESPACE2PACKAGE_MAPPINGS_FILE);
                    try {
                        parser.run(new File(SyncTools.this.baseDirectory.getAbsolutePath()
                            + File.separator
                            + "build"
                            + File.separator
                            + "schema"
                            + File.separator
                            + this.info.getIntroduceServiceProperties().get(
                                IntroduceConstants.INTRODUCE_SKELETON_SERVICE_NAME) + File.separator
                            + service.getName() + ".wsdl").getAbsolutePath());
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                    }
                    table = parser.getSymbolTable();

                    this.symbolTables.add(table);
                    parser = null;
                    System.gc();
                }

            }

            Utils.deleteDir(new File(SyncTools.this.baseDirectory.getAbsolutePath() + File.separator + "tmp"));
        }
    }

    public static final String DIR_OPT = "d";

    public static final String DIR_OPT_FULL = "directory";

    public File baseDirectory;


    public SyncTools(File directory) {
        PropertyConfigurator.configure("." + File.separator + "conf" + File.separator + "log4j.properties");
        this.baseDirectory = directory;
    }


    private String getRelativeClassName(String fullyQualifiedClassName) {
        int index = fullyQualifiedClassName.lastIndexOf(".");
        if (index >= 0) {
            return fullyQualifiedClassName.substring(index + 1);
        } else {
            return fullyQualifiedClassName;
        }
    }


    private String getPackageName(String fullyQualifiedClassName) {
        int index = fullyQualifiedClassName.lastIndexOf(".");
        if (index >= 0) {
            return fullyQualifiedClassName.substring(0, index);
        }
        return null;
    }


    public void sync() throws Exception {

        // instatiate and load up service information
        ServiceInformation info = new ServiceInformation(this.baseDirectory);

        if ((info.getServiceDescriptor().getIntroduceVersion() == null)
            || !info.getServiceDescriptor().getIntroduceVersion().equals(
                IntroducePropertiesManager.getIntroduceVersion())) {
            throw new Exception("Introduce version in project (" + info.getServiceDescriptor().getIntroduceVersion()
                + ") does not match version provided by Introduce Toolkit ( "
                + IntroducePropertiesManager.getIntroduceVersion() + " )");
        }

        // have to set the service directory in the service properties
        info.getIntroduceServiceProperties().setProperty(IntroduceConstants.INTRODUCE_SKELETON_DESTINATION_DIR,
            this.baseDirectory.getAbsolutePath());

        File schemaDir = new File(this.baseDirectory.getAbsolutePath() + File.separator + "schema");

        // before we actually process anything we must create the code and conf
        // required for any new services which were added.....

        // add remove any service extensions
        updateServiceExtensions(info);

        // create any new services that need to be created
        createNewServices(info);

        // add remove any service providers to any service which has a modified
        // list of providers
        updateServiceProviders(info);

        // add in the new exceptions types to the service and to it's
        // naemspacetypes list
        if (info.getServices().getService() != null) {
            for (int serviceI = 0; serviceI < info.getServices().getService().length; serviceI++) {
                ServiceType service = info.getServices().getService(serviceI);
                if ((service.getMethods() != null) && (service.getMethods().getMethod() != null)) {
                    for (int methodI = 0; methodI < service.getMethods().getMethod().length; methodI++) {
                        MethodType method = service.getMethods().getMethod(methodI);
                        if ((method.getExceptions() != null) && (method.getExceptions().getException() != null)) {
                            for (int exceptionI = 0; exceptionI < method.getExceptions().getException().length; exceptionI++) {
                                MethodTypeExceptionsException exception = method.getExceptions().getException(
                                    exceptionI);
                                if (exception.getQname() == null) {
                                    // need to create this exception because it
                                    // is not yet represented in the services
                                    // list.
                                    try {
                                        addFault(exception.getName(), new SpecificServiceInformation(info, service));
                                        exception.setQname(new QName(service.getNamespace() + "/types", exception
                                            .getName()));
                                    } catch (Exception e) {
                                        logger.error(e.getMessage(), e);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // STEP 3: generate a set of namespaces to not make classes/stubs for as
        // the user specified them explicitly, then save them to the build
        // properties
        Set excludeSet = generateNamespaceExcludesSet(info);
        String excludeLine = "";
        for (Iterator iter = excludeSet.iterator(); iter.hasNext();) {
            String namespace = (String) iter.next();
            excludeLine += " -x " + namespace;
        }
        info.getIntroduceServiceProperties().setProperty(IntroduceConstants.INTRODUCE_NS_EXCLUDES, excludeLine);

        Set excludeSOAPStubSet = generateSOAPStubExcludesSet(info);
        String soapBindingExcludeLine = " ";
        for (Iterator iter = excludeSOAPStubSet.iterator(); iter.hasNext();) {
            String namespace = (String) iter.next();
            soapBindingExcludeLine += namespace;
            if (iter.hasNext()) {
                soapBindingExcludeLine += " ";
            }
        }
        info.getIntroduceServiceProperties().setProperty(IntroduceConstants.INTRODUCE_SB_EXCLUDES,
            soapBindingExcludeLine);

        // write all the services into the services list property
        String servicesList = "";
        if ((info.getServices() != null) && (info.getServices().getService() != null)) {
            for (int serviceI = 0; serviceI < info.getServices().getService().length; serviceI++) {
                ServiceType service = info.getServices().getService(serviceI);
                servicesList += service.getName();
                if (serviceI < info.getServices().getService().length - 1) {
                    servicesList += ",";
                }
            }
        }
        info.getIntroduceServiceProperties().setProperty(IntroduceConstants.INTRODUCE_SKELETON_SERVICES_LIST,
            servicesList);

        logger.info("Synchronizing with pre processing extensions");
        // run any extensions that need to be ran
        if ((info.getExtensions() != null) && (info.getExtensions().getExtension() != null)) {
            ExtensionType[] extensions = info.getExtensions().getExtension();
            for (int i = 0; i < extensions.length; i++) {
                ExtensionType element = extensions[i];
                CodegenExtensionPreProcessor pp = ExtensionTools.getCodegenPreProcessor(element.getName());
                ServiceExtensionDescriptionType desc = ExtensionsLoader.getInstance().getServiceExtension(
                    element.getName());
                if (pp != null) {
                    pp.preCodegen(desc, info);
                }
            }
        }

        // persit the changed information model back to the service diretory
        info.persistInformation();

        // STEP 4: write out namespace mappings and flatten the wsdl file then
        // merge namespace
        syncWSDL(info, schemaDir);

        mergeNamespaces();

        // STEP 5: run axis to get the symbol table
        MultiServiceSymbolTable table = new MultiServiceSymbolTable(info, excludeSet);
        table.generateSymbolTable();

        // STEP 6: fill out the object model with the generated classnames where
        // the user didn't specify them explicitly
        try {
            populateClassnames(info, table);
        } catch (Exception e) {
            String mess = "ERROR: Unable to find all referenced elements in service wsdl and xsd.  Please make sure"
                + " that if there are imported wsdl or xsd that they all exist and are in the right location and are well formed and valid.";
            logger.error(mess, e);
            throw new Exception(mess, e);
        }

        // STEP 7: run the code generation tools
        SyncTool baseS = new SyncBase(this.baseDirectory, info);
        SyncTool servicesS = new SyncServices(this.baseDirectory, info);
        SyncTool serializerS = new SyncSerialization(this.baseDirectory, info);
        SyncTool propertiesS = new SyncProperties(this.baseDirectory, info);

        logger.info("Synchronizing the base files");
        baseS.sync();
        logger.info("Synchronizing the services");
        servicesS.sync();
        logger.info("Synchronizing the type mappings");
        serializerS.sync();
        logger.info("Synchronizing the service properties");
        propertiesS.sync();

        // STEP 8: run the extensions
        logger.info("Synchronizing with post processing extensions");
        // run any extensions that need to be ran
        if ((info.getExtensions() != null) && (info.getExtensions().getExtension() != null)) {
            ExtensionType[] extensions = info.getExtensions().getExtension();
            for (int i = 0; i < extensions.length; i++) {
                ExtensionType element = extensions[i];
                CodegenExtensionPostProcessor pp = ExtensionTools.getCodegenPostProcessor(element.getName());
                ServiceExtensionDescriptionType desc = ExtensionsLoader.getInstance().getServiceExtension(
                    element.getName());
                if (pp != null) {
                    pp.postCodegen(desc, info);
                }
            }
        }

        table = null;
        System.gc();

        generateDeploymentValidatorList(info);

        // make a copy of the model to compate with next time
        Utils.copyFile(new File(baseDirectory.getAbsolutePath() + File.separator
            + IntroduceConstants.INTRODUCE_XML_FILE), new File(baseDirectory.getAbsolutePath() + File.separator
            + IntroduceConstants.INTRODUCE_XML_FILE + ".prev"));

        // make a copy of the properties to compate with next time
        Utils.copyFile(new File(baseDirectory.getAbsolutePath() + File.separator
            + IntroduceConstants.INTRODUCE_PROPERTIES_FILE), new File(baseDirectory.getAbsolutePath() + File.separator
            + IntroduceConstants.INTRODUCE_PROPERTIES_FILE + ".prev"));
    }


    private void generateDeploymentValidatorList(ServiceInformation info) throws Exception {
        DeploymentValidatorDescriptor desc = new DeploymentValidatorDescriptor();
        List descs = new ArrayList();
        if (info.getExtensions() != null && info.getExtensions().getExtension() != null) {
            for (int i = 0; i < info.getExtensions().getExtension().length; i++) {
                ExtensionType ext = info.getExtensions().getExtension(i);
                ServiceExtensionDescriptionType extDesc = ExtensionsLoader.getInstance().getServiceExtension(
                    ext.getName());
                if (extDesc.getServiceDeploymentValidator() != null) {
                    ValidatorDescriptor vdesc = new ValidatorDescriptor(extDesc.getServiceDeploymentValidator());
                    descs.add(vdesc);
                }
            }
        }
        ValidatorDescriptor[] vdescs = new ValidatorDescriptor[descs.size()];
        descs.toArray(vdescs);
        desc.setValidatorDescriptor(vdescs);

        try {
            Utils.serializeDocument(info.getBaseDirectory() + File.separator + "tools" + File.separator
                + DEPLOYMENT_VALIDATOR_FILE, desc, DeploymentValidatorDescriptor.getTypeDesc().getXmlType());
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }


    private void populateClassnames(ServiceInformation info, MultiServiceSymbolTable table)
        throws SynchronizationException {

        // table.dump(System.out);
        // get the classnames from the axis symbol table
        // try {
        // logger.info("\n\nSTART OF NAMESPACES\n");
        // Utils.serializeObject(info.getServiceDescriptor().getNamespaces(),new
        // QName("gme://gov.nih.nci.cagrid.introduce/1/Namespace","NamespacesType"),new
        // PrintWriter(System.out));
        // logger.info("\n\nEND OF NAMESPACES\n");
        // } catch (Exception e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }
        if ((info.getNamespaces() != null) && (info.getNamespaces().getNamespace() != null)) {
            for (int i = 0; i < info.getNamespaces().getNamespace().length; i++) {
                NamespaceType ntype = info.getNamespaces().getNamespace(i);
                if (ntype.getSchemaElement() != null) {
                    for (int j = 0; j < ntype.getSchemaElement().length; j++) {
                        SchemaElementType type = ntype.getSchemaElement(j);
                        if (type.getClassName() == null) {
                            if (ntype.getNamespace().equals(IntroduceConstants.W3CNAMESPACE)) {
                                Type symtype = table.getType(new QName(ntype.getNamespace(), type.getType()));
                                // type may not be being used so axis will
                                // ignore it....
                                if (symtype != null) {
                                    type.setClassName(getRelativeClassName(symtype.getName()));
                                    type.setPackageName(getPackageName(symtype.getName()));
                                }
                            } else {
                                QName qname = new QName(ntype.getNamespace(), type.getType());
                                Element element = table.getElement(qname);
                                if (element == null) {
                                    table.dump(System.err);
                                    throw new SynchronizationException("Unable to find Element in symbol table for: "
                                        + qname);

                                }
                                type.setClassName(getRelativeClassName(element.getName()));
                                type.setPackageName(getPackageName(element.getName()));

                                if (type.getClassName() == null || type.getClassName().length() <= 0) {
                                    throw new SynchronizationException(
                                        "Error when setting finding classname mapping for element " + type.getType()
                                            + " , classname is null or empty.");
                                }
                            }
                        } else {
                            if ((type.getSerializer() == null) || (type.getDeserializer() == null)) {
                                throw new SynchronizationException(
                                    "When specifying a custom classname, you must also specify both a serializer and deserializer: "
                                        + type.getClassName());
                            }
                            // it the classname is already set then set the
                            // package name to the predefined
                            // package name in the namespace type
                            type.setPackageName(ntype.getPackageName());
                        }

                    }
                }

            }
        }

        // get the classnames from the axis symbol table
        if (info.getServices().getService() != null) {
            for (int serviceI = 0; serviceI < info.getServices().getService().length; serviceI++) {
                ServiceType service = info.getServices().getService(serviceI);
                if ((service.getMethods() != null) && (service.getMethods().getMethod() != null)) {
                    for (int i = 0; i < service.getMethods().getMethod().length; i++) {
                        MethodType mtype = service.getMethods().getMethod(i);
                        // process the inputs
                        if (!mtype.isIsImported()
                            || ((mtype.getImportInformation().getFromIntroduce() == null) || mtype
                                .getImportInformation().getFromIntroduce().booleanValue())) {
                            if ((mtype.getInputs() != null) && (mtype.getInputs().getInput() != null)) {
                                for (int j = 0; j < mtype.getInputs().getInput().length; j++) {
                                    MethodTypeInputsInput inputParam = mtype.getInputs().getInput(j);
                                    SchemaInformation namespace = CommonTools.getSchemaInformation(
                                        info.getNamespaces(), inputParam.getQName());
                                    if (!namespace.getNamespace().getNamespace()
                                        .equals(IntroduceConstants.W3CNAMESPACE)) {
                                        QName qname = null;
                                        if (mtype.isIsImported()) {
                                            qname = new QName(mtype.getImportInformation().getNamespace(), ">>"
                                                + CommonTools.upperCaseFirstCharacter(mtype.getName()) + "Request>"
                                                + inputParam.getName());
                                        } else {
                                            qname = new QName(service.getNamespace(), ">>"
                                                + CommonTools.upperCaseFirstCharacter(mtype.getName()) + "Request>"
                                                + inputParam.getName());
                                        }

                                        Type type = table.getType(qname);
                                        if ((type == null) && !mtype.isIsImported()) {
                                            table.dump(System.err);
                                            throw new SynchronizationException(
                                                "Unable to find Element in symbol table for: " + qname);
                                        } else if (type == null) {
                                            logger.error("ERROR: The lement cannot be found in the symbol table: "
                                                + mtype.getName() + ":" + inputParam.getName());
                                        } else {

                                            if (mtype.isIsImported()) {
                                                inputParam.setContainerClass(mtype.getImportInformation()
                                                    .getPackageName()
                                                    + "." + getRelativeClassName(type.getName()));
                                            } else {
                                                inputParam.setContainerClass(service.getPackageName() + ".stubs."
                                                    + getRelativeClassName(type.getName()));
                                            }
                                        }
                                    }

                                }
                            }
                        }

                        // process the messages so that we can find the
                        // types
                        // and the part names
                        logger.debug("LOOKING AT METHOD: " + mtype.getName());
                        // populate the input message class name
                        QName messageQName = null;
                        if (mtype.isIsImported() && (mtype.getImportInformation().getInputMessage() != null)
                            && !mtype.getImportInformation().getInputMessage().equals("")) {
                            messageQName = mtype.getImportInformation().getInputMessage();
                        } else if (mtype.isIsImported()) {
                            messageQName = new QName(mtype.getImportInformation().getNamespace(), CommonTools
                                .upperCaseFirstCharacter(mtype.getName())
                                + "Request");
                        } else {
                            messageQName = new QName(service.getNamespace(), CommonTools.upperCaseFirstCharacter(mtype
                                .getName())
                                + "Request");
                        }
                        MessageEntry type = table.getMessageEntry(messageQName);

                        if (type != null) {
                            if (type.getMessage().getParts() != null && type.getMessage().getParts().values() != null
                                && type.getMessage().getParts().size() > 0) {
                                Object obj = type.getMessage().getParts().values().iterator().next();
                                PartImpl messagePart = (PartImpl) obj;
                                if (messagePart.getElementName() != null) {
                                    Element element = table.getElement(messagePart.getElementName());

                                    mtype.setInputMessageClass(element.getName());
                                    mtype.setBoxedInputParameter(CommonTools.lowerCaseFirstCharacter(messagePart
                                        .getName()));
                                } else if (messagePart.getTypeName() != null) {
                                    Type messtype = table.getType(messagePart.getTypeName());
                                    if (messtype != null) {
                                        mtype.setInputMessageClass(messtype.getName());
                                        mtype.setBoxedInputParameter(CommonTools.lowerCaseFirstCharacter(messagePart
                                            .getName()));
                                    }
                                }
                            } else {
                                logger.warn("WARNING: message type does not have any parts: " + messageQName);
                            }
                        } else {
                            logger.warn("WARNING: Cannot find input message entry: " + messageQName);
                        }

                        // pupulate the output message class name
                        messageQName = null;
                        if (mtype.isIsImported() && (mtype.getImportInformation().getOutputMessage() != null)
                            && !mtype.getImportInformation().getOutputMessage().equals("")) {
                            messageQName = mtype.getImportInformation().getOutputMessage();
                        } else if (mtype.isIsImported()) {
                            messageQName = new QName(mtype.getImportInformation().getNamespace(), CommonTools
                                .upperCaseFirstCharacter(mtype.getName())
                                + "Response");
                        } else {
                            messageQName = new QName(service.getNamespace(), CommonTools.upperCaseFirstCharacter(mtype
                                .getName())
                                + "Response");
                        }
                        type = table.getMessageEntry(messageQName);

                        if (type != null) {
                            if (type.getMessage().getParts() != null && type.getMessage().getParts().values() != null
                                && type.getMessage().getParts().size() > 0) {
                                PartImpl messagePart = (PartImpl) type.getMessage().getParts().values().iterator()
                                    .next();
                                if (messagePart.getElementName() != null) {
                                    Element element = table.getElement(messagePart.getElementName());
                                    mtype.setOutputMessageClass(element.getName());
                                    mtype.setBoxedOutputParameter(CommonTools.lowerCaseFirstCharacter(messagePart
                                        .getName()));

                                } else if (messagePart.getTypeName() != null) {
                                    Type messType = table.getType(messagePart.getTypeName());
                                    mtype.setOutputMessageClass(messType.getName());
                                    mtype.setBoxedOutputParameter(CommonTools.lowerCaseFirstCharacter(messagePart
                                        .getName()));
                                }
                            } else {
                                logger.warn("WARNING: message type does not have any parts: " + messageQName);
                            }
                        } else {
                            logger.warn("WARNING: Cannot find output message entry: " + messageQName);
                        }

                    }
                }
            }
        }
    }


    private void updateServiceExtensions(ServiceInformation info) throws Exception {
        Properties oldProps = new Properties();
        try {
            oldProps.load(new FileInputStream(new File(baseDirectory.getAbsolutePath() + File.separator
                + IntroduceConstants.INTRODUCE_PROPERTIES_FILE + ".prev").getAbsolutePath()));
        } catch (Exception e) {
            // do nothing this might be right after creation, therefore no prev
            // file exists
        }
        if (oldProps.size() >= 0 && oldProps.getProperty(IntroduceConstants.INTRODUCE_SKELETON_EXTENSIONS) != null) {
            String oldExtensions = oldProps.getProperty(IntroduceConstants.INTRODUCE_SKELETON_EXTENSIONS);
            String currentExtensions = info.getIntroduceServiceProperties().getProperty(
                IntroduceConstants.INTRODUCE_SKELETON_EXTENSIONS);
            StringTokenizer strtok = new StringTokenizer(oldExtensions, ",", false);
            List oldExts = new ArrayList<String>();
            while (strtok.hasMoreElements()) {
                String next = strtok.nextToken();
                oldExts.add(next);
            }

            // process the new ones and compare them to the old ones
            List newExts = new ArrayList<String>();
            strtok = new StringTokenizer(currentExtensions, ",", false);
            while (strtok.hasMoreElements()) {
                String next = strtok.nextToken();
                newExts.add(next);
                if (!oldExts.contains(next)) {
                    logger.info("A new extension needs added: " + next);
                    CreationExtensionPostProcessor pp = null;
                    ServiceExtensionDescriptionType desc = ExtensionsLoader.getInstance().getServiceExtension(next);

                    pp = ExtensionTools.getCreationPostProcessor(next);

                    if (pp != null) {
                        pp.postCreate(desc, info);
                    }
                }

            }

            // process the old ones and compare to the new ones
            Iterator it = oldExts.iterator();
            while (it.hasNext()) {
                String next = (String) it.next();
                if (!newExts.contains(next)) {
                    logger.info("An extension needs removed: " + next);
                    ServiceExtensionRemover remover = ExtensionTools.getServiceExtensionRemover(next);
                    if (remover != null) {
                        remover.remove(ExtensionsLoader.getInstance().getServiceExtension(next), info);
                    }
                    ExtensionType[] modifiedExtensionsArray = new ExtensionType[info.getExtensions().getExtension().length - 1];
                    int kept = 0;
                    for (int i = 0; i < info.getExtensions().getExtension().length; i++) {
                        ExtensionType extType = info.getExtensions().getExtension(i);
                        if (!extType.getName().equals(next)) {
                            modifiedExtensionsArray[kept++] = extType;
                        }
                    }
                    info.getExtensions().setExtension(modifiedExtensionsArray);
                }
            }

        }
    }


    private void updateServiceProviders(ServiceInformation info) {
        try {
            ServiceDescription sd = null;
            try {
                sd = (ServiceDescription) Utils.deserializeDocument(new File(baseDirectory.getAbsolutePath()
                    + File.separator + IntroduceConstants.INTRODUCE_XML_FILE + ".prev").getAbsolutePath(),
                    ServiceDescription.class);
            } catch (Exception e) {
                // do nothing this might be right after creation, therefore no
                // prev file exists
            }
            if (sd != null && (sd.getServices() != null) && (sd.getServices().getService() != null)) {
                for (int serviceI = 0; serviceI < sd.getServices().getService().length; serviceI++) {
                    ServiceType oldService = sd.getServices().getService(serviceI);
                    ServiceType newService = CommonTools.getService(info.getServices(), oldService.getName());
                    if (oldService != null && newService != null) {
                        if (oldService.getResourceFrameworkOptions().getNotification() == null
                            && newService.getResourceFrameworkOptions().getNotification() != null) {
                            ProviderTools.addSubscribeResourceProvider(newService, info);
                            logger.info("Addedd subscribe provider to service: " + newService.getName());
                        } else if (oldService.getResourceFrameworkOptions().getNotification() != null
                            && newService.getResourceFrameworkOptions().getNotification() == null) {
                            ProviderTools.removeSubscribeResourceProvider(newService, info);
                            logger.info("Removed subscribe provider to service: " + newService.getName());
                        }
                        if (oldService.getResourceFrameworkOptions().getResourcePropertyManagement() == null
                            && newService.getResourceFrameworkOptions().getResourcePropertyManagement() != null) {
                            ProviderTools.addResourcePropertiesManagementResourceFrameworkOption(newService, info);
                            logger.info("Addedd resource properties management provider to service: "
                                + newService.getName());
                        } else if (oldService.getResourceFrameworkOptions().getResourcePropertyManagement() != null
                            && newService.getResourceFrameworkOptions().getResourcePropertyManagement() == null) {
                            ProviderTools.removeResourcePropertiesManagementResourceFrameworkOption(newService, info);
                            logger.info("Removed resource properties management provider to service: "
                                + newService.getName());
                        }
                        if (oldService.getResourceFrameworkOptions().getLifetime() == null
                            && newService.getResourceFrameworkOptions().getLifetime() != null) {
                            ProviderTools.addLifetimeResourceProvider(newService, info);
                            logger.info("Addedd lifetime provider to service: " + newService.getName());
                        } else if (oldService.getResourceFrameworkOptions().getLifetime() != null
                            && newService.getResourceFrameworkOptions().getLifetime() == null) {
                            ProviderTools.removeLifetimeResourceProvider(newService, info);
                            logger.info("Removed lifetime provider to service: " + newService.getName());
                        }
                    }

                }
            }
        } catch (Exception e) {
            logger.error(e);
        }
        if ((info.getServices() != null) && (info.getServices().getService() != null)) {
            for (int serviceI = 0; serviceI < info.getServices().getService().length; serviceI++) {
                ServiceType service = info.getServices().getService(serviceI);
                if (service.getResourceFrameworkOptions().getNotification() != null) {

                }
            }
        }

    }


    private void createNewServices(ServiceInformation info) {
        List newServices = new ArrayList();
        if ((info.getServices() != null) && (info.getServices().getService() != null)) {
            for (int serviceI = 0; serviceI < info.getServices().getService().length; serviceI++) {
                File serviceDir = new File(info.getBaseDirectory() + File.separator + "src" + File.separator
                    + CommonTools.getPackageDir(info.getServices().getService(serviceI)));
                if (!serviceDir.exists()) {
                    newServices.add(info.getServices().getService(serviceI));
                }
            }
        }

        // add all new service information and
        // add the new service description to the service.wsdd
        File serverConfigF = new File(info.getBaseDirectory().getAbsolutePath() + File.separator + "server-config.wsdd");

        Document serverConfigDoc = null;
        try {
            serverConfigDoc = XMLUtilities.fileNameToDocument(serverConfigF.getAbsolutePath());
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        File jndiConfigF = new File(info.getBaseDirectory().getAbsolutePath() + File.separator + "jndi-config.xml");

        Document serverConfigJNDIDoc = null;
        try {
            serverConfigJNDIDoc = XMLUtilities.fileNameToDocument(jndiConfigF.getAbsolutePath());
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        for (int i = 0; i < newServices.size(); i++) {
            ServiceType newService = (ServiceType) newServices.get(i);
            SkeletonSourceCreator ssc = new SkeletonSourceCreator();
            SkeletonSchemaCreator sschc = new SkeletonSchemaCreator();
            SkeletonEtcCreator sec = new SkeletonEtcCreator();
            SkeletonSecurityOperationProviderCreator ssopc = new SkeletonSecurityOperationProviderCreator();
            try {
                logger.debug("Adding Service for: " + newService.getName());
                ssc.createSkeleton(info.getBaseDirectory(), info, newService);
                sschc.createSkeleton(info.getBaseDirectory(), info, newService);
                ssopc.createSkeleton(new SpecificServiceInformation(info, newService));
                sec.createSkeleton(info, newService);

                // if this is a new service we need to add it's new "service"
                // element to the WSDD
                NewServerConfigTemplate newServerConfigT = new NewServerConfigTemplate();
                String newServerConfigS = newServerConfigT.generate(new SpecificServiceInformation(info, newService));
                org.jdom.Element newServiceElement = XMLUtilities.stringToDocument(newServerConfigS).getRootElement();
                serverConfigDoc.getRootElement().addContent(0, newServiceElement.detach());

                // when i add this new service i need to make a resource link in
                // every other service for this services resource home
                // <resourceLink name="home"
                // target="java:comp/env/services/SERVICE-INSTANCE-PREFIX/HelloWorld/home"
                // />
                org.jdom.Element resourceLinkEl = new org.jdom.Element("resourceLink", Namespace
                    .getNamespace("http://wsrf.globus.org/jndi/config"));
                resourceLinkEl.setAttribute("name", CommonTools.lowerCaseFirstCharacter(newService.getName()) + "Home");
                resourceLinkEl.setAttribute("target", "java:comp/env/services/SERVICE-INSTANCE-PREFIX/"
                    + newService.getName() + "/home");
                List children = serverConfigJNDIDoc.getRootElement().getChildren();
                for (int childI = 0; childI < children.size(); childI++) {
                    org.jdom.Element child = (org.jdom.Element) children.get(childI);
                    if (child.getName().equals("service")) {
                        child.addContent((org.jdom.Element) resourceLinkEl.clone());
                    }
                }

                // if this is a new service we need to add it's new "service"
                // element to the JNDI
                NewServiceJNDIConfigTemplate jndiConfigT = new NewServiceJNDIConfigTemplate();
                org.jdom.Element newServiceJNDIElement = XMLUtilities.stringToDocument(
                    jndiConfigT.generate(new SpecificServiceInformation(info, newService))).getRootElement();
                serverConfigJNDIDoc.getRootElement().addContent(0, newServiceJNDIElement.detach());

                // we now need to process the resource framework options and add
                // what
                // ever providers need to be added
                if (newService.getResourceFrameworkOptions().getLifetime() != null) {
                    ProviderTools.addLifetimeResourceProvider(newService, info);
                }
                if (newService.getResourceFrameworkOptions().getNotification() != null) {
                    ProviderTools.addSubscribeResourceProvider(newService, info);
                }
                if (newService.getResourceFrameworkOptions().getResourcePropertyManagement() != null) {
                    ProviderTools.addResourcePropertiesManagementResourceFrameworkOption(newService, info);
                }

            } catch (Exception e) {
                logger.error(e);
            }
        }
        String serverConfigS;
        String serverConfigJNDIS;
        try {
            serverConfigS = XMLUtilities.formatXML(XMLUtilities.documentToString(serverConfigDoc));
            FileWriter serverConfigFW = new FileWriter(serverConfigF);
            serverConfigFW.write(serverConfigS);
            serverConfigFW.close();

            serverConfigJNDIS = XMLUtilities.formatXML(XMLUtilities.documentToString(serverConfigJNDIDoc));
            FileWriter jndiConfigFW = new FileWriter(jndiConfigF);
            jndiConfigFW.write(serverConfigJNDIS);
            jndiConfigFW.close();
        } catch (Exception e) {
            logger.error(e);
        }
    }


    /**
     * Walk the model and build up a set of namespaces to not generate classes
     * for (from schemas in wsdl) NOTE: must be called BEFORE populateClassnames
     * TODO: we may handle this differently in the future if we have to add
     * specifications for custom serialization/deserialization.
     * 
     * @param info
     * @throws MalformedNamespaceException
     */
    private Set generateNamespaceExcludesSet(ServiceInformation info) throws Exception {
        Set excludeSet = new HashSet();
        File schemaDir = new File(this.baseDirectory.getAbsolutePath() + File.separator + "schema" + File.separator
            + info.getIntroduceServiceProperties().getProperty(IntroduceConstants.INTRODUCE_SKELETON_SERVICE_NAME));
        // exclude namespaces that have FQN for metadata class
        // get the classnames from the axis symbol table
        if ((info.getNamespaces() != null) && (info.getNamespaces().getNamespace() != null)) {
            for (int i = 0; i < info.getNamespaces().getNamespace().length; i++) {
                NamespaceType ntype = info.getNamespaces().getNamespace(i);

                if ((ntype.getGenerateStubs() != null) && !ntype.getGenerateStubs().booleanValue()
                    && !ntype.getNamespace().equals(IntroduceConstants.W3CNAMESPACE)) {
                    // the model explictly says not to generate stubs
                    excludeSet.add(ntype.getNamespace());
                } else if (ntype.getSchemaElement() != null) {
                    // only needed for backwards compatibility before the gui
                    // always set the generateStubs attribute
                    for (int j = 0; j < ntype.getSchemaElement().length; j++) {
                        SchemaElementType type = ntype.getSchemaElement(j);
                        if (type.getClassName() != null) {
                            if (ntype.getLocation() != null) {
                                // the namespace contains customly serialized
                                // beans... so don't generate stubs

                                excludeSet.add(ntype.getNamespace());
                                // this schema is excluded.. no need to check
                                // the rest of the schemaelements
                                break;
                            }
                        }
                    }
                }
            }
        }

        return excludeSet;
    }


    private Set generateSOAPStubExcludesSet(ServiceInformation info) throws Exception {
        Set excludeSet = new HashSet();
        File schemaDir = new File(this.baseDirectory.getAbsolutePath() + File.separator + "schema" + File.separator
            + info.getIntroduceServiceProperties().getProperty(IntroduceConstants.INTRODUCE_SKELETON_SERVICE_NAME));
        // exclude namespaces that have FQN for metadata class
        // get the classnames from the axis symbol table
        if ((info.getNamespaces() != null) && (info.getNamespaces().getNamespace() != null)) {
            for (int i = 0; i < info.getNamespaces().getNamespace().length; i++) {
                NamespaceType ntype = info.getNamespaces().getNamespace(i);

                if (ntype.getSchemaElement() != null) {
                    for (int j = 0; j < ntype.getSchemaElement().length; j++) {
                        SchemaElementType type = ntype.getSchemaElement(j);
                        if (type.getClassName() != null) {
                            if (ntype.getLocation() != null) {
                                // the namespace contains customly serialized
                                // beans... so don't generate stubs

                                excludeSet.add(ntype.getNamespace());
                                SyncUtils.walkSchemasGetNamespaces(schemaDir + File.separator + ntype.getLocation(),
                                    excludeSet, new HashSet(), new HashSet());
                                // this schema is excluded.. no need to check
                                // the rest of the schemaelements
                                break;
                            }
                        }
                    }
                }
            }
        }

        return excludeSet;
    }


    private void writeNamespaceMappings(ServiceInformation info) throws IOException {
        NamespaceMappingsTemplate namespaceMappingsT = new NamespaceMappingsTemplate();
        String namespaceMappingsS = namespaceMappingsT.generate(info);
        File namespaceMappingsF = new File(this.baseDirectory.getAbsolutePath() + File.separator
            + IntroduceConstants.NAMESPACE2PACKAGE_MAPPINGS_FILE);
        FileWriter namespaceMappingsFW = new FileWriter(namespaceMappingsF);
        namespaceMappingsFW.write(namespaceMappingsS);
        namespaceMappingsFW.close();
    }


    private void mergeNamespaces() throws Exception {
        String cmd = AntTools.getAntMergeCommand(this.baseDirectory.getAbsolutePath());
        Process p = CommonTools.createAndOutputProcess(cmd);
        p.waitFor();
        if (p.exitValue() != 0) {
            throw new Exception("Service merge exited abnormally");
        }
    }


    private void syncWSDL(ServiceInformation info, File schemaDir) throws Exception {
        // get the classnames from the axis symbol table
        if (info.getServices().getService() != null) {
            for (int serviceI = 0; serviceI < info.getServices().getService().length; serviceI++) {
                // rewrite the wsdl for each service....
                ServiceType service = info.getServices().getService(serviceI);
                ServiceWSDLTemplate serviceWSDLT = new ServiceWSDLTemplate();
                String serviceWSDLS = serviceWSDLT.generate(new SpecificServiceInformation(info, service));
                File serviceWSDLF = new File(schemaDir.getAbsolutePath()
                    + File.separator
                    + info.getIntroduceServiceProperties().getProperty(
                        IntroduceConstants.INTRODUCE_SKELETON_SERVICE_NAME) + File.separator + service.getName()
                    + ".wsdl");
                FileWriter serviceWSDLFW = new FileWriter(serviceWSDLF);
                serviceWSDLFW.write(serviceWSDLS);
                serviceWSDLFW.close();

            }
        }
        if (info.getServices().getService() != null) {
            for (int serviceI = 0; serviceI < info.getServices().getService().length; serviceI++) {
                // rewrite the wsdl for each service....
                ServiceType service = info.getServices().getService(serviceI);
                // for each service add any imported operations.....
                if ((service.getMethods() != null) && (service.getMethods().getMethod() != null)) {
                    for (int methodI = 0; methodI < service.getMethods().getMethod().length; methodI++) {
                        MethodType method = service.getMethods().getMethod(methodI);
                        if (method.isIsImported()) {
                            SyncUtils.addImportedOperationToService(method, new SpecificServiceInformation(info,
                                service));
                        }
                    }
                }
            }
        }

        writeNamespaceMappings(info);

    }


    public static void main(String[] args) {
        Options options = new Options();
        Option directoryOpt = new Option(DIR_OPT, DIR_OPT_FULL, true, "The include tool directory");
        options.addOption(directoryOpt);

        CommandLineParser parser = new PosixParser();

        File directory = null;

        try {
            CommandLine line = parser.parse(options, args);
            directory = new File(line.getOptionValue(DIR_OPT));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SyncTools sync = new SyncTools(directory);
        try {
            sync.sync();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }


    /**
     * Adds a fault to a service's schema.
     * 
     * @param exceptionName
     *            The name of the exception to add
     * @param info
     *            The service information for the service
     * @return true indicates the fault was new to the service, false means it
     *         was already in the service's schema
     * @throws Exception
     */
    public static boolean addFault(String exceptionName, SpecificServiceInformation info) throws Exception {
        // just for backwars compatibility with some earlier non releases
        // i will check for the existance of the xsd for service before i
        // attempt
        // to add faults to it.
        String serviceSchemaDir = info.getBaseDirectory() + File.separator + "schema" + File.separator
            + info.getServices().getService(0).getName();
        File schemaFile = new File(serviceSchemaDir + File.separator + info.getService().getName() + "Types.xsd");

        if (!schemaFile.exists()) {
            ServiceXSDTemplate serviceXSDT = new ServiceXSDTemplate();
            String serviceXSDS = serviceXSDT.generate(info);
            File serviceXSDF = new File(schemaFile.getAbsolutePath());
            FileWriter serviceXSDFW = new FileWriter(serviceXSDF);
            serviceXSDFW.write(serviceXSDS);
            serviceXSDFW.close();
        }

        // add the new fault to the schema for them automatically
        // <element name="<%=exception.getQname().getLocalPart() %>"
        // type="<%=exception.getQname().getLocalPart() %>Type"/>
        // <complexType name = name="<%=exception.getQname().getLocalPart()
        // %>Type">
        // <complexContent>
        // <extension base="wsrbf:BaseFaultType"/>
        // </complexContent>
        // </complexType>

        org.jdom.Element faultEl = new org.jdom.Element("element", org.jdom.Namespace
            .getNamespace(IntroduceConstants.W3CNAMESPACE));
        faultEl.setAttribute("name", exceptionName);
        faultEl.setAttribute("type", "tns:" + exceptionName);
        org.jdom.Element faultType = new org.jdom.Element("complexType", org.jdom.Namespace
            .getNamespace(IntroduceConstants.W3CNAMESPACE));
        faultType.setAttribute("name", exceptionName);
        org.jdom.Element ccEl = new org.jdom.Element("complexContent", org.jdom.Namespace
            .getNamespace(IntroduceConstants.W3CNAMESPACE));
        org.jdom.Element extEl = new org.jdom.Element("extension", org.jdom.Namespace
            .getNamespace(IntroduceConstants.W3CNAMESPACE));
        extEl.setAttribute("base", "wsrbf:BaseFaultType");
        faultType.addContent(ccEl);
        ccEl.addContent(extEl);

        boolean exceptionExists = false;
        Document doc = XMLUtilities.fileNameToDocument(schemaFile.getAbsolutePath());

        List children = doc.getRootElement().getChildren();
        for (int i = 0; i < children.size(); i++) {
            org.jdom.Element el = (org.jdom.Element) children.get(i);
            if (el.getAttributeValue("name") != null && el.getAttributeValue("name").equals(exceptionName)) {
                exceptionExists = true;
                break;
            }

        }
        if (!exceptionExists) {
            doc.getRootElement().addContent(faultEl.detach());
            doc.getRootElement().addContent(faultType.detach());
            FileWriter fw = new FileWriter(schemaFile);
            fw.write(XMLUtilities.formatXML(XMLUtilities.documentToString(doc)));
            fw.close();

            NamespaceType newType = CommonTools.createNamespaceType(schemaFile.getAbsolutePath(), new File(
                serviceSchemaDir));
            newType.setPackageName(info.getService().getPackageName() + ".stubs.types");
            // now add the new SchemaElement to the NamespaceType
            boolean found = false;
            for (int i = 0; i < info.getNamespaces().getNamespace().length; i++) {
                NamespaceType nst = info.getNamespaces().getNamespace(i);
                if (nst.getNamespace().equals(info.getService().getNamespace() + "/types")) {
                    found = true;
                    newType.setPackageName(info.getNamespaces().getNamespace(i).getPackageName());
                    info.getNamespaces().getNamespace()[i] = newType;
                }
            }
            if (!found) {
                CommonTools.addNamespace(info.getServiceDescriptor(), newType);
            }
        }
        return !exceptionExists;
    }
}
