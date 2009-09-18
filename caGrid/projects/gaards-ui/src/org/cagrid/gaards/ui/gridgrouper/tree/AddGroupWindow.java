package org.cagrid.gaards.ui.gridgrouper.tree;

import gov.nih.nci.cagrid.common.Runner;
import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.gridgrouper.grouper.StemI;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.cagrid.gaards.ui.common.TitlePanel;
import org.cagrid.gaards.ui.gridgrouper.GridGrouperLookAndFeel;
import org.cagrid.grape.ApplicationComponent;
import org.cagrid.grape.GridApplication;
import org.cagrid.grape.utils.ErrorDialog;


/**
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella</A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster</A>
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings</A>
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * @version $Id: GridGrouperBaseTreeNode.java,v 1.1 2006/08/04 03:49:26 langella
 *          Exp $
 */
public class AddGroupWindow extends ApplicationComponent {

    private static final long serialVersionUID = 1L;

    private StemTreeNode node;

    private JPanel mainPanel = null;

    private JLabel jLabel10 = null;

    private JTextField childName = null;

    private JLabel jLabel11 = null;

    private JTextField childDisplayName = null;

    private JButton addChildGroup = null;

    private JPanel buttonPanel = null;

    private JButton cancelButton = null;

    private JLabel jLabel1 = null;

    private JTextField gridGrouper = null;

    private JLabel jLabel2 = null;

    private JTextField parentStem = null;

    private JPanel infoPanel = null;

    private JPanel titlePanel = null;


    /**
     * This is the default constructor
     */
    public AddGroupWindow(StemTreeNode node) {
        super();
        this.node = node;
        initialize();
    }


    /**
     * This method initializes this
     */
    private void initialize() {
        this.setSize(500, 300);
        this.setContentPane(getMainPanel());
        this.setTitle("Add Group");
        this.setFrameIcon(GridGrouperLookAndFeel.getGroupIcon22x22());
    }


