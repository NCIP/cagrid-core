package org.cagrid.gaards.ui.dorian.federation;

import gov.nih.nci.cagrid.common.Runner;
import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.dorian.common.CommonUtils;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.cagrid.gaards.dorian.client.GridAdministrationClient;
import org.cagrid.gaards.dorian.client.GridUserClient;
import org.cagrid.gaards.dorian.federation.GridUser;
import org.cagrid.gaards.dorian.federation.GridUserFilter;
import org.cagrid.gaards.dorian.federation.GridUserRecord;
import org.cagrid.gaards.dorian.federation.GridUserSearchCriteria;
import org.cagrid.gaards.dorian.federation.HostCertificateFilter;
import org.cagrid.gaards.dorian.federation.HostCertificateRecord;
import org.cagrid.gaards.dorian.federation.HostRecord;
import org.cagrid.gaards.dorian.federation.HostSearchCriteria;
import org.cagrid.gaards.dorian.stubs.types.PermissionDeniedFault;
import org.cagrid.gaards.ui.common.ProgressPanel;
import org.cagrid.gaards.ui.common.TitlePanel;
import org.cagrid.gaards.ui.dorian.DorianSessionProvider;
import org.cagrid.gaards.ui.dorian.SessionPanel;
import org.cagrid.grape.GridApplication;
import org.cagrid.grape.LookAndFeel;
import org.cagrid.grape.utils.ErrorDialog;


public class CommunitySearchDialog extends JDialog {

    private static final long serialVersionUID = 1L;
    private JPanel jContentPane = null;
    private DorianSessionProvider sessionProvider;
    private JPanel titlePanel = null;
    private SessionPanel sessionPanel = null;
    private JTabbedPane jTabbedPane = null;
    private JPanel userSearch = null;
    private JPanel userSearchCriteria = null;
    private JLabel jLabel = null;
    private JTextField userIdentity = null;
    private JLabel jLabel1 = null;
    private JTextField firstName = null;
    private JLabel jLabel2 = null;
    private JTextField lastName = null;
    private JLabel jLabel3 = null;
    private JTextField email = null;
    private JButton userSearchButton = null;
    private JScrollPane jScrollPane = null;
    private GridUserRecordsTable usersTable = null;
    private JPanel userSearchButtonPanel = null;
    private JButton userSelect = null;
    private ProgressPanel progress = null;
    private String selectedIdentity;
    private JPanel hostSearch = null;
    private JPanel hostSearchCriteria = null;
    private JLabel jLabel4 = null;
    private JTextField hostIdentity = null;
    private JLabel jLabel5 = null;
    private JTextField hostname = null;
    private JLabel jLabel6 = null;
    private JTextField hostOwner = null;
    private JButton jButton = null;
    private JButton hostSearchButton = null;
    private JScrollPane jScrollPane1 = null;
    private HostRecordsTable hosts = null;
    private JButton jButton1 = null;
    private JLabel jLabel7 = null;
    private JTextField hostSubject = null;


    public CommunitySearchDialog() {
        this(null);
    }


    public CommunitySearchDialog(DorianSessionProvider provider) {
        super(GridApplication.getContext().getApplication());
        if (provider != null) {
            this.sessionProvider = provider;
        }
        initialize();
        if (provider != null) {
            setSize(800, 450);
        } else {
            setSize(800, 550);
        }

    }


    /**
     * This method initializes this
     * 
     * @return void
     */
    private void initialize() {
        this.setContentPane(getJContentPane());
        this.setTitle("Community Search");
    }


