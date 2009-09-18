package org.cagrid.grape.utils.errors;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.EventListenerList;
import javax.swing.table.TableCellEditor;

/** 
 *  JComponentCellEditor
 *  Table cell editor to handle JComponents on the table
 * 
 * @author David Ervin
 * 
 * @created Apr 13, 2007 9:20:12 AM
 * @version $Id: JComponentCellEditor.java,v 1.1 2007-04-13 18:11:01 dervin Exp $ 
 */
public class JComponentCellEditor implements TableCellEditor {

	protected EventListenerList listenerList = new EventListenerList();

	protected JComponent editorComponent = null; // the component doing the editing

	protected JComponent container = null; // the table
    
    protected transient ChangeEvent changeEvent = null;


	public JComponentCellEditor() {
		super();
	}


	public Component getComponent() {
		return editorComponent;
	}


	public Object getCellEditorValue() {
		return editorComponent;
	}


    /**
     * Subclasses may override this to make certain cells editable or not
     */
	public boolean isCellEditable(EventObject anEvent) {
		return true;
	}


	public boolean shouldSelectCell(EventObject anEvent) {
		if (editorComponent != null && anEvent instanceof MouseEvent
			&& ((MouseEvent) anEvent).getID() == MouseEvent.MOUSE_PRESSED) {
            // get the component clicked on
			Component dispatchComponent = SwingUtilities.getDeepestComponentAt(editorComponent, 3, 3);
			MouseEvent mouseEvent = (MouseEvent) anEvent;
            // refire the mouse event on the clicked component as a
            // mouse release and a mouse click
			MouseEvent releaseEvent = new MouseEvent(dispatchComponent, MouseEvent.MOUSE_RELEASED, mouseEvent.getWhen() + 100000, 
                mouseEvent.getModifiers(), 3, 3, mouseEvent.getClickCount(), mouseEvent.isPopupTrigger());
			dispatchComponent.dispatchEvent(releaseEvent);
			MouseEvent clickEvent = new MouseEvent(dispatchComponent, MouseEvent.MOUSE_CLICKED, mouseEvent.getWhen() + 100001, 
                mouseEvent.getModifiers(), 3, 3, 1, mouseEvent.isPopupTrigger());
			dispatchComponent.dispatchEvent(clickEvent);
		}
		return false;
	}


	public boolean stopCellEditing() {
		fireEditingStopped();
		return true;
	}


	public void cancelCellEditing() {
		fireEditingCanceled();
	}


	public void addCellEditorListener(CellEditorListener l) {
		listenerList.add(CellEditorListener.class, l);
	}


	public void removeCellEditorListener(CellEditorListener l) {
		listenerList.remove(CellEditorListener.class, l);
	}


	protected void fireEditingStopped() {
		Object[] listeners = listenerList.getListenerList();
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == CellEditorListener.class) {
				// Lazily create the event:
				if (changeEvent == null)
					changeEvent = new ChangeEvent(this);
				try {
					((CellEditorListener) listeners[i + 1]).editingStopped(changeEvent);
				} catch (Exception e) {
					//e.printStackTrace();
				}
			}
		}
	}


	protected void fireEditingCanceled() {
		// Guaranteed to return a non-null array
		Object[] listeners = listenerList.getListenerList();
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == CellEditorListener.class) {
				// Lazily create the event:
				if (changeEvent == null)
					changeEvent = new ChangeEvent(this);
				((CellEditorListener) listeners[i + 1]).editingCanceled(changeEvent);
			}
		}
	}


	public Component getTableCellEditorComponent(JTable table, Object value, 
        boolean isSelected, int row, int column) {
		editorComponent = (JComponent) value;
		container = table;
		return editorComponent;
	}
}
