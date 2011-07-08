package org.cagrid.grape;

import java.util.Vector;

import javax.swing.table.DefaultTableModel;

import org.cagrid.grape.configuration.Grid;
import org.cagrid.grape.table.GrapeBaseTable;


public class RepositoryTable extends GrapeBaseTable {
	private static final long serialVersionUID = 1827369007520128246L;

	public final static String SERVICE = "Service";

	public final static String DISPLAY_NAME = "Maintained Target Grid(s)";

	public final static String IVY_SETTINGS = "Repository";


	public RepositoryTable() {
		super(createTableModel());
		this.clearTable();
	}


	public static DefaultTableModel createTableModel() {
		DefaultTableModel model = new DefaultTableModel();
		model.addColumn(IVY_SETTINGS);
		model.addColumn(DISPLAY_NAME);
		return model;

	}


	public void addRepository(String repositoryName, String maintainedGrids) {
		Vector<String> v = new Vector<String>();
		v.add(repositoryName);
		v.add(maintainedGrids);
		addRow(v);
	}


	public synchronized String getSelectedRepository() throws Exception {
		int row = getSelectedRow();
		if ((row >= 0) && (row < getRowCount())) {
			return (String) getValueAt(row, 0);
		} else {
			throw new Exception("Please select a repositry!!!");
		}
	}


	public synchronized void removeRepository() throws Exception {
		int row = getSelectedRow();
		if ((row >= 0) && (row < getRowCount())) {
			removeRow(row);
		} else {
			throw new Exception("Please select a repository!!!");
		}
	}


	public void doubleClick() throws Exception {
	

	}


	public void singleClick() throws Exception {
		
	}

}