package gov.nih.nci.cagrid.metadata.xmi;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.common.XMLUtilities;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasciidammit.ConfigurationException;
import org.jasciidammit.JAsciiDammit;
import org.jdom.Element;
import org.jdom.filter.Filter;

/** 
 *  XmiCleaner
 *  Utility to clean up an XMI document so it can be processed
 *  by the fix-xmi task and the SDK
 * 
 * @author David Ervin
 * 
 * @created Dec 10, 2007 12:51:39 PM
 * @version $Id: XmiCleaner.java,v 1.4 2009-04-10 15:58:18 dervin Exp $ 
 */
public class XmiCleaner {
    private static Log log = LogFactory.getLog(XmiCleaner.class);
    
    @SuppressWarnings("all")
    public static final String ERROR_APOSTROPHE = "â€™";
    
    @SuppressWarnings("all")
    public static final String ERROR_DOUBLE_OPEN_BRACKET = "«";
    @SuppressWarnings("all")
    public static final String ERROR_DOUBLE_CLOSE_BRACKET = "»";
    
    // problematic elements in the original XMI
    public static final String DOCTYPE_UML_EA = "<!DOCTYPE XMI SYSTEM \"UML_EA.dtd\">";

    
    public static void cleanXmi(StringBuffer xmiContents) throws IOException {
        cleanDoctype(xmiContents);
        cleanBrokenBrackets(xmiContents);
        cleanSmartquotes(xmiContents);
        cleanTaggedValues(xmiContents);
        cleanUmlDiagrams(xmiContents);
    }
    
    
    private static void cleanSmartquotes(StringBuffer xmiContents) throws IOException {
        String raw = xmiContents.toString();
        raw = raw.replace(ERROR_APOSTROPHE, "'");
        try {
            raw = new JAsciiDammit().translate(raw);
            xmiContents.delete(0, xmiContents.length());
            xmiContents.append(raw);
        } catch (ConfigurationException ex) {
            ex.printStackTrace();
            IOException ioe = new IOException(ex.getMessage());
            ioe.initCause(ex);
            throw ioe;
        }
    }
    
    
    private static void cleanDoctype(StringBuffer xmiContents) {
        int start = xmiContents.indexOf(DOCTYPE_UML_EA); 
        if (start != -1) {
            log.debug("OFFENDING DOCTYPE ELEMENT FOUND");
            xmiContents.delete(start, start + DOCTYPE_UML_EA.length());
        }
    }
    
    
    private static void cleanTaggedValues(StringBuffer xmiContents) throws IOException {
        // filter for <UML:TaggedValue ../>
        Filter taggedValueFilter = new Filter() {
            public boolean matches(Object obj) {
                if (obj instanceof Element) {
                    Element elem = (Element) obj;
                    if (elem.getName().equals("TaggedValue")) {
                        return elem.getAttribute("value") == null;
                    }
                }
                return false;
            }
        };
        
        int removed = removeElementsByFilter(xmiContents, taggedValueFilter);
        log.debug("Removed " + removed + " TaggedValue elements");
    }
    
    
    private static void cleanUmlDiagrams(StringBuffer xmiContents) throws IOException {
        // filter for <UML:Diagram ../>
        Filter diagramFilter = new Filter() {
            public boolean matches(Object obj) {
                if (obj instanceof Element) {
                    Element elem = (Element) obj;
                    return elem.getName().equals("Diagram");
                }
                return false;
            }
        };
        
        int removed = removeElementsByFilter(xmiContents, diagramFilter);
        log.debug("Removed " + removed + " Diagram elements");
    }
    
    
    private static void cleanBrokenBrackets(StringBuffer xmiContents) {
        int removed = 0;
        StringBuffer cleaned = new StringBuffer();
        StringTokenizer tokenizer = new StringTokenizer(xmiContents.toString(), "\n");
        while (tokenizer.hasMoreElements()) {
            String line = tokenizer.nextToken();
            if (!line.contains(ERROR_DOUBLE_CLOSE_BRACKET) && !line.contains(ERROR_DOUBLE_OPEN_BRACKET)) {
                cleaned.append(line).append("\n");
            } else {
                removed++;
            }
        }
        
        xmiContents.delete(0, xmiContents.length());
        xmiContents.append(cleaned.toString());
        log.debug("Removed " + removed + " elements with broken brackets");
    }
    
    
    private static int removeElementsByFilter(StringBuffer xmiContents, Filter filter) throws IOException {
        Element xmiElement = null;
        try {
            xmiElement = XMLUtilities.stringToDocument(xmiContents.toString()).getRootElement();
        } catch (Exception ex) {
            IOException ioe = new IOException(ex.getMessage());
            ioe.initCause(ex);
            throw ioe;
        }
        
        Iterator filterElements = xmiElement.getDescendants(filter);
        List<Element> removeElements = new LinkedList<Element>();
        while (filterElements.hasNext()) {
            removeElements.add((Element) filterElements.next());
        }
        for (Element removeMe : removeElements) {
            removeMe.detach();
        }
        
        String cleanXmi = null;
        try {
            cleanXmi = XMLUtilities.formatXML(XMLUtilities.elementToString(xmiElement));
        } catch (Exception ex) {
            IOException ioe = new IOException(ex.getMessage());
            ioe.initCause(ex);
            throw ioe;
        }
        xmiContents.delete(0, xmiContents.length());
        xmiContents.append(cleanXmi);
        
        return removeElements.size();
    }
    
    
    private static Options getCommandOptions() {
        Option xmiFileOption = OptionBuilder.create("inputXmi");
        xmiFileOption.setRequired(true);
        xmiFileOption.setDescription("The XMI file to clean");
        
        Option outFileOption = OptionBuilder.create("outputXmi");
        outFileOption.setRequired(false);
        outFileOption.setDescription("The cleaned XMI file to output.  " +
            "If ommited, the output will be to the same directory as the inputXml.");
        
        Options opts = new Options();
        opts.addOption(xmiFileOption);
        opts.addOption(outFileOption);
        return opts;
    }
    

    public static void main(String[] args) {
        Options options = getCommandOptions();
        // parse the command line
        CommandLine cmd = null;
        try {
            cmd = new BasicParser().parse(options, args);
        } catch (ParseException e) {
            log.debug("Error parsing arguments: " + e.getMessage());
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(XmiCleaner.class.getSimpleName(), options);
            System.exit(-1);
            return;
        }
        
        String inputFilename = cmd.getOptionValue("inputXmi");
        File inputXmiFile = new File(inputFilename);
        if (!inputXmiFile.isFile()) {
            String message = inputFilename + " does not appear to be a file";
            log.error(message);
            System.err.println(message);
            System.exit(1);
        }
        
        String outputFilename = cmd.getOptionValue("outputXmi");
        File outputXmiFile = null;
        if (outputFilename == null || outputFilename.length() == 0) {
            File xmiDir = inputXmiFile.getParentFile();
            outputXmiFile = new File(xmiDir, "cleaned_" + inputXmiFile.getName());
        } else {
            outputXmiFile = new File(outputFilename);
        }
        
        StringBuffer xmiContents = null;
        try {
            log.debug("Loading xmi from " + inputXmiFile.getAbsolutePath());
            xmiContents = Utils.fileToStringBuffer(inputXmiFile);
            log.debug("Cleaning xmi");
            cleanXmi(xmiContents);
            log.debug("Writing xmi to " + outputXmiFile.getAbsolutePath());
            Utils.stringBufferToFile(xmiContents, outputXmiFile.getAbsolutePath());
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(1);
        }
    }
}
