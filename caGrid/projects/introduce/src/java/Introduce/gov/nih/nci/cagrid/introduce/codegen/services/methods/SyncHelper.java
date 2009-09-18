package gov.nih.nci.cagrid.introduce.codegen.services.methods;

import gov.nih.nci.cagrid.introduce.IntroduceConstants;
import gov.nih.nci.cagrid.introduce.beans.method.MethodType;
import gov.nih.nci.cagrid.introduce.beans.method.MethodTypeExceptions;
import gov.nih.nci.cagrid.introduce.beans.method.MethodTypeExceptionsException;
import gov.nih.nci.cagrid.introduce.beans.method.MethodTypeOutput;
import gov.nih.nci.cagrid.introduce.beans.service.ServiceType;
import gov.nih.nci.cagrid.introduce.common.CommonTools;
import gov.nih.nci.cagrid.introduce.common.SchemaInformation;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.apache.ws.jaxme.js.JavaMethod;
import org.apache.ws.jaxme.js.Parameter;


/**
 * SyncHelper Static helper methods for source synchronization operations
 * 
 * @author David Ervin
 * @created Apr 4, 2007 1:32:03 PM
 * @version $Id: SyncHelper.java,v 1.7 2008-05-21 13:06:58 hastings Exp $
 */
public class SyncHelper {
    private static final Logger logger = Logger.getLogger(SyncHelper.class);


    /**
     * Creates the string of exceptions thrown by a method
     * 
     * @param method
     *            The method
     * @param serviceInfo
     *            The service information
     * @return A string containing exceptions thrown by the method, delimited by
     *         commans
     */
    public static String createExceptions(MethodType method, ServiceInformation serviceInfo) {
        StringBuffer exceptions = new StringBuffer();
        exceptions.append("RemoteException");
        // process the faults for this method...
        MethodTypeExceptions exceptionsEl = method.getExceptions();
        if ((exceptionsEl != null) && (exceptionsEl.getException() != null)) {
            if (exceptionsEl.getException().length > 0) {
                exceptions.append(", ");
            }
            for (int i = 0; i < exceptionsEl.getException().length; i++) {
                MethodTypeExceptionsException fault = exceptionsEl.getException(i);
                SchemaInformation info = CommonTools
                    .getSchemaInformation(serviceInfo.getNamespaces(), fault.getQname());
                String ex = info.getType().getPackageName()
                    + "."
                    + CommonTools.upperCaseFirstCharacter(info.getType().getClassName() != null ? info.getType()
                        .getClassName() : info.getType().getType());
                exceptions.append(ex);
                if (i < exceptionsEl.getException().length - 1) {
                    exceptions.append(", ");
                }
            }
        }
        if (exceptions.length() > 0) {
            exceptions.insert(0, "throws ").append(" ");
        }
        return exceptions.toString();
    }


    /**
     * Creates a method signature which returns an unboxed type
     * 
     * @param method
     *            The method to create a Java signature for
     * @param serviceInfo
     *            The service information
     * @return The Java method signature of the method
     */
    public static String createUnBoxedSignatureStringFromMethod(MethodType method, ServiceInformation serviceInfo) {
        StringBuffer methodString = new StringBuffer();
        MethodTypeOutput returnTypeEl = method.getOutput();
        String methodName = CommonTools.lowerCaseFirstCharacter(method.getName());
        String returnType = null;
        if (returnTypeEl.getQName().getNamespaceURI().equals("")
            && returnTypeEl.getQName().getLocalPart().equals("void")) {
            returnType = "void";
        } else {
            SchemaInformation info = CommonTools.getSchemaInformation(serviceInfo.getNamespaces(), returnTypeEl
                .getQName());
            returnType = info.getType().getClassName();
            if ((info.getType().getPackageName() != null) && (info.getType().getPackageName().length() > 0)) {
                returnType = info.getType().getPackageName() + "." + returnType;
            }
            if (returnTypeEl.isIsArray()) {
                returnType += "[]";
            }
        }
        methodString.append("public ").append(returnType).append(" ").append(methodName).append("(");
        if ((method.getInputs() != null) && (method.getInputs().getInput() != null)) {
            for (int j = 0; j < method.getInputs().getInput().length; j++) {
                SchemaInformation info = CommonTools.getSchemaInformation(serviceInfo.getNamespaces(), method
                    .getInputs().getInput(j).getQName());
                String packageName = info.getType().getPackageName();
                String classType = null;
                if ((packageName != null) && (packageName.length() > 0)) {
                    classType = packageName + "." + info.getType().getClassName();
                } else {
                    classType = info.getType().getClassName();
                }
                if (method.getInputs().getInput(j).isIsArray()) {
                    classType += "[]";
                }
                String paramName = method.getInputs().getInput(j).getName();
                methodString.append(classType).append(" ").append(paramName);
                if (j < method.getInputs().getInput().length - 1) {
                    methodString.append(",");
                }
            }
        }
        methodString.append(")");

        return methodString.toString();
    }


