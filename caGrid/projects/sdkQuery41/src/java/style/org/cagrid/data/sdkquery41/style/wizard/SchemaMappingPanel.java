package org.cagrid.data.sdkquery41.style.wizard;

import gov.nih.nci.cagrid.data.ui.wizard.AbstractWizardPanel;
import gov.nih.nci.cagrid.introduce.beans.extension.ServiceExtensionDescriptionType;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;

import org.cagrid.data.sdkquery41.style.wizard.config.SchemaMappingConfigStep;
import org.cagrid.data.sdkquery41.style.wizard.mapping.SchemaMappingTable;
import org.cagrid.grape.utils.CompositeErrorDialog;

/**
 * Wizard panel which allows the service developer to
 * map an XML schema to each package in the domain model.
 * 
 * @author David
 *
 */
public class SchemaMappingPanel extends AbstractWizardPanel {
    
    private JPanel mainPanel = null;
    private SchemaMappingTable schemaMappingTable = null;
    private JScrollPane schemaMappingTableScrollPane = null;
    private JButton autoMapButton = null;
    
    private SchemaMappingConfigStep configuration = null;
    
    public SchemaMappingPanel(ServiceExtensionDescriptionType extensionDescription, ServiceInformation info) {
        super(extensionDescription, info);
        configuration = new SchemaMappingConfigStep(info);
        initialize();
    }


    public String getPanelShortName() {
        return "Schemas";
    }


    public String getPanelTitle() {
        return "Schema type to data object mapping";
    }


    public void update() {
        try {
            getSchemaMappingTable().reloadCadsrInformation();
        } catch (Exception ex) {
            ex.printStackTrace();
            CompositeErrorDialog.showErrorDialog(
                "Error loading information for package - schema mappings", ex.getMessage(), ex);
        }
    }

    
    public void movingNext() {
        try {
            configuration.applyConfiguration();
        } catch (Exception ex) {
            ex.printStackTrace();
            CompositeErrorDialog.showErrorDialog("Error applying configuration", ex.getMessage(), ex);
        }
    }
    
    
    private void initialize() {
        setLayout(new GridLayout());
        add(getMainPanel());
    }
    
    
    private JPanel getMainPanel() {
        if (mainPanel == null) {
            GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
            gridBagConstraints1.gridx = 0;
            gridBagConstraints1.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints1.gridy = 1;
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.fill = GridBagConstraints.BOTH;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.weightx = 1.0;
            gridBagConstraints.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints.weighty = 1.0D;
            gridBagConstraints.gridx = 0;
            mainPanel = new JPanel();
            mainPanel.setLayout(new GridBagLayout());
            mainPanel.add(getSchemaMappingTableScrollPane(), gridBagConstraints);
            mainPanel.add(getAutoMapButton(), gridBagConstraints1);
        }
        return mainPanel;
    }
    
    
    private SchemaMappingTable getSchemaMappingTable() {
        if (schemaMappingTable == null) {
            schemaMappingTable = new SchemaMappingTable(
                getServiceInformation(), configuration, new SchemaMappingValidityListener() {
                public void updateSchemaMappingValidity(boolean valid) {
                    setNextEnabled(valid);
                    setWizardComplete(valid);
                }
            });
        }
        return schemaMappingTable;
    }


    /**
     * This method initializes schemaMappingTableScrollPane	
     * 	
     * @return javax.swing.JScrollPane	
     */
    private JScrollPane getSchemaMappingTableScrollPane() {
        if (schemaMappingTableScrollPane == null) {
            schemaMappingTableScrollPane = new JScrollPane();
            schemaMappingTableScrollPane.setBorder(BorderFactory.createTitledBorder(
                null, "Schema Mappings", TitledBorder.DEFAULT_JUSTIFICATION, 
                TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
            schemaMappingTableScrollPane.setViewportView(getSchemaMappingTable());
        }
        return schemaMappingTableScrollPane;
    }


    /**
     * This method initializes autoMapButton	
     * 	
     * @return javax.swing.JButton	
     */
    private JButton getAutoMapButton() {
        if (autoMapButton == null) {
            autoMapButton = new JButton();
            autoMapButton.setText("Automatically Map From SDK Generated Schemas");
            autoMapButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    try {
                        configuration.mapFromSdkGeneratedSchemas();
                        update();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        CompositeErrorDialog.showErrorDialog(
                            "Error mapping schemas from SDK", ex.getMessage(), ex);
                    }
                }
            });
        }
        return autoMapButton;
    }
}
