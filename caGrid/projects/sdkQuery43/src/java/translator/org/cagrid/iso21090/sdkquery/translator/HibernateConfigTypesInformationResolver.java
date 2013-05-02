/**
*============================================================================
*  Copyright The Ohio State University Research Foundation, The University of Chicago - 
*	Argonne National Laboratory, Emory University, SemanticBits LLC, and 
*	Ekagra Software Technologies Ltd.
*
*  Distributed under the OSI-approved BSD 3-Clause License.
*  See http://ncip.github.com/cagrid-core/LICENSE.txt for details.
*============================================================================
**/
package org.cagrid.iso21090.sdkquery.translator;

import gov.nih.nci.cacoresdk.domain.manytomany.bidirectional.Employee;
import gov.nih.nci.cacoresdk.domain.manytomany.bidirectional.Project;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.mapping.Collection;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.OneToMany;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.Set;
import org.hibernate.mapping.Subclass;
import org.hibernate.mapping.ToOne;
import org.hibernate.mapping.Value;

public class HibernateConfigTypesInformationResolver implements TypesInformationResolver {
    
    private static Log LOG = LogFactory.getLog(HibernateConfigTypesInformationResolver.class);
    
    private Configuration configuration = null;
    private Map<String, Boolean> subclasses = null;
    private Map<String, Object> discriminators = null;
    private Map<String, Class<?>> fieldDataTypes = null;
    private Map<String, String> roleNames = null;
    private boolean reflectionFallback = false;
    
    public HibernateConfigTypesInformationResolver(Configuration hibernateConfig, boolean reflectionFallback) {
        this.configuration = hibernateConfig;
        this.subclasses = new HashMap<String, Boolean>();
        this.discriminators = new HashMap<String, Object>();
        this.fieldDataTypes = new HashMap<String, Class<?>>();
        this.roleNames = new HashMap<String, String>();
        this.reflectionFallback = reflectionFallback;
    }
    

    public boolean classHasSubclasses(String classname) throws TypesInformationException {
        Boolean hasSubclasses = subclasses.get(classname);
        if (hasSubclasses == null) {
            PersistentClass clazz = configuration.getClassMapping(classname);
            if (clazz != null) {
                hasSubclasses = Boolean.valueOf(clazz.hasSubclasses());
                subclasses.put(classname, hasSubclasses);
            } else {
                if (reflectionFallback) {
                    Class<?> c = null;
                    try {
                        c = Class.forName(classname);
                    } catch (ClassNotFoundException e) {
                        throw new TypesInformationException("Class " + classname + " not found via reflection");
                    }
                    Iterator<?> mapIter = configuration.getClassMappings();
                    boolean found = false;
                    while (mapIter.hasNext() && !found) {
                        PersistentClass p = (PersistentClass) mapIter.next();
                        Class<?> pc = null;
                        try {
                            pc = Class.forName(p.getClassName());
                        } catch (ClassNotFoundException ex) {
                            LOG.warn(ex.getMessage(), ex);
                        }
                        found = c.isAssignableFrom(pc);
                    }
                    hasSubclasses = Boolean.valueOf(found);
                } else {
                    throw new TypesInformationException("Class " + classname + " not found in configuration");
                }
            }
        }
        return hasSubclasses.booleanValue();
    }


