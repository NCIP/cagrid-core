package gov.nih.nci.cagrid.introduce.portal.modification.services.methods;

import gov.nih.nci.cagrid.common.portal.PortalBaseTable;
import gov.nih.nci.cagrid.introduce.beans.method.MethodType;
import gov.nih.nci.cagrid.introduce.beans.method.MethodTypeOutput;

import java.util.Vector;

import javax.swing.table.DefaultTableModel;


/**
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella </A>
 */
public class OutputTypeTable extends PortalBaseTable {

	public static String ISARRAY = "Is Array";

	public static String NAMESPACE = "Namespace";

	public static String TYPE = "Type";

	public static String DATA1 = "DATA1";

	public static String DATA2 = "DATA2";

	public static String DESC = "Description";

	private MethodType method;


	public OutputTypeTable(MethodType method) {
		super(createTableModel());
		this.method = method;
		initialize();
	}


	public boolean isCellEditable(int row, int column) {
		return true;
		// if (column == 4) {
		// return true;
		// } else {
		// return false;
		// }
	}


	private void initialize() {
		this.getTableHeader().setReorderingAllowed(false);
		this.getColumn(DATA1).setMaxWidth(0);
		this.getColumn(DATA1).setMinWidth(0);
		this.getColumn(DATA1).setPreferredWidth(0);
		this.getColumn(DATA2).setMaxWidth(0);
		this.getColumn(DATA2).setMinWidth(0);
		this.getColumn(DATA2).setPreferredWidth(0);
		MethodTypeOutput output = method.getOutput();
		if (output != null) {
			final Vector v = new Vector();
			v.add(new Boolean(output.isIsArray()));
			v.add(output.getQName().getNamespaceURI());
			v.add(output.getQName().getLocalPart());
			String desc = output.getDescription();
			if (desc == null) {
				desc = "";
				output.setDescription(desc);
			}
			v.add(desc);
			v.add(output);
			v.add(v);
			((DefaultTableModel) this.getModel()).addRow(v);
		}
	}


	public void modifyRow(int row, final MethodTypeOutput output) throws Exception {
		if ((row < 0) || (row >= getRowCount())) {
			throw new Exception("invalid row");
		}
		Vector v = (Vector) getValueAt(row, 5);
		v.set(0, (new Boolean(output.isIsArray())));
		v.set(1, output.getQName().getNamespaceURI());
		v.set(2, output.getQName().getLocalPart());
		if (output.getDescription() == null) {
			output.setDescription("");
		}
		v.set(3, output.getDescription());
		v.set(4, output);
		v.set(5, v);
		paint(getGraphics());
	}


	public void modifySelectedRow(final MethodTypeOutput output) throws Exception {
		modifyRow(getSelectedRow(), output);
	}


	public MethodTypeOutput getRowData(int row) throws Exception {
		MethodTypeOutput output = ((MethodTypeOutput) getValueAt(row, 4));
		boolean isArray = ((Boolean) getValueAt(row, 0)).booleanValue();
		String description = ((String) getValueAt(row, 3));
		output.setIsArray(isArray);
		output.setDescription(description);
		return output;
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


	public MethodTypeOutput getSelectedRowData() throws Exception {
		int row = getSelectedRow();
		if ((row < 0) || (row >= getRowCount())) {
			throw new Exception("invalid row");
		}
		return getRowData(row);
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
			addColumn(ISARRAY);
			addColumn(NAMESPACE);
			addColumn(TYPE);
			addColumn(DESC);
			addColumn("DATA1");
			addColumn("DATA2");
		}


		public Class getColumnClass(int c) {
			return getValueAt(0, c).getClass();
		}
	}
}