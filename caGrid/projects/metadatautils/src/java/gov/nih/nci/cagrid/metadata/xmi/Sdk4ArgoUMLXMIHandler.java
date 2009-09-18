package gov.nih.nci.cagrid.metadata.xmi;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.metadata.common.UMLAttribute;
import gov.nih.nci.cagrid.metadata.common.UMLClass;
import gov.nih.nci.cagrid.metadata.common.UMLClassUmlAttributeCollection;
import gov.nih.nci.cagrid.metadata.dataservice.UMLAssociation;
import gov.nih.nci.cagrid.metadata.dataservice.UMLAssociationEdge;
import gov.nih.nci.cagrid.metadata.dataservice.UMLAssociationSourceUMLAssociationEdge;
import gov.nih.nci.cagrid.metadata.dataservice.UMLAssociationTargetUMLAssociationEdge;
import gov.nih.nci.cagrid.metadata.dataservice.UMLClassReference;
import gov.nih.nci.cagrid.metadata.dataservice.UMLGeneralization;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
  *  Sdk4ArgoUMLXMIHandler
  *  SAX handler for SDK 4.0 Argo XMI -> Domain Model
  * 
  * @author David Ervin
  * 
  * @created Oct 22, 2007 10:26:25 AM
  * @version $Id: Sdk4ArgoUMLXMIHandler.java,v 1.6 2008-04-28 18:10:00 dervin Exp $
 */
class Sdk4ArgoUMLXMIHandler extends BaseXMIHandler {
    private static final Log LOG = LogFactory.getLog(Sdk4ArgoUMLXMIHandler.class);

    // some state variables
    private int currentNodeDepth;
    private String currentPackageName;
    private boolean handlingGeneralization;
    private boolean handlingChildGeneralization;
    private boolean handlingParentGeneralization;
    private int currentGeneralizationNodeDepth;
    private int currentClassNodeDepth;
    private boolean handlingClass;
    private boolean handlingAttribute;
    private boolean handlingAssociation;
    private boolean associationSourceIsNavigable;
    private boolean associationTargetIsNavigable;
    private boolean associationSourceMultiplicitySet;
    private boolean associationSourceParticipantSet;
    
