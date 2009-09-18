package gov.nih.nci.cagrid.data.ui.auditors;

import gov.nih.nci.cagrid.common.portal.PortalLookAndFeel;
import gov.nih.nci.cagrid.data.auditing.AuditorConfiguration;
import gov.nih.nci.cagrid.data.auditing.AuditorConfigurationConfigurationProperties;
import gov.nih.nci.cagrid.data.auditing.ConfigurationProperty;
import gov.nih.nci.cagrid.data.auditing.DataServiceAuditors;
import gov.nih.nci.cagrid.data.auditing.MonitoredEvents;
import gov.nih.nci.cagrid.data.common.ExtensionDataManager;
import gov.nih.nci.cagrid.data.service.auditing.DataServiceAuditor;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import org.cagrid.grape.GridApplication;
import org.cagrid.grape.utils.CompositeErrorDialog;

/** 
 *  AuditorConfigurationDialog
 *  Dialog to handle configuration of an auditor instance
 * 
 * @author David Ervin
 * 
 * @created Jun 26, 2007 11:41:25 AM
 * @version $Id: AuditorConfigurationDialog.java,v 1.3 2007-11-06 15:53:41 hastings Exp $ 
 */
public class AuditorConfigurationDialog extends JDialog {

    private ExtensionDataManager dataManager;
    private ServiceInformation serviceInfo;
    
    private String auditorClass;
    private String auditorInstance;
    
    private JLabel classNameLabel = null;
    private JLabel instanceNameLabel = null;
    private JTextField classNameTextField = null;
    private JTextField instanceNameTextField = null;
    private JPanel auditorInfoPanel = null;
    private AuditorConfigurationPropertiesTable propertiesTable = null;
    private MonitoredEventsPanel monitoredEventsPanel = null;
    private JScrollPane propertiesScrollPane = null;
    private JButton doneButton = null;
    private JPanel mainPanel = null;
    
    public AuditorConfigurationDialog(ExtensionDataManager dataManager, ServiceInformation serviceInfo, 
        String auditorClass, String auditorInstance) {
        super(GridApplication.getContext().getApplication(), 
            "Auditor instance configuration", true);
        this.dataManager = dataManager;
        this.serviceInfo = serviceInfo;
        this.auditorClass = auditorClass;
        this.auditorInstance = auditorInstance;
        initialize();
    }
    
    
    private void initialize() {
        loadConfiguration();
        setContentPane(getMainPanel());
        pack();
        setVisible(true);
    }


    /**
     * This method initializes classNameLabel	
     * 	
     * @return javax.swing.JLabel	
     */
    private JLabel getClassNameLabel() {
        if (classNameLabel == null) {
            classNameLabel = new JLabel();
            classNameLabel.setText("Auditor Class Name:");
        }
        return classNameLabel;
    }


    /**
     * This method initializes instanceNameLabel	
     * 	
     * @return javax.swing.JLabel	
     */
    private JLabel getInstanceNameLabel() {
        if (instanceNameLabel == null) {
            instanceNameLabel = new JLabel();
            instanceNameLabel.setText("Auditor Instance Name:");
        }
        return instanceNameLabel;
    }


    /**
     * This method initializes classNameTextField	
     * 	
     * @return javax.swing.JTextField	
     */
    private JTextField getClassNameTextField() {
        if (classNameTextField == null) {
            classNameTextField = new JTextField();
            classNameTextField.setEditable(false);
            classNameTextField.setText(auditorClass);
        }
        return classNameTextField;
    }


    /**
     * This method initializes instanceNameTextField	
     * 	
     * @return javax.swing.JTextField	
     */
    private JTextField getInstanceNameTextField() {
        if (instanceNameTextField == null) {
            instanceNameTextField = new JTextField();
            instanceNameTextField.setEditable(false);
            instanceNameTextField.setText(auditorInstance);
        }
        return instanceNameTextField;
    }


