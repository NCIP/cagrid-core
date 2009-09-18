package gov.nih.nci.cagrid.data.utilities.vizquery.attributes;

import gov.nih.nci.cagrid.cqlquery.Predicate;

import java.util.EventListener;

/** 
 *  AttributePredicateChangeListener
 *  Listens for changes to an attribute's predicate
 * 
 * @author David Ervin
 * 
 * @created Apr 6, 2007 9:40:38 AM
 * @version $Id: AttributePredicateChangeListener.java,v 1.1 2007-04-06 14:50:14 dervin Exp $ 
 */
public interface AttributePredicateChangeListener extends EventListener {

    public void attributePredicateChanged(Predicate newValue);
}