    /**
     * This method initializes jContentPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getJContentPane() {
        if (jContentPane == null) {
            GridBagConstraints gridBagConstraints31 = new GridBagConstraints();
            gridBagConstraints31.gridx = 0;
            gridBagConstraints31.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints31.weightx = 1.0D;
            gridBagConstraints31.gridy = 3;
            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            gridBagConstraints2.fill = GridBagConstraints.BOTH;
            gridBagConstraints2.weighty = 1.0;
            gridBagConstraints2.gridx = 0;
            gridBagConstraints2.gridy = 2;
            gridBagConstraints2.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints2.weightx = 1.0;
            GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
            gridBagConstraints1.gridx = 0;
            gridBagConstraints1.anchor = GridBagConstraints.WEST;
            gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints1.weightx = 1.0D;
            gridBagConstraints1.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints1.gridy = 1;
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridheight = 1;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.ipadx = 300;
            gridBagConstraints.anchor = GridBagConstraints.WEST;
            gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints.weightx = 1.0D;
            gridBagConstraints.gridx = 0;
            jContentPane = new JPanel();
            jContentPane.setLayout(new GridBagLayout());
            jContentPane.add(getTitlePanel(), gridBagConstraints);

            if (sessionProvider == null) {
                sessionProvider = getSessionPanel();
                jContentPane.add(getSessionPanel(), gridBagConstraints1);
                jContentPane.add(getJTabbedPane(), gridBagConstraints2);
                jContentPane.add(getProgress(), gridBagConstraints31);
            }

        }
        return jContentPane;
    }


    /**
     * This method initializes titlePanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getTitlePanel() {
        if (titlePanel == null) {
            titlePanel = new TitlePanel("Community Search",
                "Search for users or hosts in the community based on specified search criteria.");
        }
        return titlePanel;
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
     * This method initializes jTabbedPane
     * 
     * @return javax.swing.JTabbedPane
     */
    private JTabbedPane getJTabbedPane() {
        if (jTabbedPane == null) {
            jTabbedPane = new JTabbedPane();
            jTabbedPane.addTab("User Search", null, getUserSearch(), null);
            jTabbedPane.addTab("Host Search", null, getHostSearch(), null);
            jTabbedPane.addChangeListener(new ChangeListener() {
                public void stateChanged(ChangeEvent e) {
                    JPanel panel = (JPanel) jTabbedPane.getSelectedComponent();
                    if (panel.equals(getHostSearch())) {
                        getRootPane().setDefaultButton(getHostSearchButton());
                    } else {
                        getRootPane().setDefaultButton(getUserSearchButton());
                    }
                }
            });
        }
        return jTabbedPane;
    }


