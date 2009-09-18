package org.cagrid.gaards.ui.cds;

import gov.nih.nci.cagrid.common.Runner;
import gov.nih.nci.cagrid.common.Utils;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import org.cagrid.gaards.cds.client.DelegationAdminClient;
import org.cagrid.gaards.cds.client.DelegationUserClient;
import org.cagrid.gaards.cds.common.DelegatedCredentialAuditFilter;
import org.cagrid.gaards.cds.common.DelegatedCredentialAuditRecord;
import org.cagrid.gaards.cds.common.DelegationRecord;
import org.cagrid.gaards.ui.common.ProgressPanel;
import org.cagrid.gaards.ui.common.SelectDateDialog;
import org.cagrid.gaards.ui.common.TitlePanel;
import org.cagrid.gaards.ui.dorian.federation.UserSearchDialog;
import org.cagrid.grape.ApplicationComponent;
import org.cagrid.grape.GridApplication;
import org.cagrid.grape.LookAndFeel;
import org.cagrid.grape.utils.ErrorDialog;


/**
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Langella </A>
 * @version $Id: DelegatedCredentialWindow.java,v 1.2 2007/11/27 20:09:50
 *          langella Exp $
 */
public class DelegatedCredentialWindow extends ApplicationComponent {

    private static final long serialVersionUID = 1L;

    private final static String INFO_PANEL = "General Information";

    private final static String POLICY_PANEL = "Delegation Policy";

    private final static String CERTIFICATE_PANEL = "Certificate Chain";

    private final static String AUDITING_PANEL = "Auditing";

    private javax.swing.JPanel jContentPane = null;

    private JPanel mainPanel = null;

    private ProgressPanel progressPanel = null;

    private JButton updateStatus = null;

    private JTabbedPane jTabbedPane = null;

    private JPanel jPanel1 = null;

    private JPanel titlePanel = null;

    private JPanel infoPanel = null;

    private JLabel gridIdLabel = null;

    private JTextField gridIdentity = null;

    private JPanel certificatePanel = null;

    private DelegationRecord record;

    private JLabel jLabel = null;

    private JTextField delegationIdentifier = null;

    private JLabel jLabel1 = null;

    private JTextField initiated = null;

    private JLabel jLabel2 = null;

    private JTextField approved = null;

    private JLabel jLabel3 = null;

    private JTextField expires = null;

    private JLabel jLabel4 = null;

    private JTextField lifetime = null;

    private JLabel jLabel5 = null;

    private DelegationStatusComboBox status = null;

    private JScrollPane jScrollPane = null;

    private CertificateChainTable certificateChainTable = null;

    private JButton viewCertificate = null;

    private JPanel policyPanel = null;

    private JLabel jLabel6 = null;

    private JTextField issuedCredentialPathLength = null;

    private JPanel policyTypePanel = null;

    private JLabel jLabel7 = null;

    private JTextField delegationPolicyType = null;

    private DelegationPolicyPanel delegationPolicyPanel;

    private JPanel infoButtonPanel = null;

    private JPanel auditPanel = null;

    private JScrollPane jScrollPane1 = null;

    private DelegatedCredentialAuditRecordTable auditRecords = null;

    private JPanel searchPanel = null;

    private JLabel jLabel8 = null;

    private JPanel identityPanel = null;

    private JTextField sourceIdentity = null;

    private JButton findSource = null;

    private JPanel auditTypePanel = null;

    private JLabel jLabel9 = null;

    private DelegatedCredentialEventComboBox eventType = null;

    private JPanel dateRangePanel = null;

    private JLabel jLabel10 = null;

    private JTextField startDate = null;

    private JButton startDateButton = null;

    private JLabel jLabel11 = null;

    private JTextField endDate = null;

    private JButton endDateButton = null;

    private Calendar searchStartDate;

    private Calendar searchEndDate;

    private JPanel searchButtonPanel = null;

    private JButton searchButton = null;

    private JButton clear = null;

    private JPanel auditingButtonPanel = null;

    private JButton viewAudtingRecord = null;

    private CDSSession session;


    /**
     * This is the default constructor
     */
    public DelegatedCredentialWindow(CDSSession session, DelegationRecord record) throws Exception {
        super();
        this.session = session;
        this.record = record;
        initialize();
        this.setFrameIcon(CDSLookAndFeel.getDelegateCredentialsIcon());
        setDelegationRecord();
    }


