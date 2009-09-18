package org.cagrid.gaards.ui.common;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.security.cert.X509Certificate;

import javax.swing.JPanel;

import org.cagrid.grape.ApplicationComponent;

/**
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @version $Id: CertificateInformationComponent.java,v 1.2 2007/03/21 19:36:22
 *          langella Exp $
 */
public class CertificateInformationComponent extends ApplicationComponent {
	
	private static final long serialVersionUID = 1L;

	private javax.swing.JPanel jContentPane = null;
	private JPanel mainPanel = null;
	private X509Certificate cert;
	private JPanel certificatePanel = null;

    private JPanel titlePanel = null;

    public CertificateInformationComponent() {
		super();
		initialize();
	}

	/**
	 * This is the default constructor
	 */
	public CertificateInformationComponent(X509Certificate cert) {
		super();
		this.cert = cert;
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {
		this.setContentPane(getJContentPane());
		this.setFrameIcon(GAARDSLookAndFeel.getCertificateIcon());
		this.setTitle("Certificate Viewer");
		this.setSize(500, 500);
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private javax.swing.JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new javax.swing.JPanel();
			jContentPane.setLayout(new java.awt.BorderLayout());
			jContentPane.add(getMainPanel(), java.awt.BorderLayout.CENTER);
		}
		return jContentPane;
	}

	/**
	 * This method initializes jPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getMainPanel() {
		if (mainPanel == null) {
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.gridx = 0;
			gridBagConstraints1.gridy = 0;
			gridBagConstraints1.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints1.weightx = 1.0D;
			gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
			gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
			gridBagConstraints.weightx = 1.0D;
			gridBagConstraints.weighty = 1.0D;
			gridBagConstraints.gridy = 1;
			mainPanel = new JPanel();
			mainPanel.setLayout(new GridBagLayout());
			mainPanel.add(getCertificatePanel(), gridBagConstraints);
			mainPanel.add(getTitlePanel(), gridBagConstraints1);
		}
		return mainPanel;
	}

	/**
	 * This method initializes certificatePanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getCertificatePanel() {
		if (certificatePanel == null) {
			certificatePanel = new CertificatePanel(cert);
		}
		return certificatePanel;
	}

    /**
     * This method initializes titlePanel	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getTitlePanel() {
        if (titlePanel == null) {
            titlePanel = new TitlePanel("X.509 Certificate", this.cert.getSubjectDN().getName());
        }
        return titlePanel;
    }
}
