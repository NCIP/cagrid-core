package org.cagrid.gaards.ui.gridgrouper.mygroups;

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

import org.cagrid.gaards.ui.common.ProgressPanel;
import org.cagrid.gaards.ui.common.TitlePanel;
import org.cagrid.gaards.ui.gridgrouper.GridGrouperHandle;
import org.cagrid.gaards.ui.gridgrouper.GridGrouperSession;
import org.cagrid.gaards.ui.gridgrouper.GridGrouperUIUtils;
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
public class MyGroupsWindow extends ApplicationComponent {

    private static final long serialVersionUID = 1L;

    private JPanel jContentPane = null;

    private JPanel metadataPanel = null;

    private JLabel jLabel = null;

    private JTextField gridIdentity = null;

    private ProgressPanel progressPanel = null;

    private JPanel groupsPanel = null;

    private JScrollPane jScrollPane = null;

    private MyGroupsTable groups = null;

    private JPanel buttonPanel = null;

    private JButton view = null;

    private JButton refresh = null;

    private JPanel titlePanel = null;

    /**
     * This is the default constructor
     */
    public MyGroupsWindow() {
        super();
        initialize();
    }


    private void findGroups() {
        getProgressPanel().showProgress("Searching...");
        getGroups().clearTable();
        try {
            GlobusCredential cred = null;
            try {
                cred = ProxyUtil.getDefaultProxy();
                this.getGridIdentity().setText(cred.getIdentity());
            } catch (Exception e) {
                ErrorDialog
                    .showError(
                        "Credentials required to discover groups",
                        "In order to discover the groups in which you are a member you must have grid credentials.  No grid credentials could be found, please logon and try again!!!");
                getProgressPanel().stopProgress();
                return;
            }

            if (cred.getTimeLeft() <= 0) {
                ErrorDialog.showError("Your credentials are expired.");
                getProgressPanel().stopProgress();
                return;
            }

            RunnerGroup grp = new RunnerGroup();
            List<GridGrouperHandle> services = GridGrouperUIUtils.getGridGrouperServices();
            for (int i = 0; i < services.size(); i++) {
                MyGroupFinder finder = new MyGroupFinder(new GridGrouperSession(services.get(i), cred), getGroups());
                grp.add(finder);
            }
            GridApplication.getContext().execute(grp);

            for (int i = 0; i < grp.size(); i++) {
                MyGroupFinder finder = (MyGroupFinder) grp.get(i);
                if (!finder.isSuccessful()) {
                    ErrorDialog.showError("Could not discover groups from " + finder.getGridGrouperURI() + ".", finder
                        .getError());
                }
            }
            getProgressPanel().stopProgress();

        } catch (Exception ex) {
            ex.printStackTrace();
            getProgressPanel().stopProgress();
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
                findGroups();
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
            GridBagConstraints gridBagConstraints13 = new GridBagConstraints();
            gridBagConstraints13.gridx = 0;
            gridBagConstraints13.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints13.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints13.weightx = 1.0D;
            gridBagConstraints13.gridy = 0;
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
            gridBagConstraints11.insets = new Insets(0, 0, 0, 0);
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
            jContentPane.add(getTitlePanel(), gridBagConstraints13);
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
            jScrollPane.setViewportView(getGroups());
        }
        return jScrollPane;
    }


    /**
     * This method initializes groups
     * 
     * @return javax.swing.JTable
     */
    private MyGroupsTable getGroups() {
        if (groups == null) {
            groups = new MyGroupsTable();
        }
        return groups;
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
            buttonPanel.add(getView(), null);
            buttonPanel.add(getRefresh(), null);
        }
        return buttonPanel;
    }


    /**
     * This method initializes view
     * 
     * @return javax.swing.JButton
     */
    private JButton getView() {
        if (view == null) {
            view = new JButton();
            view.setText("View");
            view.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    getGroups().doubleClick();
                }
            });
        }
        return view;
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
                            findGroups();
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
            titlePanel = new TitlePanel("My Groups","Browse the groups that you are a member of.");
        }
        return titlePanel;
    }

}