    private void setDelegationRecord() throws Exception {
        getGridIdentity().setText(record.getGridIdentity());
        getDelegationIdentifier().setText(String.valueOf(record.getDelegationIdentifier().getDelegationId()));
        getInitiated().setText(getDateString(record.getDateInitiated()));
        getApproved().setText(getDateString(record.getDateApproved()));
        getExpires().setText(getDateString(record.getExpiration()));
        String str = record.getIssuedCredentialLifetime().getHours() + " hour(s), "
            + record.getIssuedCredentialLifetime().getMinutes() + " minute(s) "
            + record.getIssuedCredentialLifetime().getSeconds() + " second(s)";
        getLifetime().setText(str);
        getIssuedCredentialPathLength().setText(String.valueOf(record.getIssuedCredentialPathLength()));
        getStatus().setSelectedItem(record.getDelegationStatus());
        getCertificateChainTable().setCertificateChain(record.getCertificateChain());
        String policyType = CDSUIUtils.getDelegationPolicyType(record.getDelegationPolicy());
        getDelegationPolicyType().setText(policyType);

        GridBagConstraints policyPanelConstraints = new GridBagConstraints();
        policyPanelConstraints.gridx = 0;
        policyPanelConstraints.fill = GridBagConstraints.BOTH;
        policyPanelConstraints.insets = new Insets(5, 5, 5, 5);
        policyPanelConstraints.weightx = 1.0D;
        policyPanelConstraints.weighty = 1.0D;
        policyPanelConstraints.gridy = 1;
        if (this.delegationPolicyPanel != null) {
            getPolicyPanel().remove(delegationPolicyPanel);
        }
        this.delegationPolicyPanel = CDSUIUtils.getPolicyPanel(policyType, false);
        getPolicyPanel().add(this.delegationPolicyPanel, policyPanelConstraints);
        this.delegationPolicyPanel.setPolicy(record.getDelegationPolicy());
    }


    private String getDateString(long l) {
        if (l <= 0) {
            return "";
        } else {
            Calendar c = new GregorianCalendar();
            c.setTimeInMillis(l);
            return c.getTime().toString();
        }
    }


