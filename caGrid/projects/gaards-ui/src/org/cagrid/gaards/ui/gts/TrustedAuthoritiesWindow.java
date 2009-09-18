package org.cagrid.gaards.ui.gts;

import gov.nih.nci.cagrid.common.Runner;
import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.gts.bean.TrustLevel;
import gov.nih.nci.cagrid.gts.bean.TrustLevels;
import gov.nih.nci.cagrid.gts.bean.TrustedAuthority;
import gov.nih.nci.cagrid.gts.bean.TrustedAuthorityFilter;
import gov.nih.nci.cagrid.gts.client.GTSAdminClient;
import gov.nih.nci.cagrid.gts.client.GTSPublicClient;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
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
public class TrustedAuthoritiesWindow extends ApplicationComponent
    implements
        TrustedAuthorityRefresher,
        ServiceSelectionListener {

    private static final long serialVersionUID = 1L;

    private final static String ANY = "Any";

    private javax.swing.JPanel jContentPane = null;

    private JPanel mainPanel = null;

    private JPanel contentPanel = null;

    private JPanel buttonPanel = null;

    private TrustedAuthorityTable trustedAuthorityTable = null;

    private JScrollPane jScrollPane = null;

    private JButton viewTrustedAuthority = null;

    private SessionPanel sessionPanel = null;

    private JPanel queryPanel = null;

    private JButton query = null;

    private JButton removeTrustedAuthorityButton = null;

    private JPanel filterPanel = null;

    private JLabel jLabel = null;

    private JTextField trustedAuthorityName = null;

    private JLabel jLabel1 = null;

    private JLabel jLabel2 = null;

    private JComboBox trustLevel = null;

    private JComboBox status = null;

    private JButton addButton = null;

    private boolean searchDone = false;

    private JLabel jLabel3 = null;

    private LifetimeComboBox lifetime = null;

    private JLabel jLabel4 = null;

    private IsAuthorityComboBox isAuthority = null;

    private JLabel jLabel5 = null;

    private GTSServiceListComboBox authorityGTS = null;

    private JLabel jLabel6 = null;

    private GTSServiceListComboBox sourceGTS = null;

    private JPanel titlePanel = null;

    private ProgressPanel progressPanel = null;


    /**
     * This is the default constructor
     */
    public TrustedAuthoritiesWindow() {
        super();
        initialize();
        this.setFrameIcon(GTSLookAndFeel.getTrustedAuthorityIcon());
    }


    /**
     * This method initializes this
     */
    private void initialize() {
        this.setSize(500, 500);
        this.setContentPane(getJContentPane());
        this.setTitle("Trusted Certificate Authority Management");
        updateLevelsOfAssurance();

    }


    private void lockAll() {
        getAddButton().setEnabled(false);
        getViewTrustedAuthority().setEnabled(false);
        getRemoveTrustedAuthorityButton().setEnabled(false);
        getQuery().setEnabled(false);
        getTrustedAuthorityName().setEditable(false);
        getTrustLevel().setEnabled(false);
        getStatus().setEnabled(false);
        getLifetime().setEnabled(false);
        getIsAuthority().setEnabled(false);
        getAuthorityGTS().setEnabled(false);
        getSourceGTS().setEnabled(false);
    }


    private void unlockAll() {
        getAddButton().setEnabled(true);
        getViewTrustedAuthority().setEnabled(true);
        getRemoveTrustedAuthorityButton().setEnabled(true);
        getQuery().setEnabled(true);
        getTrustedAuthorityName().setEditable(true);
        getTrustLevel().setEnabled(true);
        getStatus().setEnabled(true);
        getLifetime().setEnabled(true);
        getIsAuthority().setEnabled(true);
        getAuthorityGTS().setEnabled(true);
        getSourceGTS().setEnabled(true);
    }


    private void updateLevelsOfAssurance() {
        lockAll();
        getProgressPanel().showProgress("Discovering levels of assurance...");
        trustLevel.removeAllItems();
        trustLevel.addItem(ANY);
        try {
            GTSPublicClient client = getSessionPanel().getSession().getUserClient();
            TrustLevel[] levels = client.getTrustLevels();
            if (levels != null) {
                for (int i = 0; i < levels.length; i++) {
                    trustLevel.addItem(levels[i].getName());
                }

            }
            getProgressPanel().stopProgress();
        } catch (Exception e) {
            e.printStackTrace();
            ErrorDialog.showError("Error obtaining the trust levels from " + getSessionPanel().getServiceURI() + ":\n"
                + e.getMessage());
            getProgressPanel().stopProgress();
        }
        unlockAll();
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
            GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
            gridBagConstraints21.gridx = 0;
            gridBagConstraints21.weightx = 1.0D;
            gridBagConstraints21.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints21.gridy = 6;
            GridBagConstraints gridBagConstraints18 = new GridBagConstraints();
            gridBagConstraints18.gridx = 0;
            gridBagConstraints18.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints18.weightx = 1.0D;
            gridBagConstraints18.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints18.gridy = 0;
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints.weightx = 1.0D;
            gridBagConstraints.gridy = 2;
            GridBagConstraints gridBagConstraints33 = new GridBagConstraints();
            gridBagConstraints33.gridx = 0;
            gridBagConstraints33.gridy = 3;
            GridBagConstraints gridBagConstraints35 = new GridBagConstraints();
            gridBagConstraints35.gridx = 0;
            gridBagConstraints35.weightx = 1.0D;
            gridBagConstraints35.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints35.gridy = 1;

            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
            mainPanel = new JPanel();
            mainPanel.setLayout(new GridBagLayout());
            gridBagConstraints1.gridx = 0;
            gridBagConstraints1.gridy = 4;
            gridBagConstraints1.ipadx = 0;
            gridBagConstraints1.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints1.weightx = 1.0D;
            gridBagConstraints1.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints1.weighty = 1.0D;
            gridBagConstraints2.gridx = 0;
            gridBagConstraints2.gridy = 5;
            gridBagConstraints2.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints2.anchor = java.awt.GridBagConstraints.SOUTH;
            gridBagConstraints2.fill = java.awt.GridBagConstraints.HORIZONTAL;
            mainPanel.add(getButtonPanel(), gridBagConstraints2);
            mainPanel.add(getContentPanel(), gridBagConstraints1);
            mainPanel.add(getSessionPanel(), gridBagConstraints35);
            mainPanel.add(getQueryPanel(), gridBagConstraints33);
            mainPanel.add(getFilterPanel(), gridBagConstraints);
            mainPanel.add(getTitlePanel(), gridBagConstraints18);
            mainPanel.add(getProgressPanel(), gridBagConstraints21);
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
            contentPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Trusted Authority(s)",
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
            buttonPanel.add(getAddButton(), null);
            buttonPanel.add(getViewTrustedAuthority(), null);
            buttonPanel.add(getRemoveTrustedAuthorityButton(), null);
        }
        return buttonPanel;
    }


    /**
     * This method initializes jTable
     * 
     * @return javax.swing.JTable
     */
    private TrustedAuthorityTable getTrustedAuthorityTable() {
        if (trustedAuthorityTable == null) {
            trustedAuthorityTable = new TrustedAuthorityTable(this);
        }
        return trustedAuthorityTable;
    }


    /**
     * This method initializes jScrollPane
     * 
     * @return javax.swing.JScrollPane
     */
    private JScrollPane getJScrollPane() {
        if (jScrollPane == null) {
            jScrollPane = new JScrollPane();
            jScrollPane.setViewportView(getTrustedAuthorityTable());
        }
        return jScrollPane;
    }


    /**
     * This method initializes manageUser
     * 
     * @return javax.swing.JButton
     */
    private JButton getViewTrustedAuthority() {
        if (viewTrustedAuthority == null) {
            viewTrustedAuthority = new JButton();
            viewTrustedAuthority.setText("View");
            viewTrustedAuthority.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    Runner runner = new Runner() {
                        public void execute() {
                            lockAll();
                            showTrustedAuthority();
                            unlockAll();
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

        return viewTrustedAuthority;
    }


    public void showTrustedAuthority() {
        try {
            TrustedAuthorityWindow window = new TrustedAuthorityWindow(getSessionPanel().getSession(), this
                .getTrustedAuthorityTable().getSelectedTrustedAuthority(), this);
            GridApplication.getContext().addApplicationComponent(window, 800, 600);
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
            sessionPanel = new SessionPanel(this);
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
                            lockAll();
                            findTrustedAuthorities();
                            unlockAll();
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


    private void findTrustedAuthorities() {
        this.getTrustedAuthorityTable().clearTable();
        getProgressPanel().showProgress("Searching...");

        try {

            TrustedAuthorityFilter filter = new TrustedAuthorityFilter();
            filter.setName(Utils.clean(trustedAuthorityName.getText()));
            String tl = (String) trustLevel.getSelectedItem();
            TrustLevels levels = new TrustLevels();
            if (!tl.equals(ANY)) {
                String[] list = new String[1];
                list[0] = tl;
                levels.setTrustLevel(list);
            }
            filter.setTrustLevels(levels);
            filter.setStatus(((StatusComboBox) status).getStatus());
            filter.setLifetime(this.lifetime.getLifetime());
            filter.setIsAuthority(this.getIsAuthority().getIsAuthority());
            String authority = null;
            if (this.getAuthorityGTS().getSelectedService() != null) {
                authority = this.getAuthorityGTS().getSelectedService().getServiceURL();
            }
            filter.setAuthorityGTS(authority);

            String source = null;
            if (this.getSourceGTS().getSelectedService() != null) {
                source = this.getSourceGTS().getSelectedService().getServiceURL();
            }

            filter.setSourceGTS(source);
            GTSPublicClient client = getSessionPanel().getSession().getUserClient();
            int length = 0;
            TrustedAuthority[] tas = client.findTrustedAuthorities(filter);
            if (tas != null) {
                length = tas.length;
            }
            for (int i = 0; i < length; i++) {
                this.trustedAuthorityTable.addTrustedAuthority(tas[i]);
            }
            searchDone = true;

            this.getProgressPanel().stopProgress(length + " certificate authority(s) found.");

        } catch (Exception e) {
            ErrorDialog.showError(e);
            this.getProgressPanel().stopProgress("Error");
        }
    }


    /**
     * This method initializes removeUser
     * 
     * @return javax.swing.JButton
     */
    private JButton getRemoveTrustedAuthorityButton() {
        if (removeTrustedAuthorityButton == null) {
            removeTrustedAuthorityButton = new JButton();
            removeTrustedAuthorityButton.setText("Remove");
            removeTrustedAuthorityButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    Runner runner = new Runner() {
                        public void execute() {
                            lockAll();
                            removeTrustedAuthority();
                            unlockAll();
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
        return removeTrustedAuthorityButton;
    }


    private void removeTrustedAuthority() {
        try {
            getProgressPanel().showProgress("Removing certificate authority...");
            GTSAdminClient client = getSessionPanel().getSession().getAdminClient();
            client.removeTrustedAuthority(this.getTrustedAuthorityTable().getSelectedTrustedAuthority().getName());
            this.getTrustedAuthorityTable().removeSelectedTrustedAuthority();
            getProgressPanel().stopProgress("Successfully removed certificate authority.");
            refreshTrustedAuthorities();
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
    private JPanel getFilterPanel() {
        if (filterPanel == null) {
            GridBagConstraints gridBagConstraints17 = new GridBagConstraints();
            gridBagConstraints17.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints17.gridy = 6;
            gridBagConstraints17.weightx = 1.0;
            gridBagConstraints17.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints17.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints17.gridx = 1;
            GridBagConstraints gridBagConstraints16 = new GridBagConstraints();
            gridBagConstraints16.gridx = 0;
            gridBagConstraints16.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints16.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints16.gridy = 6;
            jLabel6 = new JLabel();
            jLabel6.setText("Source GTS");
            GridBagConstraints gridBagConstraints15 = new GridBagConstraints();
            gridBagConstraints15.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints15.gridy = 5;
            gridBagConstraints15.weightx = 1.0;
            gridBagConstraints15.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints15.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints15.gridx = 1;
            GridBagConstraints gridBagConstraints14 = new GridBagConstraints();
            gridBagConstraints14.gridx = 0;
            gridBagConstraints14.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints14.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints14.gridy = 5;
            jLabel5 = new JLabel();
            jLabel5.setText("Authority GTS");
            GridBagConstraints gridBagConstraints13 = new GridBagConstraints();
            gridBagConstraints13.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints13.gridy = 4;
            gridBagConstraints13.weightx = 1.0;
            gridBagConstraints13.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints13.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints13.gridx = 1;
            GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
            gridBagConstraints12.gridx = 0;
            gridBagConstraints12.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints12.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints12.gridy = 4;
            jLabel4 = new JLabel();
            jLabel4.setText("Is Authority");
            GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
            gridBagConstraints11.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints11.gridy = 3;
            gridBagConstraints11.weightx = 1.0;
            gridBagConstraints11.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints11.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints11.gridx = 1;
            GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
            gridBagConstraints10.gridx = 0;
            gridBagConstraints10.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints10.gridy = 0;
            GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
            gridBagConstraints9.gridx = 0;
            gridBagConstraints9.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints9.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints9.gridy = 3;
            jLabel3 = new JLabel();
            jLabel3.setText("Lifetime");
            GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
            gridBagConstraints8.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints8.gridy = 2;
            gridBagConstraints8.weightx = 1.0;
            gridBagConstraints8.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints8.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints8.gridx = 1;
            GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
            gridBagConstraints7.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints7.gridy = 1;
            gridBagConstraints7.weightx = 1.0;
            gridBagConstraints7.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints7.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints7.gridx = 1;
            GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
            gridBagConstraints6.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints6.gridy = 2;
            gridBagConstraints6.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints6.gridx = 0;
            jLabel2 = new JLabel();
            jLabel2.setText("Status");
            GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
            gridBagConstraints5.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints5.gridy = 1;
            gridBagConstraints5.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints5.gridx = 0;
            jLabel1 = new JLabel();
            jLabel1.setText("Level of Assurance");
            GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
            gridBagConstraints3.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints3.gridy = 0;
            gridBagConstraints3.weightx = 1.0;
            gridBagConstraints3.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints3.gridx = 1;
            jLabel = new JLabel();
            jLabel.setText("Trusted Authority Name");
            filterPanel = new JPanel();
            filterPanel.setLayout(new GridBagLayout());
            filterPanel.setBorder(BorderFactory.createTitledBorder(null, "Search Criteria",
                TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, LookAndFeel
                    .getPanelLabelColor()));
            filterPanel.add(jLabel, gridBagConstraints10);
            filterPanel.add(getTrustedAuthorityName(), gridBagConstraints3);
            filterPanel.add(jLabel1, gridBagConstraints5);
            filterPanel.add(jLabel2, gridBagConstraints6);
            filterPanel.add(getTrustLevel(), gridBagConstraints7);
            filterPanel.add(getStatus(), gridBagConstraints8);
            filterPanel.add(jLabel3, gridBagConstraints9);
            filterPanel.add(getLifetime(), gridBagConstraints11);
            filterPanel.add(jLabel4, gridBagConstraints12);
            filterPanel.add(getIsAuthority(), gridBagConstraints13);
            filterPanel.add(jLabel5, gridBagConstraints14);
            filterPanel.add(getAuthorityGTS(), gridBagConstraints15);
            filterPanel.add(jLabel6, gridBagConstraints16);
            filterPanel.add(getSourceGTS(), gridBagConstraints17);
        }
        return filterPanel;
    }


    /**
     * This method initializes trustedAuthorityName
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getTrustedAuthorityName() {
        if (trustedAuthorityName == null) {
            trustedAuthorityName = new JTextField();
        }
        return trustedAuthorityName;
    }


    /**
     * This method initializes trustLevel
     * 
     * @return javax.swing.JComboBox
     */
    private JComboBox getTrustLevel() {
        if (trustLevel == null) {
            trustLevel = new JComboBox();
            trustLevel.addItem(ANY);
        }
        return trustLevel;
    }


    /**
     * This method initializes status
     * 
     * @return javax.swing.JComboBox
     */
    private JComboBox getStatus() {
        if (status == null) {
            status = new StatusComboBox(true);
        }
        return status;
    }


    /**
     * This method initializes addButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getAddButton() {
        if (addButton == null) {
            addButton = new JButton();
            addButton.setText("Add");
            addButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    lockAll();
                    addTrustedAuthority();
                    unlockAll();
                }
            });
        }
        return addButton;
    }


    private void addTrustedAuthority() {
        try {
            GridApplication.getContext().addApplicationComponent(
                new TrustedAuthorityWindow(getSessionPanel().getSession(), this));
        } catch (Exception e) {
            ErrorDialog.showError(e);
        }
    }


    public void refreshTrustedAuthorities() {
        if (searchDone) {
            findTrustedAuthorities();
        }
    }


    /**
     * This method initializes lifetime
     * 
     * @return javax.swing.JComboBox
     */
    private JComboBox getLifetime() {
        if (lifetime == null) {
            lifetime = new LifetimeComboBox();
        }
        return lifetime;
    }


    /**
     * This method initializes isAuthority
     * 
     * @return javax.swing.JComboBox
     */
    private IsAuthorityComboBox getIsAuthority() {
        if (isAuthority == null) {
            isAuthority = new IsAuthorityComboBox();
        }
        return isAuthority;
    }


    /**
     * This method initializes authorityGTS
     * 
     * @return javax.swing.JComboBox
     */
    private GTSServiceListComboBox getAuthorityGTS() {
        if (authorityGTS == null) {
            authorityGTS = new GTSServiceListComboBox(true);
        }
        return authorityGTS;
    }


    /**
     * This method initializes sourceGTS
     * 
     * @return javax.swing.JComboBox
     */
    private GTSServiceListComboBox getSourceGTS() {
        if (sourceGTS == null) {
            sourceGTS = new GTSServiceListComboBox(true);
        }
        return sourceGTS;
    }


    public void handleServiceSelected() {
        Runner runner = new Runner() {
            public void execute() {
                updateLevelsOfAssurance();
            }
        };
        try {
            GridApplication.getContext().executeInBackground(runner);
        } catch (Exception t) {
            t.getMessage();
        }

    }


    /**
     * This method initializes titlePanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getTitlePanel() {
        if (titlePanel == null) {
            titlePanel = new TitlePanel("Certificate Authority Search",
                "Search for and manage certificate authority(s).");
        }
        return titlePanel;
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
