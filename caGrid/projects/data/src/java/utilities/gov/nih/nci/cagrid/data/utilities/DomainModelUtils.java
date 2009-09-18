package gov.nih.nci.cagrid.data.utilities;

import gov.nih.nci.cagrid.metadata.common.SemanticMetadata;
import gov.nih.nci.cagrid.metadata.common.UMLAttribute;
import gov.nih.nci.cagrid.metadata.dataservice.DomainModel;
import gov.nih.nci.cagrid.metadata.dataservice.UMLClass;
import gov.nih.nci.cagrid.metadata.dataservice.UMLClassReference;
import gov.nih.nci.cagrid.metadata.dataservice.UMLGeneralization;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * DomainModelUtils Utilities for walking and manipulating a DomainModel
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster</A>
 * @created Jun 14, 2006
 * @version $Id$
 */
public class DomainModelUtils {

    private static Map<DomainModel, Map<String, UMLClass>> domainRefedClasses = new HashMap<DomainModel, Map<String, UMLClass>>();


    /**
     * Return the UMLClass from the DomainModel's exposed UML Classes if the
     * UMLClass with an id exists which is equal to the refid of the
     * UMLClassReference. In all other cases, null is returned.
     * 
     * @param model
     *            the DomainModel to look in
     * @param reference
     *            the UMLClassReference to the UMLClass to look for in the model
     * @return null or the referenced UMLClass
     */
    public static UMLClass getReferencedUMLClass(DomainModel model, UMLClassReference reference) {
        if (model == null) {
            return null;
        }
        Map<String, UMLClass> refedClasses = domainRefedClasses.get(model);
        if (refedClasses == null) {
            // populate class references for this model
            UMLClass[] classes = model.getExposedUMLClassCollection().getUMLClass();
            refedClasses = new HashMap<String, UMLClass>(classes.length);
            for (int i = 0; i < classes.length; i++) {
                UMLClass clazz = classes[i];
                refedClasses.put(clazz.getId(), clazz);
            }
        }
        UMLClass refedClass = refedClasses.get(reference.getRefid());
        return refedClass;
    }


    /**
     * Gets all superclasses of the specified UMLCLass
     * 
     * @param model
     *            The domain model to search for superclasses
     * @param umlClass
     *            The class to retrieve superclasses of
     * @return All superclasses of the specified UMLClass
     */
    public static UMLClass[] getAllSuperclasses(DomainModel model, UMLClass umlClass) {
        return getAllSuperclasses(model, umlClass.getPackageName() + "." + umlClass.getClassName());
    }


    /**
     * Gets all superclasses of the specified UMLCLass
     * 
     * @param model
     *            The domain model to search for superclasses
     * @param className
     *            The name of the class to retrieve superclasses of
     * @return All superclasses of the named class
     */
    public static UMLClass[] getAllSuperclasses(DomainModel model, String className) {
        Set<UMLClass> supers = getSuperclasses(model, className);
        UMLClass[] classes = new UMLClass[supers.size()];
        supers.toArray(classes);
        return classes;
    }


    /**
     * Gets the UMLClassReference for a class in a domain model
     * 
     * @param model
     *            The domain model
     * @param className
     *            The fully qualified name of the class
     * @return The UMLClassReference, or null if no class was found
     */
    public static UMLClassReference getClassReference(DomainModel model, String className) {
        UMLClass[] classes = model.getExposedUMLClassCollection().getUMLClass();
        for (UMLClass c : classes) {
            if (getQualifiedClassname(c).equals(className)) {
                return new UMLClassReference(c.getId());
            }
        }
        return null;
    }


    /**
     * @param clazz
     * @return the package qualified classname of the given class
     */
    public static String getQualifiedClassname(UMLClass clazz) {
        if (clazz.getPackageName() != null) {
            return clazz.getPackageName() + "." + clazz.getClassName();
        }
        return clazz.getClassName();
    }


    /**
     * @param model
     *            the DomainModel to examine
     * @param conceptCode
     *            the concept code to look for
     * @param primaryConceptOnly
     *            when true, only return UMLClasses whose primary concept code
     *            matches the given concept; when false, return if any concept
     *            matches
     * @return the UMLClasses in the given DomainModel which are based on the
     *         given concept code
     */
    public static UMLClass[] getUMLClassForConcept(DomainModel model, String conceptCode, boolean primaryConceptOnly) {
        if (conceptCode == null || model == null || model.getExposedUMLClassCollection() == null
            || model.getExposedUMLClassCollection().getUMLClass() == null) {
            return null;
        }
        Set<UMLClass> result = new HashSet<UMLClass>();

        UMLClass[] classes = model.getExposedUMLClassCollection().getUMLClass();
        // iterate all the model's classes
        for (UMLClass c : classes) {
            // look at the semantic information
            SemanticMetadata[] semanticMetadata = c.getSemanticMetadata();
            if (semanticMetadata != null && semanticMetadata.length > 0) {
                for (SemanticMetadata sm : semanticMetadata) {
                    // if we found a concept match
                    if (conceptCode.equals(sm.getConceptCode())) {
                        // if we are only interested in primary concepts, need
                        // to check the order too
                        if (primaryConceptOnly) {
                            if (sm.getOrder().intValue() == 0) {
                                result.add(c);
                                break;
                            }
                        } else {
                            // otherwise, we don't care about order, just added
                            // it
                            result.add(c);
                            break;
                        }
                    }
                }
            }
        }

        return result.toArray(new UMLClass[result.size()]);
    }


