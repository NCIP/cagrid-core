package org.cagrid.grape;

import java.util.Vector;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import org.cagrid.grape.configuration.ServiceDescriptor;
import org.cagrid.grape.table.GrapeBaseTable;


/**
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @version $Id: ServiceTable.java,v 1.1 2008-08-04 19:38:22 langella Exp $
 */
public class ServiceTable extends GrapeBaseTable {
	public final static String SERVICE = "Service";

	public final static String DISPLAY_NAME = "Display Name";

	public final static String SERVICE_URL = "Service URL";

	public final static String SERVICE_IDENTITY = "Service Identity";


	public ServiceTable() {
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
		model.addColumn(SERVICE_URL);
		model.addColumn(SERVICE_IDENTITY);
		return model;

	}


	public void addService(ServiceDescriptor service) {
		Vector v = new Vector();
		v.add(service);
		v.add(service.getDisplayName());
		v.add(service.getServiceURL());
		if(service.getServiceIdentity()!=null){
		v.add(service.getServiceIdentity());
		}else{
			v.add("");
		}
		addRow(v);
	}


	public synchronized ServiceDescriptor getSelectedService() throws Exception {
		int row = getSelectedRow();
		if ((row >= 0) && (row < getRowCount())) {
			return (ServiceDescriptor) getValueAt(row, 0);
		} else {
			throw new Exception("Please select a service!!!");
		}
	}


	public synchronized void removeSelectedService() throws Exception {
		int row = getSelectedRow();
		if ((row >= 0) && (row < getRowCount())) {
			removeRow(row);
		} else {
			throw new Exception("Please select a service!!!");
		}
	}


	public void doubleClick() throws Exception {
	

	}


	public void singleClick() throws Exception {
		
	}

}