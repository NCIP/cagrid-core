package gov.nih.nci.cagrid.data.service.auditing;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.data.CqlSchemaConstants;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.util.Date;
import java.util.Properties;

/** 
 *  FileDataServiceAuditor
 *  A simple data service auditor which logs information to a file
 * 
 * @author David Ervin
 * 
 * @created May 21, 2007 9:53:51 AM
 * @version $Id: FileDataServiceAuditor.java,v 1.2 2007-05-25 19:39:00 dervin Exp $ 
 */
public class FileDataServiceAuditor extends DataServiceAuditor {
    
    public static final String AUDIT_FILE = "auditingFileName";
    public static final String DEFAULT_AUDIT_FILE = "dataServiceAuditing.log";
    public static final String PRINT_FULL_RESULTS = "printFullResults";
    public static final String DEFAULT_PRINT_FULL_RESULTS = Boolean.FALSE.toString();
    
    private BufferedWriter writer = null;
   
    public FileDataServiceAuditor() {
        super();
    }
    
    
    public Properties getDefaultConfigurationProperties() {
        Properties props = new Properties();
        props.setProperty(AUDIT_FILE, DEFAULT_AUDIT_FILE);
        return props;
    }


    public void auditQueryBegin(QueryBeginAuditingEvent event) {
        try {
            writeToLog("Query Begins at " + getCurrentTime() + "\n");
            writeToLog(getBaseEventInformation(event));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    public void auditQueryProcessingFailed(QueryProcessingFailedAuditingEvent event) {
        try {
            writeToLog("Query Processing Failed at " + getCurrentTime() + "\n");
            writeToLog(getBaseEventInformation(event));
            StringWriter exceptionStringWriter = new StringWriter();
            PrintWriter exceptionPrintWriter = new PrintWriter(exceptionStringWriter);
            event.getQueryProcessingException().printStackTrace(exceptionPrintWriter);
            writeToLog("\tException follows:\n");
            writeToLog(exceptionStringWriter.getBuffer().toString());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    public void auditQueryResults(QueryResultsAuditingEvent event) {
        try {
            boolean fullResults = Boolean.valueOf(
                getConfiguredProperties().getProperty(PRINT_FULL_RESULTS)).booleanValue();
            
            writeToLog("Query Results at " + getCurrentTime() + "\n");
            writeToLog(getBaseEventInformation(event));
            if (fullResults) {
                writeToLog("Query Results follow:\n");
                StringWriter resultsWriter = new StringWriter();
                try {
                    Utils.serializeObject(event.getResults(), 
                        CqlSchemaConstants.CQL_RESULT_SET_QNAME, resultsWriter);
                } catch (Exception ex) {
                    resultsWriter.append("ERROR SERIALIZING CQL RESULTS: " + ex.getMessage());
                    ex.printStackTrace();
                }
                writeToLog(resultsWriter.getBuffer().toString());
            } else {
                String resultType = null;
                if (event.getResults().getAttributeResult() != null) {
                    resultType = "Attribute";
                } else if (event.getResults().getCountResult() != null) {
                    resultType = "Count";
                } else {
                    resultType = "Object"; 
                }
                writeToLog("Query Returned " + resultType + " results");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    public void auditValidation(ValidationAuditingEvent event) {
        try {
            writeToLog("Validation Failed at " + getCurrentTime() + "\n");
            writeToLog(getBaseEventInformation(event));
            StringWriter exceptionStringWriter = new StringWriter();
            PrintWriter exceptionPrintWriter = new PrintWriter(exceptionStringWriter);
            if (event.getCqlStructureException() != null) {
                writeToLog("\tCQL Structure Validation failed:\n");
                event.getCqlStructureException().printStackTrace(exceptionPrintWriter);
            } else {
                writeToLog("\tDomain Validation failed:\n");
                event.getDomainValidityException().printStackTrace(exceptionPrintWriter);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    
    private String getCurrentTime() {
        return DateFormat.getDateInstance(DateFormat.MEDIUM).format(new Date());
    }
    
    
    private String getBaseEventInformation(BaseAuditingEvent event) {
        StringBuffer buff = new StringBuffer();
        buff.append("\t").append("Caller: ").append(event.getCallerId()).append("\n");
        StringWriter cqlWriter = new StringWriter();
        try {
            Utils.serializeObject(event.getQuery(), CqlSchemaConstants.CQL_QUERY_QNAME, cqlWriter);
        } catch (Exception ex) {
            cqlWriter.append("ERROR SERIALIZING QUERY: " + ex.getMessage());
            ex.printStackTrace();
        }
        buff.append("\t").append(cqlWriter.getBuffer().toString()).append("\n");
        return buff.toString();
    }
    
    
    private void writeToLog(String message) throws IOException {
        if (writer == null) {
            String outputFilename = getConfiguredProperties().getProperty(AUDIT_FILE);
            writer = new BufferedWriter(new FileWriter(outputFilename));
        }
        writer.write(message);
        writer.flush();
    }
    
    
    public void finalize() {
        try {
            if (writer != null) {
                writer.close();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