    /**
     * Finds common data elements between two domain models, useful for finding
     * join points for DCQL queries. The CDEs are considered "common" if they
     * use the same public ID and version. The SemanticMetadata of the returned
     * CDE map can then be checked for appropriate Semantic Equivalence
     * 
     * @param domainModel1
     *            the first domain model
     * @param domainModel2
     *            the second domain model
     * @return a Map of CDEs from the first model, which map to CDEs in the
     *         second model
     */
    public static Map<CDE, CDE> mapCommonDataElements(DomainModel domainModel1, DomainModel domainModel2) {
        Map<CDE, CDE> commonDataElementList = new HashMap<CDE, CDE>();
        if (domainModel1 == null || domainModel1.getExposedUMLClassCollection() == null
            || domainModel1.getExposedUMLClassCollection().getUMLClass() == null || domainModel2 == null
            || domainModel2.getExposedUMLClassCollection() == null
            || domainModel2.getExposedUMLClassCollection().getUMLClass() == null

        ) {
            return commonDataElementList;
        }

        // Parse all the Data Elements from first domain model
        Map<String, CDE> dataElementMap = new HashMap<String, CDE>(50);
        for (UMLClass clazz : domainModel1.getExposedUMLClassCollection().getUMLClass()) {
            if (clazz.getUmlAttributeCollection() != null
                && clazz.getUmlAttributeCollection().getUMLAttribute() != null) {
                for (UMLAttribute att : clazz.getUmlAttributeCollection().getUMLAttribute()) {
                    CDE dataElement = new CDE(clazz, att);
                    dataElementMap.put(dataElement.getIdentifier(), dataElement);
                }
            }
        }

        // Parse all the Data Elements from second domain model and return
        // potential join points
        for (UMLClass clazz : domainModel2.getExposedUMLClassCollection().getUMLClass()) {
            if (clazz.getUmlAttributeCollection() != null
                && clazz.getUmlAttributeCollection().getUMLAttribute() != null) {
                for (UMLAttribute att : clazz.getUmlAttributeCollection().getUMLAttribute()) {
                    CDE dataElement = new CDE(clazz, att);
                    if (dataElementMap.containsKey(dataElement.getIdentifier())) {
                        CDE sourceDataElement = dataElementMap.get(dataElement.getIdentifier());
                        commonDataElementList.put(sourceDataElement, dataElement);
                    }
                }
            }
        }

        return commonDataElementList;
    }


    private static Set<UMLClass> getSuperclasses(DomainModel model, String className) {
        UMLGeneralization[] generalization = model.getUmlGeneralizationCollection().getUMLGeneralization();
        Set<UMLClass> superclasses = new HashSet<UMLClass>();
        // find all generalizations where subclass is the class in question,
        // then get the superclasses from each
        for (int i = 0; generalization != null && i < generalization.length; i++) {
            UMLClassReference subClassRef = generalization[i].getSubClassReference();
            UMLClassReference superClassRef = generalization[i].getSuperClassReference();
            if (subClassRef != null && superClassRef != null) {
                UMLClass subclass = getReferencedUMLClass(model, subClassRef);
                if ((subclass.getPackageName() + "." + subclass.getClassName()).equals(className)) {
                    UMLClass superclass = getReferencedUMLClass(model, superClassRef);
                    superclasses.add(superclass);
                    // get superclasses of the superclass
                    superclasses.addAll(getSuperclasses(model, superclass.getPackageName() + "."
                        + superclass.getClassName()));
                }
            }
        }
        return superclasses;
    }


    /**
     * Represents a simplified Common Data Element (a Class and Attribute),
     * identified by attributed public ID and version.
     */
    public static class CDE {
        private UMLClass umlClass;
        private UMLAttribute umlAttribute;


        public CDE(UMLClass umlClass, UMLAttribute umlAttribute) {
            this.umlClass = umlClass;
            this.umlAttribute = umlAttribute;
        }


        public UMLClass getUmlClass() {
            return umlClass;
        }


        public void setUmlClass(UMLClass umlClass) {
            this.umlClass = umlClass;
        }


        public UMLAttribute getUmlAttribute() {
            return umlAttribute;
        }


        public void setUmlAttribute(UMLAttribute umlAttribute) {
            this.umlAttribute = umlAttribute;
        }


        public String getIdentifier() {
            return umlAttribute.getPublicID() + ":" + String.valueOf(umlAttribute.getVersion());
        }


        @Override
        public int hashCode() {
            return getIdentifier().hashCode();
        }


        @Override
        public String toString() {
            return getIdentifier();
        }
    }

}
