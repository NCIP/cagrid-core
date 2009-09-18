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
public class AddStemWindow extends ApplicationComponent {

    private static final long serialVersionUID = 1L;

    private StemTreeNode node;

    private JPanel mainPanel = null;

    private JLabel jLabel10 = null;

    private JTextField childName = null;

    private JLabel jLabel11 = null;

    private JTextField childDisplayName = null;

    private JButton addChildStem = null;

    private JPanel buttonPanel = null;

    private JButton cancelButton = null;

    private JLabel jLabel = null;

    private JTextField gridGrouper = null;

    private JLabel jLabel2 = null;

    private JTextField parentStem = null;

    private JPanel infoPanel = null;

    private JPanel titlePanel = null;


    /**
     * This is the default constructor
     */
    public AddStemWindow(StemTreeNode node) {
        super();
        this.node = node;
        initialize();
    }


    /**
     * This method initializes this
     */
    private void initialize() {
        this.setSize(500, 225);
        this.setContentPane(getMainPanel());
        this.setTitle("Add Stem");
        this.setFrameIcon(GridGrouperLookAndFeel.getStemIcon16x16());
    }


    /**
     * This method initializes mainPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getMainPanel() {
        if (mainPanel == null) {
            GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
            gridBagConstraints11.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints11.gridy = 0;
            gridBagConstraints11.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints11.weightx = 1.0D;
            gridBagConstraints11.gridx = 0;
            GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
            gridBagConstraints8.gridx = 0;
            gridBagConstraints8.fill = GridBagConstraints.BOTH;
            gridBagConstraints8.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints8.weightx = 1.0D;
            gridBagConstraints8.weighty = 1.0D;
            gridBagConstraints8.gridy = 1;
            jLabel2 = new JLabel();
            jLabel2.setText("Parent Stem");
            jLabel = new JLabel();
            jLabel.setText("Grid Grouper");
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
            mainPanel.add(getInfoPanel(), gridBagConstraints8);
            mainPanel.add(getTitlePanel(), gridBagConstraints11);
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
     * This method initializes addChildStem
     * 
     * @return javax.swing.JButton
     */
    private JButton getAddChildStem() {
        if (addChildStem == null) {
            addChildStem = new JButton();
            addChildStem.setText("Add Stem");
            getRootPane().setDefaultButton(addChildStem);
            addChildStem.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    Runner runner = new Runner() {
                        public void execute() {
                            StemI stem = node.getStem();
                            int eid = node.getTree().startEvent("Adding a child stem....");
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
                                node.getTree().stopEvent(eid, "Successfully added a child stem!!!");
                                dispose();
                            } catch (Exception ex) {
                                node.getTree().stopEvent(eid, "Error adding a child stem!!!");
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
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints.gridx = -1;
            gridBagConstraints.gridy = -1;
            gridBagConstraints.gridwidth = 2;
            buttonPanel = new JPanel();
            buttonPanel.setLayout(new FlowLayout());
            buttonPanel.add(getAddChildStem(), null);
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
            if (node instanceof StemTreeNode) {
                parentStem.setText(this.node.getStem().getDisplayName());
            } else {
                parentStem.setText("Root");
            }
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
            GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
            gridBagConstraints10.anchor = GridBagConstraints.WEST;
            gridBagConstraints10.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints10.gridx = 1;
            gridBagConstraints10.gridy = 3;
            gridBagConstraints10.weightx = 1.0;
            gridBagConstraints10.fill = GridBagConstraints.HORIZONTAL;
            GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
            gridBagConstraints9.anchor = GridBagConstraints.WEST;
            gridBagConstraints9.gridx = 0;
            gridBagConstraints9.gridy = 3;
            gridBagConstraints9.insets = new Insets(2, 2, 2, 2);
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
            GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
            gridBagConstraints5.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints5.gridx = 1;
            gridBagConstraints5.gridy = 1;
            gridBagConstraints5.weightx = 1.0;
            gridBagConstraints5.anchor = GridBagConstraints.WEST;
            gridBagConstraints5.insets = new Insets(2, 2, 2, 2);
            GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
            gridBagConstraints4.anchor = GridBagConstraints.WEST;
            gridBagConstraints4.gridx = 0;
            gridBagConstraints4.gridy = 1;
            gridBagConstraints4.insets = new Insets(2, 2, 2, 2);
            GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
            gridBagConstraints3.anchor = GridBagConstraints.WEST;
            gridBagConstraints3.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints3.gridx = 1;
            gridBagConstraints3.gridy = 0;
            gridBagConstraints3.weightx = 1.0;
            gridBagConstraints3.fill = GridBagConstraints.HORIZONTAL;
            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            gridBagConstraints2.anchor = GridBagConstraints.WEST;
            gridBagConstraints2.gridx = 0;
            gridBagConstraints2.gridy = 0;
            gridBagConstraints2.insets = new Insets(2, 2, 2, 2);
            infoPanel = new JPanel();
            infoPanel.setLayout(new GridBagLayout());
            infoPanel.add(jLabel, gridBagConstraints2);
            infoPanel.add(getGridGrouper(), gridBagConstraints3);
            infoPanel.add(jLabel2, gridBagConstraints4);
            infoPanel.add(getParentStem(), gridBagConstraints5);
            infoPanel.add(jLabel10, gridBagConstraints6);
            infoPanel.add(getChildName(), gridBagConstraints7);
            infoPanel.add(jLabel11, gridBagConstraints9);
            infoPanel.add(getChildDisplayName(), gridBagConstraints10);
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
            titlePanel = new TitlePanel("Add Stem", "Add a stem to Grid Grouper.");
        }
        return titlePanel;
    }

}
