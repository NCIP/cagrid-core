package gov.nih.nci.cagrid.data.ui.table;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.cagrid.grape.GridApplication;


/**
 * CustomSerializationDialog Dialog for custom configuration of schema element
 * types
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * @created Jun 28, 2006
 * @version $Id: CustomSerializationDialog.java,v 1.2 2007-11-06 15:53:40 hastings Exp $
 */
public class CustomSerializationDialog extends JDialog {

    private JLabel serializerLabel = null;
    private JLabel deserialierLabel = null;
    private JTextField serialzierTextField = null;
    private JTextField deserializerTextField = null;
    private JButton okButton = null;
    private JButton cancelButton = null;
    private JPanel serializationPanel = null;
    private JPanel buttonPanel = null;
    private JPanel mainPanel = null;


    public CustomSerializationDialog() {
        super(GridApplication.getContext().getApplication(), "Custom Serialization", true);
        this.initialize();
    }


    public String getCustomSerializer() {
        return getSerialzierTextField().getText();
    }


    public String getCustomDeserializer() {
        return getDeserializerTextField().getText();
    }


    private void initialize() {
        this.setSize(new java.awt.Dimension(350, 112));
        this.setContentPane(getMainPanel());
        GridApplication.getContext().centerDialog(this);
        setVisible(true);
    }


    /**
     * This method initializes jLabel
     * 
     * @return javax.swing.JLabel
     */
    private JLabel getSerializerLabel() {
        if (this.serializerLabel == null) {
            this.serializerLabel = new JLabel();
            this.serializerLabel.setText("Serializer:");
        }
        return this.serializerLabel;
    }


    /**
     * This method initializes jLabel
     * 
     * @return javax.swing.JLabel
     */
    private JLabel getDeserialierLabel() {
        if (this.deserialierLabel == null) {
            this.deserialierLabel = new JLabel();
            this.deserialierLabel.setText("Deserializer:");
        }
        return this.deserialierLabel;
    }


    /**
     * This method initializes jTextField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getSerialzierTextField() {
        if (this.serialzierTextField == null) {
            this.serialzierTextField = new JTextField();
        }
        return this.serialzierTextField;
    }


    /**
     * This method initializes jTextField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getDeserializerTextField() {
        if (this.deserializerTextField == null) {
            this.deserializerTextField = new JTextField();
        }
        return this.deserializerTextField;
    }


    /**
     * This method initializes jButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getOkButton() {
        if (this.okButton == null) {
            this.okButton = new JButton();
            this.okButton.setText("OK");
            this.okButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    dispose();
                }
            });
        }
        return this.okButton;
    }


    /**
     * This method initializes jButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getCancelButton() {
        if (this.cancelButton == null) {
            this.cancelButton = new JButton();
            this.cancelButton.setText("Cancel");
            this.cancelButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    getSerialzierTextField().setText("");
                    getDeserializerTextField().setText("");
                    dispose();
                }
            });
        }
        return this.cancelButton;
    }


    /**
     * This method initializes jPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getSerializationPanel() {
        if (this.serializationPanel == null) {
            GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
            gridBagConstraints3.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints3.gridy = 1;
            gridBagConstraints3.weightx = 1.0;
            gridBagConstraints3.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints3.gridx = 1;
            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            gridBagConstraints2.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints2.gridy = 0;
            gridBagConstraints2.weightx = 1.0;
            gridBagConstraints2.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints2.gridx = 1;
            GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
            gridBagConstraints1.gridx = 0;
            gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints1.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints1.gridy = 1;
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints.gridy = 0;
            this.serializationPanel = new JPanel();
            this.serializationPanel.setLayout(new GridBagLayout());
            this.serializationPanel.add(getSerializerLabel(), gridBagConstraints);
            this.serializationPanel.add(getDeserialierLabel(), gridBagConstraints1);
            this.serializationPanel.add(getSerialzierTextField(), gridBagConstraints2);
            this.serializationPanel.add(getDeserializerTextField(), gridBagConstraints3);
        }
        return this.serializationPanel;
    }


    /**
     * This method initializes jPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getButtonPanel() {
        if (this.buttonPanel == null) {
            GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
            gridBagConstraints5.gridx = 1;
            gridBagConstraints5.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints5.gridy = 0;
            GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
            gridBagConstraints4.gridx = 0;
            gridBagConstraints4.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints4.gridy = 0;
            this.buttonPanel = new JPanel();
            this.buttonPanel.setLayout(new GridBagLayout());
            this.buttonPanel.add(getOkButton(), gridBagConstraints4);
            this.buttonPanel.add(getCancelButton(), gridBagConstraints5);
        }
        return this.buttonPanel;
    }


    /**
     * This method initializes jPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getMainPanel() {
        if (this.mainPanel == null) {
            GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
            gridBagConstraints7.gridx = 0;
            gridBagConstraints7.anchor = java.awt.GridBagConstraints.EAST;
            gridBagConstraints7.gridy = 1;
            GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
            gridBagConstraints6.gridx = 0;
            gridBagConstraints6.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints6.weightx = 1.0D;
            gridBagConstraints6.gridy = 0;
            this.mainPanel = new JPanel();
            this.mainPanel.setLayout(new GridBagLayout());
            this.mainPanel.add(getSerializationPanel(), gridBagConstraints6);
            this.mainPanel.add(getButtonPanel(), gridBagConstraints7);
        }
        return this.mainPanel;
    }
}
