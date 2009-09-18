package gov.nih.nci.cagrid.introduce.portal.modification.security;

import gov.nih.nci.cagrid.common.portal.PortalLookAndFeel;
import gov.nih.nci.cagrid.introduce.beans.extension.AuthorizationExtensionDescriptionType;
import gov.nih.nci.cagrid.introduce.beans.extension.ExtensionType;
import gov.nih.nci.cagrid.introduce.beans.extension.ExtensionsType;
import gov.nih.nci.cagrid.introduce.beans.method.MethodType;
import gov.nih.nci.cagrid.introduce.beans.security.AnonymousCommunication;
import gov.nih.nci.cagrid.introduce.beans.security.InheritServiceAuthorization;
import gov.nih.nci.cagrid.introduce.beans.security.IntroducePDPAuthorization;
import gov.nih.nci.cagrid.introduce.beans.security.MethodAuthorization;
import gov.nih.nci.cagrid.introduce.beans.security.MethodSecurity;
import gov.nih.nci.cagrid.introduce.beans.security.NoAuthorization;
import gov.nih.nci.cagrid.introduce.beans.security.ProxyCredential;
import gov.nih.nci.cagrid.introduce.beans.security.RunAsMode;
import gov.nih.nci.cagrid.introduce.beans.security.SecureConversation;
import gov.nih.nci.cagrid.introduce.beans.security.SecureMessage;
import gov.nih.nci.cagrid.introduce.beans.security.SecuritySetting;
import gov.nih.nci.cagrid.introduce.beans.security.ServiceCredential;
import gov.nih.nci.cagrid.introduce.beans.security.ServiceSecurity;
import gov.nih.nci.cagrid.introduce.beans.security.TransportLevelSecurity;
import gov.nih.nci.cagrid.introduce.beans.security.X509Credential;
import gov.nih.nci.cagrid.introduce.beans.service.ServiceType;
import gov.nih.nci.cagrid.introduce.common.CommonTools;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;
import gov.nih.nci.cagrid.introduce.extension.ExtensionsLoader;
import gov.nih.nci.cagrid.introduce.portal.extension.AbstractMethodAuthorizationPanel;
import gov.nih.nci.cagrid.introduce.portal.extension.AbstractServiceAuthorizationPanel;
import gov.nih.nci.cagrid.introduce.portal.extension.tools.ExtensionTools;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;


/**
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster </A>
 */
public class MethodSecurityPanel extends JPanel implements PanelSynchronizer {

    private final static String INHERIT_SERVICE_AUTHORIZATION = "Inherit Service Authorization";

    private final static String NO_AUTHORIZATION = "No Authorization";

    private JPanel secureCommunicationPanel = null;

    private ButtonGroup buttonGroup = new ButtonGroup();

    private JRadioButton noneButton = null;

    private JRadioButton customButton = null;

    private TransportLayerSecurityPanel tlsPanel = null;

    private JCheckBox tlsButton = null;

    private JCheckBox secureConversationButton = null;

    private JPanel choicePanel = null;

    private JPanel commPanel = null;

    private JCheckBox secureMessageButton = null;

    private SecureConversationPanel secureConversationPanel = null;

    private SecureMessagePanel secureMessagePanel = null;

    private JComboBox runAsMode = null;

    private ServiceSecurity serviceSecurity;

    private JComboBox anonymousCommunication = null;

    private JTabbedPane transportPanel = null;

    private JPanel communicationPanel = null;

    private JPanel generalSecurity = null;

    private JLabel runAsLabel = null;

    private boolean isSyncingRunAs = false;

    private boolean isInited = false;

    private JLabel anonClientsLabel = null;

    private JPanel authorizationPanel = null;

    private JPanel authorizationTypePanel = null;

    private JPanel jPanel1 = null;

    private JLabel authLabel = null;

    private JComboBox authorizationMechanism = null;

    private JPanel authPanel = null;

    private JPanel noAuthorizationPanel = null;

    private CardLayout authLayout;

    private ServiceType service;

    private MethodType method;

    private ServiceInformation info;

    private Map<String, AbstractMethodAuthorizationPanel> authPanels;


