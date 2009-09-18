package org.cagrid.gaards.ui.dorian.federation;

import java.util.Vector;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import org.cagrid.gaards.dorian.federation.TrustedIdP;
import org.cagrid.grape.table.GrapeBaseTable;

/**
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @version $Id: TrustedIdPTable.java,v 1.3 2008-11-20 15:29:42 langella Exp $
 */
public class TrustedIdPTable extends GrapeBaseTable {
	
	private static final long serialVersionUID = 1L;
	
	public final static String IDP = "idp";

	public final static String IDP_ID = "IdP Id";

	public final static String NAME = "Identity Provider Name";

	public final static String STATUS = "Status";

	TrustedIdPsWindow window;

	public TrustedIdPTable() {
		this(null);
	}

	public TrustedIdPTable(TrustedIdPsWindow window) {
		super(createTableModel());
		this.window = window;
		TableColumn c = this.getColumn(IDP);
		c.setMaxWidth(0);
		c.setMinWidth(0);
		c.setPreferredWidth(0);
		c.setResizable(false);

		c = this.getColumn(IDP_ID);
		c.setMaxWidth(35);
		c.setMinWidth(35);

		c = this.getColumn(STATUS);
		c.setMaxWidth(100);
		c.setMinWidth(100);

		this.clearTable();
	}

	public static DefaultTableModel createTableModel() {
		DefaultTableModel model = new DefaultTableModel();
		model.addColumn(IDP);
		model.addColumn(IDP_ID);
		model.addColumn(NAME);
		model.addColumn(STATUS);
		return model;

	}

	public void addTrustedIdP(final TrustedIdP idp) {
		Vector v = new Vector();
		v.add(idp);
		v.add(String.valueOf(idp.getId()));
		v.add(idp.getName());
		v.add(idp.getStatus().getValue());
		addRow(v);
	}

	public synchronized TrustedIdP getSelectedTrustedIdP() throws Exception {
		int row = getSelectedRow();
		if ((row >= 0) && (row < getRowCount())) {
			return (TrustedIdP) getValueAt(row, 0);
		} else {
			throw new Exception("Please select an Identity Provider!!!");
		}
	}

	public synchronized void removeSelectedTrustedIdP() throws Exception {
		int row = getSelectedRow();
		if ((row >= 0) && (row < getRowCount())) {
			removeRow(row);
		} else {
			throw new Exception("Please select an Identity Provider!!!");
		}
	}

	public void doubleClick() throws Exception {
		if (window != null) {
			int row = getSelectedRow();
			if ((row >= 0) && (row < getRowCount())) {
				window.showTrustedIdP();
			} else {
				throw new Exception("Please select an Identity Provider!!!");
			}
		}
	}

	public void singleClick() throws Exception {
		// TODO Auto-generated method stub

	}

}