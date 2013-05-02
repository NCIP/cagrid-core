/**
*============================================================================
*  Copyright The Ohio State University Research Foundation, The University of Chicago - 
*	Argonne National Laboratory, Emory University, SemanticBits LLC, and 
*	Ekagra Software Technologies Ltd.
*
*  Distributed under the OSI-approved BSD 3-Clause License.
*  See http://ncip.github.com/cagrid-core/LICENSE.txt for details.
*============================================================================
**/
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
