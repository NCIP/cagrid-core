package gov.nih.nci.cagrid.common;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.lang.reflect.Array;
import java.net.URL;
import java.net.URLDecoder;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.axis.AxisEngine;
import org.apache.axis.AxisFault;
import org.apache.axis.EngineConfiguration;
import org.apache.axis.MessageContext;
import org.apache.axis.configuration.FileProvider;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.message.MessageElement;
import org.apache.axis.server.AxisServer;
import org.apache.axis.utils.XMLUtils;
import org.globus.common.CoGProperties;
import org.globus.gsi.CertificateRevocationLists;
import org.globus.gsi.GlobusCredential;
import org.globus.gsi.TrustedCertificates;
import org.globus.gsi.proxy.ProxyPathValidator;
import org.globus.gsi.proxy.ProxyPathValidatorException;
import org.globus.wsrf.encoding.DeserializationException;
import org.globus.wsrf.encoding.ObjectDeserializer;
import org.globus.wsrf.encoding.ObjectSerializer;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


public class Utils {

    public static <T> List<T> asList(T... a) {
        if (a == null) {
            return new ArrayList<T>();
        } else {
            return Arrays.asList(a);
        }
    }


    public static File getCaGridUserHome() {
        String userHome = System.getProperty("user.home");
        File userHomeF = new File(userHome);
        File caGridCache = new File(userHomeF.getAbsolutePath() + File.separator + ".cagrid");
        if (!caGridCache.exists()) {
            caGridCache.mkdirs();
        }
        return caGridCache;
    }


    public static File getTrustedCerificatesDirectory() {
        String caDir = CoGProperties.getDefault().getCaCertLocations();
        if (caDir != null) {
            return new File(caDir);
        } else {
            String userHome = System.getProperty("user.home");
            File userHomeF = new File(userHome);
            File dir = new File(userHomeF.getAbsolutePath() + File.separator + ".globus" + File.separator
                + "certificates");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            return dir;
        }
    }


    public static void validateGlobusCredential(GlobusCredential cred) throws ProxyPathValidatorException {
        validateCertificateChain(cred.getCertificateChain());
    }


    public static void validateCertificateChain(X509Certificate[] chain) throws ProxyPathValidatorException {
        ProxyPathValidator validator = new ProxyPathValidator();
        validator.validate(chain, TrustedCertificates.getDefaultTrustedCertificates().getCertificates(),
            CertificateRevocationLists.getDefaultCertificateRevocationLists());

    }


    public static String getExceptionMessage(Throwable e) {
        String mess = e.getMessage();
        if (e instanceof AxisFault) {
            AxisFault af = (AxisFault) e;
            if ((af.getFaultCode() != null)
                && (af.getFaultCode().toString()
                    .equals("{http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd}General"))) {
                System.out.println(af.getFaultString());
                if ((af.getFaultString() != null)
                    && (af.getFaultString().equals("javax.xml.rpc.soap.SOAPFaultException"))) {
                    mess = "An error occurred establishing a secure communication channel.  The "
                        + "problem may be that the client's credentials are NOT trusted by the server.";
                } else {

                    mess = af.getFaultString();
                }

            } else if ((af.getFaultString() != null) && (af.getFaultString().equals("java.io.EOFException"))) {
                mess = "An error occurred in communicating with the service.  If using "
                    + "credentials to authenticate to the service, the problem may be "
                    + "that the credentials being used are not trusted by the server.";
            } else if ((af.getFaultString() != null)
                && (af.getFaultString().equals("java.net.SocketException: Connection reset"))) {
                mess = "An error occurred in communicating with the service.  If using "
                    + "credentials to authenticate to the service, the problem may be "
                    + "that the credentials being used are not trusted by the server.";
            } else {
                mess = af.getFaultString();
            }
        }
        return simplifyErrorMessage(mess);
    }


    public static String simplifyErrorMessage(String m) {
        if ((m == null) || (m.equalsIgnoreCase("null"))) {
            m = "Unknown Error";
        } else if (m.indexOf("Connection refused") >= 0) {
            m = "Error establishing a connection to the requested service, the service may not exist or may be down.";
        } else if (m.indexOf("Unknown CA") >= 0) {
            m = "Could establish a connection with the service, the service CA is not trusted.";
        }
        return m;
    }


    public static <T> T deserializeDocument(String fileName, Class<T> objectType) throws Exception {
        InputStream inputStream = null;

        inputStream = new FileInputStream(fileName);
        org.w3c.dom.Document doc = XMLUtils.newDocument(inputStream);
        Object obj = ObjectDeserializer.toObject(doc.getDocumentElement(), objectType);
        inputStream.close();
        return objectType.cast(obj);
    }