    /**
     * Builds a list of client handle classnames for a service
     * 
     * @param serviceInfo
     *            The service information
     * @return A List of Strings of java client class names
     */
    public static List buildServicesClientHandleClassNameList(ServiceInformation serviceInfo) {
        List list = new ArrayList();
        if (serviceInfo.getServices() != null && serviceInfo.getServices().getService() != null) {
            for (int i = 0; i < serviceInfo.getServices().getService().length; i++) {
                ServiceType thisservice = serviceInfo.getServices().getService(i);
                list.add(thisservice.getPackageName() + ".client." + thisservice.getName() + "Client");
            }
        }
        return list;
    }


    /**
     * Creates a method signature which returns an unboxed type
     * 
     * @param method
     *            The method to create a Java signature for
     * @param serviceInfo
     *            The service information
     * @return The Java method signature of the method
     */
    public static String createUnBoxedSignatureStringFromMethod(JavaMethod method, ServiceInformation serviceInfo) {
        StringBuffer methodString = new StringBuffer();
        String methodName = CommonTools.lowerCaseFirstCharacter(method.getName());
        String returnType = "";
        if (buildServicesClientHandleClassNameList(serviceInfo).contains(
             method.getType().getClassName())) {
            returnType += IntroduceConstants.WSADDRESSING_EPR_CLASSNAME;
        } else {
            //if (method.getType().getPackageName().length() > 0) {
            //    returnType += method.getType().getPackageName() + ".";
            //}
            returnType += method.getType().getClassName();
        }
        if (method.getType().isArray()) {
            returnType += "[]";
        }
        methodString.append("public ").append(returnType).append(" ").append(methodName).append("(");
        Parameter[] inputs = method.getParams();
        for (int j = 0; j < inputs.length; j++) {
            String classType = null;
            //if (inputs[j].getType().getPackageName().length() > 0) {
            //    classType = inputs[j].getType().getPackageName() + "." + inputs[j].getType().getClassName();
            //} else {
                classType = inputs[j].getType().getClassName();
            //}
            if (inputs[j].getType().isArray()) {
                classType += "[]";
            }
            String paramName = inputs[j].getName();
            methodString.append(classType).append(" ").append(paramName);
            if (j < inputs.length - 1) {
                methodString.append(",");
            }
        }
        methodString.append(")");
        return methodString.toString();
    }


    /**
     * Creates JavaDoc for a method
     * 
     * @param method
     *            The method
     * @return The javadoc block for the given method
     */
    public static String createJavaDoc(MethodType method) {
        if ((method.getDescription() != null) && !method.getDescription().trim().equals("")) {
            StringBuffer javaDoc = new StringBuffer();
            javaDoc.append(SyncSource.TAB).append("/**\n");
            if (method.getDescription() != null) {
                javaDoc.append(SyncSource.TAB).append(" * ").append(method.getDescription()).append("\n");
                javaDoc.append(SyncSource.TAB).append(" *\n");
            }
            if ((method.getInputs() != null) && (method.getInputs().getInput() != null)
                && (method.getInputs().getInput().length > 0)) {
                for (int i = 0; i < method.getInputs().getInput().length; i++) {
                    javaDoc.append(SyncSource.TAB).append(" * @param ")
                        .append(method.getInputs().getInput(i).getName()).append("\n");
                    if ((method.getInputs().getInput(i).getDescription() != null)
                        && (method.getInputs().getInput(i).getDescription().length() > 0)) {
                        javaDoc.append(SyncSource.TAB).append(" *\t").append(
                            method.getInputs().getInput(i).getDescription()).append("\n");
                    }
                }
            }
            if ((method.getOutput() != null) && (method.getOutput().getDescription() != null)
                && (method.getOutput().getDescription().length() > 0)) {
                javaDoc.append(SyncSource.TAB).append(" * @return ").append(method.getOutput().getDescription())
                    .append("\n");
            }
            if ((method.getExceptions() != null) && (method.getExceptions().getException() != null)
                && (method.getExceptions().getException().length > 0)) {
                for (int i = 0; i < method.getExceptions().getException().length; i++) {
                    javaDoc.append(SyncSource.TAB).append(" * @throws ").append(
                        method.getExceptions().getException(i).getName()).append("\n");
                    if (method.getExceptions().getException(i).getDescription() != null) {
                        javaDoc.append(SyncSource.TAB).append(" *\t").append(
                            method.getExceptions().getException(i).getDescription()).append("\n");
                    }
                }
            }
            javaDoc.append(SyncSource.TAB).append(" */");
            return javaDoc.toString();
        } else {
            return "";
        }
    }


