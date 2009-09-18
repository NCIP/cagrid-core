package gov.nih.nci.cagrid.sdkquery4.style.beanmap;

import gov.nih.nci.cagrid.data.utilities.DomainModelUtils;
import gov.nih.nci.cagrid.metadata.common.UMLAttribute;
import gov.nih.nci.cagrid.metadata.common.UMLClass;
import gov.nih.nci.cagrid.metadata.dataservice.DomainModel;
import gov.nih.nci.cagrid.metadata.dataservice.UMLGeneralization;
import gov.nih.nci.cagrid.sdkquery4.beans.domaininfo.DomainType;
import gov.nih.nci.cagrid.sdkquery4.beans.domaininfo.DomainTypesInformation;
import gov.nih.nci.cagrid.sdkquery4.beans.domaininfo.TypeAttribute;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/** 
 *  BeanTypeDiscoveryMapper
 *  Utility to read an SDK-generated beans jar file and a domain model
 *  to discovery the inheritance heirachy and attribute's java types
 * 
 * @author David Ervin
 * 
 * @created Jan 15, 2008 10:21:55 AM
 * @version $Id: BeanTypeDiscoveryMapper.java,v 1.3 2008-04-03 15:53:07 dervin Exp $ 
 */
public class BeanTypeDiscoveryMapper {

    private File beansJar = null;
    private DomainModel model = null;
    
    private Map<String, List<String>> subclasses = null;
    private Map<String, String> superclasses = null;
    private Map<String, UMLClass> classesByFullName = null;
    
    private List<BeanTypeDiscoveryEventListener> listeners = null;
    
    public BeanTypeDiscoveryMapper(File beansJar, DomainModel model) {
        this.beansJar = beansJar;
        this.model = model;
        subclasses = new HashMap<String, List<String>>();
        superclasses = new HashMap<String, String>();
        classesByFullName = new HashMap<String, UMLClass>();
        listeners = new LinkedList<BeanTypeDiscoveryEventListener>();
        initializeInheritance();
        initializeClassnameMapping();
    }
    
    
    private void initializeInheritance() {
        UMLGeneralization[] generalizations = model.getUmlGeneralizationCollection().getUMLGeneralization();
        if (generalizations != null && generalizations.length != 0) {
            for (UMLGeneralization gen : generalizations) {
                UMLClass superclass = DomainModelUtils.getReferencedUMLClass(model, gen.getSuperClassReference());
                UMLClass subclass = DomainModelUtils.getReferencedUMLClass(model, gen.getSubClassReference());

                String superName = superclass.getPackageName() + "." + superclass.getClassName();
                String subName = subclass.getPackageName() + "." + subclass.getClassName();

                // map the superclass to a list of subclasses
                List<String> subclassList = subclasses.get(superName);
                if (subclassList == null) {
                    subclassList = new LinkedList<String>();
                    subclasses.put(superName, subclassList);
                }
                subclassList.add(subName);

                // map the subclass to its super
                superclasses.put(subName, superName);
            }
        }
    }
    
    
    private void initializeClassnameMapping() {
        UMLClass[] classes = model.getExposedUMLClassCollection().getUMLClass();
        if (classes != null && classes.length != 0) {
            for (UMLClass c : classes) {
                String name = c.getPackageName() + "." + c.getClassName();
                classesByFullName.put(name, c);
            }
        }
    }
    
    
    private List<String> getClassesInModel() {
        List<String> names = new ArrayList<String>();
        UMLClass[] classes = model.getExposedUMLClassCollection().getUMLClass();
        if (classes != null && classes.length != 0) {
            for (UMLClass c : classes) {
                names.add(c.getPackageName() + "." + c.getClassName());
            }
        }
        return names;
    }
    
    
    public DomainTypesInformation discoverTypesInformation() throws IOException, 
        ClassNotFoundException, NoSuchFieldException {
        // create simple URL class loader for the beans jar
        URLClassLoader loader = new URLClassLoader(new URL[] {beansJar.toURL()}, null);
        // begin walking classes
        DomainTypesInformation info = new DomainTypesInformation();
        List<String> classNames = getClassesInModel();
        DomainType[] domainTypes = new DomainType[classNames.size()];
        for (int i = 0; i < classNames.size(); i++) {
            String beanClassName = classNames.get(i);
            DomainType domainType = new DomainType();
            domainType.setJavaClassName(beanClassName);
            // fire the begin class event
            fireBeanTypeDiscoveryBegins(classNames.size(), i, beanClassName);
            // load the class
            Class beanClass = loader.loadClass(beanClassName);
            // check the domain model for attribute names
            UMLClass umlClass = classesByFullName.get(beanClassName);
            if (umlClass.getUmlAttributeCollection() != null
                && umlClass.getUmlAttributeCollection().getUMLAttribute() != null) {
                UMLAttribute[] attribs = umlClass.getUmlAttributeCollection().getUMLAttribute();
                TypeAttribute[] typeAttribs = new TypeAttribute[attribs.length];
                for (int attIndex = 0; attIndex < attribs.length; attIndex++) {
                    String attribName = attribs[attIndex].getName();
                    Field field = getFieldOfClass(attribName, beanClass);
                    Class fieldType = field.getType();
                    typeAttribs[attIndex] = new TypeAttribute(attribName, fieldType.getName());
                }
                domainType.setTypeAttribute(typeAttribs);
            }
            // set the superclass
            String superName = superclasses.get(beanClassName);
            if (superName != null) {
                domainType.setSuperclassName(superName);
            }
            // set subclasses
            List<String> subNames = subclasses.get(beanClassName);
            if (subNames != null) {
                domainType.setSubclassName(subNames.toArray(new String[0]));
            }
            // add the domain type to the top level type info
            domainTypes[i] = domainType;
        }
        info.setDomainType(domainTypes);
        return info;
    }
    
    
    private Field getFieldOfClass(String fieldName, Class clazz) throws NoSuchFieldException {
        Class c = clazz;
        Field field = null;
        while (c != null && field == null) {
            try {
                field = c.getDeclaredField(fieldName);
            } catch (NoSuchFieldException ex) {
                c = c.getSuperclass();
            }
        }
        if (field != null) {
            return field;
        }
        throw new NoSuchFieldException(fieldName);
    }
    
    
    // --------------
    // event handlers
    // --------------
    
    
    public void addBeanTypeDiscoveryEventListener(BeanTypeDiscoveryEventListener listener) {
        listeners.add(listener);
    }
    
    
    public boolean removeBeanTypeDiscoveryEventListener(BeanTypeDiscoveryEventListener listener) {
        return listeners.remove(listener);
    }
    
    
    public BeanTypeDiscoveryEventListener[] getBeanTypeDiscoveryEventListeners() {
        return listeners.toArray(new BeanTypeDiscoveryEventListener[0]);
    }
    
    
    protected void fireBeanTypeDiscoveryBegins(int total, int current, String beanClassname) {
        BeanTypeDiscoveryEvent event = new BeanTypeDiscoveryEvent(total, current, beanClassname);
        for (BeanTypeDiscoveryEventListener l : listeners) {
            l.typeDiscoveryBegins(event);
        }
    }
}
