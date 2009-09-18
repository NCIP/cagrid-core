package org.cagrid.gaards.ui.gts;

import gov.nih.nci.cagrid.common.Runner;
import gov.nih.nci.cagrid.gts.bean.Permission;
import gov.nih.nci.cagrid.gts.bean.PermissionFilter;
import gov.nih.nci.cagrid.gts.client.GTSAdminClient;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;

import org.cagrid.gaards.ui.common.ProgressPanel;
import org.cagrid.gaards.ui.common.TitlePanel;
import org.cagrid.grape.ApplicationComponent;
import org.cagrid.grape.GridApplication;
import org.cagrid.grape.LookAndFeel;
import org.cagrid.grape.utils.ErrorDialog;


/**
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Langella </A>
 * @version $Id: TrustedAuthoritiesWindow.java,v 1.2 2006/03/27 19:05:40
 *          langella Exp $
 */
public class PermissionManagerWindow extends ApplicationComponent implements PermissionRefresher, ServiceSelectionListener {
	
	private static final long serialVersionUID = 1L;

	private javax.swing.JPanel jContentPane = null;

	private JPanel mainPanel = null;

	private JPanel contentPanel = null;

	private JPanel buttonPanel = null;

	private PermissionsTable permissionsTable = null;

	private JScrollPane jScrollPane = null;

	private JButton addPermission = null;

	private JPanel queryPanel = null;

	private JButton query = null;

	private JButton removePermissionButton = null;

	private PermissionPanel filterPanel = null;

	private String currentService = null;

	private boolean searchDone = false;

    private JPanel titlePanel = null;

    private SessionPanel session = null;

    private ProgressPanel progressPanel = null;