    public Object getClassDiscriminatorValue(String classname) throws TypesInformationException {
        Object identifier = discriminators.get(classname);
        if (identifier == null) {
            PersistentClass clazz = configuration.getClassMapping(classname);
            if (clazz != null) {
                if (clazz instanceof Subclass) {
                    Subclass sub = (Subclass) clazz;
                    if (sub.isJoinedSubclass()) {
                        identifier = Integer.valueOf(sub.getSubclassId());
                    } else {
                        identifier = getShortClassName(classname);
                    }
                } else if (clazz instanceof RootClass) {
                    RootClass root = (RootClass) clazz;
                    if (root.getDiscriminator() == null) {
                        identifier = Integer.valueOf(root.getSubclassId());
                    } else {
                        identifier = getShortClassName(classname);
                    }
                }
            } else {
                throw new TypesInformationException("Class " + classname + " not found in hibernate configuration");
            }
            discriminators.put(classname, identifier);
        }
        return identifier;
    }

    
    public Class<?> getJavaDataType(String classname, String field) throws TypesInformationException {
        String fqName = classname + "." + field;
        Class<?> type = fieldDataTypes.get(fqName);
        if (type == null) {
            PersistentClass clazz = configuration.getClassMapping(classname);
            if (clazz != null) {
                Property property = clazz.getRecursiveProperty(field);
                if (property != null) {
                    type = property.getType().getReturnedClass();
                } else {
                    throw new TypesInformationException("Field " + fqName + " not found in hibernate configuration");
                }
            } else if (reflectionFallback) {
                try {
                    Class<?> javaClass = Class.forName(classname);
                    List<Class<?>> classHierarchy = getClassHierarchy(javaClass);
                    for (Iterator<Class<?>> classIter = classHierarchy.iterator(); 
                        classIter.hasNext() && type == null;) {
                        Class<?> checkClass = classIter.next();
                        try {
                            Field javaField = checkClass.getDeclaredField(field);
                            type = javaField.getType();
                        } catch (NoSuchFieldException ex) {
                            LOG.debug("Class " + checkClass.getName() + " did not declare field " + field);
                        }
                    }
                } catch (ClassNotFoundException ex) {
                    throw new TypesInformationException("Class " + classname + " not found in hibernate configuration or via reflection");
                }
                if (type == null) {
                    throw new TypesInformationException("Field " + field + " of class " + classname + " could not be found via reflection");
                }
            } else {
                throw new TypesInformationException("Class " + classname + " not found in hibernate configuration");
            }
            fieldDataTypes.put(fqName, type);
        }
        return type;
    }


