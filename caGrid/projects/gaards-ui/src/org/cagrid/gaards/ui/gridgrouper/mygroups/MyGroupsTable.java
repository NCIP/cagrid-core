package org.cagrid.gaards.ui.gridgrouper.mygroups;

import gov.nih.nci.cagrid.gridgrouper.client.Group;

import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import org.cagrid.grape.GridApplication;
import org.cagrid.grape.table.GrapeBaseTable;
import org.cagrid.grape.utils.ErrorDialog;

/**
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella</A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster</A>
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings</A>
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * @version $Id: GridGrouperBaseTreeNode.java,v 1.1 2006/08/04 03:49:26 langella
 *          Exp $
 */
public class MyGroupsTable extends GrapeBaseTable {
	
	private static final long serialVersionUID = 1L;
	
	public final static String GROUP = "Groups";

	public final static String GRIDGROUPER_URI = "Grid Grouper";

	public final static String NAMESPACE = "Namespace";

	public final static String NAME = "Name";

	public MyGroupsTable() {
		super(createTableModel());
		TableColumn c = this.getColumn(GROUP);
		c.setMaxWidth(0);
		c.setMinWidth(0);
		c.setPreferredWidth(0);
		c.setResizable(false);

		this.clearTable();

	}

	public static DefaultTableModel createTableModel() {
		DefaultTableModel model = new DefaultTableModel();
		model.addColumn(GROUP);
		model.addColumn(GRIDGROUPER_URI);
		model.addColumn(NAMESPACE);
		model.addColumn(NAME);
		return model;
	}

	public synchronized void addGroup(final Group group) {
		Vector v = new Vector();
		v.add(group);
		v.add(group.getGridGrouper().getName());
		int index = group.getDisplayName().lastIndexOf(":");
		v.add(group.getDisplayName().substring(0, index));
		v.add(group.getDisplayExtension());
		addRow(v);
	}

	public synchronized void addGroups(final Set set) {
		Iterator<Group> itr = set.iterator();
		while (itr.hasNext()) {
			addGroup(itr.next());
		}
	}

	public synchronized Group getSelectedGroup() throws Exception {
		int row = getSelectedRow();
		if ((row >= 0) && (row < getRowCount())) {
			return (Group) getValueAt(row, 0);
		} else {
			throw new Exception("Please select a group!!!");
		}
	}

	public void doubleClick() {
		try {
			GridApplication.getContext().addApplicationComponent(
					new MyGroupViewer(getSelectedGroup()), 650, 400);
		} catch (Exception e) {
			ErrorDialog.showError(e);
		}

	}

	public void singleClick() {
		// TODO Auto-generated method stub

	}

}