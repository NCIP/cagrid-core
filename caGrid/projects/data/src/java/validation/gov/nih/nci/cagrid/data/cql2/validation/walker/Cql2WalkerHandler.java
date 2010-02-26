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
    
    public void startQuery(CQLQuery query) throws Exception;
    
    public void endQuery(CQLQuery query) throws Exception;
    
    public void startTargetObject(CQLTargetObject obj) throws Exception;
    
    public void endTargetObject(CQLTargetObject obj) throws Exception;
    
    public void startAssociation(CQLAssociatedObject assoc) throws Exception;
    
    public void endAssociation(CQLAssociatedObject assoc) throws Exception;
    
    public void startGroup(CQLGroup group) throws Exception;
    
    public void endGroup(CQLGroup group) throws Exception;
    
    public void startAttribute(CQLAttribute attrib) throws Exception;
    
    public void endAttribute(CQLAttribute attrib) throws Exception;
    
    public void startAttributeValue(AttributeValue val) throws Exception;
    
    public void endAttributeValue(AttributeValue val) throws Exception;
    
    public void startExtension(CQLExtension ext) throws Exception;
    
    public void endExtension(CQLExtension ext) throws Exception;
    
    public void startQueryModifier(CQLQueryModifier mods) throws Exception;
    
    public void endQueryModifier(CQLQueryModifier mods) throws Exception;
    
    public void startDistinctAttribute(DistinctAttribute distinct) throws Exception;
    
    public void endDistinctAttribute(DistinctAttribute distinct) throws Exception;
    
    public void startNamedAttribute(NamedAttribute named) throws Exception;
    
    public void endNamedAttribute(NamedAttribute named) throws Exception;
    
    public void startAssociationPopulation(AssociationPopulationSpecification pop) throws Exception;
    
    public void endAssociationPopulation(AssociationPopulationSpecification pop) throws Exception;
    
    public void startNamedAssociationList(NamedAssociationList list) throws Exception;
    
    public void endNamedAssociationList(NamedAssociationList list) throws Exception;
    
    public void startNamedAssociation(NamedAssociation assoc) throws Exception;
    
    public void endNamedAssociation(NamedAssociation assoc) throws Exception;
    
    public void startPopulationDepth(PopulationDepth depth) throws Exception;
    
    public void endPopulationDepth(PopulationDepth depth) throws Exception;
}
