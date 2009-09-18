package org.cagrid.data.test.system;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.data.DataServiceConstants;
import gov.nih.nci.cagrid.data.auditing.AuditorConfiguration;
import gov.nih.nci.cagrid.data.auditing.AuditorConfigurationConfigurationProperties;
import gov.nih.nci.cagrid.data.auditing.ConfigurationProperty;
import gov.nih.nci.cagrid.data.auditing.DataServiceAuditors;
import gov.nih.nci.cagrid.data.auditing.MonitoredEvents;
import gov.nih.nci.cagrid.data.service.auditing.FileDataServiceAuditor;
import gov.nih.nci.cagrid.introduce.IntroduceConstants;
import gov.nih.nci.cagrid.introduce.beans.ServiceDescription;
import gov.nih.nci.cagrid.introduce.common.CommonTools;
import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.io.File;

/** 
 *  AddFileSystemAuditorStep
 *  This step adds a file system logging data service auditor
 *  to the system tested data service
 * 
 * @author David Ervin
 * 
 * @created May 25, 2007 1:58:49 PM
 * @version $Id: AddFileSystemAuditorStep.java,v 1.1 2008-05-16 19:25:25 dervin Exp $ 
 */
public class AddFileSystemAuditorStep extends Step {
    
    private String serviceDir = null;
    private String auditorLogFile = null;
    
    public AddFileSystemAuditorStep(String serviceDir, String auditorLogFile) {
        this.serviceDir = serviceDir;
        this.auditorLogFile = auditorLogFile;
    }
    

    public void runStep() throws Throwable {
        System.out.println("Running step: " + getClass().getName());
        
        // set the service property for the auditors config file
        ServiceDescription serviceDesc = (ServiceDescription) Utils.deserializeDocument(
            serviceDir + File.separator + IntroduceConstants.INTRODUCE_XML_FILE, 
            ServiceDescription.class);
        CommonTools.setServiceProperty(serviceDesc, 
            DataServiceConstants.DATA_SERVICE_AUDITORS_CONFIG_FILE_PROPERTY, 
            DataServiceConstants.DATA_SERVICE_AUDITORS_CONFIG_FILE_NAME, true);
        Utils.serializeDocument(
            serviceDir + File.separator + IntroduceConstants.INTRODUCE_XML_FILE,
            serviceDesc, IntroduceConstants.INTRODUCE_SKELETON_QNAME);
        
        // configure the data service auditor
        DataServiceAuditors auditorsDescription = new DataServiceAuditors();
        AuditorConfiguration config = new AuditorConfiguration();
        config.setClassName(FileDataServiceAuditor.class.getName());
        config.setInstanceName("testingDataServiceAuditor");
        MonitoredEvents monitoredEvents = new MonitoredEvents(true, true, true, true);
        config.setMonitoredEvents(monitoredEvents);
        ConfigurationProperty outputFileConfigProp = new ConfigurationProperty();
        outputFileConfigProp.setKey(FileDataServiceAuditor.AUDIT_FILE);
        outputFileConfigProp.setValue(auditorLogFile);
        ConfigurationProperty printResultsConfigProp = new ConfigurationProperty();
        printResultsConfigProp.setKey(FileDataServiceAuditor.PRINT_FULL_RESULTS);
        printResultsConfigProp.setValue(FileDataServiceAuditor.DEFAULT_PRINT_FULL_RESULTS);
        AuditorConfigurationConfigurationProperties configProps = 
            new AuditorConfigurationConfigurationProperties(
                new ConfigurationProperty[] {outputFileConfigProp, printResultsConfigProp});
        config.setConfigurationProperties(configProps);
        auditorsDescription.setAuditorConfiguration(new AuditorConfiguration[] {config});
        
        // serialize the data service auditors config
        String configFilename = serviceDir + File.separator + "etc" 
            + File.separator + DataServiceConstants.DATA_SERVICE_AUDITORS_CONFIG_FILE_NAME;
        Utils.serializeDocument(configFilename, auditorsDescription, 
            DataServiceConstants.DATA_SERVICE_AUDITORS_QNAME);
    }
}
