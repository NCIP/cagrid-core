package org.cagrid.gaards.ui.gridgrouper.browser;

import gov.nih.nci.cagrid.gridgrouper.client.MembershipRequest;

import java.util.Vector;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import org.cagrid.grape.table.GrapeBaseTable;

public class MembershipRequestsTable extends GrapeBaseTable {
	
	private static final long serialVersionUID = 1L;
	
	public final static String MEMBERREQUEST = "Membership Request";

	public final static String MEMBER_IDENTITY = "Member Identity";


	public MembershipRequestsTable() {
		super(createTableModel());
		TableColumn c = this.getColumn(MEMBERREQUEST);
		c.setMaxWidth(0);
		c.setMinWidth(0);
		c.setPreferredWidth(0);
		c.setResizable(false);

		this.clearTable();

	}


	public static DefaultTableModel createTableModel() {
		DefaultTableModel model = new DefaultTableModel();
		model.addColumn(MEMBERREQUEST);
		model.addColumn(MEMBER_IDENTITY);
		return model;
	}


	public void addMembershipRequests(final MembershipRequest m) throws Exception {
		Vector v = new Vector();
		v.add(m);
		v.add(m.getRequestorId());
		addRow(v);
	}


	public synchronized MembershipRequest getSelectedMembershipRequest() throws Exception {
		int row = getSelectedRow();
		if ((row >= 0) && (row < getRowCount())) {
			return (MembershipRequest) getValueAt(row, 0);
		} else {
			throw new Exception("Please select a member!!!");
		}
	}


	public void doubleClick() throws Exception {

	}


	public void singleClick() throws Exception {

	}

}