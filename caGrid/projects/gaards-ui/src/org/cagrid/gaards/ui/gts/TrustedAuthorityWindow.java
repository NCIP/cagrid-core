package org.cagrid.gaards.ui.gts;

import gov.nih.nci.cagrid.common.Runner;
import gov.nih.nci.cagrid.gts.bean.TrustLevel;
import gov.nih.nci.cagrid.gts.bean.TrustLevels;
import gov.nih.nci.cagrid.gts.bean.TrustedAuthority;
import gov.nih.nci.cagrid.gts.client.GTSAdminClient;
import gov.nih.nci.cagrid.gts.client.GTSPublicClient;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import org.cagrid.gaards.pki.CertUtil;
import org.cagrid.gaards.ui.common.CRLPanel;
import org.cagrid.gaards.ui.common.CertificatePanel;
import org.cagrid.gaards.ui.common.ProgressPanel;
import org.cagrid.gaards.ui.common.TitlePanel;
import org.cagrid.grape.ApplicationComponent;
import org.cagrid.grape.GridApplication;
import org.cagrid.grape.LookAndFeel;
import org.cagrid.grape.utils.ErrorDialog;


/**
 * @author <A href="mailto:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A href="mailto:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A href="mailto:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @version $Id: ArgumentManagerTable.java,v 1.2 2004/10/15 16:35:16 langella
 *          Exp $
 */
public class TrustedAuthorityWindow extends ApplicationComponent {

    private static final long serialVersionUID = 1L;

    private JPanel jContentPane = null;

    private JTabbedPane taPanel = null;

    private JPanel propertiesPanel = null;

    private JLabel jLabel2 = null;

    private JTextField trustedAuthorityName = null;

    private JLabel jLabel3 = null;

    private JComboBox status = null;

    private JPanel propertiesNorthPanel = null;

    private JPanel buttonPanel = null;

    private JButton addButton = null;

    private CertificatePanel certificatePanel = null;

    private JButton importCertificate = null;

    private CRLPanel crlPanel = null;

    private JButton importCRL = null;

    private TrustedAuthorityRefresher refresher;

    private JLabel jLabel5 = null;

    private JTextField isAuthority = null;

    private JLabel jLabel6 = null;

    private JTextField authorityGTS = null;

    private JLabel jLabel7 = null;

    private JTextField sourceGTS = null;

    private JTextField expires = null;

    private JLabel jLabel9 = null;

    private JLabel jLabel10 = null;

    private JTextField lastUpdated = null;

    private final static int ADD = 0;

    private final static int UPDATE = 1;

    private final static int VIEW = 2;

    private int state;

    private JPanel trustLevels = null;

    private JScrollPane jScrollPane = null;

    private JPanel levels = null;

    private Map<String, JCheckBox> levelsMap = new HashMap<String, JCheckBox>();

    private JPanel titlePanel = null;

    private TrustedAuthority trustedAuthority;

    private GTSSession session;

    private ProgressPanel progressPanel = null;


    /**
     * This is the default constructor
     */
    public TrustedAuthorityWindow(GTSSession gts, TrustedAuthorityRefresher refresher) {
        super();
        this.refresher = refresher;
        this.session = gts;
        this.state = ADD;
        initialize();
        this.updateTrustLevels();
    }


