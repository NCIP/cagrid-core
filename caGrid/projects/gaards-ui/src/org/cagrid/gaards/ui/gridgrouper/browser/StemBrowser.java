package org.cagrid.gaards.ui.gridgrouper.browser;

import edu.internet2.middleware.subject.Subject;
import gov.nih.nci.cagrid.common.Runner;
import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.gridgrouper.grouper.StemI;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.cagrid.gaards.ui.gridgrouper.tree.GridGrouperBaseTreeNode;
import org.cagrid.gaards.ui.gridgrouper.tree.GroupTreeNode;
import org.cagrid.gaards.ui.gridgrouper.tree.StemTreeNode;
import org.cagrid.grape.GridApplication;
import org.cagrid.grape.LookAndFeel;
import org.cagrid.grape.utils.ErrorDialog;


/**
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella</A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster</A>
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings</A>
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * @version $Id: GridGrouperBaseTreeNode.java,v 1.1 2006/08/04 03:49:26 langella
 *          Exp $
 */
public class StemBrowser extends BaseBrowserPanel {

    private static final long serialVersionUID = 1L;

    private StemTreeNode node;

    private StemI stem;

    private JTabbedPane stemDetails = null;

    private JPanel details = null;

    private JPanel privileges = null;

    private JPanel childStems = null;

    private JPanel groups = null;

    private JPanel detailsPanel = null;

    private JLabel jLabel3 = null;

    private JTextField groupId = null;

    private JLabel jLabel4 = null;

    private JTextField displayName = null;

    private JLabel jLabel5 = null;

    private JTextField systemName = null;

    private JLabel displayExtensionLabel = null;

    private JTextField displayExtension = null;

    private JLabel jLabel6 = null;

    private JTextField systemExtension = null;

    private JLabel jLabel7 = null;

    private JScrollPane jScrollPane = null;

    private JTextArea description = null;

    private JButton updateStem = null;

    private JPanel privsList = null;

    private JScrollPane jScrollPane1 = null;

    private StemPrivilegesTable privs = null;

    private JPanel privButtons = null;

    private JButton getPrivileges = null;

    private JButton updatePrivilege = null;

    private JPanel stemsPanel = null;

    private JScrollPane jScrollPane2 = null;

    private StemsTable childStemsTable = null;

    private JPanel addStemPanel = null;

    private JLabel jLabel10 = null;

    private JTextField childName = null;

    private JLabel jLabel11 = null;

    private JTextField childDisplayName = null;

    private JButton addChildStem = null;

    private JPanel buttonPanel = null;

    private JButton viewStem = null;

    private JButton removeStem = null;

    private JPanel groupsPanel = null;

    private JPanel addGroupsPanel = null;

    private JScrollPane groupsPane = null;

    private GroupsTable groupsTable = null;

    private JPanel groupsButtonPanel = null;

    private JButton viewGroup = null;

    private JButton removeButton = null;

    private JLabel jLabel12 = null;

    private JTextField groupExtension = null;

    private JTextField groupDisplayExtension = null;

    private JLabel jLabel13 = null;

    private JButton addGroup = null;

    private JLabel jLabel14 = null;

    private JTextField created = null;

    private JLabel jLabel15 = null;

    private JLabel jLabel16 = null;

    private JLabel jLabel17 = null;

    private JTextField creator = null;

    private JTextField lastModified = null;

    private JTextField lastModifiedBy = null;

    private JButton addPrivileges = null;

    private StemActionListener stemListener;

    private GroupActionListener groupListener;

    private JLabel jLabel = null;

    private JTextField serviceURL = null;

    private JPanel searchPanel = null;


    /**
     * This is the default constructor
     */
    public StemBrowser(StemActionListener stemListener, GroupActionListener groupListener, StemTreeNode node) {
        super();
        this.stemListener = stemListener;
        this.groupListener = groupListener;
        this.node = node;
        this.stem = node.getStem();
        initialize();
        this.setStem();
    }


    protected void setStem() {
        this.serviceURL.setText(this.node.getGridGrouper().getName());
        this.groupId.setText(stem.getUuid());
        this.getDisplayName().setText(stem.getDisplayName());
        this.getSystemName().setText(stem.getName());
        this.getDisplayExtension().setText(stem.getDisplayExtension());
        this.getSystemExtension().setText(stem.getExtension());
        this.getDescription().setText(stem.getDescription());
        this.getCreated().setText(stem.getCreateTime().toString());
        try {
            this.getCreator().setText(stem.getCreateSubject().getId());
        } catch (Exception e) {

        }
        this.getLastModified().setText(stem.getModifyTime().toString());
        try {
            this.getLastModifiedBy().setText(stem.getModifySubject().getId());
        } catch (Exception e) {

        }
        getChildStemsTable().clearTable();
        getGroupsTable().clearTable();
        int count = node.getChildCount();
        for (int i = 0; i < count; i++) {
            GridGrouperBaseTreeNode child = (GridGrouperBaseTreeNode) node.getChildAt(i);
            if (child instanceof StemTreeNode) {
                getChildStemsTable().addStem((StemTreeNode) child);
            }
            if (child instanceof GroupTreeNode) {
                getGroupsTable().addGroup((GroupTreeNode) child);
            }
        }
    }


