package gov.nih.nci.cagrid.data.cql2.validation;

import gov.nih.nci.cagrid.data.utilities.DomainModelUtils;
import gov.nih.nci.cagrid.metadata.common.Enumeration;
import gov.nih.nci.cagrid.metadata.common.UMLAttribute;
import gov.nih.nci.cagrid.metadata.common.ValueDomain;
import gov.nih.nci.cagrid.metadata.common.ValueDomainEnumerationCollection;
import gov.nih.nci.cagrid.metadata.dataservice.DomainModel;
import gov.nih.nci.cagrid.metadata.dataservice.UMLAssociation;
import gov.nih.nci.cagrid.metadata.dataservice.UMLClass;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.cql2.AssociationPopulationSpecification;
import org.cagrid.cql2.AttributeValue;
import org.cagrid.cql2.CQLAssociatedObject;
import org.cagrid.cql2.CQLAttribute;
import org.cagrid.cql2.CQLGroup;
import org.cagrid.cql2.CQLObject;
import org.cagrid.cql2.CQLQuery;
import org.cagrid.cql2.CQLQueryModifier;
import org.cagrid.cql2.CQLTargetObject;
import org.cagrid.cql2.NamedAssociation;
import org.cagrid.cql2.NamedAssociationList;
import org.cagrid.cql2.NamedAttribute;

/**
 * DomainModelCql2DomainValidator
 * Cql2DomainValidator implementation which uses a caGrid Domain Model
 * 
 * @author David
 */
public class DomainModelCql2DomainValidator extends Cql2DomainValidator {
    
    private static final Log LOG = LogFactory.getLog(DomainModelCql2DomainValidator.class);
    
    public DomainModelCql2DomainValidator(DomainModel model) {
        super(model);
    }
    
    
    public void validateAgainstDomainModel(CQLQuery query) throws DomainValidationException {
        validateQueryTarget(query.getCQLTargetObject());
        if (query.getAssociationPopulationSpecification() != null) {
            validateAssociationPopulation(
                query.getCQLTargetObject().getCQLAssociatedObject().getClassName(), 
                query.getAssociationPopulationSpecification());
        }
        if (query.getCQLQueryModifier() != null) {
            validateQueryModifier(query.getCQLTargetObject().getClassName(), query.getCQLQueryModifier());
        }
    }
    
    
    private void validateQueryModifier(String targetClassname, CQLQueryModifier modifier) throws DomainValidationException {
        UMLClass targetClass = getUmlClass(targetClassname);
        UMLAttribute[] attributes = targetClass.getUmlAttributeCollection().getUMLAttribute();
        Set<String> attributeNames = new HashSet<String>();
        if (attributes != null) {
            for (UMLAttribute att : attributes) {
                attributeNames.add(att.getName());
            }
        }
        if (modifier.getDistinctAttribute() != null && modifier.getDistinctAttribute().getAttributeName() != null) {
            if (!attributeNames.contains(modifier.getDistinctAttribute().getAttributeName())) {
                throw new DomainValidationException("Target class " + targetClassname 
                    + " does not have attribute named " + modifier.getDistinctAttribute().getAttributeName());
            }
        } else if (modifier.getNamedAttribute() != null) {
            for (NamedAttribute named : modifier.getNamedAttribute()) {
                if (!attributeNames.contains(named.getAttributeName())) {
                    throw new DomainValidationException("Target class " + targetClassname 
                        + " does not have attribute named " + modifier.getDistinctAttribute().getAttributeName());
                }
            }
        }
    }
    
    
    private void validateAssociationPopulation(String targetClassName, AssociationPopulationSpecification spec) throws DomainValidationException {
        if (spec.getPopulationDepth() != null) {
            if (spec.getPopulationDepth().getDepth() < 0) {
                throw new DomainValidationException("Negative association population depth specified");
            }
        } else if (spec.getNamedAssociationList() != null && spec.getNamedAssociationList().getNamedAssociation() != null) {
            validateNamedAssociationList(targetClassName, spec.getNamedAssociationList());
        }
    }
    
    
    private void validateNamedAssociationList(String parentClassName, NamedAssociationList list) throws DomainValidationException {
        for (NamedAssociation association : list.getNamedAssociation()) {
            SimplifiedUmlAssociation foundAssociation = null;
            List<SimplifiedUmlAssociation> simpleAssociations = getUmlAssociations(parentClassName);
            for (SimplifiedUmlAssociation simple : simpleAssociations) {
                if (!simple.isBidirectional() && simple.getSourceRoleName().equals(association.getRoleName())) {
                    foundAssociation = simple;
                    break;
                }
            }
            if (foundAssociation == null) {
                // association not found
                throw new DomainValidationException("Association from " + parentClassName 
                    + " via role name " + association.getRoleName() + " not found");
            } else if (association.getNamedAssociationList() != null && association.getNamedAssociationList().getNamedAssociation().length != 0) {
                // association found, if there is another nested layer of associations, follow those
                validateNamedAssociationList(foundAssociation.getTargetClass(), association.getNamedAssociationList());
            }
        }
    }
    
    
    private void validateQueryTarget(CQLTargetObject target) throws DomainValidationException {
        UMLClass targetClass = getUmlClass(target.getClassName());
        if (targetClass == null) {
            throw new DomainValidationException("Query target " + target.getClassName()
                + " was not found in the domain model");
        } else if (!targetClass.isAllowableAsTarget()) {
            throw new DomainValidationException("Query target " + target.getClassName() 
                + " is not allowed as a target in the domain model");
        }
        validateObjectModel(target);
    }


