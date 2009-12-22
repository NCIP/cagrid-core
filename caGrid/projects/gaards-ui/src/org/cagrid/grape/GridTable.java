package org.cagrid.grape;

import java.util.Vector;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import org.cagrid.grape.configuration.Grid;
import org.cagrid.grape.table.GrapeBaseTable;


/**
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @version $Id: ServiceTable.java,v 1.1 2008-08-04 19:38:22 langella Exp $
 */
public class GridTable extends GrapeBaseTable {
	public final static String SERVICE = "Service";

	public final static String DISPLAY_NAME = "Display Name";

	public final static String SYSTEM_NAME = "System Name";

	public final static String IVY_SETTINGS = "Repository";


	public GridTable() {
		super(createTableModel());
		TableColumn c = this.getColumn(SERVICE);
		c.setMaxWidth(0);
		c.setMinWidth(0);
		c.setPreferredWidth(0);
		c.setResizable(false);
		this.clearTable();
	}


	public static DefaultTableModel createTableModel() {
		DefaultTableModel model = new DefaultTableModel();
		model.addColumn(SERVICE);
		model.addColumn(DISPLAY_NAME);
		model.addColumn(SYSTEM_NAME);
		model.addColumn(IVY_SETTINGS);
		return model;

	}


	public void addGrid(Grid grid) {
		Vector v = new Vector();
		v.add(grid);
		v.add(grid.getDisplayName());
		v.add(grid.getSystemName());
		if(grid.getIvySettings()!=null){
		v.add(grid.getIvySettings());
		}else{
			v.add("");
		}
		addRow(v);
	}


	public synchronized Grid getSelectedGrid() throws Exception {
		int row = getSelectedRow();
		if ((row >= 0) && (row < getRowCount())) {
			return (Grid) getValueAt(row, 0);
		} else {
			throw new Exception("Please select a grid!!!");
		}
	}


	public synchronized void removeGrid() throws Exception {
		int row = getSelectedRow();
		if ((row >= 0) && (row < getRowCount())) {
			removeRow(row);
		} else {
			throw new Exception("Please select a grid!!!");
		}
	}


	public void doubleClick() throws Exception {
	

	}


	public void singleClick() throws Exception {
		
	}

}