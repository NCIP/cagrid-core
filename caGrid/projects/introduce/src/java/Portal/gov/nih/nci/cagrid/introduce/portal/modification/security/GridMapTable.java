package gov.nih.nci.cagrid.introduce.portal.modification.security;

import gov.nih.nci.cagrid.common.portal.PortalBaseTable;

import java.util.Vector;

import javax.swing.table.DefaultTableModel;


/**
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @version $Id: GridMapTable.java,v 1.1 2006-03-23 18:58:03 langella Exp $
 */
public class GridMapTable extends PortalBaseTable {
	public final static String GID = "Grid Identity";
	public final static String LOCAL_USER = "Local User";


	public GridMapTable() {
		super(createTableModel());

		this.clearTable();

	}


	public static DefaultTableModel createTableModel() {
		DefaultTableModel model = new DefaultTableModel();
		model.addColumn(GID);
		model.addColumn(LOCAL_USER);
		return model;

	}


	public synchronized void addUser(final GridMap map) {
		Vector v = new Vector();
		v.add(map.getGridIdentity());
		if (map.getLocalUser() != null) {
			v.add(map.getLocalUser());
		} else {
			v.add("");
		}
		addRow(v);
	}


	public synchronized GridMap getSelectedUser() throws Exception {
		int row = getSelectedRow();
		if ((row >= 0) && (row < getRowCount())) {
			return new GridMap((String) getValueAt(row, 0), (String) getValueAt(row, 1));
		} else {
			throw new Exception("Please select a user!!!");
		}
	}


	public synchronized void removeSelectedUser() throws Exception {
		int row = getSelectedRow();
		if ((row >= 0) && (row < getRowCount())) {
			removeRow(row);
		} else {
			throw new Exception("Please select an user!!!");
		}
	}


	public synchronized GridMap getUserAt(int index) throws Exception {
		return new GridMap((String) getValueAt(index, 0), (String) getValueAt(index, 1));
	}


	public int getUserCount() {
		return getRowCount();
	}


	public void doubleClick() throws Exception {

	}


	public void singleClick() throws Exception {
		// TODO Auto-generated method stub

	}

}