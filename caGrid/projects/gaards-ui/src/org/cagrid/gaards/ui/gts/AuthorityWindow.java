package org.cagrid.gaards.ui.gts;

import gov.nih.nci.cagrid.common.Runner;
import gov.nih.nci.cagrid.gts.bean.AuthorityGTS;
import gov.nih.nci.cagrid.gts.bean.TimeToLive;
import gov.nih.nci.cagrid.gts.client.GTSAdminClient;
import gov.nih.nci.cagrid.gts.client.GTSPublicClient;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.commons.logging.Log; import org.apache.commons.logging.LogFactory; 
import org.apache.commons.logging.LogFactory; 
import org.cagrid.gaards.ui.common.ProgressPanel;
import org.cagrid.gaards.ui.common.TitlePanel;
import org.cagrid.grape.ApplicationComponent;
import org.cagrid.grape.GridApplication;
import org.cagrid.grape.utils.ErrorDialog;


/**
 * @author <A href="mailto:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A href="mailto:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A href="mailto:hastings@bmi.osu.edu">Shannon Hastings </A>
 */
public class AuthorityWindow extends ApplicationComponent {
	private static Log log = LogFactory.getLog(AuthorityWindow.class);
	
    private static final long serialVersionUID = 1L;

    private JPanel jContentPane = null;

    private JPanel buttonPanel = null;

    private JButton addButton = null;

    private JPanel authorityPanel = null;

    private JLabel jLabel2 = null;

    private JTextField gtsURI = null;

    private boolean update = false;

    private JLabel Priority = null;

    private JComboBox priority = null;

    private JLabel jLabel3 = null;

    private JComboBox synchronizeTrustLevels = null;

    private JLabel jLabel4 = null;

    private JComboBox performAuthorization = null;

    private JLabel jLabel5 = null;

    private JTextField authorizationIdentity = null;

    private JPanel jPanel = null;

    private JLabel jLabel6 = null;

    private JLabel jLabel7 = null;

    private JComboBox hours = null;

    private JLabel jLabel8 = null;

    private JComboBox minutes = null;

    private JLabel jLabel9 = null;

    private JComboBox seconds = null;

    private AuthorityRefresher refresher;

    private JPanel titlePanel = null;

    private AuthorityGTS authority = null;

    private ProgressPanel progressPanel = null;

    private GTSSession session;


    /**
     * This is the default constructor
     */
    public AuthorityWindow(GTSSession session, AuthorityRefresher refresher) {
        this(session, null, refresher);
    }


    public AuthorityWindow(GTSSession session, AuthorityGTS auth, AuthorityRefresher refresher) {
        super();
        this.session = session;
        this.refresher = refresher;
        if (auth != null) {
            update = true;
        } else {
            update = false;
        }
        this.authority = auth;

        initialize();
        if (auth != null) {
            this.getGtsURI().setEditable(false);
            this.getGtsURI().setText(auth.getServiceURI());
            this.getPriority().setSelectedItem(new Integer(auth.getPriority()));
            this.getPriority().setEnabled(false);
            this.getSynchronizeTrustLevels().setSelectedItem(new Boolean(auth.isSyncTrustLevels()));
            this.getPerformAuthorization().setSelectedItem(new Boolean(auth.isPerformAuthorization()));
            this.getAuthorizationIdentity().setText(auth.getServiceIdentity());
            this.getHours().setSelectedItem(new Integer(auth.getTimeToLive().getHours()));
            this.getMinutes().setSelectedItem(new Integer(auth.getTimeToLive().getMinutes()));
            this.getSeconds().setSelectedItem(new Integer(auth.getTimeToLive().getSeconds()));
        }
    }


    private void syncPriorities() {
        lockAll();
        try {
            getPriority().removeAllItems();
            getProgressPanel().showProgress("Syncing priorities...");
            GTSPublicClient client = this.session.getUserClient();
            AuthorityGTS[] auths = client.getAuthorities();
            int count = 0;
            if (auths != null) {
                count = auths.length;
            }
            if (!update) {
                count = count + 1;
            }
            for (int i = 1; i <= count; i++) {
                this.getPriority().addItem(new Integer(i));
            }
            getProgressPanel().stopProgress("Successfully synced priorities.");
        } catch (Exception e) {
            ErrorDialog.showError(e);
            getProgressPanel().stopProgress("Error");
            log.error(e, e);
        } finally {
            unlockAll();
        }
    }


