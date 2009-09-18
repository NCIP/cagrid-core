package gov.nih.nci.cagrid.data.codegen;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.data.DataServiceConstants;
import gov.nih.nci.cagrid.introduce.beans.method.MethodType;
import gov.nih.nci.cagrid.introduce.beans.service.ServiceType;
import gov.nih.nci.cagrid.introduce.codegen.services.methods.SyncHelper;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;
import gov.nih.nci.cagrid.introduce.extension.CodegenExtensionException;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

/** 
 *  BDTFeatureCodegen
 *  Generates source code for the BDT data service and writes it
 *  into the service impl and BDT Resource
 * 
 * @author David Ervin
 * 
 * @created Mar 13, 2007 1:10:16 PM
 * @version $Id: BDTFeatureCodegen.java,v 1.3 2007-08-24 14:14:50 dervin Exp $ 
 */
public class BDTFeatureCodegen extends FeatureCodegen {
    public static final String TODO_IMPL_LINE = "//TODO: Implement me";
    
    // service impl edits
    public static final String SERVICE_START = "BDTResource thisResource = (BDTResource)bdtHome.find(bdtResourceKey);";
    public static final String SERVICE_LINE1 = "\t\t\tString classToQnameMapfile = gov.nih.nci.cagrid.data.service.ServiceConfigUtil.getClassToQnameMappingsFile();\n";
    public static final String SERVICE_LINE2 = "\t\t\tjava.io.InputStream wsddStream = new java.io.FileInputStream(gov.nih.nci.cagrid.data.service.ServiceConfigUtil\n" 
        + "\t\t\t\t.getConfigProperty(gov.nih.nci.cagrid.data.DataServiceConstants.SERVER_CONFIG_LOCATION));\n";
    public static final String SERVICE_LINE3 = "\t\t\tthisResource.initialize(cqlQuery, classToQnameMapfile, wsddStream);\n";    
    
    // an assortment of method signatures to add implementations for
    public static final String INIT_METHOD_SIGNATURE = "void initialize(gov.nih.nci.cagrid.cqlquery.CQLQuery query, " +
        "String classToQnameMapfile, java.io.InputStream wsddStream) " +
        "throws java.rmi.RemoteException, gov.nih.nci.cagrid.data.service.DataServiceInitializationException";
    public static final String INIT_METHOD_SIGNATURE_FRAGMENT = "void initialize(";
    public static final String ENUM_METHOD_SIGNATURE = "public EnumIterator createEnumeration() throws BDTException";
    public static final String GET_METHOD_SIGNATURE = "public AnyXmlType get() throws BDTException";
    public static final String GRIDFTP_METHOD_SIGNATURE = "public org.apache.axis.types.URI[] getGridFTPURLs() throws BDTException";
    public static final String DESTROY_METHOD_SIGNATURE = "public void remove() throws ResourceException";

    public static final String RESOURCE_CLASS_DECLARATION = "public class BDTResource extends BDTResourceBase implements BDTResourceI {";
    public static final String HELPER_VAR_DECLARATION = "private gov.nih.nci.cagrid.data.bdt.service.BDTResourceHelper helper;";
    
    // method bodies
    public static final String INIT_METHOD = 
        "\tvoid initialize(gov.nih.nci.cagrid.cqlquery.CQLQuery query, \n" + 
        "\t\t\tString classToQnameMapfile, \n" + 
        "\t\t\tjava.io.InputStream wsddStream) throws java.rmi.RemoteException, gov.nih.nci.cagrid.data.service.DataServiceInitializationException {\n" + 
        "\t\tthis.helper = new gov.nih.nci.cagrid.data.bdt.service.BDTResourceHelper(\n" + 
        "\t\t\tquery, classToQnameMapfile, wsddStream);\n" + 
        "\t}\n";
    public static final String ENUM_METHOD_IMPL = 
        "\ttry {\n" +
        "\t\treturn helper.createEnumIterator();\n" +
        "\t} catch (gov.nih.nci.cagrid.data.faults.QueryProcessingExceptionType ex) {\n" +
        "\t\tthrow new BDTException(\"Error processing query: \" + ex.getMessage(), ex);\n" +
        "\t} catch (gov.nih.nci.cagrid.data.faults.MalformedQueryExceptionType ex) {\n" +
        "\t\tthrow new BDTException(\"Improperly formed query: \" + ex.getMessage(), ex);\n" +
        "\t}\n";
    public static final String GET_METHOD_IMPL =
        "\ttry {\n" +
        "\t\treturn helper.resultsAsAnyType();\n" +
        "\t} catch (gov.nih.nci.cagrid.data.QueryProcessingException ex) {\n"+
        "\t\tthrow new BDTException(\"Error processing query: \" + ex.getMessage(), ex);\n" +
        "\t} catch (gov.nih.nci.cagrid.data.MalformedQueryException ex) {\n" +
        "\t\tthrow new BDTException(\"Improperly formed query: \" + ex.getMessage(), ex);\n" + 
        "\t}\n";
    public static final String DESTROY_METHOD_IMPL = 
        "\thelper.cleanUp();\n";
    
    
    public BDTFeatureCodegen(ServiceInformation info, ServiceType mainService, Properties serviceProps) {
        super(info, mainService, serviceProps);
    }


