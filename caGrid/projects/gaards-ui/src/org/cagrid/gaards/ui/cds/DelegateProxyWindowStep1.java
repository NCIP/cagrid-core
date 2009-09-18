package org.cagrid.gaards.ui.cds;

import gov.nih.nci.cagrid.common.Runner;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.security.cert.X509Certificate;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.cagrid.gaards.cds.common.ProxyLifetime;
import org.cagrid.gaards.pki.CertificateExtensionsUtil;
import org.cagrid.gaards.ui.common.CredentialComboBox;
import org.cagrid.gaards.ui.common.TitlePanel;
import org.cagrid.grape.ApplicationComponent;
import org.cagrid.grape.GridApplication;
import org.cagrid.grape.utils.ErrorDialog;
import org.globus.gsi.bc.BouncyCastleUtil;

/**
 * @author <A href="mailto:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A href="mailto:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A href="mailto:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @version $Id: ArgumentManagerTable.java,v 1.2 2004/10/15 16:35:16 langella
 *          Exp $
 */
public class DelegateProxyWindowStep1 extends ApplicationComponent implements
		ProxyLifetimeListener {
	
	private static final long serialVersionUID = 1L;

	private static final int SECONDS_OFFSET = 60;

	private JPanel jContentPane = null;

	private JPanel mainPanel = null;

	private JLabel jLabel = null;

	private JLabel jLabel1 = null;

	private CredentialComboBox proxy = null;

	private JPanel buttonPanel = null;

	private JButton addButton = null;

	private JButton cancelButton = null;

	private CDSListComboBox cds = null;

	private JLabel jLabel2 = null;

	private JComboBox delegationPolicy = null;

	private JLabel jLabel3 = null;

	private ProxyLifetimePanel delegationLifetime = null;

	private JLabel jLabel4 = null;

	private JComboBox delegatedCredentialPathLength = null;

	private JLabel jLabel5 = null;

	private ProxyLifetimePanel issuedCredentialLifetime = null;

	private JLabel jLabel6 = null;

	private JComboBox issuedCredentialPathLength = null;

	private boolean firstProxyLifetimeChange = true;
	
	private static final int DEFAULT_MAX_PATH_LENGTH = 10;

    private JPanel titlePanel = null;
	

	/**
	 * This is the default constructor
	 */
	public DelegateProxyWindowStep1() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {
		this.setSize(600, 400);
		this.setContentPane(getJContentPane());
		this.setTitle("Delegate Credential (Step 1 of 2)");
		this.setFrameIcon(CDSLookAndFeel.getDelegateCredentialIcon());
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			GridBagConstraints gridBagConstraints17 = new GridBagConstraints();
			gridBagConstraints17.gridx = 0;
			gridBagConstraints17.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints17.weightx = 1.0D;
			gridBagConstraints17.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints17.gridy = 0;
			GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
			gridBagConstraints12.gridx = 0;
			gridBagConstraints12.insets = new java.awt.Insets(2, 2, 2, 2);
			gridBagConstraints12.gridy = 2;
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.ipadx = 0;
			gridBagConstraints.fill = GridBagConstraints.BOTH;
			gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
			gridBagConstraints.weightx = 1.0D;
			gridBagConstraints.weighty = 1.0D;
			gridBagConstraints.gridy = 1;
			jContentPane = new JPanel();
			jContentPane.setLayout(new GridBagLayout());
			jContentPane.add(getMainPanel(), gridBagConstraints);
			jContentPane.add(getButtonPanel(), gridBagConstraints12);
			jContentPane.add(getTitlePanel(), gridBagConstraints17);
		}
		return jContentPane;
	}

	/**
	 * This method initializes mainPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getMainPanel() {
		if (mainPanel == null) {
			GridBagConstraints gridBagConstraints16 = new GridBagConstraints();
			gridBagConstraints16.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints16.gridy = 5;
			gridBagConstraints16.weightx = 1.0;
			gridBagConstraints16.anchor = GridBagConstraints.WEST;
			gridBagConstraints16.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints16.weighty = 1.0D;
			gridBagConstraints16.gridx = 1;
			GridBagConstraints gridBagConstraints15 = new GridBagConstraints();
			gridBagConstraints15.gridx = 0;
			gridBagConstraints15.anchor = GridBagConstraints.WEST;
			gridBagConstraints15.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints15.weighty = 1.0D;
			gridBagConstraints15.gridy = 5;
			jLabel6 = new JLabel();
			jLabel6.setText("Issued Credential Path Length");
			GridBagConstraints gridBagConstraints14 = new GridBagConstraints();
			gridBagConstraints14.gridx = 1;
			gridBagConstraints14.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints14.anchor = GridBagConstraints.WEST;
			gridBagConstraints14.weightx = 1.0D;
			gridBagConstraints14.weighty = 1.0D;
			gridBagConstraints14.gridy = 4;
			GridBagConstraints gridBagConstraints13 = new GridBagConstraints();
			gridBagConstraints13.gridx = 0;
			gridBagConstraints13.anchor = GridBagConstraints.WEST;
			gridBagConstraints13.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints13.weighty = 1.0D;
			gridBagConstraints13.gridy = 4;
			jLabel5 = new JLabel();
			jLabel5.setText("Issued Credential Lifetime");
			GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
			gridBagConstraints11.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints11.gridy = 3;
			gridBagConstraints11.weightx = 1.0;
			gridBagConstraints11.anchor = GridBagConstraints.WEST;
			gridBagConstraints11.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints11.weighty = 1.0D;
			gridBagConstraints11.gridx = 1;
			GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
			gridBagConstraints10.gridx = 0;
			gridBagConstraints10.anchor = GridBagConstraints.WEST;
			gridBagConstraints10.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints10.weighty = 1.0D;
			gridBagConstraints10.gridy = 3;
			jLabel4 = new JLabel();
			jLabel4.setText("Delegated Credential Path Length");
			GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
			gridBagConstraints9.gridx = 1;
			gridBagConstraints9.anchor = GridBagConstraints.WEST;
			gridBagConstraints9.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints9.weightx = 1.0D;
			gridBagConstraints9.weighty = 1.0D;
			gridBagConstraints9.gridy = 2;
			GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
			gridBagConstraints8.gridx = 0;
			gridBagConstraints8.anchor = GridBagConstraints.WEST;
			gridBagConstraints8.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints8.weighty = 1.0D;
			gridBagConstraints8.gridy = 2;
			jLabel3 = new JLabel();
			jLabel3.setText("Delegation Lifetime");
			GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
			gridBagConstraints7.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints7.gridy = 6;
			gridBagConstraints7.weightx = 1.0;
			gridBagConstraints7.anchor = GridBagConstraints.WEST;
			gridBagConstraints7.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints7.weighty = 1.0D;
			gridBagConstraints7.gridx = 1;
			GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
			gridBagConstraints6.gridx = 0;
			gridBagConstraints6.anchor = GridBagConstraints.WEST;
			gridBagConstraints6.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints6.weighty = 1.0D;
			gridBagConstraints6.gridy = 6;
			jLabel2 = new JLabel();
			jLabel2.setText("Delegation Policy");
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints1.gridx = 1;
			gridBagConstraints1.gridy = 0;
			gridBagConstraints1.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints1.anchor = GridBagConstraints.WEST;
			gridBagConstraints1.weighty = 1.0D;
			gridBagConstraints1.weightx = 1.0;
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints5.gridy = 1;
			gridBagConstraints5.weightx = 1.0;
			gridBagConstraints5.insets = new java.awt.Insets(2, 2, 2, 2);
			gridBagConstraints5.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints5.weighty = 1.0D;
			gridBagConstraints5.gridx = 1;
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.gridx = 0;
			gridBagConstraints4.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints4.insets = new java.awt.Insets(2, 2, 2, 2);
			gridBagConstraints4.weighty = 1.0D;
			gridBagConstraints4.gridy = 1;
			jLabel1 = new JLabel();
			jLabel1.setText("Credential");
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints3.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints3.insets = new java.awt.Insets(2, 2, 2, 2);
			gridBagConstraints3.gridx = 1;
			gridBagConstraints3.gridy = 0;
			gridBagConstraints3.weightx = 1.0;
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.gridx = 0;
			gridBagConstraints2.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints2.insets = new java.awt.Insets(2, 2, 2, 2);
			gridBagConstraints2.weighty = 1.0D;
			gridBagConstraints2.gridy = 0;
			jLabel = new JLabel();
			jLabel.setText("Delegation Service");
			mainPanel = new JPanel();
			mainPanel.setLayout(new GridBagLayout());
			mainPanel.add(jLabel, gridBagConstraints2);
			mainPanel.add(jLabel1, gridBagConstraints4);
			mainPanel.add(getProxy(), gridBagConstraints5);
			mainPanel.add(getCds(), gridBagConstraints1);
			mainPanel.add(jLabel2, gridBagConstraints6);
			mainPanel.add(getDelegationPolicy(), gridBagConstraints7);
			mainPanel.add(jLabel3, gridBagConstraints8);
			mainPanel.add(getDelegationLifetime(), gridBagConstraints9);
			mainPanel.add(jLabel4, gridBagConstraints10);
			mainPanel.add(getDelegatedCredentialPathLength(),
					gridBagConstraints11);
			mainPanel.add(jLabel5, gridBagConstraints13);
			mainPanel.add(getIssuedCredentialLifetime(), gridBagConstraints14);
			mainPanel.add(jLabel6, gridBagConstraints15);
			mainPanel
					.add(getIssuedCredentialPathLength(), gridBagConstraints16);
		}
		return mainPanel;
	}

	/**
	 * This method initializes proxy
	 * 
	 * @return javax.swing.JComboBox
	 */
	private CredentialComboBox getProxy() {
		if (proxy == null) {
			proxy = new CredentialComboBox();
			proxy.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					handleCredentialSelection();
				}
			});
		}
		return proxy;
	}

	/**
	 * This method initializes buttonPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getButtonPanel() {
		if (buttonPanel == null) {
			buttonPanel = new JPanel();
			buttonPanel.add(getAddButton(), null);
			buttonPanel.add(getCancelButton(), null);
		}
		return buttonPanel;
	}

	/**
	 * This method initializes addButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getAddButton() {
		if (addButton == null) {
			addButton = new JButton();
			addButton.setText("Delegate");
			getRootPane().setDefaultButton(addButton);
			addButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					Runner runner = new Runner() {
						public void execute() {
							delegateCredential();
						}
					};
					try {
						GridApplication.getContext()
								.executeInBackground(runner);
					} catch (Exception t) {
						t.getMessage();
					}
				}

			});
		}

		return addButton;
	}

	/**
	 * This method initializes cancelButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getCancelButton() {
		if (cancelButton == null) {
			cancelButton = new JButton();
			cancelButton.setText("Cancel");
			cancelButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					dispose();
				}
			});
		}
		return cancelButton;
	}

	private void delegateCredential() {
		try {
			DelegationRequestCache cache = new DelegationRequestCache();
			cache.setDelegationHandle(getCds().getSelectedService());
			cache.setCredential(getProxy().getSelectedCredential());
			cache.setDelegationLifetime(getDelegationLifetime()
					.getProxyLifetime());
			cache
					.setDelegationPathLength(((Integer) getDelegatedCredentialPathLength()
							.getSelectedItem()).intValue());
			cache.setIssuedCredentialLifetime(getIssuedCredentialLifetime()
					.getProxyLifetime());
			cache
					.setIssuedCredentialPathLength(((Integer) getIssuedCredentialPathLength()
							.getSelectedItem()).intValue());

			DelegateProxyWindowStep2 window = new DelegateProxyWindowStep2((String)getDelegationPolicy().getSelectedItem(),
					cache);
			GridApplication.getContext().addApplicationComponent(window, 600,
					400);
			dispose();
		} catch (Exception e) {
			ErrorDialog.showError(e);
		}

	}

	/**
	 * This method initializes cds
	 * 
	 * @return javax.swing.JComboBox
	 */
	private CDSListComboBox getCds() {
		if (cds == null) {
			cds = new CDSListComboBox();
		}
		return cds;
	}

	/**
	 * This method initializes delegationPolicy
	 * 
	 * @return javax.swing.JComboBox
	 */
	private JComboBox getDelegationPolicy() {
		if (delegationPolicy == null) {
			delegationPolicy = new JComboBox();
			delegationPolicy.addItem(CDSUIConstants.IDENTITY_POLICY_TYPE);
			delegationPolicy.addItem(CDSUIConstants.GROUP_POLICY_TYPE);
		}
		return delegationPolicy;
	}

	/**
	 * This method initializes delegationLifetime
	 * 
	 * @return javax.swing.JPanel
	 */
	private ProxyLifetimePanel getDelegationLifetime() {
		if (delegationLifetime == null) {
			delegationLifetime = new ProxyLifetimePanel(this);
		}
		return delegationLifetime;
	}

	public int getDelegationPathLength(X509Certificate cert) throws Exception {
		if (org.globus.gsi.CertUtil.isProxy(BouncyCastleUtil
				.getCertificateType(cert))) {
			int delegationPathLength = CertificateExtensionsUtil
					.getDelegationPathLength(cert);
			int maxLength = delegationPathLength - 1;
			return maxLength;
		} else {
			return DEFAULT_MAX_PATH_LENGTH;
		}
	}

	public void handleCredentialSelection() {
		int maxPathLength = 0;
		long lifetimeSeconds = 0;
		try {
			X509Certificate[] certs = getProxy().getSelectedCredential()
					.getCertificateChain();
			maxPathLength = getDelegationPathLength(certs[0]);
			lifetimeSeconds = getProxy().getSelectedCredential().getTimeLeft()
					- SECONDS_OFFSET;
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (maxPathLength < 0) {
			maxPathLength = 0;
		}
		delegatedCredentialPathLength.removeAllItems();
		for (int i = 0; i <= maxPathLength; i++) {
			delegatedCredentialPathLength.addItem(new Integer(i));
		}
		if(maxPathLength>=1){
			delegatedCredentialPathLength.setSelectedIndex(new Integer(1));
		}

		getDelegationLifetime().setLifetime(lifetimeSeconds);
		getIssuedCredentialLifetime().setLifetime(
				lifetimeSeconds - SECONDS_OFFSET);
	}

	public void handleDelegationPathLengthSelection() {
		int maxPathLength = 0;
		if (getDelegatedCredentialPathLength().getSelectedItem() != null) {
			maxPathLength = ((Integer) getDelegatedCredentialPathLength()
					.getSelectedItem()).intValue() - 1;
		}
		if (maxPathLength < 0) {
			maxPathLength = 0;
		}
		getIssuedCredentialPathLength().removeAllItems();
		for (int i = 0; i <= maxPathLength; i++) {
			getIssuedCredentialPathLength().addItem(new Integer(i));
		}
	}

	/**
	 * This method initializes delegatedCredentialPathLength
	 * 
	 * @return javax.swing.JComboBox
	 */
	private JComboBox getDelegatedCredentialPathLength() {
		if (delegatedCredentialPathLength == null) {
			delegatedCredentialPathLength = new JComboBox();
			delegatedCredentialPathLength
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							handleDelegationPathLengthSelection();
						}
					});
			handleCredentialSelection();
		}
		return delegatedCredentialPathLength;
	}

	/**
	 * This method initializes issuedCredentialLifetime
	 * 
	 * @return javax.swing.JPanel
	 */
	private ProxyLifetimePanel getIssuedCredentialLifetime() {
		if (issuedCredentialLifetime == null) {
			issuedCredentialLifetime = new ProxyLifetimePanel();
		}
		return issuedCredentialLifetime;
	}

	/**
	 * This method initializes issuedCredentialPathLength
	 * 
	 * @return javax.swing.JComboBox
	 */
	private JComboBox getIssuedCredentialPathLength() {
		if (issuedCredentialPathLength == null) {
			issuedCredentialPathLength = new JComboBox();
			handleDelegationPathLengthSelection();
		}
		return issuedCredentialPathLength;
	}

	public void handleProxyLifetimeChange() {
		if (firstProxyLifetimeChange) {
			getProxy();
			handleCredentialSelection();
			firstProxyLifetimeChange = false;
		}
		ProxyLifetime l = getDelegationLifetime().getProxyLifetime();
		long seconds = ((l.getHours() * 60 * 60) + (l.getMinutes() * 60) + l
				.getSeconds())
				- SECONDS_OFFSET;
		if (seconds < 0) {
			seconds = 0;
		}
		getIssuedCredentialLifetime().setLifetime(seconds);
	}

    /**
     * This method initializes titlePanel	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getTitlePanel() {
        if (titlePanel == null) {
            titlePanel = new TitlePanel("Delegate Credential","Delegate your credential to another party such that they may act on your behalf.");
        }
        return titlePanel;
    }

}
