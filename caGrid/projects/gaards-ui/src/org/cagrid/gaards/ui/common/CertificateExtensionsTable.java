package org.cagrid.gaards.ui.common;

import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import org.cagrid.gaards.pki.CertificateExtensionsUtil;
import org.cagrid.grape.table.GrapeBaseTable;


/**
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @version $Id: CertificateExtensionsTable.java,v 1.2 2007/03/21 19:36:22
 *          langella Exp $
 */
public class CertificateExtensionsTable extends GrapeBaseTable {
	
	private static final long serialVersionUID = 1L;

	public final static String OID = "OID";
	public final static String NAME = "NAME";
	public final static String CRITICAL = "Critical";

	public final static String VALUE = "Value";


	public CertificateExtensionsTable() {
		super(createTableModel());
		TableColumn c = this.getColumn(OID);
		c.setMaxWidth(175);
		c.setMinWidth(175);
		c.setPreferredWidth(0);
		this.clearTable();

	}


	public static DefaultTableModel createTableModel() {
		DefaultTableModel model = new DefaultTableModel();
		model.addColumn(OID);
		model.addColumn(NAME);
		model.addColumn(CRITICAL);
		model.addColumn(VALUE);
		return model;

	}


	public void addCertificate(final X509Certificate cert) {
		Set set = cert.getCriticalExtensionOIDs();
		Iterator itr = set.iterator();

		while (itr.hasNext()) {
			try {
				String oid = (String) itr.next();
				Vector v = new Vector();
				v.add(oid);
				v.add(CertificateExtensionsUtil.getExtentionName(oid));
				v.add("true");
				v.add(CertificateExtensionsUtil.getExtensionDisplayValue(oid, cert));
				addRow(v);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		set = cert.getNonCriticalExtensionOIDs();
		itr = set.iterator();
		while (itr.hasNext()) {
			String oid = (String) itr.next();
			Vector v = new Vector();
			v.add(oid);
			v.add(CertificateExtensionsUtil.getExtentionName(oid));
			v.add("false");
			v.add(CertificateExtensionsUtil.getExtensionDisplayValue(oid, cert));
			addRow(v);
		}

	}


	public synchronized X509Certificate getSelectedCertificate() {
		int row = getSelectedRow();
		if ((row >= 0) && (row < getRowCount())) {
			return (X509Certificate) getValueAt(row, 0);
		} else {
			return null;
		}
	}


	public void doubleClick() throws Exception {
		/*
		 * int row = getSelectedRow(); if ((row >= 0) && (row < getRowCount())) {
		 * PortalResourceManager.getInstance().getGridPortal().addGridPortalComponent(new
		 * CertificateInformationComponent(getSelectedCertificate()),500,325); }
		 * else { throw new Exception( "No certificate selected, please select a
		 * certificate!!!"); }
		 */
	}


	public void singleClick() throws Exception {
		// TODO Auto-generated method stub

	}

}