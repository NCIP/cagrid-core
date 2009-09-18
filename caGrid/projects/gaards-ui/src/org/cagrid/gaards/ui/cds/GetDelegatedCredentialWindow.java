package org.cagrid.gaards.ui.cds;

import gov.nih.nci.cagrid.common.Runner;
import gov.nih.nci.cagrid.common.RunnerGroup;
import gov.nih.nci.cagrid.common.security.ProxyUtil;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.cagrid.gaards.cds.client.DelegatedCredentialUserClient;
import org.cagrid.gaards.ui.common.CredentialManager;
import org.cagrid.gaards.ui.common.CredentialManagerComponent;
import org.cagrid.gaards.ui.common.ProgressPanel;
import org.cagrid.gaards.ui.common.TitlePanel;
import org.cagrid.grape.ApplicationComponent;
import org.cagrid.grape.GridApplication;
import org.cagrid.grape.utils.ErrorDialog;
import org.globus.gsi.GlobusCredential;


/**
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella</A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster</A>
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings</A>
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * @version $Id: GridGrouperBaseTreeNode.java,v 1.1 2006/08/04 03:49:26 langella
 *          Exp $
 */
public class GetDelegatedCredentialWindow extends ApplicationComponent {

    private static final long serialVersionUID = 1L;

    private JPanel jContentPane = null;

    private JPanel metadataPanel = null;

    private JLabel jLabel = null;

    private JTextField gridIdentity = null;

    private ProgressPanel progressPanel = null;

    private JPanel groupsPanel = null;

    private JScrollPane jScrollPane = null;

    private DelegationDescriptorTable delegatedCredentials = null;

    private JPanel buttonPanel = null;

    private JButton getButton = null;

    private JButton refresh = null;

    private JPanel titlePanel = null;


    /**
     * This is the default constructor
     */
    public GetDelegatedCredentialWindow() {
        super();
        initialize();
    }


    private void findDelegatedCredentials() {
        getProgressPanel().showProgress("Searching...");
        getDelegatedCredentials().clearTable();
        try {
            GlobusCredential cred = null;
            try {
                cred = ProxyUtil.getDefaultProxy();
                this.getGridIdentity().setText(cred.getIdentity());
            } catch (Exception e) {
                ErrorDialog
                    .showError(
                        "A credential is required to determine who had delegated credentials to you.",
                        "A credential is required to determine who had delegated credentials to you.  No grid credentials could be found, please logon and try again!!!");
                getProgressPanel().stopProgress("Error");
                return;
            }

            if (cred.getTimeLeft() <= 0) {
                ErrorDialog.showError("Your credentials are expired.");
                getProgressPanel().stopProgress("Error");
                return;
            }

            RunnerGroup grp = new RunnerGroup();
            List<CDSHandle> services = CDSUIUtils.getCDSServices();
            for (int i = 0; i < services.size(); i++) {
                DelegatedCredentialFinder finder = new DelegatedCredentialFinder(services.get(i), cred,
                    getDelegatedCredentials());
                grp.add(finder);
            }
            GridApplication.getContext().execute(grp);

            for (int i = 0; i < grp.size(); i++) {
                DelegatedCredentialFinder finder = (DelegatedCredentialFinder) grp.get(i);
                if (!finder.isSuccessful()) {
                    ErrorDialog.showError(
                        "Error finding delegated credentials from " + finder.getDelegationURI() + ".", finder
                            .getError());
                }
            }
            getProgressPanel().stopProgress(getDelegatedCredentials().getRowCount() + " credential(s) found.");

        } catch (Exception ex) {
            ex.printStackTrace();
            getProgressPanel().stopProgress("Error");
        }
    }


