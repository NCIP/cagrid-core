package org.cagrid.gaards.ui.gts;

import gov.nih.nci.cagrid.common.Runner;
import gov.nih.nci.cagrid.gts.bean.TrustLevel;
import gov.nih.nci.cagrid.gts.client.GTSAdminClient;
import gov.nih.nci.cagrid.gts.client.GTSPublicClient;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.cagrid.gaards.ui.common.ProgressPanel;
import org.cagrid.gaards.ui.common.TitlePanel;
import org.cagrid.grape.ApplicationComponent;
import org.cagrid.grape.GridApplication;
import org.cagrid.grape.LookAndFeel;
import org.cagrid.grape.utils.ErrorDialog;


/**
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Langella </A>
 * @version $Id: TrustedAuthoritiesWindow.java,v 1.2 2006/03/27 19:05:40
 *          langella Exp $
 */
public class LevelOfAssuranceManagerWindow extends ApplicationComponent implements LevelOfAssuranceRefresher {

    private static final long serialVersionUID = 1L;

    private javax.swing.JPanel jContentPane = null;

    private JPanel mainPanel = null;

    private JPanel contentPanel = null;

    private JPanel buttonPanel = null;

    private LevelOfAssuranceTable trustLevelTable = null;

    private JScrollPane jScrollPane = null;

    private JButton addTrustLevel = null;

    private JPanel queryPanel = null;

    private JButton query = null;

    private JButton removeTrustLevelButton = null;

    private JButton viewModifyButton = null;

    private boolean searchDone = false;

    private SessionPanel sessionPanel = null;

    private JPanel titlePanel = null;

    private ProgressPanel progressPanel = null;


    /**
     * This is the default constructor
     */
    public LevelOfAssuranceManagerWindow() {
        super();
        initialize();
        this.setFrameIcon(GTSLookAndFeel.getTrustLevelIcon());
    }