    public MethodSecurityPanel(ServiceInformation info, ServiceType service, MethodType method) {
        super();
        this.info = info;
        this.service = service;
        serviceSecurity = this.service.getServiceSecurity();
        this.method = method;
        authPanels = new HashMap<String, AbstractMethodAuthorizationPanel>();
        initialize();
        if (this.method.getMethodSecurity() != null) {
            setMethodSecurity(this.method.getMethodSecurity());
        }
        synchronize();
    }


    private void initialize() {
        GridBagConstraints gridBagConstraints17 = new GridBagConstraints();
        gridBagConstraints17.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints17.weighty = 1.0;
        gridBagConstraints17.gridx = 0;
        gridBagConstraints17.gridy = 2;
        gridBagConstraints17.insets = new java.awt.Insets(2, 2, 2, 2);
        gridBagConstraints17.weightx = 1.0;
        GridBagConstraints gridBagConstraints71 = new GridBagConstraints();
        gridBagConstraints71.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints71.weightx = 1.0D;
        this.setLayout(new GridBagLayout());
        this.add(getSecureCommunicationPanel(), gridBagConstraints71);
        setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Security Configuration",
            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION,
            null, PortalLookAndFeel.getPanelLabelColor()));
        this.add(getTransportPanel(), gridBagConstraints17);
        applyServiceSettings();
        isInited = true;
    }


    /**
     * This method initializes secureCommunicationPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getSecureCommunicationPanel() {
        if (secureCommunicationPanel == null) {
            secureCommunicationPanel = new JPanel();
            secureCommunicationPanel.add(getChoicePanel());
        }
        return secureCommunicationPanel;
    }


    /**
     * This method initializes noneButton
     * 
     * @return javax.swing.JRadioButton
     */
    private JRadioButton getNoneButton() {
        if (noneButton == null) {
            noneButton = new JRadioButton();
            noneButton.setSelected(true);
            noneButton.setText("None");
            noneButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    synchronize();
                }
            });
            buttonGroup.add(noneButton);
        }
        return noneButton;
    }


    /**
     * This method initializes customButton
     * 
     * @return javax.swing.JRadioButton
     */
    private JRadioButton getCustomButton() {
        if (customButton == null) {
            customButton = new JRadioButton();
            customButton.setText("Custom");
            customButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    synchronize();
                }
            });
            buttonGroup.add(customButton);
        }
        return customButton;
    }


    private boolean isSecure() {
        if (usesTransportSecurity() || usesSecureConversation() || usesSecureMessage()) {
            return true;
        } else {
            return false;
        }

    }


    private boolean usesTransportSecurity() {
        if (tlsButton.isSelected()) {
            return true;
        } else if ((serviceSecurity != null) && (serviceSecurity.getTransportLevelSecurity() != null)) {
            return true;
        } else {
            return false;
        }
    }


    private boolean usesSecureConversation() {
        if (secureConversationButton.isSelected()) {
            return true;
        } else if ((serviceSecurity != null) && (serviceSecurity.getSecureConversation() != null)) {
            return true;
        } else {
            return false;
        }
    }


    private boolean usesSecureMessage() {
        if (secureMessageButton.isSelected()) {
            return true;
        } else if ((serviceSecurity != null) && (serviceSecurity.getSecureMessage() != null)) {
            return true;
        } else {
            return false;
        }
    }


    public MethodSecurity getMethodSecurity(String methodName) throws Exception {
        MethodSecurity ms = new MethodSecurity();
        if (noneButton.isSelected()) {
            if (serviceSecurity == null) {
                return null;
            } else {
                ms.setSecuritySetting(SecuritySetting.None);
            }
        } else if (customButton.isSelected()) {
            ms.setSecuritySetting(SecuritySetting.Custom);
            if (!isSecure()) {
                throw new Exception("You must select at least one transport mechanism!!!");
            }

            if (tlsButton.isSelected()) {
                ms.setTransportLevelSecurity(tlsPanel.getTransportLevelSecurity());

            }
            if (secureConversationButton.isSelected()) {
                ms.setSecureConversation(secureConversationPanel.getSecureConversation());

            }
            if (secureMessageButton.isSelected()) {
                ms.setSecureMessage(secureMessagePanel.getSecureMessage());
            }

            if (runAsMode.isEnabled()) {
                ms.setRunAsMode((RunAsMode) runAsMode.getSelectedItem());
            }

            if (anonymousCommunication.isEnabled()) {
                ms.setAnonymousClients((AnonymousCommunication) anonymousCommunication.getSelectedItem());
            } else {
                ms.setAnonymousClients(AnonymousCommunication.No);
            }
            String authType = (String) authorizationMechanism.getSelectedItem();
            MethodAuthorization ma = new MethodAuthorization();
            if (authType.equals(NO_AUTHORIZATION)) {
                ma.setNoAuthorization(new NoAuthorization());
            } else if (authType.equals(INHERIT_SERVICE_AUTHORIZATION)) {
                ma.setInheritServiceAuthorization(new InheritServiceAuthorization());
            } else {
                ma.setIntroducePDPAuthorization(new IntroducePDPAuthorization());
                AuthorizationExtensionDescriptionType authExt = ExtensionsLoader.getInstance()
                    .getAuthorizationExtensionByDisplayName(authType);
                AbstractMethodAuthorizationPanel panel = authPanels.get(authExt.getDisplayName());
               
                ExtensionType extensionType = panel.getAuthorizationExtensionData();
                // need to add this extension type and replace what might have
                // been there if it was there.
                List<ExtensionType> newExtensionsList = new ArrayList<ExtensionType>();
                if (method.getExtensions() != null && method.getExtensions().getExtension() != null) {
                    for (int i = 0; i < method.getExtensions().getExtension().length; i++) {
                        ExtensionType ext = method.getExtensions().getExtension(i);
                        if (!ext.getExtensionType().equals(ExtensionsLoader.AUTHORIZATION_EXTENSION)) {
                            newExtensionsList.add(ext);
                        }
                    }
                }
                newExtensionsList.add(extensionType);
                ExtensionType[] newExtensions = new ExtensionType[newExtensionsList.size()];
                newExtensionsList.toArray(newExtensions);
                ExtensionsType extensions = new ExtensionsType();
                extensions.setExtension(newExtensions);
                method.setExtensions(extensions);

            }
            
            ms.setMethodAuthorization(ma);
            
            if(ms.getMethodAuthorization().getIntroducePDPAuthorization()==null){
                //clean out authorization extensions....
                List<ExtensionType> newExtensionsList = new ArrayList<ExtensionType>();
                if (method.getExtensions() != null && method.getExtensions().getExtension() != null) {
                    for (int i = 0; i < method.getExtensions().getExtension().length; i++) {
                        ExtensionType ext = method.getExtensions().getExtension(i);
                        if (!ext.getExtensionType().equals(ExtensionsLoader.AUTHORIZATION_EXTENSION)) {
                            newExtensionsList.add(ext);
                        }
                    }
                }
                ExtensionType[] newExtensions = new ExtensionType[newExtensionsList.size()];
                newExtensionsList.toArray(newExtensions);
                ExtensionsType extensions = new ExtensionsType();
                extensions.setExtension(newExtensions);
                method.setExtensions(extensions);
            }
            
        }
        if (CommonTools.equals(serviceSecurity, ms)) {
            return null;
        }
        return ms;
    }


    public void setMethodSecurity(MethodSecurity ms) {
        if (ms != null) {
            if (ms.getSecuritySetting().equals(SecuritySetting.None)) {
                noneButton.setSelected(true);
            } else if (ms.getSecuritySetting().equals(SecuritySetting.Custom)) {
                customButton.setSelected(true);

                TransportLevelSecurity tls = ms.getTransportLevelSecurity();
                if (tls != null) {
                    tlsButton.setSelected(true);
                    tlsPanel.setTransportLevelSecurity(tls);
                }
                SecureConversation sc = ms.getSecureConversation();
                if (sc != null) {
                    secureConversationButton.setSelected(true);
                    secureConversationPanel.setSecureConversation(sc);
                }
                SecureMessage sm = ms.getSecureMessage();
                if (sm != null) {
                    secureMessageButton.setSelected(true);
                    secureMessagePanel.setSecureMessage(sm);
                }

                RunAsMode runas = ms.getRunAsMode();
                if (runas != null) {
                    runAsMode.setSelectedItem(runas);
                }

                AnonymousCommunication anon = ms.getAnonymousClients();
                if (anon != null) {
                    anonymousCommunication.setSelectedItem(anon);
                }

                MethodAuthorization ma = ms.getMethodAuthorization();
                if (ma != null) {
                    if (ma.getNoAuthorization() != null) {
                        authorizationMechanism.setSelectedItem(NO_AUTHORIZATION);
                    } else if (ma.getIntroducePDPAuthorization() != null) {
                        // set the viewer panel the the currently selected auth
                        // extension
                        // determine which auth extension is present
                        if (method.getExtensions() != null && method.getExtensions().getExtension() != null) {
                            for (int i = 0; i < method.getExtensions().getExtension().length; i++) {
                                ExtensionType ext = method.getExtensions().getExtension(i);
                                if (ext.getExtensionType().equals(ExtensionsLoader.AUTHORIZATION_EXTENSION)) {
                                    AuthorizationExtensionDescriptionType extDesc = ExtensionsLoader.getInstance()
                                        .getAuthorizationExtension(ext.getName());
                                    getAuthorizationMechanism().setSelectedItem(extDesc.getDisplayName());
                                }
                            }
                        }
                    } else {
                        authorizationMechanism.setSelectedItem(INHERIT_SERVICE_AUTHORIZATION);
                    }
                }

            }
        }

    }


    public void synchronize() {
        disableAll();
        if (customButton.isSelected()) {
            tlsButton.setEnabled(true);
            secureConversationButton.setEnabled(true);
            secureMessageButton.setEnabled(true);

            if (tlsButton.isSelected()) {
                tlsPanel.enablePanel();
            }
            if (secureConversationButton.isSelected()) {
                secureConversationPanel.enablePanel();
            }
            if (secureMessageButton.isSelected()) {
                secureMessagePanel.enablePanel();
            }

            synchRunAsMode();
            syncAuthorization();
            syncAnonymousCommunication();
        }
    }


    private void syncAuthorization() {

        if (isSecure()) {
            if ((this.serviceSecurity != null) && (this.serviceSecurity.getServiceAuthorization() != null)
                && this.serviceSecurity.getServiceAuthorization().getCustomPDPChainAuthorization() != null) {
                authorizationMechanism.setEnabled(false);
                authLayout.show(authPanel, INHERIT_SERVICE_AUTHORIZATION);
            } else if ((this.serviceSecurity != null) && (this.serviceSecurity.getServiceAuthorization() != null)
                && this.serviceSecurity.getServiceAuthorization().getGridMapAuthorization() != null) {
                authorizationMechanism.setEnabled(false);
                authLayout.show(authPanel, INHERIT_SERVICE_AUTHORIZATION);
            } else {
                authorizationMechanism.setEnabled(true);
                String mech = (String) authorizationMechanism.getSelectedItem();
                authLayout.show(authPanel, mech);
            }
        } else {
            authorizationMechanism.setEnabled(false);
            authLayout.show(authPanel, INHERIT_SERVICE_AUTHORIZATION);
        }
    }


    private void syncAnonymousCommunication() {
        if (usesTransportSecurity() || usesSecureConversation()) {
            anonymousCommunication.setEnabled(true);
        } else {
            anonymousCommunication.setEnabled(false);
        }
    }


    private synchronized void synchRunAsMode() {
        if (!isSyncingRunAs) {
            isSyncingRunAs = true;
            runAsMode.removeAllItems();
            if (isSecure()) {
                runAsMode.setEnabled(true);
                runAsMode.addItem(RunAsMode.System);
                if (hasServiceCredentials()) {
                    runAsMode.addItem(RunAsMode.Service);
                }

                if (!getAnonymousCommunication().isEnabled()) {
                    runAsMode.addItem(RunAsMode.Caller);
                } else if (getAnonymousCommunication().getSelectedItem().equals(AnonymousCommunication.No)) {
                    runAsMode.addItem(RunAsMode.Caller);
                }

            }
            isSyncingRunAs = false;
        }

    }


    private boolean hasServiceCredentials() {
        if (serviceSecurity != null) {
            if (serviceSecurity.getServiceCredentials() != null) {
                ServiceCredential cred = serviceSecurity.getServiceCredentials();
                if (cred.getX509Credential() != null) {
                    X509Credential x509 = cred.getX509Credential();
                    if ((x509.getCertificateLocation() != null) && (x509.getPrivateKeyLocation() != null)) {
                        return true;
                    }
                } else if (cred.getProxyCredential() != null) {
                    ProxyCredential proxy = cred.getProxyCredential();
                    if (proxy.getProxyLocation() != null) {
                        return true;
                    }
                }

            }
        }
        return false;
    }


    public void applyServiceSettings() {
        if (serviceSecurity != null) {
            if (this.isSecure()) {
                customButton.setSelected(true);
                if (serviceSecurity.getTransportLevelSecurity() != null) {
                    TransportLevelSecurity s = serviceSecurity.getTransportLevelSecurity();
                    tlsButton.setSelected(true);
                    tlsPanel.setTransportLevelSecurity(s);
                }

                if (serviceSecurity.getSecureConversation() != null) {
                    SecureConversation s = serviceSecurity.getSecureConversation();
                    secureConversationButton.setSelected(true);
                    secureConversationPanel.setSecureConversation(s);
                }

                if (serviceSecurity.getSecureMessage() != null) {
                    SecureMessage s = serviceSecurity.getSecureMessage();
                    secureMessageButton.setSelected(true);
                    secureMessagePanel.setSecureMessage(s);
                }
                if (serviceSecurity.getRunAsMode() != null) {
                    runAsMode.setSelectedItem(serviceSecurity.getRunAsMode());
                }

                if (serviceSecurity.getAnonymousClients() != null) {
                    anonymousCommunication.setSelectedItem(serviceSecurity.getAnonymousClients());
                }

            } else {
                noneButton.setSelected(true);
            }
        }
    }


    public void disableAll() {
        tlsPanel.disablePanel();
        tlsButton.setEnabled(false);
        secureConversationButton.setEnabled(false);
        secureMessageButton.setEnabled(false);
        secureConversationPanel.disablePanel();
        secureMessagePanel.disablePanel();
        runAsMode.setEnabled(false);
        anonymousCommunication.setEnabled(false);
        authorizationMechanism.setEnabled(false);
        authLayout.show(authPanel, NO_AUTHORIZATION);
    }


    /**
     * This method initializes tlsPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getTlsPanel() {
        if (tlsPanel == null) {
            tlsPanel = new TransportLayerSecurityPanel();
        }
        return tlsPanel;
    }


    /**
     * This method initializes tlsButton
     * 
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getTlsButton() {
        if (tlsButton == null) {
            tlsButton = new JCheckBox();
            tlsButton.setText("Transport Layer Security");
            tlsButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    synchronize();
                }
            });
        }
        return tlsButton;
    }


    /**
     * This method initializes secureConversationButton
     * 
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getSecureConversationButton() {
        if (secureConversationButton == null) {
            secureConversationButton = new JCheckBox();
            secureConversationButton.setText("Secure Conversation");
            secureConversationButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    synchronize();
                }
            });

        }
        return secureConversationButton;
    }


    /**
     * This method initializes choicePanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getChoicePanel() {
        if (choicePanel == null) {
            choicePanel = new JPanel();
            choicePanel.setLayout(new GridBagLayout());
            choicePanel.add(getNoneButton());
            choicePanel.add(getCustomButton());
        }
        return choicePanel;
    }


    /**
     * This method initializes commPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getCommPanel() {
        if (commPanel == null) {
            commPanel = new JPanel();
            commPanel.add(getTlsButton(), null);
            commPanel.add(getSecureConversationButton(), null);
            commPanel.add(getSecureMessageButton(), null);
        }
        return commPanel;
    }


    /**
     * This method initializes secureMessageButton
     * 
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getSecureMessageButton() {
        if (secureMessageButton == null) {
            secureMessageButton = new JCheckBox();
            secureMessageButton.setText("SecureMessage");
            secureMessageButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    synchronize();
                }
            });
        }
        return secureMessageButton;
    }


    /**
     * This method initializes secureConversationPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getSecureConversationPanel() {
        if (secureConversationPanel == null) {
            secureConversationPanel = new SecureConversationPanel();
        }
        return secureConversationPanel;
    }


    /**
     * This method initializes secureMessagePanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getSecureMessagePanel() {
        if (secureMessagePanel == null) {
            secureMessagePanel = new SecureMessagePanel();
        }
        return secureMessagePanel;
    }


    /**
     * This method initializes runAsMode
     * 
     * @return javax.swing.JComboBox
     */
    private JComboBox getRunAsMode() {
        if (runAsMode == null) {
            runAsMode = new JComboBox();
            runAsMode.addItem(RunAsMode.System);
            runAsMode.addItem(RunAsMode.Service);
            runAsMode.addItem(RunAsMode.Caller);
            runAsMode.addItem(RunAsMode.Resource);
        }
        return runAsMode;
    }


    /**
     * This method initializes anonymousCommunication
     * 
     * @return javax.swing.JComboBox
     */
    private JComboBox getAnonymousCommunication() {
        if (anonymousCommunication == null) {
            anonymousCommunication = new JComboBox();
            anonymousCommunication.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (isInited) {
                        synchronize();
                    }
                }
            });
            anonymousCommunication.addItem(AnonymousCommunication.No);
            anonymousCommunication.addItem(AnonymousCommunication.Yes);
        }
        return anonymousCommunication;
    }


    /**
     * This method initializes transportPanel
     * 
     * @return javax.swing.JTabbedPane
     */
    private JTabbedPane getTransportPanel() {
        if (transportPanel == null) {
            transportPanel = new JTabbedPane();
            transportPanel.addTab("Secure Communication", null, getCommunicationPanel(), null);
            transportPanel.addTab("Authorization", null, getAuthorizationPanel(), null);
            transportPanel.addTab("Other", null, getGeneralSecurity(), null);
        }
        return transportPanel;
    }


    /**
     * This method initializes communicationPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getCommunicationPanel() {
        if (communicationPanel == null) {
            GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
            gridBagConstraints11.insets = new java.awt.Insets(5, 5, 5, 5);
            gridBagConstraints11.gridy = 3;
            gridBagConstraints11.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints11.weightx = 1.0D;
            gridBagConstraints11.weighty = 1.0D;
            gridBagConstraints11.gridx = 0;
            GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
            gridBagConstraints7.insets = new java.awt.Insets(5, 5, 5, 5);
            gridBagConstraints7.gridy = 2;
            gridBagConstraints7.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints7.weightx = 1.0D;
            gridBagConstraints7.weighty = 1.0D;
            gridBagConstraints7.gridx = 0;
            GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
            gridBagConstraints6.insets = new java.awt.Insets(5, 5, 5, 5);
            gridBagConstraints6.gridy = 1;
            gridBagConstraints6.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints6.weightx = 1.0D;
            gridBagConstraints6.weighty = 1.0D;
            gridBagConstraints6.gridx = 0;
            GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
            gridBagConstraints4.insets = new java.awt.Insets(5, 5, 5, 5);
            gridBagConstraints4.gridy = 0;
            gridBagConstraints4.weightx = 1.0D;
            gridBagConstraints4.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints4.gridx = 0;
            communicationPanel = new JPanel();
            communicationPanel.setLayout(new GridBagLayout());
            communicationPanel.add(getCommPanel(), gridBagConstraints4);
            communicationPanel.add(getTlsPanel(), gridBagConstraints6);
            communicationPanel.add(getSecureConversationPanel(), gridBagConstraints7);
            communicationPanel.add(getSecureMessagePanel(), gridBagConstraints11);
        }
        return communicationPanel;
    }


    /**
     * This method initializes generalSecurity
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getGeneralSecurity() {
        if (generalSecurity == null) {
            GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
            gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints1.gridx = 1;
            gridBagConstraints1.gridy = 0;
            gridBagConstraints1.weightx = 1.0;
            gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints1.insets = new Insets(2, 2, 2, 2);
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints.gridy = 0;
            anonClientsLabel = new JLabel();
            anonClientsLabel.setText("Client should connect anonymously?");
            anonClientsLabel.setToolTipText("Adds information to the security metadata telling the client that they should or should not use credintials when connecting.");
            GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
            gridBagConstraints12.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints12.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints12.gridx = 1;
            gridBagConstraints12.gridy = 1;
            gridBagConstraints12.weightx = 1.0D;
            gridBagConstraints12.weighty = 0.0D;
            gridBagConstraints12.fill = GridBagConstraints.HORIZONTAL;
            GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
            gridBagConstraints21.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints21.gridy = 1;
            gridBagConstraints21.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints21.gridx = 0;
            runAsLabel = new JLabel();
            runAsLabel.setText("Run As");
            generalSecurity = new JPanel();
            generalSecurity.setLayout(new GridBagLayout());
            generalSecurity.add(runAsLabel, gridBagConstraints21);
            generalSecurity.add(getRunAsMode(), gridBagConstraints12);
            generalSecurity.add(anonClientsLabel, gridBagConstraints);
            generalSecurity.add(getAnonymousCommunication(), gridBagConstraints1);
        }
        return generalSecurity;
    }


    /**
     * This method initializes authorizationPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getAuthorizationPanel() {
        if (authorizationPanel == null) {
            authorizationPanel = new JPanel();
            authorizationPanel.setLayout(new BorderLayout());
            authorizationPanel.add(getAuthorizationTypePanel(), java.awt.BorderLayout.NORTH);
            authorizationPanel.add(getAuthPanel(), java.awt.BorderLayout.CENTER);
        }
        return authorizationPanel;
    }


    /**
     * This method initializes authorizationTypePanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getAuthorizationTypePanel() {
        if (authorizationTypePanel == null) {
            authorizationTypePanel = new JPanel();
            authorizationTypePanel.setLayout(new FlowLayout());
            authorizationTypePanel.add(getJPanel1(), null);
        }
        return authorizationTypePanel;
    }


    /**
     * This method initializes jPanel1
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getJPanel1() {
        if (jPanel1 == null) {
            GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
            gridBagConstraints5.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints5.gridx = 1;
            gridBagConstraints5.gridy = 1;
            gridBagConstraints5.weightx = 1.0;
            gridBagConstraints5.insets = new Insets(2, 2, 2, 2);
            GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
            gridBagConstraints3.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints3.gridy = 1;
            gridBagConstraints3.gridx = 0;
            authLabel = new JLabel();
            authLabel.setFont(new Font("Dialog", Font.BOLD, 14));
            authLabel.setText("Authorization Mechanism");
            authLabel.setForeground(PortalLookAndFeel.getPanelLabelColor());
            jPanel1 = new JPanel();
            jPanel1.setLayout(new GridBagLayout());
            jPanel1.add(authLabel, gridBagConstraints3);
            jPanel1.add(getAuthorizationMechanism(), gridBagConstraints5);
        }
        return jPanel1;
    }


    /**
     * This method initializes authorizationMechanism
     * 
     * @return javax.swing.JComboBox
     */
    private JComboBox getAuthorizationMechanism() {
        if (authorizationMechanism == null) {
            authorizationMechanism = new JComboBox();
            authorizationMechanism.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (isInited) {
                        synchronize();
                    }
                }
            });
            authorizationMechanism.addItem(INHERIT_SERVICE_AUTHORIZATION);
            authorizationMechanism.addItem(NO_AUTHORIZATION);
            List<AuthorizationExtensionDescriptionType> authExtensions = ExtensionsLoader.getInstance()
                .getAuthorizationExtensions();
            for (Iterator iterator = authExtensions.iterator(); iterator.hasNext();) {
                AuthorizationExtensionDescriptionType authorizationExtensionDescriptionType = (AuthorizationExtensionDescriptionType) iterator
                    .next();
                authorizationMechanism.addItem(authorizationExtensionDescriptionType.getDisplayName());
            }
        }
        return authorizationMechanism;
    }


    /**
     * This method initializes authPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getAuthPanel() {
        if (authPanel == null) {
            authPanel = new JPanel();
            authLayout = new CardLayout();
            authPanel.setLayout(authLayout);
            authPanel.add(getNoAuthorizationPanel(), NO_AUTHORIZATION);
            List<AuthorizationExtensionDescriptionType> authExtension = ExtensionsLoader.getInstance()
                .getAuthorizationExtensions();
            for (Iterator iterator = authExtension.iterator(); iterator.hasNext();) {
                AuthorizationExtensionDescriptionType authorizationExtensionDescriptionType = (AuthorizationExtensionDescriptionType) iterator
                    .next();
                try {
                    AbstractMethodAuthorizationPanel newAuthPanel = ExtensionTools.getMethodAuthorizationPanel(
                        authorizationExtensionDescriptionType.getName(), info, service, method);
                    authPanel.add(newAuthPanel, authorizationExtensionDescriptionType.getDisplayName());
                    authPanels.put(authorizationExtensionDescriptionType.getDisplayName(), newAuthPanel);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
        return authPanel;
    }


    /**
     * This method initializes noAuthorizationPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getNoAuthorizationPanel() {
        if (noAuthorizationPanel == null) {
            noAuthorizationPanel = new JPanel();
            noAuthorizationPanel.setName("No Authorization");
        }
        return noAuthorizationPanel;
    }

}
