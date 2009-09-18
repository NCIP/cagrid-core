package gov.nih.nci.cagrid.metadata.xmi;

import gov.nih.nci.cagrid.metadata.common.SemanticMetadata;
import gov.nih.nci.cagrid.metadata.common.UMLAttribute;
import gov.nih.nci.cagrid.metadata.common.UMLClass;
import gov.nih.nci.cagrid.metadata.common.UMLClassUmlAttributeCollection;
import gov.nih.nci.cagrid.metadata.dataservice.UMLAssociation;
import gov.nih.nci.cagrid.metadata.dataservice.UMLAssociationEdge;
import gov.nih.nci.cagrid.metadata.dataservice.UMLAssociationSourceUMLAssociationEdge;
import gov.nih.nci.cagrid.metadata.dataservice.UMLAssociationTargetUMLAssociationEdge;
import gov.nih.nci.cagrid.metadata.dataservice.UMLClassReference;
import gov.nih.nci.cagrid.metadata.dataservice.UMLGeneralization;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
  *  XMIHandler
  *  SAX handler for caCORE SDK 3.2 EA XMI -> Domain Model
  * 
  * @author Patrick McConnell
  * @author David Ervin
  * 
  * @created Oct 22, 2007 10:26:25 AM
  * @version $Id: XMIHandler.java,v 1.12 2008-04-24 19:58:14 dervin Exp $
 */
class XMIHandler extends BaseXMIHandler {
    private static final Log LOG = LogFactory.getLog(XMIHandler.class);   
    
    // state variables
    private UMLAssociationEdge edge;
    private boolean sourceNavigable = false;
    private boolean targetNavigable = false;
    private String pkg;
    private boolean handlingAttribute;

