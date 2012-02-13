package org.cagrid.gaards.ui.gridgrouper.browser;

import java.util.Vector;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import org.cagrid.gaards.ui.gridgrouper.tree.StemTreeNode;
import org.cagrid.grape.table.GrapeBaseTable;


/**
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella</A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster</A>
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings</A>
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * @version $Id: GridGrouperBaseTreeNode.java,v 1.1 2006/08/04 03:49:26 langella
 *          Exp $
 */
public class StemsTable extends GrapeBaseTable {
	
	private static final long serialVersionUID = 1L;
	
	public final static String STEM = "Stem";

	public final static String NAME = "Name";

	private StemActionListener listener;

	public StemsTable(StemActionListener listener) {
		super(createTableModel());
		TableColumn c = this.getColumn(STEM);
		c.setMaxWidth(0);
		c.setMinWidth(0);
		c.setPreferredWidth(0);
		c.setResizable(false);
		this.listener = listener;
		this.clearTable();

	}


	public static DefaultTableModel createTableModel() {
		DefaultTableModel model = new DefaultTableModel();
		model.addColumn(STEM);
		model.addColumn(NAME);
		return model;
	}


	public void addStem(final StemTreeNode node) {
		Vector v = new Vector();
		v.add(node);
		v.add(node.getStem().getDisplayExtension());
		addRow(v);
	}


	public synchronized StemTreeNode getSelectedStem() throws Exception {
		int row = getSelectedRow();
		if ((row >= 0) && (row < getRowCount())) {
			return (StemTreeNode) getValueAt(row, 0);
		} else {
			throw new Exception("Please select a stem!!!");
		}
	}


	public void doubleClick() throws Exception {
		StemTreeNode node = getSelectedStem();
		listener.viewStem(node);
	}


	public void singleClick() throws Exception {
		// TODO Auto-generated method stub

	}

}