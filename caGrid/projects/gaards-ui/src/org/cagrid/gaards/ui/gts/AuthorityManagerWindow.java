package org.cagrid.gaards.ui.gts;

import gov.nih.nci.cagrid.common.Runner;
import gov.nih.nci.cagrid.gts.bean.AuthorityGTS;
import gov.nih.nci.cagrid.gts.client.GTSAdminClient;
import gov.nih.nci.cagrid.gts.client.GTSPublicClient;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

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
public class AuthorityManagerWindow extends ApplicationComponent implements AuthorityRefresher {
	
	private static final long serialVersionUID = 1L;

	private javax.swing.JPanel jContentPane = null;

	private JPanel mainPanel = null;

	private JPanel contentPanel = null;

	private JPanel buttonPanel = null;

	private AuthorityTable authorityTable = null;

	private JScrollPane jScrollPane = null;

	private JButton addAuthority = null;

	private JPanel queryPanel = null;

	private JButton query = null;

	private JButton removeAuthority = null;

	private JButton viewModifyButton = null;

	private JPanel priorityPanel = null;

	private JButton increasePriority = null;

	private JButton decreasePriority = null;

	private JButton updatePriorities = null;

	private boolean searchDone = false;

    private JPanel titlePanel = null;

    private SessionPanel session = null;

    private ProgressPanel progressPanel = null;


