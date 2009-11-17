package org.cagrid.gaards.ui.dorian.federation;

import gov.nih.nci.cagrid.common.FaultUtil;
import gov.nih.nci.cagrid.common.Runner;
import gov.nih.nci.cagrid.common.Utils;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
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


/**
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Langella </A>
 * @version $Id: TrustedIdPWindow.java,v 1.6.2.1 2009/02/10 20:26:02 langella
 *          Exp $
 */
public class TrustedIdPWindow extends ApplicationComponent implements DorianSessionProvider {

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

    private final static String AUDIT_PANEL = "Audit"; // @jve:decl-index=0:

    private javax.swing.JPanel jContentPane = null;

    private JPanel mainPanel = null;

    private JPanel buttonPanel = null;

    private JButton updateTrustedIdP = null;

    private JTabbedPane jTabbedPane = null;

    private JPanel jPanel1 = null;

    private JPanel infoPanel = null;

    private TrustedIdP idp;

    private JPanel certificatePanel = null;

    private CertificatePanel credPanel = null;

    private boolean newTrustedIdP;

    private JLabel idLabel = null;

    private JTextField idpId = null;

    private JLabel nameLabel = null;

    private JTextField idpName = null;

    private JLabel statusLabel = null;

    private TrustedIdPStatusComboBox status = null;

    private List<GridUserPolicy> policies;

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

    private TrustedIdPsWindow window;

    private JPanel attributesPanel = null;

    private JPanel jPanel = null;

    private JLabel jLabel = null;

    private JTextField userIdNamespace = null;

    private JLabel jLabel1 = null;

    private JTextField userIdName = null;

    private JLabel jLabel2 = null;

    private JTextField firstNameNamespace = null;

    private JLabel jLabel3 = null;

    private JTextField firstName = null;

    private JLabel jLabel4 = null;

    private JLabel jLabel5 = null;

    private JLabel jLabel6 = null;

    private JLabel jLabel7 = null;

    private JTextField lastNameNamespace = null;

    private JTextField lastName = null;

    private JTextField emailNamespace = null;

    private JTextField email = null;

    private JPanel authenticationServicePanel = null;

    private JLabel jLabel8 = null;

    private JTextField displayName = null;

    private JPanel jPanel3 = null;

    private JLabel jLabel9 = null;

    private JTextField authenticationServiceURL = null;

    private JLabel jLabel10 = null;

    private JTextField authenticationServiceIdentity = null;

    private DorianSession session;

    private JPanel titlePanel = null;

    private String titleStr = null;

    private String subtitleStr = null;

    private FederationAuditPanel auditPanel = null;

    private ProgressPanel progressPanel = null;

    private JLabel jLabel11 = null;

    private JComboBox publish = null;


