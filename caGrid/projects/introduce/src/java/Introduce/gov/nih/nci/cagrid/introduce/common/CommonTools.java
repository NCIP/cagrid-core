package gov.nih.nci.cagrid.introduce.common;

import gov.nih.nci.cagrid.common.StreamGobbler;
import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.common.XMLUtilities;
import gov.nih.nci.cagrid.introduce.IntroduceConstants;
import gov.nih.nci.cagrid.introduce.beans.ServiceDescription;
import gov.nih.nci.cagrid.introduce.beans.method.MethodType;
import gov.nih.nci.cagrid.introduce.beans.method.MethodTypeExceptions;
import gov.nih.nci.cagrid.introduce.beans.method.MethodTypeExceptionsException;
import gov.nih.nci.cagrid.introduce.beans.method.MethodTypeImportInformation;
import gov.nih.nci.cagrid.introduce.beans.method.MethodTypeInputs;
import gov.nih.nci.cagrid.introduce.beans.method.MethodTypeInputsInput;
import gov.nih.nci.cagrid.introduce.beans.method.MethodTypeOutput;
import gov.nih.nci.cagrid.introduce.beans.method.MethodsType;
import gov.nih.nci.cagrid.introduce.beans.namespace.NamespaceType;
import gov.nih.nci.cagrid.introduce.beans.namespace.NamespacesType;
import gov.nih.nci.cagrid.introduce.beans.namespace.SchemaElementType;
import gov.nih.nci.cagrid.introduce.beans.property.ServiceProperties;
import gov.nih.nci.cagrid.introduce.beans.property.ServicePropertiesProperty;
import gov.nih.nci.cagrid.introduce.beans.resource.ResourcePropertiesListType;
import gov.nih.nci.cagrid.introduce.beans.resource.ResourcePropertyType;
import gov.nih.nci.cagrid.introduce.beans.security.MethodSecurity;
import gov.nih.nci.cagrid.introduce.beans.security.ServiceSecurity;
import gov.nih.nci.cagrid.introduce.beans.service.ServiceType;
import gov.nih.nci.cagrid.introduce.beans.service.ServicesType;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import javax.xml.namespace.QName;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.jdom.Document;
import org.jdom.Element;


/**
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella </A>
 */
public final class CommonTools {
    private static final Logger logger = Logger.getLogger(CommonTools.class);

    public static final String ALLOWED_JAVA_CLASS_REGEX = "[A-Z]++[A-Za-z0-9\\_\\$]*";

    public static final String ALLOWED_JAVA_FIELD_REGEX = "[a-z\\_]++[A-Za-z0-9\\_\\$]*";

    public static final String ALLOWED_JAVA_OP_NAME = "[a-zA-Z\\_]++[A-Za-z0-9\\_]*";

    public static final String ALLOWED_JAVA_PACKAGE_REGEX = "[a-zA-Z\\_]++[A-Za-z0-9\\_\\$]*";

    public static final String SUGGESTED_JAVA_PACKAGE_REGEX = "[a-z\\_]++[A-Za-z0-9\\_\\$]*";

    public static final String ALLOWED_EXISTING_JAVA_PACKAGE_REGEX = "[a-zA-Z\\_]++[A-Za-z0-9\\_\\$]*";

    public static final List JAVA_KEYWORDS = new ArrayList(Arrays.asList(new String[]{"abstract", "continue", "for",
            "new", "switch", "assert", "default", "goto", "package", "synchronized", "boolean", "do", "if", "private",
            "this", "break", "double", "implements", "protected", "throw", "byte", "else", "import", "public",
            "throws", "case", "enum", "instanceof", "return", "transient", "catch", "extends", "int", "short", "try",
            "char", "final", "interface", "static", "void", "class", "finally", "long", "strictfp", "volatile",
            "const", "float", "native", "super", "while"}));


    private CommonTools() {

    }


    public static String fixPortTypeMethodName(String typeName) {
        typeName = CommonTools.upperCaseFirstCharacter(typeName);
        String newTypeName = "";
        boolean lastwasignored = false;
        for (int i = 0; i < typeName.length(); i++) {
            if (typeName.charAt(i) == '-') {
                lastwasignored = true;
            } else if (lastwasignored) {
                lastwasignored = false;
                newTypeName += Character.toString(typeName.charAt(i)).toUpperCase();
            } else {
                newTypeName += typeName.charAt(i);
            }
        }

        return newTypeName;
    }


    public static Process createAndOutputProcess(String cmd) throws Exception {
        final Process p;

        p = Runtime.getRuntime().exec(cmd);
        StreamGobbler errGobbler = new StreamGobbler(p.getErrorStream(), "ERR", logger, Priority.ERROR);
        StreamGobbler outGobbler = new StreamGobbler(p.getInputStream(), "OUT", logger, Priority.DEBUG);
        errGobbler.start();
        outGobbler.start();

        return p;
    }


    public static List getProvidedNamespaces(File startDir) {
        List globusNamespaces = new ArrayList();
        File schemasDir = new File(startDir.getAbsolutePath() + File.separator + "share" + File.separator + "schema");

        CommonTools.getTargetNamespaces(globusNamespaces, schemasDir);
        return globusNamespaces;
    }


    public static File findSchema(String schemaNamespace, File dir) {
        File[] files = dir.listFiles();
        for (int i = 0; i < files.length; i++) {
            File curFile = files[i];
            if (curFile.isDirectory()) {
                File found = findSchema(schemaNamespace, curFile);
                if (found != null) {
                    return found;
                }
            } else {
                if (curFile.getAbsolutePath().endsWith(".xsd") || curFile.getAbsolutePath().endsWith(".XSD")) {
                    try {
                        if (getTargetNamespace(curFile).equals(schemaNamespace)) {
                            return curFile;
                        }
                    } catch (Exception e) {
                        logger.error(e);
                    }

                }
            }
        }
        return null;
    }


