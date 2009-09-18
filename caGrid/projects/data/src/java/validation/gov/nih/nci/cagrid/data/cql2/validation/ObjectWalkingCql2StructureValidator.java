package gov.nih.nci.cagrid.data.cql2.validation;

import gov.nih.nci.cagrid.cql2.attribute.AttributeValue;
import gov.nih.nci.cagrid.cql2.attribute.BinaryCQLAttribute;
import gov.nih.nci.cagrid.cql2.attribute.CQLAttribute;
import gov.nih.nci.cagrid.cql2.attribute.UnaryCQLAttribute;
import gov.nih.nci.cagrid.cql2.components.CQLAssociatedObject;
import gov.nih.nci.cagrid.cql2.components.CQLGroup;
import gov.nih.nci.cagrid.cql2.components.CQLObject;
import gov.nih.nci.cagrid.cql2.components.CQLQuery;
import gov.nih.nci.cagrid.cql2.components.CQLTargetObject;
import gov.nih.nci.cagrid.cql2.components.GroupLogicalOperator;
import gov.nih.nci.cagrid.cql2.modifiers.CQLQueryModifier;

/**
 * ObjectWalkingCql2StructureValidator
 * Cql2StructureValidator implementation that walks through 
 * the object model to perform validation
 * 
 * @author David
 */
public class ObjectWalkingCql2StructureValidator implements Cql2StructureValidator {
    
    public ObjectWalkingCql2StructureValidator() {
        super();
    }
    

    public void validateQuerySyntax(CQLQuery query) throws StructureValidationException {
        CQLTargetObject target = query.getCQLTargetObject();
        if (target == null) {
            throw new StructureValidationException("No target object was specified");
        }
        validateObjectStructure(target);
        if (query.getCQLQueryModifier() != null) {
            validateQueryModifier(query.getCQLQueryModifier());
        }
    }
    
    
    private void validateObjectStructure(CQLObject obj) throws StructureValidationException {
        if (isEmpty(obj.getClassName())) {
            throw new StructureValidationException("No class name specified on an object definition");
        }
        int populatedChildren = 0;
        if (obj.getBinaryCQLAttribute() != null) {
            populatedChildren++;
            validateAttribute(obj.getBinaryCQLAttribute());
        }
        if (obj.getUnaryCQLAttribute() != null) {
            populatedChildren++;
            validateAttribute(obj.getUnaryCQLAttribute());
        }
        if (obj.getCQLAssociatedObject() != null) {
            populatedChildren++;
            validateAssociation(obj.getCQLAssociatedObject());
        }
        if (obj.getCQLGroup() != null) {
            populatedChildren++;
            validateGroup(obj.getCQLGroup());
        }
        if (populatedChildren > 1) {
            throw new StructureValidationException(
                "A CQL object can have at most one child restriction (found " + populatedChildren + ")");
        }
    }
    
    
    private void validateAssociation(CQLAssociatedObject assoc) throws StructureValidationException {
        if (isEmpty(assoc.getSourceRoleName())) {
            throw new StructureValidationException("No source role name specified on an association definition");
        }
        validateObjectStructure(assoc);
    }
    
    
    private void validateAttribute(CQLAttribute attrib) throws StructureValidationException {
        if (isEmpty(attrib.getName())) {
            throw new StructureValidationException("No name specified on an attribute definition");
        }
        if (attrib instanceof UnaryCQLAttribute) {
            UnaryCQLAttribute unary = (UnaryCQLAttribute) attrib;
            if (unary.getPredicate() == null) {
                throw new StructureValidationException("No predicate specified on an attribute definition");
            }
        } else {
            validateBinaryAttribute((BinaryCQLAttribute) attrib);
        }
    }
    
    
    private void validateBinaryAttribute(BinaryCQLAttribute attrib) throws StructureValidationException {
        if (attrib.getPredicate() == null) {
            throw new StructureValidationException("No predicate was specified on an attribute definition");
        }
        AttributeValue value = attrib.getAttributeValue();
        if (value == null) {
            throw new StructureValidationException("No attribute value was specified on a binary attribute definition");
        }
        int populatedValues = 0;
        if (value.getBooleanValue() != null) {
            populatedValues++;
        }
        if (value.getDateValue() != null) {
            populatedValues++;
        }
        if (value.getDoubleValue() != null) {
            populatedValues++;
        }
        if (value.getIntegerValue() != null) {
            populatedValues++;
        }
        if (value.getLongValue() != null) {
            populatedValues++;
        }
        if (value.getStringValue() != null) {
            populatedValues++;
        }
        if (value.getTimeValue() != null) {
            populatedValues++;
        }
        if (populatedValues == 0) {
            throw new StructureValidationException("No typed value was specified on a binary attribute definition");
        } else if (populatedValues > 1) {
            throw new StructureValidationException("More than one (" + populatedValues 
                + ") typed values were specified on a binary attribute definition");
        }
    }
    
    
    private void validateGroup(CQLGroup group) throws StructureValidationException {
        // validate the logical operation
        if (group.getLogicalOperation() == null) {
            throw new StructureValidationException("No logical operation was specified on a group definition");
        }
        String logic = group.getLogicalOperation().getValue();
        if (!logic.equals(GroupLogicalOperator._AND) && !logic.equals(GroupLogicalOperator._OR)) {
            throw new StructureValidationException("Logical operator " + logic + " is not valid");
        }
        
        // count the number of group members
        int groupMemberCount = 0;
        
        // validate children of the group
        if (group.getBinaryCQLAttribute() != null) {
            groupMemberCount += group.getBinaryCQLAttribute().length;
            for (BinaryCQLAttribute att : group.getBinaryCQLAttribute()) {
                validateAttribute(att);
            }
        }
        if (group.getUnaryCQLAttribute() != null) {
            groupMemberCount += group.getUnaryCQLAttribute().length;
            for (UnaryCQLAttribute att : group.getUnaryCQLAttribute()) {
                validateAttribute(att);
            }
        }
        if (group.getCQLAssociatedObject() != null) {
            groupMemberCount += group.getCQLAssociatedObject().length;
            for (CQLAssociatedObject assoc : group.getCQLAssociatedObject()) {
                validateAssociation(assoc);
            }
        }
        if (group.getCQLGroup() != null) {
            groupMemberCount += group.getCQLGroup().length;
            for (CQLGroup g : group.getCQLGroup()) {
                validateGroup(g);
            }
        }
        
        if (groupMemberCount < 2) {
            throw new StructureValidationException("Groups must have two or more members (found " + groupMemberCount + ")");
        }
    }
    
    
    private void validateQueryModifier(CQLQueryModifier mods) throws StructureValidationException {
        int childCount = 0;
        if (mods.getCountOnly() != null) {
            childCount++;
        }
        if (mods.getDistinctAttribute() != null) {
            childCount++;
        }
        if (mods.getNamedAttribute() != null) {
            childCount++;
        }
        if (childCount > 1) {
            throw new StructureValidationException("Found more than one (" + childCount 
                + ") child restriction of the query modifier");
        }
    }
    
    
    private boolean isEmpty(String test) {
        boolean empty = test == null || test.trim().length() == 0;
        return empty;
    }
}
 