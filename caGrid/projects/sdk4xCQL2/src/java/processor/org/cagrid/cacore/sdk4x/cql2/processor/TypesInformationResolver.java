package org.cagrid.cacore.sdk4x.cql2.processor;

import java.util.List;

public interface TypesInformationResolver {

    public Object getClassDiscriminatorValue(String classname) throws TypesInformationException;
    
    public boolean classHasSubclasses(String classname) throws TypesInformationException;
    
    public Class<?> getJavaDataType(String classname, String field) throws TypesInformationException;
    
    public String getEndName(String parentClassname, String childClassname) throws TypesInformationException;
    
    public List<ClassAssociation> getAssociationsFromClass(String parentClassname) throws TypesInformationException;
}
