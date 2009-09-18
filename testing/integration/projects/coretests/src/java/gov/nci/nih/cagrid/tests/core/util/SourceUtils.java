package gov.nci.nih.cagrid.tests.core.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;

import org.apache.ws.jaxme.js.JavaMethod;
import org.apache.ws.jaxme.js.JavaSource;
import org.apache.ws.jaxme.js.JavaSourceFactory;
import org.apache.ws.jaxme.js.util.JavaParser;

import antlr.RecognitionException;
import antlr.TokenStreamException;


public class SourceUtils {
    /**
     * @deprecated Don't use this method - it is based on JAXME and does not
     *             work.
     */
    public static void replaceMethodBodyWithJAXME(File inFile, File outFile, String methodName)
        throws RecognitionException, TokenStreamException, IOException {
        JavaSourceFactory inFactory = new JavaSourceFactory();
        JavaSourceFactory outFactory = new JavaSourceFactory();
        JavaParser inParser = new JavaParser(inFactory);
        JavaParser outParser = new JavaParser(outFactory);

        inParser.parse(inFile);
        JavaSource inSource = (JavaSource) inFactory.getJavaSources().next();
        outParser.parse(inFile);
        JavaSource outSource = (JavaSource) outFactory.getJavaSources().next();
        // sourceI.setForcingFullyQualifiedName(true);

        JavaMethod inMethod = findMethod(inSource, methodName);
        if (inMethod == null)
            throw new IllegalArgumentException("method " + methodName + " not found in " + inFile);
        JavaMethod outMethod = findMethod(outSource, methodName);
        if (inMethod == null)
            throw new IllegalArgumentException("method " + methodName + " not found in " + outFile);

        outMethod.clear();
        for (String line : inMethod.getLines(-1))
            outMethod.addLine(line);

        BufferedWriter out = new BufferedWriter(new FileWriter(outFile));
        outSource.write(out);
        out.flush();
        out.close();
    }


    public static void modifyImpl(File sourceJava, File targetJava, String methodName) throws IOException {
        StringBuffer sourceContent = new StringBuffer(FileUtils.readText(sourceJava));
        StringBuffer targetContent = new StringBuffer(FileUtils.readText(targetJava));

        // SyncSource ss = new SyncSource(new File("junk"), null, null);

        int sourceStartOfMethod = startOfSignature(sourceContent, methodName);
        int sourceEndOfSignature = endOfSignature(sourceContent, sourceStartOfMethod);
        int sourceEndOfImplementation = endOfMethod(sourceContent, sourceEndOfSignature);

        int targetStartOfMethod = startOfSignature(targetContent, methodName);
        int targetEndOfSignature = endOfSignature(targetContent, targetStartOfMethod);
        int targetEndOfImplementation = endOfMethod(targetContent, targetEndOfSignature);

        if (targetEndOfSignature >= 0 && targetEndOfImplementation >= 0) {
            targetContent.delete(targetEndOfSignature, targetEndOfImplementation);
        }
        targetContent.insert(targetEndOfSignature, sourceContent.substring(sourceEndOfSignature,
            sourceEndOfImplementation));

        FileWriter out = new FileWriter(targetJava);
        out.write(targetContent.toString());
        out.flush();
        out.close();
    }


    public static int endOfMethod(StringBuffer sb, int startingIndex) {
        int index = startingIndex;
        if (index < 0)
            return index;

        boolean found = false;
        int openCount = 0;
        do {
            char ch = sb.charAt(index);
            if (ch == '{') {
                openCount++;
                found = true;
            } else if (ch == '}') {
                openCount--;
            }
            index++;
        } while (openCount >= 0);// || ! found);
        return index - 1;
    }


    public static int endOfSignature(StringBuffer sb, int startingIndex) {
        int index = startingIndex;
        if (index < 0) {
            return index;
        }
        boolean found = false;
        while (!found) {
            char ch = sb.charAt(index);
            if (ch == '{') {
                found = true;
            }
            index++;
        }
        return index;
    }


    public static int startOfSignature(StringBuffer sb, String methodName) throws IOException {
        BufferedReader br = new BufferedReader(new StringReader(sb.toString()));
        String line = null;
        while ((line = br.readLine()) != null) {
            int index = line.indexOf(methodName);
            if (index == -1)
                continue;
            index = line.indexOf("public");
            if (index == -1)
                continue;
            br.close();
            return sb.indexOf(line);
        }
        br.close();
        return -1;
    }


    public static JavaMethod findMethod(JavaSource source, String methodName) {
        JavaMethod[] methods = source.getMethods();
        for (int j = 0; j < methods.length; j++) {
            if (methods[j].getName().equals(methodName)) {
                return methods[j];
            }
        }
        return null;
    }
}