    /**
     * This method initializes this
     */
    private void initialize() {
        this.setSize(600, 400);
        this.setContentPane(getJContentPane());
        if (update) {
            this.setTitle("View/Modify Authority");
            this.setFrameIcon(GTSLookAndFeel.getQueryIcon());
        } else {
            this.setTitle("Add Authority");
            this.setFrameIcon(GTSLookAndFeel.getAddIcon());
        }
    }


    /**
     * This method initializes jContentPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getJContentPane() {
        if (jContentPane == null) {
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints.weightx = 1.0D;
            gridBagConstraints.gridy = 3;
            GridBagConstraints gridBagConstraints18 = new GridBagConstraints();
            gridBagConstraints18.gridx = 0;
            gridBagConstraints18.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints18.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints18.weightx = 1.0D;
            gridBagConstraints18.gridy = 0;
            GridBagConstraints gridBagConstraints31 = new GridBagConstraints();
            gridBagConstraints31.gridx = 0;
            gridBagConstraints31.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints31.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints31.weightx = 1.0D;
            gridBagConstraints31.weighty = 1.0D;
            gridBagConstraints31.gridy = 1;
            GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
            gridBagConstraints12.gridx = 0;
            gridBagConstraints12.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints12.gridy = 2;
            jContentPane = new JPanel();
            jContentPane.setLayout(new GridBagLayout());
            jContentPane.add(getButtonPanel(), gridBagConstraints12);
            jContentPane.add(getAuthorityPanel(), gridBagConstraints31);
            jContentPane.add(getTitlePanel(), gridBagConstraints18);
            jContentPane.add(getProgressPanel(), gridBagConstraints);
        }
        return jContentPane;
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
            getRootPane().setDefaultButton(addButton);
            if (update) {
                addButton.setText("Update");
            } else {
                addButton.setText("Add");
            }
            addButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    Runner runner = new Runner() {
                        public void execute() {
                            addUpdateAuthority();
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

        return addButton;
    }


    private void addUpdateAuthority() {

        try {
            lockAll();
            if (update) {
                getProgressPanel().showProgress("Updating authority...");
            } else {
                getProgressPanel().showProgress("Adding authority...");
            }
            AuthorityGTS auth = new AuthorityGTS();
            auth.setServiceURI(getGtsURI().getText().trim());
            auth.setPriority(((Integer) getPriority().getSelectedItem()).intValue());
            auth.setSyncTrustLevels(((Boolean) getSynchronizeTrustLevels().getSelectedItem()).booleanValue());
            auth.setPerformAuthorization(((Boolean) getPerformAuthorization().getSelectedItem()).booleanValue());
            if (auth.isPerformAuthorization()) {
                auth.setServiceIdentity(getAuthorizationIdentity().getText().trim());
            }
            TimeToLive ttl = new TimeToLive();
            ttl.setHours(((Integer) getHours().getSelectedItem()).intValue());
            ttl.setMinutes(((Integer) getMinutes().getSelectedItem()).intValue());
            ttl.setSeconds(((Integer) getSeconds().getSelectedItem()).intValue());
            auth.setTimeToLive(ttl);

            GTSAdminClient client = this.session.getAdminClient();
            if (update) {
                client.updateAuthority(auth);
            } else {
                client.addAuthority(auth);
            }
            refresher.refeshAuthorities();
            if (update) {
                getProgressPanel().stopProgress("Successfully updated authority.");
            } else {
                getProgressPanel().stopProgress("Successfully added authority.");
                dispose();
            }

        } catch (Exception e) {
            ErrorDialog.showError(e);
            log.error(e, e);
            getProgressPanel().stopProgress("Error");
        } finally {
            unlockAll();
        }

    }


    /**
     * This method initializes trustLevelPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getAuthorityPanel() {
        if (authorityPanel == null) {
            GridBagConstraints gridBagConstraints17 = new GridBagConstraints();
            gridBagConstraints17.gridx = 0;
            gridBagConstraints17.gridwidth = 2;
            gridBagConstraints17.insets = new java.awt.Insets(5, 5, 5, 5);
            gridBagConstraints17.gridy = 5;
            jLabel6 = new JLabel();
            jLabel6.setText("Trusted Authority Records Time To Live");
            GridBagConstraints gridBagConstraints16 = new GridBagConstraints();
            gridBagConstraints16.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints16.gridx = 0;
            gridBagConstraints16.gridy = 6;
            gridBagConstraints16.gridwidth = 2;
            gridBagConstraints16.weightx = 1.0D;
            GridBagConstraints gridBagConstraints15 = new GridBagConstraints();
            gridBagConstraints15.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints15.gridy = 4;
            gridBagConstraints15.weightx = 1.0;
            gridBagConstraints15.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints15.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints15.gridx = 1;
            GridBagConstraints gridBagConstraints14 = new GridBagConstraints();
            gridBagConstraints14.gridx = 0;
            gridBagConstraints14.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints14.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints14.gridy = 4;
            jLabel5 = new JLabel();
            jLabel5.setText("Authorization Identity");
            GridBagConstraints gridBagConstraints13 = new GridBagConstraints();
            gridBagConstraints13.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints13.gridy = 3;
            gridBagConstraints13.weightx = 1.0;
            gridBagConstraints13.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints13.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints13.gridx = 1;
            GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
            gridBagConstraints11.gridx = 0;
            gridBagConstraints11.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints11.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints11.gridy = 3;
            jLabel4 = new JLabel();
            jLabel4.setText("Perform Authorization");
            GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
            gridBagConstraints10.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints10.gridy = 2;
            gridBagConstraints10.weightx = 1.0;
            gridBagConstraints10.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints10.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints10.gridx = 1;
            GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
            gridBagConstraints9.gridx = 0;
            gridBagConstraints9.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints9.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints9.gridy = 2;
            jLabel3 = new JLabel();
            jLabel3.setText("Synchronize Assurance Levels");
            GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
            gridBagConstraints8.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints8.gridy = 1;
            gridBagConstraints8.weightx = 1.0;
            gridBagConstraints8.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints8.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints8.gridx = 1;
            GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
            gridBagConstraints7.gridx = 0;
            gridBagConstraints7.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints7.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints7.gridy = 1;
            Priority = new JLabel();
            Priority.setText("Priority");

            GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
            gridBagConstraints6.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints6.gridx = 1;
            gridBagConstraints6.gridy = 0;
            gridBagConstraints6.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints6.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints6.weightx = 1.0;
            GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
            gridBagConstraints1.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints1.gridy = 0;
            gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints1.gridx = 0;
            jLabel2 = new JLabel();
            jLabel2.setText("GTS URL");
            authorityPanel = new JPanel();
            authorityPanel.setLayout(new GridBagLayout());
            authorityPanel.add(jLabel2, gridBagConstraints1);
            authorityPanel.add(getGtsURI(), gridBagConstraints6);
            authorityPanel.add(Priority, gridBagConstraints7);
            authorityPanel.add(getPriority(), gridBagConstraints8);
            authorityPanel.add(jLabel3, gridBagConstraints9);
            authorityPanel.add(getSynchronizeTrustLevels(), gridBagConstraints10);
            authorityPanel.add(jLabel4, gridBagConstraints11);
            authorityPanel.add(getPerformAuthorization(), gridBagConstraints13);
            authorityPanel.add(jLabel5, gridBagConstraints14);
            authorityPanel.add(getAuthorizationIdentity(), gridBagConstraints15);
            authorityPanel.add(getJPanel(), gridBagConstraints16);
            authorityPanel.add(jLabel6, gridBagConstraints17);
        }
        return authorityPanel;
    }


    /**
     * This method initializes name
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getGtsURI() {
        if (gtsURI == null) {
            gtsURI = new JTextField();
        }
        return gtsURI;
    }


    /**
     * This method initializes priority
     * 
     * @return javax.swing.JComboBox
     */
    private JComboBox getPriority() {
        if (priority == null) {
            priority = new JComboBox();
            syncPriorities();
        }
        return priority;
    }


