package org.cagrid.gaards.ui.gts;

import gov.nih.nci.cagrid.gts.bean.Permission;

import java.util.Vector;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import org.cagrid.grape.table.GrapeBaseTable;


/**
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @version $Id: TrustedAuthorityTable.java,v 1.1 2006/03/27 18:52:57 langella
 *          Exp $
 */
public class PermissionsTable extends GrapeBaseTable {
	
	private static final long serialVersionUID = 1L;
	
	public final static String PERMISSION = "permission";

	public final static String GRID_IDENTITY = "Grid Identity";

	public final static String TRUST_AUTHORITY = "Trusted Authority";

	public final static String ROLE = "Role";


	public PermissionsTable() {
		super(createTableModel());
		TableColumn c = this.getColumn(PERMISSION);
		c.setMaxWidth(0);
		c.setMinWidth(0);
		c.setPreferredWidth(0);
		c.setResizable(false);

		c = this.getColumn(GRID_IDENTITY);
		c.setMaxWidth(500);
		c.setMinWidth(500);

		c = this.getColumn(ROLE);
		c.setMaxWidth(100);
		c.setMinWidth(100);

		this.clearTable();

	}


	public static DefaultTableModel createTableModel() {
		DefaultTableModel model = new DefaultTableModel();
		model.addColumn(PERMISSION);
		model.addColumn(GRID_IDENTITY);
		model.addColumn(TRUST_AUTHORITY);
		model.addColumn(ROLE);
		return model;

	}


	public void addPermission(final Permission perm) {
		Vector v = new Vector();
		v.add(perm);
		v.add(perm.getGridIdentity());
		v.add(perm.getTrustedAuthorityName());
		v.add(perm.getRole());
		addRow(v);
	}


	public synchronized Permission getSelectedPermission() throws Exception {
		int row = getSelectedRow();
		if ((row >= 0) && (row < getRowCount())) {
			return (Permission) getValueAt(row, 0);
		} else {
			throw new Exception("Please select a Permission!!!");
		}
	}


	public synchronized void removeSelectedPermission() throws Exception {
		int row = getSelectedRow();
		if ((row >= 0) && (row < getRowCount())) {
			removeRow(row);
		} else {
			throw new Exception("Please select a Permission!!!");
		}
	}


	public void doubleClick() throws Exception {
		int row = getSelectedRow();
		if ((row >= 0) && (row < getRowCount())) {

		} else {
			throw new Exception("Please select a Permission!!!");
		}

	}


	public void singleClick() throws Exception {
		// TODO Auto-generated method stub

	}

}