    /**
     * This method initializes auditorInfoPanel	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getAuditorInfoPanel() {
        if (auditorInfoPanel == null) {
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
            gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints.gridy = 0;
            auditorInfoPanel = new JPanel();
            auditorInfoPanel.setLayout(new GridBagLayout());
            auditorInfoPanel.setBorder(BorderFactory.createTitledBorder(
                null, "Selected Auditor", TitledBorder.DEFAULT_JUSTIFICATION, 
                TitledBorder.DEFAULT_POSITION, null, PortalLookAndFeel.getPanelLabelColor()));
            auditorInfoPanel.add(getClassNameLabel(), gridBagConstraints);
            auditorInfoPanel.add(getInstanceNameLabel(), gridBagConstraints1);
            auditorInfoPanel.add(getClassNameTextField(), gridBagConstraints2);
            auditorInfoPanel.add(getInstanceNameTextField(), gridBagConstraints3);
        }
        return auditorInfoPanel;
    }
    
    
    private AuditorConfigurationPropertiesTable getPropertiesTable() {
        if (propertiesTable == null) {
            propertiesTable = new AuditorConfigurationPropertiesTable();
            propertiesTable.addAuditorPropertyListener(new AuditorPropertyChangeListener() {
                public void propertyValueEdited(String key, String newValue) {
                    // store the property
                    try {
                        DataServiceAuditors auditors = dataManager.getAuditorsConfiguration();
                        for (AuditorConfiguration config : auditors.getAuditorConfiguration()) {
                            if (config.getClassName().equals(auditorClass)
                                && config.getInstanceName().equals(auditorInstance)) {
                                AuditorConfigurationConfigurationProperties props =
                                    config.getConfigurationProperties();
                                for (ConfigurationProperty prop : props.getProperty()) {
                                    if (prop.getKey().equals(key)) {
                                        prop.setValue(newValue);
                                        break;
                                    }
                                }
                                break;
                            }
                        }
                        dataManager.storeAuditorsConfiguration(auditors);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        CompositeErrorDialog.showErrorDialog(
                            "Error storing changed property", ex.getMessage(), ex);
                    }
                }
            });
        }
        return propertiesTable;
    }


    /**
     * This method initializes propertiesScrollPane	
     * 	
     * @return javax.swing.JScrollPane	
     */
    private JScrollPane getPropertiesScrollPane() {
        if (propertiesScrollPane == null) {
            propertiesScrollPane = new JScrollPane();
            propertiesScrollPane.setViewportView(getPropertiesTable());
            propertiesScrollPane.setBorder(BorderFactory.createTitledBorder(
                null, "Auditor Properties", TitledBorder.DEFAULT_JUSTIFICATION, 
                TitledBorder.DEFAULT_POSITION, null, PortalLookAndFeel.getPanelLabelColor()));
        }
        return propertiesScrollPane;
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
            GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
            gridBagConstraints7.gridx = 0;
            gridBagConstraints7.gridy = 2;
            GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
            gridBagConstraints6.gridx = 1;
            gridBagConstraints6.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints6.anchor = GridBagConstraints.SOUTHEAST;
            gridBagConstraints6.gridy = 2;
            GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
            gridBagConstraints5.fill = GridBagConstraints.BOTH;
            gridBagConstraints5.gridy = 1;
            gridBagConstraints5.weightx = 1.0;
            gridBagConstraints5.weighty = 1.0D;
            gridBagConstraints5.insets = new Insets(4, 4, 4, 4);
            gridBagConstraints5.gridwidth = 2;
            gridBagConstraints5.gridx = 0;
            GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
            gridBagConstraints4.gridx = 0;
            gridBagConstraints4.insets = new Insets(4, 4, 4, 4);
            gridBagConstraints4.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints4.gridwidth = 2;
            gridBagConstraints4.gridy = 0;
            mainPanel = new JPanel();
            mainPanel.setLayout(new GridBagLayout());
            mainPanel.add(getAuditorInfoPanel(), gridBagConstraints4);
            mainPanel.add(getPropertiesScrollPane(), gridBagConstraints5);
            mainPanel.add(getDoneButton(), gridBagConstraints6);
            mainPanel.add(getMonitoredEventsPanel(), gridBagConstraints7);
        }
        return mainPanel;
    }
    
    
    private void loadConfiguration() {
        File libDir = new File(serviceInfo.getBaseDirectory().getAbsolutePath() + File.separator + "lib");
        try {
            DataServiceAuditor auditor = AuditorsLoader.loadAuditor(libDir, auditorClass);

            // dig up the properties for this auditor and put them in
            // the auditor configuration table
            Properties auditorDefaultProps = auditor.getDefaultConfigurationProperties();
            DataServiceAuditors auditors = dataManager.getAuditorsConfiguration();
            AuditorConfigurationConfigurationProperties configProps = null;
            MonitoredEvents monitoredEvents = null;
            for (AuditorConfiguration config : auditors.getAuditorConfiguration()) {
                if (config.getClassName().equals(auditorClass)
                    && config.getInstanceName().equals(auditorInstance)) {
                    configProps = config.getConfigurationProperties();
                    monitoredEvents = config.getMonitoredEvents();
                    if (monitoredEvents == null) {
                        monitoredEvents = new MonitoredEvents();
                        config.setMonitoredEvents(monitoredEvents);
                    }
                    break;
                }
            }
            getPropertiesTable().setConfigurationProperties(configProps, auditorDefaultProps);

            // set the monitored events
            getMonitoredEventsPanel().setMonitoredEvents(monitoredEvents);
        } catch (Exception ex) {
            ex.printStackTrace();
            CompositeErrorDialog.showErrorDialog(
                "Error loading properties for auditor " + auditorClass + " : " + auditorInstance, 
                ex.getMessage(), ex);
        }
    }
    
    
    private MonitoredEventsPanel getMonitoredEventsPanel() {
        if (monitoredEventsPanel == null) {
            monitoredEventsPanel = new MonitoredEventsPanel();
            monitoredEventsPanel.setBorder(BorderFactory.createTitledBorder(
                null, "Monitored Events", TitledBorder.DEFAULT_JUSTIFICATION, 
                TitledBorder.DEFAULT_POSITION, null, PortalLookAndFeel.getPanelLabelColor()));
            monitoredEventsPanel.addMonitoredEventsChangeListener(new MonitoredEventsChangeListener() {
                public void monitoredEventsChanged() {
                    try {
                        DataServiceAuditors auditors = dataManager.getAuditorsConfiguration();
                        AuditorConfiguration selectedAuditor = null;
                        for (AuditorConfiguration config : auditors.getAuditorConfiguration()) {
                            if (config.getClassName().equals(auditorClass)
                                && config.getInstanceName().equals(auditorInstance)) {
                                selectedAuditor = config;
                                break;
                            }
                        }
                        selectedAuditor.setMonitoredEvents(
                            getMonitoredEventsPanel().getMonitoredEvents());
                        dataManager.storeAuditorsConfiguration(auditors);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        CompositeErrorDialog.showErrorDialog("Error storing monitored events", ex.getMessage(), ex);
                    }
                }
            });
        }
        return monitoredEventsPanel;
    }
}
