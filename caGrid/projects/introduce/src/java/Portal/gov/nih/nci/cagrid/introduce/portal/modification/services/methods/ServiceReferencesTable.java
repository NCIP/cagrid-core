package gov.nih.nci.cagrid.introduce.portal.modification.services.methods;

import gov.nih.nci.cagrid.common.portal.PortalBaseTable;
import gov.nih.nci.cagrid.introduce.beans.service.ServiceType;
import gov.nih.nci.cagrid.introduce.beans.service.ServicesType;

import java.util.Vector;

import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;


/**
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella </A>
 * 
 */
public class ServiceReferencesTable extends PortalBaseTable {

	public static String NAME = "Service Client Handles";

	public static String DATA1 = "DATA1";

	public static String DATA2 = "DATA2";

	private ServicesType services;


	public ServiceReferencesTable(ServicesType services) {
		super(createTableModel());
		this.services = services;

		initialize();
	}


	public boolean isCellEditable(int row, int column) {
		return false;
	}


	public void addRow(final ServiceType service) {
		final Vector v = new Vector();
		v.add(service.getName());
		v.add(service);
		v.add(v);

		((DefaultTableModel) this.getModel()).addRow(v);
	}


	public void modifySelectedRow(final ServiceType exception) throws Exception {
		int row = getSelectedRow();
		if ((row < 0) || (row >= getRowCount())) {
			throw new Exception("invalid row");
		}
		Vector v = (Vector) getValueAt(getSelectedRow(), 1);
		v.set(0, exception.getName());
		v.set(1, exception);
	}


	public ServiceType getSelectedRowData() throws Exception {
		return getRowData(getSelectedRow());
	}


	public ServiceType getRowData(int row) throws Exception {
		ServiceType type = ((ServiceType) getValueAt(row, 1));
		return type;
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
		this.getColumn(DATA1).setMaxWidth(0);
		this.getColumn(DATA1).setMinWidth(0);
		this.getColumn(DATA1).setPreferredWidth(0);
		this.getColumn(DATA2).setMaxWidth(0);
		this.getColumn(DATA2).setMinWidth(0);
		this.getColumn(DATA2).setPreferredWidth(0);

		if (services != null) {
			if (services.getService() != null) {
				for (int i = 0; i < services.getService().length; i++) {
					addRow(services.getService(i));
				}
			}
		}
	}


	public static DefaultTableModel createTableModel() {
		DefaultTableModel model = new DefaultTableModel();
		model.addColumn(NAME);
		model.addColumn(DATA1);
		model.addColumn(DATA2);

		return model;
	}


	public void singleClick() throws Exception {
		// TODO Auto-generated method stub

	}


	public void doubleClick() throws Exception {
		// TODO Auto-generated method stub

	}
}