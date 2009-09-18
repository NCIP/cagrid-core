package org.cagrid.data.sdkquery41.style.wizard;

import javax.swing.JPanel;

import org.cagrid.data.sdkquery41.style.wizard.config.DomainModelConfigurationStep;
import org.cagrid.data.sdkquery41.style.wizard.config.DomainModelConfigurationStep.DomainModelConfigurationSource;

/**
 * JPanel which is required to produce a caGrid Domain Model
 * 
 * @author David
 */
public abstract class DomainModelSourcePanel extends JPanel {
    
    private DomainModelSourceValidityListener validityListener = null;
    private DomainModelConfigurationStep configuration = null;
    
    public DomainModelSourcePanel(
        DomainModelSourceValidityListener validityListener, 
        DomainModelConfigurationStep configuration) {
        super();
        this.validityListener = validityListener;
        this.configuration = configuration;
    }
    
    
    public abstract DomainModelConfigurationSource getSourceType();
    
    
    public abstract String getName();
    
    
    public abstract void populateFromConfiguration();
    
    
    public abstract void revalidateModel();
    
    
    protected DomainModelConfigurationStep getConfiguration() {
        return this.configuration;
    }
    
    
    protected void setModelValidity(boolean valid) {
        validityListener.domainModelSourceValid(this, valid);
    }
}