    /**
     * This method initializes mainPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getMainPanel() {
        if (mainPanel == null) {
            GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
            gridBagConstraints8.gridx = 0;
            gridBagConstraints8.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints8.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints8.weightx = 1.0D;
            gridBagConstraints8.gridy = 0;
            GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
            gridBagConstraints10.gridx = 0;
            gridBagConstraints10.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints10.fill = GridBagConstraints.BOTH;
            gridBagConstraints10.weightx = 1.0D;
            gridBagConstraints10.weighty = 1.0D;
            gridBagConstraints10.anchor = GridBagConstraints.WEST;
            gridBagConstraints10.gridy = 1;
            jLabel2 = new JLabel();
            jLabel2.setText("Parent Stem");
            jLabel1 = new JLabel();
            jLabel1.setText("Grid Grouper");
            GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
            gridBagConstraints1.gridx = 0;
            gridBagConstraints1.gridwidth = 1;
            gridBagConstraints1.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints1.gridy = 2;
            jLabel11 = new JLabel();
            jLabel11.setText("Local Display Name");
            jLabel10 = new JLabel();
            jLabel10.setText("Local Name");
            mainPanel = new JPanel();
            mainPanel.setLayout(new GridBagLayout());
            mainPanel.add(getButtonPanel(), gridBagConstraints1);
            mainPanel.add(getInfoPanel(), gridBagConstraints10);
            mainPanel.add(getTitlePanel(), gridBagConstraints8);
        }
        return mainPanel;
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
     * This method initializes addChildGroup
     * 
     * @return javax.swing.JButton
     */
    private JButton getAddChildGroup() {
        if (addChildGroup == null) {
            addChildGroup = new JButton();
            addChildGroup.setText("Add Group");
            getRootPane().setDefaultButton(addChildGroup);
            addChildGroup.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    Runner runner = new Runner() {
                        public void execute() {
                            StemI stem = node.getStem();
                            int eid = node.getTree().startEvent("Adding a child group....");
                            try {

                                String ext = Utils.clean(childName.getText());
                                if (ext == null) {
                                    ErrorDialog.showError("You must enter a local name for the group!!");
                                    return;
                                }

                                String disExt = Utils.clean(childDisplayName.getText());
                                if (disExt == null) {
                                    ErrorDialog.showError("You must enter a local display name for the group!!!");
                                    return;
                                }

                                stem.addChildGroup(ext, disExt);
                                node.refresh();
                                node.getTree().stopEvent(eid, "Successfully added a child group!!!");
                                dispose();
                            } catch (Exception ex) {
                                node.getTree().stopEvent(eid, "Error adding a child group!!!");
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
        return addChildGroup;
    }


    /**
     * This method initializes buttonPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getButtonPanel() {
        if (buttonPanel == null) {
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints.gridx = -1;
            gridBagConstraints.gridy = -1;
            gridBagConstraints.gridwidth = 2;
            buttonPanel = new JPanel();
            buttonPanel.setLayout(new FlowLayout());
            buttonPanel.add(getAddChildGroup(), null);
            buttonPanel.add(getCancelButton(), null);
        }
        return buttonPanel;
    }


    /**
     * This method initializes cancelButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getCancelButton() {
        if (cancelButton == null) {
            cancelButton = new JButton();
            cancelButton.setText("Cancel");
            cancelButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    dispose();
                }
            });
        }
        return cancelButton;
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
            gridGrouper.setText(node.getGridGrouper().getName());
        }
        return gridGrouper;
    }


    /**
     * This method initializes parentStem
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getParentStem() {
        if (parentStem == null) {
            parentStem = new JTextField();
            parentStem.setEditable(false);
            parentStem.setText(this.node.getStem().getDisplayName());
        }
        return parentStem;
    }


    /**
     * This method initializes infoPanel	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getInfoPanel() {
        if (infoPanel == null) {
            GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
            gridBagConstraints12.anchor = GridBagConstraints.WEST;
            gridBagConstraints12.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints12.gridx = 1;
            gridBagConstraints12.gridy = 3;
            gridBagConstraints12.weightx = 1.0;
            gridBagConstraints12.fill = GridBagConstraints.HORIZONTAL;
            GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
            gridBagConstraints11.anchor = GridBagConstraints.WEST;
            gridBagConstraints11.gridx = 0;
            gridBagConstraints11.gridy = 3;
            gridBagConstraints11.insets = new Insets(2, 2, 2, 2);
            GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
            gridBagConstraints7.anchor = GridBagConstraints.WEST;
            gridBagConstraints7.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints7.gridx = 1;
            gridBagConstraints7.gridy = 2;
            gridBagConstraints7.weightx = 1.0;
            gridBagConstraints7.fill = GridBagConstraints.HORIZONTAL;
            GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
            gridBagConstraints6.anchor = GridBagConstraints.WEST;
            gridBagConstraints6.gridx = 0;
            gridBagConstraints6.gridy = 2;
            gridBagConstraints6.insets = new Insets(2, 2, 2, 2);
            GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
            gridBagConstraints3.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints3.gridx = 1;
            gridBagConstraints3.gridy = 1;
            gridBagConstraints3.weightx = 1.0;
            gridBagConstraints3.anchor = GridBagConstraints.WEST;
            gridBagConstraints3.insets = new Insets(2, 2, 2, 2);
            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            gridBagConstraints2.anchor = GridBagConstraints.WEST;
            gridBagConstraints2.gridx = 0;
            gridBagConstraints2.gridy = 1;
            gridBagConstraints2.insets = new Insets(2, 2, 2, 2);
            GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
            gridBagConstraints5.anchor = GridBagConstraints.WEST;
            gridBagConstraints5.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints5.gridx = 1;
            gridBagConstraints5.gridy = 0;
            gridBagConstraints5.weightx = 1.0;
            gridBagConstraints5.fill = GridBagConstraints.HORIZONTAL;
            GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
            gridBagConstraints4.anchor = GridBagConstraints.WEST;
            gridBagConstraints4.gridx = 0;
            gridBagConstraints4.gridy = 0;
            gridBagConstraints4.insets = new Insets(2, 2, 2, 2);
            infoPanel = new JPanel();
            infoPanel.setLayout(new GridBagLayout());
            infoPanel.add(jLabel1, gridBagConstraints4);
            infoPanel.add(getGridGrouper(), gridBagConstraints5);
            infoPanel.add(jLabel2, gridBagConstraints2);
            infoPanel.add(getParentStem(), gridBagConstraints3);
            infoPanel.add(jLabel10, gridBagConstraints6);
            infoPanel.add(getChildName(), gridBagConstraints7);
            infoPanel.add(jLabel11, gridBagConstraints11);
            infoPanel.add(getChildDisplayName(), gridBagConstraints12);
        }
        return infoPanel;
    }


    /**
     * This method initializes titlePanel	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getTitlePanel() {
        if (titlePanel == null) {
            titlePanel = new TitlePanel("Add Group","Add a group to GridGrouper.");
            }
        return titlePanel;
    }

}