    public static void getTargetNamespaces(List namespaces, File dir) {
        File[] files = dir.listFiles();
        for (int i = 0; i < files.length; i++) {
            File curFile = files[i];
            if (curFile.isDirectory()) {
                getTargetNamespaces(namespaces, curFile);
            } else {
                if (curFile.getAbsolutePath().endsWith(".xsd") || curFile.getAbsolutePath().endsWith(".XSD")) {
                    try {
                        namespaces.add(getTargetNamespace(curFile));
                    } catch (Exception e) {
                        logger.error(e);
                    }

                }
            }
        }
    }


    public static String getTargetNamespace(File file) throws Exception {
        Document doc = XMLUtilities.fileNameToDocument(file.getAbsolutePath());
        return doc.getRootElement().getAttributeValue("targetNamespace");

    }


    public static boolean isValidPackageAndClassName(String packageclass) {
        if (packageclass.length() > 0) {
            if (packageclass.lastIndexOf(".") < 0) {
                return false;
            } else {
                String packages = packageclass.substring(0, packageclass.lastIndexOf("."));
                String classname = packageclass.substring(packageclass.lastIndexOf(".") + 1);
                if (packages.length() <= 0 || classname.length() <= 0) {
                    return false;
                }
                if (!isValidPackageName(packages) || !isValidClassName(classname)) {
                    return false;
                }
            }
        }
        return true;
    }


    public static boolean isValidPackageName(String packageName) {
        if (packageName.length() > 0) {
            if (packageName.endsWith(".")) {
                return false;
            }
            StringTokenizer strtok = new StringTokenizer(packageName, ".", false);
            while (strtok.hasMoreElements()) {
                String packageItem = strtok.nextToken();
                if (!packageItem.matches(ALLOWED_JAVA_PACKAGE_REGEX) || JAVA_KEYWORDS.contains(packageItem)) {
                    return false;
                }
            }
        }
        return true;
    }


    public static boolean isSuggestedPackageName(String packageName) {
        if (packageName.length() > 0) {
            if (packageName.endsWith(".")) {
                return false;
            }
            StringTokenizer strtok = new StringTokenizer(packageName, ".", false);
            while (strtok.hasMoreElements()) {
                String packageItem = strtok.nextToken();
                if (!packageItem.matches(SUGGESTED_JAVA_PACKAGE_REGEX) || JAVA_KEYWORDS.contains(packageItem)) {
                    return false;
                }
            }
        }
        return true;
    }


    public static boolean isValidNoStubPackageName(String packageName) {
        if (packageName.length() > 0) {
            if (packageName.endsWith(".")) {
                return false;
            }
            StringTokenizer strtok = new StringTokenizer(packageName, ".", false);
            while (strtok.hasMoreElements()) {
                String packageItem = strtok.nextToken();
                if (!packageItem.matches(ALLOWED_EXISTING_JAVA_PACKAGE_REGEX) || JAVA_KEYWORDS.contains(packageItem)) {
                    return false;
                }
            }
        }
        return true;
    }


    public static boolean isValidServiceName(String serviceName) {
        return isValidClassName(serviceName);
    }


    public static boolean isValidClassName(String classname) {
        if ((classname == null) || classname.trim().equals("")) {
            return false;
        }

        if (classname.substring(0, 1).toLowerCase().equals(classname.substring(0, 1))) {
            return false;
        }
        if (!classname.matches(ALLOWED_JAVA_CLASS_REGEX) || JAVA_KEYWORDS.contains(classname)) {
            return false;
        }
        return true;

    }


    public static boolean isValidJavaField(String serviceName) {
        if (serviceName.length() > 0) {
            if (!serviceName.matches(ALLOWED_JAVA_FIELD_REGEX) || JAVA_KEYWORDS.contains(serviceName)) {
                return false;
            }
        }
        return true;
    }


    public static boolean isValidJavaMethod(String methodName) {
        if (methodName.length() > 0) {
            if (!methodName.matches(ALLOWED_JAVA_OP_NAME)) {
                return false;
            }
        }
        return true;
    }


    /**
     * Gets a package name for a namespace using a namespace to package mapper
     * utility
     * 
     * @param fullNamespace
     *            The namespace to derive a package name for
     * @return The package name
     */
    public static String getPackageName(String fullNamespace) {
        try {
            // TODO: where should this mapperClassname preference be set
            String mapperClassname = "gov.nih.nci.cagrid.introduce.common.CaBIGNamespaceToPackageMapper";
            Class clazz = Class.forName(mapperClassname);
            NamespaceToPackageMapper mapper = (NamespaceToPackageMapper) clazz.newInstance();
            return mapper.getPackageName(fullNamespace);
        } catch (Exception e) {
            logger.error(e);
            return null;
        }
    }


    /**
     * Gets a package name for a namespace
     * 
     * @param fullNamespace
     *            The namespace to get a package name for
     * @param namespaceTypes
     *            The namespace types of a service
     * @return The package name
     */
    public static String getPackageName(String fullNamespace, NamespacesType namespaceTypes) {
        // first check to see if this namespace is already in use....
        NamespaceType nsType = CommonTools.getNamespaceType(namespaceTypes, fullNamespace);
        if (nsType != null) {
            return nsType.getPackageName();
        } else {
            return getPackageName(fullNamespace);
        }
    }


    public static boolean equals(ServiceSecurity ss, MethodSecurity ms) {
        if ((ss == null) && (ms == null)) {
            return true;
        } else if ((ss != null) && (ms == null)) {
            return false;
        } else if ((ss == null) && (ms != null)) {
            return false;
        } else if (!Utils.equals(ss.getSecuritySetting(), ms.getSecuritySetting())) {
            return false;
        } else if (!Utils.equals(ss.getAnonymousClients(), ms.getAnonymousClients())) {
            return false;
        } else if (!Utils.equals(ss.getSecureConversation(), ms.getSecureConversation())) {
            return false;
        } else if (!Utils.equals(ss.getSecureMessage(), ms.getSecureMessage())) {
            return false;
        } else if (!Utils.equals(ss.getTransportLevelSecurity(), ms.getTransportLevelSecurity())) {
            return false;
        } else if ((!Utils.equals(ss.getServiceAuthorization(), ms.getMethodAuthorization()))
            && (!Utils.equals(ss.getServiceAuthorization().getIntroducePDPAuthorization(), ms.getMethodAuthorization()
                .getIntroducePDPAuthorization()))) {
            return false;
        } else {
            return true;
        }
    }


