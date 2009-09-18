package gov.nih.nci.cagrid.data.utilities.vizquery.attributes;

import gov.nih.nci.cagrid.cqlquery.Attribute;
import gov.nih.nci.cagrid.cqlquery.Predicate;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/** 
 *  SetAttributePredicateDialog
 *  Dialog to set an attribute's predicate
 * 
 * @author David Ervin
 * 
 * @created Apr 6, 2007 10:32:03 AM
 * @version $Id: SetAttributePredicateDialog.java,v 1.2 2007-05-31 19:34:59 dervin Exp $ 
 */
public class SetAttributePredicateDialog extends JDialog {

    private Attribute attribute = null;
    private AttributePredicateCombo predicateCombo = null;
    private JButton doneButton = null;
    private JLabel originalPredicateLabel = null;
    private JTextField originalPredicateTextField = null;
    private JPanel mainPanel = null;
    private JLabel selectionLabel = null;
    
    private SetAttributePredicateDialog(JFrame owner, Attribute attribute) {
        super(owner, "Set Predicate", true);
        this.attribute = attribute;
        initialize();
    }
    
    
    private void initialize() {
        getPredicateCombo().setSelectedItem(attribute.getPredicate());
        this.setContentPane(getMainPanel());
    }
    
    
    public static void setAttributePredicate(JFrame owner, Attribute attrib) {
        JDialog dialog = new SetAttributePredicateDialog(owner, attrib);
        dialog.pack();
        dialog.setVisible(true);
    }
    
    
    private AttributePredicateCombo getPredicateCombo() {
        if (predicateCombo == null) {
            predicateCombo = new AttributePredicateCombo();
            predicateCombo.addPredicateChangeListener(new AttributePredicateChangeListener() {
               public void attributePredicateChanged(Predicate newValue) {
                   if (newValue.equals(Predicate.IS_NOT_NULL) 
                       || newValue.equals(Predicate.IS_NULL)) {
                       // remove attribute's query value
                       attribute.setValue("");
                   }
                   attribute.setPredicate(newValue);
               }
            });
        }
        return predicateCombo;
    }


    /**
     * This method initializes doneButton	
     * 	
     * @return javax.swing.JButton	
     */
    private JButton getDoneButton() {
        if (doneButton == null) {
            doneButton = new JButton();
            doneButton.setText("Done");
            doneButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    dispose();
                }
            });
        }
        return doneButton;
    }


    /**
     * This method initializes originalPredicateLabel	
     * 	
     * @return javax.swing.JLabel	
     */
    private JLabel getOriginalPredicateLabel() {
        if (originalPredicateLabel == null) {
            originalPredicateLabel = new JLabel();
            originalPredicateLabel.setText("Original Predicate:");
        }
        return originalPredicateLabel;
    }


    /**
     * This method initializes originalPredicateTextField	
     * 	
     * @return javax.swing.JTextField	
     */
    private JTextField getOriginalPredicateTextField() {
        if (originalPredicateTextField == null) {
            originalPredicateTextField = new JTextField();
            originalPredicateTextField.setEditable(false);
        }
        return originalPredicateTextField;
    }


    /**
     * This method initializes mainPanel	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getMainPanel() {
        if (mainPanel == null) {
            GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
            gridBagConstraints3.gridx = 1;
            gridBagConstraints3.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints3.anchor = GridBagConstraints.EAST;
            gridBagConstraints3.gridy = 2;
            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            gridBagConstraints2.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints2.gridy = 1;
            gridBagConstraints2.weightx = 1.0;
            gridBagConstraints2.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints2.gridx = 1;
            GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
            gridBagConstraints11.gridx = 0;
            gridBagConstraints11.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints11.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints11.gridy = 1;
            GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
            gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints1.gridy = 0;
            gridBagConstraints1.weightx = 1.0;
            gridBagConstraints1.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints1.gridx = 1;
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints.gridy = 0;
            mainPanel = new JPanel();
            mainPanel.setLayout(new GridBagLayout());
            mainPanel.add(getOriginalPredicateLabel(), gridBagConstraints);
            mainPanel.add(getOriginalPredicateTextField(), gridBagConstraints1);
            mainPanel.add(getSelectionLabel(), gridBagConstraints11);
            mainPanel.add(getPredicateCombo(), gridBagConstraints2);
            mainPanel.add(getDoneButton(), gridBagConstraints3);
        }
        return mainPanel;
    }


    /**
     * This method initializes selectionLabel	
     * 	
     * @return javax.swing.JLabel	
     */
    private JLabel getSelectionLabel() {
        if (selectionLabel == null) {
            selectionLabel = new JLabel();
            selectionLabel.setText("Selection:");
        }
        return selectionLabel;
    }
}
