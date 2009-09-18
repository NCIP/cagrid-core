package org.cagrid.gaards.ui.gts;

import gov.nih.nci.cagrid.gts.bean.AuthorityGTS;
import gov.nih.nci.cagrid.gts.bean.AuthorityPrioritySpecification;
import gov.nih.nci.cagrid.gts.bean.AuthorityPriorityUpdate;

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
public class AuthorityTable extends GrapeBaseTable {
	
	private static final long serialVersionUID = 1L;
	
	public final static String AUTHORITY = "authority";

	public final static String SERVICE_URI = "GTS URI";

	public final static String PRIORITY = "Priority";

	private AuthorityManagerWindow window;


	public AuthorityTable(AuthorityManagerWindow window) {
		super(createTableModel());
		this.window = window;
		TableColumn c = this.getColumn(AUTHORITY);
		c.setMaxWidth(0);
		c.setMinWidth(0);
		c.setPreferredWidth(0);
		c.setResizable(false);

		c = this.getColumn(SERVICE_URI);
		c.setMaxWidth(500);
		c.setMinWidth(500);
		this.clearTable();
	}


	public static DefaultTableModel createTableModel() {
		DefaultTableModel model = new DefaultTableModel();
		model.addColumn(AUTHORITY);
		model.addColumn(SERVICE_URI);
		model.addColumn(PRIORITY);
		return model;

	}


	public void addAuthority(final AuthorityGTS gts) {
		Vector v = new Vector();
		v.add(gts);
		v.add(gts.getServiceURI());
		v.add(String.valueOf(gts.getPriority()));
		int index = -1;
		for (int i = 0; i < getRowCount(); i++) {

			AuthorityGTS auth = (AuthorityGTS) getValueAt(i, 0);
			if (gts.getPriority() < auth.getPriority()) {
				index = i;
				break;
			}
		}

		if (index == -1) {
			addRow(v);
		} else {
			((DefaultTableModel) this.getModel()).insertRow(index, v);
		}
	}


	public synchronized AuthorityGTS getSelectedAuthority() throws Exception {
		int row = getSelectedRow();
		if ((row >= 0) && (row < getRowCount())) {
			return (AuthorityGTS) getValueAt(row, 0);
		} else {
			throw new Exception("Please select an authority!!!");
		}
	}


	public synchronized void removeSelectedAuthority() throws Exception {
		int row = getSelectedRow();
		if ((row >= 0) && (row < getRowCount())) {
			removeRow(row);
		} else {
			throw new Exception("Please select an authority!!!");
		}
	}


	public void doubleClick() throws Exception {
		int row = getSelectedRow();
		if ((row >= 0) && (row < getRowCount())) {
			window.viewModifyAuthority();
		} else {
			throw new Exception("Please select a trust level!!!");
		}

	}


	public synchronized void increasePriority() throws Exception {
		int row = getSelectedRow();
		if ((row >= 0) && (row < getRowCount())) {
			AuthorityGTS selected = (AuthorityGTS) getValueAt(row, 0);
			if (row > 0) {
				int nrow = (row - 1);
				AuthorityGTS other = (AuthorityGTS) getValueAt(nrow, 0);
				other.setPriority(other.getPriority() + 1);
				selected.setPriority(selected.getPriority() - 1);
				setValueAt(other, row, 0);
				setValueAt(other.getServiceURI(), row, 1);
				setValueAt(String.valueOf(other.getPriority()), row, 2);
				setValueAt(selected, nrow, 0);
				setValueAt(selected.getServiceURI(), nrow, 1);
				setValueAt(String.valueOf(selected.getPriority()), nrow, 2);
				getSelectionModel().setSelectionInterval(nrow, nrow);
			}
		} else {
			throw new Exception("Please select an authority!!!");
		}
	}


	public synchronized void decreasePriority() throws Exception {
		int row = getSelectedRow();
		if ((row >= 0) && (row < getRowCount())) {
			AuthorityGTS selected = (AuthorityGTS) getValueAt(row, 0);
			int rowCount = getRowCount() - 1;
			if (row < rowCount) {
				int nrow = (row + 1);
				AuthorityGTS other = (AuthorityGTS) getValueAt(nrow, 0);
				other.setPriority(other.getPriority() - 1);
				selected.setPriority(selected.getPriority() + 1);
				setValueAt(other, row, 0);
				setValueAt(other.getServiceURI(), row, 1);
				setValueAt(String.valueOf(other.getPriority()), row, 2);
				setValueAt(selected, nrow, 0);
				setValueAt(selected.getServiceURI(), nrow, 1);
				setValueAt(String.valueOf(selected.getPriority()), nrow, 2);
				getSelectionModel().setSelectionInterval(nrow, nrow);
			}
		} else {
			throw new Exception("Please select an authority!!!");
		}
	}


	public synchronized AuthorityPriorityUpdate getPriorityUpdate() {
		AuthorityPriorityUpdate update = new AuthorityPriorityUpdate();
		AuthorityPrioritySpecification[] specs = new AuthorityPrioritySpecification[getRowCount()];
		for (int i = 0; i < getRowCount(); i++) {
			AuthorityGTS auth = (AuthorityGTS) getValueAt(i, 0);
			specs[i] = new AuthorityPrioritySpecification();
			specs[i].setServiceURI(auth.getServiceURI());
			specs[i].setPriority(auth.getPriority());
		}
		update.setAuthorityPrioritySpecification(specs);
		return update;
	}


	public void singleClick() throws Exception {
		// TODO Auto-generated method stub

	}

}