    /**
     * This method initializes this
     */
    private void initialize() {
        GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
        gridBagConstraints11.fill = GridBagConstraints.BOTH;
        gridBagConstraints11.weighty = 1.0;
        gridBagConstraints11.gridx = 0;
        gridBagConstraints11.gridy = 1;
        gridBagConstraints11.insets = new Insets(2, 2, 2, 2);
        gridBagConstraints11.weightx = 1.0;
        this.setSize(600, 400);
        this.setLayout(new GridBagLayout());
        this.add(getStemDetails(), gridBagConstraints11);

    }


    public StemTreeNode getStemNode() {
        return node;
    }


    /**
     * This method initializes stemDetails
     * 
     * @return javax.swing.JTabbedPane
     */
    private JTabbedPane getStemDetails() {
        if (stemDetails == null) {
            stemDetails = new JTabbedPane();
            stemDetails.addTab("Details", null, getDetails(), null);
            stemDetails.addTab("Privileges", null, getPrivileges(), null);
            stemDetails.addTab("Child Stems", null, getChildStems(), null);
            stemDetails.addTab("Groups", null, getGroups(), null);
        }
        return stemDetails;
    }


    /**
     * This method initializes details
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getDetails() {
        if (details == null) {
            details = new JPanel();
            details.setLayout(new BorderLayout());
            details.add(getDetailsPanel(), BorderLayout.CENTER);
        }
        return details;
    }


    /**
     * This method initializes privileges
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getPrivileges() {
        if (privileges == null) {
            GridBagConstraints gridBagConstraints22 = new GridBagConstraints();
            gridBagConstraints22.gridx = 0;
            gridBagConstraints22.fill = GridBagConstraints.BOTH;
            gridBagConstraints22.weighty = 1.0D;
            gridBagConstraints22.weightx = 1.0D;
            gridBagConstraints22.gridy = 0;
            privileges = new JPanel();
            privileges.setLayout(new GridBagLayout());
            privileges.add(getPrivsList(), gridBagConstraints22);
        }
        return privileges;
    }


    /**
     * This method initializes childStems
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getChildStems() {
        if (childStems == null) {
            GridBagConstraints gridBagConstraints34 = new GridBagConstraints();
            gridBagConstraints34.gridx = 0;
            gridBagConstraints34.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints34.insets = new Insets(5, 5, 5, 5);
            gridBagConstraints34.weightx = 1.0D;
            gridBagConstraints34.gridy = 1;
            GridBagConstraints gridBagConstraints32 = new GridBagConstraints();
            gridBagConstraints32.gridx = 0;
            gridBagConstraints32.insets = new Insets(5, 5, 5, 5);
            gridBagConstraints32.fill = GridBagConstraints.BOTH;
            gridBagConstraints32.weightx = 1.0D;
            gridBagConstraints32.weighty = 1.0D;
            gridBagConstraints32.gridy = 0;
            gridBagConstraints32.gridx = 0;
            gridBagConstraints32.insets = new Insets(5, 5, 5, 5);
            gridBagConstraints32.weightx = 1.0D;
            gridBagConstraints32.weighty = 1.0D;
            gridBagConstraints32.fill = GridBagConstraints.BOTH;
            gridBagConstraints32.gridy = 0;
            childStems = new JPanel();
            childStems.setLayout(new GridBagLayout());
            childStems.add(getStemsPanel(), gridBagConstraints32);
            childStems.add(getAddStemPanel(), gridBagConstraints34);
        }
        return childStems;
    }


    /**
     * This method initializes groups
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getGroups() {
        if (groups == null) {
            GridBagConstraints gridBagConstraints44 = new GridBagConstraints();
            gridBagConstraints44.gridx = 0;
            gridBagConstraints44.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints44.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints44.weightx = 1.0D;
            gridBagConstraints44.gridy = 1;
            GridBagConstraints gridBagConstraints43 = new GridBagConstraints();
            gridBagConstraints43.gridx = 0;
            gridBagConstraints43.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints43.weightx = 1.0D;
            gridBagConstraints43.weighty = 1.0D;
            gridBagConstraints43.fill = GridBagConstraints.BOTH;
            gridBagConstraints43.gridy = 0;
            groups = new JPanel();
            groups.setLayout(new GridBagLayout());
            groups.add(getGroupsPanel(), gridBagConstraints43);
            groups.add(getAddGroupsPanel(), gridBagConstraints44);
        }
        return groups;
    }


    /**
     * This method initializes detailsPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getDetailsPanel() {
        if (detailsPanel == null) {
            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            gridBagConstraints2.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints2.gridy = 0;
            gridBagConstraints2.weightx = 1.0;
            gridBagConstraints2.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints2.gridx = 1;
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.anchor = GridBagConstraints.WEST;
            gridBagConstraints.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints.gridy = 0;
            jLabel = new JLabel();
            jLabel.setText("Grid Grouper");
            GridBagConstraints gridBagConstraints61 = new GridBagConstraints();
            gridBagConstraints61.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints61.gridy = 9;
            gridBagConstraints61.weightx = 1.0;
            gridBagConstraints61.anchor = GridBagConstraints.WEST;
            gridBagConstraints61.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints61.gridx = 1;
            GridBagConstraints gridBagConstraints60 = new GridBagConstraints();
            gridBagConstraints60.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints60.gridy = 8;
            gridBagConstraints60.weightx = 1.0;
            gridBagConstraints60.anchor = GridBagConstraints.WEST;
            gridBagConstraints60.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints60.gridx = 1;
            GridBagConstraints gridBagConstraints59 = new GridBagConstraints();
            gridBagConstraints59.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints59.gridy = 7;
            gridBagConstraints59.weightx = 1.0;
            gridBagConstraints59.anchor = GridBagConstraints.WEST;
            gridBagConstraints59.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints59.gridx = 1;
            GridBagConstraints gridBagConstraints58 = new GridBagConstraints();
            gridBagConstraints58.gridx = 0;
            gridBagConstraints58.anchor = GridBagConstraints.WEST;
            gridBagConstraints58.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints58.gridy = 9;
            jLabel17 = new JLabel();
            jLabel17.setText("Last Modified By");
            GridBagConstraints gridBagConstraints57 = new GridBagConstraints();
            gridBagConstraints57.gridx = 0;
            gridBagConstraints57.anchor = GridBagConstraints.WEST;
            gridBagConstraints57.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints57.gridy = 8;
            jLabel16 = new JLabel();
            jLabel16.setText("Last Modified");
            GridBagConstraints gridBagConstraints56 = new GridBagConstraints();
            gridBagConstraints56.gridx = 0;
            gridBagConstraints56.anchor = GridBagConstraints.WEST;
            gridBagConstraints56.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints56.gridy = 7;
            jLabel15 = new JLabel();
            jLabel15.setText("Created By");
            GridBagConstraints gridBagConstraints55 = new GridBagConstraints();
            gridBagConstraints55.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints55.gridy = 6;
            gridBagConstraints55.weightx = 1.0;
            gridBagConstraints55.anchor = GridBagConstraints.WEST;
            gridBagConstraints55.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints55.gridx = 1;
            GridBagConstraints gridBagConstraints54 = new GridBagConstraints();
            gridBagConstraints54.gridx = 0;
            gridBagConstraints54.anchor = GridBagConstraints.WEST;
            gridBagConstraints54.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints54.gridy = 6;
            jLabel14 = new JLabel();
            jLabel14.setText("Created");
            GridBagConstraints gridBagConstraints20 = new GridBagConstraints();
            gridBagConstraints20.gridx = 0;
            gridBagConstraints20.insets = new Insets(5, 5, 5, 5);
            gridBagConstraints20.gridwidth = 2;
            gridBagConstraints20.gridy = 12;
            GridBagConstraints gridBagConstraints19 = new GridBagConstraints();
            gridBagConstraints19.fill = GridBagConstraints.BOTH;
            gridBagConstraints19.weighty = 1.0;
            gridBagConstraints19.gridx = 0;
            gridBagConstraints19.gridy = 11;
            gridBagConstraints19.gridwidth = 2;
            gridBagConstraints19.insets = new Insets(5, 5, 5, 5);
            gridBagConstraints19.weightx = 1.0;
            GridBagConstraints gridBagConstraints18 = new GridBagConstraints();
            gridBagConstraints18.gridx = 0;
            gridBagConstraints18.insets = new Insets(5, 5, 5, 5);
            gridBagConstraints18.gridwidth = 2;
            gridBagConstraints18.gridy = 10;
            jLabel7 = new JLabel();
            jLabel7.setText("Description");
            GridBagConstraints gridBagConstraints17 = new GridBagConstraints();
            gridBagConstraints17.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints17.gridy = 5;
            gridBagConstraints17.weightx = 1.0;
            gridBagConstraints17.anchor = GridBagConstraints.WEST;
            gridBagConstraints17.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints17.gridx = 1;
            GridBagConstraints gridBagConstraints16 = new GridBagConstraints();
            gridBagConstraints16.anchor = GridBagConstraints.WEST;
            gridBagConstraints16.gridx = 0;
            gridBagConstraints16.gridy = 5;
            gridBagConstraints16.insets = new Insets(2, 2, 2, 2);
            jLabel6 = new JLabel();
            jLabel6.setText("System Extension");
            GridBagConstraints gridBagConstraints15 = new GridBagConstraints();
            gridBagConstraints15.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints15.gridy = 4;
            gridBagConstraints15.weightx = 1.0;
            gridBagConstraints15.anchor = GridBagConstraints.WEST;
            gridBagConstraints15.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints15.gridx = 1;
            GridBagConstraints gridBagConstraints14 = new GridBagConstraints();
            gridBagConstraints14.gridx = 0;
            gridBagConstraints14.anchor = GridBagConstraints.WEST;
            gridBagConstraints14.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints14.gridy = 4;
            displayExtensionLabel = new JLabel();
            displayExtensionLabel.setText("Display Extension");
            GridBagConstraints gridBagConstraints13 = new GridBagConstraints();
            gridBagConstraints13.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints13.gridy = 3;
            gridBagConstraints13.weightx = 1.0;
            gridBagConstraints13.anchor = GridBagConstraints.WEST;
            gridBagConstraints13.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints13.gridx = 1;
            GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
            gridBagConstraints12.gridx = 0;
            gridBagConstraints12.anchor = GridBagConstraints.WEST;
            gridBagConstraints12.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints12.gridy = 3;
            jLabel5 = new JLabel();
            jLabel5.setText("System Name");
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
            jLabel4 = new JLabel();
            jLabel4.setText("Display Name");
            GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
            gridBagConstraints8.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints8.gridx = 1;
            gridBagConstraints8.gridy = 1;
            gridBagConstraints8.anchor = GridBagConstraints.WEST;
            gridBagConstraints8.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints8.weightx = 1.0;
            GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
            gridBagConstraints7.anchor = GridBagConstraints.WEST;
            gridBagConstraints7.gridy = 1;
            gridBagConstraints7.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints7.gridx = 0;
            jLabel3 = new JLabel();
            jLabel3.setText("Stem Id");
            detailsPanel = new JPanel();
            detailsPanel.setLayout(new GridBagLayout());
            detailsPanel.add(jLabel3, gridBagConstraints7);
            detailsPanel.add(getGroupId(), gridBagConstraints8);
            detailsPanel.add(jLabel4, gridBagConstraints9);
            detailsPanel.add(getDisplayName(), gridBagConstraints10);
            detailsPanel.add(jLabel5, gridBagConstraints12);
            detailsPanel.add(getSystemName(), gridBagConstraints13);
            detailsPanel.add(displayExtensionLabel, gridBagConstraints14);
            detailsPanel.add(getDisplayExtension(), gridBagConstraints15);
            detailsPanel.add(jLabel6, gridBagConstraints16);
            detailsPanel.add(getSystemExtension(), gridBagConstraints17);
            detailsPanel.add(jLabel7, gridBagConstraints18);
            detailsPanel.add(getJScrollPane(), gridBagConstraints19);
            detailsPanel.add(getUpdateStem(), gridBagConstraints20);
            detailsPanel.add(jLabel14, gridBagConstraints54);
            detailsPanel.add(getCreated(), gridBagConstraints55);
            detailsPanel.add(jLabel15, gridBagConstraints56);
            detailsPanel.add(jLabel16, gridBagConstraints57);
            detailsPanel.add(jLabel17, gridBagConstraints58);
            detailsPanel.add(getCreator(), gridBagConstraints59);
            detailsPanel.add(getLastModified(), gridBagConstraints60);
            detailsPanel.add(getLastModifiedBy(), gridBagConstraints61);
            detailsPanel.add(jLabel, gridBagConstraints);
            detailsPanel.add(getServiceURL(), gridBagConstraints2);
        }
        return detailsPanel;
    }


    /**
     * This method initializes groupId
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getGroupId() {
        if (groupId == null) {
            groupId = new JTextField();
            groupId.setEnabled(true);
            groupId.setEditable(false);
        }
        return groupId;
    }


    /**
     * This method initializes displayName
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getDisplayName() {
        if (displayName == null) {
            displayName = new JTextField();
            displayName.setEditable(false);
        }
        return displayName;
    }


    /**
     * This method initializes systemName
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getSystemName() {
        if (systemName == null) {
            systemName = new JTextField();
            systemName.setEditable(false);
        }
        return systemName;
    }


    /**
     * This method initializes displayExtension
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getDisplayExtension() {
        if (displayExtension == null) {
            displayExtension = new JTextField();
            displayExtension.addCaretListener(new javax.swing.event.CaretListener() {
                public void caretUpdate(javax.swing.event.CaretEvent e) {
                    monitorUpdate();
                }
            });

        }
        return displayExtension;
    }


    private void monitorUpdate() {
        if (!getDisplayExtension().getText().equals(stem.getDisplayExtension())) {
            this.getUpdateStem().setEnabled(true);
        } else if (!getDescription().getText().equals(stem.getDescription())) {
            this.getUpdateStem().setEnabled(true);
        } else {
            this.getUpdateStem().setEnabled(false);
        }
    }


    /**
     * This method initializes systemExtension
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getSystemExtension() {
        if (systemExtension == null) {
            systemExtension = new JTextField();
            systemExtension.setEditable(false);
        }
        return systemExtension;
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
     * This method initializes description
     * 
     * @return javax.swing.JTextArea
     */
    private JTextArea getDescription() {
        if (description == null) {
            description = new JTextArea();
            description.setLineWrap(true);
            description.addCaretListener(new javax.swing.event.CaretListener() {
                public void caretUpdate(javax.swing.event.CaretEvent e) {
                    monitorUpdate();
                }
            });
        }
        return description;
    }