    /**
     * This method initializes this
     */
    private void initialize() {
        this.setSize(300, 200);
        this.setContentPane(getJContentPane());
        this.setTitle("JFrame");
        Runner runner = new Runner() {
            public void execute() {
                findDelegatedCredentials();
            }
        };
        try {
            GridApplication.getContext().executeInBackground(runner);
        } catch (Exception t) {
            t.getMessage();
        }
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
            gridBagConstraints31.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints31.weightx = 1.0D;
            gridBagConstraints31.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints31.gridy = 0;
            GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
            gridBagConstraints12.gridx = 0;
            gridBagConstraints12.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints12.weightx = 1.0D;
            gridBagConstraints12.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints12.gridy = 3;
            GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
            gridBagConstraints21.gridx = 0;
            gridBagConstraints21.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints21.fill = GridBagConstraints.BOTH;
            gridBagConstraints21.weightx = 1.0D;
            gridBagConstraints21.weighty = 1.0D;
            gridBagConstraints21.gridy = 2;
            GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
            gridBagConstraints11.gridx = 0;
            gridBagConstraints11.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints11.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints11.weightx = 1.0D;
            gridBagConstraints11.gridy = 4;
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridheight = 1;
            gridBagConstraints.gridy = 1;
            gridBagConstraints.ipadx = 0;
            gridBagConstraints.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints.weightx = 1.0D;
            gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints.gridx = 0;
            jContentPane = new JPanel();
            jContentPane.setLayout(new GridBagLayout());
            jContentPane.add(getMetadataPanel(), gridBagConstraints);
            jContentPane.add(getProgressPanel(), gridBagConstraints11);
            jContentPane.add(getGroupsPanel(), gridBagConstraints21);
            jContentPane.add(getButtonPanel(), gridBagConstraints12);
            jContentPane.add(getTitlePanel(), gridBagConstraints31);
        }
        return jContentPane;
    }


    /**
     * This method initializes metadataPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getMetadataPanel() {
        if (metadataPanel == null) {
            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            gridBagConstraints2.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints2.anchor = GridBagConstraints.WEST;
            gridBagConstraints2.gridx = 1;
            gridBagConstraints2.gridy = 0;
            gridBagConstraints2.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints2.weightx = 1.0;
            GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
            gridBagConstraints1.anchor = GridBagConstraints.WEST;
            gridBagConstraints1.gridx = 0;
            gridBagConstraints1.gridy = 0;
            gridBagConstraints1.insets = new Insets(2, 2, 2, 2);
            jLabel = new JLabel();
            jLabel.setText("Grid Identity");
            metadataPanel = new JPanel();
            metadataPanel.setLayout(new GridBagLayout());
            metadataPanel.add(jLabel, gridBagConstraints1);
            metadataPanel.add(getGridIdentity(), gridBagConstraints2);
        }
        return metadataPanel;
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
     * This method initializes groupsPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getGroupsPanel() {
        if (groupsPanel == null) {
            GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
            gridBagConstraints3.fill = GridBagConstraints.BOTH;
            gridBagConstraints3.weighty = 1.0;
            gridBagConstraints3.weightx = 1.0;
            groupsPanel = new JPanel();
            groupsPanel.setLayout(new GridBagLayout());
            groupsPanel.add(getJScrollPane(), gridBagConstraints3);
        }
        return groupsPanel;
    }


    /**
     * This method initializes jScrollPane
     * 
     * @return javax.swing.JScrollPane
     */
    private JScrollPane getJScrollPane() {
        if (jScrollPane == null) {
            jScrollPane = new JScrollPane();
            jScrollPane.setViewportView(getDelegatedCredentials());
        }
        return jScrollPane;
    }


    /**
     * This method initializes delegatedCredentials
     * 
     * @return javax.swing.JTable
     */
    private DelegationDescriptorTable getDelegatedCredentials() {
        if (delegatedCredentials == null) {
            delegatedCredentials = new DelegationDescriptorTable();
        }
        return delegatedCredentials;
    }


    /**
     * This method initializes buttonPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getButtonPanel() {
        if (buttonPanel == null) {
            buttonPanel = new JPanel();
            buttonPanel.setLayout(new FlowLayout());
            buttonPanel.add(getGetButton(), null);
            buttonPanel.add(getRefresh(), null);
        }
        return buttonPanel;
    }


    /**
     * This method initializes getButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getGetButton() {
        if (getButton == null) {
            getButton = new JButton();
            getButton.setText("Obtain Credential");
            getButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    Runner runner = new Runner() {
                        public void execute() {
                            getDelegatedCredential();
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
        return getButton;
    }


    private void getDelegatedCredential() {
        getGetButton().setEnabled(false);
        try {
            DelegatedCredentialUserClient client = new DelegatedCredentialUserClient(getDelegatedCredentials()
                .getSelectedDelegationDescriptor().getDelegatedCredentialReference());
            GlobusCredential cred = client.getDelegatedCredential();
            CredentialManager.getInstance().addCredential(cred);
            dispose();
            GridApplication.getContext().addApplicationComponent(new CredentialManagerComponent(cred), 800, 500);

        } catch (Exception e) {
            getGetButton().setEnabled(true);
            ErrorDialog.showError(e);
        }

    }


    /**
     * This method initializes refresh
     * 
     * @return javax.swing.JButton
     */
    private JButton getRefresh() {
        if (refresh == null) {
            refresh = new JButton();
            refresh.setText("Refresh");
            refresh.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    Runner runner = new Runner() {
                        public void execute() {
                            findDelegatedCredentials();
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
        return refresh;
    }


    /**
     * This method initializes titlePanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getTitlePanel() {
        if (titlePanel == null) {
            titlePanel = new TitlePanel("Obtain Credential", "Obtain a credential that has been delegated to you.");
        }
        return titlePanel;
    }

}
