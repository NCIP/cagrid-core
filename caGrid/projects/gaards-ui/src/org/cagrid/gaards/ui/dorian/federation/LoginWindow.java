package org.cagrid.gaards.ui.dorian.federation;

import gov.nih.nci.cagrid.common.FaultUtil;
import gov.nih.nci.cagrid.common.Runner;
import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.common.security.ProxyUtil;
import gov.nih.nci.cagrid.dorian.client.IFSUserClient;
import gov.nih.nci.cagrid.opensaml.SAMLAssertion;

import java.awt.CardLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.xml.namespace.QName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.gaards.authentication.client.AuthenticationClient;
import org.cagrid.gaards.authentication.common.AuthenticationProfile;
import org.cagrid.gaards.credentials.DorianUserCredentialDescriptor;
import org.cagrid.gaards.credentials.DorianUserCredentialEntry;
import org.cagrid.gaards.dorian.client.GridUserClient;
import org.cagrid.gaards.dorian.federation.CertificateLifetime;
import org.cagrid.gaards.ui.common.CredentialManager;
import org.cagrid.gaards.ui.common.ProgressPanel;
import org.cagrid.gaards.ui.common.TitlePanel;
import org.cagrid.gaards.ui.dorian.AuthenticationServiceHandle;
import org.cagrid.gaards.ui.dorian.DorianHandle;
import org.cagrid.gaards.ui.dorian.DorianLookAndFeel;
import org.cagrid.gaards.ui.dorian.DorianServiceListComboBox;
import org.cagrid.grape.ApplicationComponent;
import org.cagrid.grape.GridApplication;
import org.cagrid.grape.LookAndFeel;
import org.cagrid.grape.utils.ErrorDialog;
import org.globus.gsi.GlobusCredential;


public class LoginWindow extends ApplicationComponent {
	private static Log log = LogFactory.getLog(LoginWindow.class);
	
    private static final long serialVersionUID = 1L;

    private JPanel jContentPane = null;

    private JPanel idpPanel = null;

    private JLabel idpLabel = null;

    private JComboBox identityProvider = null;

    private JPanel buttonPanel = null;

    private JPanel loginPanel = null;

    private JLabel ifsLabel = null;

    private DorianServiceListComboBox dorianService = null;

    private JButton authenticateButton = null;

    private JButton close = null;

    private JLabel lifetimeLabel = null;

    private JPanel lifetimePanel = null;

    private JComboBox hours = null;

    private JLabel hourLabel = null;

    private JComboBox minutes = null;

    private JLabel minutesLabel = null;

    private JComboBox seconds = null;

    private JLabel secondsLabel = null;

    private CardLayout credentialLayout = null;

    private CredentialPanel currentCredentialPanel = null;

    private Map<QName, CredentialPanel> credentialPanels;

    private JPanel titlePanel = null;

    private JTabbedPane jTabbedPane = null;

    private JPanel advancedPanel = null;

    private JLabel jLabel1 = null;

    private JCheckBox setDefault = null;

    private ProgressPanel progressPanel = null;

    private JLabel jLabel = null;

    private JPanel delegationPathLenghPanel = null;

    private JTextField delegationPathLength = null;

    private JLabel jLabel2 = null;


    /**
     * This is the default constructor
     */
    public LoginWindow() {
        super();
        this.credentialPanels = new Hashtable<QName, CredentialPanel>();
        initialize();
    }


    /**
     * This method initializes this
     */
    private void initialize() {
        this.setContentPane(getJContentPane());
        this.setFrameIcon(DorianLookAndFeel.getCertificateIcon());
        this.setTitle("Login");
    }


