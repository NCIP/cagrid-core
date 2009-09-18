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
