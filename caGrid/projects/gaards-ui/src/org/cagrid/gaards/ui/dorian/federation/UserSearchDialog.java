package org.cagrid.gaards.ui.dorian.federation;

import gov.nih.nci.cagrid.common.Runner;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import org.cagrid.gaards.dorian.client.GridAdministrationClient;
import org.cagrid.gaards.dorian.federation.GridUser;
import org.cagrid.gaards.dorian.federation.GridUserFilter;
import org.cagrid.gaards.dorian.federation.TrustedIdP;
import org.cagrid.gaards.dorian.stubs.types.PermissionDeniedFault;
import org.cagrid.gaards.ui.common.ProgressPanel;
import org.cagrid.gaards.ui.common.TitlePanel;
import org.cagrid.gaards.ui.dorian.DorianSessionProvider;
import org.cagrid.gaards.ui.dorian.SessionPanel;
import org.cagrid.grape.GridApplication;
import org.cagrid.grape.LookAndFeel;
import org.cagrid.grape.utils.ErrorDialog;
import org.globus.gsi.GlobusCredential;

/**
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Langella </A>
 * @version $Id: UserSearchDialog.java,v 1.5 2008-11-20 15:29:42 langella Exp $
 */
public class UserSearchDialog extends JDialog {
	
	private static final long serialVersionUID = 1L;

	private javax.swing.JPanel jContentPane = null;

	private JPanel mainPanel = null;

	private JPanel contentPanel = null;

	private JPanel buttonPanel = null;

	private JButton cancel = null;

	private UsersTable usersTable = null;

	private JScrollPane jScrollPane = null;

	private JButton select = null;

	private SessionPanel session = null;

	private JPanel queryPanel = null;

	private JButton query = null;

	private ProgressPanel progressPanel = null;

	private JPanel filterPanel = null;

	private JLabel idpLabel = null;

	private JComboBox idp = null;

	private JLabel gidLabel = null;

	private JTextField gridIdentity = null;

	private JLabel emailLabel = null;

	private JTextField email = null;

	private JComboBox userStatus = null;

	private String lastService = null;

	private String lastGridIdentity = null;

	private JLabel uidLabel = null;

	private JTextField userId = null;

	private JLabel statusLabel = null;

	private JLabel jLabel = null;

	private JLabel jLabel1 = null;

	private JTextField firstName = null;

	private JTextField lastName = null;

	private String selectedUser = null;

	private DorianSessionProvider sessionProvider;

	private JPanel titlePanel = null;

	/**
	 * This is the default constructor
	 */
	public UserSearchDialog() {
		this(null);
	}

	public UserSearchDialog(DorianSessionProvider provider) {
		super(GridApplication.getContext().getApplication());
		if (provider != null) {
			this.sessionProvider = provider;
		}
		initialize();
		if (provider != null) {
			setSize(700, 550);
		} else {
			setSize(700, 650);
		}

	}

