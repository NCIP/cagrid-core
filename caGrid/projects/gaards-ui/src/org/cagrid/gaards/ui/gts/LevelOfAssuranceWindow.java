package org.cagrid.gaards.ui.gts;

import gov.nih.nci.cagrid.common.Runner;
import gov.nih.nci.cagrid.gts.bean.TrustLevel;
import gov.nih.nci.cagrid.gts.client.GTSAdminClient;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

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
public class LevelOfAssuranceWindow extends ApplicationComponent {

    private static final long serialVersionUID = 1L;

    private JPanel jContentPane = null;

    private JPanel buttonPanel = null;

    private JButton addButton = null;

    private JPanel trustLevelPanel = null;

    private JLabel jLabel2 = null;

    private JTextField trustLevelName = null;

    private JLabel jLabel3 = null;

    private JTextArea description = null;

    private JScrollPane jScrollPane = null;

    private LevelOfAssuranceRefresher refresher;

    private JLabel jLabel4 = null;

    private JTextField isAuthority = null;

    private JLabel jLabel5 = null;

    private JTextField authorityGTS = null;

    private JLabel jLabel6 = null;

    private JTextField sourceGTS = null;

    private JLabel jLabel7 = null;

    private JTextField lastUpdated = null;

    private final static int ADD = 0;

    private final static int UPDATE = 1;

    private final static int VIEW = 2;

    private int state;

    private GTSSession session;

    private TrustLevel level;

    private JPanel titlePanel = null;

    private ProgressPanel progressPanel = null;


    /**
     * This is the default constructor
     */
    public LevelOfAssuranceWindow(GTSSession session, LevelOfAssuranceRefresher refresher) {
        this(session, null, refresher);
    }


    public LevelOfAssuranceWindow(GTSSession session, TrustLevel level, LevelOfAssuranceRefresher refresher) {
        super();
        this.session = session;
        this.refresher = refresher;
        this.level = level;
        if (level != null) {
            if (level.getIsAuthority().equals(Boolean.FALSE)) {
                state = VIEW;
            } else {
                state = UPDATE;
            }
        } else {
            state = ADD;
        }
        initialize();
        if (level != null) {
            this.getTrustLevelName().setEditable(false);
            this.getTrustLevelName().setText(level.getName());
            this.getDescription().setText(level.getDescription());
            this.getDescription().setCaretPosition(0);
            this.getIsAuthority().setText(level.getIsAuthority().toString());
            this.getAuthorityGTS().setText(level.getAuthorityGTS());
            this.getSourceGTS().setText(level.getSourceGTS());
            if (level.getLastUpdated() <= 0) {
                this.getLastUpdated().setText("Unknown");
            } else {
                Calendar c = new GregorianCalendar();
                c.setTimeInMillis(level.getLastUpdated());
                this.getLastUpdated().setText(c.getTime().toString());
            }
        }
        if (state == VIEW) {
            this.getDescription().setEditable(false);
        }
    }


    /**
     * This method initializes this
     */
    private void initialize() {
        this.setSize(600, 400);
        this.setContentPane(getJContentPane());
        if (state == UPDATE) {
            this.setTitle("View/Modify Level of Assurance");
            this.setFrameIcon(GTSLookAndFeel.getTrustLevelIcon());
        } else if (state == VIEW) {
            this.setTitle("View Level of Assurance");
            this.setFrameIcon(GTSLookAndFeel.getTrustLevelIcon());
        } else {
            this.setTitle("Add Level of Assurance");
            this.setFrameIcon(GTSLookAndFeel.getTrustLevelIcon());
        }
    }