    private void validateObjectModel(CQLObject obj) throws DomainValidationException {
        // verify the object exists in the project
        UMLClass classMd = getUmlClass(obj.getClassName());
        if (classMd == null) {
            throw new DomainValidationException("No data type " + obj.getClassName() 
                + " could be found in the domain model");
        }

        if (obj.getCQLAttribute() != null) {
            validateAttributeModel(obj.getCQLAttribute(), classMd);
        }

        if (obj.getCQLAssociatedObject() != null) {
            // ensure the association is valid
            validateAssociationModel(obj, obj.getCQLAssociatedObject());
            // step through the association's submodel
            validateObjectModel(obj.getCQLAssociatedObject());
        }

        if (obj.getCQLGroup() != null) {
            validateGroupModel(obj, obj.getCQLGroup());
        }
        
        if (obj.get_instanceof() != null) {
            validateInstanceof(obj.getClassName(), obj.get_instanceof());
        }
    }


    private void validateAttributeModel(CQLAttribute attrib, UMLClass classMd) throws DomainValidationException {
        // verify the attribute exists
        UMLAttribute attribMd = getUmlAttribute(attrib.getName(), classMd);
        if (attribMd == null) {
            throw new DomainValidationException("Attribute '" + attrib.getName() + "' is not defined for the class "
                + classMd.getClassName());
        }
        if (attrib.getBinaryPredicate() != null) {
            // verify the data type being used is compatible
            String datatype = attribMd.getDataTypeName();
            String valueAsString = null;
            if (datatype == null) {
                // can't validate anything if there isn't a data type
                LOG.warn("No datatype defined for attribute " + attrib.getName());
            } else {
                // verify the proper typed value is populated
                AttributeValue value = attrib.getAttributeValue();
                if (String.class.getName().equals(datatype)) {
                    if (value.getStringValue() == null) {
                        throw new DomainValidationException("Attribute " + attrib.getName() 
                            + " was expected to have a value of type " + String.class.getName() + ", but did not");
                    } else {
                        valueAsString = value.getStringValue();
                    }
                } else if (Integer.class.getName().equals(datatype)) {
                    if (value.getIntegerValue() == null) {
                        throw new DomainValidationException("Attribute " + attrib.getName() 
                            + " was expected to have a value of type " + Integer.class.getName() + ", but did not");
                    } else {
                        valueAsString = value.getIntegerValue().toString();
                    }
                } else if (Long.class.getName().equals(datatype)) {
                    if (value.getLongValue() == null) {
                        throw new DomainValidationException("Attribute " + attrib.getName() 
                            + " was expected to have a value of type " + Long.class.getName() + ", but did not");
                    } else {
                        valueAsString = value.getLongValue().toString();
                    }
                } else if (Date.class.getName().equals(datatype)) {
                    if (value.getDateValue() == null) {
                        throw new DomainValidationException("Attribute " + attrib.getName() 
                            + " was expected to have a value of type " + Date.class.getName() + ", but did not");
                    } else {
                        valueAsString = DateFormat.getDateInstance().format(value.getDateValue());                    
                    }
                } else if (Boolean.class.getName().equals(datatype)) {
                    if (value.getBooleanValue() == null) {
                        throw new DomainValidationException("Attribute " + attrib.getName() 
                            + " was expected to have a value of type " + Boolean.class.getName() + ", but did not");
                    } else {
                        valueAsString = value.getBooleanValue().toString();
                    }
                } else if (Double.class.getName().equals(datatype)) {
                    if (value.getDoubleValue() == null) {
                        throw new DomainValidationException("Attribute " + attrib.getName() 
                            + " was expected to have a value of type " + Double.class.getName() + ", but did not");
                    } else {
                        valueAsString = value.getDoubleValue().toString();
                    }
                }
            }
            //check enumeration values
            ValueDomain valueDomain = attribMd.getValueDomain();
            if (valueDomain != null) {
                ValueDomainEnumerationCollection enumerationCollection = valueDomain.getEnumerationCollection();
                if (enumerationCollection != null && enumerationCollection.getEnumeration() != null
                    && enumerationCollection.getEnumeration().length > 0) {
                    Enumeration[] enumeration = enumerationCollection.getEnumeration();
                    Set<String> permValues = new HashSet<String>();
                    for (int i = 0; i < enumeration.length; i++) {
                        Enumeration e = enumeration[i];
                        permValues.add(e.getPermissibleValue());
                    }
                    if (!permValues.contains(valueAsString)) {
                        throw new DomainValidationException("Attribute '" + attrib.getName()
                            + "' defines a permissible value enumeration, and the value'" + valueAsString
                            + "' is not permissible.");
                    }
                }
            }
        }
    }


