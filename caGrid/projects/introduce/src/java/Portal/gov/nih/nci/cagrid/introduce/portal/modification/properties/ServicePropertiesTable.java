package gov.nih.nci.cagrid.introduce.portal.modification.properties;

import gov.nih.nci.cagrid.common.portal.PortalBaseTable;
import gov.nih.nci.cagrid.introduce.beans.property.ServicePropertiesProperty;
import gov.nih.nci.cagrid.introduce.common.CommonTools;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;

import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.BadLocationException;

/**
 * ServicePropertiesTable Table to render and allow modification of service
 * properties as contained in an introduce service model.
 * 
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella </A>
 */
public class ServicePropertiesTable extends PortalBaseTable implements
		TableModelListener {

	public static String NAME = "Name";

	public static String VALUE = "Default Value";

	public static String DESC = "Description";

	public static String ETC = "Path From Etc";

	private ServiceInformation info;

	public ServicePropertiesTable(ServiceInformation info) {
		super(new MyDefaultTableModel());
		this.info = info;
		this.getModel().addTableModelListener(this);
		initialize();
	}

	public void setServiceInformation(ServiceInformation info) {
		this.info = info;
		refreshView();
	}

	public boolean isCellEditable(int row, int column) {
		if (column == 0) {
			return false;
		}
		return true;
	}

	public void refreshView() {
		// clean out old data
		while (getRowCount() != 0) {
			((DefaultTableModel) getModel()).removeRow(0);
		}
		// add new data
		if (info.getServiceProperties() != null) {
			ServicePropertiesProperty[] allProperties = info
					.getServiceProperties().getProperty();
			if ((allProperties != null) && (allProperties.length > 0)) {
				for (int i = 0; i < allProperties.length; i++) {
					Vector v = new Vector(5);
					v.add(allProperties[i].getKey());
					v.add(allProperties[i].getValue());
					if (allProperties[i].getDescription() == null) {
						allProperties[i].setDescription("");
					}
					v.add(allProperties[i].getDescription());
					if (allProperties[i].getIsFromETC() != null) {
						v.add(allProperties[i].getIsFromETC());
					} else {
						v.add(new Boolean(false));
					}
					((DefaultTableModel) this.getModel()).addRow(v);
				}
			}
		}

		sort();

		repaint();
	}

	private void setSelectedRow(int row) {
		setRowSelectionInterval(row, row);
	}

	public void addRow(String key, String value, boolean isFromETC,
			String description) {
		// add the property to the service model
		CommonTools.setServiceProperty(info.getServiceDescriptor(), key, value,
				isFromETC, description);

		// add the row to the GUI
		refreshView();

		for (int i = 0; i < getRowCount(); i++) {
			String tkey = (String) getModel().getValueAt(i, 0);
			if (tkey.equals(key)) {
				// select the newly added row
				setSelectedRow(i);
			}
		}
	}

	public void modifyServiceProperty(String key, String value,
			boolean isFromETC, String description) {
		// add the property to the service model
		CommonTools.setServiceProperty(info.getServiceDescriptor(), key, value,
				isFromETC, description);
	}

	public void modifySelectedServicePropertyValue(String value) {
		Vector v = getSelectedRowData();
		if (v != null) {
			// add the property to the service model
			CommonTools.setServiceProperty(info.getServiceDescriptor(),
					(String) v.get(0), value, (Boolean) v.get(3), (String) v
							.get(2));
		}
	}

	public void modifySelectedServicePropertyDescriptor(String desc) {
		Vector v = getSelectedRowData();
		if (v != null) {
			// add the property to the service model
			CommonTools.setServiceProperty(info.getServiceDescriptor(),
					(String) v.get(0), (String) v.get(1), (Boolean) v.get(3),
					desc);
		}
	}

	public void modifySelectedServicePropertyIsFromETC(Boolean etc) {
		Vector v = getSelectedRowData();
		if (v != null) {
			// add the property to the service model
			CommonTools.setServiceProperty(info.getServiceDescriptor(),
					(String) v.get(0), (String) v.get(1), etc, (String) v
							.get(2));
		}
	}

	public Vector getSelectedRowData() {
		int row = getSelectedRow();
		if ((row < 0) || (row >= getRowCount())) {
			return null;
		}
		Vector v = new Vector();
		v.add(((DefaultTableModel) this.getModel()).getValueAt(row, 0));
		v.add(((DefaultTableModel) this.getModel()).getValueAt(row, 1));
		v.add(((DefaultTableModel) this.getModel()).getValueAt(row, 2));
		v.add(((DefaultTableModel) this.getModel()).getValueAt(row, 3));
		return v;
	}

	public void removeSelectedRow() throws IndexOutOfBoundsException {
		int row = getSelectedRow();
		if ((row < 0) || (row >= getRowCount())) {
			return;
		}
		int oldSelectedRow = getSelectedRow();
		// remove the row from the model
		String removeKey = (String) getValueAt(oldSelectedRow, 0);
		CommonTools.removeServiceProperty(info.getServiceDescriptor(),
				removeKey);

		// update GUI
		refreshView();

		// change row selection
		if (getRowCount() > 0) {
			if (oldSelectedRow == 0) {
				setSelectedRow(0);
			} else {
				setSelectedRow(oldSelectedRow - 1);
			}
		}
	}

	private void initialize() {
		setAutoCreateColumnsFromModel(false);
		this.getTableHeader().setReorderingAllowed(false);
		JTextField value = new JTextField();
		value.getDocument().addDocumentListener(new DocumentListener() {

			public void removeUpdate(DocumentEvent e) {
				update(e);
			}

			private void update(DocumentEvent e) {
				try {
					modifySelectedServicePropertyValue(e.getDocument().getText(
							0, e.getDocument().getLength()));
				} catch (BadLocationException e2) {

				}
			}

			public void insertUpdate(DocumentEvent e) {
				update(e);
			}

			public void changedUpdate(DocumentEvent e) {
				update(e);
			}

		});
		this.getColumn(VALUE).setCellEditor(new DefaultCellEditor(value));

		JTextField desc = new JTextField();
		desc.getDocument().addDocumentListener(new DocumentListener() {

			public void removeUpdate(DocumentEvent e) {
				update(e);
			}

			private void update(DocumentEvent e) {
				try {
					modifySelectedServicePropertyDescriptor(e.getDocument()
							.getText(0, e.getDocument().getLength()));
				} catch (BadLocationException e2) {

				}
			}

			public void insertUpdate(DocumentEvent e) {
				update(e);
			}

			public void changedUpdate(DocumentEvent e) {
				update(e);
			}

		});
		this.getColumn(DESC).setCellEditor(new DefaultCellEditor(desc));

		refreshView();
	}

	public void singleClick() throws Exception {
		// TODO Auto-generated method stub
	}

	public void doubleClick() throws Exception {
		// TODO Auto-generated method stub
	}

	public void sort() {
		DefaultTableModel model = (DefaultTableModel) getModel();
		Vector data = model.getDataVector();
		Collections.sort(data, new ColumnSorter(0, true));
		model.fireTableStructureChanged();
	}

	public class ColumnSorter implements Comparator {
		int colIndex;

		boolean ascending;

		ColumnSorter(int colIndex, boolean ascending) {
			this.colIndex = colIndex;
			this.ascending = ascending;
		}

		public int compare(Object a, Object b) {
			Vector v1 = (Vector) a;
			Vector v2 = (Vector) b;
			String o1 = (String) v1.get(colIndex);
			String o2 = (String) v2.get(colIndex);

			if ((o1 == null) && (o2 == null)) {
				return 0;
			} else if (o1 == null) {
				return 1;
			} else if (o2 == null) {
				return -1;
			} else if (o1 instanceof Comparable) {
				if (ascending) {
					return ((Comparable) o1).compareTo(o2);
				} else {
					return ((Comparable) o2).compareTo(o1);
				}
			} else {
				if (ascending) {
					return o1.toString().compareTo(o2.toString());
				} else {
					return o2.toString().compareTo(o1.toString());
				}
			}
		}
	}
	
	public void tableChanged(TableModelEvent e){
		super.tableChanged(e);
		if(e.getColumn()==3){
			modifySelectedServicePropertyIsFromETC(((Boolean)getSelectedRowData().get(3)));
		}
		
	}

	public static class MyDefaultTableModel extends DefaultTableModel {

		public MyDefaultTableModel() {
			super();
			addColumn(NAME);
			addColumn(VALUE);
			addColumn(DESC);
			addColumn(ETC);
		}

		public Class getColumnClass(int c) {
			return getValueAt(0, c).getClass();
		}
	}

}
