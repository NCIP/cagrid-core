package gov.nih.nci.cagrid.data.utilities.vizquery;

import gov.nih.nci.cagrid.cqlquery.Group;
import gov.nih.nci.cagrid.cqlquery.LogicalOperator;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.TitledBorder;

/** 
 *  SetGroupLogicDialog
 *  Dialog to set the logical operator of a group
 * 
 * @author David Ervin
 * 
 * @created Apr 5, 2007 2:17:25 PM
 * @version $Id: SetGroupLogicDialog.java,v 1.1 2007-04-05 18:52:18 dervin Exp $ 
 */
public class SetGroupLogicDialog extends JDialog {
    
    private Group group;
    
    private JRadioButton andLogicRadioButton = null;
    private JRadioButton orLogicRadioButton = null;
    private JButton okButton = null;
    private JPanel logicPanel = null;
    private JPanel mainPanel = null;

    private SetGroupLogicDialog(JFrame owner, Group group) {
        super(owner, "Set Group Logic", true);
        this.group = group;
        initialize();
    }
    
    
    public static void setLogic(JFrame owner, Group group) {
        JDialog dialog = new SetGroupLogicDialog(owner, group);
        dialog.pack();
        dialog.setVisible(true);
    }
    
    
    private void initialize() {
        this.setContentPane(getMainPanel());
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(getAndLogicRadioButton());
        buttonGroup.add(getOrLogicRadioButton());
        if (group.getLogicRelation().equals(LogicalOperator.AND)) {
            buttonGroup.setSelected(getAndLogicRadioButton().getModel(), true);
        } else {
            buttonGroup.setSelected(getOrLogicRadioButton().getModel(), true);
        }
    }


    /**
     * This method initializes andLogicRadioButton	
     * 	
     * @return javax.swing.JRadioButton	
     */
    private JRadioButton getAndLogicRadioButton() {
        if (andLogicRadioButton == null) {
            andLogicRadioButton = new JRadioButton();
            andLogicRadioButton.setText("AND");
        }
        return andLogicRadioButton;
    }


    /**
     * This method initializes orLogicRadioButton	
     * 	
     * @return javax.swing.JRadioButton	
     */
    private JRadioButton getOrLogicRadioButton() {
        if (orLogicRadioButton == null) {
            orLogicRadioButton = new JRadioButton();
            orLogicRadioButton.setText("OR");
        }
        return orLogicRadioButton;
    }


    /**
     * This method initializes okButton	
     * 	
     * @return javax.swing.JButton	
     */
    private JButton getOkButton() {
        if (okButton == null) {
            okButton = new JButton();
            okButton.setText("OK");
            okButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (getAndLogicRadioButton().isSelected()) {
                        group.setLogicRelation(LogicalOperator.AND);
                    } else {
                        group.setLogicRelation(LogicalOperator.OR);
                    }
                    dispose();
                }
            });
        }
        return okButton;
    }


    /**
     * This method initializes logicPanel	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getLogicPanel() {
        if (logicPanel == null) {
            GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
            gridBagConstraints1.gridx = 1;
            gridBagConstraints1.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints1.gridy = 0;
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints.gridy = 0;
            logicPanel = new JPanel();
            logicPanel.setLayout(new GridBagLayout());
            logicPanel.setBorder(BorderFactory.createTitledBorder(null, "Logic", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
            logicPanel.add(getAndLogicRadioButton(), gridBagConstraints);
            logicPanel.add(getOrLogicRadioButton(), gridBagConstraints1);
        }
        return logicPanel;
    }


    /**
     * This method initializes mainPanel	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getMainPanel() {
        if (mainPanel == null) {
            GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
            gridBagConstraints3.gridx = 0;
            gridBagConstraints3.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints3.anchor = GridBagConstraints.EAST;
            gridBagConstraints3.gridy = 1;
            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            gridBagConstraints2.gridx = 0;
            gridBagConstraints2.gridy = 0;
            mainPanel = new JPanel();
            mainPanel.setLayout(new GridBagLayout());
            mainPanel.add(getLogicPanel(), gridBagConstraints2);
            mainPanel.add(getOkButton(), gridBagConstraints3);
        }
        return mainPanel;
    }
}
