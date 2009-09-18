package gov.nih.nci.cagrid.data.ui.table;

import gov.nih.nci.cagrid.data.common.ExtensionDataManager;
import gov.nih.nci.cagrid.data.common.ModelInformationUtil;
import gov.nih.nci.cagrid.introduce.beans.namespace.NamespaceType;
import gov.nih.nci.cagrid.introduce.beans.namespace.SchemaElementType;

import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import javax.swing.AbstractCellEditor;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** 
 *  ClassElementSerializationTable
 *  Table for showing and configuring class, namespace, element, and serialization
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * 
 * @created Oct 6, 2006 
 * @version $Id: ClassElementSerializationTable.java,v 1.3 2009-01-13 15:55:19 dervin Exp $ 
 */
public class ClassElementSerializationTable extends JTable {
    
    private static final Log LOG = LogFactory.getLog(ClassElementSerializationTable.class);
    
	private List<ClassInformatonChangeListener> classInformationChangeListeners = null;
	private SerializationPopupMenu popup = null;
    
    private ExtensionDataManager dataManager = null;
    private ModelInformationUtil modelInfoUtil = null;

	public ClassElementSerializationTable(
        ExtensionDataManager dataManager, ModelInformationUtil modelInfoUtil) {
		super(createTableModel());
        this.dataManager = dataManager;
        this.modelInfoUtil = modelInfoUtil;
		setDefaultRenderer(Object.class, new ComponentCellRenderer());
		setDefaultEditor(Component.class, new ComponentCellEditor());
		DefaultListSelectionModel listSelection = new DefaultListSelectionModel();
		listSelection.setSelectionMode(
			ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		setSelectionModel(listSelection);
		setRowSelectionAllowed(true);
		this.classInformationChangeListeners = 
            new LinkedList<ClassInformatonChangeListener>();
		// add model listener to fire off editing events
		getModel().addTableModelListener(new TableModelListener() {
			public void tableChanged(TableModelEvent e) {
				if (e.getType() == TableModelEvent.UPDATE) {
					switch (e.getColumn()) {
						case 3:
							fireElementNameChanged(e.getFirstRow());
							break;
						case 4:
						case 5:
							fireSerializationChanged(e.getFirstRow());
							break;
						case 6:
							fireTargetabilityChanged(e.getFirstRow());
							break;
					}
				}
			}
		});
		// add mouse listener to open popup menu for serialization
		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger() && getSelectedRows().length != 0) {
					if (getSelectedRows().length != 0) {
						getPopup().show(ClassElementSerializationTable.this, e.getX(), e.getY());
					}
				}
			}
			
			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger() && getSelectedRows().length != 0) {
					if (getSelectedRows().length != 0) {
						getPopup().show(ClassElementSerializationTable.this, e.getX(), e.getY());
					}
				}
			}
		});
	}
	
	
	public void addClass(String packName, String className, NamespaceType nsType) {
		Vector<Object> row = new Vector<Object>(7);
		row.add(packName);
		row.add(className);
		row.add(nsType.getNamespace());
		// create a JComboBox for all element names in the namespace
		JComboBox elementNameCombo = getElementNameCombo(nsType);
        // get the element mapped to this class
        SchemaElementType schemaType = modelInfoUtil.getMappedElement(packName, className);
		// set the combo's selection
        elementNameCombo.setSelectedItem(schemaType == null ? null : schemaType.getType());
		row.add(elementNameCombo);
		row.add(schemaType == null ? null : schemaType.getSerializer());
		row.add(schemaType == null ? null : schemaType.getDeserializer());
		JCheckBox check = new JCheckBox();
        boolean selected = false;
        try {
            selected = dataManager.getClassSelectedInModel(packName, className);
        } catch (Exception ex) {
            LOG.warn("Error obtaining selection info for class " + packName + "." + className);
        }
		check.setSelected(selected);
		row.add(check);
		((DefaultTableModel) getModel()).addRow(row);
	}
	
	
	private JComboBox getElementNameCombo(NamespaceType nsType) {
		JComboBox combo = new JComboBox();
		combo.setEditable(true);
		for (int i = 0; nsType.getSchemaElement() != null && i < nsType.getSchemaElement().length; i++) {
			combo.addItem(nsType.getSchemaElement(i).getType());
		}
		return combo;
	}
	
	
	public void removeRow(String packName, String className) {
		// find the row needed
		int row = 0;
		while (row < getRowCount()) {
			if (packName.equals(getValueAt(row, 0)) && className.equals(getValueAt(row, 1))) {
				((DefaultTableModel) getModel()).removeRow(row);
				break;
			}
			row++;
		}
	}
	
	
	public boolean isCellEditable(int row, int column) {
		return column == 3 || column == 6;
	}
	
	
	public boolean isTargetable(String packName, String className) {
		// find the row needed
		int row = 0;
		while (row < getRowCount()) {
			if (packName.equals(getValueAt(row, 0)) && className.equals(getValueAt(row, 1))) {
				JCheckBox check = (JCheckBox) getValueAt(row, 6);
				return check.isSelected();
			}
			row++;
		}
		return false;
	}
	
	
	public void addClassInformatonChangeListener(ClassInformatonChangeListener l) {
		classInformationChangeListeners.add(l);
	}
	
	
	public boolean removeClassInformatonChangeListener(ClassInformatonChangeListener l) {
		return classInformationChangeListeners.remove(l);
	}
	
	
	public ClassInformatonChangeListener[] getClassInformationChangeListeners() {
		ClassInformatonChangeListener[] listeners = 
			new ClassInformatonChangeListener[classInformationChangeListeners.size()];
		classInformationChangeListeners.toArray(listeners);
		return listeners;
	}
	
	
	public void clearTable() {
		while (getRowCount() != 0) {
			((DefaultTableModel) getModel()).removeRow(0);
		}
	}
	
	
	protected void fireElementNameChanged(int row) {
		ClassChangeEvent e = getChangeForRow(row);
		Iterator i = classInformationChangeListeners.iterator();
		while (i.hasNext()) {
			((ClassInformatonChangeListener) i.next()).elementNameChanged(e);
		}
	}
	
	
	protected void fireSerializationChanged(int row) {
		ClassChangeEvent e = getChangeForRow(row);
		Iterator i = classInformationChangeListeners.iterator();
		while (i.hasNext()) {
			((ClassInformatonChangeListener) i.next()).serializationChanged(e);
		}
	}
	
	
	protected void fireTargetabilityChanged(int row) {
		ClassChangeEvent e = getChangeForRow(row);
		Iterator i = classInformationChangeListeners.iterator();
		while (i.hasNext()) {
			((ClassInformatonChangeListener) i.next()).targetabilityChanged(e);
		}
	}
	
	
	private ClassChangeEvent getChangeForRow(int row) {
		String packName = (String) getValueAt(row, 0);
		String className = (String) getValueAt(row, 1);
		String namespace = (String) getValueAt(row, 2);
		String elemName = (String) ((JComboBox) getValueAt(row, 3)).getSelectedItem();
		String serializer = (String) getValueAt(row, 4);
		String deserializer = (String) getValueAt(row, 5);
		boolean targetable = ((JCheckBox) getValueAt(row, 6)).isSelected();
		
		ClassChangeEvent event = new ClassChangeEvent(this, packName,
			className, namespace, elemName, serializer, deserializer, targetable);
		return event;
	}
	
	
	private SerializationPopupMenu getPopup() {
		if (popup == null) {
			popup = new SerializationPopupMenu(this);
		}
		return popup;
	}
	
	
	private static DefaultTableModel createTableModel() {
		DefaultTableModel model = new DefaultTableModel() {
			public Class<?> getColumnClass(int column) {
				return column == 6 || column == 3 ? Component.class : Object.class;
			}
		};
		model.addColumn("Package Name");
		model.addColumn("Class Name");
		model.addColumn("Namespace");
		model.addColumn("Element Name");
		model.addColumn("Serializer");
		model.addColumn("Deserializer");
		model.addColumn("Targetable");
		return model;
	}
	
	
	private static class ComponentCellRenderer extends DefaultTableCellRenderer {
		
		public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
			if (value instanceof Component) {
				return (Component) value;
			}
			return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		}
	}
	
	
	private static class ComponentCellEditor extends AbstractCellEditor implements TableCellEditor {
		private ItemListener editListener = null;
		private Object editorValue = null;
		
		public ComponentCellEditor() {
			editorValue = null;
		}
		
		
		public Object getCellEditorValue() {
			return editorValue;
		}
		
		
		public Component getTableCellEditorComponent(JTable table, Object value, 
			boolean isSelected, int row, int column) {
			editorValue = value;
			if (value instanceof JCheckBox) {
				((JCheckBox) value).addItemListener(getEditListener());				
			} else if (value instanceof JComboBox) {
				((JComboBox) value).addItemListener(getEditListener());
			}
			
			return (Component) value;
		}
		
		
		private ItemListener getEditListener() {
			editListener = new ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					Object source = e.getSource();
					if (source instanceof JCheckBox) {
						((JCheckBox) source).removeItemListener(this);
					} else if (source instanceof JComboBox) {
						((JComboBox) source).removeItemListener(this);
					}
					fireEditingStopped();
				}
			};
			return editListener;
		}
	}
}
