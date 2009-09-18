package gov.nih.nci.cagrid.data.style.sdkstyle.wizard;

import java.util.EventListener;

/** 
 *  AppserviceConfigCompletionListener
 *  Simple event listener to perform notification when an appservice
 *  config panel is complete
 * 
 * @author David Ervin
 * 
 * @created Mar 23, 2007 1:57:47 PM
 * @version $Id: AppserviceConfigCompletionListener.java,v 1.1 2007-07-12 17:20:52 dervin Exp $ 
 */
public interface AppserviceConfigCompletionListener extends EventListener {

    public void completionStatusChanged(boolean isComplete);
}
