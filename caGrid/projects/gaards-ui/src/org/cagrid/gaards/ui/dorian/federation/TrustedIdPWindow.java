/**
*============================================================================
*  Copyright The Ohio State University Research Foundation, The University of Chicago - 
*	Argonne National Laboratory, Emory University, SemanticBits LLC, and 
*	Ekagra Software Technologies Ltd.
*
*  Distributed under the OSI-approved BSD 3-Clause License.
*  See http://ncip.github.com/cagrid-core/LICENSE.txt for details.
*============================================================================
**/
package org.cagrid.gaards.ui.dorian.federation;

import gov.nih.nci.cagrid.common.FaultHelper;
import gov.nih.nci.cagrid.common.FaultUtil;
import gov.nih.nci.cagrid.common.Runner;
import gov.nih.nci.cagrid.common.Utils;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.gaards.authentication.client.AuthenticationServiceClient;
import org.cagrid.gaards.dorian.client.GridAdministrationClient;
import org.cagrid.gaards.dorian.common.SAMLConstants;
import org.cagrid.gaards.dorian.federation.GridUserPolicy;
import org.cagrid.gaards.dorian.federation.SAMLAttributeDescriptor;
import org.cagrid.gaards.dorian.federation.SAMLAuthenticationMethod;
import org.cagrid.gaards.dorian.federation.TrustedIdP;
import org.cagrid.gaards.dorian.stubs.types.PermissionDeniedFault;
import org.cagrid.gaards.pki.CertUtil;
import org.cagrid.gaards.ui.common.CertificatePanel;
import org.cagrid.gaards.ui.common.ProgressPanel;
import org.cagrid.gaards.ui.common.TitlePanel;
import org.cagrid.gaards.ui.dorian.DorianLookAndFeel;
import org.cagrid.gaards.ui.dorian.DorianSession;
import org.cagrid.gaards.ui.dorian.DorianSessionProvider;
import org.cagrid.grape.ApplicationComponent;
import org.cagrid.grape.GridApplication;
import org.cagrid.grape.LookAndFeel;
import org.cagrid.grape.utils.ErrorDialog;
import org.globus.gsi.GlobusCredential;
import org.oasis.wsrf.faults.BaseFaultType;


/**
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Langella </A>
 */
public class TrustedIdPWindow extends ApplicationComponent implements DorianSessionProvider {
	private static Log log = LogFactory.getLog(TrustedIdPWindow.class);
	
    private static final long serialVersionUID = 1L;

    public static final String PUBLISH_YES = "Yes";
    public static final String PUBLISH_NO = "No";
    public static final String PASSWORD = SAMLAuthenticationMethod.value1.getValue();
    public static final String KERBEROS = SAMLAuthenticationMethod.value2.getValue();
    public static final String SRP = SAMLAuthenticationMethod.value3.getValue();
    public static final String HARDWARE_TOKEN = SAMLAuthenticationMethod.value4.getValue();
    public static final String TLS = SAMLAuthenticationMethod.value5.getValue();
    public static final String PKI = SAMLAuthenticationMethod.value6.getValue();
    public static final String PGP = SAMLAuthenticationMethod.value7.getValue();
    public static final String SPKI = SAMLAuthenticationMethod.value8.getValue();
    public static final String XKMS = SAMLAuthenticationMethod.value9.getValue();
    public static final String XML_SIGNATURE = SAMLAuthenticationMethod.value10.getValue();
    public static final String UNSPECIFIED = SAMLAuthenticationMethod.value11.getValue();

    private final static String INFO_PANEL = "General";
    private final static String AUTHENTICATION_SERVICE = "Authentication Service";
    private final static String CERTIFICATE_PANEL = "Certificate";
    private final static String ATTRIBUTES_PANEL = "Attributes";
    private final static String AUDIT_PANEL = "Audit";
    private final static String LOCKOUT_PANEL = "Lockouts";

    private JPanel mainPanel = null;
    private JPanel buttonPanel = null;
    private JButton updateTrustedIdP = null;
    private JTabbedPane jTabbedPane = null;
    private JPanel infoPanel = null;
    private TrustedIdP idp = null;
    private JPanel certificatePanel = null;
    private CertificatePanel credPanel = null;
    private boolean newTrustedIdP;
    private JLabel idLabel = null;
    private JTextField idpId = null;
    private JLabel nameLabel = null;
    private JTextField idpName = null;
    private JLabel statusLabel = null;
    private TrustedIdPStatusComboBox status = null;
    private List<GridUserPolicy> policies = null;
    private JLabel policyLabel = null;
    private JComboBox userPolicy = null;
    private JPanel authPanel = null;
    private JLabel passwordLabel = null;
    private JCheckBox passwordMethod = null;
    private JCheckBox kerberosMethod = null;
    private JLabel kerberosLabel = null;
    private JCheckBox srpMethod = null;
    private JLabel srpLabel = null;
    private JCheckBox hardwareTokenMethod = null;
    private JLabel tokenLabel = null;
    private JCheckBox tlsMethod = null;
    private JLabel tlsLabel = null;
    private JCheckBox pkiMethod = null;
    private JLabel pkiLabel = null;
    private JCheckBox pgpMethod = null;
    private JLabel pgpLabel = null;
    private JCheckBox spkiMethod = null;
    private JLabel spkiLabel = null;
    private JCheckBox xkmsMethod = null;
    private JLabel xkmsLabel = null;
    private JCheckBox xmlSignatureMethod = null;
    private JLabel xmlSignatureLabel = null;
    private JCheckBox unspecifiedMethod = null;
    private JLabel unspecifiedLabel = null;
    private TrustedIdPsWindow trustedIdPsWindow = null;
    private JPanel attributesPanel = null;
    private JLabel userIdNamespaceLabel = null;
    private JTextField userIdNamespace = null;
    private JLabel userIdAttributeLabel = null;
    private JTextField userIdName = null;
    private JLabel firstNameAttributeNamespaceLabel = null;
    private JTextField firstNameNamespace = null;
    private JLabel firstNameAttributeLabel = null;
    private JTextField firstName = null;
    private JLabel lastNameAttributeNamespaceLabel = null;
    private JLabel lastNameAttributeLabel = null;
    private JLabel emailAttributeNamespaceLabel = null;
    private JLabel emailAttributeLabel = null;
    private JTextField lastNameNamespace = null;
    private JTextField lastName = null;
    private JTextField emailNamespace = null;
    private JTextField email = null;
    private JPanel authenticationServicePanel = null;
    private JLabel displayNameLabel = null;
    private JTextField displayName = null;
    private JLabel authServiceUrlLabel = null;
    private JTextField authenticationServiceURL = null;
    private JLabel authServiceIdentityLabel = null;
    private JTextField authenticationServiceIdentity = null;
    private DorianSession session = null;
    private JPanel titlePanel = null;
    private String titleStr = null;
    private String subtitleStr = null;
    private FederationAuditPanel auditPanel = null;
    private JPanel lockoutsWrapperPanel = null;
    private IdPLockoutsPanel lockoutsPanel = null;
    private ProgressPanel progressPanel = null;
    private JLabel publishLabel = null;
    private JComboBox publish = null;
    private JButton loadLockoutsButton;
    

