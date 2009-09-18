package gov.nih.nci.cagrid.common;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;


/**
 * Hold some utilities for managing and creating documents
 * 
 * @author <A href="mailto:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A href="mailto:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @author <A href="mailto:oster@bmi.osu.edu">Scott Oster </A>
 * @created May 16, 2003
 * @version $Id: XMLUtilities.java,v 1.3 2008-02-18 17:18:20 oster Exp $
 */

public class XMLUtilities {

    public static String documentToString(Document doc) {
        XMLOutputter outputter = new XMLOutputter(Format.getRawFormat());
        return outputter.outputString(doc);
    }


    public static String elementToString(Element element) {
        XMLOutputter outputter = new XMLOutputter(Format.getRawFormat());
        return outputter.outputString(element);
    }


    /**
     * Builds a dom document from a file
     * 
     * @param fileName
     * @return A JDom document representation of the file contents
     * @throws Exception
     */
    public static Document fileNameToDocument(String fileName) throws Exception {
        // TODO: add schema validation
        try {
            SAXBuilder builder = new SAXBuilder(false);
            Document doc = builder.build(fileName);
            return doc;
        } catch (Exception e) {
            throw new Exception("Error creating document: " + e.getMessage(), e);
        }
    }


    /**
     * Builds a dom document from a string
     * 
     * @param string
     * @return A JDom document representation of the String's contents
     * @throws Exception
     */
    public static Document stringToDocument(String string) throws Exception {
        return XMLUtilities.streamToDocument(new ByteArrayInputStream(string.getBytes()));
    }


    /**
     * Builds a dom document from a stream
     * 
     * @param stream
     * @return A JDom document representation of the stream's contents
     * @throws Exception
     */
    public static Document streamToDocument(InputStream stream) throws Exception {
        try {
            SAXBuilder builder = new SAXBuilder(false);
            Document doc = builder.build(stream);
            return doc;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Document construction failed:" + e.getMessage(), e);
        }

    }


    public static String formatXML(String s) throws Exception {
        // This is not efficient but ok for now
        try {
            SAXBuilder builder = new SAXBuilder();
            Document doc = builder.build(new StringReader(s));
            XMLOutputter out = new XMLOutputter(Format.getPrettyFormat());
            return out.outputString(doc);
        } catch (Exception e) {
            throw new Exception(e.getMessage(), e);
        }
    }


    private static String bufferToString(BufferedReader br) throws Exception {
        StringBuffer sb = new StringBuffer();
        try {
            for (String s = null; (s = br.readLine()) != null;)
                sb.append(s + "\n");

        } catch (Exception e) {
            throw new Exception("Error reading the buffer: " + e.getMessage());
        }
        return sb.toString();
    }


    public static String streamToString(InputStream stream) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(stream));
        return bufferToString(br);
    }
}
