package gov.nih.nci.cagrid.introduce.portal.modification.services.methods;

import gov.nih.nci.cagrid.common.portal.PortalBaseTable;
import gov.nih.nci.cagrid.introduce.beans.method.MethodType;
import gov.nih.nci.cagrid.introduce.beans.method.MethodTypeExceptionsException;
import gov.nih.nci.cagrid.introduce.beans.service.ServiceType;

import java.util.Vector;

import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.xml.namespace.QName;


/**
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella </A>
 */
public class ExceptionsTable extends PortalBaseTable {

	public static String NAMESPACE = "Namespace";

	public static String NAME = "Name";

	public static String DESC = "Description";

	public static String IS_CREATED = "Created";

	public static String DATA1 = "DATA1";

	private MethodType method;

	private ServiceType service;


	public ExceptionsTable(MethodType method, ServiceType service) {
		super(createTableModel());
		this.method = method;
		this.service = service;

		initialize();
	}


	public boolean isCellEditable(int row, int column) {
		if (column == 2) {
			return true;
		}
		return false;
	}


	public void addRow(final QName exception, boolean isCreated, String description) {
		final Vector v = new Vector();
		v.add(exception.getNamespaceURI());
		v.add(exception.getLocalPart());
		if (description == null) {
			v.add("");
		} else {
			v.add(description);
		}
		v.add(new Boolean(isCreated));
		v.add(v);

		((DefaultTableModel) this.getModel()).addRow(v);
	}


	public MethodTypeExceptionsException getSelectedRowData() throws Exception {
		return getRowData(getSelectedRow());
	}


	public MethodTypeExceptionsException getRowData(int row) throws Exception {
		MethodTypeExceptionsException exception = new MethodTypeExceptionsException();
		if (((Boolean) getValueAt(row, 3)).booleanValue()) {
			exception.setQname(new QName((String) getValueAt(row, 0), (String) getValueAt(row, 1)));
		}
		exception.setName((String) getValueAt(row, 1));
		exception.setDescription(((String) getValueAt(row, 2)));
		return exception;
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


	private void initialize() {
		this.setColumnSelectionAllowed(false);
		this.setRowSelectionAllowed(true);
		this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.getTableHeader().setReorderingAllowed(false);
		this.getColumn(IS_CREATED).setMaxWidth(0);
		this.getColumn(IS_CREATED).setMinWidth(0);
		this.getColumn(IS_CREATED).setPreferredWidth(0);
		this.getColumn(DATA1).setMaxWidth(0);
		this.getColumn(DATA1).setMinWidth(0);
		this.getColumn(DATA1).setPreferredWidth(0);

		if (method.getExceptions() != null) {
			if (method.getExceptions().getException() != null) {
				for (int i = 0; i < method.getExceptions().getException().length; i++) {
					if (method.getExceptions().getException(i).getDescription() == null) {
						method.getExceptions().getException(i).setDescription("");
					}
					if (method.getExceptions().getException(i).getQname() != null) {
						addRow(method.getExceptions().getException(i).getQname(), true, method.getExceptions()
							.getException(i).getDescription());
					} else {
						addRow(new QName(service.getNamespace() + "/types", method.getExceptions().getException(i)
							.getName()), false, method.getExceptions().getException(i).getDescription());
					}
				}
			}
		}
	}


	public static DefaultTableModel createTableModel() {
		DefaultTableModel model = new DefaultTableModel();
		model.addColumn(NAMESPACE);
		model.addColumn(NAME);
		model.addColumn(DESC);
		model.addColumn(IS_CREATED);
		model.addColumn(DATA1);

		return model;
	}


	public void singleClick() throws Exception {
		// TODO Auto-generated method stub

	}


	public void doubleClick() throws Exception {
		// TODO Auto-generated method stub

	}
}