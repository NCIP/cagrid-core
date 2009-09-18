package org.cagrid.gaards.ui.cds;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import org.cagrid.gaards.cds.common.DelegatedCredentialAuditRecord;
import org.cagrid.grape.GridApplication;
import org.cagrid.grape.table.GrapeBaseTable;
import org.cagrid.grape.utils.ErrorDialog;

/**
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @version $Id: DelegatedCredentialAuditRecordTable.java,v 1.1 2008/01/07
 *          20:21:57 langella Exp $
 */
public class DelegatedCredentialAuditRecordTable extends GrapeBaseTable {
	
	private static final long serialVersionUID = 1L;

	public final static String RECORD = "Record";

	public final static String GRID_IDENTITY = "Source";

	public final static String EVENT_TYPE = "Event Type";

	public final static String OCCURRED_AT = "Occurred At";

	public final static String MESSAGE = "Message";

	public DelegatedCredentialAuditRecordTable() {
		super(createTableModel());
		TableColumn c = getColumn(RECORD);
		c.setMaxWidth(0);
		c.setMinWidth(0);
		c.setPreferredWidth(0);
		c.setResizable(false);
		this.clearTable();
	}

	public static DefaultTableModel createTableModel() {
		DefaultTableModel model = new DefaultTableModel();	
		model.addColumn(RECORD);
		model.addColumn(GRID_IDENTITY);
		model.addColumn(EVENT_TYPE);
		model.addColumn(OCCURRED_AT);
		model.addColumn(MESSAGE);
		return model;

	}

	public void addRecords(final List<DelegatedCredentialAuditRecord> list) {
		List<DelegatedCredentialAuditRecord> sorted = new ArrayList<DelegatedCredentialAuditRecord>();
		for (int i = 0; i < list.size(); i++) {
			boolean inserted = false;
			for (int j = 0; j < sorted.size(); j++) {
				if (list.get(i).getOccurredAt() > sorted.get(j).getOccurredAt()) {
					sorted.add(j, list.get(i));
					inserted = true;
					break;
				}
			}
			if (!inserted) {
				sorted.add(list.get(i));
			}
		}
		for (int i = 0; i < sorted.size(); i++) {
			DelegatedCredentialAuditRecord r = sorted.get(i);
			Vector v = new Vector();
			v.add(r);
			v.add(r.getSourceGridIdentity());
			v.add(r.getEvent().getValue());
			v.add((new Date(r.getOccurredAt())).toString());
			v.add(r.getMessage());
			addRow(v);
		}
	}

	public synchronized DelegatedCredentialAuditRecord getSelectedRecord()
			throws Exception {
		int row = getSelectedRow();
		if ((row >= 0) && (row < getRowCount())) {
			return (DelegatedCredentialAuditRecord) getValueAt(row, 0);
		} else {
			throw new Exception("Please select an audit record!!!");
		}
	}

	public synchronized void removeSelectedIdentity() throws Exception {
		int row = getSelectedRow();
		if ((row >= 0) && (row < getRowCount())) {
			removeRow(row);
		} else {
			throw new Exception("Please select an audit record!!!");
		}
	}

	public void doubleClick() throws Exception {
		try {
			GridApplication.getContext().addApplicationComponent(
					new DelegatedCredentialAuditRecordWindow(
							getSelectedRecord()), 600, 350);
		} catch (Exception ex) {
			ErrorDialog.showError(ex.getMessage(), ex);
		}
	}

	public void singleClick() throws Exception {
		// TODO Auto-generated method stub

	}

}