    /**
     * Creates the string of exceptions thrown by a method on the client side
     * 
     * @param method
     *            The method
     * @param serviceInfo
     *            The service information
     * @return A comma delimited string of exceptions
     */
    public static String createClientExceptions(MethodType method, ServiceInformation serviceInfo) {
        StringBuffer exceptions = new StringBuffer();
        exceptions.append("RemoteException");
        // process the faults for this method...
        MethodTypeExceptions exceptionsEl = method.getExceptions();
        if ((method.getOutput().getIsClientHandle() != null) && method.getOutput().getIsClientHandle().booleanValue()) {
            exceptions.append(", org.apache.axis.types.URI.MalformedURIException");
        }
        if ((exceptionsEl != null) && (exceptionsEl.getException() != null)) {
            if (exceptionsEl.getException().length > 0) {
                exceptions.append(", ");
            }
            for (int i = 0; i < exceptionsEl.getException().length; i++) {
                MethodTypeExceptionsException fault = exceptionsEl.getException(i);
                SchemaInformation info = CommonTools
                    .getSchemaInformation(serviceInfo.getNamespaces(), fault.getQname());
                String ex = info.getType().getPackageName()
                    + "."
                    + CommonTools.upperCaseFirstCharacter(info.getType().getClassName() != null ? info.getType()
                        .getClassName() : info.getType().getType());
                exceptions.append(ex);
                if (i < exceptionsEl.getException().length - 1) {
                    exceptions.append(", ");
                }
            }
        }
        if (exceptions.length() > 0) {
            exceptions.insert(0, "throws ");
            exceptions.append(" ");
        }
        return exceptions.toString();
    }


    /**
     * Creates the method signature for the client side which returns an unboxed
     * data type
     * 
     * @param method
     *            The method
     * @param serviceInfo
     *            The service information
     * @return The unboxed method signature
     */
    public static String createClientUnBoxedSignatureStringFromMethod(MethodType method, ServiceInformation serviceInfo) {
        StringBuffer methodString = new StringBuffer();
        MethodTypeOutput returnTypeEl = method.getOutput();
        String methodName = CommonTools.lowerCaseFirstCharacter(method.getName());
        String returnType = null;
        if (returnTypeEl.getQName().getNamespaceURI().equals("")
            && returnTypeEl.getQName().getLocalPart().equals("void")) {
            returnType = "void";
        } else {
            SchemaInformation info = CommonTools.getSchemaInformation(serviceInfo.getNamespaces(), returnTypeEl
                .getQName());
            returnType = info.getType().getClassName();
            if ((info.getType().getPackageName() != null) && (info.getType().getPackageName().length() > 0)) {
                if ((returnTypeEl.getIsClientHandle() != null) && returnTypeEl.getIsClientHandle().booleanValue()) {
                    returnType = returnTypeEl.getClientHandleClass();
                } else {
                    returnType = info.getType().getPackageName() + "." + returnType;
                }
            }
            if (returnTypeEl.isIsArray()) {
                returnType += "[]";
            }
        }

        methodString.append("public ").append(returnType).append(" ").append(methodName).append("(");

        if ((method.getInputs() != null) && (method.getInputs().getInput() != null)) {
            for (int j = 0; j < method.getInputs().getInput().length; j++) {
                SchemaInformation info = CommonTools.getSchemaInformation(serviceInfo.getNamespaces(), method
                    .getInputs().getInput(j).getQName());
                String packageName = info.getType().getPackageName();
                String classType = null;
                if ((packageName != null) && (packageName.length() > 0)) {
                    classType = packageName + "." + info.getType().getClassName();
                } else {
                    classType = info.getType().getClassName();
                }
                if (method.getInputs().getInput(j).isIsArray()) {
                    classType += "[]";
                }
                String paramName = method.getInputs().getInput(j).getName();
                methodString.append(classType).append(" ").append(paramName);
                if (j < method.getInputs().getInput().length - 1) {
                    methodString.append(",");
                }
            }
        }
        methodString.append(")");

        return methodString.toString();
    }