    public void codegenFeature() throws CodegenExtensionException {
        editBdtImpl();
        
        // load the source code
        String basePackage = getServiceInformation().getServices().getService()[0].getPackageName();
        // full name of the service impl class
        String fullClassName = basePackage + ".service.BDTResource";
        // file name of the service impl java source
        String sourceFileName = getServiceInformation().getBaseDirectory().getAbsolutePath() + File.separator + "src" + File.separator 
            + fullClassName.replace('.', File.separatorChar) + ".java";
        // read the source file in
        StringBuffer source = null;
        try {
            source = Utils.fileToStringBuffer(new File(sourceFileName));
        } catch (IOException ex) {
            throw new CodegenExtensionException("Error reading BDT Resource source file: " + ex.getMessage(), ex);
        }
        
        boolean edited = false;
        
        edited |= editInitializeMethod(source);
        
        edited |= performBaseEdits(source);
        
        edited |= editCreateEnumerationMethod(source);
        
        edited |= editGetMethod(source);
        
        edited |= editDestroyMethod(source);
        
        if (edited) {
            // write the source file out
            try {
                Utils.stringBufferToFile(source, sourceFileName);
            } catch (IOException ex) {
                throw new CodegenExtensionException("Error writing edited source file: " + ex.getMessage(), ex);
            }
        }
    }
    
    
    private void editBdtImpl() throws CodegenExtensionException {
        // figure out what the method signature is
        MethodType bdtQueryMethod = null;
        MethodType[] allMethods = getMainService().getMethods().getMethod();
        for (int i = 0; i < allMethods.length; i++) {
            MethodType current = allMethods[i];
            if (current.getName().equals(DataServiceConstants.BDT_QUERY_METHOD_NAME)
                && current.getInputs().getInput(0).getQName().equals(DataServiceConstants.CQL_QUERY_QNAME)) {
                bdtQueryMethod = current;
                break;
            }
        }
        if (bdtQueryMethod == null) {
            throw new CodegenExtensionException("No BDT query method found!");
        }
        
        String methodSignatureStart = null;
        // insert the new client method
        if (bdtQueryMethod.isIsImported() && (bdtQueryMethod.getImportInformation().getFromIntroduce() != null)
            && !bdtQueryMethod.getImportInformation().getFromIntroduce().booleanValue()) {
            methodSignatureStart = SyncHelper.createBoxedSignatureStringFromMethod(bdtQueryMethod) + " " 
                + SyncHelper.createClientExceptions(bdtQueryMethod, getServiceInformation());
        } else {
            methodSignatureStart = SyncHelper.createUnBoxedSignatureStringFromMethod(bdtQueryMethod, getServiceInformation()) + " "
                + SyncHelper.createExceptions(bdtQueryMethod, getServiceInformation());
        }
        System.out.println("Searching for method with signature:");
        System.out.println("\t" + methodSignatureStart);
        
        String serviceName = getServiceInformation().getServices().getService()[0].getName();
        String basePackage = getServiceInformation().getServices().getService()[0].getPackageName();
        // full name of the service impl class
        String fullClassName = basePackage + ".service." + serviceName + "Impl";
        // file name of the service impl java source
        String sourceFileName = getServiceInformation().getBaseDirectory().getAbsolutePath() + File.separator + "src" + File.separator 
            + fullClassName.replace('.', File.separatorChar) + ".java";
        // read the source file in
        StringBuffer source = null;
        try {
            source = Utils.fileToStringBuffer(new File(sourceFileName));
        } catch (IOException ex) {
            throw new CodegenExtensionException("Error reading service source file: " + ex.getMessage(), ex);
        }
        
        // build the edit
        StringBuffer edit = new StringBuffer();
        edit.append(SERVICE_LINE1).append(SERVICE_LINE2).append(SERVICE_LINE3);
        if (source.indexOf(edit.toString()) == -1) {
            System.out.println("Source File Contents:");
            System.out.println(source.toString());
            // edit has never been performed, perform edits
            // find method start
            int methodStart = source.indexOf(methodSignatureStart);
            if (methodStart == -1) {
                throw new CodegenExtensionException("No signature for BDT query method found!");
            }
            int startIndex = source.indexOf(SERVICE_START, methodStart);
            if (startIndex == -1) {
                throw new CodegenExtensionException("BDT implementation insertion point not found!");
            }
            startIndex += SERVICE_START.length();
            // add the source
            source.insert(startIndex, "\n");
            startIndex += "\n".length();
            source.insert(startIndex, edit.toString());
        }

        // write the source file back out
        try {
            Utils.stringBufferToFile(source, sourceFileName);
        } catch (IOException ex) {
            throw new CodegenExtensionException("Error writing service source file: " + ex.getMessage(), ex);
        }
    }
    
    
    private boolean performBaseEdits(StringBuffer source) throws CodegenExtensionException {
        boolean edited = false;
        int classStartIndex = source.indexOf(RESOURCE_CLASS_DECLARATION);
        if (classStartIndex == -1) {
            throw new CodegenExtensionException("Class declaration not found");
        }
        classStartIndex += RESOURCE_CLASS_DECLARATION.length();
        int helperVariableDeclarationIndex = source.indexOf(HELPER_VAR_DECLARATION, classStartIndex);
        if (helperVariableDeclarationIndex == -1) {
            // must add declaration
            int nextLineStart = source.indexOf("\n", classStartIndex) + 1;
            source.insert(nextLineStart, HELPER_VAR_DECLARATION);
            edited = true;
        }
        return edited;
    }
    
    
    private boolean editInitializeMethod(StringBuffer source) throws CodegenExtensionException {
        boolean edited = false;
        int initMethodStartIndex = source.indexOf(INIT_METHOD_SIGNATURE_FRAGMENT);
        if (initMethodStartIndex == -1) {
            int endIndex = source.lastIndexOf("}");
            source.insert(endIndex, INIT_METHOD);
            edited = true;
        }
        return edited;
    }
    
    
    private boolean editCreateEnumerationMethod(StringBuffer source) throws CodegenExtensionException {
        boolean edited = false;
        int enumMethodStartIndex = source.indexOf(ENUM_METHOD_SIGNATURE);
        if (enumMethodStartIndex == -1) {
            throw new CodegenExtensionException("Method signature " + ENUM_METHOD_SIGNATURE + " not found");
        }
        enumMethodStartIndex += ENUM_METHOD_SIGNATURE.length();
        int enumMethodEndIndex = SyncHelper.bracketMatch(source, enumMethodStartIndex);
        int implementPointIndex = source.indexOf(TODO_IMPL_LINE, enumMethodStartIndex);
        if (implementPointIndex != -1 && implementPointIndex < enumMethodEndIndex) {
            source.delete(implementPointIndex, implementPointIndex + TODO_IMPL_LINE.length());
            source.insert(implementPointIndex, ENUM_METHOD_IMPL);
            // delete everything from the end of MY impl to the close of the method
            int postEditEnding = SyncHelper.bracketMatch(source, enumMethodStartIndex);
            source.delete(implementPointIndex + ENUM_METHOD_IMPL.length(), postEditEnding - 1);
            edited = true;
        }
        return edited;
    }
    
    
    private boolean editGetMethod(StringBuffer source) throws CodegenExtensionException {
        boolean edited = false;
        int getMethodStartIndex = source.indexOf(GET_METHOD_SIGNATURE);
        if (getMethodStartIndex == -1) {
            throw new CodegenExtensionException("Method signature " + GET_METHOD_SIGNATURE + " not found");
        }
        getMethodStartIndex += GET_METHOD_SIGNATURE.length();
        int getMethodEndIndex = SyncHelper.bracketMatch(source, getMethodStartIndex);
        int implementPointIndex = source.indexOf(TODO_IMPL_LINE, getMethodStartIndex);
        if (implementPointIndex != -1 && implementPointIndex < getMethodEndIndex) {
            source.delete(implementPointIndex, implementPointIndex + TODO_IMPL_LINE.length());
            source.insert(implementPointIndex, GET_METHOD_IMPL);
            // delete everything from the end of MY impl to the close of the method
            int postEditEnding = SyncHelper.bracketMatch(source, getMethodStartIndex);
            source.delete(implementPointIndex + GET_METHOD_IMPL.length(), postEditEnding - 1);
            edited = true;
        }
        return edited;
    }
    
    
    private boolean editDestroyMethod(StringBuffer source) throws CodegenExtensionException {
        boolean edited = false;
        int destroyMethodStartIndex = source.indexOf(DESTROY_METHOD_SIGNATURE);
        if (destroyMethodStartIndex == -1) {
            throw new CodegenExtensionException("Method signature " + DESTROY_METHOD_SIGNATURE + " not found");
        }
        destroyMethodStartIndex += DESTROY_METHOD_SIGNATURE.length();
        int destroyMethodEndIndex = SyncHelper.bracketMatch(source, destroyMethodStartIndex);
        int implementPointIndex = source.indexOf(TODO_IMPL_LINE, destroyMethodStartIndex);
        if (implementPointIndex != -1 && implementPointIndex < destroyMethodEndIndex) {
            source.delete(implementPointIndex, implementPointIndex + TODO_IMPL_LINE.length());
            source.insert(implementPointIndex, DESTROY_METHOD_IMPL);
            edited = true;
        }
        return edited;
    }

}
