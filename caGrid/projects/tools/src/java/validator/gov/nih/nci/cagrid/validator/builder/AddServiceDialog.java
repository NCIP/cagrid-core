package gov.nih.nci.cagrid.validator.builder;

import gov.nih.nci.cagrid.tests.core.beans.validation.ServiceDescription;
import gov.nih.nci.cagrid.tests.core.beans.validation.ServiceType;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.axis.types.URI;
import org.apache.axis.types.URI.MalformedURIException;

/** 
 *  AddServiceDialog
 *  Dialog to add a service to the validation cycle
 * 
 * @author David Ervin
 * 
 * @created Aug 29, 2007 11:03:10 AM
 * @version $Id: AddServiceDialog.java,v 1.1 2008-03-25 14:20:30 dervin Exp $ 
 */
public class AddServiceDialog extends JDialog {

    private JLabel nameLabel = null;
    private JLabel typeLabel = null;
    private JLabel urlLabel = null;
    private JTextField nameTextField = null;
    private JComboBox typeComboBox = null;
    private JTextField urlTextField = null;
    private JButton addButton = null;
    private JButton cancelButton = null;
    private JPanel infoPanel = null;
    private JPanel buttonPanel = null;
    private JPanel mainPanel = null;
    private boolean canceled;


    private AddServiceDialog(JFrame owner) {
        super(owner, "Add Service", true);
        canceled = false;
        initialize();
    }
    
    
    private void initialize() {
        this.setContentPane(getMainPanel());
        this.setSize(new Dimension(370, 141));
    }
    
    
    private ServiceDescription getServiceDescription() throws MalformedURIException {
        if (!canceled) {
            ServiceDescription desc = new ServiceDescription();
            desc.setServiceName(getNameTextField().getText());
            desc.setServiceType(getTypeComboBox().getSelectedItem().toString());
            desc.setServiceUrl(new URI(getUrlTextField().getText()));
            return desc;
        }
        return null;
    }
    
    
    public static ServiceDescription getDescription(JFrame owner, String[] typeNames) throws MalformedURIException {
        AddServiceDialog dialog = new AddServiceDialog(owner);
        if (typeNames != null) {
            for (String name : typeNames) {
                dialog.getTypeComboBox().addItem(name);
            }
        }
        dialog.setVisible(true);
        return dialog.getServiceDescription();
    }


    /**
     * This method initializes nameLabel	
     * 	
     * @return javax.swing.JLabel	
     */
    private JLabel getNameLabel() {
        if (nameLabel == null) {
            nameLabel = new JLabel();
            nameLabel.setText("Service Name");
        }
        return nameLabel;
    }


    /**
     * This method initializes typeLabel	
     * 	
     * @return javax.swing.JLabel	
     */
    private JLabel getTypeLabel() {
        if (typeLabel == null) {
            typeLabel = new JLabel();
            typeLabel.setText("Service Type:");
        }
        return typeLabel;
    }


    /**
     * This method initializes urlLabel	
     * 	
     * @return javax.swing.JLabel	
     */
    private JLabel getUrlLabel() {
        if (urlLabel == null) {
            urlLabel = new JLabel();
            urlLabel.setText("Service URL:");
        }
        return urlLabel;
    }


    /**
     * This method initializes nameTextField	
     * 	
     * @return javax.swing.JTextField	
     */
    private JTextField getNameTextField() {
        if (nameTextField == null) {
            nameTextField = new JTextField();
        }
        return nameTextField;
    }


    /**
     * This method initializes typeComboBox	
     * 	
     * @return javax.swing.JComboBox	
     */
    private JComboBox getTypeComboBox() {
        if (typeComboBox == null) {
            typeComboBox = new JComboBox();
            Field[] fields = ServiceType.class.getFields();
            for (Field f : fields) {
                if (Modifier.isStatic(f.getModifiers()) && Modifier.isPublic(f.getModifiers())) {
                    if (f.getType().equals(String.class)) {
                        try {
                            String val = (String) f.get(null);
                            typeComboBox.addItem(val);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
        }
        return typeComboBox;
    }


    /**
     * This method initializes urlTextField	
     * 	
     * @return javax.swing.JTextField	
     */
    private JTextField getUrlTextField() {
        if (urlTextField == null) {
            urlTextField = new JTextField();
        }
        return urlTextField;
    }


    /**
     * This method initializes addButton	
     * 	
     * @return javax.swing.JButton	
     */
    private JButton getAddButton() {
        if (addButton == null) {
            addButton = new JButton();
            addButton.setText("Add");
            addButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    canceled = false;
                    dispose();
                }
            });
        }
        return addButton;
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
                    canceled = true;
                    dispose();
                }
            });
        }
        return cancelButton;
    }


    /**
     * This method initializes infoPanel	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getInfoPanel() {
        if (infoPanel == null) {
            GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
            gridBagConstraints5.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints5.gridy = 2;
            gridBagConstraints5.weightx = 1.0;
            gridBagConstraints5.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints5.gridx = 1;
            GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
            gridBagConstraints4.gridx = 0;
            gridBagConstraints4.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints4.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints4.gridy = 2;
            GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
            gridBagConstraints3.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints3.gridy = 1;
            gridBagConstraints3.weightx = 1.0;
            gridBagConstraints3.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints3.gridx = 1;
            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            gridBagConstraints2.gridx = 0;
            gridBagConstraints2.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints2.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints2.gridy = 1;
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
            infoPanel = new JPanel();
            infoPanel.setLayout(new GridBagLayout());
            infoPanel.add(getNameLabel(), gridBagConstraints);
            infoPanel.add(getNameTextField(), gridBagConstraints1);
            infoPanel.add(getTypeLabel(), gridBagConstraints2);
            infoPanel.add(getTypeComboBox(), gridBagConstraints3);
            infoPanel.add(getUrlLabel(), gridBagConstraints4);
            infoPanel.add(getUrlTextField(), gridBagConstraints5);
        }
        return infoPanel;
    }


    /**
     * This method initializes buttonPanel	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getButtonPanel() {
        if (buttonPanel == null) {
            GridLayout gridLayout = new GridLayout();
            gridLayout.setRows(1);
            gridLayout.setHgap(4);
            gridLayout.setColumns(2);
            buttonPanel = new JPanel();
            buttonPanel.setLayout(gridLayout);
            buttonPanel.add(getAddButton(), null);
            buttonPanel.add(getCancelButton(), null);
        }
        return buttonPanel;
    }


    /**
     * This method initializes mainPanel	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getMainPanel() {
        if (mainPanel == null) {
            GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
            gridBagConstraints7.gridx = 0;
            gridBagConstraints7.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints7.anchor = GridBagConstraints.EAST;
            gridBagConstraints7.gridy = 1;
            GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
            gridBagConstraints6.gridx = 0;
            gridBagConstraints6.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints6.weightx = 1.0D;
            gridBagConstraints6.gridy = 0;
            mainPanel = new JPanel();
            mainPanel.setLayout(new GridBagLayout());
            mainPanel.add(getInfoPanel(), gridBagConstraints6);
            mainPanel.add(getButtonPanel(), gridBagConstraints7);
        }
        return mainPanel;
    }
}
