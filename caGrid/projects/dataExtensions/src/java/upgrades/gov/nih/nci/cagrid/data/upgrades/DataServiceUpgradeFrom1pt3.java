package gov.nih.nci.cagrid.data.upgrades;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.data.BdtMethodConstants;
import gov.nih.nci.cagrid.data.extension.Data;
import gov.nih.nci.cagrid.data.style.ServiceStyleContainer;
import gov.nih.nci.cagrid.data.style.ServiceStyleLoader;
import gov.nih.nci.cagrid.data.style.StyleVersionUpgrader;
import gov.nih.nci.cagrid.data.style.VersionUpgrade;
import gov.nih.nci.cagrid.introduce.beans.extension.ExtensionType;
import gov.nih.nci.cagrid.introduce.beans.method.MethodType;
import gov.nih.nci.cagrid.introduce.beans.service.ServiceType;
import gov.nih.nci.cagrid.introduce.common.CommonTools;
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
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.axis.message.MessageElement;
import org.apache.commons.logging.LogFactory;
import org.jdom.Element;
import org.jdom.JDOMException;

/**
 * DataServiceUpgradeFrom1pt3
 * Utility to upgrade a 1.3 data service to current
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A> *
 * @created Feb 19, 2007
 * @version $Id: DataServiceUpgradeFrom1pt3.java,v 1.1 2007/02/19 21:52:52
 *          dervin Exp $
 */
public class DataServiceUpgradeFrom1pt3 extends ExtensionUpgraderBase {

	public DataServiceUpgradeFrom1pt3(ExtensionType extensionType,
			ServiceInformation serviceInformation, String servicePath,
			String fromVersion, String toVersion) {
		super(DataServiceUpgradeFrom1pt3.class.getSimpleName(),
				extensionType, serviceInformation, servicePath, fromVersion,
				toVersion);
	}

	
	protected void upgrade() throws Exception {
	    LogFactory.getLog(DataServiceUpgradeFrom1pt3.class).debug("UPGRADING DATA FROM 1.3");
		try {
			validateUpgrade();
			
			updateDataLibraries();
			
			upgradeStyle();
			
			upgradeWsdls();
			
			removeBdt();
			
			Cql2FeaturesInstaller cql2Installer = 
			    new Cql2FeaturesInstaller(
			        getServiceInformation(), getExtensionType(), getStatus());
			cql2Installer.installCql2Features();
			
			upgradeExtensionData();
            
			setCurrentExtensionVersion();
			
			getStatus().setStatus(StatusBase.UPGRADE_OK);
		} catch (UpgradeException ex) {
			getStatus().addDescriptionLine(ex.getMessage());
			getStatus().setStatus(StatusBase.UPGRADE_FAIL);
			throw ex;
		}
	}
	

