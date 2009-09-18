package org.cagrid.gaards.ui.gridgrouper.tree;

import gov.nih.nci.cagrid.common.Runner;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.cagrid.gaards.ui.common.CredentialComboBox;
import org.cagrid.gaards.ui.common.TitlePanel;
import org.cagrid.gaards.ui.gridgrouper.GridGrouperHandle;
import org.cagrid.gaards.ui.gridgrouper.GridGrouperLookAndFeel;
import org.cagrid.gaards.ui.gridgrouper.GridGrouperServiceList;
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
public class AddGridGrouperWindow extends ApplicationComponent {

    private static final long serialVersionUID = 1L;

    private JPanel jContentPane = null;

    private JPanel mainPanel = null;

    private JPanel treePanel = null;

    private JLabel jLabel = null;

    private JComboBox services = null;

    private JLabel jLabel1 = null;

    private JComboBox credentials = null;

    private JButton load = null;

    private GridGroupersTreeNode root;

    private JPanel titlePanel = null;


    /**
     * This is the default constructor
     */
    public AddGridGrouperWindow(GridGroupersTreeNode node) {
        super();
        this.root = node;
        initialize();
    }


    /**
     * This method initializes this
     */
    private void initialize() {
        this.setSize(400, 150);
        this.setContentPane(getJContentPane());
        this.setTitle("Add Grid Grouper");
        this.setFrameIcon(GridGrouperLookAndFeel.getGrouperIcon22x22());
    }


    /**
     * This method initializes jContentPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getJContentPane() {
        if (jContentPane == null) {
            jContentPane = new JPanel();
            jContentPane.setLayout(new BorderLayout());
            jContentPane.add(getMainPanel(), BorderLayout.CENTER);
        }
        return jContentPane;
    }


    /**
     * This method initializes mainPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getMainPanel() {
        if (mainPanel == null) {
            GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
            gridBagConstraints11.gridx = 0;
            gridBagConstraints11.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints11.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints11.weightx = 1.0D;
            gridBagConstraints11.gridy = 0;
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.anchor = GridBagConstraints.WEST;
            gridBagConstraints.gridy = 1;
            gridBagConstraints.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints.fill = GridBagConstraints.BOTH;
            gridBagConstraints.weightx = 1.0D;
            gridBagConstraints.weighty = 1.0D;
            gridBagConstraints.gridx = 0;
            mainPanel = new JPanel();
            mainPanel.setLayout(new GridBagLayout());
            mainPanel.add(getTreePanel(), gridBagConstraints);
            mainPanel.add(getTitlePanel(), gridBagConstraints11);
        }
        return mainPanel;
    }


    /**
     * This method initializes treePanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getTreePanel() {
        if (treePanel == null) {
            GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
            gridBagConstraints8.gridx = 0;
            gridBagConstraints8.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints8.gridwidth = 2;
            gridBagConstraints8.gridy = 4;
            GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
            gridBagConstraints5.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints5.gridx = 1;
            gridBagConstraints5.gridy = 1;
            gridBagConstraints5.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints5.anchor = GridBagConstraints.WEST;
            gridBagConstraints5.weightx = 1.0;
            GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
            gridBagConstraints4.gridx = 0;
            gridBagConstraints4.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints4.anchor = GridBagConstraints.WEST;
            gridBagConstraints4.gridy = 1;
            jLabel1 = new JLabel();
            jLabel1.setText("Credentials");
            jLabel1.setFont(new Font("Dialog", Font.BOLD, 12));
            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            gridBagConstraints2.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints2.gridx = 1;
            gridBagConstraints2.gridy = 0;
            gridBagConstraints2.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints2.anchor = GridBagConstraints.WEST;
            gridBagConstraints2.weightx = 1.0;
            GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
            gridBagConstraints1.gridx = 0;
            gridBagConstraints1.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints1.anchor = GridBagConstraints.WEST;
            gridBagConstraints1.gridy = 0;
            jLabel = new JLabel();
            jLabel.setText("Grid Grouper");
            jLabel.setFont(new Font("Dialog", Font.BOLD, 12));
            treePanel = new JPanel();
            treePanel.setLayout(new GridBagLayout());
            treePanel.add(jLabel, gridBagConstraints1);
            treePanel.add(getServices(), gridBagConstraints2);
            treePanel.add(jLabel1, gridBagConstraints4);
            treePanel.add(getCredentials(), gridBagConstraints5);
            treePanel.add(getLoad(), gridBagConstraints8);
        }
        return treePanel;
    }


    /**
     * This method initializes services
     * 
     * @return javax.swing.JComboBox
     */
    private JComboBox getServices() {
        if (services == null) {
            services = new GridGrouperServiceList();
        }
        return services;
    }


    /**
     * This method initializes credentials
     * 
     * @return javax.swing.JComboBox
     */
    private JComboBox getCredentials() {
        if (credentials == null) {
            credentials = new CredentialComboBox(true);
        }
        return credentials;
    }


    /**
     * This method initializes load
     * 
     * @return javax.swing.JButton
     */
    private JButton getLoad() {
        if (load == null) {
            load = new JButton();
            load.setText("Add");
            getRootPane().setDefaultButton(load);
            load.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    Runner runner = new Runner() {
                        public void execute() {
                            loadGridGrouper();
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
        return load;
    }


    private void loadGridGrouper() {
        GridGrouperHandle handle = null;
        try {
            handle = ((GridGrouperServiceList) this.services).getSelectedService();
            GlobusCredential cred = ((CredentialComboBox) this.credentials).getSelectedCredential();
            this.dispose();
            this.root.addGridGrouper(handle.getClient(cred));
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
            titlePanel = new TitlePanel("Add Grid Grouper","Browse stem(s) and group(s) managed by a Grid Grouper.");
        }
        return titlePanel;
    }
}