    /**
     * This method initializes this
     */
    private void initialize() {
        this.setContentPane(getJContentPane());
        this.setTitle("Delegate Credential [" + record.getGridIdentity() + "]");
        this.setSize(600, 600);

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
            GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
            gridBagConstraints4.fill = GridBagConstraints.BOTH;
            gridBagConstraints4.gridy = 1;
            gridBagConstraints4.weightx = 1.0;
            gridBagConstraints4.weighty = 1.0D;
            gridBagConstraints4.insets = new Insets(5, 5, 5, 5);
            gridBagConstraints4.gridx = 0;
            GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
            gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints1.gridy = 0;
            gridBagConstraints1.weightx = 1.0D;
            gridBagConstraints1.anchor = java.awt.GridBagConstraints.NORTH;
            gridBagConstraints1.gridx = 0;
            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            mainPanel = new JPanel();
            mainPanel.setLayout(new GridBagLayout());
            gridBagConstraints2.gridx = 0;
            gridBagConstraints2.gridy = 2;
            gridBagConstraints2.insets = new Insets(0, 0, 0, 0);
            gridBagConstraints2.anchor = java.awt.GridBagConstraints.SOUTH;
            gridBagConstraints2.fill = java.awt.GridBagConstraints.HORIZONTAL;
            mainPanel.add(getProgressPanel(), gridBagConstraints2);
            mainPanel.add(getTitlePanel(), gridBagConstraints1);
            mainPanel.add(getJTabbedPane(), gridBagConstraints4);
        }
        return mainPanel;
    }


    /**
     * This method initializes jPanel
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
     * This method initializes manageUser
     * 
     * @return javax.swing.JButton
     */
    private JButton getUpdateStatus() {
        if (updateStatus == null) {
            updateStatus = new JButton();
            updateStatus.setText("Update Status");
            updateStatus.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    Runner runner = new Runner() {
                        public void execute() {
                            updateDelegationStatus();
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
        return updateStatus;
    }


    private void updateDelegationStatus() {
        try {
            disableAllButtons();
            getProgressPanel().showProgress("Updating...");
            DelegationAdminClient client = this.session.getAdminClient();
            client.updateDelegationStatus(record.getDelegationIdentifier(), getStatus().getDelegationStatus());
            getProgressPanel().stopProgress("Delegated credential successfully updated.");
            enableAllButtons();
        } catch (Exception e) {
            getProgressPanel().stopProgress("Error");
            enableAllButtons();
            ErrorDialog.showError(e);
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
            jTabbedPane.addTab(INFO_PANEL, null, getInfoPanel(), null);
            jTabbedPane.addTab(POLICY_PANEL, null, getPolicyPanel(), null);
            jTabbedPane.addTab(CERTIFICATE_PANEL, null, getCertificatePanel(), null);
            jTabbedPane.addTab(AUDITING_PANEL, null, getAuditPanel(), null);
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
            GridBagConstraints gridBagConstraints23 = new GridBagConstraints();
            gridBagConstraints23.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints23.gridy = 6;
            gridBagConstraints23.weightx = 1.0;
            gridBagConstraints23.anchor = GridBagConstraints.WEST;
            gridBagConstraints23.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints23.gridx = 1;
            GridBagConstraints gridBagConstraints22 = new GridBagConstraints();
            gridBagConstraints22.gridx = 0;
            gridBagConstraints22.anchor = GridBagConstraints.WEST;
            gridBagConstraints22.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints22.gridy = 6;
            jLabel6 = new JLabel();
            jLabel6.setText("Issued Credential Path Length");
            GridBagConstraints gridBagConstraints19 = new GridBagConstraints();
            gridBagConstraints19.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints19.gridy = 7;
            gridBagConstraints19.weightx = 1.0;
            gridBagConstraints19.anchor = GridBagConstraints.WEST;
            gridBagConstraints19.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints19.gridx = 1;
            GridBagConstraints gridBagConstraints18 = new GridBagConstraints();
            gridBagConstraints18.gridx = 0;
            gridBagConstraints18.anchor = GridBagConstraints.WEST;
            gridBagConstraints18.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints18.gridy = 7;
            jLabel5 = new JLabel();
            jLabel5.setText("Delegation Status");
            GridBagConstraints gridBagConstraints17 = new GridBagConstraints();
            gridBagConstraints17.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints17.gridy = 5;
            gridBagConstraints17.weightx = 1.0;
            gridBagConstraints17.anchor = GridBagConstraints.WEST;
            gridBagConstraints17.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints17.gridx = 1;
            GridBagConstraints gridBagConstraints15 = new GridBagConstraints();
            gridBagConstraints15.gridx = 0;
            gridBagConstraints15.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints15.anchor = GridBagConstraints.WEST;
            gridBagConstraints15.gridy = 5;
            jLabel4 = new JLabel();
            jLabel4.setText("Issued Credential Lifetime");
            GridBagConstraints gridBagConstraints14 = new GridBagConstraints();
            gridBagConstraints14.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints14.gridy = 4;
            gridBagConstraints14.weightx = 1.0;
            gridBagConstraints14.anchor = GridBagConstraints.WEST;
            gridBagConstraints14.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints14.gridx = 1;
            GridBagConstraints gridBagConstraints13 = new GridBagConstraints();
            gridBagConstraints13.gridx = 0;
            gridBagConstraints13.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints13.anchor = GridBagConstraints.WEST;
            gridBagConstraints13.gridy = 4;
            jLabel3 = new JLabel();
            jLabel3.setText("Expires On");
            GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
            gridBagConstraints12.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints12.gridy = 3;
            gridBagConstraints12.weightx = 1.0;
            gridBagConstraints12.anchor = GridBagConstraints.WEST;
            gridBagConstraints12.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints12.gridx = 1;
            GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
            gridBagConstraints11.gridx = 0;
            gridBagConstraints11.anchor = GridBagConstraints.WEST;
            gridBagConstraints11.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints11.gridy = 3;
            jLabel2 = new JLabel();
            jLabel2.setText("Approved On");
            GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
            gridBagConstraints10.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints10.gridy = 2;
            gridBagConstraints10.weightx = 1.0;
            gridBagConstraints10.anchor = GridBagConstraints.WEST;
            gridBagConstraints10.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints10.gridx = 1;
            GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
            gridBagConstraints9.gridx = 0;
            gridBagConstraints9.anchor = GridBagConstraints.WEST;
            gridBagConstraints9.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints9.gridy = 2;
            jLabel1 = new JLabel();
            jLabel1.setText("Initiated On");
            GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
            gridBagConstraints8.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints8.gridy = 1;
            gridBagConstraints8.weightx = 1.0;
            gridBagConstraints8.anchor = GridBagConstraints.WEST;
            gridBagConstraints8.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints8.gridx = 1;
            GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
            gridBagConstraints7.gridx = 0;
            gridBagConstraints7.anchor = GridBagConstraints.WEST;
            gridBagConstraints7.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints7.gridy = 1;
            jLabel = new JLabel();
            jLabel.setText("Delegation Identifier");
            GridBagConstraints gridBagConstraints16 = new GridBagConstraints();
            gridBagConstraints16.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints16.gridy = 7;
            gridBagConstraints16.weightx = 1.0;
            gridBagConstraints16.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints16.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints16.gridx = 1;
            GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
            gridBagConstraints6.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints6.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints6.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints6.gridx = 1;
            gridBagConstraints6.gridy = 0;
            gridBagConstraints6.weightx = 1.0;
            GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
            gridBagConstraints5.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints5.gridy = 0;
            gridBagConstraints5.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints5.gridx = 0;
            gridIdLabel = new JLabel();
            gridIdLabel.setText("Grid Identity");
            jPanel1 = new JPanel();
            jPanel1.setName(INFO_PANEL);
            jPanel1.setLayout(new GridBagLayout());
            jPanel1.add(gridIdLabel, gridBagConstraints5);
            jPanel1.add(getGridIdentity(), gridBagConstraints6);
            jPanel1.add(jLabel, gridBagConstraints7);
            jPanel1.add(getDelegationIdentifier(), gridBagConstraints8);
            jPanel1.add(jLabel1, gridBagConstraints9);
            jPanel1.add(getInitiated(), gridBagConstraints10);
            jPanel1.add(jLabel2, gridBagConstraints11);
            jPanel1.add(getApproved(), gridBagConstraints12);
            jPanel1.add(jLabel3, gridBagConstraints13);
            jPanel1.add(getExpires(), gridBagConstraints14);
            jPanel1.add(jLabel4, gridBagConstraints15);
            jPanel1.add(getLifetime(), gridBagConstraints17);
            jPanel1.add(jLabel5, gridBagConstraints18);
            jPanel1.add(getStatus(), gridBagConstraints19);
            jPanel1.add(jLabel6, gridBagConstraints22);
            jPanel1.add(getIssuedCredentialPathLength(), gridBagConstraints23);
        }
        return jPanel1;
    }


    /**
     * This method initializes titlePanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getTitlePanel() {
        if (titlePanel == null) {
            titlePanel = new TitlePanel("Delegated Credential Record", record.getGridIdentity());
        }
        return titlePanel;
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
            infoPanel.add(getInfoButtonPanel(), BorderLayout.SOUTH);
        }
        return infoPanel;
    }


    /**
     * This method initializes gridIdentity
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getGridIdentity() {
        if (gridIdentity == null) {
            gridIdentity = new JTextField();
            gridIdentity.setEditable(false);
        }
        return gridIdentity;
    }


    /**
     * This method initializes certificatePanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getCertificatePanel() {
        if (certificatePanel == null) {
            GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
            gridBagConstraints21.gridx = 0;
            gridBagConstraints21.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints21.gridy = 1;
            GridBagConstraints gridBagConstraints20 = new GridBagConstraints();
            gridBagConstraints20.fill = GridBagConstraints.BOTH;
            gridBagConstraints20.weighty = 1.0;
            gridBagConstraints20.gridx = 0;
            gridBagConstraints20.gridy = 0;
            gridBagConstraints20.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints20.weightx = 1.0;
            certificatePanel = new JPanel();
            certificatePanel.setLayout(new GridBagLayout());
            certificatePanel.add(getJScrollPane(), gridBagConstraints20);
            certificatePanel.setBorder(BorderFactory.createTitledBorder(null, "Certificate Chain",
                TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, LookAndFeel
                    .getPanelLabelColor()));
            certificatePanel.add(getViewCertificate(), gridBagConstraints21);
        }
        return certificatePanel;
    }


    /**
     * This method initializes delegationIdentifier
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getDelegationIdentifier() {
        if (delegationIdentifier == null) {
            delegationIdentifier = new JTextField();
            delegationIdentifier.setEditable(false);
        }
        return delegationIdentifier;
    }


    /**
     * This method initializes initiated
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getInitiated() {
        if (initiated == null) {
            initiated = new JTextField();
            initiated.setEditable(false);
        }
        return initiated;
    }


    /**
     * This method initializes approved
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getApproved() {
        if (approved == null) {
            approved = new JTextField();
            approved.setEditable(false);
        }
        return approved;
    }


    /**
     * This method initializes expires
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getExpires() {
        if (expires == null) {
            expires = new JTextField();
            expires.setEditable(false);
        }
        return expires;
    }


    /**
     * This method initializes lifetime
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getLifetime() {
        if (lifetime == null) {
            lifetime = new JTextField();
            lifetime.setEditable(false);
        }
        return lifetime;
    }


    /**
     * This method initializes status
     * 
     * @return javax.swing.JComboBox
     */
    private DelegationStatusComboBox getStatus() {
        if (status == null) {
            status = new DelegationStatusComboBox(false);
        }
        return status;
    }


    /**
     * This method initializes jScrollPane
     * 
     * @return javax.swing.JScrollPane
     */
    private JScrollPane getJScrollPane() {
        if (jScrollPane == null) {
            jScrollPane = new JScrollPane();
            jScrollPane.setViewportView(getCertificateChainTable());
        }
        return jScrollPane;
    }


    /**
     * This method initializes certificateChainTable
     * 
     * @return javax.swing.JTable
     */
    private CertificateChainTable getCertificateChainTable() {
        if (certificateChainTable == null) {
            certificateChainTable = new CertificateChainTable();
        }
        return certificateChainTable;
    }


    /**
     * This method initializes viewCertificate
     * 
     * @return javax.swing.JButton
     */
    private JButton getViewCertificate() {
        if (viewCertificate == null) {
            viewCertificate = new JButton();
            viewCertificate.setText("View Certificate");
            viewCertificate.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    getCertificateChainTable().doubleClick();
                }
            });
        }
        return viewCertificate;
    }


    /**
     * This method initializes policyPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getPolicyPanel() {
        if (policyPanel == null) {
            GridBagConstraints gridBagConstraints24 = new GridBagConstraints();
            gridBagConstraints24.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints24.gridy = 0;
            gridBagConstraints24.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints24.anchor = GridBagConstraints.NORTH;
            gridBagConstraints24.weightx = 1.0D;
            gridBagConstraints24.gridx = 0;
            policyPanel = new JPanel();
            policyPanel.setLayout(new GridBagLayout());
            policyPanel.add(getPolicyTypePanel(), gridBagConstraints24);
        }
        return policyPanel;
    }


    /**
     * This method initializes issuedCredentialPathLength
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getIssuedCredentialPathLength() {
        if (issuedCredentialPathLength == null) {
            issuedCredentialPathLength = new JTextField();
            issuedCredentialPathLength.setEditable(false);
        }
        return issuedCredentialPathLength;
    }


    /**
     * This method initializes policyTypePanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getPolicyTypePanel() {
        if (policyTypePanel == null) {
            GridBagConstraints gridBagConstraints26 = new GridBagConstraints();
            gridBagConstraints26.gridx = 0;
            gridBagConstraints26.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints26.gridy = 0;
            GridBagConstraints gridBagConstraints25 = new GridBagConstraints();
            gridBagConstraints25.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints25.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints25.gridx = 1;
            gridBagConstraints25.gridy = 0;
            gridBagConstraints25.weightx = 1.0;
            jLabel7 = new JLabel();
            jLabel7.setText("Delegation Policy");
            jLabel7.setFont(new Font("Dialog", Font.BOLD, 14));
            policyTypePanel = new JPanel();
            policyTypePanel.setLayout(new GridBagLayout());
            policyTypePanel.add(jLabel7, gridBagConstraints26);
            policyTypePanel.add(getDelegationPolicyType(), gridBagConstraints25);
        }
        return policyTypePanel;
    }


    /**
     * This method initializes delegationPolicyType
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getDelegationPolicyType() {
        if (delegationPolicyType == null) {
            delegationPolicyType = new JTextField();
            delegationPolicyType.setEditable(false);
        }
        return delegationPolicyType;
    }


    /**
     * This method initializes infoButtonPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getInfoButtonPanel() {
        if (infoButtonPanel == null) {
            GridBagConstraints gridBagConstraints29 = new GridBagConstraints();
            gridBagConstraints29.gridx = 0;
            gridBagConstraints29.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints29.gridy = 0;
            infoButtonPanel = new JPanel();
            infoButtonPanel.setLayout(new GridBagLayout());
            infoButtonPanel.add(getUpdateStatus(), gridBagConstraints29);
        }
        return infoButtonPanel;
    }


    /**
     * This method initializes auditPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getAuditPanel() {
        if (auditPanel == null) {
            GridBagConstraints gridBagConstraints50 = new GridBagConstraints();
            gridBagConstraints50.gridx = 0;
            gridBagConstraints50.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints50.weightx = 1.0D;
            gridBagConstraints50.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints50.gridy = 3;
            GridBagConstraints gridBagConstraints47 = new GridBagConstraints();
            gridBagConstraints47.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints47.gridy = 1;
            gridBagConstraints47.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints47.weightx = 1.0D;
            gridBagConstraints47.gridx = 0;
            GridBagConstraints gridBagConstraints32 = new GridBagConstraints();
            gridBagConstraints32.gridx = 0;
            gridBagConstraints32.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints32.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints32.weightx = 1.0D;
            gridBagConstraints32.gridy = 0;
            GridBagConstraints gridBagConstraints30 = new GridBagConstraints();
            gridBagConstraints30.fill = GridBagConstraints.BOTH;
            gridBagConstraints30.gridy = 2;
            gridBagConstraints30.weightx = 1.0;
            gridBagConstraints30.weighty = 1.0;
            gridBagConstraints30.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints30.gridx = 0;
            auditPanel = new JPanel();
            auditPanel.setLayout(new GridBagLayout());
            auditPanel.add(getJScrollPane1(), gridBagConstraints30);
            auditPanel.add(getSearchPanel(), gridBagConstraints32);
            auditPanel.add(getSearchButtonPanel(), gridBagConstraints47);
            auditPanel.add(getAuditingButtonPanel(), gridBagConstraints50);
        }
        return auditPanel;
    }


    /**
     * This method initializes jScrollPane1
     * 
     * @return javax.swing.JScrollPane
     */
    private JScrollPane getJScrollPane1() {
        if (jScrollPane1 == null) {
            jScrollPane1 = new JScrollPane();
            jScrollPane1.setViewportView(getAuditRecords());
        }
        return jScrollPane1;
    }


    /**
     * This method initializes auditRecords
     * 
     * @return javax.swing.JTable
     */
    private DelegatedCredentialAuditRecordTable getAuditRecords() {
        if (auditRecords == null) {
            auditRecords = new DelegatedCredentialAuditRecordTable();
        }
        return auditRecords;
    }


    /**
     * This method initializes searchPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getSearchPanel() {
        if (searchPanel == null) {
            GridBagConstraints gridBagConstraints40 = new GridBagConstraints();
            gridBagConstraints40.gridx = 0;
            gridBagConstraints40.weightx = 1.0D;
            gridBagConstraints40.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints40.gridy = 1;
            GridBagConstraints gridBagConstraints37 = new GridBagConstraints();
            gridBagConstraints37.gridx = 0;
            gridBagConstraints37.insets = new Insets(0, 0, 0, 0);
            gridBagConstraints37.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints37.gridy = 2;
            GridBagConstraints gridBagConstraints34 = new GridBagConstraints();
            gridBagConstraints34.gridx = 0;
            gridBagConstraints34.anchor = GridBagConstraints.CENTER;
            gridBagConstraints34.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints34.insets = new Insets(0, 0, 0, 0);
            gridBagConstraints34.weightx = 1.0D;
            gridBagConstraints34.gridy = 0;
            jLabel8 = new JLabel();
            jLabel8.setText("Source Identity");
            searchPanel = new JPanel();
            searchPanel.setLayout(new GridBagLayout());
            searchPanel.setBorder(BorderFactory.createTitledBorder(null, "Audting Search Criteria",
                TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, LookAndFeel
                    .getPanelLabelColor()));
            searchPanel.add(getIdentityPanel(), gridBagConstraints34);
            searchPanel.add(getAuditTypePanel(), gridBagConstraints37);
            searchPanel.add(getDateRangePanel(), gridBagConstraints40);
        }
        return searchPanel;
    }


    /**
     * This method initializes identityPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getIdentityPanel() {
        if (identityPanel == null) {
            GridBagConstraints gridBagConstraints36 = new GridBagConstraints();
            gridBagConstraints36.gridx = 2;
            gridBagConstraints36.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints36.anchor = GridBagConstraints.WEST;
            gridBagConstraints36.gridy = 0;
            GridBagConstraints gridBagConstraints35 = new GridBagConstraints();
            gridBagConstraints35.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints35.gridx = 1;
            gridBagConstraints35.gridy = 0;
            gridBagConstraints35.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints35.anchor = GridBagConstraints.WEST;
            gridBagConstraints35.weightx = 1.0;
            GridBagConstraints gridBagConstraints33 = new GridBagConstraints();
            gridBagConstraints33.gridx = 0;
            gridBagConstraints33.anchor = GridBagConstraints.WEST;
            gridBagConstraints33.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints33.gridy = 0;
            identityPanel = new JPanel();
            identityPanel.setLayout(new GridBagLayout());
            identityPanel.add(jLabel8, gridBagConstraints33);
            identityPanel.add(getSourceIdentity(), gridBagConstraints35);
            identityPanel.add(getFindSource(), gridBagConstraints36);
        }
        return identityPanel;
    }


    /**
     * This method initializes sourceIdentity
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getSourceIdentity() {
        if (sourceIdentity == null) {
            sourceIdentity = new JTextField();
        }
        return sourceIdentity;
    }


    /**
     * This method initializes findSource
     * 
     * @return javax.swing.JButton
     */
    private JButton getFindSource() {
        if (findSource == null) {
            findSource = new JButton();
            findSource.setText("Find");
            findSource.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    UserSearchDialog dialog = new UserSearchDialog();
                    dialog.setModal(true);
                    GridApplication.getContext().showDialog(dialog);
                    if (dialog.getSelectedUser() != null) {
                        getGridIdentity().setText(dialog.getSelectedUser());
                    }
                }
            });
        }
        return findSource;
    }


    /**
     * This method initializes auditTypePanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getAuditTypePanel() {
        if (auditTypePanel == null) {
            GridBagConstraints gridBagConstraints39 = new GridBagConstraints();
            gridBagConstraints39.gridx = 0;
            gridBagConstraints39.anchor = GridBagConstraints.WEST;
            gridBagConstraints39.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints39.gridy = 0;
            GridBagConstraints gridBagConstraints38 = new GridBagConstraints();
            gridBagConstraints38.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints38.anchor = GridBagConstraints.WEST;
            gridBagConstraints38.gridx = 1;
            gridBagConstraints38.gridy = 0;
            gridBagConstraints38.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints38.weightx = 1.0;
            jLabel9 = new JLabel();
            jLabel9.setText("Audit Event");
            auditTypePanel = new JPanel();
            auditTypePanel.setLayout(new GridBagLayout());
            auditTypePanel.add(jLabel9, gridBagConstraints39);
            auditTypePanel.add(getEventType(), gridBagConstraints38);
        }
        return auditTypePanel;
    }


    /**
     * This method initializes eventType
     * 
     * @return javax.swing.JComboBox
     */
    private DelegatedCredentialEventComboBox getEventType() {
        if (eventType == null) {
            eventType = new DelegatedCredentialEventComboBox();
        }
        return eventType;
    }


    /**
     * This method initializes dateRangePanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getDateRangePanel() {
        if (dateRangePanel == null) {
            GridBagConstraints gridBagConstraints46 = new GridBagConstraints();
            gridBagConstraints46.gridx = 5;
            gridBagConstraints46.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints46.anchor = GridBagConstraints.WEST;
            gridBagConstraints46.gridy = 0;
            GridBagConstraints gridBagConstraints45 = new GridBagConstraints();
            gridBagConstraints45.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints45.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints45.gridx = 4;
            gridBagConstraints45.gridy = 0;
            gridBagConstraints45.anchor = GridBagConstraints.WEST;
            gridBagConstraints45.weightx = 1.0;
            GridBagConstraints gridBagConstraints44 = new GridBagConstraints();
            gridBagConstraints44.gridx = 2;
            gridBagConstraints44.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints44.anchor = GridBagConstraints.WEST;
            gridBagConstraints44.gridy = 0;
            GridBagConstraints gridBagConstraints43 = new GridBagConstraints();
            gridBagConstraints43.gridx = 3;
            gridBagConstraints43.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints43.gridy = 0;
            jLabel11 = new JLabel();
            jLabel11.setText("End Date");
            GridBagConstraints gridBagConstraints42 = new GridBagConstraints();
            gridBagConstraints42.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints42.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints42.gridx = 1;
            gridBagConstraints42.gridy = 0;
            gridBagConstraints42.anchor = GridBagConstraints.WEST;
            gridBagConstraints42.weightx = 1.0;
            GridBagConstraints gridBagConstraints41 = new GridBagConstraints();
            gridBagConstraints41.gridx = 0;
            gridBagConstraints41.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints41.anchor = GridBagConstraints.WEST;
            gridBagConstraints41.gridy = 0;
            jLabel10 = new JLabel();
            jLabel10.setText("Start Date");
            dateRangePanel = new JPanel();
            dateRangePanel.setLayout(new GridBagLayout());
            dateRangePanel.add(jLabel10, gridBagConstraints41);
            dateRangePanel.add(getStartDate(), gridBagConstraints42);
            dateRangePanel.add(getStartDateButton(), gridBagConstraints44);
            dateRangePanel.add(jLabel11, gridBagConstraints43);
            dateRangePanel.add(getEndDate(), gridBagConstraints45);
            dateRangePanel.add(getEndDateButton(), gridBagConstraints46);
        }
        return dateRangePanel;
    }


    /**
     * This method initializes startDate
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getStartDate() {
        if (startDate == null) {
            startDate = new JTextField();
            startDate.setEditable(false);
        }
        return startDate;
    }


    /**
     * This method initializes startDateButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getStartDateButton() {
        if (startDateButton == null) {
            startDateButton = new JButton();
            startDateButton.setText("Select");
            startDateButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    SelectDateDialog dialog = new SelectDateDialog(true);
                    dialog.setModal(true);
                    GridApplication.getContext().showDialog(dialog);
                    Calendar c = dialog.getDate();
                    if (c != null) {
                        searchStartDate = c;
                        getStartDate().setText(formatDate(searchStartDate));
                    }
                }
            });
        }
        return startDateButton;
    }


    /**
     * This method initializes endDate
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getEndDate() {
        if (endDate == null) {
            endDate = new JTextField();
            endDate.setEditable(false);
            endDate.setText("");
        }
        return endDate;
    }


    /**
     * This method initializes endDateButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getEndDateButton() {
        if (endDateButton == null) {
            endDateButton = new JButton();
            endDateButton.setText("Select");
            endDateButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    SelectDateDialog dialog = new SelectDateDialog(false);
                    dialog.setModal(true);
                    GridApplication.getContext().showDialog(dialog);
                    Calendar c = dialog.getDate();
                    if (c != null) {
                        searchEndDate = c;
                        getEndDate().setText(formatDate(searchEndDate));
                    }
                }
            });
        }
        return endDateButton;
    }


    private String formatDate(Calendar c) {
        StringBuffer sb = new StringBuffer();
        int month = c.get(Calendar.MONTH) + 1;
        if (month < 10) {
            sb.append("0" + month);
        } else {
            sb.append(month);
        }

        sb.append("/");

        int day = c.get(Calendar.DAY_OF_MONTH);
        if (day < 10) {
            sb.append("0" + day);
        } else {
            sb.append(day);
        }

        sb.append("/");

        int year = c.get(Calendar.YEAR);
        sb.append(year);

        sb.append("  @  ");

        int hour = c.get(Calendar.HOUR);
        if (hour == 0) {
            hour = 12;
        }
        if (hour < 10) {
            sb.append("0" + hour);
        } else {
            sb.append(hour);
        }

        sb.append(":");

        int minute = c.get(Calendar.MINUTE);
        if (minute < 10) {
            sb.append("0" + minute);
        } else {
            sb.append(minute);
        }
        if (c.get(Calendar.AM_PM) == Calendar.AM) {
            sb.append(" AM");
        } else {
            sb.append(" PM");
        }
        return sb.toString();
    }


    /**
     * This method initializes searchButtonPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getSearchButtonPanel() {
        if (searchButtonPanel == null) {
            searchButtonPanel = new JPanel();
            searchButtonPanel.setLayout(new FlowLayout());
            searchButtonPanel.add(getSearchButton(), null);
            searchButtonPanel.add(getClear(), null);
        }
        return searchButtonPanel;
    }


    private void disableAllButtons() {
        getSearchButton().setEnabled(false);
        getClear().setEnabled(false);
        getUpdateStatus().setEnabled(false);
        getViewCertificate().setEnabled(false);
        getFindSource().setEnabled(false);
        getStartDateButton().setEnabled(false);
        getEndDateButton().setEnabled(false);
        getViewAudtingRecord().setEnabled(false);
    }


    private void enableAllButtons() {
        getSearchButton().setEnabled(true);
        getClear().setEnabled(true);
        getUpdateStatus().setEnabled(true);
        getViewCertificate().setEnabled(true);
        getFindSource().setEnabled(true);
        getStartDateButton().setEnabled(true);
        getEndDateButton().setEnabled(true);
        getViewAudtingRecord().setEnabled(true);
    }


    private void performAudit() {
        disableAllButtons();
        getAuditRecords().clearTable();
        getProgressPanel().showProgress("Searching...");
        try {
            if ((searchStartDate != null) && (searchEndDate != null)) {
                if (searchStartDate.after(searchEndDate)) {
                    ErrorDialog.showError("The start date cannot be after the end date.");
                    getProgressPanel().stopProgress("Error");
                    enableAllButtons();
                    return;
                }
            }
            DelegatedCredentialAuditFilter f = new DelegatedCredentialAuditFilter();
            f.setDelegationIdentifier(this.record.getDelegationIdentifier());
            f.setSourceGridIdentity(Utils.clean(getSourceIdentity().getText()));
            f.setEvent(getEventType().getEvent());
            if (searchStartDate != null) {
                f.setStartDate(new Long(searchStartDate.getTimeInMillis()));
            }

            if (searchEndDate != null) {
                f.setEndDate(new Long(searchEndDate.getTimeInMillis()));
            }

            DelegationUserClient client = session.getUserClient();
            List<DelegatedCredentialAuditRecord> records = client.searchDelegatedCredentialAuditLog(f);
            this.getAuditRecords().addRecords(records);
            getProgressPanel().stopProgress(records.size() + " audit record(s) found");
            enableAllButtons();
        } catch (Exception f) {
            getProgressPanel().stopProgress("Error");
            enableAllButtons();
            ErrorDialog.showError(f);
            return;
        }
    }


    /**
     * This method initializes searchButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getSearchButton() {
        if (searchButton == null) {
            searchButton = new JButton();
            searchButton.setText("Search");
            searchButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    Runner runner = new Runner() {
                        public void execute() {
                            performAudit();
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
        return searchButton;
    }


    /**
     * This method initializes clear
     * 
     * @return javax.swing.JButton
     */
    private JButton getClear() {
        if (clear == null) {
            clear = new JButton();
            clear.setText("Clear");
            clear.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    getSourceIdentity().setText("");
                    getEventType().setToAny();
                    searchStartDate = null;
                    getStartDate().setText("");
                    searchEndDate = null;
                    getEndDate().setText("");
                }
            });
        }
        return clear;
    }


    /**
     * This method initializes auditingButtonPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getAuditingButtonPanel() {
        if (auditingButtonPanel == null) {
            auditingButtonPanel = new JPanel();
            auditingButtonPanel.setLayout(new GridBagLayout());
            auditingButtonPanel.add(getViewAudtingRecord(), new GridBagConstraints());
        }
        return auditingButtonPanel;
    }


    /**
     * This method initializes viewAudtingRecord
     * 
     * @return javax.swing.JButton
     */
    private JButton getViewAudtingRecord() {
        if (viewAudtingRecord == null) {
            viewAudtingRecord = new JButton();
            viewAudtingRecord.setText("View");
            viewAudtingRecord.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    try {
                        getAuditRecords().doubleClick();
                    } catch (Exception ex) {
                        ErrorDialog.showError(ex.getMessage(), ex);
                    }
                }
            });
        }
        return viewAudtingRecord;
    }

}
