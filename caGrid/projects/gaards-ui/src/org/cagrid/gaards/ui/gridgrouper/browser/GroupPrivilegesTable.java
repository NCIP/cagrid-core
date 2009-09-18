package org.cagrid.gaards.ui.gridgrouper.browser;

import java.util.Vector;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import org.cagrid.grape.GridApplication;
import org.cagrid.grape.table.GrapeBaseTable;


/**
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella</A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster</A>
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings</A>
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * @version $Id: GridGrouperBaseTreeNode.java,v 1.1 2006/08/04 03:49:26 langella
 *          Exp $
 */
public class GroupPrivilegesTable extends GrapeBaseTable {
	
	private static final long serialVersionUID = 1L;
	
	public final static String CADDY = "Caddy";

	public final static String IDENTITY = "Identity";

	public final static String ADMIN = "Admin";

	public final static String UPDATE = "Update";

	public final static String READ = "Read";

	public final static String VIEW = "View";

	public final static String OPTIN = "Optin";

	public final static String OPTOUT = "Optout";

	private GroupBrowser browser;


	public GroupPrivilegesTable(GroupBrowser browser) {
		super(createTableModel());
		this.browser = browser;
		TableColumn c = this.getColumn(CADDY);
		c.setMaxWidth(0);
		c.setMinWidth(0);
		c.setPreferredWidth(0);
		c.setResizable(false);

		c = this.getColumn(IDENTITY);
		c.setPreferredWidth(300);
		c = this.getColumn(ADMIN);
		c.setPreferredWidth(25);
		c = this.getColumn(UPDATE);
		c.setPreferredWidth(25);
		c = this.getColumn(READ);
		c.setPreferredWidth(25);
		c = this.getColumn(VIEW);
		c.setPreferredWidth(25);
		c = this.getColumn(OPTIN);
		c.setPreferredWidth(25);
		c = this.getColumn(OPTOUT);
		c.setPreferredWidth(25);
		this.clearTable();

	}


	public static DefaultTableModel createTableModel() {
		DefaultTableModel model = new DefaultTableModel();
		model.addColumn(CADDY);
		model.addColumn(IDENTITY);
		model.addColumn(ADMIN);
		model.addColumn(UPDATE);
		model.addColumn(READ);
		model.addColumn(VIEW);
		model.addColumn(OPTIN);
		model.addColumn(OPTOUT);
		return model;

	}


	public void addPrivilege(final GroupPrivilegeCaddy priv) {
		Vector v = new Vector();
		v.add(priv);
		v.add(priv.getIdentity());
		v.add(getDisplayText(priv.hasAdmin()));
		v.add(getDisplayText(priv.hasUpdate()));
		v.add(getDisplayText(priv.hasRead()));
		v.add(getDisplayText(priv.hasView()));
		v.add(getDisplayText(priv.hasOptin()));
		v.add(getDisplayText(priv.hasOptout()));
		addRow(v);
	}


	private String getDisplayText(boolean has) {
		if (has) {
			return "Y";
		} else {
			return "N";
		}
	}


	public synchronized GroupPrivilegeCaddy getSelectedPrivilege() throws Exception {
		int row = getSelectedRow();
		if ((row >= 0) && (row < getRowCount())) {
			return (GroupPrivilegeCaddy) getValueAt(row, 0);
		} else {
			throw new Exception("Please select a privilege!!!");
		}
	}


	public synchronized void removeSelectedPrivilege() throws Exception {
		int row = getSelectedRow();
		if ((row >= 0) && (row < getRowCount())) {
			removeRow(row);
		} else {
			throw new Exception("Please select a privilege!!!");
		}
	}


	public void doubleClick() throws Exception {
		GroupPrivilegeCaddy caddy = getSelectedPrivilege();
		GridApplication.getContext().addApplicationComponent(new GroupPrivilegeWindow(browser, caddy), 650, 250);
	}


	public void singleClick() throws Exception {
		// TODO Auto-generated method stub

	}

}