    /**
     * This method initializes updateStem
     * 
     * @return javax.swing.JButton
     */
    private JButton getUpdateStem() {
        if (updateStem == null) {
            updateStem = new JButton();
            updateStem.setText("Update Stem");
            updateStem.setEnabled(false);
            updateStem.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    Runner runner = new Runner() {
                        public void execute() {
                            updateStem();
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
        return updateStem;
    }


    private void updateStem() {
        try {
            if (!getDisplayExtension().getText().equals(stem.getDisplayExtension())) {
                stem.setDisplayExtension(getDisplayExtension().getText());
            }
            if (!getDescription().getText().equals(stem.getDescription())) {
                stem.setDescription(getDescription().getText());
            }
            node.refresh();
            setStem();
            this.monitorUpdate();
        } catch (Exception e) {
            ErrorDialog.showError(e);
            node.refresh();
            this.monitorUpdate();
        }
    }


    /**
     * This method initializes privsList
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getPrivsList() {
        if (privsList == null) {
            GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
            gridBagConstraints1.gridx = 0;
            gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints1.weightx = 10.0D;
            gridBagConstraints1.gridy = 0;
            GridBagConstraints gridBagConstraints23 = new GridBagConstraints();
            gridBagConstraints23.gridx = 0;
            gridBagConstraints23.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints23.weightx = 1.0D;
            gridBagConstraints23.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints23.gridy = 2;
            GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
            gridBagConstraints21.fill = GridBagConstraints.BOTH;
            gridBagConstraints21.weighty = 1.0;
            gridBagConstraints21.gridx = 0;
            gridBagConstraints21.gridy = 1;
            gridBagConstraints21.weightx = 1.0;
            privsList = new JPanel();
            privsList.setLayout(new GridBagLayout());
            privsList.add(getJScrollPane1(), gridBagConstraints21);
            privsList.add(getPrivButtons(), gridBagConstraints23);
            privsList.add(getSearchPanel(), gridBagConstraints1);
        }
        return privsList;
    }


    /**
     * This method initializes jScrollPane1
     * 
     * @return javax.swing.JScrollPane
     */
    private JScrollPane getJScrollPane1() {
        if (jScrollPane1 == null) {
            jScrollPane1 = new JScrollPane();
            jScrollPane1.setViewportView(getPrivs());
        }
        return jScrollPane1;
    }


    /**
     * This method initializes privs
     * 
     * @return javax.swing.JTable
     */
    private StemPrivilegesTable getPrivs() {
        if (privs == null) {
            privs = new StemPrivilegesTable(this);
        }
        return privs;
    }


    /**
     * This method initializes privButtons
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getPrivButtons() {
        if (privButtons == null) {
            privButtons = new JPanel();
            privButtons.setLayout(new FlowLayout());
            privButtons.add(getAddPrivileges(), null);
            privButtons.add(getUpdatePrivilege(), null);
        }
        return privButtons;
    }


    /**
     * This method initializes getPrivileges
     * 
     * @return javax.swing.JButton
     */
    private JButton getGetPrivileges() {
        if (getPrivileges == null) {
            getPrivileges = new JButton();
            getPrivileges.setText("Search");
            getPrivileges.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    loadPrivileges();
                }

            });
        }
        return getPrivileges;
    }


