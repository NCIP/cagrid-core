package gov.nih.nci.cagrid.data.ui;

import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;

/** 
 *  NotifyingButtonGroup
 *  Button Group that can inform listeners that a button selection has changed
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>  * 
 * @created Nov 27, 2006 
 * @version $Id: NotifyingButtonGroup.java,v 1.2 2007-12-18 19:11:40 dervin Exp $ 
 */
public class NotifyingButtonGroup extends ButtonGroup {
	
	private LinkedList<GroupSelectionListener> listeners = null;
	
	private ButtonModel previousSelection = null;
	
	public NotifyingButtonGroup() {
		super();
		this.listeners = new LinkedList<GroupSelectionListener>();
	}
	
	
	public void addGroupSelectionListener(GroupSelectionListener listener) {
		listeners.add(listener);
	}
	
	
	public GroupSelectionListener[] getGroupSelectionListeners() {
		GroupSelectionListener[] array = new GroupSelectionListener[listeners.size()];
		listeners.toArray(array);
		return array;
	}
	
	
	public boolean removeGroupSelectionListener(GroupSelectionListener listener) {
		return listeners.remove(listener);
	}
	
	
	protected void fireGroupSelectionChanged() {
		Iterator iter = listeners.iterator();
		while (iter.hasNext()) {
			((GroupSelectionListener) iter.next()).selectionChanged(previousSelection, getSelection());
		}
		previousSelection = getSelection();
	}
	
	
	public void setSelected(ButtonModel m, boolean b) {
		boolean prevState = m.isSelected();
		super.setSelected(m, b);
		if (prevState != b) {
			// the selection actually changed
			fireGroupSelectionChanged();
		}
	}
}