    /**
     * @wbp.parser.constructor
     */
    public TrustedIdPWindow(DorianSession session, TrustedIdPsWindow window, List<GridUserPolicy> policies) throws Exception {
        super();
        this.trustedIdPsWindow = window;
        this.session = session;
        this.idp = new TrustedIdP();
        this.newTrustedIdP = true;
        this.titleStr = "Add Identity Provider";
        this.subtitleStr = this.session.getHandle().getServiceURL();
        this.policies = policies;
        initialize();
    }


    /**
     * This is the default constructor
     * 
     */
    public TrustedIdPWindow(DorianSession session, TrustedIdP idp, List<GridUserPolicy> policies) throws Exception {
        super();
        this.session = session;
        this.idp = idp;
        if (this.idp.getDisplayName() != null) {
            this.titleStr = this.idp.getDisplayName();
        } else {
            this.titleStr = this.idp.getName();
        }
        this.subtitleStr = idp.getAuthenticationServiceURL();
        this.newTrustedIdP = false;
        this.policies = policies;
        initialize();
    }


    public DorianSession getSession() {
        return this.session;
    }


    public class UserPolicyCaddy {
        private GridUserPolicy policy;


        public UserPolicyCaddy(String className) {
            this.policy = new GridUserPolicy(className, "");
        }


        public UserPolicyCaddy(GridUserPolicy policy) {
            this.policy = policy;
        }


        public GridUserPolicy getPolicy() {
            return policy;
        }


        public String toString() {
            return policy.getName();
        }


        public boolean equals(Object o) {
            UserPolicyCaddy up = (UserPolicyCaddy) o;
            if (this.getPolicy().getClassName().equals(up.getPolicy().getClassName())) {
                return true;
            } else {
                return false;
            }
        }
        
        
        public int hashCode() {
            return getPolicy().hashCode();
        }
    }


    /**
     * This method initializes this
     * @throws Exception 
     */
    private void initialize() throws Exception {
        this.setContentPane(getMainPanel());
        if (this.newTrustedIdP) {
            this.setTitle("Add Trusted IdP");
        } else {
            this.setTitle(this.titleStr);
        }
        this.setFrameIcon(DorianLookAndFeel.getTrustedIdPIcon());
        this.setSize(600, 400);
    }


    /**
     * This method initializes jPanel
     * 
     * @return javax.swing.JPanel
     * @throws Exception 
     */
    private JPanel getMainPanel() throws Exception {
        if (mainPanel == null) {
            GridBagConstraints gridBagConstraints27 = new GridBagConstraints();
            gridBagConstraints27.gridx = 0;
            gridBagConstraints27.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints27.weightx = 1.0D;
            gridBagConstraints27.weighty = 0.0D;
            gridBagConstraints27.anchor = GridBagConstraints.SOUTH;
            gridBagConstraints27.gridy = 3;
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.weightx = 1.0D;
            gridBagConstraints.insets = new Insets(5, 5, 5, 5);
            gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints.gridy = 0;
            GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
            gridBagConstraints4.fill = GridBagConstraints.BOTH;
            gridBagConstraints4.gridy = 1;
            gridBagConstraints4.weightx = 1.0;
            gridBagConstraints4.weighty = 1.0D;
            gridBagConstraints4.gridx = 0;
            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            mainPanel = new JPanel();
            mainPanel.setLayout(new GridBagLayout());
            gridBagConstraints2.gridx = 0;
            gridBagConstraints2.weightx = 1.0D;
            gridBagConstraints2.gridy = 2;
            gridBagConstraints2.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints2.anchor = java.awt.GridBagConstraints.SOUTH;
            gridBagConstraints2.fill = java.awt.GridBagConstraints.HORIZONTAL;
            mainPanel.add(getJTabbedPane(), gridBagConstraints4);
            mainPanel.add(getButtonPanel(), gridBagConstraints2);
            mainPanel.add(getTitlePanel(), gridBagConstraints);
            mainPanel.add(getProgressPanel(), gridBagConstraints27);
        }
        return mainPanel;
    }


