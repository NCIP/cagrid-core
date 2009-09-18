package gov.nih.nci.cagrid.validator.builder;

import gov.nih.nci.cagrid.common.portal.PortalLookAndFeel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

/** 
 *  StepConfigurationDialog
 *  Dialog to configure properties of a service test step
 * 
 * @author David Ervin
 * 
 * @created Sep 10, 2007 1:18:44 PM
 * @version $Id: StepConfigurationDialog.java,v 1.2 2008-03-26 14:40:56 dervin Exp $ 
 */
public class StepConfigurationDialog extends JDialog {

    private Properties currentConfig;
    private boolean canceled;
    
    private JButton okButton = null;
    private JButton cancelButton = null;
    private JPanel buttonPanel = null;
    private JTable configurationTable = null;
    private JScrollPane configurationScrollPane = null;
    private JPanel mainPanel = null;
    
    private StepConfigurationDialog(JFrame parent, Properties configuration) {
        super(parent, "Step Configuration", true);
        this.currentConfig = configuration;
        this.canceled = false;
        initialize();
    }
    
    
    private void initialize() {
        this.setContentPane(getMainPanel());
        this.setSize(300, 260);
    }
    
    
    private Properties getCurrentConfig() {
        if (canceled) {
            return currentConfig;
        } else {
            Properties newConfiguration = new Properties();
            for (int i = 0; i < getConfigurationTable().getRowCount(); i++) {
                String key = (String) getConfigurationTable().getValueAt(i, 0);
                String value = (String) getConfigurationTable().getValueAt(i, 1);
                newConfiguration.setProperty(key, value);
            }
            return newConfiguration;
        }
    }
    
    
    public static Properties configureStep(JFrame parent, Properties currentProperties) {
        StepConfigurationDialog dialog = new StepConfigurationDialog(parent, currentProperties);
        dialog.setVisible(true);
        return dialog.getCurrentConfig();
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
                    canceled = false;
                    dispose();
                }
            });
        }
        return okButton;
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
     * This method initializes buttonPanel	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getButtonPanel() {
        if (buttonPanel == null) {
            GridLayout gridLayout = new GridLayout();
            gridLayout.setRows(1);
            gridLayout.setColumns(2);
            gridLayout.setHgap(4);
            buttonPanel = new JPanel();
            buttonPanel.setLayout(gridLayout);
            buttonPanel.add(getOkButton(), null);
            buttonPanel.add(getCancelButton(), null);
        }
        return buttonPanel;
    }


    /**
     * This method initializes configurationTable	
     * 	
     * @return javax.swing.JTable	
     */
    private JTable getConfigurationTable() {
        if (configurationTable == null) {
            DefaultTableModel configurationModel = new DefaultTableModel() {
                public boolean isCellEditable(int row, int col) {
                    return col == 1;
                }
            };
            configurationModel.addColumn("Key");
            configurationModel.addColumn("Value");
            configurationTable = new JTable(configurationModel);
            
            Enumeration keys = currentConfig.keys();
            while (keys.hasMoreElements()) {
                String key = (String) keys.nextElement();
                String value = currentConfig.getProperty(key);
                Vector row = new Vector(2);
                row.add(key);
                row.add(value);
                configurationModel.addRow(row);
            }
        }
        return configurationTable;
    }


    /**
     * This method initializes configurationScrollPane	
     * 	
     * @return javax.swing.JScrollPane	
     */
    private JScrollPane getConfigurationScrollPane() {
        if (configurationScrollPane == null) {
            configurationScrollPane = new JScrollPane();
            configurationScrollPane.setViewportView(getConfigurationTable());
            configurationScrollPane.setBorder(BorderFactory.createTitledBorder(
                null, "Configuration", TitledBorder.DEFAULT_JUSTIFICATION, 
                TitledBorder.DEFAULT_POSITION, null, PortalLookAndFeel.getPanelLabelColor()));
        }
        return configurationScrollPane;
    }


    /**
     * This method initializes mainPanel	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getMainPanel() {
        if (mainPanel == null) {
            GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
            gridBagConstraints1.gridx = 0;
            gridBagConstraints1.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints1.anchor = GridBagConstraints.EAST;
            gridBagConstraints1.gridy = 1;
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.fill = GridBagConstraints.BOTH;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.weightx = 1.0;
            gridBagConstraints.weighty = 1.0D;
            gridBagConstraints.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints.gridx = 0;
            mainPanel = new JPanel();
            mainPanel.setLayout(new GridBagLayout());
            mainPanel.add(getConfigurationScrollPane(), gridBagConstraints);
            mainPanel.add(getButtonPanel(), gridBagConstraints1);
        }
        return mainPanel;
    }
}
