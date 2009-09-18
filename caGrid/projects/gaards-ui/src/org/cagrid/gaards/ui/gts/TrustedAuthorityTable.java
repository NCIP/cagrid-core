package org.cagrid.gaards.ui.gts;

import gov.nih.nci.cagrid.gts.bean.TrustedAuthority;

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
public class TrustedAuthorityTable extends GrapeBaseTable {
	
	private static final long serialVersionUID = 1L;
	
	public final static String TRUSTED_AUTHORITY = "ta";

	public final static String NAME = "Trust Authority Name";

	public final static String STATUS = "Status";

	TrustedAuthoritiesWindow window;


	public TrustedAuthorityTable(TrustedAuthoritiesWindow window) {
		super(createTableModel());
		this.window = window;
		TableColumn c = this.getColumn(TRUSTED_AUTHORITY);
		c.setMaxWidth(0);
		c.setMinWidth(0);
		c.setPreferredWidth(0);
		c.setResizable(false);

		c = this.getColumn(STATUS);
		c.setMaxWidth(100);
		c.setMinWidth(100);

		this.clearTable();

	}


	public static DefaultTableModel createTableModel() {
		DefaultTableModel model = new DefaultTableModel();
		model.addColumn(TRUSTED_AUTHORITY);
		model.addColumn(NAME);
		model.addColumn(STATUS);
		return model;

	}


	public void addTrustedAuthority(final TrustedAuthority ta) {
		Vector v = new Vector();
		v.add(ta);
		v.add(ta.getName());
		v.add(ta.getStatus().getValue());
		addRow(v);
	}


	public synchronized TrustedAuthority getSelectedTrustedAuthority() throws Exception {
		int row = getSelectedRow();
		if ((row >= 0) && (row < getRowCount())) {
			return (TrustedAuthority) getValueAt(row, 0);
		} else {
			throw new Exception("Please select a Trusted Authority!!!");
		}
	}


	public synchronized void removeSelectedTrustedAuthority() throws Exception {
		int row = getSelectedRow();
		if ((row >= 0) && (row < getRowCount())) {
			removeRow(row);
		} else {
			throw new Exception("Please select a Trusted Authority!!!");
		}
	}


	public void doubleClick() throws Exception {
		int row = getSelectedRow();
		if ((row >= 0) && (row < getRowCount())) {
			window.showTrustedAuthority();
		} else {
			throw new Exception("Please select an Identity Provider!!!");
		}

	}


	public void singleClick() throws Exception {
		// TODO Auto-generated method stub

	}

}