    public TrustedIdPWindow(DorianSession session, TrustedIdPsWindow window, List<GridUserPolicy> policies) {
        super();
        this.window = window;
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


    public DorianSession getSession() throws Exception {
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
    }


    /**
     * This method initializes this
     */
    private void initialize() {
        this.setContentPane(getJContentPane());
        if (this.newTrustedIdP) {
            this.setTitle("Add Trusted IdP");
        } else {
            this.setTitle(this.titleStr);
        }
        this.setFrameIcon(DorianLookAndFeel.getTrustedIdPIcon());
        this.setSize(600, 400);

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

            if (getPublish().getSelectedItem().equals(PUBLISH_YES)) {
                idp.setPublish(true);
            } else {
                idp.setPublish(false);
            }

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
                saml[i] = (SAMLAuthenticationMethod) authMethod.get(i);
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
                window.addTrustedIdP(client.addTrustedIdP(idp));
                getProgressPanel().stopProgress("Successfully added IdP.");
                dispose();
            } else {
                client.updateTrustedIdP(idp);
                getProgressPanel().stopProgress("Successfully updated IdP.");
            }
        } catch (PermissionDeniedFault pdf) {
            FaultUtil.printFault(pdf);
            getProgressPanel().stopProgress("Error");
            ErrorDialog.showError(pdf);

        } catch (Exception e) {
            FaultUtil.printFault(e);
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
     */
    private JTabbedPane getJTabbedPane() {
        if (jTabbedPane == null) {
            jTabbedPane = new JTabbedPane();
            jTabbedPane.addTab(INFO_PANEL, null, getInfoPanel());
            jTabbedPane.addTab(AUTHENTICATION_SERVICE, null, getAuthenticationServicePanel(), null);
            jTabbedPane.addTab(CERTIFICATE_PANEL, null, getCertificatePanel(), null);
            jTabbedPane.addTab(ATTRIBUTES_PANEL, null, getAttributesPanel(), null);

            if (!this.newTrustedIdP) {
                jTabbedPane.addTab(AUDIT_PANEL, null, getAuditPanel(), null);
            }
        }
        return jTabbedPane;
    }


    /**
     * This method initializes jPanel1
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getJPanel1() {
        if (jPanel1 == null) {
            GridBagConstraints gridBagConstraints58 = new GridBagConstraints();
            gridBagConstraints58.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints58.gridy = 2;
            gridBagConstraints58.weightx = 1.0;
            gridBagConstraints58.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints58.anchor = GridBagConstraints.WEST;
            gridBagConstraints58.gridx = 1;
            GridBagConstraints gridBagConstraints57 = new GridBagConstraints();
            gridBagConstraints57.gridx = 0;
            gridBagConstraints57.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints57.anchor = GridBagConstraints.WEST;
            gridBagConstraints57.gridy = 2;
            jLabel8 = new JLabel();
            jLabel8.setText("Display Name");
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
            if (newTrustedIdP) {

            }
            jPanel1 = new JPanel();
            jPanel1.setLayout(new GridBagLayout());
            jPanel1.setName(INFO_PANEL);
            jPanel1.add(getStatus(), gridBagConstraints10);
            jPanel1.add(statusLabel, gridBagConstraints9);
            jPanel1.add(idLabel, gridBagConstraints5);
            jPanel1.add(getIdpId(), gridBagConstraints6);
            jPanel1.add(nameLabel, gridBagConstraints7);
            jPanel1.add(getIdPName(), gridBagConstraints8);
            jPanel1.add(policyLabel, gridBagConstraints11);
            jPanel1.add(getUserPolicy(), gridBagConstraints12);
            jPanel1.add(getAuthPanel(), gridBagConstraints13);
            jPanel1.add(jLabel8, gridBagConstraints57);
            jPanel1.add(getDisplayName(), gridBagConstraints58);
        }
        return jPanel1;
    }


    /**
     * This method initializes infoPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getInfoPanel() {
        if (infoPanel == null) {
            infoPanel = new JPanel();
            infoPanel.setLayout(new BorderLayout());
            infoPanel.add(getJPanel1(), java.awt.BorderLayout.NORTH);
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
            GridBagConstraints gridBagConstraints40 = new GridBagConstraints();
            gridBagConstraints40.gridx = 0;
            gridBagConstraints40.ipadx = 208;
            gridBagConstraints40.weightx = 1.0D;
            gridBagConstraints40.weighty = 1.0D;
            gridBagConstraints40.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints40.anchor = java.awt.GridBagConstraints.NORTH;
            gridBagConstraints40.gridy = 0;
            GridBagConstraints gridBagConstraints36 = new GridBagConstraints();
            gridBagConstraints36.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints36.gridy = 0;
            gridBagConstraints36.weightx = 1.0D;
            gridBagConstraints36.weighty = 1.0D;
            gridBagConstraints36.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints36.anchor = java.awt.GridBagConstraints.NORTH;
            gridBagConstraints36.gridx = 0;
            certificatePanel = new JPanel();
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
                e.printStackTrace();
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
            attributesPanel.setLayout(new BorderLayout());
            attributesPanel.add(getJPanel(), BorderLayout.NORTH);
        }
        return attributesPanel;
    }


    /**
     * This method initializes jPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getJPanel() {
        if (jPanel == null) {
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
            GridBagConstraints gridBagConstraints52 = new GridBagConstraints();
            gridBagConstraints52.gridx = 0;
            gridBagConstraints52.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints52.anchor = GridBagConstraints.WEST;
            gridBagConstraints52.gridy = 7;
            jLabel7 = new JLabel();
            jLabel7.setText("Email Attribute");
            GridBagConstraints gridBagConstraints51 = new GridBagConstraints();
            gridBagConstraints51.gridx = 0;
            gridBagConstraints51.anchor = GridBagConstraints.WEST;
            gridBagConstraints51.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints51.gridy = 6;
            jLabel6 = new JLabel();
            jLabel6.setText("Email Attribute Namespace");
            GridBagConstraints gridBagConstraints50 = new GridBagConstraints();
            gridBagConstraints50.gridx = 0;
            gridBagConstraints50.anchor = GridBagConstraints.WEST;
            gridBagConstraints50.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints50.gridy = 5;
            jLabel5 = new JLabel();
            jLabel5.setText("Last Name Attribute");
            GridBagConstraints gridBagConstraints49 = new GridBagConstraints();
            gridBagConstraints49.gridx = 0;
            gridBagConstraints49.anchor = GridBagConstraints.WEST;
            gridBagConstraints49.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints49.gridy = 4;
            jLabel4 = new JLabel();
            jLabel4.setText("Last Name Attribute Namespace");
            GridBagConstraints gridBagConstraints48 = new GridBagConstraints();
            gridBagConstraints48.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints48.gridy = 3;
            gridBagConstraints48.weightx = 1.0;
            gridBagConstraints48.anchor = GridBagConstraints.WEST;
            gridBagConstraints48.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints48.gridx = 1;
            GridBagConstraints gridBagConstraints47 = new GridBagConstraints();
            gridBagConstraints47.gridx = 0;
            gridBagConstraints47.anchor = GridBagConstraints.WEST;
            gridBagConstraints47.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints47.gridy = 3;
            jLabel3 = new JLabel();
            jLabel3.setText("First Name Attribute");
            GridBagConstraints gridBagConstraints46 = new GridBagConstraints();
            gridBagConstraints46.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints46.gridx = 1;
            gridBagConstraints46.gridy = 2;
            gridBagConstraints46.anchor = GridBagConstraints.WEST;
            gridBagConstraints46.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints46.weightx = 1.0;
            GridBagConstraints gridBagConstraints45 = new GridBagConstraints();
            gridBagConstraints45.gridx = 0;
            gridBagConstraints45.anchor = GridBagConstraints.WEST;
            gridBagConstraints45.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints45.gridy = 2;
            jLabel2 = new JLabel();
            jLabel2.setText("First Name Attribute Namespace");
            GridBagConstraints gridBagConstraints44 = new GridBagConstraints();
            gridBagConstraints44.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints44.gridy = 1;
            gridBagConstraints44.weightx = 1.0;
            gridBagConstraints44.anchor = GridBagConstraints.WEST;
            gridBagConstraints44.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints44.gridx = 1;
            GridBagConstraints gridBagConstraints43 = new GridBagConstraints();
            gridBagConstraints43.gridx = 0;
            gridBagConstraints43.anchor = GridBagConstraints.WEST;
            gridBagConstraints43.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints43.gridy = 1;
            jLabel1 = new JLabel();
            jLabel1.setText("User Id Attribute");
            GridBagConstraints gridBagConstraints42 = new GridBagConstraints();
            gridBagConstraints42.anchor = GridBagConstraints.WEST;
            gridBagConstraints42.gridx = 0;
            gridBagConstraints42.gridy = 0;
            gridBagConstraints42.insets = new Insets(2, 2, 2, 2);
            GridBagConstraints gridBagConstraints41 = new GridBagConstraints();
            gridBagConstraints41.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints41.gridx = 1;
            gridBagConstraints41.gridy = 0;
            gridBagConstraints41.anchor = GridBagConstraints.WEST;
            gridBagConstraints41.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints41.weighty = 0.0D;
            gridBagConstraints41.weightx = 1.0;
            jLabel = new JLabel();
            jLabel.setText("User Id Attribute Namespace");
            jPanel = new JPanel();
            jPanel.setLayout(new GridBagLayout());
            jPanel.add(jLabel, gridBagConstraints42);
            jPanel.add(getUserIdNamespace(), gridBagConstraints41);
            jPanel.add(jLabel1, gridBagConstraints43);
            jPanel.add(getUserIdName(), gridBagConstraints44);
            jPanel.add(jLabel2, gridBagConstraints45);
            jPanel.add(getFirstNameNamespace(), gridBagConstraints46);
            jPanel.add(jLabel3, gridBagConstraints47);
            jPanel.add(getFirstName(), gridBagConstraints48);
            jPanel.add(jLabel4, gridBagConstraints49);
            jPanel.add(jLabel5, gridBagConstraints50);
            jPanel.add(jLabel6, gridBagConstraints51);
            jPanel.add(jLabel7, gridBagConstraints52);
            jPanel.add(getLastNameNamespace(), gridBagConstraints53);
            jPanel.add(getLastName(), gridBagConstraints54);
            jPanel.add(getEmailNamespace(), gridBagConstraints55);
            jPanel.add(getEmail(), gridBagConstraints56);
            jPanel.setBorder(BorderFactory.createTitledBorder(null, "SAML Attribute Descriptions",
                TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, LookAndFeel
                    .getPanelLabelColor()));
        }
        return jPanel;
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
     */
    private JPanel getAuthenticationServicePanel() {
        if (authenticationServicePanel == null) {
            authenticationServicePanel = new JPanel();
            authenticationServicePanel.setLayout(new BorderLayout());
            authenticationServicePanel.add(getJPanel3(), BorderLayout.NORTH);
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
     * This method initializes jPanel3
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getJPanel3() {
        if (jPanel3 == null) {
            GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
            gridBagConstraints3.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints3.gridx = 1;
            gridBagConstraints3.gridy = 2;
            gridBagConstraints3.anchor = GridBagConstraints.WEST;
            gridBagConstraints3.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints3.weightx = 1.0;
            GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
            gridBagConstraints1.anchor = GridBagConstraints.WEST;
            gridBagConstraints1.gridy = 2;
            gridBagConstraints1.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints1.gridx = 0;
            jLabel11 = new JLabel();
            jLabel11.setText("Publish");
            GridBagConstraints gridBagConstraints62 = new GridBagConstraints();
            gridBagConstraints62.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints62.gridy = 1;
            gridBagConstraints62.weightx = 1.0;
            gridBagConstraints62.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints62.anchor = GridBagConstraints.WEST;
            gridBagConstraints62.gridx = 1;
            GridBagConstraints gridBagConstraints61 = new GridBagConstraints();
            gridBagConstraints61.anchor = GridBagConstraints.WEST;
            gridBagConstraints61.gridy = 1;
            gridBagConstraints61.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints61.gridx = 0;
            jLabel10 = new JLabel();
            jLabel10.setText("Authentication Service Identity");
            GridBagConstraints gridBagConstraints60 = new GridBagConstraints();
            gridBagConstraints60.gridx = 0;
            gridBagConstraints60.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints60.anchor = GridBagConstraints.WEST;
            gridBagConstraints60.gridy = 0;
            GridBagConstraints gridBagConstraints59 = new GridBagConstraints();
            gridBagConstraints59.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints59.gridx = 1;
            gridBagConstraints59.gridy = 0;
            gridBagConstraints59.anchor = GridBagConstraints.WEST;
            gridBagConstraints59.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints59.weightx = 1.0;
            jLabel9 = new JLabel();
            jLabel9.setText("Authentication Service URL");
            jPanel3 = new JPanel();
            jPanel3.setLayout(new GridBagLayout());
            jPanel3.add(jLabel9, gridBagConstraints60);
            jPanel3.add(getAuthenticationServiceURL(), gridBagConstraints59);
            jPanel3.add(jLabel10, gridBagConstraints61);
            jPanel3.add(getAuthenticationServiceIdentity(), gridBagConstraints62);
            jPanel3.add(jLabel11, gridBagConstraints1);
            jPanel3.add(getPublish(), gridBagConstraints3);
        }
        return jPanel3;
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
     */
    private JComboBox getPublish() {
        if (publish == null) {
            publish = new JComboBox();
            publish.addItem(PUBLISH_YES);
            publish.addItem(PUBLISH_NO);
            if (!newTrustedIdP) {
                if (idp.isPublish()) {
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

}