    public Sdk4ArgoUMLXMIHandler(XMIParser parser) {
        super(parser);
        // initialize state
        currentNodeDepth = 0;
        currentPackageName = "";
        handlingGeneralization = false;
        handlingChildGeneralization = false;
        handlingParentGeneralization = false;
        currentGeneralizationNodeDepth = 0;
        currentClassNodeDepth = 0;
        handlingClass = false;
        handlingAttribute = false;
        handlingAssociation = false;
    }
    
    
    public void startElement(
        String uri, String localName, String qName, Attributes atts) throws SAXException {
        currentNodeDepth++;
        // clean out the character buffer
        clearChars();
        
        // start handling elements by name
        if (qName.equals(XMIConstants.XMI_UML_PACKAGE)) {
            handlePackage(atts);
        } else if (insideValidPackage()) {
            if (qName.equals(XMIConstants.XMI_UML_CLASS)) {
                handleClass(atts);
            } else if (qName.startsWith(XMIConstants.XMI_UML_GENERALIZATION)) {
                handleGeneralization(atts);
                if (qName.endsWith(XMIConstants.XMI_UML_GENERALIZATION_CHILD)) {
                    handlingChildGeneralization = true;
                } else if (qName.endsWith(XMIConstants.XMI_UML_GENERALIZATION_PARENT)) {
                    handlingParentGeneralization = true;
                }
            } else if (qName.equals(XMIConstants.XMI_UML_ATTRIBUTE)) {
                handleAttribute(atts);
            } else if (qName.equals(XMIConstants.XMI_UML_ASSOCIATION)) {
                handleAssociation();
            } else if (qName.equals(XMIConstants.XMI_UML_ASSOCIATION_END) && handlingAssociation) {
                handleAssociationEnd(atts);
            } else if (qName.equals(XMIConstants.XMI_UML_MULTIPLICITY_RANGE) && handlingAssociation) {
                handleMultiplicityRange(atts);
            } else if (qName.equals(Sdk4ArgoUMLXMIConstants.UML_TYPE_FEATURE)) {
                // iff handling association
            }
        }
    }
    
    
    public void endElement(String uri, String localName, String qName) throws SAXException {
        // handle special element closures
        if (qName.equals(XMIConstants.XMI_UML_PACKAGE)) {
            handlePackageEnd();
        } else if (insideValidPackage()) {
            if (handlingGeneralization && 
                qName.equals(Sdk4ArgoUMLXMIConstants.XMI_UML_GENERALIZATION_CHILD)) {
                handlingChildGeneralization = false;
            } else if (handlingGeneralization &&
                qName.equals(Sdk4ArgoUMLXMIConstants.XMI_UML_GENERALIZATION_PARENT)) {
                handlingParentGeneralization = false;
            } else if (handlingGeneralization &&
                qName.equals(XMIConstants.XMI_UML_GENERALIZATION)) {
                if (currentNodeDepth == currentGeneralizationNodeDepth) {
                    LOG.debug("Done with a generalization element");
                    handlingGeneralization = false;
                }
            } else if (qName.equals(XMIConstants.XMI_UML_CLASS)) {
                if (currentNodeDepth == currentClassNodeDepth) {
                    handlingClass = false;
                    LOG.debug("CLASS ENDED");
                }
            } else if (qName.equals(XMIConstants.XMI_UML_ASSOCIATION)) {
                LOG.debug("ASSOCIATION ENDED");
                UMLAssociation currentAssociation = getLastAssociation();
                currentAssociation.setBidirectional(associationSourceIsNavigable && associationTargetIsNavigable);
                handlingAssociation = false;
            } else if (qName.equals(XMIConstants.XMI_UML_ATTRIBUTE)) {
                handlingAttribute = false;
                LOG.debug("ATTRIBUTE ENDED");
            }
        }
        
        // clean out the char buffer
        clearChars();
        currentNodeDepth--;
    }
    
    
    private boolean insideValidPackage() {
        // ignore everything that isn't part of the logical model
        String expectedPrefix = 
            Sdk4ArgoUMLXMIConstants.LOGICAL_VIEW_PACKAGE_NAME + "." +
            Sdk4ArgoUMLXMIConstants.LOGICAL_MODEL_PACKAGE_NAME;
        return currentPackageName.startsWith(expectedPrefix);
    }
    
    
    private String getTrimmedPackageName() {
        String expectedPrefix = 
            Sdk4ArgoUMLXMIConstants.LOGICAL_VIEW_PACKAGE_NAME + "." +
            Sdk4ArgoUMLXMIConstants.LOGICAL_MODEL_PACKAGE_NAME;
        String trimmedPackageName = currentPackageName.substring(expectedPrefix.length());
        if (trimmedPackageName.startsWith(".")) {
            trimmedPackageName = trimmedPackageName.substring(1);
        }
        return trimmedPackageName;
    }

    
    private void handlePackage(Attributes attribs) {
        String namePart = attribs.getValue(XMIConstants.XMI_NAME_ATTRIBUTE);
        if (!currentPackageName.equals("")) {
            currentPackageName += ".";
        }
        currentPackageName += namePart;
    }
    
    
    private void handlePackageEnd() {
        int index = currentPackageName.lastIndexOf('.');
        if (index == -1) {
            currentPackageName = "";
        } else {
            currentPackageName = currentPackageName.substring(0, index);
        }
    }
    
    
    private void handleClass(Attributes atts) {
        if (handlingAttribute) {
            handleAttributeTypeClassRef(atts);
        } else if (handlingAssociation) {
            handleAssociationParticipantClass(atts);
        } else if (handlingGeneralization) {
            handleGeneralizationClassRef(atts);
        } else if (!handlingClass) {
            // if not already handling a class, start a new one
            // clean up the package name
            String trimmedPackageName = getTrimmedPackageName();
            handleNewClass(atts, trimmedPackageName);
        }
    }
    
    
    private void handleNewClass(Attributes atts, String packageName) {
        // create a new class
        handlingClass = true;
        currentClassNodeDepth = currentNodeDepth;
        UMLClass clazz = new UMLClass();
        clazz.setClassName(atts.getValue(XMIConstants.XMI_NAME_ATTRIBUTE));
        clazz.setId(atts.getValue(XMIConstants.XMI_ID_ATTRIBUTE));
        clazz.setPackageName(packageName);
        // project name and version from parser
        clazz.setProjectName(getParser().getProjectShortName());
        clazz.setProjectVersion(getParser().getProjectVersion());
        clazz.setUmlAttributeCollection(new UMLClassUmlAttributeCollection());
        LOG.debug("Created new class " + clazz.getPackageName() + "." + clazz.getClassName());
        // add the class to the list
        addClass(clazz);
    }
    
    
    private void handleGeneralizationClassRef(Attributes atts) {
        UMLGeneralization currentGeneralization = getLastGeneralization();
        if (handlingChildGeneralization) {
            LOG.debug("Handling subclass generalization");
            currentGeneralization.setSubClassReference(
                new UMLClassReference(atts.getValue(XMIConstants.XMI_IDREF)));
        } else if (handlingParentGeneralization) {
            LOG.debug("Handling superclass generalization");
            currentGeneralization.setSuperClassReference(
                new UMLClassReference(atts.getValue(XMIConstants.XMI_IDREF)));            
        }
    }
    
    
    private void handleAttributeTypeClassRef(Attributes atts) {
        // set the attribute's data type name to the class ref
        // it will be replaced later with the correct value from a mapping table
        String refid = atts.getValue(XMIConstants.XMI_IDREF);
        UMLAttribute lastAttribute = getLastAttribute();
        lastAttribute.setDataTypeName(refid);
    }
    
    
    private void handleAttribute(Attributes atts) {
        if (handlingClass) {
            handlingAttribute = true;
            UMLAttribute attrib = new UMLAttribute();
            attrib.setName(atts.getValue(XMIConstants.XMI_NAME_ATTRIBUTE));
            attrib.setVersion(getParser().getAttributeVersion());
            String idValue = atts.getValue(XMIConstants.XMI_ID_ATTRIBUTE);
            attrib.setPublicID(idValue.hashCode());
            addAttribute(attrib);
            // attach the attribute to the most recent class
            UMLClass lastClass = getLastClass();
            UMLClassUmlAttributeCollection attribCollection = lastClass.getUmlAttributeCollection();
            UMLAttribute[] currentAttributes = attribCollection.getUMLAttribute();
            if (currentAttributes == null) {
                currentAttributes = new UMLAttribute[0];
            }
            currentAttributes = (UMLAttribute[]) Utils.appendToArray(currentAttributes, attrib);
            attribCollection.setUMLAttribute(currentAttributes);
            LOG.debug("Handled attribute " + attrib.getName() + " of class " + lastClass.getPackageName() + "." + lastClass.getClassName());
        }
    }
    
    
    private void handleGeneralization(Attributes atts) {
        // verify this is a generalization we need to handle
        if (atts.getValue(XMIConstants.XMI_ID_ATTRIBUTE) != null) {
            LOG.debug("Started new generalization");
            handlingGeneralization = true;
            currentGeneralizationNodeDepth = currentNodeDepth;
            UMLGeneralization gen = new UMLGeneralization();
            addGeneralization(gen);
        }
    }
    
    
    private void handleAssociation() {
        LOG.debug("HANDLING ASSOCIATION");
        UMLAssociation association = new UMLAssociation();
        addAssociation(association);
        handlingAssociation = true;
        associationSourceMultiplicitySet = false;
        associationSourceParticipantSet = false;
    }
    
    
    private void handleAssociationEnd(Attributes atts) {
        LOG.debug("Handling association edge");
        UMLAssociation currentAssociation = getLastAssociation();
        // create the new edge
        UMLAssociationEdge edge = new UMLAssociationEdge();
        String roleName = atts.getValue(XMIConstants.XMI_NAME_ATTRIBUTE);
        boolean isNavigable = Boolean.valueOf(
            atts.getValue(XMIConstants.XMI_UML_ASSOCIATION_IS_NAVIGABLE)).booleanValue();
        edge.setRoleName(roleName);
        
        // determine if its source or target
        if (currentAssociation.getSourceUMLAssociationEdge() == null) {
            UMLAssociationSourceUMLAssociationEdge sourceEdge = 
                new UMLAssociationSourceUMLAssociationEdge();
            sourceEdge.setUMLAssociationEdge(edge);
            currentAssociation.setSourceUMLAssociationEdge(sourceEdge);
            associationSourceIsNavigable = isNavigable;
            LOG.debug("Added source edge");
        } else {
            UMLAssociationTargetUMLAssociationEdge targetEdge = 
                new UMLAssociationTargetUMLAssociationEdge();
            targetEdge.setUMLAssociationEdge(edge);
            currentAssociation.setTargetUMLAssociationEdge(targetEdge);
            associationTargetIsNavigable = isNavigable;
            LOG.debug("Added target edge");
        }
    }
    
    
    private void handleMultiplicityRange(Attributes atts) {
        LOG.debug("Handling association multiplicity");
        UMLAssociation currentAssociation = getLastAssociation();
        UMLAssociationEdge edge = null;
        if (!associationSourceMultiplicitySet) {
            // source edge
            edge = currentAssociation.getSourceUMLAssociationEdge().getUMLAssociationEdge();
            LOG.debug("\t... to source edge");
            associationSourceMultiplicitySet = true;
        } else {
            edge = currentAssociation.getTargetUMLAssociationEdge().getUMLAssociationEdge();
            LOG.debug("\t... to target edge");
        }
        int min = Integer.parseInt(atts.getValue(XMIConstants.XMI_UML_MULTIPLICITY_LOWER));
        int max = Integer.parseInt(atts.getValue(XMIConstants.XMI_UML_MULTIPLICITY_UPPER));
        edge.setMinCardinality(min);
        edge.setMaxCardinality(max);
    }
    
    
    private void handleAssociationParticipantClass(Attributes atts) {
        LOG.debug("Handling association participant");
        UMLAssociation currentAssociation = getLastAssociation();
        UMLAssociationEdge edge = null;
        if (!associationSourceParticipantSet) {
            // source edge
            edge = currentAssociation.getSourceUMLAssociationEdge().getUMLAssociationEdge();
            LOG.debug("\t... to source edge");
            associationSourceParticipantSet = true;
        } else {
            edge = currentAssociation.getTargetUMLAssociationEdge().getUMLAssociationEdge();
            LOG.debug("\t... to target edge");
        }
        String refid = atts.getValue(XMIConstants.XMI_IDREF);
        edge.setUMLClassReference(new UMLClassReference(refid));
    }
}