    /**
     * This method initializes jContentPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getJContentPane() {
        if (jContentPane == null) {
            GridBagConstraints gridBagConstraints16 = new GridBagConstraints();
            gridBagConstraints16.gridx = 0;
            gridBagConstraints16.insets = new Insets(0, 0, 0, 0);
            gridBagConstraints16.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints16.weightx = 1.0D;
            gridBagConstraints16.gridy = 3;
            GridBagConstraints gridBagConstraints15 = new GridBagConstraints();
            gridBagConstraints15.gridx = 0;
            gridBagConstraints15.ipadx = 120;
            gridBagConstraints15.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints15.weightx = 1.0D;
            gridBagConstraints15.gridy = 2;
            GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
            gridBagConstraints8.gridx = 0;
            gridBagConstraints8.ipadx = 222;
            gridBagConstraints8.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints8.weightx = 10.0D;
            gridBagConstraints8.insets = new Insets(5, 5, 5, 5);
            gridBagConstraints8.gridy = 0;
            GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
            gridBagConstraints7.fill = GridBagConstraints.BOTH;
            gridBagConstraints7.gridy = 1;
            gridBagConstraints7.weightx = 1.0;
            gridBagConstraints7.weighty = 1.0;
            gridBagConstraints7.gridx = 0;
            jContentPane = new JPanel();
            jContentPane.setLayout(new GridBagLayout());
            jContentPane.add(getJTabbedPane(), gridBagConstraints7);
            jContentPane.add(getTitlePanel(), gridBagConstraints8);
            jContentPane.add(getButtonPanel(), gridBagConstraints15);
            jContentPane.add(getProgressPanel(), gridBagConstraints16);
        }
        return jContentPane;
    }


    /**
     * This method initializes idpPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getIdpPanel() {
        if (idpPanel == null) {
            lifetimeLabel = new JLabel();
            lifetimeLabel.setText("Lifetime");
            GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
            gridBagConstraints6.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints6.gridy = 0;
            gridBagConstraints6.weightx = 1.0;
            gridBagConstraints6.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints6.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints6.gridx = 1;
            GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
            gridBagConstraints5.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints5.gridy = 0;
            gridBagConstraints5.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints5.gridx = 0;
            ifsLabel = new JLabel();
            ifsLabel.setText("Credential Provider");
            GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
            gridBagConstraints4.gridx = 0;
            gridBagConstraints4.gridwidth = 2;
            gridBagConstraints4.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints4.weightx = 1.0D;
            gridBagConstraints4.weighty = 1.0D;
            gridBagConstraints4.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints4.gridy = 2;
            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            gridBagConstraints2.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints2.gridx = 1;
            gridBagConstraints2.gridy = 1;
            gridBagConstraints2.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints2.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints2.weightx = 1.0;
            GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
            gridBagConstraints1.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints1.gridy = 1;
            gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints1.gridx = 0;
            idpLabel = new JLabel();
            idpLabel.setText("Organization");
            idpPanel = new JPanel();
            idpPanel.setLayout(new GridBagLayout());

            // idpPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
            /*
             * idpPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null
             * , "Login", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
             * javax.swing.border.TitledBorder.DEFAULT_POSITION, null,
             * LookAndFeel.getPanelLabelColor()));
             */
            idpPanel.add(idpLabel, gridBagConstraints1);
            idpPanel.add(getIdentityProvider(), gridBagConstraints2);
            idpPanel.add(getLoginPanel(), gridBagConstraints4);
            idpPanel.add(ifsLabel, gridBagConstraints5);
            idpPanel.add(getDorianService(), gridBagConstraints6);
        }
        return idpPanel;
    }


    /**
     * This method initializes identityProvider
     * 
     * @return javax.swing.JComboBox
     */
    private JComboBox getIdentityProvider() {
        if (identityProvider == null) {
            identityProvider = new JComboBox();
            identityProvider.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    showLoginInformation();
                }
            });
        }
        return identityProvider;
    }


    private void showLoginInformation() {
        AuthenticationServiceHandle handle = (AuthenticationServiceHandle) getIdentityProvider().getSelectedItem();
        if (handle != null) {
            getIdentityProvider().setToolTipText(handle.getServiceURL());
            Set<QName> profiles = handle.getAuthenticationProfiles();
            if ((profiles == null) || (profiles.size() <= 0)) {
                this.credentialLayout
                    .show(getLoginPanel(), profileToString(AuthenticationProfile.BASIC_AUTHENTICATION));
                this.currentCredentialPanel = this.credentialPanels.get(AuthenticationProfile.BASIC_AUTHENTICATION);
            } else {
                QName profile = profiles.iterator().next();
                this.credentialLayout.show(getLoginPanel(), profileToString(profile));
                this.currentCredentialPanel = this.credentialPanels.get(profile);
            }
        }
    }


    /**
     * This method initializes buttonPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getButtonPanel() {
        if (buttonPanel == null) {
            buttonPanel = new JPanel();
            buttonPanel.add(getAuthenticateButton(), null);
            buttonPanel.add(getClose(), null);
        }
        return buttonPanel;
    }


    /**
     * This method initializes loginPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getLoginPanel() {
        if (loginPanel == null) {
            loginPanel = new JPanel();
            credentialLayout = new CardLayout();
            loginPanel.setLayout(credentialLayout);
            loginPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Login Information",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION, null, LookAndFeel.getPanelLabelColor()));

            CredentialPanel panel1 = new BasicAuthenticationPanel();
            CredentialPanel panel2 = new OneTimePasswordAuthenticationPanel();
            this.credentialPanels.put(AuthenticationProfile.BASIC_AUTHENTICATION, panel1);
            this.credentialPanels.put(AuthenticationProfile.ONE_TIME_PASSWORD, panel2);

            loginPanel.add(panel1, profileToString(AuthenticationProfile.BASIC_AUTHENTICATION));
            loginPanel.add(panel2, profileToString(AuthenticationProfile.ONE_TIME_PASSWORD));
        }
        return loginPanel;
    }


    /**
     * This method initializes dorianService
     * 
     * @return javax.swing.JComboBox
     */
    private DorianServiceListComboBox getDorianService() {
        if (dorianService == null) {
            dorianService = new DorianServiceListComboBox();
            dorianService.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    populateAuthenticationServices();
                }
            });
            populateAuthenticationServices();
        }
        return dorianService;
    }


    private void populateAuthenticationServices() {
        try {
            this.getIdentityProvider().removeAllItems();
            DorianHandle handle = getDorianService().getSelectedService();
            if (handle != null) {
                List<AuthenticationServiceHandle> providers = handle.getAuthenticationServices();
                if (providers != null) {
                    for (int i = 0; i < providers.size(); i++) {
                        this.getIdentityProvider().addItem(providers.get(i));
                    }
                }
            }
        } catch (Exception e) {
            FaultUtil.logFault(log, e);
        }
    }


    /**
     * This method initializes authenticateButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getAuthenticateButton() {
        if (authenticateButton == null) {
            authenticateButton = new JButton();
            getRootPane().setDefaultButton(authenticateButton);
            authenticateButton.setText("Login");
            authenticateButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    Runner runner = new Runner() {
                        public void execute() {
                            authenticate();
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
        return authenticateButton;
    }


    private void authenticate() {
  
        // prevent clicking this button while working
        getAuthenticateButton().setEnabled(false);

        DorianHandle dorian = ((DorianServiceListComboBox) this.getDorianService()).getSelectedService();
        AuthenticationServiceHandle as = ((AuthenticationServiceHandle) getIdentityProvider().getSelectedItem());

        if (as == null) {
            ErrorDialog
                .showError("Login Error: You have not selected an Organization to authenticate with.  If the organization list is empty either the Credential Provider you selected may be down or the Credential Provider you selected does not support authentication profiles.   If the credential provider you selected does not support authentication profiles, you can manually add your organization's authentication service through the preferences menu.");
            return;
        }

        getProgressPanel().showProgress("Authenticating with identity provider...");

        try {
            AuthenticationClient client = as.getAuthenticationClient();
            SAMLAssertion saml = client.authenticate(this.currentCredentialPanel.getCredential());
            getProgressPanel().showProgress("Requesting grid credential...");

            String version = dorian.getServiceVersion();
            GlobusCredential cred = null;

            if (version.equals(GridUserClient.VERSION_1_0) || version.equals(GridUserClient.VERSION_1_1)
                || version.equals(GridUserClient.VERSION_1_2) || version.equals(GridUserClient.VERSION_UNKNOWN)) {
                IFSUserClient c2 = dorian.getOldUserClient();
                int delegationPathLength = 0;
                String str = Utils.clean(getDelegationPathLength().getText());
                if (str != null) {
                    try {
                        delegationPathLength = Integer.valueOf(str).intValue();
                    } catch (Exception e) {
                        throw new Exception(
                            "Invalid delegation path length specified, the delegation path length must be an integer.");
                    }
                }
                gov.nih.nci.cagrid.dorian.ifs.bean.ProxyLifetime lifetime = new gov.nih.nci.cagrid.dorian.ifs.bean.ProxyLifetime();
                lifetime.setHours(Integer.valueOf((String) getHours().getSelectedItem()).intValue());
                lifetime.setMinutes(Integer.valueOf((String) getMinutes().getSelectedItem()).intValue());
                lifetime.setSeconds(Integer.valueOf((String) getSeconds().getSelectedItem()).intValue());
                cred = c2.createProxy(saml, lifetime, delegationPathLength);
            } else {
                GridUserClient c2 = dorian.getUserClient();
                CertificateLifetime lifetime = new CertificateLifetime();
                lifetime.setHours(Integer.valueOf((String) getHours().getSelectedItem()).intValue());
                lifetime.setMinutes(Integer.valueOf((String) getMinutes().getSelectedItem()).intValue());
                lifetime.setSeconds(Integer.valueOf((String) getSeconds().getSelectedItem()).intValue());
                cred = c2.requestUserCertificate(saml, lifetime);
            }
            DorianUserCredentialDescriptor des = CredentialUtils.encode(dorian.getServiceURL(), as.getServiceURL(), as.getDisplayName(), saml, cred);
            DorianUserCredentialEntry entry = new DorianUserCredentialEntry(des);
            CredentialManager.getInstance().addCredential(entry);
            if (getSetDefault().isSelected()) {
                ProxyUtil.saveProxyAsDefault(cred);
            }
            getProgressPanel().stopProgress("Login Successful");
            GridApplication.getContext().addApplicationComponent(new SuccessfulLoginWindow(cred.getIdentity()), 550,
                200);

            // enable the authenticate button
            getAuthenticateButton().setEnabled(true);

            dispose();
        } catch (Throwable e) {
            getProgressPanel().stopProgress("Error");
            ErrorDialog.showError(e);
            getAuthenticateButton().setEnabled(true);
            log.error(e, e);
        }

    }


    private JButton getClose() {
        if (close == null) {
            close = new JButton();
            close.setText("Cancel");
            // close.setIcon(LookAndFeel.getCloseIcon());
            close.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    dispose();
                }
            });
        }
        return close;
    }


    /**
     * This method initializes lifetimePanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getLifetimePanel() {
        if (lifetimePanel == null) {
            GridBagConstraints gridBagConstraints14 = new GridBagConstraints();
            gridBagConstraints14.gridx = 5;
            gridBagConstraints14.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints14.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints14.gridy = 0;
            GridBagConstraints gridBagConstraints13 = new GridBagConstraints();
            gridBagConstraints13.gridx = 3;
            gridBagConstraints13.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints13.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints13.gridy = 0;
            secondsLabel = new JLabel();
            secondsLabel.setText("sec");
            GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
            gridBagConstraints12.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints12.gridx = 4;
            gridBagConstraints12.gridy = 0;
            gridBagConstraints12.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints12.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints12.weightx = 1.0;
            minutesLabel = new JLabel();
            minutesLabel.setText("min");
            GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
            gridBagConstraints11.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints11.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints11.gridx = 2;
            gridBagConstraints11.gridy = 0;
            gridBagConstraints11.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints11.weightx = 1.0;
            GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
            gridBagConstraints10.gridx = 1;
            gridBagConstraints10.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints10.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints10.gridy = 0;
            hourLabel = new JLabel();
            hourLabel.setText("hrs");
            GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
            gridBagConstraints9.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints9.gridx = 0;
            gridBagConstraints9.gridy = 0;
            gridBagConstraints9.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints9.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints9.weightx = 1.0;
            lifetimePanel = new JPanel();
            lifetimePanel.setLayout(new GridBagLayout());
            lifetimePanel.add(getHours(), gridBagConstraints9);
            lifetimePanel.add(hourLabel, gridBagConstraints10);
            lifetimePanel.add(getMinutes(), gridBagConstraints11);
            lifetimePanel.add(minutesLabel, gridBagConstraints13);
            lifetimePanel.add(getSeconds(), gridBagConstraints12);
            lifetimePanel.add(secondsLabel, gridBagConstraints14);
        }
        return lifetimePanel;
    }


    /**
     * This method initializes hours
     * 
     * @return javax.swing.JComboBox
     */
    private JComboBox getHours() {
        if (hours == null) {
            hours = new JComboBox();
            for (int i = 0; i < 24; i++) {
                hours.addItem(String.valueOf(i));
            }
            hours.setSelectedItem("12");
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
            for (int i = 0; i < 60; i++) {
                minutes.addItem(String.valueOf(i));
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
            for (int i = 0; i < 60; i++) {
                seconds.addItem(String.valueOf(i));
            }
        }
        return seconds;
    }


    private String profileToString(QName profile) {
        return profile.getNamespaceURI() + ":" + profile.getLocalPart();
    }


    /**
     * This method initializes titlePanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getTitlePanel() {
        if (titlePanel == null) {
            titlePanel = new TitlePanel("Login",
                "Obtain a grid credential required for authenticating with grid services.");
        }
        return titlePanel;
    }


    /**
     * This method initializes jTabbedPane
     * 
     * @return javax.swing.JTabbedPane
     */
    private JTabbedPane getJTabbedPane() {
        if (jTabbedPane == null) {
            jTabbedPane = new JTabbedPane();
            jTabbedPane.setTabPlacement(JTabbedPane.TOP);
            jTabbedPane.addTab("Login", getIdpPanel());
            jTabbedPane.addTab("Advanced", getAdvancedPanel());
        }
        return jTabbedPane;
    }


    /**
     * This method initializes advancedPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getAdvancedPanel() {
        if (advancedPanel == null) {
            GridBagConstraints gridBagConstraints41 = new GridBagConstraints();
            gridBagConstraints41.gridx = 0;
            gridBagConstraints41.insets = new Insets(5, 5, 5, 5);
            gridBagConstraints41.gridwidth = 2;
            gridBagConstraints41.gridy = 3;
            jLabel2 = new JLabel();
            jLabel2.setText("** Delegation path length used only with earlier versions of Dorian (1.0 - 1.2)");
            jLabel2.setFont(new Font("Lucida Grande", Font.ITALIC, 10));
            GridBagConstraints gridBagConstraints31 = new GridBagConstraints();
            gridBagConstraints31.gridx = 1;
            gridBagConstraints31.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints31.anchor = GridBagConstraints.WEST;
            gridBagConstraints31.weightx = 1.0D;
            gridBagConstraints31.gridy = 1;
            GridBagConstraints gridBagConstraints22 = new GridBagConstraints();
            gridBagConstraints22.gridx = 0;
            gridBagConstraints22.anchor = GridBagConstraints.WEST;
            gridBagConstraints22.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints22.gridy = 1;
            jLabel = new JLabel();
            jLabel.setText("Delegation Path Length**");
            GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
            gridBagConstraints21.gridx = 1;
            gridBagConstraints21.anchor = GridBagConstraints.WEST;
            gridBagConstraints21.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints21.gridy = 2;
            GridBagConstraints gridBagConstraints110 = new GridBagConstraints();
            gridBagConstraints110.gridx = 0;
            gridBagConstraints110.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints110.anchor = GridBagConstraints.WEST;
            gridBagConstraints110.gridy = 2;
            jLabel1 = new JLabel();
            jLabel1.setText("Use as Default");
            GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
            gridBagConstraints3.anchor = GridBagConstraints.WEST;
            gridBagConstraints3.gridx = 0;
            gridBagConstraints3.gridy = 0;
            gridBagConstraints3.insets = new Insets(2, 2, 2, 2);
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.anchor = GridBagConstraints.WEST;
            gridBagConstraints.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.weightx = 1.0D;
            gridBagConstraints.weighty = 0.0D;
            gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
            advancedPanel = new JPanel();
            advancedPanel.setLayout(new GridBagLayout());
            advancedPanel.add(getLifetimePanel(), gridBagConstraints);
            advancedPanel.add(lifetimeLabel, gridBagConstraints3);
            advancedPanel.add(jLabel1, gridBagConstraints110);
            advancedPanel.add(getSetDefault(), gridBagConstraints21);
            advancedPanel.add(jLabel, gridBagConstraints22);
            advancedPanel.add(getDelegationPathLenghPanel(), gridBagConstraints31);
            advancedPanel.add(jLabel2, gridBagConstraints41);
        }
        return advancedPanel;
    }


    /**
     * This method initializes setDefault
     * 
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getSetDefault() {
        if (setDefault == null) {
            setDefault = new JCheckBox();
            setDefault.setSelected(true);
        }
        return setDefault;
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
     * This method initializes delegationPathLenghPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getDelegationPathLenghPanel() {
        if (delegationPathLenghPanel == null) {
            GridBagConstraints gridBagConstraints17 = new GridBagConstraints();
            gridBagConstraints17.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints17.gridx = 0;
            gridBagConstraints17.gridy = 0;
            gridBagConstraints17.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints17.anchor = GridBagConstraints.WEST;
            gridBagConstraints17.weightx = 1.0;
            delegationPathLenghPanel = new JPanel();
            delegationPathLenghPanel.setLayout(new GridBagLayout());
            delegationPathLenghPanel.add(getDelegationPathLength(), gridBagConstraints17);
        }
        return delegationPathLenghPanel;
    }


    /**
     * This method initializes delegationPathLength
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getDelegationPathLength() {
        if (delegationPathLength == null) {
            delegationPathLength = new JTextField();
        }
        return delegationPathLength;
    }

}