    public static void copyFile(File in, File out) throws IOException {
        File inCannon = in.getCanonicalFile();
        File outCannon = out.getCanonicalFile();
        // avoids copying a file to itself
        if (inCannon.equals(outCannon)) {
            return;
        }
        // ensure the output file location exists
        outCannon.getParentFile().mkdirs();

        BufferedInputStream fis = new BufferedInputStream(new FileInputStream(inCannon));
        BufferedOutputStream fos = new BufferedOutputStream(new FileOutputStream(outCannon));

        // a temporary buffer to read into
        byte[] tmpBuffer = new byte[8192];
        int len = 0;
        while ((len = fis.read(tmpBuffer)) != -1) {
            // add the temp data to the output
            fos.write(tmpBuffer, 0, len);
        }
        // close the input stream
        fis.close();
        // close the output stream
        fos.flush();
        fos.close();
    }


    // Copies all files under srcDir to dstDir.
    // If dstDir does not exist, it will be created.
    public static void copyDirectory(File srcDir, File dstDir) throws IOException {
        if (srcDir.isDirectory()) {
            if (!dstDir.exists()) {
                dstDir.mkdir();
            }

            String[] children = srcDir.list();
            for (int i = 0; i < children.length; i++) {
                copyDirectory(new File(srcDir, children[i]), new File(dstDir, children[i]));
            }
        } else {
            copyFile(srcDir, dstDir);
        }
    }


    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    System.err.println("could not remove directory: " + dir.getAbsolutePath());
                    return false;
                }
            }
        }
        return dir.delete();
    }


    /**
     * Merges the two arrays (not necessarily creating a new array). If both are
     * null, null is returned. If one is null, the other is returned.
     * 
     * @throws ArrayStoreException
     *             uses System.arrarycopy and has same contract
     */
    public static java.lang.Object concatenateArrays(Class<?> resultClass, java.lang.Object arr1, java.lang.Object arr2)
        throws ArrayStoreException {
        if (arr1 == null) {
            return arr2;
        } else if (arr2 == null) {
            return arr1;
        }
        java.lang.Object newArray = Array.newInstance(resultClass, Array.getLength(arr1) + Array.getLength(arr2));
        System.arraycopy(arr1, 0, newArray, 0, Array.getLength(arr1));
        System.arraycopy(arr2, 0, newArray, Array.getLength(arr1), Array.getLength(arr2));

        return newArray;
    }


    /**
     * Appends to an array
     * 
     * @param array
     *            The array to append to
     * @param appendix
     *            The object to append to the array
     * @return An array with the new item appended
     */
    public static java.lang.Object appendToArray(java.lang.Object array, java.lang.Object appendix) {
        Class<?> arrayType = array.getClass().getComponentType();
        java.lang.Object newArray = Array.newInstance(arrayType, Array.getLength(array) + 1);
        System.arraycopy(array, 0, newArray, 0, Array.getLength(array));
        Array.set(newArray, Array.getLength(newArray) - 1, appendix);
        return newArray;
    }


    /**
     * Removes an object from an array.
     * 
     * @param array
     * @param removal
     * @return An array with the item removed
     */
    public static java.lang.Object removeFromArray(java.lang.Object array, java.lang.Object removal) {
        Class<?> arrayType = array.getClass().getComponentType();
        int length = Array.getLength(array);
        List<Object> temp = new ArrayList<Object>(length - 1);
        for (int i = 0; i < length; i++) {
            java.lang.Object o = Array.get(array, i);
            if (!o.equals(removal)) {
                temp.add(o);
            }
        }
        java.lang.Object newArray = Array.newInstance(arrayType, temp.size());
        System.arraycopy(temp.toArray(), 0, newArray, 0, temp.size());
        return newArray;
    }


    /**
     * Trims an array
     * 
     * @param array
     * @param startIndex
     * @param endIndex
     * @return
     */
    public static java.lang.Object trimArray(java.lang.Object array, int startIndex, int endIndex) {
        Class<?> arrayType = array.getClass().getComponentType();
        int length = endIndex - startIndex;
        java.lang.Object newArray = Array.newInstance(arrayType, length);
        System.arraycopy(array, startIndex, newArray, 0, length);
        return newArray;
    }


    public static StringBuffer fileToStringBuffer(File file) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(file));
        StringBuffer sb = new StringBuffer();
        try {
            String s = null;
            while ((s = br.readLine()) != null) {
                sb.append(s + "\n");
            }
        } finally {
            br.close();
        }

        return sb;
    }


    public static StringBuffer inputStreamToStringBuffer(InputStream stream) throws IOException {
        InputStreamReader reader = new InputStreamReader(stream);
        StringBuffer str = new StringBuffer();
        char[] buff = new char[8192];
        int len = 0;
        while ((len = reader.read(buff)) != -1) {
            str.append(buff, 0, len);
        }
        reader.close();
        return str;
    }


    /**
     * Serialize an Object to XML
     * 
     * @param obj
     *            The object to be serialized
     * @param qname
     *            The QName of the object
     * @param writer
     *            A writer to place XML into (eg: FileWriter, StringWriter). If
     *            a file writer is used, be sure to close it!
     * @param wsdd
     *            A stream containing the WSDD configuration
     * @throws Exception
     */
    public static void serializeObject(Object obj, QName qname, Writer writer, InputStream wsdd) throws Exception {
        // derive a message element for the object
        MessageElement element = (MessageElement) ObjectSerializer.toSOAPElement(obj, qname);
        // configure the axis engine to use the supplied wsdd file
        EngineConfiguration engineConfig = new FileProvider(wsdd);
        AxisEngine axisClient = new AxisServer(engineConfig);
        MessageContext messageContext = new MessageContext(axisClient);
        messageContext.setEncodingStyle("");
        messageContext.setProperty(AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        // the following two properties prevent xsd types from appearing in
        // every single element in the serialized XML
        messageContext.setProperty(AxisEngine.PROP_EMIT_ALL_TYPES, Boolean.FALSE);
        messageContext.setProperty(AxisEngine.PROP_SEND_XSI, Boolean.FALSE);

        // create a serialization context to use the new message context
        SerializationContext serializationContext = new SerializationContext(writer, messageContext);
        serializationContext.setPretty(true);

        // output the message element through the serialization context
        element.output(serializationContext);
        writer.write("\n");
        // writer.close();
        writer.flush();
    }


    public static void serializeObject(Object obj, QName qname, Writer writer) throws Exception {
        // derive a message element for the object
        MessageElement element = (MessageElement) ObjectSerializer.toSOAPElement(obj, qname);
        // create a message context
        MessageContext messageContext = new MessageContext(new AxisServer());
        messageContext.setEncodingStyle("");
        messageContext.setProperty(AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        // the following two properties prevent xsd types from appearing in
        // every single element in the serialized XML
        messageContext.setProperty(AxisEngine.PROP_EMIT_ALL_TYPES, Boolean.FALSE);
        messageContext.setProperty(AxisEngine.PROP_SEND_XSI, Boolean.FALSE);

        // create a serialization context to use the new message context
        SerializationContext serializationContext = new SerializationContext(writer, messageContext);
        serializationContext.setPretty(true);

        // output the message element through the serialization context
        element.output(serializationContext);
        writer.write("\n");
        // writer.close();
        writer.flush();
    }


    /**
     * Deserializes XML into an object
     * 
     * @param xmlReader
     *            The reader for the XML (eg: FileReader, StringReader, etc)
     * @param clazz
     *            The class to serialize to
     * @param wsdd
     *            A stream containing the WSDD configuration
     * @return The object deserialized from the XML
     * @throws SAXException
     * @throws DeserializationException
     */
    public static <T> T deserializeObject(Reader xmlReader, Class<T> clazz, InputStream wsdd) throws SAXException,
        DeserializationException {
        // input source for the xml
        InputSource xmlSource = new InputSource(xmlReader);

        return ConfigurableObjectDeserializer.toObject(xmlSource, clazz, wsdd);
    }


    public static <T> T deserializeObject(Reader xmlReader, Class<T> clazz) throws Exception {
        org.w3c.dom.Document doc = XMLUtils.newDocument(new InputSource(xmlReader));
        Object obj = ObjectDeserializer.toObject(doc.getDocumentElement(), clazz);
        return clazz.cast(obj);
    }


    public static void serializeDocument(String fileName, Object object, QName qname) throws Exception {
        FileWriter fw = null;
        fw = new FileWriter(fileName);
        ObjectSerializer.serialize(fw, object, qname);
        fw.close();
    }


    public static String clean(String s) {
        if ((s == null) || (s.trim().length() == 0)) {
            return null;
        } else {
            return s;
        }
    }


    public static void stringBufferToFile(StringBuffer string, String fileName) throws IOException {
    	stringBufferToFile(string, new File(fileName));
    }

    public static void stringBufferToFile(StringBuffer string, File file) throws IOException {
        FileWriter fw = new FileWriter(file);
        fw.write(string.toString());
        fw.close();
    }


    /**
     * Like Object.equals, but if o1 == null && o2 == null, returns true
     * 
     * @param o1
     * @param o2
     * @return
     */
    public static boolean equals(Object o1, Object o2) {
        if (o1 == null) {
            return o2 == null;
        }
        return o1.equals(o2);
    }


    /**
     * Gets the QName that Axis has registered for the given java class
     * 
     * @param clazz
     * @return The QName corresponding to the class registered in the Axis type
     *         mappings
     */
    public static QName getRegisteredQName(Class<?> clazz) {
        return MessageContext.getCurrentContext().getTypeMapping().getTypeQName(clazz);
    }


    /**
     * Gets the Class that Axis has registerd for the given QName
     * 
     * @param qname
     * @return The class corresponding to the QName as registered in the Axis
     *         type mappings
     */
    public static Class<?> getRegisteredClass(QName qname) {
        return MessageContext.getCurrentContext().getTypeMapping().getClassForQName(qname);
    }


    public static List<File> recursiveListFiles(File baseDir, final FileFilter filter) {
        FileFilter dirFilter = new FileFilter() {
            public boolean accept(File pathname) {
                return pathname.isDirectory() || filter.accept(pathname);
            }
        };
        File[] fileArray = baseDir.listFiles(dirFilter);
        List<File> files = new ArrayList<File>(fileArray.length);
        for (int i = 0; i < fileArray.length; i++) {
            if (fileArray[i].isDirectory()) {
                files.addAll(recursiveListFiles(fileArray[i], filter));
            } else {
                files.add(fileArray[i]);
            }
        }
        return files;
    }


    /**
     * Gets a relative path from the source file to the destination
     * 
     * @param source
     *            The source file or location
     * @param destination
     *            The file to target with the relative path
     * @return The relative path from the source file's directory to the
     *         destination file
     */
    public static String getRelativePath(File source, File destination) throws IOException {
        String sourceDir = null;
        String destDir = null;
        if (source.isDirectory()) {
            sourceDir = source.getCanonicalPath();
        } else {
            sourceDir = source.getParentFile().getCanonicalPath();
        }
        if (destination.isDirectory()) {
            destDir = destination.getCanonicalPath();
        } else {
            destDir = destination.getParentFile().getCanonicalPath();
        }

        // find the overlap in the source and dest paths
        String overlap = findOverlap(sourceDir, destDir);
        // strip off a training File.separator
        if (overlap.endsWith(File.separator)) {
            if (overlap.equals(File.separator)) {
                overlap = "";
            } else {
                overlap = overlap.substring(0, overlap.length() - File.separator.length() - 1);
            }
        }
        int overlapDirs = countChars(overlap, File.separatorChar);
        if (overlapDirs == 0) {
            // no overlap at all, return full path of destination file
            return destination.getCanonicalPath();
        }
        // difference is the number of path elements to back up before moving
        // down the tree
        int parentDirsNeeded = countChars(sourceDir, File.separatorChar) - overlapDirs;
        // difference is the number of path elements above the file to keep
        int parentDirsKept = countChars(destDir, File.separatorChar) - overlapDirs;

        // build the path
        StringBuffer relPath = new StringBuffer();
        for (int i = 0; i < parentDirsNeeded; i++) {
            relPath.append("..").append(File.separatorChar);
        }
        List<String> parentPaths = new LinkedList<String>();
        File parentDir = new File(destDir);
        for (int i = 0; i < parentDirsKept; i++) {
            parentPaths.add(parentDir.getName());
            parentDir = parentDir.getParentFile();
        }
        Collections.reverse(parentPaths);
        for (Iterator<String> i = parentPaths.iterator(); i.hasNext();) {
            relPath.append(i.next()).append(File.separatorChar);
        }
        if (!destination.isDirectory()) {
            relPath.append(destination.getName());
        }
        return relPath.toString();
    }


    private static String findOverlap(String s1, String s2) {
        // TODO: More efficient would be some kind of binary search, divide and
        // conquer
        StringBuffer overlap = new StringBuffer();
        int count = Math.min(s1.length(), s2.length());
        for (int i = 0; i < count; i++) {
            char c1 = s1.charAt(i);
            char c2 = s2.charAt(i);
            if (c1 == c2) {
                overlap.append(c1);
            } else {
                break;
            }
        }
        return overlap.toString();
    }


    private static int countChars(String s, char c) {
        int count = 0;
        int index = -1;
        while ((index = s.indexOf(c, index + 1)) != -1) {
            count++;
        }
        return count;
    }


    public static Object cloneBean(Object bean, QName qname) throws Exception {
        StringWriter writer = new StringWriter();
        serializeObject(bean, qname, writer);
        return deserializeObject(new StringReader(writer.getBuffer().toString()), bean.getClass());
    }


    public static String decodeUrl(URL url) throws UnsupportedEncodingException {
        String enc = System.getProperty("file.encoding");
        String decode = URLDecoder.decode(url.getFile(), enc);
        return decode;
    }


    public static String encodeUrl(String url) {
        char[] badChars = ";?#&=+$, <>~".toCharArray();
        String[] replace = {"%3B", "%3F", "%23", "%24", "%3D", "%2B", "%26", "%2C", "%20", "%3C", "%3E", "%7E"};
        for (int i = 0; i < badChars.length; i++) {
            url = url.replace(String.valueOf(badChars[i]), replace[i]);
        }
        return url;
    }
}
