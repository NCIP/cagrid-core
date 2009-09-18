package org.cagrid.gaards.ui.common;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.security.cert.X509CRL;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;


/**
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @version $Id: CRLPanel.java,v 1.2 2008-11-20 15:29:42 langella Exp $
 */
public class CRLPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;
	
	private JPanel infoPanel = null;
	private JLabel jLabel = null;
	private JTextField issuer = null;
	private JLabel jLabel1 = null;
	private JTextField issued = null;
	private JLabel jLabel2 = null;
	private JTextField nextUpdate = null;
	private JPanel listPanel = null;
	private X509CRL crl = null;
	private JScrollPane jScrollPane = null;
	private JTable crlTable = null;


	/**
	 * This is the default constructor
	 */
	public CRLPanel() {
		super();
		initialize();
	}


	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {
		GridBagConstraints gridBagConstraints31 = new GridBagConstraints();
		gridBagConstraints31.gridx = 0;
		gridBagConstraints31.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints31.weightx = 1.0D;
		gridBagConstraints31.weighty = 1.0D;
		gridBagConstraints31.insets = new java.awt.Insets(5, 5, 5, 5);
		gridBagConstraints31.gridy = 1;
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 1.0D;
		gridBagConstraints.gridy = 0;
		this.setLayout(new GridBagLayout());
		this.add(getInfoPanel(), gridBagConstraints);
		this.add(getListPanel(), gridBagConstraints31);
	}


	/**
	 * This method initializes infoPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getInfoPanel() {
		if (infoPanel == null) {
			GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
			gridBagConstraints6.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints6.gridy = 2;
			gridBagConstraints6.weightx = 1.0;
			gridBagConstraints6.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints6.insets = new java.awt.Insets(2, 2, 2, 2);
			gridBagConstraints6.gridx = 1;
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints5.gridy = 2;
			gridBagConstraints5.insets = new java.awt.Insets(2, 2, 2, 2);
			gridBagConstraints5.gridx = 0;
			jLabel2 = new JLabel();
			jLabel2.setText("Next Update");
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints4.gridy = 1;
			gridBagConstraints4.weightx = 1.0;
			gridBagConstraints4.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints4.insets = new java.awt.Insets(2, 2, 2, 2);
			gridBagConstraints4.gridx = 1;
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints3.gridy = 1;
			gridBagConstraints3.insets = new java.awt.Insets(2, 2, 2, 2);
			gridBagConstraints3.gridx = 0;
			jLabel1 = new JLabel();
			jLabel1.setText("Issued");
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints2.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints2.gridx = 1;
			gridBagConstraints2.gridy = 0;
			gridBagConstraints2.insets = new java.awt.Insets(2, 2, 2, 2);
			gridBagConstraints2.weightx = 1.0;
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.insets = new java.awt.Insets(2, 2, 2, 2);
			gridBagConstraints1.gridy = 0;
			gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints1.gridx = 0;
			jLabel = new JLabel();
			jLabel.setText("Issuer");
			infoPanel = new JPanel();
			infoPanel.setLayout(new GridBagLayout());
			infoPanel.add(jLabel, gridBagConstraints1);
			infoPanel.add(getIssuer(), gridBagConstraints2);
			infoPanel.add(jLabel1, gridBagConstraints3);
			infoPanel.add(getIssued(), gridBagConstraints4);
			infoPanel.add(jLabel2, gridBagConstraints5);
			infoPanel.add(getNextUpdate(), gridBagConstraints6);
		}
		return infoPanel;
	}


	/**
	 * This method initializes issuer
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getIssuer() {
		if (issuer == null) {
			issuer = new JTextField();
			issuer.setEnabled(true);
			issuer.setEditable(false);
		}
		return issuer;
	}


	/**
	 * This method initializes issued
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getIssued() {
		if (issued == null) {
			issued = new JTextField();
			issued.setEditable(false);
		}
		return issued;
	}


	/**
	 * This method initializes nextUpdate
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getNextUpdate() {
		if (nextUpdate == null) {
			nextUpdate = new JTextField();
			nextUpdate.setEditable(false);
		}
		return nextUpdate;
	}


	/**
	 * This method initializes jPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getListPanel() {
		if (listPanel == null) {
			GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
			gridBagConstraints7.fill = java.awt.GridBagConstraints.BOTH;
			gridBagConstraints7.weighty = 1.0;
			gridBagConstraints7.weightx = 1.0;
			listPanel = new JPanel();
			listPanel.setLayout(new GridBagLayout());
			listPanel.add(getJScrollPane(), gridBagConstraints7);
		}
		return listPanel;
	}


	public void clearCRL() {
		this.crl = null;
		this.getIssuer().setText("");
		this.getIssued().setText("");
		this.getNextUpdate().setText("");
		((CRLTable) getCrlTable()).clearTable();
	}


	public void setCRL(X509CRL crl) {
		clearCRL();
		this.crl = crl;
		this.getIssuer().setText(crl.getIssuerDN().getName());
		this.getIssued().setText(crl.getThisUpdate().toString());
		this.getNextUpdate().setText(crl.getNextUpdate().toString());
		((CRLTable) getCrlTable()).addCRL(crl);
	}


	public X509CRL getCRL() {
		return this.crl;
	}


	/**
	 * This method initializes jScrollPane
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setViewportView(getCrlTable());
		}
		return jScrollPane;
	}


	/**
	 * This method initializes crlTable
	 * 
	 * @return javax.swing.JTable
	 */
	private JTable getCrlTable() {
		if (crlTable == null) {
			crlTable = new CRLTable();
		}
		return crlTable;
	}

}
