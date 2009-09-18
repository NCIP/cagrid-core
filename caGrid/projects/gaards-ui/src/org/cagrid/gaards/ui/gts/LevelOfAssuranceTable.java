package org.cagrid.gaards.ui.gts;

import gov.nih.nci.cagrid.gts.bean.TrustLevel;

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
public class LevelOfAssuranceTable extends GrapeBaseTable {
	
	private static final long serialVersionUID = 1L;
	
	public final static String TRUST_LEVEL = "trust-level";

	public final static String NAME = "Name";

	public final static String DESCRIPTION = "Description";

	private LevelOfAssuranceManagerWindow window;


	public LevelOfAssuranceTable(LevelOfAssuranceManagerWindow window) {
		super(createTableModel());
		this.window = window;
		TableColumn c = this.getColumn(TRUST_LEVEL);
		c.setMaxWidth(0);
		c.setMinWidth(0);
		c.setPreferredWidth(0);
		c.setResizable(false);

		c = this.getColumn(NAME);
		c.setMaxWidth(150);
		c.setMinWidth(150);
		this.clearTable();
	}


	public static DefaultTableModel createTableModel() {
		DefaultTableModel model = new DefaultTableModel();
		model.addColumn(TRUST_LEVEL);
		model.addColumn(NAME);
		model.addColumn(DESCRIPTION);
		return model;

	}


	public void addTrustLevel(final TrustLevel level) {
		Vector v = new Vector();
		v.add(level);
		v.add(level.getName());
		v.add(level.getDescription());
		addRow(v);
	}


	public synchronized TrustLevel getSelectedTrustLevel() throws Exception {
		int row = getSelectedRow();
		if ((row >= 0) && (row < getRowCount())) {
			return (TrustLevel) getValueAt(row, 0);
		} else {
			throw new Exception("Please select a trust level!!!");
		}
	}


	public synchronized void removeSelectedTrustLevel() throws Exception {
		int row = getSelectedRow();
		if ((row >= 0) && (row < getRowCount())) {
			removeRow(row);
		} else {
			throw new Exception("Please select a trust level!!!");
		}
	}


	public void doubleClick() throws Exception {
		int row = getSelectedRow();
		if ((row >= 0) && (row < getRowCount())) {
			window.viewModifyLevel();
		} else {
			throw new Exception("Please select a trust level!!!");
		}

	}


	public void singleClick() throws Exception {
		// TODO Auto-generated method stub

	}

}