	/**
	 * This is the default constructor
	 */
	public AuthorityManagerWindow() {
		super();
		initialize();
		this.setFrameIcon(GTSLookAndFeel.getAuthorityIcon());
	}


	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {
		this.setSize(700, 500);
		this.setContentPane(getJContentPane());
		this.setTitle("GTS Authority Management");
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
			GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
			gridBagConstraints8.gridx = 0;
			gridBagConstraints8.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints8.weightx = 1.0D;
			gridBagConstraints8.gridy = 5;
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.gridx = 0;
			gridBagConstraints3.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints3.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints3.gridy = 1;
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.gridx = 0;
			gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints1.weightx = 1.0D;
			gridBagConstraints1.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints1.gridy = 0;
			GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
			gridBagConstraints21.gridx = 0;
			gridBagConstraints21.fill = java.awt.GridBagConstraints.BOTH;
			gridBagConstraints21.weightx = 1.0D;
			gridBagConstraints21.weighty = 1.0D;
			gridBagConstraints21.insets = new java.awt.Insets(2, 2, 2, 2);
			gridBagConstraints21.gridy = 3;
			GridBagConstraints gridBagConstraints33 = new GridBagConstraints();
			gridBagConstraints33.gridx = 0;
			gridBagConstraints33.gridy = 2;
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			mainPanel = new JPanel();
			mainPanel.setLayout(new GridBagLayout());
			gridBagConstraints2.gridx = 0;
			gridBagConstraints2.gridy = 4;
			gridBagConstraints2.insets = new java.awt.Insets(2, 2, 2, 2);
			gridBagConstraints2.anchor = java.awt.GridBagConstraints.SOUTH;
			gridBagConstraints2.fill = java.awt.GridBagConstraints.HORIZONTAL;
			mainPanel.add(getButtonPanel(), gridBagConstraints2);
			mainPanel.add(getQueryPanel(), gridBagConstraints33);
			mainPanel.add(getContentPanel(), gridBagConstraints21);
			mainPanel.add(getTitlePanel(), gridBagConstraints1);
			mainPanel.add(getSession(), gridBagConstraints3);
			mainPanel.add(getProgressPanel(), gridBagConstraints8);
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
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 1;
			gridBagConstraints.fill = GridBagConstraints.NONE;
			gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
			gridBagConstraints.gridy = 0;
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			contentPanel = new JPanel();
			contentPanel.setLayout(new GridBagLayout());
			contentPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Authority(s)",
				javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
				javax.swing.border.TitledBorder.DEFAULT_POSITION, null, LookAndFeel.getPanelLabelColor()));
			gridBagConstraints4.weightx = 1.0;
			gridBagConstraints4.gridy = 0;
			gridBagConstraints4.gridx = 0;
			gridBagConstraints4.weighty = 1.0;
			gridBagConstraints4.fill = java.awt.GridBagConstraints.BOTH;
			contentPanel.add(getJScrollPane(), gridBagConstraints4);
			contentPanel.add(getPriorityPanel(), gridBagConstraints);
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
			buttonPanel.add(getAddAuthority(), null);
			buttonPanel.add(getViewModifyButton(), null);
			buttonPanel.add(getRemoveAuthority(), null);
		}
		return buttonPanel;
	}


	/**
	 * This method initializes jTable
	 * 
	 * @return javax.swing.JTable
	 */
	private AuthorityTable getAuthorityTable() {
		if (authorityTable == null) {
			authorityTable = new AuthorityTable(this);
		}
		return authorityTable;
	}


	/**
	 * This method initializes jScrollPane
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setViewportView(getAuthorityTable());
		}
		return jScrollPane;
	}


	/**
	 * This method initializes manageUser
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getAddAuthority() {
		if (addAuthority == null) {
			addAuthority = new JButton();
			addAuthority.setText("Add");
			addAuthority.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					Runner runner = new Runner() {
						public void execute() {
							disableAllActions();
							addAuthority();
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

		return addAuthority;
	}


	public void addAuthority() {
		try {
			GridApplication.getContext().addApplicationComponent(new AuthorityWindow(getSession().getSession(), this), 700, 375);
		} catch (Exception e) {
			ErrorDialog.showError(e);
		}
	}


	public void viewModifyAuthority() {
		try {
			GridApplication.getContext().addApplicationComponent(
				new AuthorityWindow(getSession().getSession(), getAuthorityTable().getSelectedAuthority(), this), 700, 375);
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
			query.setText("Search");
			getRootPane().setDefaultButton(query);
			query.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					disableAllActions();
					Runner runner = new Runner() {
						public void execute() {
							getAuthorities();
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


	private void getAuthorities() {
	    getProgressPanel().showProgress("Searching...");
		this.getAuthorityTable().clearTable();

		try {
			GTSPublicClient client = getSession().getSession().getUserClient();
			AuthorityGTS[] auth = client.getAuthorities();
			int length = 0;
			if (auth != null) {
				length = auth.length;
				for (int i = 0; i < auth.length; i++) {
					this.getAuthorityTable().addAuthority(auth[i]);
				}
			}
			searchDone = true;
			getProgressPanel().stopProgress(length+" authority(s) found.");

		} catch (Exception e) {
			ErrorDialog.showError(e);
			getProgressPanel().stopProgress("Error.");
		} finally {
			enableAllActions();
		}

	}


	/**
	 * This method initializes removeUser
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getRemoveAuthority() {
		if (removeAuthority == null) {
			removeAuthority = new JButton();
			removeAuthority.setText("Remove");
			removeAuthority.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					disableAllActions();
					Runner runner = new Runner() {
						public void execute() {
							removeAuthority();
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
		return removeAuthority;
	}


	private void removeAuthority() {
		try {
		    getProgressPanel().showProgress("Removing authority...");
			GTSAdminClient client = getSession().getSession().getAdminClient();
			AuthorityGTS gts = this.getAuthorityTable().getSelectedAuthority();
			client.removeAuthority(gts.getServiceURI());
			getAuthorities();
			getProgressPanel().stopProgress("Authority successfully removed.");
		} catch (Exception e) {
			ErrorDialog.showError(e);
			getProgressPanel().stopProgress("Error");
		}
	}


	private void updatePriorities() {
		try {
		    
			disableAllActions();
			getProgressPanel().showProgress("Updating priorities...");
			GTSAdminClient client = getSession().getSession().getAdminClient();
			client.updateAuthorityPriorities(getAuthorityTable().getPriorityUpdate());
			getProgressPanel().stopProgress("Successfully updated the priorities.");
		} catch (Exception e) {
			ErrorDialog.showError(e);
			getProgressPanel().stopProgress("Error");
		} finally {
			enableAllActions();
		}
	}


	private void disableAllActions() {
		getQuery().setEnabled(false);
		getAddAuthority().setEnabled(false);
		getViewModifyButton().setEnabled(false);
		getRemoveAuthority().setEnabled(false);
		getUpdatePriorities().setEnabled(false);
		getIncreasePriority().setEnabled(false);
		getDecreasePriority().setEnabled(false);
	}


	private void enableAllActions() {
		getQuery().setEnabled(true);
		getAddAuthority().setEnabled(true);
		getViewModifyButton().setEnabled(true);
		getRemoveAuthority().setEnabled(true);
		getUpdatePriorities().setEnabled(true);
		getIncreasePriority().setEnabled(true);
		getDecreasePriority().setEnabled(true);
	}


	/**
	 * This method initializes viewModifyButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getViewModifyButton() {
		if (viewModifyButton == null) {
			viewModifyButton = new JButton();
			viewModifyButton.setText("View");
			viewModifyButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					Runner runner = new Runner() {
						public void execute() {
							disableAllActions();
							try {
								getAuthorityTable().doubleClick();
							} catch (Exception ex) {
								ErrorDialog.showError(ex);
							}
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
		return viewModifyButton;
	}


	/**
	 * This method initializes priorityPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getPriorityPanel() {
		if (priorityPanel == null) {
			GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
			gridBagConstraints7.gridx = 0;
			gridBagConstraints7.insets = new Insets(0, 0, 0, 0);
			gridBagConstraints7.gridy = 2;
			GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
			gridBagConstraints6.insets = new Insets(0, 0, 0, 0);
			gridBagConstraints6.gridy = 1;
			gridBagConstraints6.anchor = GridBagConstraints.CENTER;
			gridBagConstraints6.gridx = 0;
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.insets = new Insets(0, 0, 0, 0);
			gridBagConstraints5.gridy = 0;
			gridBagConstraints5.anchor = GridBagConstraints.CENTER;
			gridBagConstraints5.gridx = 0;
			priorityPanel = new JPanel();
			priorityPanel.setLayout(new GridBagLayout());
			priorityPanel.add(getIncreasePriority(), gridBagConstraints5);
			priorityPanel.add(getDecreasePriority(), gridBagConstraints6);
			priorityPanel.add(getUpdatePriorities(), gridBagConstraints7);
		}
		return priorityPanel;
	}


	/**
	 * This method initializes increasePriority
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getIncreasePriority() {
		if (increasePriority == null) {
			increasePriority = new JButton();
			increasePriority.setText("Increase Priority");
			increasePriority.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					try {
						getAuthorityTable().increasePriority();
					} catch (Exception ex) {
						ErrorDialog.showError(ex);
					}
				}
			});
		}
		return increasePriority;
	}


	/**
	 * This method initializes decreasePriority
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getDecreasePriority() {
		if (decreasePriority == null) {
			decreasePriority = new JButton();
			decreasePriority.setText("Decrease Priority");
			decreasePriority.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					try {
						getAuthorityTable().decreasePriority();
					} catch (Exception ex) {
						ErrorDialog.showError(ex);
					}
				}
			});
		}
		return decreasePriority;
	}


	/**
	 * This method initializes updatePriorities
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getUpdatePriorities() {
		if (updatePriorities == null) {
			updatePriorities = new JButton();
			updatePriorities.setText("Update Priority(s)");
			updatePriorities.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					disableAllActions();
					Runner runner = new Runner() {
						public void execute() {
							updatePriorities();
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
		return updatePriorities;
	}


	public void refeshAuthorities() {
		if (searchDone) {
			disableAllActions();
			getAuthorities();
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
            titlePanel = new TitlePanel("Trust Fabric Authority(s)","Search for and manage trust fabric authorities.");
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
            session = new SessionPanel();
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