    protected void loadPrivileges() {
        Runner runner = new Runner() {
            public void execute() {

                synchronized (getPrivs()) {
                    int eid = startEvent("Loading the privileges for " + stem.getDisplayExtension() + "...");
                    try {

                        getPrivs().clearTable();
                        Map map = new HashMap();
                        Set s1 = stem.getStemmers();
                        Iterator itr1 = s1.iterator();
                        while (itr1.hasNext()) {
                            Subject sub = (Subject) itr1.next();
                            StemPrivilegeCaddy caddy = new StemPrivilegeCaddy(sub.getId());
                            caddy.setStem(true);
                            map.put(caddy.getIdentity(), caddy);
                        }

                        Set s2 = stem.getCreators();
                        Iterator itr2 = s2.iterator();
                        while (itr2.hasNext()) {
                            Subject sub = (Subject) itr2.next();
                            StemPrivilegeCaddy caddy = null;
                            if (map.containsKey(sub.getId())) {
                                caddy = (StemPrivilegeCaddy) map.get(sub.getId());
                            } else {
                                caddy = new StemPrivilegeCaddy(sub.getId());
                                map.put(caddy.getIdentity(), caddy);
                            }
                            caddy.setCreate(true);
                        }

                        Iterator itr3 = map.values().iterator();
                        while (itr3.hasNext()) {
                            getPrivs().addPrivilege((StemPrivilegeCaddy) itr3.next());
                        }

                        stopEvent(eid, "Loaded the privileges for " + stem.getDisplayExtension() + "!!!");
                    } catch (Exception e) {
                        stopEvent(eid, "Error loading the privileges for " + stem.getDisplayExtension() + "!!!");
                        ErrorDialog.showError(e);
                    }
                }
            }
        };
        try {
            GridApplication.getContext().executeInBackground(runner);
        } catch (Exception t) {
            t.getMessage();
        }
    }


