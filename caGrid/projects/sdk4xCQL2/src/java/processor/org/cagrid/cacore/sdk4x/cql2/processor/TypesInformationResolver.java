package org.cagrid.cacore.sdk4x.cql2.processor;

public interface TypesInformationResolver {

    public Object getClassDiscriminatorValue(String classname) throws TypesInformationException;
    
    public boolean classHasSubclasses(String classname) throws TypesInformationException;
    
    public Class<?> getJavaDataType(String classname, String field) throws TypesInformationException;
    
    public String getEndName(String parentClassname, String childClassname) throws TypesInformationException;
}