    public TrustedAuthorityWindow(GTSSession gts, TrustedAuthority ta, TrustedAuthorityRefresher refresher)
        throws Exception {
        super();
        this.session = gts;
        this.trustedAuthority = ta;
        this.refresher = refresher;
        if (ta.getIsAuthority().equals(Boolean.FALSE)) {
            state = VIEW;
        } else {
            state = UPDATE;
        }
        initialize();
        this.updateTrustLevels();
        this.getTrustedAuthorityName().setText(ta.getName());
        ((StatusComboBox) this.getStatus()).setSelectedItem(ta.getStatus());
        if (ta.getTrustLevels() != null) {
            if (ta.getTrustLevels().getTrustLevel() != null) {
                String[] list = ta.getTrustLevels().getTrustLevel();
                for (int i = 0; i < list.length; i++) {
                    JCheckBox box = (JCheckBox) this.levelsMap.get(list[i]);
                    if (box != null) {
                        box.setSelected(true);
                    }
                }
            }
        }
        this.getIsAuthority().setText(ta.getIsAuthority().toString());
        this.getAuthorityGTS().setText(ta.getAuthorityGTS());
        this.getSourceGTS().setText(ta.getSourceGTS());
        if (ta.getExpires() <= 0) {
            getExpires().setText("Never");
        } else {
            Calendar c = new GregorianCalendar();
            c.setTimeInMillis(ta.getExpires());
            getExpires().setText(c.getTime().toString());
        }
        if (ta.getLastUpdated() <= 0) {
            getLastUpdated().setText("Unknown");
        } else {
            Calendar c = new GregorianCalendar();
            c.setTimeInMillis(ta.getLastUpdated());
            getLastUpdated().setText(c.getTime().toString());
        }
        this.getCertificatePanel().setCertificate(
            CertUtil.loadCertificate(ta.getCertificate().getCertificateEncodedString()));
        if (ta.getCRL() != null) {
            if (ta.getCRL().getCrlEncodedString() != null) {
                crlPanel.setCRL(CertUtil.loadCRL(ta.getCRL().getCrlEncodedString()));
            }
        }

    }


    /**
     * This method initializes this
     */
    private void initialize() {
        this.setSize(600, 500);
        this.setContentPane(getJContentPane());
        if (state == ADD) {
            this.setTitle("Add Trusted Authority");
            this.setFrameIcon(GTSLookAndFeel.getTrustedAuthorityIcon());
        } else if (state == UPDATE) {
            this.setTitle("View/Modify Trusted Authority");
            this.setFrameIcon(GTSLookAndFeel.getTrustedAuthorityIcon());
        } else {
            this.setTitle("View Trusted Authority");
            this.setFrameIcon(GTSLookAndFeel.getTrustedAuthorityIcon());
        }
    }


    private void updateTrustLevels() {
        levelsMap.clear();
        getLevels().removeAll();
        getLevels().setLayout(new GridBagLayout());
        try {
            GTSPublicClient client = this.session.getUserClient();
            TrustLevel[] newLevels = client.getTrustLevels();
            if (newLevels != null) {
                for (int i = 0; i < newLevels.length; i++) {
                    this.addLevel(newLevels[i].getName());
                }

            }

        } catch (Exception e) {
            ErrorDialog.showError("Error obtaining the levels of assurance from "
                + this.session.getHandle().getDisplayName() + ":\n" + e.getMessage());
        }

        invalidate();
        repaint();
    }


    /**
     * This method initializes jContentPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getJContentPane() {
        if (jContentPane == null) {
            GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
            gridBagConstraints12.gridx = 0;
            gridBagConstraints12.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints12.gridy = 2;
            GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
            gridBagConstraints1.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints1.gridx = 0;
            gridBagConstraints1.gridy = 1;
            gridBagConstraints1.ipady = 0;
            gridBagConstraints1.weightx = 1.0;
            gridBagConstraints1.weighty = 1.0;
            gridBagConstraints1.insets = new java.awt.Insets(5, 5, 5, 5);
            GridBagConstraints gridBagConstraints0 = new GridBagConstraints();
            gridBagConstraints0.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints0.gridx = 0;
            gridBagConstraints0.gridy = 0;
            gridBagConstraints0.ipady = 0;
            gridBagConstraints0.weightx = 1.0;
            gridBagConstraints0.weighty = 0;
            gridBagConstraints0.insets = new java.awt.Insets(2, 2, 2, 2);
            GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
            gridBagConstraints3.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints3.gridx = 0;
            gridBagConstraints3.gridy = 3;
            gridBagConstraints3.ipady = 0;
            gridBagConstraints3.weightx = 1.0;
            gridBagConstraints3.weighty = 0;
            gridBagConstraints3.insets = new java.awt.Insets(0, 0, 0, 0);
            jContentPane = new JPanel();
            jContentPane.setLayout(new GridBagLayout());
            jContentPane.add(getTaPanel(), gridBagConstraints1);
            jContentPane.add(getButtonPanel(), gridBagConstraints12);
            jContentPane.add(getTitlePanel(), gridBagConstraints0);
            jContentPane.add(getProgressPanel(), gridBagConstraints3);
        }
        return jContentPane;
    }


    /**
     * This method initializes taPanel
     * 
     * @return javax.swing.JTabbedPane
     */
    private JTabbedPane getTaPanel() {
        if (taPanel == null) {
            taPanel = new JTabbedPane();
            taPanel.addTab("Properties", null, getPropertiesNorthPanel(), null);
            taPanel.addTab("Level of Assurance", null, getTrustLevels(), null);
            taPanel.addTab("Certificate", null, getCertificatePanel(), null);
            taPanel.addTab("Certificate Revocation List", null, getCrlPanel(), null);
        }
        return taPanel;
    }