	/**
	 * This is the default constructor
	 */
	public PermissionManagerWindow() {
		super();
		initialize();
		this.setFrameIcon(GTSLookAndFeel.getAdminIcon());
		syncServices();
	}


	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {
		this.setSize(700, 500);
		this.setContentPane(getJContentPane());
		this.setTitle("Access Control");
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
			GridBagConstraints gridBagConstraints41 = new GridBagConstraints();
			gridBagConstraints41.gridx = 0;
			gridBagConstraints41.weightx = 1.0D;
			gridBagConstraints41.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints41.gridy = 6;
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.gridx = 0;
			gridBagConstraints3.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints3.weightx = 1.0D;
			gridBagConstraints3.gridy = 1;
			GridBagConstraints gridBagConstraints22 = new GridBagConstraints();
			gridBagConstraints22.gridx = 0;
			gridBagConstraints22.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints22.weightx = 1.0D;
			gridBagConstraints22.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints22.gridy = 0;
			GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
			gridBagConstraints21.gridx = 0;
			gridBagConstraints21.fill = java.awt.GridBagConstraints.BOTH;
			gridBagConstraints21.weightx = 1.0D;
			gridBagConstraints21.weighty = 1.0D;
			gridBagConstraints21.insets = new java.awt.Insets(2, 2, 2, 2);
			gridBagConstraints21.gridy = 4;
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints.weightx = 2.0D;
			gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
			gridBagConstraints.gridy = 2;
			GridBagConstraints gridBagConstraints33 = new GridBagConstraints();
			gridBagConstraints33.gridx = 0;
			gridBagConstraints33.gridy = 3;
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			mainPanel = new JPanel();
			mainPanel.setLayout(new GridBagLayout());
			gridBagConstraints2.gridx = 0;
			gridBagConstraints2.gridy = 5;
			gridBagConstraints2.insets = new java.awt.Insets(2, 2, 2, 2);
			gridBagConstraints2.anchor = java.awt.GridBagConstraints.SOUTH;
			gridBagConstraints2.fill = java.awt.GridBagConstraints.HORIZONTAL;
			mainPanel.add(getButtonPanel(), gridBagConstraints2);
			mainPanel.add(getQueryPanel(), gridBagConstraints33);
			mainPanel.add(getFilterPanel(), gridBagConstraints);
			mainPanel.add(getContentPanel(), gridBagConstraints21);
			mainPanel.add(getTitlePanel(), gridBagConstraints22);
			mainPanel.add(getSession(), gridBagConstraints3);
			mainPanel.add(getProgressPanel(), gridBagConstraints41);
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
			contentPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Permission(s)",
				javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
				javax.swing.border.TitledBorder.DEFAULT_POSITION, null, LookAndFeel.getPanelLabelColor()));
			gridBagConstraints4.weightx = 1.0;
			gridBagConstraints4.gridy = 0;
			gridBagConstraints4.gridx = 0;
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
			buttonPanel.add(getAddPermission(), null);
			buttonPanel.add(getRemovePermissionButton(), null);
		}
		return buttonPanel;
	}


	/**
	 * This method initializes jTable
	 * 
	 * @return javax.swing.JTable
	 */
	private PermissionsTable getPermissionsTable() {
		if (permissionsTable == null) {
			permissionsTable = new PermissionsTable();
		}
		return permissionsTable;
	}


	/**
	 * This method initializes jScrollPane
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setViewportView(getPermissionsTable());
		}
		return jScrollPane;
	}


	/**
	 * This method initializes manageUser
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getAddPermission() {
		if (addPermission == null) {
			addPermission = new JButton();
			addPermission.setText("Add");
			addPermission.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					Runner runner = new Runner() {
						public void execute() {
							disableAllActions();
							addPermission();
							enableAllActions();
						}
					};
					try {
						GridApplication.getContext().executeInBackground(runner);
					} catch (Exception t) {
						t.getMessage();
					}
				}

			});
		}

		return addPermission;
	}
	
	


	public void handleServiceSelected() {
	    Runner runner = new Runner() {
            public void execute() {
                disableAllActions();
                syncServices();
                enableAllActions();
            }
        };
        try {
            GridApplication.getContext().executeInBackground(runner);
        } catch (Exception t) {
            t.getMessage();
        }
    }


    public void addPermission() {
		try {
			GridApplication.getContext().addApplicationComponent(
                new AddPermissionWindow(this.session.getSession(), this), 600, 250);
		} catch (Exception e) {
			ErrorDialog.showError(e);
		}
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
			getRootPane().setDefaultButton(query);
			query.setText("Search");
			query.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					Runner runner = new Runner() {
						public void execute() {
							disableAllActions();
							findPermissions();
							enableAllActions();
						}
					};
					try {
						GridApplication.getContext().executeInBackground(runner);
					} catch (Exception t) {
						t.getMessage();
					}

				}
			});
		}
		return query;
	}


	private void findPermissions() {

		this.getPermissionsTable().clearTable();
		getProgressPanel().showProgress("Searching...");

		try {
			
			PermissionFilter f = filterPanel.getPermissionFilter();
			GTSAdminClient client = this.session.getSession().getAdminClient();
			Permission[] perms = client.findPermissions(f);
			int length = 0;
			if (perms != null) {
				length = perms.length;
				for (int i = 0; i < perms.length; i++) {
					this.permissionsTable.addPermission(perms[i]);
				}
			}
			searchDone = true;
			getProgressPanel().stopProgress(length+" permission(s) found.");
				} catch (Exception e) {
			ErrorDialog.showError(e);
			getProgressPanel().stopProgress("Error");
		}

	}                              


	/**
	 * This method initializes removeUser
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getRemovePermissionButton() {
		if (removePermissionButton == null) {
			removePermissionButton = new JButton();
			removePermissionButton.setText("Remove");
			removePermissionButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					Runner runner = new Runner() {
						public void execute() {
							disableAllActions();
							removePermission();
							enableAllActions();
						}
					};
					try {
						GridApplication.getContext().executeInBackground(runner);
					} catch (Exception t) {
						t.getMessage();
					}
				}
			});
		}
		return removePermissionButton;
	}


	private void removePermission() {
		try {
		    getProgressPanel().showProgress("Removing permission...");
			GTSAdminClient client = this.session.getSession().getAdminClient();
			client.revokePermission(this.permissionsTable.getSelectedPermission());
			this.refreshPermissions();
			getProgressPanel().stopProgress("Permission successfully removed.");
		} catch (Exception e) {
			ErrorDialog.showError(e);
			getProgressPanel().stopProgress("Error");
		}
	}


	/**
	 * This method initializes filterPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private PermissionPanel getFilterPanel() {
		if (filterPanel == null) {
			filterPanel = new PermissionPanel(true);
			filterPanel.setBorder(BorderFactory.createTitledBorder(null, "Search Criteria",
				TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, 
                LookAndFeel.getPanelLabelColor()));
		}
		return filterPanel;
	}


	private synchronized void syncServices() {
		this.getPermissionsTable().clearTable();
		String selectedService = getSession().getServiceURI();
		if ((currentService == null) || (!currentService.equals(selectedService))) {
			try {
				currentService = selectedService;
				getProgressPanel().showProgress("Searching...");
				int length = filterPanel.syncWithService(getSession().getSession());
				getProgressPanel().stopProgress(length+" certificate authority(s) found.");
			} catch (Exception e) {
				ErrorDialog.showError(e);
				getProgressPanel().stopProgress("Error");
			}
		}

	}


	private void disableAllActions() {
		getQuery().setEnabled(false);
		getAddPermission().setEnabled(false);
		getRemovePermissionButton().setEnabled(false);
		getFilterPanel().disableAll();

	}


	private void enableAllActions() {
		getQuery().setEnabled(true);
		getAddPermission().setEnabled(true);
		getRemovePermissionButton().setEnabled(true);
		getFilterPanel().enableAll();
	}


	public void refreshPermissions() {
		if (searchDone) {
			disableAllActions();
			findPermissions();
			enableAllActions();
		}

	}


    /**
     * This method initializes titlePanel	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getTitlePanel() {
        if (titlePanel == null) {
            titlePanel = new TitlePanel("Trust Fabric Access Control","Grant and revoke administrative privileges to the trust fabric.");
        }
        return titlePanel;
    }


    /**
     * This method initializes session	
     * 	
     * @return javax.swing.JPanel	
     */
    private SessionPanel getSession() {
        if (session == null) {
            session = new SessionPanel(this);
        }
        return session;
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
