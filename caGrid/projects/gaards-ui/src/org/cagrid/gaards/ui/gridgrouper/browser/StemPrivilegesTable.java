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
public class StemPrivilegesTable extends GrapeBaseTable {
	
	private static final long serialVersionUID = 1L;
	
	public final static String CADDY = "Caddy";

	public final static String IDENTITY = "Identity";

	public final static String CREATOR = "Create";

	public final static String STEMMER = "Stem";

	private StemBrowser browser;


	public StemPrivilegesTable(StemBrowser browser) {
		super(createTableModel());
		this.browser = browser;
		TableColumn c = this.getColumn(CADDY);
		c.setMaxWidth(0);
		c.setMinWidth(0);
		c.setPreferredWidth(0);
		c.setResizable(false);

		c = this.getColumn(CREATOR);
		c.setMinWidth(60);
		c.setMaxWidth(60);
		c.setPreferredWidth(0);

		c = this.getColumn(STEMMER);
		c.setMinWidth(60);
		c.setMaxWidth(60);
		c.setPreferredWidth(0);

		this.clearTable();

	}


	public static DefaultTableModel createTableModel() {
		DefaultTableModel model = new DefaultTableModel();
		model.addColumn(CADDY);
		model.addColumn(IDENTITY);
		model.addColumn(STEMMER);
		model.addColumn(CREATOR);
		return model;

	}


	public void addPrivilege(final StemPrivilegeCaddy priv) {
		Vector v = new Vector();
		v.add(priv);
		v.add(priv.getIdentity());
		v.add(Boolean.valueOf(priv.hasStem()));
		v.add(Boolean.valueOf(priv.hasCreate()));
		addRow(v);
	}


	public synchronized StemPrivilegeCaddy getSelectedPrivilege() throws Exception {
		int row = getSelectedRow();
		if ((row >= 0) && (row < getRowCount())) {
			return (StemPrivilegeCaddy) getValueAt(row, 0);
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
		StemPrivilegeCaddy caddy = getSelectedPrivilege();
		GridApplication.getContext().addApplicationComponent(new StemPrivilegeWindow(browser, caddy), 600, 225);
	}


	public void singleClick() throws Exception {
		// TODO Auto-generated method stub

	}

}