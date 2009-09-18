package gov.nih.nci.cagrid.data.ui.auditors;

import java.util.EventListener;

/** 
 *  MonitoredEventsChangeListener
 *  Listens for changes to the monitored events selection
 * 
 * @author David Ervin
 * 
 * @created May 24, 2007 10:25:58 AM
 * @version $Id: MonitoredEventsChangeListener.java,v 1.1 2007-07-12 17:20:52 dervin Exp $ 
 */
public interface MonitoredEventsChangeListener extends EventListener {

    public void monitoredEventsChanged();
}
