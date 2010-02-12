package gov.nih.nci.cagrid.sdkquery4.style.wizard.config;

import gov.nih.nci.cagrid.data.QueryProcessorConstants;
import gov.nih.nci.cagrid.data.extension.AdditionalLibraries;
import gov.nih.nci.cagrid.data.extension.Data;
import gov.nih.nci.cagrid.introduce.common.CommonTools;
import gov.nih.nci.cagrid.introduce.common.FileFilters;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;
import gov.nih.nci.cagrid.sdkquery4.processor.SDK4QueryProcessor;
import gov.nih.nci.cagrid.sdkquery4.processor2.SDK4CQL2QueryProcessor;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.cagrid.grape.utils.CompositeErrorDialog;

/** 
 *  SDK4InitialConfigurationStep
 *  Step begins configuration process for caCORE SDK 4
 *  Data Service style
 * 
 * @author David Ervin
 * 
 * @created Jan 28, 2008 12:35:30 PM
 * @version $Id: SDK4InitialConfigurationStep.java,v 1.1 2008-01-28 21:08:58 dervin Exp $ 
 */
public class SDK4InitialConfigurationStep extends AbstractStyleConfigurationStep {
    
    private File styleLibDirectory = null;

    public SDK4InitialConfigurationStep(ServiceInformation serviceInfo) {
        super(serviceInfo);
    }
    
    
    public void setStyleLibDirectory(File dir) {
        this.styleLibDirectory = dir;
    }


    public void applyConfiguration() throws Exception {
        // shouldn't have to explicitly copy any libraries into the service to support
        // the SDK 4 query processor, since style libraries get copied into the
        // service's lib directory at creation time.
        
        try {
            Data data = getExtensionData();
            AdditionalLibraries libs = data.getAdditionalLibraries();
            if (libs == null) {
                libs = new AdditionalLibraries();
                data.setAdditionalLibraries(libs);
            }
            Set<String> jarNames = new HashSet<String>();
            if (libs.getJarName() != null) {
                Collections.addAll(jarNames, libs.getJarName());
            }
            // list the libraries
            File[] jars = styleLibDirectory.listFiles(new FileFilters.JarFileFilter());
            for (File jar : jars) {
                jarNames.add(jar.getName());
            }
            String[] names = new String[jarNames.size()];
            jarNames.toArray(names);
            libs.setJarName(names);
            // store the modified list
            storeExtensionData(data);
        } catch (Exception ex) {
            ex.printStackTrace();
            CompositeErrorDialog.showErrorDialog("Error adding the library to the service information", ex);
            return;
        }
        // add the query processor class name as a service property
        CommonTools.setServiceProperty(getServiceInformation().getServiceDescriptor(),
            QueryProcessorConstants.QUERY_PROCESSOR_CLASS_PROPERTY, 
            SDK4QueryProcessor.class.getName(), false);
        // add the CQL 2 query processor class name as a service property
        CommonTools.setServiceProperty(getServiceInformation().getServiceDescriptor(),
            QueryProcessorConstants.CQL2_QUERY_PROCESSOR_CLASS_PROPERTY, 
            SDK4CQL2QueryProcessor.class.getName(), false);
    }
}