    /**
     * This method initializes propertiesPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getPropertiesPanel() {
        if (propertiesPanel == null) {
            GridBagConstraints gridBagConstraints23 = new GridBagConstraints();
            gridBagConstraints23.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints23.gridy = 6;
            gridBagConstraints23.weightx = 1.0;
            gridBagConstraints23.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints23.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints23.gridx = 1;
            GridBagConstraints gridBagConstraints22 = new GridBagConstraints();
            gridBagConstraints22.gridx = 0;
            gridBagConstraints22.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints22.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints22.gridy = 6;
            jLabel10 = new JLabel();
            jLabel10.setText("Last Updated");
            GridBagConstraints gridBagConstraints20 = new GridBagConstraints();
            gridBagConstraints20.gridx = 0;
            gridBagConstraints20.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints20.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints20.gridy = 5;
            jLabel9 = new JLabel();
            jLabel9.setText("Expires");
            jLabel9.setName("Expires");
            GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
            gridBagConstraints21.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints21.gridy = 5;
            gridBagConstraints21.weightx = 1.0;
            gridBagConstraints21.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints21.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints21.gridx = 1;
            GridBagConstraints gridBagConstraints19 = new GridBagConstraints();
            gridBagConstraints19.gridx = 0;
            gridBagConstraints19.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints19.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints19.gridy = 6;
            GridBagConstraints gridBagConstraints18 = new GridBagConstraints();
            gridBagConstraints18.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints18.gridy = 4;
            gridBagConstraints18.weightx = 1.0;
            gridBagConstraints18.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints18.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints18.gridx = 1;
            GridBagConstraints gridBagConstraints17 = new GridBagConstraints();
            gridBagConstraints17.gridx = 0;
            gridBagConstraints17.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints17.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints17.gridy = 4;
            jLabel7 = new JLabel();
            jLabel7.setText("Source GTS");
            GridBagConstraints gridBagConstraints16 = new GridBagConstraints();
            gridBagConstraints16.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints16.gridy = 3;
            gridBagConstraints16.weightx = 1.0;
            gridBagConstraints16.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints16.gridx = 1;
            GridBagConstraints gridBagConstraints15 = new GridBagConstraints();
            gridBagConstraints15.gridx = 0;
            gridBagConstraints15.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints15.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints15.gridy = 3;
            jLabel6 = new JLabel();
            jLabel6.setText("Authority GTS");
            GridBagConstraints gridBagConstraints14 = new GridBagConstraints();
            gridBagConstraints14.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints14.gridy = 2;
            gridBagConstraints14.weightx = 1.0;
            gridBagConstraints14.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints14.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints14.gridx = 1;
            GridBagConstraints gridBagConstraints13 = new GridBagConstraints();
            gridBagConstraints13.gridx = 0;
            gridBagConstraints13.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints13.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints13.gridy = 2;
            jLabel5 = new JLabel();
            jLabel5.setText("Is Authority");
            GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
            gridBagConstraints9.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints9.gridy = 1;
            gridBagConstraints9.weightx = 1.0;
            gridBagConstraints9.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints9.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints9.gridx = 1;
            GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
            gridBagConstraints8.gridx = 0;
            gridBagConstraints8.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints8.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints8.gridy = 1;
            jLabel3 = new JLabel();
            jLabel3.setText("Status");
            GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
            gridBagConstraints7.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints7.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints7.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints7.weightx = 1.0;
            GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
            gridBagConstraints6.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints6.gridy = 0;
            gridBagConstraints6.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints6.gridx = 0;
            jLabel2 = new JLabel();
            jLabel2.setText("Trusted Authority Name");
            propertiesPanel = new JPanel();
            propertiesPanel.setLayout(new GridBagLayout());
            propertiesPanel.add(jLabel2, gridBagConstraints6);
            propertiesPanel.add(getTrustedAuthorityName(), gridBagConstraints7);
            propertiesPanel.add(jLabel3, gridBagConstraints8);
            propertiesPanel.add(getStatus(), gridBagConstraints9);
            if (state != ADD) {
                propertiesPanel.add(jLabel5, gridBagConstraints13);
                propertiesPanel.add(getIsAuthority(), gridBagConstraints14);
                propertiesPanel.add(jLabel6, gridBagConstraints15);
                propertiesPanel.add(getAuthorityGTS(), gridBagConstraints16);
                propertiesPanel.add(jLabel7, gridBagConstraints17);
                propertiesPanel.add(getSourceGTS(), gridBagConstraints18);
                propertiesPanel.add(getExpires(), gridBagConstraints21);
                propertiesPanel.add(jLabel9, gridBagConstraints20);
                propertiesPanel.add(jLabel10, gridBagConstraints22);
                propertiesPanel.add(getLastUpdated(), gridBagConstraints23);
            }
        }
        return propertiesPanel;
    }


    /**
     * This method initializes trustedAuthorityName
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getTrustedAuthorityName() {
        if (trustedAuthorityName == null) {
            trustedAuthorityName = new JTextField();
            trustedAuthorityName.setEditable(false);
        }
        return trustedAuthorityName;
    }


    /**
     * This method initializes status
     * 
     * @return javax.swing.JComboBox
     */
    private JComboBox getStatus() {
        if (status == null) {
            status = new StatusComboBox();
            if (state == VIEW) {
                this.status.setEnabled(false);
            }
        }
        return status;
    }


