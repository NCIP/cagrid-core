package org.cagrid.data.sdkquery41.style.wizard;

import gov.nih.nci.cagrid.data.style.sdkstyle.wizard.CoreDsIntroPanel;
import gov.nih.nci.cagrid.introduce.beans.extension.ServiceExtensionDescriptionType;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;

import java.io.File;

import org.cagrid.data.sdkquery41.style.common.SDK41StyleConstants;
import org.cagrid.data.sdkquery41.style.wizard.config.SDK41InitialConfigurationStep;
import org.cagrid.grape.utils.CompositeErrorDialog;


/**
 * SDK41InitializationPanel 
 * First wizard panel to create a caGrid data service
 * backed by caCORE SDK 4.1
 * 
 * @author David
 */
public class SDK41InitializationPanel extends CoreDsIntroPanel {

    private SDK41InitialConfigurationStep configuration = null;


    public SDK41InitializationPanel(ServiceExtensionDescriptionType extensionDescription, ServiceInformation info) {
        super(extensionDescription, info);
        configuration = new SDK41InitialConfigurationStep(info);
    }


    protected void setLibrariesAndProcessor() {
        // set the query processors and style lib dir on the configuration
        // this.configuration.setCql1ProcessorClassName(SDK41QueryProcessor.class.getName());
        // this.configuration.setCql2ProcessorClassName(SDK41CQL2QueryProcessor.class.getName());
        File styleLibDir = new File(SDK41StyleConstants.STYLE_DIR, "lib");
        this.configuration.setStyleLibDirectory(styleLibDir);
    }


    public void movingNext() {
        try {
            configuration.applyConfiguration();
        } catch (Exception ex) {
            ex.printStackTrace();
            CompositeErrorDialog.showErrorDialog("Error applying configuration", ex.getMessage(), ex);
        }
    }
}
