package org.cagrid.gaards.ui.cds;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import org.cagrid.gaards.cds.common.DelegationDescriptor;
import org.cagrid.grape.table.GrapeBaseTable;

/**
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella</A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster</A>
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings</A>
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * @version $Id: GridGrouperBaseTreeNode.java,v 1.1 2006/08/04 03:49:26 langella
 *          Exp $
 */
public class DelegationDescriptorTable extends GrapeBaseTable {
	
	private static final long serialVersionUID = 1L;
	
	public final static String DESCRIPTOR = "Descriptor";

	public final static String IDENTITY = "Identity";

	public final static String EXPIRATION = "Expiration";

	public final static String LIFETIME = "Issued Credential Lifetime";

	public final static String PATH_LENGTH = "Path Length";

	public DelegationDescriptorTable() {
		super(createTableModel());
		TableColumn c = this.getColumn(DESCRIPTOR);
		c.setMaxWidth(0);
		c.setMinWidth(0);
		c.setPreferredWidth(0);
		c.setResizable(false);
		
		c = this.getColumn(IDENTITY);
		c.setMinWidth(350);
		c.setPreferredWidth(0);
		
		c = this.getColumn(PATH_LENGTH);
		c.setMinWidth(75);
		c.setMaxWidth(75);
		c.setPreferredWidth(0);

		this.clearTable();

	}

	public static DefaultTableModel createTableModel() {
		DefaultTableModel model = new DefaultTableModel();
		model.addColumn(DESCRIPTOR);
		model.addColumn(IDENTITY);
		model.addColumn(EXPIRATION);
		model.addColumn(LIFETIME);
		model.addColumn(PATH_LENGTH);
		return model;
	}

	public synchronized void addDelegationDescriptor(DelegationDescriptor des) {
		Vector v = new Vector();
		v.add(des);
		v.add(des.getGridIdentity());
		Calendar d = new GregorianCalendar();
		d.setTimeInMillis(des.getExpiration());
		v.add(d.getTime().toString());
		String str = des.getIssuedCredentialLifetime().getHours() + " hrs "
				+ des.getIssuedCredentialLifetime().getMinutes() + " mins "
				+ des.getIssuedCredentialLifetime().getSeconds() + " secs";
		v.add(str);
		v.add(String.valueOf(des.getIssuedCredentialPathLength()));
		addRow(v);
	}

	public synchronized void addDelegationDescriptors(
			List<DelegationDescriptor> list) {
		for (int i = 0; i < list.size(); i++) {
			addDelegationDescriptor(list.get(i));
		}
	}

	public synchronized DelegationDescriptor getSelectedDelegationDescriptor()
			throws Exception {
		int row = getSelectedRow();
		if ((row >= 0) && (row < getRowCount())) {
			return (DelegationDescriptor) getValueAt(row, 0);
		} else {
			throw new Exception("Please select a Delegation Descriptor!!!");
		}
	}

	public void doubleClick() {

	}

	public void singleClick() {

	}

}