    /**
     * This method initializes propertiesNorthPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getPropertiesNorthPanel() {
        if (propertiesNorthPanel == null) {
            propertiesNorthPanel = new JPanel();
            propertiesNorthPanel.setLayout(new BorderLayout());
            propertiesNorthPanel.add(getPropertiesPanel(), java.awt.BorderLayout.NORTH);
        }
        return propertiesNorthPanel;
    }


    /**
     * This method initializes buttonPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getButtonPanel() {
        if (buttonPanel == null) {
            buttonPanel = new JPanel();
            if (state == ADD) {
                buttonPanel.add(getAddButton(), null);
                buttonPanel.add(getImportCertificate(), null);
            }

            if (state == UPDATE) {
                buttonPanel.add(getAddButton(), null);
            }

            if (state != VIEW) {
                buttonPanel.add(getImportCRL(), null);
            }
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
            if (state == ADD) {
                addButton.setText("Add");
                addButton.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent e) {
                        Runner runner = new Runner() {
                            public void execute() {
                                addTrustedAuthority();
                            }
                        };
                        try {
                            GridApplication.getContext().executeInBackground(runner);
                        } catch (Exception t) {
                            t.printStackTrace();
                        }
                    }

                });
            } else {
                addButton.setText("Update");
                addButton.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent e) {
                        Runner runner = new Runner() {
                            public void execute() {
                                updateTrustedAuthority();
                            }
                        };
                        try {
                            GridApplication.getContext().executeInBackground(runner);
                        } catch (Exception t) {
                            t.printStackTrace();
                        }
                    }
                });
            }

        }
        return addButton;
    }


    /**
     * This method initializes certificatePanel
     * 
     * @return javax.swing.JPanel
     */
    private CertificatePanel getCertificatePanel() {
        if (certificatePanel == null) {
            certificatePanel = new CertificatePanel();
            certificatePanel.setAllowExport(false);
            certificatePanel.setAllowImport(false);
        }
        return certificatePanel;
    }


