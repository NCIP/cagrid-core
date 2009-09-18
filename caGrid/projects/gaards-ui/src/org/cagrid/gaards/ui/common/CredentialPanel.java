package org.cagrid.gaards.ui.common;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import org.cagrid.grape.LookAndFeel;
import org.globus.gsi.GlobusCredential;


/**
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @version $Id: ProxyInformationComponent.java,v 1.3 2005/12/03 07:18:56
 *          langella Exp $
 */
public class CredentialPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;

	private JPanel jPanel = null;
	private JLabel jLabel = null;
	private JLabel jLabel1 = null;
	private JLabel jLabel2 = null;
	private JLabel jLabel3 = null;
	private JLabel jLabel4 = null;
	private JTextField identity = null;
	private JTextField issuer = null;
	private JTextField timeLeft = null;
	private JTextField strength = null;
	private JTextField subjectField = null;
	private JPanel certificateChain = null;
	private JScrollPane jScrollPane = null;
	private CertificateTable certificates = null;
	private JPanel jPanel1 = null;


	/**
	 * This is the default constructor
	 */
	public CredentialPanel() {
		super();
		initialize();
	}


	public void clearProxy() {
		subjectField.setText("");
		issuer.setText("");
		identity.setText("");
		strength.setText("");
		timeLeft.setText("");
		certificates.clearTable();
	}


	public void showProxy(GlobusCredential cred) {
		clearProxy();
		subjectField.setText(cred.getSubject());
		issuer.setText(cred.getIssuer());
		identity.setText(cred.getIdentity());
		strength.setText(cred.getStrength() + " bits");
		cred.getTimeLeft();
		GregorianCalendar c = new GregorianCalendar();
		c.add(Calendar.SECOND, (int) cred.getTimeLeft());
		timeLeft.setText(c.getTime().toString());
		X509Certificate[] certs = cred.getCertificateChain();
		for (int i = 0; i < certs.length; i++) {
			certificates.addCertificate(certs[i]);
		}
	}


	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {
		GridBagConstraints gridBagConstraints13 = new GridBagConstraints();
		gridBagConstraints13.gridx = 0;
		gridBagConstraints13.ipadx = 0;
		gridBagConstraints13.ipady = 0;
		gridBagConstraints13.weightx = 1.0D;
		gridBagConstraints13.weighty = 1.0D;
		gridBagConstraints13.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints13.gridy = 0;
		this.setLayout(new GridBagLayout());
		this.add(getJPanel1(), gridBagConstraints13);

	}


	/**
	 * This method initializes jPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel() {
		if (jPanel == null) {
			GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
			gridBagConstraints9.anchor = GridBagConstraints.WEST;
			gridBagConstraints9.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints9.gridx = 1;
			gridBagConstraints9.gridy = 0;
			gridBagConstraints9.weightx = 1.0D;
			gridBagConstraints9.weighty = 0.0D;
			gridBagConstraints9.fill = GridBagConstraints.HORIZONTAL;
			GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
			gridBagConstraints8.anchor = GridBagConstraints.WEST;
			gridBagConstraints8.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints8.gridx = 1;
			gridBagConstraints8.gridy = 4;
			gridBagConstraints8.weightx = 1.0D;
			gridBagConstraints8.weighty = 0.0D;
			gridBagConstraints8.fill = GridBagConstraints.HORIZONTAL;
			GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
			gridBagConstraints7.anchor = GridBagConstraints.WEST;
			gridBagConstraints7.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints7.gridx = 1;
			gridBagConstraints7.gridy = 5;
			gridBagConstraints7.weightx = 1.0D;
			gridBagConstraints7.weighty = 0.0D;
			gridBagConstraints7.fill = GridBagConstraints.HORIZONTAL;
			GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
			gridBagConstraints6.anchor = GridBagConstraints.WEST;
			gridBagConstraints6.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints6.gridx = 1;
			gridBagConstraints6.gridy = 1;
			gridBagConstraints6.weightx = 1.0D;
			gridBagConstraints6.weighty = 0.0D;
			gridBagConstraints6.fill = GridBagConstraints.HORIZONTAL;
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.anchor = GridBagConstraints.WEST;
			gridBagConstraints5.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints5.gridx = 1;
			gridBagConstraints5.gridy = 2;
			gridBagConstraints5.weightx = 1.0D;
			gridBagConstraints5.weighty = 0.0D;
			gridBagConstraints5.fill = GridBagConstraints.HORIZONTAL;
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.anchor = GridBagConstraints.WEST;
			gridBagConstraints4.gridx = 0;
			gridBagConstraints4.gridy = 4;
			gridBagConstraints4.insets = new Insets(2, 2, 2, 2);
			jLabel4 = new JLabel();
			jLabel4.setText("Strength");
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.anchor = GridBagConstraints.WEST;
			gridBagConstraints3.gridx = 0;
			gridBagConstraints3.gridy = 5;
			gridBagConstraints3.insets = new Insets(2, 2, 2, 2);
			jLabel3 = new JLabel();
			jLabel3.setText("Expires");
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.anchor = GridBagConstraints.WEST;
			gridBagConstraints2.gridx = 0;
			gridBagConstraints2.gridy = 1;
			gridBagConstraints2.insets = new Insets(2, 2, 2, 2);
			jLabel2 = new JLabel();
			jLabel2.setText("Issuer");
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.anchor = GridBagConstraints.WEST;
			gridBagConstraints1.gridx = 0;
			gridBagConstraints1.gridy = 2;
			gridBagConstraints1.insets = new Insets(2, 2, 2, 2);
			jLabel1 = new JLabel();
			jLabel1.setText("Identity");
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.anchor = GridBagConstraints.WEST;
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 0;
			gridBagConstraints.weightx = 0.0D;
			gridBagConstraints.weighty = 0.0D;
			gridBagConstraints.insets = new Insets(2, 2, 2, 2);
			jLabel = new JLabel();
			jLabel.setText("Subject");
			jPanel = new JPanel();
			jPanel.setLayout(new GridBagLayout());
			jPanel.setBorder(BorderFactory.createTitledBorder(null, "Credential Information",
				TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, 
                LookAndFeel.getPanelLabelColor()));
			jPanel.add(jLabel, gridBagConstraints);
			jPanel.add(jLabel1, gridBagConstraints1);
			jPanel.add(jLabel2, gridBagConstraints2);
			jPanel.add(jLabel3, gridBagConstraints3);
			jPanel.add(jLabel4, gridBagConstraints4);
			jPanel.add(getIdentity(), gridBagConstraints5);
			jPanel.add(getIssuer(), gridBagConstraints6);
			jPanel.add(getTimeLeft(), gridBagConstraints7);
			jPanel.add(getStrength(), gridBagConstraints8);
			jPanel.add(getSubjectField(), gridBagConstraints9);
		}
		return jPanel;
	}


	/**
	 * This method initializes jTextField
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getIdentity() {
		if (identity == null) {
			identity = new JTextField();
			identity.setEditable(false);
		}
		return identity;
	}


	/**
	 * This method initializes jTextField1
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getIssuer() {
		if (issuer == null) {
			issuer = new JTextField();
			issuer.setEditable(false);
		}
		return issuer;
	}


	/**
	 * This method initializes jTextField2
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getTimeLeft() {
		if (timeLeft == null) {
			timeLeft = new JTextField();
			timeLeft.setEditable(false);
		}
		return timeLeft;
	}


	/**
	 * This method initializes jTextField3
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getStrength() {
		if (strength == null) {
			strength = new JTextField();
			strength.setEditable(false);
		}
		return strength;
	}


	/**
	 * This method initializes jTextField4
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getSubjectField() {
		if (subjectField == null) {
			subjectField = new JTextField();
			subjectField.setEditable(false);
		}
		return subjectField;
	}


	/**
	 * This method initializes jPanel1
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getCertificateChain() {
		if (certificateChain == null) {
			GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
			gridBagConstraints10.fill = java.awt.GridBagConstraints.BOTH;
			gridBagConstraints10.weightx = 1.0;
			gridBagConstraints10.weighty = 1.0;
			certificateChain = new JPanel();
			certificateChain.setBorder(BorderFactory.createTitledBorder(null, "Certificate Chain",
				TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, 
                LookAndFeel.getPanelLabelColor()));
			certificateChain.setLayout(new GridBagLayout());
			certificateChain.add(getJScrollPane(), gridBagConstraints10);
		}
		return certificateChain;
	}


	/**
	 * This method initializes jScrollPane
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setViewportView(getCertificates());
		}
		return jScrollPane;
	}


	/**
	 * This method initializes certificateTable
	 * 
	 * @return gov.nih.nci.cagrid.dorian.ifs.portal.CertificateTable
	 */
	public CertificateTable getCertificates() {
		if (certificates == null) {
			certificates = new CertificateTable();
		}
		return certificates;
	}


	/**
	 * This method initializes jPanel1
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel1() {
		if (jPanel1 == null) {
			GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
			gridBagConstraints12.insets = new java.awt.Insets(5, 3, 0, 5);
			gridBagConstraints12.gridy = 1;
			gridBagConstraints12.weightx = 1.0D;
			gridBagConstraints12.weighty = 1.0D;
			gridBagConstraints12.fill = java.awt.GridBagConstraints.BOTH;
			gridBagConstraints12.gridx = 0;
			GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
			gridBagConstraints11.insets = new java.awt.Insets(5, 5, 5, 5);
			gridBagConstraints11.gridy = 0;
			gridBagConstraints11.weightx = 1.0D;
			gridBagConstraints11.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints11.gridx = 0;
			jPanel1 = new JPanel();
			jPanel1.setLayout(new GridBagLayout());
			jPanel1.add(getJPanel(), gridBagConstraints11);
			jPanel1.add(getCertificateChain(), gridBagConstraints12);
		}
		return jPanel1;
	}

}
