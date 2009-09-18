package gov.nih.nci.cagrid.data.service.auditing;

import gov.nih.nci.cagrid.data.auditing.AuditorConfiguration;
import gov.nih.nci.cagrid.data.auditing.ConfigurationProperty;

import java.util.Properties;

/** 
 *  DataServiceAuditor
 *  Base class for all data service auditors
 * 
 * @author David Ervin
 * 
 * @created May 17, 2007 10:50:11 AM
 * @version $Id: DataServiceAuditor.java,v 1.3 2007-05-24 16:11:00 dervin Exp $ 
 */
public abstract class DataServiceAuditor {

    private AuditorConfiguration configuration;
    
    public DataServiceAuditor() {
        
    }
    
    
    /**
     * Returns the configured instance name of this auditor.
     * If the configuration is null, a NullPointerException will
     * probably be thrown here
     * 
     * @return
     *      The name of this auditor instance
     */
    public String getInstanceName() {
        return configuration.getInstanceName();
    }
    
    
    public AuditorConfiguration getAuditorConfiguration() {
        return configuration;
    }
    
    
    public void setAuditorConfiguration(AuditorConfiguration config) {
        this.configuration = config;
    }
    
    
    /**
     * Subclasses should override this method to return a Properties
     * instance populated with keys and values representing the default
     * configuration values for this auditor
     * 
     * @return
     *      The default configuration properties
     */
    public Properties getDefaultConfigurationProperties() {
        return new Properties();
    }
    
    
    /**
     * Gets the configuration properties as configured by the
     * user and set at runtime.
     * @return
     *      The configured Properties
     */
    protected Properties getConfiguredProperties() {
        Properties properties = new Properties();
        if (configuration != null
            && configuration.getConfigurationProperties() != null
            && configuration.getConfigurationProperties().getProperty() != null) {
            for (ConfigurationProperty prop : configuration.getConfigurationProperties().getProperty()) {
                properties.setProperty(prop.getKey(), prop.getValue());
            }
        }
        return properties;
    }
    
    
    public abstract void auditQueryBegin(QueryBeginAuditingEvent event);
    
    
    public abstract void auditValidation(ValidationAuditingEvent event);
    
    
    public abstract void auditQueryProcessingFailed(QueryProcessingFailedAuditingEvent event);
    
    
    public abstract void auditQueryResults(QueryResultsAuditingEvent event);
}
