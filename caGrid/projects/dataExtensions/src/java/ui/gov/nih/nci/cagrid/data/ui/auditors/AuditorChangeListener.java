package gov.nih.nci.cagrid.data.ui.auditors;

import java.util.EventListener;

/** 
 *  AuditorChangeListener
 *  Listens for changes to the data service auditors
 * 
 * @author David Ervin
 * 
 * @created May 22, 2007 11:13:11 AM
 * @version $Id: AuditorChangeListener.java,v 1.1 2007-07-12 17:20:52 dervin Exp $ 
 */
public interface AuditorChangeListener extends EventListener {    
    
    public void auditorConfigureButtonClicked(String className, String instanceName);
 
    
    public void auditorRemoveButtonClicked(String className, String instanceName);
}
