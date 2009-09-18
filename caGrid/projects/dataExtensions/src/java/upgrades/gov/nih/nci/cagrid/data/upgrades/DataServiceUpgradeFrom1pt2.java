package gov.nih.nci.cagrid.data.upgrades;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.data.extension.Data;
import gov.nih.nci.cagrid.introduce.beans.extension.ExtensionType;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;
import gov.nih.nci.cagrid.introduce.extension.ExtensionsLoader;
import gov.nih.nci.cagrid.introduce.extension.utils.AxisJdomUtils;
import gov.nih.nci.cagrid.introduce.extension.utils.ExtensionUtilities;
import gov.nih.nci.cagrid.introduce.upgrade.common.StatusBase;
import gov.nih.nci.cagrid.introduce.upgrade.one.x.ExtensionUpgraderBase;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.axis.message.MessageElement;
import org.jdom.Element;
import org.jdom.JDOMException;

/**
 * DataServiceUpgradeFrom1pt2
 * Utility to upgrade a 1.2 data service to current
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A> *
 * @created Feb 19, 2007
 * @version $Id: DataServiceUpgradeFrom1pt2.java,v 1.1 2007/02/19 21:52:52
 *          dervin Exp $
 */
public class DataServiceUpgradeFrom1pt2 extends ExtensionUpgraderBase {

	public DataServiceUpgradeFrom1pt2(ExtensionType extensionType,
			ServiceInformation serviceInformation, String servicePath,
			String fromVersion, String toVersion) {
		super(DataServiceUpgradeFrom1pt2.class.getSimpleName(),
				extensionType, serviceInformation, servicePath, fromVersion,
				toVersion);
	}

	
	protected void upgrade() throws Exception {
		try {
			validateUpgrade();
			
			updateLibraries();
            
            upgradeWsdls();
            
            upgradeModelInformation();
			
			setCurrentExtensionVersion();
			
			getStatus().setStatus(StatusBase.UPGRADE_OK);
		} catch (UpgradeException ex) {
			getStatus().addDescriptionLine(ex.getMessage());
			getStatus().setStatus(StatusBase.UPGRADE_FAIL);
			throw ex;
		}
	}
	

	private void validateUpgrade() throws UpgradeException {
		if (!"1.2".equals(getFromVersion())) {
			throw new UpgradeException(getClass().getName()
				+ " upgrades FROM 1.2 TO " + UpgraderConstants.DATA_CURRENT_VERSION + 
                ", found FROM = " + getFromVersion());
		}
		if (!getToVersion().equals(UpgraderConstants.DATA_CURRENT_VERSION)) {
			throw new UpgradeException(getClass().getName()
				+ " upgrades FROM 1.2 TO " + UpgraderConstants.DATA_CURRENT_VERSION + 
                ", found TO = " + getToVersion());
		}
		String currentVersion = getExtensionType().getVersion();
		if (!"1.2".equals(currentVersion)) {
			throw new UpgradeException(getClass().getName()
				+ " upgrades FROM 1.2 TO " + UpgraderConstants.DATA_CURRENT_VERSION + 
                ", current version found is " + currentVersion);
		}
	}
	
	
	private Element getExtensionDataElement() throws UpgradeException {
        MessageElement[] anys = getExtensionType().getExtensionData().get_any();
        MessageElement rawDataElement = null;
        for (int i = 0; (anys != null) && (i < anys.length); i++) {
            if (anys[i].getQName().equals(Data.getTypeDesc().getXmlType())) {
                rawDataElement = anys[i];
                break;
            }
        }
        if (rawDataElement == null) {
            throw new UpgradeException("No extension data was found for the data service extension");
        }
        Element extensionDataElement = AxisJdomUtils.fromMessageElement(rawDataElement);
        return extensionDataElement;
    }
    
    
    private void storeExtensionDataElement(Element elem) throws UpgradeException {
        MessageElement[] anys = getExtensionType().getExtensionData().get_any();
        for (int i = 0; (anys != null) && (i < anys.length); i++) {
            if (anys[i].getQName().equals(Data.getTypeDesc().getXmlType())) {
                // remove the old extension data
                anys = (MessageElement[]) Utils.removeFromArray(anys, anys[i]);
                break;
            }
        }
        // create a message element from the JDom element
        MessageElement data = null;
        try {
            data = AxisJdomUtils.fromElement(elem);
        } catch (JDOMException ex) {
            throw new UpgradeException(
                "Error converting extension data to Axis message element: " + ex.getMessage(), ex);
        }
        anys = (MessageElement[]) Utils.appendToArray(anys, data);
        getExtensionType().getExtensionData().set_any(anys);
    }
	
	
	private void setCurrentExtensionVersion() {
        getExtensionType().setVersion(UpgraderConstants.DATA_CURRENT_VERSION);
    }
	
	
	private boolean serviceIsUsingEnumeration(Element extDataElement) {
        Element serviceFeaturesElement = extDataElement.getChild("ServiceFeatures", extDataElement.getNamespace());
        String useEnumValue = serviceFeaturesElement.getAttributeValue("useWsEnumeration");
        return Boolean.valueOf(useEnumValue).booleanValue();
    }


