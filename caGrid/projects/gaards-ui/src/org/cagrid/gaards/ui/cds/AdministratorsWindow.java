package org.cagrid.gaards.ui.cds;

import gov.nih.nci.cagrid.common.Runner;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.cagrid.gaards.cds.client.DelegationAdminClient;
import org.cagrid.gaards.cds.stubs.types.PermissionDeniedFault;
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
 * @version $Id: AdministratorsWindow.java,v 1.1 2007/04/26 18:43:49 langella
 *          Exp $
 */
public class AdministratorsWindow extends ApplicationComponent {

    private static final long serialVersionUID = 1L;

    private javax.swing.JPanel jContentPane = null;

    private JPanel mainPanel = null;

    private JPanel contentPanel = null;

    private JPanel buttonPanel = null;

    private AdminsTable adminsTable = null;

    private JScrollPane jScrollPane = null;

    private SessionPanel sessionPanel = null;

    private JPanel queryPanel = null;

    private JButton query = null;

    private ProgressPanel progressPanel = null;

    private JButton removeAdmin = null;

    private JButton addAdmin = null;

    private boolean loaded = false;

    private JPanel titlePanel = null;


    /**
     * This is the default constructor
     */
    public AdministratorsWindow() {
        super();
        initialize();
        this.setFrameIcon(CDSLookAndFeel.getAdminIcon());
    }


    /**
     * This method initializes this
     */
    private void initialize() {
        this.setContentPane(getJContentPane());
        this.setTitle("Administrators");

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
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints.weightx = 1.0D;
            gridBagConstraints.gridy = 0;
            GridBagConstraints gridBagConstraints32 = new GridBagConstraints();
            gridBagConstraints32.gridx = 0;
            gridBagConstraints32.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints32.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints32.weightx = 1.0D;
            gridBagConstraints32.gridy = 5;
            GridBagConstraints gridBagConstraints33 = new GridBagConstraints();
            gridBagConstraints33.gridx = 0;
            gridBagConstraints33.gridy = 2;
            GridBagConstraints gridBagConstraints35 = new GridBagConstraints();
            gridBagConstraints35.gridx = 0;
            gridBagConstraints35.weightx = 1.0D;
            gridBagConstraints35.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints35.gridy = 1;

            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
            mainPanel = new JPanel();
            mainPanel.setLayout(new GridBagLayout());
            gridBagConstraints1.gridx = 0;
            gridBagConstraints1.gridy = 3;
            gridBagConstraints1.ipadx = 0;
            gridBagConstraints1.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints1.weightx = 1.0D;
            gridBagConstraints1.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints1.weighty = 1.0D;
            gridBagConstraints2.gridx = 0;
            gridBagConstraints2.gridy = 4;
            gridBagConstraints2.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints2.anchor = java.awt.GridBagConstraints.SOUTH;
            gridBagConstraints2.fill = java.awt.GridBagConstraints.HORIZONTAL;
            mainPanel.add(getButtonPanel(), gridBagConstraints2);
            mainPanel.add(getContentPanel(), gridBagConstraints1);
            mainPanel.add(getSessionPanel(), gridBagConstraints35);
            mainPanel.add(getQueryPanel(), gridBagConstraints33);
            mainPanel.add(getProgressPanel(), gridBagConstraints32);
            mainPanel.add(getTitlePanel(), gridBagConstraints);
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
            contentPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Administrators",
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
            buttonPanel.add(getAddAdmin(), null);
            buttonPanel.add(getRemoveAdmin(), null);
        }
        return buttonPanel;
    }


    /**
     * This method initializes jTable
     * 
     * @return javax.swing.JTable
     */
    private AdminsTable getAdminsTable() {
        if (adminsTable == null) {
            adminsTable = new AdminsTable();
        }
        return adminsTable;
    }


    /**
     * This method initializes jScrollPane
     * 
     * @return javax.swing.JScrollPane
     */
    private JScrollPane getJScrollPane() {
        if (jScrollPane == null) {
            jScrollPane = new JScrollPane();
            jScrollPane.setViewportView(getAdminsTable());
        }
        return jScrollPane;
    }


    public void addAdmin() {
        try {
            AddAdminWindow window = new AddAdminWindow(getSessionPanel().getSession());
            window.setModal(true);
            GridApplication.getContext().showDialog(window);
            if (loaded) {
                this.listAdmins();
            }
        } catch (Exception e) {
            ErrorDialog.showError(e);
        }
    }


    /**
     * This method initializes sessionPanel
     * 
     * @return javax.swing.JPanel
     */
    private SessionPanel getSessionPanel() {
        if (sessionPanel == null) {
            sessionPanel = new SessionPanel();
        }
        return sessionPanel;
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
                    Runner runner = new Runner() {
                        public void execute() {
                            listAdmins();
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


    private void disableButtons() {
        getQuery().setEnabled(false);
        getAddAdmin().setEnabled(false);
        getRemoveAdmin().setEnabled(false);
    }


    private void enableButtons() {
        getQuery().setEnabled(true);
        getAddAdmin().setEnabled(true);
        getRemoveAdmin().setEnabled(true);
    }


    private void listAdmins() {
        disableButtons();
        this.getAdminsTable().clearTable();
        getProgressPanel().showProgress("Searching...");

        try {
            DelegationAdminClient client = getSessionPanel().getAdminClient();
            List<String> admins = client.getAdmins();

            for (int i = 0; i < admins.size(); i++) {
                this.getAdminsTable().addAdmin(admins.get(i));
            }

            loaded = true;
            getProgressPanel().stopProgress(admins.size()+" administrator(s) found.");
        } catch (PermissionDeniedFault pdf) {
            ErrorDialog.showError(pdf);
            getProgressPanel().stopProgress("Error");
        } catch (Exception e) {
            ErrorDialog.showError(e);
            getProgressPanel().stopProgress("Error");
        } finally {
            enableButtons();
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
     * This method initializes removeUser
     * 
     * @return javax.swing.JButton
     */
    private JButton getRemoveAdmin() {
        if (removeAdmin == null) {
            removeAdmin = new JButton();
            removeAdmin.setText("Remove");
            removeAdmin.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    Runner runner = new Runner() {
                        public void execute() {
                            removeAdmin();
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
        return removeAdmin;
    }


    private void removeAdmin() {
        try {
            disableButtons();
            getProgressPanel().showProgress("Removing administrator...");
            DelegationAdminClient client = getSessionPanel().getAdminClient();
            client.removeAdmin(getAdminsTable().getSelectedAdmin());
            getAdminsTable().removeSelectedAdmin();
            getProgressPanel().stopProgress("Administrator successfully removed.");
        } catch (Exception e) {
            getProgressPanel().stopProgress("Error");
            ErrorDialog.showError(e);
        }finally{
            enableButtons();
        }
    }


    /**
     * This method initializes addAdmin
     * 
     * @return javax.swing.JButton
     */
    private JButton getAddAdmin() {
        if (addAdmin == null) {
            addAdmin = new JButton();
            addAdmin.setText("Add");
            addAdmin.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    Runner runner = new Runner() {
                        public void execute() {
                            addAdmin();
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
        return addAdmin;
    }


    /**
     * This method initializes titlePanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getTitlePanel() {
        if (titlePanel == null) {
            titlePanel = new TitlePanel("Delegation Service Access Control", "List and manage administrators of delegation service(s)");
        }
        return titlePanel;
    }
}
