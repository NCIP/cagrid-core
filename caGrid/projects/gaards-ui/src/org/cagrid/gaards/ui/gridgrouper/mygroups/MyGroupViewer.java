package org.cagrid.gaards.ui.gridgrouper.mygroups;

import gov.nih.nci.cagrid.gridgrouper.client.Group;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.cagrid.gaards.ui.common.TitlePanel;
import org.cagrid.gaards.ui.gridgrouper.GridGrouperLookAndFeel;
import org.cagrid.grape.ApplicationComponent;


public class MyGroupViewer extends ApplicationComponent {

    private static final long serialVersionUID = 1L;

    private JPanel jContentPane = null;

    private Group grp;

    private JPanel detailsPanel = null;

    private JLabel jLabel = null;

    private JTextField gridGrouper = null;

    private JLabel jLabel1 = null;

    private JLabel jLabel2 = null;

    private JLabel jLabel3 = null;

    private JLabel jLabel4 = null;

    private JLabel jLabel5 = null;

    private JTextField displayNamespace = null;

    private JTextField displayName = null;

    private JTextField groupId = null;

    private JTextField namespace = null;

    private JTextField grpName = null;

    private JPanel descriptionPanel = null;

    private JLabel jLabel6 = null;

    private JScrollPane jScrollPane = null;

    private JTextArea description = null;

    private JPanel titlePanel = null;