    /**
     * This method initializes userSearch
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getUserSearch() {
        if (userSearch == null) {
            GridBagConstraints gridBagConstraints28 = new GridBagConstraints();
            gridBagConstraints28.gridx = 0;
            gridBagConstraints28.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints28.gridy = 4;
            GridBagConstraints gridBagConstraints14 = new GridBagConstraints();
            gridBagConstraints14.gridx = 0;
            gridBagConstraints14.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints14.gridy = 3;
            GridBagConstraints gridBagConstraints13 = new GridBagConstraints();
            gridBagConstraints13.fill = GridBagConstraints.BOTH;
            gridBagConstraints13.weighty = 1.0;
            gridBagConstraints13.gridx = 0;
            gridBagConstraints13.gridy = 2;
            gridBagConstraints13.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints13.weightx = 1.0;
            GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
            gridBagConstraints12.gridx = 0;
            gridBagConstraints12.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints12.gridy = 1;
            GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
            gridBagConstraints3.gridx = 0;
            gridBagConstraints3.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints3.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints3.weightx = 1.0D;
            gridBagConstraints3.gridy = 0;
            userSearch = new JPanel();
            userSearch.setLayout(new GridBagLayout());
            userSearch.add(getUserSearchCriteria(), gridBagConstraints3);
            userSearch.add(getUserSearchButton(), gridBagConstraints12);
            userSearch.add(getJScrollPane(), gridBagConstraints13);
            userSearch.add(getUserSearchButtonPanel(), gridBagConstraints14);
            userSearch.add(getUserSelect(), gridBagConstraints28);
        }
        return userSearch;
    }


    /**
     * This method initializes userSearchCriteria
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getUserSearchCriteria() {
        if (userSearchCriteria == null) {
            GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
            gridBagConstraints11.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints11.gridy = 3;
            gridBagConstraints11.weightx = 1.0;
            gridBagConstraints11.anchor = GridBagConstraints.WEST;
            gridBagConstraints11.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints11.gridx = 1;
            GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
            gridBagConstraints10.gridx = 0;
            gridBagConstraints10.anchor = GridBagConstraints.WEST;
            gridBagConstraints10.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints10.gridy = 3;
            jLabel3 = new JLabel();
            jLabel3.setText("Email");
            GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
            gridBagConstraints9.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints9.gridy = 2;
            gridBagConstraints9.weightx = 1.0;
            gridBagConstraints9.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints9.gridx = 1;
            GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
            gridBagConstraints8.gridx = 0;
            gridBagConstraints8.anchor = GridBagConstraints.WEST;
            gridBagConstraints8.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints8.gridy = 2;
            jLabel2 = new JLabel();
            jLabel2.setText("Last Name");
            GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
            gridBagConstraints7.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints7.anchor = GridBagConstraints.WEST;
            gridBagConstraints7.gridx = 1;
            gridBagConstraints7.gridy = 1;
            gridBagConstraints7.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints7.weightx = 1.0;
            GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
            gridBagConstraints6.gridx = 0;
            gridBagConstraints6.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints6.anchor = GridBagConstraints.WEST;
            gridBagConstraints6.gridy = 1;
            jLabel1 = new JLabel();
            jLabel1.setText("First Name");
            GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
            gridBagConstraints5.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints5.gridx = 1;
            gridBagConstraints5.gridy = 0;
            gridBagConstraints5.anchor = GridBagConstraints.WEST;
            gridBagConstraints5.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints5.weightx = 1.0;
            GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
            gridBagConstraints4.gridx = 0;
            gridBagConstraints4.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints4.anchor = GridBagConstraints.WEST;
            gridBagConstraints4.gridy = 0;
            jLabel = new JLabel();
            jLabel.setText("User Identity");
            userSearchCriteria = new JPanel();
            userSearchCriteria.setLayout(new GridBagLayout());
            userSearchCriteria.setBorder(BorderFactory.createTitledBorder(null, "Search Criteria",
                TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, LookAndFeel
                    .getPanelLabelColor()));
            userSearchCriteria.add(jLabel, gridBagConstraints4);
            userSearchCriteria.add(getUserIdentity(), gridBagConstraints5);
            userSearchCriteria.add(jLabel1, gridBagConstraints6);
            userSearchCriteria.add(getFirstName(), gridBagConstraints7);
            userSearchCriteria.add(jLabel2, gridBagConstraints8);
            userSearchCriteria.add(getLastName(), gridBagConstraints9);
            userSearchCriteria.add(jLabel3, gridBagConstraints10);
            userSearchCriteria.add(getEmail(), gridBagConstraints11);
        }
        return userSearchCriteria;
    }


    /**
     * This method initializes userIdentity
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getUserIdentity() {
        if (userIdentity == null) {
            userIdentity = new JTextField();
        }
        return userIdentity;
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
     * This method initializes userSearchButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getUserSearchButton() {
        if (userSearchButton == null) {
            userSearchButton = new JButton();
            userSearchButton.setText("Search");
            getRootPane().setDefaultButton(userSearchButton);
            userSearchButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    userSearchButton.setEnabled(false);
                    Runner runner = new Runner() {
                        public void execute() {
                            findUsers();
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
        return userSearchButton;
    }


    private void findUsers() {
        this.getUsersTable().clearTable();
        getProgress().showProgress("Searching...");

        try {

            GridUserClient client = this.sessionProvider.getSession().getUserClient();
            String version = client.getServiceVersion();
            if (version.equals(GridUserClient.VERSION_1_0) || version.equals(GridUserClient.VERSION_1_1)
                || version.equals(GridUserClient.VERSION_1_2) || version.equals(GridUserClient.VERSION_1_3)
                || version.equals(GridUserClient.VERSION_UNKNOWN)) {
                GridAdministrationClient admin = this.sessionProvider.getSession().getAdminClient();
                GridUserFilter f = new GridUserFilter();
                f.setEmail(Utils.clean(getEmail().getText()));
                f.setFirstName(Utils.clean(getFirstName().getText()));
                f.setLastName(Utils.clean(getLastName().getText()));
                f.setGridId(Utils.clean(getUserIdentity().getText()));
                List<GridUser> users = admin.findUsers(f);
                for (int i = 0; i < users.size(); i++) {
                    GridUser u = users.get(i);
                    GridUserRecord usr = new GridUserRecord();
                    usr.setEmail(u.getEmail());
                    usr.setFirstName(u.getFirstName());
                    usr.setIdentity(u.getGridId());
                    usr.setLastName(u.getLastName());
                    this.getUsersTable().addUser(usr);
                }
                getProgress().stopProgress(users.size() + " user(s) found.");
            } else {
                GridUserSearchCriteria search = new GridUserSearchCriteria();
                search.setEmail(Utils.clean(getEmail().getText()));
                search.setFirstName(Utils.clean(getFirstName().getText()));
                search.setLastName(Utils.clean(getLastName().getText()));
                search.setIdentity(Utils.clean(getUserIdentity().getText()));
                List<GridUserRecord> users = client.userSearch(search);

                for (int i = 0; i < users.size(); i++) {
                    this.getUsersTable().addUser(users.get(i));
                }
                getProgress().stopProgress(users.size() + " user(s) found.");
            }

        } catch (PermissionDeniedFault pdf) {
            ErrorDialog.showError(pdf);
            getProgress().stopProgress("Error");
        } catch (Exception e) {
            ErrorDialog.showError(e);
            getProgress().stopProgress("Error");
        } finally {
            userSearchButton.setEnabled(true);
        }

    }


    private void findHosts() {
        this.getHosts().clearTable();
        getProgress().showProgress("Searching...");
        try {
            GridUserClient client = this.sessionProvider.getSession().getUserClient();
            String version = client.getServiceVersion();
            if (version.equals(GridUserClient.VERSION_1_0) || version.equals(GridUserClient.VERSION_1_1)
                || version.equals(GridUserClient.VERSION_1_2) || version.equals(GridUserClient.VERSION_1_3)
                || version.equals(GridUserClient.VERSION_UNKNOWN)) {
                GridAdministrationClient admin = this.sessionProvider.getSession().getAdminClient();
                HostCertificateFilter f = new HostCertificateFilter();
                f.setHost(Utils.clean(getHostname().getText()));
                f.setSubject(CommonUtils.identityToSubject(Utils.clean(getHostIdentity().getText())));
                f.setOwner(Utils.clean(getHostOwner().getText()));
                f.setSubject(Utils.clean(getHostSubject().getText()));
                List<HostCertificateRecord> hosts = admin.findHostCertificates(f);
                for (int i = 0; i < hosts.size(); i++) {
                    HostCertificateRecord r = hosts.get(i);
                    HostRecord host = new HostRecord();
                    host.setIdentity(CommonUtils.subjectToIdentity(r.getSubject()));
                    host.setHostCertificateSubject(r.getSubject());
                    host.setHostname(r.getHost());
                    host.setOwner(r.getOwner());
                    this.getHosts().addHost(host);
                }
                getProgress().stopProgress(hosts.size() + " host(s) found.");
            } else {
                HostSearchCriteria search = new HostSearchCriteria();
                search.setHostname(Utils.clean(getHostname().getText()));
                search.setIdentity(Utils.clean(getHostIdentity().getText()));
                search.setOwner(Utils.clean(getHostOwner().getText()));
                search.setHostCertificateSubject(Utils.clean(getHostSubject().getText()));
                List<HostRecord> hosts = client.hostSearch(search);

                for (int i = 0; i < hosts.size(); i++) {
                    this.getHosts().addHost(hosts.get(i));
                }
                getProgress().stopProgress(hosts.size() + " host(s) found.");
            }

        } catch (PermissionDeniedFault pdf) {
            ErrorDialog.showError(pdf);
            getProgress().stopProgress("Error");
        } catch (Exception e) {
            ErrorDialog.showError(e);
            getProgress().stopProgress("Error");
        } finally {
            hostSearchButton.setEnabled(true);
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
            jScrollPane.setViewportView(getUsersTable());
        }
        return jScrollPane;
    }


    /**
     * This method initializes usersTable
     * 
     * @return javax.swing.JTable
     */
    private GridUserRecordsTable getUsersTable() {
        if (usersTable == null) {
            usersTable = new GridUserRecordsTable();
        }
        return usersTable;
    }


