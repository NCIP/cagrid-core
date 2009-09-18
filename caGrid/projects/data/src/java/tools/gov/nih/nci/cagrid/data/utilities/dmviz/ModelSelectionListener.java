package gov.nih.nci.cagrid.data.utilities.dmviz;

import gov.nih.nci.cagrid.metadata.dataservice.UMLAssociation;
import gov.nih.nci.cagrid.metadata.dataservice.UMLClass;

import java.util.EventListener;

/** 
 *  ModelSelectionListener
 *  TODO:DOCUMENT ME
 * 
 * @author David Ervin
 * 
 * @created Mar 30, 2007 1:17:51 PM
 * @version $Id: ModelSelectionListener.java,v 1.1 2007-03-30 20:40:42 dervin Exp $ 
 */
public interface ModelSelectionListener extends EventListener {

    public void classSelected(UMLClass selection);
    
    
    public void associationSelected(UMLAssociation selection);
}
