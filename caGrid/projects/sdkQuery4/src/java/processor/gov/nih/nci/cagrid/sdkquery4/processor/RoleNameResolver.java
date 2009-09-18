package gov.nih.nci.cagrid.sdkquery4.processor;

import gov.nih.nci.cagrid.cqlquery.Association;
import gov.nih.nci.cagrid.data.QueryProcessingException;
import gov.nih.nci.cagrid.data.utilities.DomainModelUtils;
import gov.nih.nci.cagrid.metadata.common.UMLClass;
import gov.nih.nci.cagrid.metadata.dataservice.DomainModel;
import gov.nih.nci.cagrid.metadata.dataservice.UMLAssociation;
import gov.nih.nci.cagrid.metadata.dataservice.UMLClassReference;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** 
 *  RoleNameResolver
 *  Utility for determining role names of associations
 * 
 * @author David Ervin
 * 
 * @created Dec 20, 2007 11:40:22 AM
 * @version $Id: RoleNameResolver.java,v 1.3 2008-04-17 15:26:12 dervin Exp $ 
 */
public class RoleNameResolver {
    private static Log LOG = LogFactory.getLog(RoleNameResolver.class);
    
    private DomainModel domainModel = null;
    private Map<String, String> roleNames = null;
    
    public RoleNameResolver(DomainModel domainModel) {
        this.domainModel = domainModel;
        this.roleNames = new HashMap<String, String>();
    }
    

    /**
     * Gets the role name of an association from the perspective of the parent
     * 
     * @param parentName
     *      The name of the parent class of the association
     * @param assoc
     *      The CQL association for which a role name is to be determined
     * @return
     *      The determined role name
     * @throws QueryProcessingException
     */
    public String getRoleName(String parentName, Association assoc) throws QueryProcessingException {
        String roleKey = generateRoleNameKey(parentName, assoc);
        String roleName = roleNames.get(roleKey);
        if (!roleNames.containsValue(roleKey)) {
            LOG.debug("Role name for " + roleKey + " not found... trying to locate");
            if (assoc.getRoleName() != null) { 
                // role name supplied by association
                LOG.debug("Role name for " + roleKey + " supplied by association");
                roleName = assoc.getRoleName();
                roleNames.put(roleKey, roleName);
            } else { 
                // look up role name in domain model
                LOG.debug("Role name for " + roleKey + " not supplied, checking domain model");
                
                // get associations from the source to the target
                List<UMLAssociation> associations = getUmlAssociations(parentName, assoc.getName());
                
                // verify only ONE association has been found, else ambiguous
                if (associations.size() > 1) {
                    throw new QueryProcessingException("Association from " + parentName 
                        + " to " + assoc.getName() + " is ambiguous without role name specified (" 
                        + associations.size() + " associations found)");
                } else if (associations.size() == 0) {
                    throw new QueryProcessingException("Association from " + parentName 
                        + " to " + assoc.getName() + " was not found in the domain model");
                }
                
                // only one association, so grab the role name
                UMLAssociation association = associations.get(0);
                
                // unidirectional refs need to use the target's role name
                UMLClassReference targetRef = association.getTargetUMLAssociationEdge().getUMLAssociationEdge().getUMLClassReference();
                UMLClass targetClass = DomainModelUtils.getReferencedUMLClass(domainModel, targetRef);
                String targetClassName = targetClass.getClassName();
                if (targetClass.getPackageName() != null && targetClass.getPackageName().length() != 0) {
                    targetClassName = targetClass.getPackageName() + "." + targetClassName;
                }
                if (targetClassName.equals(assoc.getName())) {
                    roleName = association.getTargetUMLAssociationEdge().getUMLAssociationEdge().getRoleName();
                } else if (association.isBidirectional()) {
                    // ok to use source role name
                    roleName = association.getSourceUMLAssociationEdge().getUMLAssociationEdge().getRoleName();
                }
                // role name stays null
            }
            // store the name, even if it's null
            roleNames.put(roleKey, roleName);
        }
        
        return roleName;
    }
    
    
    private List<UMLAssociation> getUmlAssociations(String sourceClassName, String targetClassName) {
        UMLClassReference sourceRef = DomainModelUtils.getClassReference(domainModel, sourceClassName);
        UMLClassReference targetRef = DomainModelUtils.getClassReference(domainModel, targetClassName);
        
        List<UMLAssociation> associations = new LinkedList<UMLAssociation>();
        if (domainModel.getExposedUMLAssociationCollection() != null &&
            domainModel.getExposedUMLAssociationCollection().getUMLAssociation() != null) {
            for (UMLAssociation assoc : domainModel.getExposedUMLAssociationCollection().getUMLAssociation()) {
                UMLClassReference sourceReference = assoc.getSourceUMLAssociationEdge()
                    .getUMLAssociationEdge().getUMLClassReference();
                UMLClassReference targetReference = assoc.getTargetUMLAssociationEdge()
                    .getUMLAssociationEdge().getUMLClassReference();
                String associationSourceRefid = sourceReference.getRefid();
                String associationTargetRefid = targetReference.getRefid();
                if (assoc.isBidirectional()) {
                    // bidirectional associations just need the classes to be involved,
                    // but target / source don't matter
                    if (associationSourceRefid.equals(sourceRef.getRefid()) && associationTargetRefid.equals(targetRef.getRefid()) ||
                        associationTargetRefid.equals(sourceRef.getRefid()) && associationSourceRefid.equals(targetRef.getRefid())) {
                        associations.add(assoc);
                    }
                } else {
                    // source and target must match exactly
                    if (sourceReference.getRefid().equals(sourceRef.getRefid()) &&
                        targetReference.getRefid().equals(targetRef.getRefid())) {
                        associations.add(assoc);
                    }
                }
            }
        }
        return associations;
    }
    
    
    private String generateRoleNameKey(String parentName, Association assoc) {
        return parentName + "->" + assoc.getName() + " [" + assoc.getRoleName() + "]";
    }
}