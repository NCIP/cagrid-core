package gov.nih.nci.cagrid.sdkquery4.style.wizard;

import gov.nih.nci.cagrid.data.style.sdkstyle.wizard.CoreDsIntroPanel;
import gov.nih.nci.cagrid.introduce.beans.extension.ServiceExtensionDescriptionType;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;
import gov.nih.nci.cagrid.introduce.extension.ExtensionsLoader;
import gov.nih.nci.cagrid.sdkquery4.processor.SDK4QueryProcessor;
import gov.nih.nci.cagrid.sdkquery4.style.wizard.config.SDK4InitialConfigurationStep;

import java.io.File;

import org.cagrid.grape.utils.CompositeErrorDialog;

/** 
 *  SDK4InitializationPanel
 *  Panel to initialize the SDK 4 wizard
 * 
 * @author David Ervin
 * 
 * @created Oct 5, 2007 2:08:34 PM
 * @version $Id: SDK4InitializationPanel.java,v 1.6 2008-01-28 21:08:58 dervin Exp $ 
 */
public class SDK4InitializationPanel extends CoreDsIntroPanel {
    
    public static final String SDK4_QUERY_PROCESSOR_CLASSNAME = SDK4QueryProcessor.class.getName();
    
    private SDK4InitialConfigurationStep configuration;

    public SDK4InitializationPanel(ServiceExtensionDescriptionType extensionDescription, ServiceInformation info) {
        super(extensionDescription, info);
        this.configuration = new SDK4InitialConfigurationStep(info);
    }


    protected void setLibrariesAndProcessor() {
        // set the query processor and style lib dir on the configuration
        this.configuration.setQueryProcessorClassName(SDK4_QUERY_PROCESSOR_CLASSNAME);
        File styleLibDir = new File(ExtensionsLoader.getInstance().getExtensionsDir().getAbsolutePath()
            + File.separator + "data" + File.separator + "styles" + File.separator + "cacore4" + File.separator + "lib");
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