    /**
     * This method initializes synchronizeTrustLevels
     * 
     * @return javax.swing.JComboBox
     */
    private JComboBox getSynchronizeTrustLevels() {
        if (synchronizeTrustLevels == null) {
            synchronizeTrustLevels = new JComboBox();
            synchronizeTrustLevels.addItem(Boolean.TRUE);
            synchronizeTrustLevels.addItem(Boolean.FALSE);
        }
        return synchronizeTrustLevels;
    }


    /**
     * This method initializes performAuthorization
     * 
     * @return javax.swing.JComboBox
     */
    private JComboBox getPerformAuthorization() {
        if (performAuthorization == null) {
            performAuthorization = new JComboBox();
            performAuthorization.addItem(Boolean.FALSE);
            performAuthorization.addItem(Boolean.TRUE);
            syncAuthorization();
            performAuthorization.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    syncAuthorization();
                }
            });

        }
        return performAuthorization;
    }


    private void syncAuthorization() {
        Boolean val = (Boolean) getPerformAuthorization().getSelectedItem();
        if (!val.booleanValue()) {
            getAuthorizationIdentity().setText("");
            getAuthorizationIdentity().setEnabled(false);
        } else {
            getAuthorizationIdentity().setEnabled(true);
        }
    }


    /**
     * This method initializes authorizationIdentity
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getAuthorizationIdentity() {
        if (authorizationIdentity == null) {
            authorizationIdentity = new JTextField();
        }
        return authorizationIdentity;
    }


    /**
     * This method initializes jPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getJPanel() {
        if (jPanel == null) {
            jLabel9 = new JLabel();
            jLabel9.setText("Seconds");
            jLabel8 = new JLabel();
            jLabel8.setText("Minutes");
            jLabel7 = new JLabel();
            jLabel7.setText("Hours");
            jPanel = new JPanel();
            jPanel.add(jLabel7, null);
            jPanel.add(getHours(), null);
            jPanel.add(jLabel8, null);
            jPanel.add(getMinutes(), null);
            jPanel.add(jLabel9, null);
            jPanel.add(getSeconds(), null);
        }
        return jPanel;
    }


    /**
     * This method initializes hours
     * 
     * @return javax.swing.JComboBox
     */
    private JComboBox getHours() {
        if (hours == null) {
            hours = new JComboBox();
            for (int i = 0; i <= 96; i++) {
                hours.addItem(new Integer(i));
            }
        }
        return hours;
    }


    /**
     * This method initializes minutes
     * 
     * @return javax.swing.JComboBox
     */
    private JComboBox getMinutes() {
        if (minutes == null) {
            minutes = new JComboBox();
            for (int i = 0; i <= 59; i++) {
                minutes.addItem(new Integer(i));
            }
        }
        return minutes;
    }


    /**
     * This method initializes seconds
     * 
     * @return javax.swing.JComboBox
     */
    private JComboBox getSeconds() {
        if (seconds == null) {
            seconds = new JComboBox();
            for (int i = 0; i <= 59; i++) {
                seconds.addItem(new Integer(i));
            }
        }
        return seconds;
    }


    /**
     * This method initializes titlePanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getTitlePanel() {
        if (titlePanel == null) {
            String str = "";
            String substr = "";
            if (update) {
                str = "Trust Fabric Authority";
                substr = authority.getServiceURI();
            } else {
                str = "Add Trust Fabric Authority";
                substr = "Add a Grid Trust Service (GTS) as a trust fabric authority.";
            }
            titlePanel = new TitlePanel(str, substr);
        }
        return titlePanel;
    }


    private void lockAll() {
        getAddButton().setEnabled(false);
        getGtsURI().setEnabled(false);
        getPriority().setEnabled(false);
        getSynchronizeTrustLevels().setEnabled(false);
        getPerformAuthorization().setEnabled(false);
        getAuthorizationIdentity().setEnabled(false);
        getHours().setEnabled(false);
        getMinutes().setEnabled(false);
        getSeconds().setEnabled(false);
    }


    private void unlockAll() {
        getAddButton().setEnabled(true);
        getGtsURI().setEnabled(true);
        if (!update) {
            getPriority().setEnabled(true);
        }
        getSynchronizeTrustLevels().setEnabled(true);
        getPerformAuthorization().setEnabled(true);
        getAuthorizationIdentity().setEnabled(true);
        getHours().setEnabled(true);
        getMinutes().setEnabled(true);
        getSeconds().setEnabled(true);
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
