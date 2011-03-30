package org.cagrid.gaards.ui.gridgrouper.browser;

import gov.nih.nci.cagrid.gridgrouper.client.MembershipRequestHistory;

import java.text.DateFormat;
import java.util.Date;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;

import org.cagrid.grape.table.GrapeBaseTable;


public class MembershipRequestHistoryTable extends GrapeBaseTable {
	
	private static final long serialVersionUID = 1L;
	
	public MembershipRequestHistoryTable() {
		super(createTableModel());
		this.clearTable();

	}


	public static DefaultTableModel createTableModel() {
		DefaultTableModel model = new DefaultTableModel();
		model.addColumn("Status Date");
		model.addColumn("Reviewer");
		model.addColumn("Status");
		return model;
	}


	public void addMembershipRequestHistory(final MembershipRequestHistory m) throws Exception {
		Vector<String> v = new Vector<String>();
		v.add(DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(new Date(m.getUpdateDate())));
		if (m.getReviewer() == null) {
			v.add("");
		} else {
			v.add(m.getReviewer().getSubjectId());
		}
		v.add(m.getStatus().getValue());
		addRow(v);
	}

	public void doubleClick() throws Exception {

	}


	public void singleClick() throws Exception {

	}

}