    /**
     * This method will create a namespaceType fully populated with the schema
     * elements. It will set default the location to the relative path from the
     * serviceSchemaDir.
     * 
     * @param xsdFilename
     *            The file name of the XSD schema
     * @param serviceSchemaDir
     *            the directory where the service's schemas (wsdls) are
     * @return The NamespaceType representation of the schema
     * @throws Exception
     */
    public static NamespaceType createNamespaceType(String xsdFilename, File serviceSchemaDir) throws Exception {
        NamespaceType namespaceType = new NamespaceType();
        File xsdFile = new File(xsdFilename);
        String location;
        try {
            location = "./" + Utils.getRelativePath(serviceSchemaDir, xsdFile).replace('\\', '/');
        } catch (IOException e) {
            logger.error(e);
            throw new Exception("Problem getting relative path of XSD.", e);
        }
        namespaceType.setLocation(location);
        Document schemaDoc = XMLUtilities.fileNameToDocument(xsdFilename);

        String rawNamespace = schemaDoc.getRootElement().getAttributeValue("targetNamespace");
        String packageName = getPackageName(rawNamespace);
        namespaceType.setPackageName(packageName);

        namespaceType.setNamespace(rawNamespace);

        processSchema(namespaceType, schemaDoc, new File(xsdFilename).getParentFile());

        return namespaceType;
    }


    private static void processSchema(NamespaceType namespaceType, Document schemaDoc, File dir) throws Exception {
        List elementTypes = schemaDoc.getRootElement()
            .getChildren("element", schemaDoc.getRootElement().getNamespace());

        SchemaElementType[] schemaTypes = new SchemaElementType[elementTypes.size()];
        for (int i = 0; i < elementTypes.size(); i++) {
            Element element = (Element) elementTypes.get(i);
            SchemaElementType type = new SchemaElementType();
            if (element.getAttributeValue("name") == null || element.getAttributeValue("name").length() <= 0) {
                throw new Exception("Schema does not appear to be valid: an element does not contain a name attribute");
            }
            type.setType(element.getAttributeValue("name"));
            schemaTypes[i] = type;
        }

        if (namespaceType.getSchemaElement() != null) {
            SchemaElementType[] newSchemaTypes = new SchemaElementType[schemaTypes.length
                + namespaceType.getSchemaElement().length];
            System.arraycopy(namespaceType.getSchemaElement(), 0, newSchemaTypes, 0,
                namespaceType.getSchemaElement().length);
            System.arraycopy(schemaTypes, 0, newSchemaTypes, namespaceType.getSchemaElement().length,
                schemaTypes.length);
            namespaceType.setSchemaElement(newSchemaTypes);
        } else {
            namespaceType.setSchemaElement(schemaTypes);
        }

        List includeTypes = schemaDoc.getRootElement()
            .getChildren("include", schemaDoc.getRootElement().getNamespace());
        for (int i = 0; i < includeTypes.size(); i++) {
            Element element = (Element) includeTypes.get(i);
            File xsdFile = new File(dir.getAbsolutePath() + File.separator
                + element.getAttributeValue("schemaLocation"));
            Document includeSchemaDoc = XMLUtilities.fileNameToDocument(xsdFile.getAbsolutePath());
            String rawNamespace = schemaDoc.getRootElement().getAttributeValue("targetNamespace");

            processSchema(namespaceType, includeSchemaDoc, xsdFile.getParentFile());

        }

        List redefineTypes = schemaDoc.getRootElement().getChildren("redefine",
            schemaDoc.getRootElement().getNamespace());
        for (int i = 0; i < redefineTypes.size(); i++) {
            Element element = (Element) redefineTypes.get(i);
            File xsdFile = new File(dir.getAbsolutePath() + File.separator
                + element.getAttributeValue("schemaLocation"));
            Document redefineSchemaDoc = XMLUtilities.fileNameToDocument(xsdFile.getAbsolutePath());

            processSchema(namespaceType, redefineSchemaDoc, xsdFile.getParentFile());

        }

    }


    /**
     * This method will create a namespaceType fully populated with the schema
     * elements. It will set default the location to the relative path from the
     * serviceSchemaDir.
     * 
     * @param xsdFilename
     *            The file name of the XSD schema
     * @param serviceSchemaDir
     *            the directory where the service's schemas (wsdls) are
     * @return The NamespaceType representation of the schema
     * @throws Exception
     */
    public static NamespaceType reCreateNamespaceType(String xsdFilename, File serviceSchemaDir, NamespaceType oldType)
        throws Exception {
        NamespaceType namespaceType = new NamespaceType();
        File xsdFile = new File(xsdFilename);
        String location;
        try {
            location = "./" + Utils.getRelativePath(serviceSchemaDir, xsdFile).replace('\\', '/');
        } catch (IOException e) {
            logger.error(e);
            throw new Exception("Problem getting relative path of XSD.", e);
        }
        namespaceType.setLocation(location);
        Document schemaDoc = XMLUtilities.fileNameToDocument(xsdFilename);

        String rawNamespace = schemaDoc.getRootElement().getAttributeValue("targetNamespace");
        namespaceType.setPackageName(oldType.getPackageName());
        namespaceType.setNamespace(rawNamespace);

        List elementTypes = schemaDoc.getRootElement()
            .getChildren("element", schemaDoc.getRootElement().getNamespace());
        SchemaElementType[] schemaTypes = new SchemaElementType[elementTypes.size()];
        for (int i = 0; i < elementTypes.size(); i++) {
            Element element = (Element) elementTypes.get(i);
            SchemaElementType type = new SchemaElementType();
            if (element.getAttributeValue("name") == null || element.getAttributeValue("name").length() <= 0) {
                throw new Exception("Schema does not appear to be valid: an element does not contain a name attribute");
            }
            type.setType(element.getAttributeValue("name"));
            SchemaElementType oldElType = getSchemaElementType(oldType, element.getAttributeValue("name"));
            if (oldElType != null) {
                type.setPackageName(oldElType.getPackageName());
                type.setClassName(oldElType.getClassName());
                type.setDeserializer(oldElType.getDeserializer());
                type.setSerializer(oldElType.getSerializer());
            }
            schemaTypes[i] = type;
        }
        namespaceType.setSchemaElement(schemaTypes);
        return namespaceType;
    }


