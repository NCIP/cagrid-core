package gov.nih.nci.cagrid.data.cql2.validation.walker;

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

public interface Cql2WalkerHandler {
    
    public void startQuery(CQLQuery query) throws Cql2WalkerException;
    
    public void endQuery(CQLQuery query) throws Cql2WalkerException;
    
    public void startTargetObject(CQLTargetObject obj) throws Cql2WalkerException;
    
    public void endTargetObject(CQLTargetObject obj) throws Cql2WalkerException;
    
    public void startAssociation(CQLAssociatedObject assoc) throws Cql2WalkerException;
    
    public void endAssociation(CQLAssociatedObject assoc) throws Cql2WalkerException;
    
    public void startGroup(CQLGroup group) throws Cql2WalkerException;
    
    public void endGroup(CQLGroup group) throws Cql2WalkerException;
    
    public void startAttribute(CQLAttribute attrib) throws Cql2WalkerException;
    
    public void endAttribute(CQLAttribute attrib) throws Cql2WalkerException;
    
    public void startAttributeValue(AttributeValue val) throws Cql2WalkerException;
    
    public void endAttributeValue(AttributeValue val) throws Cql2WalkerException;
    
    public void startExtension(CQLExtension ext) throws Cql2WalkerException;
    
    public void endExtension(CQLExtension ext) throws Cql2WalkerException;
    
    public void startQueryModifier(CQLQueryModifier mods) throws Cql2WalkerException;
    
    public void endQueryModifier(CQLQueryModifier mods) throws Cql2WalkerException;
    
    public void startDistinctAttribute(DistinctAttribute distinct) throws Cql2WalkerException;
    
    public void endDistinctAttribute(DistinctAttribute distinct) throws Cql2WalkerException;
    
    public void startNamedAttribute(NamedAttribute named) throws Cql2WalkerException;
    
    public void endNamedAttribute(NamedAttribute named) throws Cql2WalkerException;
    
    public void startAssociationPopulation(AssociationPopulationSpecification pop) throws Cql2WalkerException;
    
    public void endAssociationPopulation(AssociationPopulationSpecification pop) throws Cql2WalkerException;
    
    public void startNamedAssociationList(NamedAssociationList list) throws Cql2WalkerException;
    
    public void endNamedAssociationList(NamedAssociationList list) throws Cql2WalkerException;
    
    public void startNamedAssociation(NamedAssociation assoc) throws Cql2WalkerException;
    
    public void endNamedAssociation(NamedAssociation assoc) throws Cql2WalkerException;
    
    public void startPopulationDepth(PopulationDepth depth) throws Cql2WalkerException;
    
    public void endPopulationDepth(PopulationDepth depth) throws Cql2WalkerException;
}