    /**
     * This method initializes userSearchButtonPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getUserSearchButtonPanel() {
        if (userSearchButtonPanel == null) {
            userSearchButtonPanel = new JPanel();
            userSearchButtonPanel.setLayout(new GridBagLayout());
        }
        return userSearchButtonPanel;
    }


    /**
     * This method initializes userSelect
     * 
     * @return javax.swing.JButton
     */
    private JButton getUserSelect() {
        if (userSelect == null) {
            userSelect = new JButton();
            userSelect.setText("Select");
            userSelect.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    try {
                        selectedIdentity = getUsersTable().getSelectedUser().getIdentity();
                        dispose();
                    } catch (Exception ex) {
                        ErrorDialog.showError(ex);
                    }
                }
            });
        }
        return userSelect;
    }


    /**
     * This method initializes progress
     * 
     * @return javax.swing.JPanel
     */
    private ProgressPanel getProgress() {
        if (progress == null) {
            progress = new ProgressPanel();
        }
        return progress;
    }


    public String getSelectedIdentity() {
        return selectedIdentity;
    }


    /**
     * This method initializes hostSearch
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getHostSearch() {
        if (hostSearch == null) {
            GridBagConstraints gridBagConstraints25 = new GridBagConstraints();
            gridBagConstraints25.gridx = 0;
            gridBagConstraints25.gridy = 4;
            GridBagConstraints gridBagConstraints24 = new GridBagConstraints();
            gridBagConstraints24.fill = GridBagConstraints.BOTH;
            gridBagConstraints24.weighty = 1.0;
            gridBagConstraints24.gridx = 0;
            gridBagConstraints24.gridy = 2;
            gridBagConstraints24.weightx = 1.0;
            GridBagConstraints gridBagConstraints23 = new GridBagConstraints();
            gridBagConstraints23.gridx = 0;
            gridBagConstraints23.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints23.gridy = 1;
            jLabel5 = new JLabel();
            jLabel5.setText("Hostname");
            GridBagConstraints gridBagConstraints17 = new GridBagConstraints();
            gridBagConstraints17.gridx = 0;
            gridBagConstraints17.weightx = 1.0D;
            gridBagConstraints17.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints17.gridy = 0;
            hostSearch = new JPanel();
            hostSearch.setLayout(new GridBagLayout());
            hostSearch.add(getHostSearchCriteria(), gridBagConstraints17);
            hostSearch.add(getHostSearchButton(), gridBagConstraints23);
            hostSearch.add(getJScrollPane1(), gridBagConstraints24);
            hostSearch.add(getJButton1(), gridBagConstraints25);
        }
        return hostSearch;
    }


    /**
     * This method initializes hostSearchCriteria
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getHostSearchCriteria() {
        if (hostSearchCriteria == null) {
            GridBagConstraints gridBagConstraints27 = new GridBagConstraints();
            gridBagConstraints27.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints27.gridy = 2;
            gridBagConstraints27.weightx = 1.0;
            gridBagConstraints27.anchor = GridBagConstraints.WEST;
            gridBagConstraints27.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints27.gridwidth = 2;
            gridBagConstraints27.gridx = 1;
            GridBagConstraints gridBagConstraints26 = new GridBagConstraints();
            gridBagConstraints26.gridx = 0;
            gridBagConstraints26.anchor = GridBagConstraints.WEST;
            gridBagConstraints26.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints26.gridy = 2;
            jLabel7 = new JLabel();
            jLabel7.setText("Host Subject");
            GridBagConstraints gridBagConstraints22 = new GridBagConstraints();
            gridBagConstraints22.gridx = 2;
            gridBagConstraints22.anchor = GridBagConstraints.WEST;
            gridBagConstraints22.gridy = 3;
            GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
            gridBagConstraints21.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints21.gridx = 1;
            gridBagConstraints21.gridy = 3;
            gridBagConstraints21.anchor = GridBagConstraints.WEST;
            gridBagConstraints21.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints21.gridwidth = 1;
            gridBagConstraints21.weightx = 1.0;
            GridBagConstraints gridBagConstraints20 = new GridBagConstraints();
            gridBagConstraints20.anchor = GridBagConstraints.WEST;
            gridBagConstraints20.gridy = 3;
            gridBagConstraints20.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints20.gridx = 0;
            jLabel6 = new JLabel();
            jLabel6.setText("Owner");
            GridBagConstraints gridBagConstraints19 = new GridBagConstraints();
            gridBagConstraints19.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints19.gridy = 1;
            gridBagConstraints19.weightx = 1.0;
            gridBagConstraints19.anchor = GridBagConstraints.WEST;
            gridBagConstraints19.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints19.gridwidth = 2;
            gridBagConstraints19.gridx = 1;
            GridBagConstraints gridBagConstraints18 = new GridBagConstraints();
            gridBagConstraints18.gridx = 0;
            gridBagConstraints18.anchor = GridBagConstraints.WEST;
            gridBagConstraints18.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints18.gridy = 1;
            GridBagConstraints gridBagConstraints16 = new GridBagConstraints();
            gridBagConstraints16.gridx = 0;
            gridBagConstraints16.anchor = GridBagConstraints.WEST;
            gridBagConstraints16.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints16.gridy = 0;
            GridBagConstraints gridBagConstraints15 = new GridBagConstraints();
            gridBagConstraints15.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints15.anchor = GridBagConstraints.WEST;
            gridBagConstraints15.gridx = 1;
            gridBagConstraints15.gridy = 0;
            gridBagConstraints15.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints15.gridwidth = 2;
            gridBagConstraints15.weightx = 1.0;
            jLabel4 = new JLabel();
            jLabel4.setText("Host Identity");
            hostSearchCriteria = new JPanel();
            hostSearchCriteria.setBorder(BorderFactory.createTitledBorder(null, "Search Criteria",
                TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, LookAndFeel
                    .getPanelLabelColor()));
            hostSearchCriteria.setLayout(new GridBagLayout());
            hostSearchCriteria.add(jLabel4, gridBagConstraints16);
            hostSearchCriteria.add(getHostIdentity(), gridBagConstraints15);
            hostSearchCriteria.add(jLabel5, gridBagConstraints18);
            hostSearchCriteria.add(getHostname(), gridBagConstraints19);
            hostSearchCriteria.add(jLabel6, gridBagConstraints20);
            hostSearchCriteria.add(getHostOwner(), gridBagConstraints21);
            hostSearchCriteria.add(getJButton(), gridBagConstraints22);
            hostSearchCriteria.add(jLabel7, gridBagConstraints26);
            hostSearchCriteria.add(getHostSubject(), gridBagConstraints27);
        }
        return hostSearchCriteria;
    }


    /**
     * This method initializes hostIdentity
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getHostIdentity() {
        if (hostIdentity == null) {
            hostIdentity = new JTextField();
        }
        return hostIdentity;
    }


    /**
     * This method initializes hostname
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getHostname() {
        if (hostname == null) {
            hostname = new JTextField();
        }
        return hostname;
    }


    /**
     * This method initializes hostOwner
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getHostOwner() {
        if (hostOwner == null) {
            hostOwner = new JTextField();
        }
        return hostOwner;
    }


    /**
     * This method initializes jButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getJButton() {
        if (jButton == null) {
            jButton = new JButton();
            jButton.setText("Find");
            jButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    CommunitySearchDialog dialog = new CommunitySearchDialog();
                    dialog.setModal(true);
                    GridApplication.getContext().showDialog(dialog);
                    if (dialog.getSelectedIdentity() != null) {
                        getHostIdentity().setText(dialog.getSelectedIdentity());
                    }
                }
            });
        }
        return jButton;
    }


    /**
     * This method initializes hostSearchButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getHostSearchButton() {
        if (hostSearchButton == null) {
            hostSearchButton = new JButton();
            hostSearchButton.setText("Search");
            hostSearchButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    hostSearchButton.setEnabled(false);
                    Runner runner = new Runner() {
                        public void execute() {
                            findHosts();
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
        return hostSearchButton;
    }


    /**
     * This method initializes jScrollPane1
     * 
     * @return javax.swing.JScrollPane
     */
    private JScrollPane getJScrollPane1() {
        if (jScrollPane1 == null) {
            jScrollPane1 = new JScrollPane();
            jScrollPane1.setViewportView(getHosts());
        }
        return jScrollPane1;
    }


    /**
     * This method initializes hosts
     * 
     * @return javax.swing.JTable
     */
    private HostRecordsTable getHosts() {
        if (hosts == null) {
            hosts = new HostRecordsTable();
        }
        return hosts;
    }


    /**
     * This method initializes jButton1
     * 
     * @return javax.swing.JButton
     */
    private JButton getJButton1() {
        if (jButton1 == null) {
            jButton1 = new JButton();
            jButton1.setText("Select");
            jButton1.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    try {
                        selectedIdentity = getHosts().getSelectedHost().getIdentity();
                        dispose();
                    } catch (Exception ex) {
                        ErrorDialog.showError(ex);
                    }
                }
            });
        }
        return jButton1;
    }


    /**
     * This method initializes hostSubject
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getHostSubject() {
        if (hostSubject == null) {
            hostSubject = new JTextField();
        }
        return hostSubject;
    }

}
