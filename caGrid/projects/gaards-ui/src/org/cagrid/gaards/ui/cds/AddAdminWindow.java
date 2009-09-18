package org.cagrid.gaards.ui.cds;

import gov.nih.nci.cagrid.common.Runner;
import gov.nih.nci.cagrid.common.Utils;

import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.cagrid.gaards.cds.client.DelegationAdminClient;
import org.cagrid.gaards.ui.common.ProgressPanel;
import org.cagrid.gaards.ui.common.TitlePanel;
import org.cagrid.gaards.ui.dorian.federation.UserSearchDialog;
import org.cagrid.grape.GridApplication;
import org.cagrid.grape.LookAndFeel;
import org.cagrid.grape.utils.ErrorDialog;


public class AddAdminWindow extends JDialog {

    private static final long serialVersionUID = 1L;

    private JPanel jContentPane = null;

    private JPanel buttonPanel = null;

    private JButton addAdminButton = null;

    private JPanel userPanel = null;

    private JLabel jLabel2 = null;

    private JTextField gridIdentity = null;

    private JButton findUserButton = null;

    private JPanel titlePanel = null;

    private ProgressPanel progressPanel = null;

    private CDSSession session;


    /**
     * This is the default constructor
     */
    public AddAdminWindow(CDSSession session) {
        super(GridApplication.getContext().getApplication());
        this.session = session;
        initialize();
    }


    /**
     * This method initializes this
     */
    private void initialize() {
        this.setSize(500, 200);
        this.setContentPane(getJContentPane());
        this.setTitle("Add Administrator");
    }


    /**
     * This method initializes jContentPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getJContentPane() {
        if (jContentPane == null) {
            GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
            gridBagConstraints3.gridx = 0;
            gridBagConstraints3.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints3.gridy = 3;
            GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
            gridBagConstraints21.gridx = 0;
            gridBagConstraints21.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints21.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints21.weightx = 1.0D;
            gridBagConstraints21.gridy = 0;
            GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
            gridBagConstraints11.gridx = 0;
            gridBagConstraints11.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints11.weightx = 1.0D;
            gridBagConstraints11.fill = GridBagConstraints.BOTH;
            gridBagConstraints11.weighty = 1.0D;
            gridBagConstraints11.gridy = 1;
            GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
            gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints1.gridy = 2;
            gridBagConstraints1.weightx = 1.0D;
            gridBagConstraints1.gridx = 0;
            jContentPane = new JPanel();
            jContentPane.setLayout(new GridBagLayout());
            jContentPane.add(getButtonPanel(), gridBagConstraints1);
            jContentPane.add(getUserPanel(), gridBagConstraints11);
            jContentPane.add(getTitlePanel(), gridBagConstraints21);
            jContentPane.add(getProgressPanel(), gridBagConstraints3);
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
            buttonPanel.setLayout(new FlowLayout());
            buttonPanel.add(getAddAdminButton(), null);
        }
        return buttonPanel;
    }


    /**
     * This method initializes addAdmin
     * 
     * @return javax.swing.JButton
     */
    private JButton getAddAdminButton() {
        if (addAdminButton == null) {
            addAdminButton = new JButton();
            addAdminButton.setText("Add");
            addAdminButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    Runner runner = new Runner() {
                        public void execute() {
                            addAdmin();
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
        return addAdminButton;
    }


    private void addAdmin() {
        try {
            getProgressPanel().showProgress("Adding administrator...");
            getAddAdminButton().setEnabled(false);
            getFindUserButton().setEnabled(false);
            DelegationAdminClient client = this.session.getAdminClient();
            String admin = Utils.clean(getGridIdentity().getText());
            if (admin != null) {
                client.addAdmin(admin);
                dispose();
            } else {
                ErrorDialog.showError("Please specify an admin to add.");
                getProgressPanel().stopProgress("Error");
            }

        } catch (Exception e) {
            ErrorDialog.showError(e);
            getProgressPanel().stopProgress("Error");
        } finally {
            getAddAdminButton().setEnabled(true);
            getFindUserButton().setEnabled(true);
        }
    }


    /**
     * This method initializes userPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getUserPanel() {
        if (userPanel == null) {
            GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
            gridBagConstraints8.gridx = 1;
            gridBagConstraints8.gridy = 1;
            GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
            gridBagConstraints7.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints7.gridx = 0;
            gridBagConstraints7.gridy = 1;
            gridBagConstraints7.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints7.weightx = 1.0;
            GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
            gridBagConstraints6.gridx = 0;
            gridBagConstraints6.gridwidth = 2;
            gridBagConstraints6.gridy = 0;
            jLabel2 = new JLabel();
            jLabel2.setText("Grid Identity");
            jLabel2.setForeground(LookAndFeel.getPanelLabelColor());
            jLabel2.setFont(new Font("Dialog", Font.BOLD, 14));
            userPanel = new JPanel();
            userPanel.setLayout(new GridBagLayout());
            userPanel.add(jLabel2, gridBagConstraints6);
            userPanel.add(getGridIdentity(), gridBagConstraints7);
            userPanel.add(getFindUserButton(), gridBagConstraints8);
        }
        return userPanel;
    }


    /**
     * This method initializes gridIdentity
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getGridIdentity() {
        if (gridIdentity == null) {
            gridIdentity = new JTextField();
        }
        return gridIdentity;
    }


    /**
     * This method initializes findUser
     * 
     * @return javax.swing.JButton
     */
    private JButton getFindUserButton() {
        if (findUserButton == null) {
            findUserButton = new JButton();
            findUserButton.setText("Find...");
            findUserButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    UserSearchDialog dialog = new UserSearchDialog();
                    dialog.setModal(true);
                    GridApplication.getContext().showDialog(dialog);
                    if (dialog.getSelectedUser() != null) {
                        gridIdentity.setText(dialog.getSelectedUser());
                    }
                }
            });
        }
        return findUserButton;
    }


    /**
     * This method initializes titlePanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getTitlePanel() {
        if (titlePanel == null) {
            titlePanel = new TitlePanel("Add Administrator",
                "Grant a party administrative rights to a delegation service.");
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