    /**
     * This is the default constructor
     */
    public MyGroupViewer(Group grp) {
        super();
        this.grp = grp;
        initialize();
    }


    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setSize(400, 400);
        this.setContentPane(getJContentPane());
        this.setTitle("Group Details");
        this.setFrameIcon(GridGrouperLookAndFeel.getGroupIcon22x22());
    }


    /**
     * This method initializes jContentPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getJContentPane() {
        if (jContentPane == null) {
            GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
            gridBagConstraints21.gridx = 0;
            gridBagConstraints21.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints21.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints21.weightx = 1.0D;
            gridBagConstraints21.gridy = 0;
            GridBagConstraints gridBagConstraints14 = new GridBagConstraints();
            gridBagConstraints14.gridx = 0;
            gridBagConstraints14.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints14.fill = GridBagConstraints.BOTH;
            gridBagConstraints14.weightx = 1.0D;
            gridBagConstraints14.weighty = 1.0D;
            gridBagConstraints14.gridy = 2;

            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridheight = 1;
            gridBagConstraints.gridy = 1;
            gridBagConstraints.ipadx = 0;
            gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints.weightx = 1.0D;
            gridBagConstraints.gridx = 0;
            jContentPane = new JPanel();
            jContentPane.setLayout(new GridBagLayout());
            jContentPane.add(getDetailsPanel(), gridBagConstraints);
            jContentPane.add(getDescriptionPanel(), gridBagConstraints14);
            jContentPane.add(getTitlePanel(), gridBagConstraints21);
        }
        return jContentPane;
    }


    /**
     * This method initializes detailsPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getDetailsPanel() {
        if (detailsPanel == null) {
            GridBagConstraints gridBagConstraints13 = new GridBagConstraints();
            gridBagConstraints13.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints13.anchor = GridBagConstraints.WEST;
            gridBagConstraints13.gridx = 1;
            gridBagConstraints13.gridy = 5;
            gridBagConstraints13.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints13.weightx = 1.0;
            GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
            gridBagConstraints12.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints12.gridy = 4;
            gridBagConstraints12.weightx = 1.0;
            gridBagConstraints12.anchor = GridBagConstraints.WEST;
            gridBagConstraints12.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints12.gridx = 1;
            GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
            gridBagConstraints11.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints11.gridy = 3;
            gridBagConstraints11.weightx = 1.0;
            gridBagConstraints11.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints11.anchor = GridBagConstraints.WEST;
            gridBagConstraints11.gridx = 1;
            GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
            gridBagConstraints10.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints10.gridy = 2;
            gridBagConstraints10.weightx = 1.0;
            gridBagConstraints10.anchor = GridBagConstraints.WEST;
            gridBagConstraints10.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints10.gridx = 1;
            GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
            gridBagConstraints9.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints9.gridy = 1;
            gridBagConstraints9.weightx = 1.0;
            gridBagConstraints9.anchor = GridBagConstraints.WEST;
            gridBagConstraints9.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints9.gridx = 1;
            GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
            gridBagConstraints8.gridx = 0;
            gridBagConstraints8.anchor = GridBagConstraints.WEST;
            gridBagConstraints8.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints8.gridy = 5;
            jLabel5 = new JLabel();
            jLabel5.setText("Name");
            GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
            gridBagConstraints7.gridx = 0;
            gridBagConstraints7.anchor = GridBagConstraints.WEST;
            gridBagConstraints7.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints7.gridy = 4;
            jLabel4 = new JLabel();
            jLabel4.setText("Namespace");
            GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
            gridBagConstraints6.gridx = 0;
            gridBagConstraints6.anchor = GridBagConstraints.WEST;
            gridBagConstraints6.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints6.gridy = 3;
            jLabel3 = new JLabel();
            jLabel3.setText("Group Id");
            GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
            gridBagConstraints5.gridx = 0;
            gridBagConstraints5.anchor = GridBagConstraints.WEST;
            gridBagConstraints5.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints5.gridy = 2;
            jLabel2 = new JLabel();
            jLabel2.setText("Display Name");
            GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
            gridBagConstraints4.gridx = 0;
            gridBagConstraints4.anchor = GridBagConstraints.WEST;
            gridBagConstraints4.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints4.gridy = 1;
            jLabel1 = new JLabel();
            jLabel1.setText("Display Namespace");
            GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
            gridBagConstraints3.gridx = 0;
            gridBagConstraints3.anchor = GridBagConstraints.WEST;
            gridBagConstraints3.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints3.gridy = 0;
            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            gridBagConstraints2.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints2.anchor = GridBagConstraints.WEST;
            gridBagConstraints2.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints2.gridx = 1;
            gridBagConstraints2.gridy = 0;
            gridBagConstraints2.weightx = 1.0;
            jLabel = new JLabel();
            jLabel.setText("Grid Grouper");
            detailsPanel = new JPanel();
            detailsPanel.setLayout(new GridBagLayout());
            detailsPanel.add(jLabel, gridBagConstraints3);
            detailsPanel.add(getGridGrouper(), gridBagConstraints2);
            detailsPanel.add(jLabel1, gridBagConstraints4);
            detailsPanel.add(getDisplayNamespace(), gridBagConstraints9);
            detailsPanel.add(jLabel2, gridBagConstraints5);
            detailsPanel.add(getDisplayName(), gridBagConstraints10);
            detailsPanel.add(jLabel3, gridBagConstraints6);
            detailsPanel.add(getGroupId(), gridBagConstraints11);
            detailsPanel.add(jLabel4, gridBagConstraints7);
            detailsPanel.add(getNamespace(), gridBagConstraints12);
            detailsPanel.add(jLabel5, gridBagConstraints8);
            detailsPanel.add(getGrpName(), gridBagConstraints13);
        }
        return detailsPanel;
    }


    /**
     * This method initializes gridGrouper
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getGridGrouper() {
        if (gridGrouper == null) {
            gridGrouper = new JTextField();
            gridGrouper.setEditable(false);
            gridGrouper.setText(grp.getGridGrouper().getName());
        }
        return gridGrouper;
    }


    /**
     * This method initializes displayNamespace
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getDisplayNamespace() {
        if (displayNamespace == null) {
            displayNamespace = new JTextField();
            displayNamespace.setEditable(false);
            int index = grp.getDisplayName().lastIndexOf(":");
            displayNamespace.setText(grp.getDisplayName().substring(0, index));
        }
        return displayNamespace;
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
            displayName.setText(grp.getDisplayExtension());
        }
        return displayName;
    }


    /**
     * This method initializes groupId
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getGroupId() {
        if (groupId == null) {
            groupId = new JTextField();
            groupId.setEditable(false);
            groupId.setText(grp.getUuid());
        }
        return groupId;
    }


    /**
     * This method initializes namespace
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getNamespace() {
        if (namespace == null) {
            namespace = new JTextField();
            namespace.setEditable(false);
            int index = grp.getName().lastIndexOf(":");
            namespace.setText(grp.getName().substring(0, index));
        }
        return namespace;
    }


    /**
     * This method initializes grpName
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getGrpName() {
        if (grpName == null) {
            grpName = new JTextField();
            grpName.setEditable(false);
            grpName.setText(grp.getExtension());
        }
        return grpName;
    }


    /**
     * This method initializes descriptionPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getDescriptionPanel() {
        if (descriptionPanel == null) {
            GridBagConstraints gridBagConstraints16 = new GridBagConstraints();
            gridBagConstraints16.fill = GridBagConstraints.BOTH;
            gridBagConstraints16.weighty = 1.0;
            gridBagConstraints16.gridx = 0;
            gridBagConstraints16.gridy = 1;
            gridBagConstraints16.insets = new Insets(2, 10, 5, 10);
            gridBagConstraints16.weightx = 1.0;
            GridBagConstraints gridBagConstraints15 = new GridBagConstraints();
            gridBagConstraints15.gridx = 0;
            gridBagConstraints15.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints15.gridy = 0;
            jLabel6 = new JLabel();
            jLabel6.setText("Description");
            descriptionPanel = new JPanel();
            descriptionPanel.setLayout(new GridBagLayout());
            descriptionPanel.add(jLabel6, gridBagConstraints15);
            descriptionPanel.add(getJScrollPane(), gridBagConstraints16);
        }
        return descriptionPanel;
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
            description.setText(grp.getDescription());
            description.setEditable(false);
        }
        return description;
    }


    /**
     * This method initializes titlePanel	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getTitlePanel() {
        if (titlePanel == null) {
            titlePanel = new TitlePanel(grp.getDisplayExtension(),"Details of the group "+grp.getDisplayExtension()+".");
        }
        return titlePanel;
    }
}