    public XMIHandler(XMIParser parser) {
        super(parser);
        pkg = "";
        handlingAttribute = false;
    }


    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equals(XMIConstants.XMI_UML_PACKAGE)) {
            int index = pkg.lastIndexOf('.');
            if (index == -1) {
                pkg = "";
            } else {
                pkg = pkg.substring(0, index);
            }
        } else if (qName.equals(XMIConstants.XMI_UML_CLASS)) {
            UMLClass cl = getLastClass();
            cl.setUmlAttributeCollection(
                new UMLClassUmlAttributeCollection(getAttributes()));
            clearAttributeList();
            handlingAttribute = false;
        } else if (qName.equals(XMIConstants.XMI_UML_ASSOCIATION)) {
            UMLAssociation assoc = getLastAssociation();
            if (sourceNavigable && !targetNavigable) {
                UMLAssociationEdge assocEdge = assoc.getSourceUMLAssociationEdge().getUMLAssociationEdge();
                assoc.getSourceUMLAssociationEdge().setUMLAssociationEdge(
                    assoc.getTargetUMLAssociationEdge().getUMLAssociationEdge());
                assoc.getTargetUMLAssociationEdge().setUMLAssociationEdge(assocEdge);
            }
            assoc.setBidirectional(sourceNavigable && targetNavigable);
        }

        clearChars();
    }


    @Override
    public void startElement(
        String uri, String localName, String qName, Attributes atts) throws SAXException {
        clearChars();

        if (qName.equals(XMIConstants.XMI_UML_PACKAGE)) {
            handlePackage(atts);
        } else if (qName.equals(XMIConstants.XMI_UML_CLASS)) {
            handleClass(atts);
        } else if (qName.equals(XMIConstants.XMI_UML_ATTRIBUTE)) {
            handleAttribute(atts);
        } else if (qName.equals(XMIConstants.XMI_UML_ASSOCIATION)) {
            // start the new association
            UMLAssociation ass = new UMLAssociation();
            addAssociation(ass);
        } else if (qName.equals(XMIConstants.XMI_UML_ASSOCIATION_END)) {
            handleAssociationEnd(atts);
        } else if (qName.equals(XMIConstants.XMI_UML_MULTIPLICITY_RANGE) && edge != null) {
            handleMultiplicity(atts);
        } else if (qName.equals(XMIConstants.XMI_UML_GENERALIZATION)) {
            handleGeneralization(atts);
        } else if (qName.equals(XMIConstants.XMI_UML_TAGGED_VALUE)) {
            handleTag(atts);
        } else if (qName.equals(XMIConstants.XMI_UML_DATA_TYPE)) {
            handleDataType(atts);
        } else if (qName.equals(XMIConstants.XMI_FOUNDATION_CORE_CLASSIFIER)) {
            if (!handlingAttribute) {
                LOG.info("Ignoring " + XMIConstants.XMI_FOUNDATION_CORE_CLASSIFIER);
            } else {
                getLastAttribute().setDataTypeName(atts.getValue(XMIConstants.XMI_IDREF));
            }
        }
    }
    
    
    // ------------------
    // XMI type handlers
    // ------------------
    
    
    private void handleDataType(Attributes atts) {
        getTypeTable().put(atts.getValue(XMIConstants.XMI_ID_ATTRIBUTE), 
            atts.getValue(XMIConstants.XMI_NAME_ATTRIBUTE));
    }
    
    
    private void handleGeneralization(Attributes atts) {
        UMLGeneralization gen = new UMLGeneralization();
        String subId = atts.getValue(XMIConstants.XMI_UML_GENERALIZATION_CHILD);
        String superId = atts.getValue(XMIConstants.XMI_UML_GENERALIZATION_PARENT);
        if (subId == null) {
            subId = atts.getValue(XMIConstants.XMI_UML_GENERALIZATION_SUBTYPE);
        }
        if (superId == null) {
            superId = atts.getValue(XMIConstants.XMI_UML_GENERALIZATION_SUPERTYPE);
        }
        gen.setSubClassReference(new UMLClassReference(subId));
        gen.setSuperClassReference(new UMLClassReference(superId));
        addGeneralization(gen);
    }
    
    
    private void handleMultiplicity(Attributes atts) {
        edge.setMinCardinality(Integer.parseInt(
            atts.getValue(XMIConstants.XMI_UML_MULTIPLICITY_LOWER)));
        edge.setMaxCardinality(Integer.parseInt(
            atts.getValue(XMIConstants.XMI_UML_MULTIPLICITY_UPPER)));
    }
    
    
    private void handleAssociationEnd(Attributes atts) {
        // get the most recently found association
        UMLAssociation assoc = getLastAssociation();
        // TODO: something with type?
        String type = atts.getValue(XMIConstants.XMI_TYPE_ATTRIBUTE);
        boolean isNavigable = "true".equals(atts.getValue(XMIConstants.XMI_UML_ASSOCIATION_IS_NAVIGABLE));

        edge = new UMLAssociationEdge();
        if (assoc.getSourceUMLAssociationEdge() == null) {
            assoc.setSourceUMLAssociationEdge(new UMLAssociationSourceUMLAssociationEdge(edge));
            sourceNavigable = isNavigable;
        } else {
            assoc.setTargetUMLAssociationEdge(new UMLAssociationTargetUMLAssociationEdge(edge));
            targetNavigable = isNavigable;
        }
        edge.setRoleName(atts.getValue(XMIConstants.XMI_NAME_ATTRIBUTE));
        edge.setUMLClassReference(new UMLClassReference(atts.getValue(XMIConstants.XMI_TYPE_ATTRIBUTE)));
    }
    
    
    private void handleAttribute(Attributes atts) {
        handlingAttribute = true;
        UMLAttribute att = new UMLAttribute();
        att.setName(atts.getValue(XMIConstants.XMI_NAME_ATTRIBUTE));
        att.setPublicID(atts.getValue(XMIConstants.XMI_ID_ATTRIBUTE).hashCode());
        att.setVersion(getParser().attributeVersion);
        addAttribute(att);
    }
    
    
    private void handleClass(Attributes atts) {
        UMLClass cl = new UMLClass();
        cl.setClassName(atts.getValue(XMIConstants.XMI_NAME_ATTRIBUTE));
        cl.setId(atts.getValue(XMIConstants.XMI_ID_ATTRIBUTE));
        cl.setPackageName(pkg);
        cl.setProjectName(getParser().projectShortName);
        cl.setProjectVersion(getParser().projectVersion);
        addClass(cl);
    }
    
    
    private void handlePackage(Attributes atts) {
        String name = atts.getValue(XMIConstants.XMI_NAME_ATTRIBUTE);
        if (!name.equals(XMIConstants.XMI_LOGICAL_VIEW) 
            && !name.equals(XMIConstants.XMI_LOGICAL_MODEL)
            && !name.equals(XMIConstants.XMI_DATA_MODEL)) {
            if (!pkg.equals("")) {
                pkg += ".";
            }
            pkg += atts.getValue(XMIConstants.XMI_NAME_ATTRIBUTE);
        }
    }
    
    
    private void handleTag(Attributes atts) {
        String tag = atts.getValue(XMIConstants.XMI_UML_TAGGED_VALUE_TAG);
        String modelElement = atts.getValue(XMIConstants.XMI_UML_TAGGED_VALUE_MODEL_ELEMENT);
        String value = atts.getValue(XMIConstants.XMI_UML_TAGGED_VALUE_VALUE);

        LOG.debug(tag + " on " + modelElement);            
        if (tag.startsWith(XMIConstants.XMI_TAG_PROPERTY)) {
            modelElement = String.valueOf(modelElement.hashCode());
            LOG.debug(" (" + modelElement + ")");
        }
        LOG.debug(" = " + value);

        if (tag.equals(XMIConstants.XMI_TAG_DESCRIPTION)) {
            UMLClass refedClass = getClassById(modelElement);
            UMLAttribute refedAttribute = getAttributeById(modelElement);
            if (refedClass != null) {
                refedClass.setDescription(value);
            } else if (refedAttribute != null) {
                refedAttribute.setDescription(value);
            }
        } else if (tag.startsWith(XMIConstants.XMI_TAG_OBJECT_CLASS_CONCEPT_CODE)
            || tag.startsWith(XMIConstants.XMI_TAG_OBJECT_CLASS_QUALIFIER_CONCEPT_CODE) 
            || tag.startsWith(XMIConstants.XMI_TAG_PROPERTY_CONCEPT_CODE)
            || tag.startsWith(XMIConstants.XMI_TAG_PROPERTY_QUALIFIER_CONCEPT_CODE)) {
            addSemanticMetadata(tag, modelElement, value);
        } else if (tag.startsWith(XMIConstants.XMI_TAG_OBJECT_CLASS_CONCEPT_PREFERRED_NAME)
            || tag.startsWith(XMIConstants.XMI_TAG_OBJECT_CLASS_QUALIFIER_CONCEPT_PREFERRED_NAME)
            || tag.startsWith(XMIConstants.XMI_TAG_PROPERTY_CONCEPT_PREFERRED_NAME)
            || tag.startsWith(XMIConstants.XMI_TAG_PROPERTY_QUALIFIER_CONCEPT_PREFERRED_NAME)) {
            addSemanticMetadata(tag, modelElement, value);
        } else if ((tag.startsWith(XMIConstants.XMI_TAG_OBJECT_CLASS_CONCEPT_DEFINITION) 
                && !tag.startsWith(XMIConstants.XMI_TAG_OBJECT_CLASS_CONCEPT_DEFINITION_SOURCE))
            || (tag.startsWith(XMIConstants.XMI_TAG_OBJECT_CLASS_QUALIFIER_CONCEPT_DEFINITION) 
                && !tag.startsWith(XMIConstants.XMI_TAG_OBJECT_CLASS_QUALIFIER_CONCEPT_DEFINITION_SOURCE))
            || (tag.startsWith(XMIConstants.XMI_TAG_PROPERTY_CONCEPT_DEFINITION) 
                && !tag.startsWith(XMIConstants.XMI_TAG_PROPERTY_CONCEPT_DEFINITION_SOURCE))
            || (tag.startsWith(XMIConstants.XMI_TAG_PROPERTY_QUALIFIER_CONCEPT_DEFINITION) 
                && !tag.startsWith(XMIConstants.XMI_TAG_PROPERTY_QUALIFIER_CONCEPT_DEFINITION_SOURCE))) {
            addSemanticMetadata(tag, modelElement, value);
        }
    }
    
    
    //---------------------
    // general helpers
    //---------------------
    

    private int getSemanticMetadataOrder(String tag) {
        char c = tag.charAt(tag.length() - 1);
        if (Character.isDigit(c)) {
            return Integer.parseInt(String.valueOf(c));
        }
        return 0;
    }


    private void addSemanticMetadata(String tag, String modelElement, String value) {
        int order = getSemanticMetadataOrder(tag);

        List<SemanticMetadata> smList = getSemanticMetadataTable().get(modelElement);
        if (smList == null) {
            getSemanticMetadataTable().put(modelElement, smList = new ArrayList<SemanticMetadata>(9));
        }

        int size = smList.size();
        if (size <= order) {
            for (int i = smList.size(); i <= order; i++) {
                smList.add(new SemanticMetadata());
            }
        }

        SemanticMetadata sm = smList.get(order);
        if (tag.indexOf(XMIConstants.XMI_TAG_CONCEPT_CODE) != -1) {
            sm.setOrder(Integer.valueOf(order));
            sm.setConceptCode(value);
        } else if (tag.indexOf(XMIConstants.XMI_TAG_PREFERRED_NAME) != -1) {
            sm.setConceptName(value);
        } else if (tag.indexOf(XMIConstants.XMI_TAG_CONCEPT_DEFINITION) != -1) {
            sm.setConceptDefinition(value);
        }
    }
}