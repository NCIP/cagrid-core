package gov.nih.nci.cagrid.data.ui;

import gov.nih.nci.cagrid.data.cql.ui.CQLQueryProcessorConfigUI;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.io.File;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import org.cagrid.grape.GridApplication;

/** 
 *  QueryProcessorConfigurationDialog
 *  Dialog to display and manage a CQL query processor
 *  configuration UI component
 * 
 * @author David Ervin
 * 
 * @created Apr 6, 2007 1:25:32 PM
 * @version $Id: QueryProcessorConfigurationDialog.java,v 1.4 2009-05-28 19:26:49 dervin Exp $ 
 */
public class QueryProcessorConfigurationDialog extends JDialog {
    private CQLQueryProcessorConfigUI configUi = null;
    private JPanel buttonPanel = null;
    private JButton doneButton = null;
    private JButton cancelButton = null;
    private JPanel mainPanel = null;
    
    private File serviceDir = null;
    private Properties configProperties = null;
    private boolean canceled;

    private QueryProcessorConfigurationDialog(
        CQLQueryProcessorConfigUI configUi, File serviceDir, Properties configProperties) {
        super(GridApplication.getContext().getApplication(), 
            "CQL Processor Configuration", true);
        // only way to be NOT canceled is to click the done buttton
        this.canceled = true;
        this.configUi = configUi;
        this.serviceDir = serviceDir;
        this.configProperties = configProperties;
        initialize();
    }
    
    
    private void initialize() {
        setContentPane(getMainPanel());
        configUi.setUpUi(serviceDir, configProperties);
    }
    
    
    /**
     * Shows the configuration UI
     * 
     * @param configUi
     *      The config UI instance to display
     * @param serviceDir
     *      The directory in which the service resides
     * @param configProperties
     *      The current CQL query processor configuration properties
     * @return
     *      The config properties as edited by the config UI, 
     *      or <code>null</code> if the dialog was canceled
     */
    public static Properties showConfigurationUi(
        CQLQueryProcessorConfigUI configUi, File serviceDir, Properties configProperties) {
        QueryProcessorConfigurationDialog dialog = new QueryProcessorConfigurationDialog(
            configUi, serviceDir, configProperties);
        dialog.pack();
        dialog.setVisible(true);
        
        if (dialog.canceled) {
            return null;
        }
        return configUi.getConfiguredProperties();
    }
    
    
    private JPanel getButtonPanel() {
        if (buttonPanel == null) {
            GridLayout gridLayout = new GridLayout();
            gridLayout.setRows(1);
            gridLayout.setVgap(2);
            gridLayout.setColumns(2);
            gridLayout.setHgap(2);
            buttonPanel = new JPanel();
            buttonPanel.setLayout(gridLayout);
            buttonPanel.add(getDoneButton(), null);
            buttonPanel.add(getCancelButton(), null);
        }
        return buttonPanel;
    }
    
    
    private JButton getDoneButton() {
        if (doneButton == null) {
            doneButton = new JButton();
            doneButton.setText("Done");
            doneButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    canceled = false;
                    dispose();
                }
            });
        }
        return doneButton;
    }
    
    
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
    
    
    private JPanel getMainPanel() {
        if (mainPanel == null) {
            GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
            gridBagConstraints1.gridx = 0;
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
            mainPanel.add(configUi, gridBagConstraints);
            mainPanel.add(getButtonPanel(), gridBagConstraints1);
        }
        return mainPanel;
    }
}
