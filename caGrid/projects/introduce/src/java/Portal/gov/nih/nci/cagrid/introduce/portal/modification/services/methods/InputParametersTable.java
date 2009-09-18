package gov.nih.nci.cagrid.introduce.portal.modification.services.methods;

import gov.nih.nci.cagrid.common.portal.PortalBaseTable;
import gov.nih.nci.cagrid.introduce.beans.method.MethodType;
import gov.nih.nci.cagrid.introduce.beans.method.MethodTypeInputsInput;

import java.util.Vector;

import javax.swing.table.DefaultTableModel;
import javax.xml.namespace.QName;


/**
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella </A>
 */
public class InputParametersTable extends PortalBaseTable {

	public static String NAME = "Name";
	public static String ISARRAY = "Is Array";
	public static String NAMESPACE = "Namespace";
	public static String TYPE = "Type";
	public static String DESC = "Description";
	public static String DATA1 = "DATA1";

	private MethodType method;


	public InputParametersTable(MethodType method) {
		super(createTableModel());
		this.method = method;
		initialize();
	}


	public boolean isCellEditable(int row, int column) {
		return true;
	}


	public void addRow(final MethodTypeInputsInput input) {
		final Vector v = new Vector();
		v.add(input.getName());
		v.add(new Boolean(input.isIsArray()));
		v.add(input.getQName().getNamespaceURI());
		v.add(input.getQName().getLocalPart());
		String desc = input.getDescription();
		if (desc == null) {
			desc = "";
			input.setDescription("");
		}
		v.add(desc);
		v.add(v);

		((DefaultTableModel) this.getModel()).addRow(v);
		this.setRowSelectionInterval(this.getModel().getRowCount() - 1, this.getModel().getRowCount() - 1);
	}


	public void modifyRow(final MethodTypeInputsInput input, int row) throws Exception {
		if ((row < 0) || (row >= getRowCount())) {
			throw new Exception("invalid row");
		}
		Vector v = (Vector) getValueAt(row, 5);
		v.set(0, input.getName());
		v.set(1, new Boolean(input.isIsArray()));
		v.set(2, input.getQName().getNamespaceURI());
		v.set(3, input.getQName().getLocalPart());
		v.set(4, input.getDescription());
		v.set(5, v);
	}


	public void modifySelectedRow(final MethodTypeInputsInput input) throws Exception {
		int row = getSelectedRow();
		if ((row < 0) || (row >= getRowCount())) {
			throw new Exception("invalid row");
		}
		Vector v = (Vector) getValueAt(getSelectedRow(), 5);
		v.set(0, input.getName());
		v.set(1, new Boolean(input.isIsArray()));
		v.set(2, input.getQName().getNamespaceURI());
		v.set(3, input.getQName().getLocalPart());
		v.set(4, input.getDescription());
		v.set(5, v);
	}


	public void moveSelectedRowUp() throws Exception {
		if (getSelectedRow() > 0) {
			MethodTypeInputsInput input1 = getRowData(getSelectedRow());
			MethodTypeInputsInput input2 = getRowData(getSelectedRow() - 1);
			modifySelectedRow(input2);
			modifyRow(input1, getSelectedRow() - 1);
			setRowSelectionInterval(getSelectedRow() - 1, getSelectedRow() - 1);
			this.paint(this.getGraphics());
		}
	}


	public void moveSelectedRowDown() throws Exception {
		if ((getSelectedRow() < getRowCount() - 1) && (getRowCount() > 1)) {
			MethodTypeInputsInput input1 = getRowData(getSelectedRow());
			MethodTypeInputsInput input2 = getRowData(getSelectedRow() + 1);
			modifySelectedRow(input2);
			modifyRow(input1, getSelectedRow() + 1);
			setRowSelectionInterval(getSelectedRow() + 1, getSelectedRow() + 1);
			this.paint(this.getGraphics());
		}
	}


	public void removeSelectedRow() throws Exception {
		int row = getSelectedRow();
		if ((row < 0) || (row >= getRowCount())) {
			throw new Exception("invalid row");
		}
		int oldSelectedRow = getSelectedRow();
		((DefaultTableModel) getModel()).removeRow(oldSelectedRow);
		if (oldSelectedRow == 0) {
			oldSelectedRow++;
		}
		if (getRowCount() > 0) {
			setRowSelectionInterval(oldSelectedRow - 1, oldSelectedRow - 1);
		}
	}


	public MethodTypeInputsInput getRowData(int row) throws Exception {
		if ((row < 0) || (row >= getRowCount())) {
			throw new Exception("invalid row");
		}
		MethodTypeInputsInput input = new MethodTypeInputsInput();

		String name = ((String) getValueAt(row, 0));
		boolean isArray = ((Boolean) getValueAt(row, 1)).booleanValue();
		String namespace = ((String) getValueAt(row, 2));
		String type = ((String) getValueAt(row, 3));
		String description = ((String) getValueAt(row, 4));

		input.setIsArray(isArray);

		if ((name != null) && !name.equals("")) {
			input.setName(name);
		}
		if ((namespace != null) && !namespace.equals("") && (type != null) && !type.equals("")) {
			input.setQName(new QName(namespace, type));
		}

		input.setDescription(description);

		return input;
	}


	public MethodTypeInputsInput getSelectedRowData() throws Exception {
		return getRowData(getSelectedRow());
	}


	private void initialize() {
		this.getTableHeader().setReorderingAllowed(false);
		this.getColumn(DATA1).setMaxWidth(0);
		this.getColumn(DATA1).setMinWidth(0);
		this.getColumn(DATA1).setPreferredWidth(0);
		if ((method.getInputs() != null) && (method.getInputs().getInput() != null)) {
			for (int i = 0; i < method.getInputs().getInput().length; i++) {
				addRow(method.getInputs().getInput(i));
			}
		}

	}


	public static MyDefaultTableModel createTableModel() {
		MyDefaultTableModel model = new MyDefaultTableModel();
		return model;
	}


	public void singleClick() throws Exception {
		// TODO Auto-generated method stub

	}


	public void doubleClick() throws Exception {
		// TODO Auto-generated method stub

	}


	public static class MyDefaultTableModel extends DefaultTableModel {

		public MyDefaultTableModel() {
			super();
			addColumn(NAME);
			addColumn(ISARRAY);
			addColumn(NAMESPACE);
			addColumn(TYPE);
			addColumn(DESC);
			addColumn(DATA1);
		}


		public Class getColumnClass(int c) {
			return getValueAt(0, c).getClass();
		}
	}
}