    /**
     * This method initializes jContentPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getJContentPane() {
        if (jContentPane == null) {
            GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
            gridBagConstraints4.gridx = 0;
            gridBagConstraints4.weightx = 1.0D;
            gridBagConstraints4.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints4.gridy = 3;
            GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
            gridBagConstraints3.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints3.gridy = 0;
            gridBagConstraints3.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints3.weightx = 1.0D;
            gridBagConstraints3.gridx = 0;
            GridBagConstraints gridBagConstraints31 = new GridBagConstraints();
            gridBagConstraints31.gridx = 0;
            gridBagConstraints31.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints31.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints31.weightx = 1.0D;
            gridBagConstraints31.weighty = 1.0D;
            gridBagConstraints31.gridy = 1;
            GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
            gridBagConstraints12.gridx = 0;
            gridBagConstraints12.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints12.gridy = 2;
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.ipadx = 0;
            gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
            gridBagConstraints.weightx = 1.0D;
            gridBagConstraints.gridy = 0;
            jContentPane = new JPanel();
            jContentPane.setLayout(new GridBagLayout());
            jContentPane.add(getButtonPanel(), gridBagConstraints12);
            jContentPane.add(getTrustLevelPanel(), gridBagConstraints31);
            jContentPane.add(getTitlePanel(), gridBagConstraints3);
            jContentPane.add(getProgressPanel(), gridBagConstraints4);
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
            if (state != VIEW) {
                buttonPanel.add(getAddButton(), null);
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
            getRootPane().setDefaultButton(addButton);
            if (state == UPDATE) {
                addButton.setText("Update");
            } else if (state == ADD) {
                addButton.setText("Add");
            }
            addButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    Runner runner = new Runner() {
                        public void execute() {
                            addUpdateTrustLevel();
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


    private void addUpdateTrustLevel() {
        try {
            if (state == UPDATE) {
                getProgressPanel().showProgress("Updating level of assurance...");
            } else if (state == ADD) {
                getProgressPanel().showProgress("Adding level of assurance...");
            }

            getAddButton().setEnabled(false);
            TrustLevel level = new TrustLevel();
            level.setName(getTrustLevelName().getText().trim());
            level.setDescription(getDescription().getText().trim());
            GTSAdminClient client = this.session.getAdminClient();
            if (state == UPDATE) {
                client.updateTrustLevel(level);
            } else if (state == ADD) {
                client.addTrustLevel(level);
            }
            refresher.refreshTrustLevels();

            if (state == UPDATE) {
                getProgressPanel().stopProgress("The level of assurance was successfully updated.");
            } else if (state == ADD) {
                getProgressPanel().stopProgress("The level of assurance was successfully added.");
                dispose();
            }
        } catch (Exception e) {
            getAddButton().setEnabled(true);
            ErrorDialog.showError(e);
            getProgressPanel().stopProgress("Error.");
        }

    }


    /**
     * This method initializes trustLevelPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getTrustLevelPanel() {
        if (trustLevelPanel == null) {
            GridBagConstraints gridBagConstraints17 = new GridBagConstraints();
            gridBagConstraints17.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints17.gridy = 4;
            gridBagConstraints17.weightx = 1.0;
            gridBagConstraints17.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints17.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints17.gridx = 1;
            GridBagConstraints gridBagConstraints16 = new GridBagConstraints();
            gridBagConstraints16.gridx = 0;
            gridBagConstraints16.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints16.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints16.gridy = 4;
            jLabel7 = new JLabel();
            jLabel7.setText("Last Updated");
            GridBagConstraints gridBagConstraints15 = new GridBagConstraints();
            gridBagConstraints15.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints15.gridy = 3;
            gridBagConstraints15.weightx = 1.0;
            gridBagConstraints15.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints15.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints15.gridx = 1;
            GridBagConstraints gridBagConstraints14 = new GridBagConstraints();
            gridBagConstraints14.gridx = 0;
            gridBagConstraints14.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints14.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints14.gridy = 3;
            jLabel6 = new JLabel();
            jLabel6.setText("Source GTS");
            GridBagConstraints gridBagConstraints13 = new GridBagConstraints();
            gridBagConstraints13.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints13.gridy = 2;
            gridBagConstraints13.weightx = 1.0;
            gridBagConstraints13.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints13.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints13.gridx = 1;
            GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
            gridBagConstraints11.gridx = 0;
            gridBagConstraints11.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints11.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints11.gridy = 2;
            jLabel5 = new JLabel();
            jLabel5.setText("Authority GTS");
            GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
            gridBagConstraints10.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints10.gridy = 1;
            gridBagConstraints10.weightx = 1.0;
            gridBagConstraints10.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints10.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints10.gridx = 1;
            GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
            gridBagConstraints8.gridx = 0;
            gridBagConstraints8.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints8.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints8.gridy = 1;
            jLabel4 = new JLabel();
            jLabel4.setText("Is Authority");
            GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
            gridBagConstraints9.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints9.weighty = 1.0;
            gridBagConstraints9.gridx = 0;
            gridBagConstraints9.gridy = 6;
            gridBagConstraints9.insets = new java.awt.Insets(5, 5, 5, 5);
            gridBagConstraints9.gridwidth = 2;
            gridBagConstraints9.weightx = 1.0;
            GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
            gridBagConstraints7.gridx = 0;
            gridBagConstraints7.gridwidth = 2;
            gridBagConstraints7.insets = new java.awt.Insets(5, 5, 5, 5);
            gridBagConstraints7.gridy = 5;
            jLabel3 = new JLabel();
            jLabel3.setText("Description");
            GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
            gridBagConstraints6.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints6.gridx = 1;
            gridBagConstraints6.gridy = 0;
            gridBagConstraints6.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints6.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints6.weightx = 1.0;
            GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
            gridBagConstraints1.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints1.gridy = 0;
            gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints1.gridx = 0;
            jLabel2 = new JLabel();
            jLabel2.setText("Name");
            trustLevelPanel = new JPanel();
            trustLevelPanel.setLayout(new GridBagLayout());
            trustLevelPanel.add(jLabel2, gridBagConstraints1);
            trustLevelPanel.add(getTrustLevelName(), gridBagConstraints6);
            trustLevelPanel.add(jLabel3, gridBagConstraints7);
            trustLevelPanel.add(getJScrollPane(), gridBagConstraints9);
            if (state != ADD) {
                trustLevelPanel.add(jLabel4, gridBagConstraints8);
                trustLevelPanel.add(getIsAuthority(), gridBagConstraints10);
                trustLevelPanel.add(jLabel5, gridBagConstraints11);
                trustLevelPanel.add(getAuthorityGTS(), gridBagConstraints13);
                trustLevelPanel.add(jLabel6, gridBagConstraints14);
                trustLevelPanel.add(getSourceGTS(), gridBagConstraints15);
                trustLevelPanel.add(jLabel7, gridBagConstraints16);
                trustLevelPanel.add(getLastUpdated(), gridBagConstraints17);
            }
        }
        return trustLevelPanel;
    }


    /**
     * This method initializes name
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getTrustLevelName() {
        if (trustLevelName == null) {
            trustLevelName = new JTextField();
        }
        return trustLevelName;
    }


    /**
     * This method initializes description
     * 
     * @return javax.swing.JTextArea
     */
    private JTextArea getDescription() {
        if (description == null) {
            description = new JTextArea();
            description.setLineWrap(false);
        }
        return description;
    }


    /**
     * This method initializes jScrollPane
     * 
     * @return javax.swing.JScrollPane
     */
    private JScrollPane getJScrollPane() {
        if (jScrollPane == null) {
            jScrollPane = new JScrollPane();
            jScrollPane.setViewportView(getDescription());
        }
        return jScrollPane;
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
     * This method initializes titlePanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getTitlePanel() {
        if (titlePanel == null) {
            String title = "";
            String subtitle = "";

            if (state == ADD) {
                title = "Add Level of Assurance";
                subtitle = "Add a level of assurance to " + this.session.getHandle().getDisplayName() + ".";
            } else {
                title = level.getName();
                subtitle = "Level of assurance on " + this.session.getHandle().getDisplayName() + ".";
            }

            titlePanel = new TitlePanel(title, subtitle);
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