    private boolean serviceIsUsingSdkDataSource(Element extDataElement) {
        Element serviceFeaturesElement = extDataElement.getChild("ServiceFeatures", extDataElement.getNamespace());
        String useSdkValue = serviceFeaturesElement.getAttributeValue("useSdkDataSource");
        return Boolean.valueOf(useSdkValue).booleanValue();
    }
	
	
	private void updateLibraries() throws UpgradeException {
		Element extDataElement = getExtensionDataElement();
		
        updateDataLibraries();
        
        if (serviceIsUsingEnumeration(extDataElement)) {
        	getStatus().addDescriptionLine("-- Data Service WS-Enumeration Support Detected");
        }
        
        if (serviceIsUsingSdkDataSource(extDataElement)) {
        	getStatus().addDescriptionLine("-- Data Service caCORE SDK Support Detected");
            updateSdkQueryLibraries();
        }
    }


    private void updateDataLibraries() throws UpgradeException {
        FileFilter oldDataLibFilter = new FileFilter() {
            public boolean accept(File pathname) {
                String name = pathname.getName();
                return (name.endsWith(".jar") && (name.startsWith("caGrid-data-")
                    || name.startsWith("caGrid-core-") || name.startsWith("caGrid-caDSR-") 
                    || name.startsWith("caGrid-metadata-")));
            }
        };
        FileFilter newDataLibFilter = new FileFilter() {
            public boolean accept(File pathname) {
                String name = pathname.getName();
                return (name.endsWith(".jar") && 
                    (name.startsWith("caGrid-data-")
                    || name.startsWith("caGrid-core-") 
                    || name.startsWith("caGrid-caDSR-") 
                    || name.startsWith("caGrid-metadata-")));
            }
        };
        // locate the old data service libs in the service
        File serviceLibDir = new File(getServicePath() + File.separator + "lib");
        File[] serviceDataLibs = serviceLibDir.listFiles(oldDataLibFilter);
        // delete the old libraries
        for (File oldLib : serviceDataLibs) {
            oldLib.delete();
            getStatus().addDescriptionLine("caGrid 1.2 library " + oldLib.getName() + " removed");
        }
        // copy new libraries in
        File extLibDir = new File(ExtensionsLoader.EXTENSIONS_DIRECTORY + File.separator + "lib");
        File[] dataLibs = extLibDir.listFiles(newDataLibFilter);
        List<File> outLibs = new ArrayList<File>(dataLibs.length);
        for (File newLib : dataLibs) {
            File out = new File(serviceLibDir.getAbsolutePath() 
                + File.separator + newLib.getName());
            try {
                Utils.copyFile(newLib, out);
                getStatus().addDescriptionLine("caGrid " + UpgraderConstants.DATA_CURRENT_VERSION + " library " + newLib.getName() + " added");
            } catch (IOException ex) {
                throw new UpgradeException("Error copying new data service library: " 
                    + ex.getMessage(), ex);
            }
            outLibs.add(out);
        }
        
        // update the Eclipse .classpath file
        File classpathFile = new File(getServicePath() + File.separator + ".classpath");
        File[] outLibArray = new File[dataLibs.length];
        outLibs.toArray(outLibArray);
        try {
            ExtensionUtilities.syncEclipseClasspath(classpathFile, outLibArray);
            getStatus().addDescriptionLine("Eclipse .classpath file updated");
        } catch (Exception ex) {
            throw new UpgradeException("Error updating Eclipse .classpath file: " 
                + ex.getMessage(), ex);
        }
    }
    

