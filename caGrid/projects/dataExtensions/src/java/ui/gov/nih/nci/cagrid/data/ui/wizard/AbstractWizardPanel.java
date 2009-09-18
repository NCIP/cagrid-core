package gov.nih.nci.cagrid.data.ui.wizard;

import gov.nih.nci.cagrid.introduce.beans.extension.ExtensionTypeExtensionData;
import gov.nih.nci.cagrid.introduce.beans.extension.ServiceExtensionDescriptionType;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;
import gov.nih.nci.cagrid.introduce.extension.ExtensionTools;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;

/** 
 *  AbstractWizardPanel
 *  Base class for panels to be used by a service creation wizard
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * 
 * @created Sep 25, 2006 
 * @version $Id: AbstractWizardPanel.java,v 1.4 2008-01-02 19:32:08 dervin Exp $ 
 */
public abstract class AbstractWizardPanel extends JPanel {
	private static Map<Object, Object> bitBucket;

	private ServiceExtensionDescriptionType extDescription;
	private ServiceInformation serviceInfo;
	private List<ButtonEnableListener> listeners;
	
	public AbstractWizardPanel(ServiceExtensionDescriptionType extensionDescription, ServiceInformation info) {
        super();
		this.extDescription = extensionDescription;
		this.serviceInfo = info;
		this.listeners = new LinkedList<ButtonEnableListener>();
	}
	
	
	protected ServiceExtensionDescriptionType getExtensionDescription() {
		return extDescription;
	}
	
	
	protected ServiceInformation getServiceInformation() {
		return serviceInfo;
	}
	
	
	protected ExtensionTypeExtensionData getExtensionData() {
		return ExtensionTools.getExtensionData(extDescription, serviceInfo);
	}
	
	
	/**
	 * Gets a Map into which data can be stored for communicating with other panels
	 * in a sequence in a wizard.  This bit bucket is not persistant, so only use it for
	 * data which doesn't really belong in the service information or extension description.
	 * @return
	 * 		A map for storing arbitrary data
	 */
	protected Map<Object, Object> getBitBucket() {
		if (bitBucket == null) {
			bitBucket = Collections.synchronizedMap(new HashMap<Object, Object>());
		}
		return bitBucket;
	}
	
	
	/**
	 * Enable or disable the wizard button which allows moving 
	 * to the next panel in the sequence.  When the panel is shown,
	 * the previous and next buttons will be enabled / disabled
	 * according to the panel's order in the panel sequence.  If the
	 * buttons state needs to be changed, do so in the update() method.
	 * @param enable
	 */
	protected void setNextEnabled(boolean enable) {
		Iterator i = listeners.iterator();
		while (i.hasNext()) {
			((ButtonEnableListener) i.next()).setNextEnabled(enable);
		}
	}
	
	
	/**
	 * Enable or disable the wizard button which allows moving
	 * to the previous panel in the sequence.  When the panel is shown,
	 * the previous and next buttons will be enabled / disabled
	 * according to the panel's order in the panel sequence.  If the
	 * buttons state needs to be changed, do so in the update() method.
	 * @param enable
	 */
	protected void setPrevEnabled(boolean enable) {
		Iterator i = listeners.iterator();
		while (i.hasNext()) {
			((ButtonEnableListener) i.next()).setPrevEnabled(enable);
		}
	}
	
	
	protected void setWizardComplete(boolean done) {
		Iterator i = listeners.iterator();
		while (i.hasNext()) {
			((ButtonEnableListener) i.next()).setWizardDone(done);
		}
	}
	
	
	void addButtonEnableListener(ButtonEnableListener l) {
		listeners.add(l);
	}
	
	
	/**
	 * Called when the panel is shown in the wizard.  Use this call to perform any updates
	 * to the GUI needed by data changed in the extension data / bit bucket
	 */
	public abstract void update();
    
    
    /**
     * Called when the user clicks the button to travel to the next panel in the wizard.
     * Use this call to perform any changes to the underlying service model.
     */
    public void movingNext() {
        // Default implementation does nothing
    }
	
	
	/**
	 * Gets the descriptive title for this panel.
	 * @return
	 * 		The long title for the panel
	 */
	public abstract String getPanelTitle();
	
	
	/**
	 * Gets the short name for this panel.  This name will be shown in the next / back buttons
	 * of the wizard
	 * @return
	 * 		The short title for the panel
	 */
	public abstract String getPanelShortName();
}