    /**
     * This method initializes jPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getButtonPanel() {
        if (buttonPanel == null) {
            buttonPanel = new JPanel();
            buttonPanel.add(getUpdateTrustedIdP(), null);
        }
        return buttonPanel;
    }


    /**
     * This method initializes manageUser
     * 
     * @return javax.swing.JButton
     */
    private JButton getUpdateTrustedIdP() {
        if (updateTrustedIdP == null) {
            updateTrustedIdP = new JButton();

            if (this.newTrustedIdP) {
                updateTrustedIdP.setText("Add");
            } else {
                updateTrustedIdP.setText("Update");
            }

            updateTrustedIdP.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    getUpdateTrustedIdP().setEnabled(false);
                    Runner runner = new Runner() {
                        public void execute() {
                            updateTrustedIdP();
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
        return updateTrustedIdP;
    }


    private void updateTrustedIdP() {
        if (newTrustedIdP) {
            getProgressPanel().showProgress("Adding IdP...");
        } else {
            getProgressPanel().showProgress("Updating IdP...");
        }

        try {
            if (getCredPanel().getCertificate() != null) {
                idp.setIdPCertificate(CertUtil.writeCertificate(getCredPanel().getCertificate()));
            }
            idp.setName(getIdPName().getText().trim());
            idp.setDisplayName(Utils.clean(getDisplayName().getText()));
            idp.setAuthenticationServiceURL(getAuthenticationServiceURL().getText());
            idp.setAuthenticationServiceIdentity(getAuthenticationServiceIdentity().getText());
            idp.setStatus(getStatus().getSelectedStatus());
            idp.setUserPolicyClass(((UserPolicyCaddy) getUserPolicy().getSelectedItem()).getPolicy().getClassName());

            List<SAMLAuthenticationMethod> authMethod = new ArrayList<SAMLAuthenticationMethod>();
            if (getPasswordMethod().isSelected()) {
                authMethod.add(SAMLAuthenticationMethod.fromValue(PASSWORD));
            }

            if (getKerberosMethod().isSelected()) {
                authMethod.add(SAMLAuthenticationMethod.fromValue(KERBEROS));
            }

            if (getSrpMethod().isSelected()) {
                authMethod.add(SAMLAuthenticationMethod.fromValue(SRP));
            }

            if (getHardwareTokenMethod().isSelected()) {
                authMethod.add(SAMLAuthenticationMethod.fromValue(HARDWARE_TOKEN));
            }

            if (getTlsMethod().isSelected()) {
                authMethod.add(SAMLAuthenticationMethod.fromValue(TLS));
            }
            if (getPkiMethod().isSelected()) {
                authMethod.add(SAMLAuthenticationMethod.fromValue(PKI));
            }
            if (getPgpMethod().isSelected()) {
                authMethod.add(SAMLAuthenticationMethod.fromValue(PGP));
            }
            if (getSpkiMethod().isSelected()) {
                authMethod.add(SAMLAuthenticationMethod.fromValue(SPKI));
            }

            if (getXkmsMethod().isSelected()) {
                authMethod.add(SAMLAuthenticationMethod.fromValue(XKMS));
            }

            if (getXmlSignatureMethod().isSelected()) {
                authMethod.add(SAMLAuthenticationMethod.fromValue(XML_SIGNATURE));
            }

            if (getUnspecifiedMethod().isSelected()) {
                authMethod.add(SAMLAuthenticationMethod.fromValue(UNSPECIFIED));
            }
            SAMLAuthenticationMethod[] saml = new SAMLAuthenticationMethod[authMethod.size()];
            for (int i = 0; i < authMethod.size(); i++) {
                saml[i] = authMethod.get(i);
            }

            idp.setAuthenticationMethod(saml);

            SAMLAttributeDescriptor uidDes = new SAMLAttributeDescriptor();
            uidDes.setNamespaceURI(Utils.clean(this.getUserIdNamespace().getText()));
            uidDes.setName(Utils.clean(this.getUserIdName().getText()));
            idp.setUserIdAttributeDescriptor(uidDes);

            SAMLAttributeDescriptor firstNameDes = new SAMLAttributeDescriptor();
            firstNameDes.setNamespaceURI(Utils.clean(this.getFirstNameNamespace().getText()));
            firstNameDes.setName(Utils.clean(this.getFirstName().getText()));
            idp.setFirstNameAttributeDescriptor(firstNameDes);

            SAMLAttributeDescriptor lastNameDes = new SAMLAttributeDescriptor();
            lastNameDes.setNamespaceURI(Utils.clean(this.getLastNameNamespace().getText()));
            lastNameDes.setName(Utils.clean(this.getLastName().getText()));
            idp.setLastNameAttributeDescriptor(lastNameDes);

            SAMLAttributeDescriptor emailDes = new SAMLAttributeDescriptor();
            emailDes.setNamespaceURI(Utils.clean(this.getEmailNamespace().getText()));
            emailDes.setName(Utils.clean(this.getEmail().getText()));
            idp.setEmailAttributeDescriptor(emailDes);

            GridAdministrationClient client = this.session.getAdminClient();
            if (newTrustedIdP) {
            	TrustedIdP returnedIdP = client.addTrustedIdP(idp);
                trustedIdPsWindow.addTrustedIdP(returnedIdP);
				if (doesDorianSupportPublish()) {
					if (getPublish().getSelectedItem().equals(PUBLISH_YES)) {
						client.setPublish(returnedIdP, true);
					} else {
						client.setPublish(returnedIdP, false);
					}
				}
                getProgressPanel().stopProgress("Successfully added IdP.");
                dispose();
            } else {
                client.updateTrustedIdP(idp);
				if (doesDorianSupportPublish()) {
					if (getPublish().getSelectedItem().equals(PUBLISH_YES)) {
						client.setPublish(idp, true);
					} else {
						client.setPublish(idp, false);
					}
				}
                getProgressPanel().stopProgress("Successfully updated IdP.");
            }            
        } catch (PermissionDeniedFault pdf) {
            FaultUtil.logFault(log, pdf);
            getProgressPanel().stopProgress("Error");
            ErrorDialog.showError(pdf);
        } catch (Exception e) {
            FaultUtil.logFault(log, e);
            getProgressPanel().stopProgress("Error");
            ErrorDialog.showError(e);
        } finally {
            getUpdateTrustedIdP().setEnabled(true);
        }
    }


    /**
     * This method initializes jTabbedPane
     * 
     * @return javax.swing.JTabbedPane
     * @throws Exception 
     */
    private JTabbedPane getJTabbedPane() throws Exception {
        if (jTabbedPane == null) {
            jTabbedPane = new JTabbedPane();
            jTabbedPane.addTab(INFO_PANEL, null, getInfoPanel());
            jTabbedPane.addTab(AUTHENTICATION_SERVICE, null, getAuthenticationServicePanel(), null);
            jTabbedPane.addTab(CERTIFICATE_PANEL, null, getCertificatePanel(), null);
            jTabbedPane.addTab(ATTRIBUTES_PANEL, null, getAttributesPanel(), null);

            // can only audit an existing IdP
            if (!this.newTrustedIdP) {
                jTabbedPane.addTab(AUDIT_PANEL, null, getAuditPanel(), null);
            }
            // can only see lockouts from an existing IdP
            if (!this.newTrustedIdP) {
                jTabbedPane.addTab(LOCKOUT_PANEL, null, getLockoutsWrapperPanel(), null);
            }
        }
        return jTabbedPane;
    }


    /**
     * This method initializes infoPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getInfoPanel() {
        if (infoPanel == null) {
            infoPanel = new JPanel();
            GridBagConstraints gridBagConstraints58 = new GridBagConstraints();
            gridBagConstraints58.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints58.gridy = 2;
            gridBagConstraints58.weightx = 1.0;
            gridBagConstraints58.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints58.anchor = GridBagConstraints.WEST;
            gridBagConstraints58.gridx = 1;
            GridBagConstraints gbc_displayNameLabel = new GridBagConstraints();
            gbc_displayNameLabel.gridx = 0;
            gbc_displayNameLabel.insets = new Insets(2, 2, 2, 2);
            gbc_displayNameLabel.anchor = GridBagConstraints.WEST;
            gbc_displayNameLabel.gridy = 2;
            displayNameLabel = new JLabel();
            displayNameLabel.setText("Display Name");
            GridBagConstraints gridBagConstraints13 = new GridBagConstraints();
            gridBagConstraints13.gridx = 0;
            gridBagConstraints13.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints13.gridwidth = 2;
            gridBagConstraints13.weightx = 1.0D;
            gridBagConstraints13.weighty = 1.0D;
            gridBagConstraints13.insets = new java.awt.Insets(5, 5, 5, 5);
            gridBagConstraints13.gridy = 5;
            GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
            gridBagConstraints12.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints12.gridy = 4;
            gridBagConstraints12.weightx = 1.0;
            gridBagConstraints12.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints12.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints12.gridx = 1;
            GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
            gridBagConstraints11.gridx = 0;
            gridBagConstraints11.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints11.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints11.gridy = 4;
            policyLabel = new JLabel();
            policyLabel.setText("User Policy");
            GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
            gridBagConstraints10.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints10.gridy = 3;
            gridBagConstraints10.weightx = 1.0;
            gridBagConstraints10.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints10.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints10.gridx = 1;
            GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
            gridBagConstraints9.gridx = 0;
            gridBagConstraints9.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints9.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints9.gridy = 3;
            statusLabel = new JLabel();
            statusLabel.setText("Status");
            GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
            gridBagConstraints8.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints8.gridy = 1;
            gridBagConstraints8.weightx = 1.0;
            gridBagConstraints8.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints8.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints8.gridx = 1;
            GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
            gridBagConstraints7.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints7.gridy = 1;
            gridBagConstraints7.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints7.gridx = 0;
            nameLabel = new JLabel();
            nameLabel.setText("Name");
            nameLabel.setName("Name");
            GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
            gridBagConstraints6.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints6.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints6.gridx = 1;
            gridBagConstraints6.gridy = 0;
            gridBagConstraints6.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints6.weightx = 1.0;
            GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
            gridBagConstraints5.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints5.gridy = 0;
            gridBagConstraints5.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints5.gridx = 0;
            idLabel = new JLabel();
            idLabel.setText("IdP Id");
            infoPanel.setLayout(new GridBagLayout());
            infoPanel.setName(INFO_PANEL);
            infoPanel.add(getStatus(), gridBagConstraints10);
            infoPanel.add(statusLabel, gridBagConstraints9);
            infoPanel.add(idLabel, gridBagConstraints5);
            infoPanel.add(getIdpId(), gridBagConstraints6);
            infoPanel.add(nameLabel, gridBagConstraints7);
            infoPanel.add(getIdPName(), gridBagConstraints8);
            infoPanel.add(policyLabel, gridBagConstraints11);
            infoPanel.add(getUserPolicy(), gridBagConstraints12);
            infoPanel.add(getAuthPanel(), gridBagConstraints13);
            infoPanel.add(displayNameLabel, gbc_displayNameLabel);
            infoPanel.add(getDisplayName(), gridBagConstraints58);
        }
        return infoPanel;
    }


    /**
     * This method initializes certificatePanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getCertificatePanel() {
        if (certificatePanel == null) {
            certificatePanel = new JPanel();
            GridBagConstraints gridBagConstraints40 = new GridBagConstraints();
            gridBagConstraints40.gridx = 0;
            gridBagConstraints40.ipadx = 208;
            gridBagConstraints40.weightx = 1.0D;
            gridBagConstraints40.weighty = 1.0D;
            gridBagConstraints40.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints40.anchor = java.awt.GridBagConstraints.NORTH;
            gridBagConstraints40.gridy = 0;
            
            certificatePanel.setLayout(new GridBagLayout());
            certificatePanel.add(getCredPanel(), gridBagConstraints40);
        }
        return certificatePanel;
    }


    /**
     * This method initializes jPanel
     * 
     * @return javax.swing.JPanel
     */
    private CertificatePanel getCredPanel() {
        if (credPanel == null) {
            try {
                credPanel = new CertificatePanel();
                if (idp.getIdPCertificate() != null) {
                    credPanel.setCertificate(CertUtil.loadCertificate(idp.getIdPCertificate()));
                }
            } catch (Exception e) {
                FaultUtil.logFault(log, e);
            }
        }
        return credPanel;
    }


    /**
     * This method initializes idpId
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getIdpId() {
        if (idpId == null) {
            idpId = new JTextField();
            idpId.setEditable(false);
            if (!newTrustedIdP) {
                idpId.setText(String.valueOf(idp.getId()));
            }
        }
        return idpId;
    }


    /**
     * This method initializes idPName
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getIdPName() {
        if (idpName == null) {
            idpName = new JTextField();
            if (!newTrustedIdP) {
                idpName.setText(idp.getName());
                if (!this.newTrustedIdP) {
                    idpName.setEditable(false);
                }
            }
        }
        return idpName;
    }


    /**
     * This method initializes status
     * 
     * @return javax.swing.JComboBox
     */
    private TrustedIdPStatusComboBox getStatus() {
        if (status == null) {
            status = new TrustedIdPStatusComboBox();
            if (!newTrustedIdP) {
                status.setSelectedItem(idp.getStatus());
            }
        }
        return status;
    }


    /**
     * This method initializes userPolicy
     * 
     * @return javax.swing.JComboBox
     */
    private JComboBox getUserPolicy() {
        if (userPolicy == null) {
            userPolicy = new JComboBox();
            for (int i = 0; i < policies.size(); i++) {
                userPolicy.addItem(new UserPolicyCaddy(policies.get(i)));

                if (!newTrustedIdP) {

                    if (idp.getUserPolicyClass().equals(policies.get(i).getClassName())) {
                        int count = userPolicy.getItemCount();
                        userPolicy.setSelectedIndex((count - 1));
                    }
                }
            }

        }
        return userPolicy;
    }


    /**
     * This method initializes authPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getAuthPanel() {
        if (authPanel == null) {
            GridBagConstraints gridBagConstraints39 = new GridBagConstraints();
            gridBagConstraints39.gridx = 1;
            gridBagConstraints39.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints39.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints39.gridy = 5;
            unspecifiedLabel = new JLabel();
            unspecifiedLabel.setText("Unspecified");
            GridBagConstraints gridBagConstraints38 = new GridBagConstraints();
            gridBagConstraints38.gridx = 0;
            gridBagConstraints38.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints38.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints38.gridy = 5;
            GridBagConstraints gridBagConstraints37 = new GridBagConstraints();
            gridBagConstraints37.gridx = 3;
            gridBagConstraints37.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints37.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints37.gridy = 4;
            xmlSignatureLabel = new JLabel();
            xmlSignatureLabel.setText("XML Digital Signature");
            GridBagConstraints gridBagConstraints35 = new GridBagConstraints();
            gridBagConstraints35.gridx = 2;
            gridBagConstraints35.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints35.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints35.gridy = 4;
            GridBagConstraints gridBagConstraints34 = new GridBagConstraints();
            gridBagConstraints34.gridx = 1;
            gridBagConstraints34.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints34.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints34.gridy = 4;
            xkmsLabel = new JLabel();
            xkmsLabel.setText("XML Key Management Specification (XKMS)");
            GridBagConstraints gridBagConstraints33 = new GridBagConstraints();
            gridBagConstraints33.gridx = 0;
            gridBagConstraints33.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints33.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints33.gridy = 4;
            GridBagConstraints gridBagConstraints32 = new GridBagConstraints();
            gridBagConstraints32.gridx = 3;
            gridBagConstraints32.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints32.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints32.gridy = 3;
            spkiLabel = new JLabel();
            spkiLabel.setText("Simple Public Key Infrastructure (SPKI)");
            spkiLabel.setName("");
            GridBagConstraints gridBagConstraints30 = new GridBagConstraints();
            gridBagConstraints30.gridx = 2;
            gridBagConstraints30.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints30.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints30.gridy = 3;
            GridBagConstraints gridBagConstraints29 = new GridBagConstraints();
            gridBagConstraints29.gridx = 1;
            gridBagConstraints29.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints29.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints29.gridy = 3;
            pgpLabel = new JLabel();
            pgpLabel.setText("Pretty Good Privacy (PGP)");
            GridBagConstraints gridBagConstraints26 = new GridBagConstraints();
            gridBagConstraints26.gridx = 0;
            gridBagConstraints26.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints26.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints26.gridy = 3;
            GridBagConstraints gridBagConstraints25 = new GridBagConstraints();
            gridBagConstraints25.gridx = 3;
            gridBagConstraints25.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints25.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints25.gridy = 2;
            pkiLabel = new JLabel();
            pkiLabel.setText("X509 Public Key Infrastructure (PKI)");
            GridBagConstraints gridBagConstraints24 = new GridBagConstraints();
            gridBagConstraints24.gridx = 2;
            gridBagConstraints24.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints24.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints24.gridy = 2;
            GridBagConstraints gridBagConstraints23 = new GridBagConstraints();
            gridBagConstraints23.gridx = 1;
            gridBagConstraints23.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints23.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints23.gridy = 2;
            tlsLabel = new JLabel();
            tlsLabel.setText("Transport Layer Security (TLS)");
            GridBagConstraints gridBagConstraints22 = new GridBagConstraints();
            gridBagConstraints22.gridx = 0;
            gridBagConstraints22.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints22.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints22.gridy = 2;
            GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
            gridBagConstraints21.gridx = 3;
            gridBagConstraints21.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints21.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints21.gridy = 1;
            tokenLabel = new JLabel();
            tokenLabel.setText("Hardware Token");
            GridBagConstraints gridBagConstraints20 = new GridBagConstraints();
            gridBagConstraints20.gridx = 2;
            gridBagConstraints20.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints20.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints20.gridy = 1;
            GridBagConstraints gridBagConstraints19 = new GridBagConstraints();
            gridBagConstraints19.gridx = 1;
            gridBagConstraints19.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints19.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints19.gridy = 1;
            srpLabel = new JLabel();
            srpLabel.setText("Secure Remote Password (SRP)");
            srpLabel.setName("Secure Remote Password (SRP)");
            GridBagConstraints gridBagConstraints18 = new GridBagConstraints();
            gridBagConstraints18.gridx = 0;
            gridBagConstraints18.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints18.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints18.gridy = 1;
            GridBagConstraints gridBagConstraints17 = new GridBagConstraints();
            gridBagConstraints17.gridx = 3;
            gridBagConstraints17.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints17.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints17.gridy = 0;
            kerberosLabel = new JLabel();
            kerberosLabel.setText("Kerberos");
            GridBagConstraints gridBagConstraints16 = new GridBagConstraints();
            gridBagConstraints16.gridx = 2;
            gridBagConstraints16.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints16.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints16.gridy = 0;
            GridBagConstraints gridBagConstraints15 = new GridBagConstraints();
            gridBagConstraints15.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints15.gridy = 0;
            gridBagConstraints15.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints15.gridx = 1;
            GridBagConstraints gridBagConstraints14 = new GridBagConstraints();
            gridBagConstraints14.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints14.gridy = 0;
            gridBagConstraints14.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints14.gridx = 0;
            passwordLabel = new JLabel();
            passwordLabel.setText("Password");
            authPanel = new JPanel();
            authPanel.setLayout(new GridBagLayout());
            authPanel.add(getSrpMethod(), gridBagConstraints18);
            authPanel.add(getKerberosMethod(), gridBagConstraints16);
            authPanel.add(kerberosLabel, gridBagConstraints17);
            authPanel.add(srpLabel, gridBagConstraints19);
            authPanel.add(getHardwareTokenMethod(), gridBagConstraints20);
            authPanel.add(tokenLabel, gridBagConstraints21);
            authPanel.add(getTlsMethod(), gridBagConstraints22);
            authPanel.add(tlsLabel, gridBagConstraints23);
            authPanel.add(getPkiMethod(), gridBagConstraints24);
            authPanel.add(pkiLabel, gridBagConstraints25);
            authPanel.add(getPgpMethod(), gridBagConstraints26);
            authPanel.add(pgpLabel, gridBagConstraints29);
            authPanel.add(getSpkiMethod(), gridBagConstraints30);
            authPanel.add(spkiLabel, gridBagConstraints32);
            authPanel.add(getXmlSignatureMethod(), gridBagConstraints35);
            authPanel.add(getXkmsMethod(), gridBagConstraints33);
            authPanel.add(xkmsLabel, gridBagConstraints34);
            authPanel.add(xmlSignatureLabel, gridBagConstraints37);
            authPanel.add(getUnspecifiedMethod(), gridBagConstraints38);
            authPanel.add(unspecifiedLabel, gridBagConstraints39);
            authPanel.setBorder(BorderFactory.createTitledBorder(null, "Accepted Authentication Methods",
                TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, LookAndFeel
                    .getPanelLabelColor()));
            authPanel.add(passwordLabel, gridBagConstraints15);
            authPanel.add(getPasswordMethod(), gridBagConstraints14);
        }
        return authPanel;
    }


    /**
     * This method initializes passwordMethod
     * 
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getPasswordMethod() {
        if (passwordMethod == null) {
            passwordMethod = new JCheckBox();
            if (!newTrustedIdP) {
                passwordMethod.setSelected(idpAcceptsMethod(PASSWORD));
            }
        }
        return passwordMethod;
    }


    public boolean idpAcceptsMethod(String method) {
        SAMLAuthenticationMethod[] methods = idp.getAuthenticationMethod();
        if (methods != null) {
            for (int i = 0; i < methods.length; i++) {
                if (methods[i].getValue().equals(method)) {
                    return true;
                }
            }
        }
        return false;
    }


    private JCheckBox getKerberosMethod() {
        if (kerberosMethod == null) {
            kerberosMethod = new JCheckBox();
            if (!newTrustedIdP) {
                kerberosMethod.setSelected(idpAcceptsMethod(KERBEROS));
            }
        }
        return kerberosMethod;
    }


    private JCheckBox getSrpMethod() {
        if (srpMethod == null) {
            srpMethod = new JCheckBox();
            if (!newTrustedIdP) {
                srpMethod.setSelected(idpAcceptsMethod(SRP));
            }
        }
        return srpMethod;
    }


    /**
     * This method initializes hardwareTokenMethod
     * 
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getHardwareTokenMethod() {
        if (hardwareTokenMethod == null) {
            hardwareTokenMethod = new JCheckBox();
            if (!newTrustedIdP) {
                hardwareTokenMethod.setSelected(idpAcceptsMethod(HARDWARE_TOKEN));
            }
        }
        return hardwareTokenMethod;
    }


    /**
     * This method initializes tlsMethod
     * 
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getTlsMethod() {
        if (tlsMethod == null) {
            tlsMethod = new JCheckBox();
            if (!newTrustedIdP) {
                tlsMethod.setSelected(idpAcceptsMethod(TLS));
            }
        }
        return tlsMethod;
    }


    /**
     * This method initializes pkiMethod
     * 
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getPkiMethod() {
        if (pkiMethod == null) {
            pkiMethod = new JCheckBox();
            if (!newTrustedIdP) {
                pkiMethod.setSelected(idpAcceptsMethod(PKI));
            }
        }
        return pkiMethod;
    }


    /**
     * This method initializes pgpMethod
     * 
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getPgpMethod() {
        if (pgpMethod == null) {
            pgpMethod = new JCheckBox();
            if (!newTrustedIdP) {
                pgpMethod.setSelected(idpAcceptsMethod(PGP));
            }
        }
        return pgpMethod;
    }


    /**
     * This method initializes spkiMethod
     * 
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getSpkiMethod() {
        if (spkiMethod == null) {
            spkiMethod = new JCheckBox();
            if (!newTrustedIdP) {
                spkiMethod.setSelected(idpAcceptsMethod(SPKI));
            }
        }
        return spkiMethod;
    }


    /**
     * This method initializes xkmsMethod
     * 
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getXkmsMethod() {
        if (xkmsMethod == null) {
            xkmsMethod = new JCheckBox();
            if (!newTrustedIdP) {
                xkmsMethod.setSelected(idpAcceptsMethod(XKMS));
            }
        }
        return xkmsMethod;
    }


    /**
     * This method initializes xmlSignatureLabel
     * 
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getXmlSignatureMethod() {
        if (xmlSignatureMethod == null) {
            xmlSignatureMethod = new JCheckBox();
            if (!newTrustedIdP) {
                xmlSignatureMethod.setSelected(idpAcceptsMethod(XML_SIGNATURE));
            }
        }
        return xmlSignatureMethod;
    }


    /**
     * This method initializes unspecifiedMethod
     * 
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getUnspecifiedMethod() {
        if (unspecifiedMethod == null) {
            unspecifiedMethod = new JCheckBox();
            if (!newTrustedIdP) {
                unspecifiedMethod.setSelected(idpAcceptsMethod(UNSPECIFIED));
            }
        }
        return unspecifiedMethod;
    }


    /**
     * This method initializes attributesPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getAttributesPanel() {
        if (attributesPanel == null) {
            attributesPanel = new JPanel();
            GridBagConstraints gridBagConstraints56 = new GridBagConstraints();
            gridBagConstraints56.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints56.gridy = 7;
            gridBagConstraints56.weightx = 1.0;
            gridBagConstraints56.anchor = GridBagConstraints.WEST;
            gridBagConstraints56.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints56.gridx = 1;
            GridBagConstraints gridBagConstraints55 = new GridBagConstraints();
            gridBagConstraints55.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints55.gridy = 6;
            gridBagConstraints55.weightx = 1.0;
            gridBagConstraints55.anchor = GridBagConstraints.WEST;
            gridBagConstraints55.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints55.gridx = 1;
            GridBagConstraints gridBagConstraints54 = new GridBagConstraints();
            gridBagConstraints54.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints54.gridy = 5;
            gridBagConstraints54.weightx = 1.0;
            gridBagConstraints54.anchor = GridBagConstraints.WEST;
            gridBagConstraints54.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints54.gridx = 1;
            GridBagConstraints gridBagConstraints53 = new GridBagConstraints();
            gridBagConstraints53.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints53.gridy = 4;
            gridBagConstraints53.weightx = 1.0;
            gridBagConstraints53.anchor = GridBagConstraints.WEST;
            gridBagConstraints53.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints53.gridx = 1;
            GridBagConstraints gbc_emailAttributeLabel = new GridBagConstraints();
            gbc_emailAttributeLabel.gridx = 0;
            gbc_emailAttributeLabel.insets = new Insets(2, 2, 2, 2);
            gbc_emailAttributeLabel.anchor = GridBagConstraints.WEST;
            gbc_emailAttributeLabel.gridy = 7;
            emailAttributeLabel = new JLabel();
            emailAttributeLabel.setText("Email Attribute");
            GridBagConstraints gbc_emailAttributeNamespaceLabel = new GridBagConstraints();
            gbc_emailAttributeNamespaceLabel.gridx = 0;
            gbc_emailAttributeNamespaceLabel.anchor = GridBagConstraints.WEST;
            gbc_emailAttributeNamespaceLabel.insets = new Insets(2, 2, 2, 2);
            gbc_emailAttributeNamespaceLabel.gridy = 6;
            emailAttributeNamespaceLabel = new JLabel();
            emailAttributeNamespaceLabel.setText("Email Attribute Namespace");
            GridBagConstraints gbc_lastNameAttributeLabel = new GridBagConstraints();
            gbc_lastNameAttributeLabel.gridx = 0;
            gbc_lastNameAttributeLabel.anchor = GridBagConstraints.WEST;
            gbc_lastNameAttributeLabel.insets = new Insets(2, 2, 2, 2);
            gbc_lastNameAttributeLabel.gridy = 5;
            lastNameAttributeLabel = new JLabel();
            lastNameAttributeLabel.setText("Last Name Attribute");
            GridBagConstraints gbc_lastNameAttributeNamespaceLabel = new GridBagConstraints();
            gbc_lastNameAttributeNamespaceLabel.gridx = 0;
            gbc_lastNameAttributeNamespaceLabel.anchor = GridBagConstraints.WEST;
            gbc_lastNameAttributeNamespaceLabel.insets = new Insets(2, 2, 2, 2);
            gbc_lastNameAttributeNamespaceLabel.gridy = 4;
            lastNameAttributeNamespaceLabel = new JLabel();
            lastNameAttributeNamespaceLabel.setText("Last Name Attribute Namespace");
            GridBagConstraints gridBagConstraints48 = new GridBagConstraints();
            gridBagConstraints48.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints48.gridy = 3;
            gridBagConstraints48.weightx = 1.0;
            gridBagConstraints48.anchor = GridBagConstraints.WEST;
            gridBagConstraints48.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints48.gridx = 1;
            GridBagConstraints gbc_firstNameAttributeLabel = new GridBagConstraints();
            gbc_firstNameAttributeLabel.gridx = 0;
            gbc_firstNameAttributeLabel.anchor = GridBagConstraints.WEST;
            gbc_firstNameAttributeLabel.insets = new Insets(2, 2, 2, 2);
            gbc_firstNameAttributeLabel.gridy = 3;
            firstNameAttributeLabel = new JLabel();
            firstNameAttributeLabel.setText("First Name Attribute");
            GridBagConstraints gridBagConstraints46 = new GridBagConstraints();
            gridBagConstraints46.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints46.gridx = 1;
            gridBagConstraints46.gridy = 2;
            gridBagConstraints46.anchor = GridBagConstraints.WEST;
            gridBagConstraints46.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints46.weightx = 1.0;
            GridBagConstraints gbc_firstNameAttributeNamespaceLabel = new GridBagConstraints();
            gbc_firstNameAttributeNamespaceLabel.gridx = 0;
            gbc_firstNameAttributeNamespaceLabel.anchor = GridBagConstraints.WEST;
            gbc_firstNameAttributeNamespaceLabel.insets = new Insets(2, 2, 2, 2);
            gbc_firstNameAttributeNamespaceLabel.gridy = 2;
            firstNameAttributeNamespaceLabel = new JLabel();
            firstNameAttributeNamespaceLabel.setText("First Name Attribute Namespace");
            GridBagConstraints gridBagConstraints44 = new GridBagConstraints();
            gridBagConstraints44.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints44.gridy = 1;
            gridBagConstraints44.weightx = 1.0;
            gridBagConstraints44.anchor = GridBagConstraints.WEST;
            gridBagConstraints44.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints44.gridx = 1;
            GridBagConstraints gbc_userIdAttributeLabel = new GridBagConstraints();
            gbc_userIdAttributeLabel.gridx = 0;
            gbc_userIdAttributeLabel.anchor = GridBagConstraints.WEST;
            gbc_userIdAttributeLabel.insets = new Insets(2, 2, 2, 2);
            gbc_userIdAttributeLabel.gridy = 1;
            userIdAttributeLabel = new JLabel();
            userIdAttributeLabel.setText("User Id Attribute");
            GridBagConstraints gbc_userIdNamespaceLabel = new GridBagConstraints();
            gbc_userIdNamespaceLabel.anchor = GridBagConstraints.WEST;
            gbc_userIdNamespaceLabel.gridx = 0;
            gbc_userIdNamespaceLabel.gridy = 0;
            gbc_userIdNamespaceLabel.insets = new Insets(2, 2, 2, 2);
            GridBagConstraints gridBagConstraints41 = new GridBagConstraints();
            gridBagConstraints41.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints41.gridx = 1;
            gridBagConstraints41.gridy = 0;
            gridBagConstraints41.anchor = GridBagConstraints.WEST;
            gridBagConstraints41.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints41.weighty = 0.0D;
            gridBagConstraints41.weightx = 1.0;
            userIdNamespaceLabel = new JLabel();
            userIdNamespaceLabel.setText("User Id Attribute Namespace");
            
            attributesPanel.setLayout(new GridBagLayout());
            attributesPanel.add(userIdNamespaceLabel, gbc_userIdNamespaceLabel);
            attributesPanel.add(getUserIdNamespace(), gridBagConstraints41);
            attributesPanel.add(userIdAttributeLabel, gbc_userIdAttributeLabel);
            attributesPanel.add(getUserIdName(), gridBagConstraints44);
            attributesPanel.add(firstNameAttributeNamespaceLabel, gbc_firstNameAttributeNamespaceLabel);
            attributesPanel.add(getFirstNameNamespace(), gridBagConstraints46);
            attributesPanel.add(firstNameAttributeLabel, gbc_firstNameAttributeLabel);
            attributesPanel.add(getFirstName(), gridBagConstraints48);
            attributesPanel.add(lastNameAttributeNamespaceLabel, gbc_lastNameAttributeNamespaceLabel);
            attributesPanel.add(lastNameAttributeLabel, gbc_lastNameAttributeLabel);
            attributesPanel.add(emailAttributeNamespaceLabel, gbc_emailAttributeNamespaceLabel);
            attributesPanel.add(emailAttributeLabel, gbc_emailAttributeLabel);
            attributesPanel.add(getLastNameNamespace(), gridBagConstraints53);
            attributesPanel.add(getLastName(), gridBagConstraints54);
            attributesPanel.add(getEmailNamespace(), gridBagConstraints55);
            attributesPanel.add(getEmail(), gridBagConstraints56);
            attributesPanel.setBorder(BorderFactory.createTitledBorder(null, "SAML Attribute Descriptions",
                TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, LookAndFeel
                    .getPanelLabelColor()));
        }
        return attributesPanel;
    }


    /**
     * This method initializes userIdNamespace
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getUserIdNamespace() {
        if (userIdNamespace == null) {
            userIdNamespace = new JTextField();
            if (newTrustedIdP) {
                userIdNamespace.setText(SAMLConstants.UID_ATTRIBUTE_NAMESPACE);
            } else {
                userIdNamespace.setText(idp.getUserIdAttributeDescriptor().getNamespaceURI());
            }
        }
        return userIdNamespace;
    }


    /**
     * This method initializes userIdName
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getUserIdName() {
        if (userIdName == null) {
            userIdName = new JTextField();
            if (newTrustedIdP) {
                userIdName.setText(SAMLConstants.UID_ATTRIBUTE);
            } else {
                userIdName.setText(idp.getUserIdAttributeDescriptor().getName());
            }
        }
        return userIdName;
    }


    /**
     * This method initializes firstNameNamespace
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getFirstNameNamespace() {
        if (firstNameNamespace == null) {
            firstNameNamespace = new JTextField();
            if (newTrustedIdP) {
                firstNameNamespace.setText(SAMLConstants.FIRST_NAME_ATTRIBUTE_NAMESPACE);
            } else {
                firstNameNamespace.setText(idp.getFirstNameAttributeDescriptor().getNamespaceURI());
            }
        }
        return firstNameNamespace;
    }


    /**
     * This method initializes firstName
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getFirstName() {
        if (firstName == null) {
            firstName = new JTextField();
            if (newTrustedIdP) {
                firstName.setText(SAMLConstants.FIRST_NAME_ATTRIBUTE);
            } else {
                firstName.setText(idp.getFirstNameAttributeDescriptor().getName());
            }
        }
        return firstName;
    }


    /**
     * This method initializes lastNameNamespace
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getLastNameNamespace() {
        if (lastNameNamespace == null) {
            lastNameNamespace = new JTextField();
            if (newTrustedIdP) {
                lastNameNamespace.setText(SAMLConstants.LAST_NAME_ATTRIBUTE_NAMESPACE);
            } else {
                lastNameNamespace.setText(idp.getLastNameAttributeDescriptor().getNamespaceURI());
            }
        }
        return lastNameNamespace;
    }


    /**
     * This method initializes lastName
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getLastName() {
        if (lastName == null) {
            lastName = new JTextField();
            if (newTrustedIdP) {
                lastName.setText(SAMLConstants.LAST_NAME_ATTRIBUTE);
            } else {
                lastName.setText(idp.getLastNameAttributeDescriptor().getName());
            }
        }
        return lastName;
    }


    /**
     * This method initializes emailNamespace
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getEmailNamespace() {
        if (emailNamespace == null) {
            emailNamespace = new JTextField();
            if (newTrustedIdP) {
                emailNamespace.setText(SAMLConstants.EMAIL_ATTRIBUTE_NAMESPACE);
            } else {
                emailNamespace.setText(idp.getEmailAttributeDescriptor().getNamespaceURI());
            }
        }
        return emailNamespace;
    }


    /**
     * This method initializes email
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getEmail() {
        if (email == null) {
            email = new JTextField();
            if (newTrustedIdP) {
                email.setText(SAMLConstants.EMAIL_ATTRIBUTE);
            } else {
                email.setText(idp.getEmailAttributeDescriptor().getName());
            }
        }
        return email;
    }


    /**
     * This method initializes authenticationServicePanel
     * 
     * @return javax.swing.JPanel
     * @throws Exception 
     */
    private JPanel getAuthenticationServicePanel() throws Exception {
        if (authenticationServicePanel == null) {
            authenticationServicePanel = new JPanel();
            GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
            gridBagConstraints3.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints3.gridx = 1;
            gridBagConstraints3.gridy = 2;
            gridBagConstraints3.anchor = GridBagConstraints.WEST;
            gridBagConstraints3.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints3.weightx = 1.0;
            GridBagConstraints gbc_publishLabel = new GridBagConstraints();
            gbc_publishLabel.anchor = GridBagConstraints.WEST;
            gbc_publishLabel.gridy = 2;
            gbc_publishLabel.insets = new Insets(2, 2, 2, 2);
            gbc_publishLabel.gridx = 0;
            publishLabel = new JLabel();
            publishLabel.setText("Publish");
            GridBagConstraints gridBagConstraints62 = new GridBagConstraints();
            gridBagConstraints62.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints62.gridy = 1;
            gridBagConstraints62.weightx = 1.0;
            gridBagConstraints62.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints62.anchor = GridBagConstraints.WEST;
            gridBagConstraints62.gridx = 1;
            GridBagConstraints gbc_authServiceIdentityLabel = new GridBagConstraints();
            gbc_authServiceIdentityLabel.anchor = GridBagConstraints.WEST;
            gbc_authServiceIdentityLabel.gridy = 1;
            gbc_authServiceIdentityLabel.insets = new Insets(2, 2, 2, 2);
            gbc_authServiceIdentityLabel.gridx = 0;
            authServiceIdentityLabel = new JLabel();
            authServiceIdentityLabel.setText("Authentication Service Identity");
            GridBagConstraints gbc_authServiceUrlLabel = new GridBagConstraints();
            gbc_authServiceUrlLabel.gridx = 0;
            gbc_authServiceUrlLabel.insets = new Insets(2, 2, 2, 2);
            gbc_authServiceUrlLabel.anchor = GridBagConstraints.WEST;
            gbc_authServiceUrlLabel.gridy = 0;
            GridBagConstraints gridBagConstraints59 = new GridBagConstraints();
            gridBagConstraints59.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints59.gridx = 1;
            gridBagConstraints59.gridy = 0;
            gridBagConstraints59.anchor = GridBagConstraints.WEST;
            gridBagConstraints59.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints59.weightx = 1.0;
            authServiceUrlLabel = new JLabel();
            authServiceUrlLabel.setText("Authentication Service URL");
            
            authenticationServicePanel.setLayout(new GridBagLayout());
            authenticationServicePanel.add(authServiceUrlLabel, gbc_authServiceUrlLabel);
            authenticationServicePanel.add(getAuthenticationServiceURL(), gridBagConstraints59);
            authenticationServicePanel.add(authServiceIdentityLabel, gbc_authServiceIdentityLabel);
            authenticationServicePanel.add(getAuthenticationServiceIdentity(), gridBagConstraints62);
            authenticationServicePanel.add(publishLabel, gbc_publishLabel);
            
            if (doesDorianSupportPublish()) {
                authenticationServicePanel.add(getPublish(), gridBagConstraints3);
            }
        }
        return authenticationServicePanel;
    }


    /**
     * This method initializes displayName
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getDisplayName() {
        if (displayName == null) {
            displayName = new JTextField();
            if (!newTrustedIdP) {
                displayName.setText(idp.getDisplayName());
            }
        }
        return displayName;
    }


    /**
     * This method initializes authenticationServiceURL
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getAuthenticationServiceURL() {
        if (authenticationServiceURL == null) {
            authenticationServiceURL = new JTextField();
            if (!newTrustedIdP) {
                authenticationServiceURL.setText(idp.getAuthenticationServiceURL());
            }
        }
        return authenticationServiceURL;
    }


    /**
     * This method initializes authenticationServiceIdentity
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getAuthenticationServiceIdentity() {
        if (authenticationServiceIdentity == null) {
            authenticationServiceIdentity = new JTextField();
            if (!newTrustedIdP) {
                authenticationServiceIdentity.setText(idp.getAuthenticationServiceIdentity());
            }
        }
        return authenticationServiceIdentity;
    }


    /**
     * This method initializes titlePanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getTitlePanel() {
        if (titlePanel == null) {
            titlePanel = new TitlePanel(titleStr, subtitleStr);
        }
        return titlePanel;
    }


    /**
     * This method initializes auditPanel
     * 
     * @return javax.swing.JPanel
     */
    private FederationAuditPanel getAuditPanel() {
        if (auditPanel == null) {
            auditPanel = new FederationAuditPanel(this, FederationAuditPanel.IDP_MODE, this.idp.getName());
            auditPanel.setProgess(getProgressPanel());
        }
        return auditPanel;
    }
    
    
    private JPanel getLockoutsWrapperPanel() {
        if (lockoutsWrapperPanel == null) {
            lockoutsWrapperPanel = new JPanel();
            GridBagLayout gbl_lockoutsWrapperPanel = new GridBagLayout();
            gbl_lockoutsWrapperPanel.columnWidths = new int[]{0, 0};
            gbl_lockoutsWrapperPanel.rowHeights = new int[]{0, 0, 0};
            gbl_lockoutsWrapperPanel.columnWeights = new double[]{0.0, Double.MIN_VALUE};
            gbl_lockoutsWrapperPanel.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
            lockoutsWrapperPanel.setLayout(gbl_lockoutsWrapperPanel);
            
            GridBagConstraints gbc_loadLockoutsButton = new GridBagConstraints();
            gbc_loadLockoutsButton.insets = new Insets(2, 2, 2, 2);
            gbc_loadLockoutsButton.gridx = 0;
            gbc_loadLockoutsButton.gridy = 1;
            lockoutsWrapperPanel.add(getLoadLockoutsButton(), gbc_loadLockoutsButton);
            
            GridBagConstraints gbc_lockoutsPanel = new GridBagConstraints();
            gbc_lockoutsPanel.insets = new Insets(2,2,2,2);
            gbc_lockoutsPanel.gridx = 0;
            gbc_lockoutsPanel.gridy = 0;
            gbc_lockoutsPanel.fill = GridBagConstraints.BOTH;
            gbc_lockoutsPanel.weightx = 1.0;
            gbc_lockoutsPanel.weighty = 1.0;
            lockoutsWrapperPanel.add(getLockoutsPanel(), gbc_lockoutsPanel);
        }
        return lockoutsWrapperPanel;
    }
    
    
    private JButton getLoadLockoutsButton() {
        if (loadLockoutsButton == null) {
            loadLockoutsButton = new JButton("Load Lockouts");
            loadLockoutsButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    // get a client to the authentication service
                    GlobusCredential credential = getSession().getCredential();
                    String url = idp.getAuthenticationServiceURL();
                    AuthenticationServiceClient client = null;
                    try {
                        client = new AuthenticationServiceClient(url, credential);
                    } catch (Exception ex) {
                        ErrorDialog.showError("Error communicating with the service: " + ex.getMessage(), ex);
                    }
                    try {
                        getLockoutsPanel().loadFromIdP(client);
                    } catch (RemoteException ex) {
                        boolean handled = false;
                        if (ex instanceof BaseFaultType) {
                            FaultHelper helper = new FaultHelper((BaseFaultType) ex, true);
                            String message = helper.getMessage();
                            if (message.contains("Operation name could not be determined")) {
                                ErrorDialog.showError("This authentication service does not support listing lockout information", ex);
                                handled = true;
                            }
                        }
                        if (!handled) {
                            ErrorDialog.showError("Error communicating with the service: " + ex.getMessage(), ex);
                        }
                    }
                }
            });
        }
        return loadLockoutsButton;
    }
    
    
    private IdPLockoutsPanel getLockoutsPanel() {
        if (lockoutsPanel == null) {
            lockoutsPanel = new IdPLockoutsPanel();
        }
        return lockoutsPanel;
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
     * This method initializes publish
     * 
     * @return javax.swing.JComboBox
     * @throws Exception 
     */
    private JComboBox getPublish() throws Exception {
        if (publish == null) {
        	GridAdministrationClient client = this.session.getAdminClient();
        	
            publish = new JComboBox();
            publish.addItem(PUBLISH_YES);
            publish.addItem(PUBLISH_NO);
            if (!newTrustedIdP) {
                if (client.getPublish(idp)) {
                    publish.setSelectedItem(PUBLISH_YES);
                } else {
                    publish.setSelectedItem(PUBLISH_NO);
                }
            } else {
                publish.setSelectedItem(PUBLISH_YES);
            }
        }
        return publish;
    }
    
    
    private boolean doesDorianSupportPublish() {
    	try {
			if (Double.parseDouble(session.getHandle().getServiceVersion()) < 1.4) {
				return false;
			}
		} catch (Exception e) {
		}
		return true;
    }
}