    /**
     * This method initializes importCertificate
     * 
     * @return javax.swing.JButton
     */
    private JButton getImportCertificate() {
        if (importCertificate == null) {
            importCertificate = new JButton();
            importCertificate.setText("Import Certificate");
            importCertificate.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    importCertificate();
                }
            });
        }
        return importCertificate;
    }


    private void importCertificate() {

        JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int returnVal = fc.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            try {
                X509Certificate certificate = CertUtil
                    .loadCertificate(new File(fc.getSelectedFile().getAbsolutePath()));
                certificatePanel.clearCertificate();
                crlPanel.clearCRL();
                certificatePanel.setCertificate(certificate);
                this.getTrustedAuthorityName().setText(certificate.getSubjectDN().getName());
            } catch (Exception ex) {
                ErrorDialog.showError(ex);
            }
        }

    }


    private void importCRL() {
        crlPanel.clearCRL();
        X509Certificate cert = certificatePanel.getCertificate();
        if (cert == null) {
            ErrorDialog.showError("You must import a certificate before importing a CRL");

        }
        JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int returnVal = fc.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            try {
                X509CRL crl = CertUtil.loadCRL(new File(fc.getSelectedFile().getAbsolutePath()));
                try {
                    crl.verify(cert.getPublicKey());
                } catch (Exception crle) {
                    ErrorDialog
                        .showError("Error verifying CRL, the CRL must be issued and signed by same key is the Trusted Authority's Certificate");
                }
                crlPanel.setCRL(crl);
            } catch (Exception ex) {
                ErrorDialog.showError(ex);
            }
        }

    }


    /**
     * This method initializes crlPanel
     * 
     * @return javax.swing.JPanel
     */
    private CRLPanel getCrlPanel() {
        if (crlPanel == null) {
            crlPanel = new CRLPanel();
        }
        return crlPanel;
    }


    /**
     * This method initializes importCRL
     * 
     * @return javax.swing.JButton
     */
    private JButton getImportCRL() {
        if (importCRL == null) {
            importCRL = new JButton();
            importCRL.setText("Import CRL");
            importCRL.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    importCRL();
                }
            });
        }
        return importCRL;
    }


    private void addTrustedAuthority() {
        try {
            getProgressPanel().showProgress("Add certificate authority...");
            getAddButton().setEnabled(false);
            X509Certificate cert = this.certificatePanel.getCertificate();
            if (cert == null) {
                ErrorDialog
                    .showError("No certificate specified, you must specify a certificate to add a Trusted Authority!!!");
                getAddButton().setEnabled(true);
                return;

            }
            TrustedAuthority ta = new TrustedAuthority();
            ta.setName(this.trustedAuthorityName.getText());
            ta.setStatus(((StatusComboBox) getStatus()).getStatus());
            ta.setTrustLevels(getSelectedTrustLevels());
            ta.setCertificate(new gov.nih.nci.cagrid.gts.bean.X509Certificate(CertUtil.writeCertificate(cert)));
            if (crlPanel.getCRL() != null) {
                ta.setCRL(new gov.nih.nci.cagrid.gts.bean.X509CRL(CertUtil.writeCRL(crlPanel.getCRL())));
            }
            GTSAdminClient client = this.session.getAdminClient();
            client.addTrustedAuthority(ta);
            getProgressPanel().stopProgress("Certificate Authority successfully added.");
            GridApplication.getContext().showMessage(
                "The Trusted Authority, " + ta.getName() + " was succesfully added!!!");
            refresher.refreshTrustedAuthorities();
            dispose();
        } catch (Exception e) {
            getAddButton().setEnabled(true);
            ErrorDialog.showError(e);
            getProgressPanel().stopProgress("Error");
        }

    }


    private void updateTrustedAuthority() {
        try {
            getProgressPanel().showProgress("Updating certificate authority...");
            getAddButton().setEnabled(false);
            TrustedAuthority ta = new TrustedAuthority();
            ta.setName(this.trustedAuthorityName.getText());
            ta.setStatus(((StatusComboBox) getStatus()).getStatus());
            ta.setTrustLevels(getSelectedTrustLevels());
            if (crlPanel.getCRL() != null) {
                ta.setCRL(new gov.nih.nci.cagrid.gts.bean.X509CRL(CertUtil.writeCRL(crlPanel.getCRL())));
            }
            GTSAdminClient client = this.session.getAdminClient();
            client.updateTrustedAuthority(ta);
            refresher.refreshTrustedAuthorities();
            getAddButton().setEnabled(true);
            getProgressPanel().stopProgress("Certificate Authority successfully updated.");
        } catch (Exception e) {
            getAddButton().setEnabled(true);
            ErrorDialog.showError(e);
            getProgressPanel().stopProgress("Error");
        }

    }


    /**
     * This method initializes isAuthority
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getIsAuthority() {
        if (isAuthority == null) {
            isAuthority = new JTextField();
            isAuthority.setEditable(false);
        }
        return isAuthority;
    }


    /**
     * This method initializes authorityGTS
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getAuthorityGTS() {
        if (authorityGTS == null) {
            authorityGTS = new JTextField();
            authorityGTS.setEditable(false);
        }
        return authorityGTS;
    }


    /**
     * This method initializes sourceGTS
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getSourceGTS() {
        if (sourceGTS == null) {
            sourceGTS = new JTextField();
            sourceGTS.setEditable(false);
        }
        return sourceGTS;
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
     * This method initializes lastUpdated
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getLastUpdated() {
        if (lastUpdated == null) {
            lastUpdated = new JTextField();
            lastUpdated.setEditable(false);
        }
        return lastUpdated;
    }


    /**
     * This method initializes trustLevels
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getTrustLevels() {
        if (trustLevels == null) {
            trustLevels = new JPanel();
            trustLevels.setLayout(new BorderLayout());
            trustLevels.add(getJScrollPane(), BorderLayout.CENTER);
        }
        return trustLevels;
    }


    /**
     * This method initializes jScrollPane
     * 
     * @return javax.swing.JScrollPane
     */
    private JScrollPane getJScrollPane() {
        if (jScrollPane == null) {
            jScrollPane = new JScrollPane();
            jScrollPane.setViewportView(getLevels());
        }
        return jScrollPane;
    }


    /**
     * This method initializes levels
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getLevels() {
        if (levels == null) {
            levels = new JPanel();
            levels.setLayout(new GridBagLayout());
            levels.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Level of Assurance",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION, null, LookAndFeel.getPanelLabelColor()));
        }
        return levels;
    }


    private void addLevel(String level) {
        int size = levelsMap.size();
        int row = size / 3;
        int col = size % 3;
        int colOffset = col;
        if (col != 0) {
            colOffset = col * 2;
        }

        GridBagConstraints gbc1 = new GridBagConstraints();
        gbc1.gridx = colOffset;
        gbc1.gridy = row;
        gbc1.anchor = GridBagConstraints.WEST;
        gbc1.insets = new Insets(5, 5, 5, 5);

        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.gridx = colOffset + 1;
        gbc2.gridy = row;
        gbc2.anchor = GridBagConstraints.WEST;
        gbc2.insets = new Insets(5, 5, 5, 5);

        JLabel levelLabel = new JLabel();
        levelLabel.setText(level);
        JCheckBox checkbox = new JCheckBox();
        if(state == VIEW){
            checkbox.setEnabled(false);
        }
        getLevels().add(checkbox, gbc1);
        getLevels().add(levelLabel, gbc2);
        this.levelsMap.put(level, checkbox);
    }


    private TrustLevels getSelectedTrustLevels() {
        List<String> list = new ArrayList<String>();
        Iterator<String> itr = this.levelsMap.keySet().iterator();
        while (itr.hasNext()) {
            String key = (String) itr.next();
            JCheckBox box = (JCheckBox) levelsMap.get(key);
            if (box.isSelected()) {
                list.add(key);
            }
        }
        String[] tl = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            tl[i] = (String) list.get(i);
        }
        TrustLevels selectedLevels = new TrustLevels();
        selectedLevels.setTrustLevel(tl);
        return selectedLevels;
    }


    /**
     * This method initializes titlePanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getTitlePanel() {
        if (titlePanel == null) {
            String title = "";
            String subTitle = "";
            if (this.state == ADD) {
                title = "Add Certificate Authority";
                subTitle = "Add a certificate authority to " + this.session.getHandle().getDisplayName() + ".";
            } else {
                title = "Certificate Authority";
                subTitle = this.trustedAuthority.getName();
            }
            titlePanel = new TitlePanel(title, subTitle);
        }
        return titlePanel;
    }


    private ProgressPanel getProgressPanel() {
        if (progressPanel == null) {
            progressPanel = new ProgressPanel();
        }
        return progressPanel;
    }

}
