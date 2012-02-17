package org.cagrid.data.sdkquery44.translator;

import java.util.List;

/**
 * TypesInformationResolver
 * Used to resolve various information about domain datatypes
 * 
 * @author David
 */
public interface TypesInformationResolver {

    public Object getClassDiscriminatorValue(String classname) throws TypesInformationException;
    
    public boolean classHasSubclasses(String classname) throws TypesInformationException;
    
    public Class<?> getJavaDataType(String classname, String field) throws TypesInformationException;
    
    public String getRoleName(String parentClassname, String childClassname) throws TypesInformationException;
    
    public List<String> getInnerComponentNames(String parentClassname, String topLevelComponentName, String innerComponentNamePrefix);
    
    public List<String> getNestedInnerComponentNames(String parentClassname, String topLevelComponentName,
        String nestedComponentName, String innerComponentNamePrefix);
}
