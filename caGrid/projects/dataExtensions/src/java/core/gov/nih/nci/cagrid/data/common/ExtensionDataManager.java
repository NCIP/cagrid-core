package gov.nih.nci.cagrid.data.common;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.data.ExtensionDataUtils;
import gov.nih.nci.cagrid.data.auditing.DataServiceAuditors;
import gov.nih.nci.cagrid.data.extension.AdditionalLibraries;
import gov.nih.nci.cagrid.data.extension.Data;
import gov.nih.nci.cagrid.data.extension.ModelClass;
import gov.nih.nci.cagrid.data.extension.ModelInformation;
import gov.nih.nci.cagrid.data.extension.ModelPackage;
import gov.nih.nci.cagrid.data.extension.ModelSourceType;
import gov.nih.nci.cagrid.data.extension.ServiceStyle;
import gov.nih.nci.cagrid.introduce.beans.extension.ExtensionTypeExtensionData;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.cagrid.mms.domain.UMLProjectIdentifer;

/** 
 *  ExtensionDataManager
 *  Manages storage / retrieval of information in the data service extension data
 * 
 * @author David Ervin
 * 
 * @created Apr 11, 2007 10:04:04 AM
 * @version $Id: ExtensionDataManager.java,v 1.8 2009-01-14 15:28:44 dervin Exp $ 
 */
public class ExtensionDataManager {
    
    private ExtensionTypeExtensionData extensionData;
    