    public static SchemaElementType getSchemaElementType(NamespaceType nsType, String name) {
        for (int i = 0; i < nsType.getSchemaElement().length; i++) {
            SchemaElementType type = nsType.getSchemaElement(i);
            if (type.getType().equals(name)) {
                return type;
            }
        }
        return null;
    }


    /**
     * Gets a service by name from the services container type
     * 
     * @param services
     *            The services container type
     * @param name
     *            The name of the service
     * @return The named service or null if not found
     */
    public static ServiceType getService(ServicesType services, String name) {
        if ((services != null) && (services.getService() != null)) {
            for (int i = 0; i < services.getService().length; i++) {
                if (services.getService(i).getName().equals(name)) {
                    return services.getService(i);
                }
            }
        }

        return null;
    }


    /**
     * Gets a method by its name
     * 
     * @param methods
     *            The methods container type
     * @param name
     *            The name of the method
     * @return The named method, or null if not found
     */
    public static MethodType getMethod(MethodsType methods, String name) {
        if ((methods != null) && (methods.getMethod() != null)) {
            for (int i = 0; i < methods.getMethod().length; i++) {
                if (CommonTools.lowerCaseFirstCharacter(methods.getMethod(i).getName()).equals(
                    CommonTools.lowerCaseFirstCharacter(name))) {
                    return methods.getMethod(i);
                }
            }
        }

        return null;
    }


    public static String methodTypeToString(MethodType method) {
        // if it is imported from wsdl just return the name
        if (method.isIsImported() && (method.getImportInformation().getFromIntroduce() != null)
            && !method.getImportInformation().getFromIntroduce().booleanValue()) {
            return method.getName();
        }

        // assume its void to start with
        StringBuilder output = new StringBuilder();
        output.append("void");

        MethodTypeOutput outputType = method.getOutput();
        if (outputType != null) {
            // use classname if set, else use schema type
            if ((outputType.getQName() != null) && (outputType.getQName().getLocalPart() != null)
                && !outputType.getQName().getLocalPart().trim().equals("")) {
                String name = org.apache.axis.wsdl.toJava.Utils
                    .xmlNameToJavaClass(outputType.getQName().getLocalPart());
                if (name.indexOf("_") == 0) {
                    name = name.substring(1);
                }
                output.replace(0, output.length(), name);
            }

            // add array notation if its an array
            if (outputType.isIsArray()) {
                output.append("[]");
            }
        }

        StringBuilder input = new StringBuilder();
        MethodTypeInputs inputs = method.getInputs();
        if (inputs != null) {
            MethodTypeInputsInput[] inputarr = inputs.getInput();
            if (inputarr != null) {
                for (int i = 0; i < inputarr.length; i++) {
                    MethodTypeInputsInput inputType = inputarr[i];
                    // use classname if set, else use schema type
                    if ((inputType.getQName() != null) && (inputType.getQName().getLocalPart() != null)
                        && !inputType.getQName().getLocalPart().trim().equals("")) {
                        if (input.length() != 0) {
                            input.append(", ");
                        }
                        String name = org.apache.axis.wsdl.toJava.Utils.xmlNameToJavaClass(inputType.getQName()
                            .getLocalPart());
                        if (name.indexOf("_") == 0) {
                            name = name.substring(1);
                        }
                        input.append(name);
                    } else {
                        // why would this be the case?
                        continue;
                    }

                    // add array notation if its an array
                    if (inputType.isIsArray()) {
                        input.append("[]");
                    }

                    input.append(" ").append(inputType.getName());
                }
            }
        }

        output.append("  ").append(method.getName()).append("(").append(input.toString()).append(")");

        return output.toString();
    }


