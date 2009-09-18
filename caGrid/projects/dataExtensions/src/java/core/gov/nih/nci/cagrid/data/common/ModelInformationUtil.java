package gov.nih.nci.cagrid.data.common;

import gov.nih.nci.cagrid.introduce.beans.ServiceDescription;
import gov.nih.nci.cagrid.introduce.beans.namespace.NamespaceType;
import gov.nih.nci.cagrid.introduce.beans.namespace.SchemaElementType;
import gov.nih.nci.cagrid.introduce.common.CommonTools;

public class ModelInformationUtil {

    private ServiceDescription serviceDesc = null;
    
    public ModelInformationUtil(ServiceDescription serviceDesc) {
        this.serviceDesc = serviceDesc;
    }
    
    
    /**
     * Gets the namespace type which maps to the model package
     * 
     * @param pack
     *      The package to get a namespace mapping for
     * @return
     *      The namespace type, or null if no namespace is mapped to the package
     */
    public NamespaceType getMappedNamespace(String packageName) {
        NamespaceType mapped = null;
        if (serviceDesc.getNamespaces() != null && serviceDesc.getNamespaces().getNamespace() != null) {
            for (NamespaceType nsType : serviceDesc.getNamespaces().getNamespace()) {
                if (packageName.equals(nsType.getPackageName())) {
                    mapped = nsType;
                    break;
                }
            }
        }
        return mapped;
    }
    
    
    /**
     * Gets the element mapped to the class
     * 
     * @param pack
     *      The package in which the class resides
     * @param clazz
     *      The class to get an element mapping for
     * @return
     *      The schema element type, or null if no namespace or element is mapped
     */
    public SchemaElementType getMappedElement(String packageName, String className) {
        SchemaElementType mapped = null;
        NamespaceType nsType = getMappedNamespace(packageName);
        if (nsType != null && nsType.getSchemaElement() != null) {
            for (SchemaElementType element : nsType.getSchemaElement()) {
                if (className.equals(element.getClassName())) {
                    mapped = element;
                    break;
                }
            }
        }
        return mapped;
    }
    
    
    public void setMappedNamespace(String packageName, String namespace) throws Exception {
        // find the namespace
        NamespaceType nsType = null;
        if (serviceDesc.getNamespaces() != null) {
            nsType = CommonTools.getNamespaceType(serviceDesc.getNamespaces(), namespace);
        }
        if (nsType == null) {
            throw new Exception("No namespace " + namespace + " was found in the service model");
        }
        // set the package mapping
        nsType.setPackageName(packageName);
    }
    
    
    /**
     * Sets the element name mapped to the class.  The package must first
     * be mapped to a namespace in the service model or this method will fail
     * 
     * @param pack
     *      The package in which the class resides
     * @param clazz
     *      The class to map
     * @param elementName
     *      The name of the element to map
     * @throws Exception
     *      If the package is not mapped to a namespace or an element 
     *      of the given name could not be found
     */
    public void setMappedElementName(String packageName, String className, String elementName) throws Exception {
        NamespaceType mappedNamespace = getMappedNamespace(packageName);
        if (mappedNamespace == null) {
            throw new Exception("No namespace was mapped to the package " + packageName);
        }
        SchemaElementType element = CommonTools.getSchemaElementType(mappedNamespace, elementName);
        if (element == null) {
            throw new Exception("No element " + elementName 
                + " could be found in the namespace " + mappedNamespace.getNamespace());
        }
        element.setClassName(className);
    }
    
    
    public boolean unsetMappedNamespace(String packageName) throws Exception {
        boolean found = false;
        for (NamespaceType nsType : serviceDesc.getNamespaces().getNamespace()) {
            if (packageName.equals(nsType.getPackageName())) {
                nsType.setPackageName(null);
                // unset all schema elements too
                for (SchemaElementType element : nsType.getSchemaElement()) {
                    element.setClassName(null);
                    element.setSerializer(null);
                    element.setDeserializer(null);
                }
                found = true;
                break;
            }
        }
        return found;
    }
    
    
    /**
     * Removes an element to class mapping from the service's namespace information
     * 
     * @param packageName
     *      The class's package name
     * @param className
     *      The short class name
     * @return
     *      True if an element was found and updated, false otherwise
     * @throws Exception
     */
    public boolean unsetMappedElementName(String packageName, String className) throws Exception {
        NamespaceType mappedNamespace = getMappedNamespace(packageName);
        if (mappedNamespace == null) {
            throw new Exception("No namespace was mapped to the package " + packageName);
        }
        boolean found = false;
        // walk the elements and if one os mapped to the class name, null out the class name
        if (mappedNamespace.getSchemaElement() != null) {
            for (SchemaElementType element : mappedNamespace.getSchemaElement()) {
                if (className.equals(element.getClassName())) {
                    element.setClassName(null);
                    element.setSerializer(null);
                    element.setDeserializer(null);
                    found = true;
                    break;
                }
            }
        }
        return found;
    }
}