    public ExtensionDataManager(ExtensionTypeExtensionData data) {
        this.extensionData = data;
    }
    
    
    /**
     * Gets the jar names of the additional libraries added to the service
     * for supporting the query processor class
     * 
     * @return
     *      The names of the additional jars, or <code>null</code> if none are present
     * @throws Exception
     */
    public String[] getAdditionalJarNames() throws Exception {
        AdditionalLibraries libs = getAdditionalLibraries();
        return libs.getJarName();
    }
    
    
    /**
     * Adds an additional jar
     * 
     * @param jarName
     *      The name of the jar to add
     * @throws Exception
     */
    public void addAdditionalJar(String jarName) throws Exception {
        AdditionalLibraries libs = getAdditionalLibraries();
        String[] jarNames = null;
        if (libs.getJarName() == null) {
            jarNames = new String[] {jarName};
        } else {
            boolean shouldAdd = true;
            for (String name : libs.getJarName()) {
                if (name.equals(jarName)) {
                    shouldAdd = false;
                    break;
                }
            }
            if (shouldAdd) {
                jarNames = (String[]) Utils.appendToArray(libs.getJarName(), jarName);
            }
        }
        libs.setJarName(jarNames);
        storeAdditionalLibraries(libs);
    }
    
    
    /**
     * Sets the additional jars
     * 
     * @param jarNames
     *      All the jar names to be set as additional jars
     * @throws Exception
     */
    public void setAdditionalJars(String[] jarNames) throws Exception {
        AdditionalLibraries libs = getAdditionalLibraries();
        libs.setJarName(jarNames);
        storeAdditionalLibraries(libs);
    }
    
    
    /**
     * Stores a domain model's source
     * 
     * 
     * @throws Exception
     */
    public void storeDomainModelSource(ModelSourceType source) throws Exception {
        ModelInformation info = getModelInformation();
        info.setSource(source);
        storeModelInformation(info);
    }
    
    
    /**
     * Stores the project short name and version
     * 
     * @throws Exception
     */
    public void storeModelProjectInformation(String shortName, String version) throws Exception {
        ModelInformation info = getModelInformation();
        UMLProjectIdentifer id = new UMLProjectIdentifer();
        id.setIdentifier(shortName);
        id.setVersion(version);
        info.setUMLProjectIdentifer(id);
        storeModelInformation(info);
    }
    
    
    /**
     * Replaces the model package information in the extension data
     * 
     * @param packages
     * @throws Exception
     */
    public void storeModelPackages(ModelPackage[] packages) throws Exception {
        ModelInformation info = getModelInformation();
        info.setModelPackage(packages);
        storeModelInformation(info);
    }
    
    
    /**
     * Stores a Model Package.  If an existing package has the same name,
     * the existing package will be replaced
     * 
     * @param pack
     * @throws Exception
     */
    public void storeModelPackage(ModelPackage pack) throws Exception {
        ModelInformation info = getModelInformation();
        ModelPackage[] currentPackages = info.getModelPackage();
        if (currentPackages == null) {
            currentPackages = new ModelPackage[] {pack};
        } else {
            boolean replaced = false;
            for (int i = 0; i < currentPackages.length; i++) {
                if (currentPackages[i].getPackageName().equals(pack.getPackageName())) {
                    currentPackages[i] = pack;
                    replaced = true;
                    break;
                }
            }
            if (!replaced) {
                currentPackages = (ModelPackage[]) Utils.appendToArray(currentPackages, pack);
            }
        }
        info.setModelPackage(currentPackages);
        storeModelInformation(info);
    }
    
    
    /**
     * Removes a package from the model
     * 
     * @param packageName
     *      The name of the package to remove
     * @return
     *      True if the package was found and removed     
     * @throws Exception
     */
    public boolean removeCadsrPackage(String packageName) throws Exception {
        ModelInformation info = new ModelInformation();
        ModelPackage[] packs = info.getModelPackage();
        boolean found = false;
        for (ModelPackage currentPackage : packs) {
            if (currentPackage.getPackageName().equals(packageName)) {
                packs = (ModelPackage[]) Utils.removeFromArray(packs, currentPackage);
                found = true;
                break;
            }
        }
        info.setModelPackage(packs);
        storeModelInformation(info);
        return found;
    }
    
    
    /**
     * Sets that a class is targetable in the exposed domain model
     * 
     * @param packageName
     *      The package name
     * @param shortClassName
     *      The short class name
     * @param targetable
     *      The targetability state
     * @return
     *      True if the class was found and updated in the model, false otherwise
     * @throws Exception
     */
    public boolean setClassTargetableInModel(String packageName, String shortClassName, boolean targetable) throws Exception {
        ModelInformation info = getModelInformation();
        if (info.getModelPackage() != null) {
            for (ModelPackage pack : info.getModelPackage()) {
                if (pack.getPackageName().equals(packageName) && pack.getModelClass() != null) {
                    for (ModelClass clazz : pack.getModelClass()) {
                        if (clazz.getShortClassName().equals(shortClassName)) {
                            clazz.setTargetable(targetable);
                            storeModelInformation(info);
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
    
    
    public boolean getClassTargetableInModel(String packageName, String className) throws Exception {
        ModelInformation info = getModelInformation();
        if (info.getModelPackage() != null) {
            for (ModelPackage pack : info.getModelPackage()) {
                if (pack.getPackageName().equals(packageName) && pack.getModelClass() != null) {
                    for (ModelClass clazz : pack.getModelClass()) {
                        if (clazz.getShortClassName().equals(className)) {
                            return clazz.isTargetable();
                        }
                    }
                }
            }
        }
        throw new Exception("Class " + packageName + "." + className + " not found in the model information");
    }
    
    
    /**
     * Sets that a class is selected in the exposed domain model
     * 
     * @param packageName
     *      The package name
     * @param shortClassName
     *      The short class name
     * @param selected
     *      The selected state
     * @return
     *      True if the class was found and updated in the model, false otherwise
     * @throws Exception
     */
    public boolean setClassSelectedInModel(String packageName, String shortClassName, boolean selected) throws Exception {
        ModelInformation info = getModelInformation();
        if (info.getModelPackage() != null) {
            for (ModelPackage pack : info.getModelPackage()) {
                if (pack.getPackageName().equals(packageName) && pack.getModelClass() != null) {
                    for (ModelClass clazz : pack.getModelClass()) {
                        if (clazz.getShortClassName().equals(shortClassName)) {
                            clazz.setSelected(selected);
                            storeModelInformation(info);
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
    
    
    public boolean getClassSelectedInModel(String packageName, String className) throws Exception {
        ModelInformation info = getModelInformation();
        if (info.getModelPackage() != null) {
            for (ModelPackage pack : info.getModelPackage()) {
                if (pack.getPackageName().equals(packageName) && pack.getModelClass() != null) {
                    for (ModelClass clazz : pack.getModelClass()) {
                        if (clazz.getShortClassName().equals(className)) {
                            return clazz.isSelected();
                        }
                    }
                }
            }
        }
        throw new Exception("Class " + packageName + "." + className + " not found in the model information");
    }
    
    
    /**
     * Gets all model class information in a package
     * 
     * @param packName
     *      The name of the package
     * @return
     *      A list of model classes, or null if none are found
     * @throws Exception
     */
    public List<ModelClass> getClassMappingsInPackage(String packName) throws Exception {
        ModelInformation info = getModelInformation();
        if (info.getModelPackage() != null) {
            for (ModelPackage pack : info.getModelPackage()) {
                if (pack.getPackageName().equals(packName) && pack.getModelClass() != null) {
                    List<ModelClass> mappings = Arrays.asList(pack.getModelClass());
                    return mappings;
                }
            }
        }
        return null;
    }
    
    
    /**
     * Gets the package names used in the metadata model
     * 
     * @return
     *      A list of package names, or null if none are stored
     * @throws Exception
     */
    public List<String> getCadsrPackageNames() throws Exception {
        ModelInformation info = getModelInformation();
        if (info.getModelPackage() != null && info.getModelPackage().length != 0) {
            List<String> names = new LinkedList<String>();
            for (ModelPackage pack : info.getModelPackage()) {
                names.add(pack.getPackageName());
            }
            return names;
        }
        return null;
    }
    
    
    /**
     * Gets the stored short name of the caDSR project used in the domain model
     * @return
     *      The project short name, or null if not found
     * @throws Exception
     */
    public String getModelProjectShortName() throws Exception {
        ModelInformation info = getModelInformation();
        if (info.getUMLProjectIdentifer() != null) {
            return info.getUMLProjectIdentifer().getIdentifier();
        }
        return null;
    }
    
    
    /**
     * Gets the stored version of the caDSR project used in the domain model
     * @return
     *      The project's version, or null if not found
     * @throws Exception
     */
    public String getModelProjectVersion() throws Exception {
        ModelInformation info = getModelInformation();
        if (info.getUMLProjectIdentifer() != null) {
            return info.getUMLProjectIdentifer().getVersion();
        }
        return null;
    }
    
    
    /**
     * Determines if no domain model is to be used
     * 
     * @return
     *      True if the model information specifies no domain model
     * @throws Exception
     */
    public boolean isNoDomainModel() throws Exception {
        ModelInformation info = getModelInformation();
        return ModelSourceType.none.equals(info.getSource());
    }
    
    
    /**
     * Determines if a pre-built domain model is to be used
     * 
     * @return
     *      True if the model information specifies a pre-built domain model
     * @throws Exception
     */
    public boolean isPreBuiltModel() throws Exception {
        ModelInformation info = getModelInformation();
        return ModelSourceType.preBuilt.equals(info.getSource());
    }
    
    
    /**
     * Gets the flag indicating the service is to use WS-Enumeration
     * 
     * @return
     *      The use WS-Enumeration flag
     * @throws Exception
     */
    public boolean isUseWsEnumeration() throws Exception {
        Data data = getExtensionData();
        return data.getServiceFeatures().isUseWsEnumeration();
    }
    
    
    /**
     * Gets the flag indicating the service is to use caGrid Transfer
     * 
     * @return
     * @throws Exception
     */
    public boolean isUseTransfer() throws Exception {
        Data data = getExtensionData();
        return data.getServiceFeatures().isUseTransfer();
    }
    
    
    /**
     * Gets the flag indicating the service is to use Identifiers
     * 
     * @return
     * @throws Exception
     */
    public boolean isUseIdentifiers() throws Exception {
        Data data = getExtensionData();
        return data.getServiceFeatures().isUseGridIdentifiers();
    }
    
    
    /**
     * Gets the service style
     * 
     * @return
     *      The service style, or <code>null</code> if none is supplied
     * @throws Exception
     */
    public ServiceStyle getServiceStyle() throws Exception {
        Data data = getExtensionData();
        return data.getServiceFeatures().getServiceStyle();
    }
    
    
    /**
     * Gets the data service auditors configuration
     * 
     * @return
     *      The service auditors configuration
     * @throws Exception
     */
    public DataServiceAuditors getAuditorsConfiguration() throws Exception {
        Data data = getExtensionData();
        if (data.getDataServiceAuditors() == null) {
            data.setDataServiceAuditors(new DataServiceAuditors());
            saveExtensionData(data);
        }
        return data.getDataServiceAuditors();
    }
    
    
    /**
     * Stores the data service auditors configuration
     * 
     * @param auditors
     * @throws Exception
     */
    public void storeAuditorsConfiguration(DataServiceAuditors auditors) throws Exception {
        Data data = getExtensionData();
        data.setDataServiceAuditors(auditors);
        saveExtensionData(data);
    }
    
    
    private Data getExtensionData() throws Exception {
        return ExtensionDataUtils.getExtensionData(extensionData);
    }
    
    
    private void saveExtensionData(Data data) throws Exception {
        ExtensionDataUtils.storeExtensionData(extensionData, data);
    }
    
    
    private AdditionalLibraries getAdditionalLibraries() throws Exception {
        Data data = getExtensionData();
        AdditionalLibraries libs = data.getAdditionalLibraries();
        if (libs == null) {
            libs = new AdditionalLibraries();
        }
        return libs;
    }
    
    
    private void storeAdditionalLibraries(AdditionalLibraries libs) throws Exception {
        Data data = getExtensionData();
        data.setAdditionalLibraries(libs);
        saveExtensionData(data);
    }
    
    
    public ModelInformation getModelInformation() throws Exception {
        Data data = getExtensionData();
        ModelInformation info = data.getModelInformation();
        if (info == null) {
            info = new ModelInformation();
        }
        return info;
    }
    
    
    public void storeModelInformation(ModelInformation info) throws Exception {
        Data data = getExtensionData();
        data.setModelInformation(info);
        saveExtensionData(data);
    }
}
