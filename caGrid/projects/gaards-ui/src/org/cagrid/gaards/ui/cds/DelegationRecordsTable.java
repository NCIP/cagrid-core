package org.cagrid.gaards.ui.cds;

import gov.nih.nci.cagrid.common.Runner;

import java.util.Date;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import org.cagrid.gaards.cds.common.DelegationIdentifier;
import org.cagrid.gaards.cds.common.DelegationRecord;
import org.cagrid.grape.GridApplication;
import org.cagrid.grape.table.GrapeBaseTable;
import org.cagrid.grape.utils.ErrorDialog;

/**
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @version $Id: DelegationRecordsTable.java,v 1.1 2007/11/19 17:05:26 langella
 *          Exp $
 */
public class DelegationRecordsTable extends GrapeBaseTable {
	
	private static final long serialVersionUID = 1L;
	
	public final static String RECORD = "record";

	public final static String GRID_IDENTITY = "Grid Identity";

	public final static String STATUS = "Delegation Status";

	public final static String EXPIRED = "Expired?";

	public final static String EXPIRATION = "Expiration";

	private SessionPanel session;

	public DelegationRecordsTable(SessionPanel session) {
		super(createTableModel());
		this.session = session;
		TableColumn c = this.getColumn(RECORD);
		c.setMaxWidth(0);
		c.setMinWidth(0);
		c.setPreferredWidth(0);
		c.setResizable(false);

		c = this.getColumn(GRID_IDENTITY);
		c.setMinWidth(350);
		c.setPreferredWidth(0);

		this.clearTable();

	}

	public static DefaultTableModel createTableModel() {
		DefaultTableModel model = new DefaultTableModel();
		model.addColumn(RECORD);
		model.addColumn(GRID_IDENTITY);
		model.addColumn(STATUS);
		model.addColumn(EXPIRED);
		model.addColumn(EXPIRATION);

		return model;

	}

	public void addRecord(final DelegationRecord r) {
		Vector v = new Vector();
		v.add(r);
		v.add(r.getGridIdentity());
		v.add(r.getDelegationStatus().getValue());
		java.util.Date d = new java.util.Date(r.getExpiration());
		Date now = new Date();
		if (now.after(d)) {
			v.add("Expired");
		} else {
			v.add("Valid");
		}
		if (r.getExpiration() <= 0) {
			v.add("");
		} else {
			v.add(d.toString());
		}
		addRow(v);
	}

	public synchronized DelegationRecord getSelectedRecord() throws Exception {
		int row = getSelectedRow();
		if ((row >= 0) && (row < getRowCount())) {
			return (DelegationRecord) getValueAt(row, 0);
		} else {
			throw new Exception("Please select a delegated credential!!!");
		}
	}
	
	public synchronized DelegationRecord getRecord(int row) throws Exception {
		if ((row >= 0) && (row < getRowCount())) {
			return (DelegationRecord) getValueAt(row, 0);
		} else {
			return null;
		}
	}

	public void removeRecord(DelegationIdentifier id) {
		for (int i = 0; i < getRowCount(); i++) {
			DelegationRecord r = (DelegationRecord) getValueAt(i, 0);
			if (r.getDelegationIdentifier().equals(id)) {
				removeRow(i);
			}
		}
	}

	public synchronized void removeSelectedRecord() throws Exception {
		int row = getSelectedRow();
		if ((row >= 0) && (row < getRowCount())) {
			removeRow(row);
		} else {
			throw new Exception("Please select a delegated credential!!!");
		}
	}

	public void doubleClick() {
		Runner runner = new Runner() {
			public void execute() {
				try {
					GridApplication.getContext().addApplicationComponent(
							new DelegatedCredentialWindow(session.getSession(),
									getSelectedRecord()), 700, 500);
				} catch (Exception e) {
					ErrorDialog.showError(e);
				}
			}
		};
		try {
			GridApplication.getContext().executeInBackground(runner);
		} catch (Exception t) {
			t.getMessage();
		}

	}

	public void singleClick() throws Exception {
		// TODO Auto-generated method stub

	}

}