    private void updateSdkQueryLibraries() throws UpgradeException {
        FileFilter sdkLibFilter = new FileFilter() {
            public boolean accept(File name) {
                String filename = name.getName();
                return (filename.startsWith("caGrid-sdkQuery") 
                    || filename.startsWith("caGrid-sdkQuery32"))
                    && filename.endsWith(".jar");
            }
        };
        FileFilter newSdkLibFilter = new FileFilter() {
            public boolean accept(File name) {
                String filename = name.getName();
                return (filename.startsWith("caGrid-sdkQuery") 
                    || filename.startsWith("caGrid-sdkQuery32"))
                    && filename.endsWith(".jar");
            }
        };
        // locate old libraries in the service
        File serviceLibDir = new File(getServicePath() + File.separator + "lib");
        boolean isSdk31 = false;
        boolean isSdk32 = false;
        File[] oldLibs = serviceLibDir.listFiles(sdkLibFilter);
        // first must see which version of SDK we're using
        for (int i = 0; i < oldLibs.length; i++) {
            if (oldLibs[i].getName().indexOf("caGrid-sdkQuery32") != -1) {
                isSdk32 = true;
            } else {
                if (oldLibs[i].getName().indexOf("caGrid-sdkQuery") != -1) {
                    isSdk31 = true;
                }
            }
        }
        if ((!isSdk31 && !isSdk32) || (isSdk31 && isSdk32)) {
            throw new UpgradeException("Unable to determine SDK version to upgrade");
        }
        // tell user what we think the sdk version was
        getStatus().addDescriptionLine("caCORE SDK version determined to be " 
            + (isSdk31 ? "3.1" : "3.2 / 3.2.1"));
        // delete old libs
        for (File oldLib : oldLibs) {
            oldLib.delete();
            getStatus().addDescriptionLine("caGrid 1.2 library " + oldLib.getName() + " removed");
        }
        // locate new libraries
        File[] newLibs = null;
        if (isSdk31) {
            File sdk31LibDir = new File(ExtensionsLoader.EXTENSIONS_DIRECTORY + File.separator 
                + "data" + File.separator + "sdk31" + File.separator + "lib");
            newLibs = sdk31LibDir.listFiles(newSdkLibFilter);
        } else if (isSdk32) {
            File sdk32LibDir = new File(ExtensionsLoader.EXTENSIONS_DIRECTORY + File.separator 
                + "data" + File.separator + "sdk32" + File.separator + "lib");
            newLibs = sdk32LibDir.listFiles(newSdkLibFilter);
        }
        // copy the libraries in
        File[] outLibs = new File[newLibs.length];
        for (int i = 0; i < newLibs.length; i++) {
            File output = new File(getServicePath() + File.separator + "lib" 
                + File.separator + newLibs[i].getName());
            try {
                Utils.copyFile(newLibs[i], output);
                getStatus().addDescriptionLine("caGrid " + UpgraderConstants.SDK_3_CURRENT_VERSION + " library " + newLibs[i].getName() + " added");
            } catch (IOException ex) {
                throw new UpgradeException("Error copying SDK Query Processor library: " 
                    + ex.getMessage(), ex);
            }
            outLibs[i] = output;
        }
        // sync up the Eclipse .classpath file
        File classpathFile = new File(getServicePath() + File.separator + ".classpath");
        try {
            ExtensionUtilities.syncEclipseClasspath(classpathFile, outLibs);
            getStatus().addDescriptionLine("Eclipse .classpath file updated");
        } catch (Exception ex) {
            throw new UpgradeException("Error updating Eclipse .classpath file: " 
                + ex.getMessage(), ex);
        }
        getStatus().addDescriptionLine("-- caCORE SDK Support upgraded");
    }
    
    
    private void upgradeWsdls() throws UpgradeException {
        // get the service's schema dir where the wsdl files live
        String serviceName = getServiceInformation().getServiceDescriptor()
            .getServices().getService(0).getName();
        File serviceSchemasDir = new File(getServicePath(), "schema" + File.separator + serviceName);
        
        // get the data service schemas dir
        File dataSchemasDir = new File(".." + File.separator + "data" + File.separator + "schema" + File.separator + "Data");
        
        // find data wsdls
        Map<String, File> dataWsdlsByName = new HashMap<String, File>();
        for (File file : dataSchemasDir.listFiles()) {
            String name = file.getName();
            if (name.endsWith(".wsdl")) {
                dataWsdlsByName.put(name, file);
            }
        }
        
        // list wsdls in service schemas dir.  If any match one from data, replace it with the new version
        File[] serviceWsdls = serviceSchemasDir.listFiles(new FileFilter() {
            public boolean accept(File path) {
                return path.getName().endsWith(".wsdl");
            }
        });
        
        for (File serviceWsdl : serviceWsdls) {
            if (dataWsdlsByName.containsKey(serviceWsdl.getName())) {
                // overwrite the service wsdl with the new one
                try {
                    Utils.copyFile(dataWsdlsByName.get(serviceWsdl.getName()), serviceWsdl);
                    getStatus().addDescriptionLine("Replaced WSDL " + serviceWsdl.getName() 
                        + " with new version " + UpgraderConstants.DATA_CURRENT_VERSION);
                } catch (Exception ex) {
                    throw new UpgradeException("Error replacing wsdl: " + ex.getMessage(), ex);
                }
            }
        }
    }
    
    
    private void upgradeModelInformation() throws UpgradeException {
        Element extensionDataElement = getExtensionDataElement();
        Element cadsrInfoElement = extensionDataElement.getChild(
            "CadsrInformation", extensionDataElement.getNamespace());
        Element modelInformationElement = ModelInformationConverter.convertModelInformation(
            getServiceInformation(), getStatus(), cadsrInfoElement);
        extensionDataElement.removeContent(cadsrInfoElement);
        extensionDataElement.addContent(modelInformationElement.detach());
        storeExtensionDataElement(extensionDataElement);
    }
}