    public static void importMethod(MethodTypeImportInformation importInformation, File fromDir, File toDir,
        String fromService, String toService, String methodName, boolean copyFiles) throws Exception {
        ServiceDescription fromintroService = (ServiceDescription) Utils.deserializeDocument(fromDir.getAbsolutePath()
            + File.separator + IntroduceConstants.INTRODUCE_XML_FILE, ServiceDescription.class);

        ServiceDescription introService = (ServiceDescription) Utils.deserializeDocument(toDir.getAbsolutePath()
            + File.separator + IntroduceConstants.INTRODUCE_XML_FILE, ServiceDescription.class);

        if (copyFiles) {
            File fromwsdl = new File(fromDir.getAbsolutePath() + File.separator + "schema" + File.separator
                + fromService);
            File towsdl = new File(toDir.getAbsolutePath() + File.separator + "schema" + File.separator + toService);
            Utils.copyDirectory(fromwsdl, towsdl);

            File fromLibDir = new File(fromDir.getAbsolutePath() + File.separator + "lib");
            File toLibDir = new File(toDir.getAbsolutePath() + File.separator + "lib");

            Utils.copyDirectory(fromLibDir, toLibDir);
        }

        // copy over the namespaces from the imported service
        // make sure to warn on duplicates and remove them
        NamespacesType fromNamespaces = fromintroService.getNamespaces();
        int fromNamespacesLength = 0;
        if ((fromNamespaces != null) && (fromNamespaces.getNamespace() != null)) {
            fromNamespacesLength = fromNamespaces.getNamespace().length;
        }
        NamespacesType toNamespaces = introService.getNamespaces();
        int toNamespacesLength = 0;
        if ((toNamespaces != null) && (toNamespaces.getNamespace() != null)) {
            toNamespacesLength = toNamespaces.getNamespace().length;
        }

        List namespaces = new ArrayList();
        List usedNamespaces = new ArrayList();
        for (int i = 0; i < toNamespacesLength; i++) {
            if (!usedNamespaces.contains(toNamespaces.getNamespace(i).getNamespace())) {
                usedNamespaces.add(toNamespaces.getNamespace(i).getNamespace());
                namespaces.add(toNamespaces.getNamespace(i));
            }
        }
        for (int i = 0; i < fromNamespacesLength; i++) {
            if (!usedNamespaces.contains(fromNamespaces.getNamespace(i).getNamespace())) {
                usedNamespaces.add(fromNamespaces.getNamespace(i).getNamespace());
                namespaces.add(fromNamespaces.getNamespace(i));
            } else {
                System.err.println("WARNING: During Import: Namespace was already being used in the original service: "
                    + fromNamespaces.getNamespace(i).getNamespace());
            }
        }
        NamespaceType[] newNamespacesArr = new NamespaceType[namespaces.size()];
        namespaces.toArray(newNamespacesArr);
        NamespacesType newNamespaces = new NamespacesType();
        newNamespaces.setNamespace(newNamespacesArr);
        introService.setNamespaces(newNamespaces);

        // find the method and add it to service's methods....
        MethodsType fromMethods = CommonTools.getService(fromintroService.getServices(), fromService).getMethods();
        MethodType foundMethod = null;
        if ((fromMethods != null) && (fromMethods.getMethod() != null)) {
            boolean found = false;
            for (int i = 0; i < fromMethods.getMethod().length; i++) {
                foundMethod = fromMethods.getMethod(i);
                if (foundMethod.getName().equals(methodName)) {
                    found = true;
                    break;
                }
            }
            if (found != true) {
                throw new Exception("Method " + methodName + " was not found in imported service");
            }

        } else {
            throw new Exception("Imported service was supposed to have methods.....");
        }

        MethodsType methodsType = CommonTools.getService(introService.getServices(), toService).getMethods();

        foundMethod.setIsImported(true);
        foundMethod.setImportInformation(importInformation);

        // add new method to array in bean
        // this seems to be a weird way to be adding things....
        MethodType[] newMethods;
        int newLength = 0;
        if ((methodsType != null) && (methodsType.getMethod() != null)) {
            newLength = methodsType.getMethod().length + 1;
            newMethods = new MethodType[newLength];
            System.arraycopy(methodsType.getMethod(), 0, newMethods, 0, methodsType.getMethod().length);
        } else {
            newLength = 1;
            newMethods = new MethodType[newLength];
        }
        MethodsType newmethodsType = new MethodsType();
        newMethods[newLength - 1] = foundMethod;
        newmethodsType.setMethod(newMethods);
        CommonTools.getService(introService.getServices(), toService).setMethods(newmethodsType);

        Utils.serializeDocument(toDir.getAbsolutePath() + File.separator + IntroduceConstants.INTRODUCE_XML_FILE,
            introService, IntroduceConstants.INTRODUCE_SKELETON_QNAME);
    }


    /**
     * Gets the directory which corresponds to a Java package name
     * 
     * @param service
     *            The service
     * @return The service's package name changed to a relative directory
     */
    public static String getPackageDir(ServiceType service) {
        return service.getPackageName().replace('.', File.separatorChar);
    }


    public static NamespaceType getNamespaceType(NamespacesType namespacesType, String namespaceURI) {
        if ((namespacesType != null) && (namespacesType.getNamespace() != null)) {
            NamespaceType[] namespaces = namespacesType.getNamespace();
            for (int i = 0; i < namespaces.length; i++) {
                NamespaceType namespace = namespaces[i];
                if (namespace.getNamespace().equals(namespaceURI)) {
                    return namespace;
                }
            }
        }
        return null;
    }