	/**
	 * This method initializes this
	 */
	private void initialize() {
		this.setContentPane(getJContentPane());
		this.setTitle("Find Users");
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
			GridBagConstraints gridBagConstraints13 = new GridBagConstraints();
			gridBagConstraints13.gridx = 0;
			gridBagConstraints13.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints13.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints13.weightx = 1.0D;
			gridBagConstraints13.gridy = 0;
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints.weightx = 1.0D;
			gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
			gridBagConstraints.gridy = 2;
			GridBagConstraints gridBagConstraints32 = new GridBagConstraints();
			gridBagConstraints32.gridx = 0;
			gridBagConstraints32.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints32.insets = new Insets(0, 0, 0, 0);
			gridBagConstraints32.weightx = 1.0D;
			gridBagConstraints32.gridy = 7;
			GridBagConstraints gridBagConstraints33 = new GridBagConstraints();
			gridBagConstraints33.gridx = 0;
			gridBagConstraints33.gridy = 4;
			GridBagConstraints gridBagConstraints35 = new GridBagConstraints();
			gridBagConstraints35.gridx = 0;
			gridBagConstraints35.weightx = 1.0D;
			gridBagConstraints35.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints35.gridheight = 1;
			gridBagConstraints35.gridwidth = 1;
			gridBagConstraints35.insets = new Insets(0, 0, 0, 0);
			gridBagConstraints35.weighty = 0.0D;
			gridBagConstraints35.ipadx = 0;
			gridBagConstraints35.ipady = 0;
			gridBagConstraints35.gridy = 1;

			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			mainPanel = new JPanel();
			mainPanel.setLayout(new GridBagLayout());
			gridBagConstraints1.gridx = 0;
			gridBagConstraints1.gridy = 5;
			gridBagConstraints1.ipadx = 0;
			gridBagConstraints1.insets = new java.awt.Insets(2, 2, 2, 2);
			gridBagConstraints1.weightx = 1.0D;
			gridBagConstraints1.fill = java.awt.GridBagConstraints.BOTH;
			gridBagConstraints1.weighty = 1.0D;
			gridBagConstraints2.gridx = 0;
			gridBagConstraints2.gridy = 6;
			gridBagConstraints2.insets = new Insets(0, 0, 0, 0);
			gridBagConstraints2.anchor = java.awt.GridBagConstraints.SOUTH;
			gridBagConstraints2.fill = java.awt.GridBagConstraints.HORIZONTAL;
			if (sessionProvider == null) {
				sessionProvider = getSession();
				mainPanel.add(getSession(), gridBagConstraints35);
			}
			mainPanel.add(getQueryPanel(), gridBagConstraints33);
			mainPanel.add(getButtonPanel(), gridBagConstraints2);
			mainPanel.add(getContentPanel(), gridBagConstraints1);
			mainPanel.add(getProgressPanel(), gridBagConstraints32);
			mainPanel.add(getFilterPanel(), gridBagConstraints);
			mainPanel.add(getTitlePanel(), gridBagConstraints13);
		}
		return mainPanel;
	}

	/**
	 * This method initializes jPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getContentPanel() {
		if (contentPanel == null) {
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			contentPanel = new JPanel();
			contentPanel.setLayout(new GridBagLayout());
			contentPanel
					.setBorder(javax.swing.BorderFactory
							.createTitledBorder(
									null,
									"Users",
									javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
									javax.swing.border.TitledBorder.DEFAULT_POSITION,
									null, LookAndFeel.getPanelLabelColor()));
			gridBagConstraints4.weightx = 1.0;
			gridBagConstraints4.weighty = 1.0;
			gridBagConstraints4.fill = java.awt.GridBagConstraints.BOTH;
			contentPanel.add(getJScrollPane(), gridBagConstraints4);
		}
		return contentPanel;
	}

	/**
	 * This method initializes jPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getButtonPanel() {
		if (buttonPanel == null) {
			buttonPanel = new JPanel();
			buttonPanel.add(getSelect(), null);
			buttonPanel.add(getCancel(), null);
		}
		return buttonPanel;
	}

	/**
	 * This method initializes jButton1
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getCancel() {
		if (cancel == null) {
			cancel = new JButton();
			cancel.setText("Cancel");
			cancel.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					dispose();
				}
			});
		}
		return cancel;
	}

	/**
	 * This method initializes jTable
	 * 
	 * @return javax.swing.JTable
	 */
	private UsersTable getUsersTable() {
		if (usersTable == null) {
			usersTable = new UsersTable(this.sessionProvider);
		}
		return usersTable;
	}

	/**
	 * This method initializes jScrollPane
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setViewportView(getUsersTable());
		}
		return jScrollPane;
	}

	/**
	 * This method initializes select
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getSelect() {
		if (select == null) {
			select = new JButton();
			select.setText("Select");
			select.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					try {
						selectedUser = getUsersTable().getSelectedUser()
								.getGridId();
						dispose();
					} catch (Exception ex) {
						ErrorDialog.showError(ex);
					}
				}

			});
		}

		return select;
	}

	public String getSelectedUser() {
		return selectedUser;
	}

	/**
	 * This method initializes session
	 * 
	 * @return javax.swing.JPanel
	 */
	private SessionPanel getSession() {
		if (session == null) {
			session = new SessionPanel();
		}
		return session;
	}

	/**
	 * This method initializes queryPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getQueryPanel() {
		if (queryPanel == null) {
			queryPanel = new JPanel();
			queryPanel.add(getQuery(), null);
		}
		return queryPanel;
	}

	/**
	 * This method initializes query
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getQuery() {
		if (query == null) {
			query = new JButton();
			query.setText("Search");
			getRootPane().setDefaultButton(query);
			query.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					query.setEnabled(false);
					Runner runner = new Runner() {
						public void execute() {
							findUsers();
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
		return query;
	}

	private void findUsers() {
		this.getUsersTable().clearTable();
		getProgressPanel().showProgress("Searching...");

		try {
			GridUserFilter f = new GridUserFilter();

			Object o = getIdp().getSelectedItem();
			if (o instanceof TrustedIdPCaddy) {
				TrustedIdPCaddy caddy = (TrustedIdPCaddy) getIdp()
						.getSelectedItem();
				f.setIdPId(caddy.getTrustedIdP().getId());
			}
			f.setUID(format(getUserId().getText()));
			f.setGridId(format(getGridIdentity().getText()));
			f.setFirstName(format(this.firstName.getText()));
			f.setLastName(format(this.lastName.getText()));
			f.setEmail(format(getEmail().getText()));
			f.setUserStatus(((UserStatusComboBox) this.getUserStatus())
					.getSelectedUserStatus());

			GridAdministrationClient client = this.sessionProvider.getSession()
					.getAdminClient();
			List<GridUser> users = client.findUsers(f);

			for (int i = 0; i < users.size(); i++) {
				this.getUsersTable().addUser(users.get(i));
			}
			getProgressPanel().stopProgress(users.size() + " user(s) found.");

		} catch (PermissionDeniedFault pdf) {
			ErrorDialog.showError(pdf);
			getProgressPanel().stopProgress("Error");
		} catch (Exception e) {
			ErrorDialog.showError(e);
			getProgressPanel().stopProgress("Error");
		} finally {
			query.setEnabled(true);
		}

	}

	private String format(String s) {
		if ((s == null) || (s.trim().length() == 0)) {
			return null;
		} else {
			return s;
		}
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

	/**
	 * This method initializes filterPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getFilterPanel() {
		if (filterPanel == null) {
			GridBagConstraints gridBagConstraints19 = new GridBagConstraints();
			gridBagConstraints19.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints19.gridy = 4;
			gridBagConstraints19.weightx = 1.0;
			gridBagConstraints19.insets = new java.awt.Insets(2, 2, 2, 2);
			gridBagConstraints19.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints19.gridx = 1;
			GridBagConstraints gridBagConstraints18 = new GridBagConstraints();
			gridBagConstraints18.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints18.gridy = 3;
			gridBagConstraints18.weightx = 1.0;
			gridBagConstraints18.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints18.insets = new java.awt.Insets(2, 2, 2, 2);
			gridBagConstraints18.gridx = 1;
			GridBagConstraints gridBagConstraints17 = new GridBagConstraints();
			gridBagConstraints17.gridx = 0;
			gridBagConstraints17.insets = new java.awt.Insets(2, 2, 2, 2);
			gridBagConstraints17.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints17.gridy = 4;
			jLabel1 = new JLabel();
			jLabel1.setText("Last Name");
			jLabel1.setName("Last Name");
			GridBagConstraints gridBagConstraints16 = new GridBagConstraints();
			gridBagConstraints16.gridx = 0;
			gridBagConstraints16.insets = new java.awt.Insets(2, 2, 2, 2);
			gridBagConstraints16.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints16.gridy = 3;
			jLabel = new JLabel();
			jLabel.setText("First Name");
			GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
			gridBagConstraints12.gridx = 0;
			gridBagConstraints12.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints12.insets = new java.awt.Insets(2, 2, 2, 2);
			gridBagConstraints12.gridy = 6;
			statusLabel = new JLabel();
			statusLabel.setText("User Status");
			GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
			gridBagConstraints11.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints11.insets = new java.awt.Insets(2, 2, 2, 2);
			gridBagConstraints11.gridx = 1;
			gridBagConstraints11.gridy = 7;
			gridBagConstraints11.weightx = 1.0;
			gridBagConstraints11.fill = java.awt.GridBagConstraints.HORIZONTAL;
			GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
			gridBagConstraints10.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints10.insets = new java.awt.Insets(2, 2, 2, 2);
			gridBagConstraints10.gridx = 1;
			gridBagConstraints10.gridy = 6;
			gridBagConstraints10.weightx = 1.0;
			gridBagConstraints10.fill = java.awt.GridBagConstraints.HORIZONTAL;
			GridBagConstraints gridBagConstraints15 = new GridBagConstraints();
			gridBagConstraints15.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints15.gridy = 2;
			gridBagConstraints15.weightx = 1.0;
			gridBagConstraints15.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints15.insets = new java.awt.Insets(2, 2, 2, 2);
			gridBagConstraints15.gridx = 1;
			GridBagConstraints gridBagConstraints14 = new GridBagConstraints();
			gridBagConstraints14.gridx = 0;
			gridBagConstraints14.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints14.insets = new java.awt.Insets(2, 2, 2, 2);
			gridBagConstraints14.gridy = 2;
			uidLabel = new JLabel();
			uidLabel.setText("User Id");
			GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
			gridBagConstraints9.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints9.gridy = 5;
			gridBagConstraints9.weightx = 1.0;
			gridBagConstraints9.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints9.insets = new java.awt.Insets(2, 2, 2, 2);
			gridBagConstraints9.gridx = 1;
			GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
			gridBagConstraints8.gridx = 0;
			gridBagConstraints8.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints8.insets = new java.awt.Insets(2, 2, 2, 2);
			gridBagConstraints8.gridy = 5;
			emailLabel = new JLabel();
			emailLabel.setText("Email");
			GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
			gridBagConstraints7.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints7.gridy = 0;
			gridBagConstraints7.weightx = 1.0;
			gridBagConstraints7.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints7.insets = new java.awt.Insets(2, 2, 2, 2);
			gridBagConstraints7.gridx = 1;
			GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
			gridBagConstraints6.gridx = 0;
			gridBagConstraints6.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints6.insets = new java.awt.Insets(2, 2, 2, 2);
			gridBagConstraints6.gridy = 0;
			gidLabel = new JLabel();
			gidLabel.setText("Grid Identity");
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints5.gridx = 1;
			gridBagConstraints5.gridy = 1;
			gridBagConstraints5.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints5.insets = new java.awt.Insets(2, 2, 2, 2);
			gridBagConstraints5.weightx = 1.0;
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.gridx = 0;
			gridBagConstraints3.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints3.insets = new java.awt.Insets(2, 2, 2, 2);
			gridBagConstraints3.gridy = 1;
			idpLabel = new JLabel();
			idpLabel.setText("Identity Provider");
			filterPanel = new JPanel();
			filterPanel.setLayout(new GridBagLayout());
			filterPanel.setBorder(BorderFactory.createTitledBorder(null,
					"Search Criteria", TitledBorder.DEFAULT_JUSTIFICATION,
					TitledBorder.DEFAULT_POSITION, null, LookAndFeel
							.getPanelLabelColor()));
			filterPanel.add(idpLabel, gridBagConstraints3);
			filterPanel.add(getIdp(), gridBagConstraints5);
			filterPanel.add(gidLabel, gridBagConstraints6);
			filterPanel.add(getGridIdentity(), gridBagConstraints7);
			filterPanel.add(emailLabel, gridBagConstraints8);
			filterPanel.add(getEmail(), gridBagConstraints9);
			filterPanel.add(uidLabel, gridBagConstraints14);
			filterPanel.add(getUserId(), gridBagConstraints15);
			filterPanel.add(getUserStatus(), gridBagConstraints10);
			filterPanel.add(statusLabel, gridBagConstraints12);
			filterPanel.add(jLabel, gridBagConstraints16);
			filterPanel.add(jLabel1, gridBagConstraints17);
			filterPanel.add(getFirstName(), gridBagConstraints18);
			filterPanel.add(getLastName(), gridBagConstraints19);
		}
		return filterPanel;
	}

	/**
	 * This method initializes idp
	 * 
	 * @return javax.swing.JComboBox
	 */
	private JComboBox getIdp() {
		if (idp == null) {
			idp = new JComboBox();
			idp.addFocusListener(new FocusListener() {
				public void focusGained(FocusEvent ev) {
					checkUpdateIdPs();
				}

				public void focusLost(FocusEvent ev) {
					// TODO Auto-generated method stub

				}

			});
		}
		return idp;
	}

	private void updateIdPs(String serviceUrl, GlobusCredential cred) {
		try {
			getProgressPanel().showProgress("Searching...");
			this.getIdp().removeAllItems();
			GridAdministrationClient client = new GridAdministrationClient(
					serviceUrl, cred);
			List<TrustedIdP> idps = client.getTrustedIdPs();
			this.getIdp().removeAllItems();
			this.getIdp().addItem("");
			for (int i = 0; i < idps.size(); i++) {
				getIdp().addItem(new TrustedIdPCaddy(idps.get(i)));
			}
			getProgressPanel().stopProgress(
					idps.size() + " identity provider(s) found.");
			getIdp().showPopup();
		} catch (Exception e) {
			getProgressPanel().stopProgress("Error");
			ErrorDialog.showError(e);
		}
	}

	private void checkUpdateIdPs() {
		try {
			query.setEnabled(false);
			getIdp().hidePopup();
			final String serviceUrl = getSession().getServiceURI();
			final GlobusCredential cred = getSession().getCredential();
			if ((serviceUrl.equals(this.lastService))
					&& (cred.getIdentity().equals(this.lastGridIdentity))) {
				getIdp().showPopup();
				return;
			} else {
				this.lastService = serviceUrl;
				this.lastGridIdentity = cred.getIdentity();

				Runner runner = new Runner() {
					public void execute() {
						updateIdPs(serviceUrl, cred);
					}
				};
				try {
					GridApplication.getContext().executeInBackground(runner);
				} catch (Exception t) {
					t.getMessage();
				}

			}
		} catch (Exception e) {
			ErrorDialog.showError(e);
		} finally {
			query.setEnabled(true);
		}
	}

	/**
	 * This method initializes gridIdentity
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getGridIdentity() {
		if (gridIdentity == null) {
			gridIdentity = new JTextField();
		}
		return gridIdentity;
	}

	/**
	 * This method initializes email
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getEmail() {
		if (email == null) {
			email = new JTextField();
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
			userStatus = new UserStatusComboBox(true);
		}
		return userStatus;
	}

	public class TrustedIdPCaddy {
		private TrustedIdP trustedIdp;

		public TrustedIdPCaddy(TrustedIdP idp) {
			this.trustedIdp = idp;
		}

		public TrustedIdP getTrustedIdP() {
			return trustedIdp;
		}

		public String toString() {
			return "[IdP Id: " + trustedIdp.getId() + "] "
					+ trustedIdp.getName();
		}

	}

	/**
	 * This method initializes userId
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getUserId() {
		if (userId == null) {
			userId = new JTextField();
		}
		return userId;
	}

	/**
	 * This method initializes firstName
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getFirstName() {
		if (firstName == null) {
			firstName = new JTextField();
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
		}
		return lastName;
	}

	/**
	 * This method initializes titlePanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getTitlePanel() {
		if (titlePanel == null) {
			titlePanel = new TitlePanel("User Search",
					"Search for and select a user in the federation.");
		}
		return titlePanel;
	}

}
