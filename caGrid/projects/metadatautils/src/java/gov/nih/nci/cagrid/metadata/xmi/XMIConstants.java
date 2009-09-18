package gov.nih.nci.cagrid.metadata.xmi;


/** 
 *  XMIConstants
 * 
 * @author David Ervin
 * 
 * @created Oct 22, 2007 10:28:52 AM
 * @version $Id: XMIConstants.java,v 1.6 2009/06/11 19:01:44 dervin Exp $ 
 */
public class XMIConstants {
    // common? constants
    public static final String XMI_NAME_ATTRIBUTE = "name"; // for names of elements
    public static final String XMI_TYPE_ATTRIBUTE = "type"; // types of elements
    public static final String XMI_ID_ATTRIBUTE = "xmi.id"; // for xmi component ids
    public static final String XMI_IDREF = "xmi.idref"; // for id refs
    
    // association constants
    public static final String XMI_UML_ASSOCIATION = "UML:Association";
    public static final String XMI_UML_ASSOCIATION_END = "UML:AssociationEnd";
    public static final String XMI_UML_ASSOCIATION_IS_NAVIGABLE = "isNavigable";    
    
    // class constants
    public static final String XMI_UML_CLASS = "UML:Class";
    
    // package constants
    public static final String XMI_UML_PACKAGE = "UML:Package";    
    public static final String XMI_LOGICAL_MODEL = "Logical Model";
    public static final String XMI_LOGICAL_VIEW = "Logical View";
    public static final String XMI_DATA_MODEL = "Data Model";
    
    // attribute constants
    public static final String XMI_UML_ATTRIBUTE = "UML:Attribute";
    
    // multiplicity (cardinality) constants
    public static final String XMI_UML_MULTIPLICITY_RANGE = "UML:MultiplicityRange";
    public static final String XMI_UML_MULTIPLICITY_LOWER = "lower";
    public static final String XMI_UML_MULTIPLICITY_UPPER = "upper";
    
    // generalization (inheritance) constants
    public static final String XMI_UML_GENERALIZATION = "UML:Generalization";
    public static final String XMI_UML_GENERALIZATION_CHILD = "child";
    public static final String XMI_UML_GENERALIZATION_PARENT = "parent";
    // fallbacks for 'newer' versions of XMI
    public static final String XMI_UML_GENERALIZATION_SUBTYPE = "subtype";
    public static final String XMI_UML_GENERALIZATION_SUPERTYPE = "supertype";
    
    // tagged value constants
    public static final String XMI_UML_TAGGED_VALUE = "UML:TaggedValue";
    public static final String XMI_UML_TAGGED_VALUE_TAG = "tag";
    public static final String XMI_UML_TAGGED_VALUE_MODEL_ELEMENT = "modelElement";
    public static final String XMI_UML_TAGGED_VALUE_VALUE = "value";
    
    // cadsr ID tag values
    public static final String XMI_TAG_CADSR_DE_ID = "CADSR_DE_ID";
    public static final String XMI_TAG_CADSR_DE_VERSION = "CADSR_DE_VERSION";
    
    // tag values
    public static final String XMI_TAG_PROPERTY = "Property";
    public static final String XMI_TAG_DESCRIPTION = "description";
    public static final String XMI_TAG_OBJECT_CLASS_CONCEPT_CODE = "ObjectClassConceptCode";
    public static final String XMI_TAG_OBJECT_CLASS_QUALIFIER_CONCEPT_CODE = "ObjectClassQualifierConceptCode";
    public static final String XMI_TAG_PROPERTY_CONCEPT_CODE = "PropertyConceptCode";
    public static final String XMI_TAG_PROPERTY_QUALIFIER_CONCEPT_CODE = "PropertyQualifierConceptCode";
    public static final String XMI_TAG_OBJECT_CLASS_CONCEPT_PREFERRED_NAME = "ObjectClassConceptPreferredName";
    public static final String XMI_TAG_OBJECT_CLASS_QUALIFIER_CONCEPT_PREFERRED_NAME = "ObjectClassQualifierConceptPreferredName";
    public static final String XMI_TAG_PROPERTY_CONCEPT_PREFERRED_NAME = "PropertyConceptPreferredName";
    public static final String XMI_TAG_PROPERTY_QUALIFIER_CONCEPT_PREFERRED_NAME = "PropertyQualifierConceptPreferredName";
    public static final String XMI_TAG_OBJECT_CLASS_CONCEPT_DEFINITION = "ObjectClassConceptDefinition";
    public static final String XMI_TAG_OBJECT_CLASS_CONCEPT_DEFINITION_SOURCE = "ObjectClassConceptDefinitionSource";
    public static final String XMI_TAG_OBJECT_CLASS_QUALIFIER_CONCEPT_DEFINITION = "ObjectClassQualifierConceptDefinition";
    public static final String XMI_TAG_OBJECT_CLASS_QUALIFIER_CONCEPT_DEFINITION_SOURCE = "ObjectClassQualifierConceptDefinitionSource";
    public static final String XMI_TAG_PROPERTY_CONCEPT_DEFINITION = "PropertyConceptDefinition";
    public static final String XMI_TAG_PROPERTY_CONCEPT_DEFINITION_SOURCE = "PropertyConceptDefinitionSource";
    public static final String XMI_TAG_PROPERTY_QUALIFIER_CONCEPT_DEFINITION = "PropertyQualifierConceptDefinition";
    public static final String XMI_TAG_PROPERTY_QUALIFIER_CONCEPT_DEFINITION_SOURCE = "PropertyQualifierConceptDefinitionSource";
    public static final String XMI_TAG_CONCEPT_CODE = "ConceptCode";
    public static final String XMI_TAG_PREFERRED_NAME = "PreferredName";
    public static final String XMI_TAG_CONCEPT_DEFINITION = "ConceptDefinition"; 
    
    // data type constants
    public static final String XMI_UML_DATA_TYPE = "UML:DataType";
    
    // ?
    public static final String XMI_FOUNDATION_CORE_CLASSIFIER = "Foundation.Core.Classifier";

    private XMIConstants() {
        // no instantiation
    }
}
