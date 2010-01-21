package org.cagrid.gaards.ui.dorian.federation;

import gov.nih.nci.cagrid.common.Runner;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import org.apache.log4j.Logger;
import org.cagrid.gaards.dorian.client.GridAdministrationClient;
import org.cagrid.gaards.dorian.federation.GridUser;
import org.cagrid.gaards.dorian.federation.HostCertificateFilter;
import org.cagrid.gaards.dorian.federation.HostCertificateRecord;
import org.cagrid.gaards.dorian.federation.TrustedIdP;
import org.cagrid.gaards.dorian.stubs.types.PermissionDeniedFault;
import org.cagrid.gaards.ui.common.GAARDSLookAndFeel;
import org.cagrid.gaards.ui.common.ProgressPanel;
import org.cagrid.gaards.ui.dorian.DorianLookAndFeel;
import org.cagrid.gaards.ui.dorian.DorianSession;
import org.cagrid.gaards.ui.dorian.DorianSessionProvider;
import org.cagrid.grape.ApplicationComponent;
import org.cagrid.grape.GridApplication;
import org.cagrid.grape.LookAndFeel;
import org.cagrid.grape.utils.ErrorDialog;

/**
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Langella </A>
 */
public class UserWindow extends ApplicationComponent implements
		HostCertificateLauncher, DorianSessionProvider {
	private static Logger log = Logger.getLogger(UserWindow.class);
	
	private static final long serialVersionUID = 1L;

	private final static String INFO_PANEL = "Account Information";

	private final static String USER_CERTIFICATES_PANEL = "User Certificates";

	private final static String HOST_CERTIFICATES_PANEL = "Host Certificates";

	private final static String AUDIT_PANEL = "Audit";

	private javax.swing.JPanel jContentPane = null;

	private JPanel mainPanel = null;

	private JButton updateUser = null;

	private JTabbedPane jTabbedPane = null;

	private JPanel jPanel1 = null;

	private JPanel jPanel2 = null;

	private GridUser user;

	private JPanel infoPanel = null;

	private JLabel gridIdLabel = null;

	private JTextField gridIdentity = null;

	private TrustedIdP idp;

	private JLabel idpLabel = null;

	private JTextField trustedIdP = null;

	private JLabel uidLabel = null;

	private JTextField userId = null;

	private JLabel emailLabel = null;

	private JTextField email = null;

	private JLabel statusLabel = null;

	private JComboBox userStatus = null;

	private UserCertificateSearchPanel userCertificatePanel = null;

	private JLabel jLabel = null;

	private JLabel jLabel1 = null;

	private JTextField firstName = null;

	private JTextField lastName = null;

	private JLabel fullName = null;

	private JLabel emailAddress = null;

	private JLabel logo = null;

	private JPanel buttonPanel = null;

	private JLabel jLabel2 = null;

	private JLabel jLabel3 = null;

	private JTextField dorianDisplayName = null;

	private JTextField dorianURL = null;

	private DorianSession session;

	private JPanel hostCertificates = null;

	private JButton findHostCertificates = null;

	private JScrollPane jScrollPane = null;

	private HostCertificatesTable hostCertificateRecords = null;

	private JPanel hostCertificateRecordPanel = null;

	private JButton viewHostCertificate = null;

	private FederationAuditPanel auditPanel = null;

	private ProgressPanel progressPanel = null;

	/**
	 * This is the default constructor
	 */
	public UserWindow(DorianSession session, GridUser u, TrustedIdP idp)
			throws Exception {
		super();
		this.session = session;
		this.user = u;
		this.idp = idp;
		initialize();
		this.setFrameIcon(DorianLookAndFeel.getUserIcon());
	}

	/**
	 * This method initializes this
	 */
	private void initialize() {
		this.setContentPane(getJContentPane());
		this.setTitle(this.user.getFirstName() + " " + this.user.getLastName());
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
			GridBagConstraints gridBagConstraints30 = new GridBagConstraints();
			gridBagConstraints30.gridx = 0;
			gridBagConstraints30.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints30.weightx = 1.0D;
			gridBagConstraints30.gridy = 2;
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.fill = GridBagConstraints.BOTH;
			gridBagConstraints4.gridy = 1;
			gridBagConstraints4.weightx = 1.0;
			gridBagConstraints4.weighty = 1.0D;
			gridBagConstraints4.gridx = 0;
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints1.gridy = 0;
			gridBagConstraints1.weightx = 1.0D;
			gridBagConstraints1.anchor = java.awt.GridBagConstraints.NORTH;
			gridBagConstraints1.insets = new Insets(5, 5, 5, 5);
			gridBagConstraints1.gridx = 0;
			mainPanel = new JPanel();
			mainPanel.setLayout(new GridBagLayout());
			mainPanel.add(getJPanel2(), gridBagConstraints1);
			mainPanel.add(getJTabbedPane(), gridBagConstraints4);
			mainPanel.add(getProgressPanel(), gridBagConstraints30);
		}
		return mainPanel;
	}

	/**
	 * This method initializes manageUser
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getUpdateUser() {
		if (updateUser == null) {
			updateUser = new JButton();
			updateUser.setText("Update Account");
			updateUser.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					getUpdateUser().setEnabled(false);
					Runner runner = new Runner() {
						public void execute() {
							updateUser();
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
		return updateUser;
	}

	private void updateUser() {
		this.getProgressPanel().showProgress("Updating account...");
		user.setUserStatus(((UserStatusComboBox) this.getUserStatus())
				.getSelectedUserStatus());

		try {
			GridAdministrationClient client = this.session.getAdminClient();
			client.updateUser(user);
			this.getProgressPanel().stopProgress("Account successfully updated.");
		} catch (PermissionDeniedFault pdf) {
			ErrorDialog.showError(pdf);
			this.getProgressPanel().stopProgress("Error");
			log.error(pdf, pdf);
		} catch (Exception e) {
			ErrorDialog.showError(e);
			this.getProgressPanel().stopProgress("Error");
			log.error(e, e);
		} finally {
			updateUser.setEnabled(true);
			
		}

	}

	/**
	 * This method initializes jTabbedPane
	 * 
	 * @return javax.swing.JTabbedPane
	 */
	private JTabbedPane getJTabbedPane() {
		if (jTabbedPane == null) {
			jTabbedPane = new JTabbedPane();
			jTabbedPane.addTab(INFO_PANEL, null, getInfoPanel(), null);
			jTabbedPane.addTab(USER_CERTIFICATES_PANEL, null,
					getUserCertificatePanel(), null);
			jTabbedPane.addTab(HOST_CERTIFICATES_PANEL, null,
					getHostCertificates(), null);
			jTabbedPane.addTab(AUDIT_PANEL, null, getAuditPanel(), null);
		}
		return jTabbedPane;
	}

	/**
	 * This method initializes jPanel1
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel1() {
		if (jPanel1 == null) {
			GridBagConstraints gridBagConstraints24 = new GridBagConstraints();
			gridBagConstraints24.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints24.gridy = 1;
			gridBagConstraints24.weightx = 1.0;
			gridBagConstraints24.anchor = GridBagConstraints.WEST;
			gridBagConstraints24.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints24.gridx = 1;
			GridBagConstraints gridBagConstraints23 = new GridBagConstraints();
			gridBagConstraints23.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints23.gridy = 0;
			gridBagConstraints23.weightx = 1.0;
			gridBagConstraints23.anchor = GridBagConstraints.WEST;
			gridBagConstraints23.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints23.gridx = 1;
			GridBagConstraints gridBagConstraints17 = new GridBagConstraints();
			gridBagConstraints17.gridx = 0;
			gridBagConstraints17.anchor = GridBagConstraints.WEST;
			gridBagConstraints17.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints17.gridy = 1;
			jLabel3 = new JLabel();
			jLabel3.setText("Dorian Service URL");
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.gridx = 0;
			gridBagConstraints3.anchor = GridBagConstraints.WEST;
			gridBagConstraints3.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints3.gridy = 0;
			jLabel2 = new JLabel();
			jLabel2.setText("Dorian");
			GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
			gridBagConstraints21.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints21.gridy = 6;
			gridBagConstraints21.weightx = 1.0;
			gridBagConstraints21.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints21.insets = new java.awt.Insets(2, 2, 2, 2);
			gridBagConstraints21.gridx = 1;
			GridBagConstraints gridBagConstraints20 = new GridBagConstraints();
			gridBagConstraints20.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints20.gridy = 5;
			gridBagConstraints20.weightx = 1.0;
			gridBagConstraints20.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints20.insets = new java.awt.Insets(2, 2, 2, 2);
			gridBagConstraints20.gridx = 1;
			GridBagConstraints gridBagConstraints19 = new GridBagConstraints();
			gridBagConstraints19.gridx = 0;
			gridBagConstraints19.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints19.insets = new java.awt.Insets(2, 2, 2, 2);
			gridBagConstraints19.gridy = 6;
			jLabel1 = new JLabel();
			jLabel1.setText("Last Name");
			GridBagConstraints gridBagConstraints18 = new GridBagConstraints();
			gridBagConstraints18.gridx = 0;
			gridBagConstraints18.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints18.insets = new java.awt.Insets(2, 2, 2, 2);
			gridBagConstraints18.gridy = 5;
			jLabel = new JLabel();
			jLabel.setText("First Name");
			GridBagConstraints gridBagConstraints16 = new GridBagConstraints();
			gridBagConstraints16.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints16.gridy = 7;
			gridBagConstraints16.weightx = 1.0;
			gridBagConstraints16.insets = new java.awt.Insets(2, 2, 2, 2);
			gridBagConstraints16.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints16.gridx = 1;
			GridBagConstraints gridBagConstraints15 = new GridBagConstraints();
			gridBagConstraints15.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints15.gridy = 8;
			gridBagConstraints15.weightx = 1.0;
			gridBagConstraints15.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints15.insets = new java.awt.Insets(2, 2, 2, 2);
			gridBagConstraints15.gridx = 1;
			GridBagConstraints gridBagConstraints13 = new GridBagConstraints();
			gridBagConstraints13.gridx = 0;
			gridBagConstraints13.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints13.insets = new java.awt.Insets(2, 2, 2, 2);
			gridBagConstraints13.gridy = 8;
			statusLabel = new JLabel();
			statusLabel.setText("User Status");
			GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
			gridBagConstraints12.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints12.gridy = 7;
			gridBagConstraints12.weightx = 1.0;
			gridBagConstraints12.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints12.insets = new java.awt.Insets(2, 2, 2, 2);
			gridBagConstraints12.gridx = 1;
			GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
			gridBagConstraints11.gridx = 0;
			gridBagConstraints11.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints11.insets = new java.awt.Insets(2, 2, 2, 2);
			gridBagConstraints11.gridy = 7;
			emailLabel = new JLabel();
			emailLabel.setText("Email");
			GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
			gridBagConstraints10.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints10.gridy = 4;
			gridBagConstraints10.weightx = 1.0;
			gridBagConstraints10.insets = new java.awt.Insets(2, 2, 2, 2);
			gridBagConstraints10.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints10.gridx = 1;
			GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
			gridBagConstraints9.gridx = 0;
			gridBagConstraints9.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints9.insets = new java.awt.Insets(2, 2, 2, 2);
			gridBagConstraints9.gridy = 4;
			uidLabel = new JLabel();
			uidLabel.setText("Local User Id");
			GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
			gridBagConstraints8.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints8.gridy = 2;
			gridBagConstraints8.weightx = 1.0;
			gridBagConstraints8.insets = new java.awt.Insets(2, 2, 2, 2);
			gridBagConstraints8.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints8.gridx = 1;
			GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
			gridBagConstraints7.gridx = 0;
			gridBagConstraints7.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints7.insets = new java.awt.Insets(2, 2, 2, 2);
			gridBagConstraints7.gridy = 2;
			idpLabel = new JLabel();
			idpLabel.setText("Identity Provider");
			GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
			gridBagConstraints6.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints6.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints6.insets = new java.awt.Insets(2, 2, 2, 2);
			gridBagConstraints6.gridx = 1;
			gridBagConstraints6.gridy = 3;
			gridBagConstraints6.weightx = 1.0;
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints5.gridy = 3;
			gridBagConstraints5.insets = new java.awt.Insets(2, 2, 2, 2);
			gridBagConstraints5.gridx = 0;
			gridIdLabel = new JLabel();
			gridIdLabel.setText("Grid Identity");
			jPanel1 = new JPanel();
			jPanel1.setName(INFO_PANEL);
			jPanel1.setLayout(new GridBagLayout());
			jPanel1.add(gridIdLabel, gridBagConstraints5);
			jPanel1.add(getGridIdentity(), gridBagConstraints6);
			jPanel1.add(idpLabel, gridBagConstraints7);
			jPanel1.add(getTrustedIdP(), gridBagConstraints8);
			jPanel1.add(uidLabel, gridBagConstraints9);
			jPanel1.add(getUserId(), gridBagConstraints10);
			jPanel1.add(emailLabel, gridBagConstraints11);
			jPanel1.add(getEmail(), gridBagConstraints12);
			jPanel1.add(statusLabel, gridBagConstraints13);
			jPanel1.add(getUserStatus(), gridBagConstraints15);
			jPanel1.add(jLabel, gridBagConstraints18);
			jPanel1.add(jLabel1, gridBagConstraints19);
			jPanel1.add(getFirstName(), gridBagConstraints20);
			jPanel1.add(getLastName(), gridBagConstraints21);
			jPanel1.add(jLabel2, gridBagConstraints3);
			jPanel1.add(jLabel3, gridBagConstraints17);
			jPanel1.add(getDorianDisplayName(), gridBagConstraints23);
			jPanel1.add(getDorianURL(), gridBagConstraints24);
		}
		return jPanel1;
	}

	/**
	 * This method initializes jPanel2
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel2() {
		if (jPanel2 == null) {
			GridBagConstraints gridBagConstraints22 = new GridBagConstraints();
			gridBagConstraints22.gridx = 0;
			gridBagConstraints22.weightx = 0.0D;
			gridBagConstraints22.anchor = GridBagConstraints.NORTHWEST;
			gridBagConstraints22.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints22.gridheight = 2;
			gridBagConstraints22.gridy = 0;
			logo = new JLabel(GAARDSLookAndFeel.getLogoNoText32x32());
			GridBagConstraints gridBagConstraints14 = new GridBagConstraints();
			gridBagConstraints14.anchor = GridBagConstraints.WEST;
			gridBagConstraints14.gridy = 1;
			gridBagConstraints14.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints14.weightx = 1.0D;
			gridBagConstraints14.gridx = 1;
			emailAddress = new JLabel();
			emailAddress.setFont(new Font("Arial", Font.ITALIC, 12));
			emailAddress.setText(this.user.getEmail());
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 1;
			gridBagConstraints.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints.anchor = GridBagConstraints.WEST;
			gridBagConstraints.weightx = 1.0D;
			gridBagConstraints.gridy = 0;
			fullName = new JLabel();
			fullName.setText(this.user.getFirstName() + " "
					+ this.user.getLastName());
			fullName.setFont(new Font("Arial", Font.BOLD, 14));
			GridBagConstraints gridBagConstraints28 = new GridBagConstraints();
			gridBagConstraints28.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints28.gridx = 1;
			gridBagConstraints28.gridy = 0;
			gridBagConstraints28.insets = new java.awt.Insets(2, 2, 2, 2);
			gridBagConstraints28.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints28.weightx = 1.0;
			jPanel2 = new JPanel();
			jPanel2.setLayout(new GridBagLayout());
			jPanel2.add(fullName, gridBagConstraints);
			jPanel2.add(emailAddress, gridBagConstraints14);
			jPanel2.add(logo, gridBagConstraints22);

		}
		return jPanel2;
	}

	/**
	 * This method initializes infoPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getInfoPanel() {
		if (infoPanel == null) {
			infoPanel = new JPanel();
			infoPanel.setLayout(new BorderLayout());
			infoPanel.add(getJPanel1(), java.awt.BorderLayout.NORTH);
			infoPanel.add(getButtonPanel(), BorderLayout.SOUTH);
		}
		return infoPanel;
	}

	/**
	 * This method initializes gridIdentity
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getGridIdentity() {
		if (gridIdentity == null) {
			gridIdentity = new JTextField();
			gridIdentity.setText(user.getGridId());
			gridIdentity.setEditable(false);
		}
		return gridIdentity;
	}

	/**
	 * This method initializes trustedIdP
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getTrustedIdP() {
		if (trustedIdP == null) {
			trustedIdP = new JTextField();
			trustedIdP.setEditable(false);
			trustedIdP.setText(idp.getDisplayName());
		}
		return trustedIdP;
	}

	/**
	 * This method initializes userId
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getUserId() {
		if (userId == null) {
			userId = new JTextField();
			userId.setEditable(false);
			userId.setText(user.getUID());
		}
		return userId;
	}

	/**
	 * This method initializes email
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getEmail() {
		if (email == null) {
			email = new JTextField();
			email.setEditable(false);
			email.setText(user.getEmail());
		}
		return email;
	}

	/**
	 * This method initializes userStatus
	 * 
	 * @return javax.swing.JComboBox
	 */
	private JComboBox getUserStatus() {
		if (userStatus == null) {
			userStatus = new UserStatusComboBox();
			userStatus.setSelectedItem(user.getUserStatus());
		}
		return userStatus;
	}

	/**
	 * This method initializes userCertificatePanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getUserCertificatePanel() {
		if (userCertificatePanel == null) {
			userCertificatePanel = new UserCertificateSearchPanel(this.session,
					this.user.getGridId(),true);
			userCertificatePanel.setProgess(getProgressPanel());
		}
		return userCertificatePanel;
	}

	/**
	 * This method initializes firstName
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getFirstName() {
		if (firstName == null) {
			firstName = new JTextField();
			firstName.setEditable(false);
			firstName.setText(user.getFirstName());
		}
		return firstName;
	}

	/**
	 * This method initializes lastName
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getLastName() {
		if (lastName == null) {
			lastName = new JTextField();
			lastName.setEditable(false);
			lastName.setText(user.getLastName());
		}
		return lastName;
	}

	/**
	 * This method initializes buttonPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getButtonPanel() {
		if (buttonPanel == null) {
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.gridx = 0;
			gridBagConstraints2.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints2.gridy = 0;
			buttonPanel = new JPanel();
			buttonPanel.setLayout(new GridBagLayout());
			buttonPanel.add(getUpdateUser(), gridBagConstraints2);
		}
		return buttonPanel;
	}

	/**
	 * This method initializes dorianDisplayName
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getDorianDisplayName() {
		if (dorianDisplayName == null) {
			dorianDisplayName = new JTextField();
			dorianDisplayName.setEditable(false);
			dorianDisplayName.setText(this.session.getHandle()
					.getServiceDescriptor().getDisplayName());
		}
		return dorianDisplayName;
	}

	/**
	 * This method initializes dorianURL
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getDorianURL() {
		if (dorianURL == null) {
			dorianURL = new JTextField();
			dorianURL.setEditable(false);
			dorianURL.setText(this.session.getHandle().getServiceURL());
		}
		return dorianURL;
	}

	/**
	 * This method initializes hostCertificates
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getHostCertificates() {
		if (hostCertificates == null) {
			GridBagConstraints gridBagConstraints29 = new GridBagConstraints();
			gridBagConstraints29.gridx = 0;
			gridBagConstraints29.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints29.gridy = 2;
			GridBagConstraints gridBagConstraints27 = new GridBagConstraints();
			gridBagConstraints27.gridx = 0;
			gridBagConstraints27.weightx = 1.0D;
			gridBagConstraints27.weighty = 1.0D;
			gridBagConstraints27.fill = GridBagConstraints.BOTH;
			gridBagConstraints27.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints27.gridy = 1;
			GridBagConstraints gridBagConstraints25 = new GridBagConstraints();
			gridBagConstraints25.gridx = 0;
			gridBagConstraints25.insets = new Insets(5, 5, 5, 5);
			gridBagConstraints25.gridy = 0;
			hostCertificates = new JPanel();
			hostCertificates.setLayout(new GridBagLayout());
			hostCertificates.add(getHostCertificateRecordPanel(),
					gridBagConstraints27);
			hostCertificates.add(getFindHostCertificates(),
					gridBagConstraints25);
			hostCertificates
					.add(getViewHostCertificate(), gridBagConstraints29);
		}
		return hostCertificates;
	}

	/**
	 * This method initializes findHostCertificates
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getFindHostCertificates() {
		if (findHostCertificates == null) {
			findHostCertificates = new JButton();
			findHostCertificates.setText("List");
			findHostCertificates
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							getFindHostCertificates().setEnabled(false);
							getViewHostCertificate().setEnabled(false);
							Runner runner = new Runner() {
								public void execute() {
									findHostCertificates();
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
		return findHostCertificates;
	}

	private void findHostCertificates() {
		try {
			getProgressPanel().showProgress("Searching...");
			GridAdministrationClient client = this.session.getAdminClient();
			getHostCertificateRecords().clearTable();
			HostCertificateFilter f = new HostCertificateFilter();
			f.setOwner(this.user.getGridId());
			List<HostCertificateRecord> records = client
					.findHostCertificates(f);
			for (int i = 0; i < records.size(); i++) {
				getHostCertificateRecords().addHostCertificate(records.get(i));
			}
			getProgressPanel().stopProgress(records.size()+" host certificate(s) found.");
		} catch (Exception e) {
			ErrorDialog.showError(e);
			getProgressPanel().stopProgress("Error");
			log.error(e, e);
		} finally {
			getFindHostCertificates().setEnabled(true);
			getViewHostCertificate().setEnabled(true);
		}
	}

	/**
	 * This method initializes jScrollPane
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setViewportView(getHostCertificateRecords());
		}
		return jScrollPane;
	}

	/**
	 * This method initializes hostCertificateRecords
	 * 
	 * @return javax.swing.JTable
	 */
	private HostCertificatesTable getHostCertificateRecords() {
		if (hostCertificateRecords == null) {
			hostCertificateRecords = new HostCertificatesTable(this);
		}
		return hostCertificateRecords;
	}

	public void selectHostCertificate(HostCertificateRecord record) {
		try {
			HostCertificateWindow window = new HostCertificateWindow(
					this.session, getHostCertificateRecords()
							.getSelectedHostCertificate(), true);
			GridApplication.getContext().addApplicationComponent(window, 750,
					500);
		} catch (Exception e) {
			ErrorDialog.showError(e);
			log.error(e, e);
		}

	}

	/**
	 * This method initializes hostCertificateRecordPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getHostCertificateRecordPanel() {
		if (hostCertificateRecordPanel == null) {
			GridBagConstraints gridBagConstraints26 = new GridBagConstraints();
			gridBagConstraints26.fill = GridBagConstraints.BOTH;
			gridBagConstraints26.gridx = 0;
			gridBagConstraints26.gridy = 0;
			gridBagConstraints26.weightx = 1.0;
			gridBagConstraints26.weighty = 1.0;
			gridBagConstraints26.insets = new Insets(2, 2, 2, 2);
			hostCertificateRecordPanel = new JPanel();
			hostCertificateRecordPanel
					.setBorder(javax.swing.BorderFactory
							.createTitledBorder(
									null,
									"Host Certificates",
									javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
									javax.swing.border.TitledBorder.DEFAULT_POSITION,
									null, LookAndFeel.getPanelLabelColor()));
			hostCertificateRecordPanel.setLayout(new GridBagLayout());
			hostCertificateRecordPanel.add(getJScrollPane(),
					gridBagConstraints26);
		}
		return hostCertificateRecordPanel;
	}

	/**
	 * This method initializes viewHostCertificate
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getViewHostCertificate() {
		if (viewHostCertificate == null) {
			viewHostCertificate = new JButton();
			viewHostCertificate.setText("View");
			viewHostCertificate
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							try {
								getHostCertificateRecords().doubleClick();
							} catch (Exception ex) {
								ErrorDialog.showError(ex);
								log.error(ex, ex);
							}
						}
					});
		}
		return viewHostCertificate;
	}

	/**
	 * This method initializes auditPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getAuditPanel() {
		if (auditPanel == null) {
			auditPanel = new FederationAuditPanel(this,
					FederationAuditPanel.GRID_ACCOUNT_MODE, this.user
							.getGridId());
			auditPanel.setProgess(getProgressPanel());
		}
		return auditPanel;
	}

	public DorianSession getSession() throws Exception {
		return this.session;
	}

	/**
	 * This method initializes progressPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private ProgressPanel getProgressPanel() {
		if (progressPanel == null) {
			progressPanel = new ProgressPanel();
		}
		return progressPanel;
	}

}
