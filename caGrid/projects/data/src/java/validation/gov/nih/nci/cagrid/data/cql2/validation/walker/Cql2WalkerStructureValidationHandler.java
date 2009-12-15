package gov.nih.nci.cagrid.data.cql2.validation.walker;

import java.math.BigInteger;
import java.util.Stack;

import org.cagrid.cql2.AssociationPopulationSpecification;
import org.cagrid.cql2.AttributeValue;
import org.cagrid.cql2.CQLAssociatedObject;
import org.cagrid.cql2.CQLAttribute;
import org.cagrid.cql2.CQLExtension;
import org.cagrid.cql2.CQLGroup;
import org.cagrid.cql2.CQLQuery;
import org.cagrid.cql2.CQLQueryModifier;
import org.cagrid.cql2.CQLTargetObject;
import org.cagrid.cql2.DistinctAttribute;
import org.cagrid.cql2.NamedAssociation;
import org.cagrid.cql2.NamedAssociationList;
import org.cagrid.cql2.NamedAttribute;
import org.cagrid.cql2.PopulationDepth;


public class Cql2WalkerStructureValidationHandler implements Cql2WalkerHandler {

    private Stack<BigInteger> childCount = null;
    private boolean processingModifier = false;
    private boolean expectAttributeValue = false;
    private boolean foundAttributeValue = false;
    
    public Cql2WalkerStructureValidationHandler() {
        this.childCount = new Stack<BigInteger>();
    }


    public void endAssociation(CQLAssociatedObject assoc) throws Cql2WalkerException {
        BigInteger count = childCount.pop();
        if (count.intValue() > 1) {
            throw new StructureValidationException("CQL Objects may have at most one child (found " 
                + count.intValue() + ")");
        }
    }


    public void endAssociationPopulation(AssociationPopulationSpecification pop) throws Cql2WalkerException {
        // TODO Auto-generated method stub

    }


    public void endAttribute(CQLAttribute attrib) throws Cql2WalkerException {
        if (expectAttributeValue && !foundAttributeValue) {
            throw new StructureValidationException("Expected to find attribute value, did not");
        }
    }


    public void endAttributeValue(AttributeValue val) throws Cql2WalkerException {
        
    }


    public void endDistinctAttribute(DistinctAttribute distinct) throws Cql2WalkerException {
        
    }


    public void endExtension(CQLExtension ext) throws Cql2WalkerException {
        
    }


    public void endGroup(CQLGroup group) throws Cql2WalkerException {
        BigInteger count = childCount.pop();
        if (count.intValue() < 2) {
            throw new StructureValidationException("CQL Groups must have two or more children (found "
                + count.intValue() + ")");
        }
    }


    public void endNamedAssociation(NamedAssociation assoc) throws Cql2WalkerException {
        
    }


    public void endNamedAssociationList(NamedAssociationList list) throws Cql2WalkerException {
        
    }


    public void endNamedAttribute(NamedAttribute named) throws Cql2WalkerException {
        
    }


    public void endPopulationDepth(PopulationDepth depth) throws Cql2WalkerException {
        
    }


    public void endQuery(CQLQuery query) throws Cql2WalkerException {
        
    }


    public void endQueryModifier(CQLQueryModifier mods) throws Cql2WalkerException {
        processingModifier = false;
    }


    public void endTargetObject(CQLTargetObject obj) throws Cql2WalkerException {
        BigInteger count = childCount.pop();
        if (count.intValue() > 1) {
            throw new StructureValidationException("CQL Objects may have at most one child (found "
                + count.intValue() + ")");
        }
    }


    public void startAssociation(CQLAssociatedObject assoc) throws Cql2WalkerException {
        childCount.peek().add(BigInteger.ONE);
        childCount.push(BigInteger.valueOf(0));
    }


    public void startAssociationPopulation(AssociationPopulationSpecification pop) throws Cql2WalkerException {
        if (pop.getNamedAssociationList() == null || pop.getPopulationDepth() == null) {
            throw new StructureValidationException(
                "Association Population spec must have either a named association list or a population depth.  Found none");
        }
        if (pop.getNamedAssociationList() != null || pop.getPopulationDepth() != null) {
            throw new StructureValidationException(
                "Association Population spec must have either a named association list or a population depth.  Found both");
        }
    }


    public void startAttribute(CQLAttribute attrib) throws Cql2WalkerException {
        childCount.peek().add(BigInteger.ONE);
        if (attrib.getBinaryPredicate() == null && attrib.getUnaryPredicate() == null) {
            throw new StructureValidationException(
                "Attributes must have either a binary or unary predicate, found none");
        }
        if (attrib.getBinaryPredicate() != null && attrib.getUnaryPredicate() != null) {
            throw new StructureValidationException(
                "Attributes must have either a binary or unary predicate, found both");
        }
        expectAttributeValue = attrib.getBinaryPredicate() != null;
    }


    public void startAttributeValue(AttributeValue val) throws Cql2WalkerException {
        foundAttributeValue = true;
        int populatedValues = 0;
        populatedValues += val.getIntegerValue() != null ? 1 : 0;
        populatedValues += val.getBooleanValue() != null ? 1 : 0;
        populatedValues += val.getDateValue() != null ? 1 : 0;
        populatedValues += val.getDoubleValue() != null ? 1 : 0;
        populatedValues += val.getLongValue() != null ? 1 : 0;
        populatedValues += val.getStringValue() != null ? 1 : 0;
        populatedValues += val.getTimeValue() != null ? 1 : 0;
        if (populatedValues != 1) {
            throw new StructureValidationException("Expected to find one populated attribute value, found " + populatedValues);
        }
    }


    public void startDistinctAttribute(DistinctAttribute distinct) throws Cql2WalkerException {
        if (distinct.getAggregation() == null) {
            throw new StructureValidationException("Distinct Attributes must have an aggregation");
        }
        if (distinct.getAttributeName() == null) {
            throw new StructureValidationException("Distinct Attributes must have an attribute name");
        }
    }


    public void startExtension(CQLExtension ext) throws Cql2WalkerException {
        if (!processingModifier) {
            childCount.peek().add(BigInteger.ONE);
        }
    }


    public void startGroup(CQLGroup group) throws Cql2WalkerException {
        childCount.peek().add(BigInteger.ONE);
        childCount.push(BigInteger.valueOf(0));
    }


    public void startNamedAssociation(NamedAssociation assoc) throws Cql2WalkerException {
        if (assoc.getNamedAssociationList() != null && assoc.getPopulationDepth() != null) {
            throw new StructureValidationException(
                "Named associations may have either a named association list or a population depth.  Found both");
        }
    }


    public void startNamedAssociationList(NamedAssociationList list) throws Cql2WalkerException {
        if (list.getNamedAssociation() == null || list.getNamedAssociation().length == 0) {
            throw new StructureValidationException("Named association list must have 1 or more named associations");
        }
    }


    public void startNamedAttribute(NamedAttribute named) throws Cql2WalkerException {
        if (named.getAttributeName() == null) {
            throw new StructureValidationException("Named Attribute must have an attribute name");
        }
    }


    public void startPopulationDepth(PopulationDepth depth) throws Cql2WalkerException {
        // TODO Auto-generated method stub
    }


    public void startQuery(CQLQuery query) throws Cql2WalkerException {
        // reset state variables
        childCount.clear();
        processingModifier = false;
    }


    public void startQueryModifier(CQLQueryModifier mods) throws Cql2WalkerException {
        processingModifier = true;
    }


    public void startTargetObject(CQLTargetObject obj) throws Cql2WalkerException {
        childCount.push(BigInteger.valueOf(0));
    }
}
