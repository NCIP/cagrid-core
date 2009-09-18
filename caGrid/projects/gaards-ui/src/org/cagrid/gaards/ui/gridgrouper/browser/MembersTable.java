package org.cagrid.gaards.ui.gridgrouper.browser;

import gov.nih.nci.cagrid.gridgrouper.client.Membership;

import java.util.Vector;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import org.cagrid.grape.table.GrapeBaseTable;


/**
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella</A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster</A>
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings</A>
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * @version $Id: GridGrouperBaseTreeNode.java,v 1.1 2006/08/04 03:49:26 langella
 *          Exp $
 */
public class MembersTable extends GrapeBaseTable {
	
	private static final long serialVersionUID = 1L;
	
	public final static String MEMBER = "Member";

	public final static String MEMBER_NAME = "Member Name";


	public MembersTable() {
		super(createTableModel());
		TableColumn c = this.getColumn(MEMBER);
		c.setMaxWidth(0);
		c.setMinWidth(0);
		c.setPreferredWidth(0);
		c.setResizable(false);

		this.clearTable();

	}


	public static DefaultTableModel createTableModel() {
		DefaultTableModel model = new DefaultTableModel();
		model.addColumn(MEMBER);
		model.addColumn(MEMBER_NAME);
		return model;
	}


	public void addMember(final Membership m) throws Exception {
		Vector v = new Vector();
		v.add(m);
		v.add(m.getMember().getSubject().getName());
		addRow(v);
	}


	public synchronized Membership getSelectedMember() throws Exception {
		int row = getSelectedRow();
		if ((row >= 0) && (row < getRowCount())) {
			return (Membership) getValueAt(row, 0);
		} else {
			throw new Exception("Please select a member!!!");
		}
	}


	public void doubleClick() throws Exception {

	}


	public void singleClick() throws Exception {
		// TODO Auto-generated method stub

	}

}