    /**
     * This method initializes this
     */
    private void initialize() {
        this.setSize(700, 500);
        this.setContentPane(getJContentPane());
        this.setTitle("Levels of Assurance");
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
            GridBagConstraints gridBagConstraints22 = new GridBagConstraints();
            gridBagConstraints22.gridx = 0;
            gridBagConstraints22.weightx = 1.0D;
            gridBagConstraints22.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints22.gridy = 5;
            GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
            gridBagConstraints1.gridx = 0;
            gridBagConstraints1.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints1.weightx = 1.0D;
            gridBagConstraints1.gridy = 0;
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints.weightx = 1.0D;
            gridBagConstraints.gridy = 1;
            GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
            gridBagConstraints21.gridx = 0;
            gridBagConstraints21.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints21.weightx = 1.0D;
            gridBagConstraints21.weighty = 1.0D;
            gridBagConstraints21.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints21.gridy = 3;
            GridBagConstraints gridBagConstraints33 = new GridBagConstraints();
            gridBagConstraints33.gridx = 0;
            gridBagConstraints33.gridy = 2;
            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            mainPanel = new JPanel();
            mainPanel.setLayout(new GridBagLayout());
            gridBagConstraints2.gridx = 0;
            gridBagConstraints2.gridy = 4;
            gridBagConstraints2.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints2.anchor = java.awt.GridBagConstraints.SOUTH;
            gridBagConstraints2.fill = java.awt.GridBagConstraints.HORIZONTAL;
            mainPanel.add(getButtonPanel(), gridBagConstraints2);
            mainPanel.add(getQueryPanel(), gridBagConstraints33);
            mainPanel.add(getContentPanel(), gridBagConstraints21);
            mainPanel.add(getSessionPanel(), gridBagConstraints);
            mainPanel.add(getTitlePanel(), gridBagConstraints1);
            mainPanel.add(getProgressPanel(), gridBagConstraints22);
        }
        return mainPanel;
    }


    /**
     * This method initializes jPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getContentPanel() {
        if (contentPanel == null) {
            GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
            contentPanel = new JPanel();
            contentPanel.setLayout(new GridBagLayout());
            contentPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Level(s) of Assurance",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION, null, LookAndFeel.getPanelLabelColor()));
            gridBagConstraints4.weightx = 1.0;
            gridBagConstraints4.gridy = 0;
            gridBagConstraints4.gridx = 0;
            gridBagConstraints4.weighty = 1.0;
            gridBagConstraints4.fill = java.awt.GridBagConstraints.BOTH;
            contentPanel.add(getJScrollPane(), gridBagConstraints4);
        }
        return contentPanel;
    }


    /**
     * This method initializes jPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getButtonPanel() {
        if (buttonPanel == null) {
            buttonPanel = new JPanel();
            buttonPanel.add(getViewModifyButton(), null);
            buttonPanel.add(getAddTrustLevel(), null);
            buttonPanel.add(getRemoveTrustLevelButton(), null);
        }
        return buttonPanel;
    }


    /**
     * This method initializes jTable
     * 
     * @return javax.swing.JTable
     */
    private LevelOfAssuranceTable getTrustLevelTable() {
        if (trustLevelTable == null) {
            trustLevelTable = new LevelOfAssuranceTable(this);
        }
        return trustLevelTable;
    }


    /**
     * This method initializes jScrollPane
     * 
     * @return javax.swing.JScrollPane
     */
    private JScrollPane getJScrollPane() {
        if (jScrollPane == null) {
            jScrollPane = new JScrollPane();
            jScrollPane.setViewportView(getTrustLevelTable());
        }
        return jScrollPane;
    }


    /**
     * This method initializes manageUser
     * 
     * @return javax.swing.JButton
     */
    private JButton getAddTrustLevel() {
        if (addTrustLevel == null) {
            addTrustLevel = new JButton();
            addTrustLevel.setText("Add");
            addTrustLevel.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    Runner runner = new Runner() {
                        public void execute() {
                            disableAllActions();
                            addTrustLevel();
                            enableAllActions();
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

        return addTrustLevel;
    }


    public void addTrustLevel() {
        try {
            GridApplication.getContext().addApplicationComponent(
                new LevelOfAssuranceWindow(getSessionPanel().getSession(), this), 600, 300);
        } catch (Exception e) {
            ErrorDialog.showError(e);
        }
    }


    public void viewModifyLevel() {
        try {
            GridApplication.getContext().addApplicationComponent(
                new LevelOfAssuranceWindow(getSessionPanel().getSession(),
                    getTrustLevelTable().getSelectedTrustLevel(), this), 700, 500);
        } catch (Exception e) {
            ErrorDialog.showError(e);
        }
    }


    /**
     * This method initializes queryPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getQueryPanel() {
        if (queryPanel == null) {
            queryPanel = new JPanel();
            queryPanel.add(getQuery(), null);
        }
        return queryPanel;
    }


    /**
     * This method initializes query
     * 
     * @return javax.swing.JButton
     */
    private JButton getQuery() {
        if (query == null) {
            query = new JButton();
            query.setText("Search");
            getRootPane().setDefaultButton(query);
            query.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    Runner runner = new Runner() {
                        public void execute() {
                            disableAllActions();
                            getTrustLevels();
                            enableAllActions();
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
        return query;
    }


    private void getTrustLevels() {
        getProgressPanel().showProgress("Searching...");
        this.getTrustLevelTable().clearTable();

        try {
            GTSPublicClient client = getSessionPanel().getSession().getUserClient();
            TrustLevel[] levels = client.getTrustLevels();
            int length = 0;
            if (levels != null) {
                length = levels.length;
                for (int i = 0; i < levels.length; i++) {
                    getTrustLevelTable().addTrustLevel(levels[i]);
                }
            }
            searchDone = true;
            this.getProgressPanel().stopProgress(length + " level(s) of assurance found.");

        } catch (Exception e) {
            ErrorDialog.showError(e);
            this.getProgressPanel().stopProgress("Error");
        }
    }


    /**
     * This method initializes removeUser
     * 
     * @return javax.swing.JButton
     */
    private JButton getRemoveTrustLevelButton() {
        if (removeTrustLevelButton == null) {
            removeTrustLevelButton = new JButton();
            removeTrustLevelButton.setText("Remove");
            removeTrustLevelButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    Runner runner = new Runner() {
                        public void execute() {
                            disableAllActions();
                            removeTrustLevel();
                            enableAllActions();
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
        return removeTrustLevelButton;
    }


    private void removeTrustLevel() {
        try {
            getProgressPanel().showProgress("Removing level of assurance...");
            GTSAdminClient client = getSessionPanel().getSession().getAdminClient();
            TrustLevel level = getTrustLevelTable().getSelectedTrustLevel();
            client.removeTrustLevel(level.getName());
            getTrustLevelTable().removeSelectedTrustLevel();
            getProgressPanel().stopProgress("Level of assurance successfully removed.");
            refreshTrustLevels();
        } catch (Exception e) {
            ErrorDialog.showError(e);
            getProgressPanel().stopProgress("Error");
        }
    }


    /**
     * This method initializes viewModifyButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getViewModifyButton() {
        if (viewModifyButton == null) {
            viewModifyButton = new JButton();
            viewModifyButton.setText("View");
            viewModifyButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    try {
                        disableAllActions();
                        getTrustLevelTable().doubleClick();
                    } catch (Exception ex) {
                        ErrorDialog.showError(ex);
                    } finally {
                        enableAllActions();
                    }
                }
            });
        }
        return viewModifyButton;
    }


    private void disableAllActions() {
        getQuery().setEnabled(false);
        getAddTrustLevel().setEnabled(false);
        getViewModifyButton().setEnabled(false);
        getRemoveTrustLevelButton().setEnabled(false);
    }


    private void enableAllActions() {
        getQuery().setEnabled(true);
        getAddTrustLevel().setEnabled(true);
        getViewModifyButton().setEnabled(true);
        getRemoveTrustLevelButton().setEnabled(true);
    }


    public void refreshTrustLevels() {
        if (searchDone) {
            disableAllActions();
            this.getTrustLevels();
            enableAllActions();
        }

    }


    /**
     * This method initializes sessionPanel
     * 
     * @return javax.swing.JPanel
     */
    private SessionPanel getSessionPanel() {
        if (sessionPanel == null) {
            sessionPanel = new SessionPanel();
        }
        return sessionPanel;
    }


    /**
     * This method initializes titlePanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getTitlePanel() {
        if (titlePanel == null) {
            titlePanel = new TitlePanel("Level of Assurance Search",
                "Search for and manage levels of assurance in the trust fabric.");
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