    /**
     * This method initializes updatePrivilege
     * 
     * @return javax.swing.JButton
     */
    private JButton getUpdatePrivilege() {
        if (updatePrivilege == null) {
            updatePrivilege = new JButton();
            updatePrivilege.setText("View");
            updatePrivilege.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    Runner runner = new Runner() {
                        public void execute() {
                            try {
                                getPrivs().doubleClick();
                            } catch (Exception ex) {
                                ErrorDialog.showError(ex);
                            }
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
        return updatePrivilege;
    }


    public StemI getStem() {
        return stem;
    }


    /**
     * This method initializes stemsPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getStemsPanel() {
        if (stemsPanel == null) {
            GridBagConstraints gridBagConstraints40 = new GridBagConstraints();
            gridBagConstraints40.gridx = 0;
            gridBagConstraints40.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints40.weightx = 1.0D;
            gridBagConstraints40.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints40.gridy = 1;
            GridBagConstraints gridBagConstraints33 = new GridBagConstraints();
            gridBagConstraints33.fill = GridBagConstraints.BOTH;
            gridBagConstraints33.weighty = 1.0;
            gridBagConstraints33.gridx = 0;
            gridBagConstraints33.gridy = 0;
            gridBagConstraints33.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints33.weightx = 1.0;
            stemsPanel = new JPanel();
            stemsPanel.setLayout(new GridBagLayout());
            stemsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Child Stems",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION, null, LookAndFeel.getPanelLabelColor()));
            stemsPanel.add(getJScrollPane2(), gridBagConstraints33);
            stemsPanel.add(getButtonPanel(), gridBagConstraints40);
        }
        return stemsPanel;
    }


    /**
     * This method initializes jScrollPane2
     * 
     * @return javax.swing.JScrollPane
     */
    private JScrollPane getJScrollPane2() {
        if (jScrollPane2 == null) {
            jScrollPane2 = new JScrollPane();
            jScrollPane2.setViewportView(getChildStemsTable());
        }
        return jScrollPane2;
    }


    /**
     * This method initializes childStemsTable
     * 
     * @return javax.swing.JTable
     */
    private StemsTable getChildStemsTable() {
        if (childStemsTable == null) {
            childStemsTable = new StemsTable(stemListener);
        }
        return childStemsTable;
    }


    /**
     * This method initializes addStemPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getAddStemPanel() {
        if (addStemPanel == null) {
            GridBagConstraints gridBagConstraints39 = new GridBagConstraints();
            gridBagConstraints39.gridx = 0;
            gridBagConstraints39.gridwidth = 2;
            gridBagConstraints39.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints39.gridy = 2;
            GridBagConstraints gridBagConstraints38 = new GridBagConstraints();
            gridBagConstraints38.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints38.gridy = 1;
            gridBagConstraints38.weightx = 1.0;
            gridBagConstraints38.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints38.anchor = GridBagConstraints.WEST;
            gridBagConstraints38.gridx = 1;
            GridBagConstraints gridBagConstraints37 = new GridBagConstraints();
            gridBagConstraints37.anchor = GridBagConstraints.WEST;
            gridBagConstraints37.gridy = 1;
            gridBagConstraints37.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints37.gridx = 0;
            jLabel11 = new JLabel();
            jLabel11.setText("Local Display Name");
            GridBagConstraints gridBagConstraints36 = new GridBagConstraints();
            gridBagConstraints36.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints36.gridx = 1;
            gridBagConstraints36.gridy = 0;
            gridBagConstraints36.anchor = GridBagConstraints.WEST;
            gridBagConstraints36.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints36.weightx = 1.0;
            GridBagConstraints gridBagConstraints35 = new GridBagConstraints();
            gridBagConstraints35.anchor = GridBagConstraints.WEST;
            gridBagConstraints35.gridy = 0;
            gridBagConstraints35.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints35.gridx = 0;
            jLabel10 = new JLabel();
            jLabel10.setText("Local Name");
            addStemPanel = new JPanel();
            addStemPanel.setLayout(new GridBagLayout());
            addStemPanel.setBorder(BorderFactory.createTitledBorder(null, "Add Stem",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION, null, LookAndFeel.getPanelLabelColor()));
            addStemPanel.add(jLabel10, gridBagConstraints35);
            addStemPanel.add(getChildName(), gridBagConstraints36);
            addStemPanel.add(jLabel11, gridBagConstraints37);
            addStemPanel.add(getChildDisplayName(), gridBagConstraints38);
            addStemPanel.add(getAddChildStem(), gridBagConstraints39);
        }
        return addStemPanel;
    }


    /**
     * This method initializes childName
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getChildName() {
        if (childName == null) {
            childName = new JTextField();
        }
        return childName;
    }


    /**
     * This method initializes childDisplayName
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getChildDisplayName() {
        if (childDisplayName == null) {
            childDisplayName = new JTextField();
        }
        return childDisplayName;
    }


    /**
     * This method initializes addChildStem
     * 
     * @return javax.swing.JButton
     */
    private JButton getAddChildStem() {
        if (addChildStem == null) {
            addChildStem = new JButton();
            addChildStem.setText("Add");
            addChildStem.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    Runner runner = new Runner() {
                        public void execute() {
                            int eid = startEvent("Adding a child stem....");
                            try {

                                String ext = Utils.clean(childName.getText());
                                if (ext == null) {
                                    ErrorDialog.showError("You must enter a local name for the stem!!!");
                                    return;
                                }

                                String disExt = Utils.clean(childDisplayName.getText());
                                if (disExt == null) {
                                    ErrorDialog.showError("You must enter a local display name for the stem!!!");
                                    return;
                                }

                                stem.addChildStem(ext, disExt);
                                node.refresh();
                                setStem();
                                stopEvent(eid, "Successfully added a child stem!!!");
                            } catch (Exception ex) {
                                stopEvent(eid, "Error adding a child stem!!!");
                                ErrorDialog.showError(ex);
                            }
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
        return addChildStem;
    }


    /**
     * This method initializes buttonPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getButtonPanel() {
        if (buttonPanel == null) {
            GridBagConstraints gridBagConstraints42 = new GridBagConstraints();
            gridBagConstraints42.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints42.gridy = 0;
            gridBagConstraints42.gridx = 1;
            GridBagConstraints gridBagConstraints41 = new GridBagConstraints();
            gridBagConstraints41.gridx = 0;
            gridBagConstraints41.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints41.gridy = 0;
            buttonPanel = new JPanel();
            buttonPanel.setLayout(new GridBagLayout());
            buttonPanel.add(getViewStem(), gridBagConstraints41);
            buttonPanel.add(getRemoveStem(), gridBagConstraints42);
        }
        return buttonPanel;
    }


    /**
     * This method initializes viewStem
     * 
     * @return javax.swing.JButton
     */
    private JButton getViewStem() {
        if (viewStem == null) {
            viewStem = new JButton();
            viewStem.setText("View");
            viewStem.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    try {
                        getChildStemsTable().doubleClick();
                    } catch (Exception ex) {
                        ErrorDialog.showError(ex);
                    }
                }
            });
        }
        return viewStem;
    }


    /**
     * This method initializes removeStem
     * 
     * @return javax.swing.JButton
     */
    private JButton getRemoveStem() {
        if (removeStem == null) {
            removeStem = new JButton();
            removeStem.setText("Remove");
            removeStem.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    Runner runner = new Runner() {
                        public void execute() {

                            StemTreeNode child = null;
                            try {
                                child = getChildStemsTable().getSelectedStem();
                            } catch (Exception ex) {
                                ErrorDialog.showError(ex);
                                return;
                            }

                            int eid = startEvent("Removing child stem....");
                            try {
                                child.getStem().delete();
                                node.refresh();
                                setStem();
                                node.getTree().stopEvent(eid, "Successfully removed the child stem!!!");
                            } catch (Exception ex) {
                                stopEvent(eid, "Error removing the child stem!!!");
                                ErrorDialog.showError(ex);
                            }
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
        return removeStem;
    }


    /**
     * This method initializes groupsPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getGroupsPanel() {
        if (groupsPanel == null) {
            GridBagConstraints gridBagConstraints46 = new GridBagConstraints();
            gridBagConstraints46.gridx = 0;
            gridBagConstraints46.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints46.gridy = 1;
            GridBagConstraints gridBagConstraints45 = new GridBagConstraints();
            gridBagConstraints45.fill = GridBagConstraints.BOTH;
            gridBagConstraints45.weighty = 1.0;
            gridBagConstraints45.gridx = 0;
            gridBagConstraints45.gridy = 0;
            gridBagConstraints45.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints45.weightx = 1.0;
            groupsPanel = new JPanel();
            groupsPanel.setLayout(new GridBagLayout());
            groupsPanel.setBorder(BorderFactory.createTitledBorder(null, "Child Group(s)",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION, null, LookAndFeel.getPanelLabelColor()));
            groupsPanel.add(getGroupsPane(), gridBagConstraints45);
            groupsPanel.add(getGroupsButtonPanel(), gridBagConstraints46);
        }
        return groupsPanel;
    }


    /**
     * This method initializes addGroupsPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getAddGroupsPanel() {
        if (addGroupsPanel == null) {
            GridBagConstraints gridBagConstraints53 = new GridBagConstraints();
            gridBagConstraints53.gridx = 0;
            gridBagConstraints53.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints53.gridwidth = 2;
            gridBagConstraints53.gridy = 2;
            GridBagConstraints gridBagConstraints52 = new GridBagConstraints();
            gridBagConstraints52.anchor = GridBagConstraints.WEST;
            gridBagConstraints52.gridy = 1;
            gridBagConstraints52.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints52.gridx = 0;
            jLabel13 = new JLabel();
            jLabel13.setText("Local Display Name");
            GridBagConstraints gridBagConstraints51 = new GridBagConstraints();
            gridBagConstraints51.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints51.gridy = 1;
            gridBagConstraints51.weightx = 1.0;
            gridBagConstraints51.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints51.gridx = 1;
            GridBagConstraints gridBagConstraints50 = new GridBagConstraints();
            gridBagConstraints50.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints50.anchor = GridBagConstraints.WEST;
            gridBagConstraints50.gridx = 1;
            gridBagConstraints50.gridy = 0;
            gridBagConstraints50.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints50.weightx = 1.0;
            GridBagConstraints gridBagConstraints49 = new GridBagConstraints();
            gridBagConstraints49.anchor = GridBagConstraints.WEST;
            gridBagConstraints49.gridy = 0;
            gridBagConstraints49.gridx = 0;
            jLabel12 = new JLabel();
            jLabel12.setText("Local Name");
            addGroupsPanel = new JPanel();
            addGroupsPanel.setLayout(new GridBagLayout());
            addGroupsPanel.setBorder(BorderFactory.createTitledBorder(null, "Add Group",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION, null, LookAndFeel.getPanelLabelColor()));
            addGroupsPanel.add(jLabel13, gridBagConstraints52);
            addGroupsPanel.add(jLabel12, gridBagConstraints49);
            addGroupsPanel.add(getGroupExtension(), gridBagConstraints50);
            addGroupsPanel.add(getGroupDisplayExtension(), gridBagConstraints51);
            addGroupsPanel.add(getAddGroup(), gridBagConstraints53);
        }
        return addGroupsPanel;
    }


    /**
     * This method initializes groupsPane
     * 
     * @return javax.swing.JScrollPane
     */
    private JScrollPane getGroupsPane() {
        if (groupsPane == null) {
            groupsPane = new JScrollPane();
            groupsPane.setViewportView(getGroupsTable());
        }
        return groupsPane;
    }


    /**
     * This method initializes groupsTable
     * 
     * @return javax.swing.JTable
     */
    private GroupsTable getGroupsTable() {
        if (groupsTable == null) {
            groupsTable = new GroupsTable(groupListener);
        }
        return groupsTable;
    }


    /**
     * This method initializes groupsButtonPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getGroupsButtonPanel() {
        if (groupsButtonPanel == null) {
            GridBagConstraints gridBagConstraints48 = new GridBagConstraints();
            gridBagConstraints48.gridx = 1;
            gridBagConstraints48.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints48.gridy = 0;
            GridBagConstraints gridBagConstraints47 = new GridBagConstraints();
            gridBagConstraints47.gridx = 0;
            gridBagConstraints47.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints47.gridy = 0;
            groupsButtonPanel = new JPanel();
            groupsButtonPanel.setLayout(new GridBagLayout());
            groupsButtonPanel.add(getViewGroup(), gridBagConstraints47);
            groupsButtonPanel.add(getRemoveButton(), gridBagConstraints48);
        }
        return groupsButtonPanel;
    }


    /**
     * This method initializes viewGroup
     * 
     * @return javax.swing.JButton
     */
    private JButton getViewGroup() {
        if (viewGroup == null) {
            viewGroup = new JButton();
            viewGroup.setText("View");
            viewGroup.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    try {
                        getGroupsTable().doubleClick();
                    } catch (Exception ex) {
                        ErrorDialog.showError(ex);
                    }
                }
            });
        }
        return viewGroup;
    }


    /**
     * This method initializes removeButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getRemoveButton() {
        if (removeButton == null) {
            removeButton = new JButton();
            removeButton.setText("Remove");
            removeButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    Runner runner = new Runner() {
                        public void execute() {

                            GroupTreeNode child = null;
                            try {
                                child = getGroupsTable().getSelectedGroup();
                            } catch (Exception ex) {
                                ErrorDialog.showError(ex);
                                return;
                            }

                            int eid = startEvent("Removing the child group....");
                            try {
                                child.getGroup().delete();
                                node.refresh();
                                setStem();
                                stopEvent(eid, "Successfully removed the child group!!!");
                            } catch (Exception ex) {
                                stopEvent(eid, "Error removing the child group!!!");
                                ErrorDialog.showError(ex);
                            }
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
        return removeButton;
    }


    /**
     * This method initializes groupExtension
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getGroupExtension() {
        if (groupExtension == null) {
            groupExtension = new JTextField();
        }
        return groupExtension;
    }


    /**
     * This method initializes groupDisplayExtension
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getGroupDisplayExtension() {
        if (groupDisplayExtension == null) {
            groupDisplayExtension = new JTextField();
        }
        return groupDisplayExtension;
    }


    /**
     * This method initializes addGroup
     * 
     * @return javax.swing.JButton
     */
    private JButton getAddGroup() {
        if (addGroup == null) {
            addGroup = new JButton();
            addGroup.setText("Add");
            addGroup.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    Runner runner = new Runner() {
                        public void execute() {
                            int eid = startEvent("Adding a child group....");
                            try {

                                String ext = Utils.clean(getGroupExtension().getText());
                                if (ext == null) {
                                    ErrorDialog.showError("You must enter a local name for the group!!!");
                                    return;
                                }

                                String disExt = Utils.clean(getGroupDisplayExtension().getText());
                                if (disExt == null) {
                                    ErrorDialog.showError("You must enter a local display name for the group!!!");
                                    return;
                                }

                                stem.addChildGroup(ext, disExt);
                                node.refresh();
                                setStem();
                                stopEvent(eid, "Successfully added a child group!!!");
                            } catch (Exception ex) {
                                stopEvent(eid, "Error adding a child group!!!");
                                ErrorDialog.showError(ex);
                            }
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
        return addGroup;
    }


    /**
     * This method initializes created
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getCreated() {
        if (created == null) {
            created = new JTextField();
            created.setEditable(false);
        }
        return created;
    }


    /**
     * This method initializes creator
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getCreator() {
        if (creator == null) {
            creator = new JTextField();
            creator.setEditable(false);
        }
        return creator;
    }


    /**
     * This method initializes lastModified
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getLastModified() {
        if (lastModified == null) {
            lastModified = new JTextField();
            lastModified.setEditable(false);
        }
        return lastModified;
    }


    /**
     * This method initializes lastModifiedBy
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getLastModifiedBy() {
        if (lastModifiedBy == null) {
            lastModifiedBy = new JTextField();
            lastModifiedBy.setEditable(false);
        }
        return lastModifiedBy;
    }


    /**
     * This method initializes addPrivileges
     * 
     * @return javax.swing.JButton
     */
    private JButton getAddPrivileges() {
        if (addPrivileges == null) {
            addPrivileges = new JButton();
            addPrivileges.setText("Add");
            final StemBrowser sb = this;
            addPrivileges.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    Runner runner = new Runner() {
                        public void execute() {
                            try {
                                GridApplication.getContext().addApplicationComponent(new StemPrivilegeWindow(sb), 600,
                                    225);

                            } catch (Exception ex) {
                                ErrorDialog.showError(ex);
                            }
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
        return addPrivileges;
    }


    /**
     * This method initializes serviceURL
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getServiceURL() {
        if (serviceURL == null) {
            serviceURL = new JTextField();
            serviceURL.setEditable(false);
        }
        return serviceURL;
    }


    /**
     * This method initializes searchPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getSearchPanel() {
        if (searchPanel == null) {
            searchPanel = new JPanel();
            searchPanel.setLayout(new FlowLayout());
            searchPanel.add(getGetPrivileges(), null);
        }
        return searchPanel;
    }
}