    /**
     * Creates the method signature for the client side which returns an unboxed
     * data type
     * 
     * @param method
     *            The method
     * @return The unboxed method signature
     */
    public static String createClientUnBoxedSignatureStringFromMethod(JavaMethod method) {
        StringBuffer methodString = new StringBuffer();
        String methodName = CommonTools.lowerCaseFirstCharacter(method.getName());
        String returnType = "";
        //if (method.getType().getPackageName().length() > 0) {
        //    returnType += method.getType().getPackageName() + ".";
        //}
        returnType += method.getType().getClassName();
        org.apache.ws.jaxme.js.JavaQName qName = method.getType();
        while(qName.isArray()) {
            returnType += "[]";
            qName = qName.getInstanceClass();
        }
        methodString.append("public ").append(returnType).append(" ").append(methodName).append("(");
        Parameter[] inputs = method.getParams();
        for (int j = 0; j < inputs.length; j++) {
            String classType = null;
            //if (inputs[j].getType().getPackageName().length() > 0) {
            //    classType = inputs[j].getType().getPackageName() + "." + inputs[j].getType().getClassName();
            //} else {
                classType = inputs[j].getType().getClassName();
           // }
            if (inputs[j].getType().isArray()) {
                classType += "[]";
            }
            String paramName = inputs[j].getName();
            methodString.append(classType).append(" ").append(paramName);
            if (j < inputs.length - 1) {
                methodString.append(",");
            }
        }
        methodString.append(")");
        return methodString.toString();
    }


    /**
     * Creates a method signature which returnes the boxed data type
     * 
     * @param method
     *            The method
     * @return The method signature which returns a boxed data type
     */
    public static String createBoxedSignatureStringFromMethod(MethodType method) {
        StringBuffer methodString = new StringBuffer();

        String methodName = CommonTools.lowerCaseFirstCharacter(method.getName());

        if (method.getOutputMessageClass() != null) {
            methodString.append("public ").append(method.getOutputMessageClass()).append(" ").append(methodName)
                .append("(");
        } else {
            methodString.append("public void ").append(methodName).append("(");
        }

        if (method.getInputMessageClass() != null) {
            methodString.append(method.getInputMessageClass()).append(" params");
        }

        methodString.append(")");
        return methodString.toString();
    }


    /**
     * Creates a method signature which returnes the boxed data type
     * 
     * @param method
     *            The method
     * @return The method signature which returns a boxed data type
     */
    public static String createBoxedSignatureStringFromMethod(JavaMethod method) {
        StringBuffer methodString = new StringBuffer();

        String methodName = CommonTools.lowerCaseFirstCharacter(method.getName());

        //if (method.getType().getPackageName() != null && method.getType().getPackageName().length() > 0) {
        //    methodString.append("public ").append(method.getType().getPackageName()).append(".").append(
        //        method.getType().getClassName()).append(" ").append(methodName).append("(");
        //} else {
            methodString.append("public ").append(
                method.getType().getClassName()).append(" ").append(methodName).append("(");
       // }

        methodString.append(method.getParams()[0].getType().getPackageName()).append(".").append(
            method.getParams()[0].getType().getClassName()).append(" params");

        methodString.append(")");
        return methodString.toString();
    }


