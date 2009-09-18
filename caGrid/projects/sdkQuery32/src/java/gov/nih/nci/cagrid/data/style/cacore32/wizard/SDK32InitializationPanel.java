package gov.nih.nci.cagrid.data.style.cacore32.wizard;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.data.DataServiceConstants;
import gov.nih.nci.cagrid.data.ExtensionDataUtils;
import gov.nih.nci.cagrid.data.extension.AdditionalLibraries;
import gov.nih.nci.cagrid.data.extension.Data;
import gov.nih.nci.cagrid.data.style.cacore32.encoding.SDK32DeserializerFactory;
import gov.nih.nci.cagrid.data.style.cacore32.encoding.SDK32SerializerFactory;
import gov.nih.nci.cagrid.data.style.sdkstyle.wizard.CoreDsIntroPanel;
import gov.nih.nci.cagrid.data.style.sdkstyle.wizard.SchemaTypesPanel;
import gov.nih.nci.cagrid.data.ui.wizard.CacoreWizardUtils;
import gov.nih.nci.cagrid.introduce.beans.extension.ServiceExtensionDescriptionType;
import gov.nih.nci.cagrid.introduce.common.CommonTools;
import gov.nih.nci.cagrid.introduce.common.FileFilters;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;
import gov.nih.nci.cagrid.introduce.extension.ExtensionsLoader;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.cagrid.grape.utils.CompositeErrorDialog;

/** 
 *  SDK32InitializationPanel
 *  Panel to initialize the SDK query processor v 3.2
 * 
 * @author David Ervin
 * 
 * @created Jul 12, 2007 3:37:04 PM
 * @version $Id: SDK32InitializationPanel.java,v 1.2 2009-01-06 17:44:51 dervin Exp $ 
 */
public class SDK32InitializationPanel extends CoreDsIntroPanel {
    
    // directory for SDK query processor libraries
    public static final String SDK_32_LIB_DIR = ExtensionsLoader.getInstance().getExtensionsDir().getAbsolutePath() 
        + File.separator + "data" + File.separator + "sdk32" + File.separator + "lib";
        
    // the query processor class name
    public static final String SDK_32_QUERY_PROCESSOR = "gov.nih.nci.cagrid.data.sdk32query.HQLCoreQueryProcessor";


    public SDK32InitializationPanel(ServiceExtensionDescriptionType extensionDescription, ServiceInformation info) {
        super(extensionDescription, info);
    }
    
    
    private File getSdk32QPLib() {
        File libDir = new File(SDK_32_LIB_DIR);
        File[] jars = libDir.listFiles(new FileFilters.JarFileFilter());
        if (jars.length != 1) {
            StringBuffer detail = new StringBuffer();
            detail.append("Expected to find a single jar file in the directory\n");
            detail.append(libDir.getAbsolutePath()).append("\n");
            detail.append("Found the following libs instead:\n");
            for (File f : jars) {
                detail.append("\t").append(f.getName()).append("\n");
            }
            CompositeErrorDialog.showErrorDialog("Error locating SDK Query 3.2 library", detail.toString());
        }
        return jars[0];
    }


    protected void setLibrariesAndProcessor() {
        String qpClassName = SDK_32_QUERY_PROCESSOR;
        // get the path to the SDK Query project
        File sdkQuery = getSdk32QPLib();
        if (sdkQuery == null || !sdkQuery.exists()) {
            String[] error = {"The SDK Query project does not exist or has not",
                "yet been built.  Please build this project first!"};
            CompositeErrorDialog.showErrorDialog("SDK Query Library Not Found", error);
        } else {
            // copy the library to the service's lib dir
            File sdkQueryOut = new File(CacoreWizardUtils.getServiceBaseDir(getServiceInformation()) + File.separator
                + "lib" + File.separator + sdkQuery.getName());
            try {
                Utils.copyFile(sdkQuery, sdkQueryOut);
            } catch (Exception ex) {
                ex.printStackTrace();
                CompositeErrorDialog.showErrorDialog("Error copying the required query processor library", ex);
                return;
            }
            // add the library to the service's additional libs list
            try {
                Data data = ExtensionDataUtils.getExtensionData(getExtensionData());
                AdditionalLibraries libs = data.getAdditionalLibraries();
                if (libs == null) {
                    libs = new AdditionalLibraries();
                    data.setAdditionalLibraries(libs);
                }
                Set jarNames = new HashSet();
                if (libs.getJarName() != null) {
                    Collections.addAll(jarNames, (Object[]) libs.getJarName());
                }
                // add the new library
                jarNames.add(sdkQuery.getName());
                String[] names = new String[jarNames.size()];
                jarNames.toArray(names);
                libs.setJarName(names);
                // store the modified list
                ExtensionDataUtils.storeExtensionData(getExtensionData(), data);
            } catch (Exception ex) {
                ex.printStackTrace();
                CompositeErrorDialog.showErrorDialog("Error adding the library to the service information", ex);
                return;
            }
            // add the query processor class name as a service property
            CommonTools.setServiceProperty(getServiceInformation().getServiceDescriptor(),
                DataServiceConstants.QUERY_PROCESSOR_CLASS_PROPERTY, qpClassName, false);
            // set the serializer and deserializer properties
            getBitBucket().put(SchemaTypesPanel.TYPE_SERIALIZER_CLASS_PROPERTY, 
                SDK32SerializerFactory.class.getName());
            getBitBucket().put(SchemaTypesPanel.TYPE_DESERIALIZER_CLASS_PROPERTY,
                SDK32DeserializerFactory.class.getName());
        }
    }
}