	private void validateUpgrade() throws UpgradeException {
		if (!"1.3".equals(getFromVersion())) {
			throw new UpgradeException(getClass().getName()
				+ " upgrades FROM 1.3 TO " + UpgraderConstants.DATA_CURRENT_VERSION + 
                ", found FROM = " + getFromVersion());
		}
		if (!getToVersion().equals(UpgraderConstants.DATA_CURRENT_VERSION)) {
			throw new UpgradeException(getClass().getName()
				+ " upgrades FROM 1.3 TO " + UpgraderConstants.DATA_CURRENT_VERSION + 
                ", found TO = " + getToVersion());
		}
		String currentVersion = getExtensionType().getVersion();
		if (!"1.3".equals(currentVersion)) {
			throw new UpgradeException(getClass().getName()
				+ " upgrades FROM 1.3 TO " + UpgraderConstants.DATA_CURRENT_VERSION + 
                ", current version found is " + currentVersion);
		}
	}
	
	
	private File getDataSchemaDir() {
	    return new File(ExtensionsLoader.getInstance().getExtensionsDir(),
	        "data" + File.separator + "schema" + File.separator + "Data");
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
	
	
	private boolean serviceIsUsingBdt(Element extElement) {
	    Element serviceFeaturesElement = extElement.getChild("ServiceFeatures", extElement.getNamespace());
	    String useBdtValue = serviceFeaturesElement.getAttributeValue("useBdt");
	    return Boolean.valueOf(useBdtValue).booleanValue();
	}
	
	
	private String getStyleName(Element extDataElement) {
	    Element serviceFeaturesElement = extDataElement.getChild("ServiceFeatures", extDataElement.getNamespace());
	    String styleName = serviceFeaturesElement.getAttributeValue("serviceStyle");
	    return styleName;
	}
	
	
	private void upgradeExtensionData() throws UpgradeException {
	    Element extensionDataElement = getExtensionDataElement();
	    Element serviceFeaturesElement = extensionDataElement.getChild(
	        "ServiceFeatures", extensionDataElement.getNamespace());
	    serviceFeaturesElement.setAttribute("useTransfer", "false");
	    getStatus().addDescriptionLine("Data Service Extension Data Service Feature \"useTransfer\" added and set to \"false\"");
	    serviceFeaturesElement.removeAttribute("useBdt");
	    getStatus().addDescriptionLine("Data Service Extension Data Service Feature \"useBdt\" removed");
	    String styleName = getStyleName(extensionDataElement);
	    if (styleName != null) {
	        serviceFeaturesElement.removeAttribute("serviceStyle");
	        Element serviceStyleElement = new Element("ServiceStyle", serviceFeaturesElement.getNamespace());
	        serviceStyleElement.setAttribute("name", styleName);
	        serviceStyleElement.setAttribute("version", "1.3"); // set to 1.3 pending upgrade
	        getStatus().addDescriptionLine("Created Service Style element in extension data; set style version to \"1.3\"");
	    }
	    storeExtensionDataElement(extensionDataElement);
	}


    private void updateDataLibraries() throws UpgradeException {
        FileFilter oldDataLibFilter = new FileFilter() {
            public boolean accept(File pathname) {
                String name = pathname.getName();
                return name.endsWith(".jar") && 
                    (name.startsWith("caGrid-data-")
                    || name.startsWith("caGrid-core-")
                    || name.startsWith("caGrid-CQL-")
                    || name.startsWith("caGrid-caDSR-") 
                    || name.startsWith("caGrid-metadata-"));
            }
        };
        FileFilter newDataLibFilter = new FileFilter() {
            public boolean accept(File pathname) {
                String name = pathname.getName();
                return name.endsWith(".jar") && 
                    (name.startsWith("caGrid-data-")
                    || name.startsWith("caGrid-core-")
                    || name.startsWith("caGrid-CQL-")
                    || name.startsWith("caGrid-caDSR-") 
                    || name.startsWith("caGrid-metadata-")
                    || name.startsWith("caGrid-mms-")
                    || name.startsWith("castor-1.0.2"));
            }
        };
        // locate the old data service libs in the service
        File serviceLibDir = new File(getServicePath() + File.separator + "lib");
        File[] serviceDataLibs = serviceLibDir.listFiles(oldDataLibFilter);
        // delete the old libraries
        for (File oldLib : serviceDataLibs) {
            oldLib.delete();
            getStatus().addDescriptionLine("caGrid 1.3 library " + oldLib.getName() + " removed");
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
    
    
    private void upgradeWsdls() throws UpgradeException {
        // get the service's schema dir where the wsdl files live
        String serviceName = getServiceInformation().getServiceDescriptor()
            .getServices().getService(0).getName();
        File serviceSchemasDir = new File(getServicePath(), "schema" + File.separator + serviceName);
        
        // get the data service schemas dir
        File dataSchemasDir = getDataSchemaDir();
        
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
    
    
    private void removeBdt() throws UpgradeException {
        Element extElement = getExtensionDataElement();
        if (serviceIsUsingBdt(extElement)) {
            getStatus().addDescriptionLine("BDT has been removed from caGrid 1.4.  " +
                "The BDT features of this data service will be removed.");
            // use the service name to find the schema dir
            ServiceType mainService = getServiceInformation().getServices().getService(0);
            String serviceName = mainService.getName();
            File schemaDir = new File(getServiceInformation().getBaseDirectory(), "schema" + File.separator + serviceName);
            FileFilter bdtDataSchemaFilter = new FileFilter() {
                public boolean accept(File pathname) {
                    return (pathname.isFile() && pathname.getName().startsWith("BDTDataService"));
                }
            };
            // remove BDTDataService* schemas / wsdls
            File[] deleteSchemas = schemaDir.listFiles(bdtDataSchemaFilter);
            for (File delme : deleteSchemas) {
                delme.delete();
                getStatus().addDescriptionLine("Deleted BDT Data Service schema " + delme.getName());
            }
            // find and remove the bdt query operation
            MethodType bdtQueryMethod = CommonTools.getMethod(
                mainService.getMethods(), BdtMethodConstants.BDT_QUERY_METHOD_NAME);
            if (bdtQueryMethod != null) {
                CommonTools.removeMethod(mainService.getMethods(), bdtQueryMethod);
                getStatus().addDescriptionLine("Removed " + BdtMethodConstants.BDT_QUERY_METHOD_NAME + " operation from the service");
            } else {
                getStatus().addDescriptionLine(BdtMethodConstants.BDT_QUERY_METHOD_NAME + " not found; may have been removed earlier");
            }
        } else {
            getStatus().addDescriptionLine("Service was not using BDT");
        }
    }
    
    
    private void upgradeStyle() throws UpgradeException {
        Element extensionDataElement = getExtensionDataElement();
        String styleName = getStyleName(extensionDataElement);
        if (styleName == null) {
            getStatus().addDescriptionLine("No data service style detected");
        } else {
            getStatus().addDescriptionLine("Data service style " + styleName + " detected, looking for upgrader");
            ServiceStyleContainer styleContainer = null;
            try {
                styleContainer = ServiceStyleLoader.getStyle(styleName);
            } catch (Exception ex) {
                throw new UpgradeException("Error loading service style: " + ex.getMessage(), ex);
            }
            if (styleContainer == null) {
                String message = "No current version of style " + styleName + " could be loaded!";
                getStatus().addDescriptionLine("No current version of style " + styleName + " could be loaded!");
                getStatus().addIssue(message, 
                    "The style may not support the current version of caGrid.  " +
                    "Check with the developer of your style for an update");
            } else {
                VersionUpgrade[] availableUpgrades = styleContainer.getServiceStyle().getVersionUpgrade();
                // sort upgrades
                Comparator<VersionUpgrade> upgradeSorter = new Comparator<VersionUpgrade>() {
                    public int compare(VersionUpgrade a, VersionUpgrade b) {
                        // sort by from version first, then by to version
                        int val = a.getFromVersion().compareTo(b.getFromVersion());
                        if (val == 0) {
                            val = a.getToVersion().compareTo(b.getToVersion());
                        }
                        return val;
                    }
                };
                Arrays.sort(availableUpgrades, upgradeSorter);
                VersionUpgrade validUpgrade = null;
                // doing this will get the upgrade from whatever the 2nd most recent
                // version of the style is to the 1.4 version
                for (VersionUpgrade upgrade : availableUpgrades) {
                    if (upgrade.getToVersion().equals(UpgraderConstants.DATA_CURRENT_VERSION)) {
                        validUpgrade = upgrade;
                    }
                }
                if (validUpgrade == null) {
                    getStatus().addIssue("No upgrade was found for the style " + styleName, 
                        "The style may not support the current version of caGrid.  " +
                        "Check with the developer of your style for an update");
                } else {
                    StyleVersionUpgrader styleUpgrade = null;
                    try {
                        styleUpgrade = styleContainer.loadVersionUpgrader(
                            validUpgrade.getFromVersion(), validUpgrade.getToVersion());
                    } catch (Exception ex) {
                        throw new UpgradeException(
                            "Error loading style version upgrader: " + ex.getMessage(), ex);
                    }
                    try {
                        styleUpgrade.upgradeStyle(getServiceInformation(), getExtensionType().getExtensionData(),
                            getFromVersion(), getToVersion());
                    } catch (Exception ex) {
                        throw new UpgradeException("Error upgrading service style: " + ex.getMessage(), ex);
                    }
                }
            }
        }
    }
}
