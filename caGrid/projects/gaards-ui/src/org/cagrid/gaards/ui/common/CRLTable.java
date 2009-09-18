package org.cagrid.gaards.ui.common;

import java.security.cert.X509CRL;
import java.security.cert.X509CRLEntry;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;

import org.cagrid.grape.table.GrapeBaseTable;


/**
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @version $Id: CRLTable.java,v 1.2 2008-11-20 15:29:42 langella Exp $
 */
public class CRLTable extends GrapeBaseTable {
	
	private static final long serialVersionUID = 1L;
	
	public final static String SERIAL_NUMBER = "Serial Number";

	public final static String REVOKE = "Revocation Date";


	public CRLTable() {
		super(createTableModel());
		this.clearTable();

	}


	public static DefaultTableModel createTableModel() {
		DefaultTableModel model = new DefaultTableModel();
		model.addColumn(SERIAL_NUMBER);
		model.addColumn(REVOKE);
		return model;

	}


	public synchronized void addCRL(final X509CRL crl) {
		this.clearTable();
		Set s = crl.getRevokedCertificates();
		if (s != null) {
			Iterator itr = s.iterator();
			while (itr.hasNext()) {
				X509CRLEntry entry = (X509CRLEntry) itr.next();

				Vector v = new Vector();
				v.add(entry.getSerialNumber());
				v.add(entry.getRevocationDate().toString());
				addRow(v);
			}
		}
	}


	public void doubleClick() throws Exception {

	}


	public void singleClick() throws Exception {

	}

}