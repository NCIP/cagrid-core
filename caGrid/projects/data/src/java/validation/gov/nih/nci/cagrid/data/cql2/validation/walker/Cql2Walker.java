package gov.nih.nci.cagrid.data.cql2.validation.walker;

import java.util.LinkedList;
import java.util.List;

import org.cagrid.cql2.AssociationPopulationSpecification;
import org.cagrid.cql2.AttributeValue;
import org.cagrid.cql2.CQLAssociatedObject;
import org.cagrid.cql2.CQLAttribute;
import org.cagrid.cql2.CQLExtension;
import org.cagrid.cql2.CQLGroup;
import org.cagrid.cql2.CQLObject;
import org.cagrid.cql2.CQLQuery;
import org.cagrid.cql2.CQLQueryModifier;
import org.cagrid.cql2.CQLTargetObject;
import org.cagrid.cql2.DistinctAttribute;
import org.cagrid.cql2.NamedAttribute;

/**
 * Cql2Walker
 * Walks through a CQL 2 query object model and fires events when
 * each component of the query is encountered.
 * 
 * @author David
 */
public class Cql2Walker {
    
    private List<Cql2WalkerHandler> listeners;

    public Cql2Walker() {
        this.listeners = new LinkedList<Cql2WalkerHandler>();
    }
    
    
    public void walkCql(CQLQuery query) throws Cql2WalkerException {
        startQuery(query);
        walkObject(query.getCQLTargetObject());
        if (query.getCQLQueryModifier() != null) {
            walkQueryModifier(query.getCQLQueryModifier());
        }
        if (query.getAssociationPopulationSpecification() != null) {
            walkAssociationPopulation(query.getAssociationPopulationSpecification());
        }
        endQuery(query);
    }
    
    
    public void addListener(Cql2WalkerHandler listener) {
        listeners.add(listener);
    }
    
    
    public boolean removeListener(Cql2WalkerHandler listener) {
        return listeners.remove(listener);
    }
    
    
    public Cql2WalkerHandler[] getListeners() {
        return listeners.toArray(new Cql2WalkerHandler[0]);
    }
    
    
    private void walkObject(CQLObject obj) throws Cql2WalkerException {
        boolean isTarget = obj instanceof CQLTargetObject;
        boolean isAssociation = obj instanceof CQLAssociatedObject;
        if (isTarget) {
            startTarget((CQLTargetObject) obj);
        }
        if (isAssociation) {
            startAssociation((CQLAssociatedObject) obj);
        }
        
        if (obj.getCQLAttribute() != null) {
            walkAttribute(obj.getCQLAttribute());
        }
        if (obj.getCQLAssociatedObject() != null) {
            walkObject(obj.getCQLAssociatedObject());
        }
        if (obj.getCQLGroup() != null) {
            walkGroup(obj.getCQLGroup());
        }
        
        if (isTarget) {
            endTarget((CQLTargetObject) obj);
        }
        if (isAssociation) {
            endAssociation((CQLAssociatedObject) obj);
        }
    }
    
    
    private void walkAttribute(CQLAttribute attr) throws Cql2WalkerException {
        startAttribute(attr);
        if (attr.getAttributeValue() != null) {
            startAttribValue(attr.getAttributeValue());
            endAttribValue(attr.getAttributeValue());
        }
        if (attr.getAttributeExtension() != null) {
            startExtension(attr.getAttributeExtension());
            endExtension(attr.getAttributeExtension());
        }
        endAttribute(attr);
    }
    
    
    private void walkGroup(CQLGroup group) throws Cql2WalkerException {
        startGroup(group);
        if (group.getCQLAssociatedObject() != null) {
            for (CQLAssociatedObject a : group.getCQLAssociatedObject()) {
                walkObject(a);
            }
        }
        if (group.getCQLAttribute() != null) {
            for (CQLAttribute a : group.getCQLAttribute()) {
                walkAttribute(a);
            }
        }
        if (group.getCQLGroup() != null) {
            for (CQLGroup g : group.getCQLGroup()) {
                walkGroup(g);
            }
        }
        endGroup(group);
    }
    
    
    private void walkQueryModifier(CQLQueryModifier mods) throws Cql2WalkerException {
        startQueryModifier(mods);
        if (mods.getDistinctAttribute() != null) {
            startDistinctAttribute(mods.getDistinctAttribute());
            endDistinctAttribute(mods.getDistinctAttribute());
        }
        if (mods.getNamedAttribute() != null) {
            for (NamedAttribute na : mods.getNamedAttribute()) {
                startNamedAttribute(na);
                endNamedAttribute(na);
            }
        }
        endQueryModifier(mods);
    }
    
    
    private void walkAssociationPopulation(AssociationPopulationSpecification pop) throws Cql2WalkerException {
        startAssociationPopulation(pop);
        // TODO: walk association population stuff
        endAssociationPopulation(pop);
    }
    
    
    //----------
    // callbacks
    //----------
    
    
    protected void startQuery(CQLQuery query) throws Cql2WalkerException {
        for (Cql2WalkerHandler l : listeners) {
            l.startQuery(query);
        }
    }
    
    
    protected void endQuery(CQLQuery query) throws Cql2WalkerException {
        for (Cql2WalkerHandler l : listeners) {
            l.endQuery(query);
        }
    }
    
    
    protected void startTarget(CQLTargetObject obj) throws Cql2WalkerException {
        for (Cql2WalkerHandler l : listeners) {
            l.startTargetObject(obj);
        }
    }
    
    
    protected void endTarget(CQLTargetObject obj) throws Cql2WalkerException {
        for (Cql2WalkerHandler l : listeners) {
            l.endTargetObject(obj);
        }
    }
    
    
    protected void startAssociation(CQLAssociatedObject assoc) throws Cql2WalkerException {
        for (Cql2WalkerHandler l : listeners) {
            l.startAssociation(assoc);
        }
    }
    
    
    protected void endAssociation(CQLAssociatedObject assoc) throws Cql2WalkerException {
        for (Cql2WalkerHandler l : listeners) {
            l.endAssociation(assoc);
        }
    }
    
    
    protected void startAttribute(CQLAttribute attrib) throws Cql2WalkerException {
        for (Cql2WalkerHandler l : listeners) {
            l.startAttribute(attrib);
        }
    }
    
    
    protected void endAttribute(CQLAttribute attrib) throws Cql2WalkerException {
        for (Cql2WalkerHandler l : listeners) {
            l.endAttribute(attrib);
        }
    }
    
    
    protected void startAttribValue(AttributeValue val) throws Cql2WalkerException {
        for (Cql2WalkerHandler l : listeners) {
            l.startAttributeValue(val);
        }
    }
    
    
    protected void endAttribValue(AttributeValue val) throws Cql2WalkerException {
        for (Cql2WalkerHandler l : listeners) {
            l.endAttributeValue(val);
        }
    }
    
    
    protected void startGroup(CQLGroup group) throws Cql2WalkerException {
        for (Cql2WalkerHandler l : listeners) {
            l.startGroup(group);
        }
    }
    
    
    protected void endGroup(CQLGroup group) throws Cql2WalkerException {
        for (Cql2WalkerHandler l : listeners) {
            l.endGroup(group);
        }
    }
    
    
    protected void startExtension(CQLExtension ext) throws Cql2WalkerException {
        for (Cql2WalkerHandler l : listeners) {
            l.startExtension(ext);
        }
    }
    
    
    protected void endExtension(CQLExtension ext) throws Cql2WalkerException {
        for (Cql2WalkerHandler l : listeners) {
            l.endExtension(ext);
        }
    }
    
    
    protected void startQueryModifier(CQLQueryModifier mods) throws Cql2WalkerException {
        for (Cql2WalkerHandler l : listeners) {
            l.startQueryModifier(mods);
        }
    }
    
    
    protected void endQueryModifier(CQLQueryModifier mods) throws Cql2WalkerException {
        for (Cql2WalkerHandler l : listeners) {
            l.endQueryModifier(mods);
        }
    }
    
    
    protected void startDistinctAttribute(DistinctAttribute attr) throws Cql2WalkerException {
        for (Cql2WalkerHandler l : listeners) {
            l.startDistinctAttribute(attr);
        }
    }
    
    
    protected void endDistinctAttribute(DistinctAttribute attr) throws Cql2WalkerException {
        for (Cql2WalkerHandler l : listeners) {
            l.endDistinctAttribute(attr);
        }
    }
    
    
    protected void startNamedAttribute(NamedAttribute na) throws Cql2WalkerException {
        for (Cql2WalkerHandler l : listeners) {
            l.startNamedAttribute(na);
        }
    }
    
    
    protected void endNamedAttribute(NamedAttribute na) throws Cql2WalkerException {
        for (Cql2WalkerHandler l : listeners) {
            l.endNamedAttribute(na);
        }
    }
    
    
    protected void startAssociationPopulation(AssociationPopulationSpecification pop) throws Cql2WalkerException {
        for (Cql2WalkerHandler l : listeners) {
            l.startAssociationPopulation(pop);
        }
    }
    
    
    protected void endAssociationPopulation(AssociationPopulationSpecification pop) throws Cql2WalkerException {
        for (Cql2WalkerHandler l : listeners) {
            l.endAssociationPopulation(pop);
        }
    }
}