    public static SchemaInformation getSchemaInformation(NamespacesType namespacesType, QName qname) {
        if ((namespacesType != null) && (namespacesType.getNamespace() != null)) {
            NamespaceType[] namespaces = namespacesType.getNamespace();
            for (int i = 0; i < namespaces.length; i++) {
                NamespaceType namespace = namespaces[i];
                if (namespace.getNamespace().equals(qname.getNamespaceURI())) {
                    if (namespace.getSchemaElement() != null) {
                        for (int j = 0; j < namespace.getSchemaElement().length; j++) {
                            SchemaElementType type = namespace.getSchemaElement(j);
                            if (type.getType().equals(qname.getLocalPart())) {
                                SchemaInformation info = new SchemaInformation(namespace, type);
                                return info;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }


    public static void addServicePropety(ServiceDescription introService, ServicePropertiesProperty property) {
        ServicePropertiesProperty[] propertiesArray = null;
        int length = 0;
        if ((introService.getServiceProperties() != null)
            && (introService.getServiceProperties().getProperty() != null)) {
            length = introService.getServiceProperties().getProperty().length + 1;
        } else {
            length = 1;
        }
        propertiesArray = new ServicePropertiesProperty[length];
        if (length > 1) {
            System.arraycopy(introService.getServiceProperties().getProperty(), 0, propertiesArray, 0, length - 1);
        }
        propertiesArray[length - 1] = property;
        ServiceProperties properties = null;
        if (introService.getServiceProperties() == null) {
            properties = new ServiceProperties();
            introService.setServiceProperties(properties);
        } else {
            properties = introService.getServiceProperties();
        }
        properties.setProperty(propertiesArray);
    }


    /**
     * Adds a resource property to a service
     * 
     * @param service
     *            The service to add a resource property to
     * @param resource
     *            The resource property to be added
     */
    public static void addResourcePropety(ServiceType service, ResourcePropertyType resource) {
        ResourcePropertyType[] resourcesArray = null;
        int length = 0;
        if ((service.getResourcePropertiesList() != null)
            && (service.getResourcePropertiesList().getResourceProperty() != null)) {
            length = service.getResourcePropertiesList().getResourceProperty().length + 1;
        } else {
            length = 1;
        }
        resourcesArray = new ResourcePropertyType[length];
        if (length > 1) {
            System.arraycopy(service.getResourcePropertiesList().getResourceProperty(), 0, resourcesArray, 0,
                length - 1);
        }
        resourcesArray[length - 1] = resource;
        ResourcePropertiesListType resources = null;
        if (service.getResourcePropertiesList() == null) {
            resources = new ResourcePropertiesListType();
            service.setResourcePropertiesList(resources);
        } else {
            resources = service.getResourcePropertiesList();
        }
        resources.setResourceProperty(resourcesArray);
    }


    public static void removeResourceProperty(ServiceType service, QName resourcePropertyType) {
        // remove from resource properties list
        ResourcePropertyType[] metadatas;
        if (service.getResourcePropertiesList() != null
            && service.getResourcePropertiesList().getResourceProperty() != null) {
            int newLength = service.getResourcePropertiesList().getResourceProperty().length - 1;
            metadatas = new ResourcePropertyType[newLength];
            int resourcesCount = 0;
            for (int i = 0; i < service.getResourcePropertiesList().getResourceProperty().length; i++) {
                ResourcePropertyType oldResource = service.getResourcePropertiesList().getResourceProperty(i);
                if (!oldResource.getQName().equals(resourcePropertyType)) {
                    metadatas[resourcesCount++] = oldResource;
                }
            }
            ResourcePropertiesListType list = new ResourcePropertiesListType(metadatas);
            service.setResourcePropertiesList(list);
        }
    }


    /**
     * Gets all resource properties from a service which have a specified QName
     * 
     * @param service
     *            The service to find resource properties of
     * @param type
     *            The type of resource properties to locate
     * @return An Array of ResourcePropertyTypes which have the specified QName
     */
    public static ResourcePropertyType[] getResourcePropertiesOfType(ServiceType service, QName type) {
        ResourcePropertiesListType propsList = service.getResourcePropertiesList();
        List typedProperties = new ArrayList();
        if (propsList != null) {
            ResourcePropertyType[] allProperties = propsList.getResourceProperty();
            if (allProperties != null) {
                for (int i = 0; i < allProperties.length; i++) {
                    if (allProperties[i].getQName().equals(type)) {
                        typedProperties.add(allProperties[i]);
                    }
                }
            }
        }
        ResourcePropertyType[] propArray = new ResourcePropertyType[typedProperties.size()];
        typedProperties.toArray(propArray);
        return propArray;
    }


    /**
     * Adds a method to a service
     * 
     * @param service
     *            The service to add a new method to
     * @param method
     *            The method to be added
     */
    public static void addMethod(ServiceType service, MethodType method) {
        MethodType[] methodsArray = null;
        int length = 0;
        if ((service.getMethods() != null) && (service.getMethods().getMethod() != null)) {
            length = service.getMethods().getMethod().length + 1;
        } else {
            length = 1;
        }
        methodsArray = new MethodType[length];
        if (length > 1) {
            System.arraycopy(service.getMethods().getMethod(), 0, methodsArray, 0, length - 1);
        }
        methodsArray[length - 1] = method;
        MethodsType methods = null;
        if (service.getMethods() == null) {
            methods = new MethodsType();
            service.setMethods(methods);
        } else {
            methods = service.getMethods();
        }
        methods.setMethod(methodsArray);
    }


    /**
     * Removes a method from a MethodsType container object
     * 
     * @param methodsType
     *            The container object to remove a method from
     * @param method
     *            The method to be removed
     */
    public static void removeMethod(MethodsType methodsType, MethodType method) {
        MethodType[] newMethods = new MethodType[methodsType.getMethod().length - 1];
        int newMethodsI = 0;
        for (int i = 0; i < methodsType.getMethod().length; i++) {
            MethodType tmethod = methodsType.getMethod(i);
            if (!(tmethod.equals(method))) {
                newMethods[newMethodsI] = tmethod;
                newMethodsI++;
            }
        }
        methodsType.setMethod(newMethods);
    }


    /**
     * Adds a namespace type to a service description
     * 
     * @param serviceD
     *            The service description
     * @param nsType
     *            The namespace type to add
     */
    public static void addNamespace(ServiceDescription serviceD, NamespaceType nsType) {
        if (getNamespaceType(serviceD.getNamespaces(), nsType.getNamespace()) == null) {

            NamespaceType[] namespacesArray = null;
            int length = 0;
            if ((serviceD.getNamespaces() != null) && (serviceD.getNamespaces().getNamespace() != null)) {
                length = serviceD.getNamespaces().getNamespace().length + 1;
            } else {
                length = 1;
            }
            namespacesArray = new NamespaceType[length];
            if (length > 1) {
                System.arraycopy(serviceD.getNamespaces().getNamespace(), 0, namespacesArray, 0, length - 1);
            }
            namespacesArray[length - 1] = nsType;
            NamespacesType namespaces = null;
            if (serviceD.getNamespaces() == null) {
                namespaces = new NamespacesType();
                serviceD.setNamespaces(namespaces);
            } else {
                namespaces = serviceD.getNamespaces();
            }
            namespaces.setNamespace(namespacesArray);
        } else {
            logger.warn("Namesapce already exists and is being ignored: " + nsType.getNamespace(), new Throwable());
        }
    }


    public static void removeNamespace(ServiceDescription serviceD, String namespace) {
        if (serviceD.getNamespaces() != null && serviceD.getNamespaces().getNamespace() != null
            && serviceD.getNamespaces().getNamespace().length > 0) {
            NamespaceType[] newNamespaceTypes = new NamespaceType[serviceD.getNamespaces().getNamespace().length - 1];
            int kept = 0;
            for (int i = 0; i < serviceD.getNamespaces().getNamespace().length; i++) {
                NamespaceType type = serviceD.getNamespaces().getNamespace(i);
                if (!type.getNamespace().equals(namespace)) {
                    newNamespaceTypes[kept] = type;
                    kept++;
                }
            }
            serviceD.getNamespaces().setNamespace(newNamespaceTypes);
        }
    }


    /**
     * Adds aservice type to the services list
     * 
     * @param servicesType
     *            The services descriptions
     * @param serviceType
     *            The service type to add
     */
    public static void addService(ServicesType servicesType, ServiceType serviceType) {
        ServiceType[] servicesArray = null;
        int length = servicesType.getService().length + 1;
        servicesArray = new ServiceType[length];
        if (length > 1) {
            System.arraycopy(servicesType.getService(), 0, servicesArray, 0, length - 1);
        }
        servicesArray[length - 1] = serviceType;
        servicesType.setService(servicesArray);
    }


    /**
     * Define a unique name for use as a variable for the metadata at the
     * specified index given the scope of the ServiceMetadataListType.
     * 
     * @param metadataList
     *            the list of metadata
     * @param index
     *            the index into the metadata list of the targeted metadata item
     * @return the variable name to use
     */
    public static String getResourcePropertyVariableName(ResourcePropertiesListType metadataList, int index) {
        String baseName = metadataList.getResourceProperty(index).getQName().getLocalPart();

        int previousNumber = 0;
        for (int i = 0; ((i < index) && (i < metadataList.getResourceProperty().length)); i++) {
            ResourcePropertyType metadata = metadataList.getResourceProperty()[i];
            if (metadata.getQName().getLocalPart().equalsIgnoreCase(baseName)) {
                // the qname local parts are the same for multiple qnames
                // resolve the issue by appending a number
                previousNumber++;
            }
        }

        // return the orginal name, if it is unique, otherwise append a number
        return CommonTools.lowerCaseFirstCharacter(baseName
            + ((previousNumber > 0) ? String.valueOf(previousNumber) : ""));
    }


    /**
     * Sets a service property on the service information. If no service
     * properties are found, a new array of properties is created and
     * initialized with a single property containing the key and value
     * specified. If the property is found to exist in the service, it's value
     * is changed to the one specified.
     * 
     * @param desc
     *            The service information to set a property on
     * @param key
     *            The key of the service property to set
     * @param value
     *            The value to associate with the property key
     */
    public static void setServiceProperty(ServiceDescription desc, String key, String value, boolean isFromETC) {
        setServiceProperty(desc, key, value, isFromETC, "");
    }


    /**
     * Sets a service property on the service information. If no service
     * properties are found, a new array of properties is created and
     * initialized with a single property containing the key and value
     * specified. If the property is found to exist in the service, it's value
     * is changed to the one specified.
     * 
     * @param desc
     *            The service information to set a property on
     * @param key
     *            The key of the service property to set
     * @param value
     *            The value to associate with the property key
     * @param description
     *            The description of the service property
     */
    public static void setServiceProperty(ServiceDescription desc, String key, String value, boolean isFromETC,
        String description) {
        if (description == null) {
            description = "";
        }
        ServiceProperties props = desc.getServiceProperties();
        if (props == null) {
            props = new ServiceProperties();
            desc.setServiceProperties(props);
        }
        ServicePropertiesProperty[] allProperties = props.getProperty();
        if (allProperties == null) {
            allProperties = new ServicePropertiesProperty[]{new ServicePropertiesProperty(description, new Boolean(
                isFromETC), key, value)};
        } else {
            boolean found = false;
            for (int i = 0; i < allProperties.length; i++) {
                if (allProperties[i].getKey().equals(key)) {
                    allProperties[i].setValue(value);
                    allProperties[i].setIsFromETC(new Boolean(isFromETC));
                    allProperties[i].setDescription(description);
                    found = true;
                    break;
                }
            }
            if (!found) {
                allProperties = (ServicePropertiesProperty[]) Utils.appendToArray(allProperties,
                    new ServicePropertiesProperty(description, new Boolean(isFromETC), key, value));
            }
        }
        props.setProperty(allProperties);
    }


    /**
     * Determines if a service information object contains the specified service
     * property
     * 
     * @param desc
     *            The service description
     * @param key
     *            The property to check for
     * @return True if a property with the key name is found, false otherwise
     */
    public static boolean servicePropertyExists(ServiceDescription desc, String key) {
        if ((desc.getServiceProperties() != null) && (desc.getServiceProperties().getProperty() != null)) {
            ServicePropertiesProperty[] props = desc.getServiceProperties().getProperty();
            for (int i = 0; i < props.length; i++) {
                if (props[i].getKey().equals(key)) {
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * Gets the value of a service property from service information
     * 
     * @param desc
     *            The service information to pull a property value from
     * @param key
     *            The key of the property value to find
     * @return The value of the property
     * @throws Exception
     *             If no property with the specified key is found
     */
    public static String getServicePropertyValue(ServiceDescription desc, String key) throws Exception {
        if ((desc.getServiceProperties() != null) && (desc.getServiceProperties().getProperty() != null)) {
            ServicePropertiesProperty[] props = desc.getServiceProperties().getProperty();
            for (int i = 0; i < props.length; i++) {
                if (props[i].getKey().equals(key)) {
                    return props[i].getValue();
                }
            }
        }
        throw new Exception("No such property: " + key);
    }


    /**
     * Removes a service property from service information
     * 
     * @param desc
     *            The service information to remove a property from
     * @param key
     *            The key name of the property to remove
     * @return True if the property existed and was removed, false otherwise
     */
    public static boolean removeServiceProperty(ServiceDescription desc, String key) {
        List<ServicePropertiesProperty> keptProperties = new ArrayList();
        boolean removed = false;
        for (int i = 0; i < desc.getServiceProperties().getProperty().length; i++) {
            ServicePropertiesProperty current = desc.getServiceProperties().getProperty(i);
            if (!current.getKey().equals(key)) {
                keptProperties.add(current);
            } else {
                removed = true;
            }
        }
        ServicePropertiesProperty[] propertyArray = new ServicePropertiesProperty[keptProperties.size()];
        keptProperties.toArray(propertyArray);
        desc.getServiceProperties().setProperty(propertyArray);
        return removed;
    }


    /**
     * Determines if schema element types from a namespace type are referenced
     * in other parts of the service (e.g. Methods, Exceptions)
     * 
     * @param nsType
     * @param desc
     * @return True if the namespace type is in use in the service, false
     *         otherwise
     */
    public static boolean isNamespaceTypeInUse(NamespaceType nsType, ServiceDescription desc) {
        String namespace = nsType.getNamespace();
        ServiceType[] services = desc.getServices().getService();
        for (int s = 0; s < services.length; s++) {
            // resource properties
            ResourcePropertiesListType propsList = services[s].getResourcePropertiesList();
            if (propsList != null) {
                for (int p = 0; (propsList.getResourceProperty() != null)
                    && (p < propsList.getResourceProperty().length); p++) {
                    ResourcePropertyType prop = propsList.getResourceProperty(p);
                    if (prop.getQName().getNamespaceURI().equals(namespace)) {
                        return true;
                    }
                }
            }
            // methods
            MethodsType methods = services[s].getMethods();
            if (methods != null) {
                for (int m = 0; (methods.getMethod() != null) && (m < methods.getMethod().length); m++) {
                    MethodType method = methods.getMethod(m);
                    // inputs
                    MethodTypeInputs inputs = method.getInputs();
                    if (inputs != null) {
                        for (int i = 0; (inputs.getInput() != null) && (i < inputs.getInput().length); i++) {
                            MethodTypeInputsInput input = inputs.getInput(i);
                            if (input.getQName().getNamespaceURI().equals(namespace)) {
                                return true;
                            }
                        }
                    }
                    // output
                    MethodTypeOutput output = method.getOutput();
                    if (output != null) {
                        if (output.getQName().getNamespaceURI().equals(namespace)) {
                            return true;
                        }
                    }
                    // exceptions
                    MethodTypeExceptions exceptions = method.getExceptions();
                    if (exceptions != null) {
                        for (int e = 0; (exceptions.getException() != null) && (e < exceptions.getException().length); e++) {
                            MethodTypeExceptionsException exception = exceptions.getException(e);
                            if (exception.getQname().getNamespaceURI().equals(namespace)) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }


    /**
     * Determines if all schema element types used in the service are still
     * available in the service's namespace types.
     * 
     * @param desc
     * @return True if all of the types referenced by a service are still
     *         present in the service description
     */
    public static boolean usedTypesAvailable(ServiceDescription desc) {
        return getUnavailableUsedTypes(desc).size() == 0;
    }


    /**
     * Gets the types from a service description which are referenced in the
     * service but not available in the namespace types of it
     * 
     * @param desc
     *            The service description
     * @return The set of unavailable types
     */
    public static Set getUnavailableUsedTypes(ServiceDescription desc) {
        // build up a set of used types
        Set usedTypes = new HashSet();
        ServiceType[] services = desc.getServices().getService();
        for (int s = 0; s < services.length; s++) {
            // resource properties
            ResourcePropertiesListType propsList = services[s].getResourcePropertiesList();
            if (propsList != null) {
                for (int p = 0; (propsList.getResourceProperty() != null)
                    && (p < propsList.getResourceProperty().length); p++) {
                    ResourcePropertyType prop = propsList.getResourceProperty(p);
                    usedTypes.add(prop.getQName());
                }
            }
            // methods
            MethodsType methods = services[s].getMethods();
            if (methods != null) {
                for (int m = 0; (methods.getMethod() != null) && (m < methods.getMethod().length); m++) {
                    MethodType method = methods.getMethod(m);
                    // inputs
                    MethodTypeInputs inputs = method.getInputs();
                    if (inputs != null) {
                        for (int i = 0; (inputs.getInput() != null) && (i < inputs.getInput().length); i++) {
                            MethodTypeInputsInput input = inputs.getInput(i);
                            usedTypes.add(input.getQName());
                        }
                    }
                    // output
                    MethodTypeOutput output = method.getOutput();
                    if ((output != null) && (!output.getQName().getLocalPart().equals("void"))) {
                        usedTypes.add(output.getQName());
                    }
                    // exceptions
                    MethodTypeExceptions exceptions = method.getExceptions();
                    if (exceptions != null) {
                        for (int e = 0; (exceptions.getException() != null) && (e < exceptions.getException().length); e++) {
                            MethodTypeExceptionsException exception = exceptions.getException(e);
                            if (exception.getQname() != null) {
                                usedTypes.add(exception.getQname());
                            } else {
                                // this is just added in the gui and not in the
                                // actual types list yet
                                // it will be after the save
                            }
                        }
                    }
                }
            }
        }

        // walk through namespace types removing QNames from used types
        NamespacesType namespaces = desc.getNamespaces();
        if (namespaces != null) {
            for (int n = 0; (namespaces.getNamespace() != null) && (usedTypes.size() != 0)
                && (n < namespaces.getNamespace().length); n++) {
                NamespaceType nsType = namespaces.getNamespace(n);
                for (int t = 0; (nsType.getSchemaElement() != null) && (t < nsType.getSchemaElement().length)
                    && (usedTypes.size() != 0); t++) {
                    SchemaElementType type = nsType.getSchemaElement(t);
                    usedTypes.remove(new QName(nsType.getNamespace(), type.getType()));
                }
            }
        }
        return usedTypes;
    }


    /**
     * Returns the input string with the first character converted to lower case
     * 
     * @param variableName
     *            string to fix
     * @return the input string with the first character converted to lowercase
     */
    public static String lowerCaseFirstCharacter(String variableName) {
        return variableName.substring(0, 1).toLowerCase() + variableName.substring(1);
    }


    /**
     * Returns the input string with the first character converted to uppercase
     * 
     * @param variableName
     *            string to fix
     * @return the input string with the first character converted to uppercase
     */
    public static String upperCaseFirstCharacter(String variableName) {
        return variableName.substring(0, 1).toUpperCase() + variableName.substring(1);
    }


    public static String getGlobusLocation() {
        try {
            String globusLocation = System.getenv("GLOBUS_LOCATION");
            if (globusLocation != null) {
                return globusLocation;
            }
        } catch (Throwable ex) {
            ex.printStackTrace();
            String[] error = {"Error getting GLOBUS_LOCATION environment variable: ", ex.getMessage(),
                    "Will now try to get it from system properties"};
            logger.error(error);
        }
        try {
            String globusLocation = System.getProperty("GLOBUS_LOCATION");
            if (globusLocation != null) {
                return globusLocation;
            }
        } catch (Throwable ex) {
            ex.printStackTrace();
            String[] error = {"Error getting GLOBUS_LOCATION system property: ", ex.getMessage(),
                    "Please set your GLOBUS_LOCATION system property!"};
            logger.error(error);
        }
        return null;
    }

}