    private void validateAssociationModel(CQLObject current, CQLAssociatedObject assoc)
        throws DomainValidationException {
        // determine if an association exists between the current
        // and association object
        boolean associationFound = associationExists(
            current.getClassName(), assoc.getClassName(), assoc.getEndName());
        
        // fail if the association was never found
        if (!associationFound) {
            throw new DomainValidationException("No association from " + current.getClassName() + " to " + assoc.getClassName()
                + " with role name " + assoc.getEndName());
        }
        if (assoc.get_instanceof() != null) {
            validateInstanceof(assoc.getClassName(), assoc.get_instanceof());
        }
    }
    
    
    private void validateInstanceof(String parentClassName, String instanceClassName) throws DomainValidationException {
        // validate instanceof (must be a subclass of the association's named class)
        boolean validInstanceof = false;
        // verify the association's named class is a superclass of the instanceof
        String[] superclasses = getSuperclassNames(instanceClassName);
        for (String sup : superclasses) {
            if (sup.equals(parentClassName)) {
                validInstanceof = true;
                break;
            }
        }
        if (!validInstanceof) {
            throw new DomainValidationException(
                instanceClassName + " is not a subclass of " + parentClassName);
        }
    }
    
    
    private boolean associationExists(String sourceClassName, String targetClassName, String roleName) {
        Set<SimplifiedUmlAssociation> associations = getAllAssociationsInvolvingClass(sourceClassName);
        boolean associationFound = false;
        for (SimplifiedUmlAssociation association : associations) {
            // verify both ends of the association are right
            // starting with source to target
            if (sourceClassName.equals(association.getSourceClass()) &&
                targetClassName.equals(association.getTargetClass())) {
                // ensure the role name matches
                associationFound = association.getTargetRoleName().equals(roleName);
            }
            if (association.isBidirectional() && !associationFound) {
                // if bidirectional and we've not already found the association, try the reverse
                if (targetClassName.equals(association.getSourceClass()) &&
                    sourceClassName.equals(association.getTargetClass())) {
                    associationFound = association.getSourceRoleName().equals(roleName);
                }
            }
            if (associationFound) {
                break;
            }
        }
        return associationFound;
    }


    private void validateGroupModel(CQLObject current, CQLGroup group) throws DomainValidationException {
        if (group.getCQLAttribute() != null) {
            UMLClass classMd = getUmlClass(current.getClassName());
            for (int i = 0; i < group.getCQLAttribute().length; i++) {
                validateAttributeModel(group.getCQLAttribute(i), classMd);
            }
        }

        if (group.getCQLAssociatedObject() != null) {
            for (int i = 0; i < group.getCQLAssociatedObject().length; i++) {
                validateAssociationModel(current, group.getCQLAssociatedObject(i));
            }
        }

        if (group.getCQLGroup() != null) {
            for (int i = 0; i < group.getCQLGroup().length; i++) {
                validateGroupModel(current, group.getCQLGroup(i));
            }
        }
    }


    private UMLClass getUmlClass(String className) {
        UMLClass[] allClasses = model.getExposedUMLClassCollection().getUMLClass();

        for (int i = 0; allClasses != null && i < allClasses.length; i++) {
            String fqn = allClasses[i].getPackageName().trim();
            if (!fqn.equals("")) {
                fqn += "." + allClasses[i].getClassName();
            }

            if (fqn.equals(className)) {
                return allClasses[i];
            }
        }
        return null;
    }