    /**
     * Removes all cases of more than two consecutive new lines from a string,
     * replacing with just two
     * 
     * @param string
     *            The string to clean up
     * @return The cleaned string
     */
    public static String removeMultiNewLines(String string) {
        return string.replaceAll("\n\n(\n)+", "\n\n");
    }


    /**
     * Locates the matching closing bracket in a block of java code
     * 
     * @param sb
     *            The code to be searched
     * @param startingIndex
     *            The index to begin searching
     * @return The index of the matching bracket, or -1 if none is found
     */
    public static int bracketMatch(StringBuffer sb, int startingIndex) {
        // logger.debug("Starting to look for brackets on this string:");
        // logger.debug(sb.toString().substring(startingIndex));
        int parenCount = 0;
        int index = startingIndex;
        boolean found = false;
        boolean canFind = false;
        while (!found && (index < sb.length()) && (index >= 0)) {
            char ch = sb.charAt(index);
            if (ch == '{') {
                canFind = true;
                parenCount++;
            } else if (ch == '}') {
                parenCount--;
                if (canFind == true) {
                    if (parenCount == 0) {
                        found = true;
                    }
                }
            }
            index++;
        }
        if (found) {
            char ch = sb.charAt(index);
            while ((ch == '\t') || (ch == ' ')) {
                ch = sb.charAt(++index);
            }
            return index;
        } else {
            return -1;
        }
    }


    /**
     * Locates the end of a method signature
     * 
     * @param sb
     *            The block of Java code to be searched
     * @param startingIndex
     *            The starting index of the search
     * @return The index of the end of the method signature
     */
    public static int endOfSignature(StringBuffer sb, int startingIndex) {
        if (startingIndex < 0) {
            return startingIndex;
        }
        int index = sb.indexOf("{", startingIndex);
        return index != -1 ? index + 1 : startingIndex;
    }


    /**
     * Locates the start of a JavaDoc block
     * 
     * @param sb
     *            The block of java code to be searched
     * @param startOfMethod
     *            The starting index of the method
     * @return The starting index of the method's javadoc block
     */
    public static int startOfJavaDoc(StringBuffer sb, int startOfMethod) {
        BufferedReader br = new BufferedReader(new StringReader(sb.toString()));
        List backwardsBuffer = new ArrayList();
        try {
            String line = br.readLine() + "\n";
            int totalRead = 0;
            totalRead += line.length();
            if (totalRead < startOfMethod) {
                backwardsBuffer.add(0, line);
            }
            while ((line != null) && (totalRead < startOfMethod)) {
                line = br.readLine() + "\n";
                totalRead += line.length();
                if (totalRead <= startOfMethod) {
                    backwardsBuffer.add(0, line);
                }
            }
        } catch (Exception e) {
            logger.error(e);
            return startOfMethod;
        }

        int javaDocLength = 0;
        boolean stillSearch = true;
        int lineBack = 0;
        while (stillSearch) {
            String line = (String) backwardsBuffer.get(lineBack++);
            String trimmedLine = line.trim();
            if (trimmedLine.startsWith("\n")) {
                javaDocLength += line.length();
            } else if (trimmedLine.startsWith("*")) {
                javaDocLength += line.length();
            } else if (trimmedLine.startsWith("/*")) {
                javaDocLength += line.length();
                stillSearch = false;
            } else {
                javaDocLength = 0;
                stillSearch = false;
            }
        }

        return startOfMethod - javaDocLength;
    }


    public static int startOfSignature(StringBuffer sb, String searchString) {
        BufferedReader br = new BufferedReader(new StringReader(sb.toString()));
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
            if (found) {
                // logger.debug("Found start of method: " + matchedLine);
            } else {
                logger.debug("Did not find the appropriate match");
            }
            // if the last line i found the match then lets look for the start
            // of the method
            if (found) {
                StringTokenizer searchStringTokenizer = new StringTokenizer(searchString);
                String startToken = searchStringTokenizer.nextToken();
                int index = charsRead + matchedLine.indexOf(startToken);

                char prevChar = sb.toString().charAt(--index);
                while ((prevChar != '\n') && ((prevChar == ' ') || (prevChar == '\t'))) {
                    prevChar = sb.toString().charAt(--index);
                }
                index++;
                return index;
            }
        } catch (IOException e) {
            logger.error(e);
        }
        return -1;
    }
}
