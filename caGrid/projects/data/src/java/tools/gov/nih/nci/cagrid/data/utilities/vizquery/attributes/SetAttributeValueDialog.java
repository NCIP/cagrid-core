package gov.nih.nci.cagrid.data.utilities.vizquery.attributes;

import gov.nih.nci.cagrid.common.portal.DocumentChangeAdapter;
import gov.nih.nci.cagrid.cqlquery.Attribute;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.Dimension;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;

/** 
 *  SetAttributeValueDialog
 *  Dialog to change the value of a CQL query attribute
 * 
 * @author David Ervin
 * 
 * @created May 31, 2007 3:17:54 PM
 * @version $Id: SetAttributeValueDialog.java,v 1.1 2007-05-31 19:35:00 dervin Exp $ 
 */
public class SetAttributeValueDialog extends JDialog {

    private Attribute attribute = null;
    private JLabel originalValueLabel = null;
    private JLabel newValueLabel = null;
    private JTextField originalValueTextField = null;
    private JTextField newValueTextField = null;
    private JButton doneButton = null;
    private JPanel mainPanel = null;
    
    private SetAttributeValueDialog(JFrame owner, Attribute attribute) {
        super(owner, "Set Value", true);
        this.attribute = attribute;
        initialize();
    }
    
    
    private void initialize() {
        this.setSize(new Dimension(309, 120));
        this.setContentPane(getMainPanel());
    }
    
    
    public static void setAttributeValue(JFrame owner, Attribute attribute) {
        JDialog dialog = new SetAttributeValueDialog(owner, attribute);
        dialog.setVisible(true);
        dialog.pack();
    }


    /**
     * This method initializes originalValueLabel	
     * 	
     * @return javax.swing.JLabel	
     */
    private JLabel getOriginalValueLabel() {
        if (originalValueLabel == null) {
            originalValueLabel = new JLabel();
            originalValueLabel.setText("Original Value:");
        }
        return originalValueLabel;
    }


    /**
     * This method initializes newValueLabel	
     * 	
     * @return javax.swing.JLabel	
     */
    private JLabel getNewValueLabel() {
        if (newValueLabel == null) {
            newValueLabel = new JLabel();
            newValueLabel.setText("New Value:");
        }
        return newValueLabel;
    }


    /**
     * This method initializes originalValueTextField	
     * 	
     * @return javax.swing.JTextField	
     */
    private JTextField getOriginalValueTextField() {
        if (originalValueTextField == null) {
            originalValueTextField = new JTextField();
            originalValueTextField.setEditable(false);
            originalValueTextField.setText(attribute.getValue());
        }
        return originalValueTextField;
    }


    /**
     * This method initializes newValueTextField	
     * 	
     * @return javax.swing.JTextField	
     */
    private JTextField getNewValueTextField() {
        if (newValueTextField == null) {
            newValueTextField = new JTextField();
            newValueTextField.getDocument().addDocumentListener(new DocumentChangeAdapter() {
                public void documentEdited(DocumentEvent e) {
                    attribute.setValue(getNewValueTextField().getText());
                }
            });
        }
        return newValueTextField;
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
     * This method initializes mainPanel	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getMainPanel() {
        if (mainPanel == null) {
            GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
            gridBagConstraints4.gridx = 1;
            gridBagConstraints4.anchor = GridBagConstraints.EAST;
            gridBagConstraints4.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints4.gridy = 2;
            GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
            gridBagConstraints3.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints3.gridy = 1;
            gridBagConstraints3.weightx = 1.0;
            gridBagConstraints3.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints3.gridx = 1;
            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            gridBagConstraints2.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints2.gridy = 0;
            gridBagConstraints2.weightx = 1.0;
            gridBagConstraints2.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints2.gridx = 1;
            GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
            gridBagConstraints1.gridx = 0;
            gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints1.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints1.gridy = 1;
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints.gridy = 0;
            mainPanel = new JPanel();
            mainPanel.setLayout(new GridBagLayout());
            mainPanel.add(getOriginalValueLabel(), gridBagConstraints);
            mainPanel.add(getNewValueLabel(), gridBagConstraints1);
            mainPanel.add(getOriginalValueTextField(), gridBagConstraints2);
            mainPanel.add(getNewValueTextField(), gridBagConstraints3);
            mainPanel.add(getDoneButton(), gridBagConstraints4);
        }
        return mainPanel;
    }
}
