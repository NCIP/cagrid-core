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
package gov.nih.nci.cagrid.data.ui.domain;

import gov.nih.nci.cagrid.introduce.beans.namespace.NamespaceType;

import java.util.EventListener;

/** 
 *  DomainModelClassSelectionListener
 *  Listens for changes to the class selection in the domain model
 * 
 * @author David Ervin
 * 
 * @created Apr 10, 2007 3:16:54 PM
 * @version $Id: DomainModelClassSelectionListener.java,v 1.3 2009-01-13 15:55:19 dervin Exp $ 
 */
public interface DomainModelClassSelectionListener extends EventListener {

    public void classSelected(String packName, String className, NamespaceType packageNamespace);
    
    
    public void classDeselected(String packName, String className);
    
    
    public void classesCleared();
}