    private UMLAttribute getUmlAttribute(String attribName, UMLClass classMd) {
        UMLAttribute[] attribs = classMd.getUmlAttributeCollection().getUMLAttribute();
        for (int i = 0; attribs != null && i < attribs.length; i++) {
            UMLAttribute attrib = attribs[i];
            String fullAttribName = attrib.getName();
            int shortIndex = fullAttribName.indexOf(':');
            String shortAttribName = fullAttribName.substring(shortIndex + 1);
            if (shortAttribName.equals(attribName)) {
                return attrib;
            }
        }
        return null;
    }
    
    
    private Set<SimplifiedUmlAssociation> getAllAssociationsInvolvingClass(String involvedClass) {
        String[] searchClassNames = getSuperclassNames(involvedClass);
        Set<SimplifiedUmlAssociation> associations = new HashSet<SimplifiedUmlAssociation>();
        for (String className : searchClassNames) {
            associations.addAll(getUmlAssociations(className));
        }
        return associations;
    }


    private String[] getSuperclassNames(String className) {
        UMLClass[] superclasses = DomainModelUtils.getAllSuperclasses(model, className);
        String[] names = new String[superclasses.length + 1];
        for (int i = 0; i < superclasses.length; i++) {
            names[i] = superclasses[i].getPackageName() + "." + superclasses[i].getClassName();
        }
        names[names.length - 1] = className;
        return names;
    }


    private List<SimplifiedUmlAssociation> getUmlAssociations(String testClass) {
        List<SimplifiedUmlAssociation> associations = new ArrayList<SimplifiedUmlAssociation>();
        if (model.getExposedUMLAssociationCollection() != null
            && model.getExposedUMLAssociationCollection().getUMLAssociation() != null) {
            for (UMLAssociation assoc : model.getExposedUMLAssociationCollection().getUMLAssociation()) {
                UMLClass sourceClassReference = DomainModelUtils.getReferencedUMLClass(model, 
                    assoc.getSourceUMLAssociationEdge().getUMLAssociationEdge().getUMLClassReference());
                UMLClass targetClassReference = DomainModelUtils.getReferencedUMLClass(model, 
                    assoc.getTargetUMLAssociationEdge().getUMLAssociationEdge().getUMLClassReference());
                if (sourceClassReference != null && targetClassReference != null) {
                    String sourceClassName = sourceClassReference.getPackageName() + "." + sourceClassReference.getClassName();
                    String targetClassName = targetClassReference.getPackageName() + "." + targetClassReference.getClassName();
                    if (testClass.equals(sourceClassName) || testClass.equals(targetClassName)) {
                        SimplifiedUmlAssociation simple = new SimplifiedUmlAssociation(
                            sourceClassName, targetClassName, 
                            assoc.getSourceUMLAssociationEdge().getUMLAssociationEdge().getRoleName(),
                            assoc.getTargetUMLAssociationEdge().getUMLAssociationEdge().getRoleName(),
                            assoc.isBidirectional());
                        associations.add(simple);
                    }
                }
            }
        }
        return associations;
    }
    
    
    private static class SimplifiedUmlAssociation {
        private String sourceClass;
        private String targetClass;
        private String sourceRoleName;
        private String targetRoleName;
        private boolean bidirectional;
        
        public SimplifiedUmlAssociation(String sourceClass, String targetClass, 
            String sourceRoleName, String targetRoleName, boolean bidirectional) {
            super();
            this.sourceClass = sourceClass;
            this.targetClass = targetClass;
            this.sourceRoleName = sourceRoleName;
            this.targetRoleName = targetRoleName;
            this.bidirectional = bidirectional;
        }

        
        public boolean isBidirectional() {
            return bidirectional;
        }
        

        public String getSourceClass() {
            return sourceClass;
        }

        
        public String getSourceRoleName() {
            return sourceRoleName;
        }
        

        public String getTargetClass() {
            return targetClass;
        }
        

        public String getTargetRoleName() {
            return targetRoleName;
        }
        
        
        public String toString() {
            StringBuffer buff = new StringBuffer();
            buff.append("source class: ").append(sourceClass).append(" via ").append(sourceRoleName).append("\n");
            buff.append("target class: ").append(targetClass).append(" via ").append(targetRoleName).append("\n");
            buff.append("bidirectional = ").append(bidirectional);
            return buff.toString();
        }
        
        
        public int hashCode() {
            return toString().hashCode();
        }
        
        
        public boolean equals(java.lang.Object o) {
            if (o != null && o instanceof SimplifiedUmlAssociation) {
                return toString().equals(o.toString());
            }
            return false;
        }
    }
}
