package org.cagrid.data.sdkquery41.style.wizard.config;

import gov.nih.nci.cagrid.data.QueryProcessorConstants;
import gov.nih.nci.cagrid.data.extension.AdditionalLibraries;
import gov.nih.nci.cagrid.data.extension.Data;
import gov.nih.nci.cagrid.introduce.common.CommonTools;
import gov.nih.nci.cagrid.introduce.common.FileFilters;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.cagrid.data.sdkquery41.processor.SDK41QueryProcessor;
import org.cagrid.data.sdkquery41.processor2.SDK41CQL2QueryProcessor;
import org.cagrid.grape.utils.CompositeErrorDialog;

/** 
 *  SDK41InitialConfigurationStep
 *  Step begins configuration process for the caCORE SDK 4.1
 *  Data Service style
 * 
 * @author David Ervin
 * 
 * @created Jan 28, 2008 12:35:30 PM
 * @version $Id: SDK41InitialConfigurationStep.java,v 1.1 2008-11-26 20:21:51 dervin Exp $ 
 */
public class SDK41InitialConfigurationStep extends AbstractStyleConfigurationStep {
    
    // private String cql1ProcessorClassName = null;
    // private String cql2ProcessorClassName = null;
    private File styleLibDirectory = null;

    public SDK41InitialConfigurationStep(ServiceInformation serviceInfo) {
        super(serviceInfo);
    }
    
    
    /*
    public void setCql1ProcessorClassName(String className) {
        this.cql1ProcessorClassName = className;
    }
    
    
    public void setCql2ProcessorClassName(String className) {
        this.cql2ProcessorClassName = className;
    }
    */
    
    
    public void setStyleLibDirectory(File dir) {
        this.styleLibDirectory = dir;
    }


    public void applyConfiguration() throws Exception {
        // shouldn't have to explicitly copy any libraries into the service to support
        // the SDK 4.1 query processor, since style libraries get copied into the
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
            QueryProcessorConstants.QUERY_PROCESSOR_CLASS_PROPERTY, SDK41QueryProcessor.class.getName(), false);
        CommonTools.setServiceProperty(getServiceInformation().getServiceDescriptor(),
            QueryProcessorConstants.CQL2_QUERY_PROCESSOR_CLASS_PROPERTY, SDK41CQL2QueryProcessor.class.getName(), false);
    }
}
