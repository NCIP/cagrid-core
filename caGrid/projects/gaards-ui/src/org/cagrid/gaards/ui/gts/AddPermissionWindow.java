package org.cagrid.gaards.ui.gts;

import gov.nih.nci.cagrid.common.Runner;
import gov.nih.nci.cagrid.gts.client.GTSAdminClient;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JPanel;

import org.cagrid.gaards.ui.common.ProgressPanel;
import org.cagrid.gaards.ui.common.TitlePanel;
import org.cagrid.grape.ApplicationComponent;
import org.cagrid.grape.GridApplication;
import org.cagrid.grape.utils.ErrorDialog;


/**
 * @author <A href="mailto:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A href="mailto:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A href="mailto:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @version $Id: ArgumentManagerTable.java,v 1.2 2004/10/15 16:35:16 langella
 *          Exp $
 */
public class AddPermissionWindow extends ApplicationComponent {

    private static final long serialVersionUID = 1L;

    private JPanel jContentPane = null;

    private JPanel buttonPanel = null;

    private JButton addButton = null;

    private PermissionPanel permissionPanel = null;

    private PermissionRefresher refresher;

    private JPanel titlePanel = null;
    
    private GTSSession session;

    private ProgressPanel progressPanel = null;


    /**
     * This is the default constructor
     */
    public AddPermissionWindow(GTSSession session, PermissionRefresher refresher) {
        super();
        this.session = session;
        this.refresher = refresher;
        initialize();
        syncServices();
    }


    /**
     * This method initializes this
     */
    private void initialize() {
        this.setSize(600, 400);
        this.setContentPane(getJContentPane());
        this.setTitle("Add Permission");
        this.setFrameIcon(GTSLookAndFeel.getAdminIcon());
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
            GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
            gridBagConstraints11.gridx = 0;
            gridBagConstraints11.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints11.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints11.weightx = 1.0D;
            gridBagConstraints11.gridy = 0;
            GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
            gridBagConstraints1.gridx = 0;
            gridBagConstraints1.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints1.weightx = 1.0D;
            gridBagConstraints1.weighty = 1.0D;
            gridBagConstraints1.insets = new java.awt.Insets(5, 5, 5, 5);
            gridBagConstraints1.gridy = 1;
            GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
            gridBagConstraints12.gridx = 0;
            gridBagConstraints12.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints12.gridy = 2;
            jContentPane = new JPanel();
            jContentPane.setLayout(new GridBagLayout());
            jContentPane.add(getButtonPanel(), gridBagConstraints12);
            jContentPane.add(getPermissionPanel(), gridBagConstraints1);
            jContentPane.add(getTitlePanel(), gridBagConstraints11);
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
            addButton.setText("Add");
            addButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    Runner runner = new Runner() {
                        public void execute() {
                            addPermission();
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


    private void addPermission() {
        try {
            getProgressPanel().showProgress("Add permission...");
            getAddButton().setEnabled(false);
            getPermissionPanel().disableAll();
            GTSAdminClient client = this.session.getAdminClient();
            client.addPermission(permissionPanel.getPermission());
            getProgressPanel().stopProgress("Permission successfully added.");
            dispose();
            refresher.refreshPermissions();  
        } catch (Exception e) {
            getAddButton().setEnabled(true);
            getPermissionPanel().enableAll();
            ErrorDialog.showError(e);
            getProgressPanel().stopProgress("Error");
        }

    }


    /**
     * This method initializes jPanel
     * 
     * @return javax.swing.JPanel
     */
    private PermissionPanel getPermissionPanel() {
        if (permissionPanel == null) {
            permissionPanel = new PermissionPanel(false);
        }
        return permissionPanel;
    }


    private synchronized void syncServices(){ 
        try {
            permissionPanel.syncWithService(this.session);
        } catch (Exception e) {
            ErrorDialog.showError(e);
        }
    }


    /**
     * This method initializes titlePanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getTitlePanel() {
        if (titlePanel == null) {
            titlePanel = new TitlePanel("Add Permission",
                "Grant a party administrative privilege(s) to the "+this.session.getHandle().getDisplayName());
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
