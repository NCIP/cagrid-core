package org.cagrid.gaards.ui.cds;

import java.util.Vector;

import javax.swing.table.DefaultTableModel;

import org.cagrid.grape.table.GrapeBaseTable;

/**
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @version $Id: AdminsTable.java,v 1.2 2008-11-20 15:29:42 langella Exp $
 */
public class AdminsTable extends GrapeBaseTable {
	
	private static final long serialVersionUID = 1L;
	
	public final static String GRID_IDENTITY = "Grid Identity";

	public AdminsTable() {
		super(createTableModel());
		this.clearTable();
	}

	public static DefaultTableModel createTableModel() {
		DefaultTableModel model = new DefaultTableModel();
		model.addColumn(GRID_IDENTITY);
		return model;

	}

	public void addAdmin(final String admin) {
		Vector v = new Vector();
		v.add(admin);
		addRow(v);
	}

	public synchronized String getSelectedAdmin() throws Exception {
		int row = getSelectedRow();
		if ((row >= 0) && (row < getRowCount())) {
			return (String) getValueAt(row, 0);
		} else {
			throw new Exception("Please select an admin!!!");
		}
	}

	public synchronized void removeSelectedAdmin() throws Exception {
		int row = getSelectedRow();
		if ((row >= 0) && (row < getRowCount())) {
			removeRow(row);
		} else {
			throw new Exception("Please select an admin!!!");
		}
	}

	public void doubleClick() throws Exception {
	}

	public void singleClick() throws Exception {
		// TODO Auto-generated method stub

	}

}