    public String getRoleName(String parentClassname, String childClassname) throws TypesInformationException {
        String identifier = getAssociationIdentifier(parentClassname, childClassname);
        String roleName = roleNames.get(identifier);
        if (roleName == null) {
            PersistentClass clazz = configuration.getClassMapping(parentClassname);
            Iterator<?> propertyIter = clazz.getPropertyIterator();
            while (propertyIter.hasNext()) {
                Property prop = (Property) propertyIter.next();
                Value value = prop.getValue();
                String referencedEntity = null;
                if (value instanceof Collection) {
                    Value element = ((Collection) value).getElement();
                    if (element instanceof OneToMany) {
                        referencedEntity = ((OneToMany) element).getReferencedEntityName();
                    } else if (element instanceof ToOne) {
                        referencedEntity = ((ToOne) element).getReferencedEntityName();
                    }
                } else if (value instanceof ToOne) {
                    referencedEntity = ((ToOne) value).getReferencedEntityName();
                }
                if (childClassname.equals(referencedEntity)) {
                    if (roleName != null) {
                        // already found one association, so this is ambiguous
                        throw new TypesInformationException("Association from " + parentClassname + " to " 
                            + childClassname + " is ambiguous.  Please specify a valid role name");
                    }
                    roleName = prop.getName();
                }
            }
            if (roleName == null && reflectionFallback) {
                LOG.debug("No role name found for " + identifier + " using hibernate configuration, trying reflection");
                Class<?> parentClass = null;
                try {
                    parentClass = Class.forName(parentClassname);
                } catch (ClassNotFoundException ex) {
                    LOG.error("Could not load parent class: " + ex.getMessage());
                    throw new TypesInformationException("Could not load parent class: " + ex.getMessage());
                }
                Field[] fields = parentClass.getDeclaredFields();
                for (Field f : fields) {
                    // if collection, inspect the collection for type
                    Class<?> fieldType = f.getType();
                    if (java.util.Collection.class.isAssignableFrom(fieldType)) {
                        Type generic = f.getGenericType();
                        if (generic instanceof ParameterizedType) {
                            Type contents = ((ParameterizedType) generic).getActualTypeArguments()[0];
                            if (contents instanceof Class 
                                && childClassname.equals(((Class) contents).getName())) {
                                roleName = f.getName();
                            }
                        }
                    } else if (fieldType.getName().equals(childClassname)) {
                        if (roleName != null) {
                            // already found one association, so this is ambiguous
                            throw new TypesInformationException("Association from " + parentClassname + " to " 
                                + childClassname + " is ambiguous.  Please specify a valid role name");
                        }
                        roleName = f.getName();
                    }
                }
            }
        }
        return roleName;
    }
    
    
    public List<String> getInnerComponentNames(String parentClassname, String topLevelComponentName,
        String innerComponentNamePrefix) {
        List<String> names = new ArrayList<String>();
        PersistentClass parent = configuration.getClassMapping(parentClassname);
        Property topLevel = parent.getProperty(topLevelComponentName);
        Component topLevelComponent = (Component) topLevel.getValue();
        Iterator<?> propertyIter = topLevelComponent.getPropertyIterator();
        while (propertyIter.hasNext()) {
            Property prop = (Property) propertyIter.next();
            if (prop.getName().startsWith(innerComponentNamePrefix)) {
                names.add(prop.getName());
            }
        }
        return names;
    }
    
    
    public List<String> getNestedInnerComponentNames(String parentClassname, String topLevelComponentName,
        String nestedComponentName, String innerComponentNamePrefix) {
        List<String> names = new ArrayList<String>();
        PersistentClass parent = configuration.getClassMapping(parentClassname);
        Property topLevelProperty = parent.getProperty(topLevelComponentName);
        Component topLevelComponent = (Component) topLevelProperty.getValue();
        Property nestedProperty = topLevelComponent.getProperty(nestedComponentName);
        Set nestedSet = (Set) nestedProperty.getValue();
        Component nestedComponent = (Component) nestedSet.getElement();
        Iterator<?> propertyIter = nestedComponent.getPropertyIterator();
        while (propertyIter.hasNext()) {
            Property prop = (Property) propertyIter.next();
            if (prop.getName().startsWith(innerComponentNamePrefix)) {
                names.add(prop.getName());
            }
        }
        return names;
    }
    
    
    private String getShortClassName(String className) {
        int dotIndex = className.lastIndexOf('.');
        return className.substring(dotIndex + 1);
    }
    
    
    private String getAssociationIdentifier(String parentClassname, String childClassname) {
        return parentClassname + "-->" + childClassname;
    }
    
    
    /**
     * Follows the same class traversal algorithm as Class.getField()
     * 
     * @param c
     * @return
     */
    private List<Class<?>> getClassHierarchy(Class<?> c) {
        List<Class<?>> parents = new ArrayList<Class<?>>();
        parents.add(c);
        List<Class<?>> interfaces = new ArrayList<Class<?>>();
        Collections.addAll(interfaces, c.getInterfaces());
        for (Class<?> superInterface : interfaces) {
            // recurse!
            parents.addAll(getClassHierarchy(superInterface));
        }
        if (c.getSuperclass() != null) {
            parents.addAll(getClassHierarchy(c.getSuperclass()));
        }
        return parents;
    }
    
    
    public static void main(String[] args) {
        try {
            InputStream is = HibernateConfigTypesInformationResolver.class.getResourceAsStream("/hibernate.cfg.xml");
            Configuration config = new Configuration();
            config.addInputStream(is);
            config.buildMappings();
            config.configure();
            
            HibernateConfigTypesInformationResolver resolver = new HibernateConfigTypesInformationResolver(config, true);
            String roleName = resolver.getRoleName(Project.class.getName(), Employee.class.getName());
            System.out.